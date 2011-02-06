package test.job;

import java.io.*;
import java.net.*;
import java.util.*;

import okuyama.base.lang.BatchException;
import okuyama.base.job.AbstractJob;
import okuyama.base.job.AbstractHelper;
import okuyama.base.job.IJob;
import okuyama.base.util.ILogger;
import okuyama.base.util.LoggerFactory;
import okuyama.imdst.util.KeyMapManager;
import okuyama.imdst.util.StatusUtil;
import okuyama.imdst.util.JavaSystemApi;
import okuyama.imdst.client.*;

/**
 * 登録、取得、Tag登録、Tag取得、削除、Script実行、一意登録、のテストを実行
 *
 * @author T.Okuyama
 * @license GPL(Lv3)
 */
public class MethodPatterTestJob extends AbstractJob implements IJob {

    private String masterNodeName = "127.0.0.1";
    private int masterNodePort = 8888;

    private int nowCount = 0;

    private ILogger logger = LoggerFactory.createLogger(MethodPatterTestJob.class);

    // 初期化メソッド定義
    public void initJob(String initValue) {
        if (initValue != null && !initValue.equals("")) {
            String[] master = initValue.split(":");
            masterNodeName = master[0];
            masterNodePort = Integer.parseInt(master[1]);
        }
    }


