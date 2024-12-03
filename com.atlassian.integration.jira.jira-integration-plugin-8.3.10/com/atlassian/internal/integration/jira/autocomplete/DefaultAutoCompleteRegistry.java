/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.internal.integration.jira.autocomplete;

import com.atlassian.internal.integration.jira.autocomplete.AutoCompleteDataProvider;
import com.atlassian.internal.integration.jira.autocomplete.AutoCompleteDataProviderRegistry;
import java.util.Collections;
import java.util.Map;

public class DefaultAutoCompleteRegistry
implements AutoCompleteDataProviderRegistry {
    private final Map<String, AutoCompleteDataProvider> providers;

    public DefaultAutoCompleteRegistry(Map<String, AutoCompleteDataProvider> providers) {
        this.providers = Collections.unmodifiableMap(providers);
    }

    @Override
    public AutoCompleteDataProvider getProvider(String restType) {
        return this.providers.get(restType);
    }
}

