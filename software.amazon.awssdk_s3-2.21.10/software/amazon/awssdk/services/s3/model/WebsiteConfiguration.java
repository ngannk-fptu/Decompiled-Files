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
import software.amazon.awssdk.services.s3.model.ErrorDocument;
import software.amazon.awssdk.services.s3.model.IndexDocument;
import software.amazon.awssdk.services.s3.model.RedirectAllRequestsTo;
import software.amazon.awssdk.services.s3.model.RoutingRule;
import software.amazon.awssdk.services.s3.model.RoutingRulesCopier;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class WebsiteConfiguration
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, WebsiteConfiguration> {
    private static final SdkField<ErrorDocument> ERROR_DOCUMENT_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("ErrorDocument").getter(WebsiteConfiguration.getter(WebsiteConfiguration::errorDocument)).setter(WebsiteConfiguration.setter(Builder::errorDocument)).constructor(ErrorDocument::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ErrorDocument").unmarshallLocationName("ErrorDocument").build()}).build();
    private static final SdkField<IndexDocument> INDEX_DOCUMENT_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("IndexDocument").getter(WebsiteConfiguration.getter(WebsiteConfiguration::indexDocument)).setter(WebsiteConfiguration.setter(Builder::indexDocument)).constructor(IndexDocument::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("IndexDocument").unmarshallLocationName("IndexDocument").build()}).build();
    private static final SdkField<RedirectAllRequestsTo> REDIRECT_ALL_REQUESTS_TO_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("RedirectAllRequestsTo").getter(WebsiteConfiguration.getter(WebsiteConfiguration::redirectAllRequestsTo)).setter(WebsiteConfiguration.setter(Builder::redirectAllRequestsTo)).constructor(RedirectAllRequestsTo::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("RedirectAllRequestsTo").unmarshallLocationName("RedirectAllRequestsTo").build()}).build();
    private static final SdkField<List<RoutingRule>> ROUTING_RULES_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("RoutingRules").getter(WebsiteConfiguration.getter(WebsiteConfiguration::routingRules)).setter(WebsiteConfiguration.setter(Builder::routingRules)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("RoutingRules").unmarshallLocationName("RoutingRules").build(), ListTrait.builder().memberLocationName("RoutingRule").memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(RoutingRule::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("RoutingRule").unmarshallLocationName("RoutingRule").build()}).build()).build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(ERROR_DOCUMENT_FIELD, INDEX_DOCUMENT_FIELD, REDIRECT_ALL_REQUESTS_TO_FIELD, ROUTING_RULES_FIELD));
    private static final long serialVersionUID = 1L;
    private final ErrorDocument errorDocument;
    private final IndexDocument indexDocument;
    private final RedirectAllRequestsTo redirectAllRequestsTo;
    private final List<RoutingRule> routingRules;

    private WebsiteConfiguration(BuilderImpl builder) {
        this.errorDocument = builder.errorDocument;
        this.indexDocument = builder.indexDocument;
        this.redirectAllRequestsTo = builder.redirectAllRequestsTo;
        this.routingRules = builder.routingRules;
    }

    public final ErrorDocument errorDocument() {
        return this.errorDocument;
    }

    public final IndexDocument indexDocument() {
        return this.indexDocument;
    }

    public final RedirectAllRequestsTo redirectAllRequestsTo() {
        return this.redirectAllRequestsTo;
    }

    public final boolean hasRoutingRules() {
        return this.routingRules != null && !(this.routingRules instanceof SdkAutoConstructList);
    }

    public final List<RoutingRule> routingRules() {
        return this.routingRules;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.errorDocument());
        hashCode = 31 * hashCode + Objects.hashCode(this.indexDocument());
        hashCode = 31 * hashCode + Objects.hashCode(this.redirectAllRequestsTo());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasRoutingRules() ? this.routingRules() : null);
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
        if (!(obj instanceof WebsiteConfiguration)) {
            return false;
        }
        WebsiteConfiguration other = (WebsiteConfiguration)obj;
        return Objects.equals(this.errorDocument(), other.errorDocument()) && Objects.equals(this.indexDocument(), other.indexDocument()) && Objects.equals(this.redirectAllRequestsTo(), other.redirectAllRequestsTo()) && this.hasRoutingRules() == other.hasRoutingRules() && Objects.equals(this.routingRules(), other.routingRules());
    }

    public final String toString() {
        return ToString.builder((String)"WebsiteConfiguration").add("ErrorDocument", (Object)this.errorDocument()).add("IndexDocument", (Object)this.indexDocument()).add("RedirectAllRequestsTo", (Object)this.redirectAllRequestsTo()).add("RoutingRules", this.hasRoutingRules() ? this.routingRules() : null).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "ErrorDocument": {
                return Optional.ofNullable(clazz.cast(this.errorDocument()));
            }
            case "IndexDocument": {
                return Optional.ofNullable(clazz.cast(this.indexDocument()));
            }
            case "RedirectAllRequestsTo": {
                return Optional.ofNullable(clazz.cast(this.redirectAllRequestsTo()));
            }
            case "RoutingRules": {
                return Optional.ofNullable(clazz.cast(this.routingRules()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<WebsiteConfiguration, T> g) {
        return obj -> g.apply((WebsiteConfiguration)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private ErrorDocument errorDocument;
        private IndexDocument indexDocument;
        private RedirectAllRequestsTo redirectAllRequestsTo;
        private List<RoutingRule> routingRules = DefaultSdkAutoConstructList.getInstance();

        private BuilderImpl() {
        }

        private BuilderImpl(WebsiteConfiguration model) {
            this.errorDocument(model.errorDocument);
            this.indexDocument(model.indexDocument);
            this.redirectAllRequestsTo(model.redirectAllRequestsTo);
            this.routingRules(model.routingRules);
        }

        public final ErrorDocument.Builder getErrorDocument() {
            return this.errorDocument != null ? this.errorDocument.toBuilder() : null;
        }

        public final void setErrorDocument(ErrorDocument.BuilderImpl errorDocument) {
            this.errorDocument = errorDocument != null ? errorDocument.build() : null;
        }

        @Override
        public final Builder errorDocument(ErrorDocument errorDocument) {
            this.errorDocument = errorDocument;
            return this;
        }

        public final IndexDocument.Builder getIndexDocument() {
            return this.indexDocument != null ? this.indexDocument.toBuilder() : null;
        }

        public final void setIndexDocument(IndexDocument.BuilderImpl indexDocument) {
            this.indexDocument = indexDocument != null ? indexDocument.build() : null;
        }

        @Override
        public final Builder indexDocument(IndexDocument indexDocument) {
            this.indexDocument = indexDocument;
            return this;
        }

        public final RedirectAllRequestsTo.Builder getRedirectAllRequestsTo() {
            return this.redirectAllRequestsTo != null ? this.redirectAllRequestsTo.toBuilder() : null;
        }

        public final void setRedirectAllRequestsTo(RedirectAllRequestsTo.BuilderImpl redirectAllRequestsTo) {
            this.redirectAllRequestsTo = redirectAllRequestsTo != null ? redirectAllRequestsTo.build() : null;
        }

        @Override
        public final Builder redirectAllRequestsTo(RedirectAllRequestsTo redirectAllRequestsTo) {
            this.redirectAllRequestsTo = redirectAllRequestsTo;
            return this;
        }

        public final List<RoutingRule.Builder> getRoutingRules() {
            List<RoutingRule.Builder> result = RoutingRulesCopier.copyToBuilder(this.routingRules);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setRoutingRules(Collection<RoutingRule.BuilderImpl> routingRules) {
            this.routingRules = RoutingRulesCopier.copyFromBuilder(routingRules);
        }

        @Override
        public final Builder routingRules(Collection<RoutingRule> routingRules) {
            this.routingRules = RoutingRulesCopier.copy(routingRules);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder routingRules(RoutingRule ... routingRules) {
            this.routingRules(Arrays.asList(routingRules));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder routingRules(Consumer<RoutingRule.Builder> ... routingRules) {
            this.routingRules(Stream.of(routingRules).map(c -> (RoutingRule)((RoutingRule.Builder)RoutingRule.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        public WebsiteConfiguration build() {
            return new WebsiteConfiguration(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, WebsiteConfiguration> {
        public Builder errorDocument(ErrorDocument var1);

        default public Builder errorDocument(Consumer<ErrorDocument.Builder> errorDocument) {
            return this.errorDocument((ErrorDocument)((ErrorDocument.Builder)ErrorDocument.builder().applyMutation(errorDocument)).build());
        }

        public Builder indexDocument(IndexDocument var1);

        default public Builder indexDocument(Consumer<IndexDocument.Builder> indexDocument) {
            return this.indexDocument((IndexDocument)((IndexDocument.Builder)IndexDocument.builder().applyMutation(indexDocument)).build());
        }

        public Builder redirectAllRequestsTo(RedirectAllRequestsTo var1);

        default public Builder redirectAllRequestsTo(Consumer<RedirectAllRequestsTo.Builder> redirectAllRequestsTo) {
            return this.redirectAllRequestsTo((RedirectAllRequestsTo)((RedirectAllRequestsTo.Builder)RedirectAllRequestsTo.builder().applyMutation(redirectAllRequestsTo)).build());
        }

        public Builder routingRules(Collection<RoutingRule> var1);

        public Builder routingRules(RoutingRule ... var1);

        public Builder routingRules(Consumer<RoutingRule.Builder> ... var1);
    }
}

