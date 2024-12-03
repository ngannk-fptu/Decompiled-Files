/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.crypto.password;

import java.util.HashMap;
import java.util.Map;
import org.springframework.security.crypto.password.PasswordEncoder;

public class DelegatingPasswordEncoder
implements PasswordEncoder {
    private static final String DEFAULT_ID_PREFIX = "{";
    private static final String DEFAULT_ID_SUFFIX = "}";
    private final String idPrefix;
    private final String idSuffix;
    private final String idForEncode;
    private final PasswordEncoder passwordEncoderForEncode;
    private final Map<String, PasswordEncoder> idToPasswordEncoder;
    private PasswordEncoder defaultPasswordEncoderForMatches = new UnmappedIdPasswordEncoder();

    public DelegatingPasswordEncoder(String idForEncode, Map<String, PasswordEncoder> idToPasswordEncoder) {
        this(idForEncode, idToPasswordEncoder, DEFAULT_ID_PREFIX, DEFAULT_ID_SUFFIX);
    }

    public DelegatingPasswordEncoder(String idForEncode, Map<String, PasswordEncoder> idToPasswordEncoder, String idPrefix, String idSuffix) {
        if (idForEncode == null) {
            throw new IllegalArgumentException("idForEncode cannot be null");
        }
        if (idPrefix == null) {
            throw new IllegalArgumentException("prefix cannot be null");
        }
        if (idSuffix == null || idSuffix.isEmpty()) {
            throw new IllegalArgumentException("suffix cannot be empty");
        }
        if (idPrefix.contains(idSuffix)) {
            throw new IllegalArgumentException("idPrefix " + idPrefix + " cannot contain idSuffix " + idSuffix);
        }
        if (!idToPasswordEncoder.containsKey(idForEncode)) {
            throw new IllegalArgumentException("idForEncode " + idForEncode + "is not found in idToPasswordEncoder " + idToPasswordEncoder);
        }
        for (String id : idToPasswordEncoder.keySet()) {
            if (id == null) continue;
            if (!idPrefix.isEmpty() && id.contains(idPrefix)) {
                throw new IllegalArgumentException("id " + id + " cannot contain " + idPrefix);
            }
            if (!id.contains(idSuffix)) continue;
            throw new IllegalArgumentException("id " + id + " cannot contain " + idSuffix);
        }
        this.idForEncode = idForEncode;
        this.passwordEncoderForEncode = idToPasswordEncoder.get(idForEncode);
        this.idToPasswordEncoder = new HashMap<String, PasswordEncoder>(idToPasswordEncoder);
        this.idPrefix = idPrefix;
        this.idSuffix = idSuffix;
    }

    public void setDefaultPasswordEncoderForMatches(PasswordEncoder defaultPasswordEncoderForMatches) {
        if (defaultPasswordEncoderForMatches == null) {
            throw new IllegalArgumentException("defaultPasswordEncoderForMatches cannot be null");
        }
        this.defaultPasswordEncoderForMatches = defaultPasswordEncoderForMatches;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return this.idPrefix + this.idForEncode + this.idSuffix + this.passwordEncoderForEncode.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String prefixEncodedPassword) {
        if (rawPassword == null && prefixEncodedPassword == null) {
            return true;
        }
        String id = this.extractId(prefixEncodedPassword);
        PasswordEncoder delegate = this.idToPasswordEncoder.get(id);
        if (delegate == null) {
            return this.defaultPasswordEncoderForMatches.matches(rawPassword, prefixEncodedPassword);
        }
        String encodedPassword = this.extractEncodedPassword(prefixEncodedPassword);
        return delegate.matches(rawPassword, encodedPassword);
    }

    private String extractId(String prefixEncodedPassword) {
        if (prefixEncodedPassword == null) {
            return null;
        }
        int start = prefixEncodedPassword.indexOf(this.idPrefix);
        if (start != 0) {
            return null;
        }
        int end = prefixEncodedPassword.indexOf(this.idSuffix, start);
        if (end < 0) {
            return null;
        }
        return prefixEncodedPassword.substring(start + this.idPrefix.length(), end);
    }

    @Override
    public boolean upgradeEncoding(String prefixEncodedPassword) {
        String id = this.extractId(prefixEncodedPassword);
        if (!this.idForEncode.equalsIgnoreCase(id)) {
            return true;
        }
        String encodedPassword = this.extractEncodedPassword(prefixEncodedPassword);
        return this.idToPasswordEncoder.get(id).upgradeEncoding(encodedPassword);
    }

    private String extractEncodedPassword(String prefixEncodedPassword) {
        int start = prefixEncodedPassword.indexOf(this.idSuffix);
        return prefixEncodedPassword.substring(start + this.idSuffix.length());
    }

    private class UnmappedIdPasswordEncoder
    implements PasswordEncoder {
        private UnmappedIdPasswordEncoder() {
        }

        @Override
        public String encode(CharSequence rawPassword) {
            throw new UnsupportedOperationException("encode is not supported");
        }

        @Override
        public boolean matches(CharSequence rawPassword, String prefixEncodedPassword) {
            String id = DelegatingPasswordEncoder.this.extractId(prefixEncodedPassword);
            throw new IllegalArgumentException("There is no PasswordEncoder mapped for the id \"" + id + "\"");
        }
    }
}

