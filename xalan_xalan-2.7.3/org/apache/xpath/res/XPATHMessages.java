/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.res;

import java.text.MessageFormat;
import java.util.ListResourceBundle;
import org.apache.xml.res.XMLMessages;

public class XPATHMessages
extends XMLMessages {
    private static ListResourceBundle XPATHBundle = null;
    private static final String XPATH_ERROR_RESOURCES = "org.apache.xpath.res.XPATHErrorResources";

    public static final String createXPATHMessage(String msgKey, Object[] args) {
        if (XPATHBundle == null) {
            XPATHBundle = XPATHMessages.loadResourceBundle(XPATH_ERROR_RESOURCES);
        }
        if (XPATHBundle != null) {
            return XPATHMessages.createXPATHMsg(XPATHBundle, msgKey, args);
        }
        return "Could not load any resource bundles.";
    }

    public static final String createXPATHWarning(String msgKey, Object[] args) {
        if (XPATHBundle == null) {
            XPATHBundle = XPATHMessages.loadResourceBundle(XPATH_ERROR_RESOURCES);
        }
        if (XPATHBundle != null) {
            return XPATHMessages.createXPATHMsg(XPATHBundle, msgKey, args);
        }
        return "Could not load any resource bundles.";
    }

    public static final String createXPATHMsg(ListResourceBundle fResourceBundle, String msgKey, Object[] args) {
        String fmsg = null;
        boolean throwex = false;
        String msg = null;
        if (msgKey != null) {
            msg = fResourceBundle.getString(msgKey);
        }
        if (msg == null) {
            msg = fResourceBundle.getString("BAD_CODE");
            throwex = true;
        }
        if (args != null) {
            try {
                int n = args.length;
                for (int i = 0; i < n; ++i) {
                    if (null != args[i]) continue;
                    args[i] = "";
                }
                fmsg = MessageFormat.format(msg, args);
            }
            catch (Exception e) {
                fmsg = fResourceBundle.getString("FORMAT_FAILED");
                fmsg = fmsg + " " + msg;
            }
        } else {
            fmsg = msg;
        }
        if (throwex) {
            throw new RuntimeException(fmsg);
        }
        return fmsg;
    }
}

