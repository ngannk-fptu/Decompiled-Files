/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.io;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public interface EscapingWriterFactory {
    public Writer createEscapingWriterFor(Writer var1, String var2) throws UnsupportedEncodingException;

    public Writer createEscapingWriterFor(OutputStream var1, String var2) throws UnsupportedEncodingException;
}

