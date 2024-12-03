/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAnimatedLength
 *  org.w3c.dom.svg.SVGRectElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.AbstractSVGAnimatedLength;
import org.apache.batik.anim.dom.AnimatedAttributeListener;
import org.apache.batik.anim.dom.SVGGraphicsElement;
import org.apache.batik.anim.dom.SVGOMAnimatedLength;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.anim.dom.TraitInformation;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGRectElement;

public class SVGOMRectElement
extends SVGGraphicsElement
implements SVGRectElement {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedLength x;
    protected SVGOMAnimatedLength y;
    protected AbstractSVGAnimatedLength rx;
    protected AbstractSVGAnimatedLength ry;
    protected SVGOMAnimatedLength width;
    protected SVGOMAnimatedLength height;

    protected SVGOMRectElement() {
    }

    public SVGOMRectElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }

    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }

    private void initializeLiveAttributes() {
        this.x = this.createLiveAnimatedLength(null, "x", "0", (short)2, false);
        this.y = this.createLiveAnimatedLength(null, "y", "0", (short)1, false);
        this.width = this.createLiveAnimatedLength(null, "width", null, (short)2, true);
        this.height = this.createLiveAnimatedLength(null, "height", null, (short)1, true);
        this.rx = new AbstractSVGAnimatedLength(this, null, "rx", 2, true){

            @Override
            protected String getDefaultValue() {
                Attr attr = SVGOMRectElement.this.getAttributeNodeNS(null, "ry");
                if (attr == null) {
                    return "0";
                }
                return attr.getValue();
            }

            @Override
            protected void attrChanged() {
                super.attrChanged();
                AbstractSVGAnimatedLength ry = (AbstractSVGAnimatedLength)SVGOMRectElement.this.getRy();
                if (this.isSpecified() && !ry.isSpecified()) {
                    ry.attrChanged();
                }
            }
        };
        this.ry = new AbstractSVGAnimatedLength(this, null, "ry", 1, true){

            @Override
            protected String getDefaultValue() {
                Attr attr = SVGOMRectElement.this.getAttributeNodeNS(null, "rx");
                if (attr == null) {
                    return "0";
                }
                return attr.getValue();
            }

            @Override
            protected void attrChanged() {
                super.attrChanged();
                AbstractSVGAnimatedLength rx = (AbstractSVGAnimatedLength)SVGOMRectElement.this.getRx();
                if (this.isSpecified() && !rx.isSpecified()) {
                    rx.attrChanged();
                }
            }
        };
        this.liveAttributeValues.put(null, (Object)"rx", (Object)this.rx);
        this.liveAttributeValues.put(null, (Object)"ry", (Object)this.ry);
        AnimatedAttributeListener l = ((SVGOMDocument)this.ownerDocument).getAnimatedAttributeListener();
        this.rx.addAnimatedAttributeListener(l);
        this.ry.addAnimatedAttributeListener(l);
    }

    public String getLocalName() {
        return "rect";
    }

    public SVGAnimatedLength getX() {
        return this.x;
    }

    public SVGAnimatedLength getY() {
        return this.y;
    }

    public SVGAnimatedLength getWidth() {
        return this.width;
    }

    public SVGAnimatedLength getHeight() {
        return this.height;
    }

    public SVGAnimatedLength getRx() {
        return this.rx;
    }

    public SVGAnimatedLength getRy() {
        return this.ry;
    }

    protected Node newNode() {
        return new SVGOMRectElement();
    }

    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
    }

    @Override
    public void updateAttributeValue(String ns, String ln, AnimatableValue val) {
        if (ns == null) {
            if (ln.equals("rx")) {
                super.updateAttributeValue(ns, ln, val);
                AbstractSVGAnimatedLength ry = (AbstractSVGAnimatedLength)this.getRy();
                if (!ry.isSpecified()) {
                    super.updateAttributeValue(ns, "ry", val);
                }
                return;
            }
            if (ln.equals("ry")) {
                super.updateAttributeValue(ns, ln, val);
                AbstractSVGAnimatedLength rx = (AbstractSVGAnimatedLength)this.getRx();
                if (!rx.isSpecified()) {
                    super.updateAttributeValue(ns, "rx", val);
                }
                return;
            }
        }
        super.updateAttributeValue(ns, ln, val);
    }

    static {
        DoublyIndexedTable t = new DoublyIndexedTable(SVGGraphicsElement.xmlTraitInformation);
        t.put(null, (Object)"x", (Object)new TraitInformation(true, 3, 1));
        t.put(null, (Object)"y", (Object)new TraitInformation(true, 3, 2));
        t.put(null, (Object)"rx", (Object)new TraitInformation(true, 3, 1));
        t.put(null, (Object)"ry", (Object)new TraitInformation(true, 3, 2));
        t.put(null, (Object)"width", (Object)new TraitInformation(true, 3, 1));
        t.put(null, (Object)"height", (Object)new TraitInformation(true, 3, 2));
        xmlTraitInformation = t;
    }
}

