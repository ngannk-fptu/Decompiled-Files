/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration$Builder
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
package software.amazon.awssdk.services.secretsmanager.model;

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
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.ListTrait;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.services.secretsmanager.model.Filter;
import software.amazon.awssdk.services.secretsmanager.model.FiltersListTypeCopier;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerRequest;
import software.amazon.awssdk.services.secretsmanager.model.SortOrderType;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class ListSecretsRequest
extends SecretsManagerRequest
implements ToCopyableBuilder<Builder, ListSecretsRequest> {
    private static final SdkField<Boolean> INCLUDE_PLANNED_DELETION_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("IncludePlannedDeletion").getter(ListSecretsRequest.getter(ListSecretsRequest::includePlannedDeletion)).setter(ListSecretsRequest.setter(Builder::includePlannedDeletion)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("IncludePlannedDeletion").build()}).build();
    private static final SdkField<Integer> MAX_RESULTS_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("MaxResults").getter(ListSecretsRequest.getter(ListSecretsRequest::maxResults)).setter(ListSecretsRequest.setter(Builder::maxResults)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("MaxResults").build()}).build();
    private static final SdkField<String> NEXT_TOKEN_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("NextToken").getter(ListSecretsRequest.getter(ListSecretsRequest::nextToken)).setter(ListSecretsRequest.setter(Builder::nextToken)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("NextToken").build()}).build();
    private static final SdkField<List<Filter>> FILTERS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("Filters").getter(ListSecretsRequest.getter(ListSecretsRequest::filters)).setter(ListSecretsRequest.setter(Builder::filters)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Filters").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(Filter::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").build()}).build()).build()}).build();
    private static final SdkField<String> SORT_ORDER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SortOrder").getter(ListSecretsRequest.getter(ListSecretsRequest::sortOrderAsString)).setter(ListSecretsRequest.setter(Builder::sortOrder)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SortOrder").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(INCLUDE_PLANNED_DELETION_FIELD, MAX_RESULTS_FIELD, NEXT_TOKEN_FIELD, FILTERS_FIELD, SORT_ORDER_FIELD));
    private final Boolean includePlannedDeletion;
    private final Integer maxResults;
    private final String nextToken;
    private final List<Filter> filters;
    private final String sortOrder;

    private ListSecretsRequest(BuilderImpl builder) {
        super(builder);
        this.includePlannedDeletion = builder.includePlannedDeletion;
        this.maxResults = builder.maxResults;
        this.nextToken = builder.nextToken;
        this.filters = builder.filters;
        this.sortOrder = builder.sortOrder;
    }

    public final Boolean includePlannedDeletion() {
        return this.includePlannedDeletion;
    }

    public final Integer maxResults() {
        return this.maxResults;
    }

    public final String nextToken() {
        return this.nextToken;
    }

    public final boolean hasFilters() {
        return this.filters != null && !(this.filters instanceof SdkAutoConstructList);
    }

    public final List<Filter> filters() {
        return this.filters;
    }

    public final SortOrderType sortOrder() {
        return SortOrderType.fromValue(this.sortOrder);
    }

    public final String sortOrderAsString() {
        return this.sortOrder;
    }

    @Override
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
        hashCode = 31 * hashCode + Objects.hashCode(this.includePlannedDeletion());
        hashCode = 31 * hashCode + Objects.hashCode(this.maxResults());
        hashCode = 31 * hashCode + Objects.hashCode(this.nextToken());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasFilters() ? this.filters() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.sortOrderAsString());
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
        if (!(obj instanceof ListSecretsRequest)) {
            return false;
        }
        ListSecretsRequest other = (ListSecretsRequest)((Object)obj);
        return Objects.equals(this.includePlannedDeletion(), other.includePlannedDeletion()) && Objects.equals(this.maxResults(), other.maxResults()) && Objects.equals(this.nextToken(), other.nextToken()) && this.hasFilters() == other.hasFilters() && Objects.equals(this.filters(), other.filters()) && Objects.equals(this.sortOrderAsString(), other.sortOrderAsString());
    }

    public final String toString() {
        return ToString.builder((String)"ListSecretsRequest").add("IncludePlannedDeletion", (Object)this.includePlannedDeletion()).add("MaxResults", (Object)this.maxResults()).add("NextToken", (Object)this.nextToken()).add("Filters", this.hasFilters() ? this.filters() : null).add("SortOrder", (Object)this.sortOrderAsString()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "IncludePlannedDeletion": {
                return Optional.ofNullable(clazz.cast(this.includePlannedDeletion()));
            }
            case "MaxResults": {
                return Optional.ofNullable(clazz.cast(this.maxResults()));
            }
            case "NextToken": {
                return Optional.ofNullable(clazz.cast(this.nextToken()));
            }
            case "Filters": {
                return Optional.ofNullable(clazz.cast(this.filters()));
            }
            case "SortOrder": {
                return Optional.ofNullable(clazz.cast(this.sortOrderAsString()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<ListSecretsRequest, T> g) {
        return obj -> g.apply((ListSecretsRequest)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends SecretsManagerRequest.BuilderImpl
    implements Builder {
        private Boolean includePlannedDeletion;
        private Integer maxResults;
        private String nextToken;
        private List<Filter> filters = DefaultSdkAutoConstructList.getInstance();
        private String sortOrder;

        private BuilderImpl() {
        }

        private BuilderImpl(ListSecretsRequest model) {
            super(model);
            this.includePlannedDeletion(model.includePlannedDeletion);
            this.maxResults(model.maxResults);
            this.nextToken(model.nextToken);
            this.filters(model.filters);
            this.sortOrder(model.sortOrder);
        }

        public final Boolean getIncludePlannedDeletion() {
            return this.includePlannedDeletion;
        }

        public final void setIncludePlannedDeletion(Boolean includePlannedDeletion) {
            this.includePlannedDeletion = includePlannedDeletion;
        }

        @Override
        public final Builder includePlannedDeletion(Boolean includePlannedDeletion) {
            this.includePlannedDeletion = includePlannedDeletion;
            return this;
        }

        public final Integer getMaxResults() {
            return this.maxResults;
        }

        public final void setMaxResults(Integer maxResults) {
            this.maxResults = maxResults;
        }

        @Override
        public final Builder maxResults(Integer maxResults) {
            this.maxResults = maxResults;
            return this;
        }

        public final String getNextToken() {
            return this.nextToken;
        }

        public final void setNextToken(String nextToken) {
            this.nextToken = nextToken;
        }

        @Override
        public final Builder nextToken(String nextToken) {
            this.nextToken = nextToken;
            return this;
        }

        public final List<Filter.Builder> getFilters() {
            List<Filter.Builder> result = FiltersListTypeCopier.copyToBuilder(this.filters);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setFilters(Collection<Filter.BuilderImpl> filters) {
            this.filters = FiltersListTypeCopier.copyFromBuilder(filters);
        }

        @Override
        public final Builder filters(Collection<Filter> filters) {
            this.filters = FiltersListTypeCopier.copy(filters);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder filters(Filter ... filters) {
            this.filters(Arrays.asList(filters));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder filters(Consumer<Filter.Builder> ... filters) {
            this.filters(Stream.of(filters).map(c -> (Filter)((Filter.Builder)Filter.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        public final String getSortOrder() {
            return this.sortOrder;
        }

        public final void setSortOrder(String sortOrder) {
            this.sortOrder = sortOrder;
        }

        @Override
        public final Builder sortOrder(String sortOrder) {
            this.sortOrder = sortOrder;
            return this;
        }

        @Override
        public final Builder sortOrder(SortOrderType sortOrder) {
            this.sortOrder(sortOrder == null ? null : sortOrder.toString());
            return this;
        }

        @Override
        public Builder overrideConfiguration(AwsRequestOverrideConfiguration overrideConfiguration) {
            super.overrideConfiguration(overrideConfiguration);
            return this;
        }

        @Override
        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> builderConsumer) {
            super.overrideConfiguration(builderConsumer);
            return this;
        }

        @Override
        public ListSecretsRequest build() {
            return new ListSecretsRequest(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SecretsManagerRequest.Builder,
    SdkPojo,
    CopyableBuilder<Builder, ListSecretsRequest> {
        public Builder includePlannedDeletion(Boolean var1);

        public Builder maxResults(Integer var1);

        public Builder nextToken(String var1);

        public Builder filters(Collection<Filter> var1);

        public Builder filters(Filter ... var1);

        public Builder filters(Consumer<Filter.Builder> ... var1);

        public Builder sortOrder(String var1);

        public Builder sortOrder(SortOrderType var1);

        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

