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
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class RequestProgress
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, RequestProgress> {
    private static final SdkField<Boolean> ENABLED_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("Enabled").getter(RequestProgress.getter(RequestProgress::enabled)).setter(RequestProgress.setter(Builder::enabled)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Enabled").unmarshallLocationName("Enabled").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(ENABLED_FIELD));
    private static final long serialVersionUID = 1L;
    private final Boolean enabled;

    private RequestProgress(BuilderImpl builder) {
        this.enabled = builder.enabled;
    }

    public final Boolean enabled() {
        return this.enabled;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.enabled());
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
        if (!(obj instanceof RequestProgress)) {
            return false;
        }
        RequestProgress other = (RequestProgress)obj;
        return Objects.equals(this.enabled(), other.enabled());
    }

    public final String toString() {
        return ToString.builder((String)"RequestProgress").add("Enabled", (Object)this.enabled()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Enabled": {
                return Optional.ofNullable(clazz.cast(this.enabled()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<RequestProgress, T> g) {
        return obj -> g.apply((RequestProgress)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private Boolean enabled;

        private BuilderImpl() {
        }

        private BuilderImpl(RequestProgress model) {
            this.enabled(model.enabled);
        }

        public final Boolean getEnabled() {
            return this.enabled;
        }

        public final void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        @Override
        public final Builder enabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public RequestProgress build() {
            return new RequestProgress(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, RequestProgress> {
        public Builder enabled(Boolean var1);
    }
}

