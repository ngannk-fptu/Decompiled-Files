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
import software.amazon.awssdk.services.s3.model.AnalyticsConfiguration;
import software.amazon.awssdk.services.s3.model.S3Response;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class GetBucketAnalyticsConfigurationResponse
extends S3Response
implements ToCopyableBuilder<Builder, GetBucketAnalyticsConfigurationResponse> {
    private static final SdkField<AnalyticsConfiguration> ANALYTICS_CONFIGURATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("AnalyticsConfiguration").getter(GetBucketAnalyticsConfigurationResponse.getter(GetBucketAnalyticsConfigurationResponse::analyticsConfiguration)).setter(GetBucketAnalyticsConfigurationResponse.setter(Builder::analyticsConfiguration)).constructor(AnalyticsConfiguration::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("AnalyticsConfiguration").unmarshallLocationName("AnalyticsConfiguration").build(), PayloadTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(ANALYTICS_CONFIGURATION_FIELD));
    private final AnalyticsConfiguration analyticsConfiguration;

    private GetBucketAnalyticsConfigurationResponse(BuilderImpl builder) {
        super(builder);
        this.analyticsConfiguration = builder.analyticsConfiguration;
    }

    public final AnalyticsConfiguration analyticsConfiguration() {
        return this.analyticsConfiguration;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.analyticsConfiguration());
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
        if (!(obj instanceof GetBucketAnalyticsConfigurationResponse)) {
            return false;
        }
        GetBucketAnalyticsConfigurationResponse other = (GetBucketAnalyticsConfigurationResponse)((Object)obj);
        return Objects.equals(this.analyticsConfiguration(), other.analyticsConfiguration());
    }

    public final String toString() {
        return ToString.builder((String)"GetBucketAnalyticsConfigurationResponse").add("AnalyticsConfiguration", (Object)this.analyticsConfiguration()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "AnalyticsConfiguration": {
                return Optional.ofNullable(clazz.cast(this.analyticsConfiguration()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<GetBucketAnalyticsConfigurationResponse, T> g) {
        return obj -> g.apply((GetBucketAnalyticsConfigurationResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Response.BuilderImpl
    implements Builder {
        private AnalyticsConfiguration analyticsConfiguration;

        private BuilderImpl() {
        }

        private BuilderImpl(GetBucketAnalyticsConfigurationResponse model) {
            super(model);
            this.analyticsConfiguration(model.analyticsConfiguration);
        }

        public final AnalyticsConfiguration.Builder getAnalyticsConfiguration() {
            return this.analyticsConfiguration != null ? this.analyticsConfiguration.toBuilder() : null;
        }

        public final void setAnalyticsConfiguration(AnalyticsConfiguration.BuilderImpl analyticsConfiguration) {
            this.analyticsConfiguration = analyticsConfiguration != null ? analyticsConfiguration.build() : null;
        }

        @Override
        public final Builder analyticsConfiguration(AnalyticsConfiguration analyticsConfiguration) {
            this.analyticsConfiguration = analyticsConfiguration;
            return this;
        }

        @Override
        public GetBucketAnalyticsConfigurationResponse build() {
            return new GetBucketAnalyticsConfigurationResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Response.Builder,
    SdkPojo,
    CopyableBuilder<Builder, GetBucketAnalyticsConfigurationResponse> {
        public Builder analyticsConfiguration(AnalyticsConfiguration var1);

        default public Builder analyticsConfiguration(Consumer<AnalyticsConfiguration.Builder> analyticsConfiguration) {
            return this.analyticsConfiguration((AnalyticsConfiguration)((AnalyticsConfiguration.Builder)AnalyticsConfiguration.builder().applyMutation(analyticsConfiguration)).build());
        }
    }
}

