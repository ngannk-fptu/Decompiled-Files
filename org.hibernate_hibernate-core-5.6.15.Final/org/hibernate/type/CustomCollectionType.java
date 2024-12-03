/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.type.CollectionType;
import org.hibernate.type.TypeFactory;
import org.hibernate.usertype.LoggableUserType;
import org.hibernate.usertype.UserCollectionType;

public class CustomCollectionType
extends CollectionType {
    private final UserCollectionType userType;
    private final boolean customLogging;

    @Deprecated
    public CustomCollectionType(TypeFactory.TypeScope typeScope, Class userTypeClass, String role, String foreignKeyPropertyName) {
        this(userTypeClass, role, foreignKeyPropertyName);
    }

    public CustomCollectionType(Class userTypeClass, String role, String foreignKeyPropertyName) {
        super(role, foreignKeyPropertyName);
        this.userType = CustomCollectionType.createUserCollectionType(userTypeClass);
        this.customLogging = LoggableUserType.class.isAssignableFrom(userTypeClass);
    }

    private static UserCollectionType createUserCollectionType(Class userTypeClass) {
        if (!UserCollectionType.class.isAssignableFrom(userTypeClass)) {
            throw new MappingException("Custom type does not implement UserCollectionType: " + userTypeClass.getName());
        }
        try {
            return (UserCollectionType)userTypeClass.newInstance();
        }
        catch (InstantiationException ie) {
            throw new MappingException("Cannot instantiate custom type: " + userTypeClass.getName());
        }
        catch (IllegalAccessException iae) {
            throw new MappingException("IllegalAccessException trying to instantiate custom type: " + userTypeClass.getName());
        }
    }

    @Override
    public PersistentCollection instantiate(SharedSessionContractImplementor session, CollectionPersister persister, Serializable key) throws HibernateException {
        return this.userType.instantiate(session, persister);
    }

    @Override
    public PersistentCollection wrap(SharedSessionContractImplementor session, Object collection) {
        return this.userType.wrap(session, collection);
    }

    @Override
    public Class getReturnedClass() {
        return this.userType.instantiate(-1).getClass();
    }

    @Override
    public Object instantiate(int anticipatedType) {
        return this.userType.instantiate(anticipatedType);
    }

    @Override
    public Iterator getElementsIterator(Object collection) {
        return this.userType.getElementsIterator(collection);
    }

    @Override
    public boolean contains(Object collection, Object entity, SharedSessionContractImplementor session) {
        return this.userType.contains(collection, entity);
    }

    @Override
    public Object indexOf(Object collection, Object entity) {
        return this.userType.indexOf(collection, entity);
    }

    @Override
    public Object replaceElements(Object original, Object target, Object owner, Map copyCache, SharedSessionContractImplementor session) throws HibernateException {
        CollectionPersister cp = session.getFactory().getMetamodel().collectionPersister(this.getRole());
        return this.userType.replaceElements(original, target, cp, owner, copyCache, session);
    }

    @Override
    protected String renderLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
        if (this.customLogging) {
            return ((LoggableUserType)((Object)this.userType)).toLoggableString(value, factory);
        }
        return super.renderLoggableString(value, factory);
    }

    public UserCollectionType getUserType() {
        return this.userType;
    }
}

