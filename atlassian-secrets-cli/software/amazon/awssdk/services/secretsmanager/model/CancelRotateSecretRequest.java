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

public final class CancelRotateSecretRequest
extends SecretsManagerRequest
implements ToCopyableBuilder<Builder, CancelRotateSecretRequest> {
    private static final SdkField<String> SECRET_ID_FIELD = SdkField.builder(MarshallingType.STRING).memberName("SecretId").getter(CancelRotateSecretRequest.getter(CancelRotateSecretRequest::secretId)).setter(CancelRotateSecretRequest.setter(Builder::secretId)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SecretId").build()).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(SECRET_ID_FIELD));
    private final String secretId;

    private CancelRotateSecretRequest(BuilderImpl builder) {
        super(builder);
        this.secretId = builder.secretId;
    }

    public final String secretId() {
        return this.secretId;
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
        if (!(obj instanceof CancelRotateSecretRequest)) {
            return false;
        }
        CancelRotateSecretRequest other = (CancelRotateSecretRequest)obj;
        return Objects.equals(this.secretId(), other.secretId());
    }

    public final String toString() {
        return ToString.builder("CancelRotateSecretRequest").add("SecretId", this.secretId()).build();
    }

    @Override
    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "SecretId": {
                return Optional.ofNullable(clazz.cast(this.secretId()));
            }
        }
        return Optional.empty();
    }

    @Override
    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<CancelRotateSecretRequest, T> g) {
        return obj -> g.apply((CancelRotateSecretRequest)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends SecretsManagerRequest.BuilderImpl
    implements Builder {
        private String secretId;

        private BuilderImpl() {
        }

        private BuilderImpl(CancelRotateSecretRequest model) {
            super(model);
            this.secretId(model.secretId);
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
        public CancelRotateSecretRequest build() {
            return new CancelRotateSecretRequest(this);
        }

        @Override
        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SecretsManagerRequest.Builder,
    SdkPojo,
    CopyableBuilder<Builder, CancelRotateSecretRequest> {
        public Builder secretId(String var1);

        @Override
        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        @Override
        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

