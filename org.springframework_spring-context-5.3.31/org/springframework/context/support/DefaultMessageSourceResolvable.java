/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
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
        result.append("codes [").append(StringUtils.arrayToDelimitedString((Object[])this.codes, (String)","));
        result.append("]; arguments [").append(StringUtils.arrayToDelimitedString((Object[])this.arguments, (String)","));
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
        return ObjectUtils.nullSafeEquals((Object)this.getCodes(), (Object)otherResolvable.getCodes()) && ObjectUtils.nullSafeEquals((Object)this.getArguments(), (Object)otherResolvable.getArguments()) && ObjectUtils.nullSafeEquals((Object)this.getDefaultMessage(), (Object)otherResolvable.getDefaultMessage());
    }

    public int hashCode() {
        int hashCode = ObjectUtils.nullSafeHashCode((Object[])this.getCodes());
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode((Object[])this.getArguments());
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode((Object)this.getDefaultMessage());
        return hashCode;
    }
}

