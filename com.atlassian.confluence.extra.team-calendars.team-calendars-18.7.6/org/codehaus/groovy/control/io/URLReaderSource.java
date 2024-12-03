/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control.io;

import groovy.lang.GroovyRuntimeException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.io.AbstractReaderSource;

public class URLReaderSource
extends AbstractReaderSource {
    private URL url;

    public URLReaderSource(URL url, CompilerConfiguration configuration) {
        super(configuration);
        this.url = url;
    }

    @Override
    public Reader getReader() throws IOException {
        return new InputStreamReader(this.url.openStream(), this.configuration.getSourceEncoding());
    }

    @Override
    public URI getURI() {
        try {
            return this.url.toURI();
        }
        catch (URISyntaxException e) {
            throw new GroovyRuntimeException("Unable to convert the URL <" + this.url + "> to a URI!", e);
        }
    }
}

