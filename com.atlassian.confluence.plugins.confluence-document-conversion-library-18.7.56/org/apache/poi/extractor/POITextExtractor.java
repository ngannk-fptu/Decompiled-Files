/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.extractor;

import java.io.Closeable;
import java.io.IOException;

public interface POITextExtractor
extends Closeable {
    public String getText();

    public POITextExtractor getMetadataTextExtractor();

    public void setCloseFilesystem(boolean var1);

    public boolean isCloseFilesystem();

    public Closeable getFilesystem();

    @Override
    default public void close() throws IOException {
        Closeable fs = this.getFilesystem();
        if (this.isCloseFilesystem() && fs != null) {
            fs.close();
        }
    }

    public Object getDocument();
}

