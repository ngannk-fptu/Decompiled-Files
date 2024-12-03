/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.fork;

import java.lang.ref.WeakReference;
import java.net.URL;

class MemoryURLStreamRecord {
    public WeakReference<URL> url;
    public byte[] data;

    MemoryURLStreamRecord() {
    }
}

