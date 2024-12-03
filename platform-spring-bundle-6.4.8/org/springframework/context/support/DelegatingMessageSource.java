/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.support;

import java.util.Locale;
import org.springframework.context.HierarchicalMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceSupport;
import org.springframework.lang.Nullable;

public class DelegatingMessageSource
extends MessageSourceSupport
implements HierarchicalMessageSource {
    @Nullable
    private MessageSource parentMessageSource;

    @Override
    public void setParentMessageSource(@Nullable MessageSource parent) {
        this.parentMessageSource = parent;
    }

    @Override
    @Nullable
    public MessageSource getParentMessageSource() {
        return this.parentMessageSource;
    }

    @Override
    @Nullable
    public String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale) {
        if (this.parentMessageSource != null) {
            return this.parentMessageSource.getMessage(code, args, defaultMessage, locale);
        }
        if (defaultMessage != null) {
            return this.renderDefaultMessage(defaultMessage, args, locale);
        }
        return null;
    }

    @Override
    public String getMessage(String code, @Nullable Object[] args, Locale locale) throws NoSuchMessageException {
        if (this.parentMessageSource != null) {
            return this.parentMessageSource.getMessage(code, args, locale);
        }
        throw new NoSuchMessageException(code, locale);
    }

    @Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        if (this.parentMessageSource != null) {
            return this.parentMessageSource.getMessage(resolvable, locale);
        }
        if (resolvable.getDefaultMessage() != null) {
            return this.renderDefaultMessage(resolvable.getDefaultMessage(), resolvable.getArguments(), locale);
        }
        String[] codes = resolvable.getCodes();
        String code = codes != null && codes.length > 0 ? codes[0] : "";
        throw new NoSuchMessageException(code, locale);
    }

    public String toString() {
        return this.parentMessageSource != null ? this.parentMessageSource.toString() : "Empty MessageSource";
    }
}

