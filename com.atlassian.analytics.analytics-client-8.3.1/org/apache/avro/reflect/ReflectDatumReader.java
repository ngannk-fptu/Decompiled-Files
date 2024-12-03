/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.reflect;

import java.io.IOException;
import java.lang.constant.Constable;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.ResolvingDecoder;
import org.apache.avro.reflect.ArrayAccessor;
import org.apache.avro.reflect.FieldAccessor;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.specific.SpecificDatumReader;

public class ReflectDatumReader<T>
extends SpecificDatumReader<T> {
    public ReflectDatumReader() {
        this(null, null, ReflectData.get());
    }

    public ReflectDatumReader(Class<T> c) {
        this(new ReflectData(c.getClassLoader()));
        this.setSchema(this.getSpecificData().getSchema(c));
    }

    public ReflectDatumReader(Schema root) {
        this(root, root, ReflectData.get());
    }

    public ReflectDatumReader(Schema writer, Schema reader) {
        this(writer, reader, ReflectData.get());
    }

    public ReflectDatumReader(Schema writer, Schema reader, ReflectData data) {
        super(writer, reader, data);
    }

    public ReflectDatumReader(ReflectData data) {
        super(data);
    }

    @Override
    protected Object newArray(Object old, int size, Schema schema) {
        Conversion elementConversion;
        Class collectionClass = ReflectData.getClassProp(schema, "java-class");
        Class<?> elementClass = ReflectData.getClassProp(schema, "java-element-class");
        if (elementClass == null && (elementConversion = this.getData().getConversionFor(schema.getElementType().getLogicalType())) != null) {
            elementClass = elementConversion.getConvertedType();
        }
        if (collectionClass == null && elementClass == null) {
            return super.newArray(old, size, schema);
        }
        if (collectionClass != null && !collectionClass.isArray()) {
            if (old instanceof Collection) {
                ((Collection)old).clear();
                return old;
            }
            if (collectionClass.isAssignableFrom(ArrayList.class)) {
                return new ArrayList();
            }
            if (collectionClass.isAssignableFrom(HashSet.class)) {
                return new HashSet();
            }
            if (collectionClass.isAssignableFrom(HashMap.class)) {
                return new HashMap();
            }
            return SpecificData.newInstance(collectionClass, schema);
        }
        if (elementClass == null) {
            elementClass = collectionClass.getComponentType();
        }
        if (elementClass == null) {
            ReflectData data = (ReflectData)this.getData();
            elementClass = data.getClass(schema.getElementType());
        }
        return Array.newInstance(elementClass, size);
    }

    @Override
    protected Object peekArray(Object array) {
        return null;
    }

    @Override
    protected void addToArray(Object array, long pos, Object e) {
        throw new AvroRuntimeException("reflectDatumReader does not use addToArray");
    }

    @Override
    protected Object readArray(Object old, Schema expected, ResolvingDecoder in) throws IOException {
        Schema expectedType = expected.getElementType();
        long l = in.readArrayStart();
        if (l <= 0L) {
            return this.newArray(old, 0, expected);
        }
        Object array = this.newArray(old, (int)l, expected);
        if (array instanceof Collection) {
            Collection c = (Collection)array;
            return this.readCollection(c, expectedType, l, in);
        }
        if (array instanceof Map) {
            if (ReflectData.isNonStringMapSchema(expected)) {
                ArrayList<Object> c = new ArrayList<Object>();
                this.readCollection(c, expectedType, l, in);
                Map m = (Map)array;
                for (Object e : c) {
                    IndexedRecord rec = (IndexedRecord)e;
                    Object key = rec.get(0);
                    Object value = rec.get(1);
                    m.put(key, value);
                }
                return array;
            }
            String msg = "Expected a schema of map with non-string keys but got " + expected;
            throw new AvroRuntimeException(msg);
        }
        return this.readJavaArray(array, expectedType, l, in);
    }

    private Object readJavaArray(Object array, Schema expectedType, long l, ResolvingDecoder in) throws IOException {
        Class<?> elementType = array.getClass().getComponentType();
        if (elementType.isPrimitive()) {
            return this.readPrimitiveArray(array, elementType, l, in);
        }
        return this.readObjectArray((Object[])array, expectedType, l, in);
    }

    private Object readPrimitiveArray(Object array, Class<?> c, long l, ResolvingDecoder in) throws IOException {
        return ArrayAccessor.readArray(array, c, l, in);
    }

    private Object readObjectArray(Object[] array, Schema expectedType, long l, ResolvingDecoder in) throws IOException {
        LogicalType logicalType = expectedType.getLogicalType();
        Conversion conversion = this.getData().getConversionFor(logicalType);
        int index = 0;
        if (logicalType != null && conversion != null) {
            do {
                int limit = index + (int)l;
                while (index < limit) {
                    Object element;
                    array[index] = element = this.readWithConversion(null, expectedType, logicalType, conversion, in);
                    ++index;
                }
            } while ((l = in.arrayNext()) > 0L);
        } else {
            do {
                int limit = index + (int)l;
                while (index < limit) {
                    Object element;
                    array[index] = element = this.readWithoutConversion(null, expectedType, in);
                    ++index;
                }
            } while ((l = in.arrayNext()) > 0L);
        }
        return array;
    }

    private Object readCollection(Collection<Object> c, Schema expectedType, long l, ResolvingDecoder in) throws IOException {
        LogicalType logicalType = expectedType.getLogicalType();
        Conversion conversion = this.getData().getConversionFor(logicalType);
        if (logicalType != null && conversion != null) {
            do {
                int i = 0;
                while ((long)i < l) {
                    Object element = this.readWithConversion(null, expectedType, logicalType, conversion, in);
                    c.add(element);
                    ++i;
                }
            } while ((l = in.arrayNext()) > 0L);
        } else {
            do {
                int i = 0;
                while ((long)i < l) {
                    Object element = this.readWithoutConversion(null, expectedType, in);
                    c.add(element);
                    ++i;
                }
            } while ((l = in.arrayNext()) > 0L);
        }
        return c;
    }

    @Override
    protected Object readString(Object old, Decoder in) throws IOException {
        return super.readString(null, in).toString();
    }

    @Override
    protected Object createString(String value) {
        return value;
    }

    @Override
    protected Object readBytes(Object old, Schema s, Decoder in) throws IOException {
        ByteBuffer bytes = in.readBytes(null);
        Class c = ReflectData.getClassProp(s, "java-class");
        if (c != null && c.isArray()) {
            byte[] result = new byte[bytes.remaining()];
            bytes.get(result);
            return result;
        }
        return bytes;
    }

    @Override
    protected Object readInt(Object old, Schema expected, Decoder in) throws IOException {
        Constable value = in.readInt();
        String intClass = expected.getProp("java-class");
        if (Byte.class.getName().equals(intClass)) {
            value = value.byteValue();
        } else if (Short.class.getName().equals(intClass)) {
            value = value.shortValue();
        } else if (Character.class.getName().equals(intClass)) {
            value = Character.valueOf((char)((Integer)value).intValue());
        }
        return value;
    }

    @Override
    protected void readField(Object record, Schema.Field field, Object oldDatum, ResolvingDecoder in, Object state) throws IOException {
        FieldAccessor accessor;
        if (state != null && (accessor = ((FieldAccessor[])state)[field.pos()]) != null) {
            Conversion<?> conversion;
            if (accessor.supportsIO() && (!Schema.Type.UNION.equals((Object)field.schema().getType()) || accessor.isCustomEncoded())) {
                accessor.read(record, in);
                return;
            }
            if (accessor.isStringable()) {
                try {
                    String asString = (String)this.read(null, field.schema(), in);
                    accessor.set(record, asString == null ? null : this.newInstanceFromString(accessor.getField().getType(), asString));
                    return;
                }
                catch (Exception e) {
                    throw new AvroRuntimeException("Failed to read Stringable", e);
                }
            }
            LogicalType logicalType = field.schema().getLogicalType();
            if (logicalType != null && (conversion = this.getData().getConversionByClass(accessor.getField().getType(), logicalType)) != null) {
                try {
                    accessor.set(record, this.convert(this.readWithoutConversion(oldDatum, field.schema(), in), field.schema(), logicalType, conversion));
                }
                catch (IllegalAccessException e) {
                    throw new AvroRuntimeException("Failed to set " + field);
                }
                return;
            }
            try {
                accessor.set(record, this.readWithoutConversion(oldDatum, field.schema(), in));
                return;
            }
            catch (IllegalAccessException e) {
                throw new AvroRuntimeException("Failed to set " + field);
            }
        }
        super.readField(record, field, oldDatum, in, state);
    }
}

