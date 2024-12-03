/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.avro;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchemaCompatibility {
    private static final Logger LOG = LoggerFactory.getLogger(SchemaCompatibility.class);
    public static final String READER_WRITER_COMPATIBLE_MESSAGE = "Reader schema can always successfully decode data written using the writer schema.";

    private SchemaCompatibility() {
    }

    public static SchemaPairCompatibility checkReaderWriterCompatibility(Schema reader, Schema writer) {
        String message;
        SchemaCompatibilityResult compatibility = new ReaderWriterCompatibilityChecker().getCompatibility(reader, writer);
        switch (compatibility.getCompatibility()) {
            case INCOMPATIBLE: {
                message = String.format("Data encoded using writer schema:%n%s%nwill or may fail to decode using reader schema:%n%s%n", writer.toString(true), reader.toString(true));
                break;
            }
            case COMPATIBLE: {
                message = READER_WRITER_COMPATIBLE_MESSAGE;
                break;
            }
            default: {
                throw new AvroRuntimeException("Unknown compatibility: " + compatibility);
            }
        }
        return new SchemaPairCompatibility(compatibility, reader, writer, message);
    }

    public static boolean schemaNameEquals(Schema reader, Schema writer) {
        if (SchemaCompatibility.objectsEqual(reader.getName(), writer.getName())) {
            return true;
        }
        return reader.getAliases().contains(writer.getFullName());
    }

    public static Schema.Field lookupWriterField(Schema writerSchema, Schema.Field readerField) {
        assert (writerSchema.getType() == Schema.Type.RECORD);
        ArrayList<Schema.Field> writerFields = new ArrayList<Schema.Field>();
        Schema.Field direct = writerSchema.getField(readerField.name());
        if (direct != null) {
            writerFields.add(direct);
        }
        for (String readerFieldAliasName : readerField.aliases()) {
            Schema.Field writerField = writerSchema.getField(readerFieldAliasName);
            if (writerField == null) continue;
            writerFields.add(writerField);
        }
        switch (writerFields.size()) {
            case 0: {
                return null;
            }
            case 1: {
                return (Schema.Field)writerFields.get(0);
            }
        }
        throw new AvroRuntimeException(String.format("Reader record field %s matches multiple fields in writer record schema %s", readerField, writerSchema));
    }

    private static boolean objectsEqual(Object obj1, Object obj2) {
        return Objects.equals(obj1, obj2);
    }

    private static List<String> asList(Deque<String> deque) {
        ArrayList<String> list = new ArrayList<String>(deque);
        Collections.reverse(list);
        return Collections.unmodifiableList(list);
    }

    public static final class SchemaPairCompatibility {
        private final SchemaCompatibilityResult mResult;
        private final Schema mReader;
        private final Schema mWriter;
        private final String mDescription;

        public SchemaPairCompatibility(SchemaCompatibilityResult result, Schema reader, Schema writer, String description) {
            this.mResult = result;
            this.mReader = reader;
            this.mWriter = writer;
            this.mDescription = description;
        }

        public SchemaCompatibilityType getType() {
            return this.mResult.getCompatibility();
        }

        public SchemaCompatibilityResult getResult() {
            return this.mResult;
        }

        public Schema getReader() {
            return this.mReader;
        }

        public Schema getWriter() {
            return this.mWriter;
        }

        public String getDescription() {
            return this.mDescription;
        }

        public String toString() {
            return String.format("SchemaPairCompatibility{result:%s, readerSchema:%s, writerSchema:%s, description:%s}", this.mResult, this.mReader, this.mWriter, this.mDescription);
        }

        public boolean equals(Object other) {
            if (other instanceof SchemaPairCompatibility) {
                SchemaPairCompatibility result = (SchemaPairCompatibility)other;
                return SchemaCompatibility.objectsEqual(result.mResult, this.mResult) && SchemaCompatibility.objectsEqual(result.mReader, this.mReader) && SchemaCompatibility.objectsEqual(result.mWriter, this.mWriter) && SchemaCompatibility.objectsEqual(result.mDescription, this.mDescription);
            }
            return false;
        }

        public int hashCode() {
            return Arrays.hashCode(new Object[]{this.mResult, this.mReader, this.mWriter, this.mDescription});
        }
    }

    public static final class Incompatibility {
        private final SchemaIncompatibilityType mType;
        private final Schema mReaderFragment;
        private final Schema mWriterFragment;
        private final String mMessage;
        private final List<String> mLocation;

        Incompatibility(SchemaIncompatibilityType type, Schema readerFragment, Schema writerFragment, String message, List<String> location) {
            this.mType = type;
            this.mReaderFragment = readerFragment;
            this.mWriterFragment = writerFragment;
            this.mMessage = message;
            this.mLocation = location;
        }

        public SchemaIncompatibilityType getType() {
            return this.mType;
        }

        public Schema getReaderFragment() {
            return this.mReaderFragment;
        }

        public Schema getWriterFragment() {
            return this.mWriterFragment;
        }

        public String getMessage() {
            return this.mMessage;
        }

        public String getLocation() {
            StringBuilder s = new StringBuilder("/");
            boolean first = true;
            for (String coordinate : this.mLocation.subList(1, this.mLocation.size())) {
                if (first) {
                    first = false;
                } else {
                    s.append('/');
                }
                s.append(coordinate.replace("~", "~0").replace("/", "~1"));
            }
            return s.toString();
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + (this.mType == null ? 0 : this.mType.hashCode());
            result = 31 * result + (this.mReaderFragment == null ? 0 : this.mReaderFragment.hashCode());
            result = 31 * result + (this.mWriterFragment == null ? 0 : this.mWriterFragment.hashCode());
            result = 31 * result + (this.mMessage == null ? 0 : this.mMessage.hashCode());
            result = 31 * result + (this.mLocation == null ? 0 : this.mLocation.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            Incompatibility other = (Incompatibility)obj;
            if (this.mType != other.mType) {
                return false;
            }
            if (this.mReaderFragment == null ? other.mReaderFragment != null : !this.mReaderFragment.equals(other.mReaderFragment)) {
                return false;
            }
            if (this.mWriterFragment == null ? other.mWriterFragment != null : !this.mWriterFragment.equals(other.mWriterFragment)) {
                return false;
            }
            if (this.mMessage == null ? other.mMessage != null : !this.mMessage.equals(other.mMessage)) {
                return false;
            }
            if (this.mLocation == null) {
                return other.mLocation == null;
            }
            return this.mLocation.equals(other.mLocation);
        }

        public String toString() {
            return String.format("Incompatibility{type:%s, location:%s, message:%s, reader:%s, writer:%s}", new Object[]{this.mType, this.getLocation(), this.mMessage, this.mReaderFragment, this.mWriterFragment});
        }
    }

    public static final class SchemaCompatibilityResult {
        private final SchemaCompatibilityType mCompatibilityType;
        private final List<Incompatibility> mIncompatibilities;
        private static final SchemaCompatibilityResult COMPATIBLE = new SchemaCompatibilityResult(SchemaCompatibilityType.COMPATIBLE, Collections.emptyList());
        private static final SchemaCompatibilityResult RECURSION_IN_PROGRESS = new SchemaCompatibilityResult(SchemaCompatibilityType.RECURSION_IN_PROGRESS, Collections.emptyList());

        public SchemaCompatibilityResult mergedWith(SchemaCompatibilityResult toMerge) {
            ArrayList<Incompatibility> mergedIncompatibilities = new ArrayList<Incompatibility>(this.mIncompatibilities);
            mergedIncompatibilities.addAll(toMerge.getIncompatibilities());
            SchemaCompatibilityType compatibilityType = this.mCompatibilityType == SchemaCompatibilityType.COMPATIBLE ? toMerge.mCompatibilityType : SchemaCompatibilityType.INCOMPATIBLE;
            return new SchemaCompatibilityResult(compatibilityType, mergedIncompatibilities);
        }

        private SchemaCompatibilityResult(SchemaCompatibilityType compatibilityType, List<Incompatibility> incompatibilities) {
            this.mCompatibilityType = compatibilityType;
            this.mIncompatibilities = incompatibilities;
        }

        public static SchemaCompatibilityResult compatible() {
            return COMPATIBLE;
        }

        public static SchemaCompatibilityResult recursionInProgress() {
            return RECURSION_IN_PROGRESS;
        }

        public static SchemaCompatibilityResult incompatible(SchemaIncompatibilityType incompatibilityType, Schema readerFragment, Schema writerFragment, String message, List<String> location) {
            Incompatibility incompatibility = new Incompatibility(incompatibilityType, readerFragment, writerFragment, message, location);
            return new SchemaCompatibilityResult(SchemaCompatibilityType.INCOMPATIBLE, Collections.singletonList(incompatibility));
        }

        public SchemaCompatibilityType getCompatibility() {
            return this.mCompatibilityType;
        }

        public List<Incompatibility> getIncompatibilities() {
            return this.mIncompatibilities;
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + (this.mCompatibilityType == null ? 0 : this.mCompatibilityType.hashCode());
            result = 31 * result + (this.mIncompatibilities == null ? 0 : this.mIncompatibilities.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            SchemaCompatibilityResult other = (SchemaCompatibilityResult)obj;
            if (this.mIncompatibilities == null ? other.mIncompatibilities != null : !this.mIncompatibilities.equals(other.mIncompatibilities)) {
                return false;
            }
            return this.mCompatibilityType == other.mCompatibilityType;
        }

        public String toString() {
            return String.format("SchemaCompatibilityResult{compatibility:%s, incompatibilities:%s}", new Object[]{this.mCompatibilityType, this.mIncompatibilities});
        }
    }

    public static enum SchemaIncompatibilityType {
        NAME_MISMATCH,
        FIXED_SIZE_MISMATCH,
        MISSING_ENUM_SYMBOLS,
        READER_FIELD_MISSING_DEFAULT_VALUE,
        TYPE_MISMATCH,
        MISSING_UNION_BRANCH;

    }

    public static enum SchemaCompatibilityType {
        COMPATIBLE,
        INCOMPATIBLE,
        RECURSION_IN_PROGRESS;

    }

    private static final class ReaderWriterCompatibilityChecker {
        private static final String ROOT_REFERENCE_TOKEN = "";
        private final Map<ReaderWriter, SchemaCompatibilityResult> mMemoizeMap = new HashMap<ReaderWriter, SchemaCompatibilityResult>();

        private ReaderWriterCompatibilityChecker() {
        }

        public SchemaCompatibilityResult getCompatibility(Schema reader, Schema writer) {
            ArrayDeque<String> location = new ArrayDeque<String>();
            return this.getCompatibility(ROOT_REFERENCE_TOKEN, reader, writer, location);
        }

        private SchemaCompatibilityResult getCompatibility(String referenceToken, Schema reader, Schema writer, Deque<String> location) {
            location.addFirst(referenceToken);
            LOG.debug("Checking compatibility of reader {} with writer {}", (Object)reader, (Object)writer);
            ReaderWriter pair = new ReaderWriter(reader, writer);
            SchemaCompatibilityResult result = this.mMemoizeMap.get(pair);
            if (result != null) {
                if (result.getCompatibility() == SchemaCompatibilityType.RECURSION_IN_PROGRESS) {
                    result = SchemaCompatibilityResult.compatible();
                }
            } else {
                this.mMemoizeMap.put(pair, SchemaCompatibilityResult.recursionInProgress());
                result = this.calculateCompatibility(reader, writer, location);
                this.mMemoizeMap.put(pair, result);
            }
            location.removeFirst();
            return result;
        }

        private SchemaCompatibilityResult calculateCompatibility(Schema reader, Schema writer, Deque<String> location) {
            assert (reader != null);
            assert (writer != null);
            SchemaCompatibilityResult result = SchemaCompatibilityResult.compatible();
            if (reader.getType() == writer.getType()) {
                switch (reader.getType()) {
                    case NULL: 
                    case BOOLEAN: 
                    case INT: 
                    case LONG: 
                    case FLOAT: 
                    case DOUBLE: 
                    case BYTES: 
                    case STRING: {
                        return result;
                    }
                    case ARRAY: {
                        return result.mergedWith(this.getCompatibility("items", reader.getElementType(), writer.getElementType(), location));
                    }
                    case MAP: {
                        return result.mergedWith(this.getCompatibility("values", reader.getValueType(), writer.getValueType(), location));
                    }
                    case FIXED: {
                        result = result.mergedWith(this.checkSchemaNames(reader, writer, location));
                        return result.mergedWith(this.checkFixedSize(reader, writer, location));
                    }
                    case ENUM: {
                        result = result.mergedWith(this.checkSchemaNames(reader, writer, location));
                        return result.mergedWith(this.checkReaderEnumContainsAllWriterEnumSymbols(reader, writer, location));
                    }
                    case RECORD: {
                        result = result.mergedWith(this.checkSchemaNames(reader, writer, location));
                        return result.mergedWith(this.checkReaderWriterRecordFields(reader, writer, location));
                    }
                    case UNION: {
                        int i = 0;
                        for (Schema writerBranch : writer.getTypes()) {
                            location.addFirst(Integer.toString(i));
                            SchemaCompatibilityResult compatibility = this.getCompatibility(reader, writerBranch);
                            if (compatibility.getCompatibility() == SchemaCompatibilityType.INCOMPATIBLE) {
                                String message = String.format("reader union lacking writer type: %s", new Object[]{writerBranch.getType()});
                                result = result.mergedWith(SchemaCompatibilityResult.incompatible(SchemaIncompatibilityType.MISSING_UNION_BRANCH, reader, writer, message, SchemaCompatibility.asList(location)));
                            }
                            location.removeFirst();
                            ++i;
                        }
                        return result;
                    }
                }
                throw new AvroRuntimeException("Unknown schema type: " + (Object)((Object)reader.getType()));
            }
            if (writer.getType() == Schema.Type.UNION) {
                for (Schema s : writer.getTypes()) {
                    result = result.mergedWith(this.getCompatibility(reader, s));
                }
                return result;
            }
            switch (reader.getType()) {
                case NULL: {
                    return result.mergedWith(this.typeMismatch(reader, writer, location));
                }
                case BOOLEAN: {
                    return result.mergedWith(this.typeMismatch(reader, writer, location));
                }
                case INT: {
                    return result.mergedWith(this.typeMismatch(reader, writer, location));
                }
                case LONG: {
                    return writer.getType() == Schema.Type.INT ? result : result.mergedWith(this.typeMismatch(reader, writer, location));
                }
                case FLOAT: {
                    return writer.getType() == Schema.Type.INT || writer.getType() == Schema.Type.LONG ? result : result.mergedWith(this.typeMismatch(reader, writer, location));
                }
                case DOUBLE: {
                    return writer.getType() == Schema.Type.INT || writer.getType() == Schema.Type.LONG || writer.getType() == Schema.Type.FLOAT ? result : result.mergedWith(this.typeMismatch(reader, writer, location));
                }
                case BYTES: {
                    return writer.getType() == Schema.Type.STRING ? result : result.mergedWith(this.typeMismatch(reader, writer, location));
                }
                case STRING: {
                    return writer.getType() == Schema.Type.BYTES ? result : result.mergedWith(this.typeMismatch(reader, writer, location));
                }
                case ARRAY: {
                    return result.mergedWith(this.typeMismatch(reader, writer, location));
                }
                case MAP: {
                    return result.mergedWith(this.typeMismatch(reader, writer, location));
                }
                case FIXED: {
                    return result.mergedWith(this.typeMismatch(reader, writer, location));
                }
                case ENUM: {
                    return result.mergedWith(this.typeMismatch(reader, writer, location));
                }
                case RECORD: {
                    return result.mergedWith(this.typeMismatch(reader, writer, location));
                }
                case UNION: {
                    for (Schema readerBranch : reader.getTypes()) {
                        SchemaCompatibilityResult compatibility = this.getCompatibility(readerBranch, writer);
                        if (compatibility.getCompatibility() != SchemaCompatibilityType.COMPATIBLE) continue;
                        return result;
                    }
                    String message = String.format("reader union lacking writer type: %s", new Object[]{writer.getType()});
                    return result.mergedWith(SchemaCompatibilityResult.incompatible(SchemaIncompatibilityType.MISSING_UNION_BRANCH, reader, writer, message, SchemaCompatibility.asList(location)));
                }
            }
            throw new AvroRuntimeException("Unknown schema type: " + (Object)((Object)reader.getType()));
        }

        private SchemaCompatibilityResult checkReaderWriterRecordFields(Schema reader, Schema writer, Deque<String> location) {
            SchemaCompatibilityResult result = SchemaCompatibilityResult.compatible();
            location.addFirst("fields");
            for (Schema.Field readerField : reader.getFields()) {
                location.addFirst(Integer.toString(readerField.pos()));
                Schema.Field writerField = SchemaCompatibility.lookupWriterField(writer, readerField);
                if (writerField == null) {
                    if (!readerField.hasDefaultValue()) {
                        result = readerField.schema().getType() == Schema.Type.ENUM && readerField.schema().getEnumDefault() != null ? result.mergedWith(this.getCompatibility("type", readerField.schema(), writer, location)) : result.mergedWith(SchemaCompatibilityResult.incompatible(SchemaIncompatibilityType.READER_FIELD_MISSING_DEFAULT_VALUE, reader, writer, readerField.name(), SchemaCompatibility.asList(location)));
                    }
                } else {
                    result = result.mergedWith(this.getCompatibility("type", readerField.schema(), writerField.schema(), location));
                }
                location.removeFirst();
            }
            location.removeFirst();
            return result;
        }

        private SchemaCompatibilityResult checkReaderEnumContainsAllWriterEnumSymbols(Schema reader, Schema writer, Deque<String> location) {
            SchemaCompatibilityResult result = SchemaCompatibilityResult.compatible();
            location.addFirst("symbols");
            TreeSet<String> symbols = new TreeSet<String>(writer.getEnumSymbols());
            symbols.removeAll(reader.getEnumSymbols());
            if (!symbols.isEmpty()) {
                if (reader.getEnumDefault() != null && reader.getEnumSymbols().contains(reader.getEnumDefault())) {
                    symbols.clear();
                    result = SchemaCompatibilityResult.compatible();
                } else {
                    result = SchemaCompatibilityResult.incompatible(SchemaIncompatibilityType.MISSING_ENUM_SYMBOLS, reader, writer, ((Object)symbols).toString(), SchemaCompatibility.asList(location));
                }
            }
            location.removeFirst();
            return result;
        }

        private SchemaCompatibilityResult checkFixedSize(Schema reader, Schema writer, Deque<String> location) {
            SchemaCompatibilityResult result = SchemaCompatibilityResult.compatible();
            location.addFirst("size");
            int actual = reader.getFixedSize();
            int expected = writer.getFixedSize();
            if (actual != expected) {
                String message = String.format("expected: %d, found: %d", expected, actual);
                result = SchemaCompatibilityResult.incompatible(SchemaIncompatibilityType.FIXED_SIZE_MISMATCH, reader, writer, message, SchemaCompatibility.asList(location));
            }
            location.removeFirst();
            return result;
        }

        private SchemaCompatibilityResult checkSchemaNames(Schema reader, Schema writer, Deque<String> location) {
            SchemaCompatibilityResult result = SchemaCompatibilityResult.compatible();
            location.addFirst("name");
            if (!SchemaCompatibility.schemaNameEquals(reader, writer)) {
                String message = String.format("expected: %s", writer.getFullName());
                result = SchemaCompatibilityResult.incompatible(SchemaIncompatibilityType.NAME_MISMATCH, reader, writer, message, SchemaCompatibility.asList(location));
            }
            location.removeFirst();
            return result;
        }

        private SchemaCompatibilityResult typeMismatch(Schema reader, Schema writer, Deque<String> location) {
            String message = String.format("reader type: %s not compatible with writer type: %s", new Object[]{reader.getType(), writer.getType()});
            return SchemaCompatibilityResult.incompatible(SchemaIncompatibilityType.TYPE_MISMATCH, reader, writer, message, SchemaCompatibility.asList(location));
        }
    }

    private static final class ReaderWriter {
        private final Schema mReader;
        private final Schema mWriter;

        public ReaderWriter(Schema reader, Schema writer) {
            this.mReader = reader;
            this.mWriter = writer;
        }

        public int hashCode() {
            return System.identityHashCode(this.mReader) ^ System.identityHashCode(this.mWriter);
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof ReaderWriter)) {
                return false;
            }
            ReaderWriter that = (ReaderWriter)obj;
            return this.mReader == that.mReader && this.mWriter == that.mWriter;
        }

        public String toString() {
            return String.format("ReaderWriter{reader:%s, writer:%s}", this.mReader, this.mWriter);
        }
    }
}

