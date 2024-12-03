/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.Type
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.hibernate.Hibernate
 *  org.hibernate.HibernateException
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.collection.spi.PersistentCollection
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.hibernate.metadata.ClassMetadata
 *  org.hibernate.persister.collection.CollectionPersister
 *  org.hibernate.persister.entity.Joinable
 *  org.hibernate.type.AssociationType
 *  org.hibernate.type.CollectionType
 *  org.hibernate.type.EntityType
 *  org.hibernate.type.Type
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.core.persistence.hibernate;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.metamodel.Type;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.type.AssociationType;
import org.hibernate.type.CollectionType;
import org.hibernate.type.EntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
final class AllPersistentObjectsLoader {
    private static final Logger log = LoggerFactory.getLogger(AllPersistentObjectsLoader.class);

    AllPersistentObjectsLoader() {
    }

    public List<Object> doInHibernate(Session session, Set excludedClassesForRetrievingAllObjects) throws HibernateException {
        ArrayList<Object> objects = new ArrayList<Object>();
        HashMap objectsMap = new HashMap();
        SessionFactory sessionFactory = session.getSessionFactory();
        SessionFactoryImplementor sessionFactoryImplementor = (SessionFactoryImplementor)sessionFactory;
        Set entities = sessionFactory.getMetamodel().getEntities();
        List classes = entities.stream().map(Type::getJavaType).filter(Objects::nonNull).collect(Collectors.toList());
        for (Class clazz : classes) {
            if (excludedClassesForRetrievingAllObjects != null && excludedClassesForRetrievingAllObjects.contains(clazz.getName())) continue;
            try {
                Object ignored = clazz.newInstance();
                List list = session.createQuery("from " + clazz.getName()).list();
                objects.addAll(list);
                for (Object obj : list) {
                    ClassMetadata metaData = sessionFactory.getClassMetadata(clazz);
                    Class objClazz = metaData.getMappedClass();
                    Serializable objId = metaData.getIdentifier(obj);
                    objectsMap.put(new Key(objClazz, objId), obj);
                }
            }
            catch (IllegalAccessException | InstantiationException e) {
                log.warn(e.getMessage(), (Throwable)e);
            }
        }
        for (Object obj : objects) {
            Class clazz = Hibernate.getClass((Object)obj);
            ClassMetadata metaData = sessionFactory.getClassMetadata(clazz);
            org.hibernate.type.Type[] types = metaData.getPropertyTypes();
            String[] propertyNames = metaData.getPropertyNames();
            for (int j = 0; j < types.length; ++j) {
                String[] referencedColumns;
                Object associatedObjectInAssociatedObject;
                Serializable associatedObjectId;
                org.hibernate.type.Type type = types[j];
                String propertyName = propertyNames[j];
                if (type.isCollectionType()) {
                    Object col;
                    CollectionType collectionType = (CollectionType)type;
                    Joinable joinable = collectionType.getAssociatedJoinable(sessionFactoryImplementor);
                    if (joinable instanceof CollectionPersister && ((CollectionPersister)joinable).isManyToMany() || !((col = metaData.getPropertyValue(obj, propertyName)) instanceof PersistentCollection)) continue;
                    Collection collection = this.getProperCollection(col);
                    metaData.setPropertyValue(obj, propertyName, (Object)collection);
                    continue;
                }
                if (!type.isAssociationType()) continue;
                EntityType entityType = (EntityType)type;
                Class associatedClass = entityType.getReturnedClass();
                ClassMetadata associatedClassMetaData = sessionFactory.getClassMetadata(associatedClass);
                Object associatedObject = metaData.getPropertyValue(obj, propertyName);
                if (associatedObject == null) continue;
                Class associatedObjectClazz = associatedClassMetaData.getMappedClass();
                Object fullyLoadedAssociatedObject = objectsMap.get(new Key(associatedObjectClazz, associatedObjectId = associatedClassMetaData.getIdentifier(associatedObject)));
                if (fullyLoadedAssociatedObject != null) {
                    associatedObject = fullyLoadedAssociatedObject;
                    metaData.setPropertyValue(obj, propertyName, associatedObject);
                }
                if ((associatedObjectInAssociatedObject = this.findCollectionInAssociatedObject(sessionFactoryImplementor, referencedColumns = entityType.getAssociatedJoinable(sessionFactoryImplementor).getKeyColumnNames(), associatedClassMetaData, associatedObject, clazz)) == null || !(associatedObjectInAssociatedObject instanceof Collection)) continue;
                ((Collection)associatedObjectInAssociatedObject).add(obj);
            }
        }
        return objects;
    }

    private @Nullable Collection getProperCollection(Object col) {
        AbstractCollection associatedRows = null;
        if (col instanceof Set) {
            associatedRows = new HashSet();
        } else if (col instanceof List) {
            associatedRows = new ArrayList();
        }
        return associatedRows;
    }

    private @Nullable Object findCollectionInAssociatedObject(SessionFactoryImplementor sessionFactoryImplementor, String[] referencedColumns, ClassMetadata referencedClassMetaData, Object referencedObject, Class clazz) throws HibernateException {
        org.hibernate.type.Type[] types = referencedClassMetaData.getPropertyTypes();
        String[] propertyNames = referencedClassMetaData.getPropertyNames();
        for (int i = 0; i < types.length; ++i) {
            org.hibernate.type.Type type = types[i];
            if (!type.isAssociationType()) continue;
            AssociationType associationType = (AssociationType)type;
            Class associatedClass = associationType.getReturnedClass();
            Class associationTypeClass = associationType.getReturnedClass();
            Object[] referencedColumnsOfAssociation = associationType.getAssociatedJoinable(sessionFactoryImplementor).getKeyColumnNames();
            boolean sameColumns = Arrays.equals(referencedColumns, referencedColumnsOfAssociation);
            if (!sameColumns || !associatedClass.isAssignableFrom(clazz)) continue;
            try {
                AbstractCollection associatedObject = referencedClassMetaData.getPropertyValue(referencedObject, propertyNames[i]);
                if (associatedObject instanceof PersistentCollection) {
                    if (Set.class.isAssignableFrom(associationTypeClass)) {
                        associatedObject = new HashSet();
                    } else if (List.class.isAssignableFrom(associationTypeClass)) {
                        associatedObject = new ArrayList();
                    }
                }
                referencedClassMetaData.setPropertyValue(referencedObject, propertyNames[i], (Object)associatedObject);
                return associatedObject;
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    private static final class Key {
        Object key1;
        Object key2;

        public Key(Object key1, Object key2) {
            this.key1 = key1;
            this.key2 = key2;
        }

        public int hashCode() {
            int result = this.key1.hashCode();
            result = 29 * result + this.key2.hashCode();
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Key)) {
                return false;
            }
            Key theOtherObj = (Key)obj;
            return this.key1.equals(theOtherObj.key1) && this.key2.equals(theOtherObj.key2);
        }
    }
}

