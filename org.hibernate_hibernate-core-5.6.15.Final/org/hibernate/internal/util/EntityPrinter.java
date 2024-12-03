/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util;

import java.util.HashMap;
import java.util.Map;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.Type;

public final class EntityPrinter {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(EntityPrinter.class);
    private SessionFactoryImplementor factory;

    public String toString(String entityName, Object entity) throws HibernateException {
        EntityPersister entityPersister = this.factory.getEntityPersister(entityName);
        if (entityPersister == null || !entityPersister.isInstance(entity)) {
            return entity.getClass().getName();
        }
        HashMap<String, String> result = new HashMap<String, String>();
        if (entityPersister.hasIdentifierProperty()) {
            result.put(entityPersister.getIdentifierPropertyName(), entityPersister.getIdentifierType().toLoggableString(entityPersister.getIdentifier(entity), this.factory));
        }
        Type[] types = entityPersister.getPropertyTypes();
        String[] names = entityPersister.getPropertyNames();
        Object[] values = entityPersister.getPropertyValues(entity);
        for (int i = 0; i < types.length; ++i) {
            if (names[i].startsWith("_")) continue;
            String strValue = values[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY ? values[i].toString() : (!Hibernate.isInitialized(values[i]) ? "<uninitialized>" : types[i].toLoggableString(values[i], this.factory));
            result.put(names[i], strValue);
        }
        return entityName + ((Object)result).toString();
    }

    public String toString(Type[] types, Object[] values) throws HibernateException {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < types.length; ++i) {
            if (types[i] == null) continue;
            buffer.append(types[i].toLoggableString(values[i], this.factory)).append(", ");
        }
        return buffer.toString();
    }

    public String toString(Map<String, TypedValue> namedTypedValues) throws HibernateException {
        HashMap<String, String> result = new HashMap<String, String>();
        for (Map.Entry<String, TypedValue> entry : namedTypedValues.entrySet()) {
            result.put(entry.getKey(), entry.getValue().getType().toLoggableString(entry.getValue().getValue(), this.factory));
        }
        return ((Object)result).toString();
    }

    public void toString(Iterable<Map.Entry<EntityKey, Object>> entitiesByEntityKey) throws HibernateException {
        if (!LOG.isDebugEnabled() || !entitiesByEntityKey.iterator().hasNext()) {
            return;
        }
        LOG.debug("Listing entities:");
        int i = 0;
        for (Map.Entry<EntityKey, Object> entityKeyAndEntity : entitiesByEntityKey) {
            if (i++ > 20) {
                LOG.debug("More......");
                break;
            }
            LOG.debug(this.toString(entityKeyAndEntity.getKey().getEntityName(), entityKeyAndEntity.getValue()));
        }
    }

    public EntityPrinter(SessionFactoryImplementor factory) {
        this.factory = factory;
    }
}

