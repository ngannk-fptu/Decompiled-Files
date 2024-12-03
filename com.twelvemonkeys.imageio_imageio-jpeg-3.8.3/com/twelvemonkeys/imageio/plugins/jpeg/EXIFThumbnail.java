/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.color.YCbCrConverter
 *  com.twelvemonkeys.imageio.metadata.CompoundDirectory
 *  com.twelvemonkeys.imageio.metadata.Directory
 *  com.twelvemonkeys.imageio.metadata.Entry
 */
package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.color.YCbCrConverter;
import com.twelvemonkeys.imageio.metadata.CompoundDirectory;
import com.twelvemonkeys.imageio.metadata.Directory;
import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.plugins.jpeg.EXIF;
import com.twelvemonkeys.imageio.plugins.jpeg.ThumbnailReader;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;
import javax.imageio.IIOException;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

final class EXIFThumbnail {
    private EXIFThumbnail() {
    }

    static ThumbnailReader from(EXIF eXIF, CompoundDirectory compoundDirectory, ImageReader imageReader) throws IOException {
        if (eXIF != null && compoundDirectory != null && compoundDirectory.directoryCount() >= 2) {
            ImageInputStream imageInputStream = eXIF.exifData();
            Directory directory = compoundDirectory.getDirectory(1);
            Entry entry = directory.getEntryById((Object)259);
            int n = entry == null ? 6 : ((Number)entry.getValue()).intValue();
            switch (n) {
                case 1: {
                    return EXIFThumbnail.createUncompressedThumbnailReader(imageInputStream, directory);
                }
                case 6: {
                    return EXIFThumbnail.createJPEGThumbnailReader(eXIF, imageReader, imageInputStream, directory);
                }
            }
            throw new IIOException("EXIF IFD with unknown thumbnail compression (expected 1 or 6): " + n);
        }
        return null;
    }

    private static ThumbnailReader.UncompressedThumbnailReader createUncompressedThumbnailReader(ImageInputStream imageInputStream, Directory directory) throws IOException {
        Entry entry = directory.getEntryById((Object)273);
        Entry entry2 = directory.getEntryById((Object)256);
        Entry entry3 = directory.getEntryById((Object)257);
        if (entry != null && entry2 != null && entry3 != null) {
            Entry entry4 = directory.getEntryById((Object)258);
            Entry entry5 = directory.getEntryById((Object)277);
            Entry entry6 = directory.getEntryById((Object)262);
            int n = ((Number)entry2.getValue()).intValue();
            int n2 = ((Number)entry3.getValue()).intValue();
            if (entry4 != null && !Arrays.equals((int[])entry4.getValue(), new int[]{8, 8, 8})) {
                throw new IIOException("Unknown BitsPerSample value for uncompressed EXIF thumbnail (expected [8, 8, 8]): " + entry4.getValueAsString());
            }
            if (entry5 != null && ((Number)entry5.getValue()).intValue() != 3) {
                throw new IIOException("Unknown SamplesPerPixel value for uncompressed EXIF thumbnail (expected 3): " + entry5.getValueAsString());
            }
            int n3 = entry6 != null ? ((Number)entry6.getValue()).intValue() : 2;
            long l = ((Number)entry.getValue()).longValue();
            int n4 = n * n2 * 3;
            if (l >= 0L && l + (long)n4 <= imageInputStream.length()) {
                imageInputStream.seek(l);
                byte[] byArray = new byte[n4];
                imageInputStream.readFully(byArray);
                switch (n3) {
                    case 2: {
                        break;
                    }
                    case 6: {
                        for (int i = 0; i < n4; i += 3) {
                            YCbCrConverter.convertJPEGYCbCr2RGB((byte[])byArray, (byte[])byArray, (int)i);
                        }
                        break;
                    }
                    default: {
                        throw new IIOException("Unknown PhotometricInterpretation value for uncompressed EXIF thumbnail (expected 2 or 6): " + n3);
                    }
                }
                return new ThumbnailReader.UncompressedThumbnailReader(n, n2, byArray);
            }
        }
        throw new IIOException("EXIF IFD with empty or incomplete uncompressed thumbnail");
    }

    private static ThumbnailReader.JPEGThumbnailReader createJPEGThumbnailReader(EXIF eXIF, ImageReader imageReader, ImageInputStream imageInputStream, Directory directory) throws IOException {
        Entry entry = directory.getEntryById((Object)513);
        if (entry != null) {
            long l;
            Entry entry2 = directory.getEntryById((Object)514);
            long l2 = ((Number)entry.getValue()).longValue();
            long l3 = l = entry2 != null ? ((Number)entry2.getValue()).longValue() : -1L;
            if (l > 0L && l2 + l <= (long)eXIF.data.length) {
                imageInputStream.seek(l2);
                imageInputStream.setByteOrder(ByteOrder.BIG_ENDIAN);
                if (imageInputStream.readUnsignedShort() == 65496) {
                    return new ThumbnailReader.JPEGThumbnailReader(imageReader, imageInputStream, l2);
                }
            }
        }
        throw new IIOException("EXIF IFD with empty or incomplete JPEG thumbnail");
    }
}

