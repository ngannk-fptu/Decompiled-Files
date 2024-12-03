/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.api.model.reference;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.reference.ModelMapBuilder;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.api.nav.NavigationAware;
import com.atlassian.confluence.api.nav.NavigationService;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@RestEnrichable
@Internal
public class EnrichableMap<K, V>
extends AbstractMap<K, V>
implements NavigationAware {
    private final Map<K, V> delegate;
    private final Set<K> collapsedEntries;
    private final Navigation.Builder navBuilder;

    private EnrichableMap() {
        this.collapsedEntries = Collections.emptySet();
        this.delegate = Collections.emptyMap();
        this.navBuilder = null;
    }

    EnrichableMap(ModelMapBuilder<K, V> builder) {
        this(builder, null);
    }

    EnrichableMap(ModelMapBuilder<K, V> builder, Navigation.Builder navBuilder) {
        this.delegate = Collections.unmodifiableMap(builder.buildFromDelegate());
        this.collapsedEntries = builder.collapsedEntries.build();
        this.navBuilder = navBuilder;
    }

    @Override
    public Navigation.Builder resolveNavigation(NavigationService navigationService) {
        return this.navBuilder;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return this.delegate.entrySet();
    }

    public Set<K> getCollapsedEntries() {
        return this.collapsedEntries;
    }

    Navigation.Builder getNavigationBuilder() {
        return this.navBuilder;
    }
}

