/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.dom;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import org.htmlunit.cyberneko.xerces.dom.AttrImpl;
import org.htmlunit.cyberneko.xerces.dom.AttrNSImpl;
import org.htmlunit.cyberneko.xerces.dom.CDATASectionImpl;
import org.htmlunit.cyberneko.xerces.dom.CharacterDataImpl;
import org.htmlunit.cyberneko.xerces.dom.ChildNode;
import org.htmlunit.cyberneko.xerces.dom.CommentImpl;
import org.htmlunit.cyberneko.xerces.dom.CoreDOMImplementationImpl;
import org.htmlunit.cyberneko.xerces.dom.DOMMessageFormatter;
import org.htmlunit.cyberneko.xerces.dom.DeepNodeListImpl;
import org.htmlunit.cyberneko.xerces.dom.DocumentFragmentImpl;
import org.htmlunit.cyberneko.xerces.dom.DocumentTypeImpl;
import org.htmlunit.cyberneko.xerces.dom.ElementImpl;
import org.htmlunit.cyberneko.xerces.dom.ElementNSImpl;
import org.htmlunit.cyberneko.xerces.dom.EntityImpl;
import org.htmlunit.cyberneko.xerces.dom.EntityReferenceImpl;
import org.htmlunit.cyberneko.xerces.dom.NodeImpl;
import org.htmlunit.cyberneko.xerces.dom.NodeListCache;
import org.htmlunit.cyberneko.xerces.dom.ParentNode;
import org.htmlunit.cyberneko.xerces.dom.ProcessingInstructionImpl;
import org.htmlunit.cyberneko.xerces.dom.TextImpl;
import org.htmlunit.cyberneko.xerces.util.URI;
import org.htmlunit.cyberneko.xerces.util.XML11Char;
import org.htmlunit.cyberneko.xerces.util.XMLChar;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.events.EventListener;

