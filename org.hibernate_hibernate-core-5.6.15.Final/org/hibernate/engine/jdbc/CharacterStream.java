/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc;

import java.io.Reader;

public interface CharacterStream {
    public Reader asReader();

    public String asString();

    public long getLength();

    public void release();
}

