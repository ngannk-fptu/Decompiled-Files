/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import org.w3c.tidy.Configuration;
import org.w3c.tidy.StreamIn;
import org.w3c.tidy.StreamInJavaImpl;

public final class StreamInFactory {
    private StreamInFactory() {
    }

    public static StreamIn getStreamIn(Configuration config, InputStream stream) {
        try {
            return new StreamInJavaImpl(stream, config.getInCharEncodingName(), config.tabsize);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unsupported encoding: " + e.getMessage());
        }
    }

    public static StreamIn getStreamIn(Configuration config, Reader reader) {
        return new StreamInJavaImpl(reader, config.tabsize);
    }
}

