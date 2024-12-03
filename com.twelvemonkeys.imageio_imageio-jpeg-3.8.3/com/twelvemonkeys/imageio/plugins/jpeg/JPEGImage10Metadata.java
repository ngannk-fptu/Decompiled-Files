/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.AbstractMetadata
 *  com.twelvemonkeys.imageio.metadata.CompoundDirectory
 *  com.twelvemonkeys.imageio.metadata.Directory
 *  com.twelvemonkeys.imageio.metadata.Entry
 */
package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.AbstractMetadata;
import com.twelvemonkeys.imageio.metadata.CompoundDirectory;
import com.twelvemonkeys.imageio.metadata.Directory;
import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.plugins.jpeg.AdobeDCT;
import com.twelvemonkeys.imageio.plugins.jpeg.Application;
import com.twelvemonkeys.imageio.plugins.jpeg.Comment;
import com.twelvemonkeys.imageio.plugins.jpeg.Frame;
import com.twelvemonkeys.imageio.plugins.jpeg.HuffmanTable;
import com.twelvemonkeys.imageio.plugins.jpeg.ICCProfile;
import com.twelvemonkeys.imageio.plugins.jpeg.JFIF;
import com.twelvemonkeys.imageio.plugins.jpeg.JFXX;
import com.twelvemonkeys.imageio.plugins.jpeg.JPEGColorSpace;
import com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReader;
import com.twelvemonkeys.imageio.plugins.jpeg.QuantizationTable;
import com.twelvemonkeys.imageio.plugins.jpeg.RestartInterval;
import com.twelvemonkeys.imageio.plugins.jpeg.Scan;
import com.twelvemonkeys.imageio.plugins.jpeg.Segment;
import java.awt.color.ICC_Profile;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;

