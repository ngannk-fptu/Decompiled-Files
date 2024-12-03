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
import software.amazon.awssdk.services.s3.model.SelectObjectContentEventStream;
import software.amazon.awssdk.services.s3.model.SelectObjectContentResponseHandler;
import software.amazon.awssdk.services.s3.model.Stats;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public class StatsEvent
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, StatsEvent>,
SelectObjectContentEventStream {
    private static final SdkField<Stats> DETAILS_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Details").getter(StatsEvent.getter(StatsEvent::details)).setter(StatsEvent.setter(Builder::details)).constructor(Stats::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Details").unmarshallLocationName("Details").build(), PayloadTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(DETAILS_FIELD));
    private static final long serialVersionUID = 1L;
    private final Stats details;

    protected StatsEvent(BuilderImpl builder) {
        this.details = builder.details;
    }

    public final Stats details() {
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
        if (!(obj instanceof StatsEvent)) {
            return false;
        }
        StatsEvent other = (StatsEvent)obj;
        return Objects.equals(this.details(), other.details());
    }

    public final String toString() {
        return ToString.builder((String)"StatsEvent").add("Details", (Object)this.details()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Details": {
                return Optional.ofNullable(clazz.cast(this.details()));
            }
        }
        return Optional.empty();
    }

    public final StatsEvent copy(Consumer<? super Builder> modifier) {
        return (StatsEvent)super.copy(modifier);
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<StatsEvent, T> g) {
        return obj -> g.apply((StatsEvent)obj);
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
        private Stats details;

        protected BuilderImpl() {
        }

        protected BuilderImpl(StatsEvent model) {
            this.details(model.details);
        }

        public final Stats.Builder getDetails() {
            return this.details != null ? this.details.toBuilder() : null;
        }

        public final void setDetails(Stats.BuilderImpl details) {
            this.details = details != null ? details.build() : null;
        }

        @Override
        public final Builder details(Stats details) {
            this.details = details;
            return this;
        }

        public StatsEvent build() {
            return new StatsEvent(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, StatsEvent> {
        public Builder details(Stats var1);

        default public Builder details(Consumer<Stats.Builder> details) {
            return this.details((Stats)((Stats.Builder)Stats.builder().applyMutation(details)).build());
        }
    }
}

