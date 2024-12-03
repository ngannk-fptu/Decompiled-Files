/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.io.AbstractReaderSource;

public class InputStreamReaderSource
extends AbstractReaderSource {
    private InputStream stream;

    public InputStreamReaderSource(InputStream stream, CompilerConfiguration configuration) {
        super(configuration);
        this.stream = stream;
    }

    @Override
    public Reader getReader() throws IOException {
        if (this.stream != null) {
            InputStreamReader reader = new InputStreamReader(this.stream, this.configuration.getSourceEncoding());
            this.stream = null;
            return reader;
        }
        return null;
    }

    @Override
    public boolean canReopenSource() {
        return false;
    }

    @Override
    public URI getURI() {
        return null;
    }
}

