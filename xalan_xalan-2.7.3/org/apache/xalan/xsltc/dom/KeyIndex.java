/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.dom;

import java.util.StringTokenizer;
import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.DOMEnhancedForDTM;
import org.apache.xalan.xsltc.dom.DOMAdapter;
import org.apache.xalan.xsltc.dom.MultiValuedNodeHeapIterator;
import org.apache.xalan.xsltc.runtime.BasisLibrary;
import org.apache.xalan.xsltc.runtime.Hashtable;
import org.apache.xalan.xsltc.util.IntegerArray;
import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.dtm.ref.DTMAxisIteratorBase;

public class KeyIndex
extends DTMAxisIteratorBase {
    private Hashtable _index;
    private int _currentDocumentNode = -1;
    private Hashtable _rootToIndexMap = new Hashtable();
    private IntegerArray _nodes = null;
    private DOM _dom;
    private DOMEnhancedForDTM _enhancedDOM;
    private int _markedPosition = 0;
    private static final IntegerArray EMPTY_NODES = new IntegerArray(0);

    public KeyIndex(int dummy) {
    }

    @Override
    public void setRestartable(boolean flag) {
    }

    public void add(Object value, int node, int rootNode) {
        IntegerArray nodes;
        if (this._currentDocumentNode != rootNode) {
            this._currentDocumentNode = rootNode;
            this._index = new Hashtable();
            this._rootToIndexMap.put(new Integer(rootNode), this._index);
        }
        if ((nodes = (IntegerArray)this._index.get(value)) == null) {
            nodes = new IntegerArray();
            this._index.put(value, nodes);
            nodes.add(node);
        } else if (node != nodes.at(nodes.cardinality() - 1)) {
            nodes.add(node);
        }
    }

    public void merge(KeyIndex other) {
        if (other == null) {
            return;
        }
        if (other._nodes != null) {
            if (this._nodes == null) {
                this._nodes = (IntegerArray)other._nodes.clone();
            } else {
                this._nodes.merge(other._nodes);
            }
        }
    }

    public void lookupId(Object value) {
        this._nodes = null;
        StringTokenizer values = new StringTokenizer((String)value, " \n\t");
        while (values.hasMoreElements()) {
            String token = (String)values.nextElement();
            IntegerArray nodes = (IntegerArray)this._index.get(token);
            if (nodes == null && this._enhancedDOM != null && this._enhancedDOM.hasDOMSource()) {
                nodes = this.getDOMNodeById(token);
            }
            if (nodes == null) continue;
            if (this._nodes == null) {
                this._nodes = nodes = (IntegerArray)nodes.clone();
                continue;
            }
            this._nodes.merge(nodes);
        }
    }

    public IntegerArray getDOMNodeById(String id) {
        int ident;
        IntegerArray nodes = null;
        if (this._enhancedDOM != null && (ident = this._enhancedDOM.getElementById(id)) != -1) {
            Integer root = new Integer(this._enhancedDOM.getDocument());
            Hashtable index = (Hashtable)this._rootToIndexMap.get(root);
            if (index == null) {
                index = new Hashtable();
                this._rootToIndexMap.put(root, index);
            } else {
                nodes = (IntegerArray)index.get(id);
            }
            if (nodes == null) {
                nodes = new IntegerArray();
                index.put(id, nodes);
            }
            nodes.add(this._enhancedDOM.getNodeHandle(ident));
        }
        return nodes;
    }

    public void lookupKey(Object value) {
        IntegerArray nodes = (IntegerArray)this._index.get(value);
        this._nodes = nodes != null ? (IntegerArray)nodes.clone() : null;
        this._position = 0;
    }

    @Override
    public int next() {
        if (this._nodes == null) {
            return -1;
        }
        return this._position < this._nodes.cardinality() ? this._dom.getNodeHandle(this._nodes.at(this._position++)) : -1;
    }

    public int containsID(int node, Object value) {
        String string = (String)value;
        int rootHandle = this._dom.getAxisIterator(19).setStartNode(node).next();
        Hashtable index = (Hashtable)this._rootToIndexMap.get(new Integer(rootHandle));
        StringTokenizer values = new StringTokenizer(string, " \n\t");
        while (values.hasMoreElements()) {
            String token = (String)values.nextElement();
            IntegerArray nodes = null;
            if (index != null) {
                nodes = (IntegerArray)index.get(token);
            }
            if (nodes == null && this._enhancedDOM != null && this._enhancedDOM.hasDOMSource()) {
                nodes = this.getDOMNodeById(token);
            }
            if (nodes == null || nodes.indexOf(node) < 0) continue;
            return 1;
        }
        return 0;
    }

    public int containsKey(int node, Object value) {
        int rootHandle = this._dom.getAxisIterator(19).setStartNode(node).next();
        Hashtable index = (Hashtable)this._rootToIndexMap.get(new Integer(rootHandle));
        if (index != null) {
            IntegerArray nodes = (IntegerArray)index.get(value);
            return nodes != null && nodes.indexOf(node) >= 0 ? 1 : 0;
        }
        return 0;
    }

    @Override
    public DTMAxisIterator reset() {
        this._position = 0;
        return this;
    }

    @Override
    public int getLast() {
        return this._nodes == null ? 0 : this._nodes.cardinality();
    }

    @Override
    public int getPosition() {
        return this._position;
    }

    @Override
    public void setMark() {
        this._markedPosition = this._position;
    }

    @Override
    public void gotoMark() {
        this._position = this._markedPosition;
    }

    @Override
    public DTMAxisIterator setStartNode(int start) {
        if (start == -1) {
            this._nodes = null;
        } else if (this._nodes != null) {
            this._position = 0;
        }
        return this;
    }

    @Override
    public int getStartNode() {
        return 0;
    }

    @Override
    public boolean isReverse() {
        return false;
    }

    @Override
    public DTMAxisIterator cloneIterator() {
        KeyIndex other = new KeyIndex(0);
        other._index = this._index;
        other._rootToIndexMap = this._rootToIndexMap;
        other._nodes = this._nodes;
        other._position = this._position;
        return other;
    }

    public void setDom(DOM dom) {
        DOM idom;
        this._dom = dom;
        if (dom instanceof DOMEnhancedForDTM) {
            this._enhancedDOM = (DOMEnhancedForDTM)dom;
        } else if (dom instanceof DOMAdapter && (idom = ((DOMAdapter)dom).getDOMImpl()) instanceof DOMEnhancedForDTM) {
            this._enhancedDOM = (DOMEnhancedForDTM)idom;
        }
    }

    public KeyIndexIterator getKeyIndexIterator(Object keyValue, boolean isKeyCall) {
        if (keyValue instanceof DTMAxisIterator) {
            return this.getKeyIndexIterator((DTMAxisIterator)keyValue, isKeyCall);
        }
        return this.getKeyIndexIterator(BasisLibrary.stringF(keyValue, this._dom), isKeyCall);
    }

    public KeyIndexIterator getKeyIndexIterator(String keyValue, boolean isKeyCall) {
        return new KeyIndexIterator(keyValue, isKeyCall);
    }

    public KeyIndexIterator getKeyIndexIterator(DTMAxisIterator keyValue, boolean isKeyCall) {
        return new KeyIndexIterator(keyValue, isKeyCall);
    }

    public class KeyIndexIterator
    extends MultiValuedNodeHeapIterator {
        private IntegerArray _nodes;
        private DTMAxisIterator _keyValueIterator;
        private String _keyValue;
        private boolean _isKeyIterator;

        KeyIndexIterator(String keyValue, boolean isKeyIterator) {
            this._isKeyIterator = isKeyIterator;
            this._keyValue = keyValue;
        }

        KeyIndexIterator(DTMAxisIterator keyValues, boolean isKeyIterator) {
            this._keyValueIterator = keyValues;
            this._isKeyIterator = isKeyIterator;
        }

        protected IntegerArray lookupNodes(int root, String keyValue) {
            IntegerArray result = null;
            Hashtable index = (Hashtable)KeyIndex.this._rootToIndexMap.get(new Integer(root));
            if (!this._isKeyIterator) {
                StringTokenizer values = new StringTokenizer(keyValue, " \n\t");
                while (values.hasMoreElements()) {
                    String token = (String)values.nextElement();
                    IntegerArray nodes = null;
                    if (index != null) {
                        nodes = (IntegerArray)index.get(token);
                    }
                    if (nodes == null && KeyIndex.this._enhancedDOM != null && KeyIndex.this._enhancedDOM.hasDOMSource()) {
                        nodes = KeyIndex.this.getDOMNodeById(token);
                    }
                    if (nodes == null) continue;
                    if (result == null) {
                        result = (IntegerArray)nodes.clone();
                        continue;
                    }
                    result.merge(nodes);
                }
            } else if (index != null) {
                result = (IntegerArray)index.get(keyValue);
            }
            return result;
        }

        @Override
        public DTMAxisIterator setStartNode(int node) {
            this._startNode = node;
            if (this._keyValueIterator != null) {
                this._keyValueIterator = this._keyValueIterator.setStartNode(node);
            }
            this.init();
            return super.setStartNode(node);
        }

        @Override
        public int next() {
            int nodeHandle = this._nodes != null ? (this._position < this._nodes.cardinality() ? this.returnNode(this._nodes.at(this._position)) : -1) : super.next();
            return nodeHandle;
        }

        @Override
        public DTMAxisIterator reset() {
            if (this._nodes == null) {
                this.init();
            } else {
                super.reset();
            }
            return this.resetPosition();
        }

        @Override
        protected void init() {
            super.init();
            this._position = 0;
            int rootHandle = KeyIndex.this._dom.getAxisIterator(19).setStartNode(this._startNode).next();
            if (this._keyValueIterator == null) {
                this._nodes = this.lookupNodes(rootHandle, this._keyValue);
                if (this._nodes == null) {
                    this._nodes = EMPTY_NODES;
                }
            } else {
                DTMAxisIterator keyValues = this._keyValueIterator.reset();
                boolean retrievedKeyValueIdx = false;
                boolean foundNodes = false;
                this._nodes = null;
                int keyValueNode = keyValues.next();
                while (keyValueNode != -1) {
                    String keyValue = BasisLibrary.stringF(keyValueNode, KeyIndex.this._dom);
                    IntegerArray nodes = this.lookupNodes(rootHandle, keyValue);
                    if (nodes != null) {
                        if (!foundNodes) {
                            this._nodes = nodes;
                            foundNodes = true;
                        } else {
                            if (this._nodes != null) {
                                this.addHeapNode(new KeyIndexHeapNode(this._nodes));
                                this._nodes = null;
                            }
                            this.addHeapNode(new KeyIndexHeapNode(nodes));
                        }
                    }
                    keyValueNode = keyValues.next();
                }
                if (!foundNodes) {
                    this._nodes = EMPTY_NODES;
                }
            }
        }

        @Override
        public int getLast() {
            return this._nodes != null ? this._nodes.cardinality() : super.getLast();
        }

        @Override
        public int getNodeByPosition(int position) {
            int node = -1;
            if (this._nodes != null) {
                if (position > 0) {
                    if (position <= this._nodes.cardinality()) {
                        this._position = position;
                        node = this._nodes.at(position - 1);
                    } else {
                        this._position = this._nodes.cardinality();
                    }
                }
            } else {
                node = super.getNodeByPosition(position);
            }
            return node;
        }

        protected class KeyIndexHeapNode
        extends MultiValuedNodeHeapIterator.HeapNode {
            private IntegerArray _nodes;
            private int _position;
            private int _markPosition;

            KeyIndexHeapNode(IntegerArray nodes) {
                super(KeyIndexIterator.this);
                this._position = 0;
                this._markPosition = -1;
                this._nodes = nodes;
            }

            @Override
            public int step() {
                if (this._position < this._nodes.cardinality()) {
                    this._node = this._nodes.at(this._position);
                    ++this._position;
                } else {
                    this._node = -1;
                }
                return this._node;
            }

            @Override
            public MultiValuedNodeHeapIterator.HeapNode cloneHeapNode() {
                KeyIndexHeapNode clone = (KeyIndexHeapNode)super.cloneHeapNode();
                clone._nodes = this._nodes;
                clone._position = this._position;
                clone._markPosition = this._markPosition;
                return clone;
            }

            @Override
            public void setMark() {
                this._markPosition = this._position;
            }

            @Override
            public void gotoMark() {
                this._position = this._markPosition;
            }

            @Override
            public boolean isLessThan(MultiValuedNodeHeapIterator.HeapNode heapNode) {
                return this._node < heapNode._node;
            }

            @Override
            public MultiValuedNodeHeapIterator.HeapNode setStartNode(int node) {
                return this;
            }

            @Override
            public MultiValuedNodeHeapIterator.HeapNode reset() {
                this._position = 0;
                return this;
            }
        }
    }
}

