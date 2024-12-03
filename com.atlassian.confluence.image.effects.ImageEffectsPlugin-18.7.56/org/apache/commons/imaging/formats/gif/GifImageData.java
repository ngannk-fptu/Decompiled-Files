/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.gif;

import org.apache.commons.imaging.formats.gif.GraphicControlExtension;
import org.apache.commons.imaging.formats.gif.ImageDescriptor;

class GifImageData {
    final ImageDescriptor descriptor;
    final GraphicControlExtension gce;

    GifImageData(ImageDescriptor descriptor, GraphicControlExtension gce) {
        this.descriptor = descriptor;
        this.gce = gce;
    }
}

