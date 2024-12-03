/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serializer.SerializationHandler
 */
package org.apache.xalan.xsltc.dom;

import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.StripFilter;
import org.apache.xalan.xsltc.TransletException;
import org.apache.xalan.xsltc.dom.AdaptiveResultTreeImpl;
import org.apache.xalan.xsltc.dom.DOMAdapter;
import org.apache.xalan.xsltc.dom.SimpleResultTreeImpl;
import org.apache.xalan.xsltc.runtime.BasisLibrary;
import org.apache.xalan.xsltc.runtime.Hashtable;
import org.apache.xml.dtm.Axis;
import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.ref.DTMAxisIteratorBase;
import org.apache.xml.dtm.ref.DTMDefaultBase;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xml.utils.SuballocatedIntVector;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class MultiDOM
implements DOM {
    private static final int NO_TYPE = -2;
    private static final int INITIAL_SIZE = 4;
    private DOM[] _adapters;
    private DOMAdapter _main;
    private DTMManager _dtmManager;
    private int _free = 1;
    private int _size = 4;
    private Hashtable _documents = new Hashtable();

    public MultiDOM(DOM main) {
        this._adapters = new DOM[4];
        DOMAdapter adapter = (DOMAdapter)main;
        this._adapters[0] = adapter;
        this._main = adapter;
        DOM dom = adapter.getDOMImpl();
        if (dom instanceof DTMDefaultBase) {
            this._dtmManager = ((DTMDefaultBase)((Object)dom)).getManager();
        }
        this.addDOMAdapter(adapter, false);
    }

    public int nextMask() {
        return this._free;
    }

    @Override
    public void setupMapping(String[] names, String[] uris, int[] types, String[] namespaces) {
    }

    public int addDOMAdapter(DOMAdapter adapter) {
        return this.addDOMAdapter(adapter, true);
    }

    private int addDOMAdapter(DOMAdapter adapter, boolean indexByURI) {
        AdaptiveResultTreeImpl adaptiveRTF;
        DOM nestedDom;
        DOM dom = adapter.getDOMImpl();
        int domNo = 1;
        int dtmSize = 1;
        SuballocatedIntVector dtmIds = null;
        if (dom instanceof DTMDefaultBase) {
            DTMDefaultBase dtmdb = (DTMDefaultBase)((Object)dom);
            dtmIds = dtmdb.getDTMIDs();
            dtmSize = dtmIds.size();
            domNo = dtmIds.elementAt(dtmSize - 1) >>> 16;
        } else if (dom instanceof SimpleResultTreeImpl) {
            SimpleResultTreeImpl simpleRTF = (SimpleResultTreeImpl)dom;
            domNo = simpleRTF.getDocument() >>> 16;
        }
        if (domNo >= this._size) {
            int oldSize = this._size;
            do {
                this._size *= 2;
            } while (this._size <= domNo);
            DOMAdapter[] newArray = new DOMAdapter[this._size];
            System.arraycopy(this._adapters, 0, newArray, 0, oldSize);
            this._adapters = newArray;
        }
        this._free = domNo + 1;
        if (dtmSize == 1) {
            this._adapters[domNo] = adapter;
        } else if (dtmIds != null) {
            int domPos = 0;
            for (int i = dtmSize - 1; i >= 0; --i) {
                domPos = dtmIds.elementAt(i) >>> 16;
                this._adapters[domPos] = adapter;
            }
            domNo = domPos;
        }
        if (indexByURI) {
            String uri = adapter.getDocumentURI(0);
            this._documents.put(uri, new Integer(domNo));
        }
        if (dom instanceof AdaptiveResultTreeImpl && (nestedDom = (adaptiveRTF = (AdaptiveResultTreeImpl)dom).getNestedDOM()) != null) {
            DOMAdapter newAdapter = new DOMAdapter(nestedDom, adapter.getNamesArray(), adapter.getUrisArray(), adapter.getTypesArray(), adapter.getNamespaceArray());
            this.addDOMAdapter(newAdapter);
        }
        return domNo;
    }

    public int getDocumentMask(String uri) {
        Integer domIdx = (Integer)this._documents.get(uri);
        if (domIdx == null) {
            return -1;
        }
        return domIdx;
    }

    public DOM getDOMAdapter(String uri) {
        Integer domIdx = (Integer)this._documents.get(uri);
        if (domIdx == null) {
            return null;
        }
        return this._adapters[domIdx];
    }

    @Override
    public int getDocument() {
        return this._main.getDocument();
    }

    public DTMManager getDTMManager() {
        return this._dtmManager;
    }

    @Override
    public DTMAxisIterator getIterator() {
        return this._main.getIterator();
    }

    @Override
    public String getStringValue() {
        return this._main.getStringValue();
    }

    @Override
    public DTMAxisIterator getChildren(int node) {
        return this._adapters[this.getDTMId(node)].getChildren(node);
    }

    @Override
    public DTMAxisIterator getTypedChildren(int type) {
        return new AxisIterator(3, type);
    }

    @Override
    public DTMAxisIterator getAxisIterator(int axis) {
        return new AxisIterator(axis, -2);
    }

    @Override
    public DTMAxisIterator getTypedAxisIterator(int axis, int type) {
        return new AxisIterator(axis, type);
    }

    @Override
    public DTMAxisIterator getNthDescendant(int node, int n, boolean includeself) {
        return this._adapters[this.getDTMId(node)].getNthDescendant(node, n, includeself);
    }

    @Override
    public DTMAxisIterator getNodeValueIterator(DTMAxisIterator iterator, int type, String value, boolean op) {
        return new NodeValueIterator(iterator, type, value, op);
    }

    @Override
    public DTMAxisIterator getNamespaceAxisIterator(int axis, int ns) {
        DTMAxisIterator iterator = this._main.getNamespaceAxisIterator(axis, ns);
        return iterator;
    }

    @Override
    public DTMAxisIterator orderNodes(DTMAxisIterator source, int node) {
        return this._adapters[this.getDTMId(node)].orderNodes(source, node);
    }

    @Override
    public int getExpandedTypeID(int node) {
        if (node != -1) {
            return this._adapters[node >>> 16].getExpandedTypeID(node);
        }
        return -1;
    }

    @Override
    public int getNamespaceType(int node) {
        return this._adapters[this.getDTMId(node)].getNamespaceType(node);
    }

    @Override
    public int getNSType(int node) {
        return this._adapters[this.getDTMId(node)].getNSType(node);
    }

    @Override
    public int getParent(int node) {
        if (node == -1) {
            return -1;
        }
        return this._adapters[node >>> 16].getParent(node);
    }

    @Override
    public int getAttributeNode(int type, int el) {
        if (el == -1) {
            return -1;
        }
        return this._adapters[el >>> 16].getAttributeNode(type, el);
    }

    @Override
    public String getNodeName(int node) {
        if (node == -1) {
            return "";
        }
        return this._adapters[node >>> 16].getNodeName(node);
    }

    @Override
    public String getNodeNameX(int node) {
        if (node == -1) {
            return "";
        }
        return this._adapters[node >>> 16].getNodeNameX(node);
    }

    @Override
    public String getNamespaceName(int node) {
        if (node == -1) {
            return "";
        }
        return this._adapters[node >>> 16].getNamespaceName(node);
    }

    @Override
    public String getStringValueX(int node) {
        if (node == -1) {
            return "";
        }
        return this._adapters[node >>> 16].getStringValueX(node);
    }

    @Override
    public void copy(int node, SerializationHandler handler) throws TransletException {
        if (node != -1) {
            this._adapters[node >>> 16].copy(node, handler);
        }
    }

    @Override
    public void copy(DTMAxisIterator nodes, SerializationHandler handler) throws TransletException {
        int node;
        while ((node = nodes.next()) != -1) {
            this._adapters[node >>> 16].copy(node, handler);
        }
    }

    @Override
    public String shallowCopy(int node, SerializationHandler handler) throws TransletException {
        if (node == -1) {
            return "";
        }
        return this._adapters[node >>> 16].shallowCopy(node, handler);
    }

    @Override
    public boolean lessThan(int node1, int node2) {
        int dom2;
        if (node1 == -1) {
            return true;
        }
        if (node2 == -1) {
            return false;
        }
        int dom1 = this.getDTMId(node1);
        return dom1 == (dom2 = this.getDTMId(node2)) ? this._adapters[dom1].lessThan(node1, node2) : dom1 < dom2;
    }

    @Override
    public void characters(int textNode, SerializationHandler handler) throws TransletException {
        if (textNode != -1) {
            this._adapters[textNode >>> 16].characters(textNode, handler);
        }
    }

    @Override
    public void setFilter(StripFilter filter) {
        for (int dom = 0; dom < this._free; ++dom) {
            if (this._adapters[dom] == null) continue;
            this._adapters[dom].setFilter(filter);
        }
    }

    @Override
    public Node makeNode(int index) {
        if (index == -1) {
            return null;
        }
        return this._adapters[this.getDTMId(index)].makeNode(index);
    }

    @Override
    public Node makeNode(DTMAxisIterator iter) {
        return this._main.makeNode(iter);
    }

    @Override
    public NodeList makeNodeList(int index) {
        if (index == -1) {
            return null;
        }
        return this._adapters[this.getDTMId(index)].makeNodeList(index);
    }

    @Override
    public NodeList makeNodeList(DTMAxisIterator iter) {
        return this._main.makeNodeList(iter);
    }

    @Override
    public String getLanguage(int node) {
        return this._adapters[this.getDTMId(node)].getLanguage(node);
    }

    @Override
    public int getSize() {
        int size = 0;
        for (int i = 0; i < this._size; ++i) {
            size += this._adapters[i].getSize();
        }
        return size;
    }

    @Override
    public String getDocumentURI(int node) {
        if (node == -1) {
            node = 0;
        }
        return this._adapters[node >>> 16].getDocumentURI(0);
    }

    @Override
    public boolean isElement(int node) {
        if (node == -1) {
            return false;
        }
        return this._adapters[node >>> 16].isElement(node);
    }

    @Override
    public boolean isAttribute(int node) {
        if (node == -1) {
            return false;
        }
        return this._adapters[node >>> 16].isAttribute(node);
    }

    public int getDTMId(int nodeHandle) {
        int id;
        if (nodeHandle == -1) {
            return 0;
        }
        for (id = nodeHandle >>> 16; id >= 2 && this._adapters[id] == this._adapters[id - 1]; --id) {
        }
        return id;
    }

    @Override
    public int getNodeIdent(int nodeHandle) {
        return this._adapters[nodeHandle >>> 16].getNodeIdent(nodeHandle);
    }

    @Override
    public int getNodeHandle(int nodeId) {
        return this._main.getNodeHandle(nodeId);
    }

    @Override
    public DOM getResultTreeFrag(int initSize, int rtfType) {
        return this._main.getResultTreeFrag(initSize, rtfType);
    }

    @Override
    public DOM getResultTreeFrag(int initSize, int rtfType, boolean addToManager) {
        return this._main.getResultTreeFrag(initSize, rtfType, addToManager);
    }

    public DOM getMain() {
        return this._main;
    }

    @Override
    public SerializationHandler getOutputDomBuilder() {
        return this._main.getOutputDomBuilder();
    }

    @Override
    public String lookupNamespace(int node, String prefix) throws TransletException {
        return this._main.lookupNamespace(node, prefix);
    }

    @Override
    public String getUnparsedEntityURI(String entity) {
        return this._main.getUnparsedEntityURI(entity);
    }

    @Override
    public Hashtable getElementsWithIDs() {
        return this._main.getElementsWithIDs();
    }

    private final class NodeValueIterator
    extends DTMAxisIteratorBase {
        private DTMAxisIterator _source;
        private String _value;
        private boolean _op;
        private final boolean _isReverse;
        private int _returnType = 1;

        public NodeValueIterator(DTMAxisIterator source, int returnType, String value, boolean op) {
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
                clone._source = this._source.cloneIterator();
                clone.setRestartable(false);
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
                String val = MultiDOM.this.getStringValueX(node);
                if (this._value.equals(val) != this._op) continue;
                if (this._returnType == 0) {
                    return this.returnNode(node);
                }
                return this.returnNode(MultiDOM.this.getParent(node));
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

    private final class AxisIterator
    extends DTMAxisIteratorBase {
        private final int _axis;
        private final int _type;
        private DTMAxisIterator _source;
        private int _dtmId = -1;

        public AxisIterator(int axis, int type) {
            this._axis = axis;
            this._type = type;
        }

        @Override
        public int next() {
            if (this._source == null) {
                return -1;
            }
            return this._source.next();
        }

        @Override
        public void setRestartable(boolean flag) {
            if (this._source != null) {
                this._source.setRestartable(flag);
            }
        }

        @Override
        public DTMAxisIterator setStartNode(int node) {
            if (node == -1) {
                return this;
            }
            int dom = node >>> 16;
            if (this._source == null || this._dtmId != dom) {
                this._source = this._type == -2 ? MultiDOM.this._adapters[dom].getAxisIterator(this._axis) : (this._axis == 3 ? MultiDOM.this._adapters[dom].getTypedChildren(this._type) : MultiDOM.this._adapters[dom].getTypedAxisIterator(this._axis, this._type));
            }
            this._dtmId = dom;
            this._source.setStartNode(node);
            return this;
        }

        @Override
        public DTMAxisIterator reset() {
            if (this._source != null) {
                this._source.reset();
            }
            return this;
        }

        @Override
        public int getLast() {
            if (this._source != null) {
                return this._source.getLast();
            }
            return -1;
        }

        @Override
        public int getPosition() {
            if (this._source != null) {
                return this._source.getPosition();
            }
            return -1;
        }

        @Override
        public boolean isReverse() {
            return Axis.isReverse(this._axis);
        }

        @Override
        public void setMark() {
            if (this._source != null) {
                this._source.setMark();
            }
        }

        @Override
        public void gotoMark() {
            if (this._source != null) {
                this._source.gotoMark();
            }
        }

        @Override
        public DTMAxisIterator cloneIterator() {
            AxisIterator clone = new AxisIterator(this._axis, this._type);
            if (this._source != null) {
                clone._source = this._source.cloneIterator();
            }
            clone._dtmId = this._dtmId;
            return clone;
        }
    }
}

