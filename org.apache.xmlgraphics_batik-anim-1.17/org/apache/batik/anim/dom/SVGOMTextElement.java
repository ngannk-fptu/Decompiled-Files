/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.dom.svg.SVGMotionAnimatableElement
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAnimatedTransformList
 *  org.w3c.dom.svg.SVGElement
 *  org.w3c.dom.svg.SVGException
 *  org.w3c.dom.svg.SVGMatrix
 *  org.w3c.dom.svg.SVGRect
 *  org.w3c.dom.svg.SVGTextElement
 */
package org.apache.batik.anim.dom;

import java.awt.geom.AffineTransform;
import org.apache.batik.anim.dom.SVGLocatableSupport;
import org.apache.batik.anim.dom.SVGOMAnimatedTransformList;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.anim.dom.SVGOMTextPositioningElement;
import org.apache.batik.anim.dom.TraitInformation;
import org.apache.batik.anim.values.AnimatableMotionPointValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.svg.SVGMotionAnimatableElement;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedTransformList;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGTextElement;

public class SVGOMTextElement
extends SVGOMTextPositioningElement
implements SVGTextElement,
SVGMotionAnimatableElement {
    protected static final String X_DEFAULT_VALUE = "0";
    protected static final String Y_DEFAULT_VALUE = "0";
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedTransformList transform;
    protected AffineTransform motionTransform;

    protected SVGOMTextElement() {
    }

    public SVGOMTextElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }

    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }

    private void initializeLiveAttributes() {
        this.transform = this.createLiveAnimatedTransformList(null, "transform", "");
    }

    public String getLocalName() {
        return "text";
    }

    public SVGElement getNearestViewportElement() {
        return SVGLocatableSupport.getNearestViewportElement((Element)((Object)this));
    }

    public SVGElement getFarthestViewportElement() {
        return SVGLocatableSupport.getFarthestViewportElement((Element)((Object)this));
    }

    public SVGRect getBBox() {
        return SVGLocatableSupport.getBBox((Element)((Object)this));
    }

    public SVGMatrix getCTM() {
        return SVGLocatableSupport.getCTM((Element)((Object)this));
    }

    public SVGMatrix getScreenCTM() {
        return SVGLocatableSupport.getScreenCTM((Element)((Object)this));
    }

    public SVGMatrix getTransformToElement(SVGElement element) throws SVGException {
        return SVGLocatableSupport.getTransformToElement((Element)((Object)this), element);
    }

    public SVGAnimatedTransformList getTransform() {
        return this.transform;
    }

    @Override
    protected String getDefaultXValue() {
        return "0";
    }

    @Override
    protected String getDefaultYValue() {
        return "0";
    }

    protected Node newNode() {
        return new SVGOMTextElement();
    }

    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
    }

    public AffineTransform getMotionTransform() {
        return this.motionTransform;
    }

    @Override
    public void updateOtherValue(String type, AnimatableValue val) {
        if (type.equals("motion")) {
            if (this.motionTransform == null) {
                this.motionTransform = new AffineTransform();
            }
            if (val == null) {
                this.motionTransform.setToIdentity();
            } else {
                AnimatableMotionPointValue p = (AnimatableMotionPointValue)val;
                this.motionTransform.setToTranslation(p.getX(), p.getY());
                this.motionTransform.rotate(p.getAngle());
            }
            SVGOMDocument d = (SVGOMDocument)this.ownerDocument;
            d.getAnimatedAttributeListener().otherAnimationChanged((Element)((Object)this), type);
        } else {
            super.updateOtherValue(type, val);
        }
    }

    static {
        DoublyIndexedTable t = new DoublyIndexedTable(SVGOMTextPositioningElement.xmlTraitInformation);
        t.put(null, (Object)"transform", (Object)new TraitInformation(true, 9));
        xmlTraitInformation = t;
    }
}

