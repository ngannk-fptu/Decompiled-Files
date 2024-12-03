/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.MediaType
 */
package com.sun.jersey.core.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import javax.ws.rs.core.MediaType;

public final class ReaderWriter {
    public static final Charset UTF8 = Charset.forName("UTF-8");
    public static final String BUFFER_SIZE_SYSTEM_PROPERTY = "com.sun.jersey.core.util.ReaderWriter.BufferSize";
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    public static final int BUFFER_SIZE = ReaderWriter.getBufferSize();

    private static int getBufferSize() {
        String v = System.getProperty(BUFFER_SIZE_SYSTEM_PROPERTY, Integer.toString(8192));
        try {
            int i = Integer.valueOf(v);
            if (i <= 0) {
                throw new NumberFormatException();
            }
            return i;
        }
        catch (NumberFormatException ex) {
            return 8192;
        }
    }

    public static final void writeTo(InputStream in, OutputStream out) throws IOException {
        int read;
        byte[] data = new byte[BUFFER_SIZE];
        while ((read = in.read(data)) != -1) {
            out.write(data, 0, read);
        }
    }

    public static final void writeTo(Reader in, Writer out) throws IOException {
        int read;
        char[] data = new char[BUFFER_SIZE];
        while ((read = in.read(data)) != -1) {
            out.write(data, 0, read);
        }
    }

    public static final Charset getCharset(MediaType m) {
        String name = m == null ? null : (String)m.getParameters().get("charset");
        return name == null ? UTF8 : Charset.forName(name);
    }

    public static final String readFromAsString(InputStream in, MediaType type) throws IOException {
        return ReaderWriter.readFromAsString(new InputStreamReader(in, ReaderWriter.getCharset(type)));
    }

    public static final String readFromAsString(Reader reader) throws IOException {
        int l;
        StringBuilder sb = new StringBuilder();
        char[] c = new char[BUFFER_SIZE];
        while ((l = reader.read(c)) != -1) {
            sb.append(c, 0, l);
        }
        return sb.toString();
    }

    public static final void writeToAsString(String s, OutputStream out, MediaType type) throws IOException {
        BufferedWriter osw = new BufferedWriter(new OutputStreamWriter(out, ReaderWriter.getCharset(type)));
        osw.write(s);
        ((Writer)osw).flush();
    }
}

