/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Strings
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.plugins.navlink.util.i18n;

import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LocaleSupportingI18nResolverWorkAround {
    private final I18nResolver i18nResolver;
    private final Method getRawTextMethod;

    public LocaleSupportingI18nResolverWorkAround(I18nResolver i18nResolver) {
        Method getRawTextMethod;
        this.i18nResolver = i18nResolver;
        try {
            getRawTextMethod = I18nResolver.class.getDeclaredMethod("getRawText", Locale.class, String.class);
        }
        catch (Exception e) {
            getRawTextMethod = null;
        }
        this.getRawTextMethod = getRawTextMethod;
    }

    @Nonnull
    public String getText(@Nonnull Locale locale, @Nullable String key) {
        Preconditions.checkNotNull((Object)locale);
        return key != null ? Strings.nullToEmpty((String)this.translateKey(key, locale)) : "";
    }

    @Nonnull
    public String getText(@Nonnull Locale locale, @Nullable String key, Serializable ... arguments) {
        Preconditions.checkNotNull((Object)locale);
        Preconditions.checkNotNull((Object)arguments);
        String pattern = this.getText(locale, key);
        try {
            return MessageFormat.format(pattern, arguments);
        }
        catch (RuntimeException e) {
            return pattern;
        }
    }

    @Nullable
    private String translateKey(@Nonnull String key, @Nonnull Locale locale) {
        String pattern = null;
        if (this.getRawTextMethod != null) {
            try {
                pattern = (String)this.getRawTextMethod.invoke((Object)this.i18nResolver, locale, key);
            }
            catch (Exception e) {
                pattern = null;
            }
        }
        if (pattern == null) {
            pattern = (String)this.i18nResolver.getAllTranslationsForPrefix(key, locale).get(key);
        }
        return (String)MoreObjects.firstNonNull(pattern, (Object)key);
    }
}

