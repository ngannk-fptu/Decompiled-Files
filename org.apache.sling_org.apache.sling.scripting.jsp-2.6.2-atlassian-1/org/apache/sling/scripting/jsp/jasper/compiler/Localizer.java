/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.compiler;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Localizer {
    private static ResourceBundle bundle = null;

    public static String getMessage(String errCode) {
        String errMsg = errCode;
        try {
            errMsg = bundle.getString(errCode);
        }
        catch (MissingResourceException missingResourceException) {
            // empty catch block
        }
        return errMsg;
    }

    public static String getMessage(String errCode, String arg) {
        return Localizer.getMessage(errCode, new Object[]{arg});
    }

    public static String getMessage(String errCode, String arg1, String arg2) {
        return Localizer.getMessage(errCode, new Object[]{arg1, arg2});
    }

    public static String getMessage(String errCode, String arg1, String arg2, String arg3) {
        return Localizer.getMessage(errCode, new Object[]{arg1, arg2, arg3});
    }

    public static String getMessage(String errCode, String arg1, String arg2, String arg3, String arg4) {
        return Localizer.getMessage(errCode, new Object[]{arg1, arg2, arg3, arg4});
    }

    public static String getMessage(String errCode, Object[] args) {
        String errMsg = errCode;
        try {
            errMsg = bundle.getString(errCode);
            if (args != null) {
                MessageFormat formatter = new MessageFormat(errMsg);
                errMsg = formatter.format(args);
            }
        }
        catch (MissingResourceException missingResourceException) {
            // empty catch block
        }
        return errMsg;
    }

    static {
        try {
            bundle = ResourceBundle.getBundle("org.apache.sling.scripting.jsp.jasper.resources.LocalStrings");
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }
}

