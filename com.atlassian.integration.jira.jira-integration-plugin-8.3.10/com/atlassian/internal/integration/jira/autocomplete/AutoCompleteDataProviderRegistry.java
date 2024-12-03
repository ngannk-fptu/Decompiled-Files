/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.internal.integration.jira.autocomplete;

import com.atlassian.internal.integration.jira.autocomplete.AutoCompleteDataProvider;

public interface AutoCompleteDataProviderRegistry {
    public AutoCompleteDataProvider getProvider(String var1);
}

