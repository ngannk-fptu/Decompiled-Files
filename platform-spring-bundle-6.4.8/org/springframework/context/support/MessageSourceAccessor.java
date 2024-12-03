/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.support;

import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;

public class MessageSourceAccessor {
    private final MessageSource messageSource;
    @Nullable
    private final Locale defaultLocale;

    public MessageSourceAccessor(MessageSource messageSource) {
        this.messageSource = messageSource;
        this.defaultLocale = null;
    }

    public MessageSourceAccessor(MessageSource messageSource, Locale defaultLocale) {
        this.messageSource = messageSource;
        this.defaultLocale = defaultLocale;
    }

    protected Locale getDefaultLocale() {
        return this.defaultLocale != null ? this.defaultLocale : LocaleContextHolder.getLocale();
    }

    public String getMessage(String code, String defaultMessage) {
        String msg = this.messageSource.getMessage(code, null, defaultMessage, this.getDefaultLocale());
        return msg != null ? msg : "";
    }

    public String getMessage(String code, String defaultMessage, Locale locale) {
        String msg = this.messageSource.getMessage(code, null, defaultMessage, locale);
        return msg != null ? msg : "";
    }

    public String getMessage(String code, @Nullable Object[] args, String defaultMessage) {
        String msg = this.messageSource.getMessage(code, args, defaultMessage, this.getDefaultLocale());
        return msg != null ? msg : "";
    }

    public String getMessage(String code, @Nullable Object[] args, String defaultMessage, Locale locale) {
        String msg = this.messageSource.getMessage(code, args, defaultMessage, locale);
        return msg != null ? msg : "";
    }

    public String getMessage(String code) throws NoSuchMessageException {
        return this.messageSource.getMessage(code, null, this.getDefaultLocale());
    }

    public String getMessage(String code, Locale locale) throws NoSuchMessageException {
        return this.messageSource.getMessage(code, null, locale);
    }

    public String getMessage(String code, @Nullable Object[] args) throws NoSuchMessageException {
        return this.messageSource.getMessage(code, args, this.getDefaultLocale());
    }

    public String getMessage(String code, @Nullable Object[] args, Locale locale) throws NoSuchMessageException {
        return this.messageSource.getMessage(code, args, locale);
    }

    public String getMessage(MessageSourceResolvable resolvable) throws NoSuchMessageException {
        return this.messageSource.getMessage(resolvable, this.getDefaultLocale());
    }

    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        return this.messageSource.getMessage(resolvable, locale);
    }
}

