/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
import org.hibernate.collection.internal.PersistentArrayHolder;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.type.CollectionType;
import org.hibernate.type.Type;
import org.hibernate.type.TypeFactory;

public class ArrayType
extends CollectionType {
    private final Class elementClass;
    private final Class arrayClass;

    @Deprecated
    public ArrayType(TypeFactory.TypeScope typeScope, String role, String propertyRef, Class elementClass) {
        this(role, propertyRef, elementClass);
    }

    public ArrayType(String role, String propertyRef, Class elementClass) {
        super(role, propertyRef);
        this.elementClass = elementClass;
        this.arrayClass = Array.newInstance(elementClass, 0).getClass();
    }

    @Override
    public Class getReturnedClass() {
        return this.arrayClass;
    }

    @Override
    public PersistentCollection instantiate(SharedSessionContractImplementor session, CollectionPersister persister, Serializable key) throws HibernateException {
        return new PersistentArrayHolder(session, persister);
    }

    @Override
    public Iterator getElementsIterator(Object collection) {
        return Arrays.asList((Object[])collection).iterator();
    }

    @Override
    public PersistentCollection wrap(SharedSessionContractImplementor session, Object array) {
        return new PersistentArrayHolder(session, array);
    }

    @Override
    public boolean isArrayType() {
        return true;
    }

    @Override
    public String toLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
        if (value == null) {
            return "null";
        }
        int length = Array.getLength(value);
        ArrayList<String> list = new ArrayList<String>(length);
        Type elemType = this.getElementType(factory);
        for (int i = 0; i < length; ++i) {
            Object element = Array.get(value, i);
            if (element == LazyPropertyInitializer.UNFETCHED_PROPERTY || !Hibernate.isInitialized(element)) {
                list.add("<uninitialized>");
                continue;
            }
            list.add(elemType.toLoggableString(element, factory));
        }
        return ((Object)list).toString();
    }

    @Override
    public Object instantiateResult(Object original) {
        return Array.newInstance(this.elementClass, Array.getLength(original));
    }

    @Override
    public Object replaceElements(Object original, Object target, Object owner, Map copyCache, SharedSessionContractImplementor session) throws HibernateException {
        int length = Array.getLength(original);
        if (length != Array.getLength(target)) {
            target = this.instantiateResult(original);
        }
        Type elemType = this.getElementType(session.getFactory());
        for (int i = 0; i < length; ++i) {
            Array.set(target, i, elemType.replace(Array.get(original, i), null, session, owner, copyCache));
        }
        return target;
    }

    @Override
    public Object instantiate(int anticipatedSize) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object indexOf(Object array, Object element) {
        int length = Array.getLength(array);
        for (int i = 0; i < length; ++i) {
            if (Array.get(array, i) != element) continue;
            return i;
        }
        return null;
    }

    @Override
    protected boolean initializeImmediately() {
        return true;
    }

    @Override
    public boolean hasHolder() {
        return true;
    }
}

