/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.LocaleProviderFactory;
import com.opensymphony.xwork2.LocalizedTextProvider;
import com.opensymphony.xwork2.ResourceBundleTextProvider;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.TextProviderFactory;
import com.opensymphony.xwork2.TextProviderSupport;
import com.opensymphony.xwork2.inject.Inject;
import java.util.ResourceBundle;

public class StrutsTextProviderFactory
implements TextProviderFactory {
    protected LocaleProviderFactory localeProviderFactory;
    protected LocalizedTextProvider localizedTextProvider;
    protected TextProvider defaultTextProvider;

    @Inject
    public void setLocaleProviderFactory(LocaleProviderFactory localeProviderFactory) {
        this.localeProviderFactory = localeProviderFactory;
    }

    @Inject
    public void setLocalizedTextProvider(LocalizedTextProvider localizedTextProvider) {
        this.localizedTextProvider = localizedTextProvider;
    }

    @Inject(required=false)
    public void setDefaultTextProvider(TextProvider defaultTextProvider) {
        this.defaultTextProvider = defaultTextProvider;
    }

    @Override
    public TextProvider createInstance(Class clazz) {
        TextProvider instance = this.getTextProvider(clazz);
        if (instance instanceof ResourceBundleTextProvider) {
            ((ResourceBundleTextProvider)instance).setClazz(clazz);
            ((ResourceBundleTextProvider)instance).setLocaleProvider(this.localeProviderFactory.createLocaleProvider());
        }
        return instance;
    }

    @Override
    public TextProvider createInstance(ResourceBundle bundle) {
        TextProvider instance = this.getTextProvider(bundle);
        if (instance instanceof ResourceBundleTextProvider) {
            ((ResourceBundleTextProvider)instance).setBundle(bundle);
            ((ResourceBundleTextProvider)instance).setLocaleProvider(this.localeProviderFactory.createLocaleProvider());
        }
        return instance;
    }

    protected TextProvider getTextProvider(Class clazz) {
        if (this.defaultTextProvider != null) {
            return this.defaultTextProvider;
        }
        return new TextProviderSupport(clazz, this.localeProviderFactory.createLocaleProvider(), this.localizedTextProvider);
    }

    protected TextProvider getTextProvider(ResourceBundle bundle) {
        if (this.defaultTextProvider != null) {
            return this.defaultTextProvider;
        }
        return new TextProviderSupport(bundle, this.localeProviderFactory.createLocaleProvider(), this.localizedTextProvider);
    }
}

