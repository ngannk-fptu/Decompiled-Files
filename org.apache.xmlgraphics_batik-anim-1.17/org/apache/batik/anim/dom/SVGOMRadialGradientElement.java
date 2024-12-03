/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAnimatedLength
 *  org.w3c.dom.svg.SVGRadialGradientElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.AbstractSVGAnimatedLength;
import org.apache.batik.anim.dom.AnimatedAttributeListener;
import org.apache.batik.anim.dom.SVGOMAnimatedLength;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.anim.dom.SVGOMGradientElement;
import org.apache.batik.anim.dom.TraitInformation;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGRadialGradientElement;

public class SVGOMRadialGradientElement
extends SVGOMGradientElement
implements SVGRadialGradientElement {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedLength cx;
    protected SVGOMAnimatedLength cy;
    protected AbstractSVGAnimatedLength fx;
    protected AbstractSVGAnimatedLength fy;
    protected SVGOMAnimatedLength r;

    protected SVGOMRadialGradientElement() {
    }

    public SVGOMRadialGradientElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }

    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }

    private void initializeLiveAttributes() {
        this.cx = this.createLiveAnimatedLength(null, "cx", "50%", (short)2, false);
        this.cy = this.createLiveAnimatedLength(null, "cy", "50%", (short)1, false);
        this.r = this.createLiveAnimatedLength(null, "r", "50%", (short)0, false);
        this.fx = new AbstractSVGAnimatedLength(this, null, "fx", 2, false){

            @Override
            protected String getDefaultValue() {
                Attr attr = SVGOMRadialGradientElement.this.getAttributeNodeNS(null, "cx");
                if (attr == null) {
                    return "50%";
                }
                return attr.getValue();
            }
        };
        this.fy = new AbstractSVGAnimatedLength(this, null, "fy", 1, false){

            @Override
            protected String getDefaultValue() {
                Attr attr = SVGOMRadialGradientElement.this.getAttributeNodeNS(null, "cy");
                if (attr == null) {
                    return "50%";
                }
                return attr.getValue();
            }
        };
        this.liveAttributeValues.put(null, (Object)"fx", (Object)this.fx);
        this.liveAttributeValues.put(null, (Object)"fy", (Object)this.fy);
        AnimatedAttributeListener l = ((SVGOMDocument)this.ownerDocument).getAnimatedAttributeListener();
        this.fx.addAnimatedAttributeListener(l);
        this.fy.addAnimatedAttributeListener(l);
    }

    public String getLocalName() {
        return "radialGradient";
    }

    public SVGAnimatedLength getCx() {
        return this.cx;
    }

    public SVGAnimatedLength getCy() {
        return this.cy;
    }

    public SVGAnimatedLength getR() {
        return this.r;
    }

    public SVGAnimatedLength getFx() {
        return this.fx;
    }

    public SVGAnimatedLength getFy() {
        return this.fy;
    }

    @Override
    protected Node newNode() {
        return new SVGOMRadialGradientElement();
    }

    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
    }

    static {
        DoublyIndexedTable t = new DoublyIndexedTable(SVGOMGradientElement.xmlTraitInformation);
        t.put(null, (Object)"cx", (Object)new TraitInformation(true, 3, 1));
        t.put(null, (Object)"cy", (Object)new TraitInformation(true, 3, 2));
        t.put(null, (Object)"fx", (Object)new TraitInformation(true, 3, 1));
        t.put(null, (Object)"fy", (Object)new TraitInformation(true, 3, 2));
        t.put(null, (Object)"r", (Object)new TraitInformation(true, 3, 3));
        xmlTraitInformation = t;
    }
}

