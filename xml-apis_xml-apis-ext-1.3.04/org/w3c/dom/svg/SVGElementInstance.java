/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.events.EventTarget;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGElementInstanceList;
import org.w3c.dom.svg.SVGUseElement;

public interface SVGElementInstance
extends EventTarget {
    public SVGElement getCorrespondingElement();

    public SVGUseElement getCorrespondingUseElement();

    public SVGElementInstance getParentNode();

    public SVGElementInstanceList getChildNodes();

    public SVGElementInstance getFirstChild();

    public SVGElementInstance getLastChild();

    public SVGElementInstance getPreviousSibling();

    public SVGElementInstance getNextSibling();
}

