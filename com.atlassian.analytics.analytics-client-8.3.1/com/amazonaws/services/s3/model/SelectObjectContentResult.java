/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.SelectObjectContentEventStream;
import java.io.Closeable;
import java.io.IOException;

public class SelectObjectContentResult
implements Closeable {
    private SelectObjectContentEventStream payload;

    public SelectObjectContentEventStream getPayload() {
        return this.payload;
    }

    public void setPayload(SelectObjectContentEventStream payload) {
        this.payload = payload;
    }

    public SelectObjectContentResult withPayload(SelectObjectContentEventStream payload) {
        this.setPayload(payload);
        return this;
    }

    @Override
    public void close() throws IOException {
        if (this.payload != null) {
            this.payload.close();
        }
    }
}

