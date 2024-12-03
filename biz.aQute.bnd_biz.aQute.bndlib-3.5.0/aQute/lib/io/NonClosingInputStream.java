/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class NonClosingInputStream
extends FilterInputStream {
    public NonClosingInputStream(InputStream in) {
        super(Objects.requireNonNull(in));
    }

    @Override
    public void close() throws IOException {
    }
}

