/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serializer.EmptySerializer
 *  org.apache.xml.serializer.SerializationHandler
 */
package org.apache.xalan.xsltc.dom;

import javax.xml.transform.SourceLocator;
import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.StripFilter;
import org.apache.xalan.xsltc.TransletException;
import org.apache.xalan.xsltc.dom.BitArray;
import org.apache.xalan.xsltc.dom.XSLTCDTMManager;
import org.apache.xalan.xsltc.runtime.Hashtable;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.dtm.DTMAxisTraverser;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.ref.DTMAxisIteratorBase;
import org.apache.xml.dtm.ref.DTMManagerDefault;
import org.apache.xml.serializer.EmptySerializer;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xml.utils.XMLString;
import org.apache.xml.utils.XMLStringDefault;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

public class SimpleResultTreeImpl
extends EmptySerializer
implements DOM,
DTM {
    private static final DTMAxisIterator EMPTY_ITERATOR = new DTMAxisIteratorBase(){

        @Override
        public DTMAxisIterator reset() {
            return this;
        }

        @Override
        public DTMAxisIterator setStartNode(int node) {
            return this;
        }

        @Override
        public int next() {
            return -1;
        }

        @Override
        public void setMark() {
        }

        @Override
        public void gotoMark() {
        }

        @Override
        public int getLast() {
            return 0;
        }

        @Override
        public int getPosition() {
            return 0;
        }

        @Override
        public DTMAxisIterator cloneIterator() {
            return this;
        }

        @Override
        public void setRestartable(boolean isRestartable) {
        }
    };
    public static final int RTF_ROOT = 0;
    public static final int RTF_TEXT = 1;
    public static final int NUMBER_OF_NODES = 2;
    private static int _documentURIIndex = 0;
    private static final String EMPTY_STR = "";
    private String _text;
    protected String[] _textArray;
    protected XSLTCDTMManager _dtmManager;
    protected int _size = 0;
    private int _documentID;
    private BitArray _dontEscape = null;
    private boolean _escaping = true;

    public SimpleResultTreeImpl(XSLTCDTMManager dtmManager, int documentID) {
        this._dtmManager = dtmManager;
        this._documentID = documentID;
        this._textArray = new String[4];
    }

    public DTMManagerDefault getDTMManager() {
        return this._dtmManager;
    }

    @Override
    public int getDocument() {
        return this._documentID;
    }

    @Override
    public String getStringValue() {
        return this._text;
    }

    @Override
    public DTMAxisIterator getIterator() {
        return new SingletonIterator(this.getDocument());
    }

    @Override
    public DTMAxisIterator getChildren(int node) {
        return new SimpleIterator().setStartNode(node);
    }

    @Override
    public DTMAxisIterator getTypedChildren(int type) {
        return new SimpleIterator(1, type);
    }

    @Override
    public DTMAxisIterator getAxisIterator(int axis) {
        switch (axis) {
            case 3: 
            case 4: {
                return new SimpleIterator(1);
            }
            case 0: 
            case 10: {
                return new SimpleIterator(0);
            }
            case 1: {
                return new SimpleIterator(0).includeSelf();
            }
            case 5: {
                return new SimpleIterator(1).includeSelf();
            }
            case 13: {
                return new SingletonIterator();
            }
        }
        return EMPTY_ITERATOR;
    }

    @Override
    public DTMAxisIterator getTypedAxisIterator(int axis, int type) {
        switch (axis) {
            case 3: 
            case 4: {
                return new SimpleIterator(1, type);
            }
            case 0: 
            case 10: {
                return new SimpleIterator(0, type);
            }
            case 1: {
                return new SimpleIterator(0, type).includeSelf();
            }
            case 5: {
                return new SimpleIterator(1, type).includeSelf();
            }
            case 13: {
                return new SingletonIterator(type);
            }
        }
        return EMPTY_ITERATOR;
    }

    @Override
    public DTMAxisIterator getNthDescendant(int node, int n, boolean includeself) {
        return null;
    }

    @Override
    public DTMAxisIterator getNamespaceAxisIterator(int axis, int ns) {
        return null;
    }

    @Override
    public DTMAxisIterator getNodeValueIterator(DTMAxisIterator iter, int returnType, String value, boolean op) {
        return null;
    }

    @Override
    public DTMAxisIterator orderNodes(DTMAxisIterator source, int node) {
        return source;
    }

    @Override
    public String getNodeName(int node) {
        if (this.getNodeIdent(node) == 1) {
            return "#text";
        }
        return EMPTY_STR;
    }

    @Override
    public String getNodeNameX(int node) {
        return EMPTY_STR;
    }

    @Override
    public String getNamespaceName(int node) {
        return EMPTY_STR;
    }

    @Override
    public int getExpandedTypeID(int nodeHandle) {
        int nodeID = this.getNodeIdent(nodeHandle);
        if (nodeID == 1) {
            return 3;
        }
        if (nodeID == 0) {
            return 0;
        }
        return -1;
    }

    @Override
    public int getNamespaceType(int node) {
        return 0;
    }

    @Override
    public int getParent(int nodeHandle) {
        int nodeID = this.getNodeIdent(nodeHandle);
        return nodeID == 1 ? this.getNodeHandle(0) : -1;
    }

    @Override
    public int getAttributeNode(int gType, int element) {
        return -1;
    }

    @Override
    public String getStringValueX(int nodeHandle) {
        int nodeID = this.getNodeIdent(nodeHandle);
        if (nodeID == 0 || nodeID == 1) {
            return this._text;
        }
        return EMPTY_STR;
    }

    @Override
    public void copy(int node, SerializationHandler handler) throws TransletException {
        this.characters(node, handler);
    }

    @Override
    public void copy(DTMAxisIterator nodes, SerializationHandler handler) throws TransletException {
        int node;
        while ((node = nodes.next()) != -1) {
            this.copy(node, handler);
        }
    }

    @Override
    public String shallowCopy(int node, SerializationHandler handler) throws TransletException {
        this.characters(node, handler);
        return null;
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
    public void characters(int node, SerializationHandler handler) throws TransletException {
        int nodeID = this.getNodeIdent(node);
        if (nodeID == 0 || nodeID == 1) {
            boolean escapeBit = false;
            boolean oldEscapeSetting = false;
            try {
                for (int i = 0; i < this._size; ++i) {
                    if (this._dontEscape != null && (escapeBit = this._dontEscape.getBit(i))) {
                        oldEscapeSetting = handler.setEscaping(false);
                    }
                    handler.characters(this._textArray[i]);
                    if (!escapeBit) continue;
                    handler.setEscaping(oldEscapeSetting);
                }
            }
            catch (SAXException e) {
                throw new TransletException(e);
            }
        }
    }

    @Override
    public Node makeNode(int index) {
        return null;
    }

    @Override
    public Node makeNode(DTMAxisIterator iter) {
        return null;
    }

    @Override
    public NodeList makeNodeList(int index) {
        return null;
    }

    @Override
    public NodeList makeNodeList(DTMAxisIterator iter) {
        return null;
    }

    @Override
    public String getLanguage(int node) {
        return null;
    }

    @Override
    public int getSize() {
        return 2;
    }

    @Override
    public String getDocumentURI(int node) {
        return "simple_rtf" + _documentURIIndex++;
    }

    @Override
    public void setFilter(StripFilter filter) {
    }

    @Override
    public void setupMapping(String[] names, String[] uris, int[] types, String[] namespaces) {
    }

    @Override
    public boolean isElement(int node) {
        return false;
    }

    @Override
    public boolean isAttribute(int node) {
        return false;
    }

    @Override
    public String lookupNamespace(int node, String prefix) throws TransletException {
        return null;
    }

    @Override
    public int getNodeIdent(int nodehandle) {
        return nodehandle != -1 ? nodehandle - this._documentID : -1;
    }

    @Override
    public int getNodeHandle(int nodeId) {
        return nodeId != -1 ? nodeId + this._documentID : -1;
    }

    @Override
    public DOM getResultTreeFrag(int initialSize, int rtfType) {
        return null;
    }

    @Override
    public DOM getResultTreeFrag(int initialSize, int rtfType, boolean addToManager) {
        return null;
    }

    @Override
    public SerializationHandler getOutputDomBuilder() {
        return this;
    }

    @Override
    public int getNSType(int node) {
        return 0;
    }

    @Override
    public String getUnparsedEntityURI(String name) {
        return null;
    }

    @Override
    public Hashtable getElementsWithIDs() {
        return null;
    }

    public void startDocument() throws SAXException {
    }

    public void endDocument() throws SAXException {
        if (this._size == 1) {
            this._text = this._textArray[0];
        } else {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < this._size; ++i) {
                buffer.append(this._textArray[i]);
            }
            this._text = buffer.toString();
        }
    }

    public void characters(String str) throws SAXException {
        if (this._size >= this._textArray.length) {
            String[] newTextArray = new String[this._textArray.length * 2];
            System.arraycopy(this._textArray, 0, newTextArray, 0, this._textArray.length);
            this._textArray = newTextArray;
        }
        if (!this._escaping) {
            if (this._dontEscape == null) {
                this._dontEscape = new BitArray(8);
            }
            if (this._size >= this._dontEscape.size()) {
                this._dontEscape.resize(this._dontEscape.size() * 2);
            }
            this._dontEscape.setBit(this._size);
        }
        this._textArray[this._size++] = str;
    }

    public void characters(char[] ch, int offset, int length) throws SAXException {
        if (this._size >= this._textArray.length) {
            String[] newTextArray = new String[this._textArray.length * 2];
            System.arraycopy(this._textArray, 0, newTextArray, 0, this._textArray.length);
            this._textArray = newTextArray;
        }
        if (!this._escaping) {
            if (this._dontEscape == null) {
                this._dontEscape = new BitArray(8);
            }
            if (this._size >= this._dontEscape.size()) {
                this._dontEscape.resize(this._dontEscape.size() * 2);
            }
            this._dontEscape.setBit(this._size);
        }
        this._textArray[this._size++] = new String(ch, offset, length);
    }

    public boolean setEscaping(boolean escape) throws SAXException {
        boolean temp = this._escaping;
        this._escaping = escape;
        return temp;
    }

    @Override
    public void setFeature(String featureId, boolean state) {
    }

    @Override
    public void setProperty(String property, Object value) {
    }

    @Override
    public DTMAxisTraverser getAxisTraverser(int axis) {
        return null;
    }

    @Override
    public boolean hasChildNodes(int nodeHandle) {
        return this.getNodeIdent(nodeHandle) == 0;
    }

    @Override
    public int getFirstChild(int nodeHandle) {
        int nodeID = this.getNodeIdent(nodeHandle);
        if (nodeID == 0) {
            return this.getNodeHandle(1);
        }
        return -1;
    }

    @Override
    public int getLastChild(int nodeHandle) {
        return this.getFirstChild(nodeHandle);
    }

    @Override
    public int getAttributeNode(int elementHandle, String namespaceURI, String name) {
        return -1;
    }

    @Override
    public int getFirstAttribute(int nodeHandle) {
        return -1;
    }

    @Override
    public int getFirstNamespaceNode(int nodeHandle, boolean inScope) {
        return -1;
    }

    @Override
    public int getNextSibling(int nodeHandle) {
        return -1;
    }

    @Override
    public int getPreviousSibling(int nodeHandle) {
        return -1;
    }

    @Override
    public int getNextAttribute(int nodeHandle) {
        return -1;
    }

    @Override
    public int getNextNamespaceNode(int baseHandle, int namespaceHandle, boolean inScope) {
        return -1;
    }

    @Override
    public int getOwnerDocument(int nodeHandle) {
        return this.getDocument();
    }

    @Override
    public int getDocumentRoot(int nodeHandle) {
        return this.getDocument();
    }

    @Override
    public XMLString getStringValue(int nodeHandle) {
        return new XMLStringDefault(this.getStringValueX(nodeHandle));
    }

    @Override
    public int getStringValueChunkCount(int nodeHandle) {
        return 0;
    }

    @Override
    public char[] getStringValueChunk(int nodeHandle, int chunkIndex, int[] startAndLen) {
        return null;
    }

    @Override
    public int getExpandedTypeID(String namespace, String localName, int type) {
        return -1;
    }

    @Override
    public String getLocalNameFromExpandedNameID(int ExpandedNameID) {
        return EMPTY_STR;
    }

    @Override
    public String getNamespaceFromExpandedNameID(int ExpandedNameID) {
        return EMPTY_STR;
    }

    @Override
    public String getLocalName(int nodeHandle) {
        return EMPTY_STR;
    }

    @Override
    public String getPrefix(int nodeHandle) {
        return null;
    }

    @Override
    public String getNamespaceURI(int nodeHandle) {
        return EMPTY_STR;
    }

    @Override
    public String getNodeValue(int nodeHandle) {
        return this.getNodeIdent(nodeHandle) == 1 ? this._text : null;
    }

    @Override
    public short getNodeType(int nodeHandle) {
        int nodeID = this.getNodeIdent(nodeHandle);
        if (nodeID == 1) {
            return 3;
        }
        if (nodeID == 0) {
            return 0;
        }
        return -1;
    }

    @Override
    public short getLevel(int nodeHandle) {
        int nodeID = this.getNodeIdent(nodeHandle);
        if (nodeID == 1) {
            return 2;
        }
        if (nodeID == 0) {
            return 1;
        }
        return -1;
    }

    @Override
    public boolean isSupported(String feature, String version) {
        return false;
    }

    @Override
    public String getDocumentBaseURI() {
        return EMPTY_STR;
    }

    @Override
    public void setDocumentBaseURI(String baseURI) {
    }

    @Override
    public String getDocumentSystemIdentifier(int nodeHandle) {
        return null;
    }

    @Override
    public String getDocumentEncoding(int nodeHandle) {
        return null;
    }

    @Override
    public String getDocumentStandalone(int nodeHandle) {
        return null;
    }

    @Override
    public String getDocumentVersion(int documentHandle) {
        return null;
    }

    @Override
    public boolean getDocumentAllDeclarationsProcessed() {
        return false;
    }

    @Override
    public String getDocumentTypeDeclarationSystemIdentifier() {
        return null;
    }

    @Override
    public String getDocumentTypeDeclarationPublicIdentifier() {
        return null;
    }

    @Override
    public int getElementById(String elementId) {
        return -1;
    }

    @Override
    public boolean supportsPreStripping() {
        return false;
    }

    @Override
    public boolean isNodeAfter(int firstNodeHandle, int secondNodeHandle) {
        return this.lessThan(firstNodeHandle, secondNodeHandle);
    }

    @Override
    public boolean isCharacterElementContentWhitespace(int nodeHandle) {
        return false;
    }

    @Override
    public boolean isDocumentAllDeclarationsProcessed(int documentHandle) {
        return false;
    }

    @Override
    public boolean isAttributeSpecified(int attributeHandle) {
        return false;
    }

    @Override
    public void dispatchCharactersEvents(int nodeHandle, ContentHandler ch, boolean normalize) throws SAXException {
    }

    @Override
    public void dispatchToEvents(int nodeHandle, ContentHandler ch) throws SAXException {
    }

    @Override
    public Node getNode(int nodeHandle) {
        return this.makeNode(nodeHandle);
    }

    @Override
    public boolean needsTwoThreads() {
        return false;
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
    public void appendChild(int newChild, boolean clone, boolean cloneDepth) {
    }

    @Override
    public void appendTextChild(String str) {
    }

    @Override
    public SourceLocator getSourceLocatorFor(int node) {
        return null;
    }

    @Override
    public void documentRegistration() {
    }

    @Override
    public void documentRelease() {
    }

    @Override
    public void migrateTo(DTMManager manager) {
    }

    public final class SingletonIterator
    extends DTMAxisIteratorBase {
        static final int NO_TYPE = -1;
        int _type = -1;
        int _currentNode;

        public SingletonIterator() {
        }

        public SingletonIterator(int type) {
            this._type = type;
        }

        @Override
        public void setMark() {
            this._markedNode = this._currentNode;
        }

        @Override
        public void gotoMark() {
            this._currentNode = this._markedNode;
        }

        @Override
        public DTMAxisIterator setStartNode(int nodeHandle) {
            this._currentNode = this._startNode = SimpleResultTreeImpl.this.getNodeIdent(nodeHandle);
            return this;
        }

        @Override
        public int next() {
            if (this._currentNode == -1) {
                return -1;
            }
            this._currentNode = -1;
            if (this._type != -1) {
                if (this._currentNode == 0 && this._type == 0 || this._currentNode == 1 && this._type == 3) {
                    return SimpleResultTreeImpl.this.getNodeHandle(this._currentNode);
                }
            } else {
                return SimpleResultTreeImpl.this.getNodeHandle(this._currentNode);
            }
            return -1;
        }
    }

    public final class SimpleIterator
    extends DTMAxisIteratorBase {
        static final int DIRECTION_UP = 0;
        static final int DIRECTION_DOWN = 1;
        static final int NO_TYPE = -1;
        int _direction = 1;
        int _type = -1;
        int _currentNode;

        public SimpleIterator() {
        }

        public SimpleIterator(int direction) {
            this._direction = direction;
        }

        public SimpleIterator(int direction, int type) {
            this._direction = direction;
            this._type = type;
        }

        @Override
        public int next() {
            if (this._direction == 1) {
                while (this._currentNode < 2) {
                    if (this._type != -1) {
                        if (this._currentNode == 0 && this._type == 0 || this._currentNode == 1 && this._type == 3) {
                            return this.returnNode(SimpleResultTreeImpl.this.getNodeHandle(this._currentNode++));
                        }
                        ++this._currentNode;
                        continue;
                    }
                    return this.returnNode(SimpleResultTreeImpl.this.getNodeHandle(this._currentNode++));
                }
                return -1;
            }
            while (this._currentNode >= 0) {
                if (this._type != -1) {
                    if (this._currentNode == 0 && this._type == 0 || this._currentNode == 1 && this._type == 3) {
                        return this.returnNode(SimpleResultTreeImpl.this.getNodeHandle(this._currentNode--));
                    }
                    --this._currentNode;
                    continue;
                }
                return this.returnNode(SimpleResultTreeImpl.this.getNodeHandle(this._currentNode--));
            }
            return -1;
        }

        @Override
        public DTMAxisIterator setStartNode(int nodeHandle) {
            int nodeID;
            this._startNode = nodeID = SimpleResultTreeImpl.this.getNodeIdent(nodeHandle);
            if (!this._includeSelf && nodeID != -1) {
                if (this._direction == 1) {
                    ++nodeID;
                } else if (this._direction == 0) {
                    --nodeID;
                }
            }
            this._currentNode = nodeID;
            return this;
        }

        @Override
        public void setMark() {
            this._markedNode = this._currentNode;
        }

        @Override
        public void gotoMark() {
            this._currentNode = this._markedNode;
        }
    }
}

