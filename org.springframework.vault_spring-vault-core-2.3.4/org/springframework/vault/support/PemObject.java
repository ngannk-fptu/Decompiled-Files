/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Base64Utils
 */
package org.springframework.vault.support;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.util.Base64Utils;
import org.springframework.vault.support.KeystoreUtil;

public class PemObject {
    private static final Pattern PRIVATE_KEY_PATTERN = Pattern.compile("-+BEGIN\\s+.*PRIVATE\\s+KEY[^-]*-+(?:\\s|\\r|\\n)+([a-z0-9+/=\\r\\n]+)-+END\\s+.*PRIVATE\\s+KEY[^-]*-+", 2);
    private static final Pattern PUBLIC_KEY_PATTERN = Pattern.compile("-+BEGIN\\s+.*PUBLIC\\s+KEY[^-]*-+(?:\\s|\\r|\\n)+([a-z0-9+/=\\r\\n]+)-+END\\s+.*PUBLIC\\s+KEY[^-]*-+", 2);
    private static final Pattern CERTIFICATE_PATTERN = Pattern.compile("-+BEGIN\\s+.*CERTIFICATE[^-]*-+(?:\\s|\\r|\\n)+([a-z0-9+/=\\r\\n]+)-+END\\s+.*CERTIFICATE[^-]*-+", 2);
    private static final Pattern[] PATTERNS = new Pattern[]{PRIVATE_KEY_PATTERN, PUBLIC_KEY_PATTERN, CERTIFICATE_PATTERN};
    private final byte[] content;
    private final Pattern matchingPattern;

    private PemObject(String content, Pattern matchingPattern) {
        this.matchingPattern = matchingPattern;
        String sanitized = content.replaceAll("\r", "").replaceAll("\n", "");
        this.content = Base64Utils.decodeFromString((String)sanitized);
    }

    public static PemObject fromKey(String content) {
        Matcher m = PRIVATE_KEY_PATTERN.matcher(content);
        if (!m.find()) {
            throw new IllegalArgumentException("Could not find a PKCS #8 private key");
        }
        return new PemObject(m.group(1), PRIVATE_KEY_PATTERN);
    }

    public static PemObject parseFirst(String content) {
        List<PemObject> objects = PemObject.parse(content);
        if (objects.isEmpty()) {
            throw new IllegalArgumentException("Cannot find PEM object");
        }
        return objects.get(0);
    }

    public static List<PemObject> parse(String content) {
        boolean found;
        ArrayList<PemObject> objects = new ArrayList<PemObject>();
        int index = 0;
        do {
            found = false;
            Matcher discoveredMatcher = null;
            int indexDiscoveredIndex = 0;
            for (Pattern pattern : PATTERNS) {
                Matcher m = pattern.matcher(content);
                if (!m.find(index) || indexDiscoveredIndex != 0 && indexDiscoveredIndex <= m.start()) continue;
                discoveredMatcher = m;
                indexDiscoveredIndex = m.start();
            }
            if (discoveredMatcher == null) continue;
            found = true;
            index = discoveredMatcher.end();
            objects.add(new PemObject(discoveredMatcher.group(1), discoveredMatcher.pattern()));
        } while (found);
        return objects;
    }

    public boolean isCertificate() {
        return this.matchingPattern.equals(CERTIFICATE_PATTERN);
    }

    public boolean isPrivateKey() {
        return this.matchingPattern.equals(PRIVATE_KEY_PATTERN);
    }

    public boolean isPublicKey() {
        return this.matchingPattern.equals(PUBLIC_KEY_PATTERN);
    }

    @Deprecated
    public RSAPrivateCrtKeySpec getRSAKeySpec() {
        return this.getRSAPrivateKeySpec();
    }

    public X509Certificate getCertificate() {
        if (!this.isCertificate()) {
            throw new IllegalStateException("PEM object is not a certificate");
        }
        try {
            return KeystoreUtil.getCertificate(this.content);
        }
        catch (CertificateException e) {
            throw new IllegalStateException("Cannot obtain Certificate", e);
        }
    }

    public RSAPrivateCrtKeySpec getRSAPrivateKeySpec() {
        if (!this.isPrivateKey()) {
            throw new IllegalStateException("PEM object is not a private key");
        }
        try {
            return KeystoreUtil.getRSAPrivateKeySpec(this.content);
        }
        catch (IOException e) {
            throw new IllegalStateException("Cannot obtain PrivateKey", e);
        }
    }

    public RSAPublicKeySpec getRSAPublicKeySpec() {
        if (!this.isPublicKey()) {
            throw new IllegalStateException("PEM object is not a public key");
        }
        try {
            return KeystoreUtil.getRSAPublicKeySpec(this.content);
        }
        catch (IOException e) {
            throw new IllegalStateException("Cannot obtain PrivateKey", e);
        }
    }
}

