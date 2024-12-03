/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.utils.ToString
 */
package software.amazon.awssdk.identity.spi.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.identity.spi.IdentityProperty;
import software.amazon.awssdk.identity.spi.ResolveIdentityRequest;
import software.amazon.awssdk.utils.ToString;

@SdkInternalApi
@Immutable
@ThreadSafe
public final class DefaultResolveIdentityRequest
implements ResolveIdentityRequest {
    private final Map<IdentityProperty<?>, Object> properties;

    private DefaultResolveIdentityRequest(BuilderImpl builder) {
        this.properties = new HashMap(builder.properties);
    }

    public static ResolveIdentityRequest.Builder builder() {
        return new BuilderImpl();
    }

    @Override
    public <T> T property(IdentityProperty<T> property) {
        return (T)this.properties.get(property);
    }

    public ResolveIdentityRequest.Builder toBuilder() {
        return new BuilderImpl(this);
    }

    public String toString() {
        return ToString.builder((String)"ResolveIdentityRequest").add("properties", this.properties).build();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DefaultResolveIdentityRequest that = (DefaultResolveIdentityRequest)o;
        return this.properties.equals(that.properties);
    }

    public int hashCode() {
        return Objects.hashCode(this.properties);
    }

    @SdkInternalApi
    public static final class BuilderImpl
    implements ResolveIdentityRequest.Builder {
        private final Map<IdentityProperty<?>, Object> properties = new HashMap();

        private BuilderImpl() {
        }

        private BuilderImpl(DefaultResolveIdentityRequest resolveIdentityRequest) {
            this.properties.putAll(resolveIdentityRequest.properties);
        }

        @Override
        public <T> ResolveIdentityRequest.Builder putProperty(IdentityProperty<T> key, T value) {
            this.properties.put(key, value);
            return this;
        }

        public ResolveIdentityRequest build() {
            return new DefaultResolveIdentityRequest(this);
        }
    }
}

