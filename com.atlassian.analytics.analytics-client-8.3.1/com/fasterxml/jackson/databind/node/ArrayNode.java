/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.BaseJsonNode;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.RawValue;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class ArrayNode
extends ContainerNode<ArrayNode>
implements Serializable {
    private static final long serialVersionUID = 1L;
    private final List<JsonNode> _children;

    public ArrayNode(JsonNodeFactory nf) {
        super(nf);
        this._children = new ArrayList<JsonNode>();
    }

    public ArrayNode(JsonNodeFactory nf, int capacity) {
        super(nf);
        this._children = new ArrayList<JsonNode>(capacity);
    }

    public ArrayNode(JsonNodeFactory nf, List<JsonNode> children) {
        super(nf);
        this._children = children;
    }

    @Override
    protected JsonNode _at(JsonPointer ptr) {
        return this.get(ptr.getMatchingIndex());
    }

    public ArrayNode deepCopy() {
        ArrayNode ret = new ArrayNode(this._nodeFactory);
        for (JsonNode element : this._children) {
            ret._children.add((JsonNode)element.deepCopy());
        }
        return ret;
    }

    @Override
    public boolean isEmpty(SerializerProvider serializers) {
        return this._children.isEmpty();
    }

    @Override
    public JsonNodeType getNodeType() {
        return JsonNodeType.ARRAY;
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public JsonToken asToken() {
        return JsonToken.START_ARRAY;
    }

    @Override
    public int size() {
        return this._children.size();
    }

    @Override
    public boolean isEmpty() {
        return this._children.isEmpty();
    }

    @Override
    public Iterator<JsonNode> elements() {
        return this._children.iterator();
    }

    @Override
    public JsonNode get(int index) {
        if (index >= 0 && index < this._children.size()) {
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
        if (index >= 0 && index < this._children.size()) {
            return this._children.get(index);
        }
        return MissingNode.getInstance();
    }

    @Override
    public JsonNode required(int index) {
        if (index >= 0 && index < this._children.size()) {
            return this._children.get(index);
        }
        return (JsonNode)this._reportRequiredViolation("No value at index #%d [0, %d) of `ArrayNode`", index, this._children.size());
    }

    @Override
    public boolean equals(Comparator<JsonNode> comparator, JsonNode o) {
        if (!(o instanceof ArrayNode)) {
            return false;
        }
        ArrayNode other = (ArrayNode)o;
        int len = this._children.size();
        if (other.size() != len) {
            return false;
        }
        List<JsonNode> l1 = this._children;
        List<JsonNode> l2 = other._children;
        for (int i = 0; i < len; ++i) {
            if (l1.get(i).equals(comparator, l2.get(i))) continue;
            return false;
        }
        return true;
    }

    @Override
    public void serialize(JsonGenerator f, SerializerProvider provider) throws IOException {
        List<JsonNode> c = this._children;
        int size = c.size();
        f.writeStartArray(this, size);
        for (int i = 0; i < size; ++i) {
            JsonNode n = c.get(i);
            ((BaseJsonNode)n).serialize(f, provider);
        }
        f.writeEndArray();
    }

    @Override
    public void serializeWithType(JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(this, JsonToken.START_ARRAY));
        for (JsonNode n : this._children) {
            ((BaseJsonNode)n).serialize(g, provider);
        }
        typeSer.writeTypeSuffix(g, typeIdDef);
    }

    @Override
    public JsonNode findValue(String fieldName) {
        for (JsonNode node : this._children) {
            JsonNode value = node.findValue(fieldName);
            if (value == null) continue;
            return value;
        }
        return null;
    }

    @Override
    public List<JsonNode> findValues(String fieldName, List<JsonNode> foundSoFar) {
        for (JsonNode node : this._children) {
            foundSoFar = node.findValues(fieldName, foundSoFar);
        }
        return foundSoFar;
    }

    @Override
    public List<String> findValuesAsText(String fieldName, List<String> foundSoFar) {
        for (JsonNode node : this._children) {
            foundSoFar = node.findValuesAsText(fieldName, foundSoFar);
        }
        return foundSoFar;
    }

    @Override
    public ObjectNode findParent(String fieldName) {
        for (JsonNode node : this._children) {
            JsonNode parent = node.findParent(fieldName);
            if (parent == null) continue;
            return (ObjectNode)parent;
        }
        return null;
    }

    @Override
    public List<JsonNode> findParents(String fieldName, List<JsonNode> foundSoFar) {
        for (JsonNode node : this._children) {
            foundSoFar = node.findParents(fieldName, foundSoFar);
        }
        return foundSoFar;
    }

    public JsonNode set(int index, JsonNode value) {
        if (value == null) {
            value = this.nullNode();
        }
        if (index < 0 || index >= this._children.size()) {
            throw new IndexOutOfBoundsException("Illegal index " + index + ", array size " + this.size());
        }
        return this._children.set(index, value);
    }

    public ArrayNode add(JsonNode value) {
        if (value == null) {
            value = this.nullNode();
        }
        this._add(value);
        return this;
    }

    public ArrayNode addAll(ArrayNode other) {
        this._children.addAll(other._children);
        return this;
    }

    public ArrayNode addAll(Collection<? extends JsonNode> nodes) {
        for (JsonNode jsonNode : nodes) {
            this.add(jsonNode);
        }
        return this;
    }

    public ArrayNode insert(int index, JsonNode value) {
        if (value == null) {
            value = this.nullNode();
        }
        this._insert(index, value);
        return this;
    }

    public JsonNode remove(int index) {
        if (index >= 0 && index < this._children.size()) {
            return this._children.remove(index);
        }
        return null;
    }

    @Override
    public ArrayNode removeAll() {
        this._children.clear();
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

    public ArrayNode addPOJO(Object pojo) {
        return this._add(pojo == null ? this.nullNode() : this.pojoNode(pojo));
    }

    public ArrayNode addRawValue(RawValue raw) {
        return this._add(raw == null ? this.nullNode() : this.rawValueNode(raw));
    }

    public ArrayNode addNull() {
        return this._add(this.nullNode());
    }

    public ArrayNode add(short v) {
        return this._add(this.numberNode(v));
    }

    public ArrayNode add(Short v) {
        return this._add(v == null ? this.nullNode() : this.numberNode((short)v));
    }

    public ArrayNode add(int v) {
        return this._add(this.numberNode(v));
    }

    public ArrayNode add(Integer v) {
        return this._add(v == null ? this.nullNode() : this.numberNode((int)v));
    }

    public ArrayNode add(long v) {
        return this._add(this.numberNode(v));
    }

    public ArrayNode add(Long v) {
        return this._add(v == null ? this.nullNode() : this.numberNode((long)v));
    }

    public ArrayNode add(float v) {
        return this._add(this.numberNode(v));
    }

    public ArrayNode add(Float v) {
        return this._add(v == null ? this.nullNode() : this.numberNode(v.floatValue()));
    }

    public ArrayNode add(double v) {
        return this._add(this.numberNode(v));
    }

    public ArrayNode add(Double v) {
        return this._add(v == null ? this.nullNode() : this.numberNode((double)v));
    }

    public ArrayNode add(BigDecimal v) {
        return this._add(v == null ? this.nullNode() : this.numberNode(v));
    }

    public ArrayNode add(BigInteger v) {
        return this._add(v == null ? this.nullNode() : this.numberNode(v));
    }

    public ArrayNode add(String v) {
        return this._add(v == null ? this.nullNode() : this.textNode(v));
    }

    public ArrayNode add(boolean v) {
        return this._add(this.booleanNode(v));
    }

    public ArrayNode add(Boolean v) {
        return this._add(v == null ? this.nullNode() : this.booleanNode(v));
    }

    public ArrayNode add(byte[] v) {
        return this._add(v == null ? this.nullNode() : this.binaryNode(v));
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

    public ArrayNode insertNull(int index) {
        return this._insert(index, this.nullNode());
    }

    public ArrayNode insertPOJO(int index, Object pojo) {
        return this._insert(index, pojo == null ? this.nullNode() : this.pojoNode(pojo));
    }

    public ArrayNode insertRawValue(int index, RawValue raw) {
        return this._insert(index, raw == null ? this.nullNode() : this.rawValueNode(raw));
    }

    public ArrayNode insert(int index, short v) {
        return this._insert(index, this.numberNode(v));
    }

    public ArrayNode insert(int index, Short value) {
        return this._insert(index, value == null ? this.nullNode() : this.numberNode((short)value));
    }

    public ArrayNode insert(int index, int v) {
        return this._insert(index, this.numberNode(v));
    }

    public ArrayNode insert(int index, Integer v) {
        return this._insert(index, v == null ? this.nullNode() : this.numberNode((int)v));
    }

    public ArrayNode insert(int index, long v) {
        return this._insert(index, this.numberNode(v));
    }

    public ArrayNode insert(int index, Long v) {
        return this._insert(index, v == null ? this.nullNode() : this.numberNode((long)v));
    }

    public ArrayNode insert(int index, float v) {
        return this._insert(index, this.numberNode(v));
    }

    public ArrayNode insert(int index, Float v) {
        return this._insert(index, v == null ? this.nullNode() : this.numberNode(v.floatValue()));
    }

    public ArrayNode insert(int index, double v) {
        return this._insert(index, this.numberNode(v));
    }

    public ArrayNode insert(int index, Double v) {
        return this._insert(index, v == null ? this.nullNode() : this.numberNode((double)v));
    }

    public ArrayNode insert(int index, BigDecimal v) {
        return this._insert(index, v == null ? this.nullNode() : this.numberNode(v));
    }

    public ArrayNode insert(int index, BigInteger v) {
        return this._insert(index, v == null ? this.nullNode() : this.numberNode(v));
    }

    public ArrayNode insert(int index, String v) {
        return this._insert(index, v == null ? this.nullNode() : this.textNode(v));
    }

    public ArrayNode insert(int index, boolean v) {
        return this._insert(index, this.booleanNode(v));
    }

    public ArrayNode insert(int index, Boolean value) {
        if (value == null) {
            return this.insertNull(index);
        }
        return this._insert(index, this.booleanNode(value));
    }

    public ArrayNode insert(int index, byte[] v) {
        if (v == null) {
            return this.insertNull(index);
        }
        return this._insert(index, this.binaryNode(v));
    }

    public ArrayNode setNull(int index) {
        return this._set(index, this.nullNode());
    }

    public ArrayNode setPOJO(int index, Object pojo) {
        return this._set(index, pojo == null ? this.nullNode() : this.pojoNode(pojo));
    }

    public ArrayNode setRawValue(int index, RawValue raw) {
        return this._set(index, raw == null ? this.nullNode() : this.rawValueNode(raw));
    }

    public ArrayNode set(int index, short v) {
        return this._set(index, this.numberNode(v));
    }

    public ArrayNode set(int index, Short v) {
        return this._set(index, v == null ? this.nullNode() : this.numberNode((short)v));
    }

    public ArrayNode set(int index, int v) {
        return this._set(index, this.numberNode(v));
    }

    public ArrayNode set(int index, Integer v) {
        return this._set(index, v == null ? this.nullNode() : this.numberNode((int)v));
    }

    public ArrayNode set(int index, long v) {
        return this._set(index, this.numberNode(v));
    }

    public ArrayNode set(int index, Long v) {
        return this._set(index, v == null ? this.nullNode() : this.numberNode((long)v));
    }

    public ArrayNode set(int index, float v) {
        return this._set(index, this.numberNode(v));
    }

    public ArrayNode set(int index, Float v) {
        return this._set(index, v == null ? this.nullNode() : this.numberNode(v.floatValue()));
    }

    public ArrayNode set(int index, double v) {
        return this._set(index, this.numberNode(v));
    }

    public ArrayNode set(int index, Double v) {
        return this._set(index, v == null ? this.nullNode() : this.numberNode((double)v));
    }

    public ArrayNode set(int index, BigDecimal v) {
        return this._set(index, v == null ? this.nullNode() : this.numberNode(v));
    }

    public ArrayNode set(int index, BigInteger v) {
        return this._set(index, v == null ? this.nullNode() : this.numberNode(v));
    }

    public ArrayNode set(int index, String v) {
        return this._set(index, v == null ? this.nullNode() : this.textNode(v));
    }

    public ArrayNode set(int index, boolean v) {
        return this._set(index, this.booleanNode(v));
    }

    public ArrayNode set(int index, Boolean v) {
        return this._set(index, v == null ? this.nullNode() : this.booleanNode(v));
    }

    public ArrayNode set(int index, byte[] v) {
        return this._set(index, v == null ? this.nullNode() : this.binaryNode(v));
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o instanceof ArrayNode) {
            return this._children.equals(((ArrayNode)o)._children);
        }
        return false;
    }

    protected boolean _childrenEqual(ArrayNode other) {
        return this._children.equals(other._children);
    }

    @Override
    public int hashCode() {
        return this._children.hashCode();
    }

    protected ArrayNode _set(int index, JsonNode node) {
        if (index < 0 || index >= this._children.size()) {
            throw new IndexOutOfBoundsException("Illegal index " + index + ", array size " + this.size());
        }
        this._children.set(index, node);
        return this;
    }

    protected ArrayNode _add(JsonNode node) {
        this._children.add(node);
        return this;
    }

    protected ArrayNode _insert(int index, JsonNode node) {
        if (index < 0) {
            this._children.add(0, node);
        } else if (index >= this._children.size()) {
            this._children.add(node);
        } else {
            this._children.add(index, node);
        }
        return this;
    }
}

