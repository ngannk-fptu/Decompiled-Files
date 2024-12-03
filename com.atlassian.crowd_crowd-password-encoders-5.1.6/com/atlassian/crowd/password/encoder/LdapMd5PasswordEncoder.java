/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.utils.ConstantTimeComparison
 *  com.google.common.base.Strings
 *  org.springframework.security.crypto.codec.Utf8
 */
package com.atlassian.crowd.password.encoder;

import com.atlassian.crowd.password.encoder.InternalPasswordEncoder;
import com.atlassian.crowd.password.encoder.LdapPasswordEncoder;
import com.atlassian.security.utils.ConstantTimeComparison;
import com.google.common.base.Strings;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Locale;
import java.util.Optional;
import org.springframework.security.crypto.codec.Utf8;

public class LdapMd5PasswordEncoder
implements InternalPasswordEncoder,
LdapPasswordEncoder {
    static final String MD5_PREFIX = "{MD5}";
    static final String MD5_PREFIX_LC = "{MD5}".toLowerCase(Locale.ENGLISH);
    static final String MD5_ALGORITHM_KEY = "md5";
    private boolean forceLowerCasePrefix = false;

    private final String prefix() {
        return this.forceLowerCasePrefix ? MD5_PREFIX_LC : MD5_PREFIX;
    }

    @Override
    public String encodePassword(String rawPass, Object salt) {
        String saltedPassword = Optional.ofNullable(salt).filter(s -> !"".equals(s)).map(s -> String.format("%s{%s}", Strings.nullToEmpty((String)rawPass), s.toString())).orElseGet(() -> Strings.nullToEmpty((String)rawPass));
        MessageDigest messageDigest = this.getMd5MessageDigest();
        byte[] digest = messageDigest.digest(Utf8.encode((CharSequence)saltedPassword));
        return this.prefix() + Utf8.decode((byte[])Base64.getEncoder().encode(digest));
    }

    @Override
    public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
        String encPassWithoutPrefix = encPass.startsWith(MD5_PREFIX) || encPass.startsWith(MD5_PREFIX_LC) ? encPass.substring(MD5_PREFIX.length()) : encPass;
        return ConstantTimeComparison.isEqual((String)this.encodePassword(rawPass, salt), (String)(this.prefix() + encPassWithoutPrefix));
    }

    @Override
    public String getKey() {
        return MD5_ALGORITHM_KEY;
    }

    public void setForceLowerCasePrefix(boolean forceLowerCasePrefix) {
        this.forceLowerCasePrefix = forceLowerCasePrefix;
    }

    private MessageDigest getMd5MessageDigest() {
        try {
            return MessageDigest.getInstance(MD5_ALGORITHM_KEY);
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

