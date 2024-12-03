/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.spi;

import java.util.List;

public interface RegistryEntry {
    public List getStandardExtensions();

    public List getMimeTypes();

    public String getFormatName();

    public float getPriority();
}

