/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.device;

import com.nimbusds.oauth2.sdk.id.Identifier;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import net.jcip.annotations.Immutable;

@Immutable
public final class UserCode
extends Identifier {
    private static final long serialVersionUID = 6249537737406015901L;
    public static final String LETTER_CHAR_SET = "BCDFGHJKLMNPQRSTVWXZ";
    public static final String DIGIT_CHAR_SET = "0123456789";
    private final String charset;

    public UserCode(String value, String charset) {
        super(value);
        this.charset = charset;
    }

    public UserCode(String value) {
        this(value, LETTER_CHAR_SET);
    }

    public UserCode() {
        this(LETTER_CHAR_SET, 8);
    }

    public UserCode(String charset, int length) {
        this(UserCode.generateValue(charset, length), charset);
    }

    private static String generateValue(String charset, int length) {
        if (StringUtils.isBlank(charset)) {
            throw new IllegalArgumentException("The charset must not be null or empty string");
        }
        StringBuilder value = new StringBuilder();
        for (int index = 0; index < length; ++index) {
            if (index > 0 && index % 4 == 0) {
                value.append('-');
            }
            value.append(charset.charAt(secureRandom.nextInt(charset.length())));
        }
        return value.toString();
    }

    public String getCharset() {
        return this.charset;
    }

    public String getStrippedValue() {
        return UserCode.stripIllegalChars(this.getValue(), this.getCharset());
    }

    @Override
    public int compareTo(Identifier other) {
        if (!(other instanceof UserCode)) {
            return super.compareTo(other);
        }
        return this.getStrippedValue().compareTo(((UserCode)other).getStrippedValue());
    }

    @Override
    public int hashCode() {
        return this.getStrippedValue() != null ? this.getStrippedValue().hashCode() : 0;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof UserCode && this.getStrippedValue().equals(((UserCode)object).getStrippedValue());
    }

    public static String stripIllegalChars(String value, String charset) {
        if (charset == null) {
            return value.toUpperCase();
        }
        StringBuilder newValue = new StringBuilder();
        for (char curChar : value.toUpperCase().toCharArray()) {
            if (charset.indexOf(curChar) < 0) continue;
            newValue.append(curChar);
        }
        return newValue.toString();
    }
}

