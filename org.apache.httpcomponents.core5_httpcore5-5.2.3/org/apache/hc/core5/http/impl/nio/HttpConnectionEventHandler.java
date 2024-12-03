/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.impl.nio;

import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.http.HttpConnection;
import org.apache.hc.core5.reactor.IOEventHandler;

@Internal
public interface HttpConnectionEventHandler
extends IOEventHandler,
HttpConnection {
}

