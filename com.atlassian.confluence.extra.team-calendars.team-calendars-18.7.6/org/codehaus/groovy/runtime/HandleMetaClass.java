/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.DelegatingMetaClass;
import groovy.lang.ExpandoMetaClass;
import groovy.lang.GroovyObject;
import groovy.lang.MetaBeanProperty;
import groovy.lang.MetaClass;
import groovy.lang.MetaMethod;
import java.lang.reflect.Method;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.MetaClassHelper;

public class HandleMetaClass
extends DelegatingMetaClass {
    private Object object;
    private static final Object NONE = new Object();

    public HandleMetaClass(MetaClass mc) {
        this(mc, null);
    }

    public HandleMetaClass(MetaClass mc, Object obj) {
        super(mc);
        if (obj != null) {
            this.object = InvokerHelper.getMetaClass(obj.getClass()) == mc || !(mc instanceof ExpandoMetaClass) ? obj : NONE;
        }
    }

    @Override
    public void initialize() {
        this.replaceDelegate();
        this.delegate.initialize();
    }

    public GroovyObject replaceDelegate() {
        if (this.object == null) {
            if (!(this.delegate instanceof ExpandoMetaClass)) {
                this.delegate = new ExpandoMetaClass(this.delegate.getTheClass(), true, true);
                this.delegate.initialize();
            }
            DefaultGroovyMethods.setMetaClass(this.delegate.getTheClass(), this.delegate);
        } else if (this.object != NONE) {
            MetaClass metaClass = this.delegate;
            this.delegate = new ExpandoMetaClass(this.delegate.getTheClass(), false, true);
            if (metaClass instanceof ExpandoMetaClass) {
                ExpandoMetaClass emc = (ExpandoMetaClass)metaClass;
                for (MetaMethod method : emc.getExpandoMethods()) {
                    ((ExpandoMetaClass)this.delegate).registerInstanceMethod(method);
                }
            }
            this.delegate.initialize();
            MetaClassHelper.doSetMetaClass(this.object, this.delegate);
            this.object = NONE;
        }
        return (GroovyObject)((Object)this.delegate);
    }

    @Override
    public Object invokeMethod(String name, Object args) {
        return this.replaceDelegate().invokeMethod(name, args);
    }

    @Override
    public Object getProperty(String property) {
        if (ExpandoMetaClass.isValidExpandoProperty(property) && (property.equals("static") || property.equals("constructor") || Holder.META_CLASS.hasProperty(this, property) == null)) {
            return this.replaceDelegate().getProperty(property);
        }
        return Holder.META_CLASS.getProperty(this, property);
    }

    @Override
    public void setProperty(String property, Object newValue) {
        this.replaceDelegate().setProperty(property, newValue);
    }

    @Override
    public void addNewInstanceMethod(Method method) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addNewStaticMethod(Method method) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addMetaMethod(MetaMethod metaMethod) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addMetaBeanProperty(MetaBeanProperty metaBeanProperty) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) || this.getAdaptee().equals(obj) || obj instanceof HandleMetaClass && this.equals(((HandleMetaClass)obj).getAdaptee());
    }

    private static class Holder {
        static final MetaClass META_CLASS = InvokerHelper.getMetaClass(HandleMetaClass.class);

        private Holder() {
        }
    }
}

