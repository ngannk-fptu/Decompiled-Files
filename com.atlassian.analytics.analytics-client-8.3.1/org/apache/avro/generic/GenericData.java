/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.generic;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.temporal.Temporal;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import org.apache.avro.AvroMissingFieldException;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.AvroTypeException;
import org.apache.avro.Conversion;
import org.apache.avro.Conversions;
import org.apache.avro.JsonProperties;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;
import org.apache.avro.UnresolvedUnionException;
import org.apache.avro.generic.GenericArray;
import org.apache.avro.generic.GenericContainer;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericEnumSymbol;
import org.apache.avro.generic.GenericFixed;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.io.BinaryData;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.FastReaderBuilder;
import org.apache.avro.util.Utf8;
import org.apache.avro.util.internal.Accessor;
import org.apache.avro.util.springframework.ConcurrentReferenceHashMap;

public class GenericData {
    private static final GenericData INSTANCE = new GenericData();
    private static final Map<Class<?>, String> PRIMITIVE_DATUM_TYPES = new IdentityHashMap();
    public static final String STRING_PROP = "avro.java.string";
    protected static final String STRING_TYPE_STRING = "String";
    private final ClassLoader classLoader;
    private Map<String, Conversion<?>> conversions = new HashMap();
    private Map<Class<?>, Map<String, Conversion<?>>> conversionsByClass = new IdentityHashMap();
    public static final String FAST_READER_PROP = "org.apache.avro.fastread";
    private boolean fastReaderEnabled = "true".equalsIgnoreCase(System.getProperty("org.apache.avro.fastread"));
    private FastReaderBuilder fastReaderBuilder = null;
    private static final String TOSTRING_CIRCULAR_REFERENCE_ERROR_TEXT = " \">>> CIRCULAR REFERENCE CANNOT BE PUT IN JSON STRING, ABORTING RECURSION <<<\" ";
    private final ConcurrentMap<Schema.Field, Object> defaultValueCache = new ConcurrentReferenceHashMap<Schema.Field, Object>(128, ConcurrentReferenceHashMap.ReferenceType.WEAK);
    private static final Schema STRINGS;

    public static void setStringType(Schema s, StringType stringType) {
        if (stringType == StringType.String) {
            s.addProp(STRING_PROP, STRING_TYPE_STRING);
        }
    }

    public static GenericData get() {
        return INSTANCE;
    }

    public GenericData() {
        this(null);
    }

