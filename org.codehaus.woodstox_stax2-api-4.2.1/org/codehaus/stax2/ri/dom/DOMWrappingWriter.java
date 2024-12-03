/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri.dom;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.MessageFormat;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.XMLStreamLocation2;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.ri.typed.SimpleValueEncoder;
import org.codehaus.stax2.typed.Base64Variant;
import org.codehaus.stax2.typed.Base64Variants;
import org.codehaus.stax2.validation.ValidationProblemHandler;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public abstract class DOMWrappingWriter
implements XMLStreamWriter2 {
    static final String DEFAULT_OUTPUT_ENCODING = "UTF-8";
    static final String DEFAULT_XML_VERSION = "1.0";
    protected final boolean mNsAware;
    protected final boolean mNsRepairing;
    protected String mEncoding = null;
    protected NamespaceContext mNsContext;
    protected final Document mDocument;
    protected SimpleValueEncoder mValueEncoder;

    protected DOMWrappingWriter(Node treeRoot, boolean nsAware, boolean nsRepairing) throws XMLStreamException {
        if (treeRoot == null) {
            throw new IllegalArgumentException("Can not pass null Node for constructing a DOM-based XMLStreamWriter");
        }
        this.mNsAware = nsAware;
        this.mNsRepairing = nsRepairing;
        switch (treeRoot.getNodeType()) {
            case 9: {
                this.mDocument = (Document)treeRoot;
                break;
            }
            case 1: {
                this.mDocument = treeRoot.getOwnerDocument();
                break;
            }
            case 11: {
                this.mDocument = treeRoot.getOwnerDocument();
                break;
            }
            default: {
                throw new XMLStreamException("Can not create an XMLStreamWriter for a DOM node of type " + treeRoot.getClass());
            }
        }
        if (this.mDocument == null) {
            throw new XMLStreamException("Can not create an XMLStreamWriter for given node (of type " + treeRoot.getClass() + "): did not have owner document");
        }
    }

    @Override
    public void close() {
    }

    @Override
    public void flush() {
    }

    @Override
    public abstract NamespaceContext getNamespaceContext();

    @Override
    public abstract String getPrefix(String var1);

    @Override
    public abstract Object getProperty(String var1);

    @Override
    public abstract void setDefaultNamespace(String var1);

    @Override
    public void setNamespaceContext(NamespaceContext context) {
        this.mNsContext = context;
    }

    @Override
    public abstract void setPrefix(String var1, String var2) throws XMLStreamException;

    @Override
    public abstract void writeAttribute(String var1, String var2) throws XMLStreamException;

    @Override
    public abstract void writeAttribute(String var1, String var2, String var3) throws XMLStreamException;

    @Override
    public abstract void writeAttribute(String var1, String var2, String var3, String var4) throws XMLStreamException;

    @Override
    public void writeCData(String data) throws XMLStreamException {
        this.appendLeaf(this.mDocument.createCDATASection(data));
    }

    @Override
    public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
        this.writeCharacters(new String(text, start, len));
    }

    @Override
    public void writeCharacters(String text) throws XMLStreamException {
        this.appendLeaf(this.mDocument.createTextNode(text));
    }

    @Override
    public void writeComment(String data) throws XMLStreamException {
        this.appendLeaf(this.mDocument.createComment(data));
    }

    @Override
    public abstract void writeDefaultNamespace(String var1) throws XMLStreamException;

    @Override
    public void writeDTD(String dtd) throws XMLStreamException {
        this.reportUnsupported("writeDTD()");
    }

    @Override
    public abstract void writeEmptyElement(String var1) throws XMLStreamException;

    @Override
    public abstract void writeEmptyElement(String var1, String var2) throws XMLStreamException;

    @Override
    public abstract void writeEmptyElement(String var1, String var2, String var3) throws XMLStreamException;

    @Override
    public abstract void writeEndDocument() throws XMLStreamException;

    @Override
    public void writeEntityRef(String name) throws XMLStreamException {
        this.appendLeaf(this.mDocument.createEntityReference(name));
    }

    @Override
    public void writeProcessingInstruction(String target) throws XMLStreamException {
        this.writeProcessingInstruction(target, null);
    }

    @Override
    public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
        this.appendLeaf(this.mDocument.createProcessingInstruction(target, data));
    }

    @Override
    public void writeStartDocument() throws XMLStreamException {
        this.writeStartDocument(DEFAULT_OUTPUT_ENCODING, DEFAULT_XML_VERSION);
    }

    @Override
    public void writeStartDocument(String version) throws XMLStreamException {
        this.writeStartDocument(null, version);
    }

    @Override
    public void writeStartDocument(String encoding, String version) throws XMLStreamException {
        this.mEncoding = encoding;
    }

    @Override
    public XMLStreamLocation2 getLocation() {
        return null;
    }

    @Override
    public String getEncoding() {
        return this.mEncoding;
    }

    @Override
    public abstract boolean isPropertySupported(String var1);

    @Override
    public abstract boolean setProperty(String var1, Object var2);

    @Override
    public void writeCData(char[] text, int start, int len) throws XMLStreamException {
        this.writeCData(new String(text, start, len));
    }

    @Override
    public abstract void writeDTD(String var1, String var2, String var3, String var4) throws XMLStreamException;

    @Override
    public void writeFullEndElement() throws XMLStreamException {
        this.writeEndElement();
    }

    @Override
    public void writeSpace(char[] text, int start, int len) throws XMLStreamException {
        this.writeSpace(new String(text, start, len));
    }

    @Override
    public void writeSpace(String text) throws XMLStreamException {
        this.writeCharacters(text);
    }

    @Override
    public void writeStartDocument(String version, String encoding, boolean standAlone) throws XMLStreamException {
        this.writeStartDocument(encoding, version);
    }

    @Override
    public XMLValidator validateAgainst(XMLValidationSchema schema) throws XMLStreamException {
        return null;
    }

    @Override
    public XMLValidator stopValidatingAgainst(XMLValidationSchema schema) throws XMLStreamException {
        return null;
    }

    @Override
    public XMLValidator stopValidatingAgainst(XMLValidator validator) throws XMLStreamException {
        return null;
    }

    @Override
    public ValidationProblemHandler setValidationProblemHandler(ValidationProblemHandler h) {
        return null;
    }

    @Override
    public void writeRaw(String text) throws XMLStreamException {
        this.reportUnsupported("writeRaw()");
    }

    @Override
    public void writeRaw(String text, int start, int offset) throws XMLStreamException {
        this.reportUnsupported("writeRaw()");
    }

    @Override
    public void writeRaw(char[] text, int offset, int length) throws XMLStreamException {
        this.reportUnsupported("writeRaw()");
    }

    @Override
    public void copyEventFromReader(XMLStreamReader2 r, boolean preserveEventData) throws XMLStreamException {
    }

    @Override
    public void closeCompletely() {
    }

    @Override
    public void writeBoolean(boolean value) throws XMLStreamException {
        this.writeCharacters(value ? "true" : "false");
    }

    @Override
    public void writeInt(int value) throws XMLStreamException {
        this.writeCharacters(String.valueOf(value));
    }

    @Override
    public void writeLong(long value) throws XMLStreamException {
        this.writeCharacters(String.valueOf(value));
    }

    @Override
    public void writeFloat(float value) throws XMLStreamException {
        this.writeCharacters(String.valueOf(value));
    }

    @Override
    public void writeDouble(double value) throws XMLStreamException {
        this.writeCharacters(String.valueOf(value));
    }

    @Override
    public void writeInteger(BigInteger value) throws XMLStreamException {
        this.writeCharacters(value.toString());
    }

    @Override
    public void writeDecimal(BigDecimal value) throws XMLStreamException {
        this.writeCharacters(value.toString());
    }

    @Override
    public void writeQName(QName name) throws XMLStreamException {
        this.writeCharacters(this.serializeQNameValue(name));
    }

    @Override
    public void writeIntArray(int[] value, int from, int length) throws XMLStreamException {
        this.writeCharacters(this.getValueEncoder().encodeAsString(value, from, length));
    }

    @Override
    public void writeLongArray(long[] value, int from, int length) throws XMLStreamException {
        this.writeCharacters(this.getValueEncoder().encodeAsString(value, from, length));
    }

    @Override
    public void writeFloatArray(float[] value, int from, int length) throws XMLStreamException {
        this.writeCharacters(this.getValueEncoder().encodeAsString(value, from, length));
    }

    @Override
    public void writeDoubleArray(double[] value, int from, int length) throws XMLStreamException {
        this.writeCharacters(this.getValueEncoder().encodeAsString(value, from, length));
    }

    @Override
    public void writeBinary(byte[] value, int from, int length) throws XMLStreamException {
        this.writeBinary(Base64Variants.getDefaultVariant(), value, from, length);
    }

    @Override
    public void writeBinary(Base64Variant v, byte[] value, int from, int length) throws XMLStreamException {
        this.writeCharacters(this.getValueEncoder().encodeAsString(v, value, from, length));
    }

    @Override
    public void writeBooleanAttribute(String prefix, String nsURI, String localName, boolean value) throws XMLStreamException {
        this.writeAttribute(prefix, nsURI, localName, value ? "true" : "false");
    }

    @Override
    public void writeIntAttribute(String prefix, String nsURI, String localName, int value) throws XMLStreamException {
        this.writeAttribute(prefix, nsURI, localName, String.valueOf(value));
    }

    @Override
    public void writeLongAttribute(String prefix, String nsURI, String localName, long value) throws XMLStreamException {
        this.writeAttribute(prefix, nsURI, localName, String.valueOf(value));
    }

    @Override
    public void writeFloatAttribute(String prefix, String nsURI, String localName, float value) throws XMLStreamException {
        this.writeAttribute(prefix, nsURI, localName, String.valueOf(value));
    }

    @Override
    public void writeDoubleAttribute(String prefix, String nsURI, String localName, double value) throws XMLStreamException {
        this.writeAttribute(prefix, nsURI, localName, String.valueOf(value));
    }

    @Override
    public void writeIntegerAttribute(String prefix, String nsURI, String localName, BigInteger value) throws XMLStreamException {
        this.writeAttribute(prefix, nsURI, localName, value.toString());
    }

    @Override
    public void writeDecimalAttribute(String prefix, String nsURI, String localName, BigDecimal value) throws XMLStreamException {
        this.writeAttribute(prefix, nsURI, localName, value.toString());
    }

    @Override
    public void writeQNameAttribute(String prefix, String nsURI, String localName, QName name) throws XMLStreamException {
        this.writeAttribute(prefix, nsURI, localName, this.serializeQNameValue(name));
    }

    @Override
    public void writeIntArrayAttribute(String prefix, String nsURI, String localName, int[] value) throws XMLStreamException {
        this.writeAttribute(prefix, nsURI, localName, this.getValueEncoder().encodeAsString(value, 0, value.length));
    }

    @Override
    public void writeLongArrayAttribute(String prefix, String nsURI, String localName, long[] value) throws XMLStreamException {
        this.writeAttribute(prefix, nsURI, localName, this.getValueEncoder().encodeAsString(value, 0, value.length));
    }

    @Override
    public void writeFloatArrayAttribute(String prefix, String nsURI, String localName, float[] value) throws XMLStreamException {
        this.writeAttribute(prefix, nsURI, localName, this.getValueEncoder().encodeAsString(value, 0, value.length));
    }

    @Override
    public void writeDoubleArrayAttribute(String prefix, String nsURI, String localName, double[] value) throws XMLStreamException {
        this.writeAttribute(prefix, nsURI, localName, this.getValueEncoder().encodeAsString(value, 0, value.length));
    }

    @Override
    public void writeBinaryAttribute(String prefix, String nsURI, String localName, byte[] value) throws XMLStreamException {
        this.writeBinaryAttribute(Base64Variants.getDefaultVariant(), prefix, nsURI, localName, value);
    }

    @Override
    public void writeBinaryAttribute(Base64Variant v, String prefix, String nsURI, String localName, byte[] value) throws XMLStreamException {
        this.writeAttribute(prefix, nsURI, localName, this.getValueEncoder().encodeAsString(v, value, 0, value.length));
    }

    protected abstract void appendLeaf(Node var1) throws IllegalStateException;

    protected String serializeQNameValue(QName name) throws XMLStreamException {
        String prefix;
        if (this.mNsRepairing) {
            String uri = name.getNamespaceURI();
            NamespaceContext ctxt = this.getNamespaceContext();
            String string = prefix = ctxt == null ? null : ctxt.getPrefix(uri);
            if (prefix == null) {
                String origPrefix = name.getPrefix();
                if (origPrefix == null || origPrefix.length() == 0) {
                    prefix = "";
                    this.writeDefaultNamespace(uri);
                } else {
                    prefix = origPrefix;
                    this.writeNamespace(prefix, uri);
                }
            }
        } else {
            prefix = name.getPrefix();
        }
        String local = name.getLocalPart();
        if (prefix == null || prefix.length() == 0) {
            return local;
        }
        return prefix + ":" + local;
    }

    protected SimpleValueEncoder getValueEncoder() {
        if (this.mValueEncoder == null) {
            this.mValueEncoder = new SimpleValueEncoder();
        }
        return this.mValueEncoder;
    }

    protected static void throwOutputError(String msg) throws XMLStreamException {
        throw new XMLStreamException(msg);
    }

    protected static void throwOutputError(String format, Object arg) throws XMLStreamException {
        String msg = MessageFormat.format(format, arg);
        DOMWrappingWriter.throwOutputError(msg);
    }

    protected void reportUnsupported(String operName) {
        throw new UnsupportedOperationException(operName + " can not be used with DOM-backed writer");
    }
}

