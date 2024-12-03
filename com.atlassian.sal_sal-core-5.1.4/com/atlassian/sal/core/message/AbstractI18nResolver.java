/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.message.MessageCollection
 *  com.google.common.base.Preconditions
 */
package com.atlassian.sal.core.message;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.message.MessageCollection;
import com.atlassian.sal.core.message.DefaultMessage;
import com.atlassian.sal.core.message.DefaultMessageCollection;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Locale;

public abstract class AbstractI18nResolver
implements I18nResolver {
    private static final Serializable[] EMPTY_SERIALIZABLE = new Serializable[0];

    public String getText(String key, Serializable ... arguments) {
        Serializable[] resolvedArguments = new Serializable[arguments.length];
        for (int i = 0; i < arguments.length; ++i) {
            Serializable argument = arguments[i];
            resolvedArguments[i] = argument instanceof Message ? this.getText((Message)argument) : arguments[i];
        }
        return this.resolveText(key, resolvedArguments);
    }

    public String getText(Locale locale, String key, Serializable ... arguments) {
        Preconditions.checkNotNull((Object)locale);
        Serializable[] resolvedArguments = new Serializable[arguments.length];
        for (int i = 0; i < arguments.length; ++i) {
            Serializable argument = arguments[i];
            resolvedArguments[i] = argument instanceof Message ? this.getText(locale, (Message)argument) : arguments[i];
        }
        return this.resolveText(locale, key, resolvedArguments);
    }

    public String getText(String key) {
        return this.resolveText(key, EMPTY_SERIALIZABLE);
    }

    public String getText(Locale locale, String key) {
        Preconditions.checkNotNull((Object)locale);
        return this.resolveText(locale, key, EMPTY_SERIALIZABLE);
    }

    public String getText(Message message) {
        return this.getText(message.getKey(), message.getArguments());
    }

    public String getText(Locale locale, Message message) {
        return this.getText(locale, message.getKey(), message.getArguments());
    }

    public abstract String resolveText(String var1, Serializable[] var2);

    public abstract String resolveText(Locale var1, String var2, Serializable[] var3);

    public Message createMessage(String key, Serializable ... arguments) {
        return new DefaultMessage(key, arguments);
    }

    public MessageCollection createMessageCollection() {
        return new DefaultMessageCollection();
    }
}

