/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntFunction;
import org.apache.avro.AvroTypeException;
import org.apache.avro.Conversion;
import org.apache.avro.Conversions;
import org.apache.avro.Resolver;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericArray;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericEnumSymbol;
import org.apache.avro.generic.GenericFixed;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.parsing.ResolvingGrammarGenerator;
import org.apache.avro.reflect.ReflectionUtil;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.avro.util.Utf8;
import org.apache.avro.util.WeakIdentityHashMap;
import org.apache.avro.util.internal.Accessor;

public class FastReaderBuilder {
    private final GenericData data;
    private final Map<Schema, Map<Schema, RecordReader>> readerCache = Collections.synchronizedMap(new WeakIdentityHashMap());
    private boolean keyClassEnabled = true;
    private boolean classPropEnabled = true;

    public static FastReaderBuilder get() {
        return new FastReaderBuilder(GenericData.get());
    }

    public static FastReaderBuilder getSpecific() {
        return new FastReaderBuilder(SpecificData.get());
    }

    public static boolean isSupportedData(GenericData data) {
        return data.getClass() == GenericData.class || data.getClass() == SpecificData.class;
    }

    public FastReaderBuilder(GenericData parentData) {
        this.data = parentData;
    }

    public FastReaderBuilder withKeyClassEnabled(boolean enabled) {
        this.keyClassEnabled = enabled;
        return this;
    }

    public boolean isKeyClassEnabled() {
        return this.keyClassEnabled;
    }

    public FastReaderBuilder withClassPropEnabled(boolean enabled) {
        this.classPropEnabled = enabled;
        return this;
    }

    public boolean isClassPropEnabled() {
        return this.classPropEnabled;
    }

    public <D> DatumReader<D> createDatumReader(Schema schema) throws IOException {
        return this.createDatumReader(schema, schema);
    }

    public <D> DatumReader<D> createDatumReader(Schema writerSchema, Schema readerSchema) throws IOException {
        Schema resolvedWriterSchema = Schema.applyAliases(writerSchema, readerSchema);
        return this.getReaderFor(readerSchema, resolvedWriterSchema);
    }

    private FieldReader getReaderFor(Schema readerSchema, Schema writerSchema) throws IOException {
        Resolver.Action resolvedAction = Resolver.resolve(writerSchema, readerSchema, this.data);
        return this.getReaderFor(resolvedAction, null);
    }

