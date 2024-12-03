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

    public abstract URL getReference();

    public abstract Reader constructReader() throws IOException;

    public abstract InputStream constructInputStream() throws IOException;

    public String getSystemId() {
        String string = super.getSystemId();
        if (string == null) {
            string = this.getReference().toExternalForm();
        }
        return string;
    }
}

