/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.css;

import cz.vutbr.web.css.Selector;
import java.util.Collection;
import org.w3c.dom.Element;

public interface ElementMatcher {
    public String getAttribute(Element var1, String var2);

    public Collection<String> elementClasses(Element var1);

    public boolean matchesClass(Element var1, String var2);

    public String elementID(Element var1);

    public boolean matchesID(Element var1, String var2);

    public String elementName(Element var1);

    public boolean matchesName(Element var1, String var2);

    public boolean matchesAttribute(Element var1, String var2, String var3, Selector.Operator var4);
}

