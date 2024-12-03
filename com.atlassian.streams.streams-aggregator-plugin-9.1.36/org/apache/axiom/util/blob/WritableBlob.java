/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.blob;

import java.io.InputStream;
import org.apache.axiom.ext.io.StreamCopyException;
import org.apache.axiom.util.blob.Blob;
import org.apache.axiom.util.blob.BlobOutputStream;

public interface WritableBlob
extends Blob {
    public boolean isSupportingReadUncommitted();

    public BlobOutputStream getOutputStream();

    public long readFrom(InputStream var1, long var2, boolean var4) throws StreamCopyException;

    public long readFrom(InputStream var1, long var2) throws StreamCopyException;

    public void release();
}

