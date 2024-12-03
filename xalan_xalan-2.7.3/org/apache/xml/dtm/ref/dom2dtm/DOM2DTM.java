/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.dtm.ref.dom2dtm;

import java.util.Vector;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.dom.DOMSource;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.DTMWSFilter;
import org.apache.xml.dtm.ref.DTMDefaultBaseIterators;
import org.apache.xml.dtm.ref.DTMManagerDefault;
import org.apache.xml.dtm.ref.ExpandedNameTable;
import org.apache.xml.dtm.ref.IncrementalSAXSource;
import org.apache.xml.dtm.ref.dom2dtm.DOM2DTMdefaultNamespaceDeclarationNode;
import org.apache.xml.res.XMLMessages;
import org.apache.xml.utils.FastStringBuffer;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.StringBufferPool;
import org.apache.xml.utils.TreeWalker;
import org.apache.xml.utils.XMLCharacterRecognizer;
import org.apache.xml.utils.XMLString;
import org.apache.xml.utils.XMLStringFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

public class DOM2DTM
extends DTMDefaultBaseIterators {
    static final boolean JJK_DEBUG = false;
    static final boolean JJK_NEWCODE = true;
    static final String NAMESPACE_DECL_NS = "http://www.w3.org/XML/1998/namespace";
    private transient Node m_pos;
    private int m_last_parent = 0;
    private int m_last_kid = -1;
    private transient Node m_root;
    boolean m_processedFirstElement = false;
    private transient boolean m_nodesAreProcessed;
    protected Vector m_nodes = new Vector();
    TreeWalker m_walker = new TreeWalker(null);

    public DOM2DTM(DTMManager mgr, DOMSource domSource, int dtmIdentity, DTMWSFilter whiteSpaceFilter, XMLStringFactory xstringfactory, boolean doIndexing) {
        super(mgr, domSource, dtmIdentity, whiteSpaceFilter, xstringfactory, doIndexing);
        this.m_pos = this.m_root = domSource.getNode();
        this.m_last_kid = -1;
        this.m_last_parent = -1;
        this.m_last_kid = this.addNode(this.m_root, this.m_last_parent, this.m_last_kid, -1);
        if (1 == this.m_root.getNodeType()) {
            int attrsize;
            NamedNodeMap attrs = this.m_root.getAttributes();
            int n = attrsize = attrs == null ? 0 : attrs.getLength();
            if (attrsize > 0) {
                int attrIndex = -1;
                for (int i = 0; i < attrsize; ++i) {
                    attrIndex = this.addNode(attrs.item(i), 0, attrIndex, -1);
                    this.m_firstch.setElementAt(-1, attrIndex);
                }
                this.m_nextsib.setElementAt(-1, attrIndex);
            }
        }
        this.m_nodesAreProcessed = false;
    }

    protected int addNode(Node node, int parentIndex, int previousSibling, int forceNodeType) {
        String localName;
        String name;
        int nodeIndex = this.m_nodes.size();
        if (this.m_dtmIdent.size() == nodeIndex >>> 16) {
            try {
                if (this.m_mgr == null) {
                    throw new ClassCastException();
                }
                DTMManagerDefault mgrD = (DTMManagerDefault)this.m_mgr;
                int id = mgrD.getFirstFreeDTMID();
                mgrD.addDTM(this, id, nodeIndex);
                this.m_dtmIdent.addElement(id << 16);
            }
            catch (ClassCastException e) {
                this.error(XMLMessages.createXMLMessage("ER_NO_DTMIDS_AVAIL", null));
            }
        }
        ++this.m_size;
        int type = -1 == forceNodeType ? (int)node.getNodeType() : forceNodeType;
        if (2 == type && ((name = node.getNodeName()).startsWith("xmlns:") || name.equals("xmlns"))) {
            type = 13;
        }
        this.m_nodes.addElement(node);
        this.m_firstch.setElementAt(-2, nodeIndex);
        this.m_nextsib.setElementAt(-2, nodeIndex);
        this.m_prevsib.setElementAt(previousSibling, nodeIndex);
        this.m_parent.setElementAt(parentIndex, nodeIndex);
        if (-1 != parentIndex && type != 2 && type != 13 && -2 == this.m_firstch.elementAt(parentIndex)) {
            this.m_firstch.setElementAt(nodeIndex, parentIndex);
        }
        String nsURI = node.getNamespaceURI();
        String string = localName = type == 7 ? node.getNodeName() : node.getLocalName();
        if ((type == 1 || type == 2) && null == localName) {
            localName = node.getNodeName();
        }
        ExpandedNameTable exnt = this.m_expandedNameTable;
        if (node.getLocalName() != null || type == 1 || type == 2) {
            // empty if block
        }
        int expandedNameID = null != localName ? exnt.getExpandedTypeID(nsURI, localName, type) : exnt.getExpandedTypeID(type);
        this.m_exptype.setElementAt(expandedNameID, nodeIndex);
        this.indexNode(expandedNameID, nodeIndex);
        if (-1 != previousSibling) {
            this.m_nextsib.setElementAt(nodeIndex, previousSibling);
        }
        if (type == 13) {
            this.declareNamespaceInContext(parentIndex, nodeIndex);
        }
        return nodeIndex;
    }

    @Override
    public int getNumberOfNodes() {
        return this.m_nodes.size();
    }

    @Override
    protected boolean nextNode() {
        if (this.m_nodesAreProcessed) {
            return false;
        }
        Node pos = this.m_pos;
        Node next = null;
        int nexttype = -1;
        do {
            if (pos.hasChildNodes()) {
                next = pos.getFirstChild();
                if (next != null && 10 == next.getNodeType()) {
                    next = next.getNextSibling();
                }
                if (5 != pos.getNodeType()) {
                    this.m_last_parent = this.m_last_kid;
                    this.m_last_kid = -1;
                    if (null != this.m_wsfilter) {
                        short wsv = this.m_wsfilter.getShouldStripSpace(this.makeNodeHandle(this.m_last_parent), this);
                        boolean shouldStrip = 3 == wsv ? this.getShouldStripWhitespace() : 2 == wsv;
                        this.pushShouldStripWhitespace(shouldStrip);
                    }
                }
            } else {
                if (this.m_last_kid != -1 && this.m_firstch.elementAt(this.m_last_kid) == -2) {
                    this.m_firstch.setElementAt(-1, this.m_last_kid);
                }
                while (this.m_last_parent != -1) {
                    next = pos.getNextSibling();
                    if (next != null && 10 == next.getNodeType()) {
                        next = next.getNextSibling();
                    }
                    if (next != null) break;
                    if ((pos = pos.getParentNode()) == null) {
                        // empty if block
                    }
                    if (pos != null && 5 == pos.getNodeType()) continue;
                    this.popShouldStripWhitespace();
                    if (this.m_last_kid == -1) {
                        this.m_firstch.setElementAt(-1, this.m_last_parent);
                    } else {
                        this.m_nextsib.setElementAt(-1, this.m_last_kid);
                    }
                    this.m_last_kid = this.m_last_parent;
                    this.m_last_parent = this.m_parent.elementAt(this.m_last_kid);
                }
                if (this.m_last_parent == -1) {
                    next = null;
                }
            }
            if (next != null) {
                nexttype = next.getNodeType();
            }
            if (5 != nexttype) continue;
            pos = next;
        } while (5 == nexttype);
        if (next == null) {
            this.m_nextsib.setElementAt(-1, 0);
            this.m_nodesAreProcessed = true;
            this.m_pos = null;
            return false;
        }
        boolean suppressNode = false;
        Node lastTextNode = null;
        nexttype = next.getNodeType();
        if (3 == nexttype || 4 == nexttype) {
            suppressNode = null != this.m_wsfilter && this.getShouldStripWhitespace();
            Node n = next;
            while (n != null) {
                lastTextNode = n;
                if (3 == n.getNodeType()) {
                    nexttype = 3;
                }
                suppressNode &= XMLCharacterRecognizer.isWhiteSpace(n.getNodeValue());
                n = this.logicalNextDOMTextNode(n);
            }
        } else if (7 == nexttype) {
            suppressNode = pos.getNodeName().toLowerCase().equals("xml");
        }
        if (!suppressNode) {
            int nextindex;
            this.m_last_kid = nextindex = this.addNode(next, this.m_last_parent, this.m_last_kid, nexttype);
            if (1 == nexttype) {
                int attrsize;
                int attrIndex = -1;
                NamedNodeMap attrs = next.getAttributes();
                int n = attrsize = attrs == null ? 0 : attrs.getLength();
                if (attrsize > 0) {
                    for (int i = 0; i < attrsize; ++i) {
                        attrIndex = this.addNode(attrs.item(i), nextindex, attrIndex, -1);
                        this.m_firstch.setElementAt(-1, attrIndex);
                        if (this.m_processedFirstElement || !"xmlns:xml".equals(attrs.item(i).getNodeName())) continue;
                        this.m_processedFirstElement = true;
                    }
                }
                if (!this.m_processedFirstElement) {
                    attrIndex = this.addNode(new DOM2DTMdefaultNamespaceDeclarationNode((Element)next, "xml", NAMESPACE_DECL_NS, this.makeNodeHandle((attrIndex == -1 ? nextindex : attrIndex) + 1)), nextindex, attrIndex, -1);
                    this.m_firstch.setElementAt(-1, attrIndex);
                    this.m_processedFirstElement = true;
                }
                if (attrIndex != -1) {
                    this.m_nextsib.setElementAt(-1, attrIndex);
                }
            }
        }
        if (3 == nexttype || 4 == nexttype) {
            next = lastTextNode;
        }
        this.m_pos = next;
        return true;
    }

    @Override
    public Node getNode(int nodeHandle) {
        int identity = this.makeNodeIdentity(nodeHandle);
        return (Node)this.m_nodes.elementAt(identity);
    }

    protected Node lookupNode(int nodeIdentity) {
        return (Node)this.m_nodes.elementAt(nodeIdentity);
    }

    @Override
    protected int getNextNodeIdentity(int identity) {
        if (++identity >= this.m_nodes.size() && !this.nextNode()) {
            identity = -1;
        }
        return identity;
    }

    private int getHandleFromNode(Node node) {
        if (null != node) {
            int len = this.m_nodes.size();
            int i = 0;
            while (true) {
                if (i < len) {
                    if (this.m_nodes.elementAt(i) == node) {
                        return this.makeNodeHandle(i);
                    }
                    ++i;
                    continue;
                }
                boolean isMore = this.nextNode();
                len = this.m_nodes.size();
                if (!isMore && i >= len) break;
            }
        }
        return -1;
    }

    public int getHandleOfNode(Node node) {
        if (null != node && (this.m_root == node || this.m_root.getNodeType() == 9 && this.m_root == node.getOwnerDocument() || this.m_root.getNodeType() != 9 && this.m_root.getOwnerDocument() == node.getOwnerDocument())) {
            Node cursor = node;
            while (cursor != null) {
                if (cursor == this.m_root) {
                    return this.getHandleFromNode(node);
                }
                cursor = cursor.getNodeType() != 2 ? cursor.getParentNode() : ((Attr)cursor).getOwnerElement();
            }
        }
        return -1;
    }

    @Override
    public int getAttributeNode(int nodeHandle, String namespaceURI, String name) {
        short type;
        if (null == namespaceURI) {
            namespaceURI = "";
        }
        if (1 == (type = this.getNodeType(nodeHandle))) {
            int identity = this.makeNodeIdentity(nodeHandle);
            while (-1 != (identity = this.getNextNodeIdentity(identity)) && ((type = this._type(identity)) == 2 || type == 13)) {
                Node node = this.lookupNode(identity);
                String nodeuri = node.getNamespaceURI();
                if (null == nodeuri) {
                    nodeuri = "";
                }
                String nodelocalname = node.getLocalName();
                if (!nodeuri.equals(namespaceURI) || !name.equals(nodelocalname)) continue;
                return this.makeNodeHandle(identity);
            }
        }
        return -1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XMLString getStringValue(int nodeHandle) {
        short type = this.getNodeType(nodeHandle);
        Node node = this.getNode(nodeHandle);
        if (1 == type || 9 == type || 11 == type) {
            String s;
            FastStringBuffer buf = StringBufferPool.get();
            try {
                DOM2DTM.getNodeData(node, buf);
                s = buf.length() > 0 ? buf.toString() : "";
            }
            finally {
                StringBufferPool.free(buf);
            }
            return this.m_xstrf.newstr(s);
        }
        if (3 == type || 4 == type) {
            FastStringBuffer buf = StringBufferPool.get();
            while (node != null) {
                buf.append(node.getNodeValue());
                node = this.logicalNextDOMTextNode(node);
            }
            String s = buf.length() > 0 ? buf.toString() : "";
            StringBufferPool.free(buf);
            return this.m_xstrf.newstr(s);
        }
        return this.m_xstrf.newstr(node.getNodeValue());
    }

    public boolean isWhitespace(int nodeHandle) {
        short type = this.getNodeType(nodeHandle);
        Node node = this.getNode(nodeHandle);
        if (3 == type || 4 == type) {
            FastStringBuffer buf = StringBufferPool.get();
            while (node != null) {
                buf.append(node.getNodeValue());
                node = this.logicalNextDOMTextNode(node);
            }
            boolean b = buf.isWhitespace(0, buf.length());
            StringBufferPool.free(buf);
            return b;
        }
        return false;
    }

    protected static void getNodeData(Node node, FastStringBuffer buf) {
        switch (node.getNodeType()) {
            case 1: 
            case 9: 
            case 11: {
                for (Node child = node.getFirstChild(); null != child; child = child.getNextSibling()) {
                    DOM2DTM.getNodeData(child, buf);
                }
                break;
            }
            case 2: 
            case 3: 
            case 4: {
                buf.append(node.getNodeValue());
                break;
            }
            case 7: {
                break;
            }
        }
    }

    @Override
    public String getNodeName(int nodeHandle) {
        Node node = this.getNode(nodeHandle);
        return node.getNodeName();
    }

    @Override
    public String getNodeNameX(int nodeHandle) {
        String name;
        short type = this.getNodeType(nodeHandle);
        switch (type) {
            case 13: {
                Node node = this.getNode(nodeHandle);
                name = node.getNodeName();
                if (name.startsWith("xmlns:")) {
                    name = QName.getLocalPart(name);
                    break;
                }
                if (!name.equals("xmlns")) break;
                name = "";
                break;
            }
            case 1: 
            case 2: 
            case 5: 
            case 7: {
                Node node = this.getNode(nodeHandle);
                name = node.getNodeName();
                break;
            }
            default: {
                name = "";
            }
        }
        return name;
    }

    @Override
    public String getLocalName(int nodeHandle) {
        int id = this.makeNodeIdentity(nodeHandle);
        if (-1 == id) {
            return null;
        }
        Node newnode = (Node)this.m_nodes.elementAt(id);
        String newname = newnode.getLocalName();
        if (null == newname) {
            int index;
            String qname = newnode.getNodeName();
            newname = '#' == qname.charAt(0) ? "" : ((index = qname.indexOf(58)) < 0 ? qname : qname.substring(index + 1));
        }
        return newname;
    }

    @Override
    public String getPrefix(int nodeHandle) {
        String prefix;
        short type = this.getNodeType(nodeHandle);
        switch (type) {
            case 13: {
                Node node = this.getNode(nodeHandle);
                String qname = node.getNodeName();
                int index = qname.indexOf(58);
                prefix = index < 0 ? "" : qname.substring(index + 1);
                break;
            }
            case 1: 
            case 2: {
                Node node = this.getNode(nodeHandle);
                String qname = node.getNodeName();
                int index = qname.indexOf(58);
                prefix = index < 0 ? "" : qname.substring(0, index);
                break;
            }
            default: {
                prefix = "";
            }
        }
        return prefix;
    }

    @Override
    public String getNamespaceURI(int nodeHandle) {
        int id = this.makeNodeIdentity(nodeHandle);
        if (id == -1) {
            return null;
        }
        Node node = (Node)this.m_nodes.elementAt(id);
        return node.getNamespaceURI();
    }

    private Node logicalNextDOMTextNode(Node n) {
        short ntype;
        Node p = n.getNextSibling();
        if (p == null) {
            for (n = n.getParentNode(); n != null && 5 == n.getNodeType() && (p = n.getNextSibling()) == null; n = n.getParentNode()) {
            }
        }
        n = p;
        while (n != null && 5 == n.getNodeType()) {
            if (n.hasChildNodes()) {
                n = n.getFirstChild();
                continue;
            }
            n = n.getNextSibling();
        }
        if (n != null && 3 != (ntype = n.getNodeType()) && 4 != ntype) {
            n = null;
        }
        return n;
    }

    @Override
    public String getNodeValue(int nodeHandle) {
        int type = this._exptype(this.makeNodeIdentity(nodeHandle));
        int n = type = -1 != type ? (int)this.getNodeType(nodeHandle) : -1;
        if (3 != type && 4 != type) {
            return this.getNode(nodeHandle).getNodeValue();
        }
        Node node = this.getNode(nodeHandle);
        Node n2 = this.logicalNextDOMTextNode(node);
        if (n2 == null) {
            return node.getNodeValue();
        }
        FastStringBuffer buf = StringBufferPool.get();
        buf.append(node.getNodeValue());
        while (n2 != null) {
            buf.append(n2.getNodeValue());
            n2 = this.logicalNextDOMTextNode(n2);
        }
        String s = buf.length() > 0 ? buf.toString() : "";
        StringBufferPool.free(buf);
        return s;
    }

    @Override
    public String getDocumentTypeDeclarationSystemIdentifier() {
        DocumentType dtd;
        Document doc = this.m_root.getNodeType() == 9 ? (Document)this.m_root : this.m_root.getOwnerDocument();
        if (null != doc && null != (dtd = doc.getDoctype())) {
            return dtd.getSystemId();
        }
        return null;
    }

    @Override
    public String getDocumentTypeDeclarationPublicIdentifier() {
        DocumentType dtd;
        Document doc = this.m_root.getNodeType() == 9 ? (Document)this.m_root : this.m_root.getOwnerDocument();
        if (null != doc && null != (dtd = doc.getDoctype())) {
            return dtd.getPublicId();
        }
        return null;
    }

    @Override
    public int getElementById(String elementId) {
        Element elem;
        Document doc;
        Document document = doc = this.m_root.getNodeType() == 9 ? (Document)this.m_root : this.m_root.getOwnerDocument();
        if (null != doc && null != (elem = doc.getElementById(elementId))) {
            int elemHandle = this.getHandleFromNode(elem);
            if (-1 == elemHandle) {
                int identity = this.m_nodes.size() - 1;
                while (-1 != (identity = this.getNextNodeIdentity(identity))) {
                    Node node = this.getNode(identity);
                    if (node != elem) continue;
                    elemHandle = this.getHandleFromNode(elem);
                    break;
                }
            }
            return elemHandle;
        }
        return -1;
    }

    @Override
    public String getUnparsedEntityURI(String name) {
        DocumentType doctype;
        Document doc;
        String url = "";
        Document document = doc = this.m_root.getNodeType() == 9 ? (Document)this.m_root : this.m_root.getOwnerDocument();
        if (null != doc && null != (doctype = doc.getDoctype())) {
            NamedNodeMap entities = doctype.getEntities();
            if (null == entities) {
                return url;
            }
            Entity entity = (Entity)entities.getNamedItem(name);
            if (null == entity) {
                return url;
            }
            String notationName = entity.getNotationName();
            if (null != notationName && null == (url = entity.getSystemId())) {
                url = entity.getPublicId();
            }
        }
        return url;
    }

    @Override
    public boolean isAttributeSpecified(int attributeHandle) {
        short type = this.getNodeType(attributeHandle);
        if (2 == type) {
            Attr attr = (Attr)this.getNode(attributeHandle);
            return attr.getSpecified();
        }
        return false;
    }

    public void setIncrementalSAXSource(IncrementalSAXSource source) {
    }

    @Override
    public ContentHandler getContentHandler() {
        return null;
    }

    @Override
    public LexicalHandler getLexicalHandler() {
        return null;
    }

    @Override
    public EntityResolver getEntityResolver() {
        return null;
    }

    @Override
    public DTDHandler getDTDHandler() {
        return null;
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return null;
    }

    @Override
    public DeclHandler getDeclHandler() {
        return null;
    }

    @Override
    public boolean needsTwoThreads() {
        return false;
    }

    private static boolean isSpace(char ch) {
        return XMLCharacterRecognizer.isWhiteSpace(ch);
    }

    @Override
    public void dispatchCharactersEvents(int nodeHandle, ContentHandler ch, boolean normalize) throws SAXException {
        if (normalize) {
            XMLString str = this.getStringValue(nodeHandle);
            str = str.fixWhiteSpace(true, true, false);
            str.dispatchCharactersEvents(ch);
        } else {
            short type = this.getNodeType(nodeHandle);
            Node node = this.getNode(nodeHandle);
            DOM2DTM.dispatchNodeData(node, ch, 0);
            if (3 == type || 4 == type) {
                while (null != (node = this.logicalNextDOMTextNode(node))) {
                    DOM2DTM.dispatchNodeData(node, ch, 0);
                }
            }
        }
    }

    protected static void dispatchNodeData(Node node, ContentHandler ch, int depth) throws SAXException {
        switch (node.getNodeType()) {
            case 1: 
            case 9: 
            case 11: {
                for (Node child = node.getFirstChild(); null != child; child = child.getNextSibling()) {
                    DOM2DTM.dispatchNodeData(child, ch, depth + 1);
                }
                break;
            }
            case 7: 
            case 8: {
                if (0 != depth) break;
            }
            case 2: 
            case 3: 
            case 4: {
                String str = node.getNodeValue();
                if (ch instanceof CharacterNodeHandler) {
                    ((CharacterNodeHandler)((Object)ch)).characters(node);
                    break;
                }
                ch.characters(str.toCharArray(), 0, str.length());
                break;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void dispatchToEvents(int nodeHandle, ContentHandler ch) throws SAXException {
        TreeWalker treeWalker = this.m_walker;
        ContentHandler prevCH = treeWalker.getContentHandler();
        if (null != prevCH) {
            treeWalker = new TreeWalker(null);
        }
        treeWalker.setContentHandler(ch);
        try {
            Node node = this.getNode(nodeHandle);
            treeWalker.traverseFragment(node);
        }
        finally {
            treeWalker.setContentHandler(null);
        }
    }

    @Override
    public void setProperty(String property, Object value) {
    }

    @Override
    public SourceLocator getSourceLocatorFor(int node) {
        return null;
    }

    public static interface CharacterNodeHandler {
        public void characters(Node var1) throws SAXException;
    }
}

