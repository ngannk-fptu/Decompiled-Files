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

    protected Stax2WriterAdapter(XMLStreamWriter xMLStreamWriter) {
        super(xMLStreamWriter);
        this.mDelegate = xMLStreamWriter;
        Object object = xMLStreamWriter.getProperty("javax.xml.stream.isRepairingNamespaces");
        this.mNsRepairing = object instanceof Boolean && (Boolean)object != false;
    }

    public static XMLStreamWriter2 wrapIfNecessary(XMLStreamWriter xMLStreamWriter) {
        if (xMLStreamWriter instanceof XMLStreamWriter2) {
            return (XMLStreamWriter2)xMLStreamWriter;
        }
        return new Stax2WriterAdapter(xMLStreamWriter);
    }

    public void writeBoolean(boolean bl) throws XMLStreamException {
        this.mDelegate.writeCharacters(bl ? "true" : "false");
    }

    public void writeInt(int n) throws XMLStreamException {
        this.mDelegate.writeCharacters(String.valueOf(n));
    }

    public void writeLong(long l) throws XMLStreamException {
        this.mDelegate.writeCharacters(String.valueOf(l));
    }

    public void writeFloat(float f) throws XMLStreamException {
        this.mDelegate.writeCharacters(String.valueOf(f));
    }

    public void writeDouble(double d) throws XMLStreamException {
        this.mDelegate.writeCharacters(String.valueOf(d));
    }

    public void writeInteger(BigInteger bigInteger) throws XMLStreamException {
        this.mDelegate.writeCharacters(bigInteger.toString());
    }

    public void writeDecimal(BigDecimal bigDecimal) throws XMLStreamException {
        this.mDelegate.writeCharacters(bigDecimal.toString());
    }

    public void writeQName(QName qName) throws XMLStreamException {
        this.mDelegate.writeCharacters(this.serializeQNameValue(qName));
    }

    public void writeIntArray(int[] nArray, int n, int n2) throws XMLStreamException {
        this.mDelegate.writeCharacters(this.getValueEncoder().encodeAsString(nArray, n, n2));
    }

    public void writeLongArray(long[] lArray, int n, int n2) throws XMLStreamException {
        this.mDelegate.writeCharacters(this.getValueEncoder().encodeAsString(lArray, n, n2));
    }

    public void writeFloatArray(float[] fArray, int n, int n2) throws XMLStreamException {
        this.mDelegate.writeCharacters(this.getValueEncoder().encodeAsString(fArray, n, n2));
    }

    public void writeDoubleArray(double[] dArray, int n, int n2) throws XMLStreamException {
        this.mDelegate.writeCharacters(this.getValueEncoder().encodeAsString(dArray, n, n2));
    }

    public void writeBinary(Base64Variant base64Variant, byte[] byArray, int n, int n2) throws XMLStreamException {
        this.mDelegate.writeCharacters(this.getValueEncoder().encodeAsString(base64Variant, byArray, n, n2));
    }

    public void writeBinary(byte[] byArray, int n, int n2) throws XMLStreamException {
        this.writeBinary(Base64Variants.getDefaultVariant(), byArray, n, n2);
    }

    public void writeBooleanAttribute(String string, String string2, String string3, boolean bl) throws XMLStreamException {
        this.mDelegate.writeAttribute(string, string2, string3, bl ? "true" : "false");
    }

    public void writeIntAttribute(String string, String string2, String string3, int n) throws XMLStreamException {
        this.mDelegate.writeAttribute(string, string2, string3, String.valueOf(n));
    }

    public void writeLongAttribute(String string, String string2, String string3, long l) throws XMLStreamException {
        this.mDelegate.writeAttribute(string, string2, string3, String.valueOf(l));
    }

    public void writeFloatAttribute(String string, String string2, String string3, float f) throws XMLStreamException {
        this.mDelegate.writeAttribute(string, string2, string3, String.valueOf(f));
    }

    public void writeDoubleAttribute(String string, String string2, String string3, double d) throws XMLStreamException {
        this.mDelegate.writeAttribute(string, string2, string3, String.valueOf(d));
    }

    public void writeIntegerAttribute(String string, String string2, String string3, BigInteger bigInteger) throws XMLStreamException {
        this.mDelegate.writeAttribute(string, string2, string3, bigInteger.toString());
    }

    public void writeDecimalAttribute(String string, String string2, String string3, BigDecimal bigDecimal) throws XMLStreamException {
        this.mDelegate.writeAttribute(string, string2, string3, bigDecimal.toString());
    }

    public void writeQNameAttribute(String string, String string2, String string3, QName qName) throws XMLStreamException {
        this.mDelegate.writeAttribute(string, string2, string3, this.serializeQNameValue(qName));
    }

    public void writeIntArrayAttribute(String string, String string2, String string3, int[] nArray) throws XMLStreamException {
        this.mDelegate.writeAttribute(string, string2, string3, this.getValueEncoder().encodeAsString(nArray, 0, nArray.length));
    }

    public void writeLongArrayAttribute(String string, String string2, String string3, long[] lArray) throws XMLStreamException {
        this.mDelegate.writeAttribute(string, string2, string3, this.getValueEncoder().encodeAsString(lArray, 0, lArray.length));
    }

    public void writeFloatArrayAttribute(String string, String string2, String string3, float[] fArray) throws XMLStreamException {
        this.mDelegate.writeAttribute(string, string2, string3, this.getValueEncoder().encodeAsString(fArray, 0, fArray.length));
    }

    public void writeDoubleArrayAttribute(String string, String string2, String string3, double[] dArray) throws XMLStreamException {
        this.mDelegate.writeAttribute(string, string2, string3, this.getValueEncoder().encodeAsString(dArray, 0, dArray.length));
    }

    public void writeBinaryAttribute(String string, String string2, String string3, byte[] byArray) throws XMLStreamException {
        this.writeBinaryAttribute(Base64Variants.getDefaultVariant(), string, string2, string3, byArray);
    }

    public void writeBinaryAttribute(Base64Variant base64Variant, String string, String string2, String string3, byte[] byArray) throws XMLStreamException {
        this.mDelegate.writeAttribute(string, string2, string3, this.getValueEncoder().encodeAsString(base64Variant, byArray, 0, byArray.length));
    }

    public boolean isPropertySupported(String string) {
        return false;
    }

    public boolean setProperty(String string, Object object) {
        throw new IllegalArgumentException("No settable property '" + string + "'");
    }

    public XMLStreamLocation2 getLocation() {
        return null;
    }

    public String getEncoding() {
        return this.mEncoding;
    }

    public void writeCData(char[] cArray, int n, int n2) throws XMLStreamException {
        this.writeCData(new String(cArray, n, n2));
    }

    public void writeDTD(String string, String string2, String string3, String string4) throws XMLStreamException {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<!DOCTYPE");
        stringBuffer.append(string);
        if (string2 != null) {
            if (string3 != null) {
                stringBuffer.append(" PUBLIC \"");
                stringBuffer.append(string3);
                stringBuffer.append("\" \"");
            } else {
                stringBuffer.append(" SYSTEM \"");
            }
            stringBuffer.append(string2);
            stringBuffer.append('\"');
        }
        if (string4 != null && string4.length() > 0) {
            stringBuffer.append(" [");
            stringBuffer.append(string4);
            stringBuffer.append(']');
        }
        stringBuffer.append('>');
        this.writeDTD(stringBuffer.toString());
    }

    public void writeFullEndElement() throws XMLStreamException {
        this.mDelegate.writeCharacters("");
        this.mDelegate.writeEndElement();
    }

    public void writeSpace(String string) throws XMLStreamException {
        this.writeRaw(string);
    }

    public void writeSpace(char[] cArray, int n, int n2) throws XMLStreamException {
        this.writeRaw(cArray, n, n2);
    }

    public void writeStartDocument(String string, String string2, boolean bl) throws XMLStreamException {
        this.writeStartDocument(string2, string);
    }

    public void writeRaw(String string) throws XMLStreamException {
        this.writeRaw(string, 0, string.length());
    }

    public void writeRaw(String string, int n, int n2) throws XMLStreamException {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void writeRaw(char[] cArray, int n, int n2) throws XMLStreamException {
        this.writeRaw(new String(cArray, n, n2));
    }

    public void copyEventFromReader(XMLStreamReader2 xMLStreamReader2, boolean bl) throws XMLStreamException {
        switch (xMLStreamReader2.getEventType()) {
            case 7: {
                String string = xMLStreamReader2.getVersion();
                if (string != null && string.length() != 0) {
                    if (xMLStreamReader2.standaloneSet()) {
                        this.writeStartDocument(xMLStreamReader2.getVersion(), xMLStreamReader2.getCharacterEncodingScheme(), xMLStreamReader2.isStandalone());
                    } else {
                        this.writeStartDocument(xMLStreamReader2.getCharacterEncodingScheme(), xMLStreamReader2.getVersion());
                    }
                }
                return;
            }
            case 8: {
                this.writeEndDocument();
                return;
            }
            case 1: {
                this.copyStartElement(xMLStreamReader2);
                return;
            }
            case 2: {
                this.writeEndElement();
                return;
            }
            case 6: {
                this.writeSpace(xMLStreamReader2.getTextCharacters(), xMLStreamReader2.getTextStart(), xMLStreamReader2.getTextLength());
                return;
            }
            case 12: {
                this.writeCData(xMLStreamReader2.getTextCharacters(), xMLStreamReader2.getTextStart(), xMLStreamReader2.getTextLength());
                return;
            }
            case 4: {
                this.writeCharacters(xMLStreamReader2.getTextCharacters(), xMLStreamReader2.getTextStart(), xMLStreamReader2.getTextLength());
                return;
            }
            case 5: {
                this.writeComment(xMLStreamReader2.getText());
                return;
            }
            case 3: {
                this.writeProcessingInstruction(xMLStreamReader2.getPITarget(), xMLStreamReader2.getPIData());
                return;
            }
            case 11: {
                DTDInfo dTDInfo = xMLStreamReader2.getDTDInfo();
                if (dTDInfo == null) {
                    throw new XMLStreamException("Current state DOCTYPE, but not DTDInfo Object returned -- reader doesn't support DTDs?");
                }
                this.writeDTD(dTDInfo.getDTDRootName(), dTDInfo.getDTDSystemId(), dTDInfo.getDTDPublicId(), dTDInfo.getDTDInternalSubset());
                return;
            }
            case 9: {
                this.writeEntityRef(xMLStreamReader2.getLocalName());
                return;
            }
        }
        throw new XMLStreamException("Unrecognized event type (" + xMLStreamReader2.getEventType() + "); not sure how to copy");
    }

    public void closeCompletely() throws XMLStreamException {
        this.close();
    }

    public XMLValidator validateAgainst(XMLValidationSchema xMLValidationSchema) throws XMLStreamException {
        throw new UnsupportedOperationException("Not yet implemented");
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

    protected void copyStartElement(XMLStreamReader xMLStreamReader) throws XMLStreamException {
        String string;
        String string2;
        int n;
        int n2 = xMLStreamReader.getNamespaceCount();
        if (n2 > 0) {
            for (n = 0; n < n2; ++n) {
                string2 = xMLStreamReader.getNamespacePrefix(n);
                string = xMLStreamReader.getNamespaceURI(n);
                if (string2 == null || string2.length() == 0) {
                    this.setDefaultNamespace(string);
                    continue;
                }
                this.setPrefix(string2, string);
            }
        }
        this.writeStartElement(xMLStreamReader.getPrefix(), xMLStreamReader.getLocalName(), xMLStreamReader.getNamespaceURI());
        if (n2 > 0) {
            for (n = 0; n < n2; ++n) {
                string2 = xMLStreamReader.getNamespacePrefix(n);
                string = xMLStreamReader.getNamespaceURI(n);
                if (string2 == null || string2.length() == 0) {
                    this.writeDefaultNamespace(string);
                    continue;
                }
                this.writeNamespace(string2, string);
            }
        }
        if ((n = xMLStreamReader.getAttributeCount()) > 0) {
            for (int i = 0; i < n; ++i) {
                this.writeAttribute(xMLStreamReader.getAttributePrefix(i), xMLStreamReader.getAttributeNamespace(i), xMLStreamReader.getAttributeLocalName(i), xMLStreamReader.getAttributeValue(i));
            }
        }
    }

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
}

