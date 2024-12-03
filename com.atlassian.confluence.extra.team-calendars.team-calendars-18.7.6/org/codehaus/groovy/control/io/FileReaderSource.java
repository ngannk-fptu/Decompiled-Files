/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.Charset;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.io.AbstractReaderSource;

public class FileReaderSource
extends AbstractReaderSource {
    private File file;
    private final Charset UTF8 = Charset.forName("UTF-8");

    public FileReaderSource(File file, CompilerConfiguration configuration) {
        super(configuration);
        this.file = file;
    }

    public File getFile() {
        return this.file;
    }

    @Override
    public Reader getReader() throws IOException {
        Charset cs = Charset.forName(this.configuration.getSourceEncoding());
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(this.file));
        if (this.UTF8.name().equalsIgnoreCase(cs.name())) {
            ((InputStream)in).mark(3);
            boolean hasBOM = true;
            try {
                int i = ((InputStream)in).read();
                hasBOM &= i == 239;
                i = ((InputStream)in).read();
                hasBOM &= i == 187;
                i = ((InputStream)in).read();
                hasBOM &= i == 255;
            }
            catch (IOException ioe) {
                hasBOM = false;
            }
            if (!hasBOM) {
                ((InputStream)in).reset();
            }
        }
        return new InputStreamReader((InputStream)in, cs);
    }

    @Override
    public URI getURI() {
        return this.file.toURI();
    }
}

