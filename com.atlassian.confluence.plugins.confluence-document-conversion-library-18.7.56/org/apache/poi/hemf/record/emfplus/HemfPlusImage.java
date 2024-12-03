/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.hemf.record.emfplus;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.poi.hemf.draw.HemfDrawProperties;
import org.apache.poi.hemf.draw.HemfGraphics;
import org.apache.poi.hemf.record.emfplus.HemfPlusDraw;
import org.apache.poi.hemf.record.emfplus.HemfPlusGDIImageRenderer;
import org.apache.poi.hemf.record.emfplus.HemfPlusHeader;
import org.apache.poi.hemf.record.emfplus.HemfPlusObject;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.sl.draw.ImageRenderer;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndianInputStream;

public class HemfPlusImage {

    public static class EmfPlusImageAttributes
    implements HemfPlusObject.EmfPlusObjectData {
        private final HemfPlusHeader.EmfPlusGraphicsVersion graphicsVersion = new HemfPlusHeader.EmfPlusGraphicsVersion();
        private EmfPlusWrapMode wrapMode;
        private Color clampColor;
        private EmfPlusObjectClamp objectClamp;

        @Override
        public long init(LittleEndianInputStream leis, long dataSize, HemfPlusObject.EmfPlusObjectType objectType, int flags) throws IOException {
            long size = this.graphicsVersion.init(leis);
            leis.skipFully(4);
            this.wrapMode = EmfPlusWrapMode.valueOf(leis.readInt());
            this.clampColor = HemfPlusDraw.readARGB(leis.readInt());
            this.objectClamp = EmfPlusObjectClamp.valueOf(leis.readInt());
            leis.skipFully(4);
            return size + 20L;
        }

        @Override
        public HemfPlusHeader.EmfPlusGraphicsVersion getGraphicsVersion() {
            return this.graphicsVersion;
        }

        public EmfPlusWrapMode getWrapMode() {
            return this.wrapMode;
        }

        public Color getClampColor() {
            return this.clampColor;
        }

        public EmfPlusObjectClamp getObjectClamp() {
            return this.objectClamp;
        }

        @Override
        public void applyObject(HemfGraphics ctx, List<? extends HemfPlusObject.EmfPlusObjectData> continuedObjectData) {
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("graphicsVersion", this::getGraphicsVersion, "wrapMode", this::getWrapMode, "clampColor", this::getClampColor, "objectClamp", this::getObjectClamp);
        }

        public HemfPlusObject.EmfPlusObjectType getGenericRecordType() {
            return HemfPlusObject.EmfPlusObjectType.IMAGE_ATTRIBUTES;
        }
    }

