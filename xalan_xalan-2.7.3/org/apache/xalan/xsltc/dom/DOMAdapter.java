/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serializer.SerializationHandler
 */
package org.apache.xalan.xsltc.dom;

import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.DOMEnhancedForDTM;
import org.apache.xalan.xsltc.StripFilter;
import org.apache.xalan.xsltc.TransletException;
import org.apache.xalan.xsltc.runtime.Hashtable;
import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.serializer.SerializationHandler;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class DOMAdapter
implements DOM {
    private DOMEnhancedForDTM _enhancedDOM;
    private DOM _dom;
    private String[] _namesArray;
    private String[] _urisArray;
    private int[] _typesArray;
    private String[] _namespaceArray;
    private short[] _mapping = null;
    private int[] _reverse = null;
    private short[] _NSmapping = null;
    private short[] _NSreverse = null;
    private StripFilter _filter = null;
    private int _multiDOMMask;

    public DOMAdapter(DOM dom, String[] namesArray, String[] urisArray, int[] typesArray, String[] namespaceArray) {
        if (dom instanceof DOMEnhancedForDTM) {
            this._enhancedDOM = (DOMEnhancedForDTM)dom;
        }
        this._dom = dom;
        this._namesArray = namesArray;
        this._urisArray = urisArray;
        this._typesArray = typesArray;
        this._namespaceArray = namespaceArray;
    }

    @Override
    public void setupMapping(String[] names, String[] urisArray, int[] typesArray, String[] namespaces) {
        this._namesArray = names;
        this._urisArray = urisArray;
        this._typesArray = typesArray;
        this._namespaceArray = namespaces;
    }

    public String[] getNamesArray() {
        return this._namesArray;
    }

    public String[] getUrisArray() {
        return this._urisArray;
    }

    public int[] getTypesArray() {
        return this._typesArray;
    }

    public String[] getNamespaceArray() {
        return this._namespaceArray;
    }

    public DOM getDOMImpl() {
        return this._dom;
    }

    private short[] getMapping() {
        if (this._mapping == null && this._enhancedDOM != null) {
            this._mapping = this._enhancedDOM.getMapping(this._namesArray, this._urisArray, this._typesArray);
        }
        return this._mapping;
    }

    private int[] getReverse() {
        if (this._reverse == null && this._enhancedDOM != null) {
            this._reverse = this._enhancedDOM.getReverseMapping(this._namesArray, this._urisArray, this._typesArray);
        }
        return this._reverse;
    }

    private short[] getNSMapping() {
        if (this._NSmapping == null && this._enhancedDOM != null) {
            this._NSmapping = this._enhancedDOM.getNamespaceMapping(this._namespaceArray);
        }
        return this._NSmapping;
    }

    private short[] getNSReverse() {
        if (this._NSreverse == null && this._enhancedDOM != null) {
            this._NSreverse = this._enhancedDOM.getReverseNamespaceMapping(this._namespaceArray);
        }
        return this._NSreverse;
    }

    @Override
    public DTMAxisIterator getIterator() {
        return this._dom.getIterator();
    }

    @Override
    public String getStringValue() {
        return this._dom.getStringValue();
    }

    @Override
    public DTMAxisIterator getChildren(int node) {
        if (this._enhancedDOM != null) {
            return this._enhancedDOM.getChildren(node);
        }
        DTMAxisIterator iterator = this._dom.getChildren(node);
        return iterator.setStartNode(node);
    }

    @Override
    public void setFilter(StripFilter filter) {
        this._filter = filter;
    }

    @Override
    public DTMAxisIterator getTypedChildren(int type) {
        int[] reverse = this.getReverse();
        if (this._enhancedDOM != null) {
            return this._enhancedDOM.getTypedChildren(reverse[type]);
        }
        return this._dom.getTypedChildren(type);
    }

    @Override
    public DTMAxisIterator getNamespaceAxisIterator(int axis, int ns) {
        return this._dom.getNamespaceAxisIterator(axis, this.getNSReverse()[ns]);
    }

    @Override
    public DTMAxisIterator getAxisIterator(int axis) {
        if (this._enhancedDOM != null) {
            return this._enhancedDOM.getAxisIterator(axis);
        }
        return this._dom.getAxisIterator(axis);
    }

    @Override
    public DTMAxisIterator getTypedAxisIterator(int axis, int type) {
        int[] reverse = this.getReverse();
        if (this._enhancedDOM != null) {
            return this._enhancedDOM.getTypedAxisIterator(axis, reverse[type]);
        }
        return this._dom.getTypedAxisIterator(axis, type);
    }

    public int getMultiDOMMask() {
        return this._multiDOMMask;
    }

    public void setMultiDOMMask(int mask) {
        this._multiDOMMask = mask;
    }

    @Override
    public DTMAxisIterator getNthDescendant(int type, int n, boolean includeself) {
        return this._dom.getNthDescendant(this.getReverse()[type], n, includeself);
    }

    @Override
    public DTMAxisIterator getNodeValueIterator(DTMAxisIterator iterator, int type, String value, boolean op) {
        return this._dom.getNodeValueIterator(iterator, type, value, op);
    }

    @Override
    public DTMAxisIterator orderNodes(DTMAxisIterator source, int node) {
        return this._dom.orderNodes(source, node);
    }

    @Override
    public int getExpandedTypeID(int node) {
        short[] mapping = this.getMapping();
        int type = this._enhancedDOM != null ? mapping[this._enhancedDOM.getExpandedTypeID2(node)] : (null != mapping ? mapping[this._dom.getExpandedTypeID(node)] : this._dom.getExpandedTypeID(node));
        return type;
    }

    @Override
    public int getNamespaceType(int node) {
        return this.getNSMapping()[this._dom.getNSType(node)];
    }

    @Override
    public int getNSType(int node) {
        return this._dom.getNSType(node);
    }

    @Override
    public int getParent(int node) {
        return this._dom.getParent(node);
    }

    @Override
    public int getAttributeNode(int type, int element) {
        return this._dom.getAttributeNode(this.getReverse()[type], element);
    }

    @Override
    public String getNodeName(int node) {
        if (node == -1) {
            return "";
        }
        return this._dom.getNodeName(node);
    }

    @Override
    public String getNodeNameX(int node) {
        if (node == -1) {
            return "";
        }
        return this._dom.getNodeNameX(node);
    }

    @Override
    public String getNamespaceName(int node) {
        if (node == -1) {
            return "";
        }
        return this._dom.getNamespaceName(node);
    }

    @Override
    public String getStringValueX(int node) {
        if (this._enhancedDOM != null) {
            return this._enhancedDOM.getStringValueX(node);
        }
        if (node == -1) {
            return "";
        }
        return this._dom.getStringValueX(node);
    }

    @Override
    public void copy(int node, SerializationHandler handler) throws TransletException {
        this._dom.copy(node, handler);
    }

    @Override
    public void copy(DTMAxisIterator nodes, SerializationHandler handler) throws TransletException {
        this._dom.copy(nodes, handler);
    }

    @Override
    public String shallowCopy(int node, SerializationHandler handler) throws TransletException {
        if (this._enhancedDOM != null) {
            return this._enhancedDOM.shallowCopy(node, handler);
        }
        return this._dom.shallowCopy(node, handler);
    }

    @Override
    public boolean lessThan(int node1, int node2) {
        return this._dom.lessThan(node1, node2);
    }

    @Override
    public void characters(int textNode, SerializationHandler handler) throws TransletException {
        if (this._enhancedDOM != null) {
            this._enhancedDOM.characters(textNode, handler);
        } else {
            this._dom.characters(textNode, handler);
        }
    }

    @Override
    public Node makeNode(int index) {
        return this._dom.makeNode(index);
    }

    @Override
    public Node makeNode(DTMAxisIterator iter) {
        return this._dom.makeNode(iter);
    }

    @Override
    public NodeList makeNodeList(int index) {
        return this._dom.makeNodeList(index);
    }

    @Override
    public NodeList makeNodeList(DTMAxisIterator iter) {
        return this._dom.makeNodeList(iter);
    }

    @Override
    public String getLanguage(int node) {
        return this._dom.getLanguage(node);
    }

    @Override
    public int getSize() {
        return this._dom.getSize();
    }

    public void setDocumentURI(String uri) {
        if (this._enhancedDOM != null) {
            this._enhancedDOM.setDocumentURI(uri);
        }
    }

    public String getDocumentURI() {
        if (this._enhancedDOM != null) {
            return this._enhancedDOM.getDocumentURI();
        }
        return "";
    }

    @Override
    public String getDocumentURI(int node) {
        return this._dom.getDocumentURI(node);
    }

    @Override
    public int getDocument() {
        return this._dom.getDocument();
    }

    @Override
    public boolean isElement(int node) {
        return this._dom.isElement(node);
    }

    @Override
    public boolean isAttribute(int node) {
        return this._dom.isAttribute(node);
    }

    @Override
    public int getNodeIdent(int nodeHandle) {
        return this._dom.getNodeIdent(nodeHandle);
    }

    @Override
    public int getNodeHandle(int nodeId) {
        return this._dom.getNodeHandle(nodeId);
    }

    @Override
    public DOM getResultTreeFrag(int initSize, int rtfType) {
        if (this._enhancedDOM != null) {
            return this._enhancedDOM.getResultTreeFrag(initSize, rtfType);
        }
        return this._dom.getResultTreeFrag(initSize, rtfType);
    }

    @Override
    public DOM getResultTreeFrag(int initSize, int rtfType, boolean addToManager) {
        if (this._enhancedDOM != null) {
            return this._enhancedDOM.getResultTreeFrag(initSize, rtfType, addToManager);
        }
        return this._dom.getResultTreeFrag(initSize, rtfType, addToManager);
    }

    @Override
    public SerializationHandler getOutputDomBuilder() {
        return this._dom.getOutputDomBuilder();
    }

    @Override
    public String lookupNamespace(int node, String prefix) throws TransletException {
        return this._dom.lookupNamespace(node, prefix);
    }

    @Override
    public String getUnparsedEntityURI(String entity) {
        return this._dom.getUnparsedEntityURI(entity);
    }

    @Override
    public Hashtable getElementsWithIDs() {
        return this._dom.getElementsWithIDs();
    }
}

