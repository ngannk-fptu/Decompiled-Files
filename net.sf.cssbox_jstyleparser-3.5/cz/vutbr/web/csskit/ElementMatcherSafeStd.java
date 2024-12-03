/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.csskit.ElementMatcherSafeCS;
import org.w3c.dom.Element;

public class ElementMatcherSafeStd
extends ElementMatcherSafeCS {
    @Override
    public boolean matchesName(Element e, String name) {
        return name.equalsIgnoreCase(this.elementName(e));
    }
}

