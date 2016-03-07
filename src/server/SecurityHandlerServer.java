package server;
import java.security.*;
//import org.apache.commons.codec.binary.Base64;
import java.util.Base64;

import javax.crypto.*;

public class SecurityHandlerServer {
	
	public static Key gen() throws NoSuchAlgorithmException
	{
		KeyGenerator kg = KeyGenerator.getInstance("AES");
		kg.init(128);
		return kg.generateKey();
		
	}
	
	public static String encryptTheMessage(String message, Key key) throws NoSuchAlgorithmException,
	NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
		Cipher cyphr = Cipher.getInstance("AES");
        cyphr.init(Cipher.ENCRYPT_MODE, key);
        byte[] byteDataToEncrypt = message.getBytes();
        byte[] byteCipherText = cyphr.doFinal(byteDataToEncrypt);
        return Base64.getEncoder().encodeToString(byteCipherText);
	}
	
	public static String decryptTheMessage(String msg,Key key) throws NoSuchAlgorithmException, 
	NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
		byte[] data = java.util.Base64.getDecoder().decode(msg);
        Cipher cyphr = Cipher.getInstance("AES");
        cyphr.init(Cipher.DECRYPT_MODE, key);
        byte[] plainData = cyphr.doFinal(data);
        String decryptedMsg = new String(plainData);

        return decryptedMsg;
	}

	
	public static String keyToString(Key k)
	{
		String key = Base64.getEncoder().encodeToString(k.getEncoded());
		return key;
	}

}
