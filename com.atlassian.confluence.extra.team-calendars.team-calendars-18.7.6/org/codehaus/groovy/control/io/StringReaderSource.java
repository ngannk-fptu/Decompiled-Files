/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control.io;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.io.AbstractReaderSource;

public class StringReaderSource
extends AbstractReaderSource {
    private final String string;

    public StringReaderSource(String string, CompilerConfiguration configuration) {
        super(configuration);
        this.string = string;
    }

    @Override
    public Reader getReader() throws IOException {
        return new StringReader(this.string);
    }

    @Override
    public URI getURI() {
        try {
            return new URI("data", "," + this.string, null);
        }
        catch (URISyntaxException e) {
            return null;
        }
    }
}

