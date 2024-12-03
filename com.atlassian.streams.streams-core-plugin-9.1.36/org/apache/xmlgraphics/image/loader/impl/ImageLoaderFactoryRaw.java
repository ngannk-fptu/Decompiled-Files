/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader.impl;

import java.util.Map;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.impl.AbstractImageLoaderFactory;
import org.apache.xmlgraphics.image.loader.impl.ImageLoaderRaw;
import org.apache.xmlgraphics.image.loader.impl.ImageLoaderRawJPEG;
import org.apache.xmlgraphics.image.loader.impl.ImageLoaderRawPNG;
import org.apache.xmlgraphics.image.loader.spi.ImageLoader;
import org.apache.xmlgraphics.image.loader.util.Penalty;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ImageLoaderFactoryRaw
extends AbstractImageLoaderFactory {
    public static final String MIME_EMF = "image/x-emf";
    private static final String[] MIMES = new String[]{"image/png", "image/jpeg", "image/tiff", "image/x-emf"};
    private static final ImageFlavor[][] FLAVORS = new ImageFlavor[][]{{ImageFlavor.RAW_PNG}, {ImageFlavor.RAW_JPEG}, {ImageFlavor.RAW_TIFF}, {ImageFlavor.RAW_EMF}};

    public static String getMimeForRawFlavor(ImageFlavor flavor) {
        int ci = FLAVORS.length;
        for (int i = 0; i < ci; ++i) {
            int cj = FLAVORS[i].length;
            for (int j = 0; j < cj; ++j) {
                if (!FLAVORS[i][j].equals(flavor)) continue;
                return MIMES[i];
            }
        }
        throw new IllegalArgumentException("ImageFlavor is not a \"raw\" flavor: " + flavor);
    }

    @Override
    public String[] getSupportedMIMETypes() {
        return MIMES;
    }

    @Override
    public ImageFlavor[] getSupportedFlavors(String mime) {
        int c = MIMES.length;
        for (int i = 0; i < c; ++i) {
            if (!MIMES[i].equals(mime)) continue;
            return FLAVORS[i];
        }
        throw new IllegalArgumentException("Unsupported MIME type: " + mime);
    }

    @Override
    public ImageLoader newImageLoader(ImageFlavor targetFlavor) {
        if (targetFlavor.equals(ImageFlavor.RAW_JPEG)) {
            return new ImageLoaderRawJPEG();
        }
        if (targetFlavor.equals(ImageFlavor.RAW_PNG)) {
            return new ImageLoaderRawPNG();
        }
        return new ImageLoaderRaw(targetFlavor);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public boolean isSupported(ImageInfo imageInfo) {
        if ("image/png".equals(imageInfo.getMimeType())) {
            IIOMetadata metadata;
            Map additionalPenalties = (Map)imageInfo.getCustomObjects().get("additionalPenalties");
            int penalty = 0;
            Penalty penaltyObj = (Penalty)additionalPenalties.get(ImageLoaderRawPNG.class.getName());
            if (penaltyObj != null) {
                penalty = penaltyObj.getValue();
            }
            if ((metadata = (IIOMetadata)imageInfo.getCustomObjects().get(IIOMetadata.class)) != null) {
                IIOMetadataNode children = (IIOMetadataNode)metadata.getAsTree("javax_imageio_png_1.0").getChildNodes();
                NamedNodeMap attr = children.getElementsByTagName("IHDR").item(0).getAttributes();
                String bitDepth = attr.getNamedItem("bitDepth").getNodeValue();
                String interlaceMethod = attr.getNamedItem("interlaceMethod").getNodeValue();
                String colorType = attr.getNamedItem("colorType").getNodeValue();
                if (!bitDepth.equals("8") || !interlaceMethod.equals("none") || (colorType.equals("RGBAlpha") || colorType.equals("GrayAlpha")) && penalty >= 0) {
                    return false;
                }
                children = (IIOMetadataNode)metadata.getAsTree("javax_imageio_1.0").getChildNodes();
                Node numChannels = children.getElementsByTagName("NumChannels").item(0);
                String numChannelsStr = numChannels.getAttributes().getNamedItem("value").getNodeValue();
                if ("4".equals(numChannelsStr) && "Palette".equals(colorType) && penalty >= 0) {
                    return false;
                }
            }
        }
        return true;
    }
}

