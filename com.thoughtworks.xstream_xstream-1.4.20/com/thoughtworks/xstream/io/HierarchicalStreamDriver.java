/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.io;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

public interface HierarchicalStreamDriver {
    public HierarchicalStreamReader createReader(Reader var1);

    public HierarchicalStreamReader createReader(InputStream var1);

    public HierarchicalStreamReader createReader(URL var1);

    public HierarchicalStreamReader createReader(File var1);

    public HierarchicalStreamWriter createWriter(Writer var1);

    public HierarchicalStreamWriter createWriter(OutputStream var1);
}

