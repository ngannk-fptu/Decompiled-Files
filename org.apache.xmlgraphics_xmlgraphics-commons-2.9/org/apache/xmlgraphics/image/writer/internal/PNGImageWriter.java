/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.writer.internal;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.xmlgraphics.image.codec.png.PNGImageEncoder;
import org.apache.xmlgraphics.image.writer.AbstractImageWriter;
import org.apache.xmlgraphics.image.writer.ImageWriterParams;

public class PNGImageWriter
extends AbstractImageWriter {
    @Override
    public void writeImage(RenderedImage image, OutputStream out) throws IOException {
        this.writeImage(image, out, null);
    }

    @Override
    public void writeImage(RenderedImage image, OutputStream out, ImageWriterParams params) throws IOException {
        PNGImageEncoder encoder = new PNGImageEncoder(out, null);
        encoder.encode(image);
    }

    @Override
    public String getMIMEType() {
        return "image/png";
    }
}

