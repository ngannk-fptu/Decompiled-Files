/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine;

import org.apache.batik.css.engine.StyleDeclarationProvider;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Element;

public interface CSSStylableElement
extends Element {
    public StyleMap getComputedStyleMap(String var1);

    public void setComputedStyleMap(String var1, StyleMap var2);

    public String getXMLId();

    public String getCSSClass();

    public ParsedURL getCSSBase();

    public boolean isPseudoInstanceOf(String var1);

    public StyleDeclarationProvider getOverrideStyleDeclarationProvider();
}

