/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.support;

import java.io.Serializable;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class DefaultMessageSourceResolvable
implements MessageSourceResolvable,
Serializable {
    @Nullable
    private final String[] codes;
    @Nullable
    private final Object[] arguments;
    @Nullable
    private final String defaultMessage;

    public DefaultMessageSourceResolvable(String code) {
        this(new String[]{code}, null, null);
    }

    public DefaultMessageSourceResolvable(String[] codes) {
        this(codes, null, null);
    }

    public DefaultMessageSourceResolvable(String[] codes, String defaultMessage) {
        this(codes, null, defaultMessage);
    }

    public DefaultMessageSourceResolvable(String[] codes, Object[] arguments) {
        this(codes, arguments, null);
    }

    public DefaultMessageSourceResolvable(@Nullable String[] codes, @Nullable Object[] arguments, @Nullable String defaultMessage) {
        this.codes = codes;
        this.arguments = arguments;
        this.defaultMessage = defaultMessage;
    }

    public DefaultMessageSourceResolvable(MessageSourceResolvable resolvable) {
        this(resolvable.getCodes(), resolvable.getArguments(), resolvable.getDefaultMessage());
    }

    @Nullable
    public String getCode() {
        return this.codes != null && this.codes.length > 0 ? this.codes[this.codes.length - 1] : null;
    }

    @Override
    @Nullable
    public String[] getCodes() {
        return this.codes;
    }

    @Override
    @Nullable
    public Object[] getArguments() {
        return this.arguments;
    }

    @Override
    @Nullable
    public String getDefaultMessage() {
        return this.defaultMessage;
    }

    public boolean shouldRenderDefaultMessage() {
        return true;
    }

    protected final String resolvableToString() {
        StringBuilder result = new StringBuilder(64);
        result.append("codes [").append(StringUtils.arrayToDelimitedString(this.codes, ","));
        result.append("]; arguments [").append(StringUtils.arrayToDelimitedString(this.arguments, ","));
        result.append("]; default message [").append(this.defaultMessage).append(']');
        return result.toString();
    }

    public String toString() {
        return this.getClass().getName() + ": " + this.resolvableToString();
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MessageSourceResolvable)) {
            return false;
        }
        MessageSourceResolvable otherResolvable = (MessageSourceResolvable)other;
        return ObjectUtils.nullSafeEquals(this.getCodes(), otherResolvable.getCodes()) && ObjectUtils.nullSafeEquals(this.getArguments(), otherResolvable.getArguments()) && ObjectUtils.nullSafeEquals(this.getDefaultMessage(), otherResolvable.getDefaultMessage());
    }

    public int hashCode() {
        int hashCode = ObjectUtils.nullSafeHashCode(this.getCodes());
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.getArguments());
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.getDefaultMessage());
        return hashCode;
    }
}

