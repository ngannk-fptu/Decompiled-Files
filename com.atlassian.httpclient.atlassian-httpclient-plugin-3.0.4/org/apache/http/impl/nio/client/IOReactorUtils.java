/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.client;

import java.util.concurrent.ThreadFactory;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;

final class IOReactorUtils {
    private IOReactorUtils() {
    }

    public static ConnectingIOReactor create(IOReactorConfig config, ThreadFactory threadFactory) {
        try {
            return new DefaultConnectingIOReactor(config, threadFactory);
        }
        catch (IOReactorException ex) {
            throw new IllegalStateException(ex);
        }
    }
}

