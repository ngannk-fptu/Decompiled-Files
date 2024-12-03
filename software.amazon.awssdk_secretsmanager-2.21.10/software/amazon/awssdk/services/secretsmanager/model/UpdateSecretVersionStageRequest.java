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

public final class UpdateSecretVersionStageRequest
extends SecretsManagerRequest
implements ToCopyableBuilder<Builder, UpdateSecretVersionStageRequest> {
    private static final SdkField<String> SECRET_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SecretId").getter(UpdateSecretVersionStageRequest.getter(UpdateSecretVersionStageRequest::secretId)).setter(UpdateSecretVersionStageRequest.setter(Builder::secretId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SecretId").build()}).build();
    private static final SdkField<String> VERSION_STAGE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("VersionStage").getter(UpdateSecretVersionStageRequest.getter(UpdateSecretVersionStageRequest::versionStage)).setter(UpdateSecretVersionStageRequest.setter(Builder::versionStage)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("VersionStage").build()}).build();
    private static final SdkField<String> REMOVE_FROM_VERSION_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("RemoveFromVersionId").getter(UpdateSecretVersionStageRequest.getter(UpdateSecretVersionStageRequest::removeFromVersionId)).setter(UpdateSecretVersionStageRequest.setter(Builder::removeFromVersionId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("RemoveFromVersionId").build()}).build();
    private static final SdkField<String> MOVE_TO_VERSION_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("MoveToVersionId").getter(UpdateSecretVersionStageRequest.getter(UpdateSecretVersionStageRequest::moveToVersionId)).setter(UpdateSecretVersionStageRequest.setter(Builder::moveToVersionId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("MoveToVersionId").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(SECRET_ID_FIELD, VERSION_STAGE_FIELD, REMOVE_FROM_VERSION_ID_FIELD, MOVE_TO_VERSION_ID_FIELD));
    private final String secretId;
    private final String versionStage;
    private final String removeFromVersionId;
    private final String moveToVersionId;

    private UpdateSecretVersionStageRequest(BuilderImpl builder) {
        super(builder);
        this.secretId = builder.secretId;
        this.versionStage = builder.versionStage;
        this.removeFromVersionId = builder.removeFromVersionId;
        this.moveToVersionId = builder.moveToVersionId;
    }

    public final String secretId() {
        return this.secretId;
    }

    public final String versionStage() {
        return this.versionStage;
    }

    public final String removeFromVersionId() {
        return this.removeFromVersionId;
    }

    public final String moveToVersionId() {
        return this.moveToVersionId;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.versionStage());
        hashCode = 31 * hashCode + Objects.hashCode(this.removeFromVersionId());
        hashCode = 31 * hashCode + Objects.hashCode(this.moveToVersionId());
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
        if (!(obj instanceof UpdateSecretVersionStageRequest)) {
            return false;
        }
        UpdateSecretVersionStageRequest other = (UpdateSecretVersionStageRequest)((Object)obj);
        return Objects.equals(this.secretId(), other.secretId()) && Objects.equals(this.versionStage(), other.versionStage()) && Objects.equals(this.removeFromVersionId(), other.removeFromVersionId()) && Objects.equals(this.moveToVersionId(), other.moveToVersionId());
    }

    public final String toString() {
        return ToString.builder((String)"UpdateSecretVersionStageRequest").add("SecretId", (Object)this.secretId()).add("VersionStage", (Object)this.versionStage()).add("RemoveFromVersionId", (Object)this.removeFromVersionId()).add("MoveToVersionId", (Object)this.moveToVersionId()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "SecretId": {
                return Optional.ofNullable(clazz.cast(this.secretId()));
            }
            case "VersionStage": {
                return Optional.ofNullable(clazz.cast(this.versionStage()));
            }
            case "RemoveFromVersionId": {
                return Optional.ofNullable(clazz.cast(this.removeFromVersionId()));
            }
            case "MoveToVersionId": {
                return Optional.ofNullable(clazz.cast(this.moveToVersionId()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<UpdateSecretVersionStageRequest, T> g) {
        return obj -> g.apply((UpdateSecretVersionStageRequest)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends SecretsManagerRequest.BuilderImpl
    implements Builder {
        private String secretId;
        private String versionStage;
        private String removeFromVersionId;
        private String moveToVersionId;

        private BuilderImpl() {
        }

        private BuilderImpl(UpdateSecretVersionStageRequest model) {
            super(model);
            this.secretId(model.secretId);
            this.versionStage(model.versionStage);
            this.removeFromVersionId(model.removeFromVersionId);
            this.moveToVersionId(model.moveToVersionId);
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

        public final String getRemoveFromVersionId() {
            return this.removeFromVersionId;
        }

        public final void setRemoveFromVersionId(String removeFromVersionId) {
            this.removeFromVersionId = removeFromVersionId;
        }

        @Override
        public final Builder removeFromVersionId(String removeFromVersionId) {
            this.removeFromVersionId = removeFromVersionId;
            return this;
        }

        public final String getMoveToVersionId() {
            return this.moveToVersionId;
        }

        public final void setMoveToVersionId(String moveToVersionId) {
            this.moveToVersionId = moveToVersionId;
        }

        @Override
        public final Builder moveToVersionId(String moveToVersionId) {
            this.moveToVersionId = moveToVersionId;
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
        public UpdateSecretVersionStageRequest build() {
            return new UpdateSecretVersionStageRequest(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SecretsManagerRequest.Builder,
    SdkPojo,
    CopyableBuilder<Builder, UpdateSecretVersionStageRequest> {
        public Builder secretId(String var1);

        public Builder versionStage(String var1);

        public Builder removeFromVersionId(String var1);

        public Builder moveToVersionId(String var1);

        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

