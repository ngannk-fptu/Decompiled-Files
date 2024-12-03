/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.gzip;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class GZipUtils {
    public static InputStream detectCompression(InputStream stream) throws IOException {
        InputStream buffered = stream.markSupported() ? stream : new BufferedInputStream(stream);
        buffered.mark(2);
        int magic = GZipUtils.readUShort(buffered);
        buffered.reset();
        InputStream result = magic == 35615 ? new GZIPInputStream(buffered) : buffered;
        return result;
    }

    private static int readUShort(InputStream in) throws IOException {
        int b = GZipUtils.readUByte(in);
        return GZipUtils.readUByte(in) << 8 | b;
    }

    private static int readUByte(InputStream in) throws IOException {
        int b = in.read();
        if (b == -1) {
            throw new EOFException();
        }
        if (b < -1 || b > 255) {
            throw new IOException(in.getClass().getName() + ".read() returned value out of range -1..255: " + b);
        }
        return b;
    }
}

