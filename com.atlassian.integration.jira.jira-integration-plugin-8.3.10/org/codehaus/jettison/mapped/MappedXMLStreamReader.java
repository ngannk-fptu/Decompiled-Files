/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jettison.mapped;

import java.util.Set;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import org.codehaus.jettison.AbstractXMLStreamReader;
import org.codehaus.jettison.Node;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.util.FastStack;

public class MappedXMLStreamReader
extends AbstractXMLStreamReader {
    private FastStack nodes;
    private String currentValue;
    private MappedNamespaceConvention convention;
    private String valueKey = "$";
    private NamespaceContext ctx;
    private int popArrayNodes;

    public MappedXMLStreamReader(JSONObject obj) throws JSONException, XMLStreamException {
        this(obj, new MappedNamespaceConvention());
    }

    public MappedXMLStreamReader(JSONObject obj, MappedNamespaceConvention con) throws JSONException, XMLStreamException {
        String rootName = (String)obj.keys().next();
        this.convention = con;
        this.nodes = new FastStack();
        this.ctx = con;
        Object top = obj.get(rootName);
        if (top instanceof JSONObject) {
            this.node = new Node(null, rootName, (JSONObject)top, this.convention);
        } else if (top instanceof JSONArray && (((JSONArray)top).length() != 1 || !((JSONArray)top).get(0).equals(""))) {
            this.node = con.isRootElementArrayWrapper() ? new Node(null, rootName, obj, this.convention) : new Node(null, rootName, ((JSONArray)top).getJSONObject(0), this.convention);
        } else {
            this.node = new Node(rootName, this.convention);
            this.convention.processAttributesAndNamespaces(this.node, obj);
            this.currentValue = JSONObject.NULL.equals(top) ? null : top.toString();
        }
        this.nodes.push(this.node);
        this.event = 7;
    }

    @Override
    public int next() throws XMLStreamException {
        if (this.event == 7) {
            this.event = 1;
        } else if (this.event == 4) {
            this.event = 2;
            this.node = (Node)this.nodes.pop();
            this.currentValue = null;
        } else if (this.event == 1 || this.event == 2) {
            if (this.event == 2 && this.nodes.size() > 0) {
                this.node = (Node)this.nodes.peek();
                if (this.popArrayNodes > 0) {
                    this.nodes.pop();
                    if (this.node.getArray() != null) {
                        --this.popArrayNodes;
                        this.event = 2;
                        return this.event;
                    }
                }
            }
            if (this.currentValue != null) {
                this.event = 4;
            } else if (this.node.getKeys() != null && this.node.getKeys().hasNext() || this.node.getArray() != null) {
                this.processElement();
            } else if (this.nodes.size() > 0) {
                this.event = 2;
                this.node = (Node)this.nodes.pop();
            } else {
                this.event = 8;
            }
        }
        if (this.nodes.size() > 0) {
            Node next = (Node)this.nodes.peek();
            if (this.event == 1 && next.getName().getLocalPart().equals(this.valueKey)) {
                this.event = 4;
                this.node = (Node)this.nodes.pop();
            }
        }
        return this.event;
    }

    private void processElement() throws XMLStreamException {
        try {
            Object newObj = null;
            String nextKey = null;
            if (this.node.getArray() != null) {
                int index = this.node.getArrayIndex();
                if (index >= this.node.getArray().length()) {
                    this.nodes.pop();
                    this.node = (Node)this.nodes.peek();
                    if (this.node == null) {
                        this.event = 8;
                        return;
                    }
                    if (this.node.getKeys() != null && this.node.getKeys().hasNext() || this.node.getArray() != null) {
                        if (this.popArrayNodes > 0) {
                            this.node = (Node)this.nodes.pop();
                        }
                        this.processElement();
                    } else {
                        this.event = 2;
                        this.node = (Node)this.nodes.pop();
                    }
                    return;
                }
                newObj = this.node.getArray().get(index++);
                nextKey = this.node.getName().getLocalPart();
                if (!"".equals(this.node.getName().getNamespaceURI())) {
                    nextKey = this.convention.getPrefix(this.node.getName().getNamespaceURI()) + this.getConvention().getNamespaceSeparator() + nextKey;
                }
                this.node.setArrayIndex(index);
            } else {
                nextKey = (String)this.node.getKeys().next();
                newObj = this.node.getObject().get(nextKey);
            }
            if (newObj instanceof String) {
                this.node = new Node(nextKey, this.convention);
                this.nodes.push(this.node);
                this.currentValue = (String)newObj;
                this.event = 1;
                return;
            }
            if (newObj instanceof JSONArray) {
                JSONArray array = (JSONArray)newObj;
                if (!this.processUniformArrayIfPossible(nextKey, array)) {
                    this.node = new Node(nextKey, this.convention);
                    this.node.setArray(array);
                    this.node.setArrayIndex(0);
                    this.nodes.push(this.node);
                    this.processElement();
                }
                return;
            }
            if (newObj instanceof JSONObject) {
                this.node = new Node((Node)this.nodes.peek(), nextKey, (JSONObject)newObj, this.convention);
                this.nodes.push(this.node);
                this.event = 1;
                return;
            }
            this.node = new Node(nextKey, this.convention);
            this.nodes.push(this.node);
            this.currentValue = JSONObject.NULL.equals(newObj) ? null : newObj.toString();
            this.event = 1;
            return;
        }
        catch (JSONException e) {
            throw new XMLStreamException(e);
        }
    }

    private boolean processUniformArrayIfPossible(String arrayKey, JSONArray array) throws JSONException, XMLStreamException {
        int i;
        if (!this.isAvoidArraySpecificEvents(arrayKey)) {
            return false;
        }
        int arrayLength = array.length();
        int depth = 0;
        String lastKey = null;
        int parentIndex = this.nodes.size();
        boolean isRoot = ((Node)this.nodes.get(0)).getName().getLocalPart().equals(arrayKey);
        Node parent = !isRoot ? new Node(arrayKey, this.convention) : this.node;
        for (i = arrayLength - 1; i >= 0; --i) {
            JSONObject jsonObject;
            Object object = array.get(i);
            if (!(object instanceof JSONObject) || (jsonObject = (JSONObject)object).length() != 1) continue;
            String theKey = jsonObject.keys().next().toString();
            if (lastKey == null || lastKey.equals(theKey)) {
                lastKey = theKey;
                ++depth;
                Node theNode = new Node(parent, theKey, jsonObject, this.convention);
                this.nodes.push(theNode);
                continue;
            }
            lastKey = null;
            break;
        }
        if (lastKey == null) {
            for (i = 0; i < depth; ++i) {
                this.nodes.pop();
            }
            return false;
        }
        parent.setArray(array);
        parent.setArrayIndex(arrayLength);
        if (!isRoot) {
            this.nodes.add(parentIndex, parent);
            this.nodes.push(parent);
            this.node = parent;
            this.event = 1;
        } else {
            this.node = (Node)this.nodes.pop();
            this.processElement();
        }
        ++this.popArrayNodes;
        return true;
    }

    @Override
    public void close() throws XMLStreamException {
    }

    @Override
    public String getElementText() throws XMLStreamException {
        this.event = 4;
        return this.currentValue;
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return this.ctx;
    }

    @Override
    public String getText() {
        if (this.currentValue != null && "null".equals(this.currentValue) && !this.convention.isReadNullAsString()) {
            return null;
        }
        return this.currentValue;
    }

    public void setValueKey(String valueKey) {
        this.valueKey = valueKey;
    }

    public boolean isAvoidArraySpecificEvents(String key) {
        Set<?> keys = this.convention.getPrimitiveArrayKeys();
        return keys != null && keys.contains(key);
    }

    public MappedNamespaceConvention getConvention() {
        return this.convention;
    }
}

