/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.extractor;

import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.metadata.Metadata;

public interface EmbeddedStreamTranslator {
    public boolean shouldTranslate(InputStream var1, Metadata var2) throws IOException;

    public InputStream translate(InputStream var1, Metadata var2) throws IOException;
}

