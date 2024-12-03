/*
 * Decompiled with CFR 0.152.
 */
package groovy.io;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

public class EncodingAwareBufferedWriter
extends BufferedWriter {
    private OutputStreamWriter out;

    public EncodingAwareBufferedWriter(OutputStreamWriter out) {
        super(out);
        this.out = out;
    }

    public String getEncoding() {
        return this.out.getEncoding();
    }

    public String getNormalizedEncoding() {
        return Charset.forName(this.getEncoding()).name();
    }
}

