/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.metadata.exif;

import com.twelvemonkeys.imageio.metadata.Directory;
import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.metadata.MetadataWriter;
import com.twelvemonkeys.imageio.metadata.tiff.TIFFWriter;
import java.io.IOException;
import java.util.Collection;
import javax.imageio.stream.ImageOutputStream;

@Deprecated
public final class EXIFWriter
extends MetadataWriter {
    private final TIFFWriter delegate = new TIFFWriter();

    @Override
    public boolean write(Directory directory, ImageOutputStream imageOutputStream) throws IOException {
        return this.delegate.write(directory, imageOutputStream);
    }

    public boolean write(Collection<Entry> collection, ImageOutputStream imageOutputStream) throws IOException {
        return this.delegate.write(collection, imageOutputStream);
    }
}

