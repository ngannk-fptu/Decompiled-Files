/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.bmp;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import javax.imageio.IIOException;

abstract class DIBHeader {
    private final int DEFAULT_PIXELS_PER_METER = 2835;
    protected int size;
    protected int width;
    protected int height;
    protected boolean topDown = false;
    protected int planes;
    protected int bitCount;
    protected int compression;
    protected int imageSize;
    protected int xPixelsPerMeter;
    protected int yPixelsPerMeter;
    protected int colorsUsed;
    protected int colorsImportant;
    protected int[] masks;
    protected int colorSpaceType;
    protected double[] cieXYZEndpoints;
    protected int[] gamma;
    protected int intent;
    protected long profileData;
    protected long profileSize;

    protected DIBHeader() {
    }

    public static DIBHeader read(DataInput dataInput) throws IOException {
        int n = dataInput.readInt();
        DIBHeader dIBHeader = DIBHeader.createHeader(n);
        dIBHeader.read(n, dataInput);
        return dIBHeader;
    }

    private static DIBHeader createHeader(int n) throws IOException {
        switch (n) {
            case 12: {
                return new BitmapCoreHeader();
            }
            case 16: 
            case 64: {
                return new BitmapCoreHeaderV2();
            }
            case 40: 
            case 52: 
            case 56: {
                return new BitmapInfoHeader();
            }
            case 108: {
                return new BitmapV4InfoHeader();
            }
            case 124: {
                return new BitmapV5InfoHeader();
            }
        }
        throw new IIOException(String.format("Unknown Bitmap Information Header (size: %s)", n));
    }

    protected abstract void read(int var1, DataInput var2) throws IOException;

    public final int getSize() {
        return this.size;
    }

    public final int getWidth() {
        return this.width;
    }

    public final int getHeight() {
        return this.height;
    }

    public final int getPlanes() {
        return this.planes;
    }

    public final int getBitCount() {
        return this.bitCount;
    }

    public int getCompression() {
        return this.compression;
    }

    public int getImageSize() {
        return this.imageSize != 0 ? this.imageSize : (this.bitCount * this.width + 31) / 32 * 4 * this.height;
    }

    public int getXPixelsPerMeter() {
        return this.xPixelsPerMeter != 0 ? this.xPixelsPerMeter : 2835;
    }

    public int getYPixelsPerMeter() {
        return this.yPixelsPerMeter != 0 ? this.yPixelsPerMeter : 2835;
    }

    public int getColorsUsed() {
        return this.colorsUsed != 0 ? this.colorsUsed : 1 << Math.min(24, this.bitCount);
    }

    public int getColorsImportant() {
        return this.colorsImportant != 0 ? this.colorsImportant : this.getColorsUsed();
    }

    public boolean hasMasks() {
        return this.masks != null || this.compression == 3 || this.compression == 6;
    }

    public String toString() {
        return String.format("%s: size: %d bytes, width: %d, height: %d, planes: %d, bit count: %d, compression: %d, image size: %d%s, X pixels per m: %d, Y pixels per m: %d, colors used: %d%s, colors important: %d%s", this.getClass().getSimpleName(), this.getSize(), this.getWidth(), this.getHeight(), this.getPlanes(), this.getBitCount(), this.getCompression(), this.getImageSize(), this.imageSize == 0 ? " (calculated)" : "", this.getXPixelsPerMeter(), this.getYPixelsPerMeter(), this.getColorsUsed(), this.colorsUsed == 0 ? " (unknown)" : "", this.getColorsImportant(), this.colorsImportant == 0 ? " (all)" : "");
    }

    private static int[] readMasks(DataInput dataInput, boolean bl) throws IOException {
        int n = bl ? 4 : 3;
        int[] nArray = new int[4];
        for (int i = 0; i < n; ++i) {
            nArray[i] = dataInput.readInt();
        }
        return nArray;
    }

    protected abstract String getBMPVersion();

    static final class BitmapV5InfoHeader
    extends DIBHeader {
        BitmapV5InfoHeader() {
        }

        @Override
        protected void read(int n, DataInput dataInput) throws IOException {
            int n2;
            if (n != 124) {
                throw new IIOException(String.format("Size: %s !=: %s", n, 124));
            }
            this.size = n;
            this.width = dataInput.readInt();
            this.height = dataInput.readInt();
            if (this.height < 0) {
                this.height = -this.height;
                this.topDown = true;
            }
            this.planes = dataInput.readUnsignedShort();
            this.bitCount = dataInput.readUnsignedShort();
            this.compression = dataInput.readInt();
            this.imageSize = dataInput.readInt();
            this.xPixelsPerMeter = dataInput.readInt();
            this.yPixelsPerMeter = dataInput.readInt();
            this.colorsUsed = dataInput.readInt();
            this.colorsImportant = dataInput.readInt();
            this.masks = DIBHeader.readMasks(dataInput, true);
            this.colorSpaceType = dataInput.readInt();
            this.cieXYZEndpoints = new double[9];
            for (n2 = 0; n2 < this.cieXYZEndpoints.length; ++n2) {
                this.cieXYZEndpoints[n2] = dataInput.readInt();
            }
            this.gamma = new int[3];
            for (n2 = 0; n2 < this.gamma.length; ++n2) {
                this.gamma[n2] = dataInput.readInt();
            }
            this.intent = dataInput.readInt();
            this.profileData = (long)dataInput.readInt() & 0xFFFFFFFFL;
            this.profileSize = (long)dataInput.readInt() & 0xFFFFFFFFL;
            dataInput.readInt();
        }

        @Override
        public String getBMPVersion() {
            return "BMP v. 5.x";
        }
    }

