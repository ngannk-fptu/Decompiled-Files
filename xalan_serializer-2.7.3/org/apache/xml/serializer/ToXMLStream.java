/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serializer;

import java.io.IOException;
import java.io.Writer;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import org.apache.xml.serializer.CharInfo;
import org.apache.xml.serializer.Encodings;
import org.apache.xml.serializer.NamespaceMappings;
import org.apache.xml.serializer.ToStream;
import org.apache.xml.serializer.utils.Utils;
import org.xml.sax.SAXException;

public class ToXMLStream
extends ToStream {
    private CharInfo m_xmlcharInfo;

    public ToXMLStream() {
        this.m_charInfo = this.m_xmlcharInfo = CharInfo.getCharInfo(CharInfo.XML_ENTITIES_RESOURCE, "xml");
        this.initCDATA();
        this.m_prefixMap = new NamespaceMappings();
    }

    public void CopyFrom(ToXMLStream xmlListener) {
        this.setWriter(xmlListener.m_writer);
        String encoding = xmlListener.getEncoding();
        this.setEncoding(encoding);
        this.setOmitXMLDeclaration(xmlListener.getOmitXMLDeclaration());
        this.m_ispreserve = xmlListener.m_ispreserve;
        this.m_preserves = xmlListener.m_preserves;
        this.m_isprevtext = xmlListener.m_isprevtext;
        this.m_doIndent = xmlListener.m_doIndent;
        this.setIndentAmount(xmlListener.getIndentAmount());
        this.m_startNewLine = xmlListener.m_startNewLine;
        this.m_needToOutputDocTypeDecl = xmlListener.m_needToOutputDocTypeDecl;
        this.setDoctypeSystem(xmlListener.getDoctypeSystem());
        this.setDoctypePublic(xmlListener.getDoctypePublic());
        this.setStandalone(xmlListener.getStandalone());
        this.setMediaType(xmlListener.getMediaType());
        this.m_encodingInfo = xmlListener.m_encodingInfo;
        this.m_spaceBeforeClose = xmlListener.m_spaceBeforeClose;
        this.m_cdataStartCalled = xmlListener.m_cdataStartCalled;
    }

    @Override
    public void startDocumentInternal() throws SAXException {
        if (this.m_needToCallStartDocument) {
            super.startDocumentInternal();
            this.m_needToCallStartDocument = false;
            if (this.m_inEntityRef) {
                return;
            }
            this.m_needToOutputDocTypeDecl = true;
            this.m_startNewLine = false;
            String version = this.getXMLVersion();
            if (!this.getOmitXMLDeclaration()) {
                String encoding = Encodings.getMimeEncoding(this.getEncoding());
                String standalone = this.m_standaloneWasSpecified ? " standalone=\"" + this.getStandalone() + "\"" : "";
                try {
                    Writer writer = this.m_writer;
                    writer.write("<?xml version=\"");
                    writer.write(version);
                    writer.write("\" encoding=\"");
                    writer.write(encoding);
                    writer.write(34);
                    writer.write(standalone);
                    writer.write("?>");
                    if (this.m_doIndent && (this.m_standaloneWasSpecified || this.getDoctypePublic() != null || this.getDoctypeSystem() != null)) {
                        writer.write(this.m_lineSep, 0, this.m_lineSepLen);
                    }
                }
                catch (IOException e) {
                    throw new SAXException(e);
                }
            }
        }
    }

    @Override
    public void endDocument() throws SAXException {
        this.flushPending();
        if (this.m_doIndent && !this.m_isprevtext) {
            try {
                this.outputLineSep();
            }
            catch (IOException e) {
                throw new SAXException(e);
            }
        }
        this.flushWriter();
        if (this.m_tracer != null) {
            super.fireEndDoc();
        }
    }

    public void startPreserving() throws SAXException {
        this.m_preserves.push(true);
        this.m_ispreserve = true;
    }

    public void endPreserving() throws SAXException {
        this.m_ispreserve = this.m_preserves.isEmpty() ? false : this.m_preserves.pop();
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        if (this.m_inEntityRef) {
            return;
        }
        this.flushPending();
        if (target.equals("javax.xml.transform.disable-output-escaping")) {
            this.startNonEscaping();
        } else if (target.equals("javax.xml.transform.enable-output-escaping")) {
            this.endNonEscaping();
        } else {
            try {
                int indexOfQLT;
                if (this.m_elemContext.m_startTagOpen) {
                    this.closeStartTag();
                    this.m_elemContext.m_startTagOpen = false;
                } else if (this.m_needToCallStartDocument) {
                    this.startDocumentInternal();
                }
                if (this.shouldIndent()) {
                    this.indent();
                }
                Writer writer = this.m_writer;
                writer.write("<?");
                writer.write(target);
                if (data.length() > 0 && !Character.isSpaceChar(data.charAt(0))) {
                    writer.write(32);
                }
                if ((indexOfQLT = data.indexOf("?>")) >= 0) {
                    if (indexOfQLT > 0) {
                        writer.write(data.substring(0, indexOfQLT));
                    }
                    writer.write("? >");
                    if (indexOfQLT + 2 < data.length()) {
                        writer.write(data.substring(indexOfQLT + 2));
                    }
                } else {
                    writer.write(data);
                }
                writer.write(63);
                writer.write(62);
                this.m_startNewLine = true;
            }
            catch (IOException e) {
                throw new SAXException(e);
            }
        }
        if (this.m_tracer != null) {
            super.fireEscapingEvent(target, data);
        }
    }

    @Override
    public void entityReference(String name) throws SAXException {
        if (this.m_elemContext.m_startTagOpen) {
            this.closeStartTag();
            this.m_elemContext.m_startTagOpen = false;
        }
        try {
            if (this.shouldIndent()) {
                this.indent();
            }
            Writer writer = this.m_writer;
            writer.write(38);
            writer.write(name);
            writer.write(59);
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
        if (this.m_tracer != null) {
            super.fireEntityReference(name);
        }
    }

    @Override
    public void addUniqueAttribute(String name, String value, int flags) throws SAXException {
        if (this.m_elemContext.m_startTagOpen) {
            try {
                String patchedName = this.patchName(name);
                Writer writer = this.m_writer;
                if ((flags & 1) > 0 && this.m_xmlcharInfo.onlyQuotAmpLtGt) {
                    writer.write(32);
                    writer.write(patchedName);
                    writer.write("=\"");
                    writer.write(value);
                    writer.write(34);
                } else {
                    writer.write(32);
                    writer.write(patchedName);
                    writer.write("=\"");
                    this.writeAttrString(writer, value, this.getEncoding());
                    writer.write(34);
                }
            }
            catch (IOException e) {
                throw new SAXException(e);
            }
        }
    }

    @Override
    public void addAttribute(String uri, String localName, String rawName, String type, String value, boolean xslAttribute) throws SAXException {
        if (this.m_elemContext.m_startTagOpen) {
            String prefixUsed;
            boolean was_added = this.addAttributeAlways(uri, localName, rawName, type, value, xslAttribute);
            if (was_added && !xslAttribute && !rawName.startsWith("xmlns") && (prefixUsed = this.ensureAttributesNamespaceIsDeclared(uri, localName, rawName)) != null && rawName != null && !rawName.startsWith(prefixUsed)) {
                rawName = prefixUsed + ":" + localName;
            }
            this.addAttributeAlways(uri, localName, rawName, type, value, xslAttribute);
        } else {
            String msg = Utils.messages.createMessage("ER_ILLEGAL_ATTRIBUTE_POSITION", new Object[]{localName});
            try {
                Transformer tran = super.getTransformer();
                ErrorListener errHandler = tran.getErrorListener();
                if (null != errHandler && this.m_sourceLocator != null) {
                    errHandler.warning(new TransformerException(msg, this.m_sourceLocator));
                } else {
                    System.out.println(msg);
                }
            }
            catch (TransformerException e) {
                SAXException se = new SAXException(e);
                throw se;
            }
        }
    }

    @Override
    public void endElement(String elemName) throws SAXException {
        this.endElement(null, null, elemName);
    }

    @Override
    public void namespaceAfterStartElement(String prefix, String uri) throws SAXException {
        String prefix1;
        if (this.m_elemContext.m_elementURI == null && (prefix1 = ToXMLStream.getPrefixPart(this.m_elemContext.m_elementName)) == null && "".equals(prefix)) {
            this.m_elemContext.m_elementURI = uri;
        }
        this.startPrefixMapping(prefix, uri, false);
    }

    protected boolean pushNamespace(String prefix, String uri) {
        try {
            if (this.m_prefixMap.pushNamespace(prefix, uri, this.m_elemContext.m_currentElemDepth)) {
                this.startPrefixMapping(prefix, uri);
                return true;
            }
        }
        catch (SAXException sAXException) {
            // empty catch block
        }
        return false;
    }

    @Override
    public boolean reset() {
        boolean wasReset = false;
        if (super.reset()) {
            wasReset = true;
        }
        return wasReset;
    }

    private void resetToXMLStream() {
    }

    private String getXMLVersion() {
        String xmlVersion = this.getVersion();
        if (xmlVersion == null || xmlVersion.equals("1.0")) {
            xmlVersion = "1.0";
        } else if (xmlVersion.equals("1.1")) {
            xmlVersion = "1.1";
        } else {
            String msg = Utils.messages.createMessage("ER_XML_VERSION_NOT_SUPPORTED", new Object[]{xmlVersion});
            try {
                Transformer tran = super.getTransformer();
                ErrorListener errHandler = tran.getErrorListener();
                if (null != errHandler && this.m_sourceLocator != null) {
                    errHandler.warning(new TransformerException(msg, this.m_sourceLocator));
                } else {
                    System.out.println(msg);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
            xmlVersion = "1.0";
        }
        return xmlVersion;
    }
}

