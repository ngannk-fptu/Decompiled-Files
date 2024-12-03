/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import org.codehaus.stax2.io.Stax2Source;

public abstract class Stax2ReferentialSource
extends Stax2Source {
    protected Stax2ReferentialSource() {
    }

    @Override
    public abstract URL getReference();

    @Override
    public abstract Reader constructReader() throws IOException;

    @Override
    public abstract InputStream constructInputStream() throws IOException;

    @Override
    public String getSystemId() {
        String sysId = super.getSystemId();
        if (sysId == null) {
            sysId = this.getReference().toExternalForm();
        }
        return sysId;
    }
}

