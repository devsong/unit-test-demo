package unit.test.demo.util;

import com.google.common.base.Charsets;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static unit.test.demo.util.BcCipherUtil.*;

/**
 * @author guanzhisong
 * @date 2024/9/10
 */
@Slf4j
class BcCipherUtilTest {
    // 密钥长度128位 = 16bytes
    private static final byte[] key = "1234567890abcdef".getBytes(Charsets.UTF_8);

    final String private_key_1024 = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMz5Ai2pZRKdToeW" +
            "YCsAvp+f66cz8ORJQ1wGnTYaDHe9z7wXAJZKWZoWb9eL6slMWduLfxggRuSoZngr" +
            "qs8ifNBcm31eaaPzTlhdyqwdi0ds0e+6MiKFmTtkVJFOM9FJYQACUDy+Rt7SS1mu" +
            "3iGc6GRXjD0wGNfGkexCkhZZlLURAgMBAAECgYAYIekjvyHrQyIPzKCd74SB/q4c" +
            "DX17IzUPDKhNyltVbb+bgYnMKAzMm3+nfQ2zjY22z4GzMXmnSjxmQZ3mrUTMfTRH" +
            "fc79I5GXRYyMJZkJPokvWj0kDyKUfQ+LIbiVn7H3tRoFL0UgIwkP1Rspg8pBWF1J" +
            "mvNWu1yexsZVdPV9EQJBAPo29GLoVzQkQfyMzwo5quFG90SQRR2pFKn/N7V688Ld" +
            "hmn0wuLeh9Dl7e0GqG4DEpGEUJCCtOTCkY6crSSp5B8CQQDRtkMbZ/yjEl7eZPUG" +
            "aBP/2ZsMxibJQrXNcVh+MHW8aGv07rWqwjR+xL7DM8DhzFUBIgDJn+asnUBQQXJj" +
            "IsDPAkA+d/3nu64Z3HuE+/qsyNz6xWbyfTpkP1RjZM6ZxoqZOglW5r7JOVmbM+yE" +
            "4samQKogHnKmwZs8Cb+hZUhtAOVtAkEAoVbLwiX6Y6saeZbKO6eO22OaufUomeZM" +
            "4EW3gz3dFppvk/yin0DSCkVrTdpfJliBfWGfoX73hQ1mEViInGR8WQJBAKrShG7P" +
            "g7JEARgEm/sWyLqReXeWQ0/id5fjQjmIvGvIDhUVTbllMHd5TGDNCn/qBa55NeH1" +
            "Iv063e/8wQYi33Q=";
    final String public_key_1024 = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDM+QItqWUSnU6HlmArAL6fn+un" +
            "M/DkSUNcBp02Ggx3vc+8FwCWSlmaFm/Xi+rJTFnbi38YIEbkqGZ4K6rPInzQXJt9" +
            "Xmmj805YXcqsHYtHbNHvujIihZk7ZFSRTjPRSWEAAlA8vkbe0ktZrt4hnOhkV4w9" +
            "MBjXxpHsQpIWWZS1EQIDAQAB";

