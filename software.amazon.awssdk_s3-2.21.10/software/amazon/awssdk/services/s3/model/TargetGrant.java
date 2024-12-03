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
 *  software.amazon.awssdk.core.traits.XmlAttributesTrait
 *  software.amazon.awssdk.core.traits.XmlAttributesTrait$AttributeAccessors
 *  software.amazon.awssdk.utils.Pair
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
import software.amazon.awssdk.core.traits.XmlAttributesTrait;
import software.amazon.awssdk.services.s3.model.BucketLogsPermission;
import software.amazon.awssdk.services.s3.model.Grantee;
import software.amazon.awssdk.utils.Pair;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class TargetGrant
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, TargetGrant> {
    private static final SdkField<Grantee> GRANTEE_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Grantee").getter(TargetGrant.getter(TargetGrant::grantee)).setter(TargetGrant.setter(Builder::grantee)).constructor(Grantee::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Grantee").unmarshallLocationName("Grantee").build(), XmlAttributesTrait.create((Pair[])new Pair[]{Pair.of((Object)"xmlns:xsi", (Object)XmlAttributesTrait.AttributeAccessors.builder().attributeGetter(ignore -> "http://www.w3.org/2001/XMLSchema-instance").build()), Pair.of((Object)"xsi:type", (Object)XmlAttributesTrait.AttributeAccessors.builder().attributeGetter(t -> ((Grantee)t).typeAsString()).build())})}).build();
    private static final SdkField<String> PERMISSION_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Permission").getter(TargetGrant.getter(TargetGrant::permissionAsString)).setter(TargetGrant.setter(Builder::permission)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Permission").unmarshallLocationName("Permission").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(GRANTEE_FIELD, PERMISSION_FIELD));
    private static final long serialVersionUID = 1L;
    private final Grantee grantee;
    private final String permission;

    private TargetGrant(BuilderImpl builder) {
        this.grantee = builder.grantee;
        this.permission = builder.permission;
    }

    public final Grantee grantee() {
        return this.grantee;
    }

    public final BucketLogsPermission permission() {
        return BucketLogsPermission.fromValue(this.permission);
    }

    public final String permissionAsString() {
        return this.permission;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.grantee());
        hashCode = 31 * hashCode + Objects.hashCode(this.permissionAsString());
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
        if (!(obj instanceof TargetGrant)) {
            return false;
        }
        TargetGrant other = (TargetGrant)obj;
        return Objects.equals(this.grantee(), other.grantee()) && Objects.equals(this.permissionAsString(), other.permissionAsString());
    }

    public final String toString() {
        return ToString.builder((String)"TargetGrant").add("Grantee", (Object)this.grantee()).add("Permission", (Object)this.permissionAsString()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Grantee": {
                return Optional.ofNullable(clazz.cast(this.grantee()));
            }
            case "Permission": {
                return Optional.ofNullable(clazz.cast(this.permissionAsString()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<TargetGrant, T> g) {
        return obj -> g.apply((TargetGrant)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private Grantee grantee;
        private String permission;

        private BuilderImpl() {
        }

        private BuilderImpl(TargetGrant model) {
            this.grantee(model.grantee);
            this.permission(model.permission);
        }

        public final Grantee.Builder getGrantee() {
            return this.grantee != null ? this.grantee.toBuilder() : null;
        }

        public final void setGrantee(Grantee.BuilderImpl grantee) {
            this.grantee = grantee != null ? grantee.build() : null;
        }

        @Override
        public final Builder grantee(Grantee grantee) {
            this.grantee = grantee;
            return this;
        }

        public final String getPermission() {
            return this.permission;
        }

        public final void setPermission(String permission) {
            this.permission = permission;
        }

        @Override
        public final Builder permission(String permission) {
            this.permission = permission;
            return this;
        }

        @Override
        public final Builder permission(BucketLogsPermission permission) {
            this.permission(permission == null ? null : permission.toString());
            return this;
        }

        public TargetGrant build() {
            return new TargetGrant(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, TargetGrant> {
        public Builder grantee(Grantee var1);

        default public Builder grantee(Consumer<Grantee.Builder> grantee) {
            return this.grantee((Grantee)((Grantee.Builder)Grantee.builder().applyMutation(grantee)).build());
        }

        public Builder permission(String var1);

        public Builder permission(BucketLogsPermission var1);
    }
}

