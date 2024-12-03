/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.color.ColorProfiles
 *  com.twelvemonkeys.imageio.stream.ByteArrayImageInputStream
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.metadata.jpeg;

import com.twelvemonkeys.imageio.color.ColorProfiles;
import com.twelvemonkeys.imageio.metadata.Directory;
import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.metadata.jpeg.JPEGSegment;
import com.twelvemonkeys.imageio.metadata.psd.PSDReader;
import com.twelvemonkeys.imageio.metadata.tiff.TIFFReader;
import com.twelvemonkeys.imageio.metadata.xmp.XMPReader;
import com.twelvemonkeys.imageio.stream.ByteArrayImageInputStream;
import com.twelvemonkeys.lang.Validate;
import java.awt.color.ICC_Profile;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

public final class JPEGSegmentUtil {
    public static final List<String> ALL_IDS = Collections.unmodifiableList(new AllIdsList());
    public static final Map<Integer, List<String>> ALL_SEGMENTS = Collections.unmodifiableMap(new AllSegmentsMap());
    public static final Map<Integer, List<String>> APP_SEGMENTS = Collections.unmodifiableMap(new AllAppSegmentsMap());

    private JPEGSegmentUtil() {
    }

    public static List<JPEGSegment> readSegments(ImageInputStream imageInputStream, int n, String string) throws IOException {
        return JPEGSegmentUtil.readSegments(imageInputStream, Collections.singletonMap(n, string != null ? Collections.singletonList(string) : ALL_IDS));
    }

    public static List<JPEGSegment> readSegments(ImageInputStream imageInputStream, Map<Integer, List<String>> map) throws IOException {
        JPEGSegmentUtil.readSOI((ImageInputStream)Validate.notNull((Object)imageInputStream, (String)"stream"));
        List<JPEGSegment> list = Collections.emptyList();
        try {
            JPEGSegment jPEGSegment;
            do {
                if (!JPEGSegmentUtil.isRequested(jPEGSegment = JPEGSegmentUtil.readSegment(imageInputStream, map), map)) continue;
                if (list == Collections.EMPTY_LIST) {
                    list = new ArrayList<JPEGSegment>();
                }
                list.add(jPEGSegment);
            } while (!JPEGSegmentUtil.isImageDone(jPEGSegment));
        }
        catch (EOFException eOFException) {
            // empty catch block
        }
        return list;
    }

    private static boolean isRequested(JPEGSegment jPEGSegment, Map<Integer, List<String>> map) {
        return map.containsKey(jPEGSegment.marker) && (jPEGSegment.identifier() == null && map.get(jPEGSegment.marker) == null || JPEGSegmentUtil.containsSafe(jPEGSegment, map));
    }

    private static boolean containsSafe(JPEGSegment jPEGSegment, Map<Integer, List<String>> map) {
        List<String> list = map.get(jPEGSegment.marker);
        return list != null && list.contains(jPEGSegment.identifier());
    }

    private static boolean isImageDone(JPEGSegment jPEGSegment) {
        return jPEGSegment.marker == 65498 || jPEGSegment.marker == 65497 || jPEGSegment.marker == 65496;
    }

    static String asNullTerminatedAsciiString(byte[] byArray, int n) {
        for (int i = 0; i < byArray.length - n; ++i) {
            if (byArray[n + i] != 0 && i <= 255) continue;
            return JPEGSegmentUtil.asAsciiString(byArray, n, n + i);
        }
        return null;
    }

    static String asAsciiString(byte[] byArray, int n, int n2) {
        return new String(byArray, n, n2, StandardCharsets.US_ASCII);
    }

    static void readSOI(ImageInputStream imageInputStream) throws IOException {
        if (imageInputStream.readUnsignedShort() != 65496) {
            throw new IIOException("Not a JPEG stream");
        }
    }

    static JPEGSegment readSegment(ImageInputStream imageInputStream, Map<Integer, List<String>> map) throws IOException {
        byte[] byArray;
        int n = imageInputStream.readUnsignedByte();
        while (!JPEGSegmentUtil.isKnownJPEGMarker(n)) {
            while (n != 255) {
                n = imageInputStream.readUnsignedByte();
            }
            n = 0xFF00 | imageInputStream.readUnsignedByte();
            while (n == 65535) {
                n = 0xFF00 | imageInputStream.readUnsignedByte();
            }
        }
        if ((n >> 8 & 0xFF) != 255) {
            throw new IIOException(String.format("Bad marker: %04x", n));
        }
        int n2 = imageInputStream.readUnsignedShort();
        if (map.containsKey(n)) {
            byArray = new byte[Math.max(0, n2 - 2)];
            imageInputStream.readFully(byArray);
        } else if (JPEGSegment.isAppSegmentMarker(n)) {
            int n3;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(32);
            while ((n3 = imageInputStream.read()) > 0) {
                byteArrayOutputStream.write(n3);
            }
            byArray = byteArrayOutputStream.toByteArray();
            imageInputStream.skipBytes(n2 - 3 - byArray.length);
        } else {
            byArray = null;
            imageInputStream.skipBytes(n2 - 2);
        }
        return new JPEGSegment(n, byArray, n2);
    }

