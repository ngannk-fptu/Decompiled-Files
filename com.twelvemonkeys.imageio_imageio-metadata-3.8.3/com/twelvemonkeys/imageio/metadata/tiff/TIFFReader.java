/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.StringUtil
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.metadata.tiff;

import com.twelvemonkeys.imageio.metadata.Directory;
import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.metadata.MetadataReader;
import com.twelvemonkeys.imageio.metadata.tiff.IFD;
import com.twelvemonkeys.imageio.metadata.tiff.Rational;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import com.twelvemonkeys.imageio.metadata.tiff.TIFFDirectory;
import com.twelvemonkeys.imageio.metadata.tiff.TIFFEntry;
import com.twelvemonkeys.imageio.metadata.tiff.Unknown;
import com.twelvemonkeys.lang.StringUtil;
import com.twelvemonkeys.lang.Validate;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

public final class TIFFReader
extends MetadataReader {
    static final boolean DEBUG = "true".equalsIgnoreCase(System.getProperty("com.twelvemonkeys.imageio.metadata.tiff.debug"));
    private static final Collection<Integer> VALID_TOP_LEVEL_IFDS = Collections.unmodifiableCollection(Arrays.asList(330, 34665, 34853));
    private static final Map<Integer, Collection<Integer>> VALID_SUB_IFDS = TIFFReader.createSubIFDMap();
    private final Set<Long> parsedIFDs = new TreeSet<Long>();
    private long inputLength;
    private boolean longOffsets;
    private int offsetSize;

    private static Map<Integer, Collection<Integer>> createSubIFDMap() {
        HashMap<Integer, Collection<Integer>> hashMap = new HashMap<Integer, Collection<Integer>>(){

            @Override
            public Collection<Integer> get(Object object) {
                Set<Integer> set = (Set<Integer>)super.get(object);
                return set != null ? set : Collections.emptySet();
            }
        };
        hashMap.put(330, Collections.singleton(330));
        hashMap.put(34665, Collections.singleton(40965));
        return Collections.unmodifiableMap(hashMap);
    }

    @Override
    public Directory read(ImageInputStream imageInputStream) throws IOException {
        Validate.notNull((Object)imageInputStream, (String)"input");
        byte[] byArray = new byte[2];
        imageInputStream.readFully(byArray);
        if (byArray[0] == 73 && byArray[1] == 73) {
            imageInputStream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        } else if (byArray[0] == 77 && byArray[1] == 77) {
            imageInputStream.setByteOrder(ByteOrder.BIG_ENDIAN);
        } else {
            throw new IIOException(String.format("Invalid TIFF byte order mark '%s', expected: 'II' or 'MM'", StringUtil.decode((byte[])byArray, (int)0, (int)byArray.length, (String)"ASCII")));
        }
        int n = imageInputStream.readUnsignedShort();
        if (n == 42) {
            this.longOffsets = false;
            this.offsetSize = 4;
        } else if (n == 43) {
            this.longOffsets = true;
            this.offsetSize = 8;
            int n2 = imageInputStream.readUnsignedShort();
            if (n2 != 8) {
                throw new IIOException(String.format("Unexpected BigTIFF offset size: %04x, expected: %04x", n2, 8));
            }
            int n3 = imageInputStream.readUnsignedShort();
            if (n3 != 0) {
                throw new IIOException(String.format("Unexpected BigTIFF padding: %04x, expected: %04x", n3, 0));
            }
        } else {
            throw new IIOException(String.format("Wrong TIFF magic in input data: %04x, expected: %04x", n, 42));
        }
        this.inputLength = imageInputStream.length();
        return this.readLinkedIFDs(imageInputStream);
    }

    private TIFFDirectory readLinkedIFDs(ImageInputStream imageInputStream) throws IOException {
        long l = this.readOffset(imageInputStream);
        ArrayList<IFD> arrayList = new ArrayList<IFD>();
        while (l != 0L) {
            try {
                if (this.inputLength > 0L && l >= this.inputLength || !this.isValidOffset(imageInputStream, l) || !this.parsedIFDs.add(l)) {
                    if (!DEBUG) break;
                    System.err.println("Bad IFD offset: " + l);
                    break;
                }
                arrayList.add(this.readIFD(imageInputStream, l, VALID_TOP_LEVEL_IFDS));
                l = this.readOffset(imageInputStream);
            }
            catch (EOFException eOFException) {
                l = 0L;
            }
        }
        return new TIFFDirectory((Collection<? extends Directory>)arrayList);
    }

    private long readOffset(ImageInputStream imageInputStream) throws IOException {
        return this.longOffsets ? imageInputStream.readLong() : imageInputStream.readUnsignedInt();
    }

    private IFD readIFD(ImageInputStream imageInputStream, long l, Collection<Integer> collection) throws IOException {
        imageInputStream.seek(l);
        long l2 = this.readEntryCount(imageInputStream);
        ArrayList<TIFFEntry> arrayList = new ArrayList<TIFFEntry>();
        int n = 0;
        while ((long)n < l2) {
            block3: {
                try {
                    TIFFEntry tIFFEntry = this.readEntry(imageInputStream);
                    if (tIFFEntry == null) break block3;
                    arrayList.add(tIFFEntry);
                }
                catch (IIOException iIOException) {
                    if (!DEBUG) break;
                    iIOException.printStackTrace();
                    break;
                }
            }
            ++n;
        }
        this.readSubIFDs(imageInputStream, arrayList, collection);
        return new IFD(arrayList);
    }

    private long readEntryCount(ImageInputStream imageInputStream) throws IOException {
        return this.longOffsets ? imageInputStream.readLong() : (long)imageInputStream.readUnsignedShort();
    }

    private void readSubIFDs(ImageInputStream imageInputStream, List<TIFFEntry> list, Collection<Integer> collection) throws IOException {
        if (collection == null || collection.isEmpty()) {
            return;
        }
        long l = imageInputStream.getStreamPosition();
        int n = list.size();
        for (int i = 0; i < n; ++i) {
            TIFFEntry tIFFEntry = list.get(i);
            int n2 = (Integer)tIFFEntry.getIdentifier();
            if (!collection.contains(n2)) continue;
            try {
                long[] lArray = this.getPointerOffsets(tIFFEntry);
                ArrayList<IFD> arrayList = new ArrayList<IFD>(lArray.length);
                for (long l2 : lArray) {
                    try {
                        if (this.inputLength > 0L && l2 >= this.inputLength || !this.isValidOffset(imageInputStream, l2) || !this.parsedIFDs.add(l2)) {
                            if (!DEBUG) break;
                            System.err.println("Bad IFD offset: " + l2);
                            break;
                        }
                        arrayList.add(this.readIFD(imageInputStream, l2, VALID_SUB_IFDS.get(n2)));
                    }
                    catch (EOFException eOFException) {
                        if (!DEBUG) continue;
                        eOFException.printStackTrace();
                    }
                }
                if (arrayList.size() == 1) {
                    list.set(i, new TIFFEntry(n2, tIFFEntry.getType(), arrayList.get(0)));
                    continue;
                }
                if (arrayList.isEmpty()) continue;
                list.set(i, new TIFFEntry(n2, tIFFEntry.getType(), arrayList.toArray(new IFD[0])));
                continue;
            }
            catch (IIOException iIOException) {
                if (!DEBUG) continue;
                System.err.println("Error parsing sub-IFD: " + n2);
                iIOException.printStackTrace();
            }
        }
        imageInputStream.seek(l);
    }

    private long[] getPointerOffsets(Entry entry) throws IIOException {
        long[] lArray;
        Object object = entry.getValue();
        if (object instanceof Byte) {
            lArray = new long[]{(Byte)object & 0xFF};
        } else if (object instanceof Short) {
            lArray = new long[]{(Short)object & 0xFFFF};
        } else if (object instanceof Integer) {
            lArray = new long[]{(long)((Integer)object).intValue() & 0xFFFFFFFFL};
        } else if (object instanceof Long) {
            lArray = new long[]{(Long)object};
        } else if (object instanceof long[]) {
            lArray = (long[])object;
        } else {
            throw new IIOException(String.format("Unknown pointer type: %s", object != null ? object.getClass() : null));
        }
        return lArray;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private TIFFEntry readEntry(ImageInputStream imageInputStream) throws IOException {
        Object object;
        int n = imageInputStream.readUnsignedShort();
        short s = imageInputStream.readShort();
        int n2 = this.readValueCount(imageInputStream);
        if (n2 < 0) {
            throw new IIOException(String.format("Illegal count %d for tag %s type %s @%08x", n2, n, s, imageInputStream.getStreamPosition()));
        }
        if (!this.isValidType(s)) {
            imageInputStream.skipBytes(4);
            if (DEBUG) {
                long l = imageInputStream.getStreamPosition() - 12L;
                System.err.printf("Bad TIFF data @%08x\n", imageInputStream.getStreamPosition());
                System.err.println("tagId: " + n + (n <= 0 ? " (INVALID)" : ""));
                System.err.println("type: " + s + " (INVALID)");
                System.err.println("count: " + n2);
                imageInputStream.mark();
                try {
                    imageInputStream.seek(l);
                    byte[] byArray = new byte[8 + Math.min(120, Math.max(24, n2))];
                    int n3 = imageInputStream.read(byArray);
                    System.err.print(HexDump.dump(l, byArray, 0, n3));
                    System.err.println(n3 < n2 ? "[...]" : "");
                }
                finally {
                    imageInputStream.reset();
                }
            }
            return null;
        }
        long l = TIFFEntry.getValueLength(s, n2);
        if (l > 0L && l <= (long)this.offsetSize) {
            object = this.readValueInLine(imageInputStream, s, n2);
            imageInputStream.skipBytes((long)this.offsetSize - l);
        } else {
            long l2 = this.readOffset(imageInputStream);
            object = this.readValueAt(imageInputStream, l2, l, s, n2);
        }
        return new TIFFEntry(n, s, object);
    }

    private boolean isValidType(short s) {
        return s > 0 && s < TIFF.TYPE_LENGTHS.length && TIFF.TYPE_LENGTHS[s] > 0;
    }

    private int readValueCount(ImageInputStream imageInputStream) throws IOException {
        return this.assertIntCount(this.longOffsets ? imageInputStream.readLong() : imageInputStream.readUnsignedInt());
    }

    private int assertIntCount(long l) throws IOException {
        if (l > Integer.MAX_VALUE) {
            throw new IIOException(String.format("Unsupported TIFF value count value: %s > Integer.MAX_VALUE", l));
        }
        return (int)l;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean isValidOffset(ImageInputStream imageInputStream, long l) throws IOException {
        try {
            imageInputStream.mark();
            imageInputStream.seek(l);
            boolean bl = imageInputStream.read() >= 0;
            return bl;
        }
        catch (IOException iOException) {
            boolean bl = false;
            return bl;
        }
        finally {
            imageInputStream.reset();
        }
    }

    private boolean isValidLengthAtOffset(ImageInputStream imageInputStream, long l, long l2) throws IOException {
        return !(this.inputLength >= 0L && this.inputLength < l + l2 || l2 >= 32767L && !this.isValidOffset(imageInputStream, l + l2 - 1L));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Object readValueAt(ImageInputStream imageInputStream, long l, long l2, short s, int n) throws IOException {
        long l3 = imageInputStream.getStreamPosition();
        try {
            imageInputStream.seek(l);
            if (n < Integer.MAX_VALUE && this.isValidLengthAtOffset(imageInputStream, l, l2)) {
                Object object = TIFFReader.readValue(imageInputStream, s, n, this.longOffsets);
                return object;
            }
            try {
                throw new EOFException(String.format("TIFF value offset or size too large: @%08x/%d bytes (input length: %s)", l, l2, this.inputLength >= 0L ? this.inputLength + " bytes" : "unknown"));
            }
            catch (EOFException eOFException) {
                if (DEBUG) {
                    System.err.println(eOFException);
                }
                EOFException eOFException2 = eOFException;
                return eOFException2;
            }
        }
        finally {
            imageInputStream.seek(l3);
        }
    }

    private Object readValueInLine(ImageInputStream imageInputStream, short s, int n) throws IOException {
        return TIFFReader.readValue(imageInputStream, s, n, this.longOffsets);
    }

    private static Object readValue(ImageInputStream imageInputStream, short s, int n, boolean bl) throws IOException {
        long l = imageInputStream.getStreamPosition();
        switch (s) {
            case 2: {
                if (n == 0) {
                    return "";
                }
                byte[] byArray = new byte[n];
                imageInputStream.readFully(byArray);
                int n2 = byArray[byArray.length - 1] == 0 ? byArray.length - 1 : byArray.length;
                String[] stringArray = new String(byArray, 0, n2, StandardCharsets.UTF_8).split("\u0000");
                return stringArray.length == 1 ? stringArray[0] : stringArray;
            }
            case 1: {
                if (n == 1) {
                    return imageInputStream.readUnsignedByte();
                }
            }
            case 6: {
                if (n == 1) {
                    return imageInputStream.readByte();
                }
            }
            case 7: {
                byte[] byArray = new byte[n];
                imageInputStream.readFully(byArray);
                return byArray;
            }
            case 3: {
                if (n == 1) {
                    return imageInputStream.readUnsignedShort();
                }
            }
            case 8: {
                if (n == 1) {
                    return imageInputStream.readShort();
                }
                short[] sArray = new short[n];
                imageInputStream.readFully(sArray, 0, sArray.length);
                if (s == 3) {
                    int[] nArray = new int[n];
                    for (int i = 0; i < n; ++i) {
                        nArray[i] = sArray[i] & 0xFFFF;
                    }
                    return nArray;
                }
                return sArray;
            }
            case 4: 
            case 13: {
                if (n == 1) {
                    return imageInputStream.readUnsignedInt();
                }
            }
            case 9: {
                if (n == 1) {
                    return imageInputStream.readInt();
                }
                int[] nArray = new int[n];
                imageInputStream.readFully(nArray, 0, nArray.length);
                if (s == 4 || s == 13) {
                    long[] lArray = new long[n];
                    for (int i = 0; i < n; ++i) {
                        lArray[i] = (long)nArray[i] & 0xFFFFFFFFL;
                    }
                    return lArray;
                }
                return nArray;
            }
            case 11: {
                if (n == 1) {
                    return Float.valueOf(imageInputStream.readFloat());
                }
                float[] fArray = new float[n];
                imageInputStream.readFully(fArray, 0, fArray.length);
                return fArray;
            }
            case 12: {
                if (n == 1) {
                    return imageInputStream.readDouble();
                }
                double[] dArray = new double[n];
                imageInputStream.readFully(dArray, 0, dArray.length);
                return dArray;
            }
            case 5: {
                if (n == 1) {
                    return TIFFReader.createSafeRational(imageInputStream.readUnsignedInt(), imageInputStream.readUnsignedInt());
                }
                Rational[] rationalArray = new Rational[n];
                for (int i = 0; i < rationalArray.length; ++i) {
                    rationalArray[i] = TIFFReader.createSafeRational(imageInputStream.readUnsignedInt(), imageInputStream.readUnsignedInt());
                }
                return rationalArray;
            }
            case 10: {
                if (n == 1) {
                    return TIFFReader.createSafeRational(imageInputStream.readInt(), imageInputStream.readInt());
                }
                Rational[] rationalArray = new Rational[n];
                for (int i = 0; i < rationalArray.length; ++i) {
                    rationalArray[i] = TIFFReader.createSafeRational(imageInputStream.readInt(), imageInputStream.readInt());
                }
                return rationalArray;
            }
            case 16: 
            case 17: 
            case 18: {
                if (!bl) break;
                if (n == 1) {
                    long l2 = imageInputStream.readLong();
                    if (s != 17 && l2 < 0L) {
                        throw new IIOException(String.format("Value > %s", Long.MAX_VALUE));
                    }
                    return l2;
                }
                long[] lArray = new long[n];
                for (int i = 0; i < n; ++i) {
                    lArray[i] = imageInputStream.readLong();
                }
                return lArray;
            }
        }
        return new Unknown(s, n, l);
    }

    private static Rational createSafeRational(long l, long l2) {
        if (l2 == 0L) {
            return Rational.NaN;
        }
        return new Rational(l, l2);
    }

    public static void main(String[] stringArray) throws IOException {
        TIFFReader tIFFReader = new TIFFReader();
        try (ImageInputStream imageInputStream = ImageIO.createImageInputStream(new File(stringArray[0]));){
            long l = 0L;
            if (stringArray.length > 1) {
                l = stringArray[1].startsWith("0x") ? (long)Integer.parseInt(stringArray[1].substring(2), 16) : Long.parseLong(stringArray[1]);
                imageInputStream.setByteOrder(l < 0L ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);
                l = Math.abs(l);
                imageInputStream.seek(l);
            }
            Directory directory = stringArray.length > 1 ? tIFFReader.readIFD(imageInputStream, l, VALID_TOP_LEVEL_IFDS) : tIFFReader.read(imageInputStream);
            for (Entry entry : directory) {
                System.err.println(entry);
                Object object = entry.getValue();
                if (!(object instanceof byte[])) continue;
                byte[] byArray = (byte[])object;
                System.err.println(HexDump.dump(0L, byArray, 0, Math.min(byArray.length, 128)));
            }
        }
    }

    public static class HexDump {
        private static final int WIDTH = 32;

        private HexDump() {
        }

        public static String dump(byte[] byArray) {
            return HexDump.dump(0L, byArray, 0, byArray.length);
        }

        public static String dump(long l, byte[] byArray, int n, int n2) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < n2; ++i) {
                if (i % 32 == 0) {
                    if (i > 0) {
                        stringBuilder.append("\n");
                    }
                    stringBuilder.append(String.format("%08x: ", (long)(i + n) + l));
                } else if (i > 0 && i % 2 == 0) {
                    stringBuilder.append(" ");
                }
                stringBuilder.append(String.format("%02x", byArray[i + n]));
                int n3 = i + 1;
                if (n3 % 32 != 0 && n3 != n2) continue;
                int n4 = (32 - n3 % 32) % 32;
                if (n4 != 0) {
                    int n5 = n4 / 2;
                    if (n2 % 2 != 0) {
                        stringBuilder.append("  ");
                    }
                    for (int j = 0; j < n5; ++j) {
                        stringBuilder.append("     ");
                    }
                }
                stringBuilder.append("  ");
                stringBuilder.append(HexDump.toAsciiString(byArray, n3 - (32 - n4) + n, n3 + n));
            }
            return stringBuilder.toString();
        }

        private static String toAsciiString(byte[] byArray, int n, int n2) {
            byte[] byArray2 = Arrays.copyOfRange(byArray, n, n2);
            for (int i = 0; i < byArray2.length; ++i) {
                if (byArray2[i] >= 32 && byArray2[i] <= 126) continue;
                byArray2[i] = 46;
            }
            return new String(byArray2, StandardCharsets.US_ASCII);
        }
    }
}

