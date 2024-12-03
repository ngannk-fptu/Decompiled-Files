/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.io;

import java.io.IOException;

public interface IOEnumeration {
    public boolean hasMoreElements() throws IOException;

    public Object nextElement() throws IOException;
}

