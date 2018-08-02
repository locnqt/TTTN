package Image_steganography;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 * This example program shows how AES encryption and decryption can be done in Java.
 * Please note that secret key and encrypted text is unreadable binary and hence 
 * in the following program we display it in hexadecimal format of the underlying bytes.
 * @author Jayson
 */
public class AES {
 
    /**
     * 1. Generate a plain text for encryption
     * 2. Get a secret key (printed in hexadecimal form). In actual use this must 
     * by encrypted and kept safe. The same key is required for decryption.
     * 3. 
     */
    public static void main(String[] args) throws Exception {
        String plainText = "Hello WorldHello WorldHello WorldHello World";
        String secKey = getSecretEncryptionKey();
        
        System.out.println("leng: "+plainText.length());
        System.out.println("Original Text:" + plainText);
        System.out.println("Key: "+secKey);
//        System.out.println("AES Key (Hex Form):"+bytesToHex(secKey.getEncoded()));
//        System.out.println("AES Key (Hex Form):"+encodedKey);
        SecretKey originalKey = getSecretDecryptionKey(secKey);
        byte[] cipherText = encryptText(plainText, originalKey);
        System.out.println("cipherText: "+cipherText);
        System.out.println("Encrypted Text (Hex Form):"+bytesToHex(cipherText));
        String decryptedText = decryptText(cipherText, originalKey);
        System.out.println("Descrypted Text:"+decryptedText);
        
    }
    
    /**
     * gets the AES encryption key. In your actual programs, this should be safely
     * stored.
     * @return
     * @throws Exception 
     */
    public static String getSecretEncryptionKey() throws Exception{
        // create new key
        SecretKey secKey = KeyGenerator.getInstance("AES").generateKey();
// get base64 encoded version of the key
        String encodedKey = Base64.getEncoder().encodeToString(secKey.getEncoded());
//        KeyGenerator generator = KeyGenerator.getInstance("AES");
//        generator.init(128); // The AES key size in number of bits
//        SecretKey secKey = generator.generateKey();
        return encodedKey;
    }
    public static SecretKey getSecretDecryptionKey(String secKey) throws Exception{
        // decode the base64 encoded string
        byte[] decodedKey = Base64.getDecoder().decode(secKey);
        // rebuild key using SecretKeySpec
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 
        return originalKey;
    }
    /**
     * Encrypts plainText in AES using the secret key
     * @param plainText
     * @param secKey
     * @return
     * @throws Exception 
     */
    public static byte[] encryptText(String plainText,SecretKey secKey) throws Exception{
		// AES defaults to AES/ECB/PKCS5Padding in Java 7
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, secKey);
        byte[] byteCipherText = aesCipher.doFinal(plainText.getBytes());
        return byteCipherText;
    }
    
    /**
     * Decrypts encrypted byte array using the key used for encryption.
     * @param byteCipherText
     * @param secKey
     * @return
     * @throws Exception 
     */
    public static String decryptText(byte[] byteCipherText, SecretKey secKey) throws Exception {
		// AES defaults to AES/ECB/PKCS5Padding in Java 7
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, secKey);
        byte[] bytePlainText = aesCipher.doFinal(byteCipherText);
        return new String(bytePlainText);
    }
    
    /**
     * Convert a binary byte array into readable hex form
     * @param hash
     * @return 
     */
    public static String  bytesToHex(byte[] hash) {
        return DatatypeConverter.printHexBinary(hash);
    }
    public static byte[] hextoBytes(String helloHex) {
        return DatatypeConverter.parseHexBinary(helloHex);
    }
}