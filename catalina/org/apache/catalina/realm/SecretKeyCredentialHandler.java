/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.buf.HexUtils
 */
package org.apache.catalina.realm;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.apache.catalina.realm.DigestCredentialHandlerBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.HexUtils;

public class SecretKeyCredentialHandler
extends DigestCredentialHandlerBase {
    private static final Log log = LogFactory.getLog(SecretKeyCredentialHandler.class);
    public static final String DEFAULT_ALGORITHM = "PBKDF2WithHmacSHA1";
    public static final int DEFAULT_KEY_LENGTH = 160;
    public static final int DEFAULT_ITERATIONS = 20000;
    private SecretKeyFactory secretKeyFactory;
    private int keyLength = 160;

    public SecretKeyCredentialHandler() throws NoSuchAlgorithmException {
        this.setAlgorithm(DEFAULT_ALGORITHM);
    }

    @Override
    public String getAlgorithm() {
        return this.secretKeyFactory.getAlgorithm();
    }

    @Override
    public void setAlgorithm(String algorithm) throws NoSuchAlgorithmException {
        SecretKeyFactory secretKeyFactory;
        this.secretKeyFactory = secretKeyFactory = SecretKeyFactory.getInstance(algorithm);
    }

    public int getKeyLength() {
        return this.keyLength;
    }

    public void setKeyLength(int keyLength) {
        this.keyLength = keyLength;
    }

    @Override
    public boolean matches(String inputCredentials, String storedCredentials) {
        return this.matchesSaltIterationsEncoded(inputCredentials, storedCredentials);
    }

    @Override
    protected String mutate(String inputCredentials, byte[] salt, int iterations) {
        return this.mutate(inputCredentials, salt, iterations, this.getKeyLength());
    }

    @Override
    protected String mutate(String inputCredentials, byte[] salt, int iterations, int keyLength) {
        try {
            PBEKeySpec spec = new PBEKeySpec(inputCredentials.toCharArray(), salt, iterations, keyLength);
            return HexUtils.toHexString((byte[])this.secretKeyFactory.generateSecret(spec).getEncoded());
        }
        catch (IllegalArgumentException | InvalidKeySpecException e) {
            log.warn((Object)sm.getString("pbeCredentialHandler.invalidKeySpec"), (Throwable)e);
            return null;
        }
    }

    @Override
    protected int getDefaultIterations() {
        return 20000;
    }

    @Override
    protected Log getLog() {
        return log;
    }
}

