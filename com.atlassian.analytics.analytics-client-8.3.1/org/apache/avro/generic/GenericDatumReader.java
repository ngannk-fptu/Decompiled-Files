/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.generic;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Conversion;
import org.apache.avro.Conversions;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericArray;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericFixed;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.ResolvingDecoder;
import org.apache.avro.util.Utf8;
import org.apache.avro.util.WeakIdentityHashMap;
import org.apache.avro.util.internal.ThreadLocalWithInitial;

public class GenericDatumReader<D>
implements DatumReader<D> {
    private final GenericData data;
    private Schema actual;
    private Schema expected;
    private DatumReader<D> fastDatumReader = null;
    private ResolvingDecoder creatorResolver = null;
    private final Thread creator;
    private static final ThreadLocal<Map<Schema, Map<Schema, ResolvingDecoder>>> RESOLVER_CACHE = ThreadLocalWithInitial.of(WeakIdentityHashMap::new);
    private final ReaderCache readerCache = new ReaderCache(this::findStringClass);

    public GenericDatumReader() {
        this(null, null, GenericData.get());
    }

    public GenericDatumReader(Schema schema) {
        this(schema, schema, GenericData.get());
    }

    public GenericDatumReader(Schema writer, Schema reader) {
        this(writer, reader, GenericData.get());
    }

    public GenericDatumReader(Schema writer, Schema reader, GenericData data) {
        this(data);
        this.actual = writer;
        this.expected = reader;
    }

    protected GenericDatumReader(GenericData data) {
        this.data = data;
        this.creator = Thread.currentThread();
    }

    public GenericData getData() {
        return this.data;
    }

    public Schema getSchema() {
        return this.actual;
    }

    @Override
    public void setSchema(Schema writer) {
        this.actual = writer;
        if (this.expected == null) {
            this.expected = this.actual;
        }
        this.creatorResolver = null;
        this.fastDatumReader = null;
    }

    public Schema getExpected() {
        return this.expected;
    }

    public void setExpected(Schema reader) {
        this.expected = reader;
        this.creatorResolver = null;
    }

    protected final ResolvingDecoder getResolver(Schema actual, Schema expected) throws IOException {
        ResolvingDecoder resolver;
        Thread currThread = Thread.currentThread();
        if (currThread == this.creator && this.creatorResolver != null) {
            return this.creatorResolver;
        }
        Map<Schema, ResolvingDecoder> cache = RESOLVER_CACHE.get().get(actual);
        if (cache == null) {
            cache = new WeakIdentityHashMap<Schema, ResolvingDecoder>();
            RESOLVER_CACHE.get().put(actual, cache);
        }
        if ((resolver = cache.get(expected)) == null) {
            resolver = DecoderFactory.get().resolvingDecoder(Schema.applyAliases(actual, expected), expected, null);
            cache.put(expected, resolver);
        }
        if (currThread == this.creator) {
            this.creatorResolver = resolver;
        }
        return resolver;
    }

    @Override
    public D read(D reuse, Decoder in) throws IOException {
        if (this.data.isFastReaderEnabled()) {
            if (this.fastDatumReader == null) {
                this.fastDatumReader = this.data.getFastReaderBuilder().createDatumReader(this.actual, this.expected);
            }
            return this.fastDatumReader.read(reuse, in);
        }
        ResolvingDecoder resolver = this.getResolver(this.actual, this.expected);
        resolver.configure(in);
        Object result = this.read(reuse, this.expected, resolver);
        resolver.drain();
        return (D)result;
    }

    protected Object read(Object old, Schema expected, ResolvingDecoder in) throws IOException {
        Conversion conversion;
        Object datum = this.readWithoutConversion(old, expected, in);
        LogicalType logicalType = expected.getLogicalType();
        if (logicalType != null && (conversion = this.getData().getConversionFor(logicalType)) != null) {
            return this.convert(datum, expected, logicalType, conversion);
        }
        return datum;
    }

    protected Object readWithConversion(Object old, Schema expected, LogicalType logicalType, Conversion<?> conversion, ResolvingDecoder in) throws IOException {
        return this.convert(this.readWithoutConversion(old, expected, in), expected, logicalType, conversion);
    }

    protected Object readWithoutConversion(Object old, Schema expected, ResolvingDecoder in) throws IOException {
        switch (expected.getType()) {
            case RECORD: {
                return this.readRecord(old, expected, in);
            }
            case ENUM: {
                return this.readEnum(expected, in);
            }
            case ARRAY: {
                return this.readArray(old, expected, in);
            }
            case MAP: {
                return this.readMap(old, expected, in);
            }
            case UNION: {
                return this.read(old, expected.getTypes().get(in.readIndex()), in);
            }
            case FIXED: {
                return this.readFixed(old, expected, in);
            }
            case STRING: {
                return this.readString(old, expected, in);
            }
            case BYTES: {
                return this.readBytes(old, expected, in);
            }
            case INT: {
                return this.readInt(old, expected, in);
            }
            case LONG: {
                return in.readLong();
            }
            case FLOAT: {
                return Float.valueOf(in.readFloat());
            }
            case DOUBLE: {
                return in.readDouble();
            }
            case BOOLEAN: {
                return in.readBoolean();
            }
            case NULL: {
                in.readNull();
                return null;
            }
        }
        throw new AvroRuntimeException("Unknown type: " + expected);
    }

    protected Object convert(Object datum, Schema schema, LogicalType type, Conversion<?> conversion) {
        return Conversions.convertToLogicalType(datum, schema, type, conversion);
    }

    protected Object readRecord(Object old, Schema expected, ResolvingDecoder in) throws IOException {
        Object record = this.data.newRecord(old, expected);
        Object state = this.data.getRecordState(record, expected);
        for (Schema.Field field : in.readFieldOrder()) {
            int pos = field.pos();
            String name = field.name();
            Object oldDatum = null;
            if (old != null) {
                oldDatum = this.data.getField(record, name, pos, state);
            }
            this.readField(record, field, oldDatum, in, state);
        }
        return record;
    }

    protected void readField(Object record, Schema.Field field, Object oldDatum, ResolvingDecoder in, Object state) throws IOException {
        this.data.setField(record, field.name(), field.pos(), this.read(oldDatum, field.schema(), in), state);
    }

    protected Object readEnum(Schema expected, Decoder in) throws IOException {
        return this.createEnum(expected.getEnumSymbols().get(in.readEnum()), expected);
    }

    protected Object createEnum(String symbol, Schema schema) {
        return this.data.createEnum(symbol, schema);
    }

    protected Object readArray(Object old, Schema expected, ResolvingDecoder in) throws IOException {
        Schema expectedType = expected.getElementType();
        long l = in.readArrayStart();
        long base = 0L;
        if (l > 0L) {
            LogicalType logicalType = expectedType.getLogicalType();
            Conversion conversion = this.getData().getConversionFor(logicalType);
            Object array = this.newArray(old, (int)l, expected);
            do {
                long i;
                if (logicalType != null && conversion != null) {
                    for (i = 0L; i < l; ++i) {
                        this.addToArray(array, base + i, this.readWithConversion(this.peekArray(array), expectedType, logicalType, conversion, in));
                    }
                } else {
                    for (i = 0L; i < l; ++i) {
                        this.addToArray(array, base + i, this.readWithoutConversion(this.peekArray(array), expectedType, in));
                    }
                }
                base += l;
            } while ((l = in.arrayNext()) > 0L);
            return this.pruneArray(array);
        }
        return this.pruneArray(this.newArray(old, 0, expected));
    }

    private Object pruneArray(Object object) {
        if (object instanceof GenericArray) {
            ((GenericArray)object).prune();
        }
        return object;
    }

    protected Object peekArray(Object array) {
        return array instanceof GenericArray ? ((GenericArray)array).peek() : null;
    }

    protected void addToArray(Object array, long pos, Object e) {
        ((Collection)array).add(e);
    }

    protected Object readMap(Object old, Schema expected, ResolvingDecoder in) throws IOException {
        Schema eValue = expected.getValueType();
        long l = in.readMapStart();
        LogicalType logicalType = eValue.getLogicalType();
        Conversion conversion = this.getData().getConversionFor(logicalType);
        Object map = this.newMap(old, (int)l);
        if (l > 0L) {
            do {
                int i;
                if (logicalType != null && conversion != null) {
                    i = 0;
                    while ((long)i < l) {
                        this.addToMap(map, this.readMapKey(null, expected, in), this.readWithConversion(null, eValue, logicalType, conversion, in));
                        ++i;
                    }
                } else {
                    i = 0;
                    while ((long)i < l) {
                        this.addToMap(map, this.readMapKey(null, expected, in), this.readWithoutConversion(null, eValue, in));
                        ++i;
                    }
                }
            } while ((l = in.mapNext()) > 0L);
        }
        return map;
    }

    protected Object readMapKey(Object old, Schema expected, Decoder in) throws IOException {
        return this.readString(old, expected, in);
    }

    protected void addToMap(Object map, Object key, Object value) {
        ((Map)map).put(key, value);
    }

    protected Object readFixed(Object old, Schema expected, Decoder in) throws IOException {
        GenericFixed fixed = (GenericFixed)this.data.createFixed(old, expected);
        in.readFixed(fixed.bytes(), 0, expected.getFixedSize());
        return fixed;
    }

    @Deprecated
    protected Object createFixed(Object old, Schema schema) {
        return this.data.createFixed(old, schema);
    }

    @Deprecated
    protected Object createFixed(Object old, byte[] bytes, Schema schema) {
        return this.data.createFixed(old, bytes, schema);
    }

    @Deprecated
    protected Object newRecord(Object old, Schema schema) {
        return this.data.newRecord(old, schema);
    }

    protected Object newArray(Object old, int size, Schema schema) {
        return this.data.newArray(old, size, schema);
    }

    protected Object newMap(Object old, int size) {
        return this.data.newMap(old, size);
    }

    protected Object readString(Object old, Schema expected, Decoder in) throws IOException {
        Class stringClass = this.getReaderCache().getStringClass(expected);
        if (stringClass == String.class) {
            return in.readString();
        }
        if (stringClass == CharSequence.class) {
            return this.readString(old, in);
        }
        return this.newInstanceFromString(stringClass, in.readString());
    }

    protected Object readString(Object old, Decoder in) throws IOException {
        return in.readString(old instanceof Utf8 ? (Utf8)old : null);
    }

    protected Object createString(String value) {
        return new Utf8(value);
    }

    protected Class findStringClass(Schema schema) {
        String name = schema.getProp("avro.java.string");
        if (name == null) {
            return CharSequence.class;
        }
        switch (GenericData.StringType.valueOf(name)) {
            case String: {
                return String.class;
            }
        }
        return CharSequence.class;
    }

    ReaderCache getReaderCache() {
        return this.readerCache;
    }

    protected Object newInstanceFromString(Class c, String s) {
        return this.getReaderCache().newInstanceFromString(c, s);
    }

    protected Object readBytes(Object old, Schema s, Decoder in) throws IOException {
        return this.readBytes(old, in);
    }

    protected Object readBytes(Object old, Decoder in) throws IOException {
        return in.readBytes(old instanceof ByteBuffer ? (ByteBuffer)old : null);
    }

    protected Object readInt(Object old, Schema expected, Decoder in) throws IOException {
        return in.readInt();
    }

    protected Object createBytes(byte[] value) {
        return ByteBuffer.wrap(value);
    }

    public static void skip(Schema schema, Decoder in) throws IOException {
        switch (schema.getType()) {
            case RECORD: {
                for (Schema.Field field : schema.getFields()) {
                    GenericDatumReader.skip(field.schema(), in);
                }
                break;
            }
            case ENUM: {
                in.readEnum();
                break;
            }
            case ARRAY: {
                Schema elementType = schema.getElementType();
                long l = in.skipArray();
                while (l > 0L) {
                    for (long i = 0L; i < l; ++i) {
                        GenericDatumReader.skip(elementType, in);
                    }
                    l = in.skipArray();
                }
                break;
            }
            case MAP: {
                Schema value = schema.getValueType();
                long l = in.skipMap();
                while (l > 0L) {
                    for (long i = 0L; i < l; ++i) {
                        in.skipString();
                        GenericDatumReader.skip(value, in);
                    }
                    l = in.skipMap();
                }
                break;
            }
            case UNION: {
                GenericDatumReader.skip(schema.getTypes().get(in.readIndex()), in);
                break;
            }
            case FIXED: {
                in.skipFixed(schema.getFixedSize());
                break;
            }
            case STRING: {
                in.skipString();
                break;
            }
            case BYTES: {
                in.skipBytes();
                break;
            }
            case INT: {
                in.readInt();
                break;
            }
            case LONG: {
                in.readLong();
                break;
            }
            case FLOAT: {
                in.readFloat();
                break;
            }
            case DOUBLE: {
                in.readDouble();
                break;
            }
            case BOOLEAN: {
                in.readBoolean();
                break;
            }
            case NULL: {
                in.readNull();
                break;
            }
            default: {
                throw new RuntimeException("Unknown type: " + schema);
            }
        }
    }

    static class ReaderCache {
        private final Map<IdentitySchemaKey, Class> stringClassCache = new ConcurrentHashMap<IdentitySchemaKey, Class>();
        private final Map<Class, Function<String, Object>> stringCtorCache = new ConcurrentHashMap<Class, Function<String, Object>>();
        private final Function<Schema, Class> findStringClass;

        public ReaderCache(Function<Schema, Class> findStringClass) {
            this.findStringClass = findStringClass;
        }

        public Object newInstanceFromString(Class c, String s) {
            Function ctor = this.stringCtorCache.computeIfAbsent(c, this::buildFunction);
            return ctor.apply(s);
        }

        private Function<String, Object> buildFunction(Class c) {
            Constructor ctor;
            try {
                ctor = c.getDeclaredConstructor(String.class);
            }
            catch (NoSuchMethodException e) {
                throw new AvroRuntimeException(e);
            }
            ctor.setAccessible(true);
            return s -> {
                try {
                    return ctor.newInstance(s);
                }
                catch (ReflectiveOperationException e) {
                    throw new AvroRuntimeException(e);
                }
            };
        }

        public Class getStringClass(Schema s) {
            IdentitySchemaKey key = new IdentitySchemaKey(s);
            return this.stringClassCache.computeIfAbsent(key, k -> this.findStringClass.apply(((IdentitySchemaKey)k).schema));
        }
    }

    private static final class IdentitySchemaKey {
        private final Schema schema;
        private final int hashcode;

        public IdentitySchemaKey(Schema schema) {
            this.schema = schema;
            this.hashcode = System.identityHashCode(schema);
        }

        public int hashCode() {
            return this.hashcode;
        }

        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof IdentitySchemaKey)) {
                return false;
            }
            IdentitySchemaKey key = (IdentitySchemaKey)obj;
            return this == key || this.schema == key.schema;
        }
    }
}

