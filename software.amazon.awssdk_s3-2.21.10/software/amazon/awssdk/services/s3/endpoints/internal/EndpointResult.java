/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.protocols.jsoncore.JsonNode
 */
package software.amazon.awssdk.services.s3.endpoints.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.services.s3.endpoints.internal.Expr;
import software.amazon.awssdk.services.s3.endpoints.internal.Identifier;
import software.amazon.awssdk.services.s3.endpoints.internal.Literal;

@SdkInternalApi
public final class EndpointResult {
    private static final String URL = "url";
    private static final String PROPERTIES = "properties";
    private static final String HEADERS = "headers";
    private Expr url;
    private Map<Identifier, Expr> properties;
    private Map<String, List<Expr>> headers;

    private EndpointResult(Builder builder) {
        this.url = builder.url;
        this.properties = builder.properties;
        this.headers = builder.headers;
    }

    public Expr getUrl() {
        return this.url;
    }

    public Map<Identifier, Expr> getProperties() {
        return this.properties;
    }

    public Map<String, List<Expr>> getHeaders() {
        return this.headers;
    }

    public static EndpointResult fromNode(JsonNode node) {
        JsonNode headersNode;
        Map objNode = node.asObject();
        Builder b = EndpointResult.builder();
        b.url(Expr.fromNode((JsonNode)objNode.get(URL)));
        JsonNode propertiesNode = (JsonNode)objNode.get(PROPERTIES);
        if (propertiesNode != null) {
            propertiesNode.asObject().forEach((k, v) -> b.addProperty(Identifier.of(k), Literal.fromNode(v)));
        }
        if ((headersNode = (JsonNode)objNode.get(HEADERS)) != null) {
            headersNode.asObject().forEach((k, v) -> b.addHeader((String)k, v.asArray().stream().map(Literal::fromNode).collect(Collectors.toList())));
        }
        return b.build();
    }

    public String toString() {
        return "Endpoint{url=" + this.url + ", properties=" + this.properties + ", headers=" + this.headers + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        EndpointResult endpoint = (EndpointResult)o;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Expr url;
        private final Map<Identifier, Expr> properties = new HashMap<Identifier, Expr>();
        private final Map<String, List<Expr>> headers = new HashMap<String, List<Expr>>();

        public Builder url(Expr url) {
            this.url = url;
            return this;
        }

        public Builder addProperty(Identifier name, Expr value) {
            this.properties.put(name, value);
            return this;
        }

        public Builder addHeader(String name, List<Expr> value) {
            this.headers.put(name, value);
            return this;
        }

        public Builder addHeaderValue(String name, Expr value) {
            List values = this.headers.computeIfAbsent(name, n -> new ArrayList());
            values.add(value);
            return this;
        }

        public EndpointResult build() {
            return new EndpointResult(this);
        }
    }
}

