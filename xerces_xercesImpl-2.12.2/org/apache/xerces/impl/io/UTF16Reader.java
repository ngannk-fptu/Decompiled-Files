/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Locale;
import org.apache.xerces.impl.io.MalformedByteSequenceException;
import org.apache.xerces.impl.msg.XMLMessageFormatter;
import org.apache.xerces.util.MessageFormatter;

public final class UTF16Reader
extends Reader {
    public static final int DEFAULT_BUFFER_SIZE = 4096;
    protected final InputStream fInputStream;
    protected final byte[] fBuffer;
    protected final boolean fIsBigEndian;
    private final MessageFormatter fFormatter;
    private final Locale fLocale;

    public UTF16Reader(InputStream inputStream, boolean bl) {
        this(inputStream, 4096, bl, (MessageFormatter)new XMLMessageFormatter(), Locale.getDefault());
    }

    public UTF16Reader(InputStream inputStream, boolean bl, MessageFormatter messageFormatter, Locale locale) {
        this(inputStream, 4096, bl, messageFormatter, locale);
    }

    public UTF16Reader(InputStream inputStream, int n, boolean bl, MessageFormatter messageFormatter, Locale locale) {
        this(inputStream, new byte[n], bl, messageFormatter, locale);
    }

    public UTF16Reader(InputStream inputStream, byte[] byArray, boolean bl, MessageFormatter messageFormatter, Locale locale) {
        this.fInputStream = inputStream;
        this.fBuffer = byArray;
        this.fIsBigEndian = bl;
        this.fFormatter = messageFormatter;
        this.fLocale = locale;
    }

    @Override
    public int read() throws IOException {
        int n = this.fInputStream.read();
        if (n == -1) {
            return -1;
        }
        int n2 = this.fInputStream.read();
        if (n2 == -1) {
            this.expectedTwoBytes();
        }
        if (this.fIsBigEndian) {
            return n << 8 | n2;
        }
        return n2 << 8 | n;
    }

    @Override
    public int read(char[] cArray, int n, int n2) throws IOException {
        int n3;
        int n4;
        int n5 = n2 << 1;
        if (n5 > this.fBuffer.length) {
            n5 = this.fBuffer.length;
        }
        if ((n4 = this.fInputStream.read(this.fBuffer, 0, n5)) == -1) {
            return -1;
        }
        if ((n4 & 1) != 0) {
            n3 = this.fInputStream.read();
            if (n3 == -1) {
                this.expectedTwoBytes();
            }
            this.fBuffer[n4++] = (byte)n3;
        }
        n3 = n4 >> 1;
        if (this.fIsBigEndian) {
            this.processBE(cArray, n, n3);
        } else {
            this.processLE(cArray, n, n3);
        }
        return n3;
    }

    @Override
    public long skip(long l) throws IOException {
        long l2 = this.fInputStream.skip(l << 1);
        if ((l2 & 1L) != 0L) {
            int n = this.fInputStream.read();
            if (n == -1) {
                this.expectedTwoBytes();
            }
            ++l2;
        }
        return l2 >> 1;
    }

    @Override
    public boolean ready() throws IOException {
        return false;
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public void mark(int n) throws IOException {
        throw new IOException(this.fFormatter.formatMessage(this.fLocale, "OperationNotSupported", new Object[]{"mark()", "UTF-16"}));
    }

    @Override
    public void reset() throws IOException {
    }

    @Override
    public void close() throws IOException {
        this.fInputStream.close();
    }

    private void processBE(char[] cArray, int n, int n2) {
        int n3 = 0;
        for (int i = 0; i < n2; ++i) {
            int n4 = this.fBuffer[n3++] & 0xFF;
            int n5 = this.fBuffer[n3++] & 0xFF;
            cArray[n++] = (char)(n4 << 8 | n5);
        }
    }

    private void processLE(char[] cArray, int n, int n2) {
        int n3 = 0;
        for (int i = 0; i < n2; ++i) {
            int n4 = this.fBuffer[n3++] & 0xFF;
            int n5 = this.fBuffer[n3++] & 0xFF;
            cArray[n++] = (char)(n5 << 8 | n4);
        }
    }

    private void expectedTwoBytes() throws MalformedByteSequenceException {
        throw new MalformedByteSequenceException(this.fFormatter, this.fLocale, "http://www.w3.org/TR/1998/REC-xml-19980210", "ExpectedByte", new Object[]{"2", "2"});
    }
}

