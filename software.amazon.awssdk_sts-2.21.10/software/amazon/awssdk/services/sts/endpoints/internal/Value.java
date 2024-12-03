/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.protocols.jsoncore.JsonNode
 */
package software.amazon.awssdk.services.sts.endpoints.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.services.sts.endpoints.internal.Identifier;

@SdkInternalApi
public abstract class Value {
    public boolean isNone() {
        return false;
    }

    public String expectString() {
        throw new RuntimeException("Expected string but was: " + this);
    }

    public boolean expectBool() {
        throw new RuntimeException("Expected bool but was: " + this);
    }

    public Record expectRecord() {
        throw new RuntimeException("Expected object but was: " + this);
    }

    public Endpoint expectEndpoint() {
        throw new RuntimeException("Expected endpoint, found " + this);
    }

    public Array expectArray() {
        throw new RuntimeException("Expected array, found " + this);
    }

    public int expectInt() {
        throw new RuntimeException("Expected int, found " + this);
    }

    public static Value fromNode(JsonNode node) {
        if (node.isArray()) {
            return new Array(node.asArray().stream().map(Value::fromNode).collect(Collectors.toList()));
        }
        if (node.isBoolean()) {
            return Value.fromBool(node.asBoolean());
        }
        if (node.isNull()) {
            throw SdkClientException.create((String)"null cannot be used as a literal");
        }
        if (node.isNumber()) {
            return Value.fromInteger(Integer.parseInt(node.asNumber()));
        }
        if (node.isObject()) {
            HashMap<Identifier, Value> out = new HashMap<Identifier, Value>();
            node.asObject().forEach((k, v) -> out.put(Identifier.of(k), Value.fromNode(v)));
            return Value.fromRecord(out);
        }
        if (node.isString()) {
            return Value.fromStr(node.asString());
        }
        throw SdkClientException.create((String)("Unable to create Value from " + node));
    }

    public static Endpoint endpointFromNode(JsonNode source) {
        return Endpoint.fromNode(source);
    }

    public static Str fromStr(String value) {
        return new Str(value);
    }

    public static Int fromInteger(int value) {
        return new Int(value);
    }

    public static Bool fromBool(boolean value) {
        return new Bool(value);
    }

    public static Array fromArray(List<Value> value) {
        return new Array(value);
    }

    public static Record fromRecord(Map<Identifier, Value> value) {
        return new Record(value);
    }

    public static None none() {
        return new None();
    }

    public static class None
    extends Value {
        @Override
        public boolean isNone() {
            return true;
        }
    }

