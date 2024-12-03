/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.ListTrait
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.Trait
 *  software.amazon.awssdk.core.util.DefaultSdkAutoConstructList
 *  software.amazon.awssdk.core.util.SdkAutoConstructList
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.model;

import java.io.Serializable;
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
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.ListTrait;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.services.s3.model.Grant;
import software.amazon.awssdk.services.s3.model.GrantsCopier;
import software.amazon.awssdk.services.s3.model.Owner;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class AccessControlPolicy
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, AccessControlPolicy> {
    private static final SdkField<List<Grant>> GRANTS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("Grants").getter(AccessControlPolicy.getter(AccessControlPolicy::grants)).setter(AccessControlPolicy.setter(Builder::grants)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("AccessControlList").unmarshallLocationName("AccessControlList").build(), ListTrait.builder().memberLocationName("Grant").memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(Grant::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Grant").unmarshallLocationName("Grant").build()}).build()).build()}).build();
    private static final SdkField<Owner> OWNER_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Owner").getter(AccessControlPolicy.getter(AccessControlPolicy::owner)).setter(AccessControlPolicy.setter(Builder::owner)).constructor(Owner::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Owner").unmarshallLocationName("Owner").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(GRANTS_FIELD, OWNER_FIELD));
    private static final long serialVersionUID = 1L;
    private final List<Grant> grants;
    private final Owner owner;

    private AccessControlPolicy(BuilderImpl builder) {
        this.grants = builder.grants;
        this.owner = builder.owner;
    }

    public final boolean hasGrants() {
        return this.grants != null && !(this.grants instanceof SdkAutoConstructList);
    }

    public final List<Grant> grants() {
        return this.grants;
    }

    public final Owner owner() {
        return this.owner;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.hasGrants() ? this.grants() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.owner());
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
        if (!(obj instanceof AccessControlPolicy)) {
            return false;
        }
        AccessControlPolicy other = (AccessControlPolicy)obj;
        return this.hasGrants() == other.hasGrants() && Objects.equals(this.grants(), other.grants()) && Objects.equals(this.owner(), other.owner());
    }

    public final String toString() {
        return ToString.builder((String)"AccessControlPolicy").add("Grants", this.hasGrants() ? this.grants() : null).add("Owner", (Object)this.owner()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Grants": {
                return Optional.ofNullable(clazz.cast(this.grants()));
            }
            case "Owner": {
                return Optional.ofNullable(clazz.cast(this.owner()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<AccessControlPolicy, T> g) {
        return obj -> g.apply((AccessControlPolicy)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private List<Grant> grants = DefaultSdkAutoConstructList.getInstance();
        private Owner owner;

        private BuilderImpl() {
        }

        private BuilderImpl(AccessControlPolicy model) {
            this.grants(model.grants);
            this.owner(model.owner);
        }

        public final List<Grant.Builder> getGrants() {
            List<Grant.Builder> result = GrantsCopier.copyToBuilder(this.grants);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setGrants(Collection<Grant.BuilderImpl> grants) {
            this.grants = GrantsCopier.copyFromBuilder(grants);
        }

        @Override
        public final Builder grants(Collection<Grant> grants) {
            this.grants = GrantsCopier.copy(grants);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder grants(Grant ... grants) {
            this.grants(Arrays.asList(grants));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder grants(Consumer<Grant.Builder> ... grants) {
            this.grants(Stream.of(grants).map(c -> (Grant)((Grant.Builder)Grant.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        public final Owner.Builder getOwner() {
            return this.owner != null ? this.owner.toBuilder() : null;
        }

        public final void setOwner(Owner.BuilderImpl owner) {
            this.owner = owner != null ? owner.build() : null;
        }

        @Override
        public final Builder owner(Owner owner) {
            this.owner = owner;
            return this;
        }

        public AccessControlPolicy build() {
            return new AccessControlPolicy(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, AccessControlPolicy> {
        public Builder grants(Collection<Grant> var1);

        public Builder grants(Grant ... var1);

        public Builder grants(Consumer<Grant.Builder> ... var1);

        public Builder owner(Owner var1);

        default public Builder owner(Consumer<Owner.Builder> owner) {
            return this.owner((Owner)((Owner.Builder)Owner.builder().applyMutation(owner)).build());
        }
    }
}

