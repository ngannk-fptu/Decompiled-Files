/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serializer.SerializationHandler
 *  org.apache.xml.serializer.ToXMLSAXHandler
 */
package org.apache.xalan.xsltc.dom;

import java.util.Iterator;
import java.util.Map;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.DOMEnhancedForDTM;
import org.apache.xalan.xsltc.StripFilter;
import org.apache.xalan.xsltc.TransletException;
import org.apache.xalan.xsltc.dom.AdaptiveResultTreeImpl;
import org.apache.xalan.xsltc.dom.BitArray;
import org.apache.xalan.xsltc.dom.DOMBuilder;
import org.apache.xalan.xsltc.dom.DupFilterIterator;
import org.apache.xalan.xsltc.dom.SimpleResultTreeImpl;
import org.apache.xalan.xsltc.dom.XSLTCDTMManager;
import org.apache.xalan.xsltc.runtime.BasisLibrary;
import org.apache.xalan.xsltc.runtime.Hashtable;
import org.apache.xml.dtm.Axis;
import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.DTMWSFilter;
import org.apache.xml.dtm.ref.DTMAxisIterNodeList;
import org.apache.xml.dtm.ref.DTMDefaultBaseIterators;
import org.apache.xml.dtm.ref.DTMNodeProxy;
import org.apache.xml.dtm.ref.EmptyIterator;
import org.apache.xml.dtm.ref.sax2dtm.SAX2DTM2;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xml.serializer.ToXMLSAXHandler;
import org.apache.xml.utils.SystemIDResolver;
import org.apache.xml.utils.XMLStringFactory;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public final class SAXImpl
extends SAX2DTM2
implements DOMEnhancedForDTM,
DOMBuilder {
    private int _uriCount = 0;
    private int _prefixCount = 0;
    private int[] _xmlSpaceStack;
    private int _idx = 1;
    private boolean _preserve = false;
    private static final String XML_STRING = "xml:";
    private static final String XML_PREFIX = "xml";
    private static final String XMLSPACE_STRING = "xml:space";
    private static final String PRESERVE_STRING = "preserve";
    private static final String XMLNS_PREFIX = "xmlns";
    private static final String XML_URI = "http://www.w3.org/XML/1998/namespace";
    private boolean _escaping = true;
    private boolean _disableEscaping = false;
    private int _textNodeToProcess = -1;
    private static final String EMPTYSTRING = "";
    private static final DTMAxisIterator EMPTYITERATOR = EmptyIterator.getInstance();
    private int _namesSize = -1;
    private Hashtable _nsIndex = new Hashtable();
    private int _size = 0;
    private BitArray _dontEscape = null;
    private String _documentURI = null;
    private static int _documentURIIndex = 0;
    private Document _document;
    private Hashtable _node2Ids = null;
    private boolean _hasDOMSource = false;
    private XSLTCDTMManager _dtmManager;
    private Node[] _nodes;
    private NodeList[] _nodeLists;
    private static final String XML_LANG_ATTRIBUTE = "http://www.w3.org/XML/1998/namespace:@lang";

    @Override
    public void setDocumentURI(String uri) {
        if (uri != null) {
            this.setDocumentBaseURI(SystemIDResolver.getAbsoluteURI(uri));
        }
    }

    @Override
    public String getDocumentURI() {
        String baseURI = this.getDocumentBaseURI();
        return baseURI != null ? baseURI : "rtf" + _documentURIIndex++;
    }

    @Override
    public String getDocumentURI(int node) {
        return this.getDocumentURI();
    }

    @Override
    public void setupMapping(String[] names, String[] urisArray, int[] typesArray, String[] namespaces) {
    }

    @Override
    public String lookupNamespace(int node, String prefix) throws TransletException {
        int anode;
        SAX2DTM2.AncestorIterator ancestors = new SAX2DTM2.AncestorIterator(this);
        if (this.isElement(node)) {
            ancestors.includeSelf();
        }
        ancestors.setStartNode(node);
        while ((anode = ancestors.next()) != -1) {
            int nsnode;
            DTMDefaultBaseIterators.NamespaceIterator namespaces = new DTMDefaultBaseIterators.NamespaceIterator(this);
            namespaces.setStartNode(anode);
            while ((nsnode = namespaces.next()) != -1) {
                if (!this.getLocalName(nsnode).equals(prefix)) continue;
                return this.getNodeValue(nsnode);
            }
        }
        BasisLibrary.runTimeError("NAMESPACE_PREFIX_ERR", prefix);
        return null;
    }

    @Override
    public boolean isElement(int node) {
        return this.getNodeType(node) == 1;
    }

    @Override
    public boolean isAttribute(int node) {
        return this.getNodeType(node) == 2;
    }

    @Override
    public int getSize() {
        return this.getNumberOfNodes();
    }

    @Override
    public void setFilter(StripFilter filter) {
    }

    @Override
    public boolean lessThan(int node1, int node2) {
        if (node1 == -1) {
            return false;
        }
        if (node2 == -1) {
            return true;
        }
        return node1 < node2;
    }

    @Override
    public Node makeNode(int index) {
        int nodeID;
        if (this._nodes == null) {
            this._nodes = new Node[this._namesSize];
        }
        if ((nodeID = this.makeNodeIdentity(index)) < 0) {
            return null;
        }
        if (nodeID < this._nodes.length) {
            return this._nodes[nodeID] != null ? this._nodes[nodeID] : new DTMNodeProxy(this, index);
        }
        return new DTMNodeProxy(this, index);
    }

    @Override
    public Node makeNode(DTMAxisIterator iter) {
        return this.makeNode(iter.next());
    }

    @Override
    public NodeList makeNodeList(int index) {
        int nodeID;
        if (this._nodeLists == null) {
            this._nodeLists = new NodeList[this._namesSize];
        }
        if ((nodeID = this.makeNodeIdentity(index)) < 0) {
            return null;
        }
        if (nodeID < this._nodeLists.length) {
            return this._nodeLists[nodeID] != null ? this._nodeLists[nodeID] : new DTMAxisIterNodeList(this, new DTMDefaultBaseIterators.SingletonIterator(this, index));
        }
        return new DTMAxisIterNodeList(this, new DTMDefaultBaseIterators.SingletonIterator(this, index));
    }

    @Override
    public NodeList makeNodeList(DTMAxisIterator iter) {
        return new DTMAxisIterNodeList(this, iter);
    }

    @Override
    public DTMAxisIterator getNodeValueIterator(DTMAxisIterator iterator, int type, String value, boolean op) {
        return new NodeValueIterator(iterator, type, value, op);
    }

    @Override
    public DTMAxisIterator orderNodes(DTMAxisIterator source, int node) {
        return new DupFilterIterator(source);
    }

    @Override
    public DTMAxisIterator getIterator() {
        return new DTMDefaultBaseIterators.SingletonIterator(this, this.getDocument(), true);
    }

    @Override
    public int getNSType(int node) {
        String s = this.getNamespaceURI(node);
        if (s == null) {
            return 0;
        }
        int eType = this.getIdForNamespace(s);
        return (Integer)this._nsIndex.get(new Integer(eType));
    }

    @Override
    public int getNamespaceType(int node) {
        return super.getNamespaceType(node);
    }

    private int[] setupMapping(String[] names, String[] uris, int[] types, int nNames) {
        int[] result = new int[this.m_expandedNameTable.getSize()];
        for (int i = 0; i < nNames; ++i) {
            int type;
            result[type] = type = this.m_expandedNameTable.getExpandedTypeID(uris[i], names[i], types[i], false);
        }
        return result;
    }

    public int getGeneralizedType(String name) {
        return this.getGeneralizedType(name, true);
    }

    public int getGeneralizedType(String name, boolean searchOnly) {
        int code;
        int lNameStartIdx;
        String ns = null;
        int index = -1;
        index = name.lastIndexOf(58);
        if (index > -1) {
            ns = name.substring(0, index);
        }
        if (name.charAt(lNameStartIdx = index + 1) == '@') {
            code = 2;
            ++lNameStartIdx;
        } else {
            code = 1;
        }
        String lName = lNameStartIdx == 0 ? name : name.substring(lNameStartIdx);
        return this.m_expandedNameTable.getExpandedTypeID(ns, lName, code, searchOnly);
    }

    @Override
    public short[] getMapping(String[] names, String[] uris, int[] types) {
        int i;
        if (this._namesSize < 0) {
            return this.getMapping2(names, uris, types);
        }
        int namesLength = names.length;
        int exLength = this.m_expandedNameTable.getSize();
        short[] result = new short[exLength];
        for (i = 0; i < 14; ++i) {
            result[i] = (short)i;
        }
        for (i = 14; i < exLength; ++i) {
            result[i] = this.m_expandedNameTable.getType(i);
        }
        for (i = 0; i < namesLength; ++i) {
            int genType = this.m_expandedNameTable.getExpandedTypeID(uris[i], names[i], types[i], true);
            if (genType < 0 || genType >= exLength) continue;
            result[genType] = (short)(i + 14);
        }
        return result;
    }

    @Override
    public int[] getReverseMapping(String[] names, String[] uris, int[] types) {
        int i;
        int[] result = new int[names.length + 14];
        for (i = 0; i < 14; ++i) {
            result[i] = i;
        }
        for (i = 0; i < names.length; ++i) {
            int type;
            result[i + 14] = type = this.m_expandedNameTable.getExpandedTypeID(uris[i], names[i], types[i], true);
        }
        return result;
    }

    private short[] getMapping2(String[] names, String[] uris, int[] types) {
        int i;
        int namesLength = names.length;
        int exLength = this.m_expandedNameTable.getSize();
        int[] generalizedTypes = null;
        if (namesLength > 0) {
            generalizedTypes = new int[namesLength];
        }
        int resultLength = exLength;
        for (i = 0; i < namesLength; ++i) {
            generalizedTypes[i] = this.m_expandedNameTable.getExpandedTypeID(uris[i], names[i], types[i], false);
            if (this._namesSize >= 0 || generalizedTypes[i] < resultLength) continue;
            resultLength = generalizedTypes[i] + 1;
        }
        short[] result = new short[resultLength];
        for (i = 0; i < 14; ++i) {
            result[i] = (short)i;
        }
        for (i = 14; i < exLength; ++i) {
            result[i] = this.m_expandedNameTable.getType(i);
        }
        for (i = 0; i < namesLength; ++i) {
            int genType = generalizedTypes[i];
            if (genType < 0 || genType >= resultLength) continue;
            result[genType] = (short)(i + 14);
        }
        return result;
    }

    @Override
    public short[] getNamespaceMapping(String[] namespaces) {
        int i;
        int nsLength = namespaces.length;
        int mappingLength = this._uriCount;
        short[] result = new short[mappingLength];
        for (i = 0; i < mappingLength; ++i) {
            result[i] = -1;
        }
        for (i = 0; i < nsLength; ++i) {
            int eType = this.getIdForNamespace(namespaces[i]);
            Integer type = (Integer)this._nsIndex.get(new Integer(eType));
            if (type == null) continue;
            result[type.intValue()] = (short)i;
        }
        return result;
    }

    @Override
    public short[] getReverseNamespaceMapping(String[] namespaces) {
        int length = namespaces.length;
        short[] result = new short[length];
        for (int i = 0; i < length; ++i) {
            int eType = this.getIdForNamespace(namespaces[i]);
            Integer type = (Integer)this._nsIndex.get(new Integer(eType));
            result[i] = type == null ? -1 : (int)type.shortValue();
        }
        return result;
    }

    public SAXImpl(XSLTCDTMManager mgr, Source source, int dtmIdentity, DTMWSFilter whiteSpaceFilter, XMLStringFactory xstringfactory, boolean doIndexing, boolean buildIdIndex) {
        this(mgr, source, dtmIdentity, whiteSpaceFilter, xstringfactory, doIndexing, 512, buildIdIndex, false);
    }

    public SAXImpl(XSLTCDTMManager mgr, Source source, int dtmIdentity, DTMWSFilter whiteSpaceFilter, XMLStringFactory xstringfactory, boolean doIndexing, int blocksize, boolean buildIdIndex, boolean newNameTable) {
        super(mgr, source, dtmIdentity, whiteSpaceFilter, xstringfactory, doIndexing, blocksize, false, buildIdIndex, newNameTable);
        this._dtmManager = mgr;
        this._size = blocksize;
        this._xmlSpaceStack = new int[blocksize <= 64 ? 4 : 64];
        this._xmlSpaceStack[0] = 0;
        if (source instanceof DOMSource) {
            this._hasDOMSource = true;
            DOMSource domsrc = (DOMSource)source;
            Node node = domsrc.getNode();
            this._document = node instanceof Document ? (Document)node : node.getOwnerDocument();
            this._node2Ids = new Hashtable();
        }
    }

    @Override
    public void migrateTo(DTMManager manager) {
        super.migrateTo(manager);
        if (manager instanceof XSLTCDTMManager) {
            this._dtmManager = (XSLTCDTMManager)manager;
        }
    }

    @Override
    public int getElementById(String idString) {
        Element node = this._document.getElementById(idString);
        if (node != null) {
            Integer id = (Integer)this._node2Ids.get(node);
            return id != null ? id : -1;
        }
        return -1;
    }

    @Override
    public boolean hasDOMSource() {
        return this._hasDOMSource;
    }

    private void xmlSpaceDefine(String val, int node) {
        boolean setting = val.equals(PRESERVE_STRING);
        if (setting != this._preserve) {
            this._xmlSpaceStack[this._idx++] = node;
            this._preserve = setting;
        }
    }

    private void xmlSpaceRevert(int node) {
        if (node == this._xmlSpaceStack[this._idx - 1]) {
            --this._idx;
            this._preserve = !this._preserve;
        }
    }

    @Override
    protected boolean getShouldStripWhitespace() {
        return this._preserve ? false : super.getShouldStripWhitespace();
    }

    private void handleTextEscaping() {
        if (this._disableEscaping && this._textNodeToProcess != -1 && this._type(this._textNodeToProcess) == 3) {
            if (this._dontEscape == null) {
                this._dontEscape = new BitArray(this._size);
            }
            if (this._textNodeToProcess >= this._dontEscape.size()) {
                this._dontEscape.resize(this._dontEscape.size() * 2);
            }
            this._dontEscape.setBit(this._textNodeToProcess);
            this._disableEscaping = false;
        }
        this._textNodeToProcess = -1;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        this._disableEscaping = !this._escaping;
        this._textNodeToProcess = this.getNumberOfNodes();
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        this._nsIndex.put(new Integer(0), new Integer(this._uriCount++));
        this.definePrefixAndUri(XML_PREFIX, XML_URI);
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        this.handleTextEscaping();
        this._namesSize = this.m_expandedNameTable.getSize();
    }

    public void startElement(String uri, String localName, String qname, Attributes attributes, Node node) throws SAXException {
        this.startElement(uri, localName, qname, attributes);
        if (this.m_buildIdIndex) {
            this._node2Ids.put(node, new Integer(this.m_parents.peek()));
        }
    }

    @Override
    public void startElement(String uri, String localName, String qname, Attributes attributes) throws SAXException {
        int index;
        super.startElement(uri, localName, qname, attributes);
        this.handleTextEscaping();
        if (this.m_wsfilter != null && (index = attributes.getIndex(XMLSPACE_STRING)) >= 0) {
            this.xmlSpaceDefine(attributes.getValue(index), this.m_parents.peek());
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qname) throws SAXException {
        super.endElement(namespaceURI, localName, qname);
        this.handleTextEscaping();
        if (this.m_wsfilter != null) {
            this.xmlSpaceRevert(this.m_previous);
        }
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        super.processingInstruction(target, data);
        this.handleTextEscaping();
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        super.ignorableWhitespace(ch, start, length);
        this._textNodeToProcess = this.getNumberOfNodes();
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        super.startPrefixMapping(prefix, uri);
        this.handleTextEscaping();
        this.definePrefixAndUri(prefix, uri);
    }

    private void definePrefixAndUri(String prefix, String uri) throws SAXException {
        Integer eType = new Integer(this.getIdForNamespace(uri));
        if ((Integer)this._nsIndex.get(eType) == null) {
            this._nsIndex.put(eType, new Integer(this._uriCount++));
        }
    }

    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
        super.comment(ch, start, length);
        this.handleTextEscaping();
    }

    public boolean setEscaping(boolean value) {
        boolean temp = this._escaping;
        this._escaping = value;
        return temp;
    }

    public void print(int node, int level) {
        switch (this.getNodeType(node)) {
            case 0: 
            case 9: {
                this.print(this.getFirstChild(node), level);
                break;
            }
            case 3: 
            case 7: 
            case 8: {
                System.out.print(this.getStringValueX(node));
                break;
            }
            default: {
                String name = this.getNodeName(node);
                System.out.print("<" + name);
                int a = this.getFirstAttribute(node);
                while (a != -1) {
                    System.out.print("\n" + this.getNodeName(a) + "=\"" + this.getStringValueX(a) + "\"");
                    a = this.getNextAttribute(a);
                }
                System.out.print('>');
                int child = this.getFirstChild(node);
                while (child != -1) {
                    this.print(child, level + 1);
                    child = this.getNextSibling(child);
                }
                System.out.println("</" + name + '>');
            }
        }
    }

    @Override
    public String getNodeName(int node) {
        int nodeh = node;
        short type = this.getNodeType(nodeh);
        switch (type) {
            case 0: 
            case 3: 
            case 8: 
            case 9: {
                return EMPTYSTRING;
            }
            case 13: {
                return this.getLocalName(nodeh);
            }
        }
        return super.getNodeName(nodeh);
    }

    @Override
    public String getNamespaceName(int node) {
        if (node == -1) {
            return EMPTYSTRING;
        }
        String s = this.getNamespaceURI(node);
        return s == null ? EMPTYSTRING : s;
    }

    @Override
    public int getAttributeNode(int type, int element) {
        int attr = this.getFirstAttribute(element);
        while (attr != -1) {
            if (this.getExpandedTypeID(attr) == type) {
                return attr;
            }
            attr = this.getNextAttribute(attr);
        }
        return -1;
    }

    public String getAttributeValue(int type, int element) {
        int attr = this.getAttributeNode(type, element);
        return attr != -1 ? this.getStringValueX(attr) : EMPTYSTRING;
    }

    public String getAttributeValue(String name, int element) {
        return this.getAttributeValue(this.getGeneralizedType(name), element);
    }

    @Override
    public DTMAxisIterator getChildren(int node) {
        return new SAX2DTM2.ChildrenIterator(this).setStartNode(node);
    }

    @Override
    public DTMAxisIterator getTypedChildren(int type) {
        return new SAX2DTM2.TypedChildrenIterator(this, type);
    }

    @Override
    public DTMAxisIterator getAxisIterator(int axis) {
        switch (axis) {
            case 13: {
                return new DTMDefaultBaseIterators.SingletonIterator(this);
            }
            case 3: {
                return new SAX2DTM2.ChildrenIterator(this);
            }
            case 10: {
                return new SAX2DTM2.ParentIterator(this);
            }
            case 0: {
                return new SAX2DTM2.AncestorIterator(this);
            }
            case 1: {
                return new SAX2DTM2.AncestorIterator(this).includeSelf();
            }
            case 2: {
                return new SAX2DTM2.AttributeIterator(this);
            }
            case 4: {
                return new SAX2DTM2.DescendantIterator(this);
            }
            case 5: {
                return new SAX2DTM2.DescendantIterator(this).includeSelf();
            }
            case 6: {
                return new SAX2DTM2.FollowingIterator(this);
            }
            case 11: {
                return new SAX2DTM2.PrecedingIterator(this);
            }
            case 7: {
                return new SAX2DTM2.FollowingSiblingIterator(this);
            }
            case 12: {
                return new SAX2DTM2.PrecedingSiblingIterator(this);
            }
            case 9: {
                return new DTMDefaultBaseIterators.NamespaceIterator(this);
            }
            case 19: {
                return new DTMDefaultBaseIterators.RootIterator(this);
            }
        }
        BasisLibrary.runTimeError("AXIS_SUPPORT_ERR", Axis.getNames(axis));
        return null;
    }

    @Override
    public DTMAxisIterator getTypedAxisIterator(int axis, int type) {
        if (axis == 3) {
            return new SAX2DTM2.TypedChildrenIterator(this, type);
        }
        if (type == -1) {
            return EMPTYITERATOR;
        }
        switch (axis) {
            case 13: {
                return new SAX2DTM2.TypedSingletonIterator(this, type);
            }
            case 3: {
                return new SAX2DTM2.TypedChildrenIterator(this, type);
            }
            case 10: {
                return new SAX2DTM2.ParentIterator(this).setNodeType(type);
            }
            case 0: {
                return new SAX2DTM2.TypedAncestorIterator(this, type);
            }
            case 1: {
                return new SAX2DTM2.TypedAncestorIterator(this, type).includeSelf();
            }
            case 2: {
                return new SAX2DTM2.TypedAttributeIterator(this, type);
            }
            case 4: {
                return new SAX2DTM2.TypedDescendantIterator(this, type);
            }
            case 5: {
                return new SAX2DTM2.TypedDescendantIterator(this, type).includeSelf();
            }
            case 6: {
                return new SAX2DTM2.TypedFollowingIterator(this, type);
            }
            case 11: {
                return new SAX2DTM2.TypedPrecedingIterator(this, type);
            }
            case 7: {
                return new SAX2DTM2.TypedFollowingSiblingIterator(this, type);
            }
            case 12: {
                return new SAX2DTM2.TypedPrecedingSiblingIterator(this, type);
            }
            case 9: {
                return new TypedNamespaceIterator(type);
            }
            case 19: {
                return new SAX2DTM2.TypedRootIterator(this, type);
            }
        }
        BasisLibrary.runTimeError("TYPED_AXIS_SUPPORT_ERR", Axis.getNames(axis));
        return null;
    }

    @Override
    public DTMAxisIterator getNamespaceAxisIterator(int axis, int ns) {
        Object iterator = null;
        if (ns == -1) {
            return EMPTYITERATOR;
        }
        switch (axis) {
            case 3: {
                return new NamespaceChildrenIterator(ns);
            }
            case 2: {
                return new NamespaceAttributeIterator(ns);
            }
        }
        return new NamespaceWildcardIterator(axis, ns);
    }

    public DTMAxisIterator getTypedDescendantIterator(int type) {
        return new SAX2DTM2.TypedDescendantIterator(this, type);
    }

    @Override
    public DTMAxisIterator getNthDescendant(int type, int n, boolean includeself) {
        SAX2DTM2.TypedDescendantIterator source = new SAX2DTM2.TypedDescendantIterator(this, type);
        return new DTMDefaultBaseIterators.NthDescendantIterator(this, n);
    }

    @Override
    public void characters(int node, SerializationHandler handler) throws TransletException {
        if (node != -1) {
            try {
                this.dispatchCharactersEvents(node, (ContentHandler)handler, false);
            }
            catch (SAXException e) {
                throw new TransletException(e);
            }
        }
    }

    @Override
    public void copy(DTMAxisIterator nodes, SerializationHandler handler) throws TransletException {
        int node;
        while ((node = nodes.next()) != -1) {
            this.copy(node, handler);
        }
    }

    public void copy(SerializationHandler handler) throws TransletException {
        this.copy(this.getDocument(), handler);
    }

    @Override
    public void copy(int node, SerializationHandler handler) throws TransletException {
        this.copy(node, handler, false);
    }

    private final void copy(int node, SerializationHandler handler, boolean isChild) throws TransletException {
        int nodeID = this.makeNodeIdentity(node);
        int eType = this._exptype2(nodeID);
        int type = this._exptype2Type(eType);
        try {
            switch (type) {
                case 0: 
                case 9: {
                    int c = this._firstch2(nodeID);
                    while (c != -1) {
                        this.copy(this.makeNodeHandle(c), handler, true);
                        c = this._nextsib2(c);
                    }
                    break;
                }
                case 7: {
                    this.copyPI(node, handler);
                    break;
                }
                case 8: {
                    handler.comment(this.getStringValueX(node));
                    break;
                }
                case 3: {
                    boolean oldEscapeSetting = false;
                    boolean escapeBit = false;
                    if (this._dontEscape != null && (escapeBit = this._dontEscape.getBit(this.getNodeIdent(node)))) {
                        oldEscapeSetting = handler.setEscaping(false);
                    }
                    this.copyTextNode(nodeID, handler);
                    if (escapeBit) {
                        handler.setEscaping(oldEscapeSetting);
                    }
                    break;
                }
                case 2: {
                    this.copyAttribute(nodeID, eType, handler);
                    break;
                }
                case 13: {
                    handler.namespaceAfterStartElement(this.getNodeNameX(node), this.getNodeValue(node));
                    break;
                }
                default: {
                    if (type == 1) {
                        String name = this.copyElement(nodeID, eType, handler);
                        this.copyNS(nodeID, handler, !isChild);
                        this.copyAttributes(nodeID, handler);
                        int c = this._firstch2(nodeID);
                        while (c != -1) {
                            this.copy(this.makeNodeHandle(c), handler, true);
                            c = this._nextsib2(c);
                        }
                        handler.endElement(name);
                        break;
                    }
                    String uri = this.getNamespaceName(node);
                    if (uri.length() != 0) {
                        String prefix = this.getPrefix(node);
                        handler.namespaceAfterStartElement(prefix, uri);
                    }
                    handler.addAttribute(this.getNodeName(node), this.getNodeValue(node));
                }
            }
        }
        catch (Exception e) {
            throw new TransletException(e);
        }
    }

    private void copyPI(int node, SerializationHandler handler) throws TransletException {
        String target = this.getNodeName(node);
        String value = this.getStringValueX(node);
        try {
            handler.processingInstruction(target, value);
        }
        catch (Exception e) {
            throw new TransletException(e);
        }
    }

    @Override
    public String shallowCopy(int node, SerializationHandler handler) throws TransletException {
        int nodeID = this.makeNodeIdentity(node);
        int exptype = this._exptype2(nodeID);
        int type = this._exptype2Type(exptype);
        try {
            switch (type) {
                case 1: {
                    String name = this.copyElement(nodeID, exptype, handler);
                    this.copyNS(nodeID, handler, true);
                    return name;
                }
                case 0: 
                case 9: {
                    return EMPTYSTRING;
                }
                case 3: {
                    this.copyTextNode(nodeID, handler);
                    return null;
                }
                case 7: {
                    this.copyPI(node, handler);
                    return null;
                }
                case 8: {
                    handler.comment(this.getStringValueX(node));
                    return null;
                }
                case 13: {
                    handler.namespaceAfterStartElement(this.getNodeNameX(node), this.getNodeValue(node));
                    return null;
                }
                case 2: {
                    this.copyAttribute(nodeID, exptype, handler);
                    return null;
                }
            }
            String uri1 = this.getNamespaceName(node);
            if (uri1.length() != 0) {
                String prefix = this.getPrefix(node);
                handler.namespaceAfterStartElement(prefix, uri1);
            }
            handler.addAttribute(this.getNodeName(node), this.getNodeValue(node));
            return null;
        }
        catch (Exception e) {
            throw new TransletException(e);
        }
    }

    @Override
    public String getLanguage(int node) {
        int parent = node;
        while (-1 != parent) {
            int langAttr;
            if (1 == this.getNodeType(parent) && -1 != (langAttr = this.getAttributeNode(parent, XML_URI, "lang"))) {
                return this.getNodeValue(langAttr);
            }
            parent = this.getParent(parent);
        }
        return null;
    }

    public DOMBuilder getBuilder() {
        return this;
    }

    @Override
    public SerializationHandler getOutputDomBuilder() {
        return new ToXMLSAXHandler((ContentHandler)this, "UTF-8");
    }

    @Override
    public DOM getResultTreeFrag(int initSize, int rtfType) {
        return this.getResultTreeFrag(initSize, rtfType, true);
    }

    @Override
    public DOM getResultTreeFrag(int initSize, int rtfType, boolean addToManager) {
        if (rtfType == 0) {
            if (addToManager) {
                int dtmPos = this._dtmManager.getFirstFreeDTMID();
                SimpleResultTreeImpl rtf = new SimpleResultTreeImpl(this._dtmManager, dtmPos << 16);
                this._dtmManager.addDTM(rtf, dtmPos, 0);
                return rtf;
            }
            return new SimpleResultTreeImpl(this._dtmManager, 0);
        }
        if (rtfType == 1) {
            if (addToManager) {
                int dtmPos = this._dtmManager.getFirstFreeDTMID();
                AdaptiveResultTreeImpl rtf = new AdaptiveResultTreeImpl(this._dtmManager, dtmPos << 16, this.m_wsfilter, initSize, this.m_buildIdIndex);
                this._dtmManager.addDTM(rtf, dtmPos, 0);
                return rtf;
            }
            return new AdaptiveResultTreeImpl(this._dtmManager, 0, this.m_wsfilter, initSize, this.m_buildIdIndex);
        }
        return (DOM)((Object)this._dtmManager.getDTM(null, true, this.m_wsfilter, true, false, false, initSize, this.m_buildIdIndex));
    }

    @Override
    public Hashtable getElementsWithIDs() {
        if (this.m_idAttributes == null) {
            return null;
        }
        Iterator idEntries = this.m_idAttributes.entrySet().iterator();
        if (!idEntries.hasNext()) {
            return null;
        }
        Hashtable idAttrsTable = new Hashtable();
        while (idEntries.hasNext()) {
            Map.Entry entry = idEntries.next();
            idAttrsTable.put(entry.getKey(), entry.getValue());
        }
        return idAttrsTable;
    }

    @Override
    public String getUnparsedEntityURI(String name) {
        if (this._document != null) {
            String uri = EMPTYSTRING;
            DocumentType doctype = this._document.getDoctype();
            if (doctype != null) {
                NamedNodeMap entities = doctype.getEntities();
                if (entities == null) {
                    return uri;
                }
                Entity entity = (Entity)entities.getNamedItem(name);
                if (entity == null) {
                    return uri;
                }
                String notationName = entity.getNotationName();
                if (notationName != null && (uri = entity.getSystemId()) == null) {
                    uri = entity.getPublicId();
                }
            }
            return uri;
        }
        return super.getUnparsedEntityURI(name);
    }

    public final class NamespaceAttributeIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase {
        private final int _nsType;

        public NamespaceAttributeIterator(int nsType) {
            super(SAXImpl.this);
            this._nsType = nsType;
        }

        @Override
        public DTMAxisIterator setStartNode(int node) {
            if (node == 0) {
                node = SAXImpl.this.getDocument();
            }
            if (this._isRestartable) {
                int nsType = this._nsType;
                this._startNode = node;
                node = SAXImpl.this.getFirstAttribute(node);
                while (node != -1 && SAXImpl.this.getNSType(node) != nsType) {
                    node = SAXImpl.this.getNextAttribute(node);
                }
                this._currentNode = node;
                return this.resetPosition();
            }
            return this;
        }

        @Override
        public int next() {
            int node = this._currentNode;
            int nsType = this._nsType;
            if (node == -1) {
                return -1;
            }
            int nextNode = SAXImpl.this.getNextAttribute(node);
            while (nextNode != -1 && SAXImpl.this.getNSType(nextNode) != nsType) {
                nextNode = SAXImpl.this.getNextAttribute(nextNode);
            }
            this._currentNode = nextNode;
            return this.returnNode(node);
        }
    }

    public final class NamespaceChildrenIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase {
        private final int _nsType;

        public NamespaceChildrenIterator(int type) {
            super(SAXImpl.this);
            this._nsType = type;
        }

        @Override
        public DTMAxisIterator setStartNode(int node) {
            if (node == 0) {
                node = SAXImpl.this.getDocument();
            }
            if (this._isRestartable) {
                this._startNode = node;
                this._currentNode = node == -1 ? -1 : -2;
                return this.resetPosition();
            }
            return this;
        }

        @Override
        public int next() {
            if (this._currentNode != -1) {
                int node;
                int n = node = -2 == this._currentNode ? SAXImpl.this._firstch(SAXImpl.this.makeNodeIdentity(this._startNode)) : SAXImpl.this._nextsib(this._currentNode);
                while (node != -1) {
                    int nodeHandle = SAXImpl.this.makeNodeHandle(node);
                    if (SAXImpl.this.getNSType(nodeHandle) == this._nsType) {
                        this._currentNode = node;
                        return this.returnNode(nodeHandle);
                    }
                    node = SAXImpl.this._nextsib(node);
                }
            }
            return -1;
        }
    }

    public final class NamespaceWildcardIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase {
        protected int m_nsType;
        protected DTMAxisIterator m_baseIterator;

        public NamespaceWildcardIterator(int axis, int nsType) {
            super(SAXImpl.this);
            this.m_nsType = nsType;
            switch (axis) {
                case 2: {
                    this.m_baseIterator = SAXImpl.this.getAxisIterator(axis);
                }
                case 9: {
                    this.m_baseIterator = SAXImpl.this.getAxisIterator(axis);
                }
            }
            this.m_baseIterator = SAXImpl.this.getTypedAxisIterator(axis, 1);
        }

        @Override
        public DTMAxisIterator setStartNode(int node) {
            if (this._isRestartable) {
                this._startNode = node;
                this.m_baseIterator.setStartNode(node);
                this.resetPosition();
            }
            return this;
        }

        @Override
        public int next() {
            int node;
            while ((node = this.m_baseIterator.next()) != -1) {
                if (SAXImpl.this.getNSType(node) != this.m_nsType) continue;
                return this.returnNode(node);
            }
            return -1;
        }

        @Override
        public DTMAxisIterator cloneIterator() {
            try {
                DTMAxisIterator nestedClone = this.m_baseIterator.cloneIterator();
                NamespaceWildcardIterator clone = (NamespaceWildcardIterator)super.clone();
                clone.m_baseIterator = nestedClone;
                clone.m_nsType = this.m_nsType;
                clone._isRestartable = false;
                return clone;
            }
            catch (CloneNotSupportedException e) {
                BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", e.toString());
                return null;
            }
        }

        @Override
        public boolean isReverse() {
            return this.m_baseIterator.isReverse();
        }

        @Override
        public void setMark() {
            this.m_baseIterator.setMark();
        }

        @Override
        public void gotoMark() {
            this.m_baseIterator.gotoMark();
        }
    }

    private final class NodeValueIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase {
        private DTMAxisIterator _source;
        private String _value;
        private boolean _op;
        private final boolean _isReverse;
        private int _returnType;

        public NodeValueIterator(DTMAxisIterator source, int returnType, String value, boolean op) {
            super(SAXImpl.this);
            this._returnType = 1;
            this._source = source;
            this._returnType = returnType;
            this._value = value;
            this._op = op;
            this._isReverse = source.isReverse();
        }

        @Override
        public boolean isReverse() {
            return this._isReverse;
        }

        @Override
        public DTMAxisIterator cloneIterator() {
            try {
                NodeValueIterator clone = (NodeValueIterator)super.clone();
                clone._isRestartable = false;
                clone._source = this._source.cloneIterator();
                clone._value = this._value;
                clone._op = this._op;
                return clone.reset();
            }
            catch (CloneNotSupportedException e) {
                BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", e.toString());
                return null;
            }
        }

        @Override
        public void setRestartable(boolean isRestartable) {
            this._isRestartable = isRestartable;
            this._source.setRestartable(isRestartable);
        }

        @Override
        public DTMAxisIterator reset() {
            this._source.reset();
            return this.resetPosition();
        }

        @Override
        public int next() {
            int node;
            while ((node = this._source.next()) != -1) {
                String val = SAXImpl.this.getStringValueX(node);
                if (this._value.equals(val) != this._op) continue;
                if (this._returnType == 0) {
                    return this.returnNode(node);
                }
                return this.returnNode(SAXImpl.this.getParent(node));
            }
            return -1;
        }

        @Override
        public DTMAxisIterator setStartNode(int node) {
            if (this._isRestartable) {
                this._startNode = node;
                this._source.setStartNode(this._startNode);
                return this.resetPosition();
            }
            return this;
        }

        @Override
        public void setMark() {
            this._source.setMark();
        }

        @Override
        public void gotoMark() {
            this._source.gotoMark();
        }
    }

    public class TypedNamespaceIterator
    extends DTMDefaultBaseIterators.NamespaceIterator {
        private String _nsPrefix;

        public TypedNamespaceIterator(int nodeType) {
            super(SAXImpl.this);
            if (SAXImpl.this.m_expandedNameTable != null) {
                this._nsPrefix = SAXImpl.this.m_expandedNameTable.getLocalName(nodeType);
            }
        }

        @Override
        public int next() {
            if (this._nsPrefix == null || this._nsPrefix.length() == 0) {
                return -1;
            }
            int node = -1;
            node = super.next();
            while (node != -1) {
                if (this._nsPrefix.compareTo(SAXImpl.this.getLocalName(node)) == 0) {
                    return this.returnNode(node);
                }
                node = super.next();
            }
            return -1;
        }
    }
}

