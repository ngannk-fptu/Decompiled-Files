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
import software.amazon.awssdk.services.s3.model.BucketVersioningStatus;
import software.amazon.awssdk.services.s3.model.MFADelete;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class VersioningConfiguration
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, VersioningConfiguration> {
    private static final SdkField<String> MFA_DELETE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("MFADelete").getter(VersioningConfiguration.getter(VersioningConfiguration::mfaDeleteAsString)).setter(VersioningConfiguration.setter(Builder::mfaDelete)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("MfaDelete").unmarshallLocationName("MfaDelete").build()}).build();
    private static final SdkField<String> STATUS_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Status").getter(VersioningConfiguration.getter(VersioningConfiguration::statusAsString)).setter(VersioningConfiguration.setter(Builder::status)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Status").unmarshallLocationName("Status").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(MFA_DELETE_FIELD, STATUS_FIELD));
    private static final long serialVersionUID = 1L;
    private final String mfaDelete;
    private final String status;

    private VersioningConfiguration(BuilderImpl builder) {
        this.mfaDelete = builder.mfaDelete;
        this.status = builder.status;
    }

    public final MFADelete mfaDelete() {
        return MFADelete.fromValue(this.mfaDelete);
    }

    public final String mfaDeleteAsString() {
        return this.mfaDelete;
    }

    public final BucketVersioningStatus status() {
        return BucketVersioningStatus.fromValue(this.status);
    }

    public final String statusAsString() {
        return this.status;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.mfaDeleteAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.statusAsString());
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
        if (!(obj instanceof VersioningConfiguration)) {
            return false;
        }
        VersioningConfiguration other = (VersioningConfiguration)obj;
        return Objects.equals(this.mfaDeleteAsString(), other.mfaDeleteAsString()) && Objects.equals(this.statusAsString(), other.statusAsString());
    }

    public final String toString() {
        return ToString.builder((String)"VersioningConfiguration").add("MFADelete", (Object)this.mfaDeleteAsString()).add("Status", (Object)this.statusAsString()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "MFADelete": {
                return Optional.ofNullable(clazz.cast(this.mfaDeleteAsString()));
            }
            case "Status": {
                return Optional.ofNullable(clazz.cast(this.statusAsString()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<VersioningConfiguration, T> g) {
        return obj -> g.apply((VersioningConfiguration)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String mfaDelete;
        private String status;

        private BuilderImpl() {
        }

        private BuilderImpl(VersioningConfiguration model) {
            this.mfaDelete(model.mfaDelete);
            this.status(model.status);
        }

        public final String getMfaDelete() {
            return this.mfaDelete;
        }

        public final void setMfaDelete(String mfaDelete) {
            this.mfaDelete = mfaDelete;
        }

        @Override
        public final Builder mfaDelete(String mfaDelete) {
            this.mfaDelete = mfaDelete;
            return this;
        }

        @Override
        public final Builder mfaDelete(MFADelete mfaDelete) {
            this.mfaDelete(mfaDelete == null ? null : mfaDelete.toString());
            return this;
        }

        public final String getStatus() {
            return this.status;
        }

        public final void setStatus(String status) {
            this.status = status;
        }

        @Override
        public final Builder status(String status) {
            this.status = status;
            return this;
        }

        @Override
        public final Builder status(BucketVersioningStatus status) {
            this.status(status == null ? null : status.toString());
            return this;
        }

        public VersioningConfiguration build() {
            return new VersioningConfiguration(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, VersioningConfiguration> {
        public Builder mfaDelete(String var1);

        public Builder mfaDelete(MFADelete var1);

        public Builder status(String var1);

        public Builder status(BucketVersioningStatus var1);
    }
}

