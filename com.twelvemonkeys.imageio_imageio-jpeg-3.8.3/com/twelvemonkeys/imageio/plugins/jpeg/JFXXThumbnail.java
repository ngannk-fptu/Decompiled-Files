/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.stream.ByteArrayImageInputStream
 */
package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.plugins.jpeg.JFXX;
import com.twelvemonkeys.imageio.plugins.jpeg.ThumbnailReader;
import com.twelvemonkeys.imageio.stream.ByteArrayImageInputStream;
import java.io.IOException;
import javax.imageio.IIOException;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

final class JFXXThumbnail {
    private JFXXThumbnail() {
    }

    static ThumbnailReader from(JFXX jFXX, ImageReader imageReader) throws IOException {
        if (jFXX != null) {
            if (jFXX.thumbnail != null && jFXX.thumbnail.length > 2) {
                switch (jFXX.extensionCode) {
                    case 16: {
                        if (((jFXX.thumbnail[0] & 0xFF) << 8 | jFXX.thumbnail[1] & 0xFF) != 65496) break;
                        return new ThumbnailReader.JPEGThumbnailReader(imageReader, (ImageInputStream)new ByteArrayImageInputStream(jFXX.thumbnail), 0L);
                    }
                    case 17: {
                        int n = jFXX.thumbnail[0] & 0xFF;
                        int n2 = jFXX.thumbnail[1] & 0xFF;
                        if (jFXX.thumbnail.length < 770 + n * n2) break;
                        return new ThumbnailReader.IndexedThumbnailReader(n, n2, jFXX.thumbnail, 2, jFXX.thumbnail, 770);
                    }
                    case 19: {
                        int n = jFXX.thumbnail[0] & 0xFF;
                        int n3 = jFXX.thumbnail[1] & 0xFF;
                        if (jFXX.thumbnail.length < 2 + n * n3 * 3) break;
                        return new ThumbnailReader.UncompressedThumbnailReader(n, n3, jFXX.thumbnail, 2);
                    }
                    default: {
                        throw new IIOException(String.format("Unknown JFXX extension code: %d, ignoring thumbnail", jFXX.extensionCode));
                    }
                }
            }
            throw new IIOException("JFXX segment truncated, ignoring thumbnail");
        }
        return null;
    }
}

