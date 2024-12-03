/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.util;

import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class StringManager {
    private static int LOCALE_CACHE_SIZE = 10;
    private final ResourceBundle bundle;
    private final Locale locale;
    private static final Map<String, Map<Locale, StringManager>> managers = new HashMap<String, Map<Locale, StringManager>>();

    private StringManager(String packageName, Locale locale) {
        Locale bundleLocale;
        ResourceBundle bnd;
        block4: {
            String bundleName = packageName + ".LocalStrings";
            bnd = null;
            try {
                bnd = ResourceBundle.getBundle(bundleName, locale);
            }
            catch (MissingResourceException ex) {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                if (cl == null) break block4;
                try {
                    bnd = ResourceBundle.getBundle(bundleName, locale, cl);
                }
                catch (MissingResourceException missingResourceException) {
                    // empty catch block
                }
            }
        }
        this.bundle = bnd;
        this.locale = this.bundle != null ? ((bundleLocale = this.bundle.getLocale()).equals(Locale.ROOT) ? Locale.ENGLISH : bundleLocale) : null;
    }

    public String getString(String key) {
        if (key == null) {
            String msg = "key may not have a null value";
            throw new IllegalArgumentException(msg);
        }
        String str = null;
        try {
            if (this.bundle != null) {
                str = this.bundle.getString(key);
            }
        }
        catch (MissingResourceException mre) {
            str = null;
        }
        return str;
    }

    public String getString(String key, Object ... args) {
        String value = this.getString(key);
        if (value == null) {
            value = key;
        }
        MessageFormat mf = new MessageFormat(value);
        mf.setLocale(this.locale);
        return mf.format(args, new StringBuffer(), (FieldPosition)null).toString();
    }

    public Locale getLocale() {
        return this.locale;
    }

    public static final StringManager getManager(Class<?> clazz) {
        return StringManager.getManager(clazz.getPackage().getName());
    }

    public static final StringManager getManager(String packageName) {
        return StringManager.getManager(packageName, Locale.getDefault());
    }

    public static final synchronized StringManager getManager(String packageName, Locale locale) {
        StringManager mgr;
        LinkedHashMap<Locale, StringManager> map = managers.get(packageName);
        if (map == null) {
            map = new LinkedHashMap<Locale, StringManager>(LOCALE_CACHE_SIZE, 0.75f, true){
                private static final long serialVersionUID = 1L;

                @Override
                protected boolean removeEldestEntry(Map.Entry<Locale, StringManager> eldest) {
                    return this.size() > LOCALE_CACHE_SIZE - 1;
                }
            };
            managers.put(packageName, (Map<Locale, StringManager>)map);
        }
        if ((mgr = map.get(locale)) == null) {
            mgr = new StringManager(packageName, locale);
            map.put(locale, mgr);
        }
        return mgr;
    }

    public static StringManager getManager(String packageName, Enumeration<Locale> requestedLocales) {
        while (requestedLocales.hasMoreElements()) {
            Locale locale = requestedLocales.nextElement();
            StringManager result = StringManager.getManager(packageName, locale);
            if (!result.getLocale().equals(locale)) continue;
            return result;
        }
        return StringManager.getManager(packageName);
    }
}

