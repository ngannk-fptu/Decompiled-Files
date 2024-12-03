/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.plugins.jpeg.JFIF;
import com.twelvemonkeys.imageio.plugins.jpeg.ThumbnailReader;
import java.io.IOException;
import javax.imageio.IIOException;

final class JFIFThumbnail {
    private JFIFThumbnail() {
    }

    static ThumbnailReader from(JFIF jFIF) throws IOException {
        if (jFIF != null && jFIF.xThumbnail > 0 && jFIF.yThumbnail > 0) {
            if (jFIF.thumbnail == null || jFIF.thumbnail.length < jFIF.xThumbnail * jFIF.yThumbnail) {
                throw new IIOException("Truncated JFIF thumbnail");
            }
            return new ThumbnailReader.UncompressedThumbnailReader(jFIF.xThumbnail, jFIF.yThumbnail, jFIF.thumbnail);
        }
        return null;
    }
}

