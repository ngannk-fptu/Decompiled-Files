/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.model;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.ListTrait;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.MapTrait;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructMap;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructMap;
import software.amazon.awssdk.services.secretsmanager.model.ReplicationStatusListTypeCopier;
import software.amazon.awssdk.services.secretsmanager.model.ReplicationStatusType;
import software.amazon.awssdk.services.secretsmanager.model.RotationRulesType;
import software.amazon.awssdk.services.secretsmanager.model.SecretVersionsToStagesMapTypeCopier;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerResponse;
import software.amazon.awssdk.services.secretsmanager.model.Tag;
import software.amazon.awssdk.services.secretsmanager.model.TagListTypeCopier;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class DescribeSecretResponse
extends SecretsManagerResponse
implements ToCopyableBuilder<Builder, DescribeSecretResponse> {
    private static final SdkField<String> ARN_FIELD = SdkField.builder(MarshallingType.STRING).memberName("ARN").getter(DescribeSecretResponse.getter(DescribeSecretResponse::arn)).setter(DescribeSecretResponse.setter(Builder::arn)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ARN").build()).build();
    private static final SdkField<String> NAME_FIELD = SdkField.builder(MarshallingType.STRING).memberName("Name").getter(DescribeSecretResponse.getter(DescribeSecretResponse::name)).setter(DescribeSecretResponse.setter(Builder::name)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Name").build()).build();
    private static final SdkField<String> DESCRIPTION_FIELD = SdkField.builder(MarshallingType.STRING).memberName("Description").getter(DescribeSecretResponse.getter(DescribeSecretResponse::description)).setter(DescribeSecretResponse.setter(Builder::description)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Description").build()).build();
    private static final SdkField<String> KMS_KEY_ID_FIELD = SdkField.builder(MarshallingType.STRING).memberName("KmsKeyId").getter(DescribeSecretResponse.getter(DescribeSecretResponse::kmsKeyId)).setter(DescribeSecretResponse.setter(Builder::kmsKeyId)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("KmsKeyId").build()).build();
    private static final SdkField<Boolean> ROTATION_ENABLED_FIELD = SdkField.builder(MarshallingType.BOOLEAN).memberName("RotationEnabled").getter(DescribeSecretResponse.getter(DescribeSecretResponse::rotationEnabled)).setter(DescribeSecretResponse.setter(Builder::rotationEnabled)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("RotationEnabled").build()).build();
    private static final SdkField<String> ROTATION_LAMBDA_ARN_FIELD = SdkField.builder(MarshallingType.STRING).memberName("RotationLambdaARN").getter(DescribeSecretResponse.getter(DescribeSecretResponse::rotationLambdaARN)).setter(DescribeSecretResponse.setter(Builder::rotationLambdaARN)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("RotationLambdaARN").build()).build();
    private static final SdkField<RotationRulesType> ROTATION_RULES_FIELD = SdkField.builder(MarshallingType.SDK_POJO).memberName("RotationRules").getter(DescribeSecretResponse.getter(DescribeSecretResponse::rotationRules)).setter(DescribeSecretResponse.setter(Builder::rotationRules)).constructor(RotationRulesType::builder).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("RotationRules").build()).build();
    private static final SdkField<Instant> LAST_ROTATED_DATE_FIELD = SdkField.builder(MarshallingType.INSTANT).memberName("LastRotatedDate").getter(DescribeSecretResponse.getter(DescribeSecretResponse::lastRotatedDate)).setter(DescribeSecretResponse.setter(Builder::lastRotatedDate)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("LastRotatedDate").build()).build();
    private static final SdkField<Instant> LAST_CHANGED_DATE_FIELD = SdkField.builder(MarshallingType.INSTANT).memberName("LastChangedDate").getter(DescribeSecretResponse.getter(DescribeSecretResponse::lastChangedDate)).setter(DescribeSecretResponse.setter(Builder::lastChangedDate)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("LastChangedDate").build()).build();
    private static final SdkField<Instant> LAST_ACCESSED_DATE_FIELD = SdkField.builder(MarshallingType.INSTANT).memberName("LastAccessedDate").getter(DescribeSecretResponse.getter(DescribeSecretResponse::lastAccessedDate)).setter(DescribeSecretResponse.setter(Builder::lastAccessedDate)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("LastAccessedDate").build()).build();
    private static final SdkField<Instant> DELETED_DATE_FIELD = SdkField.builder(MarshallingType.INSTANT).memberName("DeletedDate").getter(DescribeSecretResponse.getter(DescribeSecretResponse::deletedDate)).setter(DescribeSecretResponse.setter(Builder::deletedDate)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("DeletedDate").build()).build();
    private static final SdkField<Instant> NEXT_ROTATION_DATE_FIELD = SdkField.builder(MarshallingType.INSTANT).memberName("NextRotationDate").getter(DescribeSecretResponse.getter(DescribeSecretResponse::nextRotationDate)).setter(DescribeSecretResponse.setter(Builder::nextRotationDate)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("NextRotationDate").build()).build();
    private static final SdkField<List<Tag>> TAGS_FIELD = SdkField.builder(MarshallingType.LIST).memberName("Tags").getter(DescribeSecretResponse.getter(DescribeSecretResponse::tags)).setter(DescribeSecretResponse.setter(Builder::tags)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Tags").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder(MarshallingType.SDK_POJO).constructor(Tag::builder).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").build()).build()).build()).build();
    private static final SdkField<Map<String, List<String>>> VERSION_IDS_TO_STAGES_FIELD = SdkField.builder(MarshallingType.MAP).memberName("VersionIdsToStages").getter(DescribeSecretResponse.getter(DescribeSecretResponse::versionIdsToStages)).setter(DescribeSecretResponse.setter(Builder::versionIdsToStages)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("VersionIdsToStages").build(), MapTrait.builder().keyLocationName("key").valueLocationName("value").valueFieldInfo(SdkField.builder(MarshallingType.LIST).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("value").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder(MarshallingType.STRING).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").build()).build()).build()).build()).build()).build();
    private static final SdkField<String> OWNING_SERVICE_FIELD = SdkField.builder(MarshallingType.STRING).memberName("OwningService").getter(DescribeSecretResponse.getter(DescribeSecretResponse::owningService)).setter(DescribeSecretResponse.setter(Builder::owningService)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("OwningService").build()).build();
    private static final SdkField<Instant> CREATED_DATE_FIELD = SdkField.builder(MarshallingType.INSTANT).memberName("CreatedDate").getter(DescribeSecretResponse.getter(DescribeSecretResponse::createdDate)).setter(DescribeSecretResponse.setter(Builder::createdDate)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("CreatedDate").build()).build();
    private static final SdkField<String> PRIMARY_REGION_FIELD = SdkField.builder(MarshallingType.STRING).memberName("PrimaryRegion").getter(DescribeSecretResponse.getter(DescribeSecretResponse::primaryRegion)).setter(DescribeSecretResponse.setter(Builder::primaryRegion)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("PrimaryRegion").build()).build();
    private static final SdkField<List<ReplicationStatusType>> REPLICATION_STATUS_FIELD = SdkField.builder(MarshallingType.LIST).memberName("ReplicationStatus").getter(DescribeSecretResponse.getter(DescribeSecretResponse::replicationStatus)).setter(DescribeSecretResponse.setter(Builder::replicationStatus)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ReplicationStatus").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder(MarshallingType.SDK_POJO).constructor(ReplicationStatusType::builder).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").build()).build()).build()).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(ARN_FIELD, NAME_FIELD, DESCRIPTION_FIELD, KMS_KEY_ID_FIELD, ROTATION_ENABLED_FIELD, ROTATION_LAMBDA_ARN_FIELD, ROTATION_RULES_FIELD, LAST_ROTATED_DATE_FIELD, LAST_CHANGED_DATE_FIELD, LAST_ACCESSED_DATE_FIELD, DELETED_DATE_FIELD, NEXT_ROTATION_DATE_FIELD, TAGS_FIELD, VERSION_IDS_TO_STAGES_FIELD, OWNING_SERVICE_FIELD, CREATED_DATE_FIELD, PRIMARY_REGION_FIELD, REPLICATION_STATUS_FIELD));
    private final String arn;
    private final String name;
    private final String description;
    private final String kmsKeyId;
    private final Boolean rotationEnabled;
    private final String rotationLambdaARN;
    private final RotationRulesType rotationRules;
    private final Instant lastRotatedDate;
    private final Instant lastChangedDate;
    private final Instant lastAccessedDate;
    private final Instant deletedDate;
    private final Instant nextRotationDate;
    private final List<Tag> tags;
    private final Map<String, List<String>> versionIdsToStages;
    private final String owningService;
    private final Instant createdDate;
    private final String primaryRegion;
    private final List<ReplicationStatusType> replicationStatus;

    private DescribeSecretResponse(BuilderImpl builder) {
        super(builder);
        this.arn = builder.arn;
        this.name = builder.name;
        this.description = builder.description;
        this.kmsKeyId = builder.kmsKeyId;
        this.rotationEnabled = builder.rotationEnabled;
        this.rotationLambdaARN = builder.rotationLambdaARN;
        this.rotationRules = builder.rotationRules;
        this.lastRotatedDate = builder.lastRotatedDate;
        this.lastChangedDate = builder.lastChangedDate;
        this.lastAccessedDate = builder.lastAccessedDate;
        this.deletedDate = builder.deletedDate;
        this.nextRotationDate = builder.nextRotationDate;
        this.tags = builder.tags;
        this.versionIdsToStages = builder.versionIdsToStages;
        this.owningService = builder.owningService;
        this.createdDate = builder.createdDate;
        this.primaryRegion = builder.primaryRegion;
        this.replicationStatus = builder.replicationStatus;
    }

    public final String arn() {
        return this.arn;
    }

    public final String name() {
        return this.name;
    }

    public final String description() {
        return this.description;
    }

    public final String kmsKeyId() {
        return this.kmsKeyId;
    }

    public final Boolean rotationEnabled() {
        return this.rotationEnabled;
    }

    public final String rotationLambdaARN() {
        return this.rotationLambdaARN;
    }

    public final RotationRulesType rotationRules() {
        return this.rotationRules;
    }

    public final Instant lastRotatedDate() {
        return this.lastRotatedDate;
    }

    public final Instant lastChangedDate() {
        return this.lastChangedDate;
    }

    public final Instant lastAccessedDate() {
        return this.lastAccessedDate;
    }

    public final Instant deletedDate() {
        return this.deletedDate;
    }

    public final Instant nextRotationDate() {
        return this.nextRotationDate;
    }

    public final boolean hasTags() {
        return this.tags != null && !(this.tags instanceof SdkAutoConstructList);
    }

    public final List<Tag> tags() {
        return this.tags;
    }

    public final boolean hasVersionIdsToStages() {
        return this.versionIdsToStages != null && !(this.versionIdsToStages instanceof SdkAutoConstructMap);
    }

    public final Map<String, List<String>> versionIdsToStages() {
        return this.versionIdsToStages;
    }

    public final String owningService() {
        return this.owningService;
    }

    public final Instant createdDate() {
        return this.createdDate;
    }

    public final String primaryRegion() {
        return this.primaryRegion;
    }

    public final boolean hasReplicationStatus() {
        return this.replicationStatus != null && !(this.replicationStatus instanceof SdkAutoConstructList);
    }

    public final List<ReplicationStatusType> replicationStatus() {
        return this.replicationStatus;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.arn());
        hashCode = 31 * hashCode + Objects.hashCode(this.name());
        hashCode = 31 * hashCode + Objects.hashCode(this.description());
        hashCode = 31 * hashCode + Objects.hashCode(this.kmsKeyId());
        hashCode = 31 * hashCode + Objects.hashCode(this.rotationEnabled());
        hashCode = 31 * hashCode + Objects.hashCode(this.rotationLambdaARN());
        hashCode = 31 * hashCode + Objects.hashCode(this.rotationRules());
        hashCode = 31 * hashCode + Objects.hashCode(this.lastRotatedDate());
        hashCode = 31 * hashCode + Objects.hashCode(this.lastChangedDate());
        hashCode = 31 * hashCode + Objects.hashCode(this.lastAccessedDate());
        hashCode = 31 * hashCode + Objects.hashCode(this.deletedDate());
        hashCode = 31 * hashCode + Objects.hashCode(this.nextRotationDate());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasTags() ? this.tags() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.hasVersionIdsToStages() ? this.versionIdsToStages() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.owningService());
        hashCode = 31 * hashCode + Objects.hashCode(this.createdDate());
        hashCode = 31 * hashCode + Objects.hashCode(this.primaryRegion());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasReplicationStatus() ? this.replicationStatus() : null);
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
        if (!(obj instanceof DescribeSecretResponse)) {
            return false;
        }
        DescribeSecretResponse other = (DescribeSecretResponse)obj;
        return Objects.equals(this.arn(), other.arn()) && Objects.equals(this.name(), other.name()) && Objects.equals(this.description(), other.description()) && Objects.equals(this.kmsKeyId(), other.kmsKeyId()) && Objects.equals(this.rotationEnabled(), other.rotationEnabled()) && Objects.equals(this.rotationLambdaARN(), other.rotationLambdaARN()) && Objects.equals(this.rotationRules(), other.rotationRules()) && Objects.equals(this.lastRotatedDate(), other.lastRotatedDate()) && Objects.equals(this.lastChangedDate(), other.lastChangedDate()) && Objects.equals(this.lastAccessedDate(), other.lastAccessedDate()) && Objects.equals(this.deletedDate(), other.deletedDate()) && Objects.equals(this.nextRotationDate(), other.nextRotationDate()) && this.hasTags() == other.hasTags() && Objects.equals(this.tags(), other.tags()) && this.hasVersionIdsToStages() == other.hasVersionIdsToStages() && Objects.equals(this.versionIdsToStages(), other.versionIdsToStages()) && Objects.equals(this.owningService(), other.owningService()) && Objects.equals(this.createdDate(), other.createdDate()) && Objects.equals(this.primaryRegion(), other.primaryRegion()) && this.hasReplicationStatus() == other.hasReplicationStatus() && Objects.equals(this.replicationStatus(), other.replicationStatus());
    }

    public final String toString() {
        return ToString.builder("DescribeSecretResponse").add("ARN", this.arn()).add("Name", this.name()).add("Description", this.description()).add("KmsKeyId", this.kmsKeyId()).add("RotationEnabled", this.rotationEnabled()).add("RotationLambdaARN", this.rotationLambdaARN()).add("RotationRules", this.rotationRules()).add("LastRotatedDate", this.lastRotatedDate()).add("LastChangedDate", this.lastChangedDate()).add("LastAccessedDate", this.lastAccessedDate()).add("DeletedDate", this.deletedDate()).add("NextRotationDate", this.nextRotationDate()).add("Tags", this.hasTags() ? this.tags() : null).add("VersionIdsToStages", this.hasVersionIdsToStages() ? this.versionIdsToStages() : null).add("OwningService", this.owningService()).add("CreatedDate", this.createdDate()).add("PrimaryRegion", this.primaryRegion()).add("ReplicationStatus", this.hasReplicationStatus() ? this.replicationStatus() : null).build();
    }

    @Override
    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "ARN": {
                return Optional.ofNullable(clazz.cast(this.arn()));
            }
            case "Name": {
                return Optional.ofNullable(clazz.cast(this.name()));
            }
            case "Description": {
                return Optional.ofNullable(clazz.cast(this.description()));
            }
            case "KmsKeyId": {
                return Optional.ofNullable(clazz.cast(this.kmsKeyId()));
            }
            case "RotationEnabled": {
                return Optional.ofNullable(clazz.cast(this.rotationEnabled()));
            }
            case "RotationLambdaARN": {
                return Optional.ofNullable(clazz.cast(this.rotationLambdaARN()));
            }
            case "RotationRules": {
                return Optional.ofNullable(clazz.cast(this.rotationRules()));
            }
            case "LastRotatedDate": {
                return Optional.ofNullable(clazz.cast(this.lastRotatedDate()));
            }
            case "LastChangedDate": {
                return Optional.ofNullable(clazz.cast(this.lastChangedDate()));
            }
            case "LastAccessedDate": {
                return Optional.ofNullable(clazz.cast(this.lastAccessedDate()));
            }
            case "DeletedDate": {
                return Optional.ofNullable(clazz.cast(this.deletedDate()));
            }
            case "NextRotationDate": {
                return Optional.ofNullable(clazz.cast(this.nextRotationDate()));
            }
            case "Tags": {
                return Optional.ofNullable(clazz.cast(this.tags()));
            }
            case "VersionIdsToStages": {
                return Optional.ofNullable(clazz.cast(this.versionIdsToStages()));
            }
            case "OwningService": {
                return Optional.ofNullable(clazz.cast(this.owningService()));
            }
            case "CreatedDate": {
                return Optional.ofNullable(clazz.cast(this.createdDate()));
            }
            case "PrimaryRegion": {
                return Optional.ofNullable(clazz.cast(this.primaryRegion()));
            }
            case "ReplicationStatus": {
                return Optional.ofNullable(clazz.cast(this.replicationStatus()));
            }
        }
        return Optional.empty();
    }

    @Override
    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<DescribeSecretResponse, T> g) {
        return obj -> g.apply((DescribeSecretResponse)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends SecretsManagerResponse.BuilderImpl
    implements Builder {
        private String arn;
        private String name;
        private String description;
        private String kmsKeyId;
        private Boolean rotationEnabled;
        private String rotationLambdaARN;
        private RotationRulesType rotationRules;
        private Instant lastRotatedDate;
        private Instant lastChangedDate;
        private Instant lastAccessedDate;
        private Instant deletedDate;
        private Instant nextRotationDate;
        private List<Tag> tags = DefaultSdkAutoConstructList.getInstance();
        private Map<String, List<String>> versionIdsToStages = DefaultSdkAutoConstructMap.getInstance();
        private String owningService;
        private Instant createdDate;
        private String primaryRegion;
        private List<ReplicationStatusType> replicationStatus = DefaultSdkAutoConstructList.getInstance();

        private BuilderImpl() {
        }

        private BuilderImpl(DescribeSecretResponse model) {
            super(model);
            this.arn(model.arn);
            this.name(model.name);
            this.description(model.description);
            this.kmsKeyId(model.kmsKeyId);
            this.rotationEnabled(model.rotationEnabled);
            this.rotationLambdaARN(model.rotationLambdaARN);
            this.rotationRules(model.rotationRules);
            this.lastRotatedDate(model.lastRotatedDate);
            this.lastChangedDate(model.lastChangedDate);
            this.lastAccessedDate(model.lastAccessedDate);
            this.deletedDate(model.deletedDate);
            this.nextRotationDate(model.nextRotationDate);
            this.tags(model.tags);
            this.versionIdsToStages(model.versionIdsToStages);
            this.owningService(model.owningService);
            this.createdDate(model.createdDate);
            this.primaryRegion(model.primaryRegion);
            this.replicationStatus(model.replicationStatus);
        }

        public final String getArn() {
            return this.arn;
        }

        public final void setArn(String arn) {
            this.arn = arn;
        }

        @Override
        public final Builder arn(String arn) {
            this.arn = arn;
            return this;
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

        public final Boolean getRotationEnabled() {
            return this.rotationEnabled;
        }

        public final void setRotationEnabled(Boolean rotationEnabled) {
            this.rotationEnabled = rotationEnabled;
        }

        @Override
        public final Builder rotationEnabled(Boolean rotationEnabled) {
            this.rotationEnabled = rotationEnabled;
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

        public final Instant getLastRotatedDate() {
            return this.lastRotatedDate;
        }

        public final void setLastRotatedDate(Instant lastRotatedDate) {
            this.lastRotatedDate = lastRotatedDate;
        }

        @Override
        public final Builder lastRotatedDate(Instant lastRotatedDate) {
            this.lastRotatedDate = lastRotatedDate;
            return this;
        }

        public final Instant getLastChangedDate() {
            return this.lastChangedDate;
        }

        public final void setLastChangedDate(Instant lastChangedDate) {
            this.lastChangedDate = lastChangedDate;
        }

        @Override
        public final Builder lastChangedDate(Instant lastChangedDate) {
            this.lastChangedDate = lastChangedDate;
            return this;
        }

        public final Instant getLastAccessedDate() {
            return this.lastAccessedDate;
        }

        public final void setLastAccessedDate(Instant lastAccessedDate) {
            this.lastAccessedDate = lastAccessedDate;
        }

        @Override
        public final Builder lastAccessedDate(Instant lastAccessedDate) {
            this.lastAccessedDate = lastAccessedDate;
            return this;
        }

        public final Instant getDeletedDate() {
            return this.deletedDate;
        }

        public final void setDeletedDate(Instant deletedDate) {
            this.deletedDate = deletedDate;
        }

        @Override
        public final Builder deletedDate(Instant deletedDate) {
            this.deletedDate = deletedDate;
            return this;
        }

        public final Instant getNextRotationDate() {
            return this.nextRotationDate;
        }

        public final void setNextRotationDate(Instant nextRotationDate) {
            this.nextRotationDate = nextRotationDate;
        }

        @Override
        public final Builder nextRotationDate(Instant nextRotationDate) {
            this.nextRotationDate = nextRotationDate;
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
            this.tags(Stream.of(tags).map(c -> (Tag)((Tag.Builder)Tag.builder().applyMutation(c)).build()).collect(Collectors.toList()));
            return this;
        }

        public final Map<String, ? extends Collection<String>> getVersionIdsToStages() {
            if (this.versionIdsToStages instanceof SdkAutoConstructMap) {
                return null;
            }
            return this.versionIdsToStages;
        }

        public final void setVersionIdsToStages(Map<String, ? extends Collection<String>> versionIdsToStages) {
            this.versionIdsToStages = SecretVersionsToStagesMapTypeCopier.copy(versionIdsToStages);
        }

        @Override
        public final Builder versionIdsToStages(Map<String, ? extends Collection<String>> versionIdsToStages) {
            this.versionIdsToStages = SecretVersionsToStagesMapTypeCopier.copy(versionIdsToStages);
            return this;
        }

        public final String getOwningService() {
            return this.owningService;
        }

        public final void setOwningService(String owningService) {
            this.owningService = owningService;
        }

        @Override
        public final Builder owningService(String owningService) {
            this.owningService = owningService;
            return this;
        }

        public final Instant getCreatedDate() {
            return this.createdDate;
        }

        public final void setCreatedDate(Instant createdDate) {
            this.createdDate = createdDate;
        }

        @Override
        public final Builder createdDate(Instant createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public final String getPrimaryRegion() {
            return this.primaryRegion;
        }

        public final void setPrimaryRegion(String primaryRegion) {
            this.primaryRegion = primaryRegion;
        }

        @Override
        public final Builder primaryRegion(String primaryRegion) {
            this.primaryRegion = primaryRegion;
            return this;
        }

        public final List<ReplicationStatusType.Builder> getReplicationStatus() {
            List<ReplicationStatusType.Builder> result = ReplicationStatusListTypeCopier.copyToBuilder(this.replicationStatus);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setReplicationStatus(Collection<ReplicationStatusType.BuilderImpl> replicationStatus) {
            this.replicationStatus = ReplicationStatusListTypeCopier.copyFromBuilder(replicationStatus);
        }

        @Override
        public final Builder replicationStatus(Collection<ReplicationStatusType> replicationStatus) {
            this.replicationStatus = ReplicationStatusListTypeCopier.copy(replicationStatus);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder replicationStatus(ReplicationStatusType ... replicationStatus) {
            this.replicationStatus(Arrays.asList(replicationStatus));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder replicationStatus(Consumer<ReplicationStatusType.Builder> ... replicationStatus) {
            this.replicationStatus(Stream.of(replicationStatus).map(c -> (ReplicationStatusType)((ReplicationStatusType.Builder)ReplicationStatusType.builder().applyMutation(c)).build()).collect(Collectors.toList()));
            return this;
        }

        @Override
        public DescribeSecretResponse build() {
            return new DescribeSecretResponse(this);
        }

        @Override
        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SecretsManagerResponse.Builder,
    SdkPojo,
    CopyableBuilder<Builder, DescribeSecretResponse> {
        public Builder arn(String var1);

        public Builder name(String var1);

        public Builder description(String var1);

        public Builder kmsKeyId(String var1);

        public Builder rotationEnabled(Boolean var1);

        public Builder rotationLambdaARN(String var1);

        public Builder rotationRules(RotationRulesType var1);

        default public Builder rotationRules(Consumer<RotationRulesType.Builder> rotationRules) {
            return this.rotationRules((RotationRulesType)RotationRulesType.builder().applyMutation(rotationRules).build());
        }

        public Builder lastRotatedDate(Instant var1);

        public Builder lastChangedDate(Instant var1);

        public Builder lastAccessedDate(Instant var1);

        public Builder deletedDate(Instant var1);

        public Builder nextRotationDate(Instant var1);

        public Builder tags(Collection<Tag> var1);

        public Builder tags(Tag ... var1);

        public Builder tags(Consumer<Tag.Builder> ... var1);

        public Builder versionIdsToStages(Map<String, ? extends Collection<String>> var1);

        public Builder owningService(String var1);

        public Builder createdDate(Instant var1);

        public Builder primaryRegion(String var1);

        public Builder replicationStatus(Collection<ReplicationStatusType> var1);

        public Builder replicationStatus(ReplicationStatusType ... var1);

        public Builder replicationStatus(Consumer<ReplicationStatusType.Builder> ... var1);
    }
}

