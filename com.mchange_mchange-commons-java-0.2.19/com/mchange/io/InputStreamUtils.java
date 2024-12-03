/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.io;

import com.mchange.util.RobustMessageLogger;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public final class InputStreamUtils {
    private static InputStream EMPTY_ISTREAM = new ByteArrayInputStream(new byte[0]);

    public static boolean compare(InputStream inputStream, InputStream inputStream2, long l) throws IOException {
        for (long i = 0L; i < l; ++i) {
            int n = inputStream.read();
            if (n != inputStream2.read()) {
                return false;
            }
            if (n < 0) break;
        }
        return true;
    }

    public static boolean compare(InputStream inputStream, InputStream inputStream2) throws IOException {
        int n = 0;
        while (n >= 0) {
            n = inputStream.read();
            if (n == inputStream2.read()) continue;
            return false;
        }
        return true;
    }

    public static byte[] getBytes(InputStream inputStream, int n) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(n);
        int n2 = inputStream.read();
        for (int i = 0; n2 >= 0 && i < n; ++i) {
            byteArrayOutputStream.write(n2);
            n2 = inputStream.read();
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int n = inputStream.read();
        while (n >= 0) {
            byteArrayOutputStream.write(n);
            n = inputStream.read();
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static String getContentsAsString(InputStream inputStream, String string) throws IOException, UnsupportedEncodingException {
        return new String(InputStreamUtils.getBytes(inputStream), string);
    }

    public static String getContentsAsString(InputStream inputStream) throws IOException {
        try {
            return InputStreamUtils.getContentsAsString(inputStream, System.getProperty("file.encoding", "8859_1"));
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new InternalError("You have no default character encoding, and iso-8859-1 is unsupported?!?!");
        }
    }

    public static String getContentsAsString(InputStream inputStream, int n, String string) throws IOException, UnsupportedEncodingException {
        return new String(InputStreamUtils.getBytes(inputStream, n), string);
    }

    public static String getContentsAsString(InputStream inputStream, int n) throws IOException {
        try {
            return InputStreamUtils.getContentsAsString(inputStream, n, System.getProperty("file.encoding", "8859_1"));
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new InternalError("You have no default character encoding, and iso-8859-1 is unsupported?!?!");
        }
    }

    public static InputStream getEmptyInputStream() {
        return EMPTY_ISTREAM;
    }

    public static void attemptClose(InputStream inputStream) {
        InputStreamUtils.attemptClose(inputStream, null);
    }

    public static void attemptClose(InputStream inputStream, RobustMessageLogger robustMessageLogger) {
        block4: {
            try {
                inputStream.close();
            }
            catch (IOException iOException) {
                if (robustMessageLogger != null) {
                    robustMessageLogger.log(iOException, "IOException trying to close InputStream");
                }
            }
            catch (NullPointerException nullPointerException) {
                if (robustMessageLogger == null) break block4;
                robustMessageLogger.log(nullPointerException, "NullPointerException trying to close InputStream");
            }
        }
    }

    public static void skipFully(InputStream inputStream, long l) throws EOFException, IOException {
        long l2 = 0L;
        while (l2 < l) {
            long l3 = inputStream.skip(l - l2);
            if (l3 > 0L) {
                l2 += l3;
                continue;
            }
            int n = inputStream.read();
            if (inputStream.read() < 0) {
                throw new EOFException("Skipped only " + l2 + " bytes to end of file.");
            }
            ++l2;
        }
    }

    private InputStreamUtils() {
    }
}

