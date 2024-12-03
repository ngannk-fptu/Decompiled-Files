/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.PayloadTrait
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
import software.amazon.awssdk.core.traits.PayloadTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.Progress;
import software.amazon.awssdk.services.s3.model.SelectObjectContentEventStream;
import software.amazon.awssdk.services.s3.model.SelectObjectContentResponseHandler;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public class ProgressEvent
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, ProgressEvent>,
SelectObjectContentEventStream {
    private static final SdkField<Progress> DETAILS_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Details").getter(ProgressEvent.getter(ProgressEvent::details)).setter(ProgressEvent.setter(Builder::details)).constructor(Progress::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Details").unmarshallLocationName("Details").build(), PayloadTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(DETAILS_FIELD));
    private static final long serialVersionUID = 1L;
    private final Progress details;

    protected ProgressEvent(BuilderImpl builder) {
        this.details = builder.details;
    }

    public final Progress details() {
        return this.details;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.details());
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
        if (!(obj instanceof ProgressEvent)) {
            return false;
        }
        ProgressEvent other = (ProgressEvent)obj;
        return Objects.equals(this.details(), other.details());
    }

    public final String toString() {
        return ToString.builder((String)"ProgressEvent").add("Details", (Object)this.details()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Details": {
                return Optional.ofNullable(clazz.cast(this.details()));
            }
        }
        return Optional.empty();
    }

    public final ProgressEvent copy(Consumer<? super Builder> modifier) {
        return (ProgressEvent)super.copy(modifier);
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<ProgressEvent, T> g) {
        return obj -> g.apply((ProgressEvent)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    @Override
    public void accept(SelectObjectContentResponseHandler.Visitor visitor) {
        throw new UnsupportedOperationException();
    }

    protected static class BuilderImpl
    implements Builder {
        private Progress details;

        protected BuilderImpl() {
        }

        protected BuilderImpl(ProgressEvent model) {
            this.details(model.details);
        }

        public final Progress.Builder getDetails() {
            return this.details != null ? this.details.toBuilder() : null;
        }

        public final void setDetails(Progress.BuilderImpl details) {
            this.details = details != null ? details.build() : null;
        }

        @Override
        public final Builder details(Progress details) {
            this.details = details;
            return this;
        }

        public ProgressEvent build() {
            return new ProgressEvent(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, ProgressEvent> {
        public Builder details(Progress var1);

        default public Builder details(Consumer<Progress.Builder> details) {
            return this.details((Progress)((Progress.Builder)Progress.builder().applyMutation(details)).build());
        }
    }
}

