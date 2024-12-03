/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.Attribute
 *  javax.persistence.metamodel.Attribute$PersistentAttributeType
 *  javax.persistence.metamodel.Bindable
 *  javax.persistence.metamodel.Bindable$BindableType
 *  javax.persistence.metamodel.ManagedType
 *  javax.persistence.metamodel.MapAttribute
 *  javax.persistence.metamodel.SingularAttribute
 *  javax.persistence.metamodel.Type
 *  javax.persistence.metamodel.Type$PersistenceType
 */
package org.hibernate.query.criteria.internal.path;

import java.io.Serializable;
import java.lang.reflect.Member;
import java.util.Map;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.MapJoinImplementor;
import org.hibernate.query.criteria.internal.PathImplementor;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.path.AbstractPathImpl;

public class MapKeyHelpers {
    private MapKeyHelpers() {
    }

    public static class MapKeyAttribute<K>
    implements SingularAttribute<Map<K, ?>, K>,
    Bindable<K>,
    Serializable {
        private final MapAttribute<?, K, ?> attribute;
        private final CollectionPersister mapPersister;
        private final org.hibernate.type.Type mapKeyType;
        private final Type<K> jpaType;
        private final Bindable.BindableType jpaBindableType;
        private final Class<K> jpaBinableJavaType;
        private final Attribute.PersistentAttributeType persistentAttributeType;

        public MapKeyAttribute(CriteriaBuilderImpl criteriaBuilder, MapAttribute<?, K, ?> attribute) {
            this.attribute = attribute;
            this.jpaType = attribute.getKeyType();
            this.jpaBinableJavaType = attribute.getKeyJavaType();
            this.jpaBindableType = Type.PersistenceType.ENTITY.equals((Object)this.jpaType.getPersistenceType()) ? Bindable.BindableType.ENTITY_TYPE : Bindable.BindableType.SINGULAR_ATTRIBUTE;
            String guessedRoleName = this.determineRole(attribute);
            SessionFactoryImplementor sfi = criteriaBuilder.getEntityManagerFactory().getSessionFactory();
            this.mapPersister = sfi.getCollectionPersister(guessedRoleName);
            if (this.mapPersister == null) {
                throw new IllegalStateException("Could not locate collection persister [" + guessedRoleName + "]");
            }
            this.mapKeyType = this.mapPersister.getIndexType();
            if (this.mapKeyType == null) {
                throw new IllegalStateException("Could not determine map-key type [" + guessedRoleName + "]");
            }
            this.persistentAttributeType = this.mapKeyType.isEntityType() ? Attribute.PersistentAttributeType.MANY_TO_ONE : (this.mapKeyType.isComponentType() ? Attribute.PersistentAttributeType.EMBEDDED : Attribute.PersistentAttributeType.BASIC);
        }

        private String determineRole(MapAttribute<?, K, ?> attribute) {
            return attribute.getDeclaringType().getJavaType().getName() + '.' + attribute.getName();
        }

        public String getName() {
            return "map-key";
        }

        public Attribute.PersistentAttributeType getPersistentAttributeType() {
            return this.persistentAttributeType;
        }

        public ManagedType<Map<K, ?>> getDeclaringType() {
            return null;
        }

        public Class<K> getJavaType() {
            return this.attribute.getKeyJavaType();
        }

        public Member getJavaMember() {
            return null;
        }

        public boolean isAssociation() {
            return this.mapKeyType.isEntityType();
        }

        public boolean isCollection() {
            return false;
        }

        public boolean isId() {
            return false;
        }

        public boolean isVersion() {
            return false;
        }

        public boolean isOptional() {
            return false;
        }

        public Type<K> getType() {
            return this.jpaType;
        }

        public Bindable.BindableType getBindableType() {
            return this.jpaBindableType;
        }

        public Class<K> getBindableJavaType() {
            return this.jpaBinableJavaType;
        }
    }

    public static class MapKeySource<K, V>
    extends AbstractPathImpl<Map<K, V>>
    implements PathImplementor<Map<K, V>>,
    Serializable {
        private final MapAttribute<?, K, V> mapAttribute;
        private final MapJoinImplementor<?, K, V> mapJoin;

        public MapKeySource(CriteriaBuilderImpl criteriaBuilder, Class<Map<K, V>> javaType, MapJoinImplementor<?, K, V> mapJoin, MapAttribute<?, K, V> attribute) {
            super(criteriaBuilder, javaType, null);
            this.mapJoin = mapJoin;
            this.mapAttribute = attribute;
        }

        public MapAttribute<?, K, V> getAttribute() {
            return this.mapAttribute;
        }

        public Bindable<Map<K, V>> getModel() {
            return this.mapAttribute;
        }

        @Override
        public PathImplementor<?> getParentPath() {
            return (PathImplementor)this.mapJoin.getParentPath();
        }

        @Override
        protected boolean canBeDereferenced() {
            return false;
        }

        @Override
        protected Attribute locateAttributeInternal(String attributeName) {
            throw new IllegalArgumentException("Map [" + this.mapJoin.getPathIdentifier() + "] cannot be dereferenced");
        }

        @Override
        public <T extends Map<K, V>> PathImplementor<T> treatAs(Class<T> treatAsType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getPathIdentifier() {
            return this.mapJoin.getPathIdentifier();
        }

        @Override
        public void prepareAlias(RenderingContext renderingContext) {
            this.mapJoin.prepareAlias(renderingContext);
        }
    }

    public static class MapKeyPath<K>
    extends AbstractPathImpl<K>
    implements PathImplementor<K>,
    Serializable {
        private final MapKeyAttribute<K> mapKeyAttribute;

        public MapKeyPath(CriteriaBuilderImpl criteriaBuilder, MapKeySource<K, ?> source, MapKeyAttribute<K> mapKeyAttribute) {
            super(criteriaBuilder, mapKeyAttribute.getJavaType(), source);
            this.mapKeyAttribute = mapKeyAttribute;
        }

        @Override
        public MapKeySource getPathSource() {
            return (MapKeySource)super.getPathSource();
        }

        public MapKeyAttribute<K> getAttribute() {
            return this.mapKeyAttribute;
        }

        private boolean isBasicTypeKey() {
            return Attribute.PersistentAttributeType.BASIC == this.mapKeyAttribute.getPersistentAttributeType();
        }

        @Override
        protected boolean canBeDereferenced() {
            return !this.isBasicTypeKey();
        }

        @Override
        protected Attribute locateAttributeInternal(String attributeName) {
            if (!this.canBeDereferenced()) {
                throw new IllegalArgumentException("Map key [" + this.getPathSource().getPathIdentifier() + "] cannot be dereferenced");
            }
            throw new UnsupportedOperationException("Not yet supported!");
        }

        public Bindable<K> getModel() {
            return this.mapKeyAttribute;
        }

        @Override
        public <T extends K> MapKeyPath<T> treatAs(Class<T> treatAsType) {
            return this;
        }

        @Override
        public String render(RenderingContext renderingContext) {
            String name;
            MapKeySource source = this.getPathSource();
            if (source != null) {
                source.prepareAlias(renderingContext);
                name = source.getPathIdentifier();
            } else {
                name = this.getAttribute().getName();
            }
            return "key(" + name + ")";
        }
    }
}