    public GenericData(ClassLoader classLoader) {
        this.classLoader = classLoader != null ? classLoader : this.getClass().getClassLoader();
        this.loadConversions();
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    private void loadConversions() {
        for (Conversion conversion : ServiceLoader.load(Conversion.class, this.classLoader)) {
            this.addLogicalTypeConversion(conversion);
        }
    }

    public Collection<Conversion<?>> getConversions() {
        return this.conversions.values();
    }

    public void addLogicalTypeConversion(Conversion<?> conversion) {
        this.conversions.put(conversion.getLogicalTypeName(), conversion);
        Class<?> type = conversion.getConvertedType();
        Map conversionsForClass = this.conversionsByClass.computeIfAbsent(type, k -> new LinkedHashMap());
        conversionsForClass.put(conversion.getLogicalTypeName(), conversion);
    }

    public <T> Conversion<T> getConversionByClass(Class<T> datumClass) {
        Map<String, Conversion<?>> conversions = this.conversionsByClass.get(datumClass);
        if (conversions != null) {
            return conversions.values().iterator().next();
        }
        return null;
    }

    public <T> Conversion<T> getConversionByClass(Class<T> datumClass, LogicalType logicalType) {
        Map<String, Conversion<?>> conversions = this.conversionsByClass.get(datumClass);
        if (conversions != null) {
            return conversions.get(logicalType.getName());
        }
        return null;
    }

    public <T> Conversion<T> getConversionFor(LogicalType logicalType) {
        if (logicalType == null) {
            return null;
        }
        return this.conversions.get(logicalType.getName());
    }

    public GenericData setFastReaderEnabled(boolean flag) {
        this.fastReaderEnabled = flag;
        return this;
    }

    public boolean isFastReaderEnabled() {
        return this.fastReaderEnabled && FastReaderBuilder.isSupportedData(this);
    }

    public FastReaderBuilder getFastReaderBuilder() {
        if (this.fastReaderBuilder == null) {
            this.fastReaderBuilder = new FastReaderBuilder(this);
        }
        return this.fastReaderBuilder;
    }

    public DatumReader createDatumReader(Schema schema) {
        return this.createDatumReader(schema, schema);
    }

    public DatumReader createDatumReader(Schema writer, Schema reader) {
        return new GenericDatumReader(writer, reader, this);
    }

    public DatumWriter createDatumWriter(Schema schema) {
        return new GenericDatumWriter(schema, this);
    }

    public boolean validate(Schema schema, Object datum) {
        switch (schema.getType()) {
            case RECORD: {
                if (!this.isRecord(datum)) {
                    return false;
                }
                for (Schema.Field f : schema.getFields()) {
                    if (this.validate(f.schema(), this.getField(datum, f.name(), f.pos()))) continue;
                    return false;
                }
                return true;
            }
            case ENUM: {
                if (!this.isEnum(datum)) {
                    return false;
                }
                return schema.getEnumSymbols().contains(datum.toString());
            }
            case ARRAY: {
                if (!this.isArray(datum)) {
                    return false;
                }
                for (Object element : this.getArrayAsCollection(datum)) {
                    if (this.validate(schema.getElementType(), element)) continue;
                    return false;
                }
                return true;
            }
            case MAP: {
                if (!this.isMap(datum)) {
                    return false;
                }
                Map map = (Map)datum;
                for (Map.Entry entry : map.entrySet()) {
                    if (this.validate(schema.getValueType(), entry.getValue())) continue;
                    return false;
                }
                return true;
            }
            case UNION: {
                try {
                    int i = this.resolveUnion(schema, datum);
                    return this.validate(schema.getTypes().get(i), datum);
                }
                catch (UnresolvedUnionException e) {
                    return false;
                }
            }
            case FIXED: {
                return datum instanceof GenericFixed && ((GenericFixed)datum).bytes().length == schema.getFixedSize();
            }
            case STRING: {
                return this.isString(datum);
            }
            case BYTES: {
                return this.isBytes(datum);
            }
            case INT: {
                return this.isInteger(datum);
            }
            case LONG: {
                return this.isLong(datum);
            }
            case FLOAT: {
                return this.isFloat(datum);
            }
            case DOUBLE: {
                return this.isDouble(datum);
            }
            case BOOLEAN: {
                return this.isBoolean(datum);
            }
            case NULL: {
                return datum == null;
            }
        }
        return false;
    }

    public String toString(Object datum) {
        StringBuilder buffer = new StringBuilder();
        this.toString(datum, buffer, new IdentityHashMap<Object, Object>(128));
        return buffer.toString();
    }

    protected void toString(Object datum, StringBuilder buffer, IdentityHashMap<Object, Object> seenObjects) {
        if (this.isRecord(datum)) {
            if (seenObjects.containsKey(datum)) {
                buffer.append(TOSTRING_CIRCULAR_REFERENCE_ERROR_TEXT);
                return;
            }
            seenObjects.put(datum, datum);
            buffer.append("{");
            int count = 0;
            Schema schema = this.getRecordSchema(datum);
            for (Schema.Field f : schema.getFields()) {
                this.toString(f.name(), buffer, seenObjects);
                buffer.append(": ");
                this.toString(this.getField(datum, f.name(), f.pos()), buffer, seenObjects);
                if (++count >= schema.getFields().size()) continue;
                buffer.append(", ");
            }
            buffer.append("}");
            seenObjects.remove(datum);
        } else if (this.isArray(datum)) {
            if (seenObjects.containsKey(datum)) {
                buffer.append(TOSTRING_CIRCULAR_REFERENCE_ERROR_TEXT);
                return;
            }
            seenObjects.put(datum, datum);
            Collection array = this.getArrayAsCollection(datum);
            buffer.append("[");
            long last = array.size() - 1;
            int i = 0;
            for (Object element : array) {
                this.toString(element, buffer, seenObjects);
                if ((long)i++ >= last) continue;
                buffer.append(", ");
            }
            buffer.append("]");
            seenObjects.remove(datum);
        } else if (this.isMap(datum)) {
            if (seenObjects.containsKey(datum)) {
                buffer.append(TOSTRING_CIRCULAR_REFERENCE_ERROR_TEXT);
                return;
            }
            seenObjects.put(datum, datum);
            buffer.append("{");
            int count = 0;
            Map map = (Map)datum;
            for (Map.Entry entry : map.entrySet()) {
                buffer.append("\"");
                GenericData.writeEscapedString(String.valueOf(entry.getKey()), buffer);
                buffer.append("\": ");
                this.toString(entry.getValue(), buffer, seenObjects);
                if (++count >= map.size()) continue;
                buffer.append(", ");
            }
            buffer.append("}");
            seenObjects.remove(datum);
        } else if (this.isString(datum) || this.isEnum(datum)) {
            buffer.append("\"");
            GenericData.writeEscapedString(datum.toString(), buffer);
            buffer.append("\"");
        } else if (this.isBytes(datum)) {
            buffer.append("\"");
            ByteBuffer bytes = ((ByteBuffer)datum).duplicate();
            GenericData.writeEscapedString(StandardCharsets.ISO_8859_1.decode(bytes), buffer);
            buffer.append("\"");
        } else if (this.isNanOrInfinity(datum) || this.isTemporal(datum) || datum instanceof UUID) {
            buffer.append("\"");
            buffer.append(datum);
            buffer.append("\"");
        } else if (datum instanceof GenericData) {
            if (seenObjects.containsKey(datum)) {
                buffer.append(TOSTRING_CIRCULAR_REFERENCE_ERROR_TEXT);
                return;
            }
            seenObjects.put(datum, datum);
            this.toString(datum, buffer, seenObjects);
            seenObjects.remove(datum);
        } else {
            buffer.append(datum);
        }
    }

    private boolean isTemporal(Object datum) {
        return datum instanceof Temporal;
    }

    private boolean isNanOrInfinity(Object datum) {
        return datum instanceof Float && (((Float)datum).isInfinite() || ((Float)datum).isNaN()) || datum instanceof Double && (((Double)datum).isInfinite() || ((Double)datum).isNaN());
    }

    private static void writeEscapedString(CharSequence string, StringBuilder builder) {
        block9: for (int i = 0; i < string.length(); ++i) {
            char ch = string.charAt(i);
            switch (ch) {
                case '\"': {
                    builder.append("\\\"");
                    continue block9;
                }
                case '\\': {
                    builder.append("\\\\");
                    continue block9;
                }
                case '\b': {
                    builder.append("\\b");
                    continue block9;
                }
                case '\f': {
                    builder.append("\\f");
                    continue block9;
                }
                case '\n': {
                    builder.append("\\n");
                    continue block9;
                }
                case '\r': {
                    builder.append("\\r");
                    continue block9;
                }
                case '\t': {
                    builder.append("\\t");
                    continue block9;
                }
                default: {
                    if (ch >= '\u0000' && ch <= '\u001f' || ch >= '\u007f' && ch <= '\u009f' || ch >= '\u2000' && ch <= '\u20ff') {
                        String hex = Integer.toHexString(ch);
                        builder.append("\\u");
                        for (int j = 0; j < 4 - hex.length(); ++j) {
                            builder.append('0');
                        }
                        builder.append(hex.toUpperCase());
                        continue block9;
                    }
                    builder.append(ch);
                }
            }
        }
    }

    public Schema induce(Object datum) {
        if (this.isRecord(datum)) {
            return this.getRecordSchema(datum);
        }
        if (this.isArray(datum)) {
            Schema elementType = null;
            for (Object element : this.getArrayAsCollection(datum)) {
                if (elementType == null) {
                    elementType = this.induce(element);
                    continue;
                }
                if (elementType.equals(this.induce(element))) continue;
                throw new AvroTypeException("No mixed type arrays.");
            }
            if (elementType == null) {
                throw new AvroTypeException("Empty array: " + datum);
            }
            return Schema.createArray(elementType);
        }
        if (this.isMap(datum)) {
            Map map = (Map)datum;
            Schema value = null;
            for (Map.Entry entry : map.entrySet()) {
                if (value == null) {
                    value = this.induce(entry.getValue());
                    continue;
                }
                if (value.equals(this.induce(entry.getValue()))) continue;
                throw new AvroTypeException("No mixed type map values.");
            }
            if (value == null) {
                throw new AvroTypeException("Empty map: " + datum);
            }
            return Schema.createMap(value);
        }
        if (datum instanceof GenericFixed) {
            return Schema.createFixed(null, null, null, ((GenericFixed)datum).bytes().length);
        }
        if (this.isString(datum)) {
            return Schema.create(Schema.Type.STRING);
        }
        if (this.isBytes(datum)) {
            return Schema.create(Schema.Type.BYTES);
        }
        if (this.isInteger(datum)) {
            return Schema.create(Schema.Type.INT);
        }
        if (this.isLong(datum)) {
            return Schema.create(Schema.Type.LONG);
        }
        if (this.isFloat(datum)) {
            return Schema.create(Schema.Type.FLOAT);
        }
        if (this.isDouble(datum)) {
            return Schema.create(Schema.Type.DOUBLE);
        }
        if (this.isBoolean(datum)) {
            return Schema.create(Schema.Type.BOOLEAN);
        }
        if (datum == null) {
            return Schema.create(Schema.Type.NULL);
        }
        throw new AvroTypeException("Can't create schema for: " + datum);
    }

    public void setField(Object record, String name, int position, Object value) {
        ((IndexedRecord)record).put(position, value);
    }

    public Object getField(Object record, String name, int position) {
        return ((IndexedRecord)record).get(position);
    }

    protected Object getRecordState(Object record, Schema schema) {
        return null;
    }

    protected void setField(Object record, String name, int position, Object value, Object state) {
        this.setField(record, name, position, value);
    }

    protected Object getField(Object record, String name, int pos, Object state) {
        return this.getField(record, name, pos);
    }

    public int resolveUnion(Schema union, Object datum) {
        Integer i;
        Map<String, Conversion<?>> conversions;
        if (datum != null && (conversions = this.conversionsByClass.get(datum.getClass())) != null) {
            List<Schema> candidates = union.getTypes();
            for (int i2 = 0; i2 < candidates.size(); ++i2) {
                Conversion<?> conversion;
                LogicalType candidateType = candidates.get(i2).getLogicalType();
                if (candidateType == null || (conversion = conversions.get(candidateType.getName())) == null) continue;
                return i2;
            }
        }
        if ((i = union.getIndexNamed(this.getSchemaName(datum))) != null) {
            return i;
        }
        throw new UnresolvedUnionException(union, datum);
    }

    protected String getSchemaName(Object datum) {
        if (datum == null || datum == JsonProperties.NULL_VALUE) {
            return Schema.Type.NULL.getName();
        }
        String primativeType = this.getPrimitiveTypeCache().get(datum.getClass());
        if (primativeType != null) {
            return primativeType;
        }
        if (this.isRecord(datum)) {
            return this.getRecordSchema(datum).getFullName();
        }
        if (this.isEnum(datum)) {
            return this.getEnumSchema(datum).getFullName();
        }
        if (this.isArray(datum)) {
            return Schema.Type.ARRAY.getName();
        }
        if (this.isMap(datum)) {
            return Schema.Type.MAP.getName();
        }
        if (this.isFixed(datum)) {
            return this.getFixedSchema(datum).getFullName();
        }
        if (this.isString(datum)) {
            return Schema.Type.STRING.getName();
        }
        if (this.isBytes(datum)) {
            return Schema.Type.BYTES.getName();
        }
        if (this.isInteger(datum)) {
            return Schema.Type.INT.getName();
        }
        if (this.isLong(datum)) {
            return Schema.Type.LONG.getName();
        }
        if (this.isFloat(datum)) {
            return Schema.Type.FLOAT.getName();
        }
        if (this.isDouble(datum)) {
            return Schema.Type.DOUBLE.getName();
        }
        if (this.isBoolean(datum)) {
            return Schema.Type.BOOLEAN.getName();
        }
        throw new AvroRuntimeException(String.format("Unknown datum type %s: %s", datum.getClass().getName(), datum));
    }

    protected Map<Class<?>, String> getPrimitiveTypeCache() {
        return PRIMITIVE_DATUM_TYPES;
    }

    protected boolean instanceOf(Schema schema, Object datum) {
        switch (schema.getType()) {
            case RECORD: {
                if (!this.isRecord(datum)) {
                    return false;
                }
                return schema.getFullName() == null ? this.getRecordSchema(datum).getFullName() == null : schema.getFullName().equals(this.getRecordSchema(datum).getFullName());
            }
            case ENUM: {
                if (!this.isEnum(datum)) {
                    return false;
                }
                return schema.getFullName().equals(this.getEnumSchema(datum).getFullName());
            }
            case ARRAY: {
                return this.isArray(datum);
            }
            case MAP: {
                return this.isMap(datum);
            }
            case FIXED: {
                if (!this.isFixed(datum)) {
                    return false;
                }
                return schema.getFullName().equals(this.getFixedSchema(datum).getFullName());
            }
            case STRING: {
                return this.isString(datum);
            }
            case BYTES: {
                return this.isBytes(datum);
            }
            case INT: {
                return this.isInteger(datum);
            }
            case LONG: {
                return this.isLong(datum);
            }
            case FLOAT: {
                return this.isFloat(datum);
            }
            case DOUBLE: {
                return this.isDouble(datum);
            }
            case BOOLEAN: {
                return this.isBoolean(datum);
            }
            case NULL: {
                return datum == null;
            }
        }
        throw new AvroRuntimeException("Unexpected type: " + schema);
    }

    protected boolean isArray(Object datum) {
        return datum instanceof Collection;
    }

    protected Collection getArrayAsCollection(Object datum) {
        return (Collection)datum;
    }

    protected boolean isRecord(Object datum) {
        return datum instanceof IndexedRecord;
    }

    protected Schema getRecordSchema(Object record) {
        return ((GenericContainer)record).getSchema();
    }

    protected boolean isEnum(Object datum) {
        return datum instanceof GenericEnumSymbol;
    }

    protected Schema getEnumSchema(Object enu) {
        return ((GenericContainer)enu).getSchema();
    }

    protected boolean isMap(Object datum) {
        return datum instanceof Map;
    }

    protected boolean isFixed(Object datum) {
        return datum instanceof GenericFixed;
    }

    protected Schema getFixedSchema(Object fixed) {
        return ((GenericContainer)fixed).getSchema();
    }

    protected boolean isString(Object datum) {
        return datum instanceof CharSequence;
    }

    protected boolean isBytes(Object datum) {
        return datum instanceof ByteBuffer;
    }

    protected boolean isInteger(Object datum) {
        return datum instanceof Integer;
    }

    protected boolean isLong(Object datum) {
        return datum instanceof Long;
    }

    protected boolean isFloat(Object datum) {
        return datum instanceof Float;
    }

    protected boolean isDouble(Object datum) {
        return datum instanceof Double;
    }

    protected boolean isBoolean(Object datum) {
        return datum instanceof Boolean;
    }

    public int hashCode(Object o, Schema s) {
        if (o == null) {
            return 0;
        }
        int hashCode = 1;
        switch (s.getType()) {
            case RECORD: {
                for (Schema.Field f : s.getFields()) {
                    if (f.order() == Schema.Field.Order.IGNORE) continue;
                    hashCode = this.hashCodeAdd(hashCode, this.getField(o, f.name(), f.pos()), f.schema());
                }
                return hashCode;
            }
            case ARRAY: {
                Collection a = (Collection)o;
                Schema elementType = s.getElementType();
                for (Object e : a) {
                    hashCode = this.hashCodeAdd(hashCode, e, elementType);
                }
                return hashCode;
            }
            case UNION: {
                return this.hashCode(o, s.getTypes().get(this.resolveUnion(s, o)));
            }
            case ENUM: {
                return s.getEnumOrdinal(o.toString());
            }
            case NULL: {
                return 0;
            }
            case STRING: {
                return (o instanceof Utf8 ? o : new Utf8(o.toString())).hashCode();
            }
        }
        return o.hashCode();
    }

    protected int hashCodeAdd(int hashCode, Object o, Schema s) {
        return 31 * hashCode + this.hashCode(o, s);
    }

    public int compare(Object o1, Object o2, Schema s) {
        return this.compare(o1, o2, s, false);
    }

    protected int compareMaps(Map<?, ?> m1, Map<?, ?> m2) {
        if (m1 == m2) {
            return 0;
        }
        if (m1.isEmpty() && m2.isEmpty()) {
            return 0;
        }
        if (m1.size() != m2.size()) {
            return 1;
        }
        Object key1 = m1.keySet().iterator().next();
        Object key2 = m2.keySet().iterator().next();
        boolean utf8ToString = false;
        boolean stringToUtf8 = false;
        if (key1 instanceof Utf8 && key2 instanceof String) {
            utf8ToString = true;
        } else if (key1 instanceof String && key2 instanceof Utf8) {
            stringToUtf8 = true;
        }
        try {
            for (Map.Entry<?, ?> e : m1.entrySet()) {
                Object key;
                Object lookupKey = key = e.getKey();
                if (utf8ToString) {
                    lookupKey = key.toString();
                } else if (stringToUtf8) {
                    lookupKey = new Utf8((String)lookupKey);
                }
                Object value = e.getValue();
                if (value == null) {
                    if (m2.get(lookupKey) == null && m2.containsKey(lookupKey)) continue;
                    return 1;
                }
                Object value2 = m2.get(lookupKey);
                if (!(value instanceof Utf8 && value2 instanceof String ? !value.toString().equals(value2) : (value instanceof String && value2 instanceof Utf8 ? !new Utf8((String)value).equals(value2) : !value.equals(value2)))) continue;
                return 1;
            }
        }
        catch (ClassCastException unused) {
            return 1;
        }
        catch (NullPointerException unused) {
            return 1;
        }
        return 0;
    }

    protected int compare(Object o1, Object o2, Schema s, boolean equals) {
        if (o1 == o2) {
            return 0;
        }
        switch (s.getType()) {
            case RECORD: {
                for (Schema.Field f : s.getFields()) {
                    if (f.order() == Schema.Field.Order.IGNORE) continue;
                    int pos = f.pos();
                    String name = f.name();
                    int compare = this.compare(this.getField(o1, name, pos), this.getField(o2, name, pos), f.schema(), equals);
                    if (compare == 0) continue;
                    return f.order() == Schema.Field.Order.DESCENDING ? -compare : compare;
                }
                return 0;
            }
            case ENUM: {
                return s.getEnumOrdinal(o1.toString()) - s.getEnumOrdinal(o2.toString());
            }
            case ARRAY: {
                Collection a1 = (Collection)o1;
                Collection a2 = (Collection)o2;
                Iterator e1 = a1.iterator();
                Iterator e2 = a2.iterator();
                Schema elementType = s.getElementType();
                while (e1.hasNext() && e2.hasNext()) {
                    int compare = this.compare(e1.next(), e2.next(), elementType, equals);
                    if (compare == 0) continue;
                    return compare;
                }
                return e1.hasNext() ? 1 : (e2.hasNext() ? -1 : 0);
            }
            case MAP: {
                if (equals) {
                    return this.compareMaps((Map)o1, (Map)o2);
                }
                throw new AvroRuntimeException("Can't compare maps!");
            }
            case UNION: {
                int i1 = this.resolveUnion(s, o1);
                int i2 = this.resolveUnion(s, o2);
                return i1 == i2 ? this.compare(o1, o2, s.getTypes().get(i1), equals) : Integer.compare(i1, i2);
            }
            case NULL: {
                return 0;
            }
            case STRING: {
                Utf8 u1 = o1 instanceof Utf8 ? (Utf8)o1 : new Utf8(o1.toString());
                Utf8 u2 = o2 instanceof Utf8 ? (Utf8)o2 : new Utf8(o2.toString());
                return u1.compareTo(u2);
            }
        }
        return ((Comparable)o1).compareTo(o2);
    }

    public Object getDefaultValue(Schema.Field field) {
        JsonNode json = Accessor.defaultValue(field);
        if (json == null) {
            throw new AvroMissingFieldException("Field " + field + " not set and has no default value", field);
        }
        if (json.isNull() && (field.schema().getType() == Schema.Type.NULL || field.schema().getType() == Schema.Type.UNION && field.schema().getTypes().get(0).getType() == Schema.Type.NULL)) {
            return null;
        }
        return this.defaultValueCache.computeIfAbsent(field, fieldToGetValueFor -> {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(baos, null);
                Accessor.encode(encoder, fieldToGetValueFor.schema(), json);
                encoder.flush();
                BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(baos.toByteArray(), null);
                return this.createDatumReader(fieldToGetValueFor.schema()).read(null, decoder);
            }
            catch (IOException e) {
                throw new AvroRuntimeException(e);
            }
        });
    }

