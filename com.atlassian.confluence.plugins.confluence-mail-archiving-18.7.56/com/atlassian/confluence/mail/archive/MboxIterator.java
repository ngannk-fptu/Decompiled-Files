/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.mail.archive;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MboxIterator
implements Iterator<byte[]> {
    private static final byte[] MESSAGE_DELIMITER = new byte[]{10, 10, 70, 114, 111, 109, 32};
    private final InputStream mbox;
    private byte[] nextMessage;

    public static Iterable<byte[]> iterable(InputStream mbox) {
        MboxIterator iter = new MboxIterator(mbox);
        return () -> iter;
    }

    public MboxIterator(InputStream mbox) {
        this.mbox = mbox;
    }

    @Override
    public boolean hasNext() {
        if (this.nextMessage == null || this.nextMessage.length == 0) {
            this.nextMessage = this.readNextMessage();
        }
        return this.nextMessage != null && this.nextMessage.length > 0;
    }

    @Override
    public byte[] next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        byte[] next = this.nextMessage;
        this.nextMessage = null;
        return next;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove() not supported: mbox is not mutable");
    }

    private byte[] readNextMessage() {
        try {
            int b;
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            int pos = 0;
            while ((b = this.mbox.read()) != -1) {
                if (b == 13) continue;
                if (b == MESSAGE_DELIMITER[pos]) {
                    if (++pos != MESSAGE_DELIMITER.length) continue;
                    byte[] msg = buf.toByteArray();
                    buf.reset();
                    buf.write(new byte[]{70, 114, 111, 109, 32});
                    return msg;
                }
                if (pos != 0 && (pos != 2 || b != 10)) {
                    buf.write(MESSAGE_DELIMITER, 0, pos);
                    pos = 0;
                }
                buf.write(b);
            }
            return buf.toByteArray();
        }
        catch (IOException e) {
            throw new RuntimeException("Error while reading mailbox: " + e.toString(), e);
        }
    }
}

