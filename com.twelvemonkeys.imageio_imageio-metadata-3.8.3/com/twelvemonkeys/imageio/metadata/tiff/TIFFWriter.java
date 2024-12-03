/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.metadata.tiff;

import com.twelvemonkeys.imageio.metadata.CompoundDirectory;
import com.twelvemonkeys.imageio.metadata.Directory;
import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.metadata.MetadataWriter;
import com.twelvemonkeys.imageio.metadata.tiff.IFD;
import com.twelvemonkeys.imageio.metadata.tiff.Rational;
import com.twelvemonkeys.imageio.metadata.tiff.TIFFEntry;
import com.twelvemonkeys.lang.Validate;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import javax.imageio.IIOException;
import javax.imageio.stream.ImageOutputStream;

public final class TIFFWriter
extends MetadataWriter {
    private static final int WORD_LENGTH = 2;
    private static final int LONGWORD_LENGTH = 4;
    private final boolean longOffsets;
    private final int offsetSize;
    private final long entryLength;
    private final int directoryCountLength;

    public TIFFWriter() {
        this(4);
    }

    public TIFFWriter(int n) {
        this.offsetSize = (Integer)Validate.isTrue((n == 4 || n == 8 ? 1 : 0) != 0, (Object)n, (String)"offsetSize must be 4 for TIFF or 8 for BigTIFF");
        this.longOffsets = n == 8;
        this.directoryCountLength = this.longOffsets ? 8 : 2;
        this.entryLength = 4 + 2 * n;
    }

    public boolean write(Collection<? extends Entry> collection, ImageOutputStream imageOutputStream) throws IOException {
        return this.write(new IFD(collection), imageOutputStream);
    }

    @Override
    public boolean write(Directory directory, ImageOutputStream imageOutputStream) throws IOException {
        Validate.notNull((Object)directory);
        Validate.notNull((Object)imageOutputStream);
        this.writeTIFFHeader(imageOutputStream);
        if (directory instanceof CompoundDirectory) {
            CompoundDirectory compoundDirectory = (CompoundDirectory)directory;
            for (int i = 0; i < compoundDirectory.directoryCount(); ++i) {
                this.writeIFD(compoundDirectory.getDirectory(i), imageOutputStream, false);
            }
        } else {
            this.writeIFD(directory, imageOutputStream, false);
        }
        this.writeOffset(imageOutputStream, 0L);
        return true;
    }

    public void writeTIFFHeader(ImageOutputStream imageOutputStream) throws IOException {
        ByteOrder byteOrder = imageOutputStream.getByteOrder();
        imageOutputStream.writeShort(byteOrder == ByteOrder.BIG_ENDIAN ? 19789 : 18761);
        imageOutputStream.writeShort(this.longOffsets ? 43 : 42);
        if (this.longOffsets) {
            imageOutputStream.writeShort(this.offsetSize);
            imageOutputStream.writeShort(0);
        }
    }

    public long writeIFD(Collection<Entry> collection, ImageOutputStream imageOutputStream) throws IOException {
        Validate.notNull(collection);
        Validate.notNull((Object)imageOutputStream);
        return this.writeIFD(new IFD(collection), imageOutputStream, false);
    }

    private long writeIFD(Directory directory, ImageOutputStream imageOutputStream, boolean bl) throws IOException {
        Directory directory2 = this.ensureOrderedDirectory(directory);
        long l = imageOutputStream.getStreamPosition();
        long l2 = this.computeDataSize(directory2);
        long l3 = imageOutputStream.getStreamPosition() + l2 + (long)this.offsetSize;
        if (!bl) {
            this.writeOffset(imageOutputStream, l3);
            l += (long)this.offsetSize;
            imageOutputStream.seek(l3);
        } else {
            l += (long)this.directoryCountLength + (long)directory2.size() * this.entryLength;
        }
        this.writeDirectoryCount(imageOutputStream, directory2.size());
        for (Entry entry : directory2) {
            imageOutputStream.writeShort((Integer)entry.getIdentifier());
            imageOutputStream.writeShort(TIFFEntry.getType(entry));
            this.writeValueCount(imageOutputStream, this.getCount(entry));
            Object object = entry.getValue();
            if (object instanceof Directory) {
                if (object instanceof CompoundDirectory) {
                    throw new AssertionError((Object)"SubIFD cannot contain linked IFDs");
                }
                long l4 = imageOutputStream.getStreamPosition() + (long)this.offsetSize;
                this.writeValueInline(l, TIFFEntry.getType(entry), imageOutputStream);
                imageOutputStream.seek(l);
                Directory directory3 = (Directory)object;
                this.writeIFD(directory3, imageOutputStream, true);
                l += this.computeDataSize(directory3);
                imageOutputStream.seek(l4);
                continue;
            }
            l += this.writeValue(entry, l, imageOutputStream);
        }
        return l3;
    }

    private void writeDirectoryCount(ImageOutputStream imageOutputStream, int n) throws IOException {
        if (this.longOffsets) {
            imageOutputStream.writeLong(n);
        } else {
            imageOutputStream.writeShort(n);
        }
    }

    private void writeValueCount(ImageOutputStream imageOutputStream, int n) throws IOException {
        if (this.longOffsets) {
            imageOutputStream.writeLong(n);
        } else {
            imageOutputStream.writeInt(n);
        }
    }

    public long computeIFDSize(Collection<? extends Entry> collection) {
        return (long)this.directoryCountLength + this.computeDataSize(new IFD(collection)) + (long)collection.size() * this.entryLength;
    }

    private long computeDataSize(Directory directory) {
        long l = 0L;
        for (Entry entry : directory) {
            long l2 = TIFFEntry.getValueLength(TIFFEntry.getType(entry), this.getCount(entry));
            if (l2 < 0L) {
                throw new IllegalArgumentException(String.format("Unknown size for entry %s", entry));
            }
            if (l2 > (long)this.offsetSize) {
                l += l2;
            }
            if (!(entry.getValue() instanceof Directory)) continue;
            Directory directory2 = (Directory)entry.getValue();
            long l3 = (long)this.directoryCountLength + this.computeDataSize(directory2) + (long)directory2.size() * this.entryLength;
            l += l3;
        }
        return l;
    }

    private Directory ensureOrderedDirectory(Directory directory) {
        if (!this.isSorted(directory)) {
            ArrayList<Entry> arrayList = new ArrayList<Entry>(directory.size());
            for (Entry entry : directory) {
                arrayList.add(entry);
            }
            Collections.sort(arrayList, new Comparator<Entry>(){

                @Override
                public int compare(Entry entry, Entry entry2) {
                    return (Integer)entry.getIdentifier() - (Integer)entry2.getIdentifier();
                }
            });
            return new IFD(arrayList);
        }
        return directory;
    }

    private boolean isSorted(Directory directory) {
        int n = 0;
        for (Entry entry : directory) {
            int n2 = (Integer)entry.getIdentifier() & 0xFFFF;
            if (n2 < n) {
                return false;
            }
            n = n2;
        }
        return true;
    }

    private long writeValue(Entry entry, long l, ImageOutputStream imageOutputStream) throws IOException {
        short s = TIFFEntry.getType(entry);
        long l2 = TIFFEntry.getValueLength(s, this.getCount(entry));
        if (l2 <= (long)this.offsetSize) {
            this.writeValueInline(entry.getValue(), s, imageOutputStream);
            for (long i = l2; i < (long)this.offsetSize; ++i) {
                imageOutputStream.write(0);
            }
            return 0L;
        }
        this.writeValueAt(l, entry.getValue(), s, imageOutputStream);
        return l2;
    }

    private int getCount(Entry entry) {
        Object object = entry.getValue();
        if (object instanceof String) {
            return this.computeStringLength((String)object);
        }
        if (object instanceof String[]) {
            return this.computeStringLength((String[])object);
        }
        return entry.valueCount();
    }

    private int computeStringLength(String ... stringArray) {
        int n = 0;
        for (String string : stringArray) {
            n += string.getBytes(StandardCharsets.UTF_8).length + 1;
        }
        return n;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void writeValueInline(Object object, short s, ImageOutputStream imageOutputStream) throws IOException {
        if (object.getClass().isArray()) {
            switch (s) {
                case 1: 
                case 6: 
                case 7: {
                    imageOutputStream.write((byte[])object);
                    return;
                }
                case 3: 
                case 8: {
                    short[] sArray;
                    if (object instanceof short[]) {
                        sArray = (short[])object;
                    } else if (object instanceof int[]) {
                        int[] nArray = (int[])object;
                        sArray = new short[nArray.length];
                        for (int i = 0; i < nArray.length; ++i) {
                            sArray[i] = (short)nArray[i];
                        }
                    } else {
                        if (!(object instanceof long[])) throw new IllegalArgumentException("Unsupported type for TIFF SHORT: " + object.getClass());
                        long[] lArray = (long[])object;
                        sArray = new short[lArray.length];
                        for (int i = 0; i < lArray.length; ++i) {
                            sArray[i] = (short)lArray[i];
                        }
                    }
                    imageOutputStream.writeShorts(sArray, 0, sArray.length);
                    return;
                }
                case 4: 
                case 9: {
                    int[] nArray;
                    if (object instanceof int[]) {
                        nArray = (int[])object;
                    } else {
                        if (!(object instanceof long[])) throw new IllegalArgumentException("Unsupported type for TIFF LONG: " + object.getClass());
                        long[] lArray = (long[])object;
                        nArray = new int[lArray.length];
                        for (int i = 0; i < lArray.length; ++i) {
                            nArray[i] = (int)lArray[i];
                        }
                    }
                    imageOutputStream.writeInts(nArray, 0, nArray.length);
                    return;
                }
                case 5: 
                case 10: {
                    Rational[] rationalArray;
                    for (Rational rational : rationalArray = (Rational[])object) {
                        imageOutputStream.writeInt((int)rational.numerator());
                        imageOutputStream.writeInt((int)rational.denominator());
                    }
                    return;
                }
                case 11: {
                    if (!(object instanceof float[])) {
                        throw new IllegalArgumentException("Unsupported type for TIFF FLOAT: " + object.getClass());
                    }
                    float[] fArray = (float[])object;
                    imageOutputStream.writeFloats(fArray, 0, fArray.length);
                    return;
                }
                case 12: {
                    if (!(object instanceof double[])) {
                        throw new IllegalArgumentException("Unsupported type for TIFF DOUBLE: " + object.getClass());
                    }
                    double[] dArray = (double[])object;
                    imageOutputStream.writeDoubles(dArray, 0, dArray.length);
                    return;
                }
                case 16: 
                case 17: {
                    if (this.longOffsets) {
                        if (!(object instanceof long[])) {
                            throw new IllegalArgumentException("Unsupported type for TIFF LONG8: " + object.getClass());
                        }
                        long[] lArray = (long[])object;
                        imageOutputStream.writeLongs(lArray, 0, lArray.length);
                        return;
                    }
                }
                case 2: {
                    this.writeStrings(imageOutputStream, (String[])object);
                    return;
                }
                default: {
                    throw new IllegalArgumentException("Unsupported TIFF type: " + s);
                }
            }
        }
        switch (s) {
            case 1: 
            case 6: 
            case 7: {
                imageOutputStream.writeByte(((Number)object).intValue());
                return;
            }
            case 2: {
                this.writeStrings(imageOutputStream, (String)object);
                return;
            }
            case 3: 
            case 8: {
                imageOutputStream.writeShort(((Number)object).intValue());
                return;
            }
            case 4: 
            case 9: 
            case 13: {
                imageOutputStream.writeInt(((Number)object).intValue());
                return;
            }
            case 5: 
            case 10: {
                Rational rational = (Rational)object;
                imageOutputStream.writeInt((int)rational.numerator());
                imageOutputStream.writeInt((int)rational.denominator());
                return;
            }
            case 11: {
                imageOutputStream.writeFloat(((Number)object).floatValue());
                return;
            }
            case 12: {
                imageOutputStream.writeDouble(((Number)object).doubleValue());
                return;
            }
            case 16: 
            case 17: 
            case 18: {
                if (!this.longOffsets) throw new IllegalArgumentException("Unsupported TIFF type: " + s);
                imageOutputStream.writeLong(((Number)object).longValue());
                return;
            }
            default: {
                throw new IllegalArgumentException("Unsupported TIFF type: " + s);
            }
        }
    }

    private void writeStrings(ImageOutputStream imageOutputStream, String ... stringArray) throws IOException {
        for (String string : stringArray) {
            imageOutputStream.write(string.getBytes(StandardCharsets.UTF_8));
            imageOutputStream.write(0);
        }
    }

    private void writeValueAt(long l, Object object, short s, ImageOutputStream imageOutputStream) throws IOException {
        this.writeOffset(imageOutputStream, l);
        long l2 = imageOutputStream.getStreamPosition();
        imageOutputStream.seek(l);
        this.writeValueInline(object, s, imageOutputStream);
        imageOutputStream.seek(l2);
    }

    public void writeOffset(ImageOutputStream imageOutputStream, long l) throws IOException {
        if (this.longOffsets) {
            imageOutputStream.writeLong(this.assertLongOffset(l));
        } else {
            imageOutputStream.writeInt(this.assertIntegerOffset(l));
        }
    }

    public int offsetSize() {
        return this.offsetSize;
    }

    private int assertIntegerOffset(long l) throws IIOException {
        if (l < 0L || l > 0xFFFFFFFFL) {
            throw new IIOException("Integer overflow for TIFF stream");
        }
        return (int)l;
    }

    private long assertLongOffset(long l) throws IIOException {
        if (l < 0L) {
            throw new IIOException("Long overflow for BigTIFF stream");
        }
        return l;
    }
}

