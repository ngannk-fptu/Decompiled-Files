/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.csskit.ElementMatcherSimpleCS;
import org.w3c.dom.Element;

public class ElementMatcherSimpleStd
extends ElementMatcherSimpleCS {
    @Override
    public boolean matchesName(Element e, String name) {
        return name.equalsIgnoreCase(this.elementName(e));
    }
}

