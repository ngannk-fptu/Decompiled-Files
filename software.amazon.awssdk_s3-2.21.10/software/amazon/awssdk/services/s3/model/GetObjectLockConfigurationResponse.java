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
import software.amazon.awssdk.services.s3.model.ObjectLockConfiguration;
import software.amazon.awssdk.services.s3.model.S3Response;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class GetObjectLockConfigurationResponse
extends S3Response
implements ToCopyableBuilder<Builder, GetObjectLockConfigurationResponse> {
    private static final SdkField<ObjectLockConfiguration> OBJECT_LOCK_CONFIGURATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("ObjectLockConfiguration").getter(GetObjectLockConfigurationResponse.getter(GetObjectLockConfigurationResponse::objectLockConfiguration)).setter(GetObjectLockConfigurationResponse.setter(Builder::objectLockConfiguration)).constructor(ObjectLockConfiguration::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ObjectLockConfiguration").unmarshallLocationName("ObjectLockConfiguration").build(), PayloadTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(OBJECT_LOCK_CONFIGURATION_FIELD));
    private final ObjectLockConfiguration objectLockConfiguration;

    private GetObjectLockConfigurationResponse(BuilderImpl builder) {
        super(builder);
        this.objectLockConfiguration = builder.objectLockConfiguration;
    }

    public final ObjectLockConfiguration objectLockConfiguration() {
        return this.objectLockConfiguration;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.objectLockConfiguration());
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
        if (!(obj instanceof GetObjectLockConfigurationResponse)) {
            return false;
        }
        GetObjectLockConfigurationResponse other = (GetObjectLockConfigurationResponse)((Object)obj);
        return Objects.equals(this.objectLockConfiguration(), other.objectLockConfiguration());
    }

    public final String toString() {
        return ToString.builder((String)"GetObjectLockConfigurationResponse").add("ObjectLockConfiguration", (Object)this.objectLockConfiguration()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "ObjectLockConfiguration": {
                return Optional.ofNullable(clazz.cast(this.objectLockConfiguration()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<GetObjectLockConfigurationResponse, T> g) {
        return obj -> g.apply((GetObjectLockConfigurationResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Response.BuilderImpl
    implements Builder {
        private ObjectLockConfiguration objectLockConfiguration;

        private BuilderImpl() {
        }

        private BuilderImpl(GetObjectLockConfigurationResponse model) {
            super(model);
            this.objectLockConfiguration(model.objectLockConfiguration);
        }

        public final ObjectLockConfiguration.Builder getObjectLockConfiguration() {
            return this.objectLockConfiguration != null ? this.objectLockConfiguration.toBuilder() : null;
        }

        public final void setObjectLockConfiguration(ObjectLockConfiguration.BuilderImpl objectLockConfiguration) {
            this.objectLockConfiguration = objectLockConfiguration != null ? objectLockConfiguration.build() : null;
        }

        @Override
        public final Builder objectLockConfiguration(ObjectLockConfiguration objectLockConfiguration) {
            this.objectLockConfiguration = objectLockConfiguration;
            return this;
        }

        @Override
        public GetObjectLockConfigurationResponse build() {
            return new GetObjectLockConfigurationResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Response.Builder,
    SdkPojo,
    CopyableBuilder<Builder, GetObjectLockConfigurationResponse> {
        public Builder objectLockConfiguration(ObjectLockConfiguration var1);

        default public Builder objectLockConfiguration(Consumer<ObjectLockConfiguration.Builder> objectLockConfiguration) {
            return this.objectLockConfiguration((ObjectLockConfiguration)((ObjectLockConfiguration.Builder)ObjectLockConfiguration.builder().applyMutation(objectLockConfiguration)).build());
        }
    }
}

