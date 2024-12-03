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

public final class GetAccessKeyInfoResponse
extends StsResponse
implements ToCopyableBuilder<Builder, GetAccessKeyInfoResponse> {
    private static final SdkField<String> ACCOUNT_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Account").getter(GetAccessKeyInfoResponse.getter(GetAccessKeyInfoResponse::account)).setter(GetAccessKeyInfoResponse.setter(Builder::account)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Account").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(ACCOUNT_FIELD));
    private final String account;

    private GetAccessKeyInfoResponse(BuilderImpl builder) {
        super(builder);
        this.account = builder.account;
    }

    public final String account() {
        return this.account;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.account());
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
        if (!(obj instanceof GetAccessKeyInfoResponse)) {
            return false;
        }
        GetAccessKeyInfoResponse other = (GetAccessKeyInfoResponse)((Object)obj);
        return Objects.equals(this.account(), other.account());
    }

    public final String toString() {
        return ToString.builder((String)"GetAccessKeyInfoResponse").add("Account", (Object)this.account()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Account": {
                return Optional.ofNullable(clazz.cast(this.account()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<GetAccessKeyInfoResponse, T> g) {
        return obj -> g.apply((GetAccessKeyInfoResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends StsResponse.BuilderImpl
    implements Builder {
        private String account;

        private BuilderImpl() {
        }

        private BuilderImpl(GetAccessKeyInfoResponse model) {
            super(model);
            this.account(model.account);
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

        @Override
        public GetAccessKeyInfoResponse build() {
            return new GetAccessKeyInfoResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends StsResponse.Builder,
    SdkPojo,
    CopyableBuilder<Builder, GetAccessKeyInfoResponse> {
        public Builder account(String var1);
    }
}

