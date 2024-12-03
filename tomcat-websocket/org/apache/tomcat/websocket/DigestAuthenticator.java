/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.buf.HexUtils
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.websocket;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;
import org.apache.tomcat.util.buf.HexUtils;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.AuthenticationException;
import org.apache.tomcat.websocket.Authenticator;

public class DigestAuthenticator
extends Authenticator {
    private static final StringManager sm = StringManager.getManager(DigestAuthenticator.class);
    public static final String schemeName = "digest";
    private static final Object cnonceGeneratorLock = new Object();
    private static volatile SecureRandom cnonceGenerator;
    private int nonceCount = 0;
    private long cNonce;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getAuthorization(String requestUri, String authenticateHeader, String userName, String userPassword, String userRealm) throws AuthenticationException {
        this.validateUsername(userName);
        this.validatePassword(userPassword);
        Map<String, String> parameterMap = this.parseAuthenticateHeader(authenticateHeader);
        String realm = parameterMap.get("realm");
        this.validateRealm(userRealm, realm);
        String nonce = parameterMap.get("nonce");
        String messageQop = parameterMap.get("qop");
        String algorithm = parameterMap.get("algorithm") == null ? "MD5" : parameterMap.get("algorithm");
        String opaque = parameterMap.get("opaque");
        StringBuilder challenge = new StringBuilder();
        if (!messageQop.isEmpty()) {
            if (cnonceGenerator == null) {
                Object object = cnonceGeneratorLock;
                synchronized (object) {
                    if (cnonceGenerator == null) {
                        cnonceGenerator = new SecureRandom();
                    }
                }
            }
            this.cNonce = cnonceGenerator.nextLong();
            ++this.nonceCount;
        }
        challenge.append("Digest ");
        challenge.append("username =\"" + userName + "\",");
        challenge.append("realm=\"" + realm + "\",");
        challenge.append("nonce=\"" + nonce + "\",");
        challenge.append("uri=\"" + requestUri + "\",");
        try {
            challenge.append("response=\"" + this.calculateRequestDigest(requestUri, userName, userPassword, realm, nonce, messageQop, algorithm) + "\",");
        }
        catch (NoSuchAlgorithmException e) {
            throw new AuthenticationException(sm.getString("digestAuthenticator.algorithm", new Object[]{e.getMessage()}));
        }
        challenge.append("algorithm=" + algorithm + ",");
        challenge.append("opaque=\"" + opaque + "\",");
        if (!messageQop.isEmpty()) {
            challenge.append("qop=\"" + messageQop + "\"");
            challenge.append(",cnonce=\"" + this.cNonce + "\",");
            challenge.append("nc=" + String.format("%08X", this.nonceCount));
        }
        return challenge.toString();
    }

    private String calculateRequestDigest(String requestUri, String userName, String password, String realm, String nonce, String qop, String algorithm) throws NoSuchAlgorithmException {
        boolean session = false;
        if (algorithm.endsWith("-sess")) {
            algorithm = algorithm.substring(0, algorithm.length() - 5);
            session = true;
        }
        StringBuilder preDigest = new StringBuilder();
        String A1 = session ? this.encode(algorithm, userName + ":" + realm + ":" + password) + ":" + nonce + ":" + this.cNonce : userName + ":" + realm + ":" + password;
        String A2 = "GET:" + requestUri;
        preDigest.append(this.encode(algorithm, A1));
        preDigest.append(':');
        preDigest.append(nonce);
        if (qop.toLowerCase().contains("auth")) {
            preDigest.append(':');
            preDigest.append(String.format("%08X", this.nonceCount));
            preDigest.append(':');
            preDigest.append(String.valueOf(this.cNonce));
            preDigest.append(':');
            preDigest.append(qop);
        }
        preDigest.append(':');
        preDigest.append(this.encode(algorithm, A2));
        return this.encode(algorithm, preDigest.toString());
    }

    private String encode(String algorithm, String value) throws NoSuchAlgorithmException {
        byte[] bytesOfMessage = value.getBytes(StandardCharsets.ISO_8859_1);
        MessageDigest md = MessageDigest.getInstance(algorithm);
        byte[] thedigest = md.digest(bytesOfMessage);
        return HexUtils.toHexString((byte[])thedigest);
    }

    @Override
    public String getSchemeName() {
        return schemeName;
    }
}

