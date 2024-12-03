/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Marshaller
 *  javax.xml.bind.UnmarshalException
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlType
 */
package com.sun.jersey.core.provider.jaxb;

import com.sun.jersey.core.impl.provider.entity.Inflector;
import com.sun.jersey.core.provider.jaxb.AbstractJAXBProvider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public abstract class AbstractListElementProvider
extends AbstractJAXBProvider<Object> {
    private static final Class[] DEFAULT_IMPLS = new Class[]{ArrayList.class, LinkedList.class, HashSet.class, TreeSet.class, Stack.class};
    private static final JaxbTypeChecker DefaultJaxbTypeCHECKER = new JaxbTypeChecker(){

        @Override
        public boolean isJaxbType(Class type) {
            return type.isAnnotationPresent(XmlRootElement.class) || type.isAnnotationPresent(XmlType.class);
        }
    };
    private final Inflector inflector = Inflector.getInstance();

    public AbstractListElementProvider(Providers ps) {
        super(ps);
    }

    public AbstractListElementProvider(Providers ps, MediaType mt) {
        super(ps, mt);
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (AbstractListElementProvider.verifyCollectionSubclass(type)) {
            return AbstractListElementProvider.verifyGenericType(genericType) && this.isSupported(mediaType);
        }
        if (type.isArray()) {
            return AbstractListElementProvider.verifyArrayType(type) && this.isSupported(mediaType);
        }
        return false;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (Collection.class.isAssignableFrom(type)) {
            return AbstractListElementProvider.verifyGenericType(genericType) && this.isSupported(mediaType);
        }
        if (type.isArray()) {
            return AbstractListElementProvider.verifyArrayType(type) && this.isSupported(mediaType);
        }
        return false;
    }

    public static boolean verifyCollectionSubclass(Class<?> type) {
        try {
            if (Collection.class.isAssignableFrom(type)) {
                for (Class c : DEFAULT_IMPLS) {
                    if (!type.isAssignableFrom(c)) continue;
                    return true;
                }
                return !Modifier.isAbstract(type.getModifiers()) && Modifier.isPublic(type.getConstructor(new Class[0]).getModifiers());
            }
        }
        catch (NoSuchMethodException ex) {
            Logger.getLogger(AbstractListElementProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (SecurityException ex) {
            Logger.getLogger(AbstractListElementProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private static boolean verifyArrayType(Class type) {
        return AbstractListElementProvider.verifyArrayType(type, DefaultJaxbTypeCHECKER);
    }

    public static boolean verifyArrayType(Class type, JaxbTypeChecker checker) {
        return checker.isJaxbType(type = type.getComponentType()) || JAXBElement.class.isAssignableFrom(type);
    }

    private static boolean verifyGenericType(Type genericType) {
        return AbstractListElementProvider.verifyGenericType(genericType, DefaultJaxbTypeCHECKER);
    }

    public static boolean verifyGenericType(Type genericType, JaxbTypeChecker checker) {
        if (!(genericType instanceof ParameterizedType)) {
            return false;
        }
        ParameterizedType pt = (ParameterizedType)genericType;
        if (pt.getActualTypeArguments().length > 1) {
            return false;
        }
        Type ta = pt.getActualTypeArguments()[0];
        if (ta instanceof ParameterizedType) {
            ParameterizedType lpt = (ParameterizedType)ta;
            return lpt.getRawType() instanceof Class && JAXBElement.class.isAssignableFrom((Class)lpt.getRawType());
        }
        if (!(pt.getActualTypeArguments()[0] instanceof Class)) {
            return false;
        }
        Class listClass = (Class)pt.getActualTypeArguments()[0];
        return checker.isJaxbType(listClass);
    }

    @Override
    public final void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
        try {
            List<Object> c = type.isArray() ? Arrays.asList((Object[])t) : (List<Object>)t;
            Class elementType = this.getElementClass(type, genericType);
            Charset charset = AbstractListElementProvider.getCharset(mediaType);
            String charsetName = charset.name();
            Marshaller m = this.getMarshaller(elementType, mediaType);
            m.setProperty("jaxb.fragment", (Object)true);
            if (charset != UTF8) {
                m.setProperty("jaxb.encoding", (Object)charsetName);
            }
            this.setHeader(m, annotations);
            this.writeList(elementType, c, mediaType, charset, m, entityStream);
        }
        catch (JAXBException ex) {
            throw new WebApplicationException((Throwable)ex, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public abstract void writeList(Class<?> var1, Collection<?> var2, MediaType var3, Charset var4, Marshaller var5, OutputStream var6) throws JAXBException, IOException;

    @Override
    public final Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
        try {
            Class elementType = this.getElementClass(type, genericType);
            Unmarshaller u = this.getUnmarshaller(elementType, mediaType);
            XMLStreamReader r = this.getXMLStreamReader(elementType, mediaType, u, entityStream);
            boolean jaxbElement = false;
            Collection<Object> l = null;
            if (type.isArray()) {
                l = new ArrayList();
            } else {
                try {
                    l = (Collection)type.newInstance();
                }
                catch (Exception e) {
                    for (Class c : DEFAULT_IMPLS) {
                        if (!type.isAssignableFrom(c)) continue;
                        try {
                            l = (Collection)c.newInstance();
                            break;
                        }
                        catch (InstantiationException ex) {
                            Logger.getLogger(AbstractListElementProvider.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        catch (IllegalAccessException ex) {
                            Logger.getLogger(AbstractListElementProvider.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
            int event = r.next();
            while (event != 1) {
                event = r.next();
            }
            event = r.next();
            while (event != 1 && event != 8) {
                event = r.next();
            }
            while (event != 8) {
                if (elementType.isAnnotationPresent(XmlRootElement.class)) {
                    l.add(u.unmarshal(r));
                } else if (elementType.isAnnotationPresent(XmlType.class)) {
                    l.add(u.unmarshal(r, elementType).getValue());
                } else {
                    l.add(u.unmarshal(r, elementType));
                    jaxbElement = true;
                }
                event = r.getEventType();
                while (event != 1 && event != 8) {
                    event = r.next();
                }
            }
            return type.isArray() ? this.createArray((List)l, jaxbElement ? JAXBElement.class : elementType) : l;
        }
        catch (UnmarshalException ex) {
            throw new WebApplicationException((Throwable)ex, Response.Status.BAD_REQUEST);
        }
        catch (XMLStreamException ex) {
            throw new WebApplicationException((Throwable)ex, Response.Status.BAD_REQUEST);
        }
        catch (JAXBException ex) {
            throw new WebApplicationException((Throwable)ex, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    private Object createArray(List l, Class componentType) {
        Object array = Array.newInstance(componentType, l.size());
        for (int i = 0; i < l.size(); ++i) {
            Array.set(array, i, l.get(i));
        }
        return array;
    }

    protected abstract XMLStreamReader getXMLStreamReader(Class<?> var1, MediaType var2, Unmarshaller var3, InputStream var4) throws XMLStreamException;

    protected Class getElementClass(Class<?> type, Type genericType) {
        Type ta = genericType instanceof ParameterizedType ? ((ParameterizedType)genericType).getActualTypeArguments()[0] : (genericType instanceof GenericArrayType ? ((GenericArrayType)genericType).getGenericComponentType() : type.getComponentType());
        if (ta instanceof ParameterizedType) {
            ta = ((ParameterizedType)ta).getActualTypeArguments()[0];
        }
        return ta;
    }

    private String convertToXmlName(String name) {
        return name.replace("$", "_");
    }

    protected final String getRootElementName(Class<?> elementType) {
        if (this.isXmlRootElementProcessing()) {
            return this.convertToXmlName(this.inflector.pluralize(this.inflector.demodulize(this.getElementName(elementType))));
        }
        return this.convertToXmlName(this.inflector.decapitalize(this.inflector.pluralize(this.inflector.demodulize(elementType.getName()))));
    }

    protected final String getElementName(Class<?> elementType) {
        String name = elementType.getName();
        XmlRootElement xre = elementType.getAnnotation(XmlRootElement.class);
        if (xre != null && !xre.name().equals("##default")) {
            name = xre.name();
        }
        return name;
    }

    public static interface JaxbTypeChecker {
        public boolean isJaxbType(Class var1);
    }
}

