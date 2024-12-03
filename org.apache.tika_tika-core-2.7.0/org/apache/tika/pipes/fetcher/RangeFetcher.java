/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.pipes.fetcher;

import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.pipes.fetcher.Fetcher;

public interface RangeFetcher
extends Fetcher {
    public InputStream fetch(String var1, long var2, long var4, Metadata var6) throws TikaException, IOException;
}

