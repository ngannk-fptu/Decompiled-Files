/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.style;

import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.parser.FSColor;
import org.xhtmlrenderer.css.style.CssContext;

public interface FSDerivedValue {
    public boolean isDeclaredInherit();

    public float asFloat();

    public FSColor asColor();

    public float getFloatProportionalTo(CSSName var1, float var2, CssContext var3);

    public String asString();

    public String[] asStringArray();

    public IdentValue asIdentValue();

    public boolean hasAbsoluteUnit();

    public boolean isDependentOnFontSize();

    public boolean isIdent();
}