    public <T> T deepCopy(Schema schema, T value) {
        if (value == null) {
            return null;
        }
        LogicalType logicalType = schema.getLogicalType();
        if (logicalType == null) {
            return (T)this.deepCopyRaw(schema, value);
        }
        Conversion<?> conversion = this.getConversionByClass(value.getClass(), logicalType);
        if (conversion == null) {
            return (T)this.deepCopyRaw(schema, value);
        }
        Object raw = Conversions.convertToRawType(value, schema, logicalType, conversion);
        Object copy = this.deepCopyRaw(schema, raw);
        return (T)Conversions.convertToLogicalType(copy, schema, logicalType, conversion);
    }

    private Object deepCopyRaw(Schema schema, Object value) {
        if (value == null) {
            return null;
        }
        switch (schema.getType()) {
            case ARRAY: {
                List arrayValue = (List)value;
                Array arrayCopy = new Array(arrayValue.size(), schema);
                for (Object obj : arrayValue) {
                    arrayCopy.add(this.deepCopy(schema.getElementType(), obj));
                }
                return arrayCopy;
            }
            case BOOLEAN: {
                return value;
            }
            case BYTES: {
                ByteBuffer byteBufferValue = (ByteBuffer)value;
                int start = byteBufferValue.position();
                int length = byteBufferValue.limit() - start;
                byte[] bytesCopy = new byte[length];
                byteBufferValue.get(bytesCopy, 0, length);
                ((Buffer)byteBufferValue).position(start);
                return ByteBuffer.wrap(bytesCopy, 0, length);
            }
            case DOUBLE: {
                return value;
            }
            case ENUM: {
                return this.createEnum(value.toString(), schema);
            }
            case FIXED: {
                return this.createFixed(null, ((GenericFixed)value).bytes(), schema);
            }
            case FLOAT: {
                return value;
            }
            case INT: {
                return value;
            }
            case LONG: {
                return value;
            }
            case MAP: {
                Map mapValue = (Map)value;
                HashMap mapCopy = new HashMap(mapValue.size());
                for (Map.Entry entry : mapValue.entrySet()) {
                    mapCopy.put(this.deepCopy(STRINGS, entry.getKey()), this.deepCopy(schema.getValueType(), entry.getValue()));
                }
                return mapCopy;
            }
            case NULL: {
                return null;
            }
            case RECORD: {
                Object oldState = this.getRecordState(value, schema);
                Object newRecord = this.newRecord(null, schema);
                Object newState = this.getRecordState(newRecord, schema);
                for (Schema.Field f : schema.getFields()) {
                    int pos = f.pos();
                    String name = f.name();
                    Object newValue = this.deepCopy(f.schema(), this.getField(value, name, pos, oldState));
                    this.setField(newRecord, name, pos, newValue, newState);
                }
                return newRecord;
            }
            case STRING: {
                return this.createString(value);
            }
            case UNION: {
                return this.deepCopy(schema.getTypes().get(this.resolveUnion(schema, value)), value);
            }
        }
        throw new AvroRuntimeException("Deep copy failed for schema \"" + schema + "\" and value \"" + value + "\"");
    }

