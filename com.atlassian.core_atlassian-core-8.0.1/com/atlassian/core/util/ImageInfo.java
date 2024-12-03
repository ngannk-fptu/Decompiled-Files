/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  org.apache.commons.lang3.ArrayUtils
 */
package com.atlassian.core.util;

import com.atlassian.annotations.VisibleForTesting;
import java.io.DataInput;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Vector;
import org.apache.commons.lang3.ArrayUtils;

public class ImageInfo {
    public static final int FORMAT_JPEG = 0;
    public static final int FORMAT_GIF = 1;
    public static final int FORMAT_PNG = 2;
    public static final int FORMAT_BMP = 3;
    public static final int FORMAT_PCX = 4;
    public static final int FORMAT_IFF = 5;
    public static final int FORMAT_RAS = 6;
    public static final int FORMAT_PBM = 7;
    public static final int FORMAT_PGM = 8;
    public static final int FORMAT_PPM = 9;
    public static final int FORMAT_PSD = 10;
    public static final int FORMAT_SWF = 11;
    private static final String[] FORMAT_NAMES = new String[]{"JPEG", "GIF", "PNG", "BMP", "PCX", "IFF", "RAS", "PBM", "PGM", "PPM", "PSD", "SWF"};
    private static final String[] MIME_TYPE_STRINGS = new String[]{"image/jpeg", "image/gif", "image/png", "image/bmp", "image/pcx", "image/iff", "image/ras", "image/x-portable-bitmap", "image/x-portable-graymap", "image/x-portable-pixmap", "image/psd", "application/x-shockwave-flash"};
    static final int PNG_IHDR_TYPE = 1229472850;
    static final int PNG_PLTE_TYPE = 1347179589;
    static final int PNG_IDAT_TYPE = 1229209940;
    static final int PNG_IEND_TYPE = 1229278788;
    static final int PNG_TEXT_TYPE = 1950701684;
    static final int PNG_TEXT_KEYWORD_LIMIT = 79;
    static final int PNG_UNKNOWN_CHUNK_LENGTH_LIMIT = 0xA00000;
    static final int[] PNG_ANCILLARY_CHUNK_TYPES = new int[]{1649100612, 1665684045, 1732332865, 1749635924, 1766015824, 1767135348, 1883789683, 1933723988, 1934642260, 1934772034, 1950960965, 1951551059, 2052348020};
    private int width;
    private int height;
    private int bitsPerPixel;
    private boolean progressive;
    private int format;
    private InputStream in;
    private DataInput din;
    private boolean collectComments = true;
    private Vector<String> comments;
    private boolean determineNumberOfImages;
    private int numberOfImages;
    private int physicalHeightDpi;
    private int physicalWidthDpi;
    private int bitBuf;
    private int bitPos;
    private boolean endOfStream = false;
    private String pngExpectedKeyword = null;
    private boolean pngValidateMetadata = false;

    private void addComment(String s) {
        if (this.comments == null) {
            this.comments = new Vector();
        }
        this.comments.addElement(s);
    }

    public boolean check() {
        this.format = -1;
        this.width = -1;
        this.height = -1;
        this.bitsPerPixel = -1;
        this.numberOfImages = 1;
        this.physicalHeightDpi = -1;
        this.physicalWidthDpi = -1;
        this.comments = null;
        try {
            int b1 = this.read() & 0xFF;
            int b2 = this.read() & 0xFF;
            if (b1 == 71 && b2 == 73) {
                return this.checkGif();
            }
            if (b1 == 137 && b2 == 80) {
                return this.checkPng();
            }
            if (b1 == 255 && b2 == 216) {
                return this.checkJpeg();
            }
            if (b1 == 66 && b2 == 77) {
                return this.checkBmp();
            }
            if (b1 == 10 && b2 < 6) {
                return this.checkPcx();
            }
            if (b1 == 70 && b2 == 79) {
                return this.checkIff();
            }
            if (b1 == 89 && b2 == 166) {
                return this.checkRas();
            }
            if (b1 == 80 && b2 >= 49 && b2 <= 54) {
                return this.checkPnm(b2 - 48);
            }
            if (b1 == 56 && b2 == 66) {
                return this.checkPsd();
            }
            if (b1 == 70 && b2 == 87) {
                return this.checkSwf();
            }
            return false;
        }
        catch (IOException ioe) {
            return false;
        }
    }

