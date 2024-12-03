/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.util.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.xmlgraphics.util.i18n.LocaleGroup;
import org.apache.xmlgraphics.util.i18n.Localizable;

public class LocalizableSupport
implements Localizable {
    protected LocaleGroup localeGroup = LocaleGroup.DEFAULT;
    protected String bundleName;
    protected ClassLoader classLoader;
    protected Locale locale;
    protected Locale usedLocale;
    protected ResourceBundle resourceBundle;

    public LocalizableSupport(String s) {
        this(s, null);
    }

    public LocalizableSupport(String s, ClassLoader cl) {
        this.bundleName = s;
        this.classLoader = cl;
    }

    @Override
    public void setLocale(Locale l) {
        if (this.locale != l) {
            this.locale = l;
            this.resourceBundle = null;
        }
    }

    @Override
    public Locale getLocale() {
        return this.locale;
    }

    public void setLocaleGroup(LocaleGroup lg) {
        this.localeGroup = lg;
    }

    public LocaleGroup getLocaleGroup() {
        return this.localeGroup;
    }

    public void setDefaultLocale(Locale l) {
        this.localeGroup.setLocale(l);
    }

    public Locale getDefaultLocale() {
        return this.localeGroup.getLocale();
    }

    @Override
    public String formatMessage(String key, Object[] args) {
        this.getResourceBundle();
        return MessageFormat.format(this.resourceBundle.getString(key), args);
    }

    public ResourceBundle getResourceBundle() {
        if (this.resourceBundle == null) {
            Locale l;
            this.usedLocale = this.locale == null ? ((l = this.localeGroup.getLocale()) == null ? Locale.getDefault() : l) : this.locale;
            this.resourceBundle = this.classLoader == null ? ResourceBundle.getBundle(this.bundleName, this.usedLocale) : ResourceBundle.getBundle(this.bundleName, this.usedLocale, this.classLoader);
        } else if (this.locale == null) {
            Locale l = this.localeGroup.getLocale();
            if (l == null) {
                l = Locale.getDefault();
                if (this.usedLocale != l) {
                    this.usedLocale = l;
                    this.resourceBundle = this.classLoader == null ? ResourceBundle.getBundle(this.bundleName, this.usedLocale) : ResourceBundle.getBundle(this.bundleName, this.usedLocale, this.classLoader);
                }
            } else if (this.usedLocale != l) {
                this.usedLocale = l;
                this.resourceBundle = this.classLoader == null ? ResourceBundle.getBundle(this.bundleName, this.usedLocale) : ResourceBundle.getBundle(this.bundleName, this.usedLocale, this.classLoader);
            }
        }
        return this.resourceBundle;
    }
}

