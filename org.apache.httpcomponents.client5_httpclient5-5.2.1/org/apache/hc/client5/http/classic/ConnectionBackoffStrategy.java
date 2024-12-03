/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.HttpResponse
 */
package org.apache.hc.client5.http.classic;

import org.apache.hc.core5.http.HttpResponse;

public interface ConnectionBackoffStrategy {
    public boolean shouldBackoff(Throwable var1);

    public boolean shouldBackoff(HttpResponse var1);
}

