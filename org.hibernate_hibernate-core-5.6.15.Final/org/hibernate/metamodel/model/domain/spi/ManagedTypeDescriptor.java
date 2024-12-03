/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.CollectionAttribute
 *  javax.persistence.metamodel.ListAttribute
 *  javax.persistence.metamodel.MapAttribute
 *  javax.persistence.metamodel.SetAttribute
 *  javax.persistence.metamodel.SingularAttribute
 */
package org.hibernate.metamodel.model.domain.spi;

import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import org.hibernate.graph.spi.SubGraphImplementor;
import org.hibernate.metamodel.model.domain.ManagedDomainType;
import org.hibernate.metamodel.model.domain.spi.BagPersistentAttribute;
import org.hibernate.metamodel.model.domain.spi.ListPersistentAttribute;
import org.hibernate.metamodel.model.domain.spi.MapPersistentAttribute;
import org.hibernate.metamodel.model.domain.spi.PersistentAttributeDescriptor;
import org.hibernate.metamodel.model.domain.spi.PluralPersistentAttribute;
import org.hibernate.metamodel.model.domain.spi.SetPersistentAttribute;
import org.hibernate.metamodel.model.domain.spi.SimpleTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.SingularPersistentAttribute;

public interface ManagedTypeDescriptor<J>
extends SimpleTypeDescriptor<J>,
ManagedDomainType<J> {
    public ManagedTypeDescriptor<? super J> getSuperType();

    public String getName();

    public SubGraphImplementor<J> makeSubGraph();

    default public SubGraphImplementor<J> getDefaultGraph() {
        throw new UnsupportedOperationException("Not yet implemented - " + this.getClass().getName());
    }

    public <S extends J> SubGraphImplementor<S> makeSubGraph(Class<S> var1);

    public <S extends J> ManagedTypeDescriptor<S> findSubType(String var1);

    public <S extends J> ManagedTypeDescriptor<S> findSubType(Class<S> var1);

    public InFlightAccess<J> getInFlightAccess();

    public PersistentAttributeDescriptor<J, ?> findDeclaredAttribute(String var1);

    public PersistentAttributeDescriptor<? super J, ?> findAttribute(String var1);

    public PersistentAttributeDescriptor<J, ?> getDeclaredAttribute(String var1);

    public PersistentAttributeDescriptor<? super J, ?> getAttribute(String var1);

    public <Y> SingularPersistentAttribute<? super J, Y> getSingularAttribute(String var1, Class<Y> var2);

    public <Y> SingularPersistentAttribute<J, Y> getDeclaredSingularAttribute(String var1, Class<Y> var2);

    public <C, E> PluralPersistentAttribute<J, C, E> getPluralAttribute(String var1);

    public <E> BagPersistentAttribute<? super J, E> getCollection(String var1, Class<E> var2);

    default public <E> CollectionAttribute<J, E> getDeclaredCollection(String name, Class<E> elementType) {
        return null;
    }

    default public <E> SetAttribute<? super J, E> getSet(String name, Class<E> elementType) {
        return null;
    }

    default public <E> SetAttribute<J, E> getDeclaredSet(String name, Class<E> elementType) {
        return null;
    }

    default public <E> ListAttribute<? super J, E> getList(String name, Class<E> elementType) {
        return null;
    }

    default public <E> ListAttribute<J, E> getDeclaredList(String name, Class<E> elementType) {
        return null;
    }

    default public <K, V> MapAttribute<? super J, K, V> getMap(String name, Class<K> keyType, Class<V> valueType) {
        return null;
    }

    default public <K, V> MapAttribute<J, K, V> getDeclaredMap(String name, Class<K> keyType, Class<V> valueType) {
        return null;
    }

    default public SingularAttribute<? super J, ?> getSingularAttribute(String name) {
        return null;
    }

    default public SingularAttribute<J, ?> getDeclaredSingularAttribute(String name) {
        return null;
    }

    default public CollectionAttribute<? super J, ?> getCollection(String name) {
        return null;
    }

    default public CollectionAttribute<J, ?> getDeclaredCollection(String name) {
        return null;
    }

    default public SetPersistentAttribute<? super J, ?> getSet(String name) {
        return null;
    }

    default public SetPersistentAttribute<J, ?> getDeclaredSet(String name) {
        return null;
    }

    default public ListPersistentAttribute<? super J, ?> getList(String name) {
        return null;
    }

    default public ListPersistentAttribute<J, ?> getDeclaredList(String name) {
        return null;
    }

    default public MapPersistentAttribute<? super J, ?, ?> getMap(String name) {
        return null;
    }

    default public MapPersistentAttribute<J, ?, ?> getDeclaredMap(String name) {
        return null;
    }

    public static interface InFlightAccess<J> {
        public void addAttribute(PersistentAttributeDescriptor<J, ?> var1);

        public void finishUp();
    }
}

