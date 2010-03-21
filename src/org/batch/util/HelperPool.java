package org.batch.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.Hashtable;
import java.util.ArrayList;
import org.batch.util.ILogger;
import org.batch.util.LoggerFactory;
import org.batch.lang.BatchDefine;
import org.batch.lang.BatchException;
import org.batch.job.AbstractJob;
import org.batch.job.AbstractHelper;
import org.batch.parameter.config.HelperConfigMap;

/**
 * Helperの蓄積と状態の管理を行うスレッド.<br>
 *
 * @author T.Okuyama
 * @license GPL(Lv3)
 */
public class  HelperPool extends Thread {

    // Logger
    private static ILogger logger = LoggerFactory.createLogger(HelperPool.class);

    // Helper管理用
    private static ArrayList helperNameList = new ArrayList();
    private static Hashtable configMap = new Hashtable();
    private static Hashtable helperMap = new Hashtable();
    private static Hashtable helperReturnParamMap = new Hashtable();
    private static Hashtable helperStatusMap = new Hashtable();

    // Helperチェック間隔
    private static int helperCheckTime = 10000;

    // HelperThread Join待機時間
    private static int helperThreadJoinTime = 5000;

    private static Object poolLock = new Object();

    // スレッド内でのチェックをコントロール
    private boolean poolRunning = true;

    private static ExecutorService ex = Executors.newCachedThreadPool();

    public HelperPool() {
        poolRunning = true;
        
    }

    public void run () {
        logger.debug("HelperPool - run - start");
        try {
            // ExecutorService を使用するために変更. 2010/03/22

            while(this.poolRunning) {
                Thread.sleep(helperCheckTime);
            }
            logger.info("HelperPool - 終了処理開始");
            // システムの停止が要求されているのでHelperを強制終了
            ex.shutdown();
            logger.info("HelperPool - 終了処理終了");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("HelperPool - run - Error", e);
        }
    }

    /**
     * 明示的に初期化を行う
     */
    public static void initializeHelperPool() {
        helperNameList = new ArrayList();
        configMap = new Hashtable();
        helperMap = new Hashtable();
        helperReturnParamMap = new Hashtable();
        helperStatusMap = new Hashtable();
    }

    /**
     * Helperインスタンスを貸し出す.<br>
     * Helperが定義として存在しない場合BatchExceptionがthrowされる.<br>
     * 全て貸し出し中の場合はNullが返る.<br>
     *
     * @param String helperName
     * @return AbstractHelper ヘルパー
     * @throw  BatchException
     */
    public static AbstractHelper getHelper(String helperName) throws BatchException {
        logger.debug("HelperPool - getHelper - start");
        AbstractHelper helper = null;
        Integer useCount = null;
        ArrayList helperList = null;

        try {
            if (!helperMap.containsKey(helperName)) 
                throw new BatchException("Helper [" + helperName + "]は存在しません");

            HelperConfigMap helperConfigMap = (HelperConfigMap)configMap.get(helperName);
            helper = ClassUtility.createHelperInstance(helperConfigMap.getHelperClassName());

            // Jobに設定情報を格納
            helper.setConfig(helperConfigMap);
            helper.initialize();
            helperStatusMap.put(new Integer(helper.hashCode()), helper.getStatus());

        } catch (Exception e) {
            logger.error("HelperPool - getHelper - BatchException");
            throw new BatchException(e);
        }
        logger.debug("HelperPool - getHelper - end");
        return helper;
    
    }


    /**
     * Helperインスタンスを返す.<br>
     * Helperが定義として存在しない場合BatchExceptionがthrowされる.<br>
     *
     * @param String helperName
     * @param AbstractHelper ヘルパーインスタンス
     * @throw  BatchException
     */
/*    public static void returnHelper(String helperName, AbstractHelper helper) throws BatchException {
        logger.debug("HelperPool - returnHelper - start");
        logger.debug("HelperPool - returnHelper - helperName = [" + helperName + "]");
        ArrayList helperList = null;

        try {
            if (!helperMap.containsKey(helperName)) 
                throw new BatchException("Helper [" + helperName + "]は存在しません");

            synchronized(poolLock) {
                helperList = (ArrayList)helperMap.get(helperName);
                helperList.add(helper);

                helperMap.put(helperName, helperList);
            }
        } catch (BatchException be) {
            logger.error("HelperPool - returnHelper - BatchException");
            throw be;
        }
        logger.debug("HelperPool - returnHelper - end");
    }
*/
    /**
     * Helperインスタンスを返す.<br>
     * Helperが定義として存在しない場合BatchExceptionがthrowされる.<br>
     *
     * @param String helperName
     * @param AbstractHelper ヘルパーインスタンス
     * @throw  BatchException
     */
    public static void returnHelper(String helperName, AbstractHelper helper) throws BatchException {
        logger.debug("HelperPool - returnHelper - start");
        logger.debug("HelperPool - returnHelper - helperName = [" + helperName + "]");
        ArrayList helperList = null;

        try {
            // ExecutorService を使用するために変更. 2010/03/22
            ex.execute(helper);

        } catch (Exception be) {
            logger.error("HelperPool - returnHelper - BatchException");
            throw new BatchException(be);
        }
        logger.debug("HelperPool - returnHelper - end");
    }

    /**
     * Helperのプールを初期化する.<br>
     * 
     * @param HelperConfigMap helperConfigMap プールしたHelperの設定ファイル
     */
    public static void managedHelperConfig(HelperConfigMap helperConfigMap) throws BatchException {
        logger.debug("HelperPool - poolingHelper - start");
        logger.debug("HelperPool - poolingHelper - heplerConfig = " + helperConfigMap);

        helperNameList.add(helperConfigMap.getHelperName());
        configMap.put(helperConfigMap.getHelperName(),helperConfigMap);
        helperMap.put(helperConfigMap.getHelperName(),new ArrayList());

        logger.debug("HelperPool - poolingHelper - end");
    }

    /**
     * Helperが終了時にセットした返却値を取得する.<br>
     *
     * @param helperHashCode 対象HelperオブジェクトのhashCode
     * @return Object 返却値 値がセットされていない場合はnullが返る
     */
    public static Object getReturnParam(int helperHashCode) {
        Object ret = null;
        if (helperReturnParamMap.containsKey(new Integer(helperHashCode))) {
            ret = helperReturnParamMap.get(new Integer(helperHashCode));
        }
        return ret;
    }

    /**
     * Helperが終了後のリソースを破棄する.<br>
     *
     * @param helperHashCode 対象HelperオブジェクトのhashCode
     */
    public static void cleanEndHelper(int helperHashCode) {

        if (helperStatusMap.containsKey(new Integer(helperHashCode))) {
            helperStatusMap.remove(new Integer(helperHashCode));
        }

        if (helperReturnParamMap.containsKey(new Integer(helperHashCode))) {
            helperReturnParamMap.remove(new Integer(helperHashCode));
        }
    }


    /**
     * 指定したHelperのステータスを返す.<br>
     *
     * @param helperHashCode 対象HelperオブジェクトのhashCode
     * @return String ステータス文字列　Helperが存在しない場合はnullが返る
     */
    public static String getHelperStatus(int helperHashCode) {
        String ret = null;
        if (helperStatusMap.containsKey(new Integer(helperHashCode))) {
            ret = (String)helperStatusMap.get(new Integer(helperHashCode));
        }
        return ret;
    }

    public void poolEnd() {
        this.poolRunning = false;
    }


}