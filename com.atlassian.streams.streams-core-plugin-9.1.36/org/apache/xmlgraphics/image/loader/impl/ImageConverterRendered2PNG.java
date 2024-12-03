/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.output.ByteArrayOutputStream
 */
package org.apache.xmlgraphics.image.loader.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.xmlgraphics.image.loader.Image;
import org.apache.xmlgraphics.image.loader.ImageException;
import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.impl.AbstractImageConverter;
import org.apache.xmlgraphics.image.loader.impl.ImageRawStream;
import org.apache.xmlgraphics.image.loader.impl.ImageRendered;
import org.apache.xmlgraphics.image.writer.ImageWriter;
import org.apache.xmlgraphics.image.writer.ImageWriterParams;
import org.apache.xmlgraphics.image.writer.ImageWriterRegistry;

public class ImageConverterRendered2PNG
extends AbstractImageConverter {
    @Override
    public Image convert(Image src, Map hints) throws ImageException, IOException {
        this.checkSourceFlavor(src);
        assert (src instanceof ImageRendered);
        ImageRendered rendered = (ImageRendered)src;
        ImageWriter writer = ImageWriterRegistry.getInstance().getWriterFor("image/png");
        if (writer == null) {
            throw new ImageException("Cannot convert image to PNG. No suitable ImageWriter found.");
        }
        ByteArrayOutputStream baout = new ByteArrayOutputStream();
        ImageWriterParams params = new ImageWriterParams();
        params.setResolution((int)Math.round(src.getSize().getDpiHorizontal()));
        writer.writeImage(rendered.getRenderedImage(), (OutputStream)baout, params);
        return new ImageRawStream(src.getInfo(), this.getTargetFlavor(), new ByteArrayInputStream(baout.toByteArray()));
    }

    @Override
    public ImageFlavor getSourceFlavor() {
        return ImageFlavor.RENDERED_IMAGE;
    }

    @Override
    public ImageFlavor getTargetFlavor() {
        return ImageFlavor.RAW_PNG;
    }
}

