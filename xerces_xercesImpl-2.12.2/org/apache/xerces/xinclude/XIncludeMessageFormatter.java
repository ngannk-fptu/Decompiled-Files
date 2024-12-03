/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xinclude;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.xerces.util.MessageFormatter;

public class XIncludeMessageFormatter
implements MessageFormatter {
    public static final String XINCLUDE_DOMAIN = "http://www.w3.org/TR/xinclude";
    private Locale fLocale = null;
    private ResourceBundle fResourceBundle = null;

    @Override
    public String formatMessage(Locale locale, String string, Object[] objectArray) throws MissingResourceException {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        if (locale != this.fLocale) {
            this.fResourceBundle = ResourceBundle.getBundle("org.apache.xerces.impl.msg.XIncludeMessages", locale);
            this.fLocale = locale;
        }
        String string2 = this.fResourceBundle.getString(string);
        if (objectArray != null) {
            try {
                string2 = MessageFormat.format(string2, objectArray);
            }
            catch (Exception exception) {
                string2 = this.fResourceBundle.getString("FormatFailed");
                string2 = string2 + " " + this.fResourceBundle.getString(string);
            }
        }
        if (string2 == null) {
            string2 = this.fResourceBundle.getString("BadMessageKey");
            throw new MissingResourceException(string2, "org.apache.xerces.impl.msg.XIncludeMessages", string);
        }
        return string2;
    }
}

