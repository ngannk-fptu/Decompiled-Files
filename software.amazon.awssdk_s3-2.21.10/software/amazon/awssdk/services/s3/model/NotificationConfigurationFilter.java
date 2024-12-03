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
import software.amazon.awssdk.services.s3.model.S3KeyFilter;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class NotificationConfigurationFilter
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, NotificationConfigurationFilter> {
    private static final SdkField<S3KeyFilter> KEY_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Key").getter(NotificationConfigurationFilter.getter(NotificationConfigurationFilter::key)).setter(NotificationConfigurationFilter.setter(Builder::key)).constructor(S3KeyFilter::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("S3Key").unmarshallLocationName("S3Key").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(KEY_FIELD));
    private static final long serialVersionUID = 1L;
    private final S3KeyFilter key;

    private NotificationConfigurationFilter(BuilderImpl builder) {
        this.key = builder.key;
    }

    public final S3KeyFilter key() {
        return this.key;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.key());
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
        if (!(obj instanceof NotificationConfigurationFilter)) {
            return false;
        }
        NotificationConfigurationFilter other = (NotificationConfigurationFilter)obj;
        return Objects.equals(this.key(), other.key());
    }

    public final String toString() {
        return ToString.builder((String)"NotificationConfigurationFilter").add("Key", (Object)this.key()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Key": {
                return Optional.ofNullable(clazz.cast(this.key()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<NotificationConfigurationFilter, T> g) {
        return obj -> g.apply((NotificationConfigurationFilter)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private S3KeyFilter key;

        private BuilderImpl() {
        }

        private BuilderImpl(NotificationConfigurationFilter model) {
            this.key(model.key);
        }

        public final S3KeyFilter.Builder getKey() {
            return this.key != null ? this.key.toBuilder() : null;
        }

        public final void setKey(S3KeyFilter.BuilderImpl key) {
            this.key = key != null ? key.build() : null;
        }

        @Override
        public final Builder key(S3KeyFilter key) {
            this.key = key;
            return this;
        }

        public NotificationConfigurationFilter build() {
            return new NotificationConfigurationFilter(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, NotificationConfigurationFilter> {
        public Builder key(S3KeyFilter var1);

        default public Builder key(Consumer<S3KeyFilter.Builder> key) {
            return this.key((S3KeyFilter)((S3KeyFilter.Builder)S3KeyFilter.builder().applyMutation(key)).build());
        }
    }
}