    static final class BitmapV4InfoHeader
    extends DIBHeader {
        BitmapV4InfoHeader() {
        }

        @Override
        protected void read(int n, DataInput dataInput) throws IOException {
            int n2;
            if (n != 108) {
                throw new IIOException(String.format("Size: %s !=: %s", n, 108));
            }
            this.size = n;
            this.width = dataInput.readInt();
            this.height = dataInput.readInt();
            if (this.height < 0) {
                this.height = -this.height;
                this.topDown = true;
            }
            this.planes = dataInput.readUnsignedShort();
            this.bitCount = dataInput.readUnsignedShort();
            this.compression = dataInput.readInt();
            this.imageSize = dataInput.readInt();
            this.xPixelsPerMeter = dataInput.readInt();
            this.yPixelsPerMeter = dataInput.readInt();
            this.colorsUsed = dataInput.readInt();
            this.colorsImportant = dataInput.readInt();
            this.masks = DIBHeader.readMasks(dataInput, true);
            this.colorSpaceType = dataInput.readInt();
            this.cieXYZEndpoints = new double[9];
            for (n2 = 0; n2 < this.cieXYZEndpoints.length; ++n2) {
                this.cieXYZEndpoints[n2] = dataInput.readInt();
            }
            this.gamma = new int[3];
            for (n2 = 0; n2 < this.gamma.length; ++n2) {
                this.gamma[n2] = dataInput.readInt();
            }
        }

        @Override
        public String getBMPVersion() {
            return "BMP v. 4.x";
        }
    }

    static final class BitmapInfoHeader
    extends DIBHeader {
        BitmapInfoHeader() {
        }

        @Override
        protected void read(int n, DataInput dataInput) throws IOException {
            if (n != 40 && n != 52 && n != 56) {
                throw new IIOException(String.format("Size: %s !=: %s", n, 40));
            }
            this.size = n;
            this.width = dataInput.readInt();
            this.height = dataInput.readInt();
            if (this.height < 0) {
                this.height = -this.height;
                this.topDown = true;
            }
            this.planes = dataInput.readUnsignedShort();
            this.bitCount = dataInput.readUnsignedShort();
            this.compression = dataInput.readInt();
            this.imageSize = dataInput.readInt();
            this.xPixelsPerMeter = dataInput.readInt();
            this.yPixelsPerMeter = dataInput.readInt();
            this.colorsUsed = dataInput.readInt();
            this.colorsImportant = dataInput.readInt();
            if (this.size == 52 || this.compression == 3) {
                this.masks = DIBHeader.readMasks(dataInput, false);
            } else if (this.size == 56 || this.compression == 6) {
                this.masks = DIBHeader.readMasks(dataInput, true);
            }
        }

        void write(DataOutput dataOutput) throws IOException {
            dataOutput.writeInt(40);
            dataOutput.writeInt(this.width);
            dataOutput.writeInt(this.topDown ? -this.height : this.height);
            dataOutput.writeShort(this.planes);
            dataOutput.writeShort(this.bitCount);
            dataOutput.writeInt(this.compression);
            dataOutput.writeInt(this.imageSize);
            dataOutput.writeInt(this.xPixelsPerMeter);
            dataOutput.writeInt(this.yPixelsPerMeter);
            dataOutput.writeInt(this.colorsUsed);
            dataOutput.writeInt(this.colorsImportant);
        }

        @Override
        public String getBMPVersion() {
            return this.size > 40 ? "BMP V2/V3 INFO" : (this.compression == 3 || this.compression == 6 ? "BMP v. 3.x NT" : "BMP v. 3.x");
        }
    }

    static final class BitmapCoreHeaderV2
    extends DIBHeader {
        BitmapCoreHeaderV2() {
        }

        @Override
        protected void read(int n, DataInput dataInput) throws IOException {
            if (n != 64 && n != 16) {
                throw new IIOException(String.format("Size: %s !=: %s", n, 64));
            }
            this.size = n;
            this.width = dataInput.readInt();
            this.height = dataInput.readInt();
            if (this.height < 0) {
                this.height = -this.height;
                this.topDown = true;
            }
            this.planes = dataInput.readUnsignedShort();
            this.bitCount = dataInput.readUnsignedShort();
            if (n != 16) {
                this.compression = dataInput.readInt();
                this.imageSize = dataInput.readInt();
                this.xPixelsPerMeter = dataInput.readInt();
                this.yPixelsPerMeter = dataInput.readInt();
                this.colorsUsed = dataInput.readInt();
                this.colorsImportant = dataInput.readInt();
            }
            short s = dataInput.readShort();
            short s2 = dataInput.readShort();
            short s3 = dataInput.readShort();
            short s4 = dataInput.readShort();
            int n2 = dataInput.readInt();
            int n3 = dataInput.readInt();
            int n4 = dataInput.readInt();
            int n5 = dataInput.readInt();
        }

        @Override
        public String getBMPVersion() {
            return "BMP v. 2.2";
        }
    }

    static final class BitmapCoreHeader
    extends DIBHeader {
        BitmapCoreHeader() {
        }

        @Override
        protected void read(int n, DataInput dataInput) throws IOException {
            if (n != 12) {
                throw new IIOException(String.format("Size: %s !=: %s", n, 12));
            }
            this.size = n;
            this.width = dataInput.readUnsignedShort();
            this.height = dataInput.readShort();
            if (this.height < 0) {
                this.height = -this.height;
                this.topDown = true;
            }
            this.planes = dataInput.readUnsignedShort();
            this.bitCount = dataInput.readUnsignedShort();
        }

        @Override
        public String getBMPVersion() {
            return "BMP v. 2.x";
        }
    }
}

