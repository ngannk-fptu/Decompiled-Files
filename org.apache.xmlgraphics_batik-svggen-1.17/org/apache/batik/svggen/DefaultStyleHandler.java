/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.util.SVGConstants
 */
package org.apache.batik.svggen;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.StyleHandler;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.Element;

public class DefaultStyleHandler
implements StyleHandler,
SVGConstants {
    static Map ignoreAttributes = new HashMap();

    @Override
    public void setStyle(Element element, Map styleMap, SVGGeneratorContext generatorContext) {
        String tagName = element.getTagName();
        for (Object o : styleMap.keySet()) {
            String styleName = (String)o;
            if (element.getAttributeNS(null, styleName).length() != 0 || !this.appliesTo(styleName, tagName)) continue;
            element.setAttributeNS(null, styleName, (String)styleMap.get(styleName));
        }
    }

    protected boolean appliesTo(String styleName, String tagName) {
        Set s = (Set)ignoreAttributes.get(tagName);
        if (s == null) {
            return true;
        }
        return !s.contains(styleName);
    }

    static {
        HashSet<String> textAttributes = new HashSet<String>();
        textAttributes.add("font-size");
        textAttributes.add("font-family");
        textAttributes.add("font-style");
        textAttributes.add("font-weight");
        ignoreAttributes.put("rect", textAttributes);
        ignoreAttributes.put("circle", textAttributes);
        ignoreAttributes.put("ellipse", textAttributes);
        ignoreAttributes.put("polygon", textAttributes);
        ignoreAttributes.put("polygon", textAttributes);
        ignoreAttributes.put("line", textAttributes);
        ignoreAttributes.put("path", textAttributes);
    }
}

