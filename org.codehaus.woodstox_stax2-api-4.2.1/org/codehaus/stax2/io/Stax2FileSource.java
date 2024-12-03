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
    final File _file;

    public Stax2FileSource(File f) {
        this._file = f;
    }

    @Override
    public URL getReference() {
        try {
            return this._file.toURL();
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException("(was " + e.getClass() + ") Could not convert File '" + this._file.getPath() + "' to URL: " + e);
        }
    }

    @Override
    public Reader constructReader() throws IOException {
        String enc = this.getEncoding();
        if (enc != null && enc.length() > 0) {
            return new InputStreamReader(this.constructInputStream(), enc);
        }
        return new FileReader(this._file);
    }

    @Override
    public InputStream constructInputStream() throws IOException {
        return new FileInputStream(this._file);
    }

    public File getFile() {
        return this._file;
    }
}

