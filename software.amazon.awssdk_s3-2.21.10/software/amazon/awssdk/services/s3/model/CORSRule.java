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
import java.util.function.Function;
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
import software.amazon.awssdk.services.s3.model.AllowedHeadersCopier;
import software.amazon.awssdk.services.s3.model.AllowedMethodsCopier;
import software.amazon.awssdk.services.s3.model.AllowedOriginsCopier;
import software.amazon.awssdk.services.s3.model.ExposeHeadersCopier;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class CORSRule
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, CORSRule> {
    private static final SdkField<String> ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ID").getter(CORSRule.getter(CORSRule::id)).setter(CORSRule.setter(Builder::id)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ID").unmarshallLocationName("ID").build()}).build();
    private static final SdkField<List<String>> ALLOWED_HEADERS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("AllowedHeaders").getter(CORSRule.getter(CORSRule::allowedHeaders)).setter(CORSRule.setter(Builder::allowedHeaders)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("AllowedHeader").unmarshallLocationName("AllowedHeader").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.STRING).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build()}).build();
    private static final SdkField<List<String>> ALLOWED_METHODS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("AllowedMethods").getter(CORSRule.getter(CORSRule::allowedMethods)).setter(CORSRule.setter(Builder::allowedMethods)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("AllowedMethod").unmarshallLocationName("AllowedMethod").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.STRING).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build(), RequiredTrait.create()}).build();
    private static final SdkField<List<String>> ALLOWED_ORIGINS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("AllowedOrigins").getter(CORSRule.getter(CORSRule::allowedOrigins)).setter(CORSRule.setter(Builder::allowedOrigins)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("AllowedOrigin").unmarshallLocationName("AllowedOrigin").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.STRING).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build(), RequiredTrait.create()}).build();
    private static final SdkField<List<String>> EXPOSE_HEADERS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("ExposeHeaders").getter(CORSRule.getter(CORSRule::exposeHeaders)).setter(CORSRule.setter(Builder::exposeHeaders)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ExposeHeader").unmarshallLocationName("ExposeHeader").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.STRING).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build()}).build();
    private static final SdkField<Integer> MAX_AGE_SECONDS_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("MaxAgeSeconds").getter(CORSRule.getter(CORSRule::maxAgeSeconds)).setter(CORSRule.setter(Builder::maxAgeSeconds)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("MaxAgeSeconds").unmarshallLocationName("MaxAgeSeconds").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(ID_FIELD, ALLOWED_HEADERS_FIELD, ALLOWED_METHODS_FIELD, ALLOWED_ORIGINS_FIELD, EXPOSE_HEADERS_FIELD, MAX_AGE_SECONDS_FIELD));
    private static final long serialVersionUID = 1L;
    private final String id;
    private final List<String> allowedHeaders;
    private final List<String> allowedMethods;
    private final List<String> allowedOrigins;
    private final List<String> exposeHeaders;
    private final Integer maxAgeSeconds;

    private CORSRule(BuilderImpl builder) {
        this.id = builder.id;
        this.allowedHeaders = builder.allowedHeaders;
        this.allowedMethods = builder.allowedMethods;
        this.allowedOrigins = builder.allowedOrigins;
        this.exposeHeaders = builder.exposeHeaders;
        this.maxAgeSeconds = builder.maxAgeSeconds;
    }

    public final String id() {
        return this.id;
    }

    public final boolean hasAllowedHeaders() {
        return this.allowedHeaders != null && !(this.allowedHeaders instanceof SdkAutoConstructList);
    }

    public final List<String> allowedHeaders() {
        return this.allowedHeaders;
    }

    public final boolean hasAllowedMethods() {
        return this.allowedMethods != null && !(this.allowedMethods instanceof SdkAutoConstructList);
    }

    public final List<String> allowedMethods() {
        return this.allowedMethods;
    }

    public final boolean hasAllowedOrigins() {
        return this.allowedOrigins != null && !(this.allowedOrigins instanceof SdkAutoConstructList);
    }

    public final List<String> allowedOrigins() {
        return this.allowedOrigins;
    }

    public final boolean hasExposeHeaders() {
        return this.exposeHeaders != null && !(this.exposeHeaders instanceof SdkAutoConstructList);
    }

    public final List<String> exposeHeaders() {
        return this.exposeHeaders;
    }

    public final Integer maxAgeSeconds() {
        return this.maxAgeSeconds;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.id());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasAllowedHeaders() ? this.allowedHeaders() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.hasAllowedMethods() ? this.allowedMethods() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.hasAllowedOrigins() ? this.allowedOrigins() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.hasExposeHeaders() ? this.exposeHeaders() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.maxAgeSeconds());
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
        if (!(obj instanceof CORSRule)) {
            return false;
        }
        CORSRule other = (CORSRule)obj;
        return Objects.equals(this.id(), other.id()) && this.hasAllowedHeaders() == other.hasAllowedHeaders() && Objects.equals(this.allowedHeaders(), other.allowedHeaders()) && this.hasAllowedMethods() == other.hasAllowedMethods() && Objects.equals(this.allowedMethods(), other.allowedMethods()) && this.hasAllowedOrigins() == other.hasAllowedOrigins() && Objects.equals(this.allowedOrigins(), other.allowedOrigins()) && this.hasExposeHeaders() == other.hasExposeHeaders() && Objects.equals(this.exposeHeaders(), other.exposeHeaders()) && Objects.equals(this.maxAgeSeconds(), other.maxAgeSeconds());
    }

    public final String toString() {
        return ToString.builder((String)"CORSRule").add("ID", (Object)this.id()).add("AllowedHeaders", this.hasAllowedHeaders() ? this.allowedHeaders() : null).add("AllowedMethods", this.hasAllowedMethods() ? this.allowedMethods() : null).add("AllowedOrigins", this.hasAllowedOrigins() ? this.allowedOrigins() : null).add("ExposeHeaders", this.hasExposeHeaders() ? this.exposeHeaders() : null).add("MaxAgeSeconds", (Object)this.maxAgeSeconds()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "ID": {
                return Optional.ofNullable(clazz.cast(this.id()));
            }
            case "AllowedHeaders": {
                return Optional.ofNullable(clazz.cast(this.allowedHeaders()));
            }
            case "AllowedMethods": {
                return Optional.ofNullable(clazz.cast(this.allowedMethods()));
            }
            case "AllowedOrigins": {
                return Optional.ofNullable(clazz.cast(this.allowedOrigins()));
            }
            case "ExposeHeaders": {
                return Optional.ofNullable(clazz.cast(this.exposeHeaders()));
            }
            case "MaxAgeSeconds": {
                return Optional.ofNullable(clazz.cast(this.maxAgeSeconds()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<CORSRule, T> g) {
        return obj -> g.apply((CORSRule)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String id;
        private List<String> allowedHeaders = DefaultSdkAutoConstructList.getInstance();
        private List<String> allowedMethods = DefaultSdkAutoConstructList.getInstance();
        private List<String> allowedOrigins = DefaultSdkAutoConstructList.getInstance();
        private List<String> exposeHeaders = DefaultSdkAutoConstructList.getInstance();
        private Integer maxAgeSeconds;

        private BuilderImpl() {
        }

        private BuilderImpl(CORSRule model) {
            this.id(model.id);
            this.allowedHeaders(model.allowedHeaders);
            this.allowedMethods(model.allowedMethods);
            this.allowedOrigins(model.allowedOrigins);
            this.exposeHeaders(model.exposeHeaders);
            this.maxAgeSeconds(model.maxAgeSeconds);
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

        public final Collection<String> getAllowedHeaders() {
            if (this.allowedHeaders instanceof SdkAutoConstructList) {
                return null;
            }
            return this.allowedHeaders;
        }

        public final void setAllowedHeaders(Collection<String> allowedHeaders) {
            this.allowedHeaders = AllowedHeadersCopier.copy(allowedHeaders);
        }

        @Override
        public final Builder allowedHeaders(Collection<String> allowedHeaders) {
            this.allowedHeaders = AllowedHeadersCopier.copy(allowedHeaders);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder allowedHeaders(String ... allowedHeaders) {
            this.allowedHeaders(Arrays.asList(allowedHeaders));
            return this;
        }

        public final Collection<String> getAllowedMethods() {
            if (this.allowedMethods instanceof SdkAutoConstructList) {
                return null;
            }
            return this.allowedMethods;
        }

        public final void setAllowedMethods(Collection<String> allowedMethods) {
            this.allowedMethods = AllowedMethodsCopier.copy(allowedMethods);
        }

        @Override
        public final Builder allowedMethods(Collection<String> allowedMethods) {
            this.allowedMethods = AllowedMethodsCopier.copy(allowedMethods);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder allowedMethods(String ... allowedMethods) {
            this.allowedMethods(Arrays.asList(allowedMethods));
            return this;
        }

        public final Collection<String> getAllowedOrigins() {
            if (this.allowedOrigins instanceof SdkAutoConstructList) {
                return null;
            }
            return this.allowedOrigins;
        }

        public final void setAllowedOrigins(Collection<String> allowedOrigins) {
            this.allowedOrigins = AllowedOriginsCopier.copy(allowedOrigins);
        }

        @Override
        public final Builder allowedOrigins(Collection<String> allowedOrigins) {
            this.allowedOrigins = AllowedOriginsCopier.copy(allowedOrigins);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder allowedOrigins(String ... allowedOrigins) {
            this.allowedOrigins(Arrays.asList(allowedOrigins));
            return this;
        }

        public final Collection<String> getExposeHeaders() {
            if (this.exposeHeaders instanceof SdkAutoConstructList) {
                return null;
            }
            return this.exposeHeaders;
        }

        public final void setExposeHeaders(Collection<String> exposeHeaders) {
            this.exposeHeaders = ExposeHeadersCopier.copy(exposeHeaders);
        }

        @Override
        public final Builder exposeHeaders(Collection<String> exposeHeaders) {
            this.exposeHeaders = ExposeHeadersCopier.copy(exposeHeaders);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder exposeHeaders(String ... exposeHeaders) {
            this.exposeHeaders(Arrays.asList(exposeHeaders));
            return this;
        }

        public final Integer getMaxAgeSeconds() {
            return this.maxAgeSeconds;
        }

        public final void setMaxAgeSeconds(Integer maxAgeSeconds) {
            this.maxAgeSeconds = maxAgeSeconds;
        }

        @Override
        public final Builder maxAgeSeconds(Integer maxAgeSeconds) {
            this.maxAgeSeconds = maxAgeSeconds;
            return this;
        }

        public CORSRule build() {
            return new CORSRule(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, CORSRule> {
        public Builder id(String var1);

        public Builder allowedHeaders(Collection<String> var1);

        public Builder allowedHeaders(String ... var1);

        public Builder allowedMethods(Collection<String> var1);

        public Builder allowedMethods(String ... var1);

        public Builder allowedOrigins(Collection<String> var1);

        public Builder allowedOrigins(String ... var1);

        public Builder exposeHeaders(Collection<String> var1);

        public Builder exposeHeaders(String ... var1);

        public Builder maxAgeSeconds(Integer var1);
    }
}

