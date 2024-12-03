/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.filter.Filter;
import org.apache.pdfbox.filter.FilterFactory;
import org.apache.pdfbox.io.RandomAccess;
import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public final class CCITTFactory {
    private CCITTFactory() {
    }

    public static PDImageXObject createFromImage(PDDocument document, BufferedImage image) throws IOException {
        if (image.getType() != 12 && image.getColorModel().getPixelSize() != 1) {
            throw new IllegalArgumentException("Only 1-bit b/w images supported");
        }
        int height = image.getHeight();
        int width = image.getWidth();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        MemoryCacheImageOutputStream mcios = new MemoryCacheImageOutputStream(bos);
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                mcios.writeBits(~(image.getRGB(x, y) & 1), 1);
            }
            int bitOffset = mcios.getBitOffset();
            if (bitOffset == 0) continue;
            mcios.writeBits(0L, 8 - bitOffset);
        }
        mcios.flush();
        mcios.close();
        return CCITTFactory.prepareImageXObject(document, bos.toByteArray(), width, height, PDDeviceGray.INSTANCE);
    }

    public static PDImageXObject createFromByteArray(PDDocument document, byte[] byteArray) throws IOException {
        return CCITTFactory.createFromByteArray(document, byteArray, 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static PDImageXObject createFromByteArray(PDDocument document, byte[] byteArray, int number) throws IOException {
        RandomAccessBuffer raf = new RandomAccessBuffer(byteArray);
        try {
            PDImageXObject pDImageXObject = CCITTFactory.createFromRandomAccessImpl(document, raf, number);
            return pDImageXObject;
        }
        finally {
            raf.close();
        }
    }

    private static PDImageXObject prepareImageXObject(PDDocument document, byte[] byteArray, int width, int height, PDColorSpace initColorSpace) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Filter filter = FilterFactory.INSTANCE.getFilter(COSName.CCITTFAX_DECODE);
        COSDictionary dict = new COSDictionary();
        dict.setInt(COSName.COLUMNS, width);
        dict.setInt(COSName.ROWS, height);
        filter.encode(new ByteArrayInputStream(byteArray), baos, dict, 0);
        ByteArrayInputStream encodedByteStream = new ByteArrayInputStream(baos.toByteArray());
        PDImageXObject image = new PDImageXObject(document, encodedByteStream, COSName.CCITTFAX_DECODE, width, height, 1, initColorSpace);
        dict.setInt(COSName.K, -1);
        image.getCOSObject().setItem(COSName.DECODE_PARMS, (COSBase)dict);
        return image;
    }

    @Deprecated
    public static PDImageXObject createFromRandomAccess(PDDocument document, RandomAccess reader) throws IOException {
        return CCITTFactory.createFromRandomAccessImpl(document, reader, 0);
    }

    @Deprecated
    public static PDImageXObject createFromRandomAccess(PDDocument document, RandomAccess reader, int number) throws IOException {
        return CCITTFactory.createFromRandomAccessImpl(document, reader, number);
    }

    public static PDImageXObject createFromFile(PDDocument document, File file) throws IOException {
        return CCITTFactory.createFromFile(document, file, 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static PDImageXObject createFromFile(PDDocument document, File file, int number) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        try {
            PDImageXObject pDImageXObject = CCITTFactory.createFromRandomAccessImpl(document, raf, number);
            return pDImageXObject;
        }
        finally {
            raf.close();
        }
    }

    private static PDImageXObject createFromRandomAccessImpl(PDDocument document, RandomAccess reader, int number) throws IOException {
        COSDictionary decodeParms = new COSDictionary();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        CCITTFactory.extractFromTiff(reader, bos, decodeParms, number);
        if (bos.size() == 0) {
            return null;
        }
        ByteArrayInputStream encodedByteStream = new ByteArrayInputStream(bos.toByteArray());
        PDImageXObject pdImage = new PDImageXObject(document, encodedByteStream, COSName.CCITTFAX_DECODE, decodeParms.getInt(COSName.COLUMNS), decodeParms.getInt(COSName.ROWS), 1, PDDeviceGray.INSTANCE);
        COSStream dict = pdImage.getCOSObject();
        dict.setItem(COSName.DECODE_PARMS, (COSBase)decodeParms);
        return pdImage;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void extractFromTiff(RandomAccess reader, OutputStream os, COSDictionary params, int number) throws IOException {
        try {
            int amountRead;
            reader.seek(0L);
            char endianess = (char)reader.read();
            if ((char)reader.read() != endianess) {
                throw new IOException("Not a valid tiff file");
            }
            if (endianess != 'M' && endianess != 'I') {
                throw new IOException("Not a valid tiff file");
            }
            int magicNumber = CCITTFactory.readshort(endianess, reader);
            if (magicNumber != 42) {
                throw new IOException("Not a valid tiff file");
            }
            long address = CCITTFactory.readlong(endianess, reader);
            reader.seek(address);
            for (int i = 0; i < number; ++i) {
                int numtags = CCITTFactory.readshort(endianess, reader);
                if (numtags > 50) {
                    throw new IOException("Not a valid tiff file");
                }
                reader.seek(address + 2L + (long)numtags * 12L);
                address = CCITTFactory.readlong(endianess, reader);
                if (address == 0L) {
                    return;
                }
                reader.seek(address);
            }
            int numtags = CCITTFactory.readshort(endianess, reader);
            if (numtags > 50) {
                throw new IOException("Not a valid tiff file");
            }
            int k = -1000;
            int dataoffset = 0;
            int datalength = 0;
            block22: for (int i = 0; i < numtags; ++i) {
                int val;
                int tag = CCITTFactory.readshort(endianess, reader);
                int type = CCITTFactory.readshort(endianess, reader);
                int count = CCITTFactory.readlong(endianess, reader);
                switch (type) {
                    case 1: {
                        val = reader.read();
                        reader.read();
                        reader.read();
                        reader.read();
                        break;
                    }
                    case 3: {
                        val = CCITTFactory.readshort(endianess, reader);
                        reader.read();
                        reader.read();
                        break;
                    }
                    default: {
                        val = CCITTFactory.readlong(endianess, reader);
                    }
                }
                switch (tag) {
                    case 256: {
                        params.setInt(COSName.COLUMNS, val);
                        continue block22;
                    }
                    case 257: {
                        params.setInt(COSName.ROWS, val);
                        continue block22;
                    }
                    case 259: {
                        if (val == 4) {
                            k = -1;
                        }
                        if (val != 3) continue block22;
                        k = 0;
                        continue block22;
                    }
                    case 262: {
                        if (val != 1) continue block22;
                        params.setBoolean(COSName.BLACK_IS_1, true);
                        continue block22;
                    }
                    case 266: {
                        if (val == 1) continue block22;
                        throw new IOException("FillOrder " + val + " is not supported");
                    }
                    case 273: {
                        if (count != 1) continue block22;
                        dataoffset = val;
                        continue block22;
                    }
                    case 274: {
                        if (val == 1) continue block22;
                        throw new IOException("Orientation " + val + " is not supported");
                    }
                    case 279: {
                        if (count != 1) continue block22;
                        datalength = val;
                        continue block22;
                    }
                    case 292: {
                        if ((val & 1) != 0) {
                            k = 50;
                        }
                        if ((val & 4) != 0) {
                            throw new IOException("CCITT Group 3 'uncompressed mode' is not supported");
                        }
                        if ((val & 2) == 0) continue block22;
                        throw new IOException("CCITT Group 3 'fill bits before EOL' is not supported");
                    }
                    case 324: {
                        if (count != 1) continue block22;
                        dataoffset = val;
                        continue block22;
                    }
                    case 325: {
                        if (count != 1) continue block22;
                        datalength = val;
                        continue block22;
                    }
                }
            }
            if (k == -1000) {
                throw new IOException("First image in tiff is not CCITT T4 or T6 compressed");
            }
            if (dataoffset == 0) {
                throw new IOException("First image in tiff is not a single tile/strip");
            }
            params.setInt(COSName.K, k);
            reader.seek(dataoffset);
            byte[] buf = new byte[8192];
            while ((amountRead = reader.read(buf, 0, Math.min(8192, datalength))) > 0) {
                datalength -= amountRead;
                os.write(buf, 0, amountRead);
            }
        }
        finally {
            os.close();
        }
    }

    private static int readshort(char endianess, RandomAccess raf) throws IOException {
        if (endianess == 'I') {
            return raf.read() | raf.read() << 8;
        }
        return raf.read() << 8 | raf.read();
    }

    private static int readlong(char endianess, RandomAccess raf) throws IOException {
        if (endianess == 'I') {
            return raf.read() | raf.read() << 8 | raf.read() << 16 | raf.read() << 24;
        }
        return raf.read() << 24 | raf.read() << 16 | raf.read() << 8 | raf.read();
    }
}

