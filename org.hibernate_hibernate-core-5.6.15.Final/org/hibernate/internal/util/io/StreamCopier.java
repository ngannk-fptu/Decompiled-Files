/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.hibernate.HibernateException;

public final class StreamCopier {
    public static final int BUFFER_SIZE = 4096;
    public static final byte[] BUFFER = new byte[4096];

    private StreamCopier() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    public static long copy(InputStream from, OutputStream into) {
        try {
            long totalRead = 0L;
            while (true) {
                byte[] byArray = BUFFER;
                // MONITORENTER : BUFFER
                int amountRead = from.read(BUFFER);
                if (amountRead == -1) {
                    // MONITOREXIT : byArray
                    return totalRead;
                }
                into.write(BUFFER, 0, amountRead);
                totalRead += (long)amountRead;
                if (amountRead < 4096) {
                    // MONITOREXIT : byArray
                    return totalRead;
                }
                // MONITOREXIT : byArray
            }
        }
        catch (IOException e) {
            throw new HibernateException("Unable to copy stream content", e);
        }
    }
}

