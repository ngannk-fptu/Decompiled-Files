/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

class FakeURLStreamHandler
extends URLStreamHandler {
    FakeURLStreamHandler() {
    }

    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        throw new IOException("FakeURLStreamHandler can not be used!");
    }
}

