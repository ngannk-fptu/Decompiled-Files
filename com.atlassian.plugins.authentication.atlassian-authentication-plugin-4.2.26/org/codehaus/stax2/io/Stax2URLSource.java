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

    public Stax2URLSource(URL uRL) {
        this.mURL = uRL;
    }

    public URL getReference() {
        return this.mURL;
    }

    public Reader constructReader() throws IOException {
        String string = this.getEncoding();
        if (string != null && string.length() > 0) {
            return new InputStreamReader(this.constructInputStream(), string);
        }
        return new InputStreamReader(this.constructInputStream());
    }

    public InputStream constructInputStream() throws IOException {
        if ("file".equals(this.mURL.getProtocol())) {
            return new FileInputStream(this.mURL.getPath());
        }
        return this.mURL.openStream();
    }
}

