/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import org.codehaus.stax2.io.Stax2ReferentialSource;

public class Stax2URLSource
extends Stax2ReferentialSource {
    final URL mURL;

    public Stax2URLSource(URL url) {
        this.mURL = url;
    }

    @Override
    public URL getReference() {
        return this.mURL;
    }

    @Override
    public Reader constructReader() throws IOException {
        String enc = this.getEncoding();
        if (enc != null && enc.length() > 0) {
            return new InputStreamReader(this.constructInputStream(), enc);
        }
        return new InputStreamReader(this.constructInputStream());
    }

    @Override
    public InputStream constructInputStream() throws IOException {
        if ("file".equals(this.mURL.getProtocol())) {
            return new FileInputStream(this.mURL.getPath());
        }
        return this.mURL.openStream();
    }
}

