/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration$Builder
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
import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.sts.model.StsRequest;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class DecodeAuthorizationMessageRequest
extends StsRequest
implements ToCopyableBuilder<Builder, DecodeAuthorizationMessageRequest> {
    private static final SdkField<String> ENCODED_MESSAGE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("EncodedMessage").getter(DecodeAuthorizationMessageRequest.getter(DecodeAuthorizationMessageRequest::encodedMessage)).setter(DecodeAuthorizationMessageRequest.setter(Builder::encodedMessage)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("EncodedMessage").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(ENCODED_MESSAGE_FIELD));
    private final String encodedMessage;

    private DecodeAuthorizationMessageRequest(BuilderImpl builder) {
        super(builder);
        this.encodedMessage = builder.encodedMessage;
    }

    public final String encodedMessage() {
        return this.encodedMessage;
    }

    @Override
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
        hashCode = 31 * hashCode + Objects.hashCode(this.encodedMessage());
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
        if (!(obj instanceof DecodeAuthorizationMessageRequest)) {
            return false;
        }
        DecodeAuthorizationMessageRequest other = (DecodeAuthorizationMessageRequest)((Object)obj);
        return Objects.equals(this.encodedMessage(), other.encodedMessage());
    }

    public final String toString() {
        return ToString.builder((String)"DecodeAuthorizationMessageRequest").add("EncodedMessage", (Object)this.encodedMessage()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "EncodedMessage": {
                return Optional.ofNullable(clazz.cast(this.encodedMessage()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<DecodeAuthorizationMessageRequest, T> g) {
        return obj -> g.apply((DecodeAuthorizationMessageRequest)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends StsRequest.BuilderImpl
    implements Builder {
        private String encodedMessage;

        private BuilderImpl() {
        }

        private BuilderImpl(DecodeAuthorizationMessageRequest model) {
            super(model);
            this.encodedMessage(model.encodedMessage);
        }

        public final String getEncodedMessage() {
            return this.encodedMessage;
        }

        public final void setEncodedMessage(String encodedMessage) {
            this.encodedMessage = encodedMessage;
        }

        @Override
        public final Builder encodedMessage(String encodedMessage) {
            this.encodedMessage = encodedMessage;
            return this;
        }

        @Override
        public Builder overrideConfiguration(AwsRequestOverrideConfiguration overrideConfiguration) {
            super.overrideConfiguration(overrideConfiguration);
            return this;
        }

        @Override
        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> builderConsumer) {
            super.overrideConfiguration(builderConsumer);
            return this;
        }

        @Override
        public DecodeAuthorizationMessageRequest build() {
            return new DecodeAuthorizationMessageRequest(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends StsRequest.Builder,
    SdkPojo,
    CopyableBuilder<Builder, DecodeAuthorizationMessageRequest> {
        public Builder encodedMessage(String var1);

        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

