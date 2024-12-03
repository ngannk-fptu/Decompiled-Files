/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.internal;

import org.hibernate.engine.internal.ForeignKeys;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.Status;
import org.hibernate.event.internal.EventUtil;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;

public enum EntityState {
    PERSISTENT,
    TRANSIENT,
    DETACHED,
    DELETED;

    static final CoreMessageLogger LOG;

    public static EntityState getEntityState(Object entity, String entityName, EntityEntry entry, SessionImplementor source, Boolean assumedUnsaved) {
        if (entry != null) {
            if (entry.getStatus() != Status.DELETED) {
                if (LOG.isTraceEnabled()) {
                    LOG.tracev("Persistent instance of: {0}", EventUtil.getLoggableName(entityName, entity));
                }
                return PERSISTENT;
            }
            if (LOG.isTraceEnabled()) {
                LOG.tracev("Deleted instance of: {0}", EventUtil.getLoggableName(entityName, entity));
            }
            return DELETED;
        }
        if (ForeignKeys.isTransient(entityName, entity, assumedUnsaved, source)) {
            if (LOG.isTraceEnabled()) {
                LOG.tracev("Transient instance of: {0}", EventUtil.getLoggableName(entityName, entity));
            }
            return TRANSIENT;
        }
        if (LOG.isTraceEnabled()) {
            LOG.tracev("Detached instance of: {0}", EventUtil.getLoggableName(entityName, entity));
        }
        return DETACHED;
    }

    static {
        LOG = CoreLogging.messageLogger(EntityState.class);
    }
}