    private boolean checkBmp() throws IOException {
        int y;
        byte[] a = new byte[44];
        if (this.read(a) != a.length) {
            return false;
        }
        this.width = ImageInfo.getIntLittleEndian(a, 16);
        this.height = ImageInfo.getIntLittleEndian(a, 20);
        if (this.width < 1 || this.height < 1) {
            return false;
        }
        this.bitsPerPixel = ImageInfo.getShortLittleEndian(a, 26);
        if (this.bitsPerPixel != 1 && this.bitsPerPixel != 4 && this.bitsPerPixel != 8 && this.bitsPerPixel != 16 && this.bitsPerPixel != 24 && this.bitsPerPixel != 32) {
            return false;
        }
        int x = (int)((double)ImageInfo.getIntLittleEndian(a, 36) * 0.0254);
        if (x > 0) {
            this.setPhysicalWidthDpi(x);
        }
        if ((y = (int)((double)ImageInfo.getIntLittleEndian(a, 40) * 0.0254)) > 0) {
            this.setPhysicalHeightDpi(y);
        }
        this.format = 3;
        return true;
    }

    private boolean checkGif() throws IOException {
        int blockType;
        byte[] GIF_MAGIC_87A = new byte[]{70, 56, 55, 97};
        byte[] GIF_MAGIC_89A = new byte[]{70, 56, 57, 97};
        byte[] a = new byte[11];
        if (this.read(a) != 11) {
            return false;
        }
        if (!ImageInfo.equals(a, 0, GIF_MAGIC_89A, 0, 4) && !ImageInfo.equals(a, 0, GIF_MAGIC_87A, 0, 4)) {
            return false;
        }
        this.format = 1;
        this.width = ImageInfo.getShortLittleEndian(a, 4);
        this.height = ImageInfo.getShortLittleEndian(a, 6);
        int flags = a[8] & 0xFF;
        this.bitsPerPixel = (flags >> 4 & 7) + 1;
        if (!this.determineNumberOfImages) {
            return true;
        }
        if ((flags & 0x80) != 0) {
            int tableSize = (1 << (flags & 7) + 1) * 3;
            this.skip(tableSize);
        }
        this.numberOfImages = 0;
        do {
            blockType = this.read();
            switch (blockType) {
                case 44: {
                    int n;
                    if (this.read(a, 0, 9) != 9) {
                        return false;
                    }
                    flags = a[8] & 0xFF;
                    this.progressive = (flags & 0x40) != 0;
                    int localBitsPerPixel = (flags & 7) + 1;
                    if (localBitsPerPixel > this.bitsPerPixel) {
                        this.bitsPerPixel = localBitsPerPixel;
                    }
                    if ((flags & 0x80) != 0) {
                        this.skip((1 << localBitsPerPixel) * 3);
                    }
                    this.skip(1);
                    do {
                        if ((n = this.read()) > 0) {
                            this.skip(n);
                            continue;
                        }
                        if (n != -1) continue;
                        return false;
                    } while (n > 0);
                    ++this.numberOfImages;
                    break;
                }
                case 33: {
                    int n;
                    int extensionType = this.read();
                    if (this.collectComments && extensionType == 254) {
                        int n2;
                        StringBuilder sb = new StringBuilder();
                        do {
                            if ((n2 = this.read()) == -1) {
                                return false;
                            }
                            if (n2 <= 0) continue;
                            for (int i = 0; i < n2; ++i) {
                                int ch = this.read();
                                if (ch == -1) {
                                    return false;
                                }
                                sb.append((char)ch);
                            }
                        } while (n2 > 0);
                        break;
                    }
                    do {
                        if ((n = this.read()) > 0) {
                            this.skip(n);
                            continue;
                        }
                        if (n != -1) continue;
                        return false;
                    } while (n > 0);
                    break;
                }
                case 59: {
                    break;
                }
                default: {
                    return false;
                }
            }
        } while (blockType != 59);
        return true;
    }

