/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.GroovyObject;
import groovy.lang.GroovySystem;
import groovy.lang.MetaBeanProperty;
import groovy.lang.MetaClass;
import groovy.lang.MetaMethod;
import groovy.lang.MetaProperty;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;
import groovy.lang.MutableMetaClass;
import java.lang.reflect.Method;
import java.util.List;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.runtime.InvokerHelper;

public class DelegatingMetaClass
implements MetaClass,
MutableMetaClass,
GroovyObject {
    protected MetaClass delegate;

    public DelegatingMetaClass(MetaClass delegate) {
        this.delegate = delegate;
    }

    public DelegatingMetaClass(Class theClass) {
        this(GroovySystem.getMetaClassRegistry().getMetaClass(theClass));
    }

    @Override
    public boolean isModified() {
        return this.delegate instanceof MutableMetaClass && ((MutableMetaClass)this.delegate).isModified();
    }

    @Override
    public void addNewInstanceMethod(Method method) {
        if (this.delegate instanceof MutableMetaClass) {
            ((MutableMetaClass)this.delegate).addNewInstanceMethod(method);
        }
    }

    @Override
    public void addNewStaticMethod(Method method) {
        if (this.delegate instanceof MutableMetaClass) {
            ((MutableMetaClass)this.delegate).addNewStaticMethod(method);
        }
    }

    @Override
    public void addMetaMethod(MetaMethod metaMethod) {
        if (this.delegate instanceof MutableMetaClass) {
            ((MutableMetaClass)this.delegate).addMetaMethod(metaMethod);
        }
    }

    @Override
    public void addMetaBeanProperty(MetaBeanProperty metaBeanProperty) {
        if (this.delegate instanceof MutableMetaClass) {
            ((MutableMetaClass)this.delegate).addMetaBeanProperty(metaBeanProperty);
        }
    }

    @Override
    public void initialize() {
        this.delegate.initialize();
    }

    @Override
    public Object getAttribute(Object object, String attribute) {
        return this.delegate.getAttribute(object, attribute);
    }

    @Override
    public ClassNode getClassNode() {
        return this.delegate.getClassNode();
    }

    @Override
    public List<MetaMethod> getMetaMethods() {
        return this.delegate.getMetaMethods();
    }

    @Override
    public List<MetaMethod> getMethods() {
        return this.delegate.getMethods();
    }

    @Override
    public List<MetaMethod> respondsTo(Object obj, String name, Object[] argTypes) {
        return this.delegate.respondsTo(obj, name, argTypes);
    }

    @Override
    public List<MetaMethod> respondsTo(Object obj, String name) {
        return this.delegate.respondsTo(obj, name);
    }

    @Override
    public MetaProperty hasProperty(Object obj, String name) {
        return this.delegate.hasProperty(obj, name);
    }

    @Override
    public List<MetaProperty> getProperties() {
        return this.delegate.getProperties();
    }

    @Override
    public Object getProperty(Object object, String property) {
        return this.delegate.getProperty(object, property);
    }

    @Override
    public Object invokeConstructor(Object[] arguments) {
        return this.delegate.invokeConstructor(arguments);
    }

    @Override
    public Object invokeMethod(Object object, String methodName, Object arguments) {
        return this.delegate.invokeMethod(object, methodName, arguments);
    }

    @Override
    public Object invokeMethod(Object object, String methodName, Object[] arguments) {
        return this.delegate.invokeMethod(object, methodName, arguments);
    }

    @Override
    public Object invokeStaticMethod(Object object, String methodName, Object[] arguments) {
        return this.delegate.invokeStaticMethod(object, methodName, arguments);
    }

    @Override
    public void setAttribute(Object object, String attribute, Object newValue) {
        this.delegate.setAttribute(object, attribute, newValue);
    }

    @Override
    public void setProperty(Object object, String property, Object newValue) {
        this.delegate.setProperty(object, property, newValue);
    }

    public boolean equals(Object obj) {
        return this.delegate.equals(obj);
    }

    public int hashCode() {
        return this.delegate.hashCode();
    }

    public String toString() {
        return super.toString() + "[" + this.delegate.toString() + "]";
    }

    @Override
    @Deprecated
    public MetaMethod pickMethod(String methodName, Class[] arguments) {
        return this.delegate.pickMethod(methodName, arguments);
    }

    @Override
    public Object getAttribute(Class sender, Object receiver, String messageName, boolean useSuper) {
        return this.delegate.getAttribute(sender, receiver, messageName, useSuper);
    }

    @Override
    public Object getProperty(Class sender, Object receiver, String messageName, boolean useSuper, boolean fromInsideClass) {
        return this.delegate.getProperty(sender, receiver, messageName, useSuper, fromInsideClass);
    }

    @Override
    public MetaProperty getMetaProperty(String name) {
        return this.delegate.getMetaProperty(name);
    }

    @Override
    public MetaMethod getStaticMetaMethod(String name, Object[] args) {
        return this.delegate.getStaticMetaMethod(name, args);
    }

    public MetaMethod getStaticMetaMethod(String name, Class[] argTypes) {
        return this.delegate.getStaticMetaMethod(name, argTypes);
    }

    @Override
    public MetaMethod getMetaMethod(String name, Object[] args) {
        return this.delegate.getMetaMethod(name, args);
    }

    @Override
    public Class getTheClass() {
        return this.delegate.getTheClass();
    }

    @Override
    public Object invokeMethod(Class sender, Object receiver, String methodName, Object[] arguments, boolean isCallToSuper, boolean fromInsideClass) {
        return this.delegate.invokeMethod(sender, receiver, methodName, arguments, isCallToSuper, fromInsideClass);
    }

    @Override
    public Object invokeMissingMethod(Object instance, String methodName, Object[] arguments) {
        return this.delegate.invokeMissingMethod(instance, methodName, arguments);
    }

    @Override
    public Object invokeMissingProperty(Object instance, String propertyName, Object optionalValue, boolean isGetter) {
        return this.delegate.invokeMissingProperty(instance, propertyName, optionalValue, isGetter);
    }

    public boolean isGroovyObject() {
        return GroovyObject.class.isAssignableFrom(this.delegate.getTheClass());
    }

    @Override
    public void setAttribute(Class sender, Object receiver, String messageName, Object messageValue, boolean useSuper, boolean fromInsideClass) {
        this.delegate.setAttribute(sender, receiver, messageName, messageValue, useSuper, fromInsideClass);
    }

    @Override
    public void setProperty(Class sender, Object receiver, String messageName, Object messageValue, boolean useSuper, boolean fromInsideClass) {
        this.delegate.setProperty(sender, receiver, messageName, messageValue, useSuper, fromInsideClass);
    }

    @Override
    public int selectConstructorAndTransformArguments(int numberOfConstructors, Object[] arguments) {
        return this.delegate.selectConstructorAndTransformArguments(numberOfConstructors, arguments);
    }

    public void setAdaptee(MetaClass adaptee) {
        this.delegate = adaptee;
    }

    public MetaClass getAdaptee() {
        return this.delegate;
    }

    @Override
    public Object invokeMethod(String name, Object args) {
        try {
            return this.getMetaClass().invokeMethod((Object)this, name, args);
        }
        catch (MissingMethodException e) {
            if (this.delegate instanceof GroovyObject) {
                return ((GroovyObject)((Object)this.delegate)).invokeMethod(name, args);
            }
            throw e;
        }
    }

    @Override
    public Object getProperty(String property) {
        try {
            return this.getMetaClass().getProperty(this, property);
        }
        catch (MissingPropertyException e) {
            if (this.delegate instanceof GroovyObject) {
                return ((GroovyObject)((Object)this.delegate)).getProperty(property);
            }
            throw e;
        }
    }

    @Override
    public void setProperty(String property, Object newValue) {
        try {
            this.getMetaClass().setProperty(this, property, newValue);
        }
        catch (MissingPropertyException e) {
            if (this.delegate instanceof GroovyObject) {
                ((GroovyObject)((Object)this.delegate)).setProperty(property, newValue);
            }
            throw e;
        }
    }

    @Override
    public MetaClass getMetaClass() {
        return InvokerHelper.getMetaClass(this.getClass());
    }

    @Override
    public void setMetaClass(MetaClass metaClass) {
        throw new UnsupportedOperationException();
    }
}

