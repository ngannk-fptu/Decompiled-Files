/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.detect;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.apache.tika.detect.Detector;
import org.apache.tika.detect.TextStatistics;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

public class TextDetector
implements Detector {
    private static final long serialVersionUID = 4774601079503507765L;
    private static final int DEFAULT_NUMBER_OF_BYTES_TO_TEST = 512;
    private static final boolean[] IS_CONTROL_BYTE = new boolean[32];
    private final int bytesToTest;

    public TextDetector() {
        this(512);
    }

    public TextDetector(int bytesToTest) {
        this.bytesToTest = bytesToTest;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public MediaType detect(InputStream input, Metadata metadata) throws IOException {
        if (input == null) {
            return MediaType.OCTET_STREAM;
        }
        input.mark(this.bytesToTest);
        try {
            TextStatistics stats = new TextStatistics();
            byte[] buffer = new byte[1024];
            int n = 0;
            int m = input.read(buffer, 0, Math.min(this.bytesToTest, buffer.length));
            while (m != -1 && n < this.bytesToTest) {
                stats.addData(buffer, 0, m);
                m = input.read(buffer, 0, Math.min(this.bytesToTest - (n += m), buffer.length));
            }
            if (stats.isMostlyAscii() || stats.looksLikeUTF8()) {
                MediaType mediaType = MediaType.TEXT_PLAIN;
                return mediaType;
            }
            MediaType mediaType = MediaType.OCTET_STREAM;
            return mediaType;
        }
        finally {
            input.reset();
        }
    }

    static {
        Arrays.fill(IS_CONTROL_BYTE, true);
        TextDetector.IS_CONTROL_BYTE[9] = false;
        TextDetector.IS_CONTROL_BYTE[10] = false;
        TextDetector.IS_CONTROL_BYTE[12] = false;
        TextDetector.IS_CONTROL_BYTE[13] = false;
        TextDetector.IS_CONTROL_BYTE[27] = false;
    }
}

