/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.writer;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.xmlgraphics.image.writer.ImageWriter;
import org.apache.xmlgraphics.image.writer.MultiImageWriter;

public abstract class AbstractImageWriter
implements ImageWriter {
    @Override
    public MultiImageWriter createMultiImageWriter(OutputStream out) throws IOException {
        throw new UnsupportedOperationException("This ImageWriter does not support writing multiple images to a single image file.");
    }

    @Override
    public boolean isFunctional() {
        return true;
    }

    @Override
    public boolean supportsMultiImageWriter() {
        return false;
    }
}

