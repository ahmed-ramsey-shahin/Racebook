package com.ramsey.racebook.Util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;

import java.nio.charset.StandardCharsets;

public class HMACGenerator {

	public static final String API_SECRET_CODE = "The quick brown fox jumps over the lazy dog";
	
	public static String generateHMAC(String key, String message) {
	
		var keyBytes = key.getBytes(StandardCharsets.UTF_8);
		final var hmacHelper = HmacUtils.getInitializedMac(HmacAlgorithms.HMAC_SHA_512, keyBytes);
		final var raw = hmacHelper.doFinal(message.getBytes(StandardCharsets.UTF_8));
		return Base64.encodeBase64String(raw);
	
	}
	
}
