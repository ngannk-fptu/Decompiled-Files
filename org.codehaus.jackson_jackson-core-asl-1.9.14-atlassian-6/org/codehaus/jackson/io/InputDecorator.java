/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.codehaus.jackson.io.IOContext;

public abstract class InputDecorator {
    public abstract InputStream decorate(IOContext var1, InputStream var2) throws IOException;

    public abstract InputStream decorate(IOContext var1, byte[] var2, int var3, int var4) throws IOException;

    public abstract Reader decorate(IOContext var1, Reader var2) throws IOException;
}

