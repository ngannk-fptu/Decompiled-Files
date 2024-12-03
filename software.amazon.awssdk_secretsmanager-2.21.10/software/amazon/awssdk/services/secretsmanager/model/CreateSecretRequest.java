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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.DefaultValueTrait;
import software.amazon.awssdk.core.traits.ListTrait;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.services.secretsmanager.model.AddReplicaRegionListTypeCopier;
import software.amazon.awssdk.services.secretsmanager.model.ReplicaRegionType;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerRequest;
import software.amazon.awssdk.services.secretsmanager.model.Tag;
import software.amazon.awssdk.services.secretsmanager.model.TagListTypeCopier;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class CreateSecretRequest
extends SecretsManagerRequest
implements ToCopyableBuilder<Builder, CreateSecretRequest> {
    private static final SdkField<String> NAME_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Name").getter(CreateSecretRequest.getter(CreateSecretRequest::name)).setter(CreateSecretRequest.setter(Builder::name)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Name").build()}).build();
    private static final SdkField<String> CLIENT_REQUEST_TOKEN_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ClientRequestToken").getter(CreateSecretRequest.getter(CreateSecretRequest::clientRequestToken)).setter(CreateSecretRequest.setter(Builder::clientRequestToken)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ClientRequestToken").build(), DefaultValueTrait.idempotencyToken()}).build();
    private static final SdkField<String> DESCRIPTION_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Description").getter(CreateSecretRequest.getter(CreateSecretRequest::description)).setter(CreateSecretRequest.setter(Builder::description)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Description").build()}).build();
    private static final SdkField<String> KMS_KEY_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("KmsKeyId").getter(CreateSecretRequest.getter(CreateSecretRequest::kmsKeyId)).setter(CreateSecretRequest.setter(Builder::kmsKeyId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("KmsKeyId").build()}).build();
    private static final SdkField<SdkBytes> SECRET_BINARY_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_BYTES).memberName("SecretBinary").getter(CreateSecretRequest.getter(CreateSecretRequest::secretBinary)).setter(CreateSecretRequest.setter(Builder::secretBinary)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SecretBinary").build()}).build();
    private static final SdkField<String> SECRET_STRING_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SecretString").getter(CreateSecretRequest.getter(CreateSecretRequest::secretString)).setter(CreateSecretRequest.setter(Builder::secretString)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SecretString").build()}).build();
    private static final SdkField<List<Tag>> TAGS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("Tags").getter(CreateSecretRequest.getter(CreateSecretRequest::tags)).setter(CreateSecretRequest.setter(Builder::tags)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Tags").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(Tag::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").build()}).build()).build()}).build();
    private static final SdkField<List<ReplicaRegionType>> ADD_REPLICA_REGIONS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("AddReplicaRegions").getter(CreateSecretRequest.getter(CreateSecretRequest::addReplicaRegions)).setter(CreateSecretRequest.setter(Builder::addReplicaRegions)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("AddReplicaRegions").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(ReplicaRegionType::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").build()}).build()).build()}).build();
    private static final SdkField<Boolean> FORCE_OVERWRITE_REPLICA_SECRET_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("ForceOverwriteReplicaSecret").getter(CreateSecretRequest.getter(CreateSecretRequest::forceOverwriteReplicaSecret)).setter(CreateSecretRequest.setter(Builder::forceOverwriteReplicaSecret)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ForceOverwriteReplicaSecret").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(NAME_FIELD, CLIENT_REQUEST_TOKEN_FIELD, DESCRIPTION_FIELD, KMS_KEY_ID_FIELD, SECRET_BINARY_FIELD, SECRET_STRING_FIELD, TAGS_FIELD, ADD_REPLICA_REGIONS_FIELD, FORCE_OVERWRITE_REPLICA_SECRET_FIELD));
    private final String name;
    private final String clientRequestToken;
    private final String description;
    private final String kmsKeyId;
    private final SdkBytes secretBinary;
    private final String secretString;
    private final List<Tag> tags;
    private final List<ReplicaRegionType> addReplicaRegions;
    private final Boolean forceOverwriteReplicaSecret;

    private CreateSecretRequest(BuilderImpl builder) {
        super(builder);
        this.name = builder.name;
        this.clientRequestToken = builder.clientRequestToken;
        this.description = builder.description;
        this.kmsKeyId = builder.kmsKeyId;
        this.secretBinary = builder.secretBinary;
        this.secretString = builder.secretString;
        this.tags = builder.tags;
        this.addReplicaRegions = builder.addReplicaRegions;
        this.forceOverwriteReplicaSecret = builder.forceOverwriteReplicaSecret;
    }

    public final String name() {
        return this.name;
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

    public final boolean hasTags() {
        return this.tags != null && !(this.tags instanceof SdkAutoConstructList);
    }

    public final List<Tag> tags() {
        return this.tags;
    }

    public final boolean hasAddReplicaRegions() {
        return this.addReplicaRegions != null && !(this.addReplicaRegions instanceof SdkAutoConstructList);
    }

    public final List<ReplicaRegionType> addReplicaRegions() {
        return this.addReplicaRegions;
    }

    public final Boolean forceOverwriteReplicaSecret() {
        return this.forceOverwriteReplicaSecret;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.name());
        hashCode = 31 * hashCode + Objects.hashCode(this.clientRequestToken());
        hashCode = 31 * hashCode + Objects.hashCode(this.description());
        hashCode = 31 * hashCode + Objects.hashCode(this.kmsKeyId());
        hashCode = 31 * hashCode + Objects.hashCode(this.secretBinary());
        hashCode = 31 * hashCode + Objects.hashCode(this.secretString());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasTags() ? this.tags() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.hasAddReplicaRegions() ? this.addReplicaRegions() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.forceOverwriteReplicaSecret());
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
        if (!(obj instanceof CreateSecretRequest)) {
            return false;
        }
        CreateSecretRequest other = (CreateSecretRequest)((Object)obj);
        return Objects.equals(this.name(), other.name()) && Objects.equals(this.clientRequestToken(), other.clientRequestToken()) && Objects.equals(this.description(), other.description()) && Objects.equals(this.kmsKeyId(), other.kmsKeyId()) && Objects.equals(this.secretBinary(), other.secretBinary()) && Objects.equals(this.secretString(), other.secretString()) && this.hasTags() == other.hasTags() && Objects.equals(this.tags(), other.tags()) && this.hasAddReplicaRegions() == other.hasAddReplicaRegions() && Objects.equals(this.addReplicaRegions(), other.addReplicaRegions()) && Objects.equals(this.forceOverwriteReplicaSecret(), other.forceOverwriteReplicaSecret());
    }

    public final String toString() {
        return ToString.builder((String)"CreateSecretRequest").add("Name", (Object)this.name()).add("ClientRequestToken", (Object)this.clientRequestToken()).add("Description", (Object)this.description()).add("KmsKeyId", (Object)this.kmsKeyId()).add("SecretBinary", (Object)(this.secretBinary() == null ? null : "*** Sensitive Data Redacted ***")).add("SecretString", (Object)(this.secretString() == null ? null : "*** Sensitive Data Redacted ***")).add("Tags", this.hasTags() ? this.tags() : null).add("AddReplicaRegions", this.hasAddReplicaRegions() ? this.addReplicaRegions() : null).add("ForceOverwriteReplicaSecret", (Object)this.forceOverwriteReplicaSecret()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Name": {
                return Optional.ofNullable(clazz.cast(this.name()));
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
            case "Tags": {
                return Optional.ofNullable(clazz.cast(this.tags()));
            }
            case "AddReplicaRegions": {
                return Optional.ofNullable(clazz.cast(this.addReplicaRegions()));
            }
            case "ForceOverwriteReplicaSecret": {
                return Optional.ofNullable(clazz.cast(this.forceOverwriteReplicaSecret()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<CreateSecretRequest, T> g) {
        return obj -> g.apply((CreateSecretRequest)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends SecretsManagerRequest.BuilderImpl
    implements Builder {
        private String name;
        private String clientRequestToken;
        private String description;
        private String kmsKeyId;
        private SdkBytes secretBinary;
        private String secretString;
        private List<Tag> tags = DefaultSdkAutoConstructList.getInstance();
        private List<ReplicaRegionType> addReplicaRegions = DefaultSdkAutoConstructList.getInstance();
        private Boolean forceOverwriteReplicaSecret;

        private BuilderImpl() {
        }

        private BuilderImpl(CreateSecretRequest model) {
            super(model);
            this.name(model.name);
            this.clientRequestToken(model.clientRequestToken);
            this.description(model.description);
            this.kmsKeyId(model.kmsKeyId);
            this.secretBinary(model.secretBinary);
            this.secretString(model.secretString);
            this.tags(model.tags);
            this.addReplicaRegions(model.addReplicaRegions);
            this.forceOverwriteReplicaSecret(model.forceOverwriteReplicaSecret);
        }

        public final String getName() {
            return this.name;
        }

        public final void setName(String name) {
            this.name = name;
        }

        @Override
        public final Builder name(String name) {
            this.name = name;
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

        public final List<Tag.Builder> getTags() {
            List<Tag.Builder> result = TagListTypeCopier.copyToBuilder(this.tags);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setTags(Collection<Tag.BuilderImpl> tags) {
            this.tags = TagListTypeCopier.copyFromBuilder(tags);
        }

        @Override
        public final Builder tags(Collection<Tag> tags) {
            this.tags = TagListTypeCopier.copy(tags);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder tags(Tag ... tags) {
            this.tags(Arrays.asList(tags));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder tags(Consumer<Tag.Builder> ... tags) {
            this.tags(Stream.of(tags).map(c -> (Tag)((Tag.Builder)Tag.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        public final List<ReplicaRegionType.Builder> getAddReplicaRegions() {
            List<ReplicaRegionType.Builder> result = AddReplicaRegionListTypeCopier.copyToBuilder(this.addReplicaRegions);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setAddReplicaRegions(Collection<ReplicaRegionType.BuilderImpl> addReplicaRegions) {
            this.addReplicaRegions = AddReplicaRegionListTypeCopier.copyFromBuilder(addReplicaRegions);
        }

        @Override
        public final Builder addReplicaRegions(Collection<ReplicaRegionType> addReplicaRegions) {
            this.addReplicaRegions = AddReplicaRegionListTypeCopier.copy(addReplicaRegions);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder addReplicaRegions(ReplicaRegionType ... addReplicaRegions) {
            this.addReplicaRegions(Arrays.asList(addReplicaRegions));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder addReplicaRegions(Consumer<ReplicaRegionType.Builder> ... addReplicaRegions) {
            this.addReplicaRegions(Stream.of(addReplicaRegions).map(c -> (ReplicaRegionType)((ReplicaRegionType.Builder)ReplicaRegionType.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        public final Boolean getForceOverwriteReplicaSecret() {
            return this.forceOverwriteReplicaSecret;
        }

        public final void setForceOverwriteReplicaSecret(Boolean forceOverwriteReplicaSecret) {
            this.forceOverwriteReplicaSecret = forceOverwriteReplicaSecret;
        }

        @Override
        public final Builder forceOverwriteReplicaSecret(Boolean forceOverwriteReplicaSecret) {
            this.forceOverwriteReplicaSecret = forceOverwriteReplicaSecret;
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
        public CreateSecretRequest build() {
            return new CreateSecretRequest(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SecretsManagerRequest.Builder,
    SdkPojo,
    CopyableBuilder<Builder, CreateSecretRequest> {
        public Builder name(String var1);

        public Builder clientRequestToken(String var1);

        public Builder description(String var1);

        public Builder kmsKeyId(String var1);

        public Builder secretBinary(SdkBytes var1);

        public Builder secretString(String var1);

        public Builder tags(Collection<Tag> var1);

        public Builder tags(Tag ... var1);

        public Builder tags(Consumer<Tag.Builder> ... var1);

        public Builder addReplicaRegions(Collection<ReplicaRegionType> var1);

        public Builder addReplicaRegions(ReplicaRegionType ... var1);

        public Builder addReplicaRegions(Consumer<ReplicaRegionType.Builder> ... var1);

        public Builder forceOverwriteReplicaSecret(Boolean var1);

        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

