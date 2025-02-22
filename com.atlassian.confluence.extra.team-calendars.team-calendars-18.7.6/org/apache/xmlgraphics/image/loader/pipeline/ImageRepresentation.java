/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader.pipeline;

import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.util.dijkstra.Vertex;

public class ImageRepresentation
implements Vertex {
    private ImageFlavor flavor;

    public ImageRepresentation(ImageFlavor flavor) {
        if (flavor == null) {
            throw new NullPointerException("flavor must not be null");
        }
        this.flavor = flavor;
    }

    public ImageFlavor getFlavor() {
        return this.flavor;
    }

    public boolean equals(Object obj) {
        assert (obj != null);
        return this.toString().equals(obj.toString());
    }

    public int hashCode() {
        return this.getFlavor().hashCode();
    }

    public int compareTo(Object obj) {
        return this.toString().compareTo(((ImageRepresentation)obj).toString());
    }

    public String toString() {
        return this.getFlavor().toString();
    }
}

