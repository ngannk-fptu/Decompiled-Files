/*
 * Decompiled with CFR 0.152.
 */
package org.apache.naming;

import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class StringManager {
    private final ResourceBundle bundle;
    private final Locale locale;
    private static final Map<String, StringManager> managers = new HashMap<String, StringManager>();

    private StringManager(String packageName) {
        ResourceBundle tempBundle;
        block4: {
            String bundleName = packageName + ".LocalStrings";
            tempBundle = null;
            try {
                tempBundle = ResourceBundle.getBundle(bundleName, Locale.getDefault());
            }
            catch (MissingResourceException ex) {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                if (cl == null) break block4;
                try {
                    tempBundle = ResourceBundle.getBundle(bundleName, Locale.getDefault(), cl);
                }
                catch (MissingResourceException missingResourceException) {
                    // empty catch block
                }
            }
        }
        this.locale = tempBundle != null ? tempBundle.getLocale() : null;
        this.bundle = tempBundle;
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

    public static final synchronized StringManager getManager(String packageName) {
        StringManager mgr = managers.get(packageName);
        if (mgr == null) {
            mgr = new StringManager(packageName);
            managers.put(packageName, mgr);
        }
        return mgr;
    }

    public static final StringManager getManager(Class<?> clazz) {
        return StringManager.getManager(clazz.getPackage().getName());
    }
}

