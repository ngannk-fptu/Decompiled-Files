/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.blob;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.axiom.ext.io.StreamCopyException;

public interface Blob {
    public InputStream getInputStream() throws IOException;

    public void writeTo(OutputStream var1) throws StreamCopyException;

    public long getLength();
}

