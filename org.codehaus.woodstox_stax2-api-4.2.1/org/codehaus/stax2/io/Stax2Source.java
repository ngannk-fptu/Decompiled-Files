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

    @Override
    public String getSystemId() {
        return this.mSystemId;
    }

    @Override
    public void setSystemId(String id) {
        this.mSystemId = id;
    }

    public String getPublicId() {
        return this.mPublicId;
    }

    public void setPublicId(String id) {
        this.mPublicId = id;
    }

    public String getEncoding() {
        return this.mEncoding;
    }

    public void setEncoding(String enc) {
        this.mEncoding = enc;
    }

    public abstract URL getReference();

    public abstract Reader constructReader() throws IOException;

    public abstract InputStream constructInputStream() throws IOException;
}

