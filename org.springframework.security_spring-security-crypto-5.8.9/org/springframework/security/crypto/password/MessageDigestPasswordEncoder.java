/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.crypto.password;

import java.util.Base64;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.crypto.password.Digester;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoderUtils;

@Deprecated
public class MessageDigestPasswordEncoder
implements PasswordEncoder {
    private static final String PREFIX = "{";
    private static final String SUFFIX = "}";
    private StringKeyGenerator saltGenerator = new Base64StringKeyGenerator();
    private boolean encodeHashAsBase64;
    private Digester digester;

    public MessageDigestPasswordEncoder(String algorithm) {
        this.digester = new Digester(algorithm, 1);
    }

    public void setEncodeHashAsBase64(boolean encodeHashAsBase64) {
        this.encodeHashAsBase64 = encodeHashAsBase64;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        String salt = PREFIX + this.saltGenerator.generateKey() + SUFFIX;
        return this.digest(salt, rawPassword);
    }

    private String digest(String salt, CharSequence rawPassword) {
        String saltedPassword = rawPassword + salt;
        byte[] digest = this.digester.digest(Utf8.encode(saltedPassword));
        String encoded = this.encode(digest);
        return salt + encoded;
    }

    private String encode(byte[] digest) {
        if (this.encodeHashAsBase64) {
            return Utf8.decode(Base64.getEncoder().encode(digest));
        }
        return new String(Hex.encode(digest));
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String salt = this.extractSalt(encodedPassword);
        String rawPasswordEncoded = this.digest(salt, rawPassword);
        return PasswordEncoderUtils.equals(encodedPassword.toString(), rawPasswordEncoded);
    }

    public void setIterations(int iterations) {
        this.digester.setIterations(iterations);
    }

    private String extractSalt(String prefixEncodedPassword) {
        int start = prefixEncodedPassword.indexOf(PREFIX);
        if (start != 0) {
            return "";
        }
        int end = prefixEncodedPassword.indexOf(SUFFIX, start);
        if (end < 0) {
            return "";
        }
        return prefixEncodedPassword.substring(start, end + 1);
    }
}

