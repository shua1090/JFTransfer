package JFT;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Level;

class AES {
    private SecretKeySpec secretKey;

    AES(String password) {
        setKey(password);
    }

    private void setKey(String passwdKey) {
        MessageDigest sha = null;
        var stringKey = passwdKey.getBytes(StandardCharsets.UTF_8);
        try {
            sha = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException ignored) {
        }
        assert sha != null;
        byte[] key = sha.digest(
                stringKey
        );
        key = Arrays.copyOf(key, 16);
        secretKey = new SecretKeySpec(key, "AES");
    }

    public String encrypt(String strToEncrypt) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            JFT.logger.log(Level.SEVERE, "Error while encrypting: " + strToEncrypt + ". Exiting immediately.");
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }

    public byte[] decrypt(String strToDecrypt) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(Base64.getDecoder().decode(strToDecrypt));
        } catch (Exception e) {
            JFT.logger.log(Level.SEVERE, "Error while decrypting: " + strToDecrypt + ". Exiting immediately.");
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }


}