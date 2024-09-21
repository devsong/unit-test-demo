package unit.test.demo.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author guanzhisong
 * @date 2024/9/20
 */
@Slf4j
public final class BcCipherUtil {
    public static final String AES_MODE_ECB = "AES/ECB/PKCS5Padding";

    public static final String AES_MODE_CBC = "AES/CBC/PKCS5Padding";

    public static final String ALGORITHM_KEY_RSA = "RSA";
    public static final String ALGORITHM_SIGNATURE_RSA = "MD5withRSA";


    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * md5散列函数
     *
     * @param input
     * @return
     */
    @SneakyThrows
    public static byte[] md5(byte[] input) {
        MessageDigest md = MessageDigest.getInstance("MD5", BouncyCastleProvider.PROVIDER_NAME);
        md.update(input);
        return md.digest();
    }

    @SneakyThrows
    public static byte[] sha256(byte[] input) {
        MessageDigest md = MessageDigest.getInstance("SHA256", BouncyCastleProvider.PROVIDER_NAME);
        md.update(input);
        return md.digest();
    }

    /**
     * aes ecb模式加密,相较于cbc，安全性较低
     *
     * @param key
     * @param input
     * @return
     */
    @SneakyThrows
    public static byte[] encryptEcb(byte[] key, byte[] input) {
        Cipher cipher = Cipher.getInstance(AES_MODE_ECB, BouncyCastleProvider.PROVIDER_NAME);
        SecretKey keySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(input);
    }

    /**
     * aes ecb模式解密,相较于cbc，安全性较低
     *
     * @param key
     * @param input
     * @return
     */
    @SneakyThrows
    public static byte[] decryptEcb(byte[] key, byte[] input) {
        Cipher cipher = Cipher.getInstance(AES_MODE_ECB, BouncyCastleProvider.PROVIDER_NAME);
        SecretKey keySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        return cipher.doFinal(input);
    }

    /**
     * aes cbc模式加密，安全性较高
     *
     * @param key
     * @param input
     * @return
     */
    @SneakyThrows
    public static byte[] encryptCbc(byte[] key, byte[] input) {
        Cipher cipher = Cipher.getInstance(AES_MODE_CBC, BouncyCastleProvider.PROVIDER_NAME);
        SecretKey keySpec = new SecretKeySpec(key, "AES");
        SecureRandom sr = SecureRandom.getInstanceStrong();
        byte[] iv = sr.generateSeed(16);
        IvParameterSpec ivPs = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivPs);
        byte[] data = cipher.doFinal(input);
        return join(iv, data);
    }

    private static byte[] join(byte[] bs1, byte[] bs2) {
        byte[] r = new byte[bs1.length + bs2.length];
        System.arraycopy(bs1, 0, r, 0, bs1.length);
        System.arraycopy(bs2, 0, r, bs1.length, bs2.length);
        return r;
    }

    /**
     * aes cbc模式解密，安全性较高
     *
     * @param key
     * @param input
     * @return
     */
    @SneakyThrows
    public static byte[] decryptCbc(byte[] key, byte[] input) {
        if (input.length < 16) {
            throw new IllegalArgumentException("decrypt len must greater than 16");
        }
        byte[] iv = new byte[16];
        byte[] data = new byte[input.length - 16];
        System.arraycopy(input, 0, iv, 0, 16);
        System.arraycopy(input, 16, data, 0, data.length);
        Cipher cipher = Cipher.getInstance(AES_MODE_CBC, BouncyCastleProvider.PROVIDER_NAME);
        SecretKey keySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivPs = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivPs);
        return cipher.doFinal(data);
    }

    @SneakyThrows
    public static byte[] rsaEncrypt(byte[] privateKey, byte[] input) {
        PrivateKey pvKey = deserializeRsaPrivateKey(privateKey);
        Cipher cipher = Cipher.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
        cipher.init(Cipher.ENCRYPT_MODE, pvKey);
        return cipher.doFinal(input);
    }

    @SneakyThrows
    public static byte[] rsaDecrypt(byte[] publicKey, byte[] input) {
        PublicKey pbKey = deserializeRsaPublicKey(publicKey);
        Cipher cipher = Cipher.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
        cipher.init(Cipher.DECRYPT_MODE, pbKey);
        return cipher.doFinal(input);
    }

    @SneakyThrows
    public static byte[] rsaSign(byte[] privateKey, byte[] input) {
        PrivateKey pvKey = deserializeRsaPrivateKey(privateKey);
        byte[] keyBytes = pvKey.getEncoded();
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_KEY_RSA);
        PrivateKey key = keyFactory.generatePrivate(keySpec);
        Signature signature = Signature.getInstance(ALGORITHM_SIGNATURE_RSA);
        signature.initSign(key);
        signature.update(input);
        return signature.sign();
    }

    @SneakyThrows
    public static boolean rsaVerify(byte[] publicKey, byte[] input, byte[] sign) {
        PublicKey pbKey = deserializeRsaPublicKey(publicKey);
        byte[] keyBytes = pbKey.getEncoded();
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_KEY_RSA);
        PublicKey key = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(ALGORITHM_SIGNATURE_RSA);
        signature.initVerify(key);
        signature.update(input);
        try {
            return signature.verify(Base64.getDecoder().decode(sign));
        } catch (SignatureException e) {
            log.error("verify sign error", e);
            return false;
        }
    }


    /**
     * 获取私钥
     *
     * @param privateKey 私钥数组
     * @return 私钥
     */
    @SneakyThrows
    public static PrivateKey deserializeRsaPrivateKey(byte[] privateKey) {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_KEY_RSA);
        byte[] decodedKey = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 获取公钥
     *
     * @param publicKey 公钥数组
     * @return 公钥
     */
    @SneakyThrows
    public static PublicKey deserializeRsaPublicKey(byte[] publicKey) {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_KEY_RSA);
        byte[] decodedKey = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        return keyFactory.generatePublic(keySpec);
    }

}
