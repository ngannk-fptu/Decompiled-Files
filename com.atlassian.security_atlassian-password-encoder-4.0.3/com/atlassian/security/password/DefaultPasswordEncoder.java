/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.utils.ConstantTimeComparison
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.commons.codec.binary.StringUtils
 *  org.apache.commons.lang.ArrayUtils
 *  org.apache.commons.lang.Validate
 */
package com.atlassian.security.password;

import com.atlassian.security.password.PKCS5S2PasswordHashGenerator;
import com.atlassian.security.password.PasswordEncoder;
import com.atlassian.security.password.PasswordHashGenerator;
import com.atlassian.security.password.RandomSaltGenerator;
import com.atlassian.security.password.SaltGenerator;
import com.atlassian.security.utils.ConstantTimeComparison;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.Validate;

public final class DefaultPasswordEncoder
implements PasswordEncoder {
    private static final PasswordEncoder DEFAULT_INSTANCE = new DefaultPasswordEncoder("PKCS5S2", new PKCS5S2PasswordHashGenerator(), new RandomSaltGenerator());
    private static final int DEFAULT_SALT_LENGTH_BYTES = 16;
    private final String prefix;
    private final PasswordHashGenerator hashGenerator;
    private final SaltGenerator saltGenerator;

    public static PasswordEncoder getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static PasswordEncoder newInstance(String identifier, PasswordHashGenerator hashGenerator) {
        return new DefaultPasswordEncoder(identifier, hashGenerator, new RandomSaltGenerator());
    }

    public DefaultPasswordEncoder(String identifier, PasswordHashGenerator hashGenerator, SaltGenerator saltGenerator) {
        this.prefix = "{" + identifier + "}";
        this.hashGenerator = hashGenerator;
        this.saltGenerator = saltGenerator;
    }

    @Override
    public final boolean canDecodePassword(String encodedPassword) {
        return encodedPassword != null && encodedPassword.startsWith(this.prefix);
    }

    @Override
    public final String encodePassword(String rawPassword) throws IllegalArgumentException {
        Validate.notEmpty((String)rawPassword, (String)"Password must not be empty");
        byte[] salt = this.saltGenerator.generateSalt(this.getSaltLength());
        byte[] hash = this.hashGenerator.generateHash(StringUtils.getBytesUtf8((String)rawPassword), salt);
        String encodedPassword = this.toEncodedForm(salt, hash);
        return this.prependPrefix(encodedPassword);
    }

    private int getSaltLength() {
        if (this.hashGenerator.getRequiredSaltLength() > 0) {
            return this.hashGenerator.getRequiredSaltLength();
        }
        return 16;
    }

    @Override
    public final boolean isValidPassword(String rawPassword, String prefixedEncodedPassword) throws IllegalArgumentException {
        Validate.notNull((Object)rawPassword);
        Validate.notNull((Object)prefixedEncodedPassword);
        if (!this.canDecodePassword(prefixedEncodedPassword)) {
            return false;
        }
        String encodedPassword = this.removePrefix(prefixedEncodedPassword);
        byte[] storedBytes = this.fromEncodedForm(encodedPassword);
        byte[] salt = ArrayUtils.subarray((byte[])storedBytes, (int)0, (int)this.getSaltLength());
        byte[] storedHash = ArrayUtils.subarray((byte[])storedBytes, (int)this.getSaltLength(), (int)storedBytes.length);
        byte[] hashAttempt = this.hashGenerator.generateHash(StringUtils.getBytesUtf8((String)rawPassword), salt);
        return ConstantTimeComparison.isEqual((byte[])storedHash, (byte[])hashAttempt);
    }

    private String prependPrefix(String encodedPassword) {
        return this.prefix + encodedPassword;
    }

    private String removePrefix(String encodedPassword) {
        return encodedPassword.substring(this.prefix.length());
    }

    private byte[] fromEncodedForm(String encodedPassword) {
        return Base64.decodeBase64((byte[])StringUtils.getBytesUtf8((String)encodedPassword));
    }

    private String toEncodedForm(byte[] salt, byte[] hash) {
        byte[] saltAndHash = ArrayUtils.addAll((byte[])salt, (byte[])hash);
        byte[] base64 = Base64.encodeBase64((byte[])saltAndHash);
        return StringUtils.newStringUtf8((byte[])base64);
    }
}

