/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xs;

import org.apache.xerces.xs.AttributePSVI;
import org.apache.xerces.xs.ElementPSVI;

public interface PSVIProvider {
    public ElementPSVI getElementPSVI();

    public AttributePSVI getAttributePSVI(int var1);

    public AttributePSVI getAttributePSVIByName(String var1, String var2);
}

