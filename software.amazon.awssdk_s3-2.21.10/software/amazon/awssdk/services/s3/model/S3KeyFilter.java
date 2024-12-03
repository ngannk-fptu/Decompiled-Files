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
package software.amazon.awssdk.services.s3.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.ListTrait;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.services.s3.model.FilterRule;
import software.amazon.awssdk.services.s3.model.FilterRuleListCopier;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class S3KeyFilter
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, S3KeyFilter> {
    private static final SdkField<List<FilterRule>> FILTER_RULES_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("FilterRules").getter(S3KeyFilter.getter(S3KeyFilter::filterRules)).setter(S3KeyFilter.setter(Builder::filterRules)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("FilterRule").unmarshallLocationName("FilterRule").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(FilterRule::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(FILTER_RULES_FIELD));
    private static final long serialVersionUID = 1L;
    private final List<FilterRule> filterRules;

    private S3KeyFilter(BuilderImpl builder) {
        this.filterRules = builder.filterRules;
    }

    public final boolean hasFilterRules() {
        return this.filterRules != null && !(this.filterRules instanceof SdkAutoConstructList);
    }

    public final List<FilterRule> filterRules() {
        return this.filterRules;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.hasFilterRules() ? this.filterRules() : null);
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
        if (!(obj instanceof S3KeyFilter)) {
            return false;
        }
        S3KeyFilter other = (S3KeyFilter)obj;
        return this.hasFilterRules() == other.hasFilterRules() && Objects.equals(this.filterRules(), other.filterRules());
    }

    public final String toString() {
        return ToString.builder((String)"S3KeyFilter").add("FilterRules", this.hasFilterRules() ? this.filterRules() : null).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "FilterRules": {
                return Optional.ofNullable(clazz.cast(this.filterRules()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<S3KeyFilter, T> g) {
        return obj -> g.apply((S3KeyFilter)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private List<FilterRule> filterRules = DefaultSdkAutoConstructList.getInstance();

        private BuilderImpl() {
        }

        private BuilderImpl(S3KeyFilter model) {
            this.filterRules(model.filterRules);
        }

        public final List<FilterRule.Builder> getFilterRules() {
            List<FilterRule.Builder> result = FilterRuleListCopier.copyToBuilder(this.filterRules);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setFilterRules(Collection<FilterRule.BuilderImpl> filterRules) {
            this.filterRules = FilterRuleListCopier.copyFromBuilder(filterRules);
        }

        @Override
        public final Builder filterRules(Collection<FilterRule> filterRules) {
            this.filterRules = FilterRuleListCopier.copy(filterRules);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder filterRules(FilterRule ... filterRules) {
            this.filterRules(Arrays.asList(filterRules));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder filterRules(Consumer<FilterRule.Builder> ... filterRules) {
            this.filterRules(Stream.of(filterRules).map(c -> (FilterRule)((FilterRule.Builder)FilterRule.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        public S3KeyFilter build() {
            return new S3KeyFilter(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, S3KeyFilter> {
        public Builder filterRules(Collection<FilterRule> var1);

        public Builder filterRules(FilterRule ... var1);

        public Builder filterRules(Consumer<FilterRule.Builder> ... var1);
    }
}

