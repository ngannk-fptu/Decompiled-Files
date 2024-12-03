/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom.svg;

import org.apache.batik.dom.svg.SVGItem;

public interface ListHandler {
    public void startList();

    public void item(SVGItem var1);

    public void endList();
}

