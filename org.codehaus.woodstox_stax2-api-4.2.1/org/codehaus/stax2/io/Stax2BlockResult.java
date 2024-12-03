/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import org.codehaus.stax2.io.Stax2Result;

public abstract class Stax2BlockResult
extends Stax2Result {
    protected Stax2BlockResult() {
    }

    @Override
    public abstract Writer constructWriter() throws IOException;

    @Override
    public abstract OutputStream constructOutputStream() throws IOException;
}

