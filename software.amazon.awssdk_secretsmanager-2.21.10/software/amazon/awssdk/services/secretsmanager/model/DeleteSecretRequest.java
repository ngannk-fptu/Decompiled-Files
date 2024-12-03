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
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerRequest;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class DeleteSecretRequest
extends SecretsManagerRequest
implements ToCopyableBuilder<Builder, DeleteSecretRequest> {
    private static final SdkField<String> SECRET_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SecretId").getter(DeleteSecretRequest.getter(DeleteSecretRequest::secretId)).setter(DeleteSecretRequest.setter(Builder::secretId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SecretId").build()}).build();
    private static final SdkField<Long> RECOVERY_WINDOW_IN_DAYS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LONG).memberName("RecoveryWindowInDays").getter(DeleteSecretRequest.getter(DeleteSecretRequest::recoveryWindowInDays)).setter(DeleteSecretRequest.setter(Builder::recoveryWindowInDays)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("RecoveryWindowInDays").build()}).build();
    private static final SdkField<Boolean> FORCE_DELETE_WITHOUT_RECOVERY_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("ForceDeleteWithoutRecovery").getter(DeleteSecretRequest.getter(DeleteSecretRequest::forceDeleteWithoutRecovery)).setter(DeleteSecretRequest.setter(Builder::forceDeleteWithoutRecovery)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ForceDeleteWithoutRecovery").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(SECRET_ID_FIELD, RECOVERY_WINDOW_IN_DAYS_FIELD, FORCE_DELETE_WITHOUT_RECOVERY_FIELD));
    private final String secretId;
    private final Long recoveryWindowInDays;
    private final Boolean forceDeleteWithoutRecovery;

    private DeleteSecretRequest(BuilderImpl builder) {
        super(builder);
        this.secretId = builder.secretId;
        this.recoveryWindowInDays = builder.recoveryWindowInDays;
        this.forceDeleteWithoutRecovery = builder.forceDeleteWithoutRecovery;
    }

    public final String secretId() {
        return this.secretId;
    }

    public final Long recoveryWindowInDays() {
        return this.recoveryWindowInDays;
    }

    public final Boolean forceDeleteWithoutRecovery() {
        return this.forceDeleteWithoutRecovery;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.recoveryWindowInDays());
        hashCode = 31 * hashCode + Objects.hashCode(this.forceDeleteWithoutRecovery());
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
        if (!(obj instanceof DeleteSecretRequest)) {
            return false;
        }
        DeleteSecretRequest other = (DeleteSecretRequest)((Object)obj);
        return Objects.equals(this.secretId(), other.secretId()) && Objects.equals(this.recoveryWindowInDays(), other.recoveryWindowInDays()) && Objects.equals(this.forceDeleteWithoutRecovery(), other.forceDeleteWithoutRecovery());
    }

    public final String toString() {
        return ToString.builder((String)"DeleteSecretRequest").add("SecretId", (Object)this.secretId()).add("RecoveryWindowInDays", (Object)this.recoveryWindowInDays()).add("ForceDeleteWithoutRecovery", (Object)this.forceDeleteWithoutRecovery()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "SecretId": {
                return Optional.ofNullable(clazz.cast(this.secretId()));
            }
            case "RecoveryWindowInDays": {
                return Optional.ofNullable(clazz.cast(this.recoveryWindowInDays()));
            }
            case "ForceDeleteWithoutRecovery": {
                return Optional.ofNullable(clazz.cast(this.forceDeleteWithoutRecovery()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<DeleteSecretRequest, T> g) {
        return obj -> g.apply((DeleteSecretRequest)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends SecretsManagerRequest.BuilderImpl
    implements Builder {
        private String secretId;
        private Long recoveryWindowInDays;
        private Boolean forceDeleteWithoutRecovery;

        private BuilderImpl() {
        }

        private BuilderImpl(DeleteSecretRequest model) {
            super(model);
            this.secretId(model.secretId);
            this.recoveryWindowInDays(model.recoveryWindowInDays);
            this.forceDeleteWithoutRecovery(model.forceDeleteWithoutRecovery);
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

        public final Long getRecoveryWindowInDays() {
            return this.recoveryWindowInDays;
        }

        public final void setRecoveryWindowInDays(Long recoveryWindowInDays) {
            this.recoveryWindowInDays = recoveryWindowInDays;
        }

        @Override
        public final Builder recoveryWindowInDays(Long recoveryWindowInDays) {
            this.recoveryWindowInDays = recoveryWindowInDays;
            return this;
        }

        public final Boolean getForceDeleteWithoutRecovery() {
            return this.forceDeleteWithoutRecovery;
        }

        public final void setForceDeleteWithoutRecovery(Boolean forceDeleteWithoutRecovery) {
            this.forceDeleteWithoutRecovery = forceDeleteWithoutRecovery;
        }

        @Override
        public final Builder forceDeleteWithoutRecovery(Boolean forceDeleteWithoutRecovery) {
            this.forceDeleteWithoutRecovery = forceDeleteWithoutRecovery;
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
        public DeleteSecretRequest build() {
            return new DeleteSecretRequest(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SecretsManagerRequest.Builder,
    SdkPojo,
    CopyableBuilder<Builder, DeleteSecretRequest> {
        public Builder secretId(String var1);

        public Builder recoveryWindowInDays(Long var1);

        public Builder forceDeleteWithoutRecovery(Boolean var1);

        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