    // Jobメイン処理定義
    public String executeJob(String optionParam) throws BatchException {

        String ret = SUCCESS;

        String[] execMethods = null;
        int count = 5000;
        HashMap retMap = new HashMap();

        try{
            execMethods = optionParam.split(",");

            int port = masterNodePort;

            // クライアントインスタンスを作成
            OkuyamaClient okuyamaClient = null;

            String startStr = super.getPropertiesValue(super.getJobName() + "start");
            int start = Integer.parseInt(startStr);
            count = count + start;
            for (int cy = 0; cy < 1; cy++) {
                for (int t = 0; t < Integer.parseInt(execMethods[0]); t++) {
                    this.nowCount = t;
                    System.out.println("Test Count =[" + t + "]");
                    for (int i = 1; i < execMethods.length; i++) {

                        if (execMethods[i].equals("set")) 
                            retMap.put("set", execSet(okuyamaClient, start, count));

                        if (execMethods[i].equals("get")) 
                            retMap.put("get", execGet(okuyamaClient, start, count));

                        if (execMethods[i].equals("settag")) 
                            retMap.put("settag", execTagSet(okuyamaClient, start, count));

                        if (execMethods[i].equals("gettag")) 
                            retMap.put("gettag", execTagGet(okuyamaClient, start, count));

                        if (execMethods[i].equals("remove")) 
                            retMap.put("remove", execRemove(okuyamaClient, start, 500));

                        if (execMethods[i].equals("script")) 
                            retMap.put("script", execScript(okuyamaClient, start, count));

                        if (execMethods[i].equals("add")) 
                            retMap.put("add", execAdd(okuyamaClient, start, count));

                        if (execMethods[i].equals("gets-cas")) 
                            retMap.put("cas", execGetsCas(okuyamaClient, start, count));

                        if (execMethods[i].equals("incr")) 
                            retMap.put("incr", execIncr(okuyamaClient, start, count));

                        if (execMethods[i].equals("decr")) 
                            retMap.put("decr", execDecr(okuyamaClient, start, count));
                    }

                    System.out.println("ErrorMap=" + retMap.toString());
                    System.out.println("---------------------------------------------");
                    // クライアントインスタンスを作成
                    okuyamaClient = new OkuyamaClient();
                    // マスタサーバに接続
                    okuyamaClient.connect(masterNodeName, port);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
			System.out.println(new Date());
            System.out.println(retMap);
            throw new BatchException(e);
        }

        return ret;
    }


    private boolean execSet(OkuyamaClient client, int start, int count) throws Exception {
        OkuyamaClient okuyamaClient = null;
        boolean errorFlg = false;
        try {
            System.out.println("execSet - Start");
            if (client != null) {
                okuyamaClient = client;
            } else {
                int port = masterNodePort;

                // クライアントインスタンスを作成
                okuyamaClient = new OkuyamaClient();

                // マスタサーバに接続
                okuyamaClient.connect(masterNodeName, port);
            }

            long startTime = new Date().getTime();
            for (int i = start; i < count; i++) {
                // データ登録

                if (!okuyamaClient.setValue(this.nowCount + "datasavekey_" + new Integer(i).toString(), this.nowCount + "testdata1234567891011121314151617181920212223242526272829_savedatavaluestr_" + new Integer(i).toString())) {
                    System.out.println("Set - Error=[" + this.nowCount + "datasavekey_" + new Integer(i).toString() + ", " + this.nowCount + "testdata1234567891011121314151617181920212223242526272829_savedatavaluestr_" + new Integer(i).toString());
                    errorFlg = true;
                }
                if ((i % 10000) == 0) System.out.println(i);
            }
            long endTime = new Date().getTime();
            System.out.println("Set Method= " + (endTime - startTime) + " milli second");

            if (client == null) {
                okuyamaClient.close();
            }
        } catch (Exception e) {
            throw e;
        }
        System.out.println("execSet - End");

        return errorFlg;
    }


    private boolean execGet(OkuyamaClient client, int start, int count) throws Exception {
        OkuyamaClient okuyamaClient = null;
        boolean errorFlg = false;
        try {
            System.out.println("execGet - Start");

            if (client != null) {
                okuyamaClient = client;
            } else {
                int port = masterNodePort;

                // クライアントインスタンスを作成
                okuyamaClient = new OkuyamaClient();

                // マスタサーバに接続
                okuyamaClient.connect(masterNodeName, port);
            }

            String[] ret = null;

            long startTime = new Date().getTime();
            for (int i = start; i < count; i++) {
                ret = okuyamaClient.getValue(this.nowCount + "datasavekey_" + new Integer(i).toString());

                if (ret[0].equals("true")) {
                    // データ有り
                    //System.out.println(ret[1]);
                    if (!ret[1].equals(this.nowCount + "testdata1234567891011121314151617181920212223242526272829_savedatavaluestr_" + new Integer(i).toString())) {
                        System.out.println("データが合っていない key=[" + this.nowCount + "datasavekey_" + new Integer(i).toString() + "]  value=[" + ret[1] + "]");
                        errorFlg = true;
                    }
                } else if (ret[0].equals("false")) {
                    System.out.println("データなし key=[" + this.nowCount + "datasavekey_" + new Integer(i).toString() + "]");
                    logger.error("データなし key=[" + this.nowCount + "datasavekey_" + new Integer(i).toString() + "]");
                    errorFlg = true;
                } else if (ret[0].equals("error")) {
                    System.out.println("Error key=[" + this.nowCount + "datasavekey_" + new Integer(i).toString() + "]" + ret[1]);
                    errorFlg = true;
                }
            }
            long endTime = new Date().getTime();
            System.out.println("Get Method= " + (endTime - startTime) + " milli second");

            if (client == null) {
                okuyamaClient.close();
            }
        } catch (Exception e) {
            throw e;
        }
        System.out.println("execGet - End");
        return errorFlg;
    }



    private boolean execTagSet(OkuyamaClient client, int start, int count) throws Exception {
        OkuyamaClient okuyamaClient = null;
        boolean errorFlg = false;
        try {
            System.out.println("execTagSet - Start");
            if (client != null) {
                okuyamaClient = client;
            } else {
                int port = masterNodePort;

                // クライアントインスタンスを作成
                okuyamaClient = new OkuyamaClient();

                // マスタサーバに接続
                okuyamaClient.connect(masterNodeName, port);
            }

            String[] tag1 = {start+"_" + this.nowCount + "_tag1"};
            String[] tag2 = {start+"_" + this.nowCount + "_tag1",start+"_" + this.nowCount + "_tag2"};
            String[] tag3 = {start+"_" + this.nowCount + "_tag1",start+"_" + this.nowCount + "_tag2",start+"_" + this.nowCount + "_tag3"};
            String[] tag4 = {start+"_" + this.nowCount + "_tag4"};
            String[] setTag = null;
            int counter = 0;

            long startTime = new Date().getTime();

            for (int i = start; i < count; i++) {
                if (counter == 0) {
                    setTag = tag1;
                    counter++;
                } else if (counter == 1) {
                    setTag = tag2;
                    counter++;
                } else if (counter == 2) {
                    setTag = tag3;
                    counter++;
                } else if (counter == 3) {
                    setTag = tag4;
                    counter = 0;
                }

                if (!okuyamaClient.setValue(this.nowCount + "tagsampledatakey_" + new Integer(i).toString(), setTag, this.nowCount + "tagsamplesavedata_" + new Integer(i).toString())) {
                    System.out.println("Tag Set - Error=[" + this.nowCount + "tagsampledatakey_" + new Integer(i).toString() + ", " + this.nowCount + "tagsamplesavedata_" + new Integer(i).toString());
                    errorFlg = true;
                }
            }
            long endTime = new Date().getTime();
            System.out.println("Tag Set Method= " + (endTime - startTime) + " milli second");

            if (client == null) {
                okuyamaClient.close();
            }
        } catch (Exception e) {
            throw e;
        }
        System.out.println("execTagSet - End");
        return errorFlg;
    }


    private boolean execTagGet(OkuyamaClient client, int start, int count) throws Exception {
        OkuyamaClient okuyamaClient = null;
        boolean errorFlg = false;
        try {
            System.out.println("execTagGet - Start");

            if (client != null) {
                okuyamaClient = client;
            } else {
                int port = masterNodePort;

                // クライアントインスタンスを作成
                okuyamaClient = new OkuyamaClient();

                // マスタサーバに接続
                okuyamaClient.connect(masterNodeName, port);
            }

            String[] tag1 = {start+"_" + this.nowCount + "_tag1"};
            String[] tag2 = {start+"_" + this.nowCount + "_tag1",start+"_" + this.nowCount + "_tag2"};
            String[] tag3 = {start+"_" + this.nowCount + "_tag1",start+"_" + this.nowCount + "_tag2",start+"_" + this.nowCount + "_tag3"};
            String[] tag4 = {start+"_" + this.nowCount + "_tag4"};
            String[] setTag = null;

            String[] keys = null;
            long startTime = new Date().getTime();
            Object[] ret = okuyamaClient.getTagKeys(start+"_" + this.nowCount + "_tag1");

            if (ret[0].equals("true")) {
                // データ有り
                keys = (String[])ret[1];

                for (int ii = start; ii < keys.length; ii++) {
                    String[] getRet = okuyamaClient.getValue(keys[ii]);

                    if (getRet[0].equals("true")) {
                        // データ有り
                        //System.out.println(getRet[1]);
                    } else if (getRet[0].equals("false")) {
                        System.out.println("データなし key=[" + keys[ii] + "]");
                        logger.error("Tag Get データなし key=[" + keys[ii] + "]");
                        errorFlg = true;
                    } else if (getRet[0].equals("error")) {
                        System.out.println("Error key=[" + keys[ii] + "]");
                        errorFlg = true;
                    }
                }

            } else if (ret[0].equals("false")) {
                System.out.println(start+"tag1=データなし");
                errorFlg = true;
            } else if (ret[0].equals("error")) {
                System.out.println(start+"tag1=Error[" + ret[1] + "]");
                errorFlg = true;
            }

            ret = okuyamaClient.getTagKeys(start+"_" + this.nowCount + "_tag2");

            if (ret[0].equals("true")) {
                // データ有り
                keys = (String[])ret[1];

                for (int ii = start; ii < keys.length; ii++) {
                    String[] getRet = okuyamaClient.getValue(keys[ii]);

                    if (getRet[0].equals("true")) {
                        // データ有り
                        //System.out.println(getRet[1]);
                    } else if (getRet[0].equals("false")) {
                        System.out.println("データなし key=[" + keys[ii] + "]");
                        errorFlg = true;
                    } else if (getRet[0].equals("error")) {
                        System.out.println("Error key=[" + keys[ii] + "]");
                        errorFlg = true;
                    }
                }

            } else if (ret[0].equals("false")) {
                System.out.println(start+"_tag2=データなし");
                errorFlg = true;
            } else if (ret[0].equals("error")) {
                System.out.println(start+"tag2=Error[" + ret[1] + "]");
                errorFlg = true;
            }

            ret = okuyamaClient.getTagKeys(start+"_" + this.nowCount + "_tag3");

            if (ret[0].equals("true")) {
                // データ有り
                keys = (String[])ret[1];

                for (int ii = start; ii < keys.length; ii++) {
                    String[] getRet = okuyamaClient.getValue(keys[ii]);

                    if (getRet[0].equals("true")) {
                        // データ有り
                        //System.out.println(getRet[1]);
                    } else if (getRet[0].equals("false")) {
                        System.out.println("データなし key=[" + keys[ii] + "]");
                        errorFlg = true;
                    } else if (getRet[0].equals("error")) {
                        System.out.println("Error key=[" + keys[ii] + "]");
                        errorFlg = true;
                    }
                }

            } else if (ret[0].equals("false")) {
                System.out.println(start+"_tag3=データなし");
                errorFlg = true;
            } else if (ret[0].equals("error")) {
                System.out.println(start+"tag3=Error[" + ret[1] + "]");
                errorFlg = true;
            }

            long endTime = new Date().getTime();
            System.out.println("Tag Get Method= " + (endTime - startTime) + " milli second");

            if (client == null) {
                okuyamaClient.close();
            }
        } catch (Exception e) {
            throw e;
        }
        System.out.println("execTagGet - End");
        return errorFlg;
    }



    private boolean execRemove(OkuyamaClient client, int start, int count) throws Exception {
        OkuyamaClient okuyamaClient = null;
        boolean errorFlg = false;
        try {
            System.out.println("execRemove - Start");

            if (client != null) {
                okuyamaClient = client;
            } else {
                int port = masterNodePort;

                // クライアントインスタンスを作成
                okuyamaClient = new OkuyamaClient();

                // マスタサーバに接続
                okuyamaClient.connect(masterNodeName, port);
            }

            String[] ret = null;

            long startTime = new Date().getTime();
            for (int i = start; i < count;i++) {
                ret = okuyamaClient.removeValue(this.nowCount + "datasavekey_" + new Integer(i).toString());
                if (ret[0].equals("true")) {
                    // データ有り
                    //System.out.println(ret[1]);
                    if (!ret[1].equals(this.nowCount + "testdata1234567891011121314151617181920212223242526272829_savedatavaluestr_" + new Integer(i).toString())) {
                        System.out.println("データが合っていない key=[" + this.nowCount + "datasavekey_" + new Integer(i).toString() + "]  value=[" + ret[1] + "]");
                        errorFlg = true;
                    }
                } else if (ret[0].equals("false")) {
                    System.out.println("データなし key=[" + this.nowCount + "datasavekey_" + new Integer(i).toString() + "]");
                    errorFlg = true;
                } else if (ret[0].equals("error")) {
                    System.out.println("Error key=[" + this.nowCount + "datasavekey_" + new Integer(i).toString() + "]" + ret[1]);
                    errorFlg = true;
                }

            }
            long endTime = new Date().getTime();
            System.out.println("Remove Method= " + (endTime - startTime) + " milli second");


            if (client == null) {
                okuyamaClient.close();
            }
        } catch (Exception e) {
            throw e;
        }
        System.out.println("execRemove - End");
        return errorFlg;
    }



    private boolean execScript(OkuyamaClient client, int start, int count) throws Exception {
        OkuyamaClient okuyamaClient = null;
        boolean errorFlg = false;
        try {
            System.out.println("execScript - Start");

            if (client != null) {
                okuyamaClient = client;
            } else {
                int port = masterNodePort;

                // クライアントインスタンスを作成
                okuyamaClient = new OkuyamaClient();

                // マスタサーバに接続
                okuyamaClient.connect(masterNodeName, port);
            }

            long startTime = new Date().getTime();
            String[] ret = okuyamaClient.getValueScript(this.nowCount + "datasavekey_" + (start + 600), "var dataValue; var retValue = dataValue.replace('data', 'dummy'); var execRet = '1';");
            if (ret[0].equals("true")) {
                // データ有り
                //System.out.println(ret[1]);
                if (!ret[1].equals(this.nowCount + "savedummyvaluestr_" +  (start + 600))) {
                    System.out.println("データが合っていない" + ret[1]);
                    errorFlg = true;
                }
            } else if (ret[0].equals("false")) {
                System.out.println("データなし key=[" + this.nowCount + "datasavekey_" + (start + 600));
                errorFlg = true;
            } else if (ret[0].equals("error")) {
                System.out.println("Error key=[" + this.nowCount + "datasavekey_" +  (start + 600));
                errorFlg = true;
            }

            long endTime = new Date().getTime();
            System.out.println("GetScript Method= " + (endTime - startTime) + " milli second");

            if (client == null) {
                okuyamaClient.close();
            }
        } catch (Exception e) {
            throw e;
        }
        System.out.println("execScript - End");
        return errorFlg;
    }


    private boolean execAdd(OkuyamaClient client, int start, int count) throws Exception {
        OkuyamaClient okuyamaClient = null;
        boolean errorFlg = false;
        try {
            System.out.println("execAdd - Start");

            if (client != null) {
                okuyamaClient = client;
            } else {
                int port = masterNodePort;

                // クライアントインスタンスを作成
                okuyamaClient = new OkuyamaClient();

                // マスタサーバに接続
                okuyamaClient.connect(masterNodeName, port);
            }

            long startTime = new Date().getTime();
            String[] retParam = okuyamaClient.setNewValue(this.nowCount + "Key_ABCDE" + start, this.nowCount + "AAAAAAAAABBBBBBBBBBBBCCCCCCCCCC" + start);

            if(retParam[0].equals("false")) {

                System.out.println("Key=[" + this.nowCount + "Key_ABCDE] Error=[" + retParam[1] + "]");
                errorFlg = true;
            } else {
                //System.out.println("処理成功");
            }
            long endTime = new Date().getTime();

            System.out.println("New Value Method= " + (endTime - startTime) + " milli second");

            if (client == null) {
                okuyamaClient.close();
            }
        } catch (Exception e) {
            throw e;
        }
        System.out.println("execAdd - End");
        return errorFlg;
    }


    private boolean execGetsCas(OkuyamaClient client, int start, int count) throws Exception {
        OkuyamaClient okuyamaClient = null;
        boolean errorFlg = false;
        try {
            System.out.println("execCas - Start");

            if (client != null) {
                okuyamaClient = client;
            } else {
                int port = masterNodePort;

                // クライアントインスタンスを作成
                okuyamaClient = new OkuyamaClient();

                // マスタサーバに接続
                okuyamaClient.connect(masterNodeName, port);
            }

            long startTime = new Date().getTime();
            int casCount = 0;
            for (int i = start; i < count; i++) {
                // データ登録

                if (!okuyamaClient.setValue(casCount + "_castest_datasavekey", "castest_testdata1234567891011121314151617181920212223242526272829_savedatavaluestr_" + casCount + "_" + new Integer(i).toString())) {
                    System.out.println("Set - Error=[" + casCount + "_castest_datasavekey] Value[" + this.nowCount + "castest_testdata1234567891011121314151617181920212223242526272829_savedatavaluestr_" + casCount + "_" + new Integer(i).toString() + "]");
                    errorFlg = true;
                }
                casCount++;
            }

            Random rndIdx = new Random();
            int casSuccessCount = 0;
            int casErrorCount = 0;
            for (int casIdx = 0; casIdx < 6000; casIdx++) {
                int rndSet = rndIdx.nextInt(casCount);
                Object[] getsRet = okuyamaClient.getValueVersionCheck(rndSet + "_castest_datasavekey");


                String[] retParam = okuyamaClient.setValueVersionCheck(rndSet + "_castest_datasavekey", "updated-" + rndSet, (String)getsRet[2]);
                if(retParam[0].equals("true")) {
                    casSuccessCount++;
                } else {
                    casErrorCount++;
                }
            }
            long endTime = new Date().getTime();

            System.out.println("Cas Method= " + (endTime - startTime) + " milli second Suucess=" + casSuccessCount + "  Error=" + casErrorCount);

            if (client == null) {
                okuyamaClient.close();
            }
        } catch (Exception e) {
            throw e;
        }
        System.out.println("execCas - End");
        return errorFlg;
    }


    private boolean execIncr(OkuyamaClient client, int start, int count) throws Exception {
        OkuyamaClient okuyamaClient = null;
        boolean errorFlg = false;
        try {
            System.out.println("execIncr - Start");

            if (client != null) {
                okuyamaClient = client;
            } else {
                int port = masterNodePort;

                // クライアントインスタンスを作成
                okuyamaClient = new OkuyamaClient();

                // マスタサーバに接続
                okuyamaClient.connect(masterNodeName, port);
            }

            long startTime = new Date().getTime();
            String[] work = okuyamaClient.setNewValue("calcKeyIncr", "0");


            for (int i = start; i < count; i++) {
                Object[] ret = okuyamaClient.incrValue("calcKeyIncr", 1);
                if (ret[0].equals("false")) {
                    errorFlg = true;
                    System.out.println(ret[0]);
                    System.out.println(ret[1]);
                }
            }

            long endTime = new Date().getTime();
            String[] nowVal = okuyamaClient.getValue("calcKeyIncr");
            System.out.println("IncrValue Method= " + (endTime - startTime) + " milli second Value=[" + nowVal[1] + "]");

            if (client == null) {
                okuyamaClient.close();
            }
        } catch (Exception e) {
            throw e;
        }
        System.out.println("execAdd - End");
        return errorFlg;
    }

    private boolean execDecr(OkuyamaClient client, int start, int count) throws Exception {
        OkuyamaClient okuyamaClient = null;
        boolean errorFlg = false;
        try {
            System.out.println("execDecr - Start");

            if (client != null) {
                okuyamaClient = client;
            } else {
                int port = masterNodePort;

                // クライアントインスタンスを作成
                okuyamaClient = new OkuyamaClient();

                // マスタサーバに接続
                okuyamaClient.connect(masterNodeName, port);
            }

            long startTime = new Date().getTime();
            String[] work = okuyamaClient.setNewValue("calcKeyDecr", "1000000");


            for (int i = start; i < count; i++) {
                Object[] ret = okuyamaClient.decrValue("calcKeyDecr", 1);
                if (ret[0].equals("false")) {
                    errorFlg = true;
                    System.out.println(ret[0]);
                    System.out.println(ret[1]);
                }
            }

            long endTime = new Date().getTime();
            String[] nowVal = okuyamaClient.getValue("calcKeyDecr");
            System.out.println("DecrValue Method= " + (endTime - startTime) + " milli second Value=[" + nowVal[1] + "]");

            if (client == null) {
                okuyamaClient.close();
            }
        } catch (Exception e) {
            throw e;
        }
        System.out.println("execDecr - End");
        return errorFlg;
    }

}