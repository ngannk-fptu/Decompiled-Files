/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import javax.xml.transform.Result;

public abstract class Stax2Result
implements Result {
    protected String mSystemId;
    protected String mPublicId;
    protected String mEncoding;

    protected Stax2Result() {
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

    public abstract Writer constructWriter() throws IOException;

    public abstract OutputStream constructOutputStream() throws IOException;
}

