/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.org_jp_gr_xml.sax;

import com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom.DOMVisitorException;
import com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom.IDOMVisitor;
import com.ctc.wstx.shaded.msv.org_jp_gr_xml.dom.UDOM;
import com.ctc.wstx.shaded.msv.org_jp_gr_xml.sax.DeclHandlerBase;
import com.ctc.wstx.shaded.msv.org_jp_gr_xml.sax.LexicalHandlerBase;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.NamespaceSupport;

public class DOMSAXProducerVisitor
implements IDOMVisitor {
    private String systemID_;
    private String publicID_;
    private DTDHandler dtd_;
    private ContentHandler content_;
    private DeclHandler decl_;
    private LexicalHandler lexical_;
    private ErrorHandler error_;
    private NamespaceSupport namespace_;
    private boolean throwException_;

    public DOMSAXProducerVisitor() {
        DefaultHandler defaulthandler = new DefaultHandler();
        this.dtd_ = defaulthandler;
        this.content_ = defaulthandler;
        this.error_ = defaulthandler;
        this.lexical_ = new LexicalHandlerBase();
        this.decl_ = new DeclHandlerBase();
        this.namespace_ = new NamespaceSupport();
        this.throwException_ = false;
    }

    public void setSystemID(String s) {
        this.systemID_ = s;
    }

    public void setPublicID(String s) {
        this.publicID_ = s;
    }

    public void setDTDHandler(DTDHandler dtdhandler) {
        this.dtd_ = dtdhandler;
    }

    public void setContentHandler(ContentHandler contenthandler) {
        this.content_ = contenthandler;
    }

    public void setLexicalHandler(LexicalHandler lexicalhandler) {
        this.lexical_ = lexicalhandler;
    }

    public void setDeclHandler(DeclHandler declhandler) {
        this.decl_ = declhandler;
    }

    public void setErrorHandler(ErrorHandler errorhandler) {
        this.error_ = errorhandler;
    }

    public void emulateStartDocument() {
        try {
            this._handleLocator();
            this.content_.startDocument();
        }
        catch (SAXException saxexception) {
            this._errorReport(saxexception);
        }
    }

    public void emulateEndDocument() {
        try {
            this.content_.endDocument();
        }
        catch (SAXException saxexception) {
            this._errorReport(saxexception);
        }
    }

    public void throwException(boolean flag) {
        this.throwException_ = flag;
    }

    public boolean enter(Element element) {
        try {
            this.namespace_.pushContext();
            String s = element.getNamespaceURI();
            if (s == null) {
                s = "";
            }
            String s1 = element.getLocalName();
            String s2 = element.getTagName();
            NamedNodeMap namednodemap = element.getAttributes();
            AttributesImpl attributesimpl = new AttributesImpl();
            int i = namednodemap.getLength();
            for (int j = 0; j < i; ++j) {
                Attr attr = (Attr)namednodemap.item(j);
                String s3 = attr.getNamespaceURI();
                if (s3 == null) {
                    s3 = "";
                }
                String s4 = attr.getLocalName();
                String s5 = attr.getName();
                String s6 = attr.getValue();
                if (s5.startsWith("xmlns")) {
                    int k = s5.indexOf(58);
                    String s7 = k == -1 ? "" : s5.substring(k + 1);
                    if (!this.namespace_.declarePrefix(s7, s6)) {
                        this._errorReport("bad prefix = " + s7);
                        continue;
                    }
                    this.content_.startPrefixMapping(s7, s6);
                    continue;
                }
                attributesimpl.addAttribute(s3, s4, s5, "CDATA", s6);
            }
            this.content_.startElement(s, s1, s2, attributesimpl);
        }
        catch (SAXException saxexception) {
            this._errorReport(saxexception);
        }
        return true;
    }

    public boolean enter(Attr attr) {
        return false;
    }

    public boolean enter(Text text) {
        try {
            String s = text.getData();
            this.content_.characters(s.toCharArray(), 0, s.length());
        }
        catch (SAXException saxexception) {
            this._errorReport(saxexception);
        }
        return false;
    }

    public boolean enter(CDATASection cdatasection) {
        try {
            this.lexical_.startCDATA();
            String s = cdatasection.getData();
            this.content_.characters(s.toCharArray(), 0, s.length());
            this.lexical_.endCDATA();
        }
        catch (SAXException saxexception) {
            this._errorReport(saxexception);
        }
        return false;
    }

    public boolean enter(EntityReference entityreference) {
        try {
            this.lexical_.startEntity(entityreference.getNodeName());
        }
        catch (SAXException saxexception) {
            this._errorReport(saxexception);
        }
        return true;
    }

    public boolean enter(Entity entity) {
        return false;
    }

    public boolean enter(ProcessingInstruction processinginstruction) {
        try {
            this.content_.processingInstruction(processinginstruction.getTarget(), processinginstruction.getData());
        }
        catch (SAXException saxexception) {
            this._errorReport(saxexception);
        }
        return false;
    }

    public boolean enter(Comment comment) {
        try {
            String s = comment.getData();
            this.lexical_.comment(s.toCharArray(), 0, s.length());
        }
        catch (SAXException saxexception) {
            this._errorReport(saxexception);
        }
        return false;
    }

    public boolean enter(Document document) {
        try {
            this._handleLocator();
            this.content_.startDocument();
            this._handleDoctype(document.getDoctype());
        }
        catch (SAXException saxexception) {
            this._errorReport(saxexception);
        }
        return true;
    }

    private void _handleLocator() {
        if (this.systemID_ == null && this.publicID_ == null) {
            return;
        }
        this._locatorEvent(this.systemID_, this.publicID_);
    }

    private void _locatorEvent(String s, String s1) {
        LocatorImpl locatorimpl = new LocatorImpl();
        locatorimpl.setSystemId(this.systemID_);
        locatorimpl.setPublicId(this.publicID_);
        locatorimpl.setLineNumber(-1);
        locatorimpl.setColumnNumber(-1);
        this.content_.setDocumentLocator(locatorimpl);
    }

    private void _handleDoctype(DocumentType documenttype) {
        try {
            if (documenttype == null) {
                return;
            }
            String s = documenttype.getSystemId();
            String s1 = documenttype.getPublicId();
            String s2 = documenttype.getInternalSubset();
            if (s != null) {
                this.lexical_.startDTD(documenttype.getName(), s1, s);
                if (s2 == null) {
                    this.lexical_.endDTD();
                    this._handleEntities(documenttype);
                } else {
                    this._handleEntities(documenttype);
                    this.lexical_.endDTD();
                }
            } else {
                this._handleEntities(documenttype);
            }
        }
        catch (SAXException saxexception) {
            this._errorReport(saxexception);
        }
    }

    private void _handleEntities(DocumentType documenttype) {
        try {
            NamedNodeMap namednodemap = documenttype.getEntities();
            int i = namednodemap.getLength();
            for (int j = 0; j < i; ++j) {
                Entity entity = (Entity)namednodemap.item(j);
                String s = entity.getPublicId();
                String s1 = entity.getSystemId();
                String s2 = entity.getNotationName();
                if (s != null || s1 != null) {
                    this._handleExternalEntity(entity.getNodeName(), s, s1, s2);
                    continue;
                }
                this._handleInternalEntity(entity);
            }
            NamedNodeMap namednodemap1 = documenttype.getNotations();
            int k = namednodemap1.getLength();
            for (int l = 0; l < k; ++l) {
                Notation notation = (Notation)namednodemap1.item(l);
                String s3 = notation.getPublicId();
                String s4 = notation.getSystemId();
                this.dtd_.notationDecl(notation.getNodeName(), s3, s4);
            }
        }
        catch (SAXException saxexception) {
            this._errorReport(saxexception);
        }
    }

    private void _handleExternalEntity(String s, String s1, String s2, String s3) {
        try {
            if (s3 == null) {
                this.decl_.externalEntityDecl(s, s1, s2);
            } else {
                this.dtd_.unparsedEntityDecl(s, s1, s2, s3);
            }
        }
        catch (SAXException saxexception) {
            this._errorReport(saxexception);
        }
    }

    private void _handleInternalEntity(Entity entity) {
        try {
            this.decl_.internalEntityDecl(entity.getNodeName(), UDOM.getXMLText(entity));
        }
        catch (SAXException saxexception) {
            this._errorReport(saxexception);
        }
    }

    public boolean enter(DocumentType documenttype) {
        return false;
    }

    public boolean enter(DocumentFragment documentfragment) {
        return true;
    }

    public boolean enter(Notation notation) {
        return false;
    }

    public boolean enter(Node node) {
        return false;
    }

    public void leave(Element element) {
        try {
            String s = element.getNamespaceURI();
            if (s == null) {
                s = "";
            }
            String s1 = element.getLocalName();
            String s2 = element.getTagName();
            this.content_.endElement(s, s1, s2);
            this.namespace_.popContext();
        }
        catch (SAXException saxexception) {
            this._errorReport(saxexception);
        }
    }

    public void leave(Attr attr) {
    }

    public void leave(Text text) {
    }

    public void leave(CDATASection cdatasection) {
    }

    public void leave(EntityReference entityreference) {
        try {
            this.lexical_.endEntity(entityreference.getNodeName());
        }
        catch (SAXException saxexception) {
            this._errorReport(saxexception);
        }
    }

    public void leave(Entity entity) {
    }

    public void leave(ProcessingInstruction processinginstruction) {
    }

    public void leave(Comment comment) {
    }

    public void leave(Document document) {
        try {
            this.content_.endDocument();
        }
        catch (SAXException saxexception) {
            this._errorReport(saxexception);
        }
    }

    public void leave(DocumentType documenttype) {
    }

    public void leave(DocumentFragment documentfragment) {
    }

    public void leave(Notation notation) {
    }

    public void leave(Node node) {
    }

    private void _errorReport(String s) throws DOMVisitorException {
        this._errorReport(new SAXParseException(s, this.publicID_, this.systemID_, -1, -1));
    }

    private void _errorReport(SAXException saxexception) throws DOMVisitorException {
        try {
            SAXParseException saxparseexception = saxexception instanceof SAXParseException ? (SAXParseException)saxexception : new SAXParseException(saxexception.getMessage(), this.publicID_, this.systemID_, -1, -1, saxexception);
            this.error_.fatalError(saxparseexception);
            if (this.throwException_) {
                throw new DOMVisitorException(saxexception);
            }
        }
        catch (SAXException sAXException) {
            // empty catch block
        }
    }
}