    private boolean checkIff() throws IOException {
        byte[] a = new byte[10];
        if (this.read(a, 0, 10) != 10) {
            return false;
        }
        byte[] IFF_RM = new byte[]{82, 77};
        if (!ImageInfo.equals(a, 0, IFF_RM, 0, 2)) {
            return false;
        }
        int type = ImageInfo.getIntBigEndian(a, 6);
        if (type != 1229734477 && type != 1346522400) {
            return false;
        }
        while (this.read(a, 0, 8) == 8) {
            int chunkId = ImageInfo.getIntBigEndian(a, 0);
            int size = ImageInfo.getIntBigEndian(a, 4);
            if ((size & 1) == 1) {
                ++size;
            }
            if (chunkId == 1112361028) {
                if (this.read(a, 0, 9) != 9) {
                    return false;
                }
                this.format = 5;
                this.width = ImageInfo.getShortBigEndian(a, 0);
                this.height = ImageInfo.getShortBigEndian(a, 2);
                this.bitsPerPixel = a[8] & 0xFF;
                return this.width > 0 && this.height > 0 && this.bitsPerPixel > 0 && this.bitsPerPixel < 33;
            }
            this.skip(size);
        }
        return false;
    }

    private boolean checkJpeg() throws IOException {
        byte[] data = new byte[12];
        while (this.read(data, 0, 4) == 4) {
            int marker = ImageInfo.getShortBigEndian(data, 0);
            int size = ImageInfo.getShortBigEndian(data, 2);
            if ((marker & 0xFF00) != 65280) {
                return false;
            }
            if (marker == 65504) {
                if (size < 14) {
                    return false;
                }
                if (this.read(data, 0, 12) != 12) {
                    return false;
                }
                byte[] APP0_ID = new byte[]{74, 70, 73, 70, 0};
                if (ImageInfo.equals(APP0_ID, 0, data, 0, 5)) {
                    if (data[7] == 1) {
                        this.setPhysicalWidthDpi(ImageInfo.getShortBigEndian(data, 8));
                        this.setPhysicalHeightDpi(ImageInfo.getShortBigEndian(data, 10));
                    } else if (data[7] == 2) {
                        int x = ImageInfo.getShortBigEndian(data, 8);
                        int y = ImageInfo.getShortBigEndian(data, 10);
                        this.setPhysicalWidthDpi((int)((float)x * 2.54f));
                        this.setPhysicalHeightDpi((int)((float)y * 2.54f));
                    }
                }
                this.skip(size - 14);
                continue;
            }
            if (this.collectComments && size > 2 && marker == 65534) {
                byte[] chars = new byte[size -= 2];
                if (this.read(chars, 0, size) != size) {
                    return false;
                }
                String comment = new String(chars, "iso-8859-1");
                comment = comment.trim();
                this.addComment(comment);
                continue;
            }
            if (marker >= 65472 && marker <= 65487 && marker != 65476 && marker != 65480) {
                if (this.read(data, 0, 6) != 6) {
                    return false;
                }
                this.format = 0;
                this.bitsPerPixel = (data[0] & 0xFF) * (data[5] & 0xFF);
                this.progressive = marker == 65474 || marker == 65478 || marker == 65482 || marker == 65486;
                this.width = ImageInfo.getShortBigEndian(data, 3);
                this.height = ImageInfo.getShortBigEndian(data, 1);
                return true;
            }
            this.skip(size - 2);
        }
        return false;
    }

