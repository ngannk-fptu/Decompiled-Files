/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGExternalResourcesRequired;
import org.w3c.dom.svg.SVGLangSpace;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGStylable;
import org.w3c.dom.svg.SVGTests;

public interface SVGTextContentElement
extends SVGElement,
SVGTests,
SVGLangSpace,
SVGExternalResourcesRequired,
SVGStylable,
EventTarget {
    public static final short LENGTHADJUST_UNKNOWN = 0;
    public static final short LENGTHADJUST_SPACING = 1;
    public static final short LENGTHADJUST_SPACINGANDGLYPHS = 2;

    public SVGAnimatedLength getTextLength();

    public SVGAnimatedEnumeration getLengthAdjust();

    public int getNumberOfChars();

    public float getComputedTextLength();

    public float getSubStringLength(int var1, int var2) throws DOMException;

    public SVGPoint getStartPositionOfChar(int var1) throws DOMException;

    public SVGPoint getEndPositionOfChar(int var1) throws DOMException;

    public SVGRect getExtentOfChar(int var1) throws DOMException;

    public float getRotationOfChar(int var1) throws DOMException;

    public int getCharNumAtPosition(SVGPoint var1);

    public void selectSubString(int var1, int var2) throws DOMException;
}