    public Object createFixed(Object old, Schema schema) {
        if (old instanceof GenericFixed && ((GenericFixed)old).bytes().length == schema.getFixedSize()) {
            return old;
        }
        return new Fixed(schema);
    }

    public Object createFixed(Object old, byte[] bytes, Schema schema) {
        GenericFixed fixed = (GenericFixed)this.createFixed(old, schema);
        System.arraycopy(bytes, 0, fixed.bytes(), 0, schema.getFixedSize());
        return fixed;
    }

    public Object createEnum(String symbol, Schema schema) {
        return new EnumSymbol(schema, symbol);
    }

    public Object newRecord(Object old, Schema schema) {
        IndexedRecord record;
        if (old instanceof IndexedRecord && (record = (IndexedRecord)old).getSchema() == schema) {
            return record;
        }
        return new Record(schema);
    }

    public Object createString(Object value) {
        if (value instanceof String) {
            return value;
        }
        if (value instanceof Utf8) {
            return new Utf8((Utf8)value);
        }
        return new Utf8(value.toString());
    }

    public Object newArray(Object old, int size, Schema schema) {
        if (old instanceof GenericArray) {
            ((GenericArray)old).reset();
            return old;
        }
        if (old instanceof Collection) {
            ((Collection)old).clear();
            return old;
        }
        return new Array(size, schema);
    }

