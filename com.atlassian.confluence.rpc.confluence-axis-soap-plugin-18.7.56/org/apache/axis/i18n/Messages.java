/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.i18n;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.axis.i18n.MessageBundle;
import org.apache.axis.i18n.MessagesConstants;

public class Messages {
    private static final Class thisClass = class$org$apache$axis$i18n$Messages == null ? (class$org$apache$axis$i18n$Messages = Messages.class$("org.apache.axis.i18n.Messages")) : class$org$apache$axis$i18n$Messages;
    private static final String projectName = MessagesConstants.projectName;
    private static final String resourceName = MessagesConstants.resourceName;
    private static final Locale locale = MessagesConstants.locale;
    private static final String packageName = Messages.getPackage(thisClass.getName());
    private static final ClassLoader classLoader = thisClass.getClassLoader();
    private static final ResourceBundle parent = MessagesConstants.rootPackageName == packageName ? null : MessagesConstants.rootBundle;
    private static final MessageBundle messageBundle = new MessageBundle(projectName, packageName, resourceName, locale, classLoader, parent);
    static /* synthetic */ Class class$org$apache$axis$i18n$Messages;

    public static String getMessage(String key) throws MissingResourceException {
        return messageBundle.getMessage(key);
    }

    public static String getMessage(String key, String arg0) throws MissingResourceException {
        return messageBundle.getMessage(key, arg0);
    }

    public static String getMessage(String key, String arg0, String arg1) throws MissingResourceException {
        return messageBundle.getMessage(key, arg0, arg1);
    }

    public static String getMessage(String key, String arg0, String arg1, String arg2) throws MissingResourceException {
        return messageBundle.getMessage(key, arg0, arg1, arg2);
    }

    public static String getMessage(String key, String arg0, String arg1, String arg2, String arg3) throws MissingResourceException {
        return messageBundle.getMessage(key, arg0, arg1, arg2, arg3);
    }

    public static String getMessage(String key, String arg0, String arg1, String arg2, String arg3, String arg4) throws MissingResourceException {
        return messageBundle.getMessage(key, arg0, arg1, arg2, arg3, arg4);
    }

    public static String getMessage(String key, String[] args) throws MissingResourceException {
        return messageBundle.getMessage(key, args);
    }

    public static ResourceBundle getResourceBundle() {
        return messageBundle.getResourceBundle();
    }

    public static MessageBundle getMessageBundle() {
        return messageBundle;
    }

    private static final String getPackage(String name) {
        return name.substring(0, name.lastIndexOf(46)).intern();
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

