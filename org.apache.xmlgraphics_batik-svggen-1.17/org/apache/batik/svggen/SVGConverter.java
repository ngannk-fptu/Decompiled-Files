/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.g2d.GraphicContext
 */
package org.apache.batik.svggen;

import java.util.List;
import org.apache.batik.ext.awt.g2d.GraphicContext;
import org.apache.batik.svggen.SVGDescriptor;
import org.apache.batik.svggen.SVGSyntax;

public interface SVGConverter
extends SVGSyntax {
    public SVGDescriptor toSVG(GraphicContext var1);

    public List getDefinitionSet();
}

