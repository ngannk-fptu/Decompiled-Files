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
import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.SSEKMS;
import software.amazon.awssdk.services.s3.model.SSES3;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class InventoryEncryption
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, InventoryEncryption> {
    private static final SdkField<SSES3> SSES3_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("SSES3").getter(InventoryEncryption.getter(InventoryEncryption::sses3)).setter(InventoryEncryption.setter(Builder::sses3)).constructor(SSES3::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SSE-S3").unmarshallLocationName("SSE-S3").build()}).build();
    private static final SdkField<SSEKMS> SSEKMS_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("SSEKMS").getter(InventoryEncryption.getter(InventoryEncryption::ssekms)).setter(InventoryEncryption.setter(Builder::ssekms)).constructor(SSEKMS::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SSE-KMS").unmarshallLocationName("SSE-KMS").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(SSES3_FIELD, SSEKMS_FIELD));
    private static final long serialVersionUID = 1L;
    private final SSES3 sses3;
    private final SSEKMS ssekms;

    private InventoryEncryption(BuilderImpl builder) {
        this.sses3 = builder.sses3;
        this.ssekms = builder.ssekms;
    }

    public final SSES3 sses3() {
        return this.sses3;
    }

    public final SSEKMS ssekms() {
        return this.ssekms;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.sses3());
        hashCode = 31 * hashCode + Objects.hashCode(this.ssekms());
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
        if (!(obj instanceof InventoryEncryption)) {
            return false;
        }
        InventoryEncryption other = (InventoryEncryption)obj;
        return Objects.equals(this.sses3(), other.sses3()) && Objects.equals(this.ssekms(), other.ssekms());
    }

    public final String toString() {
        return ToString.builder((String)"InventoryEncryption").add("SSES3", (Object)this.sses3()).add("SSEKMS", (Object)this.ssekms()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "SSES3": {
                return Optional.ofNullable(clazz.cast(this.sses3()));
            }
            case "SSEKMS": {
                return Optional.ofNullable(clazz.cast(this.ssekms()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<InventoryEncryption, T> g) {
        return obj -> g.apply((InventoryEncryption)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private SSES3 sses3;
        private SSEKMS ssekms;

        private BuilderImpl() {
        }

        private BuilderImpl(InventoryEncryption model) {
            this.sses3(model.sses3);
            this.ssekms(model.ssekms);
        }

        public final SSES3.Builder getSses3() {
            return this.sses3 != null ? this.sses3.toBuilder() : null;
        }

        public final void setSses3(SSES3.BuilderImpl sses3) {
            this.sses3 = sses3 != null ? sses3.build() : null;
        }

        @Override
        public final Builder sses3(SSES3 sses3) {
            this.sses3 = sses3;
            return this;
        }

        public final SSEKMS.Builder getSsekms() {
            return this.ssekms != null ? this.ssekms.toBuilder() : null;
        }

        public final void setSsekms(SSEKMS.BuilderImpl ssekms) {
            this.ssekms = ssekms != null ? ssekms.build() : null;
        }

        @Override
        public final Builder ssekms(SSEKMS ssekms) {
            this.ssekms = ssekms;
            return this;
        }

        public InventoryEncryption build() {
            return new InventoryEncryption(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, InventoryEncryption> {
        public Builder sses3(SSES3 var1);

        default public Builder sses3(Consumer<SSES3.Builder> sses3) {
            return this.sses3((SSES3)((SSES3.Builder)SSES3.builder().applyMutation(sses3)).build());
        }

        public Builder ssekms(SSEKMS var1);

        default public Builder ssekms(Consumer<SSEKMS.Builder> ssekms) {
            return this.ssekms((SSEKMS)((SSEKMS.Builder)SSEKMS.builder().applyMutation(ssekms)).build());
        }
    }
}

