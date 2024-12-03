/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityGraph
 */
package org.hibernate.metamodel.spi;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityGraph;
import org.hibernate.EntityNameResolver;
import org.hibernate.Metamodel;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.graph.spi.RootGraphImplementor;
import org.hibernate.metamodel.model.domain.spi.EmbeddedTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.EntityTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.ManagedTypeDescriptor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.spi.TypeConfiguration;

public interface MetamodelImplementor
extends Metamodel {
    public TypeConfiguration getTypeConfiguration();

    @Override
    public SessionFactoryImplementor getSessionFactory();

    public Collection<EntityNameResolver> getEntityNameResolvers();

    public EntityPersister locateEntityPersister(Class var1);

    public EntityPersister locateEntityPersister(String var1);

    public EntityPersister entityPersister(Class var1);

    public EntityPersister entityPersister(String var1);

    public Map<String, EntityPersister> entityPersisters();

    public CollectionPersister collectionPersister(String var1);

    public Map<String, CollectionPersister> collectionPersisters();

    public Set<String> getCollectionRolesByEntityParticipant(String var1);

    public String[] getAllEntityNames();

    public String[] getAllCollectionRoles();

    public <T> void addNamedEntityGraph(String var1, RootGraphImplementor<T> var2);

    @Deprecated
    public <T> void addNamedEntityGraph(String var1, EntityGraph<T> var2);

    public <T> RootGraphImplementor<T> findEntityGraphByName(String var1);

    public <T> List<RootGraphImplementor<? super T>> findEntityGraphsByJavaType(Class<T> var1);

    @Deprecated
    default public <T> List<EntityGraph<? super T>> findEntityGraphsByType(Class<T> entityClass) {
        return this.findEntityGraphsByJavaType(entityClass);
    }

    public void close();

    public <X> EntityTypeDescriptor<X> entity(String var1);

    public <X> EntityTypeDescriptor<X> entity(Class<X> var1);

    public <X> ManagedTypeDescriptor<X> managedType(Class<X> var1);

    public <X> EmbeddedTypeDescriptor<X> embeddable(Class<X> var1);

    @Override
    default public EntityTypeDescriptor getEntityTypeByName(String entityName) {
        return this.entity(entityName);
    }
}

