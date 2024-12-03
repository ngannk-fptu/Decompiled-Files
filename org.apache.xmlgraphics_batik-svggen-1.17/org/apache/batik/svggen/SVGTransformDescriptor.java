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

public class SVGTransformDescriptor
implements SVGDescriptor,
SVGSyntax {
    private String transform;

    public SVGTransformDescriptor(String transform) {
        this.transform = transform;
    }

    @Override
    public Map getAttributeMap(Map attrMap) {
        if (attrMap == null) {
            attrMap = new HashMap<String, String>();
        }
        attrMap.put("transform", this.transform);
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

