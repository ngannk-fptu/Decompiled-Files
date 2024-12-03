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
package software.amazon.awssdk.services.s3.model;

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

public final class PublicAccessBlockConfiguration
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, PublicAccessBlockConfiguration> {
    private static final SdkField<Boolean> BLOCK_PUBLIC_ACLS_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("BlockPublicAcls").getter(PublicAccessBlockConfiguration.getter(PublicAccessBlockConfiguration::blockPublicAcls)).setter(PublicAccessBlockConfiguration.setter(Builder::blockPublicAcls)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("BlockPublicAcls").unmarshallLocationName("BlockPublicAcls").build()}).build();
    private static final SdkField<Boolean> IGNORE_PUBLIC_ACLS_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("IgnorePublicAcls").getter(PublicAccessBlockConfiguration.getter(PublicAccessBlockConfiguration::ignorePublicAcls)).setter(PublicAccessBlockConfiguration.setter(Builder::ignorePublicAcls)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("IgnorePublicAcls").unmarshallLocationName("IgnorePublicAcls").build()}).build();
    private static final SdkField<Boolean> BLOCK_PUBLIC_POLICY_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("BlockPublicPolicy").getter(PublicAccessBlockConfiguration.getter(PublicAccessBlockConfiguration::blockPublicPolicy)).setter(PublicAccessBlockConfiguration.setter(Builder::blockPublicPolicy)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("BlockPublicPolicy").unmarshallLocationName("BlockPublicPolicy").build()}).build();
    private static final SdkField<Boolean> RESTRICT_PUBLIC_BUCKETS_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("RestrictPublicBuckets").getter(PublicAccessBlockConfiguration.getter(PublicAccessBlockConfiguration::restrictPublicBuckets)).setter(PublicAccessBlockConfiguration.setter(Builder::restrictPublicBuckets)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("RestrictPublicBuckets").unmarshallLocationName("RestrictPublicBuckets").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(BLOCK_PUBLIC_ACLS_FIELD, IGNORE_PUBLIC_ACLS_FIELD, BLOCK_PUBLIC_POLICY_FIELD, RESTRICT_PUBLIC_BUCKETS_FIELD));
    private static final long serialVersionUID = 1L;
    private final Boolean blockPublicAcls;
    private final Boolean ignorePublicAcls;
    private final Boolean blockPublicPolicy;
    private final Boolean restrictPublicBuckets;

    private PublicAccessBlockConfiguration(BuilderImpl builder) {
        this.blockPublicAcls = builder.blockPublicAcls;
        this.ignorePublicAcls = builder.ignorePublicAcls;
        this.blockPublicPolicy = builder.blockPublicPolicy;
        this.restrictPublicBuckets = builder.restrictPublicBuckets;
    }

    public final Boolean blockPublicAcls() {
        return this.blockPublicAcls;
    }

    public final Boolean ignorePublicAcls() {
        return this.ignorePublicAcls;
    }

    public final Boolean blockPublicPolicy() {
        return this.blockPublicPolicy;
    }

    public final Boolean restrictPublicBuckets() {
        return this.restrictPublicBuckets;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.blockPublicAcls());
        hashCode = 31 * hashCode + Objects.hashCode(this.ignorePublicAcls());
        hashCode = 31 * hashCode + Objects.hashCode(this.blockPublicPolicy());
        hashCode = 31 * hashCode + Objects.hashCode(this.restrictPublicBuckets());
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
        if (!(obj instanceof PublicAccessBlockConfiguration)) {
            return false;
        }
        PublicAccessBlockConfiguration other = (PublicAccessBlockConfiguration)obj;
        return Objects.equals(this.blockPublicAcls(), other.blockPublicAcls()) && Objects.equals(this.ignorePublicAcls(), other.ignorePublicAcls()) && Objects.equals(this.blockPublicPolicy(), other.blockPublicPolicy()) && Objects.equals(this.restrictPublicBuckets(), other.restrictPublicBuckets());
    }

    public final String toString() {
        return ToString.builder((String)"PublicAccessBlockConfiguration").add("BlockPublicAcls", (Object)this.blockPublicAcls()).add("IgnorePublicAcls", (Object)this.ignorePublicAcls()).add("BlockPublicPolicy", (Object)this.blockPublicPolicy()).add("RestrictPublicBuckets", (Object)this.restrictPublicBuckets()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "BlockPublicAcls": {
                return Optional.ofNullable(clazz.cast(this.blockPublicAcls()));
            }
            case "IgnorePublicAcls": {
                return Optional.ofNullable(clazz.cast(this.ignorePublicAcls()));
            }
            case "BlockPublicPolicy": {
                return Optional.ofNullable(clazz.cast(this.blockPublicPolicy()));
            }
            case "RestrictPublicBuckets": {
                return Optional.ofNullable(clazz.cast(this.restrictPublicBuckets()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<PublicAccessBlockConfiguration, T> g) {
        return obj -> g.apply((PublicAccessBlockConfiguration)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private Boolean blockPublicAcls;
        private Boolean ignorePublicAcls;
        private Boolean blockPublicPolicy;
        private Boolean restrictPublicBuckets;

        private BuilderImpl() {
        }

        private BuilderImpl(PublicAccessBlockConfiguration model) {
            this.blockPublicAcls(model.blockPublicAcls);
            this.ignorePublicAcls(model.ignorePublicAcls);
            this.blockPublicPolicy(model.blockPublicPolicy);
            this.restrictPublicBuckets(model.restrictPublicBuckets);
        }

        public final Boolean getBlockPublicAcls() {
            return this.blockPublicAcls;
        }

        public final void setBlockPublicAcls(Boolean blockPublicAcls) {
            this.blockPublicAcls = blockPublicAcls;
        }

        @Override
        public final Builder blockPublicAcls(Boolean blockPublicAcls) {
            this.blockPublicAcls = blockPublicAcls;
            return this;
        }

        public final Boolean getIgnorePublicAcls() {
            return this.ignorePublicAcls;
        }

        public final void setIgnorePublicAcls(Boolean ignorePublicAcls) {
            this.ignorePublicAcls = ignorePublicAcls;
        }

        @Override
        public final Builder ignorePublicAcls(Boolean ignorePublicAcls) {
            this.ignorePublicAcls = ignorePublicAcls;
            return this;
        }

        public final Boolean getBlockPublicPolicy() {
            return this.blockPublicPolicy;
        }

        public final void setBlockPublicPolicy(Boolean blockPublicPolicy) {
            this.blockPublicPolicy = blockPublicPolicy;
        }

        @Override
        public final Builder blockPublicPolicy(Boolean blockPublicPolicy) {
            this.blockPublicPolicy = blockPublicPolicy;
            return this;
        }

        public final Boolean getRestrictPublicBuckets() {
            return this.restrictPublicBuckets;
        }

        public final void setRestrictPublicBuckets(Boolean restrictPublicBuckets) {
            this.restrictPublicBuckets = restrictPublicBuckets;
        }

        @Override
        public final Builder restrictPublicBuckets(Boolean restrictPublicBuckets) {
            this.restrictPublicBuckets = restrictPublicBuckets;
            return this;
        }

        public PublicAccessBlockConfiguration build() {
            return new PublicAccessBlockConfiguration(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, PublicAccessBlockConfiguration> {
        public Builder blockPublicAcls(Boolean var1);

        public Builder ignorePublicAcls(Boolean var1);

        public Builder blockPublicPolicy(Boolean var1);

        public Builder restrictPublicBuckets(Boolean var1);
    }
}

