/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.UnmarshalException
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlType
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.xml.StaxUtils
 */
package org.springframework.http.converter.xml;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.xml.AbstractJaxb2HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.xml.StaxUtils;

public class Jaxb2CollectionHttpMessageConverter<T extends Collection>
extends AbstractJaxb2HttpMessageConverter<T>
implements GenericHttpMessageConverter<T> {
    private final XMLInputFactory inputFactory = this.createXmlInputFactory();

    @Override
    public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
        return false;
    }

    @Override
    public boolean canRead(Type type, @Nullable Class<?> contextClass, @Nullable MediaType mediaType) {
        if (!(type instanceof ParameterizedType)) {
            return false;
        }
        ParameterizedType parameterizedType = (ParameterizedType)type;
        if (!(parameterizedType.getRawType() instanceof Class)) {
            return false;
        }
        Class rawType = (Class)parameterizedType.getRawType();
        if (!Collection.class.isAssignableFrom(rawType)) {
            return false;
        }
        if (parameterizedType.getActualTypeArguments().length != 1) {
            return false;
        }
        Type typeArgument = parameterizedType.getActualTypeArguments()[0];
        if (!(typeArgument instanceof Class)) {
            return false;
        }
        Class typeArgumentClass = (Class)typeArgument;
        return (typeArgumentClass.isAnnotationPresent(XmlRootElement.class) || typeArgumentClass.isAnnotationPresent(XmlType.class)) && this.canRead(mediaType);
    }

    @Override
    public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
        return false;
    }

    @Override
    public boolean canWrite(@Nullable Type type, @Nullable Class<?> clazz, @Nullable MediaType mediaType) {
        return false;
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected T readFromSource(Class<? extends T> clazz, HttpHeaders headers, Source source) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public T read(Type type, @Nullable Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        ParameterizedType parameterizedType = (ParameterizedType)type;
        T result = this.createCollection((Class)parameterizedType.getRawType());
        Class elementClass = (Class)parameterizedType.getActualTypeArguments()[0];
        try {
            Unmarshaller unmarshaller = this.createUnmarshaller(elementClass);
            XMLStreamReader streamReader = this.inputFactory.createXMLStreamReader(inputMessage.getBody());
            int event = this.moveToFirstChildOfRootElement(streamReader);
            while (event != 8) {
                if (elementClass.isAnnotationPresent(XmlRootElement.class)) {
                    result.add((Object)unmarshaller.unmarshal(streamReader));
                } else if (elementClass.isAnnotationPresent(XmlType.class)) {
                    result.add((Object)unmarshaller.unmarshal(streamReader, elementClass).getValue());
                } else {
                    throw new HttpMessageNotReadableException("Cannot unmarshal to [" + elementClass + "]", inputMessage);
                }
                event = this.moveToNextElement(streamReader);
            }
            return result;
        }
        catch (XMLStreamException ex) {
            throw new HttpMessageNotReadableException("Failed to read XML stream: " + ex.getMessage(), ex, inputMessage);
        }
        catch (UnmarshalException ex) {
            throw new HttpMessageNotReadableException("Could not unmarshal to [" + elementClass + "]: " + (Object)((Object)ex), ex, inputMessage);
        }
        catch (JAXBException ex) {
            throw new HttpMessageConversionException("Invalid JAXB setup: " + ex.getMessage(), ex);
        }
    }

    protected T createCollection(Class<?> collectionClass) {
        if (!collectionClass.isInterface()) {
            try {
                return (T)((Collection)ReflectionUtils.accessibleConstructor(collectionClass, (Class[])new Class[0]).newInstance(new Object[0]));
            }
            catch (Throwable ex) {
                throw new IllegalArgumentException("Could not instantiate collection class: " + collectionClass.getName(), ex);
            }
        }
        if (List.class == collectionClass) {
            return (T)new ArrayList();
        }
        if (SortedSet.class == collectionClass) {
            return (T)new TreeSet();
        }
        return (T)new LinkedHashSet();
    }

    private int moveToFirstChildOfRootElement(XMLStreamReader streamReader) throws XMLStreamException {
        int event = streamReader.next();
        while (event != 1) {
            event = streamReader.next();
        }
        event = streamReader.next();
        while (event != 1 && event != 8) {
            event = streamReader.next();
        }
        return event;
    }

    private int moveToNextElement(XMLStreamReader streamReader) throws XMLStreamException {
        int event = streamReader.getEventType();
        while (event != 1 && event != 8) {
            event = streamReader.next();
        }
        return event;
    }

    @Override
    public void write(T t, @Nullable Type type, @Nullable MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void writeToResult(T t, HttpHeaders headers, Result result) throws Exception {
        throw new UnsupportedOperationException();
    }

    protected XMLInputFactory createXmlInputFactory() {
        return StaxUtils.createDefensiveInputFactory();
    }
}

