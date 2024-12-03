/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.detect;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.apache.tika.config.Field;
import org.apache.tika.detect.EncodingDetector;
import org.apache.tika.metadata.Metadata;

public class NonDetectingEncodingDetector
implements EncodingDetector {
    private Charset charset = StandardCharsets.UTF_8;

    public NonDetectingEncodingDetector() {
    }

    public NonDetectingEncodingDetector(Charset charset) {
        this.charset = charset;
    }

    @Override
    public Charset detect(InputStream input, Metadata metadata) throws IOException {
        return this.charset;
    }

    public Charset getCharset() {
        return this.charset;
    }

    @Field
    private void setCharset(String charsetName) {
        this.charset = Charset.forName(charsetName);
    }
}

