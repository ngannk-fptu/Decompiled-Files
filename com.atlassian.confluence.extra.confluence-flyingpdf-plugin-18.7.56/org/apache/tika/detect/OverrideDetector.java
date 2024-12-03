/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.detect;

import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MediaType;

public class OverrideDetector
implements Detector {
    @Override
    public MediaType detect(InputStream input, Metadata metadata) throws IOException {
        String type = metadata.get(TikaCoreProperties.CONTENT_TYPE_OVERRIDE);
        if (type == null) {
            return MediaType.OCTET_STREAM;
        }
        return MediaType.parse(type);
    }
}

