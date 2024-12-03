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
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.BucketsCopier;
import software.amazon.awssdk.services.s3.model.Owner;
import software.amazon.awssdk.services.s3.model.S3Response;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class ListBucketsResponse
extends S3Response
implements ToCopyableBuilder<Builder, ListBucketsResponse> {
    private static final SdkField<List<Bucket>> BUCKETS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("Buckets").getter(ListBucketsResponse.getter(ListBucketsResponse::buckets)).setter(ListBucketsResponse.setter(Builder::buckets)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Buckets").unmarshallLocationName("Buckets").build(), ListTrait.builder().memberLocationName("Bucket").memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(Bucket::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Bucket").unmarshallLocationName("Bucket").build()}).build()).build()}).build();
    private static final SdkField<Owner> OWNER_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Owner").getter(ListBucketsResponse.getter(ListBucketsResponse::owner)).setter(ListBucketsResponse.setter(Builder::owner)).constructor(Owner::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Owner").unmarshallLocationName("Owner").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(BUCKETS_FIELD, OWNER_FIELD));
    private final List<Bucket> buckets;
    private final Owner owner;

    private ListBucketsResponse(BuilderImpl builder) {
        super(builder);
        this.buckets = builder.buckets;
        this.owner = builder.owner;
    }

    public final boolean hasBuckets() {
        return this.buckets != null && !(this.buckets instanceof SdkAutoConstructList);
    }

    public final List<Bucket> buckets() {
        return this.buckets;
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
        hashCode = 31 * hashCode + super.hashCode();
        hashCode = 31 * hashCode + Objects.hashCode(this.hasBuckets() ? this.buckets() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.owner());
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
        if (!(obj instanceof ListBucketsResponse)) {
            return false;
        }
        ListBucketsResponse other = (ListBucketsResponse)((Object)obj);
        return this.hasBuckets() == other.hasBuckets() && Objects.equals(this.buckets(), other.buckets()) && Objects.equals(this.owner(), other.owner());
    }

    public final String toString() {
        return ToString.builder((String)"ListBucketsResponse").add("Buckets", this.hasBuckets() ? this.buckets() : null).add("Owner", (Object)this.owner()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Buckets": {
                return Optional.ofNullable(clazz.cast(this.buckets()));
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

    private static <T> Function<Object, T> getter(Function<ListBucketsResponse, T> g) {
        return obj -> g.apply((ListBucketsResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Response.BuilderImpl
    implements Builder {
        private List<Bucket> buckets = DefaultSdkAutoConstructList.getInstance();
        private Owner owner;

        private BuilderImpl() {
        }

        private BuilderImpl(ListBucketsResponse model) {
            super(model);
            this.buckets(model.buckets);
            this.owner(model.owner);
        }

        public final List<Bucket.Builder> getBuckets() {
            List<Bucket.Builder> result = BucketsCopier.copyToBuilder(this.buckets);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setBuckets(Collection<Bucket.BuilderImpl> buckets) {
            this.buckets = BucketsCopier.copyFromBuilder(buckets);
        }

        @Override
        public final Builder buckets(Collection<Bucket> buckets) {
            this.buckets = BucketsCopier.copy(buckets);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder buckets(Bucket ... buckets) {
            this.buckets(Arrays.asList(buckets));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder buckets(Consumer<Bucket.Builder> ... buckets) {
            this.buckets(Stream.of(buckets).map(c -> (Bucket)((Bucket.Builder)Bucket.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
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

        @Override
        public ListBucketsResponse build() {
            return new ListBucketsResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Response.Builder,
    SdkPojo,
    CopyableBuilder<Builder, ListBucketsResponse> {
        public Builder buckets(Collection<Bucket> var1);

        public Builder buckets(Bucket ... var1);

        public Builder buckets(Consumer<Bucket.Builder> ... var1);

        public Builder owner(Owner var1);

        default public Builder owner(Consumer<Owner.Builder> owner) {
            return this.owner((Owner)((Owner.Builder)Owner.builder().applyMutation(owner)).build());
        }
    }
}

