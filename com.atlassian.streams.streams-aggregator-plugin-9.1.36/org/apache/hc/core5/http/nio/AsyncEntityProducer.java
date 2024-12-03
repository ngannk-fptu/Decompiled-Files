/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.nio;

import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.nio.AsyncDataProducer;

public interface AsyncEntityProducer
extends AsyncDataProducer,
EntityDetails {
    public boolean isRepeatable();

    public void failed(Exception var1);
}

