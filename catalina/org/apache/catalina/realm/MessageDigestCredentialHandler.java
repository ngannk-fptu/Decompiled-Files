/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.buf.B2CConverter
 *  org.apache.tomcat.util.buf.HexUtils
 *  org.apache.tomcat.util.codec.binary.Base64
 *  org.apache.tomcat.util.security.ConcurrentMessageDigest
 */
package org.apache.catalina.realm;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import org.apache.catalina.realm.DigestCredentialHandlerBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.buf.HexUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.security.ConcurrentMessageDigest;

public class MessageDigestCredentialHandler
extends DigestCredentialHandlerBase {
    private static final Log log = LogFactory.getLog(MessageDigestCredentialHandler.class);
    public static final int DEFAULT_ITERATIONS = 1;
    private Charset encoding = StandardCharsets.UTF_8;
    private String algorithm = null;

    public String getEncoding() {
        return this.encoding.name();
    }

    public void setEncoding(String encodingName) {
        if (encodingName == null) {
            this.encoding = StandardCharsets.UTF_8;
        } else {
            try {
                this.encoding = B2CConverter.getCharset((String)encodingName);
            }
            catch (UnsupportedEncodingException e) {
                log.error((Object)sm.getString("mdCredentialHandler.unknownEncoding", new Object[]{encodingName, this.encoding.name()}));
            }
        }
    }

    @Override
    public String getAlgorithm() {
        return this.algorithm;
    }

    @Override
    public void setAlgorithm(String algorithm) throws NoSuchAlgorithmException {
        ConcurrentMessageDigest.init((String)algorithm);
        this.algorithm = algorithm;
    }

    @Override
    public boolean matches(String inputCredentials, String storedCredentials) {
        if (inputCredentials == null || storedCredentials == null) {
            return false;
        }
        if (this.getAlgorithm() == null) {
            return DigestCredentialHandlerBase.equals(inputCredentials, storedCredentials, false);
        }
        if (storedCredentials.startsWith("{MD5}") || storedCredentials.startsWith("{SHA}")) {
            String base64ServerDigest = storedCredentials.substring(5);
            byte[] userDigest = ConcurrentMessageDigest.digest((String)this.getAlgorithm(), (byte[][])new byte[][]{inputCredentials.getBytes(StandardCharsets.ISO_8859_1)});
            String base64UserDigest = Base64.encodeBase64String((byte[])userDigest);
            return DigestCredentialHandlerBase.equals(base64UserDigest, base64ServerDigest, false);
        }
        if (storedCredentials.startsWith("{SSHA}")) {
            String serverDigestPlusSalt = storedCredentials.substring(6);
            byte[] serverDigestPlusSaltBytes = Base64.decodeBase64((String)serverDigestPlusSalt);
            int digestLength = 20;
            byte[] serverDigestBytes = new byte[20];
            System.arraycopy(serverDigestPlusSaltBytes, 0, serverDigestBytes, 0, 20);
            int saltLength = serverDigestPlusSaltBytes.length - 20;
            byte[] serverSaltBytes = new byte[saltLength];
            System.arraycopy(serverDigestPlusSaltBytes, 20, serverSaltBytes, 0, saltLength);
            byte[] userDigestBytes = ConcurrentMessageDigest.digest((String)this.getAlgorithm(), (byte[][])new byte[][]{inputCredentials.getBytes(StandardCharsets.ISO_8859_1), serverSaltBytes});
            return Arrays.equals(userDigestBytes, serverDigestBytes);
        }
        if (storedCredentials.indexOf(36) > -1) {
            return this.matchesSaltIterationsEncoded(inputCredentials, storedCredentials);
        }
        String userDigest = this.mutate(inputCredentials, null, 1);
        if (userDigest == null) {
            return false;
        }
        return storedCredentials.equalsIgnoreCase(userDigest);
    }

    @Override
    protected String mutate(String inputCredentials, byte[] salt, int iterations) {
        if (this.algorithm == null) {
            return inputCredentials;
        }
        byte[] inputCredentialbytes = inputCredentials.getBytes(this.encoding);
        byte[] userDigest = salt == null ? ConcurrentMessageDigest.digest((String)this.algorithm, (int)iterations, (byte[][])new byte[][]{inputCredentialbytes}) : ConcurrentMessageDigest.digest((String)this.algorithm, (int)iterations, (byte[][])new byte[][]{salt, inputCredentialbytes});
        return HexUtils.toHexString((byte[])userDigest);
    }

    @Override
    protected int getDefaultIterations() {
        return 1;
    }

    @Override
    protected Log getLog() {
        return log;
    }
}

