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
import software.amazon.awssdk.services.s3.model.AnalyticsConfiguration;
import software.amazon.awssdk.services.s3.model.AnalyticsConfigurationListCopier;
import software.amazon.awssdk.services.s3.model.S3Response;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class ListBucketAnalyticsConfigurationsResponse
extends S3Response
implements ToCopyableBuilder<Builder, ListBucketAnalyticsConfigurationsResponse> {
    private static final SdkField<Boolean> IS_TRUNCATED_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("IsTruncated").getter(ListBucketAnalyticsConfigurationsResponse.getter(ListBucketAnalyticsConfigurationsResponse::isTruncated)).setter(ListBucketAnalyticsConfigurationsResponse.setter(Builder::isTruncated)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("IsTruncated").unmarshallLocationName("IsTruncated").build()}).build();
    private static final SdkField<String> CONTINUATION_TOKEN_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ContinuationToken").getter(ListBucketAnalyticsConfigurationsResponse.getter(ListBucketAnalyticsConfigurationsResponse::continuationToken)).setter(ListBucketAnalyticsConfigurationsResponse.setter(Builder::continuationToken)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ContinuationToken").unmarshallLocationName("ContinuationToken").build()}).build();
    private static final SdkField<String> NEXT_CONTINUATION_TOKEN_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("NextContinuationToken").getter(ListBucketAnalyticsConfigurationsResponse.getter(ListBucketAnalyticsConfigurationsResponse::nextContinuationToken)).setter(ListBucketAnalyticsConfigurationsResponse.setter(Builder::nextContinuationToken)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("NextContinuationToken").unmarshallLocationName("NextContinuationToken").build()}).build();
    private static final SdkField<List<AnalyticsConfiguration>> ANALYTICS_CONFIGURATION_LIST_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("AnalyticsConfigurationList").getter(ListBucketAnalyticsConfigurationsResponse.getter(ListBucketAnalyticsConfigurationsResponse::analyticsConfigurationList)).setter(ListBucketAnalyticsConfigurationsResponse.setter(Builder::analyticsConfigurationList)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("AnalyticsConfiguration").unmarshallLocationName("AnalyticsConfiguration").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(AnalyticsConfiguration::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(IS_TRUNCATED_FIELD, CONTINUATION_TOKEN_FIELD, NEXT_CONTINUATION_TOKEN_FIELD, ANALYTICS_CONFIGURATION_LIST_FIELD));
    private final Boolean isTruncated;
    private final String continuationToken;
    private final String nextContinuationToken;
    private final List<AnalyticsConfiguration> analyticsConfigurationList;

    private ListBucketAnalyticsConfigurationsResponse(BuilderImpl builder) {
        super(builder);
        this.isTruncated = builder.isTruncated;
        this.continuationToken = builder.continuationToken;
        this.nextContinuationToken = builder.nextContinuationToken;
        this.analyticsConfigurationList = builder.analyticsConfigurationList;
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

    public final boolean hasAnalyticsConfigurationList() {
        return this.analyticsConfigurationList != null && !(this.analyticsConfigurationList instanceof SdkAutoConstructList);
    }

    public final List<AnalyticsConfiguration> analyticsConfigurationList() {
        return this.analyticsConfigurationList;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.hasAnalyticsConfigurationList() ? this.analyticsConfigurationList() : null);
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
        if (!(obj instanceof ListBucketAnalyticsConfigurationsResponse)) {
            return false;
        }
        ListBucketAnalyticsConfigurationsResponse other = (ListBucketAnalyticsConfigurationsResponse)((Object)obj);
        return Objects.equals(this.isTruncated(), other.isTruncated()) && Objects.equals(this.continuationToken(), other.continuationToken()) && Objects.equals(this.nextContinuationToken(), other.nextContinuationToken()) && this.hasAnalyticsConfigurationList() == other.hasAnalyticsConfigurationList() && Objects.equals(this.analyticsConfigurationList(), other.analyticsConfigurationList());
    }

    public final String toString() {
        return ToString.builder((String)"ListBucketAnalyticsConfigurationsResponse").add("IsTruncated", (Object)this.isTruncated()).add("ContinuationToken", (Object)this.continuationToken()).add("NextContinuationToken", (Object)this.nextContinuationToken()).add("AnalyticsConfigurationList", this.hasAnalyticsConfigurationList() ? this.analyticsConfigurationList() : null).build();
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
            case "AnalyticsConfigurationList": {
                return Optional.ofNullable(clazz.cast(this.analyticsConfigurationList()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<ListBucketAnalyticsConfigurationsResponse, T> g) {
        return obj -> g.apply((ListBucketAnalyticsConfigurationsResponse)((Object)((Object)obj)));
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
        private List<AnalyticsConfiguration> analyticsConfigurationList = DefaultSdkAutoConstructList.getInstance();

        private BuilderImpl() {
        }

        private BuilderImpl(ListBucketAnalyticsConfigurationsResponse model) {
            super(model);
            this.isTruncated(model.isTruncated);
            this.continuationToken(model.continuationToken);
            this.nextContinuationToken(model.nextContinuationToken);
            this.analyticsConfigurationList(model.analyticsConfigurationList);
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

        public final List<AnalyticsConfiguration.Builder> getAnalyticsConfigurationList() {
            List<AnalyticsConfiguration.Builder> result = AnalyticsConfigurationListCopier.copyToBuilder(this.analyticsConfigurationList);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setAnalyticsConfigurationList(Collection<AnalyticsConfiguration.BuilderImpl> analyticsConfigurationList) {
            this.analyticsConfigurationList = AnalyticsConfigurationListCopier.copyFromBuilder(analyticsConfigurationList);
        }

        @Override
        public final Builder analyticsConfigurationList(Collection<AnalyticsConfiguration> analyticsConfigurationList) {
            this.analyticsConfigurationList = AnalyticsConfigurationListCopier.copy(analyticsConfigurationList);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder analyticsConfigurationList(AnalyticsConfiguration ... analyticsConfigurationList) {
            this.analyticsConfigurationList(Arrays.asList(analyticsConfigurationList));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder analyticsConfigurationList(Consumer<AnalyticsConfiguration.Builder> ... analyticsConfigurationList) {
            this.analyticsConfigurationList(Stream.of(analyticsConfigurationList).map(c -> (AnalyticsConfiguration)((AnalyticsConfiguration.Builder)AnalyticsConfiguration.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        @Override
        public ListBucketAnalyticsConfigurationsResponse build() {
            return new ListBucketAnalyticsConfigurationsResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Response.Builder,
    SdkPojo,
    CopyableBuilder<Builder, ListBucketAnalyticsConfigurationsResponse> {
        public Builder isTruncated(Boolean var1);

        public Builder continuationToken(String var1);

        public Builder nextContinuationToken(String var1);

        public Builder analyticsConfigurationList(Collection<AnalyticsConfiguration> var1);

        public Builder analyticsConfigurationList(AnalyticsConfiguration ... var1);

        public Builder analyticsConfigurationList(Consumer<AnalyticsConfiguration.Builder> ... var1);
    }
}

