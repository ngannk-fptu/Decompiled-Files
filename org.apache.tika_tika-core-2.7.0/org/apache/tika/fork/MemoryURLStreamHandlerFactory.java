/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.fork;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import org.apache.tika.fork.MemoryURLStreamHandler;

class MemoryURLStreamHandlerFactory
implements URLStreamHandlerFactory {
    MemoryURLStreamHandlerFactory() {
    }

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if ("tika-in-memory".equals(protocol)) {
            return new MemoryURLStreamHandler();
        }
        return null;
    }
}

