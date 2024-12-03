/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.macro.code.formatter;

import com.atlassian.renderer.v2.macro.code.formatter.AbstractFormatter;

public class NoneFormatter
extends AbstractFormatter {
    private static final String[] SUPPORTED_LANGUAGES = new String[]{"none"};

    @Override
    public String[] getSupportedLanguages() {
        return SUPPORTED_LANGUAGES;
    }
}

