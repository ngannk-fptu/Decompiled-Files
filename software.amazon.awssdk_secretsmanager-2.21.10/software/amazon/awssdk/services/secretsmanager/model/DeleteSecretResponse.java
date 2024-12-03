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
package software.amazon.awssdk.services.secretsmanager.model;

import java.time.Instant;
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
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerResponse;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class DeleteSecretResponse
extends SecretsManagerResponse
implements ToCopyableBuilder<Builder, DeleteSecretResponse> {
    private static final SdkField<String> ARN_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ARN").getter(DeleteSecretResponse.getter(DeleteSecretResponse::arn)).setter(DeleteSecretResponse.setter(Builder::arn)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ARN").build()}).build();
    private static final SdkField<String> NAME_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Name").getter(DeleteSecretResponse.getter(DeleteSecretResponse::name)).setter(DeleteSecretResponse.setter(Builder::name)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Name").build()}).build();
    private static final SdkField<Instant> DELETION_DATE_FIELD = SdkField.builder((MarshallingType)MarshallingType.INSTANT).memberName("DeletionDate").getter(DeleteSecretResponse.getter(DeleteSecretResponse::deletionDate)).setter(DeleteSecretResponse.setter(Builder::deletionDate)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("DeletionDate").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(ARN_FIELD, NAME_FIELD, DELETION_DATE_FIELD));
    private final String arn;
    private final String name;
    private final Instant deletionDate;

    private DeleteSecretResponse(BuilderImpl builder) {
        super(builder);
        this.arn = builder.arn;
        this.name = builder.name;
        this.deletionDate = builder.deletionDate;
    }

    public final String arn() {
        return this.arn;
    }

    public final String name() {
        return this.name;
    }

    public final Instant deletionDate() {
        return this.deletionDate;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.arn());
        hashCode = 31 * hashCode + Objects.hashCode(this.name());
        hashCode = 31 * hashCode + Objects.hashCode(this.deletionDate());
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
        if (!(obj instanceof DeleteSecretResponse)) {
            return false;
        }
        DeleteSecretResponse other = (DeleteSecretResponse)((Object)obj);
        return Objects.equals(this.arn(), other.arn()) && Objects.equals(this.name(), other.name()) && Objects.equals(this.deletionDate(), other.deletionDate());
    }

    public final String toString() {
        return ToString.builder((String)"DeleteSecretResponse").add("ARN", (Object)this.arn()).add("Name", (Object)this.name()).add("DeletionDate", (Object)this.deletionDate()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "ARN": {
                return Optional.ofNullable(clazz.cast(this.arn()));
            }
            case "Name": {
                return Optional.ofNullable(clazz.cast(this.name()));
            }
            case "DeletionDate": {
                return Optional.ofNullable(clazz.cast(this.deletionDate()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<DeleteSecretResponse, T> g) {
        return obj -> g.apply((DeleteSecretResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends SecretsManagerResponse.BuilderImpl
    implements Builder {
        private String arn;
        private String name;
        private Instant deletionDate;

        private BuilderImpl() {
        }

        private BuilderImpl(DeleteSecretResponse model) {
            super(model);
            this.arn(model.arn);
            this.name(model.name);
            this.deletionDate(model.deletionDate);
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

        public final Instant getDeletionDate() {
            return this.deletionDate;
        }

        public final void setDeletionDate(Instant deletionDate) {
            this.deletionDate = deletionDate;
        }

        @Override
        public final Builder deletionDate(Instant deletionDate) {
            this.deletionDate = deletionDate;
            return this;
        }

        @Override
        public DeleteSecretResponse build() {
            return new DeleteSecretResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SecretsManagerResponse.Builder,
    SdkPojo,
    CopyableBuilder<Builder, DeleteSecretResponse> {
        public Builder arn(String var1);

        public Builder name(String var1);

        public Builder deletionDate(Instant var1);
    }
}

