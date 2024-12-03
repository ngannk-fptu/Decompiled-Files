/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.apache.commons.imaging.ImageFormat;

public class ImageInfo {
    private static final Logger LOGGER = Logger.getLogger(ImageInfo.class.getName());
    private final String formatDetails;
    private final int bitsPerPixel;
    private final List<String> comments;
    private final ImageFormat format;
    private final String formatName;
    private final int height;
    private final String mimeType;
    private final int numberOfImages;
    private final int physicalHeightDpi;
    private final float physicalHeightInch;
    private final int physicalWidthDpi;
    private final float physicalWidthInch;
    private final int width;
    private final boolean progressive;
    private final boolean transparent;
    private final boolean usesPalette;
    private final ColorType colorType;
    private final CompressionAlgorithm compressionAlgorithm;

    public ImageInfo(String formatDetails, int bitsPerPixel, List<String> comments, ImageFormat format, String formatName, int height, String mimeType, int numberOfImages, int physicalHeightDpi, float physicalHeightInch, int physicalWidthDpi, float physicalWidthInch, int width, boolean progressive, boolean transparent, boolean usesPalette, ColorType colorType, CompressionAlgorithm compressionAlgorithm) {
        this.formatDetails = formatDetails;
        this.bitsPerPixel = bitsPerPixel;
        this.comments = comments;
        this.format = format;
        this.formatName = formatName;
        this.height = height;
        this.mimeType = mimeType;
        this.numberOfImages = numberOfImages;
        this.physicalHeightDpi = physicalHeightDpi;
        this.physicalHeightInch = physicalHeightInch;
        this.physicalWidthDpi = physicalWidthDpi;
        this.physicalWidthInch = physicalWidthInch;
        this.width = width;
        this.progressive = progressive;
        this.transparent = transparent;
        this.usesPalette = usesPalette;
        this.colorType = colorType;
        this.compressionAlgorithm = compressionAlgorithm;
    }

    public int getBitsPerPixel() {
        return this.bitsPerPixel;
    }

    public List<String> getComments() {
        return new ArrayList<String>(this.comments);
    }

    public ImageFormat getFormat() {
        return this.format;
    }

    public String getFormatName() {
        return this.formatName;
    }

    public int getHeight() {
        return this.height;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public int getNumberOfImages() {
        return this.numberOfImages;
    }

    public int getPhysicalHeightDpi() {
        return this.physicalHeightDpi;
    }

    public float getPhysicalHeightInch() {
        return this.physicalHeightInch;
    }

    public int getPhysicalWidthDpi() {
        return this.physicalWidthDpi;
    }

    public float getPhysicalWidthInch() {
        return this.physicalWidthInch;
    }

    public int getWidth() {
        return this.width;
    }

    public boolean isProgressive() {
        return this.progressive;
    }

    public ColorType getColorType() {
        return this.colorType;
    }

    public void dump() {
        LOGGER.fine(this.toString());
    }

    public String toString() {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            this.toString(pw, "");
            pw.flush();
            return sw.toString();
        }
        catch (Exception e) {
            return "Image Data: Error";
        }
    }

    public void toString(PrintWriter pw, String prefix) {
        pw.println("Format Details: " + this.formatDetails);
        pw.println("Bits Per Pixel: " + this.bitsPerPixel);
        pw.println("Comments: " + this.comments.size());
        for (int i = 0; i < this.comments.size(); ++i) {
            String s = this.comments.get(i);
            pw.println("\t" + i + ": '" + s + "'");
        }
        pw.println("Format: " + this.format.getName());
        pw.println("Format Name: " + this.formatName);
        pw.println("Compression Algorithm: " + (Object)((Object)this.compressionAlgorithm));
        pw.println("Height: " + this.height);
        pw.println("MimeType: " + this.mimeType);
        pw.println("Number Of Images: " + this.numberOfImages);
        pw.println("Physical Height Dpi: " + this.physicalHeightDpi);
        pw.println("Physical Height Inch: " + this.physicalHeightInch);
        pw.println("Physical Width Dpi: " + this.physicalWidthDpi);
        pw.println("Physical Width Inch: " + this.physicalWidthInch);
        pw.println("Width: " + this.width);
        pw.println("Is Progressive: " + this.progressive);
        pw.println("Is Transparent: " + this.transparent);
        pw.println("Color Type: " + this.colorType.toString());
        pw.println("Uses Palette: " + this.usesPalette);
        pw.flush();
    }

    public String getFormatDetails() {
        return this.formatDetails;
    }

    public boolean isTransparent() {
        return this.transparent;
    }

    public boolean usesPalette() {
        return this.usesPalette;
    }

    public CompressionAlgorithm getCompressionAlgorithm() {
        return this.compressionAlgorithm;
    }

    public static enum CompressionAlgorithm {
        UNKNOWN("Unknown"),
        NONE("None"),
        LZW("LZW"),
        PACKBITS("PackBits"),
        JPEG("JPEG"),
        RLE("RLE: Run-Length Encoding"),
        ADAPTIVE_RLE("Adaptive RLE"),
        PSD("Photoshop"),
        PNG_FILTER("PNG Filter"),
        CCITT_GROUP_3("CCITT Group 3 1-Dimensional Modified Huffman run-length encoding."),
        CCITT_GROUP_4("CCITT Group 4"),
        CCITT_1D("CCITT 1D");

        private String description;

        private CompressionAlgorithm(String description) {
            this.description = description;
        }

        public String toString() {
            return this.description;
        }
    }

    public static enum ColorType {
        BW("Black and White"),
        GRAYSCALE("Grayscale"),
        RGB("RGB"),
        CMYK("CMYK"),
        YCbCr("YCbCr"),
        YCCK("YCCK"),
        YCC("YCC"),
        OTHER("Other"),
        UNKNOWN("Unknown");

        private String description;

        private ColorType(String description) {
            this.description = description;
        }

        public String toString() {
            return this.description;
        }
    }
}

