/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader;

import java.util.HashMap;
import java.util.Map;
import org.apache.xmlgraphics.image.loader.Image;
import org.apache.xmlgraphics.image.loader.ImageSize;

public class ImageInfo {
    public static final Object ORIGINAL_IMAGE = Image.class;
    public static final Object HAS_MORE_IMAGES = "HAS_MORE_IMAGES";
    private String originalURI;
    private String mimeType;
    private ImageSize size;
    private Map customObjects = new HashMap();

    public ImageInfo(String originalURI, String mimeType) {
        this.originalURI = originalURI;
        this.mimeType = mimeType;
    }

    public String getOriginalURI() {
        return this.originalURI;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public ImageSize getSize() {
        return this.size;
    }

    public void setSize(ImageSize size) {
        this.size = size;
    }

    public Map getCustomObjects() {
        return this.customObjects;
    }

    public Image getOriginalImage() {
        return (Image)this.customObjects.get(ORIGINAL_IMAGE);
    }

    public String toString() {
        return this.getOriginalURI() + " (" + this.getMimeType() + ")";
    }
}

