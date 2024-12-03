/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader;

import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.RefinedImageFlavor;

public class XMLNamespaceEnabledImageFlavor
extends RefinedImageFlavor {
    public static final ImageFlavor SVG_DOM = new XMLNamespaceEnabledImageFlavor(ImageFlavor.XML_DOM, "http://www.w3.org/2000/svg");
    private String namespace;

    public XMLNamespaceEnabledImageFlavor(ImageFlavor parentFlavor, String namespace) {
        super(parentFlavor.getName() + ";namespace=" + namespace, parentFlavor);
        this.namespace = namespace;
    }

    @Override
    public String getNamespace() {
        return this.namespace;
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
        XMLNamespaceEnabledImageFlavor that = (XMLNamespaceEnabledImageFlavor)o;
        return !(this.namespace != null ? !this.namespace.equals(that.namespace) : that.namespace != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.namespace != null ? this.namespace.hashCode() : 0);
        return result;
    }
}

