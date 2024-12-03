/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.brotli.dec.BrotliInputStream
 */
package org.apache.hc.client5.http.entity;

import java.io.IOException;
import java.io.InputStream;
import org.apache.hc.client5.http.entity.InputStreamFactory;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.brotli.dec.BrotliInputStream;

@Contract(threading=ThreadingBehavior.STATELESS)
public class BrotliInputStreamFactory
implements InputStreamFactory {
    private static final BrotliInputStreamFactory INSTANCE = new BrotliInputStreamFactory();

    public static BrotliInputStreamFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public InputStream create(InputStream inputStream) throws IOException {
        return new BrotliInputStream(inputStream);
    }
}

