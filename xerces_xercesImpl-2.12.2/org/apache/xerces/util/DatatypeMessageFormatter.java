/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class DatatypeMessageFormatter {
    private static final String BASE_NAME = "org.apache.xerces.impl.msg.DatatypeMessages";

    public static String formatMessage(Locale locale, String string, Object[] objectArray) throws MissingResourceException {
        String string2;
        if (locale == null) {
            locale = Locale.getDefault();
        }
        ResourceBundle resourceBundle = ResourceBundle.getBundle(BASE_NAME, locale);
        try {
            string2 = resourceBundle.getString(string);
            if (objectArray != null) {
                try {
                    string2 = MessageFormat.format(string2, objectArray);
                }
                catch (Exception exception) {
                    string2 = resourceBundle.getString("FormatFailed");
                    string2 = string2 + " " + resourceBundle.getString(string);
                }
            }
        }
        catch (MissingResourceException missingResourceException) {
            String string3 = resourceBundle.getString("BadMessageKey");
            throw new MissingResourceException(string, string3, string);
        }
        if (string2 == null) {
            string2 = string;
            if (objectArray.length > 0) {
                StringBuffer stringBuffer = new StringBuffer(string2);
                stringBuffer.append('?');
                for (int i = 0; i < objectArray.length; ++i) {
                    if (i > 0) {
                        stringBuffer.append('&');
                    }
                    stringBuffer.append(String.valueOf(objectArray[i]));
                }
            }
        }
        return string2;
    }
}

