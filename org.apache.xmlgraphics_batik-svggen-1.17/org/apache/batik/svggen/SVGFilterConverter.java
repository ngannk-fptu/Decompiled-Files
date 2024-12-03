/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen;

import java.awt.Rectangle;
import java.awt.image.BufferedImageOp;
import java.util.List;
import org.apache.batik.svggen.SVGFilterDescriptor;
import org.apache.batik.svggen.SVGSyntax;

public interface SVGFilterConverter
extends SVGSyntax {
    public SVGFilterDescriptor toSVG(BufferedImageOp var1, Rectangle var2);

    public List getDefinitionSet();
}

