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

import java.io.Serializable;
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
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class FederatedUser
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, FederatedUser> {
    private static final SdkField<String> FEDERATED_USER_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("FederatedUserId").getter(FederatedUser.getter(FederatedUser::federatedUserId)).setter(FederatedUser.setter(Builder::federatedUserId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("FederatedUserId").build()}).build();
    private static final SdkField<String> ARN_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Arn").getter(FederatedUser.getter(FederatedUser::arn)).setter(FederatedUser.setter(Builder::arn)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Arn").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(FEDERATED_USER_ID_FIELD, ARN_FIELD));
    private static final long serialVersionUID = 1L;
    private final String federatedUserId;
    private final String arn;

    private FederatedUser(BuilderImpl builder) {
        this.federatedUserId = builder.federatedUserId;
        this.arn = builder.arn;
    }

    public final String federatedUserId() {
        return this.federatedUserId;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.federatedUserId());
        hashCode = 31 * hashCode + Objects.hashCode(this.arn());
        return hashCode;
    }

    public final boolean equals(Object obj) {
        return this.equalsBySdkFields(obj);
    }

    public final boolean equalsBySdkFields(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof FederatedUser)) {
            return false;
        }
        FederatedUser other = (FederatedUser)obj;
        return Objects.equals(this.federatedUserId(), other.federatedUserId()) && Objects.equals(this.arn(), other.arn());
    }

    public final String toString() {
        return ToString.builder((String)"FederatedUser").add("FederatedUserId", (Object)this.federatedUserId()).add("Arn", (Object)this.arn()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "FederatedUserId": {
                return Optional.ofNullable(clazz.cast(this.federatedUserId()));
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

    private static <T> Function<Object, T> getter(Function<FederatedUser, T> g) {
        return obj -> g.apply((FederatedUser)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String federatedUserId;
        private String arn;

        private BuilderImpl() {
        }

        private BuilderImpl(FederatedUser model) {
            this.federatedUserId(model.federatedUserId);
            this.arn(model.arn);
        }

        public final String getFederatedUserId() {
            return this.federatedUserId;
        }

        public final void setFederatedUserId(String federatedUserId) {
            this.federatedUserId = federatedUserId;
        }

        @Override
        public final Builder federatedUserId(String federatedUserId) {
            this.federatedUserId = federatedUserId;
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

        public FederatedUser build() {
            return new FederatedUser(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, FederatedUser> {
        public Builder federatedUserId(String var1);

        public Builder arn(String var1);
    }
}

