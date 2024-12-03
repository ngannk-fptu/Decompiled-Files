/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.css.sac.LexicalUnit
 */
package org.apache.batik.css.engine.value;

import org.apache.batik.css.engine.CSSEngine;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.dom.DOMException;

public interface ShorthandManager {
    public String getPropertyName();

    public boolean isAnimatableProperty();

    public boolean isAdditiveProperty();

    public void setValues(CSSEngine var1, PropertyHandler var2, LexicalUnit var3, boolean var4) throws DOMException;

    public static interface PropertyHandler {
        public void property(String var1, LexicalUnit var2, boolean var3);
    }
}

