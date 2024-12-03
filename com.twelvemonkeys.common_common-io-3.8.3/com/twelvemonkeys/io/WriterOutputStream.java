/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.DateUtil
 */
package com.twelvemonkeys.io;

import com.twelvemonkeys.io.FastByteArrayOutputStream;
import com.twelvemonkeys.io.NullOutputStream;
import com.twelvemonkeys.lang.DateUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

public class WriterOutputStream
extends OutputStream {
    protected Writer writer;
    protected final Decoder decoder;
    final ByteArrayOutputStream bufferStream = new FastByteArrayOutputStream(1024);
    private volatile boolean isFlushing = false;
    private static final boolean NIO_AVAILABLE = WriterOutputStream.isNIOAvailable();

    private static boolean isNIOAvailable() {
        try {
            Class.forName("java.nio.charset.Charset");
            return true;
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    public WriterOutputStream(Writer writer, String string) {
        this.writer = writer;
        this.decoder = WriterOutputStream.getDecoder(string);
    }

    public WriterOutputStream(Writer writer) {
        this(writer, null);
    }

    private static Decoder getDecoder(String string) {
        if (NIO_AVAILABLE) {
            return new CharsetDecoder(string);
        }
        return new StringDecoder(string);
    }

    @Override
    public void close() throws IOException {
        this.flush();
        this.writer.close();
        this.writer = null;
    }

    @Override
    public void flush() throws IOException {
        this.flushBuffer();
        this.writer.flush();
    }

    @Override
    public final void write(byte[] byArray) throws IOException {
        if (byArray == null) {
            throw new NullPointerException("bytes == null");
        }
        this.write(byArray, 0, byArray.length);
    }

    @Override
    public final void write(byte[] byArray, int n, int n2) throws IOException {
        this.flushBuffer();
        this.decoder.decodeTo(this.writer, byArray, n, n2);
    }

    @Override
    public final void write(int n) {
        this.bufferStream.write(n);
    }

    private void flushBuffer() throws IOException {
        if (!this.isFlushing && this.bufferStream.size() > 0) {
            this.isFlushing = true;
            this.bufferStream.writeTo(this);
            this.bufferStream.reset();
            this.isFlushing = false;
        }
    }

    public static void main(String[] stringArray) throws IOException {
        int n;
        int n2 = 1000000;
        byte[] byArray = "\ufffd\ufffd\ufffd\ufffd\ufffd\ufffd klashf lkash ljah lhaaklhghdfgu ksd".getBytes("UTF-8");
        PrintWriter printWriter = new PrintWriter(new NullOutputStream());
        Decoder decoder = new StringDecoder("UTF-8");
        for (n = 0; n < 10000; ++n) {
            decoder.decodeTo(printWriter, byArray, 0, byArray.length);
        }
        long l = System.currentTimeMillis();
        for (n = 0; n < n2; ++n) {
            decoder.decodeTo(printWriter, byArray, 0, byArray.length);
        }
        long l2 = DateUtil.delta((long)l);
        System.out.println("StringDecoder");
        System.out.println("time: " + l2);
        StringWriter stringWriter = new StringWriter();
        decoder.decodeTo(stringWriter, byArray, 0, byArray.length);
        String string = stringWriter.toString();
        System.out.println("str: \"" + string + "\"");
        System.out.println("chars.length: " + string.length());
        System.out.println();
        if (NIO_AVAILABLE) {
            decoder = new CharsetDecoder("UTF-8");
            for (n = 0; n < 10000; ++n) {
                decoder.decodeTo(printWriter, byArray, 0, byArray.length);
            }
            l = System.currentTimeMillis();
            for (n = 0; n < n2; ++n) {
                decoder.decodeTo(printWriter, byArray, 0, byArray.length);
            }
            l2 = DateUtil.delta((long)l);
            System.out.println("CharsetDecoder");
            System.out.println("time: " + l2);
            stringWriter = new StringWriter();
            decoder.decodeTo(stringWriter, byArray, 0, byArray.length);
            string = stringWriter.toString();
            System.out.println("str: \"" + string + "\"");
            System.out.println("chars.length: " + string.length());
            System.out.println();
        }
        WriterOutputStream writerOutputStream = new WriterOutputStream(new PrintWriter(System.out), "UTF-8");
        ((OutputStream)writerOutputStream).write(byArray);
        ((OutputStream)writerOutputStream).flush();
        System.out.println();
        for (byte by : byArray) {
            ((OutputStream)writerOutputStream).write(by & 0xFF);
        }
        ((OutputStream)writerOutputStream).flush();
    }

    private static final class StringDecoder
    implements Decoder {
        final String mCharset;

        StringDecoder(String string) {
            this.mCharset = string;
        }

        @Override
        public void decodeTo(Writer writer, byte[] byArray, int n, int n2) throws IOException {
            String string = this.mCharset == null ? new String(byArray, n, n2) : new String(byArray, n, n2, this.mCharset);
            writer.write(string);
        }
    }

    private static final class CharsetDecoder
    implements Decoder {
        final Charset mCharset;

        CharsetDecoder(String string) {
            String string2 = string != null ? string : System.getProperty("file.encoding", "ISO-8859-1");
            this.mCharset = Charset.forName(string2);
        }

        @Override
        public void decodeTo(Writer writer, byte[] byArray, int n, int n2) throws IOException {
            CharBuffer charBuffer = this.mCharset.decode(ByteBuffer.wrap(byArray, n, n2));
            writer.write(charBuffer.array(), 0, charBuffer.length());
        }
    }

    private static interface Decoder {
        public void decodeTo(Writer var1, byte[] var2, int var3, int var4) throws IOException;
    }
}

