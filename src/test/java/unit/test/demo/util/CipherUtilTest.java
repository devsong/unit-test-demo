package unit.test.demo.util;

import com.google.common.base.Charsets;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.assertj.core.api.Assertions;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;

import static unit.test.demo.util.CipherUtil.*;

/**
 * @author guanzhisong
 * @date 2024/9/10
 */
@Slf4j
class CipherUtilTest {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    // 密钥长度128位 = 16bytes
    private static final byte[] key = "1234567890abcdef".getBytes(Charsets.UTF_8);

    private static final String originMsg = "hello";
    private static final byte[] msg = originMsg.getBytes(Charsets.UTF_8);


    @Getter
    public enum RsaKey {
        KEY_1024("MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALJLWklvAMl23DDXWAiaoNWcNE5Fd5zq+3MkN7JvC+LPe2MpSrrOzEauNeDhKMtqnviLjqtD2fTzAEke2aEpJc8JP5KlgNSmkW5lZF0rqejSJqbI09CNyqIwZnhh1GC63ZRGJL4YoH+/q5KjdG1zCbn+VPiwX37ZyPFmCMs3SdPFAgMBAAECgYAeiHN9jGYq2jFSpuozTQG6mx3FArX9eCtYGE12f/hd3vDPNkDPNX97ojVlXcHN9iQjxciIsV23yk6T9xXHeOO5owrwixYgk3FYjqR2CB2i0NE/I3LPKDHv3i7LZMfk4C9Tey5md9WKjNoQq0l4+2ShqpRW02meUqdRjtjlzE0xKQJBAPrGtKwNldwgMW9qLllWeQ/kIicRH1EImEUg8BKV+OHqffj9mq3nKXulYHJ3HrFgLMcP1YtqPkLnDq4jm1GO0o0CQQC2AiD14duPltKty5cFSmNqAeBLVd1J5O47HRPaORTqOqSycUXAbnpWDX6lv+RP3JZRYabUudDmloRpxdIAhVQZAkA6gcWBJqJB+xjNf55UpYIVHQqCxtHhm/m6lxarn2Wzf3vobXJ7BCV/fXPlALmedGu9/Ym6vwzLmxCnvBLukj25AkEAjMlcwZWxhoJ9+N2QthDykV9eJigHAk/JT4SQhK6+1OgD2dvwvoc2rjXGD2Q9sODKjazhpY8OXi9zwXW5mZmIkQJBAKEUbNWGpNtr9snpTH98bcsyWu8cS00cmufyygVS3RkyKUo+ccBcbcO4TP0FYE3Exw3ykZ4IPSNQ9IT2aYcgJo8=",
                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCyS1pJbwDJdtww11gImqDVnDRORXec6vtzJDeybwviz3tjKUq6zsxGrjXg4SjLap74i46rQ9n08wBJHtmhKSXPCT+SpYDUppFuZWRdK6no0iamyNPQjcqiMGZ4YdRgut2URiS+GKB/v6uSo3Rtcwm5/lT4sF9+2cjxZgjLN0nTxQIDAQAB"),

        KEY_2048("MIIEuwIBADANBgkqhkiG9w0BAQEFAASCBKUwggShAgEAAoIBAQDI8P/gBNTP3DMjxJSoqS9FvZZFF/xNLoZXVpwpd2TX+IZZUUvQ5ui2Z/xyyiMhUzHwTvcwIxU+KAqV7wZIFvwiFNf5SfW2tboTjIP2JWv8jcKZyXammG4/m4sChhd/cHyAoY9IweJcIJZAY7M26kafW+b7nv3KjOscU+E8oq1KVUavQ8ZmNghUZS5vUCVE3yQ6A64RtXgrgbvh4s6i1+ww5lSihHn/g+4fe1uyVcKSsE3ID7Bp7j3G1yoHiHi2skcZ6sbhsrZrcpP7cbLdI72NDMp0K1UKM8mG8g4u4s4BU22nOyuiba0acw/nJvkDq5HuAkGbtEp/epP+KxpZw1rlAgMBAAECgf88uVoehyzd825rvruZxlrap1PTVekwJstRr5LZGjl1DKDqryATAdE9ZxzSCgcnMkbM5SxuAoDjLpwV/mMz8IeHLxIo5qT+c63zlx1C3dEVNDEgmL8s5hREUa2hNBNuoaOZtZwH87vN7Lnow3KsVB1+NmDa3ZZiyZ4CHFcmtoqI9cW/O2GTRAiLclpbbkShGb3Vi1RfH1MCqfa0kilAUy4T6ZRIM2u3aivI39SpsOL5uns7Mts2MTDvq1tPPiuLnNu04cDbxIXfeA1CMUextS7PqNSUytSLQ0994jSfwA7J7pmLqACelR8LM6zmN6CxsLSZKAnloEogMTeB7y/JdvECgYEA57yqdsg7advFZyYY4ld+7UElWWJ9DAfLsry8IDcNXEQsLUqt1srRQBR/VU9ED5pvDfbHjdV5jYON/ryEWCU1y+4KaOytq/MSa7BOEnGdZvGuXEB33zXvqH1oXLyddyXvkTNoycHZY3HGr1vGvxIQoTj6nwTFhR9asLXRIUUCRFECgYEA3frogmMTrSlqbM4jph0rdTW7vu0tu7GCJYt367gH6mgZgsD3ZLo50NGAmSDYWJQT94Lsb5yzBqkVevZf6vSHcMClHR7a6vftEb2seGhY7SmqdTunNo1PS+EuaCMhoiPIj+8L5eFEo5PxoHTdSBJ5+JnrT9i+vkE7206sva6I7FUCgYEAoOwM2+t0BrDMxjJYeWUVCSCCFS4lc+M9+uLBKQXRy06z9sezLEMvR/Zz7gPbWZJ+qA3EyaOCg1jllA3JQYSldPXaWL5yY8Op+Qo3TOuEyGXwFLPYCjLkN/bOMAfyuTiowgYXMHPiyAHMXR24SBr9XVe6Ag9IModxbOD1VssbJeECgYAji0+7wR2fdqhB7+gpazZQOGBtvBC8oznRxTlYl0k9MZoUI835ZRBo7YMIAghidqOT2HT/kjngcSatX9IQD0O4vEA0H4KyaSye0J5TUXIb17Yhu4GU7J+r6MzDhAyYp94vnflrv9x7zO694LimF79MHWleibhRQqTRAxIJ9W6bWQKBgFE6yuRyz5Hp31hhO8QOaoJABHcTiaJTCFCvSS6Ag8KoNAI8qp+XQQPMMtW4eIV9T6mrspS2y7VF1k7nIbJTg7DnVYoZJs8ZILejnUE8zh6O4dAmcVtN7+fBwkDk+T3WbWKu/I07uDzTnoGeF+ncvjB8fhBiLOHrAS1+onYa5oSG",
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyPD/4ATUz9wzI8SUqKkvRb2WRRf8TS6GV1acKXdk1/iGWVFL0Obotmf8csojIVMx8E73MCMVPigKle8GSBb8IhTX+Un1trW6E4yD9iVr/I3Cmcl2pphuP5uLAoYXf3B8gKGPSMHiXCCWQGOzNupGn1vm+579yozrHFPhPKKtSlVGr0PGZjYIVGUub1AlRN8kOgOuEbV4K4G74eLOotfsMOZUooR5/4PuH3tbslXCkrBNyA+wae49xtcqB4h4trJHGerG4bK2a3KT+3Gy3SO9jQzKdCtVCjPJhvIOLuLOAVNtpzsrom2tGnMP5yb5A6uR7gJBm7RKf3qT/isaWcNa5QIDAQAB"),

        KEY_3072("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",
                "MIIBojANBgkqhkiG9w0BAQEFAAOCAY8AMIIBigKCAYEArJJL4mfqpN6yPZKt9toSPxCPAu33/Sof/99NizvMahYzy/ltpg5kSSNKPLjnDCiXW92adDhvNP/+0GJEcVS6VjOs/ynM9ttMNWhyi0d38ireUCGv4hEz41H/5YJb8kPr4vNYIRarHshC4fV6cc/ukAkd2MjypR0XpotrYmWdBMKnNmcqRck1gDa43Tjqpei3b7BPwGvL/qeU81YgJvv2owHNfApilrX+5f/2HUAT1gjt9K2uyS2E2acMxzYceNWayslskBZ5ClyaGpvV5zdR7B4aPd8vXjqU38W4g1N1OT8YQYoDa7DrRAfSrvYCLJu5Rpcs34t9ZWPribduXPKpSNKQm5Ak4zzUYLGT/aodwxK78qFtV1vz9/7b5Rx07Csw4DNaCnOFhl74CNu0ZuGVP3v10uFDaKeK4iXp5GL3Y4yYhsuj5C4VUkZgfRu5YXnkQZN908IHCzgzR0YM7yv/rmG3T1ILNA3OF46TD1XHwwZvSrQxgFMVD+wTQW5qKYC/AgMBAAE="),

        KEY_4096("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",
                "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEArAipdFYU6eikLxz8G7NJ2iKmDXhpEK2l1hHSqFjuu2Dw3/7ozcjkTiIgERj1hgryoR6kfd/VX2QVQ3goighctpZWJp8z4lvtpnMwa8tVETrqtx1/p+SrkSz2fR70U6Q00cXLo624d9aMl/q/NG3DA7XuvtYYfoBKGxdkCSZc4CdzQUrFlO7gypEWdOMS7Nmv3NdUOq6lmb+fTBzm+yCEppJm6VvVPWTfZCvTxvyTAnCpbdSsIERaALp6RJYI26TDMDugGGgHvehFqurWuZ4T4QiKsABNAidkvzuIsDbyGBBgz/LvXKjuAshcQGB7yVrtzvh9wBCKgrqC9uBSQQp8r5U0VPz5Ec+9oWSyOQq1CztaY/+7BaTzQ6dT4V0SVes8Q3EctlYvK7Vj49tj/fZzloYK956kW04btVj6+NV0+RgGc3xRRAnwSz3jhFleyLw1p3mr+j20wBOxxbr6SWwOyzRvZ7u+Hp0HY6o5LRHJIryyQ2ViAzlQP5AkOktGAK/W21taN7VU7V7TOaBEJ2SRAlgHisbTEbkruIP9czGsrVXpjJvCRUcG9PkF6G4gQpZOj0dlnykTP8teljdvlVvNzdL6dheO2tpZtBQdw4cW3s2E3ADJxn3w9ccD4qN8PcAgD0ogoIoOMpoTZzKSR0F4hT6eePGblORmnSFIOiM/YrUCAwEAAQ=="),

        ;

        String privateKey;

        String publicKey;

        RsaKey(String privateKey, String publicKey) {
            this.privateKey = privateKey;
            this.publicKey = publicKey;
        }
    }

    @Getter
    public enum EccKey {
        KEY_192("MHsCAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQEEYTBfAgEBBBj6gPEAaoGZtiHQFlCKNVr2cbilsGzlw2egCgYIKoZIzj0DAQGhNAMyAARozD0yrBmeiIHN9gpYOWBfh3JsAfGxDn7iOpseaV7q09MSz6Fgb5CyTqEcb4d6ROo=",
                "MEkwEwYHKoZIzj0CAQYIKoZIzj0DAQEDMgAEaMw9MqwZnoiBzfYKWDlgX4dybAHxsQ5+4jqbHmle6tPTEs+hYG+Qsk6hHG+HekTq"),
        KEY_239("MIGNAgEAMBMGByqGSM49AgEGCCqGSM49AwEEBHMwcQIBAQQeLKOOKmU93cpOkhyUKL8xzL7jadfS+zQDM43T7/W6oAoGCCqGSM49AwEEoUADPgAEL9/HGDkUmtWPw4F2BS1WOwa0WgyF7rDbA4LF+QTOSYISvVRaLyK9s8307+RT3FOYFpSw/US4GNrGltrH",
                "MFUwEwYHKoZIzj0CAQYIKoZIzj0DAQQDPgAEL9/HGDkUmtWPw4F2BS1WOwa0WgyF7rDbA4LF+QTOSYISvVRaLyK9s8307+RT3FOYFpSw/US4GNrGltrH"),
        KEY_256("MIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQgwKXpJX4ZI7vr6mhMqTvUV3BOV04HTArYNr/ErLs0442gCgYIKoZIzj0DAQehRANCAASyFzo2Qb+CrpB3ACsRpgC72hVZowzbyqTasBRV7rW/Yukz+RkRlL2ukuS5cAHEkbKHmb4jitj62FSyjIb/sDy5",
                "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEshc6NkG/gq6QdwArEaYAu9oVWaMM28qk2rAUVe61v2LpM/kZEZS9rpLkuXABxJGyh5m+I4rY+thUsoyG/7A8uQ=="),

        KEY_224("MIGBAgEAMBAGByqGSM49AgEGBSuBBAAhBGowaAIBAQQciKP5kaDolid1Vky1FXBJxUO8lwhnG6aOw3yOFaAHBgUrgQQAIaE8AzoABOImxFT3WH8T5miotvP6hU3NY8SbrQPTNGLVPB5gw7MYSTcpWH4qtsJnghhA/pEWaNiJYz7gdVDI",
                "ME4wEAYHKoZIzj0CAQYFK4EEACEDOgAE4ibEVPdYfxPmaKi28/qFTc1jxJutA9M0YtU8HmDDsxhJNylYfiq2wmeCGED+kRZo2IljPuB1UMg="),
        KEY_384("MIG/AgEAMBAGByqGSM49AgEGBSuBBAAiBIGnMIGkAgEBBDBMXZmxZ+540rv6oltXapDGrFxikh0b2sZv+J1uoAAGXcTE9p0w4kJonZHqIGaeQ0ugBwYFK4EEACKhZANiAATL5jr+W8mlg/cT0HMicvhBquMyp85Lwr/riV+Ld8ojZ1m85t7y4GyrYzyVlQywtxw1sBnabtfzWvuOIajIoaSBVFmEd8kEgpfwwt7N/t/Dg3jKLP2b+np6Fy6YvJNr23s=",
                "MHYwEAYHKoZIzj0CAQYFK4EEACIDYgAEy+Y6/lvJpYP3E9BzInL4QarjMqfOS8K/64lfi3fKI2dZvObe8uBsq2M8lZUMsLccNbAZ2m7X81r7jiGoyKGkgVRZhHfJBIKX8MLezf7fw4N4yiz9m/p6ehcumLyTa9t7"),
        KEY_521("MIH3AgEAMBAGByqGSM49AgEGBSuBBAAjBIHfMIHcAgEBBEIA0LOMrmJpG2xa+sJJXFj92Vw/nT7LW3LNTfjj+nHcVsR577Kt9iho9V1iC56PTdknwyoWFAmThVLmkqXREPMDM0igBwYFK4EEACOhgYkDgYYABAFo1T3xKx1O0Ex4IXwjVKLR0Qm0qqAK+FiZTk2/C14VMiSzC56EpsU6/fUX/ey2u7iM4fxtiFd+TBqA2KCIIRLfEAGUa0lLkU2mcmagttgUwvHqqTqNgJ6BlPCtII2x1EGG2P/vLIgia8uisdU0pbCwi826HiqBvXgJmOPoDHxSqMMvvA==",
                "MIGbMBAGByqGSM49AgEGBSuBBAAjA4GGAAQBaNU98SsdTtBMeCF8I1Si0dEJtKqgCvhYmU5NvwteFTIkswuehKbFOv31F/3stru4jOH8bYhXfkwagNigiCES3xABlGtJS5FNpnJmoLbYFMLx6qk6jYCegZTwrSCNsdRBhtj/7yyIImvLorHVNKWwsIvNuh4qgb14CZjj6Ax8UqjDL7w="),

        ;

        String privateKey;

        String publicKey;

        EccKey(String privateKey, String publicKey) {
            this.privateKey = privateKey;
            this.publicKey = publicKey;
        }
    }

    @Getter
    public enum RsaSignMethod {
        NONE(""),

        MD5withRSA(BcCipherEnum.SIGNATURE_ALG_MD5_RSA.getName()),

        SHA256withRSA(BcCipherEnum.SIGNATURE_ALG_SHA256_RSA.getName());

        private String method;

        RsaSignMethod(String method) {
            this.method = method;
        }
    }


    @Getter
    public enum EccSignMethod {
        NONE(""),

        RIPEMD160_ECDSA(BcCipherEnum.SIGNATURE_ALG_RIPEMD160_ECDSA.getName()),

        SHA256_ECDSA(BcCipherEnum.SIGNATURE_ALG_SHA256_ECDSA.getName());

        private String method;

        EccSignMethod(String method) {
            this.method = method;
        }
    }

    @Data
    @Builder
    class Foo {
        private String key;
        private String encrypt;
    }

    @Test
    @SneakyThrows
    void should_generate_md5_success() {
        byte[] result = md5(msg);
        log.info(Hex.encodeHexString(result, true));
    }

    @Test
    @SneakyThrows
    void should_generate_sha256_success() {
        byte[] result = sha256(msg);
        log.info(Hex.encodeHexString(result, true));
    }

    @Test
    @SneakyThrows
    void should_aes_ecb_encrypt_decrypt_success() {
        byte[] results = encryptEcb(key, msg);
        log.info("aes encryptEcb {}", Base64.getEncoder().encodeToString(results));
        byte[] origin = decryptEcb(key, results);
        log.info("aes origin {}", new String(origin, Charsets.UTF_8));
    }

    @Test
    @SneakyThrows
    void should_aes_cbc_encrypt_decrypt_success() {
        byte[] results = encryptCbc(key, msg);
        log.info("aes encryptCbc {}", Base64.getEncoder().encodeToString(results));
        byte[] origin = decryptCbc(key, results);
        log.info("aes origin {}", new String(origin, Charsets.UTF_8));
    }

    @ParameterizedTest
    @EnumSource(value = RsaKey.class)
    void should_rsa_encrypt_decrypt_success(RsaKey rsaKey) {
        String privateKey = rsaKey.getPrivateKey();
        String publicKey = rsaKey.getPublicKey();
        byte[] result = rsaPrivateKeyEncrypt(privateKey.getBytes(Charsets.UTF_8), msg);
        byte[] origin = rsaPublicKeyDecrypt(publicKey.getBytes(Charsets.UTF_8), result);
        Assertions.assertThat(new String(origin, Charsets.UTF_8)).isEqualToIgnoringCase(originMsg);

        result = rsaPublicKeyEncrypt(publicKey.getBytes(Charsets.UTF_8), msg);
        origin = rsaPrivateKeyDecrypt(privateKey.getBytes(Charsets.UTF_8), result);
        Assertions.assertThat(new String(origin, Charsets.UTF_8)).isEqualToIgnoringCase(originMsg);
    }

    @ParameterizedTest
    @EnumSource(value = EccSignMethod.class)
    void should_sign_and_verify_by_ecc_success(EccSignMethod signAlg) {
        String sign = eccSign(EccKey.KEY_256.getPrivateKey().getBytes(Charsets.UTF_8), msg, signAlg.getMethod());
        System.out.println(sign);
        boolean success = eccVerify(EccKey.KEY_256.getPublicKey().getBytes(Charsets.UTF_8), msg, sign.getBytes(Charsets.UTF_8), signAlg.getMethod());
        Assertions.assertThat(success).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = RsaSignMethod.class)
    void should_sign_and_verify_by_rsa_success(RsaSignMethod signAlg) {
        String sign = rsaSign(RsaKey.KEY_2048.getPrivateKey().getBytes(Charsets.UTF_8), msg, signAlg.getMethod());
        System.out.println(sign);
        boolean success = rsaVerify(RsaKey.KEY_2048.getPublicKey().getBytes(Charsets.UTF_8), msg, sign.getBytes(Charsets.UTF_8), signAlg.getMethod());
        Assertions.assertThat(success).isTrue();
    }

    @SneakyThrows
    @ParameterizedTest
    @EnumSource(EccKey.class)
    void should_ecc_encrypt_decrypt_success(EccKey eccKey) {
        byte[] publicKeyBytes = eccKey.getPublicKey().getBytes(Charsets.UTF_8);
        byte[] privateKeyBytes = eccKey.getPrivateKey().getBytes(Charsets.UTF_8);

        byte[] result = eccEncrypt(msg, publicKeyBytes);
        byte[] origin = eccDecrypt(result, privateKeyBytes);
        Assertions.assertThat(new String(origin, Charsets.UTF_8)).isEqualToIgnoringCase(originMsg);
    }

    @Test
    void should_aes_rsa_work_together() {
        // 模拟加密
        String aesKey = RandomStringUtils.random(32);
        String encryptAesKey = Base64.getEncoder().encodeToString(rsaPublicKeyEncrypt(RsaKey.KEY_2048.getPublicKey().getBytes(Charsets.UTF_8), aesKey.getBytes(Charsets.UTF_8)));
        Foo foo = Foo.builder()
                .key(encryptAesKey)
                .encrypt(Base64.getEncoder().encodeToString(CipherUtil.encryptCbc(aesKey.getBytes(Charsets.UTF_8), msg)))
                .build();
        // MO

    }

    @Test
    @SneakyThrows
    void sign_test() {
        byte[] result = md5(msg);
        System.out.println(Hex.encodeHexString(result));
        byte[] privateKey = RsaKey.KEY_1024.getPrivateKey().getBytes();
        String rsaSign = rsaSign(privateKey, msg);
        System.out.println(rsaSign);
        byte[] decrypt = rsaPublicKeyDecrypt(RsaKey.KEY_1024.getPublicKey().getBytes(), Base64.getDecoder().decode("hOnRMjrDYyD+45s9jktVkOoGvkGDmdzHZoP5AitF4ngHS8aMMGToY7dWzpJNEDOyNOabf4DNHrRbZ4dfsnIt91ZBZTO4U3/gV4Gvf43g/zeD0TZ/ggYXdZUdcgh9dS4df9MmbOsynUU31sWJBDs4B8XfHVyJvFE7t87fXfXIDbs="));
        System.out.println(Hex.encodeHexString(decrypt));
    }

    @Test
    @SneakyThrows
    @Disabled
    public void should_generate_public_private_key_success() {
        // RSA常见的密钥长度
        int[] rsaLen = {1024, 2048, 3072, 4096};
        for (int len : rsaLen) {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(BcCipherEnum.RSA_ALG.getName(), BouncyCastleProvider.PROVIDER_NAME);
            keyPairGenerator.initialize(len, new SecureRandom());
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            log.info("rsa len {} publicKey {}", len, Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
            log.info("rsa len {} privateKey {}", len, Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
        }
        // BC支持的ECC密钥长度
        int[] eccLen = {192, 239, 256, 224, 384, 521};
        for (int len : eccLen) {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(BcCipherEnum.EC_ALG.getName(), BouncyCastleProvider.PROVIDER_NAME);
            keyPairGenerator.initialize(len, new SecureRandom());
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            log.info("ecc len {} publicKey {}", len, Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
            log.info("ecc len {} privateKey {}", len, Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
        }

    }

}
