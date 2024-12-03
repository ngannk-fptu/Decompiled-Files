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
import software.amazon.awssdk.services.s3.model.InventoryConfiguration;
import software.amazon.awssdk.services.s3.model.InventoryConfigurationListCopier;
import software.amazon.awssdk.services.s3.model.S3Response;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class ListBucketInventoryConfigurationsResponse
extends S3Response
implements ToCopyableBuilder<Builder, ListBucketInventoryConfigurationsResponse> {
    private static final SdkField<String> CONTINUATION_TOKEN_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ContinuationToken").getter(ListBucketInventoryConfigurationsResponse.getter(ListBucketInventoryConfigurationsResponse::continuationToken)).setter(ListBucketInventoryConfigurationsResponse.setter(Builder::continuationToken)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ContinuationToken").unmarshallLocationName("ContinuationToken").build()}).build();
    private static final SdkField<List<InventoryConfiguration>> INVENTORY_CONFIGURATION_LIST_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("InventoryConfigurationList").getter(ListBucketInventoryConfigurationsResponse.getter(ListBucketInventoryConfigurationsResponse::inventoryConfigurationList)).setter(ListBucketInventoryConfigurationsResponse.setter(Builder::inventoryConfigurationList)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("InventoryConfiguration").unmarshallLocationName("InventoryConfiguration").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(InventoryConfiguration::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build()}).build();
    private static final SdkField<Boolean> IS_TRUNCATED_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("IsTruncated").getter(ListBucketInventoryConfigurationsResponse.getter(ListBucketInventoryConfigurationsResponse::isTruncated)).setter(ListBucketInventoryConfigurationsResponse.setter(Builder::isTruncated)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("IsTruncated").unmarshallLocationName("IsTruncated").build()}).build();
    private static final SdkField<String> NEXT_CONTINUATION_TOKEN_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("NextContinuationToken").getter(ListBucketInventoryConfigurationsResponse.getter(ListBucketInventoryConfigurationsResponse::nextContinuationToken)).setter(ListBucketInventoryConfigurationsResponse.setter(Builder::nextContinuationToken)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("NextContinuationToken").unmarshallLocationName("NextContinuationToken").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(CONTINUATION_TOKEN_FIELD, INVENTORY_CONFIGURATION_LIST_FIELD, IS_TRUNCATED_FIELD, NEXT_CONTINUATION_TOKEN_FIELD));
    private final String continuationToken;
    private final List<InventoryConfiguration> inventoryConfigurationList;
    private final Boolean isTruncated;
    private final String nextContinuationToken;

    private ListBucketInventoryConfigurationsResponse(BuilderImpl builder) {
        super(builder);
        this.continuationToken = builder.continuationToken;
        this.inventoryConfigurationList = builder.inventoryConfigurationList;
        this.isTruncated = builder.isTruncated;
        this.nextContinuationToken = builder.nextContinuationToken;
    }

    public final String continuationToken() {
        return this.continuationToken;
    }

    public final boolean hasInventoryConfigurationList() {
        return this.inventoryConfigurationList != null && !(this.inventoryConfigurationList instanceof SdkAutoConstructList);
    }

    public final List<InventoryConfiguration> inventoryConfigurationList() {
        return this.inventoryConfigurationList;
    }

    public final Boolean isTruncated() {
        return this.isTruncated;
    }

    public final String nextContinuationToken() {
        return this.nextContinuationToken;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.continuationToken());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasInventoryConfigurationList() ? this.inventoryConfigurationList() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.isTruncated());
        hashCode = 31 * hashCode + Objects.hashCode(this.nextContinuationToken());
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
        if (!(obj instanceof ListBucketInventoryConfigurationsResponse)) {
            return false;
        }
        ListBucketInventoryConfigurationsResponse other = (ListBucketInventoryConfigurationsResponse)((Object)obj);
        return Objects.equals(this.continuationToken(), other.continuationToken()) && this.hasInventoryConfigurationList() == other.hasInventoryConfigurationList() && Objects.equals(this.inventoryConfigurationList(), other.inventoryConfigurationList()) && Objects.equals(this.isTruncated(), other.isTruncated()) && Objects.equals(this.nextContinuationToken(), other.nextContinuationToken());
    }

    public final String toString() {
        return ToString.builder((String)"ListBucketInventoryConfigurationsResponse").add("ContinuationToken", (Object)this.continuationToken()).add("InventoryConfigurationList", this.hasInventoryConfigurationList() ? this.inventoryConfigurationList() : null).add("IsTruncated", (Object)this.isTruncated()).add("NextContinuationToken", (Object)this.nextContinuationToken()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "ContinuationToken": {
                return Optional.ofNullable(clazz.cast(this.continuationToken()));
            }
            case "InventoryConfigurationList": {
                return Optional.ofNullable(clazz.cast(this.inventoryConfigurationList()));
            }
            case "IsTruncated": {
                return Optional.ofNullable(clazz.cast(this.isTruncated()));
            }
            case "NextContinuationToken": {
                return Optional.ofNullable(clazz.cast(this.nextContinuationToken()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<ListBucketInventoryConfigurationsResponse, T> g) {
        return obj -> g.apply((ListBucketInventoryConfigurationsResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Response.BuilderImpl
    implements Builder {
        private String continuationToken;
        private List<InventoryConfiguration> inventoryConfigurationList = DefaultSdkAutoConstructList.getInstance();
        private Boolean isTruncated;
        private String nextContinuationToken;

        private BuilderImpl() {
        }

        private BuilderImpl(ListBucketInventoryConfigurationsResponse model) {
            super(model);
            this.continuationToken(model.continuationToken);
            this.inventoryConfigurationList(model.inventoryConfigurationList);
            this.isTruncated(model.isTruncated);
            this.nextContinuationToken(model.nextContinuationToken);
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

        public final List<InventoryConfiguration.Builder> getInventoryConfigurationList() {
            List<InventoryConfiguration.Builder> result = InventoryConfigurationListCopier.copyToBuilder(this.inventoryConfigurationList);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setInventoryConfigurationList(Collection<InventoryConfiguration.BuilderImpl> inventoryConfigurationList) {
            this.inventoryConfigurationList = InventoryConfigurationListCopier.copyFromBuilder(inventoryConfigurationList);
        }

        @Override
        public final Builder inventoryConfigurationList(Collection<InventoryConfiguration> inventoryConfigurationList) {
            this.inventoryConfigurationList = InventoryConfigurationListCopier.copy(inventoryConfigurationList);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder inventoryConfigurationList(InventoryConfiguration ... inventoryConfigurationList) {
            this.inventoryConfigurationList(Arrays.asList(inventoryConfigurationList));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder inventoryConfigurationList(Consumer<InventoryConfiguration.Builder> ... inventoryConfigurationList) {
            this.inventoryConfigurationList(Stream.of(inventoryConfigurationList).map(c -> (InventoryConfiguration)((InventoryConfiguration.Builder)InventoryConfiguration.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
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

        @Override
        public ListBucketInventoryConfigurationsResponse build() {
            return new ListBucketInventoryConfigurationsResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Response.Builder,
    SdkPojo,
    CopyableBuilder<Builder, ListBucketInventoryConfigurationsResponse> {
        public Builder continuationToken(String var1);

        public Builder inventoryConfigurationList(Collection<InventoryConfiguration> var1);

        public Builder inventoryConfigurationList(InventoryConfiguration ... var1);

        public Builder inventoryConfigurationList(Consumer<InventoryConfiguration.Builder> ... var1);

        public Builder isTruncated(Boolean var1);

        public Builder nextContinuationToken(String var1);
    }
}

