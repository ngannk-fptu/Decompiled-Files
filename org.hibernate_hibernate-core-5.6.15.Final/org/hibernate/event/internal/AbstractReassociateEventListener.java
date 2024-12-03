/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.event.internal;

import java.io.Serializable;
import org.hibernate.LockMode;
import org.hibernate.engine.internal.Versioning;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.Status;
import org.hibernate.event.internal.OnLockVisitor;
import org.hibernate.event.spi.AbstractEvent;
import org.hibernate.event.spi.EventSource;
import org.hibernate.internal.CoreLogging;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.type.TypeHelper;
import org.jboss.logging.Logger;

public abstract class AbstractReassociateEventListener
implements Serializable {
    private static final Logger log = CoreLogging.logger(AbstractReassociateEventListener.class);

    protected final EntityEntry reassociate(AbstractEvent event, Object object, Serializable id, EntityPersister persister) {
        if (log.isTraceEnabled()) {
            log.tracev("Reassociating transient instance: {0}", (Object)MessageHelper.infoString(persister, id, event.getSession().getFactory()));
        }
        EventSource source = event.getSession();
        EntityKey key = source.generateEntityKey(id, persister);
        PersistenceContext persistenceContext = source.getPersistenceContext();
        persistenceContext.checkUniqueness(key, object);
        Object[] values = persister.getPropertyValues(object);
        TypeHelper.deepCopy(values, persister.getPropertyTypes(), persister.getPropertyUpdateability(), values, source);
        Object version = Versioning.getVersion(values, persister);
        EntityEntry newEntry = persistenceContext.addEntity(object, persister.isMutable() ? Status.MANAGED : Status.READ_ONLY, values, key, version, LockMode.NONE, true, persister, false);
        new OnLockVisitor(source, id, object).process(object, persister);
        persister.afterReassociate(object, source);
        return newEntry;
    }
}

