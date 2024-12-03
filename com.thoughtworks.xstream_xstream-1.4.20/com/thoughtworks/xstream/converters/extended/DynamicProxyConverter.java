/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.core.util.Fields;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.DynamicProxyMapper;
import com.thoughtworks.xstream.mapper.Mapper;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

public class DynamicProxyConverter
implements Converter {
    private ClassLoaderReference classLoaderReference;
    private Mapper mapper;
    static /* synthetic */ Class class$java$lang$reflect$Proxy;
    static /* synthetic */ Class class$java$lang$reflect$InvocationHandler;

    public DynamicProxyConverter(Mapper mapper) {
        this(mapper, DynamicProxyConverter.class.getClassLoader());
    }

    public DynamicProxyConverter(Mapper mapper, ClassLoaderReference classLoaderReference) {
        this.classLoaderReference = classLoaderReference;
        this.mapper = mapper;
    }

    public DynamicProxyConverter(Mapper mapper, ClassLoader classLoader) {
        this(mapper, new ClassLoaderReference(classLoader));
    }

    public boolean canConvert(Class type) {
        return type != null && (type.equals(DynamicProxyMapper.DynamicProxy.class) || Proxy.isProxyClass(type));
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(source);
        this.addInterfacesToXml(source, writer);
        writer.startNode("handler");
        String attributeName = this.mapper.aliasForSystemAttribute("class");
        if (attributeName != null) {
            writer.addAttribute(attributeName, this.mapper.serializedClass(invocationHandler.getClass()));
        }
        context.convertAnother(invocationHandler);
        writer.endNode();
    }

    private void addInterfacesToXml(Object source, HierarchicalStreamWriter writer) {
        Class<?>[] interfaces = source.getClass().getInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            Class<?> currentInterface = interfaces[i];
            writer.startNode("interface");
            writer.setValue(this.mapper.serializedClass(currentInterface));
            writer.endNode();
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        ArrayList<Class> interfaces = new ArrayList<Class>();
        InvocationHandler handler = null;
        Class handlerType = null;
        while (reader.hasMoreChildren()) {
            String attributeName;
            reader.moveDown();
            String elementName = reader.getNodeName();
            if (elementName.equals("interface")) {
                interfaces.add(this.mapper.realClass(reader.getValue()));
            } else if (elementName.equals("handler") && (attributeName = this.mapper.aliasForSystemAttribute("class")) != null) {
                handlerType = this.mapper.realClass(reader.getAttribute(attributeName));
                break;
            }
            reader.moveUp();
        }
        if (handlerType == null) {
            throw new ConversionException("No InvocationHandler specified for dynamic proxy");
        }
        Class[] interfacesAsArray = new Class[interfaces.size()];
        interfaces.toArray(interfacesAsArray);
        Object proxy = null;
        if (Reflections.HANDLER != null) {
            proxy = Proxy.newProxyInstance(this.classLoaderReference.getReference(), interfacesAsArray, Reflections.DUMMY);
        }
        handler = (InvocationHandler)context.convertAnother(proxy, handlerType);
        reader.moveUp();
        if (Reflections.HANDLER != null) {
            Fields.write(Reflections.HANDLER, proxy, handler);
        } else {
            proxy = Proxy.newProxyInstance(this.classLoaderReference.getReference(), interfacesAsArray, handler);
        }
        return proxy;
    }

    private static class Reflections {
        private static final Field HANDLER = Fields.locate(class$java$lang$reflect$Proxy == null ? (class$java$lang$reflect$Proxy = DynamicProxyConverter.class$("java.lang.reflect.Proxy")) : class$java$lang$reflect$Proxy, class$java$lang$reflect$InvocationHandler == null ? (class$java$lang$reflect$InvocationHandler = DynamicProxyConverter.class$("java.lang.reflect.InvocationHandler")) : class$java$lang$reflect$InvocationHandler, false);
        private static final InvocationHandler DUMMY = new InvocationHandler(){

            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return null;
            }
        };

        private Reflections() {
        }
    }
}

