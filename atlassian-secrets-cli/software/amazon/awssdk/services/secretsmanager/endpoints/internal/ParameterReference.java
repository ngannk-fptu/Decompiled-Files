/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.endpoints.internal;

import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.ToParameterReference;

@SdkInternalApi
public final class ParameterReference
implements ToParameterReference {
    private final String name;
    private final String context;

    private ParameterReference(Builder builder) {
        this.name = builder.name;
        this.context = builder.context;
    }

    public String getName() {
        return this.name;
    }

    public Optional<String> getContext() {
        return Optional.ofNullable(this.context);
    }

    public static ParameterReference from(String reference) {
        String[] split = reference.split("\\.", 2);
        return ParameterReference.from(split[0], split.length == 2 ? split[1] : null);
    }

    public static ParameterReference from(String name, String context) {
        Builder builder = ParameterReference.builder().name(name);
        if (context != null) {
            builder.context(context);
        }
        return builder.build();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public ParameterReference toParameterReference() {
        return this;
    }

    public String toString() {
        if (this.context == null) {
            return this.name;
        }
        return this.name + "." + this.context;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ParameterReference that = (ParameterReference)o;
        return this.getName().equals(that.getName()) && Objects.equals(this.getContext(), that.getContext());
    }

    public int hashCode() {
        int result = this.name != null ? this.name.hashCode() : 0;
        result = 31 * result + (this.context != null ? this.context.hashCode() : 0);
        return result;
    }

    public static class Builder {
        private String name;
        private String context;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder context(String context) {
            this.context = context;
            return this;
        }

        public ParameterReference build() {
            return new ParameterReference(this);
        }
    }
}

