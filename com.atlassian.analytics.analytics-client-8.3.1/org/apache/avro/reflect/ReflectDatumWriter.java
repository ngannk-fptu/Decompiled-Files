/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.reflect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.io.Encoder;
import org.apache.avro.reflect.ArrayAccessor;
import org.apache.avro.reflect.FieldAccessor;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.util.MapEntry;

public class ReflectDatumWriter<T>
extends SpecificDatumWriter<T> {
    public ReflectDatumWriter() {
        this(ReflectData.get());
    }

    public ReflectDatumWriter(Class<T> c) {
        this(c, ReflectData.get());
    }

    public ReflectDatumWriter(Class<T> c, ReflectData data) {
        this(data.getSchema(c), data);
    }

    public ReflectDatumWriter(Schema root) {
        this(root, ReflectData.get());
    }

    public ReflectDatumWriter(Schema root, ReflectData reflectData) {
        super(root, reflectData);
    }

    protected ReflectDatumWriter(ReflectData reflectData) {
        super(reflectData);
    }

    @Override
    protected void writeArray(Schema schema, Object datum, Encoder out) throws IOException {
        if (datum instanceof Collection) {
            super.writeArray(schema, datum, out);
            return;
        }
        Class<?> elementClass = datum.getClass().getComponentType();
        if (null == elementClass) {
            throw new AvroRuntimeException("Array data must be a Collection or Array");
        }
        Schema element = schema.getElementType();
        if (elementClass.isPrimitive()) {
            Schema.Type type = element.getType();
            out.writeArrayStart();
            switch (type) {
                case BOOLEAN: {
                    ArrayAccessor.writeArray((boolean[])datum, out);
                    break;
                }
                case DOUBLE: {
                    ArrayAccessor.writeArray((double[])datum, out);
                    break;
                }
                case FLOAT: {
                    ArrayAccessor.writeArray((float[])datum, out);
                    break;
                }
                case INT: {
                    if (elementClass.equals(Integer.TYPE)) {
                        ArrayAccessor.writeArray((int[])datum, out);
                        break;
                    }
                    if (elementClass.equals(Character.TYPE)) {
                        ArrayAccessor.writeArray((char[])datum, out);
                        break;
                    }
                    if (elementClass.equals(Short.TYPE)) {
                        ArrayAccessor.writeArray((short[])datum, out);
                        break;
                    }
                    this.arrayError(elementClass, type);
                    break;
                }
                case LONG: {
                    ArrayAccessor.writeArray((long[])datum, out);
                    break;
                }
                default: {
                    this.arrayError(elementClass, type);
                }
            }
            out.writeArrayEnd();
        } else {
            out.writeArrayStart();
            this.writeObjectArray(element, (Object[])datum, out);
            out.writeArrayEnd();
        }
    }

    private void writeObjectArray(Schema element, Object[] data, Encoder out) throws IOException {
        int size = data.length;
        out.setItemCount(size);
        for (Object datum : data) {
            this.write(element, datum, out);
        }
    }

    private void arrayError(Class<?> cl, Schema.Type type) {
        throw new AvroRuntimeException("Error writing array with inner type " + cl + " and avro type: " + (Object)((Object)type));
    }

    @Override
    protected void writeBytes(Object datum, Encoder out) throws IOException {
        if (datum instanceof byte[]) {
            out.writeBytes((byte[])datum);
        } else {
            super.writeBytes(datum, out);
        }
    }

    @Override
    protected void write(Schema schema, Object datum, Encoder out) throws IOException {
        if (datum instanceof Byte) {
            datum = ((Byte)((Object)datum)).intValue();
        } else if (datum instanceof Short) {
            datum = ((Short)((Object)datum)).intValue();
        } else if (datum instanceof Character) {
            datum = (int)((Character)((Object)datum)).charValue();
        } else if (datum instanceof Map && ReflectData.isNonStringMapSchema(schema)) {
            Set entries = ((Map)((Object)datum)).entrySet();
            ArrayList entryList = new ArrayList(entries.size());
            Iterator iterator = ((Map)((Object)datum)).entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry obj;
                Map.Entry e = obj = iterator.next();
                entryList.add(new MapEntry(e.getKey(), e.getValue()));
            }
            datum = entryList;
        }
        try {
            super.write(schema, datum, out);
        }
        catch (NullPointerException e) {
            throw this.npe(e, " in " + schema.getFullName());
        }
    }

    @Override
    protected void writeField(Object record, Schema.Field f, Encoder out, Object state) throws IOException {
        FieldAccessor accessor;
        if (state != null && (accessor = ((FieldAccessor[])state)[f.pos()]) != null) {
            if (accessor.supportsIO() && (!Schema.Type.UNION.equals((Object)f.schema().getType()) || accessor.isCustomEncoded())) {
                accessor.write(record, out);
                return;
            }
            if (accessor.isStringable()) {
                try {
                    Object object = accessor.get(record);
                    this.write(f.schema(), object == null ? null : object.toString(), out);
                }
                catch (IllegalAccessException e) {
                    throw new AvroRuntimeException("Failed to write Stringable", e);
                }
                return;
            }
        }
        super.writeField(record, f, out, state);
    }
}

