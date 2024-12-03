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
import software.amazon.awssdk.services.s3.model.MetricsConfiguration;
import software.amazon.awssdk.services.s3.model.S3Response;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class GetBucketMetricsConfigurationResponse
extends S3Response
implements ToCopyableBuilder<Builder, GetBucketMetricsConfigurationResponse> {
    private static final SdkField<MetricsConfiguration> METRICS_CONFIGURATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("MetricsConfiguration").getter(GetBucketMetricsConfigurationResponse.getter(GetBucketMetricsConfigurationResponse::metricsConfiguration)).setter(GetBucketMetricsConfigurationResponse.setter(Builder::metricsConfiguration)).constructor(MetricsConfiguration::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("MetricsConfiguration").unmarshallLocationName("MetricsConfiguration").build(), PayloadTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(METRICS_CONFIGURATION_FIELD));
    private final MetricsConfiguration metricsConfiguration;

    private GetBucketMetricsConfigurationResponse(BuilderImpl builder) {
        super(builder);
        this.metricsConfiguration = builder.metricsConfiguration;
    }

    public final MetricsConfiguration metricsConfiguration() {
        return this.metricsConfiguration;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.metricsConfiguration());
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
        if (!(obj instanceof GetBucketMetricsConfigurationResponse)) {
            return false;
        }
        GetBucketMetricsConfigurationResponse other = (GetBucketMetricsConfigurationResponse)((Object)obj);
        return Objects.equals(this.metricsConfiguration(), other.metricsConfiguration());
    }

    public final String toString() {
        return ToString.builder((String)"GetBucketMetricsConfigurationResponse").add("MetricsConfiguration", (Object)this.metricsConfiguration()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "MetricsConfiguration": {
                return Optional.ofNullable(clazz.cast(this.metricsConfiguration()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<GetBucketMetricsConfigurationResponse, T> g) {
        return obj -> g.apply((GetBucketMetricsConfigurationResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Response.BuilderImpl
    implements Builder {
        private MetricsConfiguration metricsConfiguration;

        private BuilderImpl() {
        }

        private BuilderImpl(GetBucketMetricsConfigurationResponse model) {
            super(model);
            this.metricsConfiguration(model.metricsConfiguration);
        }

        public final MetricsConfiguration.Builder getMetricsConfiguration() {
            return this.metricsConfiguration != null ? this.metricsConfiguration.toBuilder() : null;
        }

        public final void setMetricsConfiguration(MetricsConfiguration.BuilderImpl metricsConfiguration) {
            this.metricsConfiguration = metricsConfiguration != null ? metricsConfiguration.build() : null;
        }

        @Override
        public final Builder metricsConfiguration(MetricsConfiguration metricsConfiguration) {
            this.metricsConfiguration = metricsConfiguration;
            return this;
        }

        @Override
        public GetBucketMetricsConfigurationResponse build() {
            return new GetBucketMetricsConfigurationResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Response.Builder,
    SdkPojo,
    CopyableBuilder<Builder, GetBucketMetricsConfigurationResponse> {
        public Builder metricsConfiguration(MetricsConfiguration var1);

        default public Builder metricsConfiguration(Consumer<MetricsConfiguration.Builder> metricsConfiguration) {
            return this.metricsConfiguration((MetricsConfiguration)((MetricsConfiguration.Builder)MetricsConfiguration.builder().applyMutation(metricsConfiguration)).build());
        }
    }
}

