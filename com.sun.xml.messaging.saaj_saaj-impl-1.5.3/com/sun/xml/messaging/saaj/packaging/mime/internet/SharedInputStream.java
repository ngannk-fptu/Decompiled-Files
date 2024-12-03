/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.messaging.saaj.packaging.mime.internet;

import java.io.InputStream;
import java.io.OutputStream;

public interface SharedInputStream {
    public long getPosition();

    public InputStream newStream(long var1, long var3);

    public void writeTo(long var1, long var3, OutputStream var5);
}

