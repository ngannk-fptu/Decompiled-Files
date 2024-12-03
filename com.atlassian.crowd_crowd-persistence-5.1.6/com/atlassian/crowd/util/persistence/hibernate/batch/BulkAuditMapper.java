/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.util.persistence.hibernate.batch;

import com.atlassian.crowd.model.audit.AuditLogChangesetEntity;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

@FunctionalInterface
public interface BulkAuditMapper<T>
extends Function<Collection<T>, List<AuditLogChangesetEntity>> {
}

