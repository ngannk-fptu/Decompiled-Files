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
import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.CSVOutput;
import software.amazon.awssdk.services.s3.model.JSONOutput;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class OutputSerialization
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, OutputSerialization> {
    private static final SdkField<CSVOutput> CSV_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("CSV").getter(OutputSerialization.getter(OutputSerialization::csv)).setter(OutputSerialization.setter(Builder::csv)).constructor(CSVOutput::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("CSV").unmarshallLocationName("CSV").build()}).build();
    private static final SdkField<JSONOutput> JSON_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("JSON").getter(OutputSerialization.getter(OutputSerialization::json)).setter(OutputSerialization.setter(Builder::json)).constructor(JSONOutput::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("JSON").unmarshallLocationName("JSON").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(CSV_FIELD, JSON_FIELD));
    private static final long serialVersionUID = 1L;
    private final CSVOutput csv;
    private final JSONOutput json;

    private OutputSerialization(BuilderImpl builder) {
        this.csv = builder.csv;
        this.json = builder.json;
    }

    public final CSVOutput csv() {
        return this.csv;
    }

    public final JSONOutput json() {
        return this.json;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.csv());
        hashCode = 31 * hashCode + Objects.hashCode(this.json());
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
        if (!(obj instanceof OutputSerialization)) {
            return false;
        }
        OutputSerialization other = (OutputSerialization)obj;
        return Objects.equals(this.csv(), other.csv()) && Objects.equals(this.json(), other.json());
    }

    public final String toString() {
        return ToString.builder((String)"OutputSerialization").add("CSV", (Object)this.csv()).add("JSON", (Object)this.json()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "CSV": {
                return Optional.ofNullable(clazz.cast(this.csv()));
            }
            case "JSON": {
                return Optional.ofNullable(clazz.cast(this.json()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<OutputSerialization, T> g) {
        return obj -> g.apply((OutputSerialization)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private CSVOutput csv;
        private JSONOutput json;

        private BuilderImpl() {
        }

        private BuilderImpl(OutputSerialization model) {
            this.csv(model.csv);
            this.json(model.json);
        }

        public final CSVOutput.Builder getCsv() {
            return this.csv != null ? this.csv.toBuilder() : null;
        }

        public final void setCsv(CSVOutput.BuilderImpl csv) {
            this.csv = csv != null ? csv.build() : null;
        }

        @Override
        public final Builder csv(CSVOutput csv) {
            this.csv = csv;
            return this;
        }

        public final JSONOutput.Builder getJson() {
            return this.json != null ? this.json.toBuilder() : null;
        }

        public final void setJson(JSONOutput.BuilderImpl json) {
            this.json = json != null ? json.build() : null;
        }

        @Override
        public final Builder json(JSONOutput json) {
            this.json = json;
            return this;
        }

        public OutputSerialization build() {
            return new OutputSerialization(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, OutputSerialization> {
        public Builder csv(CSVOutput var1);

        default public Builder csv(Consumer<CSVOutput.Builder> csv) {
            return this.csv((CSVOutput)((CSVOutput.Builder)CSVOutput.builder().applyMutation(csv)).build());
        }

        public Builder json(JSONOutput var1);

        default public Builder json(Consumer<JSONOutput.Builder> json) {
            return this.json((JSONOutput)((JSONOutput.Builder)JSONOutput.builder().applyMutation(json)).build());
        }
    }
}

