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
import java.util.function.Function;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.RequiredTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.ServerSideEncryption;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class Encryption
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, Encryption> {
    private static final SdkField<String> ENCRYPTION_TYPE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("EncryptionType").getter(Encryption.getter(Encryption::encryptionTypeAsString)).setter(Encryption.setter(Builder::encryptionType)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("EncryptionType").unmarshallLocationName("EncryptionType").build(), RequiredTrait.create()}).build();
    private static final SdkField<String> KMS_KEY_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("KMSKeyId").getter(Encryption.getter(Encryption::kmsKeyId)).setter(Encryption.setter(Builder::kmsKeyId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("KMSKeyId").unmarshallLocationName("KMSKeyId").build()}).build();
    private static final SdkField<String> KMS_CONTEXT_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("KMSContext").getter(Encryption.getter(Encryption::kmsContext)).setter(Encryption.setter(Builder::kmsContext)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("KMSContext").unmarshallLocationName("KMSContext").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(ENCRYPTION_TYPE_FIELD, KMS_KEY_ID_FIELD, KMS_CONTEXT_FIELD));
    private static final long serialVersionUID = 1L;
    private final String encryptionType;
    private final String kmsKeyId;
    private final String kmsContext;

    private Encryption(BuilderImpl builder) {
        this.encryptionType = builder.encryptionType;
        this.kmsKeyId = builder.kmsKeyId;
        this.kmsContext = builder.kmsContext;
    }

    public final ServerSideEncryption encryptionType() {
        return ServerSideEncryption.fromValue(this.encryptionType);
    }

    public final String encryptionTypeAsString() {
        return this.encryptionType;
    }

    public final String kmsKeyId() {
        return this.kmsKeyId;
    }

    public final String kmsContext() {
        return this.kmsContext;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.encryptionTypeAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.kmsKeyId());
        hashCode = 31 * hashCode + Objects.hashCode(this.kmsContext());
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
        if (!(obj instanceof Encryption)) {
            return false;
        }
        Encryption other = (Encryption)obj;
        return Objects.equals(this.encryptionTypeAsString(), other.encryptionTypeAsString()) && Objects.equals(this.kmsKeyId(), other.kmsKeyId()) && Objects.equals(this.kmsContext(), other.kmsContext());
    }

    public final String toString() {
        return ToString.builder((String)"Encryption").add("EncryptionType", (Object)this.encryptionTypeAsString()).add("KMSKeyId", (Object)(this.kmsKeyId() == null ? null : "*** Sensitive Data Redacted ***")).add("KMSContext", (Object)this.kmsContext()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "EncryptionType": {
                return Optional.ofNullable(clazz.cast(this.encryptionTypeAsString()));
            }
            case "KMSKeyId": {
                return Optional.ofNullable(clazz.cast(this.kmsKeyId()));
            }
            case "KMSContext": {
                return Optional.ofNullable(clazz.cast(this.kmsContext()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<Encryption, T> g) {
        return obj -> g.apply((Encryption)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String encryptionType;
        private String kmsKeyId;
        private String kmsContext;

        private BuilderImpl() {
        }

        private BuilderImpl(Encryption model) {
            this.encryptionType(model.encryptionType);
            this.kmsKeyId(model.kmsKeyId);
            this.kmsContext(model.kmsContext);
        }

        public final String getEncryptionType() {
            return this.encryptionType;
        }

        public final void setEncryptionType(String encryptionType) {
            this.encryptionType = encryptionType;
        }

        @Override
        public final Builder encryptionType(String encryptionType) {
            this.encryptionType = encryptionType;
            return this;
        }

        @Override
        public final Builder encryptionType(ServerSideEncryption encryptionType) {
            this.encryptionType(encryptionType == null ? null : encryptionType.toString());
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

        public final String getKmsContext() {
            return this.kmsContext;
        }

        public final void setKmsContext(String kmsContext) {
            this.kmsContext = kmsContext;
        }

        @Override
        public final Builder kmsContext(String kmsContext) {
            this.kmsContext = kmsContext;
            return this;
        }

        public Encryption build() {
            return new Encryption(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, Encryption> {
        public Builder encryptionType(String var1);

        public Builder encryptionType(ServerSideEncryption var1);

        public Builder kmsKeyId(String var1);

        public Builder kmsContext(String var1);
    }
}

