/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.internal;

import java.util.Iterator;
import org.hibernate.HibernateException;
import org.hibernate.PropertyValueException;
import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
import org.hibernate.engine.spi.CascadingActions;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.tuple.GenerationTiming;
import org.hibernate.tuple.InMemoryValueGenerationStrategy;
import org.hibernate.type.CollectionType;
import org.hibernate.type.CompositeType;
import org.hibernate.type.Type;

public final class Nullability {
    private final SharedSessionContractImplementor session;
    private final boolean checkNullability;

    public Nullability(SharedSessionContractImplementor session) {
        this.session = session;
        this.checkNullability = session.getFactory().getSessionFactoryOptions().isCheckNullability();
    }

    public void checkNullability(Object[] values, EntityPersister persister, boolean isUpdate) {
        this.checkNullability(values, persister, isUpdate ? NullabilityCheckType.UPDATE : NullabilityCheckType.CREATE);
    }

    public void checkNullability(Object[] values, EntityPersister persister, NullabilityCheckType checkType) {
        if (this.checkNullability) {
            boolean[] nullability = persister.getPropertyNullability();
            boolean[] checkability = checkType == NullabilityCheckType.CREATE ? persister.getPropertyInsertability() : persister.getPropertyUpdateability();
            Type[] propertyTypes = persister.getPropertyTypes();
            InMemoryValueGenerationStrategy[] inMemoryValueGenerationStrategies = persister.getEntityMetamodel().getInMemoryValueGenerationStrategies();
            for (int i = 0; i < values.length; ++i) {
                String breakProperties;
                if (!checkability[i] || values[i] == LazyPropertyInitializer.UNFETCHED_PROPERTY || GenerationTiming.NEVER != inMemoryValueGenerationStrategies[i].getGenerationTiming()) continue;
                Object value = values[i];
                if (!nullability[i] && value == null) {
                    throw new PropertyValueException("not-null property references a null or transient value", persister.getEntityName(), persister.getPropertyNames()[i]);
                }
                if (value == null || (breakProperties = this.checkSubElementsNullability(propertyTypes[i], value)) == null) continue;
                throw new PropertyValueException("not-null property references a null or transient value", persister.getEntityName(), Nullability.buildPropertyPath(persister.getPropertyNames()[i], breakProperties));
            }
        }
    }

    private String checkSubElementsNullability(Type propertyType, Object value) throws HibernateException {
        CollectionType collectionType;
        Type collectionElementType;
        if (propertyType.isComponentType()) {
            return this.checkComponentNullability(value, (CompositeType)propertyType);
        }
        if (propertyType.isCollectionType() && (collectionElementType = (collectionType = (CollectionType)propertyType).getElementType(this.session.getFactory())).isComponentType()) {
            CompositeType componentType = (CompositeType)collectionElementType;
            Iterator itr = CascadingActions.getLoadedElementsIterator(this.session, collectionType, value);
            while (itr.hasNext()) {
                Object compositeElement = itr.next();
                if (compositeElement == null) continue;
                return this.checkComponentNullability(compositeElement, componentType);
            }
        }
        return null;
    }

    private String checkComponentNullability(Object value, CompositeType compositeType) throws HibernateException {
        if (compositeType.isAnyType()) {
            return null;
        }
        boolean[] nullability = compositeType.getPropertyNullability();
        if (nullability != null) {
            Object[] subValues = compositeType.getPropertyValues(value, this.session);
            Type[] propertyTypes = compositeType.getSubtypes();
            for (int i = 0; i < subValues.length; ++i) {
                String breakProperties;
                Object subValue = subValues[i];
                if (!nullability[i] && subValue == null) {
                    return compositeType.getPropertyNames()[i];
                }
                if (subValue == null || (breakProperties = this.checkSubElementsNullability(propertyTypes[i], subValue)) == null) continue;
                return Nullability.buildPropertyPath(compositeType.getPropertyNames()[i], breakProperties);
            }
        }
        return null;
    }

    private static String buildPropertyPath(String parent, String child) {
        return parent + '.' + child;
    }

    public static enum NullabilityCheckType {
        CREATE,
        UPDATE,
        DELETE;

    }
}

