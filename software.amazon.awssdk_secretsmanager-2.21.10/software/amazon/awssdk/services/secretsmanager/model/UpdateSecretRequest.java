/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration$Builder
 *  software.amazon.awssdk.core.SdkBytes
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.DefaultValueTrait
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.Trait
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.secretsmanager.model;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.DefaultValueTrait;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerRequest;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class UpdateSecretRequest
extends SecretsManagerRequest
implements ToCopyableBuilder<Builder, UpdateSecretRequest> {
    private static final SdkField<String> SECRET_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SecretId").getter(UpdateSecretRequest.getter(UpdateSecretRequest::secretId)).setter(UpdateSecretRequest.setter(Builder::secretId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SecretId").build()}).build();
    private static final SdkField<String> CLIENT_REQUEST_TOKEN_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ClientRequestToken").getter(UpdateSecretRequest.getter(UpdateSecretRequest::clientRequestToken)).setter(UpdateSecretRequest.setter(Builder::clientRequestToken)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ClientRequestToken").build(), DefaultValueTrait.idempotencyToken()}).build();
    private static final SdkField<String> DESCRIPTION_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Description").getter(UpdateSecretRequest.getter(UpdateSecretRequest::description)).setter(UpdateSecretRequest.setter(Builder::description)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Description").build()}).build();
    private static final SdkField<String> KMS_KEY_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("KmsKeyId").getter(UpdateSecretRequest.getter(UpdateSecretRequest::kmsKeyId)).setter(UpdateSecretRequest.setter(Builder::kmsKeyId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("KmsKeyId").build()}).build();
    private static final SdkField<SdkBytes> SECRET_BINARY_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_BYTES).memberName("SecretBinary").getter(UpdateSecretRequest.getter(UpdateSecretRequest::secretBinary)).setter(UpdateSecretRequest.setter(Builder::secretBinary)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SecretBinary").build()}).build();
    private static final SdkField<String> SECRET_STRING_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SecretString").getter(UpdateSecretRequest.getter(UpdateSecretRequest::secretString)).setter(UpdateSecretRequest.setter(Builder::secretString)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SecretString").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(SECRET_ID_FIELD, CLIENT_REQUEST_TOKEN_FIELD, DESCRIPTION_FIELD, KMS_KEY_ID_FIELD, SECRET_BINARY_FIELD, SECRET_STRING_FIELD));
    private final String secretId;
    private final String clientRequestToken;
    private final String description;
    private final String kmsKeyId;
    private final SdkBytes secretBinary;
    private final String secretString;

    private UpdateSecretRequest(BuilderImpl builder) {
        super(builder);
        this.secretId = builder.secretId;
        this.clientRequestToken = builder.clientRequestToken;
        this.description = builder.description;
        this.kmsKeyId = builder.kmsKeyId;
        this.secretBinary = builder.secretBinary;
        this.secretString = builder.secretString;
    }

    public final String secretId() {
        return this.secretId;
    }

    public final String clientRequestToken() {
        return this.clientRequestToken;
    }

    public final String description() {
        return this.description;
    }

    public final String kmsKeyId() {
        return this.kmsKeyId;
    }

    public final SdkBytes secretBinary() {
        return this.secretBinary;
    }

    public final String secretString() {
        return this.secretString;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.secretId());
        hashCode = 31 * hashCode + Objects.hashCode(this.clientRequestToken());
        hashCode = 31 * hashCode + Objects.hashCode(this.description());
        hashCode = 31 * hashCode + Objects.hashCode(this.kmsKeyId());
        hashCode = 31 * hashCode + Objects.hashCode(this.secretBinary());
        hashCode = 31 * hashCode + Objects.hashCode(this.secretString());
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
        if (!(obj instanceof UpdateSecretRequest)) {
            return false;
        }
        UpdateSecretRequest other = (UpdateSecretRequest)((Object)obj);
        return Objects.equals(this.secretId(), other.secretId()) && Objects.equals(this.clientRequestToken(), other.clientRequestToken()) && Objects.equals(this.description(), other.description()) && Objects.equals(this.kmsKeyId(), other.kmsKeyId()) && Objects.equals(this.secretBinary(), other.secretBinary()) && Objects.equals(this.secretString(), other.secretString());
    }

    public final String toString() {
        return ToString.builder((String)"UpdateSecretRequest").add("SecretId", (Object)this.secretId()).add("ClientRequestToken", (Object)this.clientRequestToken()).add("Description", (Object)this.description()).add("KmsKeyId", (Object)this.kmsKeyId()).add("SecretBinary", (Object)(this.secretBinary() == null ? null : "*** Sensitive Data Redacted ***")).add("SecretString", (Object)(this.secretString() == null ? null : "*** Sensitive Data Redacted ***")).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "SecretId": {
                return Optional.ofNullable(clazz.cast(this.secretId()));
            }
            case "ClientRequestToken": {
                return Optional.ofNullable(clazz.cast(this.clientRequestToken()));
            }
            case "Description": {
                return Optional.ofNullable(clazz.cast(this.description()));
            }
            case "KmsKeyId": {
                return Optional.ofNullable(clazz.cast(this.kmsKeyId()));
            }
            case "SecretBinary": {
                return Optional.ofNullable(clazz.cast(this.secretBinary()));
            }
            case "SecretString": {
                return Optional.ofNullable(clazz.cast(this.secretString()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<UpdateSecretRequest, T> g) {
        return obj -> g.apply((UpdateSecretRequest)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends SecretsManagerRequest.BuilderImpl
    implements Builder {
        private String secretId;
        private String clientRequestToken;
        private String description;
        private String kmsKeyId;
        private SdkBytes secretBinary;
        private String secretString;

        private BuilderImpl() {
        }

        private BuilderImpl(UpdateSecretRequest model) {
            super(model);
            this.secretId(model.secretId);
            this.clientRequestToken(model.clientRequestToken);
            this.description(model.description);
            this.kmsKeyId(model.kmsKeyId);
            this.secretBinary(model.secretBinary);
            this.secretString(model.secretString);
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

        public final String getClientRequestToken() {
            return this.clientRequestToken;
        }

        public final void setClientRequestToken(String clientRequestToken) {
            this.clientRequestToken = clientRequestToken;
        }

        @Override
        public final Builder clientRequestToken(String clientRequestToken) {
            this.clientRequestToken = clientRequestToken;
            return this;
        }

        public final String getDescription() {
            return this.description;
        }

        public final void setDescription(String description) {
            this.description = description;
        }

        @Override
        public final Builder description(String description) {
            this.description = description;
            return this;
        }

        public final String getKmsKeyId() {
            return this.kmsKeyId;
        }

        public final void setKmsKeyId(String kmsKeyId) {
            this.kmsKeyId = kmsKeyId;
        }

        @Override
        public final Builder kmsKeyId(String kmsKeyId) {
            this.kmsKeyId = kmsKeyId;
            return this;
        }

        public final ByteBuffer getSecretBinary() {
            return this.secretBinary == null ? null : this.secretBinary.asByteBuffer();
        }

        public final void setSecretBinary(ByteBuffer secretBinary) {
            this.secretBinary(secretBinary == null ? null : SdkBytes.fromByteBuffer((ByteBuffer)secretBinary));
        }

        @Override
        public final Builder secretBinary(SdkBytes secretBinary) {
            this.secretBinary = secretBinary;
            return this;
        }

        public final String getSecretString() {
            return this.secretString;
        }

        public final void setSecretString(String secretString) {
            this.secretString = secretString;
        }

        @Override
        public final Builder secretString(String secretString) {
            this.secretString = secretString;
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
        public UpdateSecretRequest build() {
            return new UpdateSecretRequest(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SecretsManagerRequest.Builder,
    SdkPojo,
    CopyableBuilder<Builder, UpdateSecretRequest> {
        public Builder secretId(String var1);

        public Builder clientRequestToken(String var1);

        public Builder description(String var1);

        public Builder kmsKeyId(String var1);

        public Builder secretBinary(SdkBytes var1);

        public Builder secretString(String var1);

        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

