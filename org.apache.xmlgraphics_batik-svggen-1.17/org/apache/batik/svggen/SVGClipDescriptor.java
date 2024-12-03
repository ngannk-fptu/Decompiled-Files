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
import org.w3c.dom.Element;

public class SVGClipDescriptor
implements SVGDescriptor,
SVGSyntax {
    private String clipPathValue;
    private Element clipPathDef;

    public SVGClipDescriptor(String clipPathValue, Element clipPathDef) {
        if (clipPathValue == null) {
            throw new SVGGraphics2DRuntimeException("clipPathValue should not be null");
        }
        this.clipPathValue = clipPathValue;
        this.clipPathDef = clipPathDef;
    }

    @Override
    public Map getAttributeMap(Map attrMap) {
        if (attrMap == null) {
            attrMap = new HashMap<String, String>();
        }
        attrMap.put("clip-path", this.clipPathValue);
        return attrMap;
    }

    @Override
    public List getDefinitionSet(List defSet) {
        if (defSet == null) {
            defSet = new LinkedList<Element>();
        }
        if (this.clipPathDef != null) {
            defSet.add(this.clipPathDef);
        }
        return defSet;
    }
}

