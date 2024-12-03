/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.node;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.node.BaseJsonNode;
import org.codehaus.jackson.node.ContainerNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.MissingNode;
import org.codehaus.jackson.node.ObjectNode;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ArrayNode
extends ContainerNode {
    protected ArrayList<JsonNode> _children;

    public ArrayNode(JsonNodeFactory nc) {
        super(nc);
    }

    @Override
    public JsonToken asToken() {
        return JsonToken.START_ARRAY;
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public int size() {
        return this._children == null ? 0 : this._children.size();
    }

    @Override
    public Iterator<JsonNode> getElements() {
        return this._children == null ? ContainerNode.NoNodesIterator.instance() : this._children.iterator();
    }

    @Override
    public JsonNode get(int index) {
        if (index >= 0 && this._children != null && index < this._children.size()) {
            return this._children.get(index);
        }
        return null;
    }

    @Override
    public JsonNode get(String fieldName) {
        return null;
    }

    @Override
    public JsonNode path(String fieldName) {
        return MissingNode.getInstance();
    }

    @Override
    public JsonNode path(int index) {
        if (index >= 0 && this._children != null && index < this._children.size()) {
            return this._children.get(index);
        }
        return MissingNode.getInstance();
    }

    @Override
    public final void serialize(JsonGenerator jg, SerializerProvider provider) throws IOException, JsonProcessingException {
        jg.writeStartArray();
        if (this._children != null) {
            for (JsonNode n : this._children) {
                ((BaseJsonNode)n).serialize(jg, provider);
            }
        }
        jg.writeEndArray();
    }

    @Override
    public void serializeWithType(JsonGenerator jg, SerializerProvider provider, TypeSerializer typeSer) throws IOException, JsonProcessingException {
        typeSer.writeTypePrefixForArray(this, jg);
        if (this._children != null) {
            for (JsonNode n : this._children) {
                ((BaseJsonNode)n).serialize(jg, provider);
            }
        }
        typeSer.writeTypeSuffixForArray(this, jg);
    }

    @Override
    public JsonNode findValue(String fieldName) {
        if (this._children != null) {
            for (JsonNode node : this._children) {
                JsonNode value = node.findValue(fieldName);
                if (value == null) continue;
                return value;
            }
        }
        return null;
    }

    @Override
    public List<JsonNode> findValues(String fieldName, List<JsonNode> foundSoFar) {
        if (this._children != null) {
            for (JsonNode node : this._children) {
                foundSoFar = node.findValues(fieldName, foundSoFar);
            }
        }
        return foundSoFar;
    }

    @Override
    public List<String> findValuesAsText(String fieldName, List<String> foundSoFar) {
        if (this._children != null) {
            for (JsonNode node : this._children) {
                foundSoFar = node.findValuesAsText(fieldName, foundSoFar);
            }
        }
        return foundSoFar;
    }

    @Override
    public ObjectNode findParent(String fieldName) {
        if (this._children != null) {
            for (JsonNode node : this._children) {
                JsonNode parent = node.findParent(fieldName);
                if (parent == null) continue;
                return (ObjectNode)parent;
            }
        }
        return null;
    }

    @Override
    public List<JsonNode> findParents(String fieldName, List<JsonNode> foundSoFar) {
        if (this._children != null) {
            for (JsonNode node : this._children) {
                foundSoFar = node.findParents(fieldName, foundSoFar);
            }
        }
        return foundSoFar;
    }

    public JsonNode set(int index, JsonNode value) {
        if (value == null) {
            value = this.nullNode();
        }
        return this._set(index, value);
    }

    public void add(JsonNode value) {
        if (value == null) {
            value = this.nullNode();
        }
        this._add(value);
    }

    public JsonNode addAll(ArrayNode other) {
        int len = other.size();
        if (len > 0) {
            if (this._children == null) {
                this._children = new ArrayList(len + 2);
            }
            other.addContentsTo(this._children);
        }
        return this;
    }

    public JsonNode addAll(Collection<JsonNode> nodes) {
        int len = nodes.size();
        if (len > 0) {
            if (this._children == null) {
                this._children = new ArrayList<JsonNode>(nodes);
            } else {
                this._children.addAll(nodes);
            }
        }
        return this;
    }

    public void insert(int index, JsonNode value) {
        if (value == null) {
            value = this.nullNode();
        }
        this._insert(index, value);
    }

    public JsonNode remove(int index) {
        if (index >= 0 && this._children != null && index < this._children.size()) {
            return this._children.remove(index);
        }
        return null;
    }

    @Override
    public ArrayNode removeAll() {
        this._children = null;
        return this;
    }

    public ArrayNode addArray() {
        ArrayNode n = this.arrayNode();
        this._add(n);
        return n;
    }

    public ObjectNode addObject() {
        ObjectNode n = this.objectNode();
        this._add(n);
        return n;
    }

    public void addPOJO(Object value) {
        if (value == null) {
            this.addNull();
        } else {
            this._add(this.POJONode(value));
        }
    }

    public void addNull() {
        this._add(this.nullNode());
    }

    public void add(int v) {
        this._add(this.numberNode(v));
    }

    public void add(Integer value) {
        if (value == null) {
            this.addNull();
        } else {
            this._add(this.numberNode(value));
        }
    }

    public void add(long v) {
        this._add(this.numberNode(v));
    }

    public void add(Long value) {
        if (value == null) {
            this.addNull();
        } else {
            this._add(this.numberNode(value));
        }
    }

    public void add(float v) {
        this._add(this.numberNode(v));
    }

    public void add(Float value) {
        if (value == null) {
            this.addNull();
        } else {
            this._add(this.numberNode(value.floatValue()));
        }
    }

    public void add(double v) {
        this._add(this.numberNode(v));
    }

    public void add(Double value) {
        if (value == null) {
            this.addNull();
        } else {
            this._add(this.numberNode(value));
        }
    }

    public void add(BigDecimal v) {
        if (v == null) {
            this.addNull();
        } else {
            this._add(this.numberNode(v));
        }
    }

    public void add(String v) {
        if (v == null) {
            this.addNull();
        } else {
            this._add(this.textNode(v));
        }
    }

    public void add(boolean v) {
        this._add(this.booleanNode(v));
    }

    public void add(Boolean value) {
        if (value == null) {
            this.addNull();
        } else {
            this._add(this.booleanNode(value));
        }
    }

    public void add(byte[] v) {
        if (v == null) {
            this.addNull();
        } else {
            this._add(this.binaryNode(v));
        }
    }

    public ArrayNode insertArray(int index) {
        ArrayNode n = this.arrayNode();
        this._insert(index, n);
        return n;
    }

    public ObjectNode insertObject(int index) {
        ObjectNode n = this.objectNode();
        this._insert(index, n);
        return n;
    }

    public void insertPOJO(int index, Object value) {
        if (value == null) {
            this.insertNull(index);
        } else {
            this._insert(index, this.POJONode(value));
        }
    }

    public void insertNull(int index) {
        this._insert(index, this.nullNode());
    }

    public void insert(int index, int v) {
        this._insert(index, this.numberNode(v));
    }

    public void insert(int index, Integer value) {
        if (value == null) {
            this.insertNull(index);
        } else {
            this._insert(index, this.numberNode(value));
        }
    }

    public void insert(int index, long v) {
        this._insert(index, this.numberNode(v));
    }

    public void insert(int index, Long value) {
        if (value == null) {
            this.insertNull(index);
        } else {
            this._insert(index, this.numberNode(value));
        }
    }

    public void insert(int index, float v) {
        this._insert(index, this.numberNode(v));
    }

    public void insert(int index, Float value) {
        if (value == null) {
            this.insertNull(index);
        } else {
            this._insert(index, this.numberNode(value.floatValue()));
        }
    }

    public void insert(int index, double v) {
        this._insert(index, this.numberNode(v));
    }

    public void insert(int index, Double value) {
        if (value == null) {
            this.insertNull(index);
        } else {
            this._insert(index, this.numberNode(value));
        }
    }

    public void insert(int index, BigDecimal v) {
        if (v == null) {
            this.insertNull(index);
        } else {
            this._insert(index, this.numberNode(v));
        }
    }

    public void insert(int index, String v) {
        if (v == null) {
            this.insertNull(index);
        } else {
            this._insert(index, this.textNode(v));
        }
    }

    public void insert(int index, boolean v) {
        this._insert(index, this.booleanNode(v));
    }

    public void insert(int index, Boolean value) {
        if (value == null) {
            this.insertNull(index);
        } else {
            this._insert(index, this.booleanNode(value));
        }
    }

    public void insert(int index, byte[] v) {
        if (v == null) {
            this.insertNull(index);
        } else {
            this._insert(index, this.binaryNode(v));
        }
    }

    protected void addContentsTo(List<JsonNode> dst) {
        if (this._children != null) {
            for (JsonNode n : this._children) {
                dst.add(n);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        ArrayNode other = (ArrayNode)o;
        if (this._children == null || this._children.size() == 0) {
            return other.size() == 0;
        }
        return other._sameChildren(this._children);
    }

    public int hashCode() {
        int hash;
        if (this._children == null) {
            hash = 1;
        } else {
            hash = this._children.size();
            for (JsonNode n : this._children) {
                if (n == null) continue;
                hash ^= n.hashCode();
            }
        }
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(16 + (this.size() << 4));
        sb.append('[');
        if (this._children != null) {
            int len = this._children.size();
            for (int i = 0; i < len; ++i) {
                if (i > 0) {
                    sb.append(',');
                }
                sb.append(this._children.get(i).toString());
            }
        }
        sb.append(']');
        return sb.toString();
    }

    public JsonNode _set(int index, JsonNode value) {
        if (this._children == null || index < 0 || index >= this._children.size()) {
            throw new IndexOutOfBoundsException("Illegal index " + index + ", array size " + this.size());
        }
        return this._children.set(index, value);
    }

    private void _add(JsonNode node) {
        if (this._children == null) {
            this._children = new ArrayList();
        }
        this._children.add(node);
    }

    private void _insert(int index, JsonNode node) {
        if (this._children == null) {
            this._children = new ArrayList();
            this._children.add(node);
            return;
        }
        if (index < 0) {
            this._children.add(0, node);
        } else if (index >= this._children.size()) {
            this._children.add(node);
        } else {
            this._children.add(index, node);
        }
    }

    private boolean _sameChildren(ArrayList<JsonNode> otherChildren) {
        int len = otherChildren.size();
        if (this.size() != len) {
            return false;
        }
        for (int i = 0; i < len; ++i) {
            if (this._children.get(i).equals(otherChildren.get(i))) continue;
            return false;
        }
        return true;
    }
}

