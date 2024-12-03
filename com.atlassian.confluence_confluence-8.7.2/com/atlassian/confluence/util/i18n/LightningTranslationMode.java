/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.util.i18n;

import com.atlassian.confluence.util.i18n.TranslationMode;
import com.google.common.collect.ImmutableList;
import java.util.Collection;

public class LightningTranslationMode
implements TranslationMode {
    private static final long serialVersionUID = 1L;
    private static final String START = "\ufeff";
    private static final String MIDDLE = "\u26a1";
    private static final String END = "\u2060";

    @Override
    public Collection<String> getParams() {
        return ImmutableList.of((Object)"on");
    }

    @Override
    public String getMarkedUpText(String key, String value, String raw) {
        StringBuilder text = new StringBuilder(START + value);
        text.append(MIDDLE).append(key);
        if (!value.equals(raw)) {
            text.append(MIDDLE).append(raw);
        }
        text.append(END);
        return text.toString();
    }
}

