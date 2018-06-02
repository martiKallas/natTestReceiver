import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class AssymEncypt {
    private static final String ENCRYPTION_TYPE = "RSA";
    private static final int KEY_LENGTH = 4096;
    private Cipher cipher;
    private KeyPair keyPair;

    private static PublicKey publicKey;
    private static PrivateKey privateKey;
    private PublicKey friendKey;
    private KeyPairGenerator keyGen;

    public static synchronized AssymEncypt getAssymEncypt() throws NoSuchAlgorithmException, NoSuchPaddingException {
        return new AssymEncypt();
    }

    private AssymEncypt() throws NoSuchAlgorithmException, NoSuchPaddingException {
        keyGen = KeyPairGenerator.getInstance(ENCRYPTION_TYPE);
        keyGen.initialize(KEY_LENGTH);
        keyPair = keyGen.generateKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
        cipher = Cipher.getInstance(ENCRYPTION_TYPE);
    }

    public String getPublicKeyString() {
        return Base64.encodeBase64String(publicKey.getEncoded());
    }

    public void setFriendPublicKey(String key) throws InvalidKeySpecException, NoSuchAlgorithmException {
        makePublicKeyFromString(key);
    }

    public String encryptString(String message) throws InvalidKeyException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException {
        cipher.init(Cipher.ENCRYPT_MODE, friendKey);
        return Base64.encodeBase64String(cipher.doFinal(message.getBytes("UTF-8")));
    }

    public String decryptString(String input) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(Base64.decodeBase64(input)), "UTF-8");
    }

    //this method is used to
    private void makePublicKeyFromString(String sentPublicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.decodeBase64(sentPublicKey.getBytes()));
        KeyFactory keyFactory = KeyFactory.getInstance(ENCRYPTION_TYPE);
        friendKey = keyFactory.generatePublic(spec);
    }


}

