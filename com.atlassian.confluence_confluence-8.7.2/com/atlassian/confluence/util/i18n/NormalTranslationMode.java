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

public class NormalTranslationMode
implements TranslationMode {
    private static final long serialVersionUID = 1L;

    @Override
    public Collection<String> getParams() {
        return ImmutableList.of((Object)"off");
    }

    @Override
    public String getMarkedUpText(String key, String value, String raw) {
        return value;
    }
}