    public static class Endpoint
    extends Value {
        private static final String URL = "url";
        private static final String PROPERTIES = "properties";
        private static final String HEADERS = "headers";
        private final String url;
        private final Map<String, Value> properties;
        private final Map<String, List<String>> headers;

        private Endpoint(Builder b) {
            this.url = b.url;
            this.properties = b.properties;
            this.headers = b.headers;
        }

        public String getUrl() {
            return this.url;
        }

        public Map<String, Value> getProperties() {
            return this.properties;
        }

        public Map<String, List<String>> getHeaders() {
            return this.headers;
        }

        @Override
        public Endpoint expectEndpoint() {
            return this;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Endpoint endpoint = (Endpoint)o;
            if (this.url != null ? !this.url.equals(endpoint.url) : endpoint.url != null) {
                return false;
            }
            if (this.properties != null ? !this.properties.equals(endpoint.properties) : endpoint.properties != null) {
                return false;
            }
            return this.headers != null ? this.headers.equals(endpoint.headers) : endpoint.headers == null;
        }

        public int hashCode() {
            int result = this.url != null ? this.url.hashCode() : 0;
            result = 31 * result + (this.properties != null ? this.properties.hashCode() : 0);
            result = 31 * result + (this.headers != null ? this.headers.hashCode() : 0);
            return result;
        }

        public String toString() {
            return "Endpoint{url='" + this.url + '\'' + ", properties=" + this.properties + ", headers=" + this.headers + '}';
        }

        public static Endpoint fromNode(JsonNode node) {
            JsonNode headersNode;
            Builder b = Endpoint.builder();
            Map objNode = node.asObject();
            b.url(((JsonNode)objNode.get(URL)).asString());
            JsonNode propertiesNode = (JsonNode)objNode.get(PROPERTIES);
            if (propertiesNode != null) {
                propertiesNode.asObject().forEach((k, v) -> b.property((String)k, Value.fromNode(v)));
            }
            if ((headersNode = (JsonNode)objNode.get(HEADERS)) != null) {
                headersNode.asObject().forEach((k, v) -> v.asArray().forEach(e -> b.addHeader((String)k, e.asString())));
            }
            return b.build();
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String url;
            private final Map<String, Value> properties = new HashMap<String, Value>();
            private final Map<String, List<String>> headers = new HashMap<String, List<String>>();

            public Builder url(String url) {
                this.url = url;
                return this;
            }

            public Builder properties(Map<String, Value> properties) {
                this.properties.clear();
                this.properties.putAll(properties);
                return this;
            }

            public Builder property(String name, Value value) {
                this.properties.put(name, value);
                return this;
            }

            public Builder addHeader(String name, String value) {
                List values = this.headers.computeIfAbsent(name, k -> new ArrayList());
                values.add(value);
                return this;
            }

            public Endpoint build() {
                return new Endpoint(this);
            }
        }
    }

    public static class Record
    extends Value {
        private final Map<Identifier, Value> value;

        private Record(Map<Identifier, Value> value) {
            this.value = value;
        }

        public Value get(Identifier id) {
            return this.value.get(id);
        }

        public Map<Identifier, Value> getValue() {
            return this.value;
        }

        public void forEach(BiConsumer<Identifier, Value> fn) {
            this.value.forEach(fn);
        }

        @Override
        public Record expectRecord() {
            return this;
        }

        public String toString() {
            return "Record{value=" + this.value + '}';
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Record record = (Record)o;
            return this.value != null ? this.value.equals(record.value) : record.value == null;
        }

        public int hashCode() {
            return this.value != null ? this.value.hashCode() : 0;
        }
    }

    public static class Array
    extends Value {
        private List<Value> inner;

        private Array(List<Value> inner) {
            this.inner = inner;
        }

        @Override
        public Array expectArray() {
            return this;
        }

        public Value get(int idx) {
            if (this.inner.size() > idx) {
                return this.inner.get(idx);
            }
            return new None();
        }

        public int size() {
            return this.inner.size();
        }

        public String toString() {
            return "Array{inner=" + this.inner + '}';
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Array array = (Array)o;
            return this.inner != null ? this.inner.equals(array.inner) : array.inner == null;
        }

        public int hashCode() {
            return this.inner != null ? this.inner.hashCode() : 0;
        }
    }

    public static class Bool
    extends Value {
        private final boolean value;

        private Bool(boolean value) {
            this.value = value;
        }

        @Override
        public boolean expectBool() {
            return this.value;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Bool bool = (Bool)o;
            return this.value == bool.value;
        }

        public int hashCode() {
            return this.value ? 1 : 0;
        }
    }

    public static class Int
    extends Value {
        private final int value;

        private Int(int value) {
            this.value = value;
        }

        @Override
        public int expectInt() {
            return this.value;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Int anInt = (Int)o;
            return this.value == anInt.value;
        }

        public int hashCode() {
            return this.value;
        }
    }

    public static class Str
    extends Value {
        private final String value;

        private Str(String value) {
            this.value = value;
        }

        @Override
        public String expectString() {
            return this.value;
        }

        public String toString() {
            return "Str{value='" + this.value + '\'' + '}';
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Str str = (Str)o;
            return this.value != null ? this.value.equals(str.value) : str.value == null;
        }

        public int hashCode() {
            return this.value != null ? this.value.hashCode() : 0;
        }
    }
}

