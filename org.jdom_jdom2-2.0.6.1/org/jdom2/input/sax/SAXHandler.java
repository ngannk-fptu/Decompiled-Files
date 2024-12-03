/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.input.sax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jdom2.Attribute;
import org.jdom2.AttributeType;
import org.jdom2.CDATA;
import org.jdom2.Comment;
import org.jdom2.DefaultJDOMFactory;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.EntityRef;
import org.jdom2.JDOMFactory;
import org.jdom2.Namespace;
import org.jdom2.Parent;
import org.jdom2.ProcessingInstruction;
import org.jdom2.Text;
import org.jdom2.input.sax.TextBuffer;
import org.xml.sax.Attributes;
import org.xml.sax.DTDHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.Attributes2;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class SAXHandler
extends DefaultHandler
implements LexicalHandler,
DeclHandler,
DTDHandler {
    private final JDOMFactory factory;
    private final List<Namespace> declaredNamespaces = new ArrayList<Namespace>(32);
    private final StringBuilder internalSubset = new StringBuilder();
    private final TextBuffer textBuffer = new TextBuffer();
    private final Map<String, String[]> externalEntities = new HashMap<String, String[]>();
    private Document currentDocument = null;
    private Element currentElement = null;
    private Locator currentLocator = null;
    private boolean atRoot = true;
    private boolean inDTD = false;
    private boolean inInternalSubset = false;
    private boolean previousCDATA = false;
    private boolean inCDATA = false;
    private boolean expand = true;
    private boolean suppress = false;
    private int entityDepth = 0;
    private boolean ignoringWhite = false;
    private boolean ignoringBoundaryWhite = false;
    private int lastline = 0;
    private int lastcol = 0;

    public SAXHandler() {
        this(null);
    }

    public SAXHandler(JDOMFactory factory) {
        this.factory = factory != null ? factory : new DefaultJDOMFactory();
        this.reset();
    }

    protected void resetSubCLass() {
    }

    public final void reset() {
        this.currentLocator = null;
        this.currentDocument = this.factory.document(null);
        this.currentElement = null;
        this.atRoot = true;
        this.inDTD = false;
        this.inInternalSubset = false;
        this.previousCDATA = false;
        this.inCDATA = false;
        this.expand = true;
        this.suppress = false;
        this.entityDepth = 0;
        this.declaredNamespaces.clear();
        this.internalSubset.setLength(0);
        this.textBuffer.clear();
        this.externalEntities.clear();
        this.ignoringWhite = false;
        this.ignoringBoundaryWhite = false;
        this.resetSubCLass();
    }

    protected void pushElement(Element element) {
        if (this.atRoot) {
            this.currentDocument.setRootElement(element);
            this.atRoot = false;
        } else {
            this.factory.addContent(this.currentElement, element);
        }
        this.currentElement = element;
    }

    public Document getDocument() {
        return this.currentDocument;
    }

    public JDOMFactory getFactory() {
        return this.factory;
    }

    public void setExpandEntities(boolean expand) {
        this.expand = expand;
    }

    public boolean getExpandEntities() {
        return this.expand;
    }

    public void setIgnoringElementContentWhitespace(boolean ignoringWhite) {
        this.ignoringWhite = ignoringWhite;
    }

    public void setIgnoringBoundaryWhitespace(boolean ignoringBoundaryWhite) {
        this.ignoringBoundaryWhite = ignoringBoundaryWhite;
    }

    public boolean getIgnoringBoundaryWhitespace() {
        return this.ignoringBoundaryWhite;
    }

    public boolean getIgnoringElementContentWhitespace() {
        return this.ignoringWhite;
    }

    public void startDocument() {
        if (this.currentLocator != null) {
            this.currentDocument.setBaseURI(this.currentLocator.getSystemId());
        }
    }

    public void externalEntityDecl(String name, String publicID, String systemID) throws SAXException {
        this.externalEntities.put(name, new String[]{publicID, systemID});
        if (!this.inInternalSubset) {
            return;
        }
        this.internalSubset.append("  <!ENTITY ").append(name);
        this.appendExternalId(publicID, systemID);
        this.internalSubset.append(">\n");
    }

    public void attributeDecl(String eName, String aName, String type, String valueDefault, String value) {
        if (!this.inInternalSubset) {
            return;
        }
        this.internalSubset.append("  <!ATTLIST ").append(eName).append(' ').append(aName).append(' ').append(type).append(' ');
        if (valueDefault != null) {
            this.internalSubset.append(valueDefault);
        } else {
            this.internalSubset.append('\"').append(value).append('\"');
        }
        if (valueDefault != null && valueDefault.equals("#FIXED")) {
            this.internalSubset.append(" \"").append(value).append('\"');
        }
        this.internalSubset.append(">\n");
    }

    public void elementDecl(String name, String model) {
        if (!this.inInternalSubset) {
            return;
        }
        this.internalSubset.append("  <!ELEMENT ").append(name).append(' ').append(model).append(">\n");
    }

    public void internalEntityDecl(String name, String value) {
        if (!this.inInternalSubset) {
            return;
        }
        this.internalSubset.append("  <!ENTITY ");
        if (name.startsWith("%")) {
            this.internalSubset.append("% ").append(name.substring(1));
        } else {
            this.internalSubset.append(name);
        }
        this.internalSubset.append(" \"").append(value).append("\">\n");
    }

    public void processingInstruction(String target, String data) throws SAXException {
        ProcessingInstruction pi;
        if (this.suppress) {
            return;
        }
        this.flushCharacters();
        ProcessingInstruction processingInstruction = pi = this.currentLocator == null ? this.factory.processingInstruction(target, data) : this.factory.processingInstruction(this.currentLocator.getLineNumber(), this.currentLocator.getColumnNumber(), target, data);
        if (this.atRoot) {
            this.factory.addContent(this.currentDocument, pi);
        } else {
            this.factory.addContent(this.getCurrentElement(), pi);
        }
    }

    public void skippedEntity(String name) throws SAXException {
        if (name.startsWith("%")) {
            return;
        }
        this.flushCharacters();
        EntityRef er = this.currentLocator == null ? this.factory.entityRef(name) : this.factory.entityRef(this.currentLocator.getLineNumber(), this.currentLocator.getColumnNumber(), name);
        this.factory.addContent(this.getCurrentElement(), er);
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        if (this.suppress) {
            return;
        }
        Namespace ns = Namespace.getNamespace(prefix, uri);
        this.declaredNamespaces.add(ns);
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        Element element;
        if (this.suppress) {
            return;
        }
        String prefix = "";
        if (!"".equals(qName)) {
            int colon = qName.indexOf(58);
            if (colon > 0) {
                prefix = qName.substring(0, colon);
            }
            if (localName == null || localName.equals("")) {
                localName = qName.substring(colon + 1);
            }
        }
        Namespace namespace = Namespace.getNamespace(prefix, namespaceURI);
        Element element2 = element = this.currentLocator == null ? this.factory.element(localName, namespace) : this.factory.element(this.currentLocator.getLineNumber(), this.currentLocator.getColumnNumber(), localName, namespace);
        if (this.declaredNamespaces.size() > 0) {
            this.transferNamespaces(element);
        }
        this.flushCharacters();
        if (this.atRoot) {
            this.factory.setRoot(this.currentDocument, element);
            this.atRoot = false;
        } else {
            this.factory.addContent(this.getCurrentElement(), element);
        }
        this.currentElement = element;
        int len = atts.getLength();
        for (int i = 0; i < len; ++i) {
            boolean specified;
            String attPrefix = "";
            String attLocalName = atts.getLocalName(i);
            String attQName = atts.getQName(i);
            boolean bl = specified = atts instanceof Attributes2 ? ((Attributes2)atts).isSpecified(i) : true;
            if (!attQName.equals("")) {
                if (attQName.startsWith("xmlns:") || attQName.equals("xmlns")) continue;
                int attColon = attQName.indexOf(58);
                if (attColon > 0) {
                    attPrefix = attQName.substring(0, attColon);
                }
                if ("".equals(attLocalName)) {
                    attLocalName = attQName.substring(attColon + 1);
                }
            }
            AttributeType attType = AttributeType.getAttributeType(atts.getType(i));
            String attValue = atts.getValue(i);
            String attURI = atts.getURI(i);
            if ("xmlns".equals(attLocalName) || "xmlns".equals(attPrefix) || "http://www.w3.org/2000/xmlns/".equals(attURI)) continue;
            if (!"".equals(attURI) && "".equals(attPrefix)) {
                HashMap<String, Namespace> tmpmap = new HashMap<String, Namespace>();
                for (Namespace nss : element.getNamespacesInScope()) {
                    if (nss.getPrefix().length() > 0 && nss.getURI().equals(attURI)) {
                        attPrefix = nss.getPrefix();
                        break;
                    }
                    tmpmap.put(nss.getPrefix(), nss);
                }
                if ("".equals(attPrefix)) {
                    int cnt = 0;
                    String base = "attns";
                    String pfx = "attns" + cnt;
                    while (tmpmap.containsKey(pfx)) {
                        pfx = "attns" + ++cnt;
                    }
                    attPrefix = pfx;
                }
            }
            Namespace attNs = Namespace.getNamespace(attPrefix, attURI);
            Attribute attribute = this.factory.attribute(attLocalName, attValue, attType, attNs);
            if (!specified) {
                attribute.setSpecified(false);
            }
            this.factory.setAttribute(element, attribute);
        }
    }

    private void transferNamespaces(Element element) {
        for (Namespace ns : this.declaredNamespaces) {
            if (ns == element.getNamespace()) continue;
            element.addNamespaceDeclaration(ns);
        }
        this.declaredNamespaces.clear();
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        if (this.suppress || length == 0 && !this.inCDATA) {
            return;
        }
        if (this.previousCDATA != this.inCDATA) {
            this.flushCharacters();
        }
        this.textBuffer.append(ch, start, length);
        if (this.currentLocator != null) {
            this.lastline = this.currentLocator.getLineNumber();
            this.lastcol = this.currentLocator.getColumnNumber();
        }
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        if (!this.ignoringWhite) {
            this.characters(ch, start, length);
        }
    }

    protected void flushCharacters() throws SAXException {
        if (this.ignoringBoundaryWhite) {
            if (!this.textBuffer.isAllWhitespace()) {
                this.flushCharacters(this.textBuffer.toString());
            }
        } else {
            this.flushCharacters(this.textBuffer.toString());
        }
        this.textBuffer.clear();
    }

    protected void flushCharacters(String data) throws SAXException {
        if (data.length() == 0 && !this.inCDATA) {
            this.previousCDATA = this.inCDATA;
            return;
        }
        if (this.previousCDATA) {
            CDATA cdata = this.currentLocator == null ? this.factory.cdata(data) : this.factory.cdata(this.lastline, this.lastcol, data);
            this.factory.addContent(this.getCurrentElement(), cdata);
        } else if (data.length() > 0) {
            Text text = this.currentLocator == null ? this.factory.text(data) : this.factory.text(this.lastline, this.lastcol, data);
            this.factory.addContent(this.getCurrentElement(), text);
        }
        this.previousCDATA = this.inCDATA;
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        if (this.suppress) {
            return;
        }
        this.flushCharacters();
        if (!this.atRoot) {
            Parent p = this.currentElement.getParent();
            if (p instanceof Document) {
                this.atRoot = true;
            } else {
                this.currentElement = (Element)p;
            }
        } else {
            throw new SAXException("Ill-formed XML document (missing opening tag for " + localName + ")");
        }
    }

    public void startDTD(String name, String publicID, String systemID) throws SAXException {
        this.flushCharacters();
        DocType doctype = this.currentLocator == null ? this.factory.docType(name, publicID, systemID) : this.factory.docType(this.currentLocator.getLineNumber(), this.currentLocator.getColumnNumber(), name, publicID, systemID);
        this.factory.addContent(this.currentDocument, doctype);
        this.inDTD = true;
        this.inInternalSubset = true;
    }

    public void endDTD() {
        this.currentDocument.getDocType().setInternalSubset(this.internalSubset.toString());
        this.inDTD = false;
        this.inInternalSubset = false;
    }

    public void startEntity(String name) throws SAXException {
        ++this.entityDepth;
        if (this.expand || this.entityDepth > 1) {
            return;
        }
        if (name.equals("[dtd]")) {
            this.inInternalSubset = false;
            return;
        }
        if (!(this.inDTD || name.equals("amp") || name.equals("lt") || name.equals("gt") || name.equals("apos") || name.equals("quot") || this.expand)) {
            String pub = null;
            String sys = null;
            String[] ids = this.externalEntities.get(name);
            if (ids != null) {
                pub = ids[0];
                sys = ids[1];
            }
            if (!this.atRoot) {
                this.flushCharacters();
                EntityRef entity = this.currentLocator == null ? this.factory.entityRef(name, pub, sys) : this.factory.entityRef(this.currentLocator.getLineNumber(), this.currentLocator.getColumnNumber(), name, pub, sys);
                this.factory.addContent(this.getCurrentElement(), entity);
            }
            this.suppress = true;
        }
    }

    public void endEntity(String name) throws SAXException {
        --this.entityDepth;
        if (this.entityDepth == 0) {
            this.suppress = false;
        }
        if (name.equals("[dtd]")) {
            this.inInternalSubset = true;
        }
    }

    public void startCDATA() {
        if (this.suppress) {
            return;
        }
        this.inCDATA = true;
    }

    public void endCDATA() throws SAXException {
        if (this.suppress) {
            return;
        }
        this.previousCDATA = true;
        this.flushCharacters();
        this.previousCDATA = false;
        this.inCDATA = false;
    }

    public void comment(char[] ch, int start, int length) throws SAXException {
        if (this.suppress) {
            return;
        }
        this.flushCharacters();
        String commentText = new String(ch, start, length);
        if (this.inDTD && this.inInternalSubset && !this.expand) {
            this.internalSubset.append("  <!--").append(commentText).append("-->\n");
            return;
        }
        if (!this.inDTD && !commentText.equals("")) {
            Comment comment;
            Comment comment2 = comment = this.currentLocator == null ? this.factory.comment(commentText) : this.factory.comment(this.currentLocator.getLineNumber(), this.currentLocator.getColumnNumber(), commentText);
            if (this.atRoot) {
                this.factory.addContent(this.currentDocument, comment);
            } else {
                this.factory.addContent(this.getCurrentElement(), comment);
            }
        }
    }

    public void notationDecl(String name, String publicID, String systemID) throws SAXException {
        if (!this.inInternalSubset) {
            return;
        }
        this.internalSubset.append("  <!NOTATION ").append(name);
        this.appendExternalId(publicID, systemID);
        this.internalSubset.append(">\n");
    }

    public void unparsedEntityDecl(String name, String publicID, String systemID, String notationName) {
        if (!this.inInternalSubset) {
            return;
        }
        this.internalSubset.append("  <!ENTITY ").append(name);
        this.appendExternalId(publicID, systemID);
        this.internalSubset.append(" NDATA ").append(notationName);
        this.internalSubset.append(">\n");
    }

    private void appendExternalId(String publicID, String systemID) {
        if (publicID != null) {
            this.internalSubset.append(" PUBLIC \"").append(publicID).append('\"');
        }
        if (systemID != null) {
            if (publicID == null) {
                this.internalSubset.append(" SYSTEM ");
            } else {
                this.internalSubset.append(' ');
            }
            this.internalSubset.append('\"').append(systemID).append('\"');
        }
    }

    public Element getCurrentElement() throws SAXException {
        if (this.currentElement == null) {
            throw new SAXException("Ill-formed XML document (multiple root elements detected)");
        }
        return this.currentElement;
    }

    public void setDocumentLocator(Locator locator) {
        this.currentLocator = locator;
    }

    public Locator getDocumentLocator() {
        return this.currentLocator;
    }
}

