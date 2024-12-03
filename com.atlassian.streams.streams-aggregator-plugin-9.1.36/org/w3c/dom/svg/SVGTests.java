/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.svg.SVGStringList;

public interface SVGTests {
    public SVGStringList getRequiredFeatures();

    public SVGStringList getRequiredExtensions();

    public SVGStringList getSystemLanguage();

    public boolean hasExtension(String var1);
}