    private boolean checkPcx() throws IOException {
        byte[] a = new byte[64];
        if (this.read(a) != a.length) {
            return false;
        }
        if (a[0] != 1) {
            return false;
        }
        int x1 = ImageInfo.getShortLittleEndian(a, 2);
        int y1 = ImageInfo.getShortLittleEndian(a, 4);
        int x2 = ImageInfo.getShortLittleEndian(a, 6);
        int y2 = ImageInfo.getShortLittleEndian(a, 8);
        if (x1 < 0 || x2 < x1 || y1 < 0 || y2 < y1) {
            return false;
        }
        this.width = x2 - x1 + 1;
        this.height = y2 - y1 + 1;
        int bits = a[1];
        byte planes = a[63];
        if (planes == 1 && (bits == 1 || bits == 2 || bits == 4 || bits == 8)) {
            this.bitsPerPixel = bits;
        } else if (planes == 3 && bits == 8) {
            this.bitsPerPixel = 24;
        } else {
            return false;
        }
        this.setPhysicalWidthDpi(ImageInfo.getShortLittleEndian(a, 10));
        this.setPhysicalHeightDpi(ImageInfo.getShortLittleEndian(a, 10));
        this.format = 4;
        return true;
    }

    private boolean checkPng() throws IOException {
        byte[] PNG_MAGIC = new byte[]{78, 71, 13, 10, 26, 10};
        byte[] a = new byte[31];
        if (this.read(a) != 31) {
            return false;
        }
        if (!ImageInfo.equals(a, 0, PNG_MAGIC, 0, 6)) {
            return false;
        }
        this.format = 2;
        this.width = ImageInfo.getIntBigEndian(a, 14);
        this.height = ImageInfo.getIntBigEndian(a, 18);
        this.bitsPerPixel = a[22] & 0xFF;
        int colorType = a[23] & 0xFF;
        if (colorType == 2 || colorType == 6) {
            this.bitsPerPixel *= 3;
        }
        boolean bl = this.progressive = (a[26] & 0xFF) != 0;
        if (this.pngValidateMetadata) {
            return this.validatePngMetaData();
        }
        return true;
    }

    private boolean validatePngMetaData() throws IOException {
        while (true) {
            int chunkLength = this.readInt();
            int chunkType = this.readInt();
            switch (chunkType) {
                case 1229472850: {
                    return false;
                }
                case 1229278788: {
                    return this.pngExpectedKeyword == null;
                }
                case 1347179589: {
                    break;
                }
                case 1229209940: {
                    break;
                }
                case 1950701684: {
                    if (this.pngExpectedKeyword == null) break;
                    String parsedKeyword = this.parsePngTextChunkKeyword(chunkLength);
                    chunkLength = 0;
                    if (parsedKeyword == null) {
                        return false;
                    }
                    if (!parsedKeyword.equals(this.pngExpectedKeyword)) break;
                    this.pngExpectedKeyword = null;
                    break;
                }
                default: {
                    if (ArrayUtils.contains((int[])PNG_ANCILLARY_CHUNK_TYPES, (int)chunkType) || chunkLength <= 0xA00000) break;
                    return false;
                }
            }
            this.skip(chunkLength);
            int n = this.readInt();
        }
    }

    private String parsePngTextChunkKeyword(int chunkLength) throws IOException {
        int nextByteOrEnd;
        boolean valid = true;
        byte[] keyword = new byte[79];
        int keywordLength = 0;
        int bytesToSkip = chunkLength;
        while (keywordLength < 79 && keywordLength < chunkLength) {
            nextByteOrEnd = this.read();
            --bytesToSkip;
            if (nextByteOrEnd < 0) {
                throw new EOFException();
            }
            if (nextByteOrEnd == 0) break;
            keyword[keywordLength++] = (byte)nextByteOrEnd;
        }
        if (keywordLength == chunkLength) {
            valid = false;
        } else if (keywordLength == 79) {
            nextByteOrEnd = this.read();
            --bytesToSkip;
            if (nextByteOrEnd < 0) {
                throw new EOFException();
            }
            if (nextByteOrEnd > 0) {
                valid = false;
            }
        }
        this.skip(bytesToSkip);
        return valid ? new String(keyword, 0, keywordLength, StandardCharsets.ISO_8859_1) : null;
    }

