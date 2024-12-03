/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.codehaus.stax2.DTDInfo;
import org.codehaus.stax2.XMLStreamLocation2;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.ri.typed.SimpleValueEncoder;
import org.codehaus.stax2.typed.Base64Variant;
import org.codehaus.stax2.typed.Base64Variants;
import org.codehaus.stax2.util.StreamWriterDelegate;
import org.codehaus.stax2.validation.ValidationProblemHandler;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;

public class Stax2WriterAdapter
extends StreamWriterDelegate
implements XMLStreamWriter2,
XMLStreamConstants {
    protected String mEncoding;
    protected SimpleValueEncoder mValueEncoder;
    protected final boolean mNsRepairing;

    protected Stax2WriterAdapter(XMLStreamWriter sw) {
        super(sw);
        this.mDelegate = sw;
        Object value = sw.getProperty("javax.xml.stream.isRepairingNamespaces");
        this.mNsRepairing = value instanceof Boolean && (Boolean)value != false;
    }

    public static XMLStreamWriter2 wrapIfNecessary(XMLStreamWriter sw) {
        if (sw instanceof XMLStreamWriter2) {
            return (XMLStreamWriter2)sw;
        }
        return new Stax2WriterAdapter(sw);
    }

    @Override
    public void writeBoolean(boolean b) throws XMLStreamException {
        this.mDelegate.writeCharacters(b ? "true" : "false");
    }

    @Override
    public void writeInt(int value) throws XMLStreamException {
        this.mDelegate.writeCharacters(String.valueOf(value));
    }

    @Override
    public void writeLong(long value) throws XMLStreamException {
        this.mDelegate.writeCharacters(String.valueOf(value));
    }

    @Override
    public void writeFloat(float value) throws XMLStreamException {
        this.mDelegate.writeCharacters(String.valueOf(value));
    }

    @Override
    public void writeDouble(double value) throws XMLStreamException {
        this.mDelegate.writeCharacters(String.valueOf(value));
    }

    @Override
    public void writeInteger(BigInteger value) throws XMLStreamException {
        this.mDelegate.writeCharacters(value.toString());
    }

    @Override
    public void writeDecimal(BigDecimal value) throws XMLStreamException {
        this.mDelegate.writeCharacters(value.toString());
    }

    @Override
    public void writeQName(QName name) throws XMLStreamException {
        this.mDelegate.writeCharacters(this.serializeQNameValue(name));
    }

    @Override
    public void writeIntArray(int[] value, int from, int length) throws XMLStreamException {
        this.mDelegate.writeCharacters(this.getValueEncoder().encodeAsString(value, from, length));
    }

    @Override
    public void writeLongArray(long[] value, int from, int length) throws XMLStreamException {
        this.mDelegate.writeCharacters(this.getValueEncoder().encodeAsString(value, from, length));
    }

    @Override
    public void writeFloatArray(float[] value, int from, int length) throws XMLStreamException {
        this.mDelegate.writeCharacters(this.getValueEncoder().encodeAsString(value, from, length));
    }

    @Override
    public void writeDoubleArray(double[] value, int from, int length) throws XMLStreamException {
        this.mDelegate.writeCharacters(this.getValueEncoder().encodeAsString(value, from, length));
    }

    @Override
    public void writeBinary(Base64Variant v, byte[] value, int from, int length) throws XMLStreamException {
        this.mDelegate.writeCharacters(this.getValueEncoder().encodeAsString(v, value, from, length));
    }

    @Override
    public void writeBinary(byte[] value, int from, int length) throws XMLStreamException {
        this.writeBinary(Base64Variants.getDefaultVariant(), value, from, length);
    }

    @Override
    public void writeBooleanAttribute(String prefix, String nsURI, String localName, boolean b) throws XMLStreamException {
        this.mDelegate.writeAttribute(prefix, nsURI, localName, b ? "true" : "false");
    }

    @Override
    public void writeIntAttribute(String prefix, String nsURI, String localName, int value) throws XMLStreamException {
        this.mDelegate.writeAttribute(prefix, nsURI, localName, String.valueOf(value));
    }

    @Override
    public void writeLongAttribute(String prefix, String nsURI, String localName, long value) throws XMLStreamException {
        this.mDelegate.writeAttribute(prefix, nsURI, localName, String.valueOf(value));
    }

    @Override
    public void writeFloatAttribute(String prefix, String nsURI, String localName, float value) throws XMLStreamException {
        this.mDelegate.writeAttribute(prefix, nsURI, localName, String.valueOf(value));
    }

    @Override
    public void writeDoubleAttribute(String prefix, String nsURI, String localName, double value) throws XMLStreamException {
        this.mDelegate.writeAttribute(prefix, nsURI, localName, String.valueOf(value));
    }

    @Override
    public void writeIntegerAttribute(String prefix, String nsURI, String localName, BigInteger value) throws XMLStreamException {
        this.mDelegate.writeAttribute(prefix, nsURI, localName, value.toString());
    }

    @Override
    public void writeDecimalAttribute(String prefix, String nsURI, String localName, BigDecimal value) throws XMLStreamException {
        this.mDelegate.writeAttribute(prefix, nsURI, localName, value.toString());
    }

    @Override
    public void writeQNameAttribute(String prefix, String nsURI, String localName, QName name) throws XMLStreamException {
        this.mDelegate.writeAttribute(prefix, nsURI, localName, this.serializeQNameValue(name));
    }

    @Override
    public void writeIntArrayAttribute(String prefix, String nsURI, String localName, int[] value) throws XMLStreamException {
        this.mDelegate.writeAttribute(prefix, nsURI, localName, this.getValueEncoder().encodeAsString(value, 0, value.length));
    }

    @Override
    public void writeLongArrayAttribute(String prefix, String nsURI, String localName, long[] value) throws XMLStreamException {
        this.mDelegate.writeAttribute(prefix, nsURI, localName, this.getValueEncoder().encodeAsString(value, 0, value.length));
    }

    @Override
    public void writeFloatArrayAttribute(String prefix, String nsURI, String localName, float[] value) throws XMLStreamException {
        this.mDelegate.writeAttribute(prefix, nsURI, localName, this.getValueEncoder().encodeAsString(value, 0, value.length));
    }

    @Override
    public void writeDoubleArrayAttribute(String prefix, String nsURI, String localName, double[] value) throws XMLStreamException {
        this.mDelegate.writeAttribute(prefix, nsURI, localName, this.getValueEncoder().encodeAsString(value, 0, value.length));
    }

    @Override
    public void writeBinaryAttribute(String prefix, String nsURI, String localName, byte[] value) throws XMLStreamException {
        this.writeBinaryAttribute(Base64Variants.getDefaultVariant(), prefix, nsURI, localName, value);
    }

    @Override
    public void writeBinaryAttribute(Base64Variant v, String prefix, String nsURI, String localName, byte[] value) throws XMLStreamException {
        this.mDelegate.writeAttribute(prefix, nsURI, localName, this.getValueEncoder().encodeAsString(v, value, 0, value.length));
    }

    @Override
    public boolean isPropertySupported(String name) {
        return false;
    }

    @Override
    public boolean setProperty(String name, Object value) {
        throw new IllegalArgumentException("No settable property '" + name + "'");
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
    public void writeCData(char[] text, int start, int len) throws XMLStreamException {
        this.writeCData(new String(text, start, len));
    }

    @Override
    public void writeDTD(String rootName, String systemId, String publicId, String internalSubset) throws XMLStreamException {
        StringBuffer sb = new StringBuffer();
        sb.append("<!DOCTYPE");
        sb.append(rootName);
        if (systemId != null) {
            if (publicId != null) {
                sb.append(" PUBLIC \"");
                sb.append(publicId);
                sb.append("\" \"");
            } else {
                sb.append(" SYSTEM \"");
            }
            sb.append(systemId);
            sb.append('\"');
        }
        if (internalSubset != null && internalSubset.length() > 0) {
            sb.append(" [");
            sb.append(internalSubset);
            sb.append(']');
        }
        sb.append('>');
        this.writeDTD(sb.toString());
    }

    @Override
    public void writeFullEndElement() throws XMLStreamException {
        this.mDelegate.writeCharacters("");
        this.mDelegate.writeEndElement();
    }

    @Override
    public void writeSpace(String text) throws XMLStreamException {
        this.writeRaw(text);
    }

    @Override
    public void writeSpace(char[] text, int offset, int length) throws XMLStreamException {
        this.writeRaw(text, offset, length);
    }

    @Override
    public void writeStartDocument(String version, String encoding, boolean standAlone) throws XMLStreamException {
        this.writeStartDocument(encoding, version);
    }

    @Override
    public void writeRaw(String text) throws XMLStreamException {
        this.writeRaw(text, 0, text.length());
    }

    @Override
    public void writeRaw(String text, int offset, int len) throws XMLStreamException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void writeRaw(char[] text, int offset, int length) throws XMLStreamException {
        this.writeRaw(new String(text, offset, length));
    }

    @Override
    public void copyEventFromReader(XMLStreamReader2 sr, boolean preserveEventData) throws XMLStreamException {
        switch (sr.getEventType()) {
            case 7: {
                String version = sr.getVersion();
                if (version != null && version.length() != 0) {
                    if (sr.standaloneSet()) {
                        this.writeStartDocument(sr.getVersion(), sr.getCharacterEncodingScheme(), sr.isStandalone());
                    } else {
                        this.writeStartDocument(sr.getCharacterEncodingScheme(), sr.getVersion());
                    }
                }
                return;
            }
            case 8: {
                this.writeEndDocument();
                return;
            }
            case 1: {
                this.copyStartElement(sr);
                return;
            }
            case 2: {
                this.writeEndElement();
                return;
            }
            case 6: {
                this.writeSpace(sr.getTextCharacters(), sr.getTextStart(), sr.getTextLength());
                return;
            }
            case 12: {
                this.writeCData(sr.getTextCharacters(), sr.getTextStart(), sr.getTextLength());
                return;
            }
            case 4: {
                this.writeCharacters(sr.getTextCharacters(), sr.getTextStart(), sr.getTextLength());
                return;
            }
            case 5: {
                this.writeComment(sr.getText());
                return;
            }
            case 3: {
                this.writeProcessingInstruction(sr.getPITarget(), sr.getPIData());
                return;
            }
            case 11: {
                DTDInfo info = sr.getDTDInfo();
                if (info == null) {
                    throw new XMLStreamException("Current state DOCTYPE, but not DTDInfo Object returned -- reader doesn't support DTDs?");
                }
                this.writeDTD(info.getDTDRootName(), info.getDTDSystemId(), info.getDTDPublicId(), info.getDTDInternalSubset());
                return;
            }
            case 9: {
                this.writeEntityRef(sr.getLocalName());
                return;
            }
        }
        throw new XMLStreamException("Unrecognized event type (" + sr.getEventType() + "); not sure how to copy");
    }

    @Override
    public void closeCompletely() throws XMLStreamException {
        this.close();
    }

    @Override
    public XMLValidator validateAgainst(XMLValidationSchema schema) throws XMLStreamException {
        throw new UnsupportedOperationException("Not yet implemented");
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

    protected void copyStartElement(XMLStreamReader sr) throws XMLStreamException {
        int attrCount;
        String uri;
        String prefix;
        int i;
        int nsCount = sr.getNamespaceCount();
        if (nsCount > 0) {
            for (i = 0; i < nsCount; ++i) {
                prefix = sr.getNamespacePrefix(i);
                uri = sr.getNamespaceURI(i);
                if (prefix == null || prefix.length() == 0) {
                    this.setDefaultNamespace(uri);
                    continue;
                }
                this.setPrefix(prefix, uri);
            }
        }
        this.writeStartElement(sr.getPrefix(), sr.getLocalName(), sr.getNamespaceURI());
        if (nsCount > 0) {
            for (i = 0; i < nsCount; ++i) {
                prefix = sr.getNamespacePrefix(i);
                uri = sr.getNamespaceURI(i);
                if (prefix == null || prefix.length() == 0) {
                    this.writeDefaultNamespace(uri);
                    continue;
                }
                this.writeNamespace(prefix, uri);
            }
        }
        if ((attrCount = sr.getAttributeCount()) > 0) {
            for (int i2 = 0; i2 < attrCount; ++i2) {
                this.writeAttribute(sr.getAttributePrefix(i2), sr.getAttributeNamespace(i2), sr.getAttributeLocalName(i2), sr.getAttributeValue(i2));
            }
        }
    }

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
}

