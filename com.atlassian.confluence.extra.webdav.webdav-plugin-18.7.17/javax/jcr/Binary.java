/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr;

import java.io.IOException;
import java.io.InputStream;
import javax.jcr.RepositoryException;

public interface Binary {
    public InputStream getStream() throws RepositoryException;

    public int read(byte[] var1, long var2) throws IOException, RepositoryException;

    public long getSize() throws RepositoryException;

    public void dispose();
}

