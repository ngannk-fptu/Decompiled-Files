/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.xerces.util.MessageFormatter;

public class XSMessageFormatter
implements MessageFormatter {
    public static final String SCHEMA_DOMAIN = "http://www.w3.org/TR/xml-schema-1";
    private Locale fLocale = null;
    private ResourceBundle fResourceBundle = null;

    @Override
    public String formatMessage(Locale locale, String string, Object[] objectArray) throws MissingResourceException {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        if (locale != this.fLocale) {
            this.fResourceBundle = ResourceBundle.getBundle("org.apache.xerces.impl.msg.XMLSchemaMessages", locale);
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
            throw new MissingResourceException(string2, "org.apache.xerces.impl.msg.SchemaMessages", string);
        }
        return string2;
    }
}

