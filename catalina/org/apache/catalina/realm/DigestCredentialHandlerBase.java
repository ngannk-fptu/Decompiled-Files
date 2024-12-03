/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.tomcat.util.buf.HexUtils
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.realm;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import org.apache.catalina.CredentialHandler;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.buf.HexUtils;
import org.apache.tomcat.util.res.StringManager;

public abstract class DigestCredentialHandlerBase
implements CredentialHandler {
    protected static final StringManager sm = StringManager.getManager(DigestCredentialHandlerBase.class);
    public static final int DEFAULT_SALT_LENGTH = 32;
    private int iterations = this.getDefaultIterations();
    private int saltLength = this.getDefaultSaltLength();
    private final Object randomLock = new Object();
    private volatile Random random = null;
    private boolean logInvalidStoredCredentials = false;

    public int getIterations() {
        return this.iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public int getSaltLength() {
        return this.saltLength;
    }

    public void setSaltLength(int saltLength) {
        this.saltLength = saltLength;
    }

    public boolean getLogInvalidStoredCredentials() {
        return this.logInvalidStoredCredentials;
    }

    public void setLogInvalidStoredCredentials(boolean logInvalidStoredCredentials) {
        this.logInvalidStoredCredentials = logInvalidStoredCredentials;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String mutate(String userCredential) {
        byte[] salt = null;
        int iterations = this.getIterations();
        int saltLength = this.getSaltLength();
        if (saltLength == 0) {
            salt = new byte[]{};
        } else if (saltLength > 0) {
            if (this.random == null) {
                Object object = this.randomLock;
                synchronized (object) {
                    if (this.random == null) {
                        this.random = new SecureRandom();
                    }
                }
            }
            salt = new byte[saltLength];
            this.random.nextBytes(salt);
        }
        String serverCredential = this.mutate(userCredential, salt, iterations);
        if (serverCredential == null) {
            return null;
        }
        if (saltLength == 0 && iterations == 1) {
            return serverCredential;
        }
        StringBuilder result = new StringBuilder((saltLength << 1) + 10 + serverCredential.length() + 2);
        result.append(HexUtils.toHexString((byte[])salt));
        result.append('$');
        result.append(iterations);
        result.append('$');
        result.append(serverCredential);
        return result.toString();
    }

    protected boolean matchesSaltIterationsEncoded(String inputCredentials, String storedCredentials) {
        byte[] salt;
        if (storedCredentials == null) {
            this.logInvalidStoredCredentials(null);
            return false;
        }
        int sep1 = storedCredentials.indexOf(36);
        int sep2 = storedCredentials.indexOf(36, sep1 + 1);
        if (sep1 < 0 || sep2 < 0) {
            this.logInvalidStoredCredentials(storedCredentials);
            return false;
        }
        String hexSalt = storedCredentials.substring(0, sep1);
        int iterations = Integer.parseInt(storedCredentials.substring(sep1 + 1, sep2));
        String storedHexEncoded = storedCredentials.substring(sep2 + 1);
        try {
            salt = HexUtils.fromHexString((String)hexSalt);
        }
        catch (IllegalArgumentException iae) {
            this.logInvalidStoredCredentials(storedCredentials);
            return false;
        }
        String inputHexEncoded = this.mutate(inputCredentials, salt, iterations, HexUtils.fromHexString((String)storedHexEncoded).length * 8);
        if (inputHexEncoded == null) {
            return false;
        }
        return DigestCredentialHandlerBase.equals(storedHexEncoded, inputHexEncoded, true);
    }

    private void logInvalidStoredCredentials(String storedCredentials) {
        if (this.logInvalidStoredCredentials) {
            this.getLog().warn((Object)sm.getString("credentialHandler.invalidStoredCredential", new Object[]{storedCredentials}));
        }
    }

    protected int getDefaultSaltLength() {
        return 32;
    }

    protected abstract String mutate(String var1, byte[] var2, int var3);

    protected String mutate(String inputCredentials, byte[] salt, int iterations, int keyLength) {
        return this.mutate(inputCredentials, salt, iterations);
    }

    public abstract void setAlgorithm(String var1) throws NoSuchAlgorithmException;

    public abstract String getAlgorithm();

    protected abstract int getDefaultIterations();

    protected abstract Log getLog();

    public static boolean equals(String s1, String s2, boolean ignoreCase) {
        if (s1 == s2) {
            return true;
        }
        if (s1 == null || s2 == null) {
            return false;
        }
        int len1 = s1.length();
        int len2 = s2.length();
        if (len2 == 0) {
            return len1 == 0;
        }
        int result = 0;
        result |= len1 - len2;
        for (int i = 0; i < len1; ++i) {
            int index2 = (i - len2 >>> 31) * i;
            char c1 = s1.charAt(i);
            char c2 = s2.charAt(index2);
            if (ignoreCase) {
                c1 = Character.toLowerCase(c1);
                c2 = Character.toLowerCase(c2);
            }
            result |= c1 ^ c2;
        }
        return result == 0;
    }

    public static boolean equals(byte[] b1, byte[] b2) {
        return MessageDigest.isEqual(b1, b2);
    }
}

