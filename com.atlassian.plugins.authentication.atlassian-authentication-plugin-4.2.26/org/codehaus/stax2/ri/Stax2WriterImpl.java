/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.codehaus.stax2.DTDInfo;
import org.codehaus.stax2.XMLStreamLocation2;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.validation.ValidationProblemHandler;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;

public abstract class Stax2WriterImpl
implements XMLStreamWriter2,
XMLStreamConstants {
    protected Stax2WriterImpl() {
    }

    public boolean isPropertySupported(String string) {
        return false;
    }

    public boolean setProperty(String string, Object object) {
        throw new IllegalArgumentException("No settable property '" + string + "'");
    }

    public abstract XMLStreamLocation2 getLocation();

    public abstract String getEncoding();

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
        this.writeCharacters("");
        this.writeEndElement();
    }

    public void writeSpace(String string) throws XMLStreamException {
        this.writeRaw(string);
    }

    public void writeSpace(char[] cArray, int n, int n2) throws XMLStreamException {
        this.writeRaw(cArray, n, n2);
    }

    public abstract void writeStartDocument(String var1, String var2, boolean var3) throws XMLStreamException;

    public void writeRaw(String string) throws XMLStreamException {
        this.writeRaw(string, 0, string.length());
    }

    public abstract void writeRaw(String var1, int var2, int var3) throws XMLStreamException;

    public abstract void writeRaw(char[] var1, int var2, int var3) throws XMLStreamException;

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
}

