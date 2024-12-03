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
import software.amazon.awssdk.core.traits.RequiredTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.ObjectIdentifierListCopier;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class Delete
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, Delete> {
    private static final SdkField<List<ObjectIdentifier>> OBJECTS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("Objects").getter(Delete.getter(Delete::objects)).setter(Delete.setter(Builder::objects)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Object").unmarshallLocationName("Object").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(ObjectIdentifier::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build(), RequiredTrait.create()}).build();
    private static final SdkField<Boolean> QUIET_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("Quiet").getter(Delete.getter(Delete::quiet)).setter(Delete.setter(Builder::quiet)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Quiet").unmarshallLocationName("Quiet").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(OBJECTS_FIELD, QUIET_FIELD));
    private static final long serialVersionUID = 1L;
    private final List<ObjectIdentifier> objects;
    private final Boolean quiet;

    private Delete(BuilderImpl builder) {
        this.objects = builder.objects;
        this.quiet = builder.quiet;
    }

    public final boolean hasObjects() {
        return this.objects != null && !(this.objects instanceof SdkAutoConstructList);
    }

    public final List<ObjectIdentifier> objects() {
        return this.objects;
    }

    public final Boolean quiet() {
        return this.quiet;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.hasObjects() ? this.objects() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.quiet());
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
        if (!(obj instanceof Delete)) {
            return false;
        }
        Delete other = (Delete)obj;
        return this.hasObjects() == other.hasObjects() && Objects.equals(this.objects(), other.objects()) && Objects.equals(this.quiet(), other.quiet());
    }

    public final String toString() {
        return ToString.builder((String)"Delete").add("Objects", this.hasObjects() ? this.objects() : null).add("Quiet", (Object)this.quiet()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Objects": {
                return Optional.ofNullable(clazz.cast(this.objects()));
            }
            case "Quiet": {
                return Optional.ofNullable(clazz.cast(this.quiet()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<Delete, T> g) {
        return obj -> g.apply((Delete)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private List<ObjectIdentifier> objects = DefaultSdkAutoConstructList.getInstance();
        private Boolean quiet;

        private BuilderImpl() {
        }

        private BuilderImpl(Delete model) {
            this.objects(model.objects);
            this.quiet(model.quiet);
        }

        public final List<ObjectIdentifier.Builder> getObjects() {
            List<ObjectIdentifier.Builder> result = ObjectIdentifierListCopier.copyToBuilder(this.objects);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setObjects(Collection<ObjectIdentifier.BuilderImpl> objects) {
            this.objects = ObjectIdentifierListCopier.copyFromBuilder(objects);
        }

        @Override
        public final Builder objects(Collection<ObjectIdentifier> objects) {
            this.objects = ObjectIdentifierListCopier.copy(objects);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder objects(ObjectIdentifier ... objects) {
            this.objects(Arrays.asList(objects));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder objects(Consumer<ObjectIdentifier.Builder> ... objects) {
            this.objects(Stream.of(objects).map(c -> (ObjectIdentifier)((ObjectIdentifier.Builder)ObjectIdentifier.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        public final Boolean getQuiet() {
            return this.quiet;
        }

        public final void setQuiet(Boolean quiet) {
            this.quiet = quiet;
        }

        @Override
        public final Builder quiet(Boolean quiet) {
            this.quiet = quiet;
            return this;
        }

        public Delete build() {
            return new Delete(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, Delete> {
        public Builder objects(Collection<ObjectIdentifier> var1);

        public Builder objects(ObjectIdentifier ... var1);

        public Builder objects(Consumer<ObjectIdentifier.Builder> ... var1);

        public Builder quiet(Boolean var1);
    }
}

