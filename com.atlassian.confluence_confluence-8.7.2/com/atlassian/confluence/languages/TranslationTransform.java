/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.languages;

import java.util.Locale;

public interface TranslationTransform {
    public String apply(Locale var1, String var2, String var3);

    default public String getStateHash() {
        return null;
    }
}