    public void setValidatePngMetadata() {
        this.pngValidateMetadata = true;
    }

    public void setExpectedPngTextKeyword(String keyword) {
        this.setValidatePngMetadata();
        this.pngExpectedKeyword = keyword;
    }

    private boolean checkPnm(int id) throws IOException {
        if (id < 1 || id > 6) {
            return false;
        }
        int[] PNM_FORMATS = new int[]{7, 8, 9};
        this.format = PNM_FORMATS[(id - 1) % 3];
        boolean hasPixelResolution = false;
        while (!this.endOfStream) {
            int maxSample;
            String s = this.readLine();
            if (s != null) {
                s = s.trim();
            }
            if (s == null || s.length() < 1) continue;
            if (s.charAt(0) == '#') {
                if (!this.collectComments || s.length() <= 1) continue;
                this.addComment(s.substring(1));
                continue;
            }
            if (!hasPixelResolution) {
                int spaceIndex = s.indexOf(32);
                if (spaceIndex == -1) {
                    return false;
                }
                String widthString = s.substring(0, spaceIndex);
                spaceIndex = s.lastIndexOf(32);
                if (spaceIndex == -1) {
                    return false;
                }
                String heightString = s.substring(spaceIndex + 1);
                try {
                    this.width = Integer.parseInt(widthString);
                    this.height = Integer.parseInt(heightString);
                }
                catch (NumberFormatException nfe) {
                    return false;
                }
                if (this.width < 1 || this.height < 1) {
                    return false;
                }
                if (this.format == 7) {
                    this.bitsPerPixel = 1;
                    return true;
                }
                hasPixelResolution = true;
                continue;
            }
            try {
                maxSample = Integer.parseInt(s);
            }
            catch (NumberFormatException nfe) {
                return false;
            }
            if (maxSample < 0) {
                return false;
            }
            for (int i = 0; i < 25; ++i) {
                if (maxSample >= 1 << i + 1) continue;
                this.bitsPerPixel = i + 1;
                if (this.format == 9) {
                    this.bitsPerPixel *= 3;
                }
                return true;
            }
            return false;
        }
        return false;
    }

    private boolean checkPsd() throws IOException {
        byte[] a = new byte[24];
        if (this.read(a) != a.length) {
            return false;
        }
        byte[] PSD_MAGIC = new byte[]{80, 83};
        if (!ImageInfo.equals(a, 0, PSD_MAGIC, 0, 2)) {
            return false;
        }
        this.format = 10;
        this.width = ImageInfo.getIntBigEndian(a, 16);
        this.height = ImageInfo.getIntBigEndian(a, 12);
        int channels = ImageInfo.getShortBigEndian(a, 10);
        int depth = ImageInfo.getShortBigEndian(a, 20);
        this.bitsPerPixel = channels * depth;
        return this.width > 0 && this.height > 0 && this.bitsPerPixel > 0 && this.bitsPerPixel <= 64;
    }

    private boolean checkRas() throws IOException {
        byte[] a = new byte[14];
        if (this.read(a) != a.length) {
            return false;
        }
        byte[] RAS_MAGIC = new byte[]{106, -107};
        if (!ImageInfo.equals(a, 0, RAS_MAGIC, 0, 2)) {
            return false;
        }
        this.format = 6;
        this.width = ImageInfo.getIntBigEndian(a, 2);
        this.height = ImageInfo.getIntBigEndian(a, 6);
        this.bitsPerPixel = ImageInfo.getIntBigEndian(a, 10);
        return this.width > 0 && this.height > 0 && this.bitsPerPixel > 0 && this.bitsPerPixel <= 24;
    }

