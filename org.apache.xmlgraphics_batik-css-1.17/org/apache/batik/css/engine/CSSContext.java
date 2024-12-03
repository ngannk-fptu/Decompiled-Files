/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.util.ParsedURL
 */
package org.apache.batik.css.engine;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Element;

public interface CSSContext {
    public Value getSystemColor(String var1);

    public Value getDefaultFontFamily();

    public float getLighterFontWeight(float var1);

    public float getBolderFontWeight(float var1);

    public float getPixelUnitToMillimeter();

    public float getPixelToMillimeter();

    public float getMediumFontSize();

    public float getBlockWidth(Element var1);

    public float getBlockHeight(Element var1);

    public void checkLoadExternalResource(ParsedURL var1, ParsedURL var2) throws SecurityException;

    public boolean isDynamic();

    public boolean isInteractive();

    public CSSEngine getCSSEngineForElement(Element var1);
}

