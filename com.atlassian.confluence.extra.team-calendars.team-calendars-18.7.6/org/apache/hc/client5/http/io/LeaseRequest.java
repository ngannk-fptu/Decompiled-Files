/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.io;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.apache.hc.client5.http.io.ConnectionEndpoint;
import org.apache.hc.core5.concurrent.Cancellable;
import org.apache.hc.core5.util.Timeout;

public interface LeaseRequest
extends Cancellable {
    public ConnectionEndpoint get(Timeout var1) throws InterruptedException, ExecutionException, TimeoutException;
}

