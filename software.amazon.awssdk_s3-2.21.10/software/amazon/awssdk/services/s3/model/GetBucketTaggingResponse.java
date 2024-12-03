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
 *  software.amazon.awssdk.core.traits.RequiredTrait
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
import software.amazon.awssdk.core.traits.RequiredTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.services.s3.model.S3Response;
import software.amazon.awssdk.services.s3.model.Tag;
import software.amazon.awssdk.services.s3.model.TagSetCopier;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class GetBucketTaggingResponse
extends S3Response
implements ToCopyableBuilder<Builder, GetBucketTaggingResponse> {
    private static final SdkField<List<Tag>> TAG_SET_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("TagSet").getter(GetBucketTaggingResponse.getter(GetBucketTaggingResponse::tagSet)).setter(GetBucketTaggingResponse.setter(Builder::tagSet)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("TagSet").unmarshallLocationName("TagSet").build(), ListTrait.builder().memberLocationName("Tag").memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(Tag::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Tag").unmarshallLocationName("Tag").build()}).build()).build(), RequiredTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(TAG_SET_FIELD));
    private final List<Tag> tagSet;

    private GetBucketTaggingResponse(BuilderImpl builder) {
        super(builder);
        this.tagSet = builder.tagSet;
    }

    public final boolean hasTagSet() {
        return this.tagSet != null && !(this.tagSet instanceof SdkAutoConstructList);
    }

    public final List<Tag> tagSet() {
        return this.tagSet;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.hasTagSet() ? this.tagSet() : null);
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
        if (!(obj instanceof GetBucketTaggingResponse)) {
            return false;
        }
        GetBucketTaggingResponse other = (GetBucketTaggingResponse)((Object)obj);
        return this.hasTagSet() == other.hasTagSet() && Objects.equals(this.tagSet(), other.tagSet());
    }

    public final String toString() {
        return ToString.builder((String)"GetBucketTaggingResponse").add("TagSet", this.hasTagSet() ? this.tagSet() : null).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "TagSet": {
                return Optional.ofNullable(clazz.cast(this.tagSet()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<GetBucketTaggingResponse, T> g) {
        return obj -> g.apply((GetBucketTaggingResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Response.BuilderImpl
    implements Builder {
        private List<Tag> tagSet = DefaultSdkAutoConstructList.getInstance();

        private BuilderImpl() {
        }

        private BuilderImpl(GetBucketTaggingResponse model) {
            super(model);
            this.tagSet(model.tagSet);
        }

        public final List<Tag.Builder> getTagSet() {
            List<Tag.Builder> result = TagSetCopier.copyToBuilder(this.tagSet);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setTagSet(Collection<Tag.BuilderImpl> tagSet) {
            this.tagSet = TagSetCopier.copyFromBuilder(tagSet);
        }

        @Override
        public final Builder tagSet(Collection<Tag> tagSet) {
            this.tagSet = TagSetCopier.copy(tagSet);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder tagSet(Tag ... tagSet) {
            this.tagSet(Arrays.asList(tagSet));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder tagSet(Consumer<Tag.Builder> ... tagSet) {
            this.tagSet(Stream.of(tagSet).map(c -> (Tag)((Tag.Builder)Tag.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        @Override
        public GetBucketTaggingResponse build() {
            return new GetBucketTaggingResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Response.Builder,
    SdkPojo,
    CopyableBuilder<Builder, GetBucketTaggingResponse> {
        public Builder tagSet(Collection<Tag> var1);

        public Builder tagSet(Tag ... var1);

        public Builder tagSet(Consumer<Tag.Builder> ... var1);
    }
}

