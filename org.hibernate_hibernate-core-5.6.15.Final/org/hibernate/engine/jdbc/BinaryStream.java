/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc;

import java.io.InputStream;

public interface BinaryStream {
    public InputStream getInputStream();

    public byte[] getBytes();

    public long getLength();

    public void release();
}