    public static class EmfPlusImage
    implements HemfPlusObject.EmfPlusObjectData {
        private static final int MAX_OBJECT_SIZE = 50000000;
        private static final String GDI_CONTENT = "GDI";
        private final HemfPlusHeader.EmfPlusGraphicsVersion graphicsVersion = new HemfPlusHeader.EmfPlusGraphicsVersion();
        private EmfPlusImageDataType imageDataType;
        private int bitmapWidth;
        private int bitmapHeight;
        private int bitmapStride;
        private EmfPlusPixelFormat pixelFormat;
        private EmfPlusBitmapDataType bitmapType;
        private byte[] imageData;
        private EmfPlusMetafileDataType metafileType;
        private int metafileDataSize;

        public EmfPlusImageDataType getImageDataType() {
            return this.imageDataType;
        }

        public byte[] getImageData() {
            return this.imageData;
        }

        public EmfPlusPixelFormat getPixelFormat() {
            return this.pixelFormat;
        }

        public EmfPlusBitmapDataType getBitmapType() {
            return this.bitmapType;
        }

        public int getBitmapWidth() {
            return this.bitmapWidth;
        }

        public int getBitmapHeight() {
            return this.bitmapHeight;
        }

        public int getBitmapStride() {
            return this.bitmapStride;
        }

        public EmfPlusMetafileDataType getMetafileType() {
            return this.metafileType;
        }

        @Override
        public long init(LittleEndianInputStream leis, long dataSize, HemfPlusObject.EmfPlusObjectType objectType, int flags) throws IOException {
            int fileSize;
            leis.mark(4);
            long size = this.graphicsVersion.init(leis);
            if (this.isContinuedRecord()) {
                this.imageDataType = EmfPlusImageDataType.CONTINUED;
                leis.reset();
                size = 0L;
            } else {
                this.imageDataType = EmfPlusImageDataType.valueOf(leis.readInt());
                size += 4L;
            }
            if (this.imageDataType == null) {
                this.imageDataType = EmfPlusImageDataType.UNKNOWN;
            }
            switch (this.imageDataType) {
                default: {
                    this.bitmapWidth = -1;
                    this.bitmapHeight = -1;
                    this.bitmapStride = -1;
                    this.bitmapType = null;
                    this.pixelFormat = null;
                    fileSize = (int)dataSize;
                    break;
                }
                case BITMAP: {
                    this.bitmapWidth = leis.readInt();
                    this.bitmapHeight = leis.readInt();
                    this.bitmapStride = leis.readInt();
                    int pixelFormatInt = leis.readInt();
                    this.bitmapType = EmfPlusBitmapDataType.valueOf(leis.readInt());
                    size += 20L;
                    EmfPlusPixelFormat emfPlusPixelFormat = this.pixelFormat = this.bitmapType == EmfPlusBitmapDataType.PIXEL ? EmfPlusPixelFormat.valueOf(pixelFormatInt) : EmfPlusPixelFormat.UNDEFINED;
                    assert (this.pixelFormat != null);
                    fileSize = (int)(dataSize - size);
                    break;
                }
                case METAFILE: {
                    this.metafileType = EmfPlusMetafileDataType.valueOf(leis.readInt());
                    this.metafileDataSize = leis.readInt();
                    fileSize = (int)(dataSize - (size += 8L));
                }
            }
            assert ((long)fileSize <= dataSize - size);
            this.imageData = IOUtils.toByteArray(leis, fileSize, 50000000);
            return size + (long)fileSize;
        }

        @Override
        public HemfPlusHeader.EmfPlusGraphicsVersion getGraphicsVersion() {
            return this.graphicsVersion;
        }

        /*
         * Exception decompiling
         */
        public Rectangle2D getBounds(List<? extends HemfPlusObject.EmfPlusObjectData> continuedObjectData) {
            /*
             * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
             * 
             * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [24[CATCHBLOCK], 16[CASE], 0[TRYBLOCK]], but top level block is 3[TRYBLOCK]
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
             *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
             *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseInnerClassesPass1(ClassFile.java:923)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1035)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
             *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
             *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
             *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
             *     at org.benf.cfr.reader.Main.main(Main.java:54)
             */
            throw new IllegalStateException("Decompilation failed");
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        public byte[] getRawData(List<? extends HemfPlusObject.EmfPlusObjectData> continuedObjectData) {
            try (UnsynchronizedByteArrayOutputStream bos = new UnsynchronizedByteArrayOutputStream();){
                bos.write(this.getImageData());
                if (continuedObjectData != null) {
                    for (HemfPlusObject.EmfPlusObjectData emfPlusObjectData : continuedObjectData) {
                        bos.write(((EmfPlusImage)emfPlusObjectData).getImageData());
                    }
                }
                Object object = bos.toByteArray();
                return object;
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void applyObject(HemfGraphics ctx, List<? extends HemfPlusObject.EmfPlusObjectData> continuedObjectData) {
            HemfDrawProperties prop = ctx.getProperties();
            byte[] data = this.getRawData(continuedObjectData);
            String contentType = this.getContentType(data);
            ImageRenderer imgr = GDI_CONTENT.equals(contentType) ? this.getGDIRenderer() : ctx.getImageRenderer(contentType);
            try {
                imgr.loadImage(data, contentType);
            }
            catch (IOException ignored) {
                imgr = null;
            }
            prop.setEmfPlusImage(imgr);
        }

        public BufferedImage readGDIImage(byte[] data) {
            return this.getGDIRenderer().readGDIImage(data);
        }

        private HemfPlusGDIImageRenderer getGDIRenderer() {
            if (this.getImageDataType() != EmfPlusImageDataType.BITMAP || this.getBitmapType() != EmfPlusBitmapDataType.PIXEL) {
                throw new RuntimeException("image data is not a GDI image");
            }
            HemfPlusGDIImageRenderer renderer = new HemfPlusGDIImageRenderer();
            renderer.setWidth(this.getBitmapWidth());
            renderer.setHeight(this.getBitmapHeight());
            renderer.setStride(this.getBitmapStride());
            renderer.setPixelFormat(this.getPixelFormat());
            return renderer;
        }

        private String getContentType(byte[] data) {
            PictureData.PictureType pictureType = PictureData.PictureType.UNKNOWN;
            block0 : switch (this.getImageDataType()) {
                case BITMAP: {
                    if (this.getBitmapType() == EmfPlusBitmapDataType.PIXEL) {
                        return GDI_CONTENT;
                    }
                    switch (FileMagic.valueOf(data)) {
                        case GIF: {
                            pictureType = PictureData.PictureType.GIF;
                            break;
                        }
                        case TIFF: {
                            pictureType = PictureData.PictureType.TIFF;
                            break;
                        }
                        case PNG: {
                            pictureType = PictureData.PictureType.PNG;
                            break;
                        }
                        case JPEG: {
                            pictureType = PictureData.PictureType.JPEG;
                            break;
                        }
                        case BMP: {
                            pictureType = PictureData.PictureType.BMP;
                        }
                    }
                    break;
                }
                case METAFILE: {
                    assert (this.getMetafileType() != null);
                    switch (this.getMetafileType()) {
                        case Wmf: 
                        case WmfPlaceable: {
                            pictureType = PictureData.PictureType.WMF;
                            break block0;
                        }
                        case Emf: 
                        case EmfPlusDual: 
                        case EmfPlusOnly: {
                            pictureType = PictureData.PictureType.EMF;
                        }
                    }
                }
            }
            return pictureType.contentType;
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public HemfPlusObject.EmfPlusObjectType getGenericRecordType() {
            return HemfPlusObject.EmfPlusObjectType.IMAGE;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            LinkedHashMap<String, Supplier<Object>> m = new LinkedHashMap<String, Supplier<Object>>();
            m.put("graphicsVersion", this::getGraphicsVersion);
            m.put("imageDataType", this::getImageDataType);
            m.put("bitmapWidth", this::getBitmapWidth);
            m.put("bitmapHeight", this::getBitmapHeight);
            m.put("bitmapStride", this::getBitmapStride);
            m.put("pixelFormat", this::getPixelFormat);
            m.put("bitmapType", this::getBitmapType);
            m.put("imageData", this::getImageData);
            m.put("metafileType", this::getMetafileType);
            m.put("metafileDataSize", () -> this.metafileDataSize);
            return Collections.unmodifiableMap(m);
        }
    }

    public static enum EmfPlusObjectClamp {
        RectClamp(0),
        BitmapClamp(1);

        public final int id;

        private EmfPlusObjectClamp(int id) {
            this.id = id;
        }

        public static EmfPlusObjectClamp valueOf(int id) {
            for (EmfPlusObjectClamp wrt : EmfPlusObjectClamp.values()) {
                if (wrt.id != id) continue;
                return wrt;
            }
            return null;
        }
    }

    public static enum EmfPlusWrapMode {
        WRAP_MODE_TILE(0),
        WRAP_MODE_TILE_FLIP_X(1),
        WRAP_MODE_TILE_FLIP_Y(2),
        WRAP_MODE_TILE_FLIP_XY(3),
        WRAP_MODE_CLAMP(4);

        public final int id;

        private EmfPlusWrapMode(int id) {
            this.id = id;
        }

        public static EmfPlusWrapMode valueOf(int id) {
            for (EmfPlusWrapMode wrt : EmfPlusWrapMode.values()) {
                if (wrt.id != id) continue;
                return wrt;
            }
            return null;
        }
    }

    public static enum EmfPlusMetafileDataType {
        Wmf(1),
        WmfPlaceable(2),
        Emf(3),
        EmfPlusOnly(4),
        EmfPlusDual(5);

        public final int id;

        private EmfPlusMetafileDataType(int id) {
            this.id = id;
        }

        public static EmfPlusMetafileDataType valueOf(int id) {
            for (EmfPlusMetafileDataType wrt : EmfPlusMetafileDataType.values()) {
                if (wrt.id != id) continue;
                return wrt;
            }
            return null;
        }
    }

    public static enum EmfPlusBitmapDataType {
        PIXEL(0),
        COMPRESSED(1);

        public final int id;

        private EmfPlusBitmapDataType(int id) {
            this.id = id;
        }

        public static EmfPlusBitmapDataType valueOf(int id) {
            for (EmfPlusBitmapDataType wrt : EmfPlusBitmapDataType.values()) {
                if (wrt.id != id) continue;
                return wrt;
            }
            return null;
        }
    }

    public static enum EmfPlusPixelFormat {
        UNDEFINED(0),
        INDEXED_1BPP(196865),
        INDEXED_4BPP(197634),
        INDEXED_8BPP(198659),
        GRAYSCALE_16BPP(0x101004),
        RGB555_16BPP(135173),
        RGB565_16BPP(135174),
        ARGB1555_16BPP(397319),
        RGB_24BPP(137224),
        RGB_32BPP(139273),
        ARGB_32BPP(2498570),
        PARGB_32BPP(925707),
        RGB_48BPP(1060876),
        ARGB_64BPP(3424269),
        PARGB_64BPP(1720334);

        private static final BitField CANONICAL;
        private static final BitField EXTCOLORS;
        private static final BitField PREMULTI;
        private static final BitField ALPHA;
        private static final BitField GDI;
        private static final BitField PALETTE;
        private static final BitField BPP;
        private static final BitField INDEX;
        public final int id;

        private EmfPlusPixelFormat(int id) {
            this.id = id;
        }

        public static EmfPlusPixelFormat valueOf(int id) {
            for (EmfPlusPixelFormat wrt : EmfPlusPixelFormat.values()) {
                if (wrt.id != id) continue;
                return wrt;
            }
            return null;
        }

        public int getGDIEnumIndex() {
            return this.id == -1 ? -1 : INDEX.getValue(this.id);
        }

        public int getBitsPerPixel() {
            return this.id == -1 ? -1 : BPP.getValue(this.id);
        }

        public boolean isPaletteIndexed() {
            return this.id != -1 && PALETTE.isSet(this.id);
        }

        public boolean isGDISupported() {
            return this.id != -1 && GDI.isSet(this.id);
        }

        public boolean isAlpha() {
            return this.id != -1 && ALPHA.isSet(this.id);
        }

        public boolean isPreMultiplied() {
            return this.id != -1 && PREMULTI.isSet(this.id);
        }

        public boolean isExtendedColors() {
            return this.id != -1 && EXTCOLORS.isSet(this.id);
        }

        public boolean isCanonical() {
            return this.id != -1 && CANONICAL.isSet(this.id);
        }

        static {
            CANONICAL = BitFieldFactory.getInstance(0x200000);
            EXTCOLORS = BitFieldFactory.getInstance(0x100000);
            PREMULTI = BitFieldFactory.getInstance(524288);
            ALPHA = BitFieldFactory.getInstance(262144);
            GDI = BitFieldFactory.getInstance(131072);
            PALETTE = BitFieldFactory.getInstance(65536);
            BPP = BitFieldFactory.getInstance(65280);
            INDEX = BitFieldFactory.getInstance(255);
        }
    }

    public static enum EmfPlusImageDataType {
        UNKNOWN(0),
        BITMAP(1),
        METAFILE(2),
        CONTINUED(-1);

        public final int id;

        private EmfPlusImageDataType(int id) {
            this.id = id;
        }

        public static EmfPlusImageDataType valueOf(int id) {
            for (EmfPlusImageDataType wrt : EmfPlusImageDataType.values()) {
                if (wrt.id != id) continue;
                return wrt;
            }
            return null;
        }
    }
}

