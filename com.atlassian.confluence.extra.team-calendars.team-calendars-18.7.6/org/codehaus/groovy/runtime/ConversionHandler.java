/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.GroovyObject;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.metaclass.MetaClassRegistryImpl;
import org.codehaus.groovy.vmplugin.VMPlugin;
import org.codehaus.groovy.vmplugin.VMPluginFactory;

public abstract class ConversionHandler
implements InvocationHandler,
Serializable {
    private final Object delegate;
    private static final long serialVersionUID = 1162833717190835227L;
    private final ConcurrentHashMap<Method, Object> handleCache = VMPluginFactory.getPlugin().getVersion() >= 7 ? new ConcurrentHashMap(16, 0.9f, 2) : null;
    private MetaClass metaClass;

    public ConversionHandler(Object delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate must not be null");
        }
        this.delegate = delegate;
    }

    public Object getDelegate() {
        return this.delegate;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (this.handleCache != null && this.isDefaultMethod(method)) {
            VMPlugin plugin = VMPluginFactory.getPlugin();
            Object handle = this.handleCache.get(method);
            if (handle == null) {
                handle = plugin.getInvokeSpecialHandle(method, proxy);
                this.handleCache.put(method, handle);
            }
            return plugin.invokeHandle(handle, args);
        }
        if (!this.checkMethod(method)) {
            try {
                if (method.getDeclaringClass() == GroovyObject.class) {
                    if ("getMetaClass".equals(method.getName())) {
                        return this.getMetaClass(proxy);
                    }
                    if ("setMetaClass".equals(method.getName())) {
                        return this.setMetaClass((MetaClass)args[0]);
                    }
                }
                return this.invokeCustom(proxy, method, args);
            }
            catch (GroovyRuntimeException gre) {
                throw ScriptBytecodeAdapter.unwrap(gre);
            }
        }
        try {
            return method.invoke((Object)this, args);
        }
        catch (InvocationTargetException ite) {
            throw ite.getTargetException();
        }
    }

    protected boolean isDefaultMethod(Method method) {
        return (method.getModifiers() & 0x409) == 1 && method.getDeclaringClass().isInterface();
    }

    protected boolean checkMethod(Method method) {
        return ConversionHandler.isCoreObjectMethod(method);
    }

    public abstract Object invokeCustom(Object var1, Method var2, Object[] var3) throws Throwable;

    public boolean equals(Object obj) {
        if (obj instanceof Proxy) {
            obj = Proxy.getInvocationHandler(obj);
        }
        if (obj instanceof ConversionHandler) {
            return ((ConversionHandler)obj).getDelegate().equals(this.delegate);
        }
        return false;
    }

    public int hashCode() {
        return this.delegate.hashCode();
    }

    public String toString() {
        return this.delegate.toString();
    }

    public static boolean isCoreObjectMethod(Method method) {
        return Object.class.equals(method.getDeclaringClass());
    }

    private MetaClass setMetaClass(MetaClass mc) {
        this.metaClass = mc;
        return mc;
    }

    private MetaClass getMetaClass(Object proxy) {
        MetaClass mc = this.metaClass;
        if (mc == null) {
            this.metaClass = mc = ((MetaClassRegistryImpl)GroovySystem.getMetaClassRegistry()).getMetaClass(proxy);
        }
        return mc;
    }
}

