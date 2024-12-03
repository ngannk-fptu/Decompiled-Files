/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.api.model.reference;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.reference.Collapsed;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.api.nav.NavigationService;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

@Internal
class CollapsedMap<K, V>
extends AbstractMap<K, V>
implements Collapsed {
    private final Navigation.Builder navBuilder;

    CollapsedMap() {
        this.navBuilder = null;
    }

    CollapsedMap(Navigation.Builder navBuilder) {
        this.navBuilder = navBuilder;
    }

    @Override
    public Navigation.Builder resolveNavigation(NavigationService navigationService) {
        return this.navBuilder;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        throw Collapsed.Exceptions.throwCollapsedException("entrySet()");
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public String toString() {
        return "null (CollapsedMap)";
    }
}

