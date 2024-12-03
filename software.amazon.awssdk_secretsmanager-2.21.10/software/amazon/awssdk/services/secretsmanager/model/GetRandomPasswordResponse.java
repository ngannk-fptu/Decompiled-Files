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

public final class GetRandomPasswordResponse
extends SecretsManagerResponse
implements ToCopyableBuilder<Builder, GetRandomPasswordResponse> {
    private static final SdkField<String> RANDOM_PASSWORD_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("RandomPassword").getter(GetRandomPasswordResponse.getter(GetRandomPasswordResponse::randomPassword)).setter(GetRandomPasswordResponse.setter(Builder::randomPassword)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("RandomPassword").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(RANDOM_PASSWORD_FIELD));
    private final String randomPassword;

    private GetRandomPasswordResponse(BuilderImpl builder) {
        super(builder);
        this.randomPassword = builder.randomPassword;
    }

    public final String randomPassword() {
        return this.randomPassword;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.randomPassword());
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
        if (!(obj instanceof GetRandomPasswordResponse)) {
            return false;
        }
        GetRandomPasswordResponse other = (GetRandomPasswordResponse)((Object)obj);
        return Objects.equals(this.randomPassword(), other.randomPassword());
    }

    public final String toString() {
        return ToString.builder((String)"GetRandomPasswordResponse").add("RandomPassword", (Object)(this.randomPassword() == null ? null : "*** Sensitive Data Redacted ***")).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "RandomPassword": {
                return Optional.ofNullable(clazz.cast(this.randomPassword()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<GetRandomPasswordResponse, T> g) {
        return obj -> g.apply((GetRandomPasswordResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends SecretsManagerResponse.BuilderImpl
    implements Builder {
        private String randomPassword;

        private BuilderImpl() {
        }

        private BuilderImpl(GetRandomPasswordResponse model) {
            super(model);
            this.randomPassword(model.randomPassword);
        }

        public final String getRandomPassword() {
            return this.randomPassword;
        }

        public final void setRandomPassword(String randomPassword) {
            this.randomPassword = randomPassword;
        }

        @Override
        public final Builder randomPassword(String randomPassword) {
            this.randomPassword = randomPassword;
            return this;
        }

        @Override
        public GetRandomPasswordResponse build() {
            return new GetRandomPasswordResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SecretsManagerResponse.Builder,
    SdkPojo,
    CopyableBuilder<Builder, GetRandomPasswordResponse> {
        public Builder randomPassword(String var1);
    }
}

