/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.codec.binary.Hex
 */
package com.atlassian.jwt.core;

import com.atlassian.jwt.core.http.JavaxJwtRequestExtractor;
import com.atlassian.jwt.core.http.JwtRequestExtractor;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.codec.binary.Hex;

public class JwtUtil {
    private static final String ENCODING = "UTF-8";
    public static final char QUERY_PARAMS_SEPARATOR = '&';
    private static JwtRequestExtractor<HttpServletRequest> jwtRequestExtractor = new JavaxJwtRequestExtractor();

    public static boolean requestContainsJwt(HttpServletRequest request) {
        return JwtUtil.extractJwt(request) != null;
    }

    public static String extractJwt(HttpServletRequest request) {
        return jwtRequestExtractor.extractJwt(request);
    }

    public static String percentEncode(String str) throws UnsupportedEncodingException {
        if (str == null) {
            return "";
        }
        String basic = URLEncoder.encode(str, ENCODING);
        boolean changed = !basic.equals(str);
        int length = basic.length();
        StringBuilder out = new StringBuilder(length * 2);
        block5: for (int i = 0; i < length; ++i) {
            char c = basic.charAt(i);
            switch (c) {
                case '+': {
                    out.append("%20");
                    changed = true;
                    continue block5;
                }
                case '*': {
                    out.append("%2A");
                    changed = true;
                    continue block5;
                }
                case '%': {
                    if (i < length - 2 && basic.charAt(i + 1) == '7' && basic.charAt(i + 2) == 'E') {
                        out.append('~');
                        changed = true;
                        i += 2;
                        continue block5;
                    }
                    out.append(c);
                    continue block5;
                }
                default: {
                    out.append(c);
                }
            }
        }
        return changed ? out.toString() : str;
    }

    public static String computeSha256Hash(String hashInput) throws NoSuchAlgorithmException {
        if (null == hashInput) {
            throw new IllegalArgumentException("hashInput cannot be null");
        }
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashInputBytes = hashInput.getBytes();
        digest.update(hashInputBytes, 0, hashInputBytes.length);
        return new String(Hex.encodeHex((byte[])digest.digest()));
    }
}

