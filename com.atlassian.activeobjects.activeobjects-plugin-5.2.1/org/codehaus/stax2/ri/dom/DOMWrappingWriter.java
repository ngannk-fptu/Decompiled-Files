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

    protected DOMWrappingWriter(Node node, boolean bl, boolean bl2) throws XMLStreamException {
        if (node == null) {
            throw new IllegalArgumentException("Can not pass null Node for constructing a DOM-based XMLStreamWriter");
        }
        this.mNsAware = bl;
        this.mNsRepairing = bl2;
        switch (node.getNodeType()) {
            case 9: {
                this.mDocument = (Document)node;
                break;
            }
            case 1: {
                this.mDocument = node.getOwnerDocument();
                break;
            }
            case 11: {
                this.mDocument = node.getOwnerDocument();
                break;
            }
            default: {
                throw new XMLStreamException("Can not create an XMLStreamWriter for a DOM node of type " + node.getClass());
            }
        }
        if (this.mDocument == null) {
            throw new XMLStreamException("Can not create an XMLStreamWriter for given node (of type " + node.getClass() + "): did not have owner document");
        }
    }

    public void close() {
    }

    public void flush() {
    }

    public abstract NamespaceContext getNamespaceContext();

    public abstract String getPrefix(String var1);

    public abstract Object getProperty(String var1);

    public abstract void setDefaultNamespace(String var1);

    public void setNamespaceContext(NamespaceContext namespaceContext) {
        this.mNsContext = namespaceContext;
    }

    public abstract void setPrefix(String var1, String var2) throws XMLStreamException;

    public abstract void writeAttribute(String var1, String var2) throws XMLStreamException;

    public abstract void writeAttribute(String var1, String var2, String var3) throws XMLStreamException;

    public abstract void writeAttribute(String var1, String var2, String var3, String var4) throws XMLStreamException;

    public void writeCData(String string) throws XMLStreamException {
        this.appendLeaf(this.mDocument.createCDATASection(string));
    }

    public void writeCharacters(char[] cArray, int n, int n2) throws XMLStreamException {
        this.writeCharacters(new String(cArray, n, n2));
    }

    public void writeCharacters(String string) throws XMLStreamException {
        this.appendLeaf(this.mDocument.createTextNode(string));
    }

    public void writeComment(String string) throws XMLStreamException {
        this.appendLeaf(this.mDocument.createComment(string));
    }

    public abstract void writeDefaultNamespace(String var1) throws XMLStreamException;

    public void writeDTD(String string) throws XMLStreamException {
        this.reportUnsupported("writeDTD()");
    }

    public abstract void writeEmptyElement(String var1) throws XMLStreamException;

    public abstract void writeEmptyElement(String var1, String var2) throws XMLStreamException;

    public abstract void writeEmptyElement(String var1, String var2, String var3) throws XMLStreamException;

    public abstract void writeEndDocument() throws XMLStreamException;

    public void writeEntityRef(String string) throws XMLStreamException {
        this.appendLeaf(this.mDocument.createEntityReference(string));
    }

    public void writeProcessingInstruction(String string) throws XMLStreamException {
        this.writeProcessingInstruction(string, null);
    }

    public void writeProcessingInstruction(String string, String string2) throws XMLStreamException {
        this.appendLeaf(this.mDocument.createProcessingInstruction(string, string2));
    }

    public void writeStartDocument() throws XMLStreamException {
        this.writeStartDocument(DEFAULT_OUTPUT_ENCODING, DEFAULT_XML_VERSION);
    }

    public void writeStartDocument(String string) throws XMLStreamException {
        this.writeStartDocument(null, string);
    }

    public void writeStartDocument(String string, String string2) throws XMLStreamException {
        this.mEncoding = string;
    }

    public XMLStreamLocation2 getLocation() {
        return null;
    }

    public String getEncoding() {
        return this.mEncoding;
    }

    public abstract boolean isPropertySupported(String var1);

    public abstract boolean setProperty(String var1, Object var2);

    public void writeCData(char[] cArray, int n, int n2) throws XMLStreamException {
        this.writeCData(new String(cArray, n, n2));
    }

    public abstract void writeDTD(String var1, String var2, String var3, String var4) throws XMLStreamException;

    public void writeFullEndElement() throws XMLStreamException {
        this.writeEndElement();
    }

    public void writeSpace(char[] cArray, int n, int n2) throws XMLStreamException {
        this.writeSpace(new String(cArray, n, n2));
    }

    public void writeSpace(String string) throws XMLStreamException {
        this.writeCharacters(string);
    }

    public void writeStartDocument(String string, String string2, boolean bl) throws XMLStreamException {
        this.writeStartDocument(string2, string);
    }

    public XMLValidator validateAgainst(XMLValidationSchema xMLValidationSchema) throws XMLStreamException {
        return null;
    }

    public XMLValidator stopValidatingAgainst(XMLValidationSchema xMLValidationSchema) throws XMLStreamException {
        return null;
    }

    public XMLValidator stopValidatingAgainst(XMLValidator xMLValidator) throws XMLStreamException {
        return null;
    }

    public ValidationProblemHandler setValidationProblemHandler(ValidationProblemHandler validationProblemHandler) {
        return null;
    }

    public void writeRaw(String string) throws XMLStreamException {
        this.reportUnsupported("writeRaw()");
    }

    public void writeRaw(String string, int n, int n2) throws XMLStreamException {
        this.reportUnsupported("writeRaw()");
    }

    public void writeRaw(char[] cArray, int n, int n2) throws XMLStreamException {
        this.reportUnsupported("writeRaw()");
    }

    public void copyEventFromReader(XMLStreamReader2 xMLStreamReader2, boolean bl) throws XMLStreamException {
    }

    public void closeCompletely() {
    }

    public void writeBoolean(boolean bl) throws XMLStreamException {
        this.writeCharacters(bl ? "true" : "false");
    }

    public void writeInt(int n) throws XMLStreamException {
        this.writeCharacters(String.valueOf(n));
    }

    public void writeLong(long l) throws XMLStreamException {
        this.writeCharacters(String.valueOf(l));
    }

    public void writeFloat(float f) throws XMLStreamException {
        this.writeCharacters(String.valueOf(f));
    }

    public void writeDouble(double d) throws XMLStreamException {
        this.writeCharacters(String.valueOf(d));
    }

    public void writeInteger(BigInteger bigInteger) throws XMLStreamException {
        this.writeCharacters(bigInteger.toString());
    }

    public void writeDecimal(BigDecimal bigDecimal) throws XMLStreamException {
        this.writeCharacters(bigDecimal.toString());
    }

    public void writeQName(QName qName) throws XMLStreamException {
        this.writeCharacters(this.serializeQNameValue(qName));
    }

    public void writeIntArray(int[] nArray, int n, int n2) throws XMLStreamException {
        this.writeCharacters(this.getValueEncoder().encodeAsString(nArray, n, n2));
    }

    public void writeLongArray(long[] lArray, int n, int n2) throws XMLStreamException {
        this.writeCharacters(this.getValueEncoder().encodeAsString(lArray, n, n2));
    }

    public void writeFloatArray(float[] fArray, int n, int n2) throws XMLStreamException {
        this.writeCharacters(this.getValueEncoder().encodeAsString(fArray, n, n2));
    }

    public void writeDoubleArray(double[] dArray, int n, int n2) throws XMLStreamException {
        this.writeCharacters(this.getValueEncoder().encodeAsString(dArray, n, n2));
    }

    public void writeBinary(byte[] byArray, int n, int n2) throws XMLStreamException {
        this.writeBinary(Base64Variants.getDefaultVariant(), byArray, n, n2);
    }

    public void writeBinary(Base64Variant base64Variant, byte[] byArray, int n, int n2) throws XMLStreamException {
        this.writeCharacters(this.getValueEncoder().encodeAsString(base64Variant, byArray, n, n2));
    }

    public void writeBooleanAttribute(String string, String string2, String string3, boolean bl) throws XMLStreamException {
        this.writeAttribute(string, string2, string3, bl ? "true" : "false");
    }

    public void writeIntAttribute(String string, String string2, String string3, int n) throws XMLStreamException {
        this.writeAttribute(string, string2, string3, String.valueOf(n));
    }

    public void writeLongAttribute(String string, String string2, String string3, long l) throws XMLStreamException {
        this.writeAttribute(string, string2, string3, String.valueOf(l));
    }

    public void writeFloatAttribute(String string, String string2, String string3, float f) throws XMLStreamException {
        this.writeAttribute(string, string2, string3, String.valueOf(f));
    }

    public void writeDoubleAttribute(String string, String string2, String string3, double d) throws XMLStreamException {
        this.writeAttribute(string, string2, string3, String.valueOf(d));
    }

    public void writeIntegerAttribute(String string, String string2, String string3, BigInteger bigInteger) throws XMLStreamException {
        this.writeAttribute(string, string2, string3, bigInteger.toString());
    }

    public void writeDecimalAttribute(String string, String string2, String string3, BigDecimal bigDecimal) throws XMLStreamException {
        this.writeAttribute(string, string2, string3, bigDecimal.toString());
    }

    public void writeQNameAttribute(String string, String string2, String string3, QName qName) throws XMLStreamException {
        this.writeAttribute(string, string2, string3, this.serializeQNameValue(qName));
    }

    public void writeIntArrayAttribute(String string, String string2, String string3, int[] nArray) throws XMLStreamException {
        this.writeAttribute(string, string2, string3, this.getValueEncoder().encodeAsString(nArray, 0, nArray.length));
    }

    public void writeLongArrayAttribute(String string, String string2, String string3, long[] lArray) throws XMLStreamException {
        this.writeAttribute(string, string2, string3, this.getValueEncoder().encodeAsString(lArray, 0, lArray.length));
    }

    public void writeFloatArrayAttribute(String string, String string2, String string3, float[] fArray) throws XMLStreamException {
        this.writeAttribute(string, string2, string3, this.getValueEncoder().encodeAsString(fArray, 0, fArray.length));
    }

    public void writeDoubleArrayAttribute(String string, String string2, String string3, double[] dArray) throws XMLStreamException {
        this.writeAttribute(string, string2, string3, this.getValueEncoder().encodeAsString(dArray, 0, dArray.length));
    }

    public void writeBinaryAttribute(String string, String string2, String string3, byte[] byArray) throws XMLStreamException {
        this.writeBinaryAttribute(Base64Variants.getDefaultVariant(), string, string2, string3, byArray);
    }

    public void writeBinaryAttribute(Base64Variant base64Variant, String string, String string2, String string3, byte[] byArray) throws XMLStreamException {
        this.writeAttribute(string, string2, string3, this.getValueEncoder().encodeAsString(base64Variant, byArray, 0, byArray.length));
    }

    protected abstract void appendLeaf(Node var1) throws IllegalStateException;

    protected String serializeQNameValue(QName qName) throws XMLStreamException {
        String string;
        String string2;
        if (this.mNsRepairing) {
            string2 = qName.getNamespaceURI();
            NamespaceContext namespaceContext = this.getNamespaceContext();
            String string3 = string = namespaceContext == null ? null : namespaceContext.getPrefix(string2);
            if (string == null) {
                String string4 = qName.getPrefix();
                if (string4 == null || string4.length() == 0) {
                    string = "";
                    this.writeDefaultNamespace(string2);
                } else {
                    string = string4;
                    this.writeNamespace(string, string2);
                }
            }
        } else {
            string = qName.getPrefix();
        }
        string2 = qName.getLocalPart();
        if (string == null || string.length() == 0) {
            return string2;
        }
        return string + ":" + string2;
    }

    protected SimpleValueEncoder getValueEncoder() {
        if (this.mValueEncoder == null) {
            this.mValueEncoder = new SimpleValueEncoder();
        }
        return this.mValueEncoder;
    }

    protected static void throwOutputError(String string) throws XMLStreamException {
        throw new XMLStreamException(string);
    }

    protected static void throwOutputError(String string, Object object) throws XMLStreamException {
        String string2 = MessageFormat.format(string, object);
        DOMWrappingWriter.throwOutputError(string2);
    }

    protected void reportUnsupported(String string) {
        throw new UnsupportedOperationException(string + " can not be used with DOM-backed writer");
    }
}

