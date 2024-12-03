/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi.entry;

import java.io.Serializable;
import org.hibernate.AssertionFailure;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.cache.spi.entry.CacheEntry;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.event.service.spi.EventListenerGroup;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PreLoadEvent;
import org.hibernate.event.spi.PreLoadEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.TypeHelper;

public class StandardCacheEntryImpl
implements CacheEntry {
    private final Serializable[] disassembledState;
    private final Object version;
    private final String subclass;

    public StandardCacheEntryImpl(Object[] state, EntityPersister persister, Object version, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        this.disassembledState = TypeHelper.disassemble(state, persister.getPropertyTypes(), persister.isLazyPropertiesCacheable() ? null : persister.getPropertyLaziness(), session, owner);
        this.subclass = persister.getEntityName();
        this.version = version;
    }

    StandardCacheEntryImpl(Serializable[] disassembledState, String subclass, Object version) {
        this.disassembledState = disassembledState;
        this.subclass = subclass;
        this.version = version;
    }

    @Override
    public boolean isReferenceEntry() {
        return false;
    }

    @Override
    public Serializable[] getDisassembledState() {
        return this.disassembledState;
    }

    @Override
    public String getSubclass() {
        return this.subclass;
    }

    @Override
    public Object getVersion() {
        return this.version;
    }

    public boolean isDeepCopyNeeded() {
        return true;
    }

    public Object[] assemble(Object instance, Serializable id, EntityPersister persister, Interceptor interceptor, EventSource session) throws HibernateException {
        if (!persister.getEntityName().equals(this.subclass)) {
            throw new AssertionFailure("Tried to assemble a different subclass instance");
        }
        Object[] state = TypeHelper.assemble(this.disassembledState, persister.getPropertyTypes(), session, instance);
        PreLoadEvent preLoadEvent = new PreLoadEvent(session).setEntity(instance).setState(state).setId(id).setPersister(persister);
        EventListenerGroup<PreLoadEventListener> listenerGroup = session.getFactory().getServiceRegistry().getService(EventListenerRegistry.class).getEventListenerGroup(EventType.PRE_LOAD);
        for (PreLoadEventListener listener : listenerGroup.listeners()) {
            listener.onPreLoad(preLoadEvent);
        }
        persister.setPropertyValues(instance, state);
        return state;
    }

    public String toString() {
        return "CacheEntry(" + this.subclass + ')';
    }
}

