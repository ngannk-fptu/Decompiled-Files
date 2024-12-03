/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.core.util;

import com.atlassian.applinks.core.util.Message;
import com.atlassian.sal.api.message.I18nResolver;
import java.io.Serializable;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;

public class MessageFactory {
    private final I18nResolver resolver;

    @Autowired
    public MessageFactory(I18nResolver resolver) {
        this.resolver = Objects.requireNonNull(resolver, "resolver");
    }

    public Message newI18nMessage(final String key, final Serializable ... params) {
        Objects.requireNonNull(this.resolver, "resolver");
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(params, "params");
        return new Message(){

            public String toString() {
                return MessageFactory.this.resolver.getText(key, params);
            }
        };
    }

    public Message newLocalizedMessage(final String message) {
        return new Message(){

            public String toString() {
                return message;
            }
        };
    }
}

