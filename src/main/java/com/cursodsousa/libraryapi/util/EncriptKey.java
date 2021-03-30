package com.cursodsousa.libraryapi.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EncriptKey {

	private static final String AES_ECB_PKCS5PADDING = "AES/ECB/PKCS5PADDING";

	private static SecretKeySpec setKey(String myKey) {
		SecretKeySpec secretKey = null;
		try {
			byte[] key;
			key = myKey.getBytes(StandardCharsets.UTF_8);
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, "AES");
		} catch (NoSuchAlgorithmException e) {
			log.error("Error while get secret key: " + e.toString());
		}
		return secretKey;
	}

	public static String encrypt(String strToEncrypt, String secret) {
		try {
			Cipher cipher = Cipher.getInstance(AES_ECB_PKCS5PADDING);
			cipher.init(Cipher.ENCRYPT_MODE, setKey(secret));
			return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
		} catch (Exception e) {
			log.error("Error while encrypting: " + e.toString());
		}
		return null;
	}

	public static String decrypt(String strToDecrypt, String secret) {
		try {
			Cipher cipher = Cipher.getInstance(AES_ECB_PKCS5PADDING);
			cipher.init(Cipher.DECRYPT_MODE, setKey(secret));
			return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
		} catch (Exception e) {
			log.error("Error while decrypting: " + e.toString());
		}
		return null;
	}
	
}
