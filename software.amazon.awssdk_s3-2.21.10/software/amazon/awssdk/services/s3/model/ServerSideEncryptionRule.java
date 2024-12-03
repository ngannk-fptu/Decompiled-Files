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
import software.amazon.awssdk.services.s3.model.ServerSideEncryptionByDefault;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class ServerSideEncryptionRule
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, ServerSideEncryptionRule> {
    private static final SdkField<ServerSideEncryptionByDefault> APPLY_SERVER_SIDE_ENCRYPTION_BY_DEFAULT_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("ApplyServerSideEncryptionByDefault").getter(ServerSideEncryptionRule.getter(ServerSideEncryptionRule::applyServerSideEncryptionByDefault)).setter(ServerSideEncryptionRule.setter(Builder::applyServerSideEncryptionByDefault)).constructor(ServerSideEncryptionByDefault::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ApplyServerSideEncryptionByDefault").unmarshallLocationName("ApplyServerSideEncryptionByDefault").build()}).build();
    private static final SdkField<Boolean> BUCKET_KEY_ENABLED_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("BucketKeyEnabled").getter(ServerSideEncryptionRule.getter(ServerSideEncryptionRule::bucketKeyEnabled)).setter(ServerSideEncryptionRule.setter(Builder::bucketKeyEnabled)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("BucketKeyEnabled").unmarshallLocationName("BucketKeyEnabled").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(APPLY_SERVER_SIDE_ENCRYPTION_BY_DEFAULT_FIELD, BUCKET_KEY_ENABLED_FIELD));
    private static final long serialVersionUID = 1L;
    private final ServerSideEncryptionByDefault applyServerSideEncryptionByDefault;
    private final Boolean bucketKeyEnabled;

    private ServerSideEncryptionRule(BuilderImpl builder) {
        this.applyServerSideEncryptionByDefault = builder.applyServerSideEncryptionByDefault;
        this.bucketKeyEnabled = builder.bucketKeyEnabled;
    }

    public final ServerSideEncryptionByDefault applyServerSideEncryptionByDefault() {
        return this.applyServerSideEncryptionByDefault;
    }

    public final Boolean bucketKeyEnabled() {
        return this.bucketKeyEnabled;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.applyServerSideEncryptionByDefault());
        hashCode = 31 * hashCode + Objects.hashCode(this.bucketKeyEnabled());
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
        if (!(obj instanceof ServerSideEncryptionRule)) {
            return false;
        }
        ServerSideEncryptionRule other = (ServerSideEncryptionRule)obj;
        return Objects.equals(this.applyServerSideEncryptionByDefault(), other.applyServerSideEncryptionByDefault()) && Objects.equals(this.bucketKeyEnabled(), other.bucketKeyEnabled());
    }

    public final String toString() {
        return ToString.builder((String)"ServerSideEncryptionRule").add("ApplyServerSideEncryptionByDefault", (Object)this.applyServerSideEncryptionByDefault()).add("BucketKeyEnabled", (Object)this.bucketKeyEnabled()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "ApplyServerSideEncryptionByDefault": {
                return Optional.ofNullable(clazz.cast(this.applyServerSideEncryptionByDefault()));
            }
            case "BucketKeyEnabled": {
                return Optional.ofNullable(clazz.cast(this.bucketKeyEnabled()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<ServerSideEncryptionRule, T> g) {
        return obj -> g.apply((ServerSideEncryptionRule)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private ServerSideEncryptionByDefault applyServerSideEncryptionByDefault;
        private Boolean bucketKeyEnabled;

        private BuilderImpl() {
        }

        private BuilderImpl(ServerSideEncryptionRule model) {
            this.applyServerSideEncryptionByDefault(model.applyServerSideEncryptionByDefault);
            this.bucketKeyEnabled(model.bucketKeyEnabled);
        }

        public final ServerSideEncryptionByDefault.Builder getApplyServerSideEncryptionByDefault() {
            return this.applyServerSideEncryptionByDefault != null ? this.applyServerSideEncryptionByDefault.toBuilder() : null;
        }

        public final void setApplyServerSideEncryptionByDefault(ServerSideEncryptionByDefault.BuilderImpl applyServerSideEncryptionByDefault) {
            this.applyServerSideEncryptionByDefault = applyServerSideEncryptionByDefault != null ? applyServerSideEncryptionByDefault.build() : null;
        }

        @Override
        public final Builder applyServerSideEncryptionByDefault(ServerSideEncryptionByDefault applyServerSideEncryptionByDefault) {
            this.applyServerSideEncryptionByDefault = applyServerSideEncryptionByDefault;
            return this;
        }

        public final Boolean getBucketKeyEnabled() {
            return this.bucketKeyEnabled;
        }

        public final void setBucketKeyEnabled(Boolean bucketKeyEnabled) {
            this.bucketKeyEnabled = bucketKeyEnabled;
        }

        @Override
        public final Builder bucketKeyEnabled(Boolean bucketKeyEnabled) {
            this.bucketKeyEnabled = bucketKeyEnabled;
            return this;
        }

        public ServerSideEncryptionRule build() {
            return new ServerSideEncryptionRule(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, ServerSideEncryptionRule> {
        public Builder applyServerSideEncryptionByDefault(ServerSideEncryptionByDefault var1);

        default public Builder applyServerSideEncryptionByDefault(Consumer<ServerSideEncryptionByDefault.Builder> applyServerSideEncryptionByDefault) {
            return this.applyServerSideEncryptionByDefault((ServerSideEncryptionByDefault)((ServerSideEncryptionByDefault.Builder)ServerSideEncryptionByDefault.builder().applyMutation(applyServerSideEncryptionByDefault)).build());
        }

        public Builder bucketKeyEnabled(Boolean var1);
    }
}