    private FieldReader getReaderFor(Resolver.Action action, Conversion<?> explicitConversion) throws IOException {
        FieldReader baseReader = this.getNonConvertedReader(action);
        return this.applyConversions(action.reader, baseReader, explicitConversion);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private RecordReader createRecordReader(Resolver.RecordAdjust action) throws IOException {
        RecordReader recordReader;
        RecordReader recordReader2 = recordReader = this.getRecordReaderFromCache(action.reader, action.writer);
        synchronized (recordReader2) {
            if (recordReader.getInitializationStage() == RecordReader.Stage.NEW) {
                this.initializeRecordReader(recordReader, action);
            }
        }
        return recordReader;
    }

    private RecordReader initializeRecordReader(RecordReader recordReader, Resolver.RecordAdjust action) throws IOException {
        int i;
        recordReader.startInitialization();
        Object testInstance = action.instanceSupplier.newInstance(null, action.reader);
        IntFunction<Conversion<?>> conversionSupplier = this.getConversionSupplier(testInstance);
        ExecutionStep[] readSteps = new ExecutionStep[action.fieldActions.length + action.readerOrder.length - action.firstDefault];
        int fieldCounter = 0;
        for (i = 0; i < action.fieldActions.length; ++i) {
            Resolver.Action fieldAction = action.fieldActions[i];
            if (fieldAction instanceof Resolver.Skip) {
                readSteps[i] = (r, decoder) -> GenericDatumReader.skip(fieldAction.writer, decoder);
                continue;
            }
            Schema.Field readerField = action.readerOrder[fieldCounter++];
            Conversion<?> conversion = conversionSupplier.apply(readerField.pos());
            FieldReader reader = this.getReaderFor(fieldAction, conversion);
            readSteps[i] = this.createFieldSetter(readerField, reader);
        }
        while (i < readSteps.length) {
            readSteps[i] = this.getDefaultingStep(action.readerOrder[fieldCounter++]);
            ++i;
        }
        recordReader.finishInitialization(readSteps, action.reader, action.instanceSupplier);
        return recordReader;
    }

    private ExecutionStep createFieldSetter(Schema.Field field, FieldReader reader) {
        int pos = field.pos();
        if (reader.canReuse()) {
            return (object, decoder) -> {
                IndexedRecord record = (IndexedRecord)object;
                record.put(pos, reader.read(record.get(pos), decoder));
            };
        }
        return (object, decoder) -> {
            IndexedRecord record = (IndexedRecord)object;
            record.put(pos, reader.read((Object)null, decoder));
        };
    }

    private ExecutionStep getDefaultingStep(Schema.Field field) throws IOException {
        Object defaultValue = this.data.getDefaultValue(field);
        if (this.isObjectImmutable(defaultValue)) {
            return this.createFieldSetter(field, (old, d) -> defaultValue);
        }
        if (defaultValue instanceof Utf8) {
            return this.createFieldSetter(field, FastReaderBuilder.reusingReader((old, d) -> this.readUtf8(old, (Utf8)defaultValue)));
        }
        if (defaultValue instanceof List && ((List)defaultValue).isEmpty()) {
            return this.createFieldSetter(field, FastReaderBuilder.reusingReader((old, d) -> this.data.newArray(old, 0, field.schema())));
        }
        if (defaultValue instanceof Map && ((Map)defaultValue).isEmpty()) {
            return this.createFieldSetter(field, FastReaderBuilder.reusingReader((old, d) -> this.data.newMap(old, 0)));
        }
        DatumReader datumReader = this.createDatumReader(field.schema());
        byte[] encoded = this.getEncodedValue(field);
        FieldReader fieldReader = FastReaderBuilder.reusingReader((old, decoder) -> datumReader.read(old, DecoderFactory.get().binaryDecoder(encoded, null)));
        return this.createFieldSetter(field, fieldReader);
    }

    private boolean isObjectImmutable(Object object) {
        return object == null || object instanceof Number || object instanceof String || object instanceof GenericEnumSymbol || object.getClass().isEnum();
    }

    private Utf8 readUtf8(Object reuse, Utf8 newValue) {
        if (reuse instanceof Utf8) {
            Utf8 oldUtf8 = (Utf8)reuse;
            oldUtf8.set(newValue);
            return oldUtf8;
        }
        return new Utf8(newValue);
    }

    private byte[] getEncodedValue(Schema.Field field) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        ResolvingGrammarGenerator.encode(encoder, field.schema(), Accessor.defaultValue(field));
        encoder.flush();
        return out.toByteArray();
    }

    private IntFunction<Conversion<?>> getConversionSupplier(Object record) {
        if (record instanceof SpecificRecordBase) {
            return ((SpecificRecordBase)record)::getConversion;
        }
        return index -> null;
    }

    private RecordReader getRecordReaderFromCache(Schema readerSchema, Schema writerSchema) {
        return this.readerCache.computeIfAbsent(readerSchema, k -> new WeakIdentityHashMap()).computeIfAbsent(writerSchema, k -> new RecordReader());
    }

    private FieldReader applyConversions(Schema readerSchema, FieldReader reader, Conversion<?> explicitConversion) {
        Conversion<Object> conversion = explicitConversion;
        if (conversion == null) {
            if (readerSchema.getLogicalType() == null) {
                return reader;
            }
            conversion = this.data.getConversionFor(readerSchema.getLogicalType());
            if (conversion == null) {
                return reader;
            }
        }
        Conversion<?> finalConversion = conversion;
        return (old, decoder) -> Conversions.convertToLogicalType(reader.read(old, decoder), readerSchema, readerSchema.getLogicalType(), finalConversion);
    }

