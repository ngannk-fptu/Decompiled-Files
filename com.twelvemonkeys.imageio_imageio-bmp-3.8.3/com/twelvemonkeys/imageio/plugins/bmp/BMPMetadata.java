/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.AbstractMetadata
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.plugins.bmp;

import com.twelvemonkeys.imageio.AbstractMetadata;
import com.twelvemonkeys.imageio.plugins.bmp.DIBHeader;
import com.twelvemonkeys.lang.Validate;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;

final class BMPMetadata
extends AbstractMetadata {
    public static final String nativeMetadataFormatName = "javax_imageio_bmp_1.0";
    private final DIBHeader header;
    private final int[] colorMap;

    BMPMetadata(DIBHeader dIBHeader, int[] nArray) {
        super(true, nativeMetadataFormatName, "com.sun.imageio.plugins.bmp.BMPMetadataFormat", null, null);
        this.header = (DIBHeader)Validate.notNull((Object)dIBHeader, (String)"header");
        this.colorMap = nArray == null || nArray.length == 0 ? null : nArray;
    }

    protected Node getNativeTree() {
        IIOMetadataNode iIOMetadataNode;
        IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode(nativeMetadataFormatName);
        this.addChildNode(iIOMetadataNode2, "BMPVersion", this.header.getBMPVersion());
        this.addChildNode(iIOMetadataNode2, "Width", this.header.getWidth());
        this.addChildNode(iIOMetadataNode2, "Height", this.header.getHeight());
        this.addChildNode(iIOMetadataNode2, "BitsPerPixel", (short)this.header.getBitCount());
        this.addChildNode(iIOMetadataNode2, "Compression", this.header.getCompression());
        this.addChildNode(iIOMetadataNode2, "ImageSize", this.header.getImageSize());
        IIOMetadataNode iIOMetadataNode3 = this.addChildNode(iIOMetadataNode2, "PixelsPerMeter", null);
        this.addChildNode(iIOMetadataNode3, "X", this.header.xPixelsPerMeter);
        this.addChildNode(iIOMetadataNode3, "Y", this.header.yPixelsPerMeter);
        this.addChildNode(iIOMetadataNode2, "ColorsUsed", this.header.colorsUsed);
        this.addChildNode(iIOMetadataNode2, "ColorsImportant", this.header.colorsImportant);
        if (this.header.getSize() == 108 || this.header.getSize() == 124) {
            iIOMetadataNode = this.addChildNode(iIOMetadataNode2, "Mask", null);
            this.addChildNode(iIOMetadataNode, "Red", this.header.masks[0]);
            this.addChildNode(iIOMetadataNode, "Green", this.header.masks[1]);
            this.addChildNode(iIOMetadataNode, "Blue", this.header.masks[2]);
            this.addChildNode(iIOMetadataNode, "Alpha", this.header.masks[3]);
            this.addChildNode(iIOMetadataNode2, "ColorSpaceType", this.header.colorSpaceType);
            IIOMetadataNode iIOMetadataNode4 = this.addChildNode(iIOMetadataNode2, "CIEXYZEndPoints", null);
            this.addXYZPoints(iIOMetadataNode4, "Red", this.header.cieXYZEndpoints[0], this.header.cieXYZEndpoints[1], this.header.cieXYZEndpoints[2]);
            this.addXYZPoints(iIOMetadataNode4, "Green", this.header.cieXYZEndpoints[3], this.header.cieXYZEndpoints[4], this.header.cieXYZEndpoints[5]);
            this.addXYZPoints(iIOMetadataNode4, "Blue", this.header.cieXYZEndpoints[6], this.header.cieXYZEndpoints[7], this.header.cieXYZEndpoints[8]);
            this.addChildNode(iIOMetadataNode2, "Intent", this.header.intent);
        }
        if (this.colorMap != null) {
            iIOMetadataNode = this.addChildNode(iIOMetadataNode2, "Palette", null);
            boolean bl = this.header.getSize() != 12;
            for (int n : this.colorMap) {
                IIOMetadataNode iIOMetadataNode5 = this.addChildNode(iIOMetadataNode, "PaletteEntry", null);
                this.addChildNode(iIOMetadataNode5, "Red", (byte)(n >> 16 & 0xFF));
                this.addChildNode(iIOMetadataNode5, "Green", (byte)(n >> 8 & 0xFF));
                this.addChildNode(iIOMetadataNode5, "Blue", (byte)(n & 0xFF));
                if (!bl) continue;
                this.addChildNode(iIOMetadataNode5, "Alpha", (byte)(n >>> 24 & 0xFF));
            }
        }
        return iIOMetadataNode2;
    }

    private void addXYZPoints(IIOMetadataNode iIOMetadataNode, String string, double d, double d2, double d3) {
        IIOMetadataNode iIOMetadataNode2 = this.addChildNode(iIOMetadataNode, string, null);
        this.addChildNode(iIOMetadataNode2, "X", d);
        this.addChildNode(iIOMetadataNode2, "Y", d2);
        this.addChildNode(iIOMetadataNode2, "Z", d3);
    }

    private IIOMetadataNode addChildNode(IIOMetadataNode iIOMetadataNode, String string, Object object) {
        IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode(string);
        if (object != null) {
            iIOMetadataNode2.setUserObject(object);
            iIOMetadataNode2.setNodeValue(object.toString());
        }
        iIOMetadataNode.appendChild(iIOMetadataNode2);
        return iIOMetadataNode2;
    }

    protected IIOMetadataNode getStandardChromaNode() {
        if (this.colorMap != null) {
            IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("Chroma");
            IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("Palette");
            iIOMetadataNode.appendChild(iIOMetadataNode2);
            for (int i = 0; i < this.colorMap.length; ++i) {
                IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("PaletteEntry");
                iIOMetadataNode3.setAttribute("index", Integer.toString(i));
                iIOMetadataNode3.setAttribute("red", Integer.toString(this.colorMap[i] >> 16 & 0xFF));
                iIOMetadataNode3.setAttribute("green", Integer.toString(this.colorMap[i] >> 8 & 0xFF));
                iIOMetadataNode3.setAttribute("blue", Integer.toString(this.colorMap[i] & 0xFF));
                iIOMetadataNode2.appendChild(iIOMetadataNode3);
            }
            return iIOMetadataNode;
        }
        return null;
    }

    protected IIOMetadataNode getStandardCompressionNode() {
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("Compression");
        IIOMetadataNode iIOMetadataNode2 = this.addChildNode(iIOMetadataNode, "CompressionTypeName", null);
        switch (this.header.compression) {
            case 1: 
            case 2: {
                iIOMetadataNode2.setAttribute("value", "RLE");
                break;
            }
            case 4: {
                iIOMetadataNode2.setAttribute("value", "JPEG");
                break;
            }
            case 5: {
                iIOMetadataNode2.setAttribute("value", "PNG");
                break;
            }
            default: {
                iIOMetadataNode2.setAttribute("value", "NONE");
            }
        }
        return iIOMetadataNode;
    }

    protected IIOMetadataNode getStandardDataNode() {
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("Data");
        IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("BitsPerSample");
        switch (this.header.getBitCount()) {
            case 1: 
            case 2: 
            case 4: 
            case 8: {
                iIOMetadataNode2.setAttribute("value", this.createListValue(1, Integer.toString(this.header.getBitCount())));
                break;
            }
            case 16: {
                iIOMetadataNode2.setAttribute("value", this.header.hasMasks() ? this.createBitsPerSampleForBitMasks() : this.createListValue(3, Integer.toString(5)));
                break;
            }
            case 24: {
                iIOMetadataNode2.setAttribute("value", this.createListValue(3, Integer.toString(8)));
                break;
            }
            case 32: {
                iIOMetadataNode2.setAttribute("value", this.header.hasMasks() ? this.createBitsPerSampleForBitMasks() : this.createListValue(3, Integer.toString(8)));
            }
        }
        iIOMetadataNode.appendChild(iIOMetadataNode2);
        return iIOMetadataNode;
    }

    private String createBitsPerSampleForBitMasks() {
        boolean bl = this.header.masks[3] != 0;
        return this.createListValue(bl ? 4 : 3, Integer.toString(this.countMaskBits(this.header.masks[0])), Integer.toString(this.countMaskBits(this.header.masks[1])), Integer.toString(this.countMaskBits(this.header.masks[2])), Integer.toString(this.countMaskBits(this.header.masks[3])));
    }

    private int countMaskBits(int n) {
        int n2 = 0;
        while (n != 0) {
            n &= n - 1;
            ++n2;
        }
        return n2;
    }

    private String createListValue(int n, String ... stringArray) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < n; ++i) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(' ');
            }
            stringBuilder.append(stringArray[i % stringArray.length]);
        }
        return stringBuilder.toString();
    }

    protected IIOMetadataNode getStandardDimensionNode() {
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("Dimension");
        if (this.header.xPixelsPerMeter > 0 && this.header.yPixelsPerMeter > 0) {
            float f = (float)this.header.xPixelsPerMeter / (float)this.header.yPixelsPerMeter;
            this.addChildNode(iIOMetadataNode, "PixelAspectRatio", null).setAttribute("value", String.valueOf(f));
            this.addChildNode(iIOMetadataNode, "HorizontalPixelSize", null).setAttribute("value", String.valueOf(1.0f / (float)this.header.xPixelsPerMeter * 1000.0f));
            this.addChildNode(iIOMetadataNode, "VerticalPixelSize", null).setAttribute("value", String.valueOf(1.0f / (float)this.header.yPixelsPerMeter * 1000.0f));
            this.addChildNode(iIOMetadataNode, "HorizontalPhysicalPixelSpacing", null).setAttribute("value", String.valueOf(0));
            this.addChildNode(iIOMetadataNode, "VerticalPhysicalPixelSpacing", null).setAttribute("value", String.valueOf(0));
        }
        if (this.header.topDown) {
            this.addChildNode(iIOMetadataNode, "ImageOrientation", null).setAttribute("value", "FlipH");
        }
        return iIOMetadataNode;
    }

    protected IIOMetadataNode getStandardTransparencyNode() {
        if (this.header.hasMasks() && this.header.masks[3] != 0) {
            IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("Transparency");
            IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("Alpha");
            iIOMetadataNode2.setAttribute("value", "nonpremultiplied");
            iIOMetadataNode.appendChild(iIOMetadataNode2);
            return iIOMetadataNode;
        }
        return null;
    }
}

