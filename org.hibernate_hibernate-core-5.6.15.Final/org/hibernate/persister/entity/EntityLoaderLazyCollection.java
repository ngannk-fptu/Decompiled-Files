/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.entity;

import java.util.function.Function;
import org.hibernate.LockMode;
import org.hibernate.internal.util.collections.LazyIndexedMap;
import org.hibernate.loader.entity.UniqueEntityLoader;

final class EntityLoaderLazyCollection
extends LazyIndexedMap<Object, UniqueEntityLoader> {
    private static final int MERGE_INDEX = LockMode.values().length;
    private static final int REFRESH_INDEX = MERGE_INDEX + 1;
    private static final int TOTAL_STORAGE_SIZE = REFRESH_INDEX + 1;

    public EntityLoaderLazyCollection() {
        super(TOTAL_STORAGE_SIZE);
    }

    UniqueEntityLoader getOrBuildByLockMode(LockMode lockMode, Function<LockMode, UniqueEntityLoader> builderFunction) {
        return super.computeIfAbsent(lockMode.ordinal(), lockMode, builderFunction);
    }

    UniqueEntityLoader getOrCreateByInternalFetchProfileMerge(Function<LockMode, UniqueEntityLoader> builderFunction) {
        return super.computeIfAbsent(MERGE_INDEX, null, builderFunction);
    }

    UniqueEntityLoader getOrCreateByInternalFetchProfileRefresh(Function<LockMode, UniqueEntityLoader> builderFunction) {
        return super.computeIfAbsent(REFRESH_INDEX, null, builderFunction);
    }
}

