/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.detect;

import java.io.InputStream;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

public class TypeDetector
implements Detector {
    @Override
    public MediaType detect(InputStream input, Metadata metadata) {
        MediaType type;
        String hint = metadata.get("Content-Type");
        if (hint != null && (type = MediaType.parse(hint)) != null) {
            return type;
        }
        return MediaType.OCTET_STREAM;
    }
}

