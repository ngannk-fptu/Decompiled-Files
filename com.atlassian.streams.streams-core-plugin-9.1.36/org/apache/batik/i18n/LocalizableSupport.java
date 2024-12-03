/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.i18n;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.batik.i18n.LocaleGroup;
import org.apache.batik.i18n.Localizable;

public class LocalizableSupport
implements Localizable {
    protected LocaleGroup localeGroup = LocaleGroup.DEFAULT;
    protected String bundleName;
    protected ClassLoader classLoader;
    protected Locale locale;
    protected Locale usedLocale;
    List resourceBundles = new ArrayList();
    Class lastResourceClass;
    Class cls;

    public LocalizableSupport(String s, Class cls) {
        this(s, cls, null);
    }

    public LocalizableSupport(String s, Class cls, ClassLoader cl) {
        this.bundleName = s;
        this.cls = cls;
        this.classLoader = cl;
    }

    public LocalizableSupport(String s) {
        this(s, (ClassLoader)null);
    }

    public LocalizableSupport(String s, ClassLoader cl) {
        this.bundleName = s;
        this.classLoader = cl;
    }

    @Override
    public void setLocale(Locale l) {
        if (this.locale != l) {
            this.locale = l;
            this.resourceBundles.clear();
            this.lastResourceClass = null;
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
        return MessageFormat.format(this.getString(key), args);
    }

    protected Locale getCurrentLocale() {
        if (this.locale != null) {
            return this.locale;
        }
        Locale l = this.localeGroup.getLocale();
        if (l != null) {
            return l;
        }
        return Locale.getDefault();
    }

    protected boolean setUsedLocale() {
        Locale l = this.getCurrentLocale();
        if (this.usedLocale == l) {
            return false;
        }
        this.usedLocale = l;
        this.resourceBundles.clear();
        this.lastResourceClass = null;
        return true;
    }

    public ResourceBundle getResourceBundle() {
        return this.getResourceBundle(0);
    }

    protected boolean hasNextResourceBundle(int i) {
        if (i == 0) {
            return true;
        }
        if (i < this.resourceBundles.size()) {
            return true;
        }
        if (this.lastResourceClass == null) {
            return false;
        }
        return this.lastResourceClass != Object.class;
    }

    protected ResourceBundle lookupResourceBundle(String bundle, Class theClass) {
        ClassLoader cl = this.classLoader;
        ResourceBundle rb = null;
        if (cl != null) {
            try {
                rb = ResourceBundle.getBundle(bundle, this.usedLocale, cl);
            }
            catch (MissingResourceException missingResourceException) {
                // empty catch block
            }
            if (rb != null) {
                return rb;
            }
        }
        if (theClass != null) {
            try {
                cl = theClass.getClassLoader();
            }
            catch (SecurityException securityException) {
                // empty catch block
            }
        }
        if (cl == null) {
            cl = this.getClass().getClassLoader();
        }
        try {
            rb = ResourceBundle.getBundle(bundle, this.usedLocale, cl);
        }
        catch (MissingResourceException missingResourceException) {
            // empty catch block
        }
        return rb;
    }

    protected ResourceBundle getResourceBundle(int i) {
        this.setUsedLocale();
        ResourceBundle rb = null;
        if (this.cls == null) {
            if (this.resourceBundles.size() == 0) {
                rb = this.lookupResourceBundle(this.bundleName, null);
                this.resourceBundles.add(rb);
            }
            return (ResourceBundle)this.resourceBundles.get(0);
        }
        while (i >= this.resourceBundles.size()) {
            if (this.lastResourceClass == Object.class) {
                return null;
            }
            this.lastResourceClass = this.lastResourceClass == null ? this.cls : this.lastResourceClass.getSuperclass();
            Class cl = this.lastResourceClass;
            String bundle = cl.getPackage().getName() + "." + this.bundleName;
            this.resourceBundles.add(this.lookupResourceBundle(bundle, cl));
        }
        return (ResourceBundle)this.resourceBundles.get(i);
    }

    public String getString(String key) throws MissingResourceException {
        this.setUsedLocale();
        int i = 0;
        while (this.hasNextResourceBundle(i)) {
            ResourceBundle rb = this.getResourceBundle(i);
            if (rb != null) {
                try {
                    String ret = rb.getString(key);
                    if (ret != null) {
                        return ret;
                    }
                }
                catch (MissingResourceException missingResourceException) {
                    // empty catch block
                }
            }
            ++i;
        }
        String classStr = this.cls != null ? this.cls.toString() : this.bundleName;
        throw new MissingResourceException("Unable to find resource: " + key, classStr, key);
    }

    public int getInteger(String key) throws MissingResourceException {
        String i = this.getString(key);
        try {
            return Integer.parseInt(i);
        }
        catch (NumberFormatException e) {
            throw new MissingResourceException("Malformed integer", this.bundleName, key);
        }
    }

    public int getCharacter(String key) throws MissingResourceException {
        String s = this.getString(key);
        if (s == null || s.length() == 0) {
            throw new MissingResourceException("Malformed character", this.bundleName, key);
        }
        return s.charAt(0);
    }
}

