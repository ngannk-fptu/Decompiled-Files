/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import org.codehaus.stax2.io.Stax2ReferentialSource;

public class Stax2FileSource
extends Stax2ReferentialSource {
    final File mFile;

    public Stax2FileSource(File file) {
        this.mFile = file;
    }

    public URL getReference() {
        try {
            return this.mFile.toURL();
        }
        catch (MalformedURLException malformedURLException) {
            throw new IllegalArgumentException("(was " + malformedURLException.getClass() + ") Could not convert File '" + this.mFile.getPath() + "' to URL: " + malformedURLException);
        }
    }

    public Reader constructReader() throws IOException {
        String string = this.getEncoding();
        if (string != null && string.length() > 0) {
            return new InputStreamReader(this.constructInputStream(), string);
        }
        return new FileReader(this.mFile);
    }

    public InputStream constructInputStream() throws IOException {
        return new FileInputStream(this.mFile);
    }

    public File getFile() {
        return this.mFile;
    }
}

