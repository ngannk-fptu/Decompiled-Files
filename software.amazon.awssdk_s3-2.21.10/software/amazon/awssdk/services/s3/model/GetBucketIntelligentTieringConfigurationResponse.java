/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.PayloadTrait
 *  software.amazon.awssdk.core.traits.Trait
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.PayloadTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.IntelligentTieringConfiguration;
import software.amazon.awssdk.services.s3.model.S3Response;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class GetBucketIntelligentTieringConfigurationResponse
extends S3Response
implements ToCopyableBuilder<Builder, GetBucketIntelligentTieringConfigurationResponse> {
    private static final SdkField<IntelligentTieringConfiguration> INTELLIGENT_TIERING_CONFIGURATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("IntelligentTieringConfiguration").getter(GetBucketIntelligentTieringConfigurationResponse.getter(GetBucketIntelligentTieringConfigurationResponse::intelligentTieringConfiguration)).setter(GetBucketIntelligentTieringConfigurationResponse.setter(Builder::intelligentTieringConfiguration)).constructor(IntelligentTieringConfiguration::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("IntelligentTieringConfiguration").unmarshallLocationName("IntelligentTieringConfiguration").build(), PayloadTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(INTELLIGENT_TIERING_CONFIGURATION_FIELD));
    private final IntelligentTieringConfiguration intelligentTieringConfiguration;

    private GetBucketIntelligentTieringConfigurationResponse(BuilderImpl builder) {
        super(builder);
        this.intelligentTieringConfiguration = builder.intelligentTieringConfiguration;
    }

    public final IntelligentTieringConfiguration intelligentTieringConfiguration() {
        return this.intelligentTieringConfiguration;
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
        hashCode = 31 * hashCode + super.hashCode();
        hashCode = 31 * hashCode + Objects.hashCode(this.intelligentTieringConfiguration());
        return hashCode;
    }

    public final boolean equals(Object obj) {
        return super.equals(obj) && this.equalsBySdkFields(obj);
    }

    public final boolean equalsBySdkFields(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof GetBucketIntelligentTieringConfigurationResponse)) {
            return false;
        }
        GetBucketIntelligentTieringConfigurationResponse other = (GetBucketIntelligentTieringConfigurationResponse)((Object)obj);
        return Objects.equals(this.intelligentTieringConfiguration(), other.intelligentTieringConfiguration());
    }

    public final String toString() {
        return ToString.builder((String)"GetBucketIntelligentTieringConfigurationResponse").add("IntelligentTieringConfiguration", (Object)this.intelligentTieringConfiguration()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "IntelligentTieringConfiguration": {
                return Optional.ofNullable(clazz.cast(this.intelligentTieringConfiguration()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<GetBucketIntelligentTieringConfigurationResponse, T> g) {
        return obj -> g.apply((GetBucketIntelligentTieringConfigurationResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Response.BuilderImpl
    implements Builder {
        private IntelligentTieringConfiguration intelligentTieringConfiguration;

        private BuilderImpl() {
        }

        private BuilderImpl(GetBucketIntelligentTieringConfigurationResponse model) {
            super(model);
            this.intelligentTieringConfiguration(model.intelligentTieringConfiguration);
        }

        public final IntelligentTieringConfiguration.Builder getIntelligentTieringConfiguration() {
            return this.intelligentTieringConfiguration != null ? this.intelligentTieringConfiguration.toBuilder() : null;
        }

        public final void setIntelligentTieringConfiguration(IntelligentTieringConfiguration.BuilderImpl intelligentTieringConfiguration) {
            this.intelligentTieringConfiguration = intelligentTieringConfiguration != null ? intelligentTieringConfiguration.build() : null;
        }

        @Override
        public final Builder intelligentTieringConfiguration(IntelligentTieringConfiguration intelligentTieringConfiguration) {
            this.intelligentTieringConfiguration = intelligentTieringConfiguration;
            return this;
        }

        @Override
        public GetBucketIntelligentTieringConfigurationResponse build() {
            return new GetBucketIntelligentTieringConfigurationResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Response.Builder,
    SdkPojo,
    CopyableBuilder<Builder, GetBucketIntelligentTieringConfigurationResponse> {
        public Builder intelligentTieringConfiguration(IntelligentTieringConfiguration var1);

        default public Builder intelligentTieringConfiguration(Consumer<IntelligentTieringConfiguration.Builder> intelligentTieringConfiguration) {
            return this.intelligentTieringConfiguration((IntelligentTieringConfiguration)((IntelligentTieringConfiguration.Builder)IntelligentTieringConfiguration.builder().applyMutation(intelligentTieringConfiguration)).build());
        }
    }
}

