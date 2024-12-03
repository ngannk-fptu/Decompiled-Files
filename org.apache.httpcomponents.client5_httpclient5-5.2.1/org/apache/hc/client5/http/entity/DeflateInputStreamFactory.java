/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 */
package org.apache.hc.client5.http.entity;

import java.io.IOException;
import java.io.InputStream;
import org.apache.hc.client5.http.entity.DeflateInputStream;
import org.apache.hc.client5.http.entity.InputStreamFactory;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;

@Contract(threading=ThreadingBehavior.STATELESS)
public class DeflateInputStreamFactory
implements InputStreamFactory {
    private static final DeflateInputStreamFactory INSTANCE = new DeflateInputStreamFactory();

    public static DeflateInputStreamFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public InputStream create(InputStream inputStream) throws IOException {
        return new DeflateInputStream(inputStream);
    }
}

