/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import org.codehaus.jackson.io.IOContext;

public abstract class OutputDecorator {
    public abstract OutputStream decorate(IOContext var1, OutputStream var2) throws IOException;

    public abstract Writer decorate(IOContext var1, Writer var2) throws IOException;
}

