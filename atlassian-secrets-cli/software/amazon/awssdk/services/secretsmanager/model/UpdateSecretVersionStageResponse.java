/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.model;

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
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerResponse;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class UpdateSecretVersionStageResponse
extends SecretsManagerResponse
implements ToCopyableBuilder<Builder, UpdateSecretVersionStageResponse> {
    private static final SdkField<String> ARN_FIELD = SdkField.builder(MarshallingType.STRING).memberName("ARN").getter(UpdateSecretVersionStageResponse.getter(UpdateSecretVersionStageResponse::arn)).setter(UpdateSecretVersionStageResponse.setter(Builder::arn)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ARN").build()).build();
    private static final SdkField<String> NAME_FIELD = SdkField.builder(MarshallingType.STRING).memberName("Name").getter(UpdateSecretVersionStageResponse.getter(UpdateSecretVersionStageResponse::name)).setter(UpdateSecretVersionStageResponse.setter(Builder::name)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Name").build()).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(ARN_FIELD, NAME_FIELD));
    private final String arn;
    private final String name;

    private UpdateSecretVersionStageResponse(BuilderImpl builder) {
        super(builder);
        this.arn = builder.arn;
        this.name = builder.name;
    }

    public final String arn() {
        return this.arn;
    }

    public final String name() {
        return this.name;
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

    @Override
    public final int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + super.hashCode();
        hashCode = 31 * hashCode + Objects.hashCode(this.arn());
        hashCode = 31 * hashCode + Objects.hashCode(this.name());
        return hashCode;
    }

    @Override
    public final boolean equals(Object obj) {
        return super.equals(obj) && this.equalsBySdkFields(obj);
    }

    @Override
    public final boolean equalsBySdkFields(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof UpdateSecretVersionStageResponse)) {
            return false;
        }
        UpdateSecretVersionStageResponse other = (UpdateSecretVersionStageResponse)obj;
        return Objects.equals(this.arn(), other.arn()) && Objects.equals(this.name(), other.name());
    }

    public final String toString() {
        return ToString.builder("UpdateSecretVersionStageResponse").add("ARN", this.arn()).add("Name", this.name()).build();
    }

    @Override
    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "ARN": {
                return Optional.ofNullable(clazz.cast(this.arn()));
            }
            case "Name": {
                return Optional.ofNullable(clazz.cast(this.name()));
            }
        }
        return Optional.empty();
    }

    @Override
    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<UpdateSecretVersionStageResponse, T> g) {
        return obj -> g.apply((UpdateSecretVersionStageResponse)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends SecretsManagerResponse.BuilderImpl
    implements Builder {
        private String arn;
        private String name;

        private BuilderImpl() {
        }

        private BuilderImpl(UpdateSecretVersionStageResponse model) {
            super(model);
            this.arn(model.arn);
            this.name(model.name);
        }

        public final String getArn() {
            return this.arn;
        }

        public final void setArn(String arn) {
            this.arn = arn;
        }

        @Override
        public final Builder arn(String arn) {
            this.arn = arn;
            return this;
        }

        public final String getName() {
            return this.name;
        }

        public final void setName(String name) {
            this.name = name;
        }

        @Override
        public final Builder name(String name) {
            this.name = name;
            return this;
        }

        @Override
        public UpdateSecretVersionStageResponse build() {
            return new UpdateSecretVersionStageResponse(this);
        }

        @Override
        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SecretsManagerResponse.Builder,
    SdkPojo,
    CopyableBuilder<Builder, UpdateSecretVersionStageResponse> {
        public Builder arn(String var1);

        public Builder name(String var1);
    }
}

