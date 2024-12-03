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

public final class GetSecretValueRequest
extends SecretsManagerRequest
implements ToCopyableBuilder<Builder, GetSecretValueRequest> {
    private static final SdkField<String> SECRET_ID_FIELD = SdkField.builder(MarshallingType.STRING).memberName("SecretId").getter(GetSecretValueRequest.getter(GetSecretValueRequest::secretId)).setter(GetSecretValueRequest.setter(Builder::secretId)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SecretId").build()).build();
    private static final SdkField<String> VERSION_ID_FIELD = SdkField.builder(MarshallingType.STRING).memberName("VersionId").getter(GetSecretValueRequest.getter(GetSecretValueRequest::versionId)).setter(GetSecretValueRequest.setter(Builder::versionId)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("VersionId").build()).build();
    private static final SdkField<String> VERSION_STAGE_FIELD = SdkField.builder(MarshallingType.STRING).memberName("VersionStage").getter(GetSecretValueRequest.getter(GetSecretValueRequest::versionStage)).setter(GetSecretValueRequest.setter(Builder::versionStage)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("VersionStage").build()).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(SECRET_ID_FIELD, VERSION_ID_FIELD, VERSION_STAGE_FIELD));
    private final String secretId;
    private final String versionId;
    private final String versionStage;

    private GetSecretValueRequest(BuilderImpl builder) {
        super(builder);
        this.secretId = builder.secretId;
        this.versionId = builder.versionId;
        this.versionStage = builder.versionStage;
    }

    public final String secretId() {
        return this.secretId;
    }

    public final String versionId() {
        return this.versionId;
    }

    public final String versionStage() {
        return this.versionStage;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.versionId());
        hashCode = 31 * hashCode + Objects.hashCode(this.versionStage());
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
        if (!(obj instanceof GetSecretValueRequest)) {
            return false;
        }
        GetSecretValueRequest other = (GetSecretValueRequest)obj;
        return Objects.equals(this.secretId(), other.secretId()) && Objects.equals(this.versionId(), other.versionId()) && Objects.equals(this.versionStage(), other.versionStage());
    }

    public final String toString() {
        return ToString.builder("GetSecretValueRequest").add("SecretId", this.secretId()).add("VersionId", this.versionId()).add("VersionStage", this.versionStage()).build();
    }

    @Override
    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "SecretId": {
                return Optional.ofNullable(clazz.cast(this.secretId()));
            }
            case "VersionId": {
                return Optional.ofNullable(clazz.cast(this.versionId()));
            }
            case "VersionStage": {
                return Optional.ofNullable(clazz.cast(this.versionStage()));
            }
        }
        return Optional.empty();
    }

    @Override
    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<GetSecretValueRequest, T> g) {
        return obj -> g.apply((GetSecretValueRequest)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends SecretsManagerRequest.BuilderImpl
    implements Builder {
        private String secretId;
        private String versionId;
        private String versionStage;

        private BuilderImpl() {
        }

        private BuilderImpl(GetSecretValueRequest model) {
            super(model);
            this.secretId(model.secretId);
            this.versionId(model.versionId);
            this.versionStage(model.versionStage);
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

        public final String getVersionId() {
            return this.versionId;
        }

        public final void setVersionId(String versionId) {
            this.versionId = versionId;
        }

        @Override
        public final Builder versionId(String versionId) {
            this.versionId = versionId;
            return this;
        }

        public final String getVersionStage() {
            return this.versionStage;
        }

        public final void setVersionStage(String versionStage) {
            this.versionStage = versionStage;
        }

        @Override
        public final Builder versionStage(String versionStage) {
            this.versionStage = versionStage;
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
        public GetSecretValueRequest build() {
            return new GetSecretValueRequest(this);
        }

        @Override
        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SecretsManagerRequest.Builder,
    SdkPojo,
    CopyableBuilder<Builder, GetSecretValueRequest> {
        public Builder secretId(String var1);

        public Builder versionId(String var1);

        public Builder versionStage(String var1);

        @Override
        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        @Override
        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

