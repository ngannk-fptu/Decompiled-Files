/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http;

import org.apache.hc.core5.http.HttpEntity;

public interface HttpEntityContainer {
    public HttpEntity getEntity();

    public void setEntity(HttpEntity var1);
}

