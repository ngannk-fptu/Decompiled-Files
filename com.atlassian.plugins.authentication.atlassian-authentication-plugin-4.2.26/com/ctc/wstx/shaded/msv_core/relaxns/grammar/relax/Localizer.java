/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.relaxns.grammar.relax;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class Localizer {
    public static final String WRN_ANYOTHER_NAMESPACE_IGNORED = "AnyOtherElementExp.Warning.AnyOtherNamespaceIgnored";

    public static String localize(String propertyName, Object[] args) {
        String format = ResourceBundle.getBundle("com.ctc.wstx.shaded.msv_core.relaxns.grammar.relax.Messages").getString(propertyName);
        return MessageFormat.format(format, args);
    }

    public static String localize(String prop) {
        return Localizer.localize(prop, null);
    }

    public static String localize(String prop, Object arg1) {
        return Localizer.localize(prop, new Object[]{arg1});
    }

    public static String localize(String prop, Object arg1, Object arg2) {
        return Localizer.localize(prop, new Object[]{arg1, arg2});
    }
}

