/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 */
package com.sun.jersey.json.impl.writer;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.json.impl.DefaultJaxbXmlDocumentStructure;
import com.sun.jersey.json.impl.JaxbXmlDocumentStructure;
import com.sun.jersey.json.impl.writer.DefaultXmlStreamWriter;
import com.sun.jersey.json.impl.writer.JacksonStringMergingGenerator;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.codehaus.jackson.JsonGenerator;

public class Stax2JacksonWriter
extends DefaultXmlStreamWriter
implements XMLStreamWriter {
    private final boolean attrsWithPrefix;
    static final String XML_SCHEMA_INSTANCE = "http://www.w3.org/2001/XMLSchema-instance";
    JacksonStringMergingGenerator generator;
    final List<ProcessingInfo> processingStack = new ArrayList<ProcessingInfo>();
    boolean writingAttr = false;
    private JaxbXmlDocumentStructure documentStructure;
    static final Type[] _pt = new Type[]{Byte.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Boolean.TYPE, Character.TYPE, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Boolean.class, Character.class, String.class};
    static final Type[] _nst = new Type[]{Byte.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Boolean.TYPE, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Boolean.class, BigInteger.class, BigDecimal.class};
    static final Set<Type> primitiveTypes = new HashSet<Type>(){
        {
            this.addAll(Arrays.asList(_pt));
        }
    };
    static final Set<Type> nonStringTypes = new HashSet<Type>(){
        {
            this.addAll(Arrays.asList(_nst));
        }
    };

    static <T> T pop(List<T> stack) {
        return stack.remove(stack.size() - 1);
    }

    static <T> T peek(List<T> stack) {
        return stack.size() > 0 ? (T)stack.get(stack.size() - 1) : null;
    }

    static <T> T peek2nd(List<T> stack) {
        return stack.size() > 1 ? (T)stack.get(stack.size() - 2) : null;
    }

    public Stax2JacksonWriter(JsonGenerator generator, Class<?> expectedType, JAXBContext jaxbContext) {
        this(generator, JSONConfiguration.DEFAULT, expectedType, jaxbContext);
    }

    public Stax2JacksonWriter(JsonGenerator generator, JSONConfiguration config, Class<?> expectedType, JAXBContext jaxbContext) {
        this.attrsWithPrefix = config.isUsingPrefixesAtNaturalAttributes();
        this.generator = JacksonStringMergingGenerator.createGenerator(generator);
        this.documentStructure = DefaultJaxbXmlDocumentStructure.getXmlDocumentStructure(jaxbContext, expectedType, false);
    }

    @Override
    public void writeStartElement(String localName) throws XMLStreamException {
        this.writeStartElement(null, localName, null);
    }

    @Override
    public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
        this.writeStartElement(null, localName, namespaceURI);
    }

    private void ensureStartObjectBeforeFieldName(ProcessingInfo pi) throws IOException {
        if (pi != null && pi.afterFN) {
            this.generator.writeStartObject();
            Stax2JacksonWriter.peek2nd(this.processingStack).startObjectWritten = true;
            pi.afterFN = false;
        }
    }

    @Override
    public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        try {
            if (!this.writingAttr) {
                this.pushPropInfo(namespaceURI, localName, null);
            }
            ProcessingInfo currentPI = Stax2JacksonWriter.peek(this.processingStack);
            ProcessingInfo parentPI = Stax2JacksonWriter.peek2nd(this.processingStack);
            if (!currentPI.isArray) {
                if (parentPI != null && parentPI.lastUnderlyingPI != null && parentPI.lastUnderlyingPI.isArray) {
                    this.generator.writeEndArray();
                    parentPI.afterFN = false;
                }
                this.ensureStartObjectBeforeFieldName(parentPI);
                this.generator.writeFieldName(localName);
                currentPI.afterFN = true;
            } else if (parentPI == null || !currentPI.equals(parentPI.lastUnderlyingPI)) {
                if (parentPI != null && parentPI.lastUnderlyingPI != null && parentPI.lastUnderlyingPI.isArray) {
                    this.generator.writeEndArray();
                    parentPI.afterFN = false;
                }
                this.ensureStartObjectBeforeFieldName(parentPI);
                this.generator.writeFieldName(localName);
                this.generator.writeStartArray();
                currentPI.afterFN = true;
            } else {
                currentPI.afterFN = true;
            }
        }
        catch (IOException ex) {
            Logger.getLogger(Stax2JacksonWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new XMLStreamException(ex);
        }
    }

    private void pushPropInfo(String namespaceUri, String localName, String value) {
        QName qname = new QName(namespaceUri == null ? "" : namespaceUri, localName);
        if (this.writingAttr) {
            this.documentStructure.handleAttribute(qname, value);
        } else {
            this.documentStructure.startElement(qname);
        }
        ProcessingInfo parentPI = Stax2JacksonWriter.peek(this.processingStack);
        boolean sameArrayCollection = this.documentStructure.isSameArrayCollection();
        if (localName != null && parentPI != null && parentPI.lastUnderlyingPI != null && (localName.equals(parentPI.lastUnderlyingPI.elementName.getLocalPart()) || sameArrayCollection)) {
            this.processingStack.add(new ProcessingInfo(parentPI.lastUnderlyingPI));
            return;
        }
        Type rt = this.documentStructure.getEntityType(qname, this.writingAttr);
        Type individualType = this.documentStructure.getIndividualType();
        if (null == rt) {
            this.processingStack.add(new ProcessingInfo(qname, false, null, null));
            return;
        }
        if (primitiveTypes.contains(rt)) {
            this.processingStack.add(new ProcessingInfo(qname, false, rt, individualType));
            return;
        }
        if (!(!this.documentStructure.isArrayCollection() || this.writingAttr || parentPI != null && parentPI.isArray && sameArrayCollection)) {
            this.processingStack.add(new ProcessingInfo(qname, true, rt, individualType));
            return;
        }
        this.processingStack.add(new ProcessingInfo(qname, false, rt, individualType));
    }

    @Override
    public void writeEmptyElement(String localName) throws XMLStreamException {
        this.writeEmptyElement(null, localName, null);
    }

    @Override
    public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
        this.writeEmptyElement(null, localName, namespaceURI);
    }

    @Override
    public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        this.writeStartElement(prefix, localName, namespaceURI);
        this.writeEndElement();
    }

    private void cleanlyEndObject(ProcessingInfo pi) throws IOException {
        if (pi.startObjectWritten) {
            this.generator.writeEndObject();
        } else if (pi.afterFN && pi.lastUnderlyingPI == null) {
            if (this.documentStructure.isArrayCollection() || this.documentStructure.hasSubElements()) {
                this.generator.writeStartObject();
                this.generator.writeEndObject();
            } else {
                this.generator.writeNull();
            }
        }
    }

    @Override
    public void writeEndElement() throws XMLStreamException {
        try {
            ProcessingInfo removedPI = Stax2JacksonWriter.pop(this.processingStack);
            ProcessingInfo currentPI = Stax2JacksonWriter.peek(this.processingStack);
            if (currentPI != null) {
                currentPI.lastUnderlyingPI = removedPI;
            }
            if (removedPI.lastUnderlyingPI != null && removedPI.lastUnderlyingPI.isArray) {
                this.generator.writeEndArray();
            }
            this.cleanlyEndObject(removedPI);
            this.documentStructure.endElement(removedPI.elementName);
        }
        catch (IOException ex) {
            Logger.getLogger(Stax2JacksonWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new XMLStreamException(ex);
        }
    }

    @Override
    public void writeEndDocument() throws XMLStreamException {
        try {
            this.generator.writeEndObject();
        }
        catch (IOException ex) {
            Logger.getLogger(Stax2JacksonWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new XMLStreamException(ex);
        }
    }

    @Override
    public void close() throws XMLStreamException {
        try {
            this.generator.close();
        }
        catch (IOException ex) {
            Logger.getLogger(Stax2JacksonWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new XMLStreamException(ex);
        }
    }

    @Override
    public void flush() throws XMLStreamException {
        try {
            this.generator.flush();
        }
        catch (IOException ex) {
            Logger.getLogger(Stax2JacksonWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new XMLStreamException(ex);
        }
    }

    @Override
    public void writeAttribute(String localName, String value) throws XMLStreamException {
        this.writeAttribute(null, null, localName, value);
    }

    @Override
    public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
        this.writeAttribute(null, namespaceURI, localName, value);
    }

    @Override
    public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
        this.writingAttr = true;
        this.pushPropInfo(namespaceURI, localName, value);
        this.writeStartElement(prefix, this.attrsWithPrefix ? "@" + localName : localName, namespaceURI);
        this.writingAttr = false;
        this.writeCharacters(value, "type".equals(localName) && XML_SCHEMA_INSTANCE.equals(namespaceURI));
        this.writeEndElement();
    }

    @Override
    public void writeStartDocument() throws XMLStreamException {
        this.writeStartDocument(null, null);
    }

    @Override
    public void writeStartDocument(String version) throws XMLStreamException {
        this.writeStartDocument(null, version);
    }

    @Override
    public void writeStartDocument(String encoding, String version) throws XMLStreamException {
        try {
            this.generator.writeStartObject();
        }
        catch (IOException ex) {
            if (ex instanceof SocketTimeoutException || ex instanceof SocketException) {
                Logger.getLogger(Stax2JacksonWriter.class.getName()).log(Level.FINE, "Socket excption", ex);
            }
            Logger.getLogger(Stax2JacksonWriter.class.getName()).log(Level.SEVERE, "IO exception", ex);
            throw new XMLStreamException(ex);
        }
    }

    private void writeCharacters(String text, boolean forceString) throws XMLStreamException {
        try {
            ProcessingInfo currentPI = Stax2JacksonWriter.peek(this.processingStack);
            if (currentPI.startObjectWritten && !currentPI.afterFN) {
                this.generator.writeFieldName("$");
            }
            currentPI.afterFN = false;
            Type valueType = this.getValueType(currentPI.rawType, currentPI.individualType);
            if (forceString || !nonStringTypes.contains(valueType)) {
                if (!currentPI.isArray) {
                    this.generator.writeStringToMerge(text);
                } else {
                    this.generator.writeString(text);
                }
            } else {
                this.writePrimitiveType(text, valueType);
            }
        }
        catch (IOException ex) {
            Logger.getLogger(Stax2JacksonWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new XMLStreamException(ex);
        }
    }

    private void writePrimitiveType(String text, Type valueType) throws IOException {
        if (Boolean.TYPE == valueType || Boolean.class == valueType) {
            this.generator.writeBoolean(Boolean.parseBoolean(text));
        } else {
            this.generator.writeNumber(text);
        }
    }

    private Type getValueType(Type rawType, Type individualType) {
        Type[] actualTypeArguments;
        ParameterizedType parameterizedType;
        Type parameterizedTypeRawType;
        if (individualType != null) {
            return individualType;
        }
        if (rawType instanceof ParameterizedType && (parameterizedTypeRawType = (parameterizedType = (ParameterizedType)rawType).getRawType()) instanceof Class && Collection.class.isAssignableFrom((Class)parameterizedTypeRawType) && (actualTypeArguments = parameterizedType.getActualTypeArguments()) != null && actualTypeArguments.length > 0) {
            return actualTypeArguments[0];
        }
        return rawType;
    }

    @Override
    public void writeCharacters(String text) throws XMLStreamException {
        this.writeCharacters(text, false);
    }

    @Override
    public void writeCharacters(char[] text, int start, int length) throws XMLStreamException {
        this.writeCharacters(new String(text, start, length));
    }

    private static class ProcessingInfo {
        boolean isArray;
        Type rawType;
        Type individualType;
        ProcessingInfo lastUnderlyingPI;
        boolean startObjectWritten = false;
        boolean afterFN = false;
        QName elementName;

        public ProcessingInfo(QName elementName, boolean isArray, Type rawType, Type individualType) {
            this.elementName = elementName;
            this.isArray = isArray;
            this.rawType = rawType;
            this.individualType = individualType;
        }

        public ProcessingInfo(ProcessingInfo pi) {
            this(pi.elementName, pi.isArray, pi.rawType, pi.individualType);
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            ProcessingInfo other = (ProcessingInfo)obj;
            if (this.isArray != other.isArray) {
                return false;
            }
            if (!(this.elementName == other.elementName || this.elementName != null && this.elementName.equals(other.elementName))) {
                return false;
            }
            if (!(this.rawType == other.rawType || this.rawType != null && this.rawType.equals(other.rawType))) {
                return false;
            }
            return this.individualType == other.individualType || this.individualType != null && this.individualType.equals(other.individualType);
        }

        public int hashCode() {
            int hash = 5;
            hash = 47 * hash + (this.isArray ? 1 : 0);
            hash = 47 * hash + (this.elementName != null ? this.elementName.hashCode() : 0);
            hash = 47 * hash + (this.rawType != null ? this.rawType.hashCode() : 0);
            hash = 47 * hash + (this.individualType != null ? this.individualType.hashCode() : 0);
            return hash;
        }
    }
}

