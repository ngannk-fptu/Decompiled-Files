/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.ListTrait
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.Trait
 *  software.amazon.awssdk.core.util.DefaultSdkAutoConstructList
 *  software.amazon.awssdk.core.util.SdkAutoConstructList
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.secretsmanager.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
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
import software.amazon.awssdk.core.traits.ListTrait;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.services.secretsmanager.model.FilterNameStringType;
import software.amazon.awssdk.services.secretsmanager.model.FilterValuesStringListCopier;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class Filter
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, Filter> {
    private static final SdkField<String> KEY_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Key").getter(Filter.getter(Filter::keyAsString)).setter(Filter.setter(Builder::key)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Key").build()}).build();
    private static final SdkField<List<String>> VALUES_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("Values").getter(Filter.getter(Filter::values)).setter(Filter.setter(Builder::values)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Values").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.STRING).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").build()}).build()).build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(KEY_FIELD, VALUES_FIELD));
    private static final long serialVersionUID = 1L;
    private final String key;
    private final List<String> values;

    private Filter(BuilderImpl builder) {
        this.key = builder.key;
        this.values = builder.values;
    }

    public final FilterNameStringType key() {
        return FilterNameStringType.fromValue(this.key);
    }

    public final String keyAsString() {
        return this.key;
    }

    public final boolean hasValues() {
        return this.values != null && !(this.values instanceof SdkAutoConstructList);
    }

    public final List<String> values() {
        return this.values;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.keyAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasValues() ? this.values() : null);
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
        if (!(obj instanceof Filter)) {
            return false;
        }
        Filter other = (Filter)obj;
        return Objects.equals(this.keyAsString(), other.keyAsString()) && this.hasValues() == other.hasValues() && Objects.equals(this.values(), other.values());
    }

    public final String toString() {
        return ToString.builder((String)"Filter").add("Key", (Object)this.keyAsString()).add("Values", this.hasValues() ? this.values() : null).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Key": {
                return Optional.ofNullable(clazz.cast(this.keyAsString()));
            }
            case "Values": {
                return Optional.ofNullable(clazz.cast(this.values()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<Filter, T> g) {
        return obj -> g.apply((Filter)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String key;
        private List<String> values = DefaultSdkAutoConstructList.getInstance();

        private BuilderImpl() {
        }

        private BuilderImpl(Filter model) {
            this.key(model.key);
            this.values(model.values);
        }

        public final String getKey() {
            return this.key;
        }

        public final void setKey(String key) {
            this.key = key;
        }

        @Override
        public final Builder key(String key) {
            this.key = key;
            return this;
        }

        @Override
        public final Builder key(FilterNameStringType key) {
            this.key(key == null ? null : key.toString());
            return this;
        }

        public final Collection<String> getValues() {
            if (this.values instanceof SdkAutoConstructList) {
                return null;
            }
            return this.values;
        }

        public final void setValues(Collection<String> values) {
            this.values = FilterValuesStringListCopier.copy(values);
        }

        @Override
        public final Builder values(Collection<String> values) {
            this.values = FilterValuesStringListCopier.copy(values);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder values(String ... values) {
            this.values(Arrays.asList(values));
            return this;
        }

        public Filter build() {
            return new Filter(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, Filter> {
        public Builder key(String var1);

        public Builder key(FilterNameStringType var1);

        public Builder values(Collection<String> var1);

        public Builder values(String ... var1);
    }
}