    private boolean checkSwf() throws IOException {
        byte[] a = new byte[6];
        if (this.read(a) != a.length) {
            return false;
        }
        this.format = 11;
        int bitSize = (int)this.readUBits(5);
        int maxX = this.readSBits(bitSize);
        int maxY = this.readSBits(bitSize);
        this.width = maxX / 20;
        this.height = maxY / 20;
        this.setPhysicalWidthDpi(72);
        this.setPhysicalHeightDpi(72);
        return this.width > 0 && this.height > 0;
    }

    private static boolean determineVerbosity(String[] args) {
        if (args != null && args.length > 0) {
            for (String arg : args) {
                if (!"-c".equals(arg)) continue;
                return false;
            }
        }
        return true;
    }

    private static boolean equals(byte[] a1, int offs1, byte[] a2, int offs2, int num) {
        while (num-- > 0) {
            if (a1[offs1++] == a2[offs2++]) continue;
            return false;
        }
        return true;
    }

    public int getBitsPerPixel() {
        return this.bitsPerPixel;
    }

    public String getComment(int index) {
        if (this.comments == null || index < 0 || index >= this.comments.size()) {
            throw new IllegalArgumentException("Not a valid comment index: " + index);
        }
        return this.comments.elementAt(index);
    }

    public int getFormat() {
        return this.format;
    }

    public String getFormatName() {
        if (this.format >= 0 && this.format < FORMAT_NAMES.length) {
            return FORMAT_NAMES[this.format];
        }
        return "?";
    }

    public int getHeight() {
        return this.height;
    }

    private static int getIntBigEndian(byte[] a, int offs) {
        return (a[offs] & 0xFF) << 24 | (a[offs + 1] & 0xFF) << 16 | (a[offs + 2] & 0xFF) << 8 | a[offs + 3] & 0xFF;
    }

    private static int getIntLittleEndian(byte[] a, int offs) {
        return (a[offs + 3] & 0xFF) << 24 | (a[offs + 2] & 0xFF) << 16 | (a[offs + 1] & 0xFF) << 8 | a[offs] & 0xFF;
    }

    public String getMimeType() {
        if (this.format >= 0 && this.format < MIME_TYPE_STRINGS.length) {
            if (this.format == 0 && this.progressive) {
                return "image/pjpeg";
            }
            return MIME_TYPE_STRINGS[this.format];
        }
        return null;
    }

    public int getNumberOfComments() {
        if (this.comments == null) {
            return 0;
        }
        return this.comments.size();
    }

    public int getNumberOfImages() {
        return this.numberOfImages;
    }

    public int getPhysicalHeightDpi() {
        return this.physicalHeightDpi;
    }

    public float getPhysicalHeightInch() {
        int h = this.getHeight();
        int ph = this.getPhysicalHeightDpi();
        if (h > 0 && ph > 0) {
            return (float)h / (float)ph;
        }
        return -1.0f;
    }

    public int getPhysicalWidthDpi() {
        return this.physicalWidthDpi;
    }

    public float getPhysicalWidthInch() {
        int w = this.getWidth();
        int pw = this.getPhysicalWidthDpi();
        if (w > 0 && pw > 0) {
            return (float)w / (float)pw;
        }
        return -1.0f;
    }

    private static int getShortBigEndian(byte[] a, int offs) {
        return (a[offs] & 0xFF) << 8 | a[offs + 1] & 0xFF;
    }

    private static int getShortLittleEndian(byte[] a, int offs) {
        return a[offs] & 0xFF | (a[offs + 1] & 0xFF) << 8;
    }

    public int getWidth() {
        return this.width;
    }

    public boolean isProgressive() {
        return this.progressive;
    }

    public static void main(String[] args) {
        ImageInfo imageInfo = new ImageInfo();
        imageInfo.setDetermineImageNumber(true);
        boolean verbose = ImageInfo.determineVerbosity(args);
        if (args.length == 0) {
            ImageInfo.run(null, System.in, imageInfo, verbose);
        } else {
            int index = 0;
            while (index < args.length) {
                InputStream in = null;
                try {
                    String name = args[index++];
                    System.out.print(name + ";");
                    in = name.startsWith("http://") ? new URL(name).openConnection().getInputStream() : new FileInputStream(name);
                    ImageInfo.run(name, in, imageInfo, verbose);
                    in.close();
                }
                catch (IOException e) {
                    try {
                        in.close();
                    }
                    catch (IOException iOException) {}
                }
            }
        }
    }

