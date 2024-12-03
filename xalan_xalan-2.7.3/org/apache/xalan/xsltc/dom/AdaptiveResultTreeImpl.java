/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serializer.SerializationHandler
 */
package org.apache.xalan.xsltc.dom;

import javax.xml.transform.SourceLocator;
import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.StripFilter;
import org.apache.xalan.xsltc.TransletException;
import org.apache.xalan.xsltc.dom.SAXImpl;
import org.apache.xalan.xsltc.dom.SimpleResultTreeImpl;
import org.apache.xalan.xsltc.dom.XSLTCDTMManager;
import org.apache.xalan.xsltc.runtime.AttributeList;
import org.apache.xalan.xsltc.runtime.BasisLibrary;
import org.apache.xalan.xsltc.runtime.Hashtable;
import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.dtm.DTMAxisTraverser;
import org.apache.xml.dtm.DTMWSFilter;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xml.utils.XMLString;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

public class AdaptiveResultTreeImpl
extends SimpleResultTreeImpl {
    private static int _documentURIIndex = 0;
    private SAXImpl _dom;
    private DTMWSFilter _wsfilter;
    private int _initSize;
    private boolean _buildIdIndex;
    private final AttributeList _attributes = new AttributeList();
    private String _openElementName;

    public AdaptiveResultTreeImpl(XSLTCDTMManager dtmManager, int documentID, DTMWSFilter wsfilter, int initSize, boolean buildIdIndex) {
        super(dtmManager, documentID);
        this._wsfilter = wsfilter;
        this._initSize = initSize;
        this._buildIdIndex = buildIdIndex;
    }

    public DOM getNestedDOM() {
        return this._dom;
    }

    @Override
    public int getDocument() {
        if (this._dom != null) {
            return this._dom.getDocument();
        }
        return super.getDocument();
    }

    @Override
    public String getStringValue() {
        if (this._dom != null) {
            return this._dom.getStringValue();
        }
        return super.getStringValue();
    }

    @Override
    public DTMAxisIterator getIterator() {
        if (this._dom != null) {
            return this._dom.getIterator();
        }
        return super.getIterator();
    }

    @Override
    public DTMAxisIterator getChildren(int node) {
        if (this._dom != null) {
            return this._dom.getChildren(node);
        }
        return super.getChildren(node);
    }

    @Override
    public DTMAxisIterator getTypedChildren(int type) {
        if (this._dom != null) {
            return this._dom.getTypedChildren(type);
        }
        return super.getTypedChildren(type);
    }

    @Override
    public DTMAxisIterator getAxisIterator(int axis) {
        if (this._dom != null) {
            return this._dom.getAxisIterator(axis);
        }
        return super.getAxisIterator(axis);
    }

    @Override
    public DTMAxisIterator getTypedAxisIterator(int axis, int type) {
        if (this._dom != null) {
            return this._dom.getTypedAxisIterator(axis, type);
        }
        return super.getTypedAxisIterator(axis, type);
    }

    @Override
    public DTMAxisIterator getNthDescendant(int node, int n, boolean includeself) {
        if (this._dom != null) {
            return this._dom.getNthDescendant(node, n, includeself);
        }
        return super.getNthDescendant(node, n, includeself);
    }

    @Override
    public DTMAxisIterator getNamespaceAxisIterator(int axis, int ns) {
        if (this._dom != null) {
            return this._dom.getNamespaceAxisIterator(axis, ns);
        }
        return super.getNamespaceAxisIterator(axis, ns);
    }

    @Override
    public DTMAxisIterator getNodeValueIterator(DTMAxisIterator iter, int returnType, String value, boolean op) {
        if (this._dom != null) {
            return this._dom.getNodeValueIterator(iter, returnType, value, op);
        }
        return super.getNodeValueIterator(iter, returnType, value, op);
    }

    @Override
    public DTMAxisIterator orderNodes(DTMAxisIterator source, int node) {
        if (this._dom != null) {
            return this._dom.orderNodes(source, node);
        }
        return super.orderNodes(source, node);
    }

    @Override
    public String getNodeName(int node) {
        if (this._dom != null) {
            return this._dom.getNodeName(node);
        }
        return super.getNodeName(node);
    }

    @Override
    public String getNodeNameX(int node) {
        if (this._dom != null) {
            return this._dom.getNodeNameX(node);
        }
        return super.getNodeNameX(node);
    }

    @Override
    public String getNamespaceName(int node) {
        if (this._dom != null) {
            return this._dom.getNamespaceName(node);
        }
        return super.getNamespaceName(node);
    }

    @Override
    public int getExpandedTypeID(int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getExpandedTypeID(nodeHandle);
        }
        return super.getExpandedTypeID(nodeHandle);
    }

    @Override
    public int getNamespaceType(int node) {
        if (this._dom != null) {
            return this._dom.getNamespaceType(node);
        }
        return super.getNamespaceType(node);
    }

    @Override
    public int getParent(int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getParent(nodeHandle);
        }
        return super.getParent(nodeHandle);
    }

    @Override
    public int getAttributeNode(int gType, int element) {
        if (this._dom != null) {
            return this._dom.getAttributeNode(gType, element);
        }
        return super.getAttributeNode(gType, element);
    }

    @Override
    public String getStringValueX(int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getStringValueX(nodeHandle);
        }
        return super.getStringValueX(nodeHandle);
    }

    @Override
    public void copy(int node, SerializationHandler handler) throws TransletException {
        if (this._dom != null) {
            this._dom.copy(node, handler);
        } else {
            super.copy(node, handler);
        }
    }

    @Override
    public void copy(DTMAxisIterator nodes, SerializationHandler handler) throws TransletException {
        if (this._dom != null) {
            this._dom.copy(nodes, handler);
        } else {
            super.copy(nodes, handler);
        }
    }

    @Override
    public String shallowCopy(int node, SerializationHandler handler) throws TransletException {
        if (this._dom != null) {
            return this._dom.shallowCopy(node, handler);
        }
        return super.shallowCopy(node, handler);
    }

    @Override
    public boolean lessThan(int node1, int node2) {
        if (this._dom != null) {
            return this._dom.lessThan(node1, node2);
        }
        return super.lessThan(node1, node2);
    }

    @Override
    public void characters(int node, SerializationHandler handler) throws TransletException {
        if (this._dom != null) {
            this._dom.characters(node, handler);
        } else {
            super.characters(node, handler);
        }
    }

    @Override
    public Node makeNode(int index) {
        if (this._dom != null) {
            return this._dom.makeNode(index);
        }
        return super.makeNode(index);
    }

    @Override
    public Node makeNode(DTMAxisIterator iter) {
        if (this._dom != null) {
            return this._dom.makeNode(iter);
        }
        return super.makeNode(iter);
    }

    @Override
    public NodeList makeNodeList(int index) {
        if (this._dom != null) {
            return this._dom.makeNodeList(index);
        }
        return super.makeNodeList(index);
    }

    @Override
    public NodeList makeNodeList(DTMAxisIterator iter) {
        if (this._dom != null) {
            return this._dom.makeNodeList(iter);
        }
        return super.makeNodeList(iter);
    }

    @Override
    public String getLanguage(int node) {
        if (this._dom != null) {
            return this._dom.getLanguage(node);
        }
        return super.getLanguage(node);
    }

    @Override
    public int getSize() {
        if (this._dom != null) {
            return this._dom.getSize();
        }
        return super.getSize();
    }

    @Override
    public String getDocumentURI(int node) {
        if (this._dom != null) {
            return this._dom.getDocumentURI(node);
        }
        return "adaptive_rtf" + _documentURIIndex++;
    }

    @Override
    public void setFilter(StripFilter filter) {
        if (this._dom != null) {
            this._dom.setFilter(filter);
        } else {
            super.setFilter(filter);
        }
    }

    @Override
    public void setupMapping(String[] names, String[] uris, int[] types, String[] namespaces) {
        if (this._dom != null) {
            this._dom.setupMapping(names, uris, types, namespaces);
        } else {
            super.setupMapping(names, uris, types, namespaces);
        }
    }

    @Override
    public boolean isElement(int node) {
        if (this._dom != null) {
            return this._dom.isElement(node);
        }
        return super.isElement(node);
    }

    @Override
    public boolean isAttribute(int node) {
        if (this._dom != null) {
            return this._dom.isAttribute(node);
        }
        return super.isAttribute(node);
    }

    @Override
    public String lookupNamespace(int node, String prefix) throws TransletException {
        if (this._dom != null) {
            return this._dom.lookupNamespace(node, prefix);
        }
        return super.lookupNamespace(node, prefix);
    }

    @Override
    public final int getNodeIdent(int nodehandle) {
        if (this._dom != null) {
            return this._dom.getNodeIdent(nodehandle);
        }
        return super.getNodeIdent(nodehandle);
    }

    @Override
    public final int getNodeHandle(int nodeId) {
        if (this._dom != null) {
            return this._dom.getNodeHandle(nodeId);
        }
        return super.getNodeHandle(nodeId);
    }

    @Override
    public DOM getResultTreeFrag(int initialSize, int rtfType) {
        if (this._dom != null) {
            return this._dom.getResultTreeFrag(initialSize, rtfType);
        }
        return super.getResultTreeFrag(initialSize, rtfType);
    }

    @Override
    public SerializationHandler getOutputDomBuilder() {
        return this;
    }

    @Override
    public int getNSType(int node) {
        if (this._dom != null) {
            return this._dom.getNSType(node);
        }
        return super.getNSType(node);
    }

    @Override
    public String getUnparsedEntityURI(String name) {
        if (this._dom != null) {
            return this._dom.getUnparsedEntityURI(name);
        }
        return super.getUnparsedEntityURI(name);
    }

    @Override
    public Hashtable getElementsWithIDs() {
        if (this._dom != null) {
            return this._dom.getElementsWithIDs();
        }
        return super.getElementsWithIDs();
    }

    private void maybeEmitStartElement() throws SAXException {
        if (this._openElementName != null) {
            int index = this._openElementName.indexOf(":");
            if (index < 0) {
                this._dom.startElement(null, this._openElementName, this._openElementName, this._attributes);
            } else {
                this._dom.startElement(null, this._openElementName.substring(index + 1), this._openElementName, this._attributes);
            }
            this._openElementName = null;
        }
    }

    private void prepareNewDOM() throws SAXException {
        this._dom = (SAXImpl)this._dtmManager.getDTM(null, true, this._wsfilter, true, false, false, this._initSize, this._buildIdIndex);
        this._dom.startDocument();
        for (int i = 0; i < this._size; ++i) {
            String str = this._textArray[i];
            this._dom.characters(str.toCharArray(), 0, str.length());
        }
        this._size = 0;
    }

    @Override
    public void startDocument() throws SAXException {
    }

    @Override
    public void endDocument() throws SAXException {
        if (this._dom != null) {
            this._dom.endDocument();
        } else {
            super.endDocument();
        }
    }

    @Override
    public void characters(String str) throws SAXException {
        if (this._dom != null) {
            this.characters(str.toCharArray(), 0, str.length());
        } else {
            super.characters(str);
        }
    }

    @Override
    public void characters(char[] ch, int offset, int length) throws SAXException {
        if (this._dom != null) {
            this.maybeEmitStartElement();
            this._dom.characters(ch, offset, length);
        } else {
            super.characters(ch, offset, length);
        }
    }

    @Override
    public boolean setEscaping(boolean escape) throws SAXException {
        if (this._dom != null) {
            return this._dom.setEscaping(escape);
        }
        return super.setEscaping(escape);
    }

    public void startElement(String elementName) throws SAXException {
        if (this._dom == null) {
            this.prepareNewDOM();
        }
        this.maybeEmitStartElement();
        this._openElementName = elementName;
        this._attributes.clear();
    }

    public void startElement(String uri, String localName, String qName) throws SAXException {
        this.startElement(qName);
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        this.startElement(qName);
    }

    public void endElement(String elementName) throws SAXException {
        this.maybeEmitStartElement();
        this._dom.endElement(null, null, elementName);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        this.endElement(qName);
    }

    public void addUniqueAttribute(String qName, String value, int flags) throws SAXException {
        this.addAttribute(qName, value);
    }

    public void addAttribute(String name, String value) {
        if (this._openElementName != null) {
            this._attributes.add(name, value);
        } else {
            BasisLibrary.runTimeError("STRAY_ATTRIBUTE_ERR", name);
        }
    }

    public void namespaceAfterStartElement(String prefix, String uri) throws SAXException {
        if (this._dom == null) {
            this.prepareNewDOM();
        }
        this._dom.startPrefixMapping(prefix, uri);
    }

    public void comment(String comment) throws SAXException {
        if (this._dom == null) {
            this.prepareNewDOM();
        }
        this.maybeEmitStartElement();
        char[] chars = comment.toCharArray();
        this._dom.comment(chars, 0, chars.length);
    }

    public void comment(char[] chars, int offset, int length) throws SAXException {
        if (this._dom == null) {
            this.prepareNewDOM();
        }
        this.maybeEmitStartElement();
        this._dom.comment(chars, offset, length);
    }

    public void processingInstruction(String target, String data) throws SAXException {
        if (this._dom == null) {
            this.prepareNewDOM();
        }
        this.maybeEmitStartElement();
        this._dom.processingInstruction(target, data);
    }

    @Override
    public void setFeature(String featureId, boolean state) {
        if (this._dom != null) {
            this._dom.setFeature(featureId, state);
        }
    }

    @Override
    public void setProperty(String property, Object value) {
        if (this._dom != null) {
            this._dom.setProperty(property, value);
        }
    }

    @Override
    public DTMAxisTraverser getAxisTraverser(int axis) {
        if (this._dom != null) {
            return this._dom.getAxisTraverser(axis);
        }
        return super.getAxisTraverser(axis);
    }

    @Override
    public boolean hasChildNodes(int nodeHandle) {
        if (this._dom != null) {
            return this._dom.hasChildNodes(nodeHandle);
        }
        return super.hasChildNodes(nodeHandle);
    }

    @Override
    public int getFirstChild(int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getFirstChild(nodeHandle);
        }
        return super.getFirstChild(nodeHandle);
    }

    @Override
    public int getLastChild(int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getLastChild(nodeHandle);
        }
        return super.getLastChild(nodeHandle);
    }

    @Override
    public int getAttributeNode(int elementHandle, String namespaceURI, String name) {
        if (this._dom != null) {
            return this._dom.getAttributeNode(elementHandle, namespaceURI, name);
        }
        return super.getAttributeNode(elementHandle, namespaceURI, name);
    }

    @Override
    public int getFirstAttribute(int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getFirstAttribute(nodeHandle);
        }
        return super.getFirstAttribute(nodeHandle);
    }

    @Override
    public int getFirstNamespaceNode(int nodeHandle, boolean inScope) {
        if (this._dom != null) {
            return this._dom.getFirstNamespaceNode(nodeHandle, inScope);
        }
        return super.getFirstNamespaceNode(nodeHandle, inScope);
    }

    @Override
    public int getNextSibling(int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getNextSibling(nodeHandle);
        }
        return super.getNextSibling(nodeHandle);
    }

    @Override
    public int getPreviousSibling(int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getPreviousSibling(nodeHandle);
        }
        return super.getPreviousSibling(nodeHandle);
    }

    @Override
    public int getNextAttribute(int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getNextAttribute(nodeHandle);
        }
        return super.getNextAttribute(nodeHandle);
    }

    @Override
    public int getNextNamespaceNode(int baseHandle, int namespaceHandle, boolean inScope) {
        if (this._dom != null) {
            return this._dom.getNextNamespaceNode(baseHandle, namespaceHandle, inScope);
        }
        return super.getNextNamespaceNode(baseHandle, namespaceHandle, inScope);
    }

    @Override
    public int getOwnerDocument(int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getOwnerDocument(nodeHandle);
        }
        return super.getOwnerDocument(nodeHandle);
    }

    @Override
    public int getDocumentRoot(int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getDocumentRoot(nodeHandle);
        }
        return super.getDocumentRoot(nodeHandle);
    }

    @Override
    public XMLString getStringValue(int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getStringValue(nodeHandle);
        }
        return super.getStringValue(nodeHandle);
    }

    @Override
    public int getStringValueChunkCount(int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getStringValueChunkCount(nodeHandle);
        }
        return super.getStringValueChunkCount(nodeHandle);
    }

    @Override
    public char[] getStringValueChunk(int nodeHandle, int chunkIndex, int[] startAndLen) {
        if (this._dom != null) {
            return this._dom.getStringValueChunk(nodeHandle, chunkIndex, startAndLen);
        }
        return super.getStringValueChunk(nodeHandle, chunkIndex, startAndLen);
    }

    @Override
    public int getExpandedTypeID(String namespace, String localName, int type) {
        if (this._dom != null) {
            return this._dom.getExpandedTypeID(namespace, localName, type);
        }
        return super.getExpandedTypeID(namespace, localName, type);
    }

    @Override
    public String getLocalNameFromExpandedNameID(int ExpandedNameID) {
        if (this._dom != null) {
            return this._dom.getLocalNameFromExpandedNameID(ExpandedNameID);
        }
        return super.getLocalNameFromExpandedNameID(ExpandedNameID);
    }

    @Override
    public String getNamespaceFromExpandedNameID(int ExpandedNameID) {
        if (this._dom != null) {
            return this._dom.getNamespaceFromExpandedNameID(ExpandedNameID);
        }
        return super.getNamespaceFromExpandedNameID(ExpandedNameID);
    }

    @Override
    public String getLocalName(int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getLocalName(nodeHandle);
        }
        return super.getLocalName(nodeHandle);
    }

    @Override
    public String getPrefix(int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getPrefix(nodeHandle);
        }
        return super.getPrefix(nodeHandle);
    }

    @Override
    public String getNamespaceURI(int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getNamespaceURI(nodeHandle);
        }
        return super.getNamespaceURI(nodeHandle);
    }

    @Override
    public String getNodeValue(int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getNodeValue(nodeHandle);
        }
        return super.getNodeValue(nodeHandle);
    }

    @Override
    public short getNodeType(int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getNodeType(nodeHandle);
        }
        return super.getNodeType(nodeHandle);
    }

    @Override
    public short getLevel(int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getLevel(nodeHandle);
        }
        return super.getLevel(nodeHandle);
    }

    @Override
    public boolean isSupported(String feature, String version) {
        if (this._dom != null) {
            return this._dom.isSupported(feature, version);
        }
        return super.isSupported(feature, version);
    }

    @Override
    public String getDocumentBaseURI() {
        if (this._dom != null) {
            return this._dom.getDocumentBaseURI();
        }
        return super.getDocumentBaseURI();
    }

    @Override
    public void setDocumentBaseURI(String baseURI) {
        if (this._dom != null) {
            this._dom.setDocumentBaseURI(baseURI);
        } else {
            super.setDocumentBaseURI(baseURI);
        }
    }

    @Override
    public String getDocumentSystemIdentifier(int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getDocumentSystemIdentifier(nodeHandle);
        }
        return super.getDocumentSystemIdentifier(nodeHandle);
    }

    @Override
    public String getDocumentEncoding(int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getDocumentEncoding(nodeHandle);
        }
        return super.getDocumentEncoding(nodeHandle);
    }

    @Override
    public String getDocumentStandalone(int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getDocumentStandalone(nodeHandle);
        }
        return super.getDocumentStandalone(nodeHandle);
    }

    @Override
    public String getDocumentVersion(int documentHandle) {
        if (this._dom != null) {
            return this._dom.getDocumentVersion(documentHandle);
        }
        return super.getDocumentVersion(documentHandle);
    }

    @Override
    public boolean getDocumentAllDeclarationsProcessed() {
        if (this._dom != null) {
            return this._dom.getDocumentAllDeclarationsProcessed();
        }
        return super.getDocumentAllDeclarationsProcessed();
    }

    @Override
    public String getDocumentTypeDeclarationSystemIdentifier() {
        if (this._dom != null) {
            return this._dom.getDocumentTypeDeclarationSystemIdentifier();
        }
        return super.getDocumentTypeDeclarationSystemIdentifier();
    }

    @Override
    public String getDocumentTypeDeclarationPublicIdentifier() {
        if (this._dom != null) {
            return this._dom.getDocumentTypeDeclarationPublicIdentifier();
        }
        return super.getDocumentTypeDeclarationPublicIdentifier();
    }

    @Override
    public int getElementById(String elementId) {
        if (this._dom != null) {
            return this._dom.getElementById(elementId);
        }
        return super.getElementById(elementId);
    }

    @Override
    public boolean supportsPreStripping() {
        if (this._dom != null) {
            return this._dom.supportsPreStripping();
        }
        return super.supportsPreStripping();
    }

    @Override
    public boolean isNodeAfter(int firstNodeHandle, int secondNodeHandle) {
        if (this._dom != null) {
            return this._dom.isNodeAfter(firstNodeHandle, secondNodeHandle);
        }
        return super.isNodeAfter(firstNodeHandle, secondNodeHandle);
    }

    @Override
    public boolean isCharacterElementContentWhitespace(int nodeHandle) {
        if (this._dom != null) {
            return this._dom.isCharacterElementContentWhitespace(nodeHandle);
        }
        return super.isCharacterElementContentWhitespace(nodeHandle);
    }

    @Override
    public boolean isDocumentAllDeclarationsProcessed(int documentHandle) {
        if (this._dom != null) {
            return this._dom.isDocumentAllDeclarationsProcessed(documentHandle);
        }
        return super.isDocumentAllDeclarationsProcessed(documentHandle);
    }

    @Override
    public boolean isAttributeSpecified(int attributeHandle) {
        if (this._dom != null) {
            return this._dom.isAttributeSpecified(attributeHandle);
        }
        return super.isAttributeSpecified(attributeHandle);
    }

    @Override
    public void dispatchCharactersEvents(int nodeHandle, ContentHandler ch, boolean normalize) throws SAXException {
        if (this._dom != null) {
            this._dom.dispatchCharactersEvents(nodeHandle, ch, normalize);
        } else {
            super.dispatchCharactersEvents(nodeHandle, ch, normalize);
        }
    }

    @Override
    public void dispatchToEvents(int nodeHandle, ContentHandler ch) throws SAXException {
        if (this._dom != null) {
            this._dom.dispatchToEvents(nodeHandle, ch);
        } else {
            super.dispatchToEvents(nodeHandle, ch);
        }
    }

    @Override
    public Node getNode(int nodeHandle) {
        if (this._dom != null) {
            return this._dom.getNode(nodeHandle);
        }
        return super.getNode(nodeHandle);
    }

    @Override
    public boolean needsTwoThreads() {
        if (this._dom != null) {
            return this._dom.needsTwoThreads();
        }
        return super.needsTwoThreads();
    }

    @Override
    public ContentHandler getContentHandler() {
        if (this._dom != null) {
            return this._dom.getContentHandler();
        }
        return super.getContentHandler();
    }

    @Override
    public LexicalHandler getLexicalHandler() {
        if (this._dom != null) {
            return this._dom.getLexicalHandler();
        }
        return super.getLexicalHandler();
    }

    @Override
    public EntityResolver getEntityResolver() {
        if (this._dom != null) {
            return this._dom.getEntityResolver();
        }
        return super.getEntityResolver();
    }

    @Override
    public DTDHandler getDTDHandler() {
        if (this._dom != null) {
            return this._dom.getDTDHandler();
        }
        return super.getDTDHandler();
    }

    @Override
    public ErrorHandler getErrorHandler() {
        if (this._dom != null) {
            return this._dom.getErrorHandler();
        }
        return super.getErrorHandler();
    }

    @Override
    public DeclHandler getDeclHandler() {
        if (this._dom != null) {
            return this._dom.getDeclHandler();
        }
        return super.getDeclHandler();
    }

    @Override
    public void appendChild(int newChild, boolean clone, boolean cloneDepth) {
        if (this._dom != null) {
            this._dom.appendChild(newChild, clone, cloneDepth);
        } else {
            super.appendChild(newChild, clone, cloneDepth);
        }
    }

    @Override
    public void appendTextChild(String str) {
        if (this._dom != null) {
            this._dom.appendTextChild(str);
        } else {
            super.appendTextChild(str);
        }
    }

    @Override
    public SourceLocator getSourceLocatorFor(int node) {
        if (this._dom != null) {
            return this._dom.getSourceLocatorFor(node);
        }
        return super.getSourceLocatorFor(node);
    }

    @Override
    public void documentRegistration() {
        if (this._dom != null) {
            this._dom.documentRegistration();
        } else {
            super.documentRegistration();
        }
    }

    @Override
    public void documentRelease() {
        if (this._dom != null) {
            this._dom.documentRelease();
        } else {
            super.documentRelease();
        }
    }
}

