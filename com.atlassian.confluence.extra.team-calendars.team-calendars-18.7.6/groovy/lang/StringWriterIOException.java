/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import java.io.IOException;

public class StringWriterIOException
extends RuntimeException {
    public StringWriterIOException(IOException e) {
        super(e);
    }

    public IOException getIOException() {
        return (IOException)this.getCause();
    }
}

