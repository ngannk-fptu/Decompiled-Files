/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.util.BatchResult
 */
package com.atlassian.crowd.util.persistence.hibernate.batch;

import com.atlassian.crowd.model.InternalDirectoryEntity;
import com.atlassian.crowd.util.BatchResult;
import com.atlassian.crowd.util.persistence.hibernate.batch.Pair;
import java.util.HashMap;
import java.util.Map;

public class BatchResultWithIdReferences<T>
extends BatchResult<T> {
    private final Map<Pair<Long, String>, Long> entityToId = new HashMap<Pair<Long, String>, Long>();

    public BatchResultWithIdReferences(int totalEntities) {
        super(totalEntities);
    }

    public void addIdReference(InternalDirectoryEntity entity) {
        this.entityToId.put(new Pair<Long, String>(entity.getDirectoryId(), entity.getName()), entity.getId());
    }

    public Long getIdReference(Long directoryId, String name) {
        return this.entityToId.get(new Pair<Long, String>(directoryId, name));
    }
}

