/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.internal.serialization.impl.PortableNavigatorContext;
import com.hazelcast.internal.serialization.impl.PortablePathCursor;
import com.hazelcast.internal.serialization.impl.PortablePosition;
import com.hazelcast.internal.serialization.impl.PortablePositionNavigator;
import com.hazelcast.internal.serialization.impl.PortableSerializer;
import com.hazelcast.internal.serialization.impl.PortableUtils;
import com.hazelcast.nio.BufferObjectDataInput;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.FieldType;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.query.extractor.ValueCallback;
import com.hazelcast.query.extractor.ValueCollector;
import com.hazelcast.query.extractor.ValueReader;
import com.hazelcast.query.extractor.ValueReadingException;
import com.hazelcast.query.impl.getters.ImmutableMultiResult;
import com.hazelcast.query.impl.getters.MultiResult;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class DefaultPortableReader
extends ValueReader
implements PortableReader {
    private static final MultiResult NULL_EMPTY_TARGET_MULTIRESULT;
    protected final ClassDefinition cd;
    protected final PortableSerializer serializer;
    private final BufferObjectDataInput in;
    private final int finalPosition;
    private final int offset;
    private final PortableNavigatorContext ctx;
    private final PortablePathCursor pathCursor;
    private boolean raw;

    DefaultPortableReader(PortableSerializer serializer, BufferObjectDataInput in, ClassDefinition cd) {
        this.in = in;
        this.serializer = serializer;
        this.cd = cd;
        this.ctx = new PortableNavigatorContext(in, cd, serializer);
        this.pathCursor = new PortablePathCursor();
        this.finalPosition = this.ctx.getCurrentFinalPosition();
        this.offset = this.ctx.getCurrentOffset();
    }

    @Override
    public int getVersion() {
        return this.cd.getVersion();
    }

    @Override
    public boolean hasField(String fieldName) {
        return this.cd.hasField(fieldName);
    }

    @Override
    public Set<String> getFieldNames() {
        return this.cd.getFieldNames();
    }

    @Override
    public FieldType getFieldType(String fieldName) {
        return this.cd.getFieldType(fieldName);
    }

    @Override
    public int getFieldClassId(String fieldName) {
        return this.cd.getFieldClassId(fieldName);
    }

    @Override
    public ObjectDataInput getRawDataInput() throws IOException {
        if (!this.raw) {
            int pos = this.in.readInt(this.offset + this.cd.getFieldCount() * 4);
            this.in.position(pos);
        }
        this.raw = true;
        return this.in;
    }

    final void end() {
        this.in.position(this.finalPosition);
    }

    @Override
    public byte readByte(String path) throws IOException {
        PortablePosition pos = this.findPositionForReading(path);
        this.validatePrimitive(pos, FieldType.BYTE);
        return this.in.readByte(pos.getStreamPosition());
    }

    @Override
    public short readShort(String path) throws IOException {
        PortablePosition pos = this.findPositionForReading(path);
        this.validatePrimitive(pos, FieldType.SHORT);
        return this.in.readShort(pos.getStreamPosition());
    }

    @Override
    public int readInt(String path) throws IOException {
        PortablePosition pos = this.findPositionForReading(path);
        this.validatePrimitive(pos, FieldType.INT);
        return this.in.readInt(pos.getStreamPosition());
    }

    @Override
    public long readLong(String path) throws IOException {
        PortablePosition pos = this.findPositionForReading(path);
        this.validatePrimitive(pos, FieldType.LONG);
        return this.in.readLong(pos.getStreamPosition());
    }

    @Override
    public float readFloat(String path) throws IOException {
        PortablePosition pos = this.findPositionForReading(path);
        this.validatePrimitive(pos, FieldType.FLOAT);
        return this.in.readFloat(pos.getStreamPosition());
    }

    @Override
    public double readDouble(String path) throws IOException {
        PortablePosition pos = this.findPositionForReading(path);
        this.validatePrimitive(pos, FieldType.DOUBLE);
        return this.in.readDouble(pos.getStreamPosition());
    }

    @Override
    public boolean readBoolean(String path) throws IOException {
        PortablePosition pos = this.findPositionForReading(path);
        this.validatePrimitive(pos, FieldType.BOOLEAN);
        return this.in.readBoolean(pos.getStreamPosition());
    }

    @Override
    public char readChar(String path) throws IOException {
        PortablePosition pos = this.findPositionForReading(path);
        this.validatePrimitive(pos, FieldType.CHAR);
        return this.in.readChar(pos.getStreamPosition());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String readUTF(String path) throws IOException {
        int currentPos = this.in.position();
        try {
            PortablePosition position = this.findPositionForReading(path);
            if (position.isNullOrEmpty()) {
                String string = null;
                return string;
            }
            this.validateNotMultiPosition(position);
            this.validateType(position, FieldType.UTF);
            this.in.position(position.getStreamPosition());
            String string = this.in.readUTF();
            return string;
        }
        finally {
            this.in.position(currentPos);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Portable readPortable(String path) throws IOException {
        int currentPos = this.in.position();
        try {
            PortablePosition position = this.findPositionForReading(path);
            if (position.isNullOrEmpty()) {
                Portable portable = null;
                return portable;
            }
            this.validateNotMultiPosition(position);
            this.validateType(position, FieldType.PORTABLE);
            this.in.position(position.getStreamPosition());
            Portable portable = this.serializer.readAndInitialize(this.in, position.getFactoryId(), position.getClassId());
            return portable;
        }
        finally {
            this.in.position(currentPos);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public byte[] readByteArray(String path) throws IOException {
        int currentPos = this.in.position();
        try {
            PortablePosition position = this.findPositionForReading(path);
            if (position.isNullOrEmpty()) {
                byte[] byArray = null;
                return byArray;
            }
            if (position.isMultiPosition()) {
                byte[] byArray = this.readMultiByteArray(position.asMultiPosition());
                return byArray;
            }
            byte[] byArray = this.readSingleByteArray(position);
            return byArray;
        }
        finally {
            this.in.position(currentPos);
        }
    }

    private byte[] readMultiByteArray(List<PortablePosition> positions) throws IOException {
        byte[] result = new byte[positions.size()];
        for (int i = 0; i < result.length; ++i) {
            PortablePosition position = positions.get(i);
            this.validateNonNullOrEmptyPosition(position);
            this.validateType(position, FieldType.BYTE);
            result[i] = this.in.readByte(position.getStreamPosition());
        }
        return result;
    }

    private byte[] readSingleByteArray(PortablePosition position) throws IOException {
        this.validateType(position, FieldType.BYTE_ARRAY);
        this.in.position(position.getStreamPosition());
        return this.in.readByteArray();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean[] readBooleanArray(String path) throws IOException {
        int currentPos = this.in.position();
        try {
            PortablePosition position = this.findPositionForReading(path);
            if (position.isNullOrEmpty()) {
                boolean[] blArray = null;
                return blArray;
            }
            if (position.isMultiPosition()) {
                boolean[] blArray = this.readMultiBooleanArray(position.asMultiPosition());
                return blArray;
            }
            boolean[] blArray = this.readSingleBooleanArray(position);
            return blArray;
        }
        finally {
            this.in.position(currentPos);
        }
    }

    private boolean[] readMultiBooleanArray(List<PortablePosition> positions) throws IOException {
        boolean[] result = new boolean[positions.size()];
        for (int i = 0; i < result.length; ++i) {
            PortablePosition position = positions.get(i);
            this.validateNonNullOrEmptyPosition(position);
            this.validateType(position, FieldType.BOOLEAN);
            result[i] = this.in.readBoolean(position.getStreamPosition());
        }
        return result;
    }

    private boolean[] readSingleBooleanArray(PortablePosition position) throws IOException {
        this.validateType(position, FieldType.BOOLEAN_ARRAY);
        this.in.position(position.getStreamPosition());
        return this.in.readBooleanArray();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public char[] readCharArray(String path) throws IOException {
        int currentPos = this.in.position();
        try {
            PortablePosition position = this.findPositionForReading(path);
            if (position.isNullOrEmpty()) {
                char[] cArray = null;
                return cArray;
            }
            if (position.isMultiPosition()) {
                char[] cArray = this.readMultiCharArray(position.asMultiPosition());
                return cArray;
            }
            char[] cArray = this.readSingleCharArray(position);
            return cArray;
        }
        finally {
            this.in.position(currentPos);
        }
    }

    private char[] readMultiCharArray(List<PortablePosition> positions) throws IOException {
        char[] result = new char[positions.size()];
        for (int i = 0; i < result.length; ++i) {
            PortablePosition position = positions.get(i);
            this.validateNonNullOrEmptyPosition(position);
            this.validateType(position, FieldType.CHAR);
            result[i] = this.in.readChar(position.getStreamPosition());
        }
        return result;
    }

    private char[] readSingleCharArray(PortablePosition position) throws IOException {
        this.validateType(position, FieldType.CHAR_ARRAY);
        this.in.position(position.getStreamPosition());
        return this.in.readCharArray();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int[] readIntArray(String path) throws IOException {
        int currentPos = this.in.position();
        try {
            PortablePosition position = this.findPositionForReading(path);
            if (position.isNullOrEmpty()) {
                int[] nArray = null;
                return nArray;
            }
            if (position.isMultiPosition()) {
                int[] nArray = this.readMultiIntArray(position.asMultiPosition());
                return nArray;
            }
            int[] nArray = this.readSingleIntArray(position);
            return nArray;
        }
        finally {
            this.in.position(currentPos);
        }
    }

    private int[] readMultiIntArray(List<PortablePosition> positions) throws IOException {
        int[] result = new int[positions.size()];
        for (int i = 0; i < result.length; ++i) {
            PortablePosition position = positions.get(i);
            this.validateNonNullOrEmptyPosition(position);
            this.validateType(position, FieldType.INT);
            result[i] = this.in.readInt(position.getStreamPosition());
        }
        return result;
    }

    private int[] readSingleIntArray(PortablePosition position) throws IOException {
        this.validateType(position, FieldType.INT_ARRAY);
        this.in.position(position.getStreamPosition());
        return this.in.readIntArray();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long[] readLongArray(String path) throws IOException {
        int currentPos = this.in.position();
        try {
            PortablePosition position = this.findPositionForReading(path);
            if (position.isNullOrEmpty()) {
                long[] lArray = null;
                return lArray;
            }
            if (position.isMultiPosition()) {
                long[] lArray = this.readMultiLongArray(position.asMultiPosition());
                return lArray;
            }
            long[] lArray = this.readSingleLongArray(position);
            return lArray;
        }
        finally {
            this.in.position(currentPos);
        }
    }

    private long[] readMultiLongArray(List<PortablePosition> positions) throws IOException {
        long[] result = new long[positions.size()];
        for (int i = 0; i < result.length; ++i) {
            PortablePosition position = positions.get(i);
            this.validateNonNullOrEmptyPosition(position);
            this.validateType(position, FieldType.LONG);
            result[i] = this.in.readLong(position.getStreamPosition());
        }
        return result;
    }

    private long[] readSingleLongArray(PortablePosition position) throws IOException {
        this.validateType(position, FieldType.LONG_ARRAY);
        this.in.position(position.getStreamPosition());
        return this.in.readLongArray();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public double[] readDoubleArray(String path) throws IOException {
        int currentPos = this.in.position();
        try {
            PortablePosition position = this.findPositionForReading(path);
            if (position.isNullOrEmpty()) {
                double[] dArray = null;
                return dArray;
            }
            if (position.isMultiPosition()) {
                double[] dArray = this.readMultiDoubleArray(position.asMultiPosition());
                return dArray;
            }
            double[] dArray = this.readSingleDoubleArray(position);
            return dArray;
        }
        finally {
            this.in.position(currentPos);
        }
    }

    private double[] readMultiDoubleArray(List<PortablePosition> positions) throws IOException {
        double[] result = new double[positions.size()];
        for (int i = 0; i < result.length; ++i) {
            PortablePosition position = positions.get(i);
            this.validateNonNullOrEmptyPosition(position);
            this.validateType(position, FieldType.DOUBLE);
            result[i] = this.in.readDouble(position.getStreamPosition());
        }
        return result;
    }

    private double[] readSingleDoubleArray(PortablePosition position) throws IOException {
        this.validateType(position, FieldType.DOUBLE_ARRAY);
        this.in.position(position.getStreamPosition());
        return this.in.readDoubleArray();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public float[] readFloatArray(String path) throws IOException {
        int currentPos = this.in.position();
        try {
            PortablePosition position = this.findPositionForReading(path);
            if (position.isNullOrEmpty()) {
                float[] fArray = null;
                return fArray;
            }
            if (position.isMultiPosition()) {
                float[] fArray = this.readMultiFloatArray(position.asMultiPosition());
                return fArray;
            }
            float[] fArray = this.readSingleFloatArray(position);
            return fArray;
        }
        finally {
            this.in.position(currentPos);
        }
    }

    private float[] readMultiFloatArray(List<PortablePosition> positions) throws IOException {
        float[] result = new float[positions.size()];
        for (int i = 0; i < result.length; ++i) {
            PortablePosition position = positions.get(i);
            this.validateNonNullOrEmptyPosition(position);
            this.validateType(position, FieldType.FLOAT);
            result[i] = this.in.readFloat(position.getStreamPosition());
        }
        return result;
    }

    private float[] readSingleFloatArray(PortablePosition position) throws IOException {
        this.validateType(position, FieldType.FLOAT_ARRAY);
        this.in.position(position.getStreamPosition());
        return this.in.readFloatArray();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public short[] readShortArray(String path) throws IOException {
        int currentPos = this.in.position();
        try {
            PortablePosition position = this.findPositionForReading(path);
            if (position.isNullOrEmpty()) {
                short[] sArray = null;
                return sArray;
            }
            if (position.isMultiPosition()) {
                short[] sArray = this.readMultiShortArray(position.asMultiPosition());
                return sArray;
            }
            short[] sArray = this.readSingleShortArray(position);
            return sArray;
        }
        finally {
            this.in.position(currentPos);
        }
    }

    private short[] readMultiShortArray(List<PortablePosition> positions) throws IOException {
        short[] result = new short[positions.size()];
        for (int i = 0; i < result.length; ++i) {
            PortablePosition position = positions.get(i);
            this.validateNonNullOrEmptyPosition(position);
            this.validateType(position, FieldType.SHORT);
            result[i] = this.in.readShort(position.getStreamPosition());
        }
        return result;
    }

    private short[] readSingleShortArray(PortablePosition position) throws IOException {
        this.validateType(position, FieldType.SHORT_ARRAY);
        this.in.position(position.getStreamPosition());
        return this.in.readShortArray();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String[] readUTFArray(String path) throws IOException {
        int currentPos = this.in.position();
        try {
            PortablePosition position = this.findPositionForReading(path);
            if (position.isNullOrEmpty()) {
                String[] stringArray = null;
                return stringArray;
            }
            if (position.isMultiPosition()) {
                String[] stringArray = this.readMultiUTFArray(position.asMultiPosition());
                return stringArray;
            }
            String[] stringArray = this.readSingleUTFArray(position);
            return stringArray;
        }
        finally {
            this.in.position(currentPos);
        }
    }

    private String[] readMultiUTFArray(List<PortablePosition> positions) throws IOException {
        String[] result = new String[positions.size()];
        for (int i = 0; i < result.length; ++i) {
            PortablePosition position = positions.get(i);
            if (position.isNullOrEmpty()) continue;
            this.validateType(position, FieldType.UTF);
            this.in.position(position.getStreamPosition());
            result[i] = this.in.readUTF();
        }
        return result;
    }

    private String[] readSingleUTFArray(PortablePosition position) throws IOException {
        this.validateType(position, FieldType.UTF_ARRAY);
        this.in.position(position.getStreamPosition());
        return this.in.readUTFArray();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Portable[] readPortableArray(String fieldName) throws IOException {
        int currentPos = this.in.position();
        try {
            PortablePosition position = this.findPositionForReading(fieldName);
            if (position.isMultiPosition()) {
                Portable[] portableArray = this.readMultiPortableArray(position.asMultiPosition());
                return portableArray;
            }
            if (position.isNull()) {
                Portable[] portableArray = null;
                return portableArray;
            }
            if (position.isEmpty() && position.isAny()) {
                Portable[] portableArray = null;
                return portableArray;
            }
            Portable[] portableArray = this.readSinglePortableArray(position);
            return portableArray;
        }
        finally {
            this.in.position(currentPos);
        }
    }

    private Portable[] readSinglePortableArray(PortablePosition position) throws IOException {
        this.in.position(position.getStreamPosition());
        if (position.getLen() == -1) {
            return null;
        }
        this.validateType(position, FieldType.PORTABLE_ARRAY);
        Portable[] portables = new Portable[position.getLen()];
        for (int index = 0; index < position.getLen(); ++index) {
            this.in.position(PortableUtils.getPortableArrayCellPosition(this.in, position.getStreamPosition(), index));
            portables[index] = this.serializer.readAndInitialize(this.in, position.getFactoryId(), position.getClassId());
        }
        return portables;
    }

    private Portable[] readMultiPortableArray(List<PortablePosition> positions) throws IOException {
        Portable[] portables = new Portable[positions.size()];
        for (int i = 0; i < portables.length; ++i) {
            PortablePosition position = positions.get(i);
            if (position.isNullOrEmpty()) continue;
            this.validateType(position, FieldType.PORTABLE);
            this.in.position(position.getStreamPosition());
            portables[i] = this.serializer.readAndInitialize(this.in, position.getFactoryId(), position.getClassId());
        }
        return portables;
    }

    public void read(String path, ValueCallback callback) {
        try {
            Object result = this.read(path);
            if (result instanceof MultiResult) {
                MultiResult multiResult = (MultiResult)result;
                for (Object singleResult : multiResult.getResults()) {
                    callback.onResult(singleResult);
                }
            } else {
                callback.onResult(result);
            }
        }
        catch (IOException e) {
            throw new ValueReadingException(e.getMessage(), e);
        }
        catch (RuntimeException e) {
            throw new ValueReadingException(e.getMessage(), e);
        }
    }

    public void read(String path, ValueCollector collector) {
        try {
            Object result = this.read(path);
            if (result instanceof MultiResult) {
                MultiResult multiResult = (MultiResult)result;
                for (Object singleResult : multiResult.getResults()) {
                    collector.addObject(singleResult);
                }
            } else {
                collector.addObject(result);
            }
        }
        catch (IOException e) {
            throw new ValueReadingException(e.getMessage(), e);
        }
        catch (RuntimeException e) {
            throw new ValueReadingException(e.getMessage(), e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object read(String path) throws IOException {
        int currentPos = this.in.position();
        try {
            PortablePosition position = this.findPositionForReading(path);
            if (position.isMultiPosition()) {
                MultiResult multiResult = this.readMultiPosition(position.asMultiPosition());
                return multiResult;
            }
            if (position.isNull()) {
                if (position.isAny()) {
                    MultiResult multiResult = NULL_EMPTY_TARGET_MULTIRESULT;
                    return multiResult;
                }
                Object var4_6 = null;
                return var4_6;
            }
            if (position.isEmpty()) {
                if (position.isLeaf() && position.getType() != null) {
                    Object t = this.readSinglePosition(position);
                    return t;
                }
                if (position.isAny()) {
                    MultiResult multiResult = NULL_EMPTY_TARGET_MULTIRESULT;
                    return multiResult;
                }
                Object var4_9 = null;
                return var4_9;
            }
            Object t = this.readSinglePosition(position);
            return t;
        }
        finally {
            this.in.position(currentPos);
        }
    }

    private <T> MultiResult<T> readMultiPosition(List<PortablePosition> positions) throws IOException {
        MultiResult<T> result = new MultiResult<T>();
        for (PortablePosition position : positions) {
            if (!position.isNullOrEmpty()) {
                T read = this.readSinglePosition(position);
                result.add(read);
                continue;
            }
            result.addNullOrEmptyTarget();
        }
        return result;
    }

    private <T> T readSinglePosition(PortablePosition position) throws IOException {
        if (position.getIndex() >= 0) {
            return this.readSinglePositionFromArray(position);
        }
        return this.readSinglePositionFromNonArray(position);
    }

    private PortablePosition findPositionForReading(String path) throws IOException {
        if (this.raw) {
            throw new HazelcastSerializationException("Cannot read Portable fields after getRawDataInput() is called!");
        }
        try {
            PortablePosition portablePosition = PortablePositionNavigator.findPositionForReading(this.ctx, path, this.pathCursor);
            return portablePosition;
        }
        finally {
            this.ctx.reset();
        }
    }

    private <T> T readSinglePositionFromArray(PortablePosition position) throws IOException {
        assert (position.getType() != null) : "Unsupported type read: null";
        switch (position.getType()) {
            case BYTE: 
            case BYTE_ARRAY: {
                return (T)Byte.valueOf(this.in.readByte(position.getStreamPosition()));
            }
            case SHORT: 
            case SHORT_ARRAY: {
                return (T)Short.valueOf(this.in.readShort(position.getStreamPosition()));
            }
            case INT: 
            case INT_ARRAY: {
                return (T)Integer.valueOf(this.in.readInt(position.getStreamPosition()));
            }
            case LONG: 
            case LONG_ARRAY: {
                return (T)Long.valueOf(this.in.readLong(position.getStreamPosition()));
            }
            case FLOAT: 
            case FLOAT_ARRAY: {
                return (T)Float.valueOf(this.in.readFloat(position.getStreamPosition()));
            }
            case DOUBLE: 
            case DOUBLE_ARRAY: {
                return (T)Double.valueOf(this.in.readDouble(position.getStreamPosition()));
            }
            case BOOLEAN: 
            case BOOLEAN_ARRAY: {
                return (T)Boolean.valueOf(this.in.readBoolean(position.getStreamPosition()));
            }
            case CHAR: 
            case CHAR_ARRAY: {
                return (T)Character.valueOf(this.in.readChar(position.getStreamPosition()));
            }
            case UTF: 
            case UTF_ARRAY: {
                this.in.position(position.getStreamPosition());
                return (T)this.in.readUTF();
            }
            case PORTABLE: 
            case PORTABLE_ARRAY: {
                this.in.position(position.getStreamPosition());
                return (T)this.serializer.readAndInitialize(this.in, position.getFactoryId(), position.getClassId());
            }
        }
        throw new IllegalArgumentException("Unsupported type: " + (Object)((Object)position.getType()));
    }

    private <T> T readSinglePositionFromNonArray(PortablePosition position) throws IOException {
        assert (position.getType() != null) : "Unsupported type read: null";
        switch (position.getType()) {
            case BYTE: {
                return (T)Byte.valueOf(this.in.readByte(position.getStreamPosition()));
            }
            case BYTE_ARRAY: {
                return (T)this.readSingleByteArray(position);
            }
            case SHORT: {
                return (T)Short.valueOf(this.in.readShort(position.getStreamPosition()));
            }
            case SHORT_ARRAY: {
                return (T)this.readSingleShortArray(position);
            }
            case INT: {
                return (T)Integer.valueOf(this.in.readInt(position.getStreamPosition()));
            }
            case INT_ARRAY: {
                return (T)this.readSingleIntArray(position);
            }
            case LONG: {
                return (T)Long.valueOf(this.in.readLong(position.getStreamPosition()));
            }
            case LONG_ARRAY: {
                return (T)this.readSingleLongArray(position);
            }
            case FLOAT: {
                return (T)Float.valueOf(this.in.readFloat(position.getStreamPosition()));
            }
            case FLOAT_ARRAY: {
                return (T)this.readSingleFloatArray(position);
            }
            case DOUBLE: {
                return (T)Double.valueOf(this.in.readDouble(position.getStreamPosition()));
            }
            case DOUBLE_ARRAY: {
                return (T)this.readSingleDoubleArray(position);
            }
            case BOOLEAN: {
                return (T)Boolean.valueOf(this.in.readBoolean(position.getStreamPosition()));
            }
            case BOOLEAN_ARRAY: {
                return (T)this.readSingleBooleanArray(position);
            }
            case CHAR: {
                return (T)Character.valueOf(this.in.readChar(position.getStreamPosition()));
            }
            case CHAR_ARRAY: {
                return (T)this.readSingleCharArray(position);
            }
            case UTF: {
                this.in.position(position.getStreamPosition());
                return (T)this.in.readUTF();
            }
            case UTF_ARRAY: {
                return (T)this.readSingleUTFArray(position);
            }
            case PORTABLE: {
                this.in.position(position.getStreamPosition());
                return (T)this.serializer.readAndInitialize(this.in, position.getFactoryId(), position.getClassId());
            }
            case PORTABLE_ARRAY: {
                return (T)this.readSinglePortableArray(position);
            }
        }
        throw new IllegalArgumentException("Unsupported type " + (Object)((Object)position.getType()));
    }

    private void validatePrimitive(PortablePosition position, FieldType expectedType) {
        this.validateNonNullOrEmptyPosition(position);
        this.validateNotMultiPosition(position);
        this.validateType(position, expectedType);
    }

    private void validateNonNullOrEmptyPosition(PortablePosition position) {
        if (position.isNullOrEmpty()) {
            throw new IllegalArgumentException("Primitive type cannot be returned since the result is/contains null.");
        }
    }

    private void validateNotMultiPosition(PortablePosition position) {
        if (position.isMultiPosition()) {
            throw new IllegalArgumentException("The method expected a single result but multiple results have been returned. Did you use the [any] quantifier? If so, use the readArray method family.");
        }
    }

    private void validateType(PortablePosition position, FieldType expectedType) {
        FieldType returnedType = position.getType();
        if (position.getIndex() >= 0) {
            FieldType fieldType = returnedType = returnedType != null ? returnedType.getSingleType() : null;
        }
        if (expectedType != returnedType) {
            String name = returnedType != null ? returnedType.name() : null;
            throw new IllegalArgumentException("Wrong type read! Actual: " + name + " Expected: " + expectedType.name() + ". Did you use a correct read method? E.g. readInt() for int.");
        }
    }

    static {
        MultiResult result = new MultiResult();
        result.addNullOrEmptyTarget();
        NULL_EMPTY_TARGET_MULTIRESULT = new ImmutableMultiResult(result);
    }
}

