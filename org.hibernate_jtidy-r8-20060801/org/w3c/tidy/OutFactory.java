/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import org.w3c.tidy.Configuration;
import org.w3c.tidy.Out;
import org.w3c.tidy.OutJavaImpl;

public final class OutFactory {
    private OutFactory() {
    }

    public static Out getOut(Configuration config, OutputStream stream) {
        try {
            return new OutJavaImpl(config, config.getOutCharEncodingName(), stream);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unsupported encoding: " + e.getMessage());
        }
    }

    public static Out getOut(Configuration config, Writer writer) {
        return new OutJavaImpl(config, writer);
    }
}

