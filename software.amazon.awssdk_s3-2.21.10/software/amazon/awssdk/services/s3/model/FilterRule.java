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
import software.amazon.awssdk.services.s3.model.FilterRuleName;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class FilterRule
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, FilterRule> {
    private static final SdkField<String> NAME_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Name").getter(FilterRule.getter(FilterRule::nameAsString)).setter(FilterRule.setter(Builder::name)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Name").unmarshallLocationName("Name").build()}).build();
    private static final SdkField<String> VALUE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Value").getter(FilterRule.getter(FilterRule::value)).setter(FilterRule.setter(Builder::value)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Value").unmarshallLocationName("Value").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(NAME_FIELD, VALUE_FIELD));
    private static final long serialVersionUID = 1L;
    private final String name;
    private final String value;

    private FilterRule(BuilderImpl builder) {
        this.name = builder.name;
        this.value = builder.value;
    }

    public final FilterRuleName name() {
        return FilterRuleName.fromValue(this.name);
    }

    public final String nameAsString() {
        return this.name;
    }

    public final String value() {
        return this.value;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.nameAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.value());
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
        if (!(obj instanceof FilterRule)) {
            return false;
        }
        FilterRule other = (FilterRule)obj;
        return Objects.equals(this.nameAsString(), other.nameAsString()) && Objects.equals(this.value(), other.value());
    }

    public final String toString() {
        return ToString.builder((String)"FilterRule").add("Name", (Object)this.nameAsString()).add("Value", (Object)this.value()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Name": {
                return Optional.ofNullable(clazz.cast(this.nameAsString()));
            }
            case "Value": {
                return Optional.ofNullable(clazz.cast(this.value()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<FilterRule, T> g) {
        return obj -> g.apply((FilterRule)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String name;
        private String value;

        private BuilderImpl() {
        }

        private BuilderImpl(FilterRule model) {
            this.name(model.name);
            this.value(model.value);
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
        public final Builder name(FilterRuleName name) {
            this.name(name == null ? null : name.toString());
            return this;
        }

        public final String getValue() {
            return this.value;
        }

        public final void setValue(String value) {
            this.value = value;
        }

        @Override
        public final Builder value(String value) {
            this.value = value;
            return this;
        }

        public FilterRule build() {
            return new FilterRule(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, FilterRule> {
        public Builder name(String var1);

        public Builder name(FilterRuleName var1);

        public Builder value(String var1);
    }
}

