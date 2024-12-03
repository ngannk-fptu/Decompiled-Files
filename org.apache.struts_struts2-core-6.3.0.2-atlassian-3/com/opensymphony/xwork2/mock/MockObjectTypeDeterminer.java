/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ognl.OgnlException
 *  ognl.OgnlRuntime
 */
package com.opensymphony.xwork2.mock;

import com.opensymphony.xwork2.conversion.ObjectTypeDeterminer;
import java.util.Map;
import ognl.OgnlException;
import ognl.OgnlRuntime;

public class MockObjectTypeDeterminer
implements ObjectTypeDeterminer {
    private Class keyClass;
    private Class elementClass;
    private String keyProperty;
    private boolean shouldCreateIfNew;

    public MockObjectTypeDeterminer() {
    }

    public MockObjectTypeDeterminer(Class keyClass, Class elementClass, String keyProperty, boolean shouldCreateIfNew) {
        this.keyClass = keyClass;
        this.elementClass = elementClass;
        this.keyProperty = keyProperty;
        this.shouldCreateIfNew = shouldCreateIfNew;
    }

    @Override
    public Class getKeyClass(Class parentClass, String property) {
        return this.getKeyClass();
    }

    @Override
    public Class getElementClass(Class parentClass, String property, Object key) {
        return this.getElementClass();
    }

    @Override
    public String getKeyProperty(Class parentClass, String property) {
        return this.getKeyProperty();
    }

    @Override
    public boolean shouldCreateIfNew(Class parentClass, String property, Object target, String keyProperty, boolean isIndexAccessed) {
        try {
            System.out.println("ognl:" + OgnlRuntime.getPropertyAccessor(Map.class) + " this:" + this);
        }
        catch (OgnlException e) {
            e.printStackTrace();
        }
        return this.isShouldCreateIfNew();
    }

    public Class getElementClass() {
        return this.elementClass;
    }

    public void setElementClass(Class elementClass) {
        this.elementClass = elementClass;
    }

    public Class getKeyClass() {
        return this.keyClass;
    }

    public void setKeyClass(Class keyClass) {
        this.keyClass = keyClass;
    }

    public String getKeyProperty() {
        return this.keyProperty;
    }

    public void setKeyProperty(String keyProperty) {
        this.keyProperty = keyProperty;
    }

    public boolean isShouldCreateIfNew() {
        return this.shouldCreateIfNew;
    }

    public void setShouldCreateIfNew(boolean shouldCreateIfNew) {
        this.shouldCreateIfNew = shouldCreateIfNew;
    }
}

