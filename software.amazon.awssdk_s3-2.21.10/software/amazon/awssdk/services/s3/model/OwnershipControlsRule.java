/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.RequiredTrait
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
import software.amazon.awssdk.core.traits.RequiredTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.ObjectOwnership;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class OwnershipControlsRule
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, OwnershipControlsRule> {
    private static final SdkField<String> OBJECT_OWNERSHIP_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ObjectOwnership").getter(OwnershipControlsRule.getter(OwnershipControlsRule::objectOwnershipAsString)).setter(OwnershipControlsRule.setter(Builder::objectOwnership)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ObjectOwnership").unmarshallLocationName("ObjectOwnership").build(), RequiredTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(OBJECT_OWNERSHIP_FIELD));
    private static final long serialVersionUID = 1L;
    private final String objectOwnership;

    private OwnershipControlsRule(BuilderImpl builder) {
        this.objectOwnership = builder.objectOwnership;
    }

    public final ObjectOwnership objectOwnership() {
        return ObjectOwnership.fromValue(this.objectOwnership);
    }

    public final String objectOwnershipAsString() {
        return this.objectOwnership;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.objectOwnershipAsString());
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
        if (!(obj instanceof OwnershipControlsRule)) {
            return false;
        }
        OwnershipControlsRule other = (OwnershipControlsRule)obj;
        return Objects.equals(this.objectOwnershipAsString(), other.objectOwnershipAsString());
    }

    public final String toString() {
        return ToString.builder((String)"OwnershipControlsRule").add("ObjectOwnership", (Object)this.objectOwnershipAsString()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "ObjectOwnership": {
                return Optional.ofNullable(clazz.cast(this.objectOwnershipAsString()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<OwnershipControlsRule, T> g) {
        return obj -> g.apply((OwnershipControlsRule)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String objectOwnership;

        private BuilderImpl() {
        }

        private BuilderImpl(OwnershipControlsRule model) {
            this.objectOwnership(model.objectOwnership);
        }

        public final String getObjectOwnership() {
            return this.objectOwnership;
        }

        public final void setObjectOwnership(String objectOwnership) {
            this.objectOwnership = objectOwnership;
        }

        @Override
        public final Builder objectOwnership(String objectOwnership) {
            this.objectOwnership = objectOwnership;
            return this;
        }

        @Override
        public final Builder objectOwnership(ObjectOwnership objectOwnership) {
            this.objectOwnership(objectOwnership == null ? null : objectOwnership.toString());
            return this;
        }

        public OwnershipControlsRule build() {
            return new OwnershipControlsRule(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, OwnershipControlsRule> {
        public Builder objectOwnership(String var1);

        public Builder objectOwnership(ObjectOwnership var1);
    }
}