    final String private_key_2048 = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC7vyNakPQfd1Tn" +
            "YLSiyyPhEKduqJAmUYtuufCn7hf2cEXS1XiubmA87pWREVldhlVvuQQwEG1bK9ix" +
            "DTmbgoNi/OPJGGa4A3U3mbf5AfN6o13HO/Y8OCRmrIHdgCOyzly2LAzAF0+ojvqB" +
            "GnSQEGrxQ4IsqNWFMpjMlKch8SOIux7VR6bKLspLqxMxcO/MhViFoJk388vheulE" +
            "s+zwoRcAJbZGfSWHpXpyZORBDCfmhNEamJLeWhaukAIcHcxd/ww1T1NXQCSvOywY" +
            "MiVThImiahthLrLgyFzjmcSdQaMwmd9WzkH+7b0uFIHamluCzPNN61o6RKKYPvlS" +
            "WkPlNxqZAgMBAAECggEAH6wvGqg5htVqzV3TG2OJuf5FQLXPG6fGDffwUWQpRrnK" +
            "DbdVlJeqRJ07Pi3fFZgD8cUUn8clq6vJ8pR4k3OBFsHy64niSkjLDYYxMyDRIm9M" +
            "8r962s3d1jRj9CoZ26BUo6bqv70vAxCb0gvUfjlG0UzCOSaCXuYaQOkTCPbVN8uP" +
            "fvKQEX6BnBazU8pf837l+aawWPJMX7/6PaOF2M0ngYfOCIANKIK/hWhfUpXCptXr" +
            "BGtQ2eSQWunCI1CbISgWY44awkD2Ggr5GBSFQq/Q7UdwbkYZsZWrq/rCXfRZqq7c" +
            "S4mOlDnsLVQmqMrmOuZF/top/QWOERzrU3imI4xtwQKBgQDdTzLk1VVzqm7LOyJk" +
            "dpUYbmt4uMRHk9W3zlWabbqLO51oJbjasg8IgMxI1ApD0sKC8Svio6l2U0c9N2SG" +
            "xK+8p13ISdCNrSz0/du6W1ydkXI+5gNHS95jriZmpNCDoFysgnzN3RiAkNul5oxN" +
            "mMRIDmCoLobBBkyMdU+/UTuDjwKBgQDZLR5psFf4SH+4twIjprquql1HCPEnRBjr" +
            "3idpMhEkzi5KC7GjLiJMtQaHiWqdZRDNvLpd3NVv1gxzFe8G8oU5zBRoOCPRqL7c" +
            "Q+Lj8toJxZFKwEw1UGPskwhWW/HffkI5QdgSv3Wk68F5G0oYJz5eEfgm80z/y3yy" +
            "Ijp1GyXLVwKBgQC1c8hAw4GqsmThEP0j7e5U98P6WVPldBrY975MPgejPR/UUol8" +
            "Fl4pFKFqXSCFd3qwLdlTm+jH8YQJdy9fKEkGnxtPquXqI8EmpjXQWDRZpPVNCP5L" +
            "KLOQeytVIXEtWwBhGSQHDm6Wiy7smVkUDltw0QkDHBK0lNuSlmTqckoflQKBgDUM" +
            "8XXtzr9mrlRn2AzvaIDMOvbkN0xZNSFA5i2rPkbYWrRZwO3UkipcuhiXt8R08LLh" +
            "HEFbdYPZLLdcL0RqL6MbuAxxUkafyuexaZY3N43VhhVVfggz+ATNMeculJn8lJ65" +
            "99c0j79Wm69CvEnEJFcEWrLhEfkWMwKsm80JcaUHAoGAKCCK50wA9mlWZkJNUONp" +
            "221Z6uBbXfR9Pz2CVC4RJGSYJOSTOdbzOofrRob2eRj4Yh1NVifQP3syYPKQF6w+" +
            "ARk7qpT05oAWR/RisHfVkM6EBHabyOvWyrmmugTcWS3ILQ+HE3LO2PpCNSbHvPOa" +
            "dHDVg+/DbX7p/w8oFr+L6bA=";
    final String public_key_2048 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAu78jWpD0H3dU52C0ossj" +
            "4RCnbqiQJlGLbrnwp+4X9nBF0tV4rm5gPO6VkRFZXYZVb7kEMBBtWyvYsQ05m4KD" +
            "YvzjyRhmuAN1N5m3+QHzeqNdxzv2PDgkZqyB3YAjss5ctiwMwBdPqI76gRp0kBBq" +
            "8UOCLKjVhTKYzJSnIfEjiLse1Uemyi7KS6sTMXDvzIVYhaCZN/PL4XrpRLPs8KEX" +
            "ACW2Rn0lh6V6cmTkQQwn5oTRGpiS3loWrpACHB3MXf8MNU9TV0AkrzssGDIlU4SJ" +
            "omobYS6y4Mhc45nEnUGjMJnfVs5B/u29LhSB2ppbgszzTetaOkSimD75UlpD5Tca" +
            "mQIDAQAB";

