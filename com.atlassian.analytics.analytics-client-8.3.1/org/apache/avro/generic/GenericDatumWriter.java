/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.generic;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.AvroTypeException;
import org.apache.avro.Conversion;
import org.apache.avro.Conversions;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;
import org.apache.avro.UnresolvedUnionException;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericFixed;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.path.ArrayPositionPredicate;
import org.apache.avro.path.LocationStep;
import org.apache.avro.path.MapKeyPredicate;
import org.apache.avro.path.PathTracingException;
import org.apache.avro.path.TracingAvroTypeException;
import org.apache.avro.path.TracingClassCastException;
import org.apache.avro.path.TracingNullPointException;
import org.apache.avro.path.UnionTypePredicate;
import org.apache.avro.util.SchemaUtil;

public class GenericDatumWriter<D>
implements DatumWriter<D> {
    private final GenericData data;
    private Schema root;

    public GenericDatumWriter() {
        this(GenericData.get());
    }

    protected GenericDatumWriter(GenericData data) {
        this.data = data;
    }

    public GenericDatumWriter(Schema root) {
        this();
        this.setSchema(root);
    }

    public GenericDatumWriter(Schema root, GenericData data) {
        this(data);
        this.setSchema(root);
    }

    public GenericData getData() {
        return this.data;
    }

    @Override
    public void setSchema(Schema root) {
        this.root = root;
    }

    @Override
    public void write(D datum, Encoder out) throws IOException {
        Objects.requireNonNull(out, "Encoder cannot be null");
        try {
            this.write(this.root, datum, out);
        }
        catch (TracingAvroTypeException | TracingClassCastException | TracingNullPointException e) {
            throw (RuntimeException)((PathTracingException)((Object)e)).summarize(this.root);
        }
    }

    protected void write(Schema schema, Object datum, Encoder out) throws IOException {
        LogicalType logicalType = schema.getLogicalType();
        if (datum != null && logicalType != null) {
            Conversion<?> conversion = this.getData().getConversionByClass(datum.getClass(), logicalType);
            this.writeWithoutConversion(schema, this.convert(schema, logicalType, conversion, datum), out);
        } else {
            this.writeWithoutConversion(schema, datum, out);
        }
    }

    protected <T> Object convert(Schema schema, LogicalType logicalType, Conversion<T> conversion, Object datum) {
        try {
            if (conversion == null) {
                return datum;
            }
            return Conversions.convertToRawType(datum, schema, logicalType, conversion);
        }
        catch (AvroRuntimeException e) {
            Throwable cause = e.getCause();
            if (cause != null && cause.getClass() == ClassCastException.class) {
                throw (ClassCastException)cause;
            }
            throw e;
        }
    }

    protected void writeWithoutConversion(Schema schema, Object datum, Encoder out) throws IOException {
        int unionIndex = -1;
        Schema.Type schemaType = schema.getType();
        try {
            switch (schemaType) {
                case RECORD: {
                    this.writeRecord(schema, datum, out);
                    break;
                }
                case ENUM: {
                    this.writeEnum(schema, datum, out);
                    break;
                }
                case ARRAY: {
                    this.writeArray(schema, datum, out);
                    break;
                }
                case MAP: {
                    this.writeMap(schema, datum, out);
                    break;
                }
                case UNION: {
                    unionIndex = this.resolveUnion(schema, datum);
                    out.writeIndex(unionIndex);
                    this.write(schema.getTypes().get(unionIndex), datum, out);
                    break;
                }
                case FIXED: {
                    this.writeFixed(schema, datum, out);
                    break;
                }
                case STRING: {
                    this.writeString(schema, datum, out);
                    break;
                }
                case BYTES: {
                    this.writeBytes(datum, out);
                    break;
                }
                case INT: {
                    out.writeInt(((Number)datum).intValue());
                    break;
                }
                case LONG: {
                    out.writeLong(((Number)datum).longValue());
                    break;
                }
                case FLOAT: {
                    out.writeFloat(((Number)datum).floatValue());
                    break;
                }
                case DOUBLE: {
                    out.writeDouble(((Number)datum).doubleValue());
                    break;
                }
                case BOOLEAN: {
                    out.writeBoolean((Boolean)datum);
                    break;
                }
                case NULL: {
                    out.writeNull();
                    break;
                }
                default: {
                    this.error(schema, datum);
                    break;
                }
            }
        }
        catch (TracingAvroTypeException | TracingClassCastException | TracingNullPointException e) {
            if (schemaType == Schema.Type.UNION) {
                ((PathTracingException)((Object)e)).tracePath(new UnionTypePredicate(schema.getTypes().get(unionIndex).getName()));
            }
            throw e;
        }
        catch (NullPointerException e) {
            throw new TracingNullPointException(e, schema, false);
        }
        catch (ClassCastException e) {
            throw new TracingClassCastException(e, datum, schema, false);
        }
        catch (AvroTypeException e) {
            throw new TracingAvroTypeException(e);
        }
    }

    protected NullPointerException npe(NullPointerException e, String s) {
        NullPointerException result = new NullPointerException(e.getMessage() + s);
        result.initCause(e.getCause() == null ? e : e.getCause());
        return result;
    }

    protected ClassCastException addClassCastMsg(ClassCastException e, String s) {
        ClassCastException result = new ClassCastException(e.getMessage() + s);
        result.initCause(e.getCause() == null ? e : e.getCause());
        return result;
    }

    protected AvroTypeException addAvroTypeMsg(AvroTypeException e, String s) {
        AvroTypeException result = new AvroTypeException(e.getMessage() + s);
        result.initCause(e.getCause() == null ? e : e.getCause());
        return result;
    }

    protected void writeRecord(Schema schema, Object datum, Encoder out) throws IOException {
        Object state = this.data.getRecordState(datum, schema);
        for (Schema.Field f : schema.getFields()) {
            this.writeField(datum, f, out, state);
        }
    }

    protected void writeField(Object datum, Schema.Field f, Encoder out, Object state) throws IOException {
        Object value = this.data.getField(datum, f.name(), f.pos(), state);
        try {
            this.write(f.schema(), value, out);
        }
        catch (UnresolvedUnionException uue) {
            UnresolvedUnionException unresolvedUnionException = new UnresolvedUnionException(f.schema(), f, value);
            unresolvedUnionException.addSuppressed(uue);
            throw unresolvedUnionException;
        }
        catch (TracingAvroTypeException | TracingClassCastException | TracingNullPointException e) {
            ((PathTracingException)((Object)e)).tracePath(new LocationStep(".", f.name()));
            throw e;
        }
        catch (NullPointerException e) {
            throw this.npe(e, " in field " + f.name());
        }
        catch (ClassCastException cce) {
            throw this.addClassCastMsg(cce, " in field " + f.name());
        }
        catch (AvroTypeException ate) {
            throw this.addAvroTypeMsg(ate, " in field " + f.name());
        }
    }

    protected void writeEnum(Schema schema, Object datum, Encoder out) throws IOException {
        if (!this.data.isEnum(datum)) {
            AvroTypeException cause = new AvroTypeException("value " + SchemaUtil.describe(datum) + " is not a " + SchemaUtil.describe(schema));
            throw new TracingAvroTypeException(cause);
        }
        out.writeEnum(schema.getEnumOrdinal(datum.toString()));
    }

    protected void writeArray(Schema schema, Object datum, Encoder out) throws IOException {
        Schema element = schema.getElementType();
        long size = this.getArraySize(datum);
        long actualSize = 0L;
        out.writeArrayStart();
        out.setItemCount(size);
        Iterator<?> it = this.getArrayElements(datum);
        while (it.hasNext()) {
            out.startItem();
            try {
                this.write(element, it.next(), out);
            }
            catch (TracingAvroTypeException | TracingClassCastException | TracingNullPointException e) {
                ((PathTracingException)((Object)e)).tracePath(new ArrayPositionPredicate(actualSize));
                throw e;
            }
            ++actualSize;
        }
        out.writeArrayEnd();
        if (actualSize != size) {
            throw new ConcurrentModificationException("Size of array written was " + size + ", but number of elements written was " + actualSize + ". ");
        }
    }

    protected int resolveUnion(Schema union, Object datum) {
        return this.data.resolveUnion(union, datum);
    }

    protected long getArraySize(Object array) {
        return ((Collection)array).size();
    }

    protected Iterator<?> getArrayElements(Object array) {
        return ((Collection)array).iterator();
    }

    protected void writeMap(Schema schema, Object datum, Encoder out) throws IOException {
        Schema value = schema.getValueType();
        int size = this.getMapSize(datum);
        int actualSize = 0;
        out.writeMapStart();
        out.setItemCount(size);
        for (Map.Entry<Object, Object> entry : this.getMapEntries(datum)) {
            String key;
            out.startItem();
            try {
                key = entry.getKey().toString();
            }
            catch (NullPointerException npe) {
                TracingNullPointException tnpe = new TracingNullPointException(npe, Schema.create(Schema.Type.STRING), false);
                tnpe.tracePath(new MapKeyPredicate(null));
                throw tnpe;
            }
            this.writeString(key, out);
            try {
                this.write(value, entry.getValue(), out);
            }
            catch (TracingAvroTypeException | TracingClassCastException | TracingNullPointException e) {
                ((PathTracingException)((Object)e)).tracePath(new MapKeyPredicate(key));
                throw e;
            }
            ++actualSize;
        }
        out.writeMapEnd();
        if (actualSize != size) {
            throw new ConcurrentModificationException("Size of map written was " + size + ", but number of entries written was " + actualSize + ". ");
        }
    }

    protected int getMapSize(Object map) {
        return ((Map)map).size();
    }

    protected Iterable<Map.Entry<Object, Object>> getMapEntries(Object map) {
        return ((Map)map).entrySet();
    }

    protected void writeString(Schema schema, Object datum, Encoder out) throws IOException {
        this.writeString(datum, out);
    }

    protected void writeString(Object datum, Encoder out) throws IOException {
        out.writeString((CharSequence)datum);
    }

    protected void writeBytes(Object datum, Encoder out) throws IOException {
        out.writeBytes((ByteBuffer)datum);
    }

    protected void writeFixed(Schema schema, Object datum, Encoder out) throws IOException {
        out.writeFixed(((GenericFixed)datum).bytes(), 0, schema.getFixedSize());
    }

    private void error(Schema schema, Object datum) {
        throw new AvroTypeException("value " + SchemaUtil.describe(datum) + " is not a " + SchemaUtil.describe(schema));
    }
}

