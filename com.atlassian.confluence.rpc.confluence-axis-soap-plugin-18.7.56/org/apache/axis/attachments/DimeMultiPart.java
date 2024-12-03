/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.attachments;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;
import org.apache.axis.attachments.DimeBodyPart;

public final class DimeMultiPart {
    static final long transSize = Integer.MAX_VALUE;
    static final byte CURRENT_VERSION = 1;
    protected Vector parts = new Vector();

    public void addBodyPart(DimeBodyPart part) {
        this.parts.add(part);
    }

    public void write(OutputStream os) throws IOException {
        int size = this.parts.size();
        int last = size - 1;
        for (int i = 0; i < size; ++i) {
            ((DimeBodyPart)this.parts.elementAt(i)).write(os, (byte)((i == 0 ? 4 : 0) | (i == last ? 2 : 0)), Integer.MAX_VALUE);
        }
    }

    public long getTransmissionSize() {
        long size = 0L;
        for (int i = this.parts.size() - 1; i > -1; --i) {
            size += ((DimeBodyPart)this.parts.elementAt(i)).getTransmissionSize(Integer.MAX_VALUE);
        }
        return size;
    }
}

