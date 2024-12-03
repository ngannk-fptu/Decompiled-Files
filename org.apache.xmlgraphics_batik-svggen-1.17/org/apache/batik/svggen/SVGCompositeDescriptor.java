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

public class SVGCompositeDescriptor
implements SVGDescriptor,
SVGSyntax {
    private Element def;
    private String opacityValue;
    private String filterValue;

    public SVGCompositeDescriptor(String opacityValue, String filterValue) {
        this.opacityValue = opacityValue;
        this.filterValue = filterValue;
    }

    public SVGCompositeDescriptor(String opacityValue, String filterValue, Element def) {
        this(opacityValue, filterValue);
        this.def = def;
    }

    public String getOpacityValue() {
        return this.opacityValue;
    }

    public String getFilterValue() {
        return this.filterValue;
    }

    public Element getDef() {
        return this.def;
    }

    @Override
    public Map getAttributeMap(Map attrMap) {
        if (attrMap == null) {
            attrMap = new HashMap<String, String>();
        }
        attrMap.put("opacity", this.opacityValue);
        attrMap.put("filter", this.filterValue);
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

