/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.StringUtils;

public enum Display {
    PAGE,
    POPUP,
    TOUCH,
    WAP;


    public static Display getDefault() {
        return PAGE;
    }

    public String toString() {
        return super.toString().toLowerCase();
    }

    public static Display parse(String s) throws ParseException {
        if (StringUtils.isBlank(s)) {
            return Display.getDefault();
        }
        if (s.equals("page")) {
            return PAGE;
        }
        if (s.equals("popup")) {
            return POPUP;
        }
        if (s.equals("touch")) {
            return TOUCH;
        }
        if (s.equals("wap")) {
            return WAP;
        }
        throw new ParseException("Unknown display type: " + s);
    }
}

