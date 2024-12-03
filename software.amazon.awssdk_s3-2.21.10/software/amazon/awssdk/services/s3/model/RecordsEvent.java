/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.SdkBytes
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
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.PayloadTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.SelectObjectContentEventStream;
import software.amazon.awssdk.services.s3.model.SelectObjectContentResponseHandler;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public class RecordsEvent
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, RecordsEvent>,
SelectObjectContentEventStream {
    private static final SdkField<SdkBytes> PAYLOAD_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_BYTES).memberName("Payload").getter(RecordsEvent.getter(RecordsEvent::payload)).setter(RecordsEvent.setter(Builder::payload)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Payload").unmarshallLocationName("Payload").build(), PayloadTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(PAYLOAD_FIELD));
    private static final long serialVersionUID = 1L;
    private final SdkBytes payload;

    protected RecordsEvent(BuilderImpl builder) {
        this.payload = builder.payload;
    }

    public final SdkBytes payload() {
        return this.payload;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.payload());
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
        if (!(obj instanceof RecordsEvent)) {
            return false;
        }
        RecordsEvent other = (RecordsEvent)obj;
        return Objects.equals(this.payload(), other.payload());
    }

    public final String toString() {
        return ToString.builder((String)"RecordsEvent").add("Payload", (Object)this.payload()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Payload": {
                return Optional.ofNullable(clazz.cast(this.payload()));
            }
        }
        return Optional.empty();
    }

    public final RecordsEvent copy(Consumer<? super Builder> modifier) {
        return (RecordsEvent)super.copy(modifier);
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<RecordsEvent, T> g) {
        return obj -> g.apply((RecordsEvent)obj);
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
        private SdkBytes payload;

        protected BuilderImpl() {
        }

        protected BuilderImpl(RecordsEvent model) {
            this.payload(model.payload);
        }

        public final ByteBuffer getPayload() {
            return this.payload == null ? null : this.payload.asByteBuffer();
        }

        public final void setPayload(ByteBuffer payload) {
            this.payload(payload == null ? null : SdkBytes.fromByteBuffer((ByteBuffer)payload));
        }

        @Override
        public final Builder payload(SdkBytes payload) {
            this.payload = payload;
            return this;
        }

        public RecordsEvent build() {
            return new RecordsEvent(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, RecordsEvent> {
        public Builder payload(SdkBytes var1);
    }
}