    private FieldReader getNonConvertedReader(Resolver.Action action) throws IOException {
        switch (action.type) {
            case CONTAINER: {
                switch (action.reader.getType()) {
                    case MAP: {
                        return this.createMapReader(action.reader, (Resolver.Container)action);
                    }
                    case ARRAY: {
                        return this.createArrayReader(action.reader, (Resolver.Container)action);
                    }
                }
                throw new IllegalStateException("Error getting reader for action type " + action.getClass());
            }
            case DO_NOTHING: {
                return this.getReaderForBaseType(action.reader, action.writer);
            }
            case RECORD: {
                return this.createRecordReader((Resolver.RecordAdjust)action);
            }
            case ENUM: {
                return this.createEnumReader((Resolver.EnumAdjust)action);
            }
            case PROMOTE: {
                return this.createPromotingReader((Resolver.Promote)action);
            }
            case WRITER_UNION: {
                return this.createUnionReader((Resolver.WriterUnion)action);
            }
            case READER_UNION: {
                return this.getReaderFor(((Resolver.ReaderUnion)action).actualAction, null);
            }
            case ERROR: {
                return (old, decoder) -> {
                    throw new AvroTypeException(action.toString());
                };
            }
        }
        throw new IllegalStateException("Error getting reader for action type " + action.getClass());
    }

    private FieldReader getReaderForBaseType(Schema readerSchema, Schema writerSchema) throws IOException {
        switch (readerSchema.getType()) {
            case NULL: {
                return (old, decoder) -> {
                    decoder.readNull();
                    return null;
                };
            }
            case BOOLEAN: {
                return (old, decoder) -> decoder.readBoolean();
            }
            case STRING: {
                return this.createStringReader(readerSchema, writerSchema);
            }
            case INT: {
                return (old, decoder) -> decoder.readInt();
            }
            case LONG: {
                return (old, decoder) -> decoder.readLong();
            }
            case FLOAT: {
                return (old, decoder) -> Float.valueOf(decoder.readFloat());
            }
            case DOUBLE: {
                return (old, decoder) -> decoder.readDouble();
            }
            case BYTES: {
                return this.createBytesReader();
            }
            case FIXED: {
                return this.createFixedReader(readerSchema, writerSchema);
            }
        }
        throw new IllegalStateException("Error getting reader for type " + readerSchema.getFullName());
    }

    private FieldReader createPromotingReader(Resolver.Promote promote) throws IOException {
        switch (promote.reader.getType()) {
            case BYTES: {
                return (reuse, decoder) -> ByteBuffer.wrap(decoder.readString(null).getBytes());
            }
            case STRING: {
                return this.createBytesPromotingToStringReader(promote.reader);
            }
            case LONG: {
                return (reuse, decoder) -> (long)decoder.readInt();
            }
            case FLOAT: {
                switch (promote.writer.getType()) {
                    case INT: {
                        return (reuse, decoder) -> Float.valueOf(decoder.readInt());
                    }
                    case LONG: {
                        return (reuse, decoder) -> Float.valueOf(decoder.readLong());
                    }
                }
                break;
            }
            case DOUBLE: {
                switch (promote.writer.getType()) {
                    case INT: {
                        return (reuse, decoder) -> (double)decoder.readInt();
                    }
                    case LONG: {
                        return (reuse, decoder) -> (double)decoder.readLong();
                    }
                    case FLOAT: {
                        return (reuse, decoder) -> (double)decoder.readFloat();
                    }
                }
                break;
            }
        }
        throw new IllegalStateException("No promotion possible for type " + (Object)((Object)promote.writer.getType()) + " to " + (Object)((Object)promote.reader.getType()));
    }

    private FieldReader createStringReader(Schema readerSchema, Schema writerSchema) {
        FieldReader stringReader = this.createSimpleStringReader(readerSchema);
        if (this.isClassPropEnabled()) {
            return this.getTransformingStringReader(readerSchema.getProp("java-class"), stringReader);
        }
        return stringReader;
    }

