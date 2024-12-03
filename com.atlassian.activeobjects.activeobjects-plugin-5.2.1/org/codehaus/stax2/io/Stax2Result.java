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

    public abstract Writer constructWriter() throws IOException;

    public abstract OutputStream constructOutputStream() throws IOException;
}

