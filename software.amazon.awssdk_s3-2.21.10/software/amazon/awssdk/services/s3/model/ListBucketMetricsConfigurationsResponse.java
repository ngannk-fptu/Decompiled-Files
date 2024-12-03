/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.ListTrait
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.Trait
 *  software.amazon.awssdk.core.util.DefaultSdkAutoConstructList
 *  software.amazon.awssdk.core.util.SdkAutoConstructList
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.ListTrait;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.services.s3.model.MetricsConfiguration;
import software.amazon.awssdk.services.s3.model.MetricsConfigurationListCopier;
import software.amazon.awssdk.services.s3.model.S3Response;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class ListBucketMetricsConfigurationsResponse
extends S3Response
implements ToCopyableBuilder<Builder, ListBucketMetricsConfigurationsResponse> {
    private static final SdkField<Boolean> IS_TRUNCATED_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("IsTruncated").getter(ListBucketMetricsConfigurationsResponse.getter(ListBucketMetricsConfigurationsResponse::isTruncated)).setter(ListBucketMetricsConfigurationsResponse.setter(Builder::isTruncated)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("IsTruncated").unmarshallLocationName("IsTruncated").build()}).build();
    private static final SdkField<String> CONTINUATION_TOKEN_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ContinuationToken").getter(ListBucketMetricsConfigurationsResponse.getter(ListBucketMetricsConfigurationsResponse::continuationToken)).setter(ListBucketMetricsConfigurationsResponse.setter(Builder::continuationToken)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ContinuationToken").unmarshallLocationName("ContinuationToken").build()}).build();
    private static final SdkField<String> NEXT_CONTINUATION_TOKEN_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("NextContinuationToken").getter(ListBucketMetricsConfigurationsResponse.getter(ListBucketMetricsConfigurationsResponse::nextContinuationToken)).setter(ListBucketMetricsConfigurationsResponse.setter(Builder::nextContinuationToken)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("NextContinuationToken").unmarshallLocationName("NextContinuationToken").build()}).build();
    private static final SdkField<List<MetricsConfiguration>> METRICS_CONFIGURATION_LIST_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("MetricsConfigurationList").getter(ListBucketMetricsConfigurationsResponse.getter(ListBucketMetricsConfigurationsResponse::metricsConfigurationList)).setter(ListBucketMetricsConfigurationsResponse.setter(Builder::metricsConfigurationList)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("MetricsConfiguration").unmarshallLocationName("MetricsConfiguration").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(MetricsConfiguration::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(IS_TRUNCATED_FIELD, CONTINUATION_TOKEN_FIELD, NEXT_CONTINUATION_TOKEN_FIELD, METRICS_CONFIGURATION_LIST_FIELD));
    private final Boolean isTruncated;
    private final String continuationToken;
    private final String nextContinuationToken;
    private final List<MetricsConfiguration> metricsConfigurationList;

    private ListBucketMetricsConfigurationsResponse(BuilderImpl builder) {
        super(builder);
        this.isTruncated = builder.isTruncated;
        this.continuationToken = builder.continuationToken;
        this.nextContinuationToken = builder.nextContinuationToken;
        this.metricsConfigurationList = builder.metricsConfigurationList;
    }

    public final Boolean isTruncated() {
        return this.isTruncated;
    }

    public final String continuationToken() {
        return this.continuationToken;
    }

    public final String nextContinuationToken() {
        return this.nextContinuationToken;
    }

    public final boolean hasMetricsConfigurationList() {
        return this.metricsConfigurationList != null && !(this.metricsConfigurationList instanceof SdkAutoConstructList);
    }

    public final List<MetricsConfiguration> metricsConfigurationList() {
        return this.metricsConfigurationList;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.isTruncated());
        hashCode = 31 * hashCode + Objects.hashCode(this.continuationToken());
        hashCode = 31 * hashCode + Objects.hashCode(this.nextContinuationToken());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasMetricsConfigurationList() ? this.metricsConfigurationList() : null);
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
        if (!(obj instanceof ListBucketMetricsConfigurationsResponse)) {
            return false;
        }
        ListBucketMetricsConfigurationsResponse other = (ListBucketMetricsConfigurationsResponse)((Object)obj);
        return Objects.equals(this.isTruncated(), other.isTruncated()) && Objects.equals(this.continuationToken(), other.continuationToken()) && Objects.equals(this.nextContinuationToken(), other.nextContinuationToken()) && this.hasMetricsConfigurationList() == other.hasMetricsConfigurationList() && Objects.equals(this.metricsConfigurationList(), other.metricsConfigurationList());
    }

    public final String toString() {
        return ToString.builder((String)"ListBucketMetricsConfigurationsResponse").add("IsTruncated", (Object)this.isTruncated()).add("ContinuationToken", (Object)this.continuationToken()).add("NextContinuationToken", (Object)this.nextContinuationToken()).add("MetricsConfigurationList", this.hasMetricsConfigurationList() ? this.metricsConfigurationList() : null).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "IsTruncated": {
                return Optional.ofNullable(clazz.cast(this.isTruncated()));
            }
            case "ContinuationToken": {
                return Optional.ofNullable(clazz.cast(this.continuationToken()));
            }
            case "NextContinuationToken": {
                return Optional.ofNullable(clazz.cast(this.nextContinuationToken()));
            }
            case "MetricsConfigurationList": {
                return Optional.ofNullable(clazz.cast(this.metricsConfigurationList()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<ListBucketMetricsConfigurationsResponse, T> g) {
        return obj -> g.apply((ListBucketMetricsConfigurationsResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Response.BuilderImpl
    implements Builder {
        private Boolean isTruncated;
        private String continuationToken;
        private String nextContinuationToken;
        private List<MetricsConfiguration> metricsConfigurationList = DefaultSdkAutoConstructList.getInstance();

        private BuilderImpl() {
        }

        private BuilderImpl(ListBucketMetricsConfigurationsResponse model) {
            super(model);
            this.isTruncated(model.isTruncated);
            this.continuationToken(model.continuationToken);
            this.nextContinuationToken(model.nextContinuationToken);
            this.metricsConfigurationList(model.metricsConfigurationList);
        }

        public final Boolean getIsTruncated() {
            return this.isTruncated;
        }

        public final void setIsTruncated(Boolean isTruncated) {
            this.isTruncated = isTruncated;
        }

        @Override
        public final Builder isTruncated(Boolean isTruncated) {
            this.isTruncated = isTruncated;
            return this;
        }

        public final String getContinuationToken() {
            return this.continuationToken;
        }

        public final void setContinuationToken(String continuationToken) {
            this.continuationToken = continuationToken;
        }

        @Override
        public final Builder continuationToken(String continuationToken) {
            this.continuationToken = continuationToken;
            return this;
        }

        public final String getNextContinuationToken() {
            return this.nextContinuationToken;
        }

        public final void setNextContinuationToken(String nextContinuationToken) {
            this.nextContinuationToken = nextContinuationToken;
        }

        @Override
        public final Builder nextContinuationToken(String nextContinuationToken) {
            this.nextContinuationToken = nextContinuationToken;
            return this;
        }

        public final List<MetricsConfiguration.Builder> getMetricsConfigurationList() {
            List<MetricsConfiguration.Builder> result = MetricsConfigurationListCopier.copyToBuilder(this.metricsConfigurationList);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setMetricsConfigurationList(Collection<MetricsConfiguration.BuilderImpl> metricsConfigurationList) {
            this.metricsConfigurationList = MetricsConfigurationListCopier.copyFromBuilder(metricsConfigurationList);
        }

        @Override
        public final Builder metricsConfigurationList(Collection<MetricsConfiguration> metricsConfigurationList) {
            this.metricsConfigurationList = MetricsConfigurationListCopier.copy(metricsConfigurationList);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder metricsConfigurationList(MetricsConfiguration ... metricsConfigurationList) {
            this.metricsConfigurationList(Arrays.asList(metricsConfigurationList));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder metricsConfigurationList(Consumer<MetricsConfiguration.Builder> ... metricsConfigurationList) {
            this.metricsConfigurationList(Stream.of(metricsConfigurationList).map(c -> (MetricsConfiguration)((MetricsConfiguration.Builder)MetricsConfiguration.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        @Override
        public ListBucketMetricsConfigurationsResponse build() {
            return new ListBucketMetricsConfigurationsResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Response.Builder,
    SdkPojo,
    CopyableBuilder<Builder, ListBucketMetricsConfigurationsResponse> {
        public Builder isTruncated(Boolean var1);

        public Builder continuationToken(String var1);

        public Builder nextContinuationToken(String var1);

        public Builder metricsConfigurationList(Collection<MetricsConfiguration> var1);

        public Builder metricsConfigurationList(MetricsConfiguration ... var1);

        public Builder metricsConfigurationList(Consumer<MetricsConfiguration.Builder> ... var1);
    }
}

