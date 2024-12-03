/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.test;

import java.awt.Dimension;
import org.w3c.dom.Element;
import org.xhtmlrenderer.layout.LayoutContext;

public class XLayout {
    public Dimension getIntrinsicDimensions(LayoutContext c, Element elem) {
        return new Dimension(50, 50);
    }
}

