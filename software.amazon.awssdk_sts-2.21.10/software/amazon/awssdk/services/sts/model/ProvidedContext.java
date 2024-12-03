/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.Trait
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.sts.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class ProvidedContext
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, ProvidedContext> {
    private static final SdkField<String> PROVIDER_ARN_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ProviderArn").getter(ProvidedContext.getter(ProvidedContext::providerArn)).setter(ProvidedContext.setter(Builder::providerArn)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ProviderArn").build()}).build();
    private static final SdkField<String> CONTEXT_ASSERTION_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ContextAssertion").getter(ProvidedContext.getter(ProvidedContext::contextAssertion)).setter(ProvidedContext.setter(Builder::contextAssertion)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ContextAssertion").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(PROVIDER_ARN_FIELD, CONTEXT_ASSERTION_FIELD));
    private static final long serialVersionUID = 1L;
    private final String providerArn;
    private final String contextAssertion;

    private ProvidedContext(BuilderImpl builder) {
        this.providerArn = builder.providerArn;
        this.contextAssertion = builder.contextAssertion;
    }

    public final String providerArn() {
        return this.providerArn;
    }

    public final String contextAssertion() {
        return this.contextAssertion;
    }

    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public static Class<? extends Builder> serializableBuilderClass() {
        return BuilderImpl.class;
    }

    public final int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + Objects.hashCode(this.providerArn());
        hashCode = 31 * hashCode + Objects.hashCode(this.contextAssertion());
        return hashCode;
    }

    public final boolean equals(Object obj) {
        return this.equalsBySdkFields(obj);
    }

    public final boolean equalsBySdkFields(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ProvidedContext)) {
            return false;
        }
        ProvidedContext other = (ProvidedContext)obj;
        return Objects.equals(this.providerArn(), other.providerArn()) && Objects.equals(this.contextAssertion(), other.contextAssertion());
    }

    public final String toString() {
        return ToString.builder((String)"ProvidedContext").add("ProviderArn", (Object)this.providerArn()).add("ContextAssertion", (Object)this.contextAssertion()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "ProviderArn": {
                return Optional.ofNullable(clazz.cast(this.providerArn()));
            }
            case "ContextAssertion": {
                return Optional.ofNullable(clazz.cast(this.contextAssertion()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<ProvidedContext, T> g) {
        return obj -> g.apply((ProvidedContext)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String providerArn;
        private String contextAssertion;

        private BuilderImpl() {
        }

        private BuilderImpl(ProvidedContext model) {
            this.providerArn(model.providerArn);
            this.contextAssertion(model.contextAssertion);
        }

        public final String getProviderArn() {
            return this.providerArn;
        }

        public final void setProviderArn(String providerArn) {
            this.providerArn = providerArn;
        }

        @Override
        public final Builder providerArn(String providerArn) {
            this.providerArn = providerArn;
            return this;
        }

        public final String getContextAssertion() {
            return this.contextAssertion;
        }

        public final void setContextAssertion(String contextAssertion) {
            this.contextAssertion = contextAssertion;
        }

        @Override
        public final Builder contextAssertion(String contextAssertion) {
            this.contextAssertion = contextAssertion;
            return this;
        }

        public ProvidedContext build() {
            return new ProvidedContext(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, ProvidedContext> {
        public Builder providerArn(String var1);

        public Builder contextAssertion(String var1);
    }
}

