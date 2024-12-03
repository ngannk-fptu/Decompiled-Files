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
import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.RequiredTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.DeleteMarkerReplication;
import software.amazon.awssdk.services.s3.model.Destination;
import software.amazon.awssdk.services.s3.model.ExistingObjectReplication;
import software.amazon.awssdk.services.s3.model.ReplicationRuleFilter;
import software.amazon.awssdk.services.s3.model.ReplicationRuleStatus;
import software.amazon.awssdk.services.s3.model.SourceSelectionCriteria;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class ReplicationRule
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, ReplicationRule> {
    private static final SdkField<String> ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ID").getter(ReplicationRule.getter(ReplicationRule::id)).setter(ReplicationRule.setter(Builder::id)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ID").unmarshallLocationName("ID").build()}).build();
    private static final SdkField<Integer> PRIORITY_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("Priority").getter(ReplicationRule.getter(ReplicationRule::priority)).setter(ReplicationRule.setter(Builder::priority)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Priority").unmarshallLocationName("Priority").build()}).build();
    private static final SdkField<String> PREFIX_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Prefix").getter(ReplicationRule.getter(ReplicationRule::prefix)).setter(ReplicationRule.setter(Builder::prefix)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Prefix").unmarshallLocationName("Prefix").build()}).build();
    private static final SdkField<ReplicationRuleFilter> FILTER_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Filter").getter(ReplicationRule.getter(ReplicationRule::filter)).setter(ReplicationRule.setter(Builder::filter)).constructor(ReplicationRuleFilter::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Filter").unmarshallLocationName("Filter").build()}).build();
    private static final SdkField<String> STATUS_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Status").getter(ReplicationRule.getter(ReplicationRule::statusAsString)).setter(ReplicationRule.setter(Builder::status)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Status").unmarshallLocationName("Status").build(), RequiredTrait.create()}).build();
    private static final SdkField<SourceSelectionCriteria> SOURCE_SELECTION_CRITERIA_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("SourceSelectionCriteria").getter(ReplicationRule.getter(ReplicationRule::sourceSelectionCriteria)).setter(ReplicationRule.setter(Builder::sourceSelectionCriteria)).constructor(SourceSelectionCriteria::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SourceSelectionCriteria").unmarshallLocationName("SourceSelectionCriteria").build()}).build();
    private static final SdkField<ExistingObjectReplication> EXISTING_OBJECT_REPLICATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("ExistingObjectReplication").getter(ReplicationRule.getter(ReplicationRule::existingObjectReplication)).setter(ReplicationRule.setter(Builder::existingObjectReplication)).constructor(ExistingObjectReplication::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ExistingObjectReplication").unmarshallLocationName("ExistingObjectReplication").build()}).build();
    private static final SdkField<Destination> DESTINATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Destination").getter(ReplicationRule.getter(ReplicationRule::destination)).setter(ReplicationRule.setter(Builder::destination)).constructor(Destination::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Destination").unmarshallLocationName("Destination").build(), RequiredTrait.create()}).build();
    private static final SdkField<DeleteMarkerReplication> DELETE_MARKER_REPLICATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("DeleteMarkerReplication").getter(ReplicationRule.getter(ReplicationRule::deleteMarkerReplication)).setter(ReplicationRule.setter(Builder::deleteMarkerReplication)).constructor(DeleteMarkerReplication::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("DeleteMarkerReplication").unmarshallLocationName("DeleteMarkerReplication").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(ID_FIELD, PRIORITY_FIELD, PREFIX_FIELD, FILTER_FIELD, STATUS_FIELD, SOURCE_SELECTION_CRITERIA_FIELD, EXISTING_OBJECT_REPLICATION_FIELD, DESTINATION_FIELD, DELETE_MARKER_REPLICATION_FIELD));
    private static final long serialVersionUID = 1L;
    private final String id;
    private final Integer priority;
    private final String prefix;
    private final ReplicationRuleFilter filter;
    private final String status;
    private final SourceSelectionCriteria sourceSelectionCriteria;
    private final ExistingObjectReplication existingObjectReplication;
    private final Destination destination;
    private final DeleteMarkerReplication deleteMarkerReplication;

    private ReplicationRule(BuilderImpl builder) {
        this.id = builder.id;
        this.priority = builder.priority;
        this.prefix = builder.prefix;
        this.filter = builder.filter;
        this.status = builder.status;
        this.sourceSelectionCriteria = builder.sourceSelectionCriteria;
        this.existingObjectReplication = builder.existingObjectReplication;
        this.destination = builder.destination;
        this.deleteMarkerReplication = builder.deleteMarkerReplication;
    }

    public final String id() {
        return this.id;
    }

    public final Integer priority() {
        return this.priority;
    }

    @Deprecated
    public final String prefix() {
        return this.prefix;
    }

    public final ReplicationRuleFilter filter() {
        return this.filter;
    }

    public final ReplicationRuleStatus status() {
        return ReplicationRuleStatus.fromValue(this.status);
    }

    public final String statusAsString() {
        return this.status;
    }

    public final SourceSelectionCriteria sourceSelectionCriteria() {
        return this.sourceSelectionCriteria;
    }

    public final ExistingObjectReplication existingObjectReplication() {
        return this.existingObjectReplication;
    }

    public final Destination destination() {
        return this.destination;
    }

    public final DeleteMarkerReplication deleteMarkerReplication() {
        return this.deleteMarkerReplication;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.priority());
        hashCode = 31 * hashCode + Objects.hashCode(this.prefix());
        hashCode = 31 * hashCode + Objects.hashCode(this.filter());
        hashCode = 31 * hashCode + Objects.hashCode(this.statusAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.sourceSelectionCriteria());
        hashCode = 31 * hashCode + Objects.hashCode(this.existingObjectReplication());
        hashCode = 31 * hashCode + Objects.hashCode(this.destination());
        hashCode = 31 * hashCode + Objects.hashCode(this.deleteMarkerReplication());
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
        if (!(obj instanceof ReplicationRule)) {
            return false;
        }
        ReplicationRule other = (ReplicationRule)obj;
        return Objects.equals(this.id(), other.id()) && Objects.equals(this.priority(), other.priority()) && Objects.equals(this.prefix(), other.prefix()) && Objects.equals(this.filter(), other.filter()) && Objects.equals(this.statusAsString(), other.statusAsString()) && Objects.equals(this.sourceSelectionCriteria(), other.sourceSelectionCriteria()) && Objects.equals(this.existingObjectReplication(), other.existingObjectReplication()) && Objects.equals(this.destination(), other.destination()) && Objects.equals(this.deleteMarkerReplication(), other.deleteMarkerReplication());
    }

    public final String toString() {
        return ToString.builder((String)"ReplicationRule").add("ID", (Object)this.id()).add("Priority", (Object)this.priority()).add("Prefix", (Object)this.prefix()).add("Filter", (Object)this.filter()).add("Status", (Object)this.statusAsString()).add("SourceSelectionCriteria", (Object)this.sourceSelectionCriteria()).add("ExistingObjectReplication", (Object)this.existingObjectReplication()).add("Destination", (Object)this.destination()).add("DeleteMarkerReplication", (Object)this.deleteMarkerReplication()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "ID": {
                return Optional.ofNullable(clazz.cast(this.id()));
            }
            case "Priority": {
                return Optional.ofNullable(clazz.cast(this.priority()));
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
            case "SourceSelectionCriteria": {
                return Optional.ofNullable(clazz.cast(this.sourceSelectionCriteria()));
            }
            case "ExistingObjectReplication": {
                return Optional.ofNullable(clazz.cast(this.existingObjectReplication()));
            }
            case "Destination": {
                return Optional.ofNullable(clazz.cast(this.destination()));
            }
            case "DeleteMarkerReplication": {
                return Optional.ofNullable(clazz.cast(this.deleteMarkerReplication()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<ReplicationRule, T> g) {
        return obj -> g.apply((ReplicationRule)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String id;
        private Integer priority;
        private String prefix;
        private ReplicationRuleFilter filter;
        private String status;
        private SourceSelectionCriteria sourceSelectionCriteria;
        private ExistingObjectReplication existingObjectReplication;
        private Destination destination;
        private DeleteMarkerReplication deleteMarkerReplication;

        private BuilderImpl() {
        }

        private BuilderImpl(ReplicationRule model) {
            this.id(model.id);
            this.priority(model.priority);
            this.prefix(model.prefix);
            this.filter(model.filter);
            this.status(model.status);
            this.sourceSelectionCriteria(model.sourceSelectionCriteria);
            this.existingObjectReplication(model.existingObjectReplication);
            this.destination(model.destination);
            this.deleteMarkerReplication(model.deleteMarkerReplication);
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

        public final Integer getPriority() {
            return this.priority;
        }

        public final void setPriority(Integer priority) {
            this.priority = priority;
        }

        @Override
        public final Builder priority(Integer priority) {
            this.priority = priority;
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

        public final ReplicationRuleFilter.Builder getFilter() {
            return this.filter != null ? this.filter.toBuilder() : null;
        }

        public final void setFilter(ReplicationRuleFilter.BuilderImpl filter) {
            this.filter = filter != null ? filter.build() : null;
        }

        @Override
        public final Builder filter(ReplicationRuleFilter filter) {
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
        public final Builder status(ReplicationRuleStatus status) {
            this.status(status == null ? null : status.toString());
            return this;
        }

        public final SourceSelectionCriteria.Builder getSourceSelectionCriteria() {
            return this.sourceSelectionCriteria != null ? this.sourceSelectionCriteria.toBuilder() : null;
        }

        public final void setSourceSelectionCriteria(SourceSelectionCriteria.BuilderImpl sourceSelectionCriteria) {
            this.sourceSelectionCriteria = sourceSelectionCriteria != null ? sourceSelectionCriteria.build() : null;
        }

        @Override
        public final Builder sourceSelectionCriteria(SourceSelectionCriteria sourceSelectionCriteria) {
            this.sourceSelectionCriteria = sourceSelectionCriteria;
            return this;
        }

        public final ExistingObjectReplication.Builder getExistingObjectReplication() {
            return this.existingObjectReplication != null ? this.existingObjectReplication.toBuilder() : null;
        }

        public final void setExistingObjectReplication(ExistingObjectReplication.BuilderImpl existingObjectReplication) {
            this.existingObjectReplication = existingObjectReplication != null ? existingObjectReplication.build() : null;
        }

        @Override
        public final Builder existingObjectReplication(ExistingObjectReplication existingObjectReplication) {
            this.existingObjectReplication = existingObjectReplication;
            return this;
        }

        public final Destination.Builder getDestination() {
            return this.destination != null ? this.destination.toBuilder() : null;
        }

        public final void setDestination(Destination.BuilderImpl destination) {
            this.destination = destination != null ? destination.build() : null;
        }

        @Override
        public final Builder destination(Destination destination) {
            this.destination = destination;
            return this;
        }

        public final DeleteMarkerReplication.Builder getDeleteMarkerReplication() {
            return this.deleteMarkerReplication != null ? this.deleteMarkerReplication.toBuilder() : null;
        }

        public final void setDeleteMarkerReplication(DeleteMarkerReplication.BuilderImpl deleteMarkerReplication) {
            this.deleteMarkerReplication = deleteMarkerReplication != null ? deleteMarkerReplication.build() : null;
        }

        @Override
        public final Builder deleteMarkerReplication(DeleteMarkerReplication deleteMarkerReplication) {
            this.deleteMarkerReplication = deleteMarkerReplication;
            return this;
        }

        public ReplicationRule build() {
            return new ReplicationRule(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, ReplicationRule> {
        public Builder id(String var1);

        public Builder priority(Integer var1);

        @Deprecated
        public Builder prefix(String var1);

        public Builder filter(ReplicationRuleFilter var1);

        default public Builder filter(Consumer<ReplicationRuleFilter.Builder> filter) {
            return this.filter((ReplicationRuleFilter)((ReplicationRuleFilter.Builder)ReplicationRuleFilter.builder().applyMutation(filter)).build());
        }

        public Builder status(String var1);

        public Builder status(ReplicationRuleStatus var1);

        public Builder sourceSelectionCriteria(SourceSelectionCriteria var1);

        default public Builder sourceSelectionCriteria(Consumer<SourceSelectionCriteria.Builder> sourceSelectionCriteria) {
            return this.sourceSelectionCriteria((SourceSelectionCriteria)((SourceSelectionCriteria.Builder)SourceSelectionCriteria.builder().applyMutation(sourceSelectionCriteria)).build());
        }

        public Builder existingObjectReplication(ExistingObjectReplication var1);

        default public Builder existingObjectReplication(Consumer<ExistingObjectReplication.Builder> existingObjectReplication) {
            return this.existingObjectReplication((ExistingObjectReplication)((ExistingObjectReplication.Builder)ExistingObjectReplication.builder().applyMutation(existingObjectReplication)).build());
        }

        public Builder destination(Destination var1);

        default public Builder destination(Consumer<Destination.Builder> destination) {
            return this.destination((Destination)((Destination.Builder)Destination.builder().applyMutation(destination)).build());
        }

        public Builder deleteMarkerReplication(DeleteMarkerReplication var1);

        default public Builder deleteMarkerReplication(Consumer<DeleteMarkerReplication.Builder> deleteMarkerReplication) {
            return this.deleteMarkerReplication((DeleteMarkerReplication)((DeleteMarkerReplication.Builder)DeleteMarkerReplication.builder().applyMutation(deleteMarkerReplication)).build());
        }
    }
}

