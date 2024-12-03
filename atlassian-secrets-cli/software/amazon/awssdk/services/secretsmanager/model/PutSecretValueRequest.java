/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.model;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
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
import software.amazon.awssdk.core.traits.ListTrait;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.services.secretsmanager.model.SecretVersionStagesTypeCopier;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerRequest;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class PutSecretValueRequest
extends SecretsManagerRequest
implements ToCopyableBuilder<Builder, PutSecretValueRequest> {
    private static final SdkField<String> SECRET_ID_FIELD = SdkField.builder(MarshallingType.STRING).memberName("SecretId").getter(PutSecretValueRequest.getter(PutSecretValueRequest::secretId)).setter(PutSecretValueRequest.setter(Builder::secretId)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SecretId").build()).build();
    private static final SdkField<String> CLIENT_REQUEST_TOKEN_FIELD = SdkField.builder(MarshallingType.STRING).memberName("ClientRequestToken").getter(PutSecretValueRequest.getter(PutSecretValueRequest::clientRequestToken)).setter(PutSecretValueRequest.setter(Builder::clientRequestToken)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ClientRequestToken").build(), DefaultValueTrait.idempotencyToken()).build();
    private static final SdkField<SdkBytes> SECRET_BINARY_FIELD = SdkField.builder(MarshallingType.SDK_BYTES).memberName("SecretBinary").getter(PutSecretValueRequest.getter(PutSecretValueRequest::secretBinary)).setter(PutSecretValueRequest.setter(Builder::secretBinary)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SecretBinary").build()).build();
    private static final SdkField<String> SECRET_STRING_FIELD = SdkField.builder(MarshallingType.STRING).memberName("SecretString").getter(PutSecretValueRequest.getter(PutSecretValueRequest::secretString)).setter(PutSecretValueRequest.setter(Builder::secretString)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SecretString").build()).build();
    private static final SdkField<List<String>> VERSION_STAGES_FIELD = SdkField.builder(MarshallingType.LIST).memberName("VersionStages").getter(PutSecretValueRequest.getter(PutSecretValueRequest::versionStages)).setter(PutSecretValueRequest.setter(Builder::versionStages)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("VersionStages").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder(MarshallingType.STRING).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").build()).build()).build()).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(SECRET_ID_FIELD, CLIENT_REQUEST_TOKEN_FIELD, SECRET_BINARY_FIELD, SECRET_STRING_FIELD, VERSION_STAGES_FIELD));
    private final String secretId;
    private final String clientRequestToken;
    private final SdkBytes secretBinary;
    private final String secretString;
    private final List<String> versionStages;

    private PutSecretValueRequest(BuilderImpl builder) {
        super(builder);
        this.secretId = builder.secretId;
        this.clientRequestToken = builder.clientRequestToken;
        this.secretBinary = builder.secretBinary;
        this.secretString = builder.secretString;
        this.versionStages = builder.versionStages;
    }

    public final String secretId() {
        return this.secretId;
    }

    public final String clientRequestToken() {
        return this.clientRequestToken;
    }

    public final SdkBytes secretBinary() {
        return this.secretBinary;
    }

    public final String secretString() {
        return this.secretString;
    }

    public final boolean hasVersionStages() {
        return this.versionStages != null && !(this.versionStages instanceof SdkAutoConstructList);
    }

    public final List<String> versionStages() {
        return this.versionStages;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.clientRequestToken());
        hashCode = 31 * hashCode + Objects.hashCode(this.secretBinary());
        hashCode = 31 * hashCode + Objects.hashCode(this.secretString());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasVersionStages() ? this.versionStages() : null);
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
        if (!(obj instanceof PutSecretValueRequest)) {
            return false;
        }
        PutSecretValueRequest other = (PutSecretValueRequest)obj;
        return Objects.equals(this.secretId(), other.secretId()) && Objects.equals(this.clientRequestToken(), other.clientRequestToken()) && Objects.equals(this.secretBinary(), other.secretBinary()) && Objects.equals(this.secretString(), other.secretString()) && this.hasVersionStages() == other.hasVersionStages() && Objects.equals(this.versionStages(), other.versionStages());
    }

    public final String toString() {
        return ToString.builder("PutSecretValueRequest").add("SecretId", this.secretId()).add("ClientRequestToken", this.clientRequestToken()).add("SecretBinary", this.secretBinary() == null ? null : "*** Sensitive Data Redacted ***").add("SecretString", this.secretString() == null ? null : "*** Sensitive Data Redacted ***").add("VersionStages", this.hasVersionStages() ? this.versionStages() : null).build();
    }

    @Override
    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "SecretId": {
                return Optional.ofNullable(clazz.cast(this.secretId()));
            }
            case "ClientRequestToken": {
                return Optional.ofNullable(clazz.cast(this.clientRequestToken()));
            }
            case "SecretBinary": {
                return Optional.ofNullable(clazz.cast(this.secretBinary()));
            }
            case "SecretString": {
                return Optional.ofNullable(clazz.cast(this.secretString()));
            }
            case "VersionStages": {
                return Optional.ofNullable(clazz.cast(this.versionStages()));
            }
        }
        return Optional.empty();
    }

    @Override
    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<PutSecretValueRequest, T> g) {
        return obj -> g.apply((PutSecretValueRequest)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends SecretsManagerRequest.BuilderImpl
    implements Builder {
        private String secretId;
        private String clientRequestToken;
        private SdkBytes secretBinary;
        private String secretString;
        private List<String> versionStages = DefaultSdkAutoConstructList.getInstance();

        private BuilderImpl() {
        }

        private BuilderImpl(PutSecretValueRequest model) {
            super(model);
            this.secretId(model.secretId);
            this.clientRequestToken(model.clientRequestToken);
            this.secretBinary(model.secretBinary);
            this.secretString(model.secretString);
            this.versionStages(model.versionStages);
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

        public final ByteBuffer getSecretBinary() {
            return this.secretBinary == null ? null : this.secretBinary.asByteBuffer();
        }

        public final void setSecretBinary(ByteBuffer secretBinary) {
            this.secretBinary(secretBinary == null ? null : SdkBytes.fromByteBuffer(secretBinary));
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

        public final Collection<String> getVersionStages() {
            if (this.versionStages instanceof SdkAutoConstructList) {
                return null;
            }
            return this.versionStages;
        }

        public final void setVersionStages(Collection<String> versionStages) {
            this.versionStages = SecretVersionStagesTypeCopier.copy(versionStages);
        }

        @Override
        public final Builder versionStages(Collection<String> versionStages) {
            this.versionStages = SecretVersionStagesTypeCopier.copy(versionStages);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder versionStages(String ... versionStages) {
            this.versionStages(Arrays.asList(versionStages));
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
        public PutSecretValueRequest build() {
            return new PutSecretValueRequest(this);
        }

        @Override
        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SecretsManagerRequest.Builder,
    SdkPojo,
    CopyableBuilder<Builder, PutSecretValueRequest> {
        public Builder secretId(String var1);

        public Builder clientRequestToken(String var1);

        public Builder secretBinary(SdkBytes var1);

        public Builder secretString(String var1);

        public Builder versionStages(Collection<String> var1);

        public Builder versionStages(String ... var1);

        @Override
        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        @Override
        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