    private FieldReader createSimpleStringReader(Schema readerSchema) {
        String stringProperty = readerSchema.getProp("avro.java.string");
        if (GenericData.StringType.String.name().equals(stringProperty)) {
            return (old, decoder) -> decoder.readString();
        }
        return (old, decoder) -> decoder.readString(old instanceof Utf8 ? (Utf8)old : null);
    }

    private FieldReader createBytesPromotingToStringReader(Schema readerSchema) {
        String stringProperty = readerSchema.getProp("avro.java.string");
        if (GenericData.StringType.String.name().equals(stringProperty)) {
            return (old, decoder) -> this.getStringFromByteBuffer(decoder.readBytes(null));
        }
        return (old, decoder) -> this.getUtf8FromByteBuffer(old, decoder.readBytes(null));
    }

    private String getStringFromByteBuffer(ByteBuffer buffer) {
        return new String(buffer.array(), buffer.position(), buffer.remaining(), StandardCharsets.UTF_8);
    }

    private Utf8 getUtf8FromByteBuffer(Object old, ByteBuffer buffer) {
        return old instanceof Utf8 ? ((Utf8)old).set(new Utf8(buffer.array())) : new Utf8(buffer.array());
    }

    private FieldReader createUnionReader(Resolver.WriterUnion action) throws IOException {
        FieldReader[] unionReaders = new FieldReader[action.actions.length];
        for (int i = 0; i < action.actions.length; ++i) {
            unionReaders[i] = this.getReaderFor(action.actions[i], null);
        }
        return this.createUnionReader(unionReaders);
    }

    private FieldReader createUnionReader(FieldReader[] unionReaders) {
        return FastReaderBuilder.reusingReader((reuse, decoder) -> {
            int selection = decoder.readIndex();
            return unionReaders[selection].read((Object)null, decoder);
        });
    }

    private FieldReader createMapReader(Schema readerSchema, Resolver.Container action) throws IOException {
        FieldReader keyReader = this.createMapKeyReader(readerSchema);
        FieldReader valueReader = this.getReaderFor(action.elementAction, null);
        return new MapReader(keyReader, valueReader);
    }

    private FieldReader createMapKeyReader(Schema readerSchema) {
        FieldReader stringReader = this.createSimpleStringReader(readerSchema);
        if (this.isKeyClassEnabled()) {
            return this.getTransformingStringReader(readerSchema.getProp("java-key-class"), this.createSimpleStringReader(readerSchema));
        }
        return stringReader;
    }

    private FieldReader getTransformingStringReader(String valueClass, FieldReader stringReader) {
        if (valueClass == null) {
            return stringReader;
        }
        Function transformer = this.findClass(valueClass).map(clazz -> ReflectionUtil.getConstructorAsFunction(String.class, clazz)).orElse(null);
        if (transformer != null) {
            return (old, decoder) -> transformer.apply((String)stringReader.read((Object)null, decoder));
        }
        return stringReader;
    }

    private Optional<Class<?>> findClass(String clazz) {
        try {
            return Optional.of(this.data.getClassLoader().loadClass(clazz));
        }
        catch (ReflectiveOperationException e) {
            return Optional.empty();
        }
    }

    private FieldReader createArrayReader(Schema readerSchema, Resolver.Container action) throws IOException {
        FieldReader elementReader = this.getReaderFor(action.elementAction, null);
        return FastReaderBuilder.reusingReader((reuse, decoder) -> {
            if (reuse instanceof GenericArray) {
                GenericArray reuseArray = (GenericArray)reuse;
                long l = decoder.readArrayStart();
                reuseArray.clear();
                while (l > 0L) {
                    for (long i = 0L; i < l; ++i) {
                        reuseArray.add(elementReader.read(reuseArray.peek(), decoder));
                    }
                    l = decoder.arrayNext();
                }
                return reuseArray;
            }
            long l = decoder.readArrayStart();
            List<Object> array = reuse instanceof List ? (List<Object>)reuse : new GenericData.Array((int)l, readerSchema);
            array.clear();
            while (l > 0L) {
                for (long i = 0L; i < l; ++i) {
                    array.add(elementReader.read((Object)null, decoder));
                }
                l = decoder.arrayNext();
            }
            return array;
        });
    }

