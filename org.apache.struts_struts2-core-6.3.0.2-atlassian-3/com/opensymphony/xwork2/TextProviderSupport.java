/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.LocaleProviderFactory;
import com.opensymphony.xwork2.LocalizedTextProvider;
import com.opensymphony.xwork2.ResourceBundleTextProvider;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class TextProviderSupport
implements ResourceBundleTextProvider {
    protected Class clazz;
    protected LocaleProvider localeProvider;
    protected ResourceBundle bundle;
    protected LocalizedTextProvider localizedTextProvider;

    public TextProviderSupport(Class clazz, LocaleProvider provider, LocalizedTextProvider localizedTextProvider) {
        this.clazz = clazz;
        this.localeProvider = provider;
        this.localizedTextProvider = localizedTextProvider;
    }

    public TextProviderSupport(ResourceBundle bundle, LocaleProvider provider, LocalizedTextProvider localizedTextProvider) {
        this.bundle = bundle;
        this.localeProvider = provider;
        this.localizedTextProvider = localizedTextProvider;
    }

    @Override
    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    @Override
    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    @Override
    public void setLocaleProvider(LocaleProvider localeProvider) {
        this.localeProvider = localeProvider;
    }

    @Inject
    public void setLocaleProviderFactory(LocaleProviderFactory localeProviderFactory) {
        this.localeProvider = localeProviderFactory.createLocaleProvider();
    }

    @Inject
    public void setLocalizedTextProvider(LocalizedTextProvider localizedTextProvider) {
        this.localizedTextProvider = localizedTextProvider;
    }

    @Override
    public boolean hasKey(String key) {
        String message = this.clazz != null ? this.localizedTextProvider.findText(this.clazz, key, this.getLocale(), null, new Object[0]) : this.localizedTextProvider.findText(this.bundle, key, this.getLocale(), null, new Object[0]);
        return message != null;
    }

    @Override
    public String getText(String key) {
        return this.getText(key, key, Collections.emptyList());
    }

    @Override
    public String getText(String key, String defaultValue) {
        return this.getText(key, defaultValue, Collections.emptyList());
    }

    @Override
    public String getText(String key, String defaultValue, String arg) {
        ArrayList<String> args = new ArrayList<String>();
        args.add(arg);
        return this.getText(key, defaultValue, args);
    }

    @Override
    public String getText(String key, List<?> args) {
        return this.getText(key, key, args);
    }

    @Override
    public String getText(String key, String[] args) {
        return this.getText(key, key, args);
    }

    @Override
    public String getText(String key, String defaultValue, List<?> args) {
        Object[] argsArray;
        Object[] objectArray = argsArray = args != null && !args.equals(Collections.emptyList()) ? args.toArray() : null;
        if (this.clazz != null) {
            return this.localizedTextProvider.findText(this.clazz, key, this.getLocale(), defaultValue, argsArray);
        }
        return this.localizedTextProvider.findText(this.bundle, key, this.getLocale(), defaultValue, argsArray);
    }

    @Override
    public String getText(String key, String defaultValue, String[] args) {
        if (this.clazz != null) {
            return this.localizedTextProvider.findText(this.clazz, key, this.getLocale(), defaultValue, (Object[])args);
        }
        return this.localizedTextProvider.findText(this.bundle, key, this.getLocale(), defaultValue, (Object[])args);
    }

    @Override
    public String getText(String key, String defaultValue, List<?> args, ValueStack stack) {
        Object[] argsArray = args != null ? args.toArray() : null;
        Locale locale = stack == null ? this.getLocale() : stack.getActionContext().getLocale();
        if (locale == null) {
            locale = this.getLocale();
        }
        if (this.clazz != null) {
            return this.localizedTextProvider.findText(this.clazz, key, locale, defaultValue, argsArray, stack);
        }
        return this.localizedTextProvider.findText(this.bundle, key, locale, defaultValue, argsArray, stack);
    }

    @Override
    public String getText(String key, String defaultValue, String[] args, ValueStack stack) {
        Locale locale = stack == null ? this.getLocale() : stack.getActionContext().getLocale();
        if (locale == null) {
            locale = this.getLocale();
        }
        if (this.clazz != null) {
            return this.localizedTextProvider.findText(this.clazz, key, locale, defaultValue, (Object[])args, stack);
        }
        return this.localizedTextProvider.findText(this.bundle, key, locale, defaultValue, (Object[])args, stack);
    }

    @Override
    public ResourceBundle getTexts(String aBundleName) {
        return this.localizedTextProvider.findResourceBundle(aBundleName, this.getLocale());
    }

    @Override
    public ResourceBundle getTexts() {
        if (this.clazz != null) {
            return this.getTexts(this.clazz.getName());
        }
        return this.bundle;
    }

    private Locale getLocale() {
        return this.localeProvider.getLocale();
    }
}

