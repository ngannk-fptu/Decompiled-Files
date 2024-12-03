/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CommonPersister {
    private static final Object DUMMY = new Object();
    private final Map<Object, Object> persistedObjectIds = new ConcurrentHashMap<Object, Object>();

    public Set<Object> getPersistableObjects(Collection<Object> objectIds) {
        HashSet<Object> idsToPersist = new HashSet<Object>();
        for (Object id : objectIds) {
            if (id == null || this.persistedObjectIds.putIfAbsent(id, DUMMY) != null) continue;
            idsToPersist.add(id);
        }
        return idsToPersist;
    }
}

