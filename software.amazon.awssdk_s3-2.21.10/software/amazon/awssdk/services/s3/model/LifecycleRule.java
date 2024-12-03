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
 *  software.amazon.awssdk.core.traits.RequiredTrait
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
import software.amazon.awssdk.core.traits.RequiredTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.services.s3.model.AbortIncompleteMultipartUpload;
import software.amazon.awssdk.services.s3.model.ExpirationStatus;
import software.amazon.awssdk.services.s3.model.LifecycleExpiration;
import software.amazon.awssdk.services.s3.model.LifecycleRuleFilter;
import software.amazon.awssdk.services.s3.model.NoncurrentVersionExpiration;
import software.amazon.awssdk.services.s3.model.NoncurrentVersionTransition;
import software.amazon.awssdk.services.s3.model.NoncurrentVersionTransitionListCopier;
import software.amazon.awssdk.services.s3.model.Transition;
import software.amazon.awssdk.services.s3.model.TransitionListCopier;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class LifecycleRule
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, LifecycleRule> {
    private static final SdkField<LifecycleExpiration> EXPIRATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Expiration").getter(LifecycleRule.getter(LifecycleRule::expiration)).setter(LifecycleRule.setter(Builder::expiration)).constructor(LifecycleExpiration::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Expiration").unmarshallLocationName("Expiration").build()}).build();
    private static final SdkField<String> ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ID").getter(LifecycleRule.getter(LifecycleRule::id)).setter(LifecycleRule.setter(Builder::id)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ID").unmarshallLocationName("ID").build()}).build();
    private static final SdkField<String> PREFIX_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Prefix").getter(LifecycleRule.getter(LifecycleRule::prefix)).setter(LifecycleRule.setter(Builder::prefix)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Prefix").unmarshallLocationName("Prefix").build()}).build();
    private static final SdkField<LifecycleRuleFilter> FILTER_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Filter").getter(LifecycleRule.getter(LifecycleRule::filter)).setter(LifecycleRule.setter(Builder::filter)).constructor(LifecycleRuleFilter::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Filter").unmarshallLocationName("Filter").build()}).build();
    private static final SdkField<String> STATUS_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Status").getter(LifecycleRule.getter(LifecycleRule::statusAsString)).setter(LifecycleRule.setter(Builder::status)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Status").unmarshallLocationName("Status").build(), RequiredTrait.create()}).build();
    private static final SdkField<List<Transition>> TRANSITIONS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("Transitions").getter(LifecycleRule.getter(LifecycleRule::transitions)).setter(LifecycleRule.setter(Builder::transitions)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Transition").unmarshallLocationName("Transition").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(Transition::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build()}).build();
    private static final SdkField<List<NoncurrentVersionTransition>> NONCURRENT_VERSION_TRANSITIONS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("NoncurrentVersionTransitions").getter(LifecycleRule.getter(LifecycleRule::noncurrentVersionTransitions)).setter(LifecycleRule.setter(Builder::noncurrentVersionTransitions)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("NoncurrentVersionTransition").unmarshallLocationName("NoncurrentVersionTransition").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(NoncurrentVersionTransition::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build()}).build();
    private static final SdkField<NoncurrentVersionExpiration> NONCURRENT_VERSION_EXPIRATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("NoncurrentVersionExpiration").getter(LifecycleRule.getter(LifecycleRule::noncurrentVersionExpiration)).setter(LifecycleRule.setter(Builder::noncurrentVersionExpiration)).constructor(NoncurrentVersionExpiration::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("NoncurrentVersionExpiration").unmarshallLocationName("NoncurrentVersionExpiration").build()}).build();
    private static final SdkField<AbortIncompleteMultipartUpload> ABORT_INCOMPLETE_MULTIPART_UPLOAD_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("AbortIncompleteMultipartUpload").getter(LifecycleRule.getter(LifecycleRule::abortIncompleteMultipartUpload)).setter(LifecycleRule.setter(Builder::abortIncompleteMultipartUpload)).constructor(AbortIncompleteMultipartUpload::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("AbortIncompleteMultipartUpload").unmarshallLocationName("AbortIncompleteMultipartUpload").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(EXPIRATION_FIELD, ID_FIELD, PREFIX_FIELD, FILTER_FIELD, STATUS_FIELD, TRANSITIONS_FIELD, NONCURRENT_VERSION_TRANSITIONS_FIELD, NONCURRENT_VERSION_EXPIRATION_FIELD, ABORT_INCOMPLETE_MULTIPART_UPLOAD_FIELD));
    private static final long serialVersionUID = 1L;
    private final LifecycleExpiration expiration;
    private final String id;
    private final String prefix;
    private final LifecycleRuleFilter filter;
    private final String status;
    private final List<Transition> transitions;
    private final List<NoncurrentVersionTransition> noncurrentVersionTransitions;
    private final NoncurrentVersionExpiration noncurrentVersionExpiration;
    private final AbortIncompleteMultipartUpload abortIncompleteMultipartUpload;

    private LifecycleRule(BuilderImpl builder) {
        this.expiration = builder.expiration;
        this.id = builder.id;
        this.prefix = builder.prefix;
        this.filter = builder.filter;
        this.status = builder.status;
        this.transitions = builder.transitions;
        this.noncurrentVersionTransitions = builder.noncurrentVersionTransitions;
        this.noncurrentVersionExpiration = builder.noncurrentVersionExpiration;
        this.abortIncompleteMultipartUpload = builder.abortIncompleteMultipartUpload;
    }

    public final LifecycleExpiration expiration() {
        return this.expiration;
    }

    public final String id() {
        return this.id;
    }

    @Deprecated
    public final String prefix() {
        return this.prefix;
    }

    public final LifecycleRuleFilter filter() {
        return this.filter;
    }

    public final ExpirationStatus status() {
        return ExpirationStatus.fromValue(this.status);
    }

    public final String statusAsString() {
        return this.status;
    }

    public final boolean hasTransitions() {
        return this.transitions != null && !(this.transitions instanceof SdkAutoConstructList);
    }

    public final List<Transition> transitions() {
        return this.transitions;
    }

    public final boolean hasNoncurrentVersionTransitions() {
        return this.noncurrentVersionTransitions != null && !(this.noncurrentVersionTransitions instanceof SdkAutoConstructList);
    }

    public final List<NoncurrentVersionTransition> noncurrentVersionTransitions() {
        return this.noncurrentVersionTransitions;
    }

    public final NoncurrentVersionExpiration noncurrentVersionExpiration() {
        return this.noncurrentVersionExpiration;
    }

    public final AbortIncompleteMultipartUpload abortIncompleteMultipartUpload() {
        return this.abortIncompleteMultipartUpload;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.expiration());
        hashCode = 31 * hashCode + Objects.hashCode(this.id());
        hashCode = 31 * hashCode + Objects.hashCode(this.prefix());
        hashCode = 31 * hashCode + Objects.hashCode(this.filter());
        hashCode = 31 * hashCode + Objects.hashCode(this.statusAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasTransitions() ? this.transitions() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.hasNoncurrentVersionTransitions() ? this.noncurrentVersionTransitions() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.noncurrentVersionExpiration());
        hashCode = 31 * hashCode + Objects.hashCode(this.abortIncompleteMultipartUpload());
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
        if (!(obj instanceof LifecycleRule)) {
            return false;
        }
        LifecycleRule other = (LifecycleRule)obj;
        return Objects.equals(this.expiration(), other.expiration()) && Objects.equals(this.id(), other.id()) && Objects.equals(this.prefix(), other.prefix()) && Objects.equals(this.filter(), other.filter()) && Objects.equals(this.statusAsString(), other.statusAsString()) && this.hasTransitions() == other.hasTransitions() && Objects.equals(this.transitions(), other.transitions()) && this.hasNoncurrentVersionTransitions() == other.hasNoncurrentVersionTransitions() && Objects.equals(this.noncurrentVersionTransitions(), other.noncurrentVersionTransitions()) && Objects.equals(this.noncurrentVersionExpiration(), other.noncurrentVersionExpiration()) && Objects.equals(this.abortIncompleteMultipartUpload(), other.abortIncompleteMultipartUpload());
    }

    public final String toString() {
        return ToString.builder((String)"LifecycleRule").add("Expiration", (Object)this.expiration()).add("ID", (Object)this.id()).add("Prefix", (Object)this.prefix()).add("Filter", (Object)this.filter()).add("Status", (Object)this.statusAsString()).add("Transitions", this.hasTransitions() ? this.transitions() : null).add("NoncurrentVersionTransitions", this.hasNoncurrentVersionTransitions() ? this.noncurrentVersionTransitions() : null).add("NoncurrentVersionExpiration", (Object)this.noncurrentVersionExpiration()).add("AbortIncompleteMultipartUpload", (Object)this.abortIncompleteMultipartUpload()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Expiration": {
                return Optional.ofNullable(clazz.cast(this.expiration()));
            }
            case "ID": {
                return Optional.ofNullable(clazz.cast(this.id()));
            }
            case "Prefix": {
                return Optional.ofNullable(clazz.cast(this.prefix()));
            }
            case "Filter": {
                return Optional.ofNullable(clazz.cast(this.filter()));
            }
            case "Status": {
                return Optional.ofNullable(clazz.cast(this.statusAsString()));
            }
            case "Transitions": {
                return Optional.ofNullable(clazz.cast(this.transitions()));
            }
            case "NoncurrentVersionTransitions": {
                return Optional.ofNullable(clazz.cast(this.noncurrentVersionTransitions()));
            }
            case "NoncurrentVersionExpiration": {
                return Optional.ofNullable(clazz.cast(this.noncurrentVersionExpiration()));
            }
            case "AbortIncompleteMultipartUpload": {
                return Optional.ofNullable(clazz.cast(this.abortIncompleteMultipartUpload()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<LifecycleRule, T> g) {
        return obj -> g.apply((LifecycleRule)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private LifecycleExpiration expiration;
        private String id;
        private String prefix;
        private LifecycleRuleFilter filter;
        private String status;
        private List<Transition> transitions = DefaultSdkAutoConstructList.getInstance();
        private List<NoncurrentVersionTransition> noncurrentVersionTransitions = DefaultSdkAutoConstructList.getInstance();
        private NoncurrentVersionExpiration noncurrentVersionExpiration;
        private AbortIncompleteMultipartUpload abortIncompleteMultipartUpload;

        private BuilderImpl() {
        }

        private BuilderImpl(LifecycleRule model) {
            this.expiration(model.expiration);
            this.id(model.id);
            this.prefix(model.prefix);
            this.filter(model.filter);
            this.status(model.status);
            this.transitions(model.transitions);
            this.noncurrentVersionTransitions(model.noncurrentVersionTransitions);
            this.noncurrentVersionExpiration(model.noncurrentVersionExpiration);
            this.abortIncompleteMultipartUpload(model.abortIncompleteMultipartUpload);
        }

        public final LifecycleExpiration.Builder getExpiration() {
            return this.expiration != null ? this.expiration.toBuilder() : null;
        }

        public final void setExpiration(LifecycleExpiration.BuilderImpl expiration) {
            this.expiration = expiration != null ? expiration.build() : null;
        }

        @Override
        public final Builder expiration(LifecycleExpiration expiration) {
            this.expiration = expiration;
            return this;
        }

        public final String getId() {
            return this.id;
        }

        public final void setId(String id) {
            this.id = id;
        }

        @Override
        public final Builder id(String id) {
            this.id = id;
            return this;
        }

        @Deprecated
        public final String getPrefix() {
            return this.prefix;
        }

        @Deprecated
        public final void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        @Override
        @Deprecated
        public final Builder prefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public final LifecycleRuleFilter.Builder getFilter() {
            return this.filter != null ? this.filter.toBuilder() : null;
        }

        public final void setFilter(LifecycleRuleFilter.BuilderImpl filter) {
            this.filter = filter != null ? filter.build() : null;
        }

        @Override
        public final Builder filter(LifecycleRuleFilter filter) {
            this.filter = filter;
            return this;
        }

        public final String getStatus() {
            return this.status;
        }

        public final void setStatus(String status) {
            this.status = status;
        }

        @Override
        public final Builder status(String status) {
            this.status = status;
            return this;
        }

        @Override
        public final Builder status(ExpirationStatus status) {
            this.status(status == null ? null : status.toString());
            return this;
        }

        public final List<Transition.Builder> getTransitions() {
            List<Transition.Builder> result = TransitionListCopier.copyToBuilder(this.transitions);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setTransitions(Collection<Transition.BuilderImpl> transitions) {
            this.transitions = TransitionListCopier.copyFromBuilder(transitions);
        }

        @Override
        public final Builder transitions(Collection<Transition> transitions) {
            this.transitions = TransitionListCopier.copy(transitions);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder transitions(Transition ... transitions) {
            this.transitions(Arrays.asList(transitions));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder transitions(Consumer<Transition.Builder> ... transitions) {
            this.transitions(Stream.of(transitions).map(c -> (Transition)((Transition.Builder)Transition.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        public final List<NoncurrentVersionTransition.Builder> getNoncurrentVersionTransitions() {
            List<NoncurrentVersionTransition.Builder> result = NoncurrentVersionTransitionListCopier.copyToBuilder(this.noncurrentVersionTransitions);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setNoncurrentVersionTransitions(Collection<NoncurrentVersionTransition.BuilderImpl> noncurrentVersionTransitions) {
            this.noncurrentVersionTransitions = NoncurrentVersionTransitionListCopier.copyFromBuilder(noncurrentVersionTransitions);
        }

        @Override
        public final Builder noncurrentVersionTransitions(Collection<NoncurrentVersionTransition> noncurrentVersionTransitions) {
            this.noncurrentVersionTransitions = NoncurrentVersionTransitionListCopier.copy(noncurrentVersionTransitions);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder noncurrentVersionTransitions(NoncurrentVersionTransition ... noncurrentVersionTransitions) {
            this.noncurrentVersionTransitions(Arrays.asList(noncurrentVersionTransitions));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder noncurrentVersionTransitions(Consumer<NoncurrentVersionTransition.Builder> ... noncurrentVersionTransitions) {
            this.noncurrentVersionTransitions(Stream.of(noncurrentVersionTransitions).map(c -> (NoncurrentVersionTransition)((NoncurrentVersionTransition.Builder)NoncurrentVersionTransition.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        public final NoncurrentVersionExpiration.Builder getNoncurrentVersionExpiration() {
            return this.noncurrentVersionExpiration != null ? this.noncurrentVersionExpiration.toBuilder() : null;
        }

        public final void setNoncurrentVersionExpiration(NoncurrentVersionExpiration.BuilderImpl noncurrentVersionExpiration) {
            this.noncurrentVersionExpiration = noncurrentVersionExpiration != null ? noncurrentVersionExpiration.build() : null;
        }

        @Override
        public final Builder noncurrentVersionExpiration(NoncurrentVersionExpiration noncurrentVersionExpiration) {
            this.noncurrentVersionExpiration = noncurrentVersionExpiration;
            return this;
        }

        public final AbortIncompleteMultipartUpload.Builder getAbortIncompleteMultipartUpload() {
            return this.abortIncompleteMultipartUpload != null ? this.abortIncompleteMultipartUpload.toBuilder() : null;
        }

        public final void setAbortIncompleteMultipartUpload(AbortIncompleteMultipartUpload.BuilderImpl abortIncompleteMultipartUpload) {
            this.abortIncompleteMultipartUpload = abortIncompleteMultipartUpload != null ? abortIncompleteMultipartUpload.build() : null;
        }

        @Override
        public final Builder abortIncompleteMultipartUpload(AbortIncompleteMultipartUpload abortIncompleteMultipartUpload) {
            this.abortIncompleteMultipartUpload = abortIncompleteMultipartUpload;
            return this;
        }

        public LifecycleRule build() {
            return new LifecycleRule(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, LifecycleRule> {
        public Builder expiration(LifecycleExpiration var1);

        default public Builder expiration(Consumer<LifecycleExpiration.Builder> expiration) {
            return this.expiration((LifecycleExpiration)((LifecycleExpiration.Builder)LifecycleExpiration.builder().applyMutation(expiration)).build());
        }

        public Builder id(String var1);

        @Deprecated
        public Builder prefix(String var1);

        public Builder filter(LifecycleRuleFilter var1);

        default public Builder filter(Consumer<LifecycleRuleFilter.Builder> filter) {
            return this.filter((LifecycleRuleFilter)((LifecycleRuleFilter.Builder)LifecycleRuleFilter.builder().applyMutation(filter)).build());
        }

        public Builder status(String var1);

        public Builder status(ExpirationStatus var1);

        public Builder transitions(Collection<Transition> var1);

        public Builder transitions(Transition ... var1);

        public Builder transitions(Consumer<Transition.Builder> ... var1);

        public Builder noncurrentVersionTransitions(Collection<NoncurrentVersionTransition> var1);

        public Builder noncurrentVersionTransitions(NoncurrentVersionTransition ... var1);

        public Builder noncurrentVersionTransitions(Consumer<NoncurrentVersionTransition.Builder> ... var1);

        public Builder noncurrentVersionExpiration(NoncurrentVersionExpiration var1);

        default public Builder noncurrentVersionExpiration(Consumer<NoncurrentVersionExpiration.Builder> noncurrentVersionExpiration) {
            return this.noncurrentVersionExpiration((NoncurrentVersionExpiration)((NoncurrentVersionExpiration.Builder)NoncurrentVersionExpiration.builder().applyMutation(noncurrentVersionExpiration)).build());
        }

        public Builder abortIncompleteMultipartUpload(AbortIncompleteMultipartUpload var1);

        default public Builder abortIncompleteMultipartUpload(Consumer<AbortIncompleteMultipartUpload.Builder> abortIncompleteMultipartUpload) {
            return this.abortIncompleteMultipartUpload((AbortIncompleteMultipartUpload)((AbortIncompleteMultipartUpload.Builder)AbortIncompleteMultipartUpload.builder().applyMutation(abortIncompleteMultipartUpload)).build());
        }
    }
}

