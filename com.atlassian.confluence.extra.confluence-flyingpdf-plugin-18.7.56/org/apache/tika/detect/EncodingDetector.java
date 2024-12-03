/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.detect;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import org.apache.tika.metadata.Metadata;

public interface EncodingDetector
extends Serializable {
    public Charset detect(InputStream var1, Metadata var2) throws IOException;
}

