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
import software.amazon.awssdk.services.s3.model.IntelligentTieringConfiguration;
import software.amazon.awssdk.services.s3.model.IntelligentTieringConfigurationListCopier;
import software.amazon.awssdk.services.s3.model.S3Response;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class ListBucketIntelligentTieringConfigurationsResponse
extends S3Response
implements ToCopyableBuilder<Builder, ListBucketIntelligentTieringConfigurationsResponse> {
    private static final SdkField<Boolean> IS_TRUNCATED_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("IsTruncated").getter(ListBucketIntelligentTieringConfigurationsResponse.getter(ListBucketIntelligentTieringConfigurationsResponse::isTruncated)).setter(ListBucketIntelligentTieringConfigurationsResponse.setter(Builder::isTruncated)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("IsTruncated").unmarshallLocationName("IsTruncated").build()}).build();
    private static final SdkField<String> CONTINUATION_TOKEN_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ContinuationToken").getter(ListBucketIntelligentTieringConfigurationsResponse.getter(ListBucketIntelligentTieringConfigurationsResponse::continuationToken)).setter(ListBucketIntelligentTieringConfigurationsResponse.setter(Builder::continuationToken)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ContinuationToken").unmarshallLocationName("ContinuationToken").build()}).build();
    private static final SdkField<String> NEXT_CONTINUATION_TOKEN_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("NextContinuationToken").getter(ListBucketIntelligentTieringConfigurationsResponse.getter(ListBucketIntelligentTieringConfigurationsResponse::nextContinuationToken)).setter(ListBucketIntelligentTieringConfigurationsResponse.setter(Builder::nextContinuationToken)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("NextContinuationToken").unmarshallLocationName("NextContinuationToken").build()}).build();
    private static final SdkField<List<IntelligentTieringConfiguration>> INTELLIGENT_TIERING_CONFIGURATION_LIST_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("IntelligentTieringConfigurationList").getter(ListBucketIntelligentTieringConfigurationsResponse.getter(ListBucketIntelligentTieringConfigurationsResponse::intelligentTieringConfigurationList)).setter(ListBucketIntelligentTieringConfigurationsResponse.setter(Builder::intelligentTieringConfigurationList)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("IntelligentTieringConfiguration").unmarshallLocationName("IntelligentTieringConfiguration").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(IntelligentTieringConfiguration::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(IS_TRUNCATED_FIELD, CONTINUATION_TOKEN_FIELD, NEXT_CONTINUATION_TOKEN_FIELD, INTELLIGENT_TIERING_CONFIGURATION_LIST_FIELD));
    private final Boolean isTruncated;
    private final String continuationToken;
    private final String nextContinuationToken;
    private final List<IntelligentTieringConfiguration> intelligentTieringConfigurationList;

    private ListBucketIntelligentTieringConfigurationsResponse(BuilderImpl builder) {
        super(builder);
        this.isTruncated = builder.isTruncated;
        this.continuationToken = builder.continuationToken;
        this.nextContinuationToken = builder.nextContinuationToken;
        this.intelligentTieringConfigurationList = builder.intelligentTieringConfigurationList;
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

    public final boolean hasIntelligentTieringConfigurationList() {
        return this.intelligentTieringConfigurationList != null && !(this.intelligentTieringConfigurationList instanceof SdkAutoConstructList);
    }

    public final List<IntelligentTieringConfiguration> intelligentTieringConfigurationList() {
        return this.intelligentTieringConfigurationList;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.hasIntelligentTieringConfigurationList() ? this.intelligentTieringConfigurationList() : null);
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
        if (!(obj instanceof ListBucketIntelligentTieringConfigurationsResponse)) {
            return false;
        }
        ListBucketIntelligentTieringConfigurationsResponse other = (ListBucketIntelligentTieringConfigurationsResponse)((Object)obj);
        return Objects.equals(this.isTruncated(), other.isTruncated()) && Objects.equals(this.continuationToken(), other.continuationToken()) && Objects.equals(this.nextContinuationToken(), other.nextContinuationToken()) && this.hasIntelligentTieringConfigurationList() == other.hasIntelligentTieringConfigurationList() && Objects.equals(this.intelligentTieringConfigurationList(), other.intelligentTieringConfigurationList());
    }

    public final String toString() {
        return ToString.builder((String)"ListBucketIntelligentTieringConfigurationsResponse").add("IsTruncated", (Object)this.isTruncated()).add("ContinuationToken", (Object)this.continuationToken()).add("NextContinuationToken", (Object)this.nextContinuationToken()).add("IntelligentTieringConfigurationList", this.hasIntelligentTieringConfigurationList() ? this.intelligentTieringConfigurationList() : null).build();
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
            case "IntelligentTieringConfigurationList": {
                return Optional.ofNullable(clazz.cast(this.intelligentTieringConfigurationList()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<ListBucketIntelligentTieringConfigurationsResponse, T> g) {
        return obj -> g.apply((ListBucketIntelligentTieringConfigurationsResponse)((Object)((Object)obj)));
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
        private List<IntelligentTieringConfiguration> intelligentTieringConfigurationList = DefaultSdkAutoConstructList.getInstance();

        private BuilderImpl() {
        }

        private BuilderImpl(ListBucketIntelligentTieringConfigurationsResponse model) {
            super(model);
            this.isTruncated(model.isTruncated);
            this.continuationToken(model.continuationToken);
            this.nextContinuationToken(model.nextContinuationToken);
            this.intelligentTieringConfigurationList(model.intelligentTieringConfigurationList);
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

        public final List<IntelligentTieringConfiguration.Builder> getIntelligentTieringConfigurationList() {
            List<IntelligentTieringConfiguration.Builder> result = IntelligentTieringConfigurationListCopier.copyToBuilder(this.intelligentTieringConfigurationList);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setIntelligentTieringConfigurationList(Collection<IntelligentTieringConfiguration.BuilderImpl> intelligentTieringConfigurationList) {
            this.intelligentTieringConfigurationList = IntelligentTieringConfigurationListCopier.copyFromBuilder(intelligentTieringConfigurationList);
        }

        @Override
        public final Builder intelligentTieringConfigurationList(Collection<IntelligentTieringConfiguration> intelligentTieringConfigurationList) {
            this.intelligentTieringConfigurationList = IntelligentTieringConfigurationListCopier.copy(intelligentTieringConfigurationList);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder intelligentTieringConfigurationList(IntelligentTieringConfiguration ... intelligentTieringConfigurationList) {
            this.intelligentTieringConfigurationList(Arrays.asList(intelligentTieringConfigurationList));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder intelligentTieringConfigurationList(Consumer<IntelligentTieringConfiguration.Builder> ... intelligentTieringConfigurationList) {
            this.intelligentTieringConfigurationList(Stream.of(intelligentTieringConfigurationList).map(c -> (IntelligentTieringConfiguration)((IntelligentTieringConfiguration.Builder)IntelligentTieringConfiguration.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        @Override
        public ListBucketIntelligentTieringConfigurationsResponse build() {
            return new ListBucketIntelligentTieringConfigurationsResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Response.Builder,
    SdkPojo,
    CopyableBuilder<Builder, ListBucketIntelligentTieringConfigurationsResponse> {
        public Builder isTruncated(Boolean var1);

        public Builder continuationToken(String var1);

        public Builder nextContinuationToken(String var1);

        public Builder intelligentTieringConfigurationList(Collection<IntelligentTieringConfiguration> var1);

        public Builder intelligentTieringConfigurationList(IntelligentTieringConfiguration ... var1);

        public Builder intelligentTieringConfigurationList(Consumer<IntelligentTieringConfiguration.Builder> ... var1);
    }
}

