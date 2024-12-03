/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.detect;

import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

public class ZeroSizeFileDetector
implements Detector {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public MediaType detect(InputStream stream, Metadata metadata) throws IOException {
        if (stream != null) {
            try {
                stream.mark(1);
                if (stream.read() == -1) {
                    MediaType mediaType = MediaType.EMPTY;
                    return mediaType;
                }
            }
            finally {
                stream.reset();
            }
        }
        return MediaType.OCTET_STREAM;
    }
}

