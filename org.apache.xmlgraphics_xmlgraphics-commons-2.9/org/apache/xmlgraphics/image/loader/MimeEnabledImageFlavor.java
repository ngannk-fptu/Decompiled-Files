/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader;

import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.RefinedImageFlavor;

public class MimeEnabledImageFlavor
extends RefinedImageFlavor {
    private String mime;

    public MimeEnabledImageFlavor(ImageFlavor parentFlavor, String mime) {
        super(mime + ";" + parentFlavor.getName(), parentFlavor);
        this.mime = mime;
    }

    @Override
    public String getMimeType() {
        return this.mime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        MimeEnabledImageFlavor that = (MimeEnabledImageFlavor)o;
        return !(this.mime != null ? !this.mime.equals(that.mime) : that.mime != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.mime != null ? this.mime.hashCode() : 0);
        return result;
    }
}

