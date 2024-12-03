/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.ClassicHttpRequest
 */
package org.apache.hc.client5.http.classic.methods;

import org.apache.hc.client5.http.config.Configurable;
import org.apache.hc.core5.http.ClassicHttpRequest;

public interface HttpUriRequest
extends ClassicHttpRequest,
Configurable {
    public void abort() throws UnsupportedOperationException;

    public boolean isAborted();
}

