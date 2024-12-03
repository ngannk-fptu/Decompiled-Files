/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.ha.store.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

public interface Storeable
extends Serializable {
    public long _storeable_getVersion();

    public void _storeable_setVersion(long var1);

    public long _storeable_getLastAccessTime();

    public void _storeable_setLastAccessTime(long var1);

    public long _storeable_getMaxIdleTime();

    public void _storeable_setMaxIdleTime(long var1);

    public String[] _storeable_getAttributeNames();

    public boolean[] _storeable_getDirtyStatus();

    public void _storeable_writeState(OutputStream var1) throws IOException;

    public void _storeable_readState(InputStream var1) throws IOException;
}

