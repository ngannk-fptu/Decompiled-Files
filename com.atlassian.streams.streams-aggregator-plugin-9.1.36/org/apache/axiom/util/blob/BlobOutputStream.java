/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.blob;

import java.io.InputStream;
import java.io.OutputStream;
import org.apache.axiom.ext.io.ReadFromSupport;
import org.apache.axiom.ext.io.StreamCopyException;
import org.apache.axiom.util.blob.WritableBlob;

public abstract class BlobOutputStream
extends OutputStream
implements ReadFromSupport {
    public abstract WritableBlob getBlob();

    public long readFrom(InputStream inputStream, long length) throws StreamCopyException {
        return this.getBlob().readFrom(inputStream, length);
    }
}

