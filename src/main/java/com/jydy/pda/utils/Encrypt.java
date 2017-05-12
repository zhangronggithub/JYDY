package com.jydy.pda.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by 23923 on 2016/11/18.
 */
public class Encrypt {
    public  final String TAG = getClass().getSimpleName();
    /**
     * AES加密为base 64 code
     * @param content 待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的base 64 code
     * @throws Exception
     */
    public static String aesEncrypt(String content, String encryptKey) throws Exception {
        return base64Encode(aesEncryptToBytes1(content, encryptKey));
    }
    /**
     * base 64 encode
     * @param bytes 待编码的byte[]
     * @return 编码后的base 64 code
     */
    public static String base64Encode(byte[] bytes){


        return new Base64().encode(bytes);
    }
    /**
     * AES加密
     * @param content 待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的byte[]
     * @throws Exception
     */
    public static byte[] aesEncryptToBytes(String content, String encryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = null;
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        } else {
            sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        }
        sr.setSeed(encryptKey.getBytes());
        kgen.init(128, sr);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(kgen.generateKey().getEncoded(), "AES"));

        return cipher.doFinal(content.getBytes("utf-8"));
    }
    public static String encrypt(Context context) throws Exception {
        TelephonyManager tm = (TelephonyManager)context.getSystemService(TELEPHONY_SERVICE);
        String str = tm.getDeviceId();
        Log.d("设备ID",str);
        String key = "LPHOON08";
        return aesEncrypt(str, key).replace("=","");
    }

    public static byte[] aesEncryptToBytes1(String content, String encryptKey) throws Exception {
        int keyLength = 128;
        int saltLength = 16;
        byte[] salt;

        // 先获取一个随机的盐值
        // 你需要将此次生成的盐值保存到磁盘上下次再从字符串换算密钥时传入
        // 如果盐值不一致将导致换算的密钥值不同
        // 保存密钥的逻辑官方并没写，需要自行实现
        SecureRandom random = new SecureRandom();
        salt = new byte[saltLength];
//        random.nextBytes(salt);

        salt = "LPHOON08".getBytes();
        // 将密码明文、盐值等使用新的方法换算密钥
        int iterationCount = 1000;
        KeySpec keySpec = new PBEKeySpec(encryptKey.toCharArray(), salt,
                iterationCount, keyLength);
        SecretKeyFactory keyFactory = SecretKeyFactory
                .getInstance("PBKDF2WithHmacSHA1");
        // 到这里你就能拿到一个安全的密钥了
        byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
        SecretKey key = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        return cipher.doFinal(content.getBytes("utf-8"));
    }
    private static class Base64
    {
//        private  final char[] legalChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
        private  final char[] legalChars = "0123456789012345678901234567890123456789012345678901234567890123".toCharArray();


        public  String encode(byte[] data) {
            int start = 0;
            int len = data.length;
            StringBuffer buf = new StringBuffer(data.length * 3 / 2);

            int end = len - 3;
            int i = start;
            int n = 0;

            while (i <= end) {
                int d = ((((int) data[i]) & 0x0ff) << 16)
                        | ((((int) data[i + 1]) & 0x0ff) << 8)
                        | (((int) data[i + 2]) & 0x0ff);

                buf.append(legalChars[(d >> 18) & 63]);
                buf.append(legalChars[(d >> 12) & 63]);
                buf.append(legalChars[(d >> 6) & 63]);
                buf.append(legalChars[d & 63]);

                i += 3;

                if (n++ >= 14) {
                    n = 0;
                    buf.append(" ");
                }
            }

            if (i == start + len - 2) {
                int d = ((((int) data[i]) & 0x0ff) << 16)
                        | ((((int) data[i + 1]) & 255) << 8);

                buf.append(legalChars[(d >> 18) & 63]);
                buf.append(legalChars[(d >> 12) & 63]);
                buf.append(legalChars[(d >> 6) & 63]);
                buf.append("=");
            } else if (i == start + len - 1) {
                int d = (((int) data[i]) & 0x0ff) << 16;

                buf.append(legalChars[(d >> 18) & 63]);
                buf.append(legalChars[(d >> 12) & 63]);
                buf.append("==");
            }

            return buf.toString();
        }

        private  int decode(char c) {
            if (c >= 'A' && c <= 'Z')
                return ((int) c) - 65;
            else if (c >= 'a' && c <= 'z')
                return ((int) c) - 97 + 26;
            else if (c >= '0' && c <= '9')
                return ((int) c) - 48 + 26 + 26;
            else
                switch (c) {
                    case '+':
                        return 62;
                    case '/':
                        return 63;
                    case '=':
                        return 0;
                    default:
                        throw new RuntimeException("unexpected code: " + c);
                }
        }



        public  byte[] decode(String s) {

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                decode(s, bos);
            } catch (IOException e) {
                throw new RuntimeException();
            }
            byte[] decodedBytes = bos.toByteArray();
            try {
                bos.close();
                bos = null;
            } catch (IOException ex) {
                System.err.println("Error while decoding BASE64: " + ex.toString());
            }
            return decodedBytes;
        }

        private  void decode(String s, OutputStream os) throws IOException {
            int i = 0;

            int len = s.length();

            while (true) {
                while (i < len && s.charAt(i) <= ' ')
                    i++;

                if (i == len)
                    break;

                int tri = (decode(s.charAt(i)) << 18)
                        + (decode(s.charAt(i + 1)) << 12)
                        + (decode(s.charAt(i + 2)) << 6)
                        + (decode(s.charAt(i + 3)));

                os.write((tri >> 16) & 255);
                if (s.charAt(i + 2) == '=')
                    break;
                os.write((tri >> 8) & 255);
                if (s.charAt(i + 3) == '=')
                    break;
                os.write(tri & 255);

                i += 4;
            }
        }
    }
}
