/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen;

import java.util.HashMap;
import java.util.Map;
import org.apache.batik.svggen.SVGAttribute;

public class SVGAttributeMap {
    private static Map attrMap = new HashMap();

    public static SVGAttribute get(String attrName) {
        return (SVGAttribute)attrMap.get(attrName);
    }
}

