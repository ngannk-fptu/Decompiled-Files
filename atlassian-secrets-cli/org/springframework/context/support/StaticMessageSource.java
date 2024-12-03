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
    private final Map<String, String> messages = new HashMap<String, String>();
    private final Map<String, MessageFormat> cachedMessageFormats = new HashMap<String, MessageFormat>();

    @Override
    protected String resolveCodeWithoutArguments(String code, Locale locale) {
        return this.messages.get(code + '_' + locale.toString());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Nullable
    protected MessageFormat resolveCode(String code, Locale locale) {
        String key = code + '_' + locale.toString();
        String msg = this.messages.get(key);
        if (msg == null) {
            return null;
        }
        Map<String, MessageFormat> map = this.cachedMessageFormats;
        synchronized (map) {
            MessageFormat messageFormat = this.cachedMessageFormats.get(key);
            if (messageFormat == null) {
                messageFormat = this.createMessageFormat(msg, locale);
                this.cachedMessageFormats.put(key, messageFormat);
            }
            return messageFormat;
        }
    }

    public void addMessage(String code, Locale locale, String msg) {
        Assert.notNull((Object)code, "Code must not be null");
        Assert.notNull((Object)locale, "Locale must not be null");
        Assert.notNull((Object)msg, "Message must not be null");
        this.messages.put(code + '_' + locale.toString(), msg);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Added message [" + msg + "] for code [" + code + "] and Locale [" + locale + "]");
        }
    }

    public void addMessages(Map<String, String> messages, Locale locale) {
        Assert.notNull(messages, "Messages Map must not be null");
        messages.forEach((code, msg) -> this.addMessage((String)code, locale, (String)msg));
    }

    public String toString() {
        return this.getClass().getName() + ": " + this.messages;
    }
}