    public Object newMap(Object old, int size) {
        if (old instanceof Map) {
            ((Map)old).clear();
            return old;
        }
        return new HashMap(size);
    }

    public InstanceSupplier getNewRecordSupplier(Schema schema) {
        return this::newRecord;
    }

    static {
        PRIMITIVE_DATUM_TYPES.put(Integer.class, Schema.Type.INT.getName());
        PRIMITIVE_DATUM_TYPES.put(Long.class, Schema.Type.LONG.getName());
        PRIMITIVE_DATUM_TYPES.put(Float.class, Schema.Type.FLOAT.getName());
        PRIMITIVE_DATUM_TYPES.put(Double.class, Schema.Type.DOUBLE.getName());
        PRIMITIVE_DATUM_TYPES.put(Boolean.class, Schema.Type.BOOLEAN.getName());
        PRIMITIVE_DATUM_TYPES.put(String.class, Schema.Type.STRING.getName());
        PRIMITIVE_DATUM_TYPES.put(Utf8.class, Schema.Type.STRING.getName());
        STRINGS = Schema.create(Schema.Type.STRING);
    }

    public static interface InstanceSupplier {
        public Object newInstance(Object var1, Schema var2);
    }

    public static class EnumSymbol
    implements GenericEnumSymbol<EnumSymbol> {
        private Schema schema;
        private String symbol;

        public EnumSymbol(Schema schema, String symbol) {
            this.schema = schema;
            this.symbol = symbol;
        }

        public EnumSymbol(Schema schema, Object symbol) {
            this(schema, symbol.toString());
        }

        @Override
        public Schema getSchema() {
            return this.schema;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            return o instanceof GenericEnumSymbol && this.symbol.equals(o.toString());
        }

        public int hashCode() {
            return this.symbol.hashCode();
        }

        @Override
        public String toString() {
            return this.symbol;
        }

        @Override
        public int compareTo(EnumSymbol that) {
            return GenericData.get().compare(this, that, this.schema);
        }
    }

