/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.dcx;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.imaging.ImageFormat;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageParser;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.PixelDensity;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.common.BinaryOutputStream;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.bytesource.ByteSource;
import org.apache.commons.imaging.common.bytesource.ByteSourceInputStream;
import org.apache.commons.imaging.formats.pcx.PcxImageParser;

public class DcxImageParser
extends ImageParser {
    private static final String DEFAULT_EXTENSION = ".dcx";
    private static final String[] ACCEPTED_EXTENSIONS = new String[]{".dcx"};

    public DcxImageParser() {
        super.setByteOrder(ByteOrder.LITTLE_ENDIAN);
    }

    @Override
    public String getName() {
        return "Dcx-Custom";
    }

    @Override
    public String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    @Override
    protected String[] getAcceptedExtensions() {
        return ACCEPTED_EXTENSIONS;
    }

    @Override
    protected ImageFormat[] getAcceptedTypes() {
        return new ImageFormat[]{ImageFormats.DCX};
    }

    @Override
    public ImageMetadata getMetadata(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        return null;
    }

    @Override
    public ImageInfo getImageInfo(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        return null;
    }

    @Override
    public Dimension getImageSize(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        return null;
    }

    @Override
    public byte[] getICCProfileBytes(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        return null;
    }

    private DcxHeader readDcxHeader(ByteSource byteSource) throws ImageReadException, IOException {
        try (InputStream is = byteSource.getInputStream();){
            DcxHeader ret;
            long pageOffset;
            int id = BinaryFunctions.read4Bytes("Id", is, "Not a Valid DCX File", this.getByteOrder());
            ArrayList<Long> pageTable = new ArrayList<Long>(1024);
            for (int i = 0; i < 1024 && (pageOffset = 0xFFFFFFFFL & (long)BinaryFunctions.read4Bytes("PageTable", is, "Not a Valid DCX File", this.getByteOrder())) != 0L; ++i) {
                pageTable.add(pageOffset);
            }
            if (id != 987654321) {
                throw new ImageReadException("Not a Valid DCX File: file id incorrect");
            }
            if (pageTable.size() == 1024) {
                throw new ImageReadException("DCX page table not terminated by zero entry");
            }
            Object[] objects = pageTable.toArray();
            long[] pages = new long[objects.length];
            for (int i = 0; i < objects.length; ++i) {
                pages[i] = (Long)objects[i];
            }
            DcxHeader dcxHeader = ret = new DcxHeader(id, pages);
            return dcxHeader;
        }
    }

    @Override
    public boolean dumpImageFile(PrintWriter pw, ByteSource byteSource) throws ImageReadException, IOException {
        this.readDcxHeader(byteSource).dump(pw);
        return true;
    }

    @Override
    public final BufferedImage getBufferedImage(ByteSource byteSource, Map<String, Object> params) throws ImageReadException, IOException {
        List<BufferedImage> list = this.getAllBufferedImages(byteSource);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<BufferedImage> getAllBufferedImages(ByteSource byteSource) throws ImageReadException, IOException {
        DcxHeader dcxHeader = this.readDcxHeader(byteSource);
        ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
        PcxImageParser pcxImageParser = new PcxImageParser();
        for (long element : dcxHeader.pageTable) {
            try (InputStream stream = byteSource.getInputStream(element);){
                ByteSourceInputStream pcxSource = new ByteSourceInputStream(stream, null);
                BufferedImage image = pcxImageParser.getBufferedImage(pcxSource, new HashMap<String, Object>());
                images.add(image);
            }
        }
        return images;
    }

    @Override
    public void writeImage(BufferedImage src, OutputStream os, Map<String, Object> params) throws ImageWriteException, IOException {
        Object value;
        params = params == null ? new HashMap<String, Object>() : new HashMap<String, Object>(params);
        HashMap<String, Object> pcxParams = new HashMap<String, Object>();
        if (params.containsKey("FORMAT")) {
            params.remove("FORMAT");
        }
        if (params.containsKey("PCX_COMPRESSION")) {
            value = params.remove("PCX_COMPRESSION");
            pcxParams.put("PCX_COMPRESSION", value);
        }
        if (params.containsKey("PIXEL_DENSITY") && (value = params.remove("PIXEL_DENSITY")) != null) {
            if (!(value instanceof PixelDensity)) {
                throw new ImageWriteException("Invalid pixel density parameter");
            }
            pcxParams.put("PIXEL_DENSITY", value);
        }
        if (!params.isEmpty()) {
            String firstKey = params.keySet().iterator().next();
            throw new ImageWriteException("Unknown parameter: " + firstKey);
        }
        int headerSize = 4100;
        BinaryOutputStream bos = new BinaryOutputStream(os, ByteOrder.LITTLE_ENDIAN);
        bos.write4Bytes(987654321);
        bos.write4Bytes(4100);
        for (int i = 0; i < 1023; ++i) {
            bos.write4Bytes(0);
        }
        PcxImageParser pcxImageParser = new PcxImageParser();
        pcxImageParser.writeImage(src, bos, pcxParams);
    }

    private static class DcxHeader {
        public static final int DCX_ID = 987654321;
        public final int id;
        public final long[] pageTable;

        DcxHeader(int id, long[] pageTable) {
            this.id = id;
            this.pageTable = pageTable;
        }

        public void dump(PrintWriter pw) {
            pw.println("DcxHeader");
            pw.println("Id: 0x" + Integer.toHexString(this.id));
            pw.println("Pages: " + this.pageTable.length);
            pw.println();
        }
    }
}

