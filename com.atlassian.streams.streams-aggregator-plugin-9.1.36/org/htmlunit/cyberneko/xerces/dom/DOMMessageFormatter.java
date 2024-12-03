/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.dom;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class DOMMessageFormatter {
    public static final String DOM_DOMAIN = "http://www.w3.org/dom/DOMTR";
    public static final String XML_DOMAIN = "http://www.w3.org/TR/1998/REC-xml-19980210";
    private static ResourceBundle domResourceBundle = null;
    private static ResourceBundle xmlResourceBundle = null;

    public static String formatMessage(String domain, String key, Object[] arguments) throws MissingResourceException {
        String msg;
        ResourceBundle resourceBundle = DOMMessageFormatter.getResourceBundle(domain);
        if (resourceBundle == null) {
            DOMMessageFormatter.init();
            resourceBundle = DOMMessageFormatter.getResourceBundle(domain);
            if (resourceBundle == null) {
                throw new MissingResourceException("Unknown domain" + domain, null, key);
            }
        }
        try {
            msg = key + ": " + resourceBundle.getString(key);
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

    static ResourceBundle getResourceBundle(String domain) {
        if (domain == DOM_DOMAIN || domain.equals(DOM_DOMAIN)) {
            return domResourceBundle;
        }
        if (domain == XML_DOMAIN || domain.equals(XML_DOMAIN)) {
            return xmlResourceBundle;
        }
        return null;
    }

    public static void init() {
        domResourceBundle = ResourceBundle.getBundle("org.htmlunit.cyberneko.xerces.impl.msg.DOMMessages");
        xmlResourceBundle = ResourceBundle.getBundle("org.htmlunit.cyberneko.xerces.impl.msg.XMLMessages");
    }
}

