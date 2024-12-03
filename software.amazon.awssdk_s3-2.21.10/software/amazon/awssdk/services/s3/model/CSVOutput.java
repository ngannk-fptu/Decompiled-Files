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
import software.amazon.awssdk.services.s3.model.QuoteFields;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class CSVOutput
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, CSVOutput> {
    private static final SdkField<String> QUOTE_FIELDS_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("QuoteFields").getter(CSVOutput.getter(CSVOutput::quoteFieldsAsString)).setter(CSVOutput.setter(Builder::quoteFields)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("QuoteFields").unmarshallLocationName("QuoteFields").build()}).build();
    private static final SdkField<String> QUOTE_ESCAPE_CHARACTER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("QuoteEscapeCharacter").getter(CSVOutput.getter(CSVOutput::quoteEscapeCharacter)).setter(CSVOutput.setter(Builder::quoteEscapeCharacter)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("QuoteEscapeCharacter").unmarshallLocationName("QuoteEscapeCharacter").build()}).build();
    private static final SdkField<String> RECORD_DELIMITER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("RecordDelimiter").getter(CSVOutput.getter(CSVOutput::recordDelimiter)).setter(CSVOutput.setter(Builder::recordDelimiter)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("RecordDelimiter").unmarshallLocationName("RecordDelimiter").build()}).build();
    private static final SdkField<String> FIELD_DELIMITER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("FieldDelimiter").getter(CSVOutput.getter(CSVOutput::fieldDelimiter)).setter(CSVOutput.setter(Builder::fieldDelimiter)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("FieldDelimiter").unmarshallLocationName("FieldDelimiter").build()}).build();
    private static final SdkField<String> QUOTE_CHARACTER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("QuoteCharacter").getter(CSVOutput.getter(CSVOutput::quoteCharacter)).setter(CSVOutput.setter(Builder::quoteCharacter)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("QuoteCharacter").unmarshallLocationName("QuoteCharacter").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(QUOTE_FIELDS_FIELD, QUOTE_ESCAPE_CHARACTER_FIELD, RECORD_DELIMITER_FIELD, FIELD_DELIMITER_FIELD, QUOTE_CHARACTER_FIELD));
    private static final long serialVersionUID = 1L;
    private final String quoteFields;
    private final String quoteEscapeCharacter;
    private final String recordDelimiter;
    private final String fieldDelimiter;
    private final String quoteCharacter;

    private CSVOutput(BuilderImpl builder) {
        this.quoteFields = builder.quoteFields;
        this.quoteEscapeCharacter = builder.quoteEscapeCharacter;
        this.recordDelimiter = builder.recordDelimiter;
        this.fieldDelimiter = builder.fieldDelimiter;
        this.quoteCharacter = builder.quoteCharacter;
    }

    public final QuoteFields quoteFields() {
        return QuoteFields.fromValue(this.quoteFields);
    }

    public final String quoteFieldsAsString() {
        return this.quoteFields;
    }

    public final String quoteEscapeCharacter() {
        return this.quoteEscapeCharacter;
    }

    public final String recordDelimiter() {
        return this.recordDelimiter;
    }

    public final String fieldDelimiter() {
        return this.fieldDelimiter;
    }

    public final String quoteCharacter() {
        return this.quoteCharacter;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.quoteFieldsAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.quoteEscapeCharacter());
        hashCode = 31 * hashCode + Objects.hashCode(this.recordDelimiter());
        hashCode = 31 * hashCode + Objects.hashCode(this.fieldDelimiter());
        hashCode = 31 * hashCode + Objects.hashCode(this.quoteCharacter());
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
        if (!(obj instanceof CSVOutput)) {
            return false;
        }
        CSVOutput other = (CSVOutput)obj;
        return Objects.equals(this.quoteFieldsAsString(), other.quoteFieldsAsString()) && Objects.equals(this.quoteEscapeCharacter(), other.quoteEscapeCharacter()) && Objects.equals(this.recordDelimiter(), other.recordDelimiter()) && Objects.equals(this.fieldDelimiter(), other.fieldDelimiter()) && Objects.equals(this.quoteCharacter(), other.quoteCharacter());
    }

    public final String toString() {
        return ToString.builder((String)"CSVOutput").add("QuoteFields", (Object)this.quoteFieldsAsString()).add("QuoteEscapeCharacter", (Object)this.quoteEscapeCharacter()).add("RecordDelimiter", (Object)this.recordDelimiter()).add("FieldDelimiter", (Object)this.fieldDelimiter()).add("QuoteCharacter", (Object)this.quoteCharacter()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "QuoteFields": {
                return Optional.ofNullable(clazz.cast(this.quoteFieldsAsString()));
            }
            case "QuoteEscapeCharacter": {
                return Optional.ofNullable(clazz.cast(this.quoteEscapeCharacter()));
            }
            case "RecordDelimiter": {
                return Optional.ofNullable(clazz.cast(this.recordDelimiter()));
            }
            case "FieldDelimiter": {
                return Optional.ofNullable(clazz.cast(this.fieldDelimiter()));
            }
            case "QuoteCharacter": {
                return Optional.ofNullable(clazz.cast(this.quoteCharacter()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<CSVOutput, T> g) {
        return obj -> g.apply((CSVOutput)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String quoteFields;
        private String quoteEscapeCharacter;
        private String recordDelimiter;
        private String fieldDelimiter;
        private String quoteCharacter;

        private BuilderImpl() {
        }

        private BuilderImpl(CSVOutput model) {
            this.quoteFields(model.quoteFields);
            this.quoteEscapeCharacter(model.quoteEscapeCharacter);
            this.recordDelimiter(model.recordDelimiter);
            this.fieldDelimiter(model.fieldDelimiter);
            this.quoteCharacter(model.quoteCharacter);
        }

        public final String getQuoteFields() {
            return this.quoteFields;
        }

        public final void setQuoteFields(String quoteFields) {
            this.quoteFields = quoteFields;
        }

        @Override
        public final Builder quoteFields(String quoteFields) {
            this.quoteFields = quoteFields;
            return this;
        }

        @Override
        public final Builder quoteFields(QuoteFields quoteFields) {
            this.quoteFields(quoteFields == null ? null : quoteFields.toString());
            return this;
        }

        public final String getQuoteEscapeCharacter() {
            return this.quoteEscapeCharacter;
        }

        public final void setQuoteEscapeCharacter(String quoteEscapeCharacter) {
            this.quoteEscapeCharacter = quoteEscapeCharacter;
        }

        @Override
        public final Builder quoteEscapeCharacter(String quoteEscapeCharacter) {
            this.quoteEscapeCharacter = quoteEscapeCharacter;
            return this;
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

        public final String getFieldDelimiter() {
            return this.fieldDelimiter;
        }

        public final void setFieldDelimiter(String fieldDelimiter) {
            this.fieldDelimiter = fieldDelimiter;
        }

        @Override
        public final Builder fieldDelimiter(String fieldDelimiter) {
            this.fieldDelimiter = fieldDelimiter;
            return this;
        }

        public final String getQuoteCharacter() {
            return this.quoteCharacter;
        }

        public final void setQuoteCharacter(String quoteCharacter) {
            this.quoteCharacter = quoteCharacter;
        }

        @Override
        public final Builder quoteCharacter(String quoteCharacter) {
            this.quoteCharacter = quoteCharacter;
            return this;
        }

        public CSVOutput build() {
            return new CSVOutput(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, CSVOutput> {
        public Builder quoteFields(String var1);

        public Builder quoteFields(QuoteFields var1);

        public Builder quoteEscapeCharacter(String var1);

        public Builder recordDelimiter(String var1);

        public Builder fieldDelimiter(String var1);

        public Builder quoteCharacter(String var1);
    }
}