    public static boolean isKnownJPEGMarker(int n) {
        switch (n) {
            case 65281: 
            case 65472: 
            case 65473: 
            case 65474: 
            case 65475: 
            case 65476: 
            case 65477: 
            case 65478: 
            case 65479: 
            case 65481: 
            case 65482: 
            case 65483: 
            case 65484: 
            case 65485: 
            case 65486: 
            case 65487: 
            case 65496: 
            case 65497: 
            case 65498: 
            case 65499: 
            case 65500: 
            case 65501: 
            case 65502: 
            case 65503: 
            case 65504: 
            case 65505: 
            case 65506: 
            case 65507: 
            case 65508: 
            case 65509: 
            case 65510: 
            case 65511: 
            case 65512: 
            case 65513: 
            case 65514: 
            case 65515: 
            case 65516: 
            case 65517: 
            case 65518: 
            case 65519: 
            case 65527: 
            case 65528: 
            case 65534: {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] stringArray) throws IOException {
        for (String string : stringArray) {
            if (stringArray.length > 1) {
                System.out.println("File: " + string);
                System.out.println("------");
            }
            List<JPEGSegment> list = JPEGSegmentUtil.readSegments(ImageIO.createImageInputStream(new File(string)), ALL_SEGMENTS);
            for (JPEGSegment jPEGSegment : list) {
                Directory directory;
                Object object;
                System.err.println("segment: " + jPEGSegment);
                if ("Exif".equals(jPEGSegment.identifier())) {
                    object = new ByteArrayImageInputStream(jPEGSegment.data, jPEGSegment.offset() + 1, jPEGSegment.length() - 1);
                    directory = new TIFFReader().read((ImageInputStream)object);
                    System.err.println("EXIF: " + directory);
                    continue;
                }
                if ("http://ns.adobe.com/xap/1.0/".equals(jPEGSegment.identifier())) {
                    object = new XMPReader().read((ImageInputStream)new ByteArrayImageInputStream(jPEGSegment.data, jPEGSegment.offset(), jPEGSegment.length()));
                    System.err.println("XMP: " + object);
                    System.err.println(TIFFReader.HexDump.dump(jPEGSegment.data));
                    continue;
                }
                if ("Photoshop 3.0".equals(jPEGSegment.identifier())) {
                    object = new ByteArrayImageInputStream(jPEGSegment.data, jPEGSegment.offset(), jPEGSegment.length());
                    directory = new PSDReader().read((ImageInputStream)object);
                    Entry entry = directory.getEntryById(1039);
                    if (entry != null) {
                        ICC_Profile iCC_Profile = ColorProfiles.createProfile((byte[])((byte[])entry.getValue()));
                        System.err.println("ICC Profile: " + iCC_Profile);
                    }
                    System.err.println("PSD: " + directory);
                    System.err.println(TIFFReader.HexDump.dump(jPEGSegment.data));
                    continue;
                }
                if ("ICC_PROFILE".equals(jPEGSegment.identifier())) continue;
                System.err.println(TIFFReader.HexDump.dump(jPEGSegment.data));
            }
            if (stringArray.length <= 1) continue;
            System.out.println("------");
            System.out.println();
        }
    }

    private static class AllAppSegmentsMap
    extends HashMap<Integer, List<String>> {
        private AllAppSegmentsMap() {
        }

        @Override
        public String toString() {
            return "{All APPn segments}";
        }

        @Override
        public List<String> get(Object object) {
            return this.containsKey(object) ? ALL_IDS : null;
        }

        @Override
        public boolean containsKey(Object object) {
            return object instanceof Integer && JPEGSegment.isAppSegmentMarker((Integer)object);
        }
    }

    private static class AllSegmentsMap
    extends HashMap<Integer, List<String>> {
        private AllSegmentsMap() {
        }

        @Override
        public String toString() {
            return "{All segments}";
        }

        @Override
        public List<String> get(Object object) {
            return object instanceof Integer && JPEGSegment.isAppSegmentMarker((Integer)object) ? ALL_IDS : null;
        }

        @Override
        public boolean containsKey(Object object) {
            return true;
        }
    }

    private static class AllIdsList
    extends ArrayList<String> {
        private AllIdsList() {
        }

        @Override
        public String toString() {
            return "[All ids]";
        }

        @Override
        public boolean contains(Object object) {
            return true;
        }
    }
}

