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
 *  software.amazon.awssdk.core.traits.DefaultValueTrait
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.Trait
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
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
import software.amazon.awssdk.core.traits.DefaultValueTrait;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.secretsmanager.model.RotationRulesType;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerRequest;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class RotateSecretRequest
extends SecretsManagerRequest
implements ToCopyableBuilder<Builder, RotateSecretRequest> {
    private static final SdkField<String> SECRET_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SecretId").getter(RotateSecretRequest.getter(RotateSecretRequest::secretId)).setter(RotateSecretRequest.setter(Builder::secretId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SecretId").build()}).build();
    private static final SdkField<String> CLIENT_REQUEST_TOKEN_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ClientRequestToken").getter(RotateSecretRequest.getter(RotateSecretRequest::clientRequestToken)).setter(RotateSecretRequest.setter(Builder::clientRequestToken)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ClientRequestToken").build(), DefaultValueTrait.idempotencyToken()}).build();
    private static final SdkField<String> ROTATION_LAMBDA_ARN_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("RotationLambdaARN").getter(RotateSecretRequest.getter(RotateSecretRequest::rotationLambdaARN)).setter(RotateSecretRequest.setter(Builder::rotationLambdaARN)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("RotationLambdaARN").build()}).build();
    private static final SdkField<RotationRulesType> ROTATION_RULES_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("RotationRules").getter(RotateSecretRequest.getter(RotateSecretRequest::rotationRules)).setter(RotateSecretRequest.setter(Builder::rotationRules)).constructor(RotationRulesType::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("RotationRules").build()}).build();
    private static final SdkField<Boolean> ROTATE_IMMEDIATELY_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("RotateImmediately").getter(RotateSecretRequest.getter(RotateSecretRequest::rotateImmediately)).setter(RotateSecretRequest.setter(Builder::rotateImmediately)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("RotateImmediately").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(SECRET_ID_FIELD, CLIENT_REQUEST_TOKEN_FIELD, ROTATION_LAMBDA_ARN_FIELD, ROTATION_RULES_FIELD, ROTATE_IMMEDIATELY_FIELD));
    private final String secretId;
    private final String clientRequestToken;
    private final String rotationLambdaARN;
    private final RotationRulesType rotationRules;
    private final Boolean rotateImmediately;

    private RotateSecretRequest(BuilderImpl builder) {
        super(builder);
        this.secretId = builder.secretId;
        this.clientRequestToken = builder.clientRequestToken;
        this.rotationLambdaARN = builder.rotationLambdaARN;
        this.rotationRules = builder.rotationRules;
        this.rotateImmediately = builder.rotateImmediately;
    }

    public final String secretId() {
        return this.secretId;
    }

    public final String clientRequestToken() {
        return this.clientRequestToken;
    }

    public final String rotationLambdaARN() {
        return this.rotationLambdaARN;
    }

    public final RotationRulesType rotationRules() {
        return this.rotationRules;
    }

    public final Boolean rotateImmediately() {
        return this.rotateImmediately;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.rotationLambdaARN());
        hashCode = 31 * hashCode + Objects.hashCode(this.rotationRules());
        hashCode = 31 * hashCode + Objects.hashCode(this.rotateImmediately());
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
        if (!(obj instanceof RotateSecretRequest)) {
            return false;
        }
        RotateSecretRequest other = (RotateSecretRequest)((Object)obj);
        return Objects.equals(this.secretId(), other.secretId()) && Objects.equals(this.clientRequestToken(), other.clientRequestToken()) && Objects.equals(this.rotationLambdaARN(), other.rotationLambdaARN()) && Objects.equals(this.rotationRules(), other.rotationRules()) && Objects.equals(this.rotateImmediately(), other.rotateImmediately());
    }

    public final String toString() {
        return ToString.builder((String)"RotateSecretRequest").add("SecretId", (Object)this.secretId()).add("ClientRequestToken", (Object)this.clientRequestToken()).add("RotationLambdaARN", (Object)this.rotationLambdaARN()).add("RotationRules", (Object)this.rotationRules()).add("RotateImmediately", (Object)this.rotateImmediately()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "SecretId": {
                return Optional.ofNullable(clazz.cast(this.secretId()));
            }
            case "ClientRequestToken": {
                return Optional.ofNullable(clazz.cast(this.clientRequestToken()));
            }
            case "RotationLambdaARN": {
                return Optional.ofNullable(clazz.cast(this.rotationLambdaARN()));
            }
            case "RotationRules": {
                return Optional.ofNullable(clazz.cast(this.rotationRules()));
            }
            case "RotateImmediately": {
                return Optional.ofNullable(clazz.cast(this.rotateImmediately()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<RotateSecretRequest, T> g) {
        return obj -> g.apply((RotateSecretRequest)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends SecretsManagerRequest.BuilderImpl
    implements Builder {
        private String secretId;
        private String clientRequestToken;
        private String rotationLambdaARN;
        private RotationRulesType rotationRules;
        private Boolean rotateImmediately;

        private BuilderImpl() {
        }

        private BuilderImpl(RotateSecretRequest model) {
            super(model);
            this.secretId(model.secretId);
            this.clientRequestToken(model.clientRequestToken);
            this.rotationLambdaARN(model.rotationLambdaARN);
            this.rotationRules(model.rotationRules);
            this.rotateImmediately(model.rotateImmediately);
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

        public final String getRotationLambdaARN() {
            return this.rotationLambdaARN;
        }

        public final void setRotationLambdaARN(String rotationLambdaARN) {
            this.rotationLambdaARN = rotationLambdaARN;
        }

        @Override
        public final Builder rotationLambdaARN(String rotationLambdaARN) {
            this.rotationLambdaARN = rotationLambdaARN;
            return this;
        }

        public final RotationRulesType.Builder getRotationRules() {
            return this.rotationRules != null ? this.rotationRules.toBuilder() : null;
        }

        public final void setRotationRules(RotationRulesType.BuilderImpl rotationRules) {
            this.rotationRules = rotationRules != null ? rotationRules.build() : null;
        }

        @Override
        public final Builder rotationRules(RotationRulesType rotationRules) {
            this.rotationRules = rotationRules;
            return this;
        }

        public final Boolean getRotateImmediately() {
            return this.rotateImmediately;
        }

        public final void setRotateImmediately(Boolean rotateImmediately) {
            this.rotateImmediately = rotateImmediately;
        }

        @Override
        public final Builder rotateImmediately(Boolean rotateImmediately) {
            this.rotateImmediately = rotateImmediately;
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
        public RotateSecretRequest build() {
            return new RotateSecretRequest(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SecretsManagerRequest.Builder,
    SdkPojo,
    CopyableBuilder<Builder, RotateSecretRequest> {
        public Builder secretId(String var1);

        public Builder clientRequestToken(String var1);

        public Builder rotationLambdaARN(String var1);

        public Builder rotationRules(RotationRulesType var1);

        default public Builder rotationRules(Consumer<RotationRulesType.Builder> rotationRules) {
            return this.rotationRules((RotationRulesType)((RotationRulesType.Builder)RotationRulesType.builder().applyMutation(rotationRules)).build());
        }

        public Builder rotateImmediately(Boolean var1);

        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

