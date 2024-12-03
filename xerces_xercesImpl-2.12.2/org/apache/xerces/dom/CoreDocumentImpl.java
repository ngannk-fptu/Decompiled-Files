/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.WeakHashMap;
import org.apache.xerces.dom.AttrImpl;
import org.apache.xerces.dom.AttrNSImpl;
import org.apache.xerces.dom.CDATASectionImpl;
import org.apache.xerces.dom.CharacterDataImpl;
import org.apache.xerces.dom.ChildNode;
import org.apache.xerces.dom.CommentImpl;
import org.apache.xerces.dom.CoreDOMImplementationImpl;
import org.apache.xerces.dom.DOMConfigurationImpl;
import org.apache.xerces.dom.DOMImplementationImpl;
import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xerces.dom.DOMNormalizer;
import org.apache.xerces.dom.DeepNodeListImpl;
import org.apache.xerces.dom.DeferredDOMImplementationImpl;
import org.apache.xerces.dom.DocumentFragmentImpl;
import org.apache.xerces.dom.DocumentTypeImpl;
import org.apache.xerces.dom.ElementDefinitionImpl;
import org.apache.xerces.dom.ElementImpl;
import org.apache.xerces.dom.ElementNSImpl;
import org.apache.xerces.dom.EntityImpl;
import org.apache.xerces.dom.EntityReferenceImpl;
import org.apache.xerces.dom.NodeImpl;
import org.apache.xerces.dom.NodeListCache;
import org.apache.xerces.dom.NotationImpl;
import org.apache.xerces.dom.ObjectFactory;
import org.apache.xerces.dom.ParentNode;
import org.apache.xerces.dom.ProcessingInstructionImpl;
import org.apache.xerces.dom.TextImpl;
import org.apache.xerces.util.URI;
import org.apache.xerces.util.XML11Char;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.xni.NamespaceContext;
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
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

