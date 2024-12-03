/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.cglib.proxy.Callback
 *  net.sf.cglib.proxy.CallbackFilter
 *  net.sf.cglib.proxy.Enhancer
 *  net.sf.cglib.proxy.Factory
 *  net.sf.cglib.proxy.NoOp
 */
package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.ReflectionProviderWrapper;
import com.thoughtworks.xstream.converters.reflection.SerializableConverter;
import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.CGLIBMapper;
import com.thoughtworks.xstream.mapper.Mapper;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.NoOp;

public class CGLIBEnhancedConverter
extends SerializableConverter {
    private static String DEFAULT_NAMING_MARKER = "$$EnhancerByCGLIB$$";
    private static String CALLBACK_MARKER = "CGLIB$CALLBACK_";
    private transient Map fieldCache = new HashMap();
    static /* synthetic */ Class class$net$sf$cglib$proxy$MethodInterceptor;
    static /* synthetic */ Class class$net$sf$cglib$proxy$NoOp;
    static /* synthetic */ Class class$net$sf$cglib$proxy$Callback;

    public CGLIBEnhancedConverter(Mapper mapper, ReflectionProvider reflectionProvider, ClassLoaderReference classLoaderReference) {
        super(mapper, (ReflectionProvider)new CGLIBFilteringReflectionProvider(reflectionProvider), classLoaderReference);
    }

    public CGLIBEnhancedConverter(Mapper mapper, ReflectionProvider reflectionProvider, ClassLoader classLoader) {
        super(mapper, (ReflectionProvider)new CGLIBFilteringReflectionProvider(reflectionProvider), classLoader);
    }

    public CGLIBEnhancedConverter(Mapper mapper, ReflectionProvider reflectionProvider) {
        this(mapper, (ReflectionProvider)new CGLIBFilteringReflectionProvider(reflectionProvider), CGLIBEnhancedConverter.class.getClassLoader());
    }

    public boolean canConvert(Class type) {
        return type != null && Enhancer.isEnhanced((Class)type) && type.getName().indexOf(DEFAULT_NAMING_MARKER) > 0 || type == CGLIBMapper.Marker.class;
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Callback[] callbacks;
        Class<?> type = source.getClass();
        boolean hasFactory = Factory.class.isAssignableFrom(type);
        ExtendedHierarchicalStreamWriterHelper.startNode(writer, "type", type);
        context.convertAnother(type.getSuperclass());
        writer.endNode();
        writer.startNode("interfaces");
        Class<?>[] interfaces = type.getInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            if (interfaces[i] == (class$net$sf$cglib$proxy$Factory == null ? CGLIBEnhancedConverter.class$("net.sf.cglib.proxy.Factory") : class$net$sf$cglib$proxy$Factory)) continue;
            ExtendedHierarchicalStreamWriterHelper.startNode(writer, this.mapper.serializedClass(interfaces[i].getClass()), interfaces[i].getClass());
            context.convertAnother(interfaces[i]);
            writer.endNode();
        }
        writer.endNode();
        writer.startNode("hasFactory");
        writer.setValue(String.valueOf(hasFactory));
        writer.endNode();
        Map callbackIndexMap = null;
        Callback[] callbackArray = callbacks = hasFactory ? ((Factory)source).getCallbacks() : this.getCallbacks(source);
        if (callbacks.length > 1) {
            if (!hasFactory) {
                ConversionException exception = new ConversionException("Cannot handle CGLIB enhanced proxies without factory that have multiple callbacks");
                exception.add("proxy-superclass", type.getSuperclass().getName());
                exception.add("number-of-callbacks", String.valueOf(callbacks.length));
                throw exception;
            }
            callbackIndexMap = this.createCallbackIndexMap((Factory)source);
            writer.startNode("callbacks");
            writer.startNode("mapping");
            context.convertAnother(callbackIndexMap);
            writer.endNode();
        }
        boolean hasInterceptor = false;
        for (int i = 0; i < callbacks.length; ++i) {
            Callback callback = callbacks[i];
            if (callback == null) {
                String name = this.mapper.serializedClass(null);
                writer.startNode(name);
                writer.endNode();
                continue;
            }
            hasInterceptor = hasInterceptor || (class$net$sf$cglib$proxy$MethodInterceptor == null ? CGLIBEnhancedConverter.class$("net.sf.cglib.proxy.MethodInterceptor") : class$net$sf$cglib$proxy$MethodInterceptor).isAssignableFrom(callback.getClass());
            ExtendedHierarchicalStreamWriterHelper.startNode(writer, this.mapper.serializedClass(callback.getClass()), callback.getClass());
            context.convertAnother(callback);
            writer.endNode();
        }
        if (callbacks.length > 1) {
            writer.endNode();
        }
        try {
            Field field = type.getDeclaredField("serialVersionUID");
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            long serialVersionUID = field.getLong(null);
            ExtendedHierarchicalStreamWriterHelper.startNode(writer, "serialVersionUID", String.class);
            writer.setValue(String.valueOf(serialVersionUID));
            writer.endNode();
        }
        catch (NoSuchFieldException field) {
        }
        catch (IllegalAccessException e) {
            ObjectAccessException exception = new ObjectAccessException("Cannot access field", e);
            exception.add("field", type.getName() + ".serialVersionUID");
            throw exception;
        }
        if (hasInterceptor) {
            writer.startNode("instance");
            super.doMarshalConditionally(source, writer, context);
            writer.endNode();
        }
    }

    private Callback[] getCallbacks(Object source) {
        Class<?> type = source.getClass();
        ArrayList<Field> fields = (ArrayList<Field>)this.fieldCache.get(type.getName());
        if (fields == null) {
            fields = new ArrayList<Field>();
            this.fieldCache.put(type.getName(), fields);
            int i = 0;
            while (true) {
                try {
                    Field field = type.getDeclaredField(CALLBACK_MARKER + i);
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    fields.add(field);
                }
                catch (NoSuchFieldException e) {
                    break;
                }
                ++i;
            }
        }
        ArrayList<Object> list = new ArrayList<Object>();
        for (int i = 0; i < fields.size(); ++i) {
            try {
                Field field = (Field)fields.get(i);
                Object callback = field.get(source);
                list.add(callback);
                continue;
            }
            catch (IllegalAccessException e) {
                ObjectAccessException exception = new ObjectAccessException("Cannot access field", e);
                exception.add("field", type.getName() + "." + CALLBACK_MARKER + i);
                throw exception;
            }
        }
        return list.toArray(new Callback[list.size()]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Map createCallbackIndexMap(Factory source) {
        Callback[] originalCallbacks = source.getCallbacks();
        Callback[] reverseEngineeringCallbacks = new Callback[originalCallbacks.length];
        HashMap callbackIndexMap = new HashMap();
        int idxNoOp = -1;
        for (int i = 0; i < originalCallbacks.length; ++i) {
            Callback callback = originalCallbacks[i];
            if (callback == null) {
                reverseEngineeringCallbacks[i] = null;
                continue;
            }
            if ((class$net$sf$cglib$proxy$NoOp == null ? CGLIBEnhancedConverter.class$("net.sf.cglib.proxy.NoOp") : class$net$sf$cglib$proxy$NoOp).isAssignableFrom(callback.getClass())) {
                reverseEngineeringCallbacks[i] = NoOp.INSTANCE;
                idxNoOp = i;
                continue;
            }
            reverseEngineeringCallbacks[i] = this.createReverseEngineeredCallbackOfProperType(callback, i, callbackIndexMap);
        }
        try {
            source.setCallbacks(reverseEngineeringCallbacks);
            HashSet interfaces = new HashSet();
            HashSet<Method> methods = new HashSet<Method>();
            Class type = source.getClass();
            do {
                methods.addAll(Arrays.asList(type.getDeclaredMethods()));
                methods.addAll(Arrays.asList(type.getMethods()));
                Class<?>[] implementedInterfaces = type.getInterfaces();
                interfaces.addAll(Arrays.asList(implementedInterfaces));
            } while ((type = type.getSuperclass()) != null);
            Iterator iterator = interfaces.iterator();
            while (iterator.hasNext()) {
                type = (Class)iterator.next();
                methods.addAll(Arrays.asList(type.getDeclaredMethods()));
            }
            Iterator iter = methods.iterator();
            while (iter.hasNext()) {
                Method method = (Method)iter.next();
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                if ((class$net$sf$cglib$proxy$Factory == null ? CGLIBEnhancedConverter.class$("net.sf.cglib.proxy.Factory") : class$net$sf$cglib$proxy$Factory).isAssignableFrom(method.getDeclaringClass()) || (method.getModifiers() & 0x18) > 0) {
                    iter.remove();
                    continue;
                }
                Class[] parameterTypes = method.getParameterTypes();
                Method calledMethod = method;
                try {
                    if ((method.getModifiers() & 0x400) > 0) {
                        calledMethod = source.getClass().getMethod(method.getName(), method.getParameterTypes());
                    }
                    callbackIndexMap.put(null, method);
                    calledMethod.invoke((Object)source, parameterTypes == null ? (Object[])null : this.createNullArguments(parameterTypes));
                }
                catch (IllegalAccessException e) {
                    ObjectAccessException exception = new ObjectAccessException("Cannot access method", e);
                    exception.add("method", calledMethod.toString());
                    throw exception;
                }
                catch (InvocationTargetException e) {
                }
                catch (NoSuchMethodException e) {
                    ConversionException exception = new ConversionException("CGLIB enhanced proxies wit abstract nethod that has not been implemented");
                    exception.add("proxy-superclass", type.getSuperclass().getName());
                    exception.add("method", method.toString());
                    throw exception;
                }
                if (!callbackIndexMap.containsKey(method)) continue;
                iter.remove();
            }
            if (idxNoOp >= 0) {
                Integer idx = new Integer(idxNoOp);
                Iterator iter2 = methods.iterator();
                while (iter2.hasNext()) {
                    callbackIndexMap.put(iter2.next(), idx);
                }
            }
        }
        finally {
            source.setCallbacks(originalCallbacks);
        }
        callbackIndexMap.remove(null);
        return callbackIndexMap;
    }

    private Object[] createNullArguments(Class[] parameterTypes) {
        Object[] arguments = new Object[parameterTypes.length];
        for (int i = 0; i < arguments.length; ++i) {
            Class type = parameterTypes[i];
            if (!type.isPrimitive()) continue;
            arguments[i] = type == Byte.TYPE ? new Byte(0) : (type == Short.TYPE ? new Short(0) : (type == Integer.TYPE ? new Integer(0) : (type == Long.TYPE ? new Long(0L) : (type == Float.TYPE ? new Float(0.0f) : (type == Double.TYPE ? new Double(0.0) : (type == Character.TYPE ? (Comparable<Character>)new Character('\u0000') : (Comparable<Character>)Boolean.FALSE))))));
        }
        return arguments;
    }

    private Callback createReverseEngineeredCallbackOfProperType(Callback callback, int index, Map callbackIndexMap) {
        Class<?> iface = null;
        Class<?>[] interfaces = callback.getClass().getInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            if (!(class$net$sf$cglib$proxy$Callback == null ? CGLIBEnhancedConverter.class$("net.sf.cglib.proxy.Callback") : class$net$sf$cglib$proxy$Callback).isAssignableFrom(interfaces[i])) continue;
            iface = interfaces[i];
            if (iface == (class$net$sf$cglib$proxy$Callback == null ? CGLIBEnhancedConverter.class$("net.sf.cglib.proxy.Callback") : class$net$sf$cglib$proxy$Callback)) {
                ConversionException exception = new ConversionException("Cannot handle CGLIB callback");
                exception.add("CGLIB-callback-type", callback.getClass().getName());
                throw exception;
            }
            interfaces = iface.getInterfaces();
            if (Arrays.asList(interfaces).contains(class$net$sf$cglib$proxy$Callback == null ? CGLIBEnhancedConverter.class$("net.sf.cglib.proxy.Callback") : class$net$sf$cglib$proxy$Callback)) break;
            i = -1;
        }
        return (Callback)Proxy.newProxyInstance(iface.getClassLoader(), new Class[]{iface}, (InvocationHandler)new ReverseEngineeringInvocationHandler(index, callbackIndexMap));
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Enhancer enhancer = new Enhancer();
        reader.moveDown();
        enhancer.setSuperclass((Class)context.convertAnother(null, Class.class));
        reader.moveUp();
        reader.moveDown();
        ArrayList<Object> interfaces = new ArrayList<Object>();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            interfaces.add(context.convertAnother(null, this.mapper.realClass(reader.getNodeName())));
            reader.moveUp();
        }
        enhancer.setInterfaces(interfaces.toArray(new Class[interfaces.size()]));
        reader.moveUp();
        reader.moveDown();
        boolean useFactory = Boolean.valueOf(reader.getValue());
        enhancer.setUseFactory(useFactory);
        reader.moveUp();
        ArrayList callbacksToEnhance = new ArrayList();
        ArrayList callbacks = new ArrayList();
        Map callbackIndexMap = null;
        reader.moveDown();
        if ("callbacks".equals(reader.getNodeName())) {
            reader.moveDown();
            callbackIndexMap = (Map)context.convertAnother(null, HashMap.class);
            reader.moveUp();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                this.readCallback(reader, context, callbacksToEnhance, callbacks);
                reader.moveUp();
            }
        } else {
            this.readCallback(reader, context, callbacksToEnhance, callbacks);
        }
        enhancer.setCallbacks(callbacksToEnhance.toArray(new Callback[callbacksToEnhance.size()]));
        if (callbackIndexMap != null) {
            enhancer.setCallbackFilter((CallbackFilter)new ReverseEngineeredCallbackFilter(callbackIndexMap));
        }
        reader.moveUp();
        Object result = null;
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if (reader.getNodeName().equals("serialVersionUID")) {
                enhancer.setSerialVersionUID(Long.valueOf(reader.getValue()));
            } else if (reader.getNodeName().equals("instance")) {
                result = this.create(enhancer, callbacks, useFactory);
                super.doUnmarshalConditionally(result, reader, context);
            }
            reader.moveUp();
        }
        if (result == null) {
            result = this.create(enhancer, callbacks, useFactory);
        }
        return this.serializationMembers.callReadResolve(result);
    }

    private void readCallback(HierarchicalStreamReader reader, UnmarshallingContext context, List callbacksToEnhance, List callbacks) {
        Callback callback = (Callback)context.convertAnother(null, this.mapper.realClass(reader.getNodeName()));
        callbacks.add(callback);
        if (callback == null) {
            callbacksToEnhance.add(NoOp.INSTANCE);
        } else {
            callbacksToEnhance.add(callback);
        }
    }

    private Object create(Enhancer enhancer, List callbacks, boolean useFactory) {
        Object result = enhancer.create();
        if (useFactory) {
            ((Factory)result).setCallbacks(callbacks.toArray(new Callback[callbacks.size()]));
        }
        return result;
    }

    protected List hierarchyFor(Class type) {
        List typeHierarchy = super.hierarchyFor(type);
        typeHierarchy.remove(typeHierarchy.size() - 1);
        return typeHierarchy;
    }

    protected Object readResolve() {
        super.readResolve();
        this.fieldCache = new HashMap();
        return this;
    }

    private static class ReverseEngineeredCallbackFilter
    implements CallbackFilter {
        private final Map callbackIndexMap;

        public ReverseEngineeredCallbackFilter(Map callbackIndexMap) {
            this.callbackIndexMap = callbackIndexMap;
        }

        public int accept(Method method) {
            if (!this.callbackIndexMap.containsKey(method)) {
                ConversionException exception = new ConversionException("CGLIB callback not detected in reverse engineering");
                exception.add("CGLIB-callback", method.toString());
                throw exception;
            }
            return (Integer)this.callbackIndexMap.get(method);
        }
    }

    private static final class ReverseEngineeringInvocationHandler
    implements InvocationHandler {
        private final Integer index;
        private final Map indexMap;

        public ReverseEngineeringInvocationHandler(int index, Map indexMap) {
            this.indexMap = indexMap;
            this.index = new Integer(index);
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            this.indexMap.put(this.indexMap.get(null), this.index);
            return null;
        }
    }

    private static class CGLIBFilteringReflectionProvider
    extends ReflectionProviderWrapper {
        public CGLIBFilteringReflectionProvider(ReflectionProvider reflectionProvider) {
            super(reflectionProvider);
        }

        public void visitSerializableFields(Object object, final ReflectionProvider.Visitor visitor) {
            this.wrapped.visitSerializableFields(object, new ReflectionProvider.Visitor(){

                public void visit(String name, Class type, Class definedIn, Object value) {
                    if (!name.startsWith("CGLIB$")) {
                        visitor.visit(name, type, definedIn, value);
                    }
                }
            });
        }
    }
}

