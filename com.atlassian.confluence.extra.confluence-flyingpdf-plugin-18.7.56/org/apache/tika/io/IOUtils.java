/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.channels.Channel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class IOUtils {
    public static final Charset UTF_8 = StandardCharsets.UTF_8;
    private static final int SKIP_BUFFER_SIZE = 2048;
    private static byte[] SKIP_BYTE_BUFFER;
    private static final int DEFAULT_BUFFER_SIZE = 4096;

    public static void closeQuietly(Reader input) {
        try {
            if (input != null) {
                input.close();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public static void closeQuietly(Channel channel) {
        try {
            if (channel != null) {
                channel.close();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public static void closeQuietly(Writer output) {
        try {
            if (output != null) {
                output.close();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public static void closeQuietly(InputStream input) {
        try {
            if (input != null) {
                input.close();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public static void closeQuietly(OutputStream output) {
        try {
            if (output != null) {
                output.close();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        IOUtils.copy(input, (OutputStream)output);
        return output.toByteArray();
    }

    public static byte[] toByteArray(Reader input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        IOUtils.copy(input, (OutputStream)output);
        return output.toByteArray();
    }

    public static byte[] toByteArray(Reader input, String encoding) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        IOUtils.copy(input, output, encoding);
        return output.toByteArray();
    }

    @Deprecated
    public static byte[] toByteArray(String input) throws IOException {
        return input.getBytes(UTF_8);
    }

    public static char[] toCharArray(InputStream is) throws IOException {
        CharArrayWriter output = new CharArrayWriter();
        IOUtils.copy(is, (Writer)output);
        return output.toCharArray();
    }

    public static char[] toCharArray(InputStream is, String encoding) throws IOException {
        CharArrayWriter output = new CharArrayWriter();
        IOUtils.copy(is, output, encoding);
        return output.toCharArray();
    }

    public static char[] toCharArray(Reader input) throws IOException {
        CharArrayWriter sw = new CharArrayWriter();
        IOUtils.copy(input, (Writer)sw);
        return sw.toCharArray();
    }

    public static String toString(InputStream input) throws IOException {
        StringWriter sw = new StringWriter();
        IOUtils.copy(input, (Writer)sw);
        return sw.toString();
    }

    public static String toString(InputStream input, String encoding) throws IOException {
        StringWriter sw = new StringWriter();
        IOUtils.copy(input, sw, encoding);
        return sw.toString();
    }

    public static String toString(Reader input) throws IOException {
        StringWriter sw = new StringWriter();
        IOUtils.copy(input, (Writer)sw);
        return sw.toString();
    }

    @Deprecated
    public static String toString(byte[] input) throws IOException {
        return new String(input, UTF_8);
    }

    @Deprecated
    public static String toString(byte[] input, String encoding) throws IOException {
        if (encoding == null) {
            return new String(input, UTF_8);
        }
        return new String(input, encoding);
    }

    public static List<String> readLines(InputStream input) throws IOException {
        InputStreamReader reader = new InputStreamReader(input, UTF_8);
        return IOUtils.readLines(reader);
    }

    public static List<String> readLines(InputStream input, String encoding) throws IOException {
        if (encoding == null) {
            return IOUtils.readLines(input);
        }
        InputStreamReader reader = new InputStreamReader(input, encoding);
        return IOUtils.readLines(reader);
    }

    public static List<String> readLines(Reader input) throws IOException {
        BufferedReader reader = new BufferedReader(input);
        ArrayList<String> list = new ArrayList<String>();
        String line = reader.readLine();
        while (line != null) {
            list.add(line);
            line = reader.readLine();
        }
        return list;
    }

    public static InputStream toInputStream(CharSequence input) {
        return IOUtils.toInputStream(input.toString());
    }

    public static InputStream toInputStream(CharSequence input, String encoding) throws IOException {
        return IOUtils.toInputStream(input.toString(), encoding);
    }

    public static InputStream toInputStream(String input) {
        byte[] bytes = input.getBytes(UTF_8);
        return new ByteArrayInputStream(bytes);
    }

    public static InputStream toInputStream(String input, String encoding) throws IOException {
        byte[] bytes = encoding != null ? input.getBytes(encoding) : input.getBytes(UTF_8);
        return new ByteArrayInputStream(bytes);
    }

    public static void write(byte[] data, OutputStream output) throws IOException {
        if (data != null) {
            output.write(data);
        }
    }

    public static void write(byte[] data, Writer output) throws IOException {
        if (data != null) {
            output.write(new String(data, UTF_8));
        }
    }

    public static void write(byte[] data, Writer output, String encoding) throws IOException {
        if (data != null) {
            if (encoding == null) {
                IOUtils.write(data, output);
            } else {
                output.write(new String(data, encoding));
            }
        }
    }

    public static void write(char[] data, Writer output) throws IOException {
        if (data != null) {
            output.write(data);
        }
    }

    public static void write(char[] data, OutputStream output) throws IOException {
        if (data != null) {
            output.write(new String(data).getBytes(UTF_8));
        }
    }

    public static void write(char[] data, OutputStream output, String encoding) throws IOException {
        if (data != null) {
            if (encoding == null) {
                IOUtils.write(data, output);
            } else {
                output.write(new String(data).getBytes(encoding));
            }
        }
    }

    public static void write(CharSequence data, Writer output) throws IOException {
        if (data != null) {
            IOUtils.write(data.toString(), output);
        }
    }

    public static void write(CharSequence data, OutputStream output) throws IOException {
        if (data != null) {
            IOUtils.write(data.toString(), output);
        }
    }

    public static void write(CharSequence data, OutputStream output, String encoding) throws IOException {
        if (data != null) {
            IOUtils.write(data.toString(), output, encoding);
        }
    }

    public static void write(String data, Writer output) throws IOException {
        if (data != null) {
            output.write(data);
        }
    }

    public static void write(String data, OutputStream output) throws IOException {
        if (data != null) {
            output.write(data.getBytes(UTF_8));
        }
    }

    public static void write(String data, OutputStream output, String encoding) throws IOException {
        if (data != null) {
            if (encoding == null) {
                IOUtils.write(data, output);
            } else {
                output.write(data.getBytes(encoding));
            }
        }
    }

    @Deprecated
    public static void write(StringBuffer data, Writer output) throws IOException {
        if (data != null) {
            output.write(data.toString());
        }
    }

    @Deprecated
    public static void write(StringBuffer data, OutputStream output) throws IOException {
        if (data != null) {
            output.write(data.toString().getBytes(UTF_8));
        }
    }

    @Deprecated
    public static void write(StringBuffer data, OutputStream output, String encoding) throws IOException {
        if (data != null) {
            if (encoding == null) {
                IOUtils.write(data, output);
            } else {
                output.write(data.toString().getBytes(encoding));
            }
        }
    }

    public static int copy(InputStream input, OutputStream output) throws IOException {
        long count = IOUtils.copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int)count;
    }

    public static long copyLarge(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[4096];
        long count = 0L;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += (long)n;
        }
        return count;
    }

    public static void copy(InputStream input, Writer output) throws IOException {
        InputStreamReader in = new InputStreamReader(input, UTF_8);
        IOUtils.copy((Reader)in, output);
    }

    public static void copy(InputStream input, Writer output, String encoding) throws IOException {
        if (encoding == null) {
            IOUtils.copy(input, output);
        } else {
            InputStreamReader in = new InputStreamReader(input, encoding);
            IOUtils.copy((Reader)in, output);
        }
    }

    public static int copy(Reader input, Writer output) throws IOException {
        long count = IOUtils.copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int)count;
    }

    public static long copyLarge(Reader input, Writer output) throws IOException {
        char[] buffer = new char[4096];
        long count = 0L;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += (long)n;
        }
        return count;
    }

    public static void copy(Reader input, OutputStream output) throws IOException {
        OutputStreamWriter out = new OutputStreamWriter(output, UTF_8);
        IOUtils.copy(input, (Writer)out);
        out.flush();
    }

    public static void copy(Reader input, OutputStream output, String encoding) throws IOException {
        if (encoding == null) {
            IOUtils.copy(input, output);
        } else {
            OutputStreamWriter out = new OutputStreamWriter(output, encoding);
            IOUtils.copy(input, (Writer)out);
            out.flush();
        }
    }

    public static boolean contentEquals(InputStream input1, InputStream input2) throws IOException {
        int ch2;
        if (!(input1 instanceof BufferedInputStream)) {
            input1 = new BufferedInputStream(input1);
        }
        if (!(input2 instanceof BufferedInputStream)) {
            input2 = new BufferedInputStream(input2);
        }
        int ch = input1.read();
        while (-1 != ch) {
            ch2 = input2.read();
            if (ch != ch2) {
                return false;
            }
            ch = input1.read();
        }
        ch2 = input2.read();
        return ch2 == -1;
    }

    public static boolean contentEquals(Reader input1, Reader input2) throws IOException {
        int ch2;
        if (!(input1 instanceof BufferedReader)) {
            input1 = new BufferedReader(input1);
        }
        if (!(input2 instanceof BufferedReader)) {
            input2 = new BufferedReader(input2);
        }
        int ch = input1.read();
        while (-1 != ch) {
            ch2 = input2.read();
            if (ch != ch2) {
                return false;
            }
            ch = input1.read();
        }
        ch2 = input2.read();
        return ch2 == -1;
    }

    public static int read(InputStream input, byte[] buffer, int offset, int length) throws IOException {
        int location;
        int remaining;
        int count;
        if (length < 0) {
            throw new IllegalArgumentException("Length must not be negative: " + length);
        }
        for (remaining = length; remaining > 0 && (count = input.read(buffer, offset + (location = length - remaining), remaining)) != -1; remaining -= count) {
        }
        return length - remaining;
    }

    public static long skip(InputStream input, long toSkip) throws IOException {
        long remain;
        long n;
        if (toSkip < 0L) {
            throw new IllegalArgumentException("Skip count must be non-negative, actual: " + toSkip);
        }
        if (SKIP_BYTE_BUFFER == null) {
            SKIP_BYTE_BUFFER = new byte[2048];
        }
        for (remain = toSkip; remain > 0L && (n = (long)input.read(SKIP_BYTE_BUFFER, 0, (int)Math.min(remain, 2048L))) >= 0L; remain -= n) {
        }
        return toSkip - remain;
    }

    public static long skip(InputStream input, long toSkip, byte[] buffer) throws IOException {
        long remain;
        long n;
        if (toSkip < 0L) {
            throw new IllegalArgumentException("Skip count must be non-negative, actual: " + toSkip);
        }
        for (remain = toSkip; remain > 0L && (n = (long)input.read(buffer, 0, (int)Math.min(remain, (long)buffer.length))) >= 0L; remain -= n) {
        }
        return toSkip - remain;
    }
}

