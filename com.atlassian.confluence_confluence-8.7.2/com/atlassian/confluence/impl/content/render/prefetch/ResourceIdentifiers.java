/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.content.render.prefetch;

import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ResourceIdentifiers {
    private final Map<Class<? extends ResourceIdentifier>, Set<ResourceIdentifier>> resourceIdentifiers;

    public ResourceIdentifiers(Map<Class<? extends ResourceIdentifier>, Set<ResourceIdentifier>> resourceIdentifiers) {
        this.resourceIdentifiers = resourceIdentifiers;
    }

    public <T extends ResourceIdentifier> Set<T> getResourceIdentifiers(Class<T> identifierType) {
        return this.resourceIdentifiers.getOrDefault(identifierType, Collections.emptySet()).stream().map(ri -> (ResourceIdentifier)identifierType.cast(ri)).collect(Collectors.toSet());
    }
}

