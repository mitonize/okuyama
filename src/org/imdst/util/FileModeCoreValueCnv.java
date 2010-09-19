package org.imdst.util;


import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.BASE64EncoderStream;

/**
 * 最終保存媒体がFile時のConverter.<br>
 * Encode仕様:Key=BASE64でデコード後、バイト配列で返す
 *            Value=なにもしない
 *
 * Decode仕様:Key=BASE64でエンコード後、Stringで返す
 *            Value=なにもしない
 *
 *
 * @author T.Okuyama
 * @license GPL(Lv3)
 */
public class FileModeCoreValueCnv implements ICoreValueConverter {


    /**
     *
     */
    public Object convertEncodeKey(Object key) {
        return decode(((String)key).getBytes());
    }

    /**
     *
     */
    public Object convertEncodeValue(Object value) {
        return value;
    }



    /**
     *
     */
    public Object convertDecodeKey(Object key) {
        return new String(encode((byte[])key));
    }

    /**
     *
     */
    public Object convertDecodeValue(Object value) {
        return value;
    }



    private byte[] encode(byte[] datas) {
        return BASE64EncoderStream.encode(datas)
    }

    private byte[] decode(byte[] datas) {
        return BASE64DecoderStream.decode(datas)
    }


}
