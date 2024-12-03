/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class S3Error
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, S3Error> {
    private static final SdkField<String> KEY_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Key").getter(S3Error.getter(S3Error::key)).setter(S3Error.setter(Builder::key)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Key").unmarshallLocationName("Key").build()}).build();
    private static final SdkField<String> VERSION_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("VersionId").getter(S3Error.getter(S3Error::versionId)).setter(S3Error.setter(Builder::versionId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("VersionId").unmarshallLocationName("VersionId").build()}).build();
    private static final SdkField<String> CODE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Code").getter(S3Error.getter(S3Error::code)).setter(S3Error.setter(Builder::code)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Code").unmarshallLocationName("Code").build()}).build();
    private static final SdkField<String> MESSAGE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Message").getter(S3Error.getter(S3Error::message)).setter(S3Error.setter(Builder::message)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Message").unmarshallLocationName("Message").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(KEY_FIELD, VERSION_ID_FIELD, CODE_FIELD, MESSAGE_FIELD));
    private static final long serialVersionUID = 1L;
    private final String key;
    private final String versionId;
    private final String code;
    private final String message;

    private S3Error(BuilderImpl builder) {
        this.key = builder.key;
        this.versionId = builder.versionId;
        this.code = builder.code;
        this.message = builder.message;
    }

    public final String key() {
        return this.key;
    }

    public final String versionId() {
        return this.versionId;
    }

    public final String code() {
        return this.code;
    }

    public final String message() {
        return this.message;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.key());
        hashCode = 31 * hashCode + Objects.hashCode(this.versionId());
        hashCode = 31 * hashCode + Objects.hashCode(this.code());
        hashCode = 31 * hashCode + Objects.hashCode(this.message());
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
        if (!(obj instanceof S3Error)) {
            return false;
        }
        S3Error other = (S3Error)obj;
        return Objects.equals(this.key(), other.key()) && Objects.equals(this.versionId(), other.versionId()) && Objects.equals(this.code(), other.code()) && Objects.equals(this.message(), other.message());
    }

    public final String toString() {
        return ToString.builder((String)"S3Error").add("Key", (Object)this.key()).add("VersionId", (Object)this.versionId()).add("Code", (Object)this.code()).add("Message", (Object)this.message()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Key": {
                return Optional.ofNullable(clazz.cast(this.key()));
            }
            case "VersionId": {
                return Optional.ofNullable(clazz.cast(this.versionId()));
            }
            case "Code": {
                return Optional.ofNullable(clazz.cast(this.code()));
            }
            case "Message": {
                return Optional.ofNullable(clazz.cast(this.message()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<S3Error, T> g) {
        return obj -> g.apply((S3Error)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String key;
        private String versionId;
        private String code;
        private String message;

        private BuilderImpl() {
        }

        private BuilderImpl(S3Error model) {
            this.key(model.key);
            this.versionId(model.versionId);
            this.code(model.code);
            this.message(model.message);
        }

        public final String getKey() {
            return this.key;
        }

        public final void setKey(String key) {
            this.key = key;
        }

        @Override
        public final Builder key(String key) {
            this.key = key;
            return this;
        }

        public final String getVersionId() {
            return this.versionId;
        }

        public final void setVersionId(String versionId) {
            this.versionId = versionId;
        }

        @Override
        public final Builder versionId(String versionId) {
            this.versionId = versionId;
            return this;
        }

        public final String getCode() {
            return this.code;
        }

        public final void setCode(String code) {
            this.code = code;
        }

        @Override
        public final Builder code(String code) {
            this.code = code;
            return this;
        }

        public final String getMessage() {
            return this.message;
        }

        public final void setMessage(String message) {
            this.message = message;
        }

        @Override
        public final Builder message(String message) {
            this.message = message;
            return this;
        }

        public S3Error build() {
            return new S3Error(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, S3Error> {
        public Builder key(String var1);

        public Builder versionId(String var1);

        public Builder code(String var1);

        public Builder message(String var1);
    }
}

