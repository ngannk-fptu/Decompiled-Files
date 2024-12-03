/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.reference.Collapsed
 *  com.atlassian.confluence.api.nav.Navigation$Builder
 *  com.atlassian.confluence.api.nav.NavigationService
 *  com.atlassian.confluence.rest.api.model.RestEntity
 *  com.atlassian.confluence.rest.api.services.RestNavigationService
 *  com.atlassian.confluence.rest.serialization.enrich.SchemaType
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.plugins.restapi.enrich;

import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.reference.Collapsed;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.api.nav.NavigationService;
import com.atlassian.confluence.plugins.restapi.enrich.EntityEnricher;
import com.atlassian.confluence.rest.api.model.RestEntity;
import com.atlassian.confluence.rest.api.services.RestNavigationService;
import com.atlassian.confluence.rest.serialization.enrich.SchemaType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;

class ExpandableEntityEnricher
implements EntityEnricher {
    private final RestNavigationService navigationService;

    public ExpandableEntityEnricher(RestNavigationService navigationService) {
        this.navigationService = navigationService;
    }

    @Override
    public boolean isRecursive() {
        return true;
    }

    @Override
    public @NonNull Map<String, Type> getEnrichedPropertyTypes(@NonNull Type type) {
        return Collections.emptyMap();
    }

    @Override
    public void enrich(@NonNull RestEntity entity, @NonNull SchemaType schemaType) {
        if (schemaType != SchemaType.REST) {
            return;
        }
        Map expandables = (Map)entity.removeProperty("_expandable");
        if (expandables != null) {
            HashMap<String, String> enrichedExpandables = new HashMap<String, String>();
            for (Map.Entry expandable : expandables.entrySet()) {
                enrichedExpandables.put(Expansions.encode((String)((String)expandable.getKey())), this.getUrl((Collapsed)expandable.getValue()));
            }
            entity.putProperty("_expandable", enrichedExpandables);
        }
    }

    private String getUrl(Collapsed reference) {
        Navigation.Builder builder = reference.resolveNavigation((NavigationService)this.navigationService);
        if (builder == null) {
            return "";
        }
        return builder.buildRelative();
    }
}

