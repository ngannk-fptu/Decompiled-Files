/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.batik.svggen.SVGDescriptor;
import org.apache.batik.svggen.SVGSyntax;
import org.w3c.dom.Element;

public class SVGPaintDescriptor
implements SVGDescriptor,
SVGSyntax {
    private Element def;
    private String paintValue;
    private String opacityValue;

    public SVGPaintDescriptor(String paintValue, String opacityValue) {
        this.paintValue = paintValue;
        this.opacityValue = opacityValue;
    }

    public SVGPaintDescriptor(String paintValue, String opacityValue, Element def) {
        this(paintValue, opacityValue);
        this.def = def;
    }

    public String getPaintValue() {
        return this.paintValue;
    }

    public String getOpacityValue() {
        return this.opacityValue;
    }

    public Element getDef() {
        return this.def;
    }

    @Override
    public Map getAttributeMap(Map attrMap) {
        if (attrMap == null) {
            attrMap = new HashMap<String, String>();
        }
        attrMap.put("fill", this.paintValue);
        attrMap.put("stroke", this.paintValue);
        attrMap.put("fill-opacity", this.opacityValue);
        attrMap.put("stroke-opacity", this.opacityValue);
        return attrMap;
    }

    @Override
    public List getDefinitionSet(List defSet) {
        if (defSet == null) {
            defSet = new LinkedList<Element>();
        }
        if (this.def != null) {
            defSet.add(this.def);
        }
        return defSet;
    }
}

