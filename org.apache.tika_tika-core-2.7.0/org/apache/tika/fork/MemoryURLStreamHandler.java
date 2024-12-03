/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.fork;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.tika.fork.MemoryURLConnection;
import org.apache.tika.fork.MemoryURLStreamRecord;

class MemoryURLStreamHandler
extends URLStreamHandler {
    private static final AtomicInteger counter = new AtomicInteger();
    private static final List<MemoryURLStreamRecord> records = new LinkedList<MemoryURLStreamRecord>();

    MemoryURLStreamHandler() {
    }

    public static URL createURL(byte[] data) {
        try {
            int i = counter.incrementAndGet();
            URL url = new URL("tika-in-memory", "localhost", "/" + i);
            MemoryURLStreamRecord record = new MemoryURLStreamRecord();
            record.url = new WeakReference<URL>(url);
            record.data = data;
            records.add(record);
            return url;
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        Iterator<MemoryURLStreamRecord> iterator = records.iterator();
        while (iterator.hasNext()) {
            MemoryURLStreamRecord record = iterator.next();
            URL url = (URL)record.url.get();
            if (url == null) {
                iterator.remove();
                continue;
            }
            if (url != u) continue;
            return new MemoryURLConnection(u, record.data);
        }
        throw new IOException("Unknown URL: " + u);
    }
}

