/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.util;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class SAXMessageFormatter {
    public static String formatMessage(String key, Object[] arguments) throws MissingResourceException {
        String msg;
        ResourceBundle resourceBundle = ResourceBundle.getBundle("org.htmlunit.cyberneko.xerces.impl.msg.SAXMessages");
        try {
            msg = resourceBundle.getString(key);
            if (arguments != null) {
                try {
                    msg = MessageFormat.format(msg, arguments);
                }
                catch (Exception e) {
                    msg = resourceBundle.getString("FormatFailed");
                    msg = msg + " " + resourceBundle.getString(key);
                }
            }
        }
        catch (MissingResourceException e) {
            String msg2 = resourceBundle.getString("BadMessageKey");
            throw new MissingResourceException(key, msg2, key);
        }
        if (msg == null) {
            msg = key;
            if (arguments.length > 0) {
                StringBuilder str = new StringBuilder(msg);
                str.append('?');
                for (int i = 0; i < arguments.length; ++i) {
                    if (i > 0) {
                        str.append('&');
                    }
                    str.append(arguments[i]);
                }
                msg = str.toString();
            }
        }
        return msg;
    }
}

