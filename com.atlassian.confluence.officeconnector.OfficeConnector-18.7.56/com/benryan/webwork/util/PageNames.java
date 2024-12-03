/*
 * Decompiled with CFR 0.152.
 */
package com.benryan.webwork.util;

import java.util.regex.Pattern;

public class PageNames {
    private static final Pattern START_PATTERN;
    private static final Pattern GENERAL_PATTERN;

    public static String fixPageTitle(String pageTitle) {
        String startFix = START_PATTERN.matcher(pageTitle).replaceAll("_");
        return GENERAL_PATTERN.matcher(startFix).replaceAll("_");
    }

    static {
        String startRegEx = "^(\\.\\.|[$~:@/\\\\|^#;\\[\\]{}<>])+";
        String bodyRegEx = "([\\cA-\\cZ:@/\\\\|^#;\\[\\]{}<>])+";
        START_PATTERN = Pattern.compile(startRegEx);
        GENERAL_PATTERN = Pattern.compile(bodyRegEx);
    }
}

