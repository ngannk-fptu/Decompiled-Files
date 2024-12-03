/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.vmplugin.v7;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.MetaClass;
import groovy.lang.MetaMethod;
import groovy.lang.MetaObjectProtocol;
import groovy.lang.MetaProperty;
import groovy.lang.MissingMethodException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.reflection.stdclasses.CachedSAMClass;
import org.codehaus.groovy.runtime.GroovyCategorySupport;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.metaclass.MissingMethodExecutionFailed;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.GroovyCastException;
import org.codehaus.groovy.runtime.wrappers.Wrapper;
import org.codehaus.groovy.vmplugin.v7.IndyInterface;

public class IndyGuardsFiltersAndSignatures {
    private static final MethodType ZERO_GUARD = MethodType.methodType(Boolean.TYPE);
    private static final MethodType OBJECT_GUARD = MethodType.methodType(Boolean.TYPE, Object.class);
    private static final MethodType CLASS1_GUARD = MethodType.methodType(Boolean.TYPE, Class.class, Object.class);
    private static final MethodType METACLASS1_GUARD = MethodType.methodType(Boolean.TYPE, MetaClass.class, Object.class);
    private static final MethodType GRE_GUARD = MethodType.methodType(Object.class, GroovyRuntimeException.class);
    private static final MethodType OBJECT_FILTER = MethodType.methodType(Object.class, Object.class);
    private static final MethodType BOUND_INVOKER = MethodType.methodType(Object.class, Object[].class);
    private static final MethodType ANO_INVOKER = MethodType.methodType(Object.class, Object.class, Object[].class);
    private static final MethodType INVOKER = MethodType.methodType(Object.class, Object.class, String.class, Object[].class);
    private static final MethodType GET_INVOKER = MethodType.methodType(Object.class, String.class);
    protected static final MethodHandle SAME_CLASS;
    protected static final MethodHandle UNWRAP_METHOD;
    protected static final MethodHandle SAME_MC;
    protected static final MethodHandle IS_NULL;
    protected static final MethodHandle UNWRAP_EXCEPTION;
    protected static final MethodHandle META_METHOD_INVOKER;
    protected static final MethodHandle GROOVY_OBJECT_INVOKER;
    protected static final MethodHandle GROOVY_OBJECT_GET_PROPERTY;
    protected static final MethodHandle HAS_CATEGORY_IN_CURRENT_THREAD_GUARD;
    protected static final MethodHandle BEAN_CONSTRUCTOR_PROPERTY_SETTER;
    protected static final MethodHandle META_PROPERTY_GETTER;
    protected static final MethodHandle SLOW_META_CLASS_FIND;
    protected static final MethodHandle META_CLASS_INVOKE_STATIC_METHOD;
    protected static final MethodHandle MOP_GET;
    protected static final MethodHandle MOP_INVOKE_CONSTRUCTOR;
    protected static final MethodHandle MOP_INVOKE_METHOD;
    protected static final MethodHandle INTERCEPTABLE_INVOKER;
    protected static final MethodHandle CLASS_FOR_NAME;
    protected static final MethodHandle BOOLEAN_IDENTITY;
    protected static final MethodHandle DTT_CAST_TO_TYPE;
    protected static final MethodHandle SAM_CONVERSION;
    protected static final MethodHandle HASHSET_CONSTRUCTOR;
    protected static final MethodHandle ARRAYLIST_CONSTRUCTOR;
    protected static final MethodHandle GROOVY_CAST_EXCEPTION;
    protected static final MethodHandle EQUALS;
    protected static final MethodHandle NULL_REF;

    public static Object setBeanProperties(MetaClass mc, Object bean, Map properties) {
        for (Map.Entry entry : properties.entrySet()) {
            String key = entry.getKey().toString();
            Object value = entry.getValue();
            mc.setProperty(bean, key, value);
        }
        return bean;
    }

    public static Object invokeGroovyObjectInvoker(MissingMethodException e, Object receiver, String name, Object[] args) {
        if (e instanceof MissingMethodExecutionFailed) {
            throw (MissingMethodException)e.getCause();
        }
        if (receiver.getClass() == e.getType() && e.getMethod().equals(name)) {
            return ((GroovyObject)receiver).invokeMethod(name, args);
        }
        throw e;
    }

    public static Object unwrap(GroovyRuntimeException gre) throws Throwable {
        throw ScriptBytecodeAdapter.unwrap(gre);
    }

    public static boolean isSameMetaClass(MetaClass mc, Object receiver) {
        return receiver instanceof GroovyObject && mc == ((GroovyObject)receiver).getMetaClass();
    }

    public static Object unwrap(Object o) {
        Wrapper w = (Wrapper)o;
        return w.unwrap();
    }

    public static boolean isNull(Object o) {
        return o == null;
    }

    public static boolean sameClass(Class c, Object o) {
        if (o == null) {
            return false;
        }
        return o.getClass() == c;
    }

