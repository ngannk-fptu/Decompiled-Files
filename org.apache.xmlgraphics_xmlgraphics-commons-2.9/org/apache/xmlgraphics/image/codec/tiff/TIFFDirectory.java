/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.codec.tiff;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.xmlgraphics.image.codec.tiff.TIFFField;
import org.apache.xmlgraphics.image.codec.util.PropertyUtil;
import org.apache.xmlgraphics.image.codec.util.SeekableStream;

public class TIFFDirectory
implements Serializable {
    private static final long serialVersionUID = 2007844835460959003L;
    boolean isBigEndian;
    int numEntries;
    TIFFField[] fields;
    Map fieldIndex = new HashMap();
    long ifdOffset = 8L;
    long nextIFDOffset;
    private static final int[] SIZE_OF_TYPE = new int[]{0, 1, 1, 2, 4, 8, 1, 1, 2, 4, 8, 4, 8};

    TIFFDirectory() {
    }

    private static boolean isValidEndianTag(int endian) {
        return endian == 18761 || endian == 19789;
    }

    public TIFFDirectory(SeekableStream stream, int directory) throws IOException {
        long globalSaveOffset = stream.getFilePointer();
        stream.seek(0L);
        int endian = stream.readUnsignedShort();
        if (!TIFFDirectory.isValidEndianTag(endian)) {
            throw new IllegalArgumentException(PropertyUtil.getString("TIFFDirectory1"));
        }
        this.isBigEndian = endian == 19789;
        int magic = this.readUnsignedShort(stream);
        if (magic != 42) {
            throw new IllegalArgumentException(PropertyUtil.getString("TIFFDirectory2"));
        }
        long ifdOffset = this.readUnsignedInt(stream);
        for (int i = 0; i < directory; ++i) {
            if (ifdOffset == 0L) {
                throw new IllegalArgumentException(PropertyUtil.getString("TIFFDirectory3"));
            }
            stream.seek(ifdOffset);
            long entries = this.readUnsignedShort(stream);
            stream.skip(12L * entries);
            ifdOffset = this.readUnsignedInt(stream);
        }
        if (ifdOffset == 0L) {
            throw new IllegalArgumentException(PropertyUtil.getString("TIFFDirectory3"));
        }
        stream.seek(ifdOffset);
        this.initialize(stream);
        stream.seek(globalSaveOffset);
    }

    public TIFFDirectory(SeekableStream stream, long ifdOffset, int directory) throws IOException {
        long globalSaveOffset = stream.getFilePointer();
        stream.seek(0L);
        int endian = stream.readUnsignedShort();
        if (!TIFFDirectory.isValidEndianTag(endian)) {
            throw new IllegalArgumentException(PropertyUtil.getString("TIFFDirectory1"));
        }
        this.isBigEndian = endian == 19789;
        stream.seek(ifdOffset);
        for (int dirNum = 0; dirNum < directory; ++dirNum) {
            long numEntries = this.readUnsignedShort(stream);
            stream.seek(ifdOffset + 12L * numEntries);
            ifdOffset = this.readUnsignedInt(stream);
            stream.seek(ifdOffset);
        }
        this.initialize(stream);
        stream.seek(globalSaveOffset);
    }

    private void initialize(SeekableStream stream) throws IOException {
        this.ifdOffset = stream.getFilePointer();
        this.numEntries = this.readUnsignedShort(stream);
        this.fields = new TIFFField[this.numEntries];
        for (int i = 0; i < this.numEntries; ++i) {
            long nextTagOffset;
            int count;
            int type;
            int tag;
            block25: {
                tag = this.readUnsignedShort(stream);
                type = this.readUnsignedShort(stream);
                count = (int)this.readUnsignedInt(stream);
                int value = 0;
                nextTagOffset = stream.getFilePointer() + 4L;
                try {
                    if (count * SIZE_OF_TYPE[type] <= 4) break block25;
                    value = (int)this.readUnsignedInt(stream);
                    stream.seek(value);
                }
                catch (ArrayIndexOutOfBoundsException ae) {
                    stream.seek(nextTagOffset);
                    continue;
                }
            }
            this.fieldIndex.put(tag, i);
            Object obj = null;
            switch (type) {
                case 1: 
                case 2: 
                case 6: 
                case 7: {
                    byte[] bvalues = new byte[count];
                    stream.readFully(bvalues, 0, count);
                    if (type == 2) {
                        int index = 0;
                        int prevIndex = 0;
                        ArrayList<String> v = new ArrayList<String>();
                        while (index < count) {
                            while (index < count && bvalues[index++] != 0) {
                            }
                            v.add(new String(bvalues, prevIndex, index - prevIndex, "UTF-8"));
                            prevIndex = index;
                        }
                        count = v.size();
                        String[] strings = new String[count];
                        v.toArray(strings);
                        obj = strings;
                        break;
                    }
                    obj = bvalues;
                    break;
                }
                case 3: {
                    int j;
                    char[] cvalues = new char[count];
                    for (j = 0; j < count; ++j) {
                        cvalues[j] = (char)this.readUnsignedShort(stream);
                    }
                    obj = cvalues;
                    break;
                }
                case 4: {
                    int j;
                    long[] lvalues = new long[count];
                    for (j = 0; j < count; ++j) {
                        lvalues[j] = this.readUnsignedInt(stream);
                    }
                    obj = lvalues;
                    break;
                }
                case 5: {
                    int j;
                    long[][] llvalues = new long[count][2];
                    for (j = 0; j < count; ++j) {
                        llvalues[j][0] = this.readUnsignedInt(stream);
                        llvalues[j][1] = this.readUnsignedInt(stream);
                    }
                    obj = llvalues;
                    break;
                }
                case 8: {
                    int j;
                    short[] svalues = new short[count];
                    for (j = 0; j < count; ++j) {
                        svalues[j] = this.readShort(stream);
                    }
                    obj = svalues;
                    break;
                }
                case 9: {
                    int j;
                    int[] ivalues = new int[count];
                    for (j = 0; j < count; ++j) {
                        ivalues[j] = this.readInt(stream);
                    }
                    obj = ivalues;
                    break;
                }
                case 10: {
                    int j;
                    int[][] iivalues = new int[count][2];
                    for (j = 0; j < count; ++j) {
                        iivalues[j][0] = this.readInt(stream);
                        iivalues[j][1] = this.readInt(stream);
                    }
                    obj = iivalues;
                    break;
                }
                case 11: {
                    int j;
                    float[] fvalues = new float[count];
                    for (j = 0; j < count; ++j) {
                        fvalues[j] = this.readFloat(stream);
                    }
                    obj = fvalues;
                    break;
                }
                case 12: {
                    int j;
                    double[] dvalues = new double[count];
                    for (j = 0; j < count; ++j) {
                        dvalues[j] = this.readDouble(stream);
                    }
                    obj = dvalues;
                    break;
                }
                default: {
                    throw new RuntimeException(PropertyUtil.getString("TIFFDirectory0"));
                }
            }
            this.fields[i] = new TIFFField(tag, type, count, obj);
            stream.seek(nextTagOffset);
        }
        this.nextIFDOffset = this.readUnsignedInt(stream);
    }

    public int getNumEntries() {
        return this.numEntries;
    }

    public TIFFField getField(int tag) {
        Integer i = (Integer)this.fieldIndex.get(tag);
        if (i == null) {
            return null;
        }
        return this.fields[i];
    }

    public boolean isTagPresent(int tag) {
        return this.fieldIndex.containsKey(tag);
    }

    public int[] getTags() {
        int[] tags = new int[this.fieldIndex.size()];
        Iterator iter = this.fieldIndex.keySet().iterator();
        int i = 0;
        while (iter.hasNext()) {
            tags[i++] = (Integer)iter.next();
        }
        return tags;
    }

    public TIFFField[] getFields() {
        return this.fields;
    }

    public byte getFieldAsByte(int tag, int index) {
        Integer i = (Integer)this.fieldIndex.get(tag);
        byte[] b = this.fields[i].getAsBytes();
        return b[index];
    }

    public byte getFieldAsByte(int tag) {
        return this.getFieldAsByte(tag, 0);
    }

    public long getFieldAsLong(int tag, int index) {
        Integer i = (Integer)this.fieldIndex.get(tag);
        return this.fields[i].getAsLong(index);
    }

    public long getFieldAsLong(int tag) {
        return this.getFieldAsLong(tag, 0);
    }

    public float getFieldAsFloat(int tag, int index) {
        Integer i = (Integer)this.fieldIndex.get(tag);
        return this.fields[i].getAsFloat(index);
    }

    public float getFieldAsFloat(int tag) {
        return this.getFieldAsFloat(tag, 0);
    }

    public double getFieldAsDouble(int tag, int index) {
        Integer i = (Integer)this.fieldIndex.get(tag);
        return this.fields[i].getAsDouble(index);
    }

    public double getFieldAsDouble(int tag) {
        return this.getFieldAsDouble(tag, 0);
    }

    private short readShort(SeekableStream stream) throws IOException {
        if (this.isBigEndian) {
            return stream.readShort();
        }
        return stream.readShortLE();
    }

    private int readUnsignedShort(SeekableStream stream) throws IOException {
        if (this.isBigEndian) {
            return stream.readUnsignedShort();
        }
        return stream.readUnsignedShortLE();
    }

    private int readInt(SeekableStream stream) throws IOException {
        if (this.isBigEndian) {
            return stream.readInt();
        }
        return stream.readIntLE();
    }

    private long readUnsignedInt(SeekableStream stream) throws IOException {
        if (this.isBigEndian) {
            return stream.readUnsignedInt();
        }
        return stream.readUnsignedIntLE();
    }

    private float readFloat(SeekableStream stream) throws IOException {
        if (this.isBigEndian) {
            return stream.readFloat();
        }
        return stream.readFloatLE();
    }

    private double readDouble(SeekableStream stream) throws IOException {
        if (this.isBigEndian) {
            return stream.readDouble();
        }
        return stream.readDoubleLE();
    }

    private static int readUnsignedShort(SeekableStream stream, boolean isBigEndian) throws IOException {
        if (isBigEndian) {
            return stream.readUnsignedShort();
        }
        return stream.readUnsignedShortLE();
    }

    private static long readUnsignedInt(SeekableStream stream, boolean isBigEndian) throws IOException {
        if (isBigEndian) {
            return stream.readUnsignedInt();
        }
        return stream.readUnsignedIntLE();
    }

    public static int getNumDirectories(SeekableStream stream) throws IOException {
        long pointer = stream.getFilePointer();
        stream.seek(0L);
        int endian = stream.readUnsignedShort();
        if (!TIFFDirectory.isValidEndianTag(endian)) {
            throw new IllegalArgumentException(PropertyUtil.getString("TIFFDirectory1"));
        }
        boolean isBigEndian = endian == 19789;
        int magic = TIFFDirectory.readUnsignedShort(stream, isBigEndian);
        if (magic != 42) {
            throw new IllegalArgumentException(PropertyUtil.getString("TIFFDirectory2"));
        }
        stream.seek(4L);
        long offset = TIFFDirectory.readUnsignedInt(stream, isBigEndian);
        int numDirectories = 0;
        while (offset != 0L) {
            ++numDirectories;
            stream.seek(offset);
            long entries = TIFFDirectory.readUnsignedShort(stream, isBigEndian);
            stream.skip(12L * entries);
            offset = TIFFDirectory.readUnsignedInt(stream, isBigEndian);
        }
        stream.seek(pointer);
        return numDirectories;
    }

    public boolean isBigEndian() {
        return this.isBigEndian;
    }

    public long getIFDOffset() {
        return this.ifdOffset;
    }

    public long getNextIFDOffset() {
        return this.nextIFDOffset;
    }
}

