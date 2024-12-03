/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.pipes.fetcher;

import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;

public interface Fetcher {
    public String getName();

    public InputStream fetch(String var1, Metadata var2) throws TikaException, IOException;
}

