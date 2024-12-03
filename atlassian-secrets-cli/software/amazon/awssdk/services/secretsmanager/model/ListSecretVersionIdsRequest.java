/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerRequest;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class ListSecretVersionIdsRequest
extends SecretsManagerRequest
implements ToCopyableBuilder<Builder, ListSecretVersionIdsRequest> {
    private static final SdkField<String> SECRET_ID_FIELD = SdkField.builder(MarshallingType.STRING).memberName("SecretId").getter(ListSecretVersionIdsRequest.getter(ListSecretVersionIdsRequest::secretId)).setter(ListSecretVersionIdsRequest.setter(Builder::secretId)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SecretId").build()).build();
    private static final SdkField<Integer> MAX_RESULTS_FIELD = SdkField.builder(MarshallingType.INTEGER).memberName("MaxResults").getter(ListSecretVersionIdsRequest.getter(ListSecretVersionIdsRequest::maxResults)).setter(ListSecretVersionIdsRequest.setter(Builder::maxResults)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("MaxResults").build()).build();
    private static final SdkField<String> NEXT_TOKEN_FIELD = SdkField.builder(MarshallingType.STRING).memberName("NextToken").getter(ListSecretVersionIdsRequest.getter(ListSecretVersionIdsRequest::nextToken)).setter(ListSecretVersionIdsRequest.setter(Builder::nextToken)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("NextToken").build()).build();
    private static final SdkField<Boolean> INCLUDE_DEPRECATED_FIELD = SdkField.builder(MarshallingType.BOOLEAN).memberName("IncludeDeprecated").getter(ListSecretVersionIdsRequest.getter(ListSecretVersionIdsRequest::includeDeprecated)).setter(ListSecretVersionIdsRequest.setter(Builder::includeDeprecated)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("IncludeDeprecated").build()).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(SECRET_ID_FIELD, MAX_RESULTS_FIELD, NEXT_TOKEN_FIELD, INCLUDE_DEPRECATED_FIELD));
    private final String secretId;
    private final Integer maxResults;
    private final String nextToken;
    private final Boolean includeDeprecated;

    private ListSecretVersionIdsRequest(BuilderImpl builder) {
        super(builder);
        this.secretId = builder.secretId;
        this.maxResults = builder.maxResults;
        this.nextToken = builder.nextToken;
        this.includeDeprecated = builder.includeDeprecated;
    }

    public final String secretId() {
        return this.secretId;
    }

    public final Integer maxResults() {
        return this.maxResults;
    }

    public final String nextToken() {
        return this.nextToken;
    }

    public final Boolean includeDeprecated() {
        return this.includeDeprecated;
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

    @Override
    public final int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + super.hashCode();
        hashCode = 31 * hashCode + Objects.hashCode(this.secretId());
        hashCode = 31 * hashCode + Objects.hashCode(this.maxResults());
        hashCode = 31 * hashCode + Objects.hashCode(this.nextToken());
        hashCode = 31 * hashCode + Objects.hashCode(this.includeDeprecated());
        return hashCode;
    }

    @Override
    public final boolean equals(Object obj) {
        return super.equals(obj) && this.equalsBySdkFields(obj);
    }

    @Override
    public final boolean equalsBySdkFields(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ListSecretVersionIdsRequest)) {
            return false;
        }
        ListSecretVersionIdsRequest other = (ListSecretVersionIdsRequest)obj;
        return Objects.equals(this.secretId(), other.secretId()) && Objects.equals(this.maxResults(), other.maxResults()) && Objects.equals(this.nextToken(), other.nextToken()) && Objects.equals(this.includeDeprecated(), other.includeDeprecated());
    }

    public final String toString() {
        return ToString.builder("ListSecretVersionIdsRequest").add("SecretId", this.secretId()).add("MaxResults", this.maxResults()).add("NextToken", this.nextToken()).add("IncludeDeprecated", this.includeDeprecated()).build();
    }

    @Override
    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "SecretId": {
                return Optional.ofNullable(clazz.cast(this.secretId()));
            }
            case "MaxResults": {
                return Optional.ofNullable(clazz.cast(this.maxResults()));
            }
            case "NextToken": {
                return Optional.ofNullable(clazz.cast(this.nextToken()));
            }
            case "IncludeDeprecated": {
                return Optional.ofNullable(clazz.cast(this.includeDeprecated()));
            }
        }
        return Optional.empty();
    }

    @Override
    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<ListSecretVersionIdsRequest, T> g) {
        return obj -> g.apply((ListSecretVersionIdsRequest)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends SecretsManagerRequest.BuilderImpl
    implements Builder {
        private String secretId;
        private Integer maxResults;
        private String nextToken;
        private Boolean includeDeprecated;

        private BuilderImpl() {
        }

        private BuilderImpl(ListSecretVersionIdsRequest model) {
            super(model);
            this.secretId(model.secretId);
            this.maxResults(model.maxResults);
            this.nextToken(model.nextToken);
            this.includeDeprecated(model.includeDeprecated);
        }

        public final String getSecretId() {
            return this.secretId;
        }

        public final void setSecretId(String secretId) {
            this.secretId = secretId;
        }

        @Override
        public final Builder secretId(String secretId) {
            this.secretId = secretId;
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

        public final Boolean getIncludeDeprecated() {
            return this.includeDeprecated;
        }

        public final void setIncludeDeprecated(Boolean includeDeprecated) {
            this.includeDeprecated = includeDeprecated;
        }

        @Override
        public final Builder includeDeprecated(Boolean includeDeprecated) {
            this.includeDeprecated = includeDeprecated;
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
        public ListSecretVersionIdsRequest build() {
            return new ListSecretVersionIdsRequest(this);
        }

        @Override
        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SecretsManagerRequest.Builder,
    SdkPojo,
    CopyableBuilder<Builder, ListSecretVersionIdsRequest> {
        public Builder secretId(String var1);

        public Builder maxResults(Integer var1);

        public Builder nextToken(String var1);

        public Builder includeDeprecated(Boolean var1);

        @Override
        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        @Override
        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

