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
import software.amazon.awssdk.services.s3.model.CORSRule;
import software.amazon.awssdk.services.s3.model.CORSRulesCopier;
import software.amazon.awssdk.services.s3.model.S3Response;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class GetBucketCorsResponse
extends S3Response
implements ToCopyableBuilder<Builder, GetBucketCorsResponse> {
    private static final SdkField<List<CORSRule>> CORS_RULES_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("CORSRules").getter(GetBucketCorsResponse.getter(GetBucketCorsResponse::corsRules)).setter(GetBucketCorsResponse.setter(Builder::corsRules)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("CORSRule").unmarshallLocationName("CORSRule").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(CORSRule::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(CORS_RULES_FIELD));
    private final List<CORSRule> corsRules;

    private GetBucketCorsResponse(BuilderImpl builder) {
        super(builder);
        this.corsRules = builder.corsRules;
    }

    public final boolean hasCorsRules() {
        return this.corsRules != null && !(this.corsRules instanceof SdkAutoConstructList);
    }

    public final List<CORSRule> corsRules() {
        return this.corsRules;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.hasCorsRules() ? this.corsRules() : null);
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
        if (!(obj instanceof GetBucketCorsResponse)) {
            return false;
        }
        GetBucketCorsResponse other = (GetBucketCorsResponse)((Object)obj);
        return this.hasCorsRules() == other.hasCorsRules() && Objects.equals(this.corsRules(), other.corsRules());
    }

    public final String toString() {
        return ToString.builder((String)"GetBucketCorsResponse").add("CORSRules", this.hasCorsRules() ? this.corsRules() : null).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "CORSRules": {
                return Optional.ofNullable(clazz.cast(this.corsRules()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<GetBucketCorsResponse, T> g) {
        return obj -> g.apply((GetBucketCorsResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Response.BuilderImpl
    implements Builder {
        private List<CORSRule> corsRules = DefaultSdkAutoConstructList.getInstance();

        private BuilderImpl() {
        }

        private BuilderImpl(GetBucketCorsResponse model) {
            super(model);
            this.corsRules(model.corsRules);
        }

        public final List<CORSRule.Builder> getCorsRules() {
            List<CORSRule.Builder> result = CORSRulesCopier.copyToBuilder(this.corsRules);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setCorsRules(Collection<CORSRule.BuilderImpl> corsRules) {
            this.corsRules = CORSRulesCopier.copyFromBuilder(corsRules);
        }

        @Override
        public final Builder corsRules(Collection<CORSRule> corsRules) {
            this.corsRules = CORSRulesCopier.copy(corsRules);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder corsRules(CORSRule ... corsRules) {
            this.corsRules(Arrays.asList(corsRules));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder corsRules(Consumer<CORSRule.Builder> ... corsRules) {
            this.corsRules(Stream.of(corsRules).map(c -> (CORSRule)((CORSRule.Builder)CORSRule.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        @Override
        public GetBucketCorsResponse build() {
            return new GetBucketCorsResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Response.Builder,
    SdkPojo,
    CopyableBuilder<Builder, GetBucketCorsResponse> {
        public Builder corsRules(Collection<CORSRule> var1);

        public Builder corsRules(CORSRule ... var1);

        public Builder corsRules(Consumer<CORSRule.Builder> ... var1);
    }
}

