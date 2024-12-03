/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractNode
 *  org.apache.batik.util.SVGConstants
 */
package org.apache.batik.dom.svg;

import org.apache.batik.dom.AbstractNode;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

public class SVGZoomAndPanSupport
implements SVGConstants {
    protected SVGZoomAndPanSupport() {
    }

    public static void setZoomAndPan(Element elt, short val) throws DOMException {
        switch (val) {
            case 1: {
                elt.setAttributeNS(null, "zoomAndPan", "disable");
                break;
            }
            case 2: {
                elt.setAttributeNS(null, "zoomAndPan", "magnify");
                break;
            }
            default: {
                throw ((AbstractNode)elt).createDOMException((short)13, "zoom.and.pan", new Object[]{(int)val});
            }
        }
    }

    public static short getZoomAndPan(Element elt) {
        String s = elt.getAttributeNS(null, "zoomAndPan");
        if (s.equals("magnify")) {
            return 2;
        }
        return 1;
    }
}

