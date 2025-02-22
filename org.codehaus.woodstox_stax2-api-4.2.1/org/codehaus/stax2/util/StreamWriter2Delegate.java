/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.codehaus.stax2.XMLStreamLocation2;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.typed.Base64Variant;
import org.codehaus.stax2.util.StreamWriterDelegate;
import org.codehaus.stax2.validation.ValidationProblemHandler;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;

public class StreamWriter2Delegate
extends StreamWriterDelegate
implements XMLStreamWriter2 {
    protected XMLStreamWriter2 mDelegate2;

    public StreamWriter2Delegate(XMLStreamWriter2 parent) {
        super(parent);
    }

    @Override
    public void setParent(XMLStreamWriter w) {
        super.setParent(w);
        this.mDelegate2 = (XMLStreamWriter2)w;
    }

    @Override
    public void closeCompletely() throws XMLStreamException {
        this.mDelegate2.closeCompletely();
    }

    @Override
    public void copyEventFromReader(XMLStreamReader2 r, boolean preserveEventData) throws XMLStreamException {
        this.mDelegate2.copyEventFromReader(r, preserveEventData);
    }

    @Override
    public String getEncoding() {
        return this.mDelegate2.getEncoding();
    }

    @Override
    public XMLStreamLocation2 getLocation() {
        return this.mDelegate2.getLocation();
    }

    @Override
    public boolean isPropertySupported(String name) {
        return this.mDelegate2.isPropertySupported(name);
    }

    @Override
    public boolean setProperty(String name, Object value) {
        return this.mDelegate2.setProperty(name, value);
    }

    @Override
    public void writeCData(char[] text, int start, int len) throws XMLStreamException {
        this.mDelegate2.writeCData(text, start, len);
    }

    @Override
    public void writeDTD(String rootName, String systemId, String publicId, String internalSubset) throws XMLStreamException {
        this.mDelegate2.writeDTD(rootName, systemId, publicId, internalSubset);
    }

    @Override
    public void writeFullEndElement() throws XMLStreamException {
        this.mDelegate2.writeFullEndElement();
    }

    @Override
    public void writeRaw(String text) throws XMLStreamException {
        this.mDelegate2.writeRaw(text);
    }

    @Override
    public void writeRaw(String text, int offset, int length) throws XMLStreamException {
        this.mDelegate2.writeRaw(text, offset, length);
    }

    @Override
    public void writeRaw(char[] text, int offset, int length) throws XMLStreamException {
        this.mDelegate2.writeRaw(text, offset, length);
    }

    @Override
    public void writeSpace(String text) throws XMLStreamException {
        this.mDelegate2.writeSpace(text);
    }

    @Override
    public void writeSpace(char[] text, int offset, int length) throws XMLStreamException {
        this.mDelegate2.writeSpace(text, offset, length);
    }

    @Override
    public void writeStartDocument(String version, String encoding, boolean standAlone) throws XMLStreamException {
        this.mDelegate2.writeStartDocument(version, encoding, standAlone);
    }

    @Override
    public void writeBinary(byte[] value, int from, int length) throws XMLStreamException {
        this.mDelegate2.writeBinary(value, from, length);
    }

    @Override
    public void writeBinary(Base64Variant v, byte[] value, int from, int length) throws XMLStreamException {
        this.mDelegate2.writeBinary(v, value, from, length);
    }

    @Override
    public void writeBinaryAttribute(String prefix, String namespaceURI, String localName, byte[] value) throws XMLStreamException {
        this.mDelegate2.writeBinaryAttribute(prefix, namespaceURI, localName, value);
    }

    @Override
    public void writeBinaryAttribute(Base64Variant v, String prefix, String namespaceURI, String localName, byte[] value) throws XMLStreamException {
        this.mDelegate2.writeBinaryAttribute(v, prefix, namespaceURI, localName, value);
    }

    @Override
    public void writeBoolean(boolean value) throws XMLStreamException {
        this.mDelegate2.writeBoolean(value);
    }

    @Override
    public void writeBooleanAttribute(String prefix, String namespaceURI, String localName, boolean value) throws XMLStreamException {
        this.mDelegate2.writeBooleanAttribute(prefix, namespaceURI, localName, value);
    }

    @Override
    public void writeDecimal(BigDecimal value) throws XMLStreamException {
        this.mDelegate2.writeDecimal(value);
    }

    @Override
    public void writeDecimalAttribute(String prefix, String namespaceURI, String localName, BigDecimal value) throws XMLStreamException {
        this.mDelegate2.writeDecimalAttribute(prefix, namespaceURI, localName, value);
    }

    @Override
    public void writeDouble(double value) throws XMLStreamException {
        this.mDelegate2.writeDouble(value);
    }

    @Override
    public void writeDoubleArray(double[] value, int from, int length) throws XMLStreamException {
        this.mDelegate2.writeDoubleArray(value, from, length);
    }

    @Override
    public void writeDoubleArrayAttribute(String prefix, String namespaceURI, String localName, double[] value) throws XMLStreamException {
        this.mDelegate2.writeDoubleArrayAttribute(prefix, namespaceURI, localName, value);
    }

    @Override
    public void writeDoubleAttribute(String prefix, String namespaceURI, String localName, double value) throws XMLStreamException {
        this.mDelegate2.writeDoubleAttribute(prefix, namespaceURI, localName, value);
    }

    @Override
    public void writeFloat(float value) throws XMLStreamException {
        this.mDelegate2.writeFloat(value);
    }

    @Override
    public void writeFloatArray(float[] value, int from, int length) throws XMLStreamException {
        this.mDelegate2.writeFloatArray(value, from, length);
    }

    @Override
    public void writeFloatArrayAttribute(String prefix, String namespaceURI, String localName, float[] value) throws XMLStreamException {
        this.mDelegate2.writeFloatArrayAttribute(prefix, namespaceURI, localName, value);
    }

    @Override
    public void writeFloatAttribute(String prefix, String namespaceURI, String localName, float value) throws XMLStreamException {
        this.mDelegate2.writeFloatAttribute(prefix, namespaceURI, localName, value);
    }

    @Override
    public void writeInt(int value) throws XMLStreamException {
        this.mDelegate2.writeInt(value);
    }

    @Override
    public void writeIntArray(int[] value, int from, int length) throws XMLStreamException {
        this.mDelegate2.writeIntArray(value, from, length);
    }

    @Override
    public void writeIntArrayAttribute(String prefix, String namespaceURI, String localName, int[] value) throws XMLStreamException {
        this.mDelegate2.writeIntArrayAttribute(prefix, namespaceURI, localName, value);
    }

    @Override
    public void writeIntAttribute(String prefix, String namespaceURI, String localName, int value) throws XMLStreamException {
        this.mDelegate2.writeIntAttribute(prefix, namespaceURI, localName, value);
    }

    @Override
    public void writeInteger(BigInteger value) throws XMLStreamException {
        this.mDelegate2.writeInteger(value);
    }

    @Override
    public void writeIntegerAttribute(String prefix, String namespaceURI, String localName, BigInteger value) throws XMLStreamException {
        this.mDelegate2.writeIntegerAttribute(prefix, namespaceURI, localName, value);
    }

    @Override
    public void writeLong(long value) throws XMLStreamException {
        this.mDelegate2.writeLong(value);
    }

    @Override
    public void writeLongArray(long[] value, int from, int length) throws XMLStreamException {
        this.mDelegate2.writeLongArray(value, from, length);
    }

    @Override
    public void writeLongArrayAttribute(String prefix, String namespaceURI, String localName, long[] value) throws XMLStreamException {
        this.mDelegate2.writeLongArrayAttribute(prefix, namespaceURI, localName, value);
    }

    @Override
    public void writeLongAttribute(String prefix, String namespaceURI, String localName, long value) throws XMLStreamException {
        this.mDelegate2.writeLongAttribute(prefix, namespaceURI, localName, value);
    }

    @Override
    public void writeQName(QName value) throws XMLStreamException {
        this.mDelegate2.writeQName(value);
    }

    @Override
    public void writeQNameAttribute(String prefix, String namespaceURI, String localName, QName value) throws XMLStreamException {
        this.mDelegate2.writeQNameAttribute(prefix, namespaceURI, localName, value);
    }

    @Override
    public ValidationProblemHandler setValidationProblemHandler(ValidationProblemHandler h) {
        return this.mDelegate2.setValidationProblemHandler(h);
    }

    @Override
    public XMLValidator stopValidatingAgainst(XMLValidationSchema schema) throws XMLStreamException {
        return this.mDelegate2.stopValidatingAgainst(schema);
    }

    @Override
    public XMLValidator stopValidatingAgainst(XMLValidator validator) throws XMLStreamException {
        return this.mDelegate2.stopValidatingAgainst(validator);
    }

    @Override
    public XMLValidator validateAgainst(XMLValidationSchema schema) throws XMLStreamException {
        return this.mDelegate2.validateAgainst(schema);
    }
}

