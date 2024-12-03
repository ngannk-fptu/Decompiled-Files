/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.pipes.fetcher;

import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.pipes.fetcher.Fetcher;

public class EmptyFetcher
implements Fetcher {
    @Override
    public String getName() {
        return "empty";
    }

    @Override
    public InputStream fetch(String fetchKey, Metadata metadata) throws TikaException, IOException {
        return null;
    }
}

