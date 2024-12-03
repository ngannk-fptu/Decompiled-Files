/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.util.ParsedURL
 */
package org.apache.batik.ext.awt.image.spi;

import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.spi.RegistryEntry;
import org.apache.batik.util.ParsedURL;

public interface URLRegistryEntry
extends RegistryEntry {
    public boolean isCompatibleURL(ParsedURL var1);

    public Filter handleURL(ParsedURL var1, boolean var2);
}

