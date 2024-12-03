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

public final class GetAccessKeyInfoRequest
extends StsRequest
implements ToCopyableBuilder<Builder, GetAccessKeyInfoRequest> {
    private static final SdkField<String> ACCESS_KEY_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("AccessKeyId").getter(GetAccessKeyInfoRequest.getter(GetAccessKeyInfoRequest::accessKeyId)).setter(GetAccessKeyInfoRequest.setter(Builder::accessKeyId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("AccessKeyId").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(ACCESS_KEY_ID_FIELD));
    private final String accessKeyId;

    private GetAccessKeyInfoRequest(BuilderImpl builder) {
        super(builder);
        this.accessKeyId = builder.accessKeyId;
    }

    public final String accessKeyId() {
        return this.accessKeyId;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.accessKeyId());
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
        if (!(obj instanceof GetAccessKeyInfoRequest)) {
            return false;
        }
        GetAccessKeyInfoRequest other = (GetAccessKeyInfoRequest)((Object)obj);
        return Objects.equals(this.accessKeyId(), other.accessKeyId());
    }

    public final String toString() {
        return ToString.builder((String)"GetAccessKeyInfoRequest").add("AccessKeyId", (Object)this.accessKeyId()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "AccessKeyId": {
                return Optional.ofNullable(clazz.cast(this.accessKeyId()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<GetAccessKeyInfoRequest, T> g) {
        return obj -> g.apply((GetAccessKeyInfoRequest)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends StsRequest.BuilderImpl
    implements Builder {
        private String accessKeyId;

        private BuilderImpl() {
        }

        private BuilderImpl(GetAccessKeyInfoRequest model) {
            super(model);
            this.accessKeyId(model.accessKeyId);
        }

        public final String getAccessKeyId() {
            return this.accessKeyId;
        }

        public final void setAccessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
        }

        @Override
        public final Builder accessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
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
        public GetAccessKeyInfoRequest build() {
            return new GetAccessKeyInfoRequest(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends StsRequest.Builder,
    SdkPojo,
    CopyableBuilder<Builder, GetAccessKeyInfoRequest> {
        public Builder accessKeyId(String var1);

        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

