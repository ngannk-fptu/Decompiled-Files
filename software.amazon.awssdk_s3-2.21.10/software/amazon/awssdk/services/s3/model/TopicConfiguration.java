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
import software.amazon.awssdk.services.s3.model.Event;
import software.amazon.awssdk.services.s3.model.EventListCopier;
import software.amazon.awssdk.services.s3.model.NotificationConfigurationFilter;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class TopicConfiguration
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, TopicConfiguration> {
    private static final SdkField<String> ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Id").getter(TopicConfiguration.getter(TopicConfiguration::id)).setter(TopicConfiguration.setter(Builder::id)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Id").unmarshallLocationName("Id").build()}).build();
    private static final SdkField<String> TOPIC_ARN_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("TopicArn").getter(TopicConfiguration.getter(TopicConfiguration::topicArn)).setter(TopicConfiguration.setter(Builder::topicArn)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Topic").unmarshallLocationName("Topic").build(), RequiredTrait.create()}).build();
    private static final SdkField<List<String>> EVENTS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("Events").getter(TopicConfiguration.getter(TopicConfiguration::eventsAsStrings)).setter(TopicConfiguration.setter(Builder::eventsWithStrings)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Event").unmarshallLocationName("Event").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.STRING).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build(), RequiredTrait.create()}).build();
    private static final SdkField<NotificationConfigurationFilter> FILTER_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Filter").getter(TopicConfiguration.getter(TopicConfiguration::filter)).setter(TopicConfiguration.setter(Builder::filter)).constructor(NotificationConfigurationFilter::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Filter").unmarshallLocationName("Filter").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(ID_FIELD, TOPIC_ARN_FIELD, EVENTS_FIELD, FILTER_FIELD));
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String topicArn;
    private final List<String> events;
    private final NotificationConfigurationFilter filter;

    private TopicConfiguration(BuilderImpl builder) {
        this.id = builder.id;
        this.topicArn = builder.topicArn;
        this.events = builder.events;
        this.filter = builder.filter;
    }

    public final String id() {
        return this.id;
    }

    public final String topicArn() {
        return this.topicArn;
    }

    public final List<Event> events() {
        return EventListCopier.copyStringToEnum(this.events);
    }

    public final boolean hasEvents() {
        return this.events != null && !(this.events instanceof SdkAutoConstructList);
    }

    public final List<String> eventsAsStrings() {
        return this.events;
    }

    public final NotificationConfigurationFilter filter() {
        return this.filter;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.topicArn());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasEvents() ? this.eventsAsStrings() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.filter());
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
        if (!(obj instanceof TopicConfiguration)) {
            return false;
        }
        TopicConfiguration other = (TopicConfiguration)obj;
        return Objects.equals(this.id(), other.id()) && Objects.equals(this.topicArn(), other.topicArn()) && this.hasEvents() == other.hasEvents() && Objects.equals(this.eventsAsStrings(), other.eventsAsStrings()) && Objects.equals(this.filter(), other.filter());
    }

    public final String toString() {
        return ToString.builder((String)"TopicConfiguration").add("Id", (Object)this.id()).add("TopicArn", (Object)this.topicArn()).add("Events", this.hasEvents() ? this.eventsAsStrings() : null).add("Filter", (Object)this.filter()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Id": {
                return Optional.ofNullable(clazz.cast(this.id()));
            }
            case "TopicArn": {
                return Optional.ofNullable(clazz.cast(this.topicArn()));
            }
            case "Events": {
                return Optional.ofNullable(clazz.cast(this.eventsAsStrings()));
            }
            case "Filter": {
                return Optional.ofNullable(clazz.cast(this.filter()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<TopicConfiguration, T> g) {
        return obj -> g.apply((TopicConfiguration)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String id;
        private String topicArn;
        private List<String> events = DefaultSdkAutoConstructList.getInstance();
        private NotificationConfigurationFilter filter;

        private BuilderImpl() {
        }

        private BuilderImpl(TopicConfiguration model) {
            this.id(model.id);
            this.topicArn(model.topicArn);
            this.eventsWithStrings(model.events);
            this.filter(model.filter);
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

        public final String getTopicArn() {
            return this.topicArn;
        }

        public final void setTopicArn(String topicArn) {
            this.topicArn = topicArn;
        }

        @Override
        public final Builder topicArn(String topicArn) {
            this.topicArn = topicArn;
            return this;
        }

        public final Collection<String> getEvents() {
            if (this.events instanceof SdkAutoConstructList) {
                return null;
            }
            return this.events;
        }

        public final void setEvents(Collection<String> events) {
            this.events = EventListCopier.copy(events);
        }

        @Override
        public final Builder eventsWithStrings(Collection<String> events) {
            this.events = EventListCopier.copy(events);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder eventsWithStrings(String ... events) {
            this.eventsWithStrings(Arrays.asList(events));
            return this;
        }

        @Override
        public final Builder events(Collection<Event> events) {
            this.events = EventListCopier.copyEnumToString(events);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder events(Event ... events) {
            this.events(Arrays.asList(events));
            return this;
        }

        public final NotificationConfigurationFilter.Builder getFilter() {
            return this.filter != null ? this.filter.toBuilder() : null;
        }

        public final void setFilter(NotificationConfigurationFilter.BuilderImpl filter) {
            this.filter = filter != null ? filter.build() : null;
        }

        @Override
        public final Builder filter(NotificationConfigurationFilter filter) {
            this.filter = filter;
            return this;
        }

        public TopicConfiguration build() {
            return new TopicConfiguration(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, TopicConfiguration> {
        public Builder id(String var1);

        public Builder topicArn(String var1);

        public Builder eventsWithStrings(Collection<String> var1);

        public Builder eventsWithStrings(String ... var1);

        public Builder events(Collection<Event> var1);

        public Builder events(Event ... var1);

        public Builder filter(NotificationConfigurationFilter var1);

        default public Builder filter(Consumer<NotificationConfigurationFilter.Builder> filter) {
            return this.filter((NotificationConfigurationFilter)((NotificationConfigurationFilter.Builder)NotificationConfigurationFilter.builder().applyMutation(filter)).build());
        }
    }
}

