/*
 * Decompiled with CFR 0.152.
 */
package org.apache.coyote.http2;

import org.apache.coyote.http2.StreamProcessor;
import org.apache.tomcat.util.net.SocketEvent;

class StreamRunnable
implements Runnable {
    private final StreamProcessor processor;
    private final SocketEvent event;

    StreamRunnable(StreamProcessor processor, SocketEvent event) {
        this.processor = processor;
        this.event = event;
    }

    @Override
    public void run() {
        this.processor.process(this.event);
    }
}

