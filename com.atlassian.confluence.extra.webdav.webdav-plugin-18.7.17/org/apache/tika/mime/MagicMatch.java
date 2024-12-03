/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.mime;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.tika.detect.MagicDetector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.Clause;
import org.apache.tika.mime.MediaType;

class MagicMatch
implements Clause {
    private final MediaType mediaType;
    private final String type;
    private final String offset;
    private final String value;
    private final String mask;
    private MagicDetector detector = null;

    MagicMatch(MediaType mediaType, String type, String offset, String value, String mask) {
        this.mediaType = mediaType;
        this.type = type;
        this.offset = offset;
        this.value = value;
        this.mask = mask;
    }

    private synchronized MagicDetector getDetector() {
        if (this.detector == null) {
            this.detector = MagicDetector.parse(this.mediaType, this.type, this.offset, this.value, this.mask);
        }
        return this.detector;
    }

    @Override
    public boolean eval(byte[] data) {
        try {
            return this.getDetector().detect(new ByteArrayInputStream(data), new Metadata()) != MediaType.OCTET_STREAM;
        }
        catch (IOException e) {
            return false;
        }
    }

    @Override
    public int size() {
        return this.getDetector().getLength();
    }

    public String toString() {
        return this.mediaType.toString() + " " + this.type + " " + this.offset + " " + this.value + " " + this.mask;
    }
}

