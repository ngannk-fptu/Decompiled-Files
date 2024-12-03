/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.dtd;

import java.text.MessageFormat;
import java.util.ResourceBundle;

class Localizer {
    Localizer() {
    }

    public static String localize(String prop, Object[] args) {
        return MessageFormat.format(ResourceBundle.getBundle("com.ctc.wstx.shaded.msv_core.reader.dtd.Messages").getString(prop), args);
    }

    public static String localize(String prop) {
        return Localizer.localize(prop, null);
    }

    public static String localize(String prop, Object arg1) {
        return Localizer.localize(prop, new Object[]{arg1});
    }
}

