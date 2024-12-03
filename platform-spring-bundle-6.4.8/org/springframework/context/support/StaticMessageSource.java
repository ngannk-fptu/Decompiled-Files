/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.support;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class StaticMessageSource
extends AbstractMessageSource {
    private final Map<String, Map<Locale, MessageHolder>> messageMap = new HashMap<String, Map<Locale, MessageHolder>>();

    @Override
    @Nullable
    protected String resolveCodeWithoutArguments(String code, Locale locale) {
        Map<Locale, MessageHolder> localeMap = this.messageMap.get(code);
        if (localeMap == null) {
            return null;
        }
        MessageHolder holder = localeMap.get(locale);
        if (holder == null) {
            return null;
        }
        return holder.getMessage();
    }

    @Override
    @Nullable
    protected MessageFormat resolveCode(String code, Locale locale) {
        Map<Locale, MessageHolder> localeMap = this.messageMap.get(code);
        if (localeMap == null) {
            return null;
        }
        MessageHolder holder = localeMap.get(locale);
        if (holder == null) {
            return null;
        }
        return holder.getMessageFormat();
    }

    public void addMessage(String code, Locale locale, String msg) {
        Assert.notNull((Object)code, "Code must not be null");
        Assert.notNull((Object)locale, "Locale must not be null");
        Assert.notNull((Object)msg, "Message must not be null");
        this.messageMap.computeIfAbsent(code, key -> new HashMap(4)).put(locale, new MessageHolder(msg, locale));
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Added message [" + msg + "] for code [" + code + "] and Locale [" + locale + "]"));
        }
    }

    public void addMessages(Map<String, String> messages, Locale locale) {
        Assert.notNull(messages, "Messages Map must not be null");
        messages.forEach((code, msg) -> this.addMessage((String)code, locale, (String)msg));
    }

    public String toString() {
        return this.getClass().getName() + ": " + this.messageMap;
    }

    private class MessageHolder {
        private final String message;
        private final Locale locale;
        @Nullable
        private volatile MessageFormat cachedFormat;

        public MessageHolder(String message, Locale locale) {
            this.message = message;
            this.locale = locale;
        }

        public String getMessage() {
            return this.message;
        }

        public MessageFormat getMessageFormat() {
            MessageFormat messageFormat = this.cachedFormat;
            if (messageFormat == null) {
                this.cachedFormat = messageFormat = StaticMessageSource.this.createMessageFormat(this.message, this.locale);
            }
            return messageFormat;
        }

        public String toString() {
            return this.message;
        }
    }
}

