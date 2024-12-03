/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.io;

import com.mchange.io.IOEnumeration;
import java.io.IOException;

public interface IOStringEnumeration
extends IOEnumeration {
    public boolean hasMoreStrings() throws IOException;

    public String nextString() throws IOException;
}

