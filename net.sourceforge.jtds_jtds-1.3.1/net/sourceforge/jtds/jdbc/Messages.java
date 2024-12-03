/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class Messages {
    private static final String DEFAULT_RESOURCE = "net.sourceforge.jtds.jdbc.Messages";
    private static ResourceBundle defaultResource;

    private Messages() {
    }

    public static String get(String key) {
        return Messages.get(key, null);
    }

    public static String get(String key, Object param1) {
        Object[] args = new Object[]{param1};
        return Messages.get(key, args);
    }

    static String get(String key, Object param1, Object param2) {
        Object[] args = new Object[]{param1, param2};
        return Messages.get(key, args);
    }

    private static String get(String key, Object[] arguments) {
        try {
            ResourceBundle bundle = Messages.loadResourceBundle();
            String formatString = bundle.getString(key);
            if (arguments == null || arguments.length == 0) {
                return formatString;
            }
            MessageFormat formatter = new MessageFormat(formatString);
            return formatter.format(arguments);
        }
        catch (MissingResourceException mre) {
            throw new RuntimeException("No message resource found for message property " + key);
        }
    }

    static void loadDriverProperties(Map propertyMap, Map descriptionMap) {
        ResourceBundle bundle = Messages.loadResourceBundle();
        Enumeration<String> keys = bundle.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            String descriptionPrefix = "prop.desc.";
            String propertyPrefix = "prop.";
            if (key.startsWith("prop.desc.")) {
                descriptionMap.put(key.substring("prop.desc.".length()), bundle.getString(key));
                continue;
            }
            if (!key.startsWith("prop.")) continue;
            propertyMap.put(key.substring("prop.".length()), bundle.getString(key));
        }
    }

    private static ResourceBundle loadResourceBundle() {
        if (defaultResource == null) {
            defaultResource = ResourceBundle.getBundle(DEFAULT_RESOURCE);
        }
        return defaultResource;
    }
}

