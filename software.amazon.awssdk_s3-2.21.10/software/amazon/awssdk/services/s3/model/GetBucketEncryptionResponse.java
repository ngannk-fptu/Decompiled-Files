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
import software.amazon.awssdk.services.s3.model.S3Response;
import software.amazon.awssdk.services.s3.model.ServerSideEncryptionConfiguration;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class GetBucketEncryptionResponse
extends S3Response
implements ToCopyableBuilder<Builder, GetBucketEncryptionResponse> {
    private static final SdkField<ServerSideEncryptionConfiguration> SERVER_SIDE_ENCRYPTION_CONFIGURATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("ServerSideEncryptionConfiguration").getter(GetBucketEncryptionResponse.getter(GetBucketEncryptionResponse::serverSideEncryptionConfiguration)).setter(GetBucketEncryptionResponse.setter(Builder::serverSideEncryptionConfiguration)).constructor(ServerSideEncryptionConfiguration::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ServerSideEncryptionConfiguration").unmarshallLocationName("ServerSideEncryptionConfiguration").build(), PayloadTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(SERVER_SIDE_ENCRYPTION_CONFIGURATION_FIELD));
    private final ServerSideEncryptionConfiguration serverSideEncryptionConfiguration;

    private GetBucketEncryptionResponse(BuilderImpl builder) {
        super(builder);
        this.serverSideEncryptionConfiguration = builder.serverSideEncryptionConfiguration;
    }

    public final ServerSideEncryptionConfiguration serverSideEncryptionConfiguration() {
        return this.serverSideEncryptionConfiguration;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.serverSideEncryptionConfiguration());
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
        if (!(obj instanceof GetBucketEncryptionResponse)) {
            return false;
        }
        GetBucketEncryptionResponse other = (GetBucketEncryptionResponse)((Object)obj);
        return Objects.equals(this.serverSideEncryptionConfiguration(), other.serverSideEncryptionConfiguration());
    }

    public final String toString() {
        return ToString.builder((String)"GetBucketEncryptionResponse").add("ServerSideEncryptionConfiguration", (Object)this.serverSideEncryptionConfiguration()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "ServerSideEncryptionConfiguration": {
                return Optional.ofNullable(clazz.cast(this.serverSideEncryptionConfiguration()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<GetBucketEncryptionResponse, T> g) {
        return obj -> g.apply((GetBucketEncryptionResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Response.BuilderImpl
    implements Builder {
        private ServerSideEncryptionConfiguration serverSideEncryptionConfiguration;

        private BuilderImpl() {
        }

        private BuilderImpl(GetBucketEncryptionResponse model) {
            super(model);
            this.serverSideEncryptionConfiguration(model.serverSideEncryptionConfiguration);
        }

        public final ServerSideEncryptionConfiguration.Builder getServerSideEncryptionConfiguration() {
            return this.serverSideEncryptionConfiguration != null ? this.serverSideEncryptionConfiguration.toBuilder() : null;
        }

        public final void setServerSideEncryptionConfiguration(ServerSideEncryptionConfiguration.BuilderImpl serverSideEncryptionConfiguration) {
            this.serverSideEncryptionConfiguration = serverSideEncryptionConfiguration != null ? serverSideEncryptionConfiguration.build() : null;
        }

        @Override
        public final Builder serverSideEncryptionConfiguration(ServerSideEncryptionConfiguration serverSideEncryptionConfiguration) {
            this.serverSideEncryptionConfiguration = serverSideEncryptionConfiguration;
            return this;
        }

        @Override
        public GetBucketEncryptionResponse build() {
            return new GetBucketEncryptionResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Response.Builder,
    SdkPojo,
    CopyableBuilder<Builder, GetBucketEncryptionResponse> {
        public Builder serverSideEncryptionConfiguration(ServerSideEncryptionConfiguration var1);

        default public Builder serverSideEncryptionConfiguration(Consumer<ServerSideEncryptionConfiguration.Builder> serverSideEncryptionConfiguration) {
            return this.serverSideEncryptionConfiguration((ServerSideEncryptionConfiguration)((ServerSideEncryptionConfiguration.Builder)ServerSideEncryptionConfiguration.builder().applyMutation(serverSideEncryptionConfiguration)).build());
        }
    }
}

