/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.messages;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class NLS {
    private static Map<String, Class<? extends NLS>> bundles = new HashMap<String, Class<? extends NLS>>(0);

    protected NLS() {
    }

    public static String getLocalizedMessage(String key) {
        return NLS.getLocalizedMessage(key, Locale.getDefault());
    }

    public static String getLocalizedMessage(String key, Locale locale) {
        Object message = NLS.getResourceBundleObject(key, locale);
        if (message == null) {
            return "Message with key:" + key + " and locale: " + locale + " not found.";
        }
        return message.toString();
    }

    public static String getLocalizedMessage(String key, Locale locale, Object ... args) {
        String str = NLS.getLocalizedMessage(key, locale);
        if (args.length > 0) {
            MessageFormat formatter = new MessageFormat(str, Locale.getDefault());
            str = formatter.format(Arrays.stream(args).toArray(), new StringBuffer(), new FieldPosition(0)).toString();
        }
        return str;
    }

    public static String getLocalizedMessage(String key, Object ... args) {
        return NLS.getLocalizedMessage(key, Locale.getDefault(), args);
    }

    protected static void initializeMessages(String bundleName, Class<? extends NLS> clazz) {
        try {
            NLS.load(clazz);
            if (!bundles.containsKey(bundleName)) {
                bundles.put(bundleName, clazz);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static Object getResourceBundleObject(String messageKey, Locale locale) {
        Iterator<String> it = bundles.keySet().iterator();
        while (it.hasNext()) {
            Class<? extends NLS> clazz = bundles.get(it.next());
            ResourceBundle resourceBundle = ResourceBundle.getBundle(clazz.getName(), locale);
            if (resourceBundle == null) continue;
            try {
                Object obj = resourceBundle.getObject(messageKey);
                if (obj == null) continue;
                return obj;
            }
            catch (MissingResourceException missingResourceException) {
            }
        }
        return null;
    }

    private static void load(Class<? extends NLS> clazz) {
        Field[] fieldArray = clazz.getDeclaredFields();
        boolean isFieldAccessible = (clazz.getModifiers() & 1) != 0;
        int len = fieldArray.length;
        HashMap<String, Field> fields = new HashMap<String, Field>(len * 2);
        for (int i = 0; i < len; ++i) {
            fields.put(fieldArray[i].getName(), fieldArray[i]);
            NLS.loadfieldValue(fieldArray[i], isFieldAccessible, clazz);
        }
    }

    private static void loadfieldValue(Field field, boolean isFieldAccessible, Class<? extends NLS> clazz) {
        int MOD_EXPECTED = 9;
        int MOD_MASK = MOD_EXPECTED | 0x10;
        if ((field.getModifiers() & MOD_MASK) != MOD_EXPECTED) {
            return;
        }
        if (!isFieldAccessible) {
            NLS.makeAccessible(field);
        }
        try {
            field.set(null, field.getName());
            NLS.validateMessage(field.getName(), clazz);
        }
        catch (IllegalArgumentException illegalArgumentException) {
        }
        catch (IllegalAccessException illegalAccessException) {
            // empty catch block
        }
    }

    private static void validateMessage(String key, Class<? extends NLS> clazz) {
        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle(clazz.getName(), Locale.getDefault());
            if (resourceBundle != null) {
                Object object = resourceBundle.getObject(key);
            }
        }
        catch (MissingResourceException missingResourceException) {
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void makeAccessible(final Field field) {
        if (System.getSecurityManager() == null) {
            field.setAccessible(true);
        } else {
            AccessController.doPrivileged(new PrivilegedAction<Void>(){

                @Override
                public Void run() {
                    field.setAccessible(true);
                    return null;
                }
            });
        }
    }
}

