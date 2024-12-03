/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.msg;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.xerces.util.MessageFormatter;

public class XMLMessageFormatter
implements MessageFormatter {
    public static final String XML_DOMAIN = "http://www.w3.org/TR/1998/REC-xml-19980210";
    public static final String XMLNS_DOMAIN = "http://www.w3.org/TR/1999/REC-xml-names-19990114";
    private Locale fLocale = null;
    private ResourceBundle fResourceBundle = null;

    @Override
    public String formatMessage(Locale locale, String string, Object[] objectArray) throws MissingResourceException {
        String string2;
        if (locale == null) {
            locale = Locale.getDefault();
        }
        if (locale != this.fLocale) {
            this.fResourceBundle = ResourceBundle.getBundle("org.apache.xerces.impl.msg.XMLMessages", locale);
            this.fLocale = locale;
        }
        try {
            string2 = this.fResourceBundle.getString(string);
            if (objectArray != null) {
                try {
                    string2 = MessageFormat.format(string2, objectArray);
                }
                catch (Exception exception) {
                    string2 = this.fResourceBundle.getString("FormatFailed");
                    string2 = string2 + " " + this.fResourceBundle.getString(string);
                }
            }
        }
        catch (MissingResourceException missingResourceException) {
            String string3 = this.fResourceBundle.getString("BadMessageKey");
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

