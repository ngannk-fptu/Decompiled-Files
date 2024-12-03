/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.google.common.base.Function
 *  com.google.common.base.Functions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.model;

import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.model.DirectoryEntity;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectoryEntities {
    private static final Logger log = LoggerFactory.getLogger(DirectoryEntities.class);
    public static final Function<DirectoryEntity, String> NAME_FUNCTION = DirectoryEntity::getName;
    public static final Function<DirectoryEntity, String> LOWER_NAME_FUNCTION = Functions.compose((Function)IdentifierUtils.TO_LOWER_CASE, NAME_FUNCTION);

    public static Iterable<String> namesOf(Iterable<? extends DirectoryEntity> entities) {
        return Iterables.transform(entities, NAME_FUNCTION);
    }

    public static <T extends DirectoryEntity> List<T> filterOutDuplicates(List<T> remoteEntities) {
        return DirectoryEntities.filterOutDuplicates(remoteEntities, DirectoryEntity::getName);
    }

    public static <T> List<T> filterOutDuplicates(Collection<T> remoteEntities, Function<T, String> nameProvider) {
        LinkedHashMap entityMap = Maps.newLinkedHashMap();
        HashSet badEntities = Sets.newHashSet();
        for (T remoteEntity : remoteEntities) {
            T origEntity;
            String remoteName = (String)nameProvider.apply(remoteEntity);
            String entityId = IdentifierUtils.toLowerCase((String)remoteName);
            if (badEntities.contains(entityId) || (origEntity = entityMap.put(entityId, remoteEntity)) == null) continue;
            entityMap.remove(entityId);
            badEntities.add(entityId);
            String origName = (String)nameProvider.apply(origEntity);
            if (!origName.equals(remoteName)) {
                log.warn("entity [{}] of type {} duplicated in remote directory by entity [{}]. Ignoring all occurrences.", new Object[]{origName, remoteEntity.getClass().getSimpleName(), remoteName});
                continue;
            }
            log.warn("entity [{}] of type {} duplicated in remote directory. Ignoring all occurrences.", (Object)origName, (Object)remoteEntity.getClass().getSimpleName());
        }
        return badEntities.isEmpty() && remoteEntities instanceof List ? (List)remoteEntities : ImmutableList.copyOf(entityMap.values());
    }
}

