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
import java.util.function.Function;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class JSONOutput
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, JSONOutput> {
    private static final SdkField<String> RECORD_DELIMITER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("RecordDelimiter").getter(JSONOutput.getter(JSONOutput::recordDelimiter)).setter(JSONOutput.setter(Builder::recordDelimiter)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("RecordDelimiter").unmarshallLocationName("RecordDelimiter").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(RECORD_DELIMITER_FIELD));
    private static final long serialVersionUID = 1L;
    private final String recordDelimiter;

    private JSONOutput(BuilderImpl builder) {
        this.recordDelimiter = builder.recordDelimiter;
    }

    public final String recordDelimiter() {
        return this.recordDelimiter;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.recordDelimiter());
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
        if (!(obj instanceof JSONOutput)) {
            return false;
        }
        JSONOutput other = (JSONOutput)obj;
        return Objects.equals(this.recordDelimiter(), other.recordDelimiter());
    }

    public final String toString() {
        return ToString.builder((String)"JSONOutput").add("RecordDelimiter", (Object)this.recordDelimiter()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "RecordDelimiter": {
                return Optional.ofNullable(clazz.cast(this.recordDelimiter()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<JSONOutput, T> g) {
        return obj -> g.apply((JSONOutput)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String recordDelimiter;

        private BuilderImpl() {
        }

        private BuilderImpl(JSONOutput model) {
            this.recordDelimiter(model.recordDelimiter);
        }

        public final String getRecordDelimiter() {
            return this.recordDelimiter;
        }

        public final void setRecordDelimiter(String recordDelimiter) {
            this.recordDelimiter = recordDelimiter;
        }

        @Override
        public final Builder recordDelimiter(String recordDelimiter) {
            this.recordDelimiter = recordDelimiter;
            return this;
        }

        public JSONOutput build() {
            return new JSONOutput(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, JSONOutput> {
        public Builder recordDelimiter(String var1);
    }
}

