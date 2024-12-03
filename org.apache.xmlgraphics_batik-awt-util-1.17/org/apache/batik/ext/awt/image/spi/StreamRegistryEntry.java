/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.util.ParsedURL
 */
package org.apache.batik.ext.awt.image.spi;

import java.io.InputStream;
import java.io.StreamCorruptedException;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.spi.RegistryEntry;
import org.apache.batik.util.ParsedURL;

public interface StreamRegistryEntry
extends RegistryEntry {
    public int getReadlimit();

    public boolean isCompatibleStream(InputStream var1) throws StreamCorruptedException;

    public Filter handleStream(InputStream var1, ParsedURL var2, boolean var3);
}

