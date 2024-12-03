/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.metadata;

import com.twelvemonkeys.imageio.metadata.Directory;
import java.io.IOException;
import javax.imageio.stream.ImageInputStream;

public abstract class MetadataReader {
    public abstract Directory read(ImageInputStream var1) throws IOException;
}

