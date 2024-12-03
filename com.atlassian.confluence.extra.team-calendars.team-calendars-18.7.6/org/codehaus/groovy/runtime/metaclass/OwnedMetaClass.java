/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.metaclass;

import groovy.lang.DelegatingMetaClass;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.MetaMethod;
import groovy.lang.MetaProperty;
import java.util.List;
import org.codehaus.groovy.ast.ClassNode;

public abstract class OwnedMetaClass
extends DelegatingMetaClass {
    public OwnedMetaClass(MetaClass delegate) {
        super(delegate);
    }

    @Override
    public Object getAttribute(Object object, String attribute) {
        Object owner = this.getOwner();
        MetaClass ownerMetaClass = this.getOwnerMetaClass(owner);
        return ownerMetaClass.getAttribute(owner, attribute);
    }

    protected abstract Object getOwner();

    @Override
    public ClassNode getClassNode() {
        Object owner = this.getOwner();
        MetaClass ownerMetaClass = this.getOwnerMetaClass(owner);
        return ownerMetaClass.getClassNode();
    }

    @Override
    public List<MetaMethod> getMetaMethods() {
        Object owner = this.getOwner();
        MetaClass ownerMetaClass = this.getOwnerMetaClass(owner);
        return ownerMetaClass.getMetaMethods();
    }

    @Override
    public List<MetaMethod> getMethods() {
        Object owner = this.getOwner();
        MetaClass ownerMetaClass = this.getOwnerMetaClass(owner);
        return ownerMetaClass.getMethods();
    }

    @Override
    public List<MetaMethod> respondsTo(Object obj, String name, Object[] argTypes) {
        Object owner = this.getOwner();
        MetaClass ownerMetaClass = this.getOwnerMetaClass(owner);
        return ownerMetaClass.respondsTo(owner, name, argTypes);
    }

    @Override
    public List<MetaMethod> respondsTo(Object obj, String name) {
        Object owner = this.getOwner();
        MetaClass ownerMetaClass = this.getOwnerMetaClass(owner);
        return ownerMetaClass.respondsTo(owner, name);
    }

    @Override
    public MetaProperty hasProperty(Object obj, String name) {
        Object owner = this.getOwner();
        MetaClass ownerMetaClass = this.getOwnerMetaClass(owner);
        return ownerMetaClass.hasProperty(owner, name);
    }

    @Override
    public List<MetaProperty> getProperties() {
        Object owner = this.getOwner();
        MetaClass ownerMetaClass = this.getOwnerMetaClass(owner);
        return ownerMetaClass.getProperties();
    }

    @Override
    public Object getProperty(Object object, String property) {
        Object owner = this.getOwner();
        MetaClass ownerMetaClass = this.getOwnerMetaClass(owner);
        return ownerMetaClass.getProperty(owner, property);
    }

    @Override
    public Object invokeConstructor(Object[] arguments) {
        Object owner = this.getOwner();
        MetaClass ownerMetaClass = this.getOwnerMetaClass(owner);
        return ownerMetaClass.invokeConstructor(arguments);
    }

    @Override
    public Object invokeMethod(Object object, String methodName, Object arguments) {
        Object owner = this.getOwner();
        MetaClass ownerMetaClass = this.getOwnerMetaClass(owner);
        return ownerMetaClass.invokeMethod(owner, methodName, arguments);
    }

    @Override
    public Object invokeMethod(Object object, String methodName, Object[] arguments) {
        Object owner = this.getOwner();
        MetaClass ownerMetaClass = this.getOwnerMetaClass(owner);
        return ownerMetaClass.invokeMethod(owner, methodName, arguments);
    }

    protected abstract MetaClass getOwnerMetaClass(Object var1);

    @Override
    public Object invokeStaticMethod(Object object, String methodName, Object[] arguments) {
        Object owner = this.getOwner();
        MetaClass ownerMetaClass = this.getOwnerMetaClass(owner);
        return ownerMetaClass.invokeStaticMethod(object, methodName, arguments);
    }

    @Override
    public void setAttribute(Object object, String attribute, Object newValue) {
        Object owner = this.getOwner();
        MetaClass ownerMetaClass = this.getOwnerMetaClass(owner);
        ownerMetaClass.setAttribute(object, attribute, newValue);
    }

    @Override
    public void setProperty(Object object, String property, Object newValue) {
        Object owner = this.getOwner();
        MetaClass ownerMetaClass = this.getOwnerMetaClass(owner);
        ownerMetaClass.setProperty(object, property, newValue);
    }

    @Override
    public boolean equals(Object obj) {
        return this.delegate.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.delegate.hashCode();
    }

    @Override
    public String toString() {
        return super.toString() + "[" + this.delegate.toString() + "]";
    }

    @Override
    public Object getAttribute(Class sender, Object receiver, String messageName, boolean useSuper) {
        Object owner = this.getOwner();
        MetaClass ownerMetaClass = this.getOwnerMetaClass(owner);
        return ownerMetaClass.getAttribute(sender, receiver, messageName, useSuper);
    }

    @Override
    public Object getProperty(Class sender, Object receiver, String messageName, boolean useSuper, boolean fromInsideClass) {
        Object owner = this.getOwner();
        MetaClass ownerMetaClass = this.getOwnerMetaClass(owner);
        return ownerMetaClass.getProperty(sender, receiver, messageName, useSuper, fromInsideClass);
    }

    @Override
    public MetaProperty getMetaProperty(String name) {
        Object owner = this.getOwner();
        MetaClass ownerMetaClass = this.getOwnerMetaClass(owner);
        return ownerMetaClass.getMetaProperty(name);
    }

    @Override
    public MetaMethod getStaticMetaMethod(String name, Object[] args) {
        Object owner = this.getOwner();
        MetaClass ownerMetaClass = this.getOwnerMetaClass(owner);
        return ownerMetaClass.getStaticMetaMethod(name, args);
    }

    @Override
    public MetaMethod getStaticMetaMethod(String name, Class[] argTypes) {
        Object owner = this.getOwner();
        MetaClass ownerMetaClass = this.getOwnerMetaClass(owner);
        return ownerMetaClass.getStaticMetaMethod(name, argTypes);
    }

    @Override
    public MetaMethod getMetaMethod(String name, Object[] args) {
        Object owner = this.getOwner();
        MetaClass ownerMetaClass = this.getOwnerMetaClass(owner);
        return ownerMetaClass.getMetaMethod(name, args);
    }

    public MetaMethod getMetaMethod(String name, Class[] argTypes) {
        Object owner = this.getOwner();
        MetaClass ownerMetaClass = this.getOwnerMetaClass(owner);
        return ownerMetaClass.getMetaMethod(name, argTypes);
    }

    @Override
    public Class getTheClass() {
        Object owner = this.getOwner();
        MetaClass ownerMetaClass = this.getOwnerMetaClass(owner);
        return ownerMetaClass.getTheClass();
    }

    @Override
    public Object invokeMethod(Class sender, Object receiver, String methodName, Object[] arguments, boolean isCallToSuper, boolean fromInsideClass) {
        Object owner = this.getOwner();
        MetaClass ownerMetaClass = this.getOwnerMetaClass(owner);
        return ownerMetaClass.invokeMethod(sender, owner, methodName, arguments, isCallToSuper, fromInsideClass);
    }

    @Override
    public Object invokeMissingMethod(Object instance, String methodName, Object[] arguments) {
        Object owner = this.getOwner();
        MetaClass ownerMetaClass = this.getOwnerMetaClass(owner);
        return ownerMetaClass.invokeMissingMethod(owner, methodName, arguments);
    }

    @Override
    public Object invokeMissingProperty(Object instance, String propertyName, Object optionalValue, boolean isGetter) {
        Object owner = this.getOwner();
        MetaClass ownerMetaClass = this.getOwnerMetaClass(owner);
        return ownerMetaClass.invokeMissingProperty(owner, propertyName, optionalValue, isGetter);
    }

    @Override
    public boolean isGroovyObject() {
        Object owner = this.getOwner();
        MetaClass ownerMetaClass = this.getOwnerMetaClass(owner);
        return GroovyObject.class.isAssignableFrom(ownerMetaClass.getTheClass());
    }

    @Override
    public void setAttribute(Class sender, Object receiver, String messageName, Object messageValue, boolean useSuper, boolean fromInsideClass) {
        Object owner = this.getOwner();
        MetaClass ownerMetaClass = this.getOwnerMetaClass(owner);
        ownerMetaClass.setAttribute(sender, owner, messageName, messageValue, useSuper, fromInsideClass);
    }

    @Override
    public void setProperty(Class sender, Object receiver, String messageName, Object messageValue, boolean useSuper, boolean fromInsideClass) {
        Object owner = this.getOwner();
        MetaClass ownerMetaClass = this.getOwnerMetaClass(owner);
        ownerMetaClass.setProperty(sender, owner, messageName, messageValue, useSuper, fromInsideClass);
    }

    @Override
    public int selectConstructorAndTransformArguments(int numberOfConstructors, Object[] arguments) {
        Object owner = this.getOwner();
        MetaClass ownerMetaClass = this.getOwnerMetaClass(owner);
        return ownerMetaClass.selectConstructorAndTransformArguments(numberOfConstructors, arguments);
    }
}

