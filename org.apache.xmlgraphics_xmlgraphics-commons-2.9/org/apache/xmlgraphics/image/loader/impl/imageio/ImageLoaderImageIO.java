/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.xmlgraphics.image.loader.impl.imageio;

import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.spi.IIOServiceProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlgraphics.image.loader.Image;
import org.apache.xmlgraphics.image.loader.ImageException;
import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.ImageSessionContext;
import org.apache.xmlgraphics.image.loader.impl.AbstractImageLoader;
import org.apache.xmlgraphics.image.loader.impl.imageio.ImageIOUtil;
import org.apache.xmlgraphics.java2d.color.profile.ColorProfileUtil;
import org.w3c.dom.Element;

public class ImageLoaderImageIO
extends AbstractImageLoader {
    protected static final Log log = LogFactory.getLog(ImageLoaderImageIO.class);
    private ImageFlavor targetFlavor;
    private static final String PNG_METADATA_NODE = "javax_imageio_png_1.0";
    private static final String JPEG_METADATA_NODE = "javax_imageio_jpeg_image_1.0";
    private static final Set PROVIDERS_IGNORING_ICC = new HashSet();

    public ImageLoaderImageIO(ImageFlavor targetFlavor) {
        if (!ImageFlavor.BUFFERED_IMAGE.equals(targetFlavor) && !ImageFlavor.RENDERED_IMAGE.equals(targetFlavor)) {
            throw new IllegalArgumentException("Unsupported target ImageFlavor: " + targetFlavor);
        }
        this.targetFlavor = targetFlavor;
    }

    @Override
    public ImageFlavor getTargetFlavor() {
        return this.targetFlavor;
    }

    /*
     * Exception decompiling
     */
    @Override
    public Image loadImage(ImageInfo info, Map hints, ImageSessionContext session) throws ImageException, IOException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [7[CATCHBLOCK]], but top level block is 3[TRYBLOCK]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    private ImageReadParam getParam(ImageReader reader, Map hints) throws IOException {
        if (hints != null && Boolean.TRUE.equals(hints.get("CMYK"))) {
            Iterator<ImageTypeSpecifier> types = reader.getImageTypes(0);
            while (types.hasNext()) {
                ImageTypeSpecifier type = types.next();
                if (type.getNumComponents() != 4) continue;
                ImageReadParam param = new ImageReadParam();
                param.setDestinationType(type);
                return param;
            }
        }
        return reader.getDefaultReadParam();
    }

    private boolean checkProviderIgnoresICC(IIOServiceProvider provider) {
        StringBuffer b = new StringBuffer(provider.getDescription(Locale.ENGLISH));
        b.append('/').append(provider.getVendorName());
        b.append('/').append(provider.getVersion());
        if (log.isDebugEnabled()) {
            log.debug((Object)("Image Provider: " + b.toString()));
        }
        return PROVIDERS_IGNORING_ICC.contains(b.toString());
    }

    private ICC_Profile tryToExctractICCProfile(IIOMetadata iiometa) {
        String[] supportedFormats;
        ICC_Profile iccProf = null;
        for (String format : supportedFormats = iiometa.getMetadataFormatNames()) {
            Element root = (Element)iiometa.getAsTree(format);
            if (PNG_METADATA_NODE.equals(format)) {
                iccProf = this.tryToExctractICCProfileFromPNGMetadataNode(root);
                continue;
            }
            if (!JPEG_METADATA_NODE.equals(format)) continue;
            iccProf = this.tryToExctractICCProfileFromJPEGMetadataNode(root);
        }
        return iccProf;
    }

    private ICC_Profile tryToExctractICCProfileFromPNGMetadataNode(Element pngNode) {
        ICC_Profile iccProf = null;
        Element iccpNode = ImageIOUtil.getChild(pngNode, "iCCP");
        if (iccpNode instanceof IIOMetadataNode) {
            IIOMetadataNode imn = (IIOMetadataNode)iccpNode;
            byte[] prof = (byte[])imn.getUserObject();
            String comp = imn.getAttribute("compressionMethod");
            if ("deflate".equalsIgnoreCase(comp)) {
                Inflater decompresser = new Inflater();
                decompresser.setInput(prof);
                byte[] result = new byte[100];
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                boolean failed = false;
                while (!decompresser.finished() && !failed) {
                    try {
                        int resultLength = decompresser.inflate(result);
                        bos.write(result, 0, resultLength);
                        if (resultLength != 0) continue;
                        log.debug((Object)"Failed to deflate ICC Profile");
                        failed = true;
                    }
                    catch (DataFormatException e) {
                        log.debug((Object)"Failed to deflate ICC Profile", (Throwable)e);
                        failed = true;
                    }
                }
                decompresser.end();
                try {
                    iccProf = ColorProfileUtil.getICC_Profile(bos.toByteArray());
                }
                catch (IllegalArgumentException e) {
                    log.debug((Object)"Failed to interpret embedded ICC Profile", (Throwable)e);
                    iccProf = null;
                }
            }
        }
        return iccProf;
    }

    private ICC_Profile tryToExctractICCProfileFromJPEGMetadataNode(Element jpgNode) {
        Element app2iccNode;
        ICC_Profile iccProf = null;
        Element jfifNode = ImageIOUtil.getChild(jpgNode, "app0JFIF");
        if (jfifNode != null && (app2iccNode = ImageIOUtil.getChild(jfifNode, "app2ICC")) instanceof IIOMetadataNode) {
            IIOMetadataNode imn = (IIOMetadataNode)app2iccNode;
            iccProf = (ICC_Profile)imn.getUserObject();
        }
        return iccProf;
    }

    private BufferedImage getFallbackBufferedImage(ImageReader reader, int pageIndex, ImageReadParam param) throws IOException {
        int imageType;
        Raster raster = reader.readRaster(pageIndex, param);
        int numBands = raster.getNumBands();
        switch (numBands) {
            case 1: {
                imageType = 10;
                break;
            }
            case 3: {
                imageType = 5;
                break;
            }
            case 4: {
                imageType = 6;
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unsupported band count: " + numBands);
            }
        }
        BufferedImage bi = new BufferedImage(raster.getWidth(), raster.getHeight(), imageType);
        bi.getRaster().setRect(raster);
        return bi;
    }

    static {
        PROVIDERS_IGNORING_ICC.add("Standard PNG image reader/Sun Microsystems, Inc./1.0");
        PROVIDERS_IGNORING_ICC.add("Standard PNG image reader/Oracle Corporation/1.0");
        PROVIDERS_IGNORING_ICC.add("Standard JPEG Image Reader/Sun Microsystems, Inc./0.5");
        PROVIDERS_IGNORING_ICC.add("Standard JPEG Image Reader/Oracle Corporation/0.5");
    }
}

