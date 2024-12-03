/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGTextContentElement;
import org.w3c.dom.svg.SVGURIReference;

public interface SVGTextPathElement
extends SVGTextContentElement,
SVGURIReference {
    public static final short TEXTPATH_METHODTYPE_UNKNOWN = 0;
    public static final short TEXTPATH_METHODTYPE_ALIGN = 1;
    public static final short TEXTPATH_METHODTYPE_STRETCH = 2;
    public static final short TEXTPATH_SPACINGTYPE_UNKNOWN = 0;
    public static final short TEXTPATH_SPACINGTYPE_AUTO = 1;
    public static final short TEXTPATH_SPACINGTYPE_EXACT = 2;

    public SVGAnimatedLength getStartOffset();

    public SVGAnimatedEnumeration getMethod();

    public SVGAnimatedEnumeration getSpacing();
}