class JPEGImage10Metadata
extends AbstractMetadata {
    static final String JAVAX_IMAGEIO_JPEG_IMAGE_1_0 = "javax_imageio_jpeg_image_1.0";
    private final List<Segment> segments;
    private final Frame frame;
    private final JFIF jfif;
    private final AdobeDCT adobeDCT;
    private final JFXX jfxx;
    private final ICC_Profile embeddedICCProfile;
    private final CompoundDirectory exif;

    JPEGImage10Metadata(List<Segment> list, Frame frame, JFIF jFIF, JFXX jFXX, ICC_Profile iCC_Profile, AdobeDCT adobeDCT, CompoundDirectory compoundDirectory) {
        super(true, JAVAX_IMAGEIO_JPEG_IMAGE_1_0, null, null, null);
        this.segments = list;
        this.frame = frame;
        this.jfif = jFIF;
        this.adobeDCT = adobeDCT;
        this.jfxx = jFXX;
        this.embeddedICCProfile = iCC_Profile;
        this.exif = compoundDirectory;
    }

    protected Node getNativeTree() {
        boolean bl;
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode(JAVAX_IMAGEIO_JPEG_IMAGE_1_0);
        IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("JPEGvariety");
        boolean bl2 = bl = this.jfif != null;
        if (bl) {
            IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("app0JFIF");
            iIOMetadataNode3.setAttribute("majorVersion", Integer.toString(this.jfif.majorVersion));
            iIOMetadataNode3.setAttribute("minorVersion", Integer.toString(this.jfif.minorVersion));
            iIOMetadataNode3.setAttribute("resUnits", Integer.toString(this.jfif.units));
            iIOMetadataNode3.setAttribute("Xdensity", Integer.toString(this.jfif.xDensity));
            iIOMetadataNode3.setAttribute("Ydensity", Integer.toString(this.jfif.yDensity));
            iIOMetadataNode3.setAttribute("thumbWidth", Integer.toString(this.jfif.xThumbnail));
            iIOMetadataNode3.setAttribute("thumbHeight", Integer.toString(this.jfif.yThumbnail));
            iIOMetadataNode2.appendChild(iIOMetadataNode3);
            this.apendJFXX(iIOMetadataNode3);
            this.appendICCProfile(iIOMetadataNode3);
        }
        iIOMetadataNode.appendChild(iIOMetadataNode2);
        this.appendMarkerSequence(iIOMetadataNode, this.segments, bl);
        return iIOMetadataNode;
    }

    private void appendMarkerSequence(IIOMetadataNode iIOMetadataNode, List<Segment> list, boolean bl) {
        IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("markerSequence");
        iIOMetadataNode.appendChild(iIOMetadataNode2);
        block11: for (Segment segment : list) {
            switch (segment.marker) {
                case 65472: 
                case 65473: 
                case 65474: 
                case 65475: 
                case 65477: 
                case 65478: 
                case 65479: 
                case 65481: 
                case 65482: 
                case 65483: 
                case 65485: 
                case 65486: 
                case 65487: {
                    IIOMetadataNode iIOMetadataNode3;
                    Frame frame = (Frame)segment;
                    IIOMetadataNode iIOMetadataNode4 = new IIOMetadataNode("sof");
                    iIOMetadataNode4.setAttribute("process", String.valueOf(frame.marker & 0xF));
                    iIOMetadataNode4.setAttribute("samplePrecision", String.valueOf(frame.samplePrecision));
                    iIOMetadataNode4.setAttribute("numLines", String.valueOf(frame.lines));
                    iIOMetadataNode4.setAttribute("samplesPerLine", String.valueOf(frame.samplesPerLine));
                    iIOMetadataNode4.setAttribute("numFrameComponents", String.valueOf(frame.componentsInFrame()));
                    for (Frame.Component component : frame.components) {
                        iIOMetadataNode3 = new IIOMetadataNode("componentSpec");
                        iIOMetadataNode3.setAttribute("componentId", String.valueOf(component.id));
                        iIOMetadataNode3.setAttribute("HsamplingFactor", String.valueOf(component.hSub));
                        iIOMetadataNode3.setAttribute("VsamplingFactor", String.valueOf(component.vSub));
                        iIOMetadataNode3.setAttribute("QtableSelector", String.valueOf(component.qtSel));
                        iIOMetadataNode4.appendChild(iIOMetadataNode3);
                    }
                    iIOMetadataNode2.appendChild(iIOMetadataNode4);
                    break;
                }
                case 65476: {
                    Frame.Component[] componentArray = (Frame.Component[])segment;
                    IIOMetadataNode iIOMetadataNode5 = new IIOMetadataNode("dht");
                    IIOMetadataNode iIOMetadataNode6 = new IIOMetadataNode("dht");
                    this.appendHuffmanTables((HuffmanTable)componentArray, 0, iIOMetadataNode5);
                    this.appendHuffmanTables((HuffmanTable)componentArray, 1, iIOMetadataNode6);
                    iIOMetadataNode2.appendChild(iIOMetadataNode5);
                    if (iIOMetadataNode5.getLength() + iIOMetadataNode6.getLength() > 4) {
                        iIOMetadataNode2.appendChild(iIOMetadataNode6);
                        break;
                    }
                    while (iIOMetadataNode6.hasChildNodes()) {
                        iIOMetadataNode5.appendChild(iIOMetadataNode6.removeChild(iIOMetadataNode6.getFirstChild()));
                    }
                    continue block11;
                }
                case 65499: {
                    IIOMetadataNode iIOMetadataNode4;
                    QuantizationTable quantizationTable = (QuantizationTable)segment;
                    IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("dqt");
                    for (int i = 0; i < 4; ++i) {
                        if (!quantizationTable.isPresent(i)) continue;
                        iIOMetadataNode4 = new IIOMetadataNode("dqtable");
                        iIOMetadataNode4.setAttribute("elementPrecision", quantizationTable.precision(i) != 16 ? "0" : "1");
                        iIOMetadataNode4.setAttribute("qtableId", Integer.toString(i));
                        iIOMetadataNode4.setUserObject(quantizationTable.toNativeTable(i));
                        iIOMetadataNode3.appendChild(iIOMetadataNode4);
                    }
                    iIOMetadataNode2.appendChild(iIOMetadataNode3);
                    break;
                }
                case 65501: {
                    RestartInterval restartInterval = (RestartInterval)segment;
                    IIOMetadataNode iIOMetadataNode4 = new IIOMetadataNode("dri");
                    iIOMetadataNode4.setAttribute("interval", Integer.toString(restartInterval.interval));
                    iIOMetadataNode2.appendChild(iIOMetadataNode4);
                    break;
                }
                case 65498: {
                    Scan scan = (Scan)segment;
                    IIOMetadataNode iIOMetadataNode5 = new IIOMetadataNode("sos");
                    iIOMetadataNode5.setAttribute("numScanComponents", String.valueOf(scan.components.length));
                    iIOMetadataNode5.setAttribute("startSpectralSelection", String.valueOf(scan.spectralSelStart));
                    iIOMetadataNode5.setAttribute("endSpectralSelection", String.valueOf(scan.spectralSelEnd));
                    iIOMetadataNode5.setAttribute("approxHigh", String.valueOf(scan.approxHigh));
                    iIOMetadataNode5.setAttribute("approxLow", String.valueOf(scan.approxLow));
                    for (Scan.Component component : scan.components) {
                        IIOMetadataNode iIOMetadataNode6 = new IIOMetadataNode("scanComponentSpec");
                        iIOMetadataNode6.setAttribute("componentSelector", String.valueOf(component.scanCompSel));
                        iIOMetadataNode6.setAttribute("dcHuffTable", String.valueOf(component.dcTabSel));
                        iIOMetadataNode6.setAttribute("acHuffTable", String.valueOf(component.acTabSel));
                        iIOMetadataNode5.appendChild(iIOMetadataNode6);
                    }
                    iIOMetadataNode2.appendChild(iIOMetadataNode5);
                    break;
                }
                case 65534: {
                    Scan.Component[] componentArray = new IIOMetadataNode("com");
                    componentArray.setAttribute("comment", ((Comment)segment).comment);
                    iIOMetadataNode2.appendChild((Node)componentArray);
                    break;
                }
                case 65504: {
                    if (segment instanceof JFIF || bl && segment instanceof JFXX) break;
                }
                case 65506: {
                    if (bl && segment instanceof ICCProfile) break;
                }
                case 65518: {
                    if (segment instanceof AdobeDCT) {
                        AdobeDCT adobeDCT = (AdobeDCT)segment;
                        IIOMetadataNode iIOMetadataNode7 = new IIOMetadataNode("app14Adobe");
                        iIOMetadataNode7.setAttribute("version", String.valueOf(adobeDCT.version));
                        iIOMetadataNode7.setAttribute("flags0", String.valueOf(adobeDCT.flags0));
                        iIOMetadataNode7.setAttribute("flags1", String.valueOf(adobeDCT.flags1));
                        iIOMetadataNode7.setAttribute("transform", String.valueOf(adobeDCT.transform));
                        iIOMetadataNode2.appendChild(iIOMetadataNode7);
                        break;
                    }
                }
                default: {
                    IIOMetadataNode iIOMetadataNode8 = new IIOMetadataNode("unknown");
                    iIOMetadataNode8.setAttribute("MarkerTag", String.valueOf(segment.marker & 0xFF));
                    iIOMetadataNode8.setUserObject(((Application)segment).data);
                    iIOMetadataNode2.appendChild(iIOMetadataNode8);
                }
            }
        }
    }

    private void appendHuffmanTables(HuffmanTable huffmanTable, int n, IIOMetadataNode iIOMetadataNode) {
        for (int i = 0; i < 4; ++i) {
            if (!huffmanTable.isPresent(i, n)) continue;
            IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("dhtable");
            iIOMetadataNode2.setAttribute("class", String.valueOf(n));
            iIOMetadataNode2.setAttribute("htableId", String.valueOf(i));
            iIOMetadataNode2.setUserObject(huffmanTable.toNativeTable(i, n));
            iIOMetadataNode.appendChild(iIOMetadataNode2);
        }
    }

    private void appendICCProfile(IIOMetadataNode iIOMetadataNode) {
        if (this.embeddedICCProfile != null) {
            IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("app2ICC");
            iIOMetadataNode2.setUserObject(this.embeddedICCProfile);
            iIOMetadataNode.appendChild(iIOMetadataNode2);
        }
    }

    private void apendJFXX(IIOMetadataNode iIOMetadataNode) {
        if (this.jfxx != null) {
            IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("JFXX");
            iIOMetadataNode.appendChild(iIOMetadataNode2);
            IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("app0JFXX");
            iIOMetadataNode3.setAttribute("extensionCode", Integer.toString(this.jfxx.extensionCode));
            iIOMetadataNode2.appendChild(iIOMetadataNode3);
            switch (this.jfxx.extensionCode) {
                case 16: {
                    IIOMetadataNode iIOMetadataNode4 = new IIOMetadataNode("JFIFthumbJPEG");
                    iIOMetadataNode4.appendChild(new IIOMetadataNode("markerSequence"));
                    iIOMetadataNode3.appendChild(iIOMetadataNode4);
                    break;
                }
                case 17: {
                    IIOMetadataNode iIOMetadataNode5 = new IIOMetadataNode("JFIFthumbPalette");
                    iIOMetadataNode5.setAttribute("thumbWidth", Integer.toString(this.jfxx.thumbnail[0] & 0xFF));
                    iIOMetadataNode5.setAttribute("thumbHeight", Integer.toString(this.jfxx.thumbnail[1] & 0xFF));
                    iIOMetadataNode3.appendChild(iIOMetadataNode5);
                    break;
                }
                case 19: {
                    IIOMetadataNode iIOMetadataNode6 = new IIOMetadataNode("JFIFthumbRGB");
                    iIOMetadataNode6.setAttribute("thumbWidth", Integer.toString(this.jfxx.thumbnail[0] & 0xFF));
                    iIOMetadataNode6.setAttribute("thumbHeight", Integer.toString(this.jfxx.thumbnail[1] & 0xFF));
                    iIOMetadataNode3.appendChild(iIOMetadataNode6);
                }
            }
        }
    }

    protected IIOMetadataNode getStandardChromaNode() {
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("Chroma");
        IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("ColorSpaceType");
        iIOMetadataNode2.setAttribute("name", this.getColorSpaceType());
        iIOMetadataNode.appendChild(iIOMetadataNode2);
        IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("NumChannels");
        iIOMetadataNode3.setAttribute("value", String.valueOf(this.frame.componentsInFrame()));
        iIOMetadataNode.appendChild(iIOMetadataNode3);
        return iIOMetadataNode;
    }

    private String getColorSpaceType() {
        try {
            JPEGColorSpace jPEGColorSpace = JPEGImageReader.getSourceCSType(this.jfif, this.adobeDCT, this.frame);
            switch (jPEGColorSpace) {
                case Gray: 
                case GrayA: {
                    return "GRAY";
                }
                case YCbCr: 
                case YCbCrA: {
                    return "YCbCr";
                }
                case RGB: 
                case RGBA: {
                    return "RGB";
                }
                case PhotoYCC: 
                case PhotoYCCA: {
                    return "PhotoYCC";
                }
                case YCCK: {
                    return "YCCK";
                }
                case CMYK: {
                    return "CMYK";
                }
            }
        }
        catch (IIOException iIOException) {
            // empty catch block
        }
        return Integer.toString(this.frame.componentsInFrame(), 16) + "CLR";
    }

    private boolean hasAlpha() {
        try {
            JPEGColorSpace jPEGColorSpace = JPEGImageReader.getSourceCSType(this.jfif, this.adobeDCT, this.frame);
            switch (jPEGColorSpace) {
                case GrayA: 
                case YCbCrA: 
                case RGBA: 
                case PhotoYCCA: {
                    return true;
                }
            }
        }
        catch (IIOException iIOException) {
            // empty catch block
        }
        return false;
    }

    private boolean isLossess() {
        switch (this.frame.marker) {
            case 65475: 
            case 65479: 
            case 65483: 
            case 65487: {
                return true;
            }
        }
        return false;
    }

    protected IIOMetadataNode getStandardTransparencyNode() {
        if (this.hasAlpha()) {
            IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("Transparency");
            IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("Alpha");
            iIOMetadataNode2.setAttribute("value", "nonpremultipled");
            iIOMetadataNode.appendChild(iIOMetadataNode2);
            return iIOMetadataNode;
        }
        return null;
    }

    protected IIOMetadataNode getStandardCompressionNode() {
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("Compression");
        IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("CompressionTypeName");
        iIOMetadataNode2.setAttribute("value", "JPEG");
        iIOMetadataNode.appendChild(iIOMetadataNode2);
        IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("Lossless");
        iIOMetadataNode3.setAttribute("value", this.isLossess() ? "TRUE" : "FALSE");
        iIOMetadataNode.appendChild(iIOMetadataNode3);
        IIOMetadataNode iIOMetadataNode4 = new IIOMetadataNode("NumProgressiveScans");
        iIOMetadataNode4.setAttribute("value", "1");
        iIOMetadataNode.appendChild(iIOMetadataNode4);
        return iIOMetadataNode;
    }

    protected IIOMetadataNode getStandardDimensionNode() {
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("Dimension");
        IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("ImageOrientation");
        iIOMetadataNode2.setAttribute("value", this.getExifOrientation((Directory)this.exif));
        iIOMetadataNode.appendChild(iIOMetadataNode2);
        if (this.jfif != null) {
            float f = Math.max(1, this.jfif.xDensity);
            float f2 = Math.max(1, this.jfif.yDensity);
            float f3 = this.jfif.units == 0 ? f / f2 : f2 / f;
            IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("PixelAspectRatio");
            iIOMetadataNode3.setAttribute("value", Float.toString(f3));
            iIOMetadataNode.insertBefore(iIOMetadataNode3, iIOMetadataNode2);
            if (this.jfif.units != 0) {
                float f4 = this.jfif.units == 1 ? 25.4f : 10.0f;
                IIOMetadataNode iIOMetadataNode4 = new IIOMetadataNode("HorizontalPixelSize");
                iIOMetadataNode4.setAttribute("value", Float.toString(f4 / f));
                iIOMetadataNode.appendChild(iIOMetadataNode4);
                IIOMetadataNode iIOMetadataNode5 = new IIOMetadataNode("VerticalPixelSize");
                iIOMetadataNode5.setAttribute("value", Float.toString(f4 / f2));
                iIOMetadataNode.appendChild(iIOMetadataNode5);
            }
        }
        return iIOMetadataNode;
    }

    private String getExifOrientation(Directory directory) {
        Entry entry;
        if (directory != null && (entry = directory.getEntryById((Object)274)) != null) {
            switch (((Number)entry.getValue()).intValue()) {
                case 2: {
                    return "FlipH";
                }
                case 3: {
                    return "Rotate180";
                }
                case 4: {
                    return "FlipV";
                }
                case 5: {
                    return "FlipVRotate90";
                }
                case 6: {
                    return "Rotate270";
                }
                case 7: {
                    return "FlipHRotate90";
                }
                case 8: {
                    return "Rotate90";
                }
            }
        }
        return "Normal";
    }

    protected IIOMetadataNode getStandardTextNode() {
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("Text");
        for (Segment segment : this.segments) {
            if (!(segment instanceof Comment)) continue;
            IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("TextEntry");
            iIOMetadataNode2.setAttribute("keyword", "comment");
            iIOMetadataNode2.setAttribute("value", ((Comment)segment).comment);
            iIOMetadataNode.appendChild(iIOMetadataNode2);
        }
        return iIOMetadataNode.hasChildNodes() ? iIOMetadataNode : null;
    }
}

