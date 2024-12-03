/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.RequiredTrait
 *  software.amazon.awssdk.core.traits.Trait
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.RequiredTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.AccessControlTranslation;
import software.amazon.awssdk.services.s3.model.EncryptionConfiguration;
import software.amazon.awssdk.services.s3.model.Metrics;
import software.amazon.awssdk.services.s3.model.ReplicationTime;
import software.amazon.awssdk.services.s3.model.StorageClass;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class Destination
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, Destination> {
    private static final SdkField<String> BUCKET_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Bucket").getter(Destination.getter(Destination::bucket)).setter(Destination.setter(Builder::bucket)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Bucket").unmarshallLocationName("Bucket").build(), RequiredTrait.create()}).build();
    private static final SdkField<String> ACCOUNT_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Account").getter(Destination.getter(Destination::account)).setter(Destination.setter(Builder::account)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Account").unmarshallLocationName("Account").build()}).build();
    private static final SdkField<String> STORAGE_CLASS_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("StorageClass").getter(Destination.getter(Destination::storageClassAsString)).setter(Destination.setter(Builder::storageClass)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("StorageClass").unmarshallLocationName("StorageClass").build()}).build();
    private static final SdkField<AccessControlTranslation> ACCESS_CONTROL_TRANSLATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("AccessControlTranslation").getter(Destination.getter(Destination::accessControlTranslation)).setter(Destination.setter(Builder::accessControlTranslation)).constructor(AccessControlTranslation::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("AccessControlTranslation").unmarshallLocationName("AccessControlTranslation").build()}).build();
    private static final SdkField<EncryptionConfiguration> ENCRYPTION_CONFIGURATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("EncryptionConfiguration").getter(Destination.getter(Destination::encryptionConfiguration)).setter(Destination.setter(Builder::encryptionConfiguration)).constructor(EncryptionConfiguration::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("EncryptionConfiguration").unmarshallLocationName("EncryptionConfiguration").build()}).build();
    private static final SdkField<ReplicationTime> REPLICATION_TIME_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("ReplicationTime").getter(Destination.getter(Destination::replicationTime)).setter(Destination.setter(Builder::replicationTime)).constructor(ReplicationTime::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ReplicationTime").unmarshallLocationName("ReplicationTime").build()}).build();
    private static final SdkField<Metrics> METRICS_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Metrics").getter(Destination.getter(Destination::metrics)).setter(Destination.setter(Builder::metrics)).constructor(Metrics::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Metrics").unmarshallLocationName("Metrics").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(BUCKET_FIELD, ACCOUNT_FIELD, STORAGE_CLASS_FIELD, ACCESS_CONTROL_TRANSLATION_FIELD, ENCRYPTION_CONFIGURATION_FIELD, REPLICATION_TIME_FIELD, METRICS_FIELD));
    private static final long serialVersionUID = 1L;
    private final String bucket;
    private final String account;
    private final String storageClass;
    private final AccessControlTranslation accessControlTranslation;
    private final EncryptionConfiguration encryptionConfiguration;
    private final ReplicationTime replicationTime;
    private final Metrics metrics;

    private Destination(BuilderImpl builder) {
        this.bucket = builder.bucket;
        this.account = builder.account;
        this.storageClass = builder.storageClass;
        this.accessControlTranslation = builder.accessControlTranslation;
        this.encryptionConfiguration = builder.encryptionConfiguration;
        this.replicationTime = builder.replicationTime;
        this.metrics = builder.metrics;
    }

    public final String bucket() {
        return this.bucket;
    }

    public final String account() {
        return this.account;
    }

    public final StorageClass storageClass() {
        return StorageClass.fromValue(this.storageClass);
    }

    public final String storageClassAsString() {
        return this.storageClass;
    }

    public final AccessControlTranslation accessControlTranslation() {
        return this.accessControlTranslation;
    }

    public final EncryptionConfiguration encryptionConfiguration() {
        return this.encryptionConfiguration;
    }

    public final ReplicationTime replicationTime() {
        return this.replicationTime;
    }

    public final Metrics metrics() {
        return this.metrics;
    }

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
        hashCode = 31 * hashCode + Objects.hashCode(this.bucket());
        hashCode = 31 * hashCode + Objects.hashCode(this.account());
        hashCode = 31 * hashCode + Objects.hashCode(this.storageClassAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.accessControlTranslation());
        hashCode = 31 * hashCode + Objects.hashCode(this.encryptionConfiguration());
        hashCode = 31 * hashCode + Objects.hashCode(this.replicationTime());
        hashCode = 31 * hashCode + Objects.hashCode(this.metrics());
        return hashCode;
    }

    public final boolean equals(Object obj) {
        return this.equalsBySdkFields(obj);
    }

    public final boolean equalsBySdkFields(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Destination)) {
            return false;
        }
        Destination other = (Destination)obj;
        return Objects.equals(this.bucket(), other.bucket()) && Objects.equals(this.account(), other.account()) && Objects.equals(this.storageClassAsString(), other.storageClassAsString()) && Objects.equals(this.accessControlTranslation(), other.accessControlTranslation()) && Objects.equals(this.encryptionConfiguration(), other.encryptionConfiguration()) && Objects.equals(this.replicationTime(), other.replicationTime()) && Objects.equals(this.metrics(), other.metrics());
    }

    public final String toString() {
        return ToString.builder((String)"Destination").add("Bucket", (Object)this.bucket()).add("Account", (Object)this.account()).add("StorageClass", (Object)this.storageClassAsString()).add("AccessControlTranslation", (Object)this.accessControlTranslation()).add("EncryptionConfiguration", (Object)this.encryptionConfiguration()).add("ReplicationTime", (Object)this.replicationTime()).add("Metrics", (Object)this.metrics()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Bucket": {
                return Optional.ofNullable(clazz.cast(this.bucket()));
            }
            case "Account": {
                return Optional.ofNullable(clazz.cast(this.account()));
            }
            case "StorageClass": {
                return Optional.ofNullable(clazz.cast(this.storageClassAsString()));
            }
            case "AccessControlTranslation": {
                return Optional.ofNullable(clazz.cast(this.accessControlTranslation()));
            }
            case "EncryptionConfiguration": {
                return Optional.ofNullable(clazz.cast(this.encryptionConfiguration()));
            }
            case "ReplicationTime": {
                return Optional.ofNullable(clazz.cast(this.replicationTime()));
            }
            case "Metrics": {
                return Optional.ofNullable(clazz.cast(this.metrics()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<Destination, T> g) {
        return obj -> g.apply((Destination)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String bucket;
        private String account;
        private String storageClass;
        private AccessControlTranslation accessControlTranslation;
        private EncryptionConfiguration encryptionConfiguration;
        private ReplicationTime replicationTime;
        private Metrics metrics;

        private BuilderImpl() {
        }

        private BuilderImpl(Destination model) {
            this.bucket(model.bucket);
            this.account(model.account);
            this.storageClass(model.storageClass);
            this.accessControlTranslation(model.accessControlTranslation);
            this.encryptionConfiguration(model.encryptionConfiguration);
            this.replicationTime(model.replicationTime);
            this.metrics(model.metrics);
        }

        public final String getBucket() {
            return this.bucket;
        }

        public final void setBucket(String bucket) {
            this.bucket = bucket;
        }

        @Override
        public final Builder bucket(String bucket) {
            this.bucket = bucket;
            return this;
        }

        public final String getAccount() {
            return this.account;
        }

        public final void setAccount(String account) {
            this.account = account;
        }

        @Override
        public final Builder account(String account) {
            this.account = account;
            return this;
        }

        public final String getStorageClass() {
            return this.storageClass;
        }

        public final void setStorageClass(String storageClass) {
            this.storageClass = storageClass;
        }

        @Override
        public final Builder storageClass(String storageClass) {
            this.storageClass = storageClass;
            return this;
        }

        @Override
        public final Builder storageClass(StorageClass storageClass) {
            this.storageClass(storageClass == null ? null : storageClass.toString());
            return this;
        }

        public final AccessControlTranslation.Builder getAccessControlTranslation() {
            return this.accessControlTranslation != null ? this.accessControlTranslation.toBuilder() : null;
        }

        public final void setAccessControlTranslation(AccessControlTranslation.BuilderImpl accessControlTranslation) {
            this.accessControlTranslation = accessControlTranslation != null ? accessControlTranslation.build() : null;
        }

        @Override
        public final Builder accessControlTranslation(AccessControlTranslation accessControlTranslation) {
            this.accessControlTranslation = accessControlTranslation;
            return this;
        }

        public final EncryptionConfiguration.Builder getEncryptionConfiguration() {
            return this.encryptionConfiguration != null ? this.encryptionConfiguration.toBuilder() : null;
        }

        public final void setEncryptionConfiguration(EncryptionConfiguration.BuilderImpl encryptionConfiguration) {
            this.encryptionConfiguration = encryptionConfiguration != null ? encryptionConfiguration.build() : null;
        }

        @Override
        public final Builder encryptionConfiguration(EncryptionConfiguration encryptionConfiguration) {
            this.encryptionConfiguration = encryptionConfiguration;
            return this;
        }

        public final ReplicationTime.Builder getReplicationTime() {
            return this.replicationTime != null ? this.replicationTime.toBuilder() : null;
        }

        public final void setReplicationTime(ReplicationTime.BuilderImpl replicationTime) {
            this.replicationTime = replicationTime != null ? replicationTime.build() : null;
        }

        @Override
        public final Builder replicationTime(ReplicationTime replicationTime) {
            this.replicationTime = replicationTime;
            return this;
        }

        public final Metrics.Builder getMetrics() {
            return this.metrics != null ? this.metrics.toBuilder() : null;
        }

        public final void setMetrics(Metrics.BuilderImpl metrics) {
            this.metrics = metrics != null ? metrics.build() : null;
        }

        @Override
        public final Builder metrics(Metrics metrics) {
            this.metrics = metrics;
            return this;
        }

        public Destination build() {
            return new Destination(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, Destination> {
        public Builder bucket(String var1);

        public Builder account(String var1);

        public Builder storageClass(String var1);

        public Builder storageClass(StorageClass var1);

        public Builder accessControlTranslation(AccessControlTranslation var1);

        default public Builder accessControlTranslation(Consumer<AccessControlTranslation.Builder> accessControlTranslation) {
            return this.accessControlTranslation((AccessControlTranslation)((AccessControlTranslation.Builder)AccessControlTranslation.builder().applyMutation(accessControlTranslation)).build());
        }

        public Builder encryptionConfiguration(EncryptionConfiguration var1);

        default public Builder encryptionConfiguration(Consumer<EncryptionConfiguration.Builder> encryptionConfiguration) {
            return this.encryptionConfiguration((EncryptionConfiguration)((EncryptionConfiguration.Builder)EncryptionConfiguration.builder().applyMutation(encryptionConfiguration)).build());
        }

        public Builder replicationTime(ReplicationTime var1);

        default public Builder replicationTime(Consumer<ReplicationTime.Builder> replicationTime) {
            return this.replicationTime((ReplicationTime)((ReplicationTime.Builder)ReplicationTime.builder().applyMutation(replicationTime)).build());
        }

        public Builder metrics(Metrics var1);

        default public Builder metrics(Consumer<Metrics.Builder> metrics) {
            return this.metrics((Metrics)((Metrics.Builder)Metrics.builder().applyMutation(metrics)).build());
        }
    }
}

