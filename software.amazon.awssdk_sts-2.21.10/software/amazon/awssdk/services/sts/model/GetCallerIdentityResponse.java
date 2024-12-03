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

public final class GetCallerIdentityResponse
extends StsResponse
implements ToCopyableBuilder<Builder, GetCallerIdentityResponse> {
    private static final SdkField<String> USER_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("UserId").getter(GetCallerIdentityResponse.getter(GetCallerIdentityResponse::userId)).setter(GetCallerIdentityResponse.setter(Builder::userId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("UserId").build()}).build();
    private static final SdkField<String> ACCOUNT_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Account").getter(GetCallerIdentityResponse.getter(GetCallerIdentityResponse::account)).setter(GetCallerIdentityResponse.setter(Builder::account)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Account").build()}).build();
    private static final SdkField<String> ARN_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Arn").getter(GetCallerIdentityResponse.getter(GetCallerIdentityResponse::arn)).setter(GetCallerIdentityResponse.setter(Builder::arn)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Arn").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(USER_ID_FIELD, ACCOUNT_FIELD, ARN_FIELD));
    private final String userId;
    private final String account;
    private final String arn;

    private GetCallerIdentityResponse(BuilderImpl builder) {
        super(builder);
        this.userId = builder.userId;
        this.account = builder.account;
        this.arn = builder.arn;
    }

    public final String userId() {
        return this.userId;
    }

    public final String account() {
        return this.account;
    }

    public final String arn() {
        return this.arn;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.userId());
        hashCode = 31 * hashCode + Objects.hashCode(this.account());
        hashCode = 31 * hashCode + Objects.hashCode(this.arn());
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
        if (!(obj instanceof GetCallerIdentityResponse)) {
            return false;
        }
        GetCallerIdentityResponse other = (GetCallerIdentityResponse)((Object)obj);
        return Objects.equals(this.userId(), other.userId()) && Objects.equals(this.account(), other.account()) && Objects.equals(this.arn(), other.arn());
    }

    public final String toString() {
        return ToString.builder((String)"GetCallerIdentityResponse").add("UserId", (Object)this.userId()).add("Account", (Object)this.account()).add("Arn", (Object)this.arn()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "UserId": {
                return Optional.ofNullable(clazz.cast(this.userId()));
            }
            case "Account": {
                return Optional.ofNullable(clazz.cast(this.account()));
            }
            case "Arn": {
                return Optional.ofNullable(clazz.cast(this.arn()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<GetCallerIdentityResponse, T> g) {
        return obj -> g.apply((GetCallerIdentityResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends StsResponse.BuilderImpl
    implements Builder {
        private String userId;
        private String account;
        private String arn;

        private BuilderImpl() {
        }

        private BuilderImpl(GetCallerIdentityResponse model) {
            super(model);
            this.userId(model.userId);
            this.account(model.account);
            this.arn(model.arn);
        }

        public final String getUserId() {
            return this.userId;
        }

        public final void setUserId(String userId) {
            this.userId = userId;
        }

        @Override
        public final Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public final String getAccount() {
            return this.account;
        }

        public final void setAccount(String account) {
            this.account = account;
        }

        @Override
        public final Builder account(String account) {
            this.account = account;
            return this;
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

        @Override
        public GetCallerIdentityResponse build() {
            return new GetCallerIdentityResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends StsResponse.Builder,
    SdkPojo,
    CopyableBuilder<Builder, GetCallerIdentityResponse> {
        public Builder userId(String var1);

        public Builder account(String var1);

        public Builder arn(String var1);
    }
}