    static {
        try {
            SAME_CLASS = IndyInterface.LOOKUP.findStatic(IndyGuardsFiltersAndSignatures.class, "sameClass", CLASS1_GUARD);
            UNWRAP_METHOD = IndyInterface.LOOKUP.findStatic(IndyGuardsFiltersAndSignatures.class, "unwrap", OBJECT_FILTER);
            SAME_MC = IndyInterface.LOOKUP.findStatic(IndyGuardsFiltersAndSignatures.class, "isSameMetaClass", METACLASS1_GUARD);
            IS_NULL = IndyInterface.LOOKUP.findStatic(IndyGuardsFiltersAndSignatures.class, "isNull", OBJECT_GUARD);
            UNWRAP_EXCEPTION = IndyInterface.LOOKUP.findStatic(IndyGuardsFiltersAndSignatures.class, "unwrap", GRE_GUARD);
            GROOVY_OBJECT_INVOKER = IndyInterface.LOOKUP.findStatic(IndyGuardsFiltersAndSignatures.class, "invokeGroovyObjectInvoker", INVOKER.insertParameterTypes(0, MissingMethodException.class));
            META_METHOD_INVOKER = IndyInterface.LOOKUP.findVirtual(MetaMethod.class, "doMethodInvoke", ANO_INVOKER);
            HAS_CATEGORY_IN_CURRENT_THREAD_GUARD = IndyInterface.LOOKUP.findStatic(GroovyCategorySupport.class, "hasCategoryInCurrentThread", ZERO_GUARD);
            GROOVY_OBJECT_GET_PROPERTY = IndyInterface.LOOKUP.findVirtual(GroovyObject.class, "getProperty", GET_INVOKER);
            META_CLASS_INVOKE_STATIC_METHOD = IndyInterface.LOOKUP.findVirtual(MetaObjectProtocol.class, "invokeStaticMethod", INVOKER);
            BEAN_CONSTRUCTOR_PROPERTY_SETTER = IndyInterface.LOOKUP.findStatic(IndyGuardsFiltersAndSignatures.class, "setBeanProperties", MethodType.methodType(Object.class, MetaClass.class, Object.class, Map.class));
            META_PROPERTY_GETTER = IndyInterface.LOOKUP.findVirtual(MetaProperty.class, "getProperty", OBJECT_FILTER);
            MOP_GET = IndyInterface.LOOKUP.findVirtual(MetaObjectProtocol.class, "getProperty", MethodType.methodType(Object.class, Object.class, String.class));
            MOP_INVOKE_CONSTRUCTOR = IndyInterface.LOOKUP.findVirtual(MetaObjectProtocol.class, "invokeConstructor", BOUND_INVOKER);
            MOP_INVOKE_METHOD = IndyInterface.LOOKUP.findVirtual(MetaObjectProtocol.class, "invokeMethod", INVOKER);
            SLOW_META_CLASS_FIND = IndyInterface.LOOKUP.findStatic(InvokerHelper.class, "getMetaClass", MethodType.methodType(MetaClass.class, Object.class));
            INTERCEPTABLE_INVOKER = IndyInterface.LOOKUP.findVirtual(GroovyObject.class, "invokeMethod", MethodType.methodType(Object.class, String.class, Object.class));
            CLASS_FOR_NAME = IndyInterface.LOOKUP.findStatic(Class.class, "forName", MethodType.methodType(Class.class, String.class, Boolean.TYPE, ClassLoader.class));
            BOOLEAN_IDENTITY = MethodHandles.identity(Boolean.class);
            DTT_CAST_TO_TYPE = IndyInterface.LOOKUP.findStatic(DefaultTypeTransformation.class, "castToType", MethodType.methodType(Object.class, Object.class, Class.class));
            SAM_CONVERSION = IndyInterface.LOOKUP.findStatic(CachedSAMClass.class, "coerceToSAM", MethodType.methodType(Object.class, Closure.class, Method.class, Class.class, Boolean.TYPE));
            HASHSET_CONSTRUCTOR = IndyInterface.LOOKUP.findConstructor(HashSet.class, MethodType.methodType(Void.TYPE, Collection.class));
            ARRAYLIST_CONSTRUCTOR = IndyInterface.LOOKUP.findConstructor(ArrayList.class, MethodType.methodType(Void.TYPE, Collection.class));
            GROOVY_CAST_EXCEPTION = IndyInterface.LOOKUP.findConstructor(GroovyCastException.class, MethodType.methodType(Void.TYPE, Object.class, Class.class));
            EQUALS = IndyInterface.LOOKUP.findVirtual(Object.class, "equals", OBJECT_GUARD);
        }
        catch (Exception e) {
            throw new GroovyBugError(e);
        }
        NULL_REF = MethodHandles.constant(Object.class, null);
    }
}

