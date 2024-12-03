/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.codec.multipart;

import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

abstract class MultipartUtils {
    MultipartUtils() {
    }

    public static Charset charset(HttpHeaders headers) {
        Charset charset;
        MediaType contentType = headers.getContentType();
        if (contentType != null && (charset = contentType.getCharset()) != null) {
            return charset;
        }
        return StandardCharsets.UTF_8;
    }

    public static byte[] concat(byte[] ... byteArrays) {
        int len = 0;
        for (byte[] byteArray : byteArrays) {
            len += byteArray.length;
        }
        byte[] result = new byte[len];
        len = 0;
        for (byte[] byteArray : byteArrays) {
            System.arraycopy(byteArray, 0, result, len, byteArray.length);
            len += byteArray.length;
        }
        return result;
    }

    public static DataBuffer sliceTo(DataBuffer buf, int idx) {
        int pos = buf.readPosition();
        int len = idx - pos + 1;
        return buf.retainedSlice(pos, len);
    }

    public static DataBuffer sliceFrom(DataBuffer buf, int idx) {
        int len = buf.writePosition() - idx - 1;
        return buf.retainedSlice(idx + 1, len);
    }

    public static void closeChannel(Channel channel) {
        try {
            if (channel.isOpen()) {
                channel.close();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public static void deleteFile(Path file) {
        try {
            Files.delete(file);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }
}

