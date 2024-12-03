/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.JsonProcessingException
 *  org.codehaus.jackson.JsonToken
 */
package org.codehaus.jackson.node;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.BaseJsonNode;
import org.codehaus.jackson.node.ContainerNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.MissingNode;
import org.codehaus.jackson.node.TextNode;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ObjectNode
extends ContainerNode {
    protected LinkedHashMap<String, JsonNode> _children = null;

    public ObjectNode(JsonNodeFactory nc) {
        super(nc);
    }

    @Override
    public JsonToken asToken() {
        return JsonToken.START_OBJECT;
    }

    public boolean isObject() {
        return true;
    }

    @Override
    public int size() {
        return this._children == null ? 0 : this._children.size();
    }

    public Iterator<JsonNode> getElements() {
        return this._children == null ? ContainerNode.NoNodesIterator.instance() : this._children.values().iterator();
    }

    @Override
    public JsonNode get(int index) {
        return null;
    }

    @Override
    public JsonNode get(String fieldName) {
        if (this._children != null) {
            return this._children.get(fieldName);
        }
        return null;
    }

    public Iterator<String> getFieldNames() {
        return this._children == null ? ContainerNode.NoStringsIterator.instance() : this._children.keySet().iterator();
    }

    public JsonNode path(int index) {
        return MissingNode.getInstance();
    }

    public JsonNode path(String fieldName) {
        JsonNode n;
        if (this._children != null && (n = this._children.get(fieldName)) != null) {
            return n;
        }
        return MissingNode.getInstance();
    }

    public Iterator<Map.Entry<String, JsonNode>> getFields() {
        if (this._children == null) {
            return NoFieldsIterator.instance;
        }
        return this._children.entrySet().iterator();
    }

    public ObjectNode with(String propertyName) {
        if (this._children == null) {
            this._children = new LinkedHashMap();
        } else {
            JsonNode n = this._children.get(propertyName);
            if (n != null) {
                if (n instanceof ObjectNode) {
                    return (ObjectNode)n;
                }
                throw new UnsupportedOperationException("Property '" + propertyName + "' has value that is not of type ObjectNode (but " + n.getClass().getName() + ")");
            }
        }
        ObjectNode result = this.objectNode();
        this._children.put(propertyName, result);
        return result;
    }

    @Override
    public JsonNode findValue(String fieldName) {
        if (this._children != null) {
            for (Map.Entry<String, JsonNode> entry : this._children.entrySet()) {
                if (fieldName.equals(entry.getKey())) {
                    return entry.getValue();
                }
                JsonNode value = entry.getValue().findValue(fieldName);
                if (value == null) continue;
                return value;
            }
        }
        return null;
    }

    @Override
    public List<JsonNode> findValues(String fieldName, List<JsonNode> foundSoFar) {
        if (this._children != null) {
            for (Map.Entry<String, JsonNode> entry : this._children.entrySet()) {
                if (fieldName.equals(entry.getKey())) {
                    if (foundSoFar == null) {
                        foundSoFar = new ArrayList<JsonNode>();
                    }
                    foundSoFar.add((JsonNode)entry.getValue());
                    continue;
                }
                foundSoFar = entry.getValue().findValues(fieldName, foundSoFar);
            }
        }
        return foundSoFar;
    }

    @Override
    public List<String> findValuesAsText(String fieldName, List<String> foundSoFar) {
        if (this._children != null) {
            for (Map.Entry<String, JsonNode> entry : this._children.entrySet()) {
                if (fieldName.equals(entry.getKey())) {
                    if (foundSoFar == null) {
                        foundSoFar = new ArrayList<String>();
                    }
                    foundSoFar.add(entry.getValue().asText());
                    continue;
                }
                foundSoFar = entry.getValue().findValuesAsText(fieldName, foundSoFar);
            }
        }
        return foundSoFar;
    }

    @Override
    public ObjectNode findParent(String fieldName) {
        if (this._children != null) {
            for (Map.Entry<String, JsonNode> entry : this._children.entrySet()) {
                if (fieldName.equals(entry.getKey())) {
                    return this;
                }
                JsonNode value = entry.getValue().findParent(fieldName);
                if (value == null) continue;
                return (ObjectNode)value;
            }
        }
        return null;
    }

    @Override
    public List<JsonNode> findParents(String fieldName, List<JsonNode> foundSoFar) {
        if (this._children != null) {
            for (Map.Entry<String, JsonNode> entry : this._children.entrySet()) {
                if (fieldName.equals(entry.getKey())) {
                    if (foundSoFar == null) {
                        foundSoFar = new ArrayList<JsonNode>();
                    }
                    foundSoFar.add((JsonNode)this);
                    continue;
                }
                foundSoFar = entry.getValue().findParents(fieldName, foundSoFar);
            }
        }
        return foundSoFar;
    }

    @Override
    public final void serialize(JsonGenerator jg, SerializerProvider provider) throws IOException, JsonProcessingException {
        jg.writeStartObject();
        if (this._children != null) {
            for (Map.Entry<String, JsonNode> en : this._children.entrySet()) {
                jg.writeFieldName(en.getKey());
                ((BaseJsonNode)en.getValue()).serialize(jg, provider);
            }
        }
        jg.writeEndObject();
    }

    @Override
    public void serializeWithType(JsonGenerator jg, SerializerProvider provider, TypeSerializer typeSer) throws IOException, JsonProcessingException {
        typeSer.writeTypePrefixForObject(this, jg);
        if (this._children != null) {
            for (Map.Entry<String, JsonNode> en : this._children.entrySet()) {
                jg.writeFieldName(en.getKey());
                ((BaseJsonNode)en.getValue()).serialize(jg, provider);
            }
        }
        typeSer.writeTypeSuffixForObject(this, jg);
    }

    public JsonNode put(String fieldName, JsonNode value) {
        if (value == null) {
            value = this.nullNode();
        }
        return this._put(fieldName, value);
    }

    public JsonNode remove(String fieldName) {
        if (this._children != null) {
            return (JsonNode)this._children.remove(fieldName);
        }
        return null;
    }

    public ObjectNode remove(Collection<String> fieldNames) {
        if (this._children != null) {
            for (String fieldName : fieldNames) {
                this._children.remove(fieldName);
            }
        }
        return this;
    }

    @Override
    public ObjectNode removeAll() {
        this._children = null;
        return this;
    }

    public JsonNode putAll(Map<String, JsonNode> properties) {
        if (this._children == null) {
            this._children = new LinkedHashMap<String, JsonNode>(properties);
        } else {
            for (Map.Entry<String, JsonNode> en : properties.entrySet()) {
                JsonNode n = en.getValue();
                if (n == null) {
                    n = this.nullNode();
                }
                this._children.put(en.getKey(), n);
            }
        }
        return this;
    }

    public JsonNode putAll(ObjectNode other) {
        int len = other.size();
        if (len > 0) {
            if (this._children == null) {
                this._children = new LinkedHashMap(len);
            }
            other.putContentsTo(this._children);
        }
        return this;
    }

    public ObjectNode retain(Collection<String> fieldNames) {
        if (this._children != null) {
            Iterator<Map.Entry<String, JsonNode>> entries = this._children.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, JsonNode> entry = entries.next();
                if (fieldNames.contains(entry.getKey())) continue;
                entries.remove();
            }
        }
        return this;
    }

    public ObjectNode retain(String ... fieldNames) {
        return this.retain(Arrays.asList(fieldNames));
    }

    public ArrayNode putArray(String fieldName) {
        ArrayNode n = this.arrayNode();
        this._put(fieldName, n);
        return n;
    }

    public ObjectNode putObject(String fieldName) {
        ObjectNode n = this.objectNode();
        this._put(fieldName, n);
        return n;
    }

    public void putPOJO(String fieldName, Object pojo) {
        this._put(fieldName, this.POJONode(pojo));
    }

    public void putNull(String fieldName) {
        this._put(fieldName, this.nullNode());
    }

    public void put(String fieldName, int v) {
        this._put(fieldName, this.numberNode(v));
    }

    public void put(String fieldName, Integer value) {
        if (value == null) {
            this._put(fieldName, this.nullNode());
        } else {
            this._put(fieldName, this.numberNode(value));
        }
    }

    public void put(String fieldName, long v) {
        this._put(fieldName, this.numberNode(v));
    }

    public void put(String fieldName, Long value) {
        if (value == null) {
            this._put(fieldName, this.nullNode());
        } else {
            this._put(fieldName, this.numberNode(value));
        }
    }

    public void put(String fieldName, float v) {
        this._put(fieldName, this.numberNode(v));
    }

    public void put(String fieldName, Float value) {
        if (value == null) {
            this._put(fieldName, this.nullNode());
        } else {
            this._put(fieldName, this.numberNode(value.floatValue()));
        }
    }

    public void put(String fieldName, double v) {
        this._put(fieldName, this.numberNode(v));
    }

    public void put(String fieldName, Double value) {
        if (value == null) {
            this._put(fieldName, this.nullNode());
        } else {
            this._put(fieldName, this.numberNode(value));
        }
    }

    public void put(String fieldName, BigDecimal v) {
        if (v == null) {
            this.putNull(fieldName);
        } else {
            this._put(fieldName, this.numberNode(v));
        }
    }

    public void put(String fieldName, String v) {
        if (v == null) {
            this.putNull(fieldName);
        } else {
            this._put(fieldName, this.textNode(v));
        }
    }

    public void put(String fieldName, boolean v) {
        this._put(fieldName, this.booleanNode(v));
    }

    public void put(String fieldName, Boolean value) {
        if (value == null) {
            this._put(fieldName, this.nullNode());
        } else {
            this._put(fieldName, this.booleanNode(value));
        }
    }

    public void put(String fieldName, byte[] v) {
        if (v == null) {
            this._put(fieldName, this.nullNode());
        } else {
            this._put(fieldName, this.binaryNode(v));
        }
    }

    protected void putContentsTo(Map<String, JsonNode> dst) {
        if (this._children != null) {
            for (Map.Entry<String, JsonNode> en : this._children.entrySet()) {
                dst.put(en.getKey(), en.getValue());
            }
        }
    }

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
        ObjectNode other = (ObjectNode)o;
        if (other.size() != this.size()) {
            return false;
        }
        if (this._children != null) {
            for (Map.Entry<String, JsonNode> en : this._children.entrySet()) {
                String key = en.getKey();
                JsonNode value = en.getValue();
                JsonNode otherValue = other.get(key);
                if (otherValue != null && otherValue.equals((Object)value)) continue;
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        return this._children == null ? -1 : this._children.hashCode();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(32 + (this.size() << 4));
        sb.append("{");
        if (this._children != null) {
            int count = 0;
            for (Map.Entry<String, JsonNode> en : this._children.entrySet()) {
                if (count > 0) {
                    sb.append(",");
                }
                ++count;
                TextNode.appendQuoted(sb, en.getKey());
                sb.append(':');
                sb.append(en.getValue().toString());
            }
        }
        sb.append("}");
        return sb.toString();
    }

    private final JsonNode _put(String fieldName, JsonNode value) {
        if (this._children == null) {
            this._children = new LinkedHashMap();
        }
        return this._children.put(fieldName, value);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static class NoFieldsIterator
    implements Iterator<Map.Entry<String, JsonNode>> {
        static final NoFieldsIterator instance = new NoFieldsIterator();

        private NoFieldsIterator() {
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Map.Entry<String, JsonNode> next() {
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new IllegalStateException();
        }
    }
}

