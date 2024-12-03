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
import software.amazon.awssdk.services.s3.model.FileHeaderInfo;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class CSVInput
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, CSVInput> {
    private static final SdkField<String> FILE_HEADER_INFO_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("FileHeaderInfo").getter(CSVInput.getter(CSVInput::fileHeaderInfoAsString)).setter(CSVInput.setter(Builder::fileHeaderInfo)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("FileHeaderInfo").unmarshallLocationName("FileHeaderInfo").build()}).build();
    private static final SdkField<String> COMMENTS_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Comments").getter(CSVInput.getter(CSVInput::comments)).setter(CSVInput.setter(Builder::comments)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Comments").unmarshallLocationName("Comments").build()}).build();
    private static final SdkField<String> QUOTE_ESCAPE_CHARACTER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("QuoteEscapeCharacter").getter(CSVInput.getter(CSVInput::quoteEscapeCharacter)).setter(CSVInput.setter(Builder::quoteEscapeCharacter)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("QuoteEscapeCharacter").unmarshallLocationName("QuoteEscapeCharacter").build()}).build();
    private static final SdkField<String> RECORD_DELIMITER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("RecordDelimiter").getter(CSVInput.getter(CSVInput::recordDelimiter)).setter(CSVInput.setter(Builder::recordDelimiter)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("RecordDelimiter").unmarshallLocationName("RecordDelimiter").build()}).build();
    private static final SdkField<String> FIELD_DELIMITER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("FieldDelimiter").getter(CSVInput.getter(CSVInput::fieldDelimiter)).setter(CSVInput.setter(Builder::fieldDelimiter)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("FieldDelimiter").unmarshallLocationName("FieldDelimiter").build()}).build();
    private static final SdkField<String> QUOTE_CHARACTER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("QuoteCharacter").getter(CSVInput.getter(CSVInput::quoteCharacter)).setter(CSVInput.setter(Builder::quoteCharacter)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("QuoteCharacter").unmarshallLocationName("QuoteCharacter").build()}).build();
    private static final SdkField<Boolean> ALLOW_QUOTED_RECORD_DELIMITER_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("AllowQuotedRecordDelimiter").getter(CSVInput.getter(CSVInput::allowQuotedRecordDelimiter)).setter(CSVInput.setter(Builder::allowQuotedRecordDelimiter)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("AllowQuotedRecordDelimiter").unmarshallLocationName("AllowQuotedRecordDelimiter").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(FILE_HEADER_INFO_FIELD, COMMENTS_FIELD, QUOTE_ESCAPE_CHARACTER_FIELD, RECORD_DELIMITER_FIELD, FIELD_DELIMITER_FIELD, QUOTE_CHARACTER_FIELD, ALLOW_QUOTED_RECORD_DELIMITER_FIELD));
    private static final long serialVersionUID = 1L;
    private final String fileHeaderInfo;
    private final String comments;
    private final String quoteEscapeCharacter;
    private final String recordDelimiter;
    private final String fieldDelimiter;
    private final String quoteCharacter;
    private final Boolean allowQuotedRecordDelimiter;

    private CSVInput(BuilderImpl builder) {
        this.fileHeaderInfo = builder.fileHeaderInfo;
        this.comments = builder.comments;
        this.quoteEscapeCharacter = builder.quoteEscapeCharacter;
        this.recordDelimiter = builder.recordDelimiter;
        this.fieldDelimiter = builder.fieldDelimiter;
        this.quoteCharacter = builder.quoteCharacter;
        this.allowQuotedRecordDelimiter = builder.allowQuotedRecordDelimiter;
    }

    public final FileHeaderInfo fileHeaderInfo() {
        return FileHeaderInfo.fromValue(this.fileHeaderInfo);
    }

    public final String fileHeaderInfoAsString() {
        return this.fileHeaderInfo;
    }

    public final String comments() {
        return this.comments;
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

    public final Boolean allowQuotedRecordDelimiter() {
        return this.allowQuotedRecordDelimiter;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.fileHeaderInfoAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.comments());
        hashCode = 31 * hashCode + Objects.hashCode(this.quoteEscapeCharacter());
        hashCode = 31 * hashCode + Objects.hashCode(this.recordDelimiter());
        hashCode = 31 * hashCode + Objects.hashCode(this.fieldDelimiter());
        hashCode = 31 * hashCode + Objects.hashCode(this.quoteCharacter());
        hashCode = 31 * hashCode + Objects.hashCode(this.allowQuotedRecordDelimiter());
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
        if (!(obj instanceof CSVInput)) {
            return false;
        }
        CSVInput other = (CSVInput)obj;
        return Objects.equals(this.fileHeaderInfoAsString(), other.fileHeaderInfoAsString()) && Objects.equals(this.comments(), other.comments()) && Objects.equals(this.quoteEscapeCharacter(), other.quoteEscapeCharacter()) && Objects.equals(this.recordDelimiter(), other.recordDelimiter()) && Objects.equals(this.fieldDelimiter(), other.fieldDelimiter()) && Objects.equals(this.quoteCharacter(), other.quoteCharacter()) && Objects.equals(this.allowQuotedRecordDelimiter(), other.allowQuotedRecordDelimiter());
    }

    public final String toString() {
        return ToString.builder((String)"CSVInput").add("FileHeaderInfo", (Object)this.fileHeaderInfoAsString()).add("Comments", (Object)this.comments()).add("QuoteEscapeCharacter", (Object)this.quoteEscapeCharacter()).add("RecordDelimiter", (Object)this.recordDelimiter()).add("FieldDelimiter", (Object)this.fieldDelimiter()).add("QuoteCharacter", (Object)this.quoteCharacter()).add("AllowQuotedRecordDelimiter", (Object)this.allowQuotedRecordDelimiter()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "FileHeaderInfo": {
                return Optional.ofNullable(clazz.cast(this.fileHeaderInfoAsString()));
            }
            case "Comments": {
                return Optional.ofNullable(clazz.cast(this.comments()));
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
            case "AllowQuotedRecordDelimiter": {
                return Optional.ofNullable(clazz.cast(this.allowQuotedRecordDelimiter()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<CSVInput, T> g) {
        return obj -> g.apply((CSVInput)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String fileHeaderInfo;
        private String comments;
        private String quoteEscapeCharacter;
        private String recordDelimiter;
        private String fieldDelimiter;
        private String quoteCharacter;
        private Boolean allowQuotedRecordDelimiter;

        private BuilderImpl() {
        }

        private BuilderImpl(CSVInput model) {
            this.fileHeaderInfo(model.fileHeaderInfo);
            this.comments(model.comments);
            this.quoteEscapeCharacter(model.quoteEscapeCharacter);
            this.recordDelimiter(model.recordDelimiter);
            this.fieldDelimiter(model.fieldDelimiter);
            this.quoteCharacter(model.quoteCharacter);
            this.allowQuotedRecordDelimiter(model.allowQuotedRecordDelimiter);
        }

        public final String getFileHeaderInfo() {
            return this.fileHeaderInfo;
        }

        public final void setFileHeaderInfo(String fileHeaderInfo) {
            this.fileHeaderInfo = fileHeaderInfo;
        }

        @Override
        public final Builder fileHeaderInfo(String fileHeaderInfo) {
            this.fileHeaderInfo = fileHeaderInfo;
            return this;
        }

        @Override
        public final Builder fileHeaderInfo(FileHeaderInfo fileHeaderInfo) {
            this.fileHeaderInfo(fileHeaderInfo == null ? null : fileHeaderInfo.toString());
            return this;
        }

        public final String getComments() {
            return this.comments;
        }

        public final void setComments(String comments) {
            this.comments = comments;
        }

        @Override
        public final Builder comments(String comments) {
            this.comments = comments;
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

        public final Boolean getAllowQuotedRecordDelimiter() {
            return this.allowQuotedRecordDelimiter;
        }

        public final void setAllowQuotedRecordDelimiter(Boolean allowQuotedRecordDelimiter) {
            this.allowQuotedRecordDelimiter = allowQuotedRecordDelimiter;
        }

        @Override
        public final Builder allowQuotedRecordDelimiter(Boolean allowQuotedRecordDelimiter) {
            this.allowQuotedRecordDelimiter = allowQuotedRecordDelimiter;
            return this;
        }

        public CSVInput build() {
            return new CSVInput(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, CSVInput> {
        public Builder fileHeaderInfo(String var1);

        public Builder fileHeaderInfo(FileHeaderInfo var1);

        public Builder comments(String var1);

        public Builder quoteEscapeCharacter(String var1);

        public Builder recordDelimiter(String var1);

        public Builder fieldDelimiter(String var1);

        public Builder quoteCharacter(String var1);

        public Builder allowQuotedRecordDelimiter(Boolean var1);
    }
}

