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
package software.amazon.awssdk.services.sts.model;

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
import software.amazon.awssdk.services.sts.model.StsResponse;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class DecodeAuthorizationMessageResponse
extends StsResponse
implements ToCopyableBuilder<Builder, DecodeAuthorizationMessageResponse> {
    private static final SdkField<String> DECODED_MESSAGE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("DecodedMessage").getter(DecodeAuthorizationMessageResponse.getter(DecodeAuthorizationMessageResponse::decodedMessage)).setter(DecodeAuthorizationMessageResponse.setter(Builder::decodedMessage)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("DecodedMessage").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(DECODED_MESSAGE_FIELD));
    private final String decodedMessage;

    private DecodeAuthorizationMessageResponse(BuilderImpl builder) {
        super(builder);
        this.decodedMessage = builder.decodedMessage;
    }

    public final String decodedMessage() {
        return this.decodedMessage;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.decodedMessage());
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
        if (!(obj instanceof DecodeAuthorizationMessageResponse)) {
            return false;
        }
        DecodeAuthorizationMessageResponse other = (DecodeAuthorizationMessageResponse)((Object)obj);
        return Objects.equals(this.decodedMessage(), other.decodedMessage());
    }

    public final String toString() {
        return ToString.builder((String)"DecodeAuthorizationMessageResponse").add("DecodedMessage", (Object)this.decodedMessage()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "DecodedMessage": {
                return Optional.ofNullable(clazz.cast(this.decodedMessage()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<DecodeAuthorizationMessageResponse, T> g) {
        return obj -> g.apply((DecodeAuthorizationMessageResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends StsResponse.BuilderImpl
    implements Builder {
        private String decodedMessage;

        private BuilderImpl() {
        }

        private BuilderImpl(DecodeAuthorizationMessageResponse model) {
            super(model);
            this.decodedMessage(model.decodedMessage);
        }

        public final String getDecodedMessage() {
            return this.decodedMessage;
        }

        public final void setDecodedMessage(String decodedMessage) {
            this.decodedMessage = decodedMessage;
        }

        @Override
        public final Builder decodedMessage(String decodedMessage) {
            this.decodedMessage = decodedMessage;
            return this;
        }

        @Override
        public DecodeAuthorizationMessageResponse build() {
            return new DecodeAuthorizationMessageResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends StsResponse.Builder,
    SdkPojo,
    CopyableBuilder<Builder, DecodeAuthorizationMessageResponse> {
        public Builder decodedMessage(String var1);
    }
}

