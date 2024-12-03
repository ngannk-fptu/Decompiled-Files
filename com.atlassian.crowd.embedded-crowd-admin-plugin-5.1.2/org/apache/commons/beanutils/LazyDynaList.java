/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.beanutils.LazyDynaClass;
import org.apache.commons.beanutils.LazyDynaMap;
import org.apache.commons.beanutils.WrapDynaBean;
import org.apache.commons.beanutils.WrapDynaClass;

public class LazyDynaList
extends ArrayList<Object> {
    private DynaClass elementDynaClass;
    private transient WrapDynaClass wrapDynaClass;
    private Class<?> elementType;
    private Class<?> elementDynaBeanType;

    public LazyDynaList() {
    }

    public LazyDynaList(int capacity) {
        super(capacity);
    }

    public LazyDynaList(DynaClass elementDynaClass) {
        this.setElementDynaClass(elementDynaClass);
    }

    public LazyDynaList(Class<?> elementType) {
        this.setElementType(elementType);
    }

    public LazyDynaList(Collection<?> collection) {
        super(collection.size());
        this.addAll(collection);
    }

    public LazyDynaList(Object[] array) {
        super(array.length);
        for (Object element : array) {
            this.add(element);
        }
    }

    @Override
    public void add(int index, Object element) {
        DynaBean dynaBean = this.transform(element);
        this.growList(index);
        super.add(index, dynaBean);
    }

    @Override
    public boolean add(Object element) {
        DynaBean dynaBean = this.transform(element);
        return super.add(dynaBean);
    }

    @Override
    public boolean addAll(Collection<?> collection) {
        if (collection == null || collection.size() == 0) {
            return false;
        }
        this.ensureCapacity(this.size() + collection.size());
        for (Object e : collection) {
            this.add(e);
        }
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<?> collection) {
        if (collection == null || collection.size() == 0) {
            return false;
        }
        this.ensureCapacity((index > this.size() ? index : this.size()) + collection.size());
        if (this.size() == 0) {
            this.transform(collection.iterator().next());
        }
        this.growList(index);
        int currentIndex = index;
        for (Object e : collection) {
            this.add(currentIndex++, e);
        }
        return true;
    }

    @Override
    public Object get(int index) {
        this.growList(index + 1);
        return super.get(index);
    }

    @Override
    public Object set(int index, Object element) {
        DynaBean dynaBean = this.transform(element);
        this.growList(index + 1);
        return super.set(index, dynaBean);
    }

    @Override
    public Object[] toArray() {
        if (this.size() == 0 && this.elementType == null) {
            return new LazyDynaBean[0];
        }
        Object[] array = (Object[])Array.newInstance(this.elementType, this.size());
        for (int i = 0; i < this.size(); ++i) {
            array[i] = Map.class.isAssignableFrom(this.elementType) ? ((LazyDynaMap)this.get(i)).getMap() : (DynaBean.class.isAssignableFrom(this.elementType) ? this.get(i) : ((WrapDynaBean)this.get(i)).getInstance());
        }
        return array;
    }

    @Override
    public <T> T[] toArray(T[] model) {
        Class<?> arrayType = model.getClass().getComponentType();
        if (DynaBean.class.isAssignableFrom(arrayType) || this.size() == 0 && this.elementType == null) {
            return super.toArray(model);
        }
        if (arrayType.isAssignableFrom(this.elementType)) {
            Object[] array;
            if (model.length >= this.size()) {
                array = model;
            } else {
                Object[] tempArray = (Object[])Array.newInstance(arrayType, this.size());
                array = tempArray;
            }
            for (int i = 0; i < this.size(); ++i) {
                Object elem = Map.class.isAssignableFrom(this.elementType) ? ((LazyDynaMap)this.get(i)).getMap() : (DynaBean.class.isAssignableFrom(this.elementType) ? this.get(i) : ((WrapDynaBean)this.get(i)).getInstance());
                Array.set(array, i, elem);
            }
            return array;
        }
        throw new IllegalArgumentException("Invalid array type: " + arrayType.getName() + " - not compatible with '" + this.elementType.getName());
    }

    public DynaBean[] toDynaBeanArray() {
        if (this.size() == 0 && this.elementDynaBeanType == null) {
            return new LazyDynaBean[0];
        }
        DynaBean[] array = (DynaBean[])Array.newInstance(this.elementDynaBeanType, this.size());
        for (int i = 0; i < this.size(); ++i) {
            array[i] = (DynaBean)this.get(i);
        }
        return array;
    }

    public void setElementType(Class<?> elementType) {
        boolean changeType;
        if (elementType == null) {
            throw new IllegalArgumentException("Element Type is missing");
        }
        boolean bl = changeType = this.elementType != null && !this.elementType.equals(elementType);
        if (changeType && this.size() > 0) {
            throw new IllegalStateException("Element Type cannot be reset");
        }
        this.elementType = elementType;
        Object object = null;
        try {
            object = elementType.newInstance();
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Error creating type: " + elementType.getName() + " - " + e);
        }
        DynaBean dynaBean = null;
        if (Map.class.isAssignableFrom(elementType)) {
            dynaBean = this.createDynaBeanForMapProperty(object);
            this.elementDynaClass = dynaBean.getDynaClass();
        } else if (DynaBean.class.isAssignableFrom(elementType)) {
            dynaBean = object;
            this.elementDynaClass = dynaBean.getDynaClass();
        } else {
            dynaBean = new WrapDynaBean(object);
            this.wrapDynaClass = (WrapDynaClass)dynaBean.getDynaClass();
        }
        this.elementDynaBeanType = dynaBean.getClass();
        if (WrapDynaBean.class.isAssignableFrom(this.elementDynaBeanType)) {
            this.elementType = ((WrapDynaBean)dynaBean).getInstance().getClass();
        } else if (LazyDynaMap.class.isAssignableFrom(this.elementDynaBeanType)) {
            this.elementType = ((LazyDynaMap)dynaBean).getMap().getClass();
        }
    }

    public void setElementDynaClass(DynaClass elementDynaClass) {
        if (elementDynaClass == null) {
            throw new IllegalArgumentException("Element DynaClass is missing");
        }
        if (this.size() > 0) {
            throw new IllegalStateException("Element DynaClass cannot be reset");
        }
        try {
            DynaBean dynaBean = elementDynaClass.newInstance();
            this.elementDynaBeanType = dynaBean.getClass();
            if (WrapDynaBean.class.isAssignableFrom(this.elementDynaBeanType)) {
                this.elementType = ((WrapDynaBean)dynaBean).getInstance().getClass();
                this.wrapDynaClass = (WrapDynaClass)elementDynaClass;
            } else if (LazyDynaMap.class.isAssignableFrom(this.elementDynaBeanType)) {
                this.elementType = ((LazyDynaMap)dynaBean).getMap().getClass();
                this.elementDynaClass = elementDynaClass;
            } else {
                this.elementType = dynaBean.getClass();
                this.elementDynaClass = elementDynaClass;
            }
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Error creating DynaBean from " + elementDynaClass.getClass().getName() + " - " + e);
        }
    }

    private void growList(int requiredSize) {
        if (requiredSize < this.size()) {
            return;
        }
        this.ensureCapacity(requiredSize + 1);
        for (int i = this.size(); i < requiredSize; ++i) {
            DynaBean dynaBean = this.transform(null);
            super.add(dynaBean);
        }
    }

    private DynaBean transform(Object element) {
        DynaBean dynaBean = null;
        Class<?> newDynaBeanType = null;
        Class<?> newElementType = null;
        if (element == null) {
            if (this.elementType == null) {
                this.setElementDynaClass(new LazyDynaClass());
            }
            if (this.getDynaClass() == null) {
                this.setElementType(this.elementType);
            }
            try {
                dynaBean = this.getDynaClass().newInstance();
                newDynaBeanType = dynaBean.getClass();
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Error creating DynaBean: " + this.getDynaClass().getClass().getName() + " - " + e);
            }
        } else {
            newElementType = element.getClass();
            dynaBean = Map.class.isAssignableFrom(element.getClass()) ? this.createDynaBeanForMapProperty(element) : (DynaBean.class.isAssignableFrom(element.getClass()) ? (DynaBean)element : new WrapDynaBean(element));
            newDynaBeanType = dynaBean.getClass();
        }
        newElementType = dynaBean.getClass();
        if (WrapDynaBean.class.isAssignableFrom(newDynaBeanType)) {
            newElementType = ((WrapDynaBean)dynaBean).getInstance().getClass();
        } else if (LazyDynaMap.class.isAssignableFrom(newDynaBeanType)) {
            newElementType = ((LazyDynaMap)dynaBean).getMap().getClass();
        }
        if (this.elementType != null && !newElementType.equals(this.elementType)) {
            throw new IllegalArgumentException("Element Type " + newElementType + " doesn't match other elements " + this.elementType);
        }
        return dynaBean;
    }

    private LazyDynaMap createDynaBeanForMapProperty(Object value) {
        Map valueMap = (Map)value;
        return new LazyDynaMap(valueMap);
    }

    private DynaClass getDynaClass() {
        return this.elementDynaClass == null ? this.wrapDynaClass : this.elementDynaClass;
    }
}

