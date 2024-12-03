/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import org.codehaus.stax2.io.Stax2Source;

public abstract class Stax2BlockSource
extends Stax2Source {
    protected Stax2BlockSource() {
    }

    @Override
    public URL getReference() {
        return null;
    }

    @Override
    public abstract Reader constructReader() throws IOException;

    @Override
    public abstract InputStream constructInputStream() throws IOException;
}

