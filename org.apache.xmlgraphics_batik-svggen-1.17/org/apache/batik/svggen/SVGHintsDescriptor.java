/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.batik.svggen.SVGDescriptor;
import org.apache.batik.svggen.SVGGraphics2DRuntimeException;
import org.apache.batik.svggen.SVGSyntax;

public class SVGHintsDescriptor
implements SVGDescriptor,
SVGSyntax {
    private String colorInterpolation;
    private String colorRendering;
    private String textRendering;
    private String shapeRendering;
    private String imageRendering;

    public SVGHintsDescriptor(String colorInterpolation, String colorRendering, String textRendering, String shapeRendering, String imageRendering) {
        if (colorInterpolation == null || colorRendering == null || textRendering == null || shapeRendering == null || imageRendering == null) {
            throw new SVGGraphics2DRuntimeException("none of the hints description parameters should be null");
        }
        this.colorInterpolation = colorInterpolation;
        this.colorRendering = colorRendering;
        this.textRendering = textRendering;
        this.shapeRendering = shapeRendering;
        this.imageRendering = imageRendering;
    }

    @Override
    public Map getAttributeMap(Map attrMap) {
        if (attrMap == null) {
            attrMap = new HashMap<String, String>();
        }
        attrMap.put("color-interpolation", this.colorInterpolation);
        attrMap.put("color-rendering", this.colorRendering);
        attrMap.put("text-rendering", this.textRendering);
        attrMap.put("shape-rendering", this.shapeRendering);
        attrMap.put("image-rendering", this.imageRendering);
        return attrMap;
    }

    @Override
    public List getDefinitionSet(List defSet) {
        if (defSet == null) {
            defSet = new LinkedList();
        }
        return defSet;
    }
}

