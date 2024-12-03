/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.internal;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.VersionType;
import org.jboss.logging.Logger;

public final class Versioning {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)Versioning.class.getName());

    private Versioning() {
    }

    private static Object seed(VersionType versionType, SharedSessionContractImplementor session) {
        Object seed = versionType.seed(session);
        LOG.tracef("Seeding: %s", seed);
        return seed;
    }

    public static boolean seedVersion(Object[] fields, int versionProperty, VersionType versionType, SharedSessionContractImplementor session) {
        Object initialVersion = fields[versionProperty];
        if (initialVersion == null || initialVersion instanceof Number && ((Number)initialVersion).longValue() < 0L) {
            fields[versionProperty] = Versioning.seed(versionType, session);
            return true;
        }
        LOG.tracev("Using initial version: {0}", initialVersion);
        return false;
    }

    public static Object increment(Object version, VersionType versionType, SharedSessionContractImplementor session) {
        Object next = versionType.next(version, session);
        if (LOG.isTraceEnabled()) {
            LOG.tracef("Incrementing: %s to %s", versionType.toLoggableString(version, session.getFactory()), versionType.toLoggableString(next, session.getFactory()));
        }
        return next;
    }

    public static void setVersion(Object[] fields, Object version, EntityPersister persister) {
        if (!persister.isVersioned()) {
            return;
        }
        fields[persister.getVersionProperty()] = version;
    }

    public static Object getVersion(Object[] fields, EntityPersister persister) {
        if (!persister.isVersioned()) {
            return null;
        }
        return fields[persister.getVersionProperty()];
    }

    public static boolean isVersionIncrementRequired(int[] dirtyProperties, boolean hasDirtyCollections, boolean[] propertyVersionability) {
        if (hasDirtyCollections) {
            return true;
        }
        for (int dirtyProperty : dirtyProperties) {
            if (!propertyVersionability[dirtyProperty]) continue;
            return true;
        }
        return false;
    }
}