    private static void print(String sourceName, ImageInfo ii, boolean verbose) {
        if (verbose) {
            ImageInfo.printVerbose(sourceName, ii);
        } else {
            ImageInfo.printCompact(sourceName, ii);
        }
    }

    private static void printCompact(String sourceName, ImageInfo imageInfo) {
        String SEP = "\t";
        System.out.println(sourceName + "\t" + imageInfo.getFormatName() + "\t" + imageInfo.getMimeType() + "\t" + imageInfo.getWidth() + "\t" + imageInfo.getHeight() + "\t" + imageInfo.getBitsPerPixel() + "\t" + imageInfo.getNumberOfImages() + "\t" + imageInfo.getPhysicalWidthDpi() + "\t" + imageInfo.getPhysicalHeightDpi() + "\t" + imageInfo.getPhysicalWidthInch() + "\t" + imageInfo.getPhysicalHeightInch() + "\t" + imageInfo.isProgressive());
    }

    private static void printLine(int indentLevels, String text, float value, float minValidValue) {
        if (value < minValidValue) {
            return;
        }
        ImageInfo.printLine(indentLevels, text, Float.toString(value));
    }

    private static void printLine(int indentLevels, String text, int value, int minValidValue) {
        if (value >= minValidValue) {
            ImageInfo.printLine(indentLevels, text, Integer.toString(value));
        }
    }

    private static void printLine(int indentLevels, String text, String value) {
        if (value == null || value.length() == 0) {
            return;
        }
        while (indentLevels-- > 0) {
            System.out.print("\t");
        }
        if (text != null && text.length() > 0) {
            System.out.print(text);
            System.out.print(" ");
        }
        System.out.println(value);
    }

    private static void printVerbose(String sourceName, ImageInfo ii) {
        ImageInfo.printLine(0, null, sourceName);
        ImageInfo.printLine(1, "File format: ", ii.getFormatName());
        ImageInfo.printLine(1, "MIME type: ", ii.getMimeType());
        ImageInfo.printLine(1, "Width (pixels): ", ii.getWidth(), 1);
        ImageInfo.printLine(1, "Height (pixels): ", ii.getHeight(), 1);
        ImageInfo.printLine(1, "Bits per pixel: ", ii.getBitsPerPixel(), 1);
        ImageInfo.printLine(1, "Progressive: ", ii.isProgressive() ? "yes" : "no");
        ImageInfo.printLine(1, "Number of images: ", ii.getNumberOfImages(), 1);
        ImageInfo.printLine(1, "Physical width (dpi): ", ii.getPhysicalWidthDpi(), 1);
        ImageInfo.printLine(1, "Physical height (dpi): ", ii.getPhysicalHeightDpi(), 1);
        ImageInfo.printLine(1, "Physical width (inches): ", ii.getPhysicalWidthInch(), 1.0f);
        ImageInfo.printLine(1, "Physical height (inches): ", ii.getPhysicalHeightInch(), 1.0f);
        int numComments = ii.getNumberOfComments();
        ImageInfo.printLine(1, "Number of textual comments: ", numComments, 1);
        if (numComments > 0) {
            for (int i = 0; i < numComments; ++i) {
                ImageInfo.printLine(2, null, ii.getComment(i));
            }
        }
    }

    private int read() throws IOException {
        if (this.in != null) {
            return this.in.read();
        }
        return this.din.readUnsignedByte();
    }

    private int read(byte[] a) throws IOException {
        if (this.in != null) {
            return this.in.read(a);
        }
        this.din.readFully(a);
        return a.length;
    }

