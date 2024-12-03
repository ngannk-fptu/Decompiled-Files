/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.LocaleUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.message.Message
 *  org.apache.logging.log4j.message.ParameterizedMessage
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.LocaleProvider;
import java.util.Locale;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ParameterizedMessage;

public class DefaultLocaleProvider
implements LocaleProvider {
    private static final Logger LOG = LogManager.getLogger(DefaultLocaleProvider.class);

    @Override
    public Locale getLocale() {
        ActionContext ctx = ActionContext.getContext();
        if (ctx != null) {
            return ctx.getLocale();
        }
        LOG.debug("Action context not initialized");
        return null;
    }

    @Override
    public boolean isValidLocaleString(String localeStr) {
        Locale locale = null;
        try {
            locale = LocaleUtils.toLocale((String)StringUtils.trimToNull((String)localeStr));
        }
        catch (IllegalArgumentException e) {
            LOG.warn((Message)new ParameterizedMessage("Cannot convert [{}] to proper locale", (Object)localeStr), (Throwable)e);
        }
        return this.isValidLocale(locale);
    }

    @Override
    public boolean isValidLocale(Locale locale) {
        return LocaleUtils.isAvailableLocale((Locale)locale);
    }
}

