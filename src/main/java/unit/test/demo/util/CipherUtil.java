package unit.test.demo.util;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * 基于BouncyCastleProvider封装,部分BouncyCastleProvider不支持的算法，使用jdk本身提供的算法
 *
 * @author guanzhisong
 * @date 2024/9/20
 */
@Slf4j
public final class CipherUtil {
    @Getter
    public enum BcCipherEnum {
        ////////////////////////////////////////////
        // 消息摘要算法
        ////////////////////////////////////////////
        MD5("MD5"),

        SHA256("SHA256"),

        SHA384("SHA384"),

        SHA512("SHA512"),

        ////////////////////////////////////////////
        // 加盐hash散列算法
        ////////////////////////////////////////////
        HMAC_MD5("HmacMD5"),

        HMAC_SHA256("HmacSHA256"),

        HMAC_SHA384("HmacSHA384"),

        HMAC_SHA512("HmacSHA512"),

        ////////////////////////////////////////////
        // 对称加密/解密算法
        ////////////////////////////////////////////
        AES_ALG("AES"),

        AES_MODE_ECB("AES/ECB/PKCS5Padding"),

        AES_MODE_CBC("AES/CBC/PKCS5Padding"),

        ////////////////////////////////////////////
        // 非对称加密/解密算法
        ////////////////////////////////////////////
        RSA_ALG("RSA"),

        EC_ALG("EC"),

        ECIES_ALG("ECIES"),

        ////////////////////////////////////////////
        // Hash/签名/验证算法
        ////////////////////////////////////////////
        SIGNATURE_ALG_MD5_RSA("MD5withRSA"),

        SIGNATURE_ALG_SHA256_RSA("SHA256withRSA"),

        SIGNATURE_ALG_RIPEMD160_ECDSA("RIPEMD160withECDSA"),

        SIGNATURE_ALG_SHA256_ECDSA("SHA256withECDSA");

        public static final String NONEwithECDSA = "NONEwithECDSA";
        public static final String RIPEMD160withECDSA = "RIPEMD160withECDSA";
        public static final String SHA1withECDSA = "SHA1withECDSA";
        public static final String SHA224withECDSA = "SHA224withECDSA";
        public static final String SHA256withECDSA = "SHA256withECDSA";
        public static final String SHA384withECDSA = "SHA384withECDSA";
        public static final String SHA512withECDSA = "SHA512withECDSA";

        ;

        private String name;

        BcCipherEnum(String name) {
            this.name = name;
        }
    }

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * md5散列函数
     *
     * @param input 输入数据
     * @return
     */
    @SneakyThrows
    public static byte[] md5(byte[] input) {
        return mdWithAlg(input, BcCipherEnum.MD5.getName());
    }

    /**
     * sha256 散列函数
     *
     * @param input 输入数据
     * @return
     */
    @SneakyThrows
    public static byte[] sha256(byte[] input) {
        return mdWithAlg(input, BcCipherEnum.SHA256.getName());
    }

    @SneakyThrows
    public static byte[] mdWithAlg(byte[] input, String mdAlg) {
        MessageDigest md = MessageDigest.getInstance(mdAlg, BouncyCastleProvider.PROVIDER_NAME);
        md.update(input);
        return md.digest();
    }

    /**
     * hMacMd5实现
     *
     * @param input
     * @param secret
     * @return
     */
    @SneakyThrows
    public static byte[] hMacMd5(byte[] input, byte[] secret) {
        return hMac(input, secret, BcCipherEnum.HMAC_MD5.getName());
    }

    /**
     * hMacSha256实现
     *
     * @param input
     * @param secret
     * @return
     */
    @SneakyThrows
    public static byte[] hMacSha256(byte[] input, byte[] secret) {
        return hMac(input, secret, BcCipherEnum.HMAC_SHA256.getName());
    }

