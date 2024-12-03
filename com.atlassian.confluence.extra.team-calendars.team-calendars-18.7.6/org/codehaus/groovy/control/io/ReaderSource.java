/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control.io;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import org.codehaus.groovy.control.HasCleanup;
import org.codehaus.groovy.control.Janitor;

public interface ReaderSource
extends HasCleanup {
    public Reader getReader() throws IOException;

    public boolean canReopenSource();

    public String getLine(int var1, Janitor var2);

    @Override
    public void cleanup();

    public URI getURI();
}