    private FieldReader createEnumReader(Resolver.EnumAdjust action) {
        return FastReaderBuilder.reusingReader((reuse, decoder) -> {
            int index = decoder.readEnum();
            Object resultObject = action.values[index];
            if (resultObject == null) {
                throw new AvroTypeException("No match for " + action.writer.getEnumSymbols().get(index));
            }
            return resultObject;
        });
    }

    private FieldReader createFixedReader(Schema readerSchema, Schema writerSchema) {
        return FastReaderBuilder.reusingReader((reuse, decoder) -> {
            GenericFixed fixed = (GenericFixed)this.data.createFixed(reuse, readerSchema);
            decoder.readFixed(fixed.bytes(), 0, readerSchema.getFixedSize());
            return fixed;
        });
    }

    private FieldReader createBytesReader() {
        return FastReaderBuilder.reusingReader((reuse, decoder) -> decoder.readBytes(reuse instanceof ByteBuffer ? (ByteBuffer)reuse : null));
    }

    public static FieldReader reusingReader(ReusingFieldReader reader) {
        return reader;
    }

    public static interface ExecutionStep {
        public void execute(Object var1, Decoder var2) throws IOException;
    }

    public static class MapReader
    implements FieldReader {
        private final FieldReader keyReader;
        private final FieldReader valueReader;

        public MapReader(FieldReader keyReader, FieldReader valueReader) {
            this.keyReader = keyReader;
            this.valueReader = valueReader;
        }

        @Override
        public Object read(Object reuse, Decoder decoder) throws IOException {
            long l = decoder.readMapStart();
            HashMap<Object, Object> targetMap = new HashMap<Object, Object>();
            while (l > 0L) {
                int i = 0;
                while ((long)i < l) {
                    Object key = this.keyReader.read((Object)null, decoder);
                    Object value = this.valueReader.read((Object)null, decoder);
                    targetMap.put(key, value);
                    ++i;
                }
                l = decoder.mapNext();
            }
            return targetMap;
        }
    }

    public static class RecordReader
    implements FieldReader {
        private ExecutionStep[] readSteps;
        private GenericData.InstanceSupplier supplier;
        private Schema schema;
        private Stage stage = Stage.NEW;

        public Stage getInitializationStage() {
            return this.stage;
        }

        public void reset() {
            this.stage = Stage.NEW;
        }

        public void startInitialization() {
            this.stage = Stage.INITIALIZING;
        }

        public void finishInitialization(ExecutionStep[] readSteps, Schema schema, GenericData.InstanceSupplier supp) {
            this.readSteps = readSteps;
            this.schema = schema;
            this.supplier = supp;
            this.stage = Stage.INITIALIZED;
        }

        @Override
        public boolean canReuse() {
            return true;
        }

        @Override
        public Object read(Object reuse, Decoder decoder) throws IOException {
            Object object = this.supplier.newInstance(reuse, this.schema);
            for (ExecutionStep thisStep : this.readSteps) {
                thisStep.execute(object, decoder);
            }
            return object;
        }

        public static enum Stage {
            NEW,
            INITIALIZING,
            INITIALIZED;

        }
    }

    public static interface ReusingFieldReader
    extends FieldReader {
        @Override
        default public boolean canReuse() {
            return true;
        }
    }

    public static interface FieldReader
    extends DatumReader<Object> {
        @Override
        public Object read(Object var1, Decoder var2) throws IOException;

        default public boolean canReuse() {
            return false;
        }

        @Override
        default public void setSchema(Schema schema) {
            throw new UnsupportedOperationException();
        }
    }
}

