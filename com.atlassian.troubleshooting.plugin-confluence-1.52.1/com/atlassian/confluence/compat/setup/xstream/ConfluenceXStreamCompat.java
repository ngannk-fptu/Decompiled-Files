/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 */
package com.atlassian.confluence.compat.setup.xstream;

import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.compat.setup.xstream.XStreamCompat;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Method;

class ConfluenceXStreamCompat
implements XStreamCompat {
    private static final String COULD_NOT_DESERIALIZE_OBJECT = "Could not deserialize object as XStream might not be properly initialized";
    private static final String COULD_NOT_SERIALIZE_OBJECT = "Could not serialize object as XStream might not be properly initialized";
    private final Object confluenceXStream;
    private final Method toXMLObjMethod;
    private final Method toXMLObjOnWriterMethod;
    private final Method fromXMLMethod;
    private final Method fromXMLReaderMethod;
    private final Method getXStreamMethod;
    private final Method registerConverterMethod;
    private final Method aliasMethod;

    ConfluenceXStreamCompat(Object confluenceXStream) throws ReflectiveOperationException {
        this.confluenceXStream = confluenceXStream;
        this.toXMLObjMethod = this.getConfluenceXStreamMethod("toXML", Object.class);
        this.toXMLObjOnWriterMethod = this.getConfluenceXStreamMethod("toXML", Object.class, Writer.class);
        this.fromXMLMethod = this.getConfluenceXStreamMethod("fromXML", String.class);
        this.fromXMLReaderMethod = this.getConfluenceXStreamMethod("fromXML", Reader.class);
        this.getXStreamMethod = this.getConfluenceXStreamMethod("getXStream", new Class[0]);
        this.registerConverterMethod = this.getConfluenceXStreamMethod("registerConverter", Converter.class, Integer.class);
        this.aliasMethod = this.getConfluenceXStreamMethod("alias", String.class, Class.class);
    }

    @Override
    public String toXML(Object obj) {
        try {
            return (String)this.toXMLObjMethod.invoke(this.confluenceXStream, obj);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException(COULD_NOT_SERIALIZE_OBJECT, (Throwable)e);
        }
    }

    @Override
    public void toXML(Object obj, Writer writer) {
        try {
            this.toXMLObjOnWriterMethod.invoke(this.confluenceXStream, obj, writer);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException(COULD_NOT_SERIALIZE_OBJECT, (Throwable)e);
        }
    }

    @Override
    public Object fromXML(String xml) {
        try {
            return this.fromXMLMethod.invoke(this.confluenceXStream, xml);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException(COULD_NOT_DESERIALIZE_OBJECT, (Throwable)e);
        }
    }

    @Override
    public Object fromXML(Reader reader) {
        try {
            return this.fromXMLReaderMethod.invoke(this.confluenceXStream, reader);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException(COULD_NOT_DESERIALIZE_OBJECT, (Throwable)e);
        }
    }

    @Override
    public XStream getXStream() {
        try {
            return (XStream)this.getXStreamMethod.invoke(this.confluenceXStream, new Object[0]);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException(COULD_NOT_DESERIALIZE_OBJECT, (Throwable)e);
        }
    }

    @Override
    public void registerConverter(Converter converter, Integer priority) {
        try {
            this.registerConverterMethod.invoke(this.confluenceXStream, converter, priority);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException(COULD_NOT_DESERIALIZE_OBJECT, (Throwable)e);
        }
    }

    @Override
    public void alias(String name, Class<?> type) {
        try {
            this.aliasMethod.invoke(this.confluenceXStream, name, type);
        }
        catch (ReflectiveOperationException e) {
            throw new ServiceException(COULD_NOT_DESERIALIZE_OBJECT, (Throwable)e);
        }
    }

    private Method getConfluenceXStreamMethod(String methodName, Class<?> ... parameterTypes) throws ReflectiveOperationException {
        return Class.forName("com.atlassian.confluence.setup.xstream.ConfluenceXStreamInternal").getMethod(methodName, parameterTypes);
    }
}

