/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.Message
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.common.i18n;

import com.atlassian.sal.api.message.Message;
import java.io.Serializable;
import java.util.Objects;
import javax.annotation.Nonnull;

public class I18nKey
implements Message {
    private final String key;
    private final Serializable[] args;

    @Nonnull
    public static I18nKey newI18nKey(@Nonnull String key, Serializable ... args) {
        return new I18nKey(Objects.requireNonNull(key, "key"), Objects.requireNonNull(args, "args"));
    }

    private I18nKey(String key, Serializable[] args) {
        this.key = key;
        this.args = args;
    }

    @Nonnull
    public String getKey() {
        return this.key;
    }

    @Nonnull
    public Serializable[] getArguments() {
        return this.args;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.key);
        builder.append(": ");
        for (Serializable argument : this.args) {
            builder.append(argument);
            builder.append(",");
        }
        return builder.toString();
    }
}

