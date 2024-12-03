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

public final class PutResourcePolicyRequest
extends SecretsManagerRequest
implements ToCopyableBuilder<Builder, PutResourcePolicyRequest> {
    private static final SdkField<String> SECRET_ID_FIELD = SdkField.builder(MarshallingType.STRING).memberName("SecretId").getter(PutResourcePolicyRequest.getter(PutResourcePolicyRequest::secretId)).setter(PutResourcePolicyRequest.setter(Builder::secretId)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SecretId").build()).build();
    private static final SdkField<String> RESOURCE_POLICY_FIELD = SdkField.builder(MarshallingType.STRING).memberName("ResourcePolicy").getter(PutResourcePolicyRequest.getter(PutResourcePolicyRequest::resourcePolicy)).setter(PutResourcePolicyRequest.setter(Builder::resourcePolicy)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ResourcePolicy").build()).build();
    private static final SdkField<Boolean> BLOCK_PUBLIC_POLICY_FIELD = SdkField.builder(MarshallingType.BOOLEAN).memberName("BlockPublicPolicy").getter(PutResourcePolicyRequest.getter(PutResourcePolicyRequest::blockPublicPolicy)).setter(PutResourcePolicyRequest.setter(Builder::blockPublicPolicy)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("BlockPublicPolicy").build()).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(SECRET_ID_FIELD, RESOURCE_POLICY_FIELD, BLOCK_PUBLIC_POLICY_FIELD));
    private final String secretId;
    private final String resourcePolicy;
    private final Boolean blockPublicPolicy;

    private PutResourcePolicyRequest(BuilderImpl builder) {
        super(builder);
        this.secretId = builder.secretId;
        this.resourcePolicy = builder.resourcePolicy;
        this.blockPublicPolicy = builder.blockPublicPolicy;
    }

    public final String secretId() {
        return this.secretId;
    }

    public final String resourcePolicy() {
        return this.resourcePolicy;
    }

    public final Boolean blockPublicPolicy() {
        return this.blockPublicPolicy;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.resourcePolicy());
        hashCode = 31 * hashCode + Objects.hashCode(this.blockPublicPolicy());
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
        if (!(obj instanceof PutResourcePolicyRequest)) {
            return false;
        }
        PutResourcePolicyRequest other = (PutResourcePolicyRequest)obj;
        return Objects.equals(this.secretId(), other.secretId()) && Objects.equals(this.resourcePolicy(), other.resourcePolicy()) && Objects.equals(this.blockPublicPolicy(), other.blockPublicPolicy());
    }

    public final String toString() {
        return ToString.builder("PutResourcePolicyRequest").add("SecretId", this.secretId()).add("ResourcePolicy", this.resourcePolicy()).add("BlockPublicPolicy", this.blockPublicPolicy()).build();
    }

    @Override
    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "SecretId": {
                return Optional.ofNullable(clazz.cast(this.secretId()));
            }
            case "ResourcePolicy": {
                return Optional.ofNullable(clazz.cast(this.resourcePolicy()));
            }
            case "BlockPublicPolicy": {
                return Optional.ofNullable(clazz.cast(this.blockPublicPolicy()));
            }
        }
        return Optional.empty();
    }

    @Override
    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<PutResourcePolicyRequest, T> g) {
        return obj -> g.apply((PutResourcePolicyRequest)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends SecretsManagerRequest.BuilderImpl
    implements Builder {
        private String secretId;
        private String resourcePolicy;
        private Boolean blockPublicPolicy;

        private BuilderImpl() {
        }

        private BuilderImpl(PutResourcePolicyRequest model) {
            super(model);
            this.secretId(model.secretId);
            this.resourcePolicy(model.resourcePolicy);
            this.blockPublicPolicy(model.blockPublicPolicy);
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

        public final String getResourcePolicy() {
            return this.resourcePolicy;
        }

        public final void setResourcePolicy(String resourcePolicy) {
            this.resourcePolicy = resourcePolicy;
        }

        @Override
        public final Builder resourcePolicy(String resourcePolicy) {
            this.resourcePolicy = resourcePolicy;
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
        public PutResourcePolicyRequest build() {
            return new PutResourcePolicyRequest(this);
        }

        @Override
        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SecretsManagerRequest.Builder,
    SdkPojo,
    CopyableBuilder<Builder, PutResourcePolicyRequest> {
        public Builder secretId(String var1);

        public Builder resourcePolicy(String var1);

        public Builder blockPublicPolicy(Boolean var1);

        @Override
        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        @Override
        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

