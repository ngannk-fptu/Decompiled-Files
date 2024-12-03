/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.service.importer.support;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

public enum CollectionType {
    LIST(List.class),
    SET(Set.class),
    SORTED_LIST(List.class),
    SORTED_SET(SortedSet.class);

    private final Class<?> collectionClass;

    public Class<?> getCollectionClass() {
        return this.collectionClass;
    }

    private CollectionType(Class<?> collectionClass) {
        this.collectionClass = collectionClass;
    }
}

