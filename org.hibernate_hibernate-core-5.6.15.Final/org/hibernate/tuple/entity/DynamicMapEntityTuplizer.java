/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple.entity;

import java.util.Map;
import org.hibernate.EntityMode;
import org.hibernate.EntityNameResolver;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.property.access.internal.PropertyAccessStrategyMapImpl;
import org.hibernate.property.access.spi.Getter;
import org.hibernate.property.access.spi.PropertyAccess;
import org.hibernate.property.access.spi.Setter;
import org.hibernate.proxy.ProxyFactory;
import org.hibernate.proxy.map.MapProxyFactory;
import org.hibernate.tuple.DynamicMapInstantiator;
import org.hibernate.tuple.Instantiator;
import org.hibernate.tuple.entity.AbstractEntityTuplizer;
import org.hibernate.tuple.entity.EntityMetamodel;

public class DynamicMapEntityTuplizer
extends AbstractEntityTuplizer {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(DynamicMapEntityTuplizer.class);

    DynamicMapEntityTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappedEntity) {
        super(entityMetamodel, mappedEntity);
    }

    @Override
    public EntityMode getEntityMode() {
        return EntityMode.MAP;
    }

    private PropertyAccess buildPropertyAccess(Property mappedProperty) {
        if (mappedProperty.isBackRef()) {
            return mappedProperty.getPropertyAccessStrategy(null).buildPropertyAccess(null, mappedProperty.getName());
        }
        return PropertyAccessStrategyMapImpl.INSTANCE.buildPropertyAccess(null, mappedProperty.getName());
    }

    @Override
    protected Getter buildPropertyGetter(Property mappedProperty, PersistentClass mappedEntity) {
        return this.buildPropertyAccess(mappedProperty).getGetter();
    }

    @Override
    protected Setter buildPropertySetter(Property mappedProperty, PersistentClass mappedEntity) {
        return this.buildPropertyAccess(mappedProperty).getSetter();
    }

    @Override
    protected Instantiator buildInstantiator(EntityMetamodel entityMetamodel, PersistentClass mappingInfo) {
        return new DynamicMapInstantiator(mappingInfo);
    }

    @Override
    protected ProxyFactory buildProxyFactory(PersistentClass mappingInfo, Getter idGetter, Setter idSetter) {
        MapProxyFactory pf = new MapProxyFactory();
        try {
            pf.postInstantiate(this.getEntityName(), null, null, null, null, null);
        }
        catch (HibernateException he) {
            LOG.unableToCreateProxyFactory(this.getEntityName(), he);
            pf = null;
        }
        return pf;
    }

    @Override
    public Class getMappedClass() {
        return Map.class;
    }

    @Override
    public Class getConcreteProxyClass() {
        return Map.class;
    }

    @Override
    public EntityNameResolver[] getEntityNameResolvers() {
        return new EntityNameResolver[]{BasicEntityNameResolver.INSTANCE};
    }

    @Override
    public String determineConcreteSubclassEntityName(Object entityInstance, SessionFactoryImplementor factory) {
        return DynamicMapEntityTuplizer.extractEmbeddedEntityName((Map)entityInstance);
    }

    public static String extractEmbeddedEntityName(Map entity) {
        return (String)entity.get("$type$");
    }

    public static class BasicEntityNameResolver
    implements EntityNameResolver {
        public static final BasicEntityNameResolver INSTANCE = new BasicEntityNameResolver();

        @Override
        public String resolveEntityName(Object entity) {
            if (!Map.class.isInstance(entity)) {
                return null;
            }
            String entityName = DynamicMapEntityTuplizer.extractEmbeddedEntityName((Map)entity);
            if (entityName == null) {
                throw new HibernateException("Could not determine type of dynamic map entity");
            }
            return entityName;
        }

        public boolean equals(Object obj) {
            return obj != null && this.getClass().equals(obj.getClass());
        }

        public int hashCode() {
            return this.getClass().hashCode();
        }
    }
}

