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

    @Override
    public boolean isPropertySupported(String name) {
        return false;
    }

    @Override
    public boolean setProperty(String name, Object value) {
        throw new IllegalArgumentException("No settable property '" + name + "'");
    }

    @Override
    public abstract XMLStreamLocation2 getLocation();

    @Override
    public abstract String getEncoding();

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
        this.writeCharacters("");
        this.writeEndElement();
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
    public abstract void writeStartDocument(String var1, String var2, boolean var3) throws XMLStreamException;

    @Override
    public void writeRaw(String text) throws XMLStreamException {
        this.writeRaw(text, 0, text.length());
    }

    @Override
    public abstract void writeRaw(String var1, int var2, int var3) throws XMLStreamException;

    @Override
    public abstract void writeRaw(char[] var1, int var2, int var3) throws XMLStreamException;

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
}

