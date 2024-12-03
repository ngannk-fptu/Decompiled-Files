/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.jackrabbit;

import java.io.IOException;
import java.io.InputStream;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.api.ReferenceBinary;
import org.apache.jackrabbit.api.ReferenceBinaryException;

public class SimpleReferenceBinary
implements ReferenceBinary {
    private final String reference;

    public SimpleReferenceBinary(String reference) {
        this.reference = reference;
    }

    @Override
    public String getReference() {
        return this.reference;
    }

    @Override
    public InputStream getStream() throws RepositoryException {
        throw new ReferenceBinaryException("Broken binary reference: " + this.reference);
    }

    @Override
    public int read(byte[] b, long position) throws IOException, RepositoryException {
        throw new ReferenceBinaryException("Broken binary reference: " + this.reference);
    }

    @Override
    public long getSize() throws RepositoryException {
        throw new ReferenceBinaryException("Broken binary reference: " + this.reference);
    }

    @Override
    public void dispose() {
    }
}

