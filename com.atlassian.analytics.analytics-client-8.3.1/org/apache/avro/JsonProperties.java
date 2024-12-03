/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.io.IOException;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.util.MapEntry;
import org.apache.avro.util.internal.Accessor;
import org.apache.avro.util.internal.JacksonUtils;

public abstract class JsonProperties {
    public static final Null NULL_VALUE;
    private ConcurrentMap<String, JsonNode> props = new ConcurrentHashMap<String, JsonNode>(){
        private static final long serialVersionUID = 1L;
        private Queue<MapEntry<String, JsonNode>> propOrder = new ConcurrentLinkedQueue<MapEntry<String, JsonNode>>();

        @Override
        public JsonNode putIfAbsent(String key, JsonNode value) {
            JsonNode r = super.putIfAbsent(key, value);
            if (r == null) {
                this.propOrder.add(new MapEntry<String, JsonNode>(key, value));
            }
            return r;
        }

        @Override
        public JsonNode put(String key, JsonNode value) {
            return this.putIfAbsent(key, value);
        }

        @Override
        public Set<Map.Entry<String, JsonNode>> entrySet() {
            return new AbstractSet<Map.Entry<String, JsonNode>>(){

                @Override
                public Iterator<Map.Entry<String, JsonNode>> iterator() {
                    return new Iterator<Map.Entry<String, JsonNode>>(){
                        Iterator<MapEntry<String, JsonNode>> it;
                        {
                            this.it = propOrder.iterator();
                        }

                        @Override
                        public boolean hasNext() {
                            return this.it.hasNext();
                        }

                        @Override
                        public Map.Entry<String, JsonNode> next() {
                            return this.it.next();
                        }
                    };
                }

                @Override
                public int size() {
                    return propOrder.size();
                }
            };
        }
    };
    private Set<String> reserved;

    JsonProperties(Set<String> reserved) {
        this.reserved = reserved;
    }

    JsonProperties(Set<String> reserved, Map<String, ?> propMap) {
        this.reserved = reserved;
        for (Map.Entry<String, ?> a : propMap.entrySet()) {
            Object v = a.getValue();
            JsonNode json = null;
            json = v instanceof String ? TextNode.valueOf((String)v) : (v instanceof JsonNode ? (JsonNode)v : JacksonUtils.toJsonNode(v));
            this.props.put(a.getKey(), json);
        }
    }

    public String getProp(String name) {
        JsonNode value = this.getJsonProp(name);
        return value != null && value.isTextual() ? value.textValue() : null;
    }

    private JsonNode getJsonProp(String name) {
        return (JsonNode)this.props.get(name);
    }

    public Object getObjectProp(String name) {
        return JacksonUtils.toObject((JsonNode)this.props.get(name));
    }

    public void addProp(String name, String value) {
        this.addProp(name, TextNode.valueOf(value));
    }

    public void addProp(String name, Object value) {
        if (value instanceof JsonNode) {
            this.addProp(name, (JsonNode)value);
        } else {
            this.addProp(name, JacksonUtils.toJsonNode(value));
        }
    }

    public void putAll(JsonProperties np) {
        for (Map.Entry e : np.props.entrySet()) {
            this.addProp((String)e.getKey(), (JsonNode)e.getValue());
        }
    }

    private void addProp(String name, JsonNode value) {
        if (this.reserved.contains(name)) {
            throw new AvroRuntimeException("Can't set reserved property: " + name);
        }
        if (value == null) {
            throw new AvroRuntimeException("Can't set a property to null: " + name);
        }
        JsonNode old = this.props.putIfAbsent(name, value);
        if (old != null && !old.equals(value)) {
            throw new AvroRuntimeException("Can't overwrite property: " + name);
        }
    }

    public void addAllProps(JsonProperties properties) {
        for (Map.Entry entry : properties.props.entrySet()) {
            this.addProp((String)entry.getKey(), (JsonNode)entry.getValue());
        }
    }

    public Map<String, Object> getObjectProps() {
        LinkedHashMap result = new LinkedHashMap();
        for (Map.Entry e : this.props.entrySet()) {
            result.put(e.getKey(), JacksonUtils.toObject((JsonNode)e.getValue()));
        }
        return Collections.unmodifiableMap(result);
    }

    void writeProps(JsonGenerator gen) throws IOException {
        for (Map.Entry e : this.props.entrySet()) {
            gen.writeObjectField((String)e.getKey(), e.getValue());
        }
    }

    int propsHashCode() {
        return this.props.hashCode();
    }

    boolean propsEqual(JsonProperties np) {
        return this.props.equals(np.props);
    }

    public boolean hasProps() {
        return !this.props.isEmpty();
    }

    static {
        Accessor.setAccessor(new Accessor.JsonPropertiesAccessor(){

            @Override
            protected void addProp(JsonProperties props, String name, JsonNode value) {
                props.addProp(name, value);
            }
        });
        NULL_VALUE = new Null();
    }

    public static class Null {
        private Null() {
        }
    }
}

