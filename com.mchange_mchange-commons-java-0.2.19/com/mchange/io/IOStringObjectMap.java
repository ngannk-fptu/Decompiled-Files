/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.io;

import com.mchange.io.IOStringEnumeration;
import java.io.IOException;

public interface IOStringObjectMap {
    public Object get(String var1) throws IOException;

    public void put(String var1, Object var2) throws IOException;

    public boolean putNoReplace(String var1, Object var2) throws IOException;

    public boolean remove(String var1) throws IOException;

    public boolean containsKey(String var1) throws IOException;

    public IOStringEnumeration keys() throws IOException;
}

