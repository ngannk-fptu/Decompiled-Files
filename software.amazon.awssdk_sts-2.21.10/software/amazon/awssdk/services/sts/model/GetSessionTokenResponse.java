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
import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.sts.model.Credentials;
import software.amazon.awssdk.services.sts.model.StsResponse;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class GetSessionTokenResponse
extends StsResponse
implements ToCopyableBuilder<Builder, GetSessionTokenResponse> {
    private static final SdkField<Credentials> CREDENTIALS_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Credentials").getter(GetSessionTokenResponse.getter(GetSessionTokenResponse::credentials)).setter(GetSessionTokenResponse.setter(Builder::credentials)).constructor(Credentials::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Credentials").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(CREDENTIALS_FIELD));
    private final Credentials credentials;

    private GetSessionTokenResponse(BuilderImpl builder) {
        super(builder);
        this.credentials = builder.credentials;
    }

    public final Credentials credentials() {
        return this.credentials;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.credentials());
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
        if (!(obj instanceof GetSessionTokenResponse)) {
            return false;
        }
        GetSessionTokenResponse other = (GetSessionTokenResponse)((Object)obj);
        return Objects.equals(this.credentials(), other.credentials());
    }

    public final String toString() {
        return ToString.builder((String)"GetSessionTokenResponse").add("Credentials", (Object)this.credentials()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Credentials": {
                return Optional.ofNullable(clazz.cast(this.credentials()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<GetSessionTokenResponse, T> g) {
        return obj -> g.apply((GetSessionTokenResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends StsResponse.BuilderImpl
    implements Builder {
        private Credentials credentials;

        private BuilderImpl() {
        }

        private BuilderImpl(GetSessionTokenResponse model) {
            super(model);
            this.credentials(model.credentials);
        }

        public final Credentials.Builder getCredentials() {
            return this.credentials != null ? this.credentials.toBuilder() : null;
        }

        public final void setCredentials(Credentials.BuilderImpl credentials) {
            this.credentials = credentials != null ? credentials.build() : null;
        }

        @Override
        public final Builder credentials(Credentials credentials) {
            this.credentials = credentials;
            return this;
        }

        @Override
        public GetSessionTokenResponse build() {
            return new GetSessionTokenResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends StsResponse.Builder,
    SdkPojo,
    CopyableBuilder<Builder, GetSessionTokenResponse> {
        public Builder credentials(Credentials var1);

        default public Builder credentials(Consumer<Credentials.Builder> credentials) {
            return this.credentials((Credentials)((Credentials.Builder)Credentials.builder().applyMutation(credentials)).build());
        }
    }
}

