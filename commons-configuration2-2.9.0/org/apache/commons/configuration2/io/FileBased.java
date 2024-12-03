/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.io;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import org.apache.commons.configuration2.ex.ConfigurationException;

public interface FileBased {
    public void read(Reader var1) throws ConfigurationException, IOException;

    public void write(Writer var1) throws ConfigurationException, IOException;
}

