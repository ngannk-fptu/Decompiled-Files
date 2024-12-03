/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import javax.xml.transform.Source;

public abstract class Stax2Source
implements Source {
    protected String mSystemId;
    protected String mPublicId;
    protected String mEncoding;

    protected Stax2Source() {
    }

    public String getSystemId() {
        return this.mSystemId;
    }

    public void setSystemId(String string) {
        this.mSystemId = string;
    }

    public String getPublicId() {
        return this.mPublicId;
    }

    public void setPublicId(String string) {
        this.mPublicId = string;
    }

    public String getEncoding() {
        return this.mEncoding;
    }

    public void setEncoding(String string) {
        this.mEncoding = string;
    }

    public abstract URL getReference();

    public abstract Reader constructReader() throws IOException;

    public abstract InputStream constructInputStream() throws IOException;
}

