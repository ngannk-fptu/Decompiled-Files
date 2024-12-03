/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.attachment.AttachmentMarshaller
 *  javax.xml.bind.attachment.AttachmentUnmarshaller
 */
package com.sun.xml.ws.spi.db;

import com.sun.xml.ws.spi.db.BindingContext;
import com.sun.xml.ws.spi.db.TypeInfo;
import com.sun.xml.ws.spi.db.XMLBridge;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.xml.bind.JAXBException;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

public class RepeatedElementBridge<T>
implements XMLBridge<T> {
    XMLBridge<T> delegate;
    CollectionHandler collectionHandler;
    static final CollectionHandler ListHandler = new BaseCollectionHandler(List.class){

        @Override
        public Object convert(List list) {
            return list;
        }
    };
    static final CollectionHandler HashSetHandler = new BaseCollectionHandler(HashSet.class){

        @Override
        public Object convert(List list) {
            return new HashSet(list);
        }
    };

    public RepeatedElementBridge(TypeInfo typeInfo, XMLBridge xb) {
        this.delegate = xb;
        this.collectionHandler = RepeatedElementBridge.create(typeInfo);
    }

    public CollectionHandler collectionHandler() {
        return this.collectionHandler;
    }

    @Override
    public BindingContext context() {
        return this.delegate.context();
    }

    @Override
    public void marshal(T object, XMLStreamWriter output, AttachmentMarshaller am) throws JAXBException {
        this.delegate.marshal(object, output, am);
    }

    @Override
    public void marshal(T object, OutputStream output, NamespaceContext nsContext, AttachmentMarshaller am) throws JAXBException {
        this.delegate.marshal(object, output, nsContext, am);
    }

    @Override
    public void marshal(T object, Node output) throws JAXBException {
        this.delegate.marshal(object, output);
    }

    @Override
    public void marshal(T object, ContentHandler contentHandler, AttachmentMarshaller am) throws JAXBException {
        this.delegate.marshal(object, contentHandler, am);
    }

    @Override
    public void marshal(T object, Result result) throws JAXBException {
        this.delegate.marshal(object, result);
    }

    @Override
    public T unmarshal(XMLStreamReader in, AttachmentUnmarshaller au) throws JAXBException {
        return this.delegate.unmarshal(in, au);
    }

    @Override
    public T unmarshal(Source in, AttachmentUnmarshaller au) throws JAXBException {
        return this.delegate.unmarshal(in, au);
    }

    @Override
    public T unmarshal(InputStream in) throws JAXBException {
        return this.delegate.unmarshal(in);
    }

    @Override
    public T unmarshal(Node n, AttachmentUnmarshaller au) throws JAXBException {
        return this.delegate.unmarshal(n, au);
    }

    @Override
    public TypeInfo getTypeInfo() {
        return this.delegate.getTypeInfo();
    }

    @Override
    public boolean supportOutputStream() {
        return this.delegate.supportOutputStream();
    }

    public static CollectionHandler create(TypeInfo ti) {
        Class javaClass = (Class)ti.type;
        if (javaClass.isArray()) {
            return new ArrayHandler((Class)ti.getItemType().type);
        }
        if (List.class.equals((Object)javaClass) || Collection.class.equals((Object)javaClass)) {
            return ListHandler;
        }
        if (Set.class.equals((Object)javaClass) || HashSet.class.equals((Object)javaClass)) {
            return HashSetHandler;
        }
        return new BaseCollectionHandler(javaClass);
    }

    static class ArrayHandler
    implements CollectionHandler {
        Class componentClass;

        public ArrayHandler(Class component) {
            this.componentClass = component;
        }

        @Override
        public int getSize(Object c) {
            return Array.getLength(c);
        }

        @Override
        public Object convert(List list) {
            Object array = Array.newInstance(this.componentClass, list.size());
            for (int i = 0; i < list.size(); ++i) {
                Array.set(array, i, list.get(i));
            }
            return array;
        }

        @Override
        public Iterator iterator(final Object c) {
            return new Iterator(){
                int index = 0;

                @Override
                public boolean hasNext() {
                    if (c == null || Array.getLength(c) == 0) {
                        return false;
                    }
                    return this.index != Array.getLength(c);
                }

                public Object next() throws NoSuchElementException {
                    Object retVal = null;
                    try {
                        retVal = Array.get(c, this.index++);
                    }
                    catch (ArrayIndexOutOfBoundsException ex) {
                        throw new NoSuchElementException();
                    }
                    return retVal;
                }

                @Override
                public void remove() {
                }
            };
        }
    }

    static class BaseCollectionHandler
    implements CollectionHandler {
        Class type;

        BaseCollectionHandler(Class c) {
            this.type = c;
        }

        @Override
        public int getSize(Object c) {
            return ((Collection)c).size();
        }

        @Override
        public Object convert(List list) {
            try {
                Object o = this.type.newInstance();
                ((Collection)o).addAll(list);
                return o;
            }
            catch (Exception e) {
                e.printStackTrace();
                return list;
            }
        }

        @Override
        public Iterator iterator(Object c) {
            return ((Collection)c).iterator();
        }
    }

    public static interface CollectionHandler {
        public int getSize(Object var1);

        public Iterator iterator(Object var1);

        public Object convert(List var1);
    }
}

