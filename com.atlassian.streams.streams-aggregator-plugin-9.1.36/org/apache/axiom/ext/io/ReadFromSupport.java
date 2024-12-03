/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.ext.io;

import java.io.InputStream;
import org.apache.axiom.ext.io.StreamCopyException;

public interface ReadFromSupport {
    public long readFrom(InputStream var1, long var2) throws StreamCopyException;
}

