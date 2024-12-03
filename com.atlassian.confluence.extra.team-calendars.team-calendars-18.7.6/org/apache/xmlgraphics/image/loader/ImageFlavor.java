/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader;

import org.apache.xmlgraphics.image.loader.MimeEnabledImageFlavor;
import org.apache.xmlgraphics.image.loader.SimpleRefinedImageFlavor;

public class ImageFlavor {
    public static final ImageFlavor RENDERED_IMAGE = new ImageFlavor("RenderedImage");
    public static final ImageFlavor BUFFERED_IMAGE = new SimpleRefinedImageFlavor(RENDERED_IMAGE, "BufferedImage");
    private static final ImageFlavor DOM = new ImageFlavor("DOM");
    public static final ImageFlavor XML_DOM = new MimeEnabledImageFlavor(DOM, "text/xml");
    public static final ImageFlavor RAW = new ImageFlavor("Raw");
    public static final ImageFlavor RAW_PNG = new MimeEnabledImageFlavor(RAW, "image/png");
    public static final ImageFlavor RAW_JPEG = new MimeEnabledImageFlavor(RAW, "image/jpeg");
    public static final ImageFlavor RAW_TIFF = new MimeEnabledImageFlavor(RAW, "image/tiff");
    public static final ImageFlavor RAW_EMF = new MimeEnabledImageFlavor(RAW, "image/x-emf");
    public static final ImageFlavor RAW_EPS = new MimeEnabledImageFlavor(RAW, "application/postscript");
    public static final ImageFlavor RAW_PDF = new MimeEnabledImageFlavor(RAW, "application/pdf");
    public static final ImageFlavor RAW_LZW = new ImageFlavor("RawLZW");
    public static final ImageFlavor RAW_CCITTFAX = new ImageFlavor("RawCCITTFax");
    public static final ImageFlavor GRAPHICS2D = new ImageFlavor("Graphics2DImage");
    private String name;

    public ImageFlavor(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String getMimeType() {
        return null;
    }

    public String getNamespace() {
        return null;
    }

    public boolean isCompatible(ImageFlavor flavor) {
        return this.equals(flavor);
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.name == null ? 0 : this.name.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ImageFlavor other = (ImageFlavor)obj;
        return !(this.name == null ? other.name != null : !this.name.equals(other.name));
    }

    public String toString() {
        return this.getName();
    }
}