    @SneakyThrows
    public static byte[] hMac(byte[] input, byte[] secret, String algorithm) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret, algorithm);
        Mac mac = Mac.getInstance(algorithm, BouncyCastleProvider.PROVIDER_NAME);
        mac.init(secretKeySpec);
        return mac.doFinal(input);
    }

    /**
     * aes ecb模式加密,相较于cbc，安全性较低
     *
     * @param key   密钥
     * @param input 输入数据
     * @return
     */
    @SneakyThrows
    public static byte[] encryptEcb(byte[] key, byte[] input) {
        Cipher cipher = Cipher.getInstance(BcCipherEnum.AES_MODE_ECB.getName(), BouncyCastleProvider.PROVIDER_NAME);
        SecretKey keySpec = new SecretKeySpec(key, BcCipherEnum.AES_ALG.getName());
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(input);
    }

    /**
     * aes ecb模式解密,相较于cbc，安全性较低
     *
     * @param key   密钥
     * @param input 输入数据
     * @return
     */
    @SneakyThrows
    public static byte[] decryptEcb(byte[] key, byte[] input) {
        Cipher cipher = Cipher.getInstance(BcCipherEnum.AES_MODE_ECB.getName(), BouncyCastleProvider.PROVIDER_NAME);
        SecretKey keySpec = new SecretKeySpec(key, BcCipherEnum.AES_ALG.getName());
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        return cipher.doFinal(input);
    }

    /**
     * aes cbc模式加密，安全性较高
     *
     * @param key   密钥
     * @param input 输入数据
     * @return
     */
    @SneakyThrows
    public static byte[] encryptCbc(byte[] key, byte[] input) {
        Cipher cipher = Cipher.getInstance(BcCipherEnum.AES_MODE_CBC.getName(), BouncyCastleProvider.PROVIDER_NAME);
        SecretKey keySpec = new SecretKeySpec(key, BcCipherEnum.AES_ALG.getName());
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
     * @param key   密钥
     * @param input 输入数据
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
        Cipher cipher = Cipher.getInstance(BcCipherEnum.AES_MODE_CBC.getName(), BouncyCastleProvider.PROVIDER_NAME);
        SecretKey keySpec = new SecretKeySpec(key, BcCipherEnum.AES_ALG.getName());
        IvParameterSpec ivPs = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivPs);
        return cipher.doFinal(data);
    }

    /**
     * 基础的rsa加密方法,加密内容长度应小于密钥的长度
     *
     * @param privateKey 密钥
     * @param input      输入数据
     * @return
     */
    @SneakyThrows
    public static byte[] rsaPrivateKeyEncrypt(byte[] privateKey, byte[] input) {
        PrivateKey pvKey = deserializePrivateKey(privateKey, BcCipherEnum.RSA_ALG.getName());
        Cipher cipher = Cipher.getInstance(BcCipherEnum.RSA_ALG.getName(), BouncyCastleProvider.PROVIDER_NAME);
        cipher.init(Cipher.ENCRYPT_MODE, pvKey);
        return cipher.doFinal(input);
    }

    /**
     * 基础的rsa解密方法
     *
     * @param publicKey 公钥
     * @param input     输入数据
     * @return
     */
    @SneakyThrows
    public static byte[] rsaPublicKeyDecrypt(byte[] publicKey, byte[] input) {
        PublicKey pbKey = deserializePublicKey(publicKey, BcCipherEnum.RSA_ALG.getName());
        Cipher cipher = Cipher.getInstance(BcCipherEnum.RSA_ALG.getName(), BouncyCastleProvider.PROVIDER_NAME);
        cipher.init(Cipher.DECRYPT_MODE, pbKey);
        return cipher.doFinal(input);
    }

    /**
     * 基础的rsa加密方法,加密内容长度应小于密钥的长度
     *
     * @param publicKey 私钥
     * @param input      输入数据
     * @return
     */
    @SneakyThrows
    public static byte[] rsaPublicKeyEncrypt(byte[] publicKey, byte[] input) {
        PublicKey pbKey = deserializePublicKey(publicKey, BcCipherEnum.RSA_ALG.getName());
        Cipher cipher = Cipher.getInstance(BcCipherEnum.RSA_ALG.getName(), BouncyCastleProvider.PROVIDER_NAME);
        cipher.init(Cipher.ENCRYPT_MODE, pbKey);
        return cipher.doFinal(input);
    }

    /**
     * 基础的rsa解密方法
     *
     * @param privateKey 私钥
     * @param input      输入数据
     * @return
     */
    @SneakyThrows
    public static byte[] rsaPrivateKeyDecrypt(byte[] privateKey, byte[] input) {
        PrivateKey pvKey = deserializePrivateKey(privateKey, BcCipherEnum.RSA_ALG.getName());
        Cipher cipher = Cipher.getInstance(BcCipherEnum.RSA_ALG.getName(), BouncyCastleProvider.PROVIDER_NAME);
        cipher.init(Cipher.DECRYPT_MODE, pvKey);
        return cipher.doFinal(input);
    }


    /**
     * RSA数据签名,默认使用MD5withRSA
     *
     * @param privateKey 私钥
     * @param input      输入数据
     * @return
     */
    @SneakyThrows
    public static String rsaSign(byte[] privateKey, byte[] input) {
        return rsaSign(privateKey, input, BcCipherEnum.SIGNATURE_ALG_MD5_RSA.getName(), BouncyCastleProvider.PROVIDER_NAME);
    }

    /**
     * RSA数据签名,默认使用MD5withRSA
     *
     * @param privateKey 私钥
     * @param input      输入数据
     * @param signAlg    签名算法
     * @return
     */
    @SneakyThrows
    public static String rsaSign(byte[] privateKey, byte[] input, String signAlg) {
        if (StringUtils.isBlank(signAlg)) {
            return rsaSign(privateKey, input);
        }
        return rsaSign(privateKey, input, signAlg, BouncyCastleProvider.PROVIDER_NAME);
    }


    /**
     * RSA数据签名
     *
     * @param privateKey 私钥
     * @param input      输入数据
     * @param signAlg    采用算法
     * @param provider   算法提供方
     * @return
     */
    @SneakyThrows
    public static String rsaSign(byte[] privateKey, byte[] input, String signAlg, String provider) {
        PrivateKey pvKey = deserializePrivateKey(privateKey, BcCipherEnum.RSA_ALG.getName());
        byte[] keyBytes = pvKey.getEncoded();
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(BcCipherEnum.RSA_ALG.getName());
        PrivateKey key = keyFactory.generatePrivate(keySpec);
        Signature signature;
        if (StringUtils.isBlank(provider)) {
            signature = Signature.getInstance(signAlg);
        } else {
            signature = Signature.getInstance(signAlg, provider);
        }
        signature.initSign(key);
        signature.update(input);
        return Base64.getEncoder().encodeToString(signature.sign());
    }

    /**
     * RSA数据验签名,默认使用MD5withRSA
     *
     * @param publicKey 验签公钥
     * @param input     输入数据
     * @param sign      签名串
     * @return
     */
    @SneakyThrows
    public static boolean rsaVerify(byte[] publicKey, byte[] input, byte[] sign) {
        return rsaVerify(publicKey, input, sign, BcCipherEnum.SIGNATURE_ALG_MD5_RSA.getName(), BouncyCastleProvider.PROVIDER_NAME);
    }

    /**
     * RSA数据验签名
     *
     * @param publicKey 公钥
     * @param input     输入数据
     * @param sign      签名串
     * @param signAlg   签名算法
     * @return
     */
    @SneakyThrows
    public static boolean rsaVerify(byte[] publicKey, byte[] input, byte[] sign, String signAlg) {
        if (StringUtils.isBlank(signAlg)) {
            return rsaVerify(publicKey, input, sign);
        }
        return rsaVerify(publicKey, input, sign, signAlg, BouncyCastleProvider.PROVIDER_NAME);
    }

    /**
     * RSA数据验签名
     *
     * @param publicKey 公钥
     * @param input     输入数据
     * @param sign      签名串
     * @param signAlg   签名算法
     * @param provider  算法提供方
     * @return
     */
    @SneakyThrows
    public static boolean rsaVerify(byte[] publicKey, byte[] input, byte[] sign, String signAlg, String provider) {
        PublicKey pbKey = deserializePublicKey(publicKey, BcCipherEnum.RSA_ALG.getName());
        byte[] keyBytes = pbKey.getEncoded();
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(BcCipherEnum.RSA_ALG.getName());
        PublicKey key = keyFactory.generatePublic(keySpec);
        Signature signature;
        if (StringUtils.isBlank(provider)) {
            signature = Signature.getInstance(signAlg);
        } else {
            signature = Signature.getInstance(signAlg, provider);
        }
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
     * ECC公钥加密算法
     *
     * @param content
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static byte[] eccEncrypt(byte[] content, byte[] publicKey) throws Exception {
        PublicKey key = deserializePublicKey(publicKey, BcCipherEnum.EC_ALG.getName());
        Cipher cipher = Cipher.getInstance(BcCipherEnum.ECIES_ALG.getName(), BouncyCastleProvider.PROVIDER_NAME);//写不写 BC都可以，都是会选择BC实现来做
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(content);
    }

    /**
     * ECC私钥解密算法
     *
     * @param content
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static byte[] eccDecrypt(byte[] content, byte[] privateKey) throws Exception {
        PrivateKey key = deserializePrivateKey(privateKey, BcCipherEnum.EC_ALG.getName());
        Cipher cipher = Cipher.getInstance(BcCipherEnum.ECIES_ALG.getName(), BouncyCastleProvider.PROVIDER_NAME);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(content);
    }

    /**
     * ECC 签名算法,默认使用SHA256withECDSA算法
     *
     * @param privateKey
     * @param input
     * @return
     */
    @SneakyThrows
    public static String eccSign(byte[] privateKey, byte[] input) {
        return eccSign(privateKey, input, BcCipherEnum.SIGNATURE_ALG_SHA256_ECDSA.getName(), BouncyCastleProvider.PROVIDER_NAME);
    }

    /**
     * ECC 签名算法,指定签名算法
     *
     * @param privateKey
     * @param input
     * @param signAlg
     * @return
     */
    @SneakyThrows
    public static String eccSign(byte[] privateKey, byte[] input, String signAlg) {
        if (StringUtils.isBlank(signAlg)) {
            return eccSign(privateKey, input);
        }
        return eccSign(privateKey, input, signAlg, BouncyCastleProvider.PROVIDER_NAME);
    }


    /**
     * ECC 签名算法,指定签名算法、算法提供方
     *
     * @param privateKey 私钥
     * @param input      输入数据
     * @param signAlg    签名算法
     * @param provider   算法提供方
     * @return
     */
    @SneakyThrows
    public static String eccSign(byte[] privateKey, byte[] input, String signAlg, String provider) {
        PrivateKey pvKey = deserializePrivateKey(privateKey, BcCipherEnum.EC_ALG.getName());
        byte[] keyBytes = pvKey.getEncoded();
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(BcCipherEnum.EC_ALG.getName());
        PrivateKey key = keyFactory.generatePrivate(keySpec);
        Signature signature;
        if (StringUtils.isBlank(provider)) {
            signature = Signature.getInstance(signAlg);
        } else {
            signature = Signature.getInstance(signAlg, provider);
        }
        signature.initSign(key);
        signature.update(input);
        return Base64.getEncoder().encodeToString(signature.sign());
    }


    /**
     * ECC签名验证算法,默认使用SHA256withECDSA算法
     *
     * @param publicKey 公钥
     * @param origin    原始数据
     * @param sign      签名
     * @return
     */
    @SneakyThrows
    public static boolean eccVerify(byte[] publicKey, byte[] origin, byte[] sign) {
        return eccVerify(publicKey, origin, sign, BcCipherEnum.SIGNATURE_ALG_SHA256_ECDSA.getName(), BouncyCastleProvider.PROVIDER_NAME);
    }

    /**
     * ECC签名验证算法
     *
     * @param publicKey 公钥
     * @param origin    原始数据
     * @param sign      签名
     * @param signAlg   签名算法
     * @return
     */
    @SneakyThrows
    public static boolean eccVerify(byte[] publicKey, byte[] origin, byte[] sign, String signAlg) {
        if (StringUtils.isBlank(signAlg)) {
            return eccVerify(publicKey, origin, sign);
        }
        return eccVerify(publicKey, origin, sign, signAlg, BouncyCastleProvider.PROVIDER_NAME);
    }

    /**
     * ECC签名算法
     *
     * @param publicKey 公钥
     * @param origin    原始数据
     * @param sign      签名
     * @param signAlg   签名算法
     * @param provider  算法提供方
     * @return
     */
    @SneakyThrows
    public static boolean eccVerify(byte[] publicKey, byte[] origin, byte[] sign, String signAlg, String provider) {
        PublicKey pbKey = deserializePublicKey(publicKey, BcCipherEnum.EC_ALG.getName());
        byte[] keyBytes = pbKey.getEncoded();
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(BcCipherEnum.EC_ALG.getName());
        PublicKey key = keyFactory.generatePublic(keySpec);
        Signature signature;
        if (StringUtils.isBlank(provider)) {
            signature = Signature.getInstance(signAlg);
        } else {
            signature = Signature.getInstance(signAlg, provider);
        }
        signature.initVerify(key);
        signature.update(origin);
        try {
            return signature.verify(Base64.getDecoder().decode(sign));
        } catch (SignatureException e) {
            log.error("verify sign error", e);
            return false;
        }
    }

    /**
     * 获取非对称私钥,格式为PKCS8
     *
     * @param privateKey 私钥数组
     * @return 私钥
     */
    @SneakyThrows
    public static PrivateKey deserializePrivateKey(byte[] privateKey, String alg) {
        KeyFactory keyFactory = KeyFactory.getInstance(alg);
        byte[] decodedKey = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 获取非对称公钥,格式为x509
     *
     * @param publicKey 公钥数组
     * @return 公钥
     */
    @SneakyThrows
    public static PublicKey deserializePublicKey(byte[] publicKey, String alg) {
        KeyFactory keyFactory = KeyFactory.getInstance(alg);
        byte[] decodedKey = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        return keyFactory.generatePublic(keySpec);
    }

}
