/*
 * Decompiled with CFR 0.152.
 */
package groovy.mock.interceptor;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.GroovySystem;
import groovy.lang.Interceptor;
import groovy.lang.MetaClass;
import groovy.lang.MetaClassRegistry;
import groovy.lang.PropertyAccessInterceptor;
import groovy.lang.ProxyMetaClass;

public class MockProxyMetaClass
extends ProxyMetaClass {
    public final boolean interceptConstruction;
    private boolean fallingThrough;
    static final FallThroughMarker FALL_THROUGH_MARKER = new FallThroughMarker(new Object());

    public MockProxyMetaClass(MetaClassRegistry registry, Class theClass, MetaClass adaptee) {
        this(registry, theClass, adaptee, false);
    }

    public MockProxyMetaClass(MetaClassRegistry registry, Class theClass, MetaClass adaptee, boolean interceptConstruction) {
        super(registry, theClass, adaptee);
        this.interceptConstruction = interceptConstruction;
    }

    public static MockProxyMetaClass make(Class theClass) {
        return MockProxyMetaClass.make(theClass, false);
    }

    public static MockProxyMetaClass make(Class theClass, boolean interceptConstruction) {
        MetaClassRegistry metaRegistry = GroovySystem.getMetaClassRegistry();
        MetaClass meta = metaRegistry.getMetaClass(theClass);
        return new MockProxyMetaClass(metaRegistry, theClass, meta, interceptConstruction);
    }

    @Override
    public Object invokeMethod(Object object, String methodName, Object[] arguments) {
        if (null == this.interceptor && !this.fallingThrough) {
            throw new RuntimeException("cannot invoke method '" + methodName + "' without interceptor");
        }
        Object result = FALL_THROUGH_MARKER;
        if (this.interceptor != null) {
            result = this.interceptor.beforeInvoke(object, methodName, arguments);
        }
        if (result == FALL_THROUGH_MARKER) {
            Interceptor saved = this.interceptor;
            this.interceptor = null;
            boolean savedFallingThrough = this.fallingThrough;
            this.fallingThrough = true;
            result = this.adaptee.invokeMethod(object, methodName, arguments);
            this.fallingThrough = savedFallingThrough;
            this.interceptor = saved;
        }
        return result;
    }

    @Override
    public Object invokeStaticMethod(Object object, String methodName, Object[] arguments) {
        if (null == this.interceptor && !this.fallingThrough) {
            throw new RuntimeException("cannot invoke static method '" + methodName + "' without interceptor");
        }
        Object result = FALL_THROUGH_MARKER;
        if (this.interceptor != null) {
            result = this.interceptor.beforeInvoke(object, methodName, arguments);
        }
        if (result == FALL_THROUGH_MARKER) {
            Interceptor saved = this.interceptor;
            this.interceptor = null;
            boolean savedFallingThrough = this.fallingThrough;
            this.fallingThrough = true;
            result = this.adaptee.invokeStaticMethod(object, methodName, arguments);
            this.fallingThrough = savedFallingThrough;
            this.interceptor = saved;
        }
        return result;
    }

    @Override
    public Object getProperty(Class aClass, Object object, String property, boolean b, boolean b1) {
        if (null == this.interceptor && !this.fallingThrough) {
            throw new RuntimeException("cannot get property '" + property + "' without interceptor");
        }
        Object result = FALL_THROUGH_MARKER;
        if (this.interceptor != null && this.interceptor instanceof PropertyAccessInterceptor) {
            result = ((PropertyAccessInterceptor)this.interceptor).beforeGet(object, property);
        }
        if (result == FALL_THROUGH_MARKER) {
            Interceptor saved = this.interceptor;
            this.interceptor = null;
            boolean savedFallingThrough = this.fallingThrough;
            this.fallingThrough = true;
            result = this.adaptee.getProperty(aClass, object, property, b, b1);
            this.fallingThrough = savedFallingThrough;
            this.interceptor = saved;
        }
        return result;
    }

    @Override
    public void setProperty(Class aClass, Object object, String property, Object newValue, boolean b, boolean b1) {
        if (null == this.interceptor && !this.fallingThrough) {
            throw new RuntimeException("cannot set property '" + property + "' without interceptor");
        }
        Object result = FALL_THROUGH_MARKER;
        if (this.interceptor != null && this.interceptor instanceof PropertyAccessInterceptor) {
            Object[] resultHolder = new Object[1];
            ((PropertyAccessInterceptor)this.interceptor).beforeSet(resultHolder, property, newValue);
            result = resultHolder[0];
        }
        if (result == FALL_THROUGH_MARKER) {
            Interceptor saved = this.interceptor;
            this.interceptor = null;
            boolean savedFallingThrough = this.fallingThrough;
            this.fallingThrough = true;
            this.adaptee.setProperty(aClass, object, property, newValue, b, b1);
            this.fallingThrough = savedFallingThrough;
            this.interceptor = saved;
        }
    }

    @Override
    public Object invokeConstructor(Object[] arguments) {
        if (this.interceptConstruction && null == this.interceptor) {
            throw new RuntimeException("cannot invoke constructor without interceptor");
        }
        if (this.interceptConstruction) {
            GroovyObject newInstance = (GroovyObject)this.interceptor.beforeInvoke(null, this.getTheClass().getSimpleName(), arguments);
            newInstance.setMetaClass(this);
            return newInstance;
        }
        return this.adaptee.invokeConstructor(arguments);
    }

    static class FallThroughMarker
    extends Closure {
        public FallThroughMarker(Object owner) {
            super(owner);
        }
    }
}