public class CoreDocumentImpl
extends ParentNode
implements Document {
    static final long serialVersionUID = 0L;
    protected DocumentTypeImpl docType;
    protected ElementImpl docElement;
    transient NodeListCache fFreeNLCache;
    protected String encoding;
    protected String actualEncoding;
    protected String version;
    protected boolean standalone;
    protected String fDocumentURI;
    protected Map userData;
    protected Hashtable identifiers;
    transient DOMNormalizer domNormalizer = null;
    transient DOMConfigurationImpl fConfiguration = null;
    transient Object fXPathEvaluator = null;
    private static final int[] kidOK = new int[13];
    protected int changes = 0;
    protected boolean allowGrammarAccess;
    protected boolean errorChecking = true;
    protected boolean xmlVersionChanged = false;
    private int documentNumber = 0;
    private int nodeCounter = 0;
    private Map nodeTable;
    private boolean xml11Version = false;

    public CoreDocumentImpl() {
        this(false);
    }

    public CoreDocumentImpl(boolean bl) {
        super(null);
        this.ownerDocument = this;
        this.allowGrammarAccess = bl;
    }

    public CoreDocumentImpl(DocumentType documentType) {
        this(documentType, false);
    }

    public CoreDocumentImpl(DocumentType documentType, boolean bl) {
        this(bl);
        if (documentType != null) {
            try {
                DocumentTypeImpl documentTypeImpl = (DocumentTypeImpl)documentType;
            }
            catch (ClassCastException classCastException) {
                String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
                throw new DOMException(4, string);
            }
            documentTypeImpl.ownerDocument = this;
            this.appendChild(documentType);
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
    public Node cloneNode(boolean bl) {
        CoreDocumentImpl coreDocumentImpl = new CoreDocumentImpl();
        this.callUserDataHandlers(this, coreDocumentImpl, (short)1);
        this.cloneNode(coreDocumentImpl, bl);
        return coreDocumentImpl;
    }

    protected void cloneNode(CoreDocumentImpl coreDocumentImpl, boolean bl) {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        if (bl) {
            HashMap hashMap = null;
            if (this.identifiers != null) {
                hashMap = new HashMap();
                for (Map.Entry entry : this.identifiers.entrySet()) {
                    Object k = entry.getKey();
                    Object v = entry.getValue();
                    hashMap.put(v, k);
                }
            }
            Object object = this.firstChild;
            while (object != null) {
                coreDocumentImpl.appendChild(coreDocumentImpl.importNode((Node)object, true, true, hashMap));
                object = ((ChildNode)object).nextSibling;
            }
        }
        coreDocumentImpl.allowGrammarAccess = this.allowGrammarAccess;
        coreDocumentImpl.errorChecking = this.errorChecking;
    }

    @Override
    public Node insertBefore(Node node, Node node2) throws DOMException {
        short s = node.getNodeType();
        if (this.errorChecking) {
            if (this.needsSyncChildren()) {
                this.synchronizeChildren();
            }
            if (s == 1 && this.docElement != null || s == 10 && this.docType != null) {
                String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null);
                throw new DOMException(3, string);
            }
        }
        if (node.getOwnerDocument() == null && node instanceof DocumentTypeImpl) {
            ((DocumentTypeImpl)node).ownerDocument = this;
        }
        super.insertBefore(node, node2);
        if (s == 1) {
            this.docElement = (ElementImpl)node;
        } else if (s == 10) {
            this.docType = (DocumentTypeImpl)node;
        }
        return node;
    }

    @Override
    public Node removeChild(Node node) throws DOMException {
        super.removeChild(node);
        short s = node.getNodeType();
        if (s == 1) {
            this.docElement = null;
        } else if (s == 10) {
            this.docType = null;
        }
        return node;
    }

    @Override
    public Node replaceChild(Node node, Node node2) throws DOMException {
        if (node.getOwnerDocument() == null && node instanceof DocumentTypeImpl) {
            ((DocumentTypeImpl)node).ownerDocument = this;
        }
        if (this.errorChecking && (this.docType != null && node2.getNodeType() != 10 && node.getNodeType() == 10 || this.docElement != null && node2.getNodeType() != 1 && node.getNodeType() == 1)) {
            throw new DOMException(3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null));
        }
        super.replaceChild(node, node2);
        short s = node2.getNodeType();
        if (s == 1) {
            this.docElement = (ElementImpl)node;
        } else if (s == 10) {
            this.docType = (DocumentTypeImpl)node;
        }
        return node2;
    }

    @Override
    public String getTextContent() throws DOMException {
        return null;
    }

    @Override
    public void setTextContent(String string) throws DOMException {
    }

    @Override
    public Object getFeature(String string, String string2) {
        boolean bl;
        boolean bl2 = bl = string2 == null || string2.length() == 0;
        if (string.equalsIgnoreCase("+XPath") && (bl || string2.equals("3.0"))) {
            if (this.fXPathEvaluator != null) {
                return this.fXPathEvaluator;
            }
            try {
                Class clazz = ObjectFactory.findProviderClass("org.apache.xpath.domapi.XPathEvaluatorImpl", ObjectFactory.findClassLoader(), true);
                Constructor constructor = clazz.getConstructor(Document.class);
                Class<?>[] classArray = clazz.getInterfaces();
                for (int i = 0; i < classArray.length; ++i) {
                    if (!classArray[i].getName().equals("org.w3c.dom.xpath.XPathEvaluator")) continue;
                    this.fXPathEvaluator = constructor.newInstance(this);
                    return this.fXPathEvaluator;
                }
                return null;
            }
            catch (Exception exception) {
                return null;
            }
        }
        return super.getFeature(string, string2);
    }

    @Override
    public Attr createAttribute(String string) throws DOMException {
        if (this.errorChecking && !CoreDocumentImpl.isXMLName(string, this.xml11Version)) {
            String string2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
            throw new DOMException(5, string2);
        }
        return new AttrImpl(this, string);
    }

    @Override
    public CDATASection createCDATASection(String string) throws DOMException {
        return new CDATASectionImpl(this, string);
    }

    @Override
    public Comment createComment(String string) {
        return new CommentImpl(this, string);
    }

    @Override
    public DocumentFragment createDocumentFragment() {
        return new DocumentFragmentImpl(this);
    }

    @Override
    public Element createElement(String string) throws DOMException {
        if (this.errorChecking && !CoreDocumentImpl.isXMLName(string, this.xml11Version)) {
            String string2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
            throw new DOMException(5, string2);
        }
        return new ElementImpl(this, string);
    }

    @Override
    public EntityReference createEntityReference(String string) throws DOMException {
        if (this.errorChecking && !CoreDocumentImpl.isXMLName(string, this.xml11Version)) {
            String string2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
            throw new DOMException(5, string2);
        }
        return new EntityReferenceImpl(this, string);
    }

    @Override
    public ProcessingInstruction createProcessingInstruction(String string, String string2) throws DOMException {
        if (this.errorChecking && !CoreDocumentImpl.isXMLName(string, this.xml11Version)) {
            String string3 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
            throw new DOMException(5, string3);
        }
        return new ProcessingInstructionImpl(this, string, string2);
    }

    @Override
    public Text createTextNode(String string) {
        return new TextImpl(this, string);
    }

    @Override
    public DocumentType getDoctype() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        return this.docType;
    }

    @Override
    public Element getDocumentElement() {
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        return this.docElement;
    }

    @Override
    public NodeList getElementsByTagName(String string) {
        return new DeepNodeListImpl(this, string);
    }

    @Override
    public DOMImplementation getImplementation() {
        return CoreDOMImplementationImpl.getDOMImplementation();
    }

    public void setErrorChecking(boolean bl) {
        this.errorChecking = bl;
    }

    @Override
    public void setStrictErrorChecking(boolean bl) {
        this.errorChecking = bl;
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
        return this.actualEncoding;
    }

    public void setInputEncoding(String string) {
        this.actualEncoding = string;
    }

    public void setXmlEncoding(String string) {
        this.encoding = string;
    }

    public void setEncoding(String string) {
        this.setXmlEncoding(string);
    }

    @Override
    public String getXmlEncoding() {
        return this.encoding;
    }

    public String getEncoding() {
        return this.getXmlEncoding();
    }

    @Override
    public void setXmlVersion(String string) {
        if (string.equals("1.0") || string.equals("1.1")) {
            if (!this.getXmlVersion().equals(string)) {
                this.xmlVersionChanged = true;
                this.isNormalized(false);
                this.version = string;
            }
        } else {
            String string2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
            throw new DOMException(9, string2);
        }
        this.xml11Version = this.getXmlVersion().equals("1.1");
    }

    public void setVersion(String string) {
        this.setXmlVersion(string);
    }

    @Override
    public String getXmlVersion() {
        return this.version == null ? "1.0" : this.version;
    }

    public String getVersion() {
        return this.getXmlVersion();
    }

    @Override
    public void setXmlStandalone(boolean bl) throws DOMException {
        this.standalone = bl;
    }

    public void setStandalone(boolean bl) {
        this.setXmlStandalone(bl);
    }

    @Override
    public boolean getXmlStandalone() {
        return this.standalone;
    }

    public boolean getStandalone() {
        return this.getXmlStandalone();
    }

    @Override
    public String getDocumentURI() {
        return this.fDocumentURI;
    }

    protected boolean canRenameElements(String string, String string2, ElementImpl elementImpl) {
        return true;
    }

    @Override
    public Node renameNode(Node node, String string, String string2) throws DOMException {
        if (this.errorChecking && node.getOwnerDocument() != this && node != this) {
            String string3 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
            throw new DOMException(4, string3);
        }
        switch (node.getNodeType()) {
            case 1: {
                ElementImpl elementImpl = (ElementImpl)node;
                if (elementImpl instanceof ElementNSImpl) {
                    if (this.canRenameElements(string, string2, elementImpl)) {
                        ((ElementNSImpl)elementImpl).rename(string, string2);
                        this.callUserDataHandlers(elementImpl, null, (short)4);
                    } else {
                        elementImpl = this.replaceRenameElement(elementImpl, string, string2);
                    }
                } else if (string == null && this.canRenameElements(null, string2, elementImpl)) {
                    elementImpl.rename(string2);
                    this.callUserDataHandlers(elementImpl, null, (short)4);
                } else {
                    elementImpl = this.replaceRenameElement(elementImpl, string, string2);
                }
                this.renamedElement((Element)node, elementImpl);
                return elementImpl;
            }
            case 2: {
                AttrImpl attrImpl = (AttrImpl)node;
                Element element = attrImpl.getOwnerElement();
                if (element != null) {
                    element.removeAttributeNode(attrImpl);
                }
                if (node instanceof AttrNSImpl) {
                    ((AttrNSImpl)attrImpl).rename(string, string2);
                    if (element != null) {
                        element.setAttributeNodeNS(attrImpl);
                    }
                    this.callUserDataHandlers(attrImpl, null, (short)4);
                } else if (string == null) {
                    attrImpl.rename(string2);
                    if (element != null) {
                        element.setAttributeNode(attrImpl);
                    }
                    this.callUserDataHandlers(attrImpl, null, (short)4);
                } else {
                    AttrNSImpl attrNSImpl = (AttrNSImpl)this.createAttributeNS(string, string2);
                    this.copyEventListeners(attrImpl, attrNSImpl);
                    Hashtable hashtable = this.removeUserDataTable(attrImpl);
                    Node node2 = attrImpl.getFirstChild();
                    while (node2 != null) {
                        attrImpl.removeChild(node2);
                        attrNSImpl.appendChild(node2);
                        node2 = attrImpl.getFirstChild();
                    }
                    this.setUserDataTable(attrNSImpl, hashtable);
                    this.callUserDataHandlers(attrImpl, attrNSImpl, (short)4);
                    if (element != null) {
                        element.setAttributeNode(attrNSImpl);
                    }
                    attrImpl = attrNSImpl;
                }
                this.renamedAttrNode((Attr)node, attrImpl);
                return attrImpl;
            }
        }
        String string4 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
        throw new DOMException(9, string4);
    }

    private ElementImpl replaceRenameElement(ElementImpl elementImpl, String string, String string2) {
        ElementNSImpl elementNSImpl = (ElementNSImpl)this.createElementNS(string, string2);
        this.copyEventListeners(elementImpl, elementNSImpl);
        Hashtable hashtable = this.removeUserDataTable(elementImpl);
        Node node = elementImpl.getParentNode();
        Node node2 = elementImpl.getNextSibling();
        if (node != null) {
            node.removeChild(elementImpl);
        }
        Node node3 = elementImpl.getFirstChild();
        while (node3 != null) {
            elementImpl.removeChild(node3);
            elementNSImpl.appendChild(node3);
            node3 = elementImpl.getFirstChild();
        }
        elementNSImpl.moveSpecifiedAttributes(elementImpl);
        this.setUserDataTable(elementNSImpl, hashtable);
        this.callUserDataHandlers(elementImpl, elementNSImpl, (short)4);
        if (node != null) {
            node.insertBefore(elementNSImpl, node2);
        }
        return elementNSImpl;
    }

    @Override
    public void normalizeDocument() {
        if (this.isNormalized() && !this.isNormalizeDocRequired()) {
            return;
        }
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        if (this.domNormalizer == null) {
            this.domNormalizer = new DOMNormalizer();
        }
        if (this.fConfiguration == null) {
            this.fConfiguration = new DOMConfigurationImpl();
        } else {
            this.fConfiguration.reset();
        }
        this.domNormalizer.normalizeDocument(this, this.fConfiguration);
        this.isNormalized(true);
        this.xmlVersionChanged = false;
    }

    @Override
    public DOMConfiguration getDomConfig() {
        if (this.fConfiguration == null) {
            this.fConfiguration = new DOMConfigurationImpl();
        }
        return this.fConfiguration;
    }

    @Override
    public String getBaseURI() {
        if (this.fDocumentURI != null && this.fDocumentURI.length() != 0) {
            try {
                return new URI(this.fDocumentURI).toString();
            }
            catch (URI.MalformedURIException malformedURIException) {
                return null;
            }
        }
        return this.fDocumentURI;
    }

    @Override
    public void setDocumentURI(String string) {
        this.fDocumentURI = string;
    }

    public boolean getAsync() {
        return false;
    }

    public void setAsync(boolean bl) {
        if (bl) {
            String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
            throw new DOMException(9, string);
        }
    }

    public void abort() {
    }

    public boolean load(String string) {
        return false;
    }

    public boolean loadXML(String string) {
        return false;
    }

    public String saveXML(Node node) throws DOMException {
        if (this.errorChecking && node != null && this != node.getOwnerDocument()) {
            String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
            throw new DOMException(4, string);
        }
        DOMImplementationLS dOMImplementationLS = (DOMImplementationLS)((Object)DOMImplementationImpl.getDOMImplementation());
        LSSerializer lSSerializer = dOMImplementationLS.createLSSerializer();
        if (node == null) {
            node = this;
        }
        return lSSerializer.writeToString(node);
    }

    void setMutationEvents(boolean bl) {
    }

    boolean getMutationEvents() {
        return false;
    }

    public DocumentType createDocumentType(String string, String string2, String string3) throws DOMException {
        return new DocumentTypeImpl(this, string, string2, string3);
    }

    public Entity createEntity(String string) throws DOMException {
        if (this.errorChecking && !CoreDocumentImpl.isXMLName(string, this.xml11Version)) {
            String string2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
            throw new DOMException(5, string2);
        }
        return new EntityImpl(this, string);
    }

    public Notation createNotation(String string) throws DOMException {
        if (this.errorChecking && !CoreDocumentImpl.isXMLName(string, this.xml11Version)) {
            String string2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
            throw new DOMException(5, string2);
        }
        return new NotationImpl(this, string);
    }

    public ElementDefinitionImpl createElementDefinition(String string) throws DOMException {
        if (this.errorChecking && !CoreDocumentImpl.isXMLName(string, this.xml11Version)) {
            String string2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
            throw new DOMException(5, string2);
        }
        return new ElementDefinitionImpl(this, string);
    }

    @Override
    protected int getNodeNumber() {
        if (this.documentNumber == 0) {
            CoreDOMImplementationImpl coreDOMImplementationImpl = (CoreDOMImplementationImpl)CoreDOMImplementationImpl.getDOMImplementation();
            this.documentNumber = coreDOMImplementationImpl.assignDocumentNumber();
        }
        return this.documentNumber;
    }

    protected int getNodeNumber(Node node) {
        int n;
        if (this.nodeTable == null) {
            this.nodeTable = new WeakHashMap();
            n = --this.nodeCounter;
            this.nodeTable.put(node, new Integer(n));
        } else {
            Integer n2 = (Integer)this.nodeTable.get(node);
            if (n2 == null) {
                n = --this.nodeCounter;
                this.nodeTable.put(node, new Integer(n));
            } else {
                n = n2;
            }
        }
        return n;
    }

    @Override
    public Node importNode(Node node, boolean bl) throws DOMException {
        return this.importNode(node, bl, false, null);
    }

    private Node importNode(Node node, boolean bl, boolean bl2, HashMap hashMap) throws DOMException {
        Node node2;
        Node node3 = null;
        Hashtable hashtable = null;
        if (node instanceof NodeImpl) {
            hashtable = ((NodeImpl)node).getUserDataRecord();
        }
        short s = node.getNodeType();
        switch (s) {
            case 1: {
                Object v;
                int n;
                boolean bl3 = node.getOwnerDocument().getImplementation().hasFeature("XML", "2.0");
                node2 = !bl3 || node.getLocalName() == null ? this.createElement(node.getNodeName()) : this.createElementNS(node.getNamespaceURI(), node.getNodeName());
                NamedNodeMap namedNodeMap = node.getAttributes();
                if (namedNodeMap != null) {
                    int n2 = namedNodeMap.getLength();
                    for (n = 0; n < n2; ++n) {
                        Attr attr = (Attr)namedNodeMap.item(n);
                        if (!attr.getSpecified() && !bl2) continue;
                        Attr attr2 = (Attr)this.importNode(attr, true, bl2, hashMap);
                        if (!bl3 || attr.getLocalName() == null) {
                            node2.setAttributeNode(attr2);
                            continue;
                        }
                        node2.setAttributeNodeNS(attr2);
                    }
                }
                if (hashMap != null && (v = hashMap.get(node)) != null) {
                    if (this.identifiers == null) {
                        this.identifiers = new Hashtable();
                    }
                    this.identifiers.put(v, node2);
                }
                node3 = node2;
                break;
            }
            case 2: {
                node3 = node.getOwnerDocument().getImplementation().hasFeature("XML", "2.0") ? (node.getLocalName() == null ? this.createAttribute(node.getNodeName()) : this.createAttributeNS(node.getNamespaceURI(), node.getNodeName())) : this.createAttribute(node.getNodeName());
                if (node instanceof AttrImpl) {
                    node2 = (AttrImpl)node;
                    if (((NodeImpl)node2).hasStringValue()) {
                        AttrImpl attrImpl = (AttrImpl)node3;
                        attrImpl.setValue(((AttrImpl)node2).getValue());
                        bl = false;
                        break;
                    }
                    bl = true;
                    break;
                }
                if (node.getFirstChild() == null) {
                    node3.setNodeValue(node.getNodeValue());
                    bl = false;
                    break;
                }
                bl = true;
                break;
            }
            case 3: {
                node3 = this.createTextNode(node.getNodeValue());
                break;
            }
            case 4: {
                node3 = this.createCDATASection(node.getNodeValue());
                break;
            }
            case 5: {
                node3 = this.createEntityReference(node.getNodeName());
                bl = false;
                break;
            }
            case 6: {
                node2 = (Entity)node;
                EntityImpl entityImpl = (EntityImpl)this.createEntity(node.getNodeName());
                entityImpl.setPublicId(node2.getPublicId());
                entityImpl.setSystemId(node2.getSystemId());
                entityImpl.setNotationName(node2.getNotationName());
                entityImpl.isReadOnly(false);
                node3 = entityImpl;
                break;
            }
            case 7: {
                node3 = this.createProcessingInstruction(node.getNodeName(), node.getNodeValue());
                break;
            }
            case 8: {
                node3 = this.createComment(node.getNodeValue());
                break;
            }
            case 10: {
                int n;
                if (!bl2) {
                    String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
                    throw new DOMException(9, string);
                }
                node2 = (DocumentType)node;
                DocumentTypeImpl documentTypeImpl = (DocumentTypeImpl)this.createDocumentType(node2.getNodeName(), node2.getPublicId(), node2.getSystemId());
                documentTypeImpl.setInternalSubset(node2.getInternalSubset());
                NamedNodeMap namedNodeMap = node2.getEntities();
                NamedNodeMap namedNodeMap2 = documentTypeImpl.getEntities();
                if (namedNodeMap != null) {
                    for (n = 0; n < namedNodeMap.getLength(); ++n) {
                        namedNodeMap2.setNamedItem(this.importNode(namedNodeMap.item(n), true, true, hashMap));
                    }
                }
                namedNodeMap = node2.getNotations();
                namedNodeMap2 = documentTypeImpl.getNotations();
                if (namedNodeMap != null) {
                    for (n = 0; n < namedNodeMap.getLength(); ++n) {
                        namedNodeMap2.setNamedItem(this.importNode(namedNodeMap.item(n), true, true, hashMap));
                    }
                }
                node3 = documentTypeImpl;
                break;
            }
            case 11: {
                node3 = this.createDocumentFragment();
                break;
            }
            case 12: {
                node2 = (Notation)node;
                NotationImpl notationImpl = (NotationImpl)this.createNotation(node.getNodeName());
                notationImpl.setPublicId(node2.getPublicId());
                notationImpl.setSystemId(node2.getSystemId());
                node3 = notationImpl;
                break;
            }
            default: {
                String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
                throw new DOMException(9, string);
            }
        }
        if (hashtable != null) {
            this.callUserDataHandlers(node, node3, (short)2, hashtable);
        }
        if (bl) {
            for (node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
                node3.appendChild(this.importNode(node2, true, bl2, hashMap));
            }
        }
        if (node3.getNodeType() == 6) {
            ((NodeImpl)node3).setReadOnly(true, true);
        }
        return node3;
    }

    @Override
    public Node adoptNode(Node node) {
        Object object;
        Object object2;
        NodeImpl nodeImpl;
        Hashtable hashtable = null;
        try {
            nodeImpl = (NodeImpl)node;
        }
        catch (ClassCastException classCastException) {
            return null;
        }
        if (node == null) {
            return null;
        }
        if (node != null && node.getOwnerDocument() != null) {
            object2 = this.getImplementation();
            if (object2 != (object = node.getOwnerDocument().getImplementation())) {
                if (object2 instanceof DOMImplementationImpl && object instanceof DeferredDOMImplementationImpl) {
                    this.undeferChildren(nodeImpl);
                } else if (!(object2 instanceof DeferredDOMImplementationImpl) || !(object instanceof DOMImplementationImpl)) {
                    return null;
                }
            } else if (object instanceof DeferredDOMImplementationImpl) {
                this.undeferChildren(nodeImpl);
            }
        }
        switch (nodeImpl.getNodeType()) {
            case 2: {
                object2 = (AttrImpl)nodeImpl;
                if (((AttrImpl)object2).getOwnerElement() != null) {
                    ((AttrImpl)object2).getOwnerElement().removeAttributeNode((Attr)object2);
                }
                ((NodeImpl)object2).isSpecified(true);
                hashtable = nodeImpl.getUserDataRecord();
                ((AttrImpl)object2).setOwnerDocument(this);
                if (hashtable == null) break;
                this.setUserDataTable(nodeImpl, hashtable);
                break;
            }
            case 6: 
            case 12: {
                object2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException(7, (String)object2);
            }
            case 9: 
            case 10: {
                object2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
                throw new DOMException(9, (String)object2);
            }
            case 5: {
                NamedNodeMap namedNodeMap;
                Node node2;
                hashtable = nodeImpl.getUserDataRecord();
                object2 = nodeImpl.getParentNode();
                if (object2 != null) {
                    object2.removeChild(node);
                }
                while ((object = nodeImpl.getFirstChild()) != null) {
                    nodeImpl.removeChild((Node)object);
                }
                nodeImpl.setOwnerDocument(this);
                if (hashtable != null) {
                    this.setUserDataTable(nodeImpl, hashtable);
                }
                if (this.docType == null || (node2 = (namedNodeMap = this.docType.getEntities()).getNamedItem(nodeImpl.getNodeName())) == null) break;
                for (object = node2.getFirstChild(); object != null; object = object.getNextSibling()) {
                    Node node3 = object.cloneNode(true);
                    nodeImpl.appendChild(node3);
                }
                break;
            }
            case 1: {
                hashtable = nodeImpl.getUserDataRecord();
                object2 = nodeImpl.getParentNode();
                if (object2 != null) {
                    object2.removeChild(node);
                }
                nodeImpl.setOwnerDocument(this);
                if (hashtable != null) {
                    this.setUserDataTable(nodeImpl, hashtable);
                }
                ((ElementImpl)nodeImpl).reconcileDefaultAttributes();
                break;
            }
            default: {
                hashtable = nodeImpl.getUserDataRecord();
                object2 = nodeImpl.getParentNode();
                if (object2 != null) {
                    object2.removeChild(node);
                }
                nodeImpl.setOwnerDocument(this);
                if (hashtable == null) break;
                this.setUserDataTable(nodeImpl, hashtable);
            }
        }
        if (hashtable != null) {
            this.callUserDataHandlers(node, null, (short)5, hashtable);
        }
        return nodeImpl;
    }

    protected void undeferChildren(Node node) {
        Node node2 = node;
        while (null != node) {
            NamedNodeMap namedNodeMap;
            if (((NodeImpl)node).needsSyncData()) {
                ((NodeImpl)node).synchronizeData();
            }
            if ((namedNodeMap = node.getAttributes()) != null) {
                int n = namedNodeMap.getLength();
                for (int i = 0; i < n; ++i) {
                    this.undeferChildren(namedNodeMap.item(i));
                }
            }
            Node node3 = null;
            node3 = node.getFirstChild();
            while (null == node3 && !node2.equals(node)) {
                node3 = node.getNextSibling();
                if (null != node3 || null != (node = node.getParentNode()) && !node2.equals(node)) continue;
                node3 = null;
                break;
            }
            node = node3;
        }
    }

    @Override
    public Element getElementById(String string) {
        return this.getIdentifier(string);
    }

    protected final void clearIdentifiers() {
        if (this.identifiers != null) {
            this.identifiers.clear();
        }
    }

    public void putIdentifier(String string, Element element) {
        if (element == null) {
            this.removeIdentifier(string);
            return;
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.identifiers == null) {
            this.identifiers = new Hashtable();
        }
        this.identifiers.put(string, element);
    }

    public Element getIdentifier(String string) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.identifiers == null) {
            return null;
        }
        Element element = (Element)this.identifiers.get(string);
        if (element != null) {
            for (Node node = element.getParentNode(); node != null; node = node.getParentNode()) {
                if (node != this) continue;
                return element;
            }
        }
        return null;
    }

    public void removeIdentifier(String string) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.identifiers == null) {
            return;
        }
        this.identifiers.remove(string);
    }

    public Enumeration getIdentifiers() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.identifiers == null) {
            this.identifiers = new Hashtable();
        }
        return this.identifiers.keys();
    }

    @Override
    public Element createElementNS(String string, String string2) throws DOMException {
        return new ElementNSImpl(this, string, string2);
    }

    public Element createElementNS(String string, String string2, String string3) throws DOMException {
        return new ElementNSImpl(this, string, string2, string3);
    }

    @Override
    public Attr createAttributeNS(String string, String string2) throws DOMException {
        return new AttrNSImpl(this, string, string2);
    }

    public Attr createAttributeNS(String string, String string2, String string3) throws DOMException {
        return new AttrNSImpl(this, string, string2, string3);
    }

    @Override
    public NodeList getElementsByTagNameNS(String string, String string2) {
        return new DeepNodeListImpl(this, string, string2);
    }

    public Object clone() throws CloneNotSupportedException {
        CoreDocumentImpl coreDocumentImpl = (CoreDocumentImpl)super.clone();
        coreDocumentImpl.docType = null;
        coreDocumentImpl.docElement = null;
        return coreDocumentImpl;
    }

    public static final boolean isXMLName(String string, boolean bl) {
        if (string == null) {
            return false;
        }
        if (!bl) {
            return XMLChar.isValidName(string);
        }
        return XML11Char.isXML11ValidName(string);
    }

    public static final boolean isValidQName(String string, String string2, boolean bl) {
        if (string2 == null) {
            return false;
        }
        boolean bl2 = false;
        bl2 = !bl ? (string == null || XMLChar.isValidNCName(string)) && XMLChar.isValidNCName(string2) : (string == null || XML11Char.isXML11ValidNCName(string)) && XML11Char.isXML11ValidNCName(string2);
        return bl2;
    }

    protected boolean isKidOK(Node node, Node node2) {
        if (this.allowGrammarAccess && node.getNodeType() == 10) {
            return node2.getNodeType() == 1;
        }
        return 0 != (kidOK[node.getNodeType()] & 1 << node2.getNodeType());
    }

    @Override
    protected void changed() {
        ++this.changes;
    }

    @Override
    protected int changes() {
        return this.changes;
    }

    NodeListCache getNodeListCache(ParentNode parentNode) {
        if (this.fFreeNLCache == null) {
            return new NodeListCache(parentNode);
        }
        NodeListCache nodeListCache = this.fFreeNLCache;
        this.fFreeNLCache = this.fFreeNLCache.next;
        nodeListCache.fChild = null;
        nodeListCache.fChildIndex = -1;
        nodeListCache.fLength = -1;
        if (nodeListCache.fOwner != null) {
            nodeListCache.fOwner.fNodeListCache = null;
        }
        nodeListCache.fOwner = parentNode;
        return nodeListCache;
    }

    void freeNodeListCache(NodeListCache nodeListCache) {
        nodeListCache.next = this.fFreeNLCache;
        this.fFreeNLCache = nodeListCache;
    }

    public Object setUserData(Node node, String string, Object object, UserDataHandler userDataHandler) {
        Hashtable<String, ParentNode.UserDataRecord> hashtable;
        if (object == null) {
            Object v;
            Hashtable hashtable2;
            if (this.userData != null && (hashtable2 = (Hashtable)this.userData.get(node)) != null && (v = hashtable2.remove(string)) != null) {
                ParentNode.UserDataRecord userDataRecord = (ParentNode.UserDataRecord)v;
                return userDataRecord.fData;
            }
            return null;
        }
        if (this.userData == null) {
            this.userData = new WeakHashMap();
            hashtable = new Hashtable<String, ParentNode.UserDataRecord>();
            this.userData.put(node, hashtable);
        } else {
            hashtable = (Hashtable<String, ParentNode.UserDataRecord>)this.userData.get(node);
            if (hashtable == null) {
                hashtable = new Hashtable();
                this.userData.put(node, hashtable);
            }
        }
        ParentNode.UserDataRecord userDataRecord = hashtable.put(string, new ParentNode.UserDataRecord(this, object, userDataHandler));
        if (userDataRecord != null) {
            ParentNode.UserDataRecord userDataRecord2 = userDataRecord;
            return userDataRecord2.fData;
        }
        return null;
    }

    public Object getUserData(Node node, String string) {
        if (this.userData == null) {
            return null;
        }
        Hashtable hashtable = (Hashtable)this.userData.get(node);
        if (hashtable == null) {
            return null;
        }
        Object v = hashtable.get(string);
        if (v != null) {
            ParentNode.UserDataRecord userDataRecord = (ParentNode.UserDataRecord)v;
            return userDataRecord.fData;
        }
        return null;
    }

    protected Hashtable getUserDataRecord(Node node) {
        if (this.userData == null) {
            return null;
        }
        Hashtable hashtable = (Hashtable)this.userData.get(node);
        if (hashtable == null) {
            return null;
        }
        return hashtable;
    }

    Hashtable removeUserDataTable(Node node) {
        if (this.userData == null) {
            return null;
        }
        return (Hashtable)this.userData.get(node);
    }

    void setUserDataTable(Node node, Hashtable hashtable) {
        if (this.userData == null) {
            this.userData = new WeakHashMap();
        }
        if (hashtable != null) {
            this.userData.put(node, hashtable);
        }
    }

    protected void callUserDataHandlers(Node node, Node node2, short s) {
        if (this.userData == null) {
            return;
        }
        if (node instanceof NodeImpl) {
            Hashtable hashtable = ((NodeImpl)node).getUserDataRecord();
            if (hashtable == null || hashtable.isEmpty()) {
                return;
            }
            this.callUserDataHandlers(node, node2, s, hashtable);
        }
    }

    void callUserDataHandlers(Node node, Node node2, short s, Hashtable hashtable) {
        if (hashtable == null || hashtable.isEmpty()) {
            return;
        }
        for (Map.Entry entry : hashtable.entrySet()) {
            String string = (String)entry.getKey();
            ParentNode.UserDataRecord userDataRecord = (ParentNode.UserDataRecord)entry.getValue();
            if (userDataRecord.fHandler == null) continue;
            userDataRecord.fHandler.handle(s, string, userDataRecord.fData, node, node2);
        }
    }

    protected final void checkNamespaceWF(String string, int n, int n2) {
        if (!this.errorChecking) {
            return;
        }
        if (n == 0 || n == string.length() - 1 || n2 != n) {
            String string2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
            throw new DOMException(14, string2);
        }
    }

    protected final void checkDOMNSErr(String string, String string2) {
        if (this.errorChecking) {
            if (string2 == null) {
                String string3 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
                throw new DOMException(14, string3);
            }
            if (string.equals("xml") && !string2.equals(NamespaceContext.XML_URI)) {
                String string4 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
                throw new DOMException(14, string4);
            }
            if (string.equals("xmlns") && !string2.equals(NamespaceContext.XMLNS_URI) || !string.equals("xmlns") && string2.equals(NamespaceContext.XMLNS_URI)) {
                String string5 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
                throw new DOMException(14, string5);
            }
        }
    }

    protected final void checkQName(String string, String string2) {
        if (!this.errorChecking) {
            return;
        }
        boolean bl = false;
        if (!this.xml11Version) {
            bl = (string == null || XMLChar.isValidNCName(string)) && XMLChar.isValidNCName(string2);
        } else {
            boolean bl2 = bl = (string == null || XML11Char.isXML11ValidNCName(string)) && XML11Char.isXML11ValidNCName(string2);
        }
        if (!bl) {
            String string3 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
            throw new DOMException(5, string3);
        }
    }

    boolean isXML11Version() {
        return this.xml11Version;
    }

    boolean isNormalizeDocRequired() {
        return true;
    }

    boolean isXMLVersionChanged() {
        return this.xmlVersionChanged;
    }

    protected void setUserData(NodeImpl nodeImpl, Object object) {
        this.setUserData(nodeImpl, "XERCES1DOMUSERDATA", object, null);
    }

    protected Object getUserData(NodeImpl nodeImpl) {
        return this.getUserData(nodeImpl, "XERCES1DOMUSERDATA");
    }

    protected void addEventListener(NodeImpl nodeImpl, String string, EventListener eventListener, boolean bl) {
    }

    protected void removeEventListener(NodeImpl nodeImpl, String string, EventListener eventListener, boolean bl) {
    }

    protected void copyEventListeners(NodeImpl nodeImpl, NodeImpl nodeImpl2) {
    }

    protected boolean dispatchEvent(NodeImpl nodeImpl, Event event) {
        return false;
    }

    void replacedText(CharacterDataImpl characterDataImpl) {
    }

    void deletedText(CharacterDataImpl characterDataImpl, int n, int n2) {
    }

    void insertedText(CharacterDataImpl characterDataImpl, int n, int n2) {
    }

    void modifyingCharacterData(NodeImpl nodeImpl, boolean bl) {
    }

    void modifiedCharacterData(NodeImpl nodeImpl, String string, String string2, boolean bl) {
    }

    void insertingNode(NodeImpl nodeImpl, boolean bl) {
    }

    void insertedNode(NodeImpl nodeImpl, NodeImpl nodeImpl2, boolean bl) {
    }

    void removingNode(NodeImpl nodeImpl, NodeImpl nodeImpl2, boolean bl) {
    }

    void removedNode(NodeImpl nodeImpl, boolean bl) {
    }

    void replacingNode(NodeImpl nodeImpl) {
    }

    void replacedNode(NodeImpl nodeImpl) {
    }

    void replacingData(NodeImpl nodeImpl) {
    }

    void replacedCharacterData(NodeImpl nodeImpl, String string, String string2) {
    }

    void modifiedAttrValue(AttrImpl attrImpl, String string) {
    }

    void setAttrNode(AttrImpl attrImpl, AttrImpl attrImpl2) {
    }

    void removedAttrNode(AttrImpl attrImpl, NodeImpl nodeImpl, String string) {
    }

    void renamedAttrNode(Attr attr, Attr attr2) {
    }

    void renamedElement(Element element, Element element2) {
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        if (this.userData != null) {
            this.userData = new WeakHashMap(this.userData);
        }
        if (this.nodeTable != null) {
            this.nodeTable = new WeakHashMap(this.nodeTable);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        Map map = this.userData;
        Map map2 = this.nodeTable;
        try {
            if (map != null) {
                this.userData = new Hashtable(map);
            }
            if (map2 != null) {
                this.nodeTable = new Hashtable(map2);
            }
            objectOutputStream.defaultWriteObject();
        }
        finally {
            this.userData = map;
            this.nodeTable = map2;
        }
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

