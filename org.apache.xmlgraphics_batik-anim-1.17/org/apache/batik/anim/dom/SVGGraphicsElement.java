/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.dom.svg.SVGMotionAnimatableElement
 *  org.apache.batik.dom.svg.SVGTestsSupport
 *  org.apache.batik.dom.util.XMLSupport
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAnimatedBoolean
 *  org.w3c.dom.svg.SVGAnimatedTransformList
 *  org.w3c.dom.svg.SVGElement
 *  org.w3c.dom.svg.SVGException
 *  org.w3c.dom.svg.SVGMatrix
 *  org.w3c.dom.svg.SVGRect
 *  org.w3c.dom.svg.SVGStringList
 */
package org.apache.batik.anim.dom;

import java.awt.geom.AffineTransform;
import org.apache.batik.anim.dom.SVGLocatableSupport;
import org.apache.batik.anim.dom.SVGOMAnimatedBoolean;
import org.apache.batik.anim.dom.SVGOMAnimatedTransformList;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.anim.dom.SVGStylableElement;
import org.apache.batik.anim.dom.TraitInformation;
import org.apache.batik.anim.values.AnimatableMotionPointValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.svg.SVGMotionAnimatableElement;
import org.apache.batik.dom.svg.SVGTestsSupport;
import org.apache.batik.dom.util.XMLSupport;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedTransformList;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGStringList;

public abstract class SVGGraphicsElement
extends SVGStylableElement
implements SVGMotionAnimatableElement {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedTransformList transform;
    protected SVGOMAnimatedBoolean externalResourcesRequired;
    protected AffineTransform motionTransform;

    protected SVGGraphicsElement() {
    }

    protected SVGGraphicsElement(String prefix, AbstractDocument owner) {
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
        this.externalResourcesRequired = this.createLiveAnimatedBoolean(null, "externalResourcesRequired", false);
    }

    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
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

    public SVGAnimatedBoolean getExternalResourcesRequired() {
        return this.externalResourcesRequired;
    }

    public String getXMLlang() {
        return XMLSupport.getXMLLang((Element)((Object)this));
    }

    public void setXMLlang(String lang) {
        this.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:lang", lang);
    }

    public String getXMLspace() {
        return XMLSupport.getXMLSpace((Element)((Object)this));
    }

    public void setXMLspace(String space) {
        this.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:space", space);
    }

    public SVGStringList getRequiredFeatures() {
        return SVGTestsSupport.getRequiredFeatures((Element)((Object)this));
    }

    public SVGStringList getRequiredExtensions() {
        return SVGTestsSupport.getRequiredExtensions((Element)((Object)this));
    }

    public SVGStringList getSystemLanguage() {
        return SVGTestsSupport.getSystemLanguage((Element)((Object)this));
    }

    public boolean hasExtension(String extension) {
        return SVGTestsSupport.hasExtension((Element)((Object)this), (String)extension);
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
        DoublyIndexedTable t = new DoublyIndexedTable(SVGStylableElement.xmlTraitInformation);
        t.put(null, (Object)"transform", (Object)new TraitInformation(true, 9));
        t.put(null, (Object)"externalResourcesRequired", (Object)new TraitInformation(true, 49));
        xmlTraitInformation = t;
    }
}

