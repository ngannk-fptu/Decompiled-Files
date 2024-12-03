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
import software.amazon.awssdk.services.s3.model.TargetGrant;
import software.amazon.awssdk.services.s3.model.TargetGrantsCopier;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class LoggingEnabled
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, LoggingEnabled> {
    private static final SdkField<String> TARGET_BUCKET_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("TargetBucket").getter(LoggingEnabled.getter(LoggingEnabled::targetBucket)).setter(LoggingEnabled.setter(Builder::targetBucket)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("TargetBucket").unmarshallLocationName("TargetBucket").build(), RequiredTrait.create()}).build();
    private static final SdkField<List<TargetGrant>> TARGET_GRANTS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("TargetGrants").getter(LoggingEnabled.getter(LoggingEnabled::targetGrants)).setter(LoggingEnabled.setter(Builder::targetGrants)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("TargetGrants").unmarshallLocationName("TargetGrants").build(), ListTrait.builder().memberLocationName("Grant").memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(TargetGrant::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Grant").unmarshallLocationName("Grant").build()}).build()).build()}).build();
    private static final SdkField<String> TARGET_PREFIX_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("TargetPrefix").getter(LoggingEnabled.getter(LoggingEnabled::targetPrefix)).setter(LoggingEnabled.setter(Builder::targetPrefix)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("TargetPrefix").unmarshallLocationName("TargetPrefix").build(), RequiredTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(TARGET_BUCKET_FIELD, TARGET_GRANTS_FIELD, TARGET_PREFIX_FIELD));
    private static final long serialVersionUID = 1L;
    private final String targetBucket;
    private final List<TargetGrant> targetGrants;
    private final String targetPrefix;

    private LoggingEnabled(BuilderImpl builder) {
        this.targetBucket = builder.targetBucket;
        this.targetGrants = builder.targetGrants;
        this.targetPrefix = builder.targetPrefix;
    }

    public final String targetBucket() {
        return this.targetBucket;
    }

    public final boolean hasTargetGrants() {
        return this.targetGrants != null && !(this.targetGrants instanceof SdkAutoConstructList);
    }

    public final List<TargetGrant> targetGrants() {
        return this.targetGrants;
    }

    public final String targetPrefix() {
        return this.targetPrefix;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.targetBucket());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasTargetGrants() ? this.targetGrants() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.targetPrefix());
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
        if (!(obj instanceof LoggingEnabled)) {
            return false;
        }
        LoggingEnabled other = (LoggingEnabled)obj;
        return Objects.equals(this.targetBucket(), other.targetBucket()) && this.hasTargetGrants() == other.hasTargetGrants() && Objects.equals(this.targetGrants(), other.targetGrants()) && Objects.equals(this.targetPrefix(), other.targetPrefix());
    }

    public final String toString() {
        return ToString.builder((String)"LoggingEnabled").add("TargetBucket", (Object)this.targetBucket()).add("TargetGrants", this.hasTargetGrants() ? this.targetGrants() : null).add("TargetPrefix", (Object)this.targetPrefix()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "TargetBucket": {
                return Optional.ofNullable(clazz.cast(this.targetBucket()));
            }
            case "TargetGrants": {
                return Optional.ofNullable(clazz.cast(this.targetGrants()));
            }
            case "TargetPrefix": {
                return Optional.ofNullable(clazz.cast(this.targetPrefix()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<LoggingEnabled, T> g) {
        return obj -> g.apply((LoggingEnabled)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String targetBucket;
        private List<TargetGrant> targetGrants = DefaultSdkAutoConstructList.getInstance();
        private String targetPrefix;

        private BuilderImpl() {
        }

        private BuilderImpl(LoggingEnabled model) {
            this.targetBucket(model.targetBucket);
            this.targetGrants(model.targetGrants);
            this.targetPrefix(model.targetPrefix);
        }

        public final String getTargetBucket() {
            return this.targetBucket;
        }

        public final void setTargetBucket(String targetBucket) {
            this.targetBucket = targetBucket;
        }

        @Override
        public final Builder targetBucket(String targetBucket) {
            this.targetBucket = targetBucket;
            return this;
        }

        public final List<TargetGrant.Builder> getTargetGrants() {
            List<TargetGrant.Builder> result = TargetGrantsCopier.copyToBuilder(this.targetGrants);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setTargetGrants(Collection<TargetGrant.BuilderImpl> targetGrants) {
            this.targetGrants = TargetGrantsCopier.copyFromBuilder(targetGrants);
        }

        @Override
        public final Builder targetGrants(Collection<TargetGrant> targetGrants) {
            this.targetGrants = TargetGrantsCopier.copy(targetGrants);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder targetGrants(TargetGrant ... targetGrants) {
            this.targetGrants(Arrays.asList(targetGrants));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder targetGrants(Consumer<TargetGrant.Builder> ... targetGrants) {
            this.targetGrants(Stream.of(targetGrants).map(c -> (TargetGrant)((TargetGrant.Builder)TargetGrant.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        public final String getTargetPrefix() {
            return this.targetPrefix;
        }

        public final void setTargetPrefix(String targetPrefix) {
            this.targetPrefix = targetPrefix;
        }

        @Override
        public final Builder targetPrefix(String targetPrefix) {
            this.targetPrefix = targetPrefix;
            return this;
        }

        public LoggingEnabled build() {
            return new LoggingEnabled(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, LoggingEnabled> {
        public Builder targetBucket(String var1);

        public Builder targetGrants(Collection<TargetGrant> var1);

        public Builder targetGrants(TargetGrant ... var1);

        public Builder targetGrants(Consumer<TargetGrant.Builder> ... var1);

        public Builder targetPrefix(String var1);
    }
}

