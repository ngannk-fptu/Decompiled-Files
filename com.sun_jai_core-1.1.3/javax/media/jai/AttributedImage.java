/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedImageAdapter;

public class AttributedImage
extends RenderedImageAdapter {
    protected Object attribute;

    public AttributedImage(PlanarImage image, Object attribute) {
        super(image);
        this.attribute = attribute;
    }

    public PlanarImage getImage() {
        return (PlanarImage)this.theImage;
    }

    public void setAttribute(Object attribute) {
        this.attribute = attribute;
    }

    public Object getAttribute() {
        return this.attribute;
    }

    public boolean equals(Object o) {
        if (o != null && o instanceof AttributedImage) {
            AttributedImage ai = (AttributedImage)o;
            Object a = ai.getAttribute();
            return this.getImage().equals(ai.getImage()) && (this.attribute == null ? a == null : a != null && this.attribute.equals(a));
        }
        return false;
    }

    public String toString() {
        return "Attribute=(" + this.getAttribute() + ")  Image=" + this.getImage();
    }
}

