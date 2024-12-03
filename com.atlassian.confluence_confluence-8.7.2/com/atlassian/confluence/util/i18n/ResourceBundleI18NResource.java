/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.LocaleUtils
 */
package com.atlassian.confluence.util.i18n;

import com.atlassian.confluence.util.i18n.I18NResource;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.commons.lang3.LocaleUtils;

public abstract class ResourceBundleI18NResource
implements I18NResource {
    @Override
    public ResourceBundle getBundle() {
        return this.getBundle(Locale.ROOT);
    }

    @Override
    public ResourceBundle getBundle(String localeString) {
        return this.getBundle(LocaleUtils.toLocale((String)localeString));
    }

    private ResourceBundle getBundle(Locale locale) {
        try {
            return ResourceBundle.getBundle(this.getLocation(), locale, this.getClassLoader(), this.getControl());
        }
        catch (MissingResourceException e) {
            return null;
        }
    }

    protected abstract String getLocation();

    protected abstract ClassLoader getClassLoader();

    protected abstract ResourceBundle.Control getControl();
}

