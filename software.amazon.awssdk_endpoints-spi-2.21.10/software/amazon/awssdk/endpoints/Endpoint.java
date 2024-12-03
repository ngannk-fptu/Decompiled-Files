/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 */
package software.amazon.awssdk.endpoints;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.endpoints.EndpointAttributeKey;

@SdkPublicApi
public final class Endpoint {
    private final URI url;
    private final Map<String, List<String>> headers;
    private final Map<EndpointAttributeKey<?>, Object> attributes;

    private Endpoint(BuilderImpl b) {
        this.url = b.url;
        this.headers = b.headers;
        this.attributes = b.attributes;
    }

    public URI url() {
        return this.url;
    }

    public Map<String, List<String>> headers() {
        return this.headers;
    }

    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    public <T> T attribute(EndpointAttributeKey<T> key) {
        return (T)this.attributes.get(key);
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
        if (this.headers != null ? !this.headers.equals(endpoint.headers) : endpoint.headers != null) {
            return false;
        }
        return this.attributes != null ? this.attributes.equals(endpoint.attributes) : endpoint.attributes == null;
    }

    public int hashCode() {
        int result = this.url != null ? this.url.hashCode() : 0;
        result = 31 * result + (this.headers != null ? this.headers.hashCode() : 0);
        result = 31 * result + (this.attributes != null ? this.attributes.hashCode() : 0);
        return result;
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    private static class BuilderImpl
    implements Builder {
        private URI url;
        private final Map<String, List<String>> headers = new HashMap<String, List<String>>();
        private final Map<EndpointAttributeKey<?>, Object> attributes = new HashMap();

        private BuilderImpl() {
        }

        private BuilderImpl(Endpoint e) {
            this.url = e.url;
            if (e.headers != null) {
                e.headers.forEach((n, v) -> this.headers.put((String)n, new ArrayList(v)));
            }
            this.attributes.putAll(e.attributes);
        }

        @Override
        public Builder url(URI url) {
            this.url = url;
            return this;
        }

        @Override
        public Builder putHeader(String name, String value) {
            List values = this.headers.computeIfAbsent(name, n -> new ArrayList());
            values.add(value);
            return this;
        }

        @Override
        public <T> Builder putAttribute(EndpointAttributeKey<T> key, T value) {
            this.attributes.put(key, value);
            return this;
        }

        @Override
        public Endpoint build() {
            return new Endpoint(this);
        }
    }

    public static interface Builder {
        public Builder url(URI var1);

        public Builder putHeader(String var1, String var2);

        public <T> Builder putAttribute(EndpointAttributeKey<T> var1, T var2);

        public Endpoint build();
    }
}

