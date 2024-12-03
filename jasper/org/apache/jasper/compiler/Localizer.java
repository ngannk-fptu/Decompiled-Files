/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jasper.compiler;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.jasper.runtime.ExceptionUtils;

public class Localizer {
    private static ResourceBundle bundle;

    public static String getMessage(String errCode) {
        String errMsg = errCode;
        try {
            if (bundle != null) {
                errMsg = bundle.getString(errCode);
            }
        }
        catch (MissingResourceException missingResourceException) {
            // empty catch block
        }
        return errMsg;
    }

    public static String getMessage(String errCode, Object ... args) {
        String errMsg = Localizer.getMessage(errCode);
        if (args != null && args.length > 0) {
            MessageFormat formatter = new MessageFormat(errMsg);
            errMsg = formatter.format(args);
        }
        return errMsg;
    }

    static {
        try {
            bundle = ResourceBundle.getBundle("org.apache.jasper.resources.LocalStrings");
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
        }
    }
}