    public static class Fixed
    implements GenericFixed,
    Comparable<Fixed> {
        private Schema schema;
        private byte[] bytes;

        public Fixed(Schema schema) {
            this.setSchema(schema);
        }

        public Fixed(Schema schema, byte[] bytes) {
            this.schema = schema;
            this.bytes = bytes;
        }

        protected Fixed() {
        }

        protected void setSchema(Schema schema) {
            this.schema = schema;
            this.bytes = new byte[schema.getFixedSize()];
        }

        @Override
        public Schema getSchema() {
            return this.schema;
        }

        public void bytes(byte[] bytes) {
            this.bytes = bytes;
        }

        @Override
        public byte[] bytes() {
            return this.bytes;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            return o instanceof GenericFixed && Arrays.equals(this.bytes, ((GenericFixed)o).bytes());
        }

        public int hashCode() {
            return Arrays.hashCode(this.bytes);
        }

        public String toString() {
            return Arrays.toString(this.bytes);
        }

        @Override
        public int compareTo(Fixed that) {
            return BinaryData.compareBytes(this.bytes, 0, this.bytes.length, that.bytes, 0, that.bytes.length);
        }
    }

    public static class Array<T>
    extends AbstractList<T>
    implements GenericArray<T>,
    Comparable<GenericArray<T>> {
        private static final Object[] EMPTY = new Object[0];
        private final Schema schema;
        private int size;
        private Object[] elements = EMPTY;

        public Array(int capacity, Schema schema) {
            if (schema == null || !Schema.Type.ARRAY.equals((Object)schema.getType())) {
                throw new AvroRuntimeException("Not an array schema: " + schema);
            }
            this.schema = schema;
            if (capacity != 0) {
                this.elements = new Object[capacity];
            }
        }

        public Array(Schema schema, Collection<T> c) {
            if (schema == null || !Schema.Type.ARRAY.equals((Object)schema.getType())) {
                throw new AvroRuntimeException("Not an array schema: " + schema);
            }
            this.schema = schema;
            if (c != null) {
                this.elements = new Object[c.size()];
                this.addAll(c);
            }
        }

        @Override
        public Schema getSchema() {
            return this.schema;
        }

        @Override
        public int size() {
            return this.size;
        }

        @Override
        public void clear() {
            Arrays.fill(this.elements, 0, this.size, null);
            this.size = 0;
        }

        @Override
        public void reset() {
            this.size = 0;
        }

        @Override
        public void prune() {
            if (this.size < this.elements.length) {
                Arrays.fill(this.elements, this.size, this.elements.length, null);
            }
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>(){
                private int position = 0;

                @Override
                public boolean hasNext() {
                    return this.position < size;
                }

                @Override
                public T next() {
                    return elements[this.position++];
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        @Override
        public T get(int i) {
            if (i >= this.size) {
                throw new IndexOutOfBoundsException("Index " + i + " out of bounds.");
            }
            return (T)this.elements[i];
        }

        @Override
        public void add(int location, T o) {
            if (location > this.size || location < 0) {
                throw new IndexOutOfBoundsException("Index " + location + " out of bounds.");
            }
            if (this.size == this.elements.length) {
                int newSize = this.size + (this.size >> 1) + 1;
                this.elements = Arrays.copyOf(this.elements, newSize);
            }
            System.arraycopy(this.elements, location, this.elements, location + 1, this.size - location);
            this.elements[location] = o;
            ++this.size;
        }

        @Override
        public T set(int i, T o) {
            if (i >= this.size) {
                throw new IndexOutOfBoundsException("Index " + i + " out of bounds.");
            }
            Object response = this.elements[i];
            this.elements[i] = o;
            return (T)response;
        }

        @Override
        public T remove(int i) {
            if (i >= this.size) {
                throw new IndexOutOfBoundsException("Index " + i + " out of bounds.");
            }
            Object result = this.elements[i];
            --this.size;
            System.arraycopy(this.elements, i + 1, this.elements, i, this.size - i);
            this.elements[this.size] = null;
            return (T)result;
        }

        @Override
        public T peek() {
            return (T)(this.size < this.elements.length ? this.elements[this.size] : null);
        }

        @Override
        public int compareTo(GenericArray<T> that) {
            return GenericData.get().compare(this, that, this.getSchema());
        }

        @Override
        public void reverse() {
            int left = 0;
            for (int right = this.elements.length - 1; left < right; ++left, --right) {
                Object tmp = this.elements[left];
                this.elements[left] = this.elements[right];
                this.elements[right] = tmp;
            }
        }
    }

    public static class Record
    implements GenericRecord,
    Comparable<Record> {
        private final Schema schema;
        private final Object[] values;

        public Record(Schema schema) {
            if (schema == null || !Schema.Type.RECORD.equals((Object)schema.getType())) {
                throw new AvroRuntimeException("Not a record schema: " + schema);
            }
            this.schema = schema;
            this.values = new Object[schema.getFields().size()];
        }

        public Record(Record other, boolean deepCopy) {
            this.schema = other.schema;
            this.values = new Object[this.schema.getFields().size()];
            if (deepCopy) {
                for (int ii = 0; ii < this.values.length; ++ii) {
                    this.values[ii] = INSTANCE.deepCopy(this.schema.getFields().get(ii).schema(), other.values[ii]);
                }
            } else {
                System.arraycopy(other.values, 0, this.values, 0, other.values.length);
            }
        }

        @Override
        public Schema getSchema() {
            return this.schema;
        }

        @Override
        public void put(String key, Object value) {
            Schema.Field field = this.schema.getField(key);
            if (field == null) {
                throw new AvroRuntimeException("Not a valid schema field: " + key);
            }
            this.values[field.pos()] = value;
        }

        @Override
        public void put(int i, Object v) {
            this.values[i] = v;
        }

        @Override
        public Object get(String key) {
            Schema.Field field = this.schema.getField(key);
            if (field == null) {
                throw new AvroRuntimeException("Not a valid schema field: " + key);
            }
            return this.values[field.pos()];
        }

        @Override
        public Object get(int i) {
            return this.values[i];
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Record)) {
                return false;
            }
            Record that = (Record)o;
            if (!this.schema.equals(that.schema)) {
                return false;
            }
            return GenericData.get().compare(this, that, this.schema, true) == 0;
        }

        public int hashCode() {
            return GenericData.get().hashCode(this, this.schema);
        }

        @Override
        public int compareTo(Record that) {
            return GenericData.get().compare(this, that, this.schema);
        }

        public String toString() {
            return GenericData.get().toString(this);
        }
    }

    public static enum StringType {
        CharSequence,
        String,
        Utf8;

    }
}

