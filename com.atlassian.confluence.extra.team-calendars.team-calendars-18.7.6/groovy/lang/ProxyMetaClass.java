/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.AdaptingMetaClass;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.GroovySystem;
import groovy.lang.Interceptor;
import groovy.lang.MetaClass;
import groovy.lang.MetaClassImpl;
import groovy.lang.MetaClassRegistry;
import groovy.lang.PropertyAccessInterceptor;

public class ProxyMetaClass
extends MetaClassImpl
implements AdaptingMetaClass {
    protected MetaClass adaptee = null;
    protected Interceptor interceptor = null;

    public static ProxyMetaClass getInstance(Class theClass) {
        MetaClassRegistry metaRegistry = GroovySystem.getMetaClassRegistry();
        MetaClass meta = metaRegistry.getMetaClass(theClass);
        return new ProxyMetaClass(metaRegistry, theClass, meta);
    }

    public ProxyMetaClass(MetaClassRegistry registry, Class theClass, MetaClass adaptee) {
        super(registry, theClass);
        this.adaptee = adaptee;
        if (null == adaptee) {
            throw new IllegalArgumentException("adaptee must not be null");
        }
        super.initialize();
    }

    @Override
    public synchronized void initialize() {
        this.adaptee.initialize();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object use(Closure closure) {
        MetaClass origMetaClass = this.registry.getMetaClass(this.theClass);
        this.registry.setMetaClass(this.theClass, this);
        try {
            Object v = closure.call();
            return v;
        }
        finally {
            this.registry.setMetaClass(this.theClass, origMetaClass);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object use(GroovyObject object, Closure closure) {
        MetaClass origMetaClass = object.getMetaClass();
        object.setMetaClass(this);
        try {
            Object v = closure.call();
            return v;
        }
        finally {
            object.setMetaClass(origMetaClass);
        }
    }

    public Interceptor getInterceptor() {
        return this.interceptor;
    }

    public void setInterceptor(Interceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public Object invokeMethod(final Object object, final String methodName, final Object[] arguments) {
        return this.doCall(object, methodName, arguments, this.interceptor, new Callable(){

            @Override
            public Object call() {
                return ProxyMetaClass.this.adaptee.invokeMethod(object, methodName, arguments);
            }
        });
    }

    @Override
    public Object invokeStaticMethod(final Object object, final String methodName, final Object[] arguments) {
        return this.doCall(object, methodName, arguments, this.interceptor, new Callable(){

            @Override
            public Object call() {
                return ProxyMetaClass.this.adaptee.invokeStaticMethod(object, methodName, arguments);
            }
        });
    }

    @Override
    public Object invokeConstructor(final Object[] arguments) {
        return this.doCall(this.theClass, "ctor", arguments, this.interceptor, new Callable(){

            @Override
            public Object call() {
                return ProxyMetaClass.this.adaptee.invokeConstructor(arguments);
            }
        });
    }

    @Override
    public Object getProperty(Class aClass, Object object, String property, boolean b, boolean b1) {
        if (null == this.interceptor) {
            return super.getProperty(aClass, object, property, b, b1);
        }
        if (this.interceptor instanceof PropertyAccessInterceptor) {
            PropertyAccessInterceptor pae = (PropertyAccessInterceptor)this.interceptor;
            Object result = pae.beforeGet(object, property);
            if (this.interceptor.doInvoke()) {
                result = super.getProperty(aClass, object, property, b, b1);
            }
            return result;
        }
        return super.getProperty(aClass, object, property, b, b1);
    }

    @Override
    public void setProperty(Class aClass, Object object, String property, Object newValue, boolean b, boolean b1) {
        if (null == this.interceptor) {
            super.setProperty(aClass, object, property, newValue, b, b1);
        }
        if (this.interceptor instanceof PropertyAccessInterceptor) {
            PropertyAccessInterceptor pae = (PropertyAccessInterceptor)this.interceptor;
            pae.beforeSet(object, property, newValue);
            if (this.interceptor.doInvoke()) {
                super.setProperty(aClass, object, property, newValue, b, b1);
            }
        } else {
            super.setProperty(aClass, object, property, newValue, b, b1);
        }
    }

    @Override
    public MetaClass getAdaptee() {
        return this.adaptee;
    }

    @Override
    public void setAdaptee(MetaClass metaClass) {
        this.adaptee = metaClass;
    }

    private Object doCall(Object object, String methodName, Object[] arguments, Interceptor interceptor, Callable howToInvoke) {
        if (null == interceptor) {
            return howToInvoke.call();
        }
        Object result = interceptor.beforeInvoke(object, methodName, arguments);
        if (interceptor.doInvoke()) {
            result = howToInvoke.call();
        }
        result = interceptor.afterInvoke(object, methodName, arguments, result);
        return result;
    }

    private static interface Callable {
        public Object call();
    }
}

