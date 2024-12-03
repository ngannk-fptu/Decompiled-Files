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

public final class CompletedPart
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, CompletedPart> {
    private static final SdkField<String> E_TAG_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ETag").getter(CompletedPart.getter(CompletedPart::eTag)).setter(CompletedPart.setter(Builder::eTag)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ETag").unmarshallLocationName("ETag").build()}).build();
    private static final SdkField<String> CHECKSUM_CRC32_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ChecksumCRC32").getter(CompletedPart.getter(CompletedPart::checksumCRC32)).setter(CompletedPart.setter(Builder::checksumCRC32)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ChecksumCRC32").unmarshallLocationName("ChecksumCRC32").build()}).build();
    private static final SdkField<String> CHECKSUM_CRC32_C_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ChecksumCRC32C").getter(CompletedPart.getter(CompletedPart::checksumCRC32C)).setter(CompletedPart.setter(Builder::checksumCRC32C)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ChecksumCRC32C").unmarshallLocationName("ChecksumCRC32C").build()}).build();
    private static final SdkField<String> CHECKSUM_SHA1_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ChecksumSHA1").getter(CompletedPart.getter(CompletedPart::checksumSHA1)).setter(CompletedPart.setter(Builder::checksumSHA1)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ChecksumSHA1").unmarshallLocationName("ChecksumSHA1").build()}).build();
    private static final SdkField<String> CHECKSUM_SHA256_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ChecksumSHA256").getter(CompletedPart.getter(CompletedPart::checksumSHA256)).setter(CompletedPart.setter(Builder::checksumSHA256)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ChecksumSHA256").unmarshallLocationName("ChecksumSHA256").build()}).build();
    private static final SdkField<Integer> PART_NUMBER_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("PartNumber").getter(CompletedPart.getter(CompletedPart::partNumber)).setter(CompletedPart.setter(Builder::partNumber)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("PartNumber").unmarshallLocationName("PartNumber").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(E_TAG_FIELD, CHECKSUM_CRC32_FIELD, CHECKSUM_CRC32_C_FIELD, CHECKSUM_SHA1_FIELD, CHECKSUM_SHA256_FIELD, PART_NUMBER_FIELD));
    private static final long serialVersionUID = 1L;
    private final String eTag;
    private final String checksumCRC32;
    private final String checksumCRC32C;
    private final String checksumSHA1;
    private final String checksumSHA256;
    private final Integer partNumber;

    private CompletedPart(BuilderImpl builder) {
        this.eTag = builder.eTag;
        this.checksumCRC32 = builder.checksumCRC32;
        this.checksumCRC32C = builder.checksumCRC32C;
        this.checksumSHA1 = builder.checksumSHA1;
        this.checksumSHA256 = builder.checksumSHA256;
        this.partNumber = builder.partNumber;
    }

    public final String eTag() {
        return this.eTag;
    }

    public final String checksumCRC32() {
        return this.checksumCRC32;
    }

    public final String checksumCRC32C() {
        return this.checksumCRC32C;
    }

    public final String checksumSHA1() {
        return this.checksumSHA1;
    }

    public final String checksumSHA256() {
        return this.checksumSHA256;
    }

    public final Integer partNumber() {
        return this.partNumber;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.eTag());
        hashCode = 31 * hashCode + Objects.hashCode(this.checksumCRC32());
        hashCode = 31 * hashCode + Objects.hashCode(this.checksumCRC32C());
        hashCode = 31 * hashCode + Objects.hashCode(this.checksumSHA1());
        hashCode = 31 * hashCode + Objects.hashCode(this.checksumSHA256());
        hashCode = 31 * hashCode + Objects.hashCode(this.partNumber());
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
        if (!(obj instanceof CompletedPart)) {
            return false;
        }
        CompletedPart other = (CompletedPart)obj;
        return Objects.equals(this.eTag(), other.eTag()) && Objects.equals(this.checksumCRC32(), other.checksumCRC32()) && Objects.equals(this.checksumCRC32C(), other.checksumCRC32C()) && Objects.equals(this.checksumSHA1(), other.checksumSHA1()) && Objects.equals(this.checksumSHA256(), other.checksumSHA256()) && Objects.equals(this.partNumber(), other.partNumber());
    }

    public final String toString() {
        return ToString.builder((String)"CompletedPart").add("ETag", (Object)this.eTag()).add("ChecksumCRC32", (Object)this.checksumCRC32()).add("ChecksumCRC32C", (Object)this.checksumCRC32C()).add("ChecksumSHA1", (Object)this.checksumSHA1()).add("ChecksumSHA256", (Object)this.checksumSHA256()).add("PartNumber", (Object)this.partNumber()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "ETag": {
                return Optional.ofNullable(clazz.cast(this.eTag()));
            }
            case "ChecksumCRC32": {
                return Optional.ofNullable(clazz.cast(this.checksumCRC32()));
            }
            case "ChecksumCRC32C": {
                return Optional.ofNullable(clazz.cast(this.checksumCRC32C()));
            }
            case "ChecksumSHA1": {
                return Optional.ofNullable(clazz.cast(this.checksumSHA1()));
            }
            case "ChecksumSHA256": {
                return Optional.ofNullable(clazz.cast(this.checksumSHA256()));
            }
            case "PartNumber": {
                return Optional.ofNullable(clazz.cast(this.partNumber()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<CompletedPart, T> g) {
        return obj -> g.apply((CompletedPart)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String eTag;
        private String checksumCRC32;
        private String checksumCRC32C;
        private String checksumSHA1;
        private String checksumSHA256;
        private Integer partNumber;

        private BuilderImpl() {
        }

        private BuilderImpl(CompletedPart model) {
            this.eTag(model.eTag);
            this.checksumCRC32(model.checksumCRC32);
            this.checksumCRC32C(model.checksumCRC32C);
            this.checksumSHA1(model.checksumSHA1);
            this.checksumSHA256(model.checksumSHA256);
            this.partNumber(model.partNumber);
        }

        public final String getETag() {
            return this.eTag;
        }

        public final void setETag(String eTag) {
            this.eTag = eTag;
        }

        @Override
        public final Builder eTag(String eTag) {
            this.eTag = eTag;
            return this;
        }

        public final String getChecksumCRC32() {
            return this.checksumCRC32;
        }

        public final void setChecksumCRC32(String checksumCRC32) {
            this.checksumCRC32 = checksumCRC32;
        }

        @Override
        public final Builder checksumCRC32(String checksumCRC32) {
            this.checksumCRC32 = checksumCRC32;
            return this;
        }

        public final String getChecksumCRC32C() {
            return this.checksumCRC32C;
        }

        public final void setChecksumCRC32C(String checksumCRC32C) {
            this.checksumCRC32C = checksumCRC32C;
        }

        @Override
        public final Builder checksumCRC32C(String checksumCRC32C) {
            this.checksumCRC32C = checksumCRC32C;
            return this;
        }

        public final String getChecksumSHA1() {
            return this.checksumSHA1;
        }

        public final void setChecksumSHA1(String checksumSHA1) {
            this.checksumSHA1 = checksumSHA1;
        }

        @Override
        public final Builder checksumSHA1(String checksumSHA1) {
            this.checksumSHA1 = checksumSHA1;
            return this;
        }

        public final String getChecksumSHA256() {
            return this.checksumSHA256;
        }

        public final void setChecksumSHA256(String checksumSHA256) {
            this.checksumSHA256 = checksumSHA256;
        }

        @Override
        public final Builder checksumSHA256(String checksumSHA256) {
            this.checksumSHA256 = checksumSHA256;
            return this;
        }

        public final Integer getPartNumber() {
            return this.partNumber;
        }

        public final void setPartNumber(Integer partNumber) {
            this.partNumber = partNumber;
        }

        @Override
        public final Builder partNumber(Integer partNumber) {
            this.partNumber = partNumber;
            return this;
        }

        public CompletedPart build() {
            return new CompletedPart(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, CompletedPart> {
        public Builder eTag(String var1);

        public Builder checksumCRC32(String var1);

        public Builder checksumCRC32C(String var1);

        public Builder checksumSHA1(String var1);

        public Builder checksumSHA256(String var1);

        public Builder partNumber(Integer var1);
    }
}

