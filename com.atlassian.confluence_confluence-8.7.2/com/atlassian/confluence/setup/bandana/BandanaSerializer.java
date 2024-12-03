/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.setup.bandana;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public interface BandanaSerializer {
    public void serialize(Object var1, Writer var2) throws IOException;

    public Object deserialize(Reader var1) throws IOException;
}