public class CoreDocumentImpl
extends ParentNode
implements Document {
    private DocumentTypeImpl docType_;
    private ElementImpl docElement_;
    private NodeListCache fFreeNLCache_;
    private String encoding_;
    private String actualEncoding_;
    private String version_;
    private boolean standalone_;
    private String fDocumentURI_;
    private HashMap<String, Element> identifiers_;
    private static final int[] kidOK = new int[13];
    protected int changes = 0;
    protected boolean allowGrammarAccess;
    protected boolean errorChecking = true;
    private int documentNumber_ = 0;
    private int nodeCounter_ = 0;
    private Map<Node, Integer> nodeTable_;
    private boolean xml11Version_ = false;

    public CoreDocumentImpl() {
        this(false);
    }

    public CoreDocumentImpl(boolean grammarAccess) {
        super(null);
        this.ownerDocument = this;
        this.allowGrammarAccess = grammarAccess;
    }

    public CoreDocumentImpl(DocumentType doctype) {
        this(doctype, false);
    }

    public CoreDocumentImpl(DocumentType doctype, boolean grammarAccess) {
        this(grammarAccess);
        if (doctype != null) {
            try {
                DocumentTypeImpl doctypeImpl = (DocumentTypeImpl)doctype;
            }
            catch (ClassCastException e) {
                String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
                throw new DOMException(4, msg);
            }
            doctypeImpl.ownerDocument = this;
            this.appendChild(doctype);
        }
    }

    @Override
    public final Document getOwnerDocument() {
        return null;
    }

    @Override
    public short getNodeType() {
        return 9;
    }

    @Override
    public String getNodeName() {
        return "#document";
    }

    @Override
    public Node cloneNode(boolean deep) {
        CoreDocumentImpl newdoc = new CoreDocumentImpl();
        this.cloneNode(newdoc, deep);
        return newdoc;
    }

    protected void cloneNode(CoreDocumentImpl newdoc, boolean deep) {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        if (deep) {
            HashMap<Node, String> reversedIdentifiers = null;
            if (this.identifiers_ != null) {
                reversedIdentifiers = new HashMap<Node, String>();
                for (Map.Entry<String, Element> stringElementEntry : this.identifiers_.entrySet()) {
                    reversedIdentifiers.put(stringElementEntry.getValue(), stringElementEntry.getKey());
                }
            }
            ChildNode kid = this.firstChild;
            while (kid != null) {
                newdoc.appendChild(newdoc.importNode(kid, true, true, reversedIdentifiers));
                kid = kid.nextSibling;
            }
        }
        newdoc.allowGrammarAccess = this.allowGrammarAccess;
        newdoc.errorChecking = this.errorChecking;
    }

    @Override
    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        short type = newChild.getNodeType();
        if (this.errorChecking) {
            if (this.needsSyncChildren()) {
                this.synchronizeChildren();
            }
            if (type == 1 && this.docElement_ != null || type == 10 && this.docType_ != null) {
                String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null);
                throw new DOMException(3, msg);
            }
        }
        if (newChild.getOwnerDocument() == null && newChild instanceof DocumentTypeImpl) {
            ((DocumentTypeImpl)newChild).ownerDocument = this;
        }
        super.insertBefore(newChild, refChild);
        if (type == 1) {
            this.docElement_ = (ElementImpl)newChild;
        } else if (type == 10) {
            this.docType_ = (DocumentTypeImpl)newChild;
        }
        return newChild;
    }

    @Override
    public Node removeChild(Node oldChild) throws DOMException {
        super.removeChild(oldChild);
        short type = oldChild.getNodeType();
        if (type == 1) {
            this.docElement_ = null;
        } else if (type == 10) {
            this.docType_ = null;
        }
        return oldChild;
    }

    @Override
    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        if (newChild.getOwnerDocument() == null && newChild instanceof DocumentTypeImpl) {
            ((DocumentTypeImpl)newChild).ownerDocument = this;
        }
        if (this.errorChecking && (this.docType_ != null && oldChild.getNodeType() != 10 && newChild.getNodeType() == 10 || this.docElement_ != null && oldChild.getNodeType() != 1 && newChild.getNodeType() == 1)) {
            throw new DOMException(3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null));
        }
        super.replaceChild(newChild, oldChild);
        short type = oldChild.getNodeType();
        if (type == 1) {
            this.docElement_ = (ElementImpl)newChild;
        } else if (type == 10) {
            this.docType_ = (DocumentTypeImpl)newChild;
        }
        return oldChild;
    }

    @Override
    public String getTextContent() throws DOMException {
        return null;
    }

    @Override
    public void setTextContent(String textContent) throws DOMException {
    }

    @Override
    public Attr createAttribute(String name) throws DOMException {
        if (this.errorChecking && !CoreDocumentImpl.isXMLName(name, this.xml11Version_)) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
            throw new DOMException(5, msg);
        }
        return new AttrImpl(this, name);
    }

    @Override
    public CDATASection createCDATASection(String data) throws DOMException {
        return new CDATASectionImpl(this, data);
    }

    @Override
    public Comment createComment(String data) {
        return new CommentImpl(this, data);
    }

    @Override
    public DocumentFragment createDocumentFragment() {
        return new DocumentFragmentImpl(this);
    }

    @Override
    public Element createElement(String tagName) throws DOMException {
        if (this.errorChecking && !CoreDocumentImpl.isXMLName(tagName, this.xml11Version_)) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
            throw new DOMException(5, msg);
        }
        return new ElementImpl(this, tagName);
    }

    @Override
    public EntityReference createEntityReference(String name) throws DOMException {
        if (this.errorChecking && !CoreDocumentImpl.isXMLName(name, this.xml11Version_)) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
            throw new DOMException(5, msg);
        }
        return new EntityReferenceImpl(this, name);
    }

    @Override
    public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException {
        if (this.errorChecking && !CoreDocumentImpl.isXMLName(target, this.xml11Version_)) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
            throw new DOMException(5, msg);
        }
        return new ProcessingInstructionImpl(this, target, data);
    }

    @Override
    public Text createTextNode(String data) {
        return new TextImpl(this, data);
    }

    @Override
    public DocumentType getDoctype() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        return this.docType_;
    }

    @Override
    public Element getDocumentElement() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        return this.docElement_;
    }

    @Override
    public NodeList getElementsByTagName(String tagname) {
        return new DeepNodeListImpl(this, tagname);
    }

    @Override
    public DOMImplementation getImplementation() {
        return CoreDOMImplementationImpl.getDOMImplementation();
    }

    public void setErrorChecking(boolean check) {
        this.errorChecking = check;
    }

    @Override
    public void setStrictErrorChecking(boolean check) {
        this.errorChecking = check;
    }

    public boolean getErrorChecking() {
        return this.errorChecking;
    }

    @Override
    public boolean getStrictErrorChecking() {
        return this.errorChecking;
    }

    @Override
    public String getInputEncoding() {
        return this.actualEncoding_;
    }

    public void setInputEncoding(String value) {
        this.actualEncoding_ = value;
    }

    public void setXmlEncoding(String value) {
        this.encoding_ = value;
    }

    @Override
    public String getXmlEncoding() {
        return this.encoding_;
    }

    @Override
    public void setXmlVersion(String value) {
        if ("1.0".equals(value) || "1.1".equals(value)) {
            if (!this.getXmlVersion().equals(value)) {
                this.isNormalized(false);
                this.version_ = value;
            }
        } else {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
            throw new DOMException(9, msg);
        }
        this.xml11Version_ = "1.1".equals(this.getXmlVersion());
    }

    @Override
    public String getXmlVersion() {
        return this.version_ == null ? "1.0" : this.version_;
    }

    @Override
    public void setXmlStandalone(boolean value) throws DOMException {
        this.standalone_ = value;
    }

    @Override
    public boolean getXmlStandalone() {
        return this.standalone_;
    }

    @Override
    public String getDocumentURI() {
        return this.fDocumentURI_;
    }

    protected boolean canRenameElements(String newNamespaceURI, String newNodeName, ElementImpl el) {
        return true;
    }

    @Override
    public Node renameNode(Node n, String namespaceURI, String name) throws DOMException {
        if (this.errorChecking && n.getOwnerDocument() != this && n != this) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
            throw new DOMException(4, msg);
        }
        switch (n.getNodeType()) {
            case 1: {
                ElementImpl el = (ElementImpl)n;
                if (el instanceof ElementNSImpl) {
                    if (this.canRenameElements(namespaceURI, name, el)) {
                        ((ElementNSImpl)el).rename(namespaceURI, name);
                    } else {
                        el = this.replaceRenameElement(el, namespaceURI, name);
                    }
                } else if (namespaceURI == null && this.canRenameElements(null, name, el)) {
                    el.rename(name);
                } else {
                    el = this.replaceRenameElement(el, namespaceURI, name);
                }
                this.renamedElement((Element)n, el);
                return el;
            }
            case 2: {
                AttrImpl at = (AttrImpl)n;
                Element el = at.getOwnerElement();
                if (el != null) {
                    el.removeAttributeNode(at);
                }
                if (n instanceof AttrNSImpl) {
                    ((AttrNSImpl)at).rename(namespaceURI, name);
                    if (el != null) {
                        el.setAttributeNodeNS(at);
                    }
                } else if (namespaceURI == null) {
                    at.rename(name);
                    if (el != null) {
                        el.setAttributeNode(at);
                    }
                } else {
                    AttrNSImpl nat = (AttrNSImpl)this.createAttributeNS(namespaceURI, name);
                    this.copyEventListeners(at, nat);
                    Node child = at.getFirstChild();
                    while (child != null) {
                        at.removeChild(child);
                        nat.appendChild(child);
                        child = at.getFirstChild();
                    }
                    if (el != null) {
                        el.setAttributeNode(nat);
                    }
                    at = nat;
                }
                this.renamedAttrNode((Attr)n, at);
                return at;
            }
        }
        String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
        throw new DOMException(9, msg);
    }

    private ElementImpl replaceRenameElement(ElementImpl el, String namespaceURI, String name) {
        ElementNSImpl nel = (ElementNSImpl)this.createElementNS(namespaceURI, name);
        this.copyEventListeners(el, nel);
        Node parent = el.getParentNode();
        Node nextSib = el.getNextSibling();
        if (parent != null) {
            parent.removeChild(el);
        }
        Node child = el.getFirstChild();
        while (child != null) {
            el.removeChild(child);
            nel.appendChild(child);
            child = el.getFirstChild();
        }
        nel.moveSpecifiedAttributes(el);
        if (parent != null) {
            parent.insertBefore(nel, nextSib);
        }
        return nel;
    }

    @Override
    public void normalizeDocument() {
    }

    @Override
    public DOMConfiguration getDomConfig() {
        return null;
    }

    @Override
    public String getBaseURI() {
        if (this.fDocumentURI_ != null && this.fDocumentURI_.length() != 0) {
            try {
                return new URI(this.fDocumentURI_).toString();
            }
            catch (URI.MalformedURIException e) {
                return null;
            }
        }
        return this.fDocumentURI_;
    }

    @Override
    public void setDocumentURI(String documentURI) {
        this.fDocumentURI_ = documentURI;
    }

    public DocumentType createDocumentType(String qualifiedName, String publicID, String systemID) throws DOMException {
        return new DocumentTypeImpl(this, qualifiedName, publicID, systemID);
    }

    public Entity createEntity(String name) throws DOMException {
        if (this.errorChecking && !CoreDocumentImpl.isXMLName(name, this.xml11Version_)) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
            throw new DOMException(5, msg);
        }
        return new EntityImpl(this, name);
    }

    @Override
    protected int getNodeNumber() {
        if (this.documentNumber_ == 0) {
            CoreDOMImplementationImpl cd = (CoreDOMImplementationImpl)CoreDOMImplementationImpl.getDOMImplementation();
            this.documentNumber_ = cd.assignDocumentNumber();
        }
        return this.documentNumber_;
    }

    protected int getNodeNumber(Node node) {
        int num;
        if (this.nodeTable_ == null) {
            this.nodeTable_ = new WeakHashMap<Node, Integer>();
            num = --this.nodeCounter_;
            this.nodeTable_.put(node, new Integer(num));
        } else {
            Integer n = this.nodeTable_.get(node);
            if (n == null) {
                num = --this.nodeCounter_;
                this.nodeTable_.put(node, new Integer(num));
            } else {
                num = n;
            }
        }
        return num;
    }

    @Override
    public Node importNode(Node source, boolean deep) throws DOMException {
        return this.importNode(source, deep, false, null);
    }

    private Node importNode(Node source, boolean deep, boolean cloningDoc, HashMap<Node, String> reversedIdentifiers) throws DOMException {
        Node newnode = null;
        short type = source.getNodeType();
        switch (type) {
            case 1: {
                String elementId;
                boolean domLevel20 = source.getOwnerDocument().getImplementation().hasFeature("XML", "2.0");
                Element newElement = !domLevel20 || source.getLocalName() == null ? this.createElement(source.getNodeName()) : this.createElementNS(source.getNamespaceURI(), source.getNodeName());
                NamedNodeMap sourceAttrs = source.getAttributes();
                if (sourceAttrs != null) {
                    int length = sourceAttrs.getLength();
                    for (int index = 0; index < length; ++index) {
                        Attr attr = (Attr)sourceAttrs.item(index);
                        if (!attr.getSpecified() && !cloningDoc) continue;
                        Attr newAttr = (Attr)this.importNode(attr, true, cloningDoc, reversedIdentifiers);
                        if (!domLevel20 || attr.getLocalName() == null) {
                            newElement.setAttributeNode(newAttr);
                            continue;
                        }
                        newElement.setAttributeNodeNS(newAttr);
                    }
                }
                if (reversedIdentifiers != null && (elementId = reversedIdentifiers.get(source)) != null) {
                    if (this.identifiers_ == null) {
                        this.identifiers_ = new HashMap();
                    }
                    this.identifiers_.put(elementId, newElement);
                }
                newnode = newElement;
                break;
            }
            case 2: {
                newnode = source.getOwnerDocument().getImplementation().hasFeature("XML", "2.0") ? (source.getLocalName() == null ? this.createAttribute(source.getNodeName()) : this.createAttributeNS(source.getNamespaceURI(), source.getNodeName())) : this.createAttribute(source.getNodeName());
                if (source instanceof AttrImpl) {
                    AttrImpl attr = (AttrImpl)source;
                    if (attr.hasStringValue()) {
                        AttrImpl newattr = (AttrImpl)newnode;
                        newattr.setValue(attr.getValue());
                        deep = false;
                        break;
                    }
                    deep = true;
                    break;
                }
                if (source.getFirstChild() == null) {
                    newnode.setNodeValue(source.getNodeValue());
                    deep = false;
                    break;
                }
                deep = true;
                break;
            }
            case 3: {
                newnode = this.createTextNode(source.getNodeValue());
                break;
            }
            case 4: {
                newnode = this.createCDATASection(source.getNodeValue());
                break;
            }
            case 5: {
                newnode = this.createEntityReference(source.getNodeName());
                deep = false;
                break;
            }
            case 6: {
                Entity srcentity = (Entity)source;
                EntityImpl newentity = (EntityImpl)this.createEntity(source.getNodeName());
                newentity.setPublicId(srcentity.getPublicId());
                newentity.setSystemId(srcentity.getSystemId());
                newentity.setNotationName(srcentity.getNotationName());
                newnode = newentity;
                break;
            }
            case 7: {
                newnode = this.createProcessingInstruction(source.getNodeName(), source.getNodeValue());
                break;
            }
            case 8: {
                newnode = this.createComment(source.getNodeValue());
                break;
            }
            case 10: {
                int i;
                if (!cloningDoc) {
                    String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
                    throw new DOMException(9, msg);
                }
                DocumentType srcdoctype = (DocumentType)source;
                DocumentTypeImpl newdoctype = (DocumentTypeImpl)this.createDocumentType(srcdoctype.getNodeName(), srcdoctype.getPublicId(), srcdoctype.getSystemId());
                newdoctype.setInternalSubset(srcdoctype.getInternalSubset());
                NamedNodeMap smap = srcdoctype.getEntities();
                NamedNodeMap tmap = newdoctype.getEntities();
                if (smap != null) {
                    for (i = 0; i < smap.getLength(); ++i) {
                        tmap.setNamedItem(this.importNode(smap.item(i), true, true, reversedIdentifiers));
                    }
                }
                smap = srcdoctype.getNotations();
                tmap = newdoctype.getNotations();
                if (smap != null) {
                    for (i = 0; i < smap.getLength(); ++i) {
                        tmap.setNamedItem(this.importNode(smap.item(i), true, true, reversedIdentifiers));
                    }
                }
                newnode = newdoctype;
                break;
            }
            case 11: {
                newnode = this.createDocumentFragment();
                break;
            }
            case 12: {
                break;
            }
            default: {
                String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
                throw new DOMException(9, msg);
            }
        }
        if (deep) {
            for (Node srckid = source.getFirstChild(); srckid != null; srckid = srckid.getNextSibling()) {
                newnode.appendChild(this.importNode(srckid, true, cloningDoc, reversedIdentifiers));
            }
        }
        return newnode;
    }

    @Override
    public Node adoptNode(Node source) {
        DOMImplementation otherImpl;
        DOMImplementation thisImpl;
        NodeImpl node;
        try {
            node = (NodeImpl)source;
        }
        catch (ClassCastException e) {
            return null;
        }
        if (source == null) {
            return null;
        }
        if (source.getOwnerDocument() != null && (thisImpl = this.getImplementation()) != (otherImpl = source.getOwnerDocument().getImplementation())) {
            return null;
        }
        switch (node.getNodeType()) {
            case 2: {
                AttrImpl attr = (AttrImpl)node;
                if (attr.getOwnerElement() != null) {
                    attr.getOwnerElement().removeAttributeNode(attr);
                }
                attr.isSpecified(true);
                attr.setOwnerDocument(this);
                break;
            }
            case 6: 
            case 12: {
                String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException(7, msg);
            }
            case 9: 
            case 10: {
                String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
                throw new DOMException(9, msg);
            }
            case 5: {
                NamedNodeMap entities;
                Node entityNode;
                Node child;
                Node parent = node.getParentNode();
                if (parent != null) {
                    parent.removeChild(source);
                }
                while ((child = node.getFirstChild()) != null) {
                    node.removeChild(child);
                }
                node.setOwnerDocument(this);
                if (this.docType_ == null || (entityNode = (entities = this.docType_.getEntities()).getNamedItem(node.getNodeName())) == null) break;
                for (child = entityNode.getFirstChild(); child != null; child = child.getNextSibling()) {
                    Node childClone = child.cloneNode(true);
                    node.appendChild(childClone);
                }
                break;
            }
            case 1: {
                Node parent = node.getParentNode();
                if (parent != null) {
                    parent.removeChild(source);
                }
                node.setOwnerDocument(this);
                break;
            }
            default: {
                Node parent = node.getParentNode();
                if (parent != null) {
                    parent.removeChild(source);
                }
                node.setOwnerDocument(this);
            }
        }
        return node;
    }

    protected void undeferChildren(Node node) {
        Node top = node;
        while (null != node) {
            NamedNodeMap attributes;
            if (((NodeImpl)node).needsSyncData()) {
                ((NodeImpl)node).synchronizeData();
            }
            if ((attributes = node.getAttributes()) != null) {
                int length = attributes.getLength();
                for (int i = 0; i < length; ++i) {
                    this.undeferChildren(attributes.item(i));
                }
            }
            Node nextNode = node.getFirstChild();
            while (null == nextNode && !top.equals(node)) {
                nextNode = node.getNextSibling();
                if (null != nextNode || null != (node = node.getParentNode()) && !top.equals(node)) continue;
                nextNode = null;
                break;
            }
            node = nextNode;
        }
    }

    @Override
    public Element getElementById(String elementId) {
        return this.getIdentifier(elementId);
    }

    protected final void clearIdentifiers() {
        if (this.identifiers_ != null) {
            this.identifiers_.clear();
        }
    }

    public void putIdentifier(String idName, Element element) {
        if (element == null) {
            this.removeIdentifier(idName);
            return;
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.identifiers_ == null) {
            this.identifiers_ = new HashMap();
        }
        this.identifiers_.put(idName, element);
    }

    public Element getIdentifier(String idName) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.identifiers_ == null) {
            return null;
        }
        Element elem = this.identifiers_.get(idName);
        if (elem != null) {
            for (Node parent = elem.getParentNode(); parent != null; parent = parent.getParentNode()) {
                if (parent != this) continue;
                return elem;
            }
        }
        return null;
    }

    public void removeIdentifier(String idName) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.identifiers_ == null) {
            return;
        }
        this.identifiers_.remove(idName);
    }

    @Override
    public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
        return new ElementNSImpl(this, namespaceURI, qualifiedName);
    }

    public Element createElementNS(String namespaceURI, String qualifiedName, String localpart) throws DOMException {
        return new ElementNSImpl(this, namespaceURI, qualifiedName, localpart);
    }

    @Override
    public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException {
        return new AttrNSImpl(this, namespaceURI, qualifiedName);
    }

    public Attr createAttributeNS(String namespaceURI, String qualifiedName, String localpart) throws DOMException {
        return new AttrNSImpl(this, namespaceURI, qualifiedName, localpart);
    }

    @Override
    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
        return new DeepNodeListImpl(this, namespaceURI, localName);
    }

    public Object clone() throws CloneNotSupportedException {
        CoreDocumentImpl newdoc = (CoreDocumentImpl)super.clone();
        newdoc.docType_ = null;
        newdoc.docElement_ = null;
        return newdoc;
    }

    public static boolean isXMLName(String s, boolean xml11Version) {
        if (s == null) {
            return false;
        }
        if (!xml11Version) {
            return XMLChar.isValidName(s);
        }
        return XML11Char.isXML11ValidName(s);
    }

    public static boolean isValidQName(String prefix, String local, boolean xml11Version) {
        if (local == null) {
            return false;
        }
        boolean validNCName = !xml11Version ? (prefix == null || XMLChar.isValidNCName(prefix)) && XMLChar.isValidNCName(local) : (prefix == null || XML11Char.isXML11ValidNCName(prefix)) && XML11Char.isXML11ValidNCName(local);
        return validNCName;
    }

    protected boolean isKidOK(Node parent, Node child) {
        if (this.allowGrammarAccess && parent.getNodeType() == 10) {
            return child.getNodeType() == 1;
        }
        return 0 != (kidOK[parent.getNodeType()] & 1 << child.getNodeType());
    }

    @Override
    protected void changed() {
        ++this.changes;
    }

    @Override
    protected int changes() {
        return this.changes;
    }

    NodeListCache getNodeListCache(ParentNode owner) {
        if (this.fFreeNLCache_ == null) {
            return new NodeListCache(owner);
        }
        NodeListCache c = this.fFreeNLCache_;
        this.fFreeNLCache_ = this.fFreeNLCache_.next;
        c.fChild = null;
        c.fChildIndex = -1;
        c.fLength = -1;
        if (c.fOwner != null) {
            c.fOwner.fNodeListCache = null;
        }
        c.fOwner = owner;
        return c;
    }

    void freeNodeListCache(NodeListCache c) {
        c.next = this.fFreeNLCache_;
        this.fFreeNLCache_ = c;
    }

    protected final void checkNamespaceWF(String qname, int colon1, int colon2) {
        if (!this.errorChecking) {
            return;
        }
        if (colon1 == 0 || colon1 == qname.length() - 1 || colon2 != colon1) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
            throw new DOMException(14, msg);
        }
    }

    protected final void checkDOMNSErr(String prefix, String namespace) {
        if (this.errorChecking) {
            if (namespace == null) {
                String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
                throw new DOMException(14, msg);
            }
            if ("xml".equals(prefix) && !namespace.equals("http://www.w3.org/XML/1998/namespace")) {
                String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
                throw new DOMException(14, msg);
            }
            if ("xmlns".equals(prefix) && !namespace.equals("http://www.w3.org/2000/xmlns/") || !"xmlns".equals(prefix) && namespace.equals("http://www.w3.org/2000/xmlns/")) {
                String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
                throw new DOMException(14, msg);
            }
        }
    }

    protected final void checkQName(String prefix, String local) {
        boolean validNCName;
        if (!this.errorChecking) {
            return;
        }
        if (!this.xml11Version_) {
            validNCName = (prefix == null || XMLChar.isValidNCName(prefix)) && XMLChar.isValidNCName(local);
        } else {
            boolean bl = validNCName = (prefix == null || XML11Char.isXML11ValidNCName(prefix)) && XML11Char.isXML11ValidNCName(local);
        }
        if (!validNCName) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
            throw new DOMException(5, msg);
        }
    }

    boolean isXML11Version() {
        return this.xml11Version_;
    }

    protected void addEventListener(NodeImpl node, String type, EventListener listener, boolean useCapture) {
    }

    protected void removeEventListener(NodeImpl node, String type, EventListener listener, boolean useCapture) {
    }

    protected void copyEventListeners(NodeImpl src, NodeImpl tgt) {
    }

    void replacedText(CharacterDataImpl node) {
    }

    void deletedText(CharacterDataImpl node, int offset, int count) {
    }

    void insertedText(CharacterDataImpl node, int offset, int count) {
    }

    void modifyingCharacterData(NodeImpl node, boolean replace) {
    }

    void modifiedCharacterData(NodeImpl node, String oldvalue, String value, boolean replace) {
    }

    void insertingNode(NodeImpl node, boolean replace) {
    }

    void insertedNode(NodeImpl node, NodeImpl newInternal, boolean replace) {
    }

    void removingNode(NodeImpl node, NodeImpl oldChild, boolean replace) {
    }

    void removedNode(NodeImpl node, boolean replace) {
    }

    void replacingNode(NodeImpl node) {
    }

    void replacedNode(NodeImpl node) {
    }

    void replacingData(NodeImpl node) {
    }

    void replacedCharacterData(NodeImpl node, String oldvalue, String value) {
    }

    void modifiedAttrValue(AttrImpl attr, String oldvalue) {
    }

    void setAttrNode(AttrImpl attr, AttrImpl previous) {
    }

    void removedAttrNode(AttrImpl attr, NodeImpl oldOwner, String name) {
    }

    void renamedAttrNode(Attr oldAt, Attr newAt) {
    }

    void renamedElement(Element oldEl, Element newEl) {
    }

    static {
        CoreDocumentImpl.kidOK[9] = 1410;
        CoreDocumentImpl.kidOK[1] = 442;
        CoreDocumentImpl.kidOK[5] = 442;
        CoreDocumentImpl.kidOK[6] = 442;
        CoreDocumentImpl.kidOK[11] = 442;
        CoreDocumentImpl.kidOK[2] = 40;
        CoreDocumentImpl.kidOK[12] = 0;
        CoreDocumentImpl.kidOK[4] = 0;
        CoreDocumentImpl.kidOK[3] = 0;
        CoreDocumentImpl.kidOK[8] = 0;
        CoreDocumentImpl.kidOK[7] = 0;
        CoreDocumentImpl.kidOK[10] = 0;
    }
}