    private void readFully(byte[] a) throws IOException {
        if (this.in != null) {
            ImageInfo.readInputStreamFully(this.in, a);
        } else {
            this.din.readFully(a);
        }
    }

    @VisibleForTesting
    static void readInputStreamFully(InputStream stream, byte[] a) throws IOException {
        int actualBytesRead;
        int bytesNeeded = a.length;
        for (int byteCount = 0; byteCount < bytesNeeded; byteCount += actualBytesRead) {
            int bytesRemaining = bytesNeeded - byteCount;
            actualBytesRead = stream.read(a, byteCount, bytesRemaining);
            if (actualBytesRead >= 0) continue;
            throw new EOFException("Attempted to read " + bytesNeeded + " bytes, but only " + byteCount + " were available");
        }
    }

    private int read(byte[] a, int offset, int num) throws IOException {
        if (this.in != null) {
            return this.in.read(a, offset, num);
        }
        this.din.readFully(a, offset, num);
        return num;
    }

    private int readInt() throws IOException {
        byte[] buf = new byte[4];
        this.readFully(buf);
        return ImageInfo.getIntBigEndian(buf, 0);
    }

    private String readLine() throws IOException {
        return this.readLine(new StringBuffer());
    }

    private String readLine(StringBuffer sb) throws IOException {
        return this.readLine(sb, 10);
    }

    private String readLine(StringBuffer sb, int finishValue) throws IOException {
        boolean finished;
        do {
            int value;
            if ((value = this.read()) == -1) {
                this.endOfStream = true;
            }
            boolean bl = finished = value == -1 || value == finishValue;
            if (finished) continue;
            sb.append((char)value);
        } while (!finished);
        return sb.toString();
    }

    private long readUBits(int numBits) throws IOException {
        int shift;
        if (numBits == 0) {
            return 0L;
        }
        int bitsLeft = numBits;
        long result = 0L;
        if (this.bitPos == 0) {
            this.bitBuf = this.in != null ? this.in.read() : (int)this.din.readByte();
            this.bitPos = 8;
        }
        while ((shift = bitsLeft - this.bitPos) > 0) {
            result |= (long)(this.bitBuf << shift);
            bitsLeft -= this.bitPos;
            this.bitBuf = this.in != null ? this.in.read() : (int)this.din.readByte();
            this.bitPos = 8;
        }
        this.bitPos -= bitsLeft;
        this.bitBuf &= 255 >> 8 - this.bitPos;
        return result |= (long)(this.bitBuf >> -shift);
    }

    private int readSBits(int numBits) throws IOException {
        long uBits = this.readUBits(numBits);
        if ((uBits & 1L << numBits - 1) != 0L) {
            uBits |= -1L << numBits;
        }
        return (int)uBits;
    }

    private static void run(String sourceName, InputStream in, ImageInfo imageInfo, boolean verbose) {
        imageInfo.setInput(in);
        imageInfo.setDetermineImageNumber(true);
        imageInfo.setCollectComments(verbose);
        if (imageInfo.check()) {
            ImageInfo.print(sourceName, imageInfo, verbose);
        }
    }

    public void setCollectComments(boolean newValue) {
        this.collectComments = newValue;
    }

    public void setDetermineImageNumber(boolean newValue) {
        this.determineNumberOfImages = newValue;
    }

    public void setInput(DataInput dataInput) {
        this.din = dataInput;
        this.in = null;
    }

    public void setInput(InputStream inputStream) {
        this.in = inputStream;
        this.din = null;
    }

    private void setPhysicalHeightDpi(int newValue) {
        this.physicalWidthDpi = newValue;
    }

    private void setPhysicalWidthDpi(int newValue) {
        this.physicalHeightDpi = newValue;
    }

    private void skip(int numBytesToSkip) throws IOException {
        for (int bytesSkipped = 0; bytesSkipped < numBytesToSkip; ++bytesSkipped) {
            if (this.read() != -1) continue;
            throw new EOFException("Reached end of stream before " + numBytesToSkip + " bytes could be skipped");
        }
    }
}

