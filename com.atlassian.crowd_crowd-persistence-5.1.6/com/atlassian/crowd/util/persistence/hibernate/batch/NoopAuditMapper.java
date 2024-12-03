/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.util.persistence.hibernate.batch;

import com.atlassian.crowd.model.audit.AuditLogChangesetEntity;
import com.atlassian.crowd.util.persistence.hibernate.batch.BulkAuditMapper;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class NoopAuditMapper<T>
implements BulkAuditMapper<T> {
    @Override
    public List<AuditLogChangesetEntity> apply(Collection<T> ts) {
        return Collections.emptyList();
    }
}

