/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwmf.record;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.imageio.ImageIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hwmf.usermodel.HwmfPicture;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LittleEndianInputStream;
import org.apache.poi.util.RecordFormatException;

public class HwmfBitmapDib
implements GenericRecord {
    private static final Logger LOG = LogManager.getLogger(HwmfBitmapDib.class);
    private static final int BMP_HEADER_SIZE = 14;
    private int headerSize;
    private int headerWidth;
    private int headerHeight;
    private int headerPlanes;
    private BitCount headerBitCount;
    private Compression headerCompression;
    private long headerImageSize = -1L;
    private int headerXPelsPerMeter = -1;
    private int headerYPelsPerMeter = -1;
    private long headerColorUsed = -1L;
    private long headerColorImportant = -1L;
    private Color[] colorTable;
    private int colorMaskR;
    private int colorMaskG;
    private int colorMaskB;
    private int introSize;
    private byte[] imageData;

    public int init(LittleEndianInputStream leis, int recordSize) throws IOException {
        leis.mark(10000);
        this.introSize = this.readHeader(leis);
        assert (this.introSize == this.headerSize);
        this.introSize += this.readColors(leis);
        assert (this.introSize < 10000);
        leis.reset();
        int bodySize = (this.headerWidth * this.headerPlanes * this.headerBitCount.flag + 31 & 0xFFFFFFE0) / 8 * Math.abs(this.headerHeight);
        assert (this.headerSize != 12 || (long)bodySize == this.headerImageSize);
        if (this.headerSize == 12 || this.headerCompression == Compression.BI_RGB || this.headerCompression == Compression.BI_BITFIELDS || this.headerCompression == Compression.BI_CMYK) {
            int fileSize = Math.min(this.introSize + bodySize, recordSize);
            this.imageData = IOUtils.safelyAllocate(fileSize, HwmfPicture.getMaxRecordLength());
            leis.readFully(this.imageData, 0, this.introSize);
            leis.skipFully(recordSize - fileSize);
            int readBytes = leis.read(this.imageData, this.introSize, fileSize - this.introSize);
            return this.introSize + (recordSize - fileSize) + readBytes;
        }
        this.imageData = IOUtils.safelyAllocate(recordSize, HwmfPicture.getMaxRecordLength());
        leis.readFully(this.imageData);
        return recordSize;
    }

    protected int readHeader(LittleEndianInputStream leis) {
        int size = 0;
        this.headerSize = leis.readInt();
        size += 4;
        if (this.headerSize == 12) {
            this.headerWidth = leis.readUShort();
            this.headerHeight = leis.readUShort();
            this.headerPlanes = leis.readUShort();
            this.headerBitCount = BitCount.valueOf(leis.readUShort());
            size += 8;
        } else {
            this.headerSize = 40;
            this.headerWidth = leis.readInt();
            this.headerHeight = leis.readInt();
            this.headerPlanes = leis.readUShort();
            this.headerBitCount = BitCount.valueOf(leis.readUShort());
            this.headerCompression = Compression.valueOf((int)leis.readUInt());
            this.headerImageSize = leis.readUInt();
            this.headerXPelsPerMeter = leis.readInt();
            this.headerYPelsPerMeter = leis.readInt();
            this.headerColorUsed = leis.readUInt();
            this.headerColorImportant = leis.readUInt();
            size += 36;
        }
        return size;
    }

    protected int readColors(LittleEndianInputStream leis) throws IOException {
        switch (this.headerBitCount) {
            default: {
                return 0;
            }
            case BI_BITCOUNT_1: {
                return this.readRGBQuad(leis, (int)(this.headerColorUsed == 0L ? 2L : Math.min(this.headerColorUsed, 2L)));
            }
            case BI_BITCOUNT_2: {
                return this.readRGBQuad(leis, (int)(this.headerColorUsed == 0L ? 16L : Math.min(this.headerColorUsed, 16L)));
            }
            case BI_BITCOUNT_3: {
                return this.readRGBQuad(leis, (int)(this.headerColorUsed == 0L ? 256L : Math.min(this.headerColorUsed, 256L)));
            }
            case BI_BITCOUNT_4: {
                switch (this.headerCompression) {
                    case BI_RGB: {
                        this.colorMaskB = 31;
                        this.colorMaskG = 992;
                        this.colorMaskR = 31744;
                        return 0;
                    }
                    case BI_BITFIELDS: {
                        this.colorMaskB = leis.readInt();
                        this.colorMaskG = leis.readInt();
                        this.colorMaskR = leis.readInt();
                        return 12;
                    }
                }
                throw new IOException("Invalid compression option (" + (Object)((Object)this.headerCompression) + ") for bitcount (" + (Object)((Object)this.headerBitCount) + ").");
            }
            case BI_BITCOUNT_5: 
            case BI_BITCOUNT_6: 
        }
        switch (this.headerCompression) {
            case BI_RGB: {
                this.colorMaskR = 255;
                this.colorMaskG = 255;
                this.colorMaskB = 255;
                return 0;
            }
            case BI_BITFIELDS: {
                this.colorMaskB = leis.readInt();
                this.colorMaskG = leis.readInt();
                this.colorMaskR = leis.readInt();
                return 12;
            }
        }
        throw new IOException("Invalid compression option (" + (Object)((Object)this.headerCompression) + ") for bitcount (" + (Object)((Object)this.headerBitCount) + ").");
    }

    protected int readRGBQuad(LittleEndianInputStream leis, int count) throws IOException {
        int size = 0;
        this.colorTable = new Color[count];
        for (int i = 0; i < count; ++i) {
            int blue = leis.readUByte();
            int green = leis.readUByte();
            int red = leis.readUByte();
            int reserved = leis.readUByte();
            this.colorTable[i] = new Color(red, green, blue);
            size += 4;
        }
        return size;
    }

    public boolean isValid() {
        if (this.imageData == null) {
            return false;
        }
        if (this.headerBitCount == BitCount.BI_BITCOUNT_1) {
            if (this.colorTable == null) {
                return false;
            }
            for (Color c : this.colorTable) {
                if (Color.BLACK.equals(c)) continue;
                return true;
            }
            return false;
        }
        return true;
    }

    public InputStream getBMPStream() {
        return new ByteArrayInputStream(this.getBMPData());
    }

    public byte[] getBMPData() {
        if (this.headerWidth <= 0 || this.headerHeight <= 0) {
            return null;
        }
        if (this.imageData == null) {
            throw new RecordFormatException("used to throw exception: bitmap not initialized ... need to call init() before");
        }
        int imageSize = (int)Math.max((long)this.imageData.length, (long)this.introSize + this.headerImageSize);
        byte[] buf = IOUtils.safelyAllocate(14L + (long)imageSize, HwmfPicture.getMaxRecordLength());
        buf[0] = 66;
        buf[1] = 77;
        LittleEndian.putInt(buf, 2, 14 + imageSize);
        LittleEndian.putInt(buf, 6, 0);
        LittleEndian.putInt(buf, 10, 14 + this.introSize);
        System.arraycopy(this.imageData, 0, buf, 14, this.imageData.length);
        return buf;
    }

    public BufferedImage getImage() {
        return this.getImage(null, null, false);
    }

    public BufferedImage getImage(Color foreground, Color background, boolean hasAlpha) {
        BufferedImage bi;
        try {
            bi = ImageIO.read(this.getBMPStream());
        }
        catch (IOException | RuntimeException e) {
            LOG.atError().log("invalid bitmap data - returning placeholder image");
            return this.getPlaceholder();
        }
        if (foreground != null && background != null && this.headerBitCount == BitCount.BI_BITCOUNT_1) {
            int[] nArray;
            int transPixel;
            IndexColorModel cmOld = (IndexColorModel)bi.getColorModel();
            int fg = foreground.getRGB();
            int bg = background.getRGB() & (hasAlpha ? 0xFFFFFF : -1);
            boolean ordered = (cmOld.getRGB(0) & 0xFFFFFF) == (bg & 0xFFFFFF);
            int n = transPixel = ordered ? 0 : 1;
            if (ordered) {
                int[] nArray2 = new int[2];
                nArray2[0] = bg;
                nArray = nArray2;
                nArray2[1] = fg;
            } else {
                int[] nArray3 = new int[2];
                nArray3[0] = fg;
                nArray = nArray3;
                nArray3[1] = bg;
            }
            int[] cmap = nArray;
            int transferType = bi.getData().getTransferType();
            IndexColorModel cmNew = new IndexColorModel(1, 2, cmap, 0, hasAlpha, transPixel, transferType);
            bi = new BufferedImage(cmNew, bi.getRaster(), false, null);
        }
        return bi;
    }

    public String toString() {
        return GenericRecordJsonWriter.marshal(this);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        LinkedHashMap<String, Supplier<Object>> m = new LinkedHashMap<String, Supplier<Object>>();
        m.put("headerSize", () -> this.headerSize);
        m.put("width", () -> this.headerWidth);
        m.put("height", () -> this.headerHeight);
        m.put("planes", () -> this.headerPlanes);
        m.put("bitCount", () -> this.headerBitCount);
        m.put("compression", () -> this.headerCompression);
        m.put("imageSize", () -> this.headerImageSize);
        m.put("xPelsPerMeter", () -> this.headerXPelsPerMeter);
        m.put("yPelsPerMeter", () -> this.headerYPelsPerMeter);
        m.put("colorUsed", () -> this.headerColorUsed);
        m.put("colorImportant", () -> this.headerColorImportant);
        m.put("image", this::getImage);
        m.put("bmpData", this::getBMPData);
        return Collections.unmodifiableMap(m);
    }

    protected BufferedImage getPlaceholder() {
        if (this.headerHeight <= 0 || this.headerWidth <= 0) {
            return new BufferedImage(1, 1, 2);
        }
        BufferedImage bi = new BufferedImage(this.headerWidth, this.headerHeight, 2);
        Graphics2D g = bi.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, this.headerWidth, this.headerHeight);
        int arcs = Math.min(this.headerWidth, this.headerHeight) / 7;
        Color bg = Color.LIGHT_GRAY;
        Color fg = Color.GRAY;
        LinearGradientPaint lgp = new LinearGradientPaint(0.0f, 0.0f, 5.0f, 5.0f, new float[]{0.0f, 0.1f, 0.1001f}, new Color[]{fg, fg, bg}, MultipleGradientPaint.CycleMethod.REFLECT);
        g.setComposite(AlphaComposite.SrcOver.derive(0.4f));
        g.setPaint(lgp);
        g.fillRoundRect(0, 0, this.headerWidth - 1, this.headerHeight - 1, arcs, arcs);
        g.setColor(Color.DARK_GRAY);
        g.setComposite(AlphaComposite.Src);
        g.setStroke(new BasicStroke(2.0f));
        g.drawRoundRect(0, 0, this.headerWidth - 1, this.headerHeight - 1, arcs, arcs);
        g.dispose();
        return bi;
    }

    public static enum Compression {
        BI_RGB(0),
        BI_RLE8(1),
        BI_RLE4(2),
        BI_BITFIELDS(3),
        BI_JPEG(4),
        BI_PNG(5),
        BI_CMYK(11),
        BI_CMYKRLE8(12),
        BI_CMYKRLE4(13);

        int flag;

        private Compression(int flag) {
            this.flag = flag;
        }

        static Compression valueOf(int flag) {
            for (Compression c : Compression.values()) {
                if (c.flag != flag) continue;
                return c;
            }
            return null;
        }
    }

    public static enum BitCount {
        BI_BITCOUNT_0(0),
        BI_BITCOUNT_1(1),
        BI_BITCOUNT_2(4),
        BI_BITCOUNT_3(8),
        BI_BITCOUNT_4(16),
        BI_BITCOUNT_5(24),
        BI_BITCOUNT_6(32);

        int flag;

        private BitCount(int flag) {
            this.flag = flag;
        }

        static BitCount valueOf(int flag) {
            for (BitCount bc : BitCount.values()) {
                if (bc.flag != flag) continue;
                return bc;
            }
            return null;
        }
    }
}

