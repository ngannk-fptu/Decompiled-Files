/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.search;

import com.atlassian.confluence.search.SearchLanguage;

@FunctionalInterface
public interface SearchLanguageProvider {
    public SearchLanguage get();
}

