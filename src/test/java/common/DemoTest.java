package common;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class DemoTest {
    public static final String key = "q1w2e3r4t5y6u7i8";
    /**********************************************************************************************/
    /*AES加密解密方式一：此处使用AES-128-ECB加密模式，key需要为16位。
        1.这种加密解密方式，对应JS（js-DES-AES.html）中的AES加密解密方式；
		2.加密解密的key必须是长度为16的字符串（对应128bit = 16 * 8bit）；
	*/
    // 加密
    public static String Encrypt(String sSrc, String sKey) throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        byte[] raw = sKey.getBytes("utf-8");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");//"算法/模式/补码方式"
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
        //此处使用BASE64做转码功能，同时能起到2次加密的作用。
        //return new Base64().encodeToString(encrypted);
        return Base64.encodeBase64String(encrypted);
    }

    // 解密
    public static String Decrypt(String sSrc, String sKey) throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        byte[] raw = sKey.getBytes("utf-8");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        //先用base64转码
        //byte[] encrypted1 = new Base64().decode(sSrc);
        byte[] encrypted1 = Base64.decodeBase64(sSrc);

        byte[] original = cipher.doFinal(encrypted1);
        String originalString = new String(original, "utf-8");
        return originalString;
    }

    /*加密解密时密码器的输入输出都为2进制数组，这里我们用Base64在加解密的中间过程中进行了转码，所以可以以字符串形式展示加密后的密文，
    也可以使用下面的方法将二进制数组转换为16进制的字符串来表示，同样起到转换的作用*/
    //将二进制数组转化为16进制字符串
    public static String parseByte2HexStr(byte buf[]) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    //将16进制字符串转化为二进制数组
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return new byte[0];
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }


    public static void main(String[] args) throws Exception {
       String password= DemoTest.Encrypt("admin",DemoTest.key);
       //String password= DemoTest.Decrypt("1vsqvCsGNXW+d6rHFKo0aQ==",DemoTest.key);
       System.out.println(password);

    }


}




