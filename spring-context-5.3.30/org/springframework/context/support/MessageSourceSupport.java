/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.context.support;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

public abstract class MessageSourceSupport {
    private static final MessageFormat INVALID_MESSAGE_FORMAT = new MessageFormat("");
    protected final Log logger = LogFactory.getLog(this.getClass());
    private boolean alwaysUseMessageFormat = false;
    private final Map<String, Map<Locale, MessageFormat>> messageFormatsPerMessage = new HashMap<String, Map<Locale, MessageFormat>>();

    public void setAlwaysUseMessageFormat(boolean alwaysUseMessageFormat) {
        this.alwaysUseMessageFormat = alwaysUseMessageFormat;
    }

    protected boolean isAlwaysUseMessageFormat() {
        return this.alwaysUseMessageFormat;
    }

    protected String renderDefaultMessage(String defaultMessage, @Nullable Object[] args, Locale locale) {
        return this.formatMessage(defaultMessage, args, locale);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected String formatMessage(String msg, @Nullable Object[] args, Locale locale) {
        if (!this.isAlwaysUseMessageFormat() && ObjectUtils.isEmpty((Object[])args)) {
            return msg;
        }
        MessageFormat messageFormat = null;
        Object object = this.messageFormatsPerMessage;
        synchronized (object) {
            Map<Locale, MessageFormat> messageFormatsPerLocale = this.messageFormatsPerMessage.get(msg);
            if (messageFormatsPerLocale != null) {
                messageFormat = messageFormatsPerLocale.get(locale);
            } else {
                messageFormatsPerLocale = new HashMap<Locale, MessageFormat>();
                this.messageFormatsPerMessage.put(msg, messageFormatsPerLocale);
            }
            if (messageFormat == null) {
                try {
                    messageFormat = this.createMessageFormat(msg, locale);
                }
                catch (IllegalArgumentException ex) {
                    if (this.isAlwaysUseMessageFormat()) {
                        throw ex;
                    }
                    messageFormat = INVALID_MESSAGE_FORMAT;
                }
                messageFormatsPerLocale.put(locale, messageFormat);
            }
        }
        if (messageFormat == INVALID_MESSAGE_FORMAT) {
            return msg;
        }
        object = messageFormat;
        synchronized (object) {
            return messageFormat.format(this.resolveArguments(args, locale));
        }
    }

    protected MessageFormat createMessageFormat(String msg, Locale locale) {
        return new MessageFormat(msg, locale);
    }

    protected Object[] resolveArguments(@Nullable Object[] args, Locale locale) {
        return args != null ? args : new Object[]{};
    }
}

