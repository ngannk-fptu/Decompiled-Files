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
import software.amazon.awssdk.services.s3.model.DefaultRetention;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class ObjectLockRule
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, ObjectLockRule> {
    private static final SdkField<DefaultRetention> DEFAULT_RETENTION_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("DefaultRetention").getter(ObjectLockRule.getter(ObjectLockRule::defaultRetention)).setter(ObjectLockRule.setter(Builder::defaultRetention)).constructor(DefaultRetention::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("DefaultRetention").unmarshallLocationName("DefaultRetention").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(DEFAULT_RETENTION_FIELD));
    private static final long serialVersionUID = 1L;
    private final DefaultRetention defaultRetention;

    private ObjectLockRule(BuilderImpl builder) {
        this.defaultRetention = builder.defaultRetention;
    }

    public final DefaultRetention defaultRetention() {
        return this.defaultRetention;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.defaultRetention());
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
        if (!(obj instanceof ObjectLockRule)) {
            return false;
        }
        ObjectLockRule other = (ObjectLockRule)obj;
        return Objects.equals(this.defaultRetention(), other.defaultRetention());
    }

    public final String toString() {
        return ToString.builder((String)"ObjectLockRule").add("DefaultRetention", (Object)this.defaultRetention()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "DefaultRetention": {
                return Optional.ofNullable(clazz.cast(this.defaultRetention()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<ObjectLockRule, T> g) {
        return obj -> g.apply((ObjectLockRule)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private DefaultRetention defaultRetention;

        private BuilderImpl() {
        }

        private BuilderImpl(ObjectLockRule model) {
            this.defaultRetention(model.defaultRetention);
        }

        public final DefaultRetention.Builder getDefaultRetention() {
            return this.defaultRetention != null ? this.defaultRetention.toBuilder() : null;
        }

        public final void setDefaultRetention(DefaultRetention.BuilderImpl defaultRetention) {
            this.defaultRetention = defaultRetention != null ? defaultRetention.build() : null;
        }

        @Override
        public final Builder defaultRetention(DefaultRetention defaultRetention) {
            this.defaultRetention = defaultRetention;
            return this;
        }

        public ObjectLockRule build() {
            return new ObjectLockRule(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, ObjectLockRule> {
        public Builder defaultRetention(DefaultRetention var1);

        default public Builder defaultRetention(Consumer<DefaultRetention.Builder> defaultRetention) {
            return this.defaultRetention((DefaultRetention)((DefaultRetention.Builder)DefaultRetention.builder().applyMutation(defaultRetention)).build());
        }
    }
}

