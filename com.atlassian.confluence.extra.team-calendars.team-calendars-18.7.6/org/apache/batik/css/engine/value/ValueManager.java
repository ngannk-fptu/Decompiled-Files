/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.css.sac.LexicalUnit
 */
package org.apache.batik.css.engine.value;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.value.Value;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;

public interface ValueManager {
    public String getPropertyName();

    public boolean isInheritedProperty();

    public boolean isAnimatableProperty();

    public boolean isAdditiveProperty();

    public int getPropertyType();

    public Value getDefaultValue();

    public Value createValue(LexicalUnit var1, CSSEngine var2) throws DOMException;

    public Value createFloatValue(short var1, float var2) throws DOMException;

    public Value createStringValue(short var1, String var2, CSSEngine var3) throws DOMException;

    public Value computeValue(CSSStylableElement var1, String var2, CSSEngine var3, int var4, StyleMap var5, Value var6);
}

