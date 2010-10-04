import java.util.*;
import java.io.*;

import java.util.concurrent.locks.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.commons.codec.digest.DigestUtils;

public class Main2 extends Thread {

    public volatile int name = -1;

    public static int dataSize = 10000000;

    public static FileHashMap[] fileHashMaps = null;

    public static void main(String[] args) {
        fileHashMaps = new FileHashMap[2];

        String[] dirs1 = {"/usr/local/okuyama/work1/data1/","/usr/local/okuyama/work1/data2/","/usr/local/okuyama/work1/data3/","/usr/local/okuyama/work1/data4/","/usr/local/okuyama/work1/data5/","/usr/local/okuyama/work1/data6/","/usr/local/okuyama/work1/data7/","/usr/local/okuyama/work1/data8/"};
        String[] dirs2 = {"/usr/local/okuyama/work2/data1/","/usr/local/okuyama/work2/data2/","/usr/local/okuyama/work2/data3/","/usr/local/okuyama/work2/data4/","/usr/local/okuyama/work2/data5/","/usr/local/okuyama/work2/data6/","/usr/local/okuyama/work2/data7/","/usr/local/okuyama/work2/data8/"};

        fileHashMaps[0] = new FileHashMap(dirs1);
        fileHashMaps[1] = new FileHashMap(dirs2);

        Main2[] me = new Main2[Integer.parseInt(args[0])];


        try {
            long start2 = System.nanoTime();


            fileHashMaps[getHashCode("keyAbCddEfGhIjK`;:8547asdf7822kuioZj_201_0_848558") % fileHashMaps.length].put("keyAbCddEfGhIjK`;:8547asdf7822kuioZj_201_0_848558", "123");
            long end2 = System.nanoTime();
            System.out.println((end2 - start2));

            start2 = System.nanoTime();
            fileHashMaps[getHashCode("keyAbCddEfGhIjK`;:8547asdf7822kuioZj_201_0_177999") % fileHashMaps.length].get("keyAbCddEfGhIjK`;:8547asdf7822kuioZj_201_0_177999");
            end2 = System.nanoTime();
            System.out.println((end2 - start2));

            for (int i = 0; i < Integer.parseInt(args[0]); i++) {
                me[i] = new Main2(i);
            }

            for (int i = 0; i < Integer.parseInt(args[0]); i++) {
                me[i].start();
            }

            for (int i = 0; i < Integer.parseInt(args[0]); i++) {
                me[i].join();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Main2(int setName) {
        this.name = setName;
    }


    public static int getHashCode(String key) {
        
        int index = new String(DigestUtils.sha(key.getBytes())).hashCode();
        
        //int index = key.hashCode();
        if (index < 0) {
            index = index - index - index;
        }

        return index;
    } 


    public void run() {
        exec();
    }

    public void exec() {

        long start1 = 0L;
        long end1 = 0L;
        Random rdn = new Random();

        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {

            String rndKey = "keyAbCddEfGhIjK`;:8547asdf7822kuioZj_201" + this.name + "_" + i;
            fileHashMaps[getHashCode(rndKey) % fileHashMaps.length].put(rndKey, "abcdefg" + i);
            if ((i % 10000) == 0) {

                long end2 = System.currentTimeMillis();
                System.out.println(i + "=" + (end2 - start1));
                start1 = System.currentTimeMillis();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("Data Write Time = [" + (end - start) + "]");

        start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {

            String rndKey = "keyAbCddEfGhIjK`;:8547asdf7822kuioZj_201" + this.name + "_" + i;
            String var = fileHashMaps[getHashCode(rndKey) % fileHashMaps.length].get(rndKey);
            if ((i % 10000) == 0) {

                if (var == null) break;
                long end2 = System.currentTimeMillis();
                System.out.println(i + "=" + (end2 - start1) + "[" + var + "]");
                start1 = System.currentTimeMillis();
            }
        }
        end = System.currentTimeMillis();
        System.out.println("Data Read Time = [" + (end - start) + "]");



        start = System.currentTimeMillis();
        for (int i = 0; i < dataSize; i++) {

            String rndKey = "keyAbCddEfGhIjK`;:8547asdf7822kuioZj_201" + this.name + "_" + rdn.nextInt(10000);
            String var = fileHashMaps[getHashCode(rndKey) % fileHashMaps.length].get(rndKey);
            if ((i % 10000) == 0) {
                if (var == null) break;
                long end2 = System.currentTimeMillis();
                System.out.println(i + "=" + (end2 - start1) + "[" + var + "]");
                start1 = System.currentTimeMillis();
            }
        }
        end = System.currentTimeMillis();
        System.out.println("Data Read Time = [" + (end - start) + "]");

        System.out.println("getCacheSize-1[" + fileHashMaps[0].getCacheSize() + "]");
        System.out.println("getCacheSize-2[" + fileHashMaps[1].getCacheSize() + "]");
        start = System.currentTimeMillis();
        start1 = System.currentTimeMillis();
        for (int i = 0; i < dataSize; i++) {
            String putKey = "keyAbCddEfGhIjK`;:8547asdf7822kuioZj_201" + this.name + "_" + i;
            fileHashMaps[getHashCode(putKey) % fileHashMaps.length].put(putKey, new Integer(i).toString() + "_" + "1");
            if ((i % 10000) == 0) {
                end1 = System.currentTimeMillis();
                System.out.println(i + "=" + (end1 - start1));
                start1 = System.currentTimeMillis();
            }
        }
        end = System.currentTimeMillis();
        System.out.println((end - start));
        System.out.println("Data Write Time = [" + (end - start) + "]");
        System.out.println("getCacheSize-1[" + fileHashMaps[0].getCacheSize() + "]");
        System.out.println("getCacheSize-2[" + fileHashMaps[1].getCacheSize() + "]");
        System.out.println("------------------ END-1 -------------------");


        start = System.currentTimeMillis();
        for (int i = dataSize; i < dataSize / 20; i++) {

            String rndKey = "keyAbCddEfGhIjK`;:8547asdf7822kuioZj_201" + "0" + "_" + rdn.nextInt(10000000);
            String var = fileHashMaps[getHashCode(rndKey) % fileHashMaps.length].get(rndKey);
            if ((i % 10000) == 0) {
                if (var == null) break;
                long end2 = System.currentTimeMillis();
                System.out.println(i + "=" + (end2 - start1) + "[" + var + "]");
                start1 = System.currentTimeMillis();
            }
        }
        end = System.currentTimeMillis();
        System.out.println("Data Read Time = [" + (end - start) + "]");

        System.out.println("getCacheSize-1[" + fileHashMaps[0].getCacheSize() + "]");
        System.out.println("getCacheSize-2[" + fileHashMaps[1].getCacheSize() + "]");

        start = System.currentTimeMillis();
        start1 = System.currentTimeMillis();

        for (int i = dataSize; i < dataSize * 2; i++) {
            String putKey = "keyAbCddEfGhIjK`;:8547asdf7822kuioZj_201" + this.name + "_" + i;
            fileHashMaps[getHashCode(putKey) % fileHashMaps.length].put(putKey, new Integer(i).toString() + "_" + "2");
            if ((i % 10000) == 0) {
                end1 = System.currentTimeMillis();
                System.out.println(i + "=" + (end1 - start1));
                start1 = System.currentTimeMillis();
            }
        }
        end = System.currentTimeMillis();
        System.out.println((end - start));
        System.out.println("Data Write Time = [" + (end - start) + "]");
        System.out.println("getCacheSize-1[" + fileHashMaps[0].getCacheSize() + "]");
        System.out.println("getCacheSize-2[" + fileHashMaps[1].getCacheSize() + "]");
        System.out.println("------------------ END-2 -------------------");


        start = System.currentTimeMillis();
        for (int i = 0; i < dataSize / 20; i++) {

            String rndKey = "keyAbCddEfGhIjK`;:8547asdf7822kuioZj_201" + "0" + "_" + rdn.nextInt(20000000);
            String var = fileHashMaps[getHashCode(rndKey) % fileHashMaps.length].get(rndKey);
            if ((i % 10000) == 0) {
                if (var == null) break;
                long end2 = System.currentTimeMillis();
                System.out.println(i + "=" + (end2 - start1) + "[" + var + "]");
                start1 = System.currentTimeMillis();
            }
        }
        end = System.currentTimeMillis();
        System.out.println("Data Read Time = [" + (end - start) + "]");

        System.out.println("getCacheSize-1[" + fileHashMaps[0].getCacheSize() + "]");
        System.out.println("getCacheSize-2[" + fileHashMaps[1].getCacheSize() + "]");

        start = System.currentTimeMillis();
        start1 = System.currentTimeMillis();

        for (int i = dataSize * 2; i < dataSize * 3; i++) {
            String putKey = "keyAbCddEfGhIjK`;:8547asdf7822kuioZj_201" + this.name + "_" + i;
            fileHashMaps[getHashCode(putKey) % fileHashMaps.length].put(putKey, new Integer(i).toString() + "_" + "3");
            if ((i % 10000) == 0) {
                end1 = System.currentTimeMillis();
                System.out.println(i + "=" + (end1 - start1));
                start1 = System.currentTimeMillis();
            }
        }
        end = System.currentTimeMillis();
        System.out.println((end - start));
        System.out.println("Data Write Time = [" + (end - start) + "]");
        System.out.println("getCacheSize-1[" + fileHashMaps[0].getCacheSize() + "]");
        System.out.println("getCacheSize-2[" + fileHashMaps[1].getCacheSize() + "]");

        System.out.println("------------------ END-3 -------------------");


    }
}



class FileHashMap {

    String[] baseFileDirs = {"./data/data1/","./data/data2/","./data/data3/","./data/data4/","./data/data5/","./data/data6/","./data/data7/","./data/data8/"};
    String[] fileDirs = null;

    ValueCacheMap valueCacheMap = new ValueCacheMap(1024);

    int keyDataLength = 129;

    int oneDataLength = 16;

    int lineDataSize =  keyDataLength + oneDataLength;

    // 一度に取得するデータサイズ
    int getDataSize = lineDataSize * 56;

    int accessCount = 1024 * 10;

    File[] fileAccessList = new File[accessCount];

    public FileHashMap(String[] dirs) {
        this.baseFileDirs = dirs;
        try {
            fileDirs = new String[baseFileDirs.length * 20];
            int counter = 0;
            for (int idx = 0; idx < baseFileDirs.length; idx++) {
                for (int idx2 = 0; idx2 < 20; idx2++) {

                    fileDirs[counter] = baseFileDirs[idx] + idx2 + "/";
                    File dir = new File(fileDirs[counter]);
                    if (!dir.exists()) dir.mkdirs();
                    counter++;
                }
            }

            for (int i = 0; i < accessCount; i++) {
                File file = new File(fileDirs[i % fileDirs.length] + i + ".data");



                fileAccessList[i] = file;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void put(String key, String value) {
        try {

            // Create HashCode 
            int index = createHashCode(key);

            File file = fileAccessList[index % accessCount];

            StringBuffer buf = new StringBuffer(this.fillCharacter(key, keyDataLength));
            buf.append(this.fillCharacter(value, oneDataLength));

            synchronized (valueCacheMap.syncObj) {

                CacheContainer accessor = (CacheContainer)valueCacheMap.get(file.getAbsolutePath());
                RandomAccessFile raf = null;
                BufferedWriter wr = null;

                if (accessor == null || accessor.isClosed == true) {

                    raf = new RandomAccessFile(file, "rw");
                    wr = new BufferedWriter(new FileWriter(file, true));
                    accessor = new CacheContainer();
                    accessor.raf = raf;
                    accessor.wr = wr;
                    accessor.file = file;
                    valueCacheMap.put(file.getAbsolutePath(), accessor);
                } else {

                    raf = accessor.raf;
                    wr = accessor.wr;
                }

                long dataLineNo = this.getLinePoint(key, raf);

                if (dataLineNo == -1) {

                    wr.write(buf.toString());
                    wr.flush();
                } else {
                    raf.seek(dataLineNo * (lineDataSize));
                    raf.write(buf.toString().getBytes(), 0, lineDataSize);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public long getLinePoint(String key, RandomAccessFile raf) {

        long line = -1;
        long lineCount = 0L;


        String ret = null;
        byte[] keyBytes = key.getBytes();
        byte[] equalKeyBytes = new byte[keyBytes.length + 1];
        byte[] lineBufs = new byte[this.getDataSize];
        boolean matchFlg = true;

        // マッチング用配列作成
        for (int idx = 0; idx < keyBytes.length; idx++) {
            equalKeyBytes[idx] = keyBytes[idx];
        }

        equalKeyBytes[equalKeyBytes.length - 1] = 38;

        try {

            raf.seek(0);
            int readLen = -1;
            while((readLen = raf.read(lineBufs)) != -1) {

                matchFlg = true;

                int loop = readLen / lineDataSize;

                for (int loopIdx = 0; loopIdx < loop; loopIdx++) {

                    int assist = (lineDataSize * loopIdx);

                    matchFlg = true;
                    if (equalKeyBytes[equalKeyBytes.length - 1] == lineBufs[assist + (equalKeyBytes.length - 1)]) {
                        for (int i = 0; i < equalKeyBytes.length; i++) {
                            if (equalKeyBytes[i] != lineBufs[assist + i]) {
                                matchFlg = false;
                                break;
                            }
                        }
                    } else {
                        matchFlg = false;
                    }
                    
                    // マッチした場合のみ返す
                    if (matchFlg) {
                        line = lineCount;
                        break;
                    }
                    lineCount++;
                }
                if (matchFlg) break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return line;
    }


    public String get(String key) {
        byte[] tmpBytes = null;

        String ret = null;
        byte[] keyBytes = key.getBytes();
        byte[] equalKeyBytes = new byte[keyBytes.length + 1];
        byte[] lineBufs = new byte[this.getDataSize];
        boolean matchFlg = true;

        // マッチング用配列作成
        for (int idx = 0; idx < keyBytes.length; idx++) {
            equalKeyBytes[idx] = keyBytes[idx];
        }

        equalKeyBytes[equalKeyBytes.length - 1] = 38;

        try {

            // Create HashCode
            int index = createHashCode(key);

            File file = fileAccessList[index % accessCount];

            synchronized (valueCacheMap.syncObj) {
                CacheContainer accessor = (CacheContainer)valueCacheMap.get(file.getAbsolutePath());
                RandomAccessFile raf = null;
                BufferedWriter wr = null;

                if (accessor == null || accessor.isClosed) {

                    raf = new RandomAccessFile(file, "rw");
                    wr = new BufferedWriter(new FileWriter(file, true));
                    accessor = new CacheContainer();
                    accessor.raf = raf;
                    accessor.wr = wr;
                    accessor.file = file;
                    valueCacheMap.put(file.getAbsolutePath(), accessor);
                } else {

                    raf = accessor.raf;
                }

                raf.seek(0);
                int readLen = -1;
                while((readLen = raf.read(lineBufs)) != -1) {

                    matchFlg = true;

                    int loop = readLen / lineDataSize;

                    for (int loopIdx = 0; loopIdx < loop; loopIdx++) {

                        int assist = (lineDataSize * loopIdx);

                        matchFlg = true;
                        if (equalKeyBytes[equalKeyBytes.length - 1] == lineBufs[assist + (equalKeyBytes.length - 1)]) {

                            for (int i = 0; i < equalKeyBytes.length; i++) {

                                if (equalKeyBytes[i] != lineBufs[assist + i]) {
                                    matchFlg = false;
                                    break;
                                }
                            }
                        } else {
                            matchFlg = false;
                        }

                        // マッチした場合のみ配列化
                        if (matchFlg) {
                            tmpBytes = new byte[lineDataSize];
                            for (int i = 0; i < lineDataSize; i++) {
                                tmpBytes[i] = lineBufs[assist + i];
                            }
                            break;
                        }
                    }
                    if (matchFlg) break;
                }
            }

            if (tmpBytes != null) {
                if (tmpBytes[keyDataLength] != 38) {
                    int i = keyDataLength;
                    int counter = 0;
                    for (; i < tmpBytes.length; i++) {
                        if (tmpBytes[i] == 38) break;
                        counter++;
                    }

                    ret = new String(tmpBytes, keyDataLength, counter, "UTF-8");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }


    private String fillCharacter(String data, int fixSize) {
        StringBuffer writeBuf = new StringBuffer(data);

        int valueSize = data.length();

        // 渡されたデータが固定の長さ分ない場合は足りない部分を補う
        // 足りない文字列は固定の"&"で補う(38)
        byte[] appendDatas = new byte[fixSize - valueSize];

        for (int i = 0; i < appendDatas.length; i++) {
            appendDatas[i] = 38;
        }

        writeBuf.append(new String(appendDatas));
        return writeBuf.toString();
    }

    public int getCacheSize() {
        return valueCacheMap.getSize();
    }


    public int createHashCode(String key) {
        
        int index = new String(DigestUtils.sha(key.getBytes())).hashCode();

        if (index < 0) {
            index = index - index - index;
        }

        return index;
    } 
}



class CacheContainer {
    public RandomAccessFile raf = null;
    public BufferedWriter wr = null;
    public File file = null;
    public boolean isClosed = false;
}


class ValueCacheMap extends LinkedHashMap {

    private boolean fileWrite = false;

    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock r = rwl.readLock();
    private final Lock w = rwl.writeLock();

    private int maxCacheSize = 16;

    public Object syncObj = new Object();

    // コンストラクタ
    public ValueCacheMap() {
        super(1024, 0.75f, true);
    }


    // コンストラクタ
    public ValueCacheMap(int maxCacheCapacity) {
        super(maxCacheCapacity, 0.75f, true);
        maxCacheSize = maxCacheCapacity;
    }


    /**
     * set<br>
     *
     * @param key
     * @param value
     */
    public Object put(Object key, Object value) {
        w.lock();
        try { 
            return super.put(key, value);
        } finally {
            w.unlock(); 
        }
    }


    /**
     * containsKey<br>
     *
     * @param key
     * @return boolean
     */
    public boolean containsKey(Object key) {
        r.lock();
        try { 
            return super.containsKey(key);
        } finally { 
            r.unlock(); 
        }
    }


    /**
     * get<br>
     *
     * @param key
     * @return Object
     */
    public Object get(Object key) {
        r.lock();
        try { 
            return super.get(key); 
        } finally { 
            r.unlock(); 
        }
    }


    /**
     * remove<br>
     *
     * @param key
     * @return Object
     */
    public Object remove(Object key) {
        w.lock();
        try {
            return super.remove(key);
        } finally {
            w.unlock(); 
        }
    }


    /**
     * clear<br>
     *
     */
    public void clear() {
        w.lock();
        try { 
            super.clear();
        } finally {
            w.unlock(); 
        }
    }


    /**
     * 削除指標実装.<br>
     */
    protected boolean removeEldestEntry(Map.Entry eldest) {
        boolean ret = false;
        if (size() > maxCacheSize) {
            CacheContainer accessor= (CacheContainer)eldest.getValue();
            try {
                if (accessor != null) {

                    synchronized (syncObj) {

                        if (accessor.raf != null) {
                            accessor.raf.close();
                            accessor.raf = null;
                        }

                        if (accessor.wr != null) {
                            accessor.wr.close();
                            accessor.wr = null;
                        }
                        accessor.isClosed = true;
                        eldest.setValue(accessor);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            ret = true;
        }
        return ret;
    }

    public int getSize() {
        return size();
    }
}
