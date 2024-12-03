/*
 * Decompiled with CFR 0.152.
 */
package org.apache.el.util;

import java.text.FieldPosition;
import java.text.Format;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class MessageFactory {
    private static final ResourceBundle DEFAULT_BUNDLE = ResourceBundle.getBundle("org.apache.el.Messages");
    private static final MessageFactory DEFAULT_MESSAGE_FACTORY = new MessageFactory(DEFAULT_BUNDLE);
    private final ResourceBundle bundle;

    public static String get(String key) {
        return DEFAULT_MESSAGE_FACTORY.getInternal(key);
    }

    public static String get(String key, Object ... args) {
        return DEFAULT_MESSAGE_FACTORY.getInternal(key, args);
    }

    public MessageFactory(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    protected String getInternal(String key) {
        try {
            return this.bundle.getString(key);
        }
        catch (MissingResourceException e) {
            return key;
        }
    }

    protected String getInternal(String key, Object ... args) {
        String value = this.getInternal(key);
        MessageFormat mf = new MessageFormat(value);
        Format[] formats = null;
        if (args != null) {
            for (int i = 0; i < args.length; ++i) {
                if (!(args[i] instanceof Number)) continue;
                if (formats == null) {
                    formats = mf.getFormatsByArgumentIndex();
                }
                if (i >= formats.length || formats[i] instanceof NumberFormat) continue;
                args[i] = args[i].toString();
            }
        }
        return mf.format(args, new StringBuffer(), (FieldPosition)null).toString();
    }
}

