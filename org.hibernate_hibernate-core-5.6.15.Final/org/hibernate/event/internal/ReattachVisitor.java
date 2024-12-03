/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.internal;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.action.internal.CollectionRemoveAction;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.event.internal.ProxyVisitor;
import org.hibernate.event.spi.EventSource;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.type.CompositeType;
import org.hibernate.type.Type;

public abstract class ReattachVisitor
extends ProxyVisitor {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(ReattachVisitor.class);
    private final Serializable ownerIdentifier;
    private final Object owner;

    public ReattachVisitor(EventSource session, Serializable ownerIdentifier, Object owner) {
        super(session);
        this.ownerIdentifier = ownerIdentifier;
        this.owner = owner;
    }

    final Serializable getOwnerIdentifier() {
        return this.ownerIdentifier;
    }

    final Object getOwner() {
        return this.owner;
    }

    @Override
    Object processComponent(Object component, CompositeType componentType) throws HibernateException {
        Type[] types = componentType.getSubtypes();
        if (component == null) {
            this.processValues(new Object[types.length], types);
        } else {
            super.processComponent(component, componentType);
        }
        return null;
    }

    void removeCollection(CollectionPersister role, Serializable collectionKey, EventSource source) throws HibernateException {
        if (LOG.isTraceEnabled()) {
            LOG.tracev("Collection dereferenced while transient {0}", MessageHelper.collectionInfoString(role, this.ownerIdentifier, source.getFactory()));
        }
        source.getActionQueue().addAction(new CollectionRemoveAction(this.owner, role, collectionKey, false, (SharedSessionContractImplementor)source));
    }

    final Serializable extractCollectionKeyFromOwner(CollectionPersister role) {
        if (role.getCollectionType().useLHSPrimaryKey()) {
            return this.ownerIdentifier;
        }
        return (Serializable)role.getOwnerEntityPersister().getPropertyValue(this.owner, role.getCollectionType().getLHSPropertyName());
    }
}