    final String private_key_4096 = "MIIJQwIBADANBgkqhkiG9w0BAQEFAASCCS0wggkpAgEAAoICAQC9U6hbAh9D3CFX" +
            "Gd3ir0kmY0+Sl6fJlX4OrKLmDhS1mqLGivLEK8CMjdeX03Sfvt1BvzbxIX0ahTgL" +
            "tEMOyEV0iVOjQs41iI7/8oXyhnbfCFPMKq7BfjtrAzmS/V4sAOnchZTqa4LAbxOq" +
            "Azmm037kQAGiK5QydFDYGebwyk5q+yVi9/vLYxiGIRLHuPsdq4PEw7WWPbgjGpvn" +
            "xzoygV+SNbvMXiKCiEv/ESc7mS8D3vPwxzEvnVnl3vgdd/Vn0zvIuTkp5DyhoEBI" +
            "oERWGHRsllJsTplvla8TMIeaz4fyP9bklVbBGVpVcwbdLyhwjZKRD7sEk5CV25EB" +
            "TU+dpFgZBjQvZoPqmOsuD2wIWOK18R5QcfcR4BgBYVfRuZELWva1emaTycx39Jp4" +
            "xrtkFvZdrvV+j8PrmJ7AsopFopJrRyXXJvUxPemq/WRm73fQp96HPpkNkzgFmoii" +
            "D1y4XeKjJ0UUJtVwybRbkeOiWseVUBOTBOCAnCD6EGNEJvZxKoZFNBtzmba1lAeo" +
            "IY0+fjKJhJW0Qyx6hKHC6h5ZSBnnL7j1eJA+M7a5YtRh0wqMiUjAjdlkffsaV94X" +
            "WZtzlaF7I1ola/cxYv4dZ4wvM6o60qos21lMVnLUMD8qKUkyssTyoaJm0JdICfTi" +
            "tR46IkFALpWhHWriANMhWI/vpnrQnwIDAQABAoICAHQzhIFXLBlaYpwzJByduSf2" +
            "jEBNFI6FUnlvPAw3WD6eSonRfm8o62VZKZnmDw589xADbJVTGJXObjfAy+emw/pi" +
            "zxLFTgtNZXdyQwQ3w33gfo4xJFFeSNnFgPDW0ZXweZj/5AZ9NiMmCcuvbxMVfCxb" +
            "nKUMWkkdWwFH5JallLvUqX209rY+fC7W3c/3mSj8vENoW2t7NilVi5sWjIGzJhV3" +
            "JWyp2kSDoyFKReoueszFarO9jlLLIQIVd/OeTOEMpQTtqkgr7YrF8K6ir06oc8yx" +
            "9qTDGSmDlZERmClMdwQYaqyhfD3IcclAcFYRO6DZQ7wFC6QjbfqMaNODv/nhhK/H" +
            "MbvQq6uZUN7R+30vdf63Qn1ltcY9n5AMQTxFYlx8EtNW3TzG0w52lP9wxHrGp1Ss" +
            "e1rb4GuPX/fqyjibJHDpumTYBq7SQ9EzEdvjlYdfGxrm5TAOt67cx6A3xEzfy4BU" +
            "XgevV/W86AvOV932QFhAGYpIe9tiGunJqDAiBo24F6p4AX2hJaR+GY7bWmbQz/fF" +
            "tWZZ2US8uC56nW3MTeAZ8L2x8lpuKIWDOgN2I7U1Dv2LRwG+R6KO5kzkQJZj9T9y" +
            "Drrndl8w9o/mqkLNpalHsijp60MYHvv0XQHAftD1uVtAhjl1depLDmKcep/YrlfM" +
            "6/zoU6Js/5lFVn2007EBAoIBAQD3G5eu15fhCqAzQD87IAszpydqQrTspi5dYgh7" +
            "HYZ0bZLtvOkN8e1dyeDZIZw87b+yLCv4W29f/pTcTWCRsQR8SVITZ13ejsLwlCRZ" +
            "dpiNApA+YHopWqsXqQFX4K/focZKsL4bUuHSTJKVGNVZDRVTV04Q09mOqB68RSqU" +
            "UVk8v1AtVzQQ+9h1OaKurGD9H/y0JYGHmEDE3zu7yiZdMCiia9hmHZ8+z5VSoTHD" +
            "H240WlLhy7VDZDJ4PDerWxXLG8iOF/70mvfRFNkKQwT2u4m+aXR9PUDf5NEiEZN1" +
            "/XRn5k2UWj896vHxcPsEUPTq3LG0rf+iWM6eXXV8pEJFJKHTAoIBAQDEI8ZXF0Si" +
            "T81iNxgRAu7htY1hzb6VhUngnfRgrLxOTfnBgjxTEmmI8aZZ7DB2OiLZ288VTL1+" +
            "0JiFx1yUdptzN0JczE45WxaK2/omff4iYP4dwEVJBrY7PvWliKFqV+Ae+53aBeLx" +
            "67N18juBBsesLTv6uyoExshb8iYifN9R8YFzpfgIZ1PUe0DIw4ZEPKcm6OQ6DdG+" +
            "JnQe+okA75JO3804bg/H4Bdck0XAPe+JPsFpoal+JQ4EpFguEFY6DbhxUvi0jUO0" +
            "zVw10CGIO4dyk4D/R4uLHHIhXes8JsUWDmWMj0Uhi1hUcBAmVLoynkp76tOnxTpH" +
            "kE+R5FN+jYqFAoIBAQC2DDPxcY/JEWT4nQp4aqEWCr11AXP9LGjYD1MkMJpPyDII" +
            "x7YCugsfn4zxKlvKX3VstDIopGQnGo1cx/yvGPSvA+1dsjdrsU1YpCsrZb35lLvj" +
            "2HvnX47QvYfpz/SCEtLdT9yekK8hmZwcD9GN65+caKNwrkKHYMBT/7jg0BWoNWHC" +
            "JY6MPJ45rp51hTVnBlvL9B27YaXF5JhDPyiIWuR01HL2uDgLtC7UU0Ev+UXdd/W7" +
            "t7Hw1i6AegyhR1eE7RER7ge8+TY9aNiFQN61T3CpQVO6uJfE2vRf3wb+VB2gNgFN" +
            "nDcfTKm2MIAdq+7j4HdjHxVH65k8/qmBjBnuipBVAoIBADWF/a3ub5BLbqSKnEqo" +
            "HkABpx81YfizcsGU2Qc4m/pSVQF1ZLjWoW35Y4fOaDsbCj+hOBV754ofqj93c9Af" +
            "vbPnNHjWV9JXBQPHm6+9WghPp4i+2yleZ4QrXiVZ75JxgHtz9lN0n8WcqFBbVdmT" +
            "3BEkFuYJBSJgjHRq0/U9VkOqP8UrWVARwlKNf9RJZu5gDs0TaJRfCQl2vOFnRNO4" +
            "jJhxgjMP9cZ5ZGai2bnoLOGq+mS09OezfOvPXTDUzHLzI43pMm0yz3MbRzp4HW5r" +
            "sD6X+e+vumivzoVEd9jmS4oFS7jbr+XDfMst+Xo8TMNbZsACCNgTfh/hRZVuVUqH" +
            "nXECggEBAL6IftnKvlcB5BxztYz51925KCg+rCcPm5kKDki4643ObZ6BEX9oikGu" +
            "aS1qwK/gHHMphm/GPcdin1YrHVGiXd9diQUSPYxSqgyDeQRFe40RGLNG6DwOlMEj" +
            "WbJLFmPonsv9o8XPLdP/VhhsAN2u9Rk1678CK4/WnkbptOeM1hTkfdTEFpfPTky4" +
            "kZx1Lj5m15O5Lu/q7LHxcRykf7+oC5vU3PYDj6G0Ub55dQ99mXXoew3mu/CRbn3N" +
            "jujge3wO+imgFTtXMRk7yiAyFtKsSIpzwkpl6K6+Manie5PyiBKS+tpIGrh94nHQ" +
            "fjrJuJzO7IrDOUs/RGBgzckS12Jdx8E=";
    final String public_key_4096 = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAvVOoWwIfQ9whVxnd4q9J" +
            "JmNPkpenyZV+Dqyi5g4UtZqixoryxCvAjI3Xl9N0n77dQb828SF9GoU4C7RDDshF" +
            "dIlTo0LONYiO//KF8oZ23whTzCquwX47awM5kv1eLADp3IWU6muCwG8TqgM5ptN+" +
            "5EABoiuUMnRQ2Bnm8MpOavslYvf7y2MYhiESx7j7HauDxMO1lj24Ixqb58c6MoFf" +
            "kjW7zF4igohL/xEnO5kvA97z8McxL51Z5d74HXf1Z9M7yLk5KeQ8oaBASKBEVhh0" +
            "bJZSbE6Zb5WvEzCHms+H8j/W5JVWwRlaVXMG3S8ocI2SkQ+7BJOQlduRAU1PnaRY" +
            "GQY0L2aD6pjrLg9sCFjitfEeUHH3EeAYAWFX0bmRC1r2tXpmk8nMd/SaeMa7ZBb2" +
            "Xa71fo/D65iewLKKRaKSa0cl1yb1MT3pqv1kZu930Kfehz6ZDZM4BZqIog9cuF3i" +
            "oydFFCbVcMm0W5HjolrHlVATkwTggJwg+hBjRCb2cSqGRTQbc5m2tZQHqCGNPn4y" +
            "iYSVtEMseoShwuoeWUgZ5y+49XiQPjO2uWLUYdMKjIlIwI3ZZH37GlfeF1mbc5Wh" +
            "eyNaJWv3MWL+HWeMLzOqOtKqLNtZTFZy1DA/KilJMrLE8qGiZtCXSAn04rUeOiJB" +
            "QC6VoR1q4gDTIViP76Z60J8CAwEAAQ==";

    @Test
    @SneakyThrows
    void should_generate_md5_success() {
        byte[] result = md5("hello".getBytes(Charsets.UTF_8));
        log.info(Hex.encodeHexString(result, true));
    }

    @Test
    @SneakyThrows
    void should_generate_sha256_success() {
        byte[] result = sha256("hello".getBytes(Charsets.UTF_8));
        log.info(Hex.encodeHexString(result, true));
    }

    @Test
    @SneakyThrows
    void should_generate_aes_ecb_success() {
        byte[] msg = "hello".getBytes(Charsets.UTF_8);

        byte[] results = encryptEcb(key, msg);
        log.info("aes encryptEcb {}", Hex.encodeHexString(results));
        byte[] origin = decryptEcb(key, results);
        log.info("aes origin {}", new String(origin, Charsets.UTF_8));
    }

    @Test
    @SneakyThrows
    void should_generate_aes_cbc_success() {
        byte[] msg = "hello".getBytes(Charsets.UTF_8);

        byte[] results = encryptCbc(key, msg);
        log.info("aes encryptCbc {}", Hex.encodeHexString(results));
        byte[] origin = decryptCbc(key, results);
        log.info("aes origin {}", new String(origin, Charsets.UTF_8));
    }

}
