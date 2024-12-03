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
import software.amazon.awssdk.services.s3.model.S3Response;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class GetBucketAclResponse
extends S3Response
implements ToCopyableBuilder<Builder, GetBucketAclResponse> {
    private static final SdkField<Owner> OWNER_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Owner").getter(GetBucketAclResponse.getter(GetBucketAclResponse::owner)).setter(GetBucketAclResponse.setter(Builder::owner)).constructor(Owner::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Owner").unmarshallLocationName("Owner").build()}).build();
    private static final SdkField<List<Grant>> GRANTS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("Grants").getter(GetBucketAclResponse.getter(GetBucketAclResponse::grants)).setter(GetBucketAclResponse.setter(Builder::grants)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("AccessControlList").unmarshallLocationName("AccessControlList").build(), ListTrait.builder().memberLocationName("Grant").memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(Grant::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Grant").unmarshallLocationName("Grant").build()}).build()).build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(OWNER_FIELD, GRANTS_FIELD));
    private final Owner owner;
    private final List<Grant> grants;

    private GetBucketAclResponse(BuilderImpl builder) {
        super(builder);
        this.owner = builder.owner;
        this.grants = builder.grants;
    }

    public final Owner owner() {
        return this.owner;
    }

    public final boolean hasGrants() {
        return this.grants != null && !(this.grants instanceof SdkAutoConstructList);
    }

    public final List<Grant> grants() {
        return this.grants;
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
        hashCode = 31 * hashCode + super.hashCode();
        hashCode = 31 * hashCode + Objects.hashCode(this.owner());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasGrants() ? this.grants() : null);
        return hashCode;
    }

    public final boolean equals(Object obj) {
        return super.equals(obj) && this.equalsBySdkFields(obj);
    }

    public final boolean equalsBySdkFields(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof GetBucketAclResponse)) {
            return false;
        }
        GetBucketAclResponse other = (GetBucketAclResponse)((Object)obj);
        return Objects.equals(this.owner(), other.owner()) && this.hasGrants() == other.hasGrants() && Objects.equals(this.grants(), other.grants());
    }

    public final String toString() {
        return ToString.builder((String)"GetBucketAclResponse").add("Owner", (Object)this.owner()).add("Grants", this.hasGrants() ? this.grants() : null).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Owner": {
                return Optional.ofNullable(clazz.cast(this.owner()));
            }
            case "Grants": {
                return Optional.ofNullable(clazz.cast(this.grants()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<GetBucketAclResponse, T> g) {
        return obj -> g.apply((GetBucketAclResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Response.BuilderImpl
    implements Builder {
        private Owner owner;
        private List<Grant> grants = DefaultSdkAutoConstructList.getInstance();

        private BuilderImpl() {
        }

        private BuilderImpl(GetBucketAclResponse model) {
            super(model);
            this.owner(model.owner);
            this.grants(model.grants);
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

        @Override
        public GetBucketAclResponse build() {
            return new GetBucketAclResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Response.Builder,
    SdkPojo,
    CopyableBuilder<Builder, GetBucketAclResponse> {
        public Builder owner(Owner var1);

        default public Builder owner(Consumer<Owner.Builder> owner) {
            return this.owner((Owner)((Owner.Builder)Owner.builder().applyMutation(owner)).build());
        }

        public Builder grants(Collection<Grant> var1);

        public Builder grants(Grant ... var1);

        public Builder grants(Consumer<Grant.Builder> ... var1);
    }
}

