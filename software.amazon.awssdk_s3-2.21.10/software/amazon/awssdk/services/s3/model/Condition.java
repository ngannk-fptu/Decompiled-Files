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
package software.amazon.awssdk.services.s3.model;

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

public final class Condition
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, Condition> {
    private static final SdkField<String> HTTP_ERROR_CODE_RETURNED_EQUALS_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("HttpErrorCodeReturnedEquals").getter(Condition.getter(Condition::httpErrorCodeReturnedEquals)).setter(Condition.setter(Builder::httpErrorCodeReturnedEquals)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("HttpErrorCodeReturnedEquals").unmarshallLocationName("HttpErrorCodeReturnedEquals").build()}).build();
    private static final SdkField<String> KEY_PREFIX_EQUALS_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("KeyPrefixEquals").getter(Condition.getter(Condition::keyPrefixEquals)).setter(Condition.setter(Builder::keyPrefixEquals)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("KeyPrefixEquals").unmarshallLocationName("KeyPrefixEquals").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(HTTP_ERROR_CODE_RETURNED_EQUALS_FIELD, KEY_PREFIX_EQUALS_FIELD));
    private static final long serialVersionUID = 1L;
    private final String httpErrorCodeReturnedEquals;
    private final String keyPrefixEquals;

    private Condition(BuilderImpl builder) {
        this.httpErrorCodeReturnedEquals = builder.httpErrorCodeReturnedEquals;
        this.keyPrefixEquals = builder.keyPrefixEquals;
    }

    public final String httpErrorCodeReturnedEquals() {
        return this.httpErrorCodeReturnedEquals;
    }

    public final String keyPrefixEquals() {
        return this.keyPrefixEquals;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.httpErrorCodeReturnedEquals());
        hashCode = 31 * hashCode + Objects.hashCode(this.keyPrefixEquals());
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
        if (!(obj instanceof Condition)) {
            return false;
        }
        Condition other = (Condition)obj;
        return Objects.equals(this.httpErrorCodeReturnedEquals(), other.httpErrorCodeReturnedEquals()) && Objects.equals(this.keyPrefixEquals(), other.keyPrefixEquals());
    }

    public final String toString() {
        return ToString.builder((String)"Condition").add("HttpErrorCodeReturnedEquals", (Object)this.httpErrorCodeReturnedEquals()).add("KeyPrefixEquals", (Object)this.keyPrefixEquals()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "HttpErrorCodeReturnedEquals": {
                return Optional.ofNullable(clazz.cast(this.httpErrorCodeReturnedEquals()));
            }
            case "KeyPrefixEquals": {
                return Optional.ofNullable(clazz.cast(this.keyPrefixEquals()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<Condition, T> g) {
        return obj -> g.apply((Condition)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String httpErrorCodeReturnedEquals;
        private String keyPrefixEquals;

        private BuilderImpl() {
        }

        private BuilderImpl(Condition model) {
            this.httpErrorCodeReturnedEquals(model.httpErrorCodeReturnedEquals);
            this.keyPrefixEquals(model.keyPrefixEquals);
        }

        public final String getHttpErrorCodeReturnedEquals() {
            return this.httpErrorCodeReturnedEquals;
        }

        public final void setHttpErrorCodeReturnedEquals(String httpErrorCodeReturnedEquals) {
            this.httpErrorCodeReturnedEquals = httpErrorCodeReturnedEquals;
        }

        @Override
        public final Builder httpErrorCodeReturnedEquals(String httpErrorCodeReturnedEquals) {
            this.httpErrorCodeReturnedEquals = httpErrorCodeReturnedEquals;
            return this;
        }

        public final String getKeyPrefixEquals() {
            return this.keyPrefixEquals;
        }

        public final void setKeyPrefixEquals(String keyPrefixEquals) {
            this.keyPrefixEquals = keyPrefixEquals;
        }

        @Override
        public final Builder keyPrefixEquals(String keyPrefixEquals) {
            this.keyPrefixEquals = keyPrefixEquals;
            return this;
        }

        public Condition build() {
            return new Condition(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, Condition> {
        public Builder httpErrorCodeReturnedEquals(String var1);

        public Builder keyPrefixEquals(String var1);
    }
}

