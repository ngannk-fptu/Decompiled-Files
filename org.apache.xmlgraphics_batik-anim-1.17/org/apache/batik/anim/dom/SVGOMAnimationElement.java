/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.dom.svg.SVGAnimationContext
 *  org.apache.batik.dom.svg.SVGTestsSupport
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAnimatedBoolean
 *  org.w3c.dom.svg.SVGAnimationElement
 *  org.w3c.dom.svg.SVGElement
 *  org.w3c.dom.svg.SVGStringList
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGOMAnimatedBoolean;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.anim.dom.TraitInformation;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.svg.SVGAnimationContext;
import org.apache.batik.dom.svg.SVGTestsSupport;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimationElement;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGStringList;

public abstract class SVGOMAnimationElement
extends SVGOMElement
implements SVGAnimationElement {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedBoolean externalResourcesRequired;

    protected SVGOMAnimationElement() {
    }

    protected SVGOMAnimationElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }

    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }

    private void initializeLiveAttributes() {
        this.externalResourcesRequired = this.createLiveAnimatedBoolean(null, "externalResourcesRequired", false);
    }

    public SVGElement getTargetElement() {
        return ((SVGAnimationContext)this.getSVGContext()).getTargetElement();
    }

    public float getStartTime() {
        return ((SVGAnimationContext)this.getSVGContext()).getStartTime();
    }

    public float getCurrentTime() {
        return ((SVGAnimationContext)this.getSVGContext()).getCurrentTime();
    }

    public float getSimpleDuration() throws DOMException {
        float dur = ((SVGAnimationContext)this.getSVGContext()).getSimpleDuration();
        if (dur == Float.POSITIVE_INFINITY) {
            throw this.createDOMException((short)9, "animation.dur.indefinite", null);
        }
        return dur;
    }

    public float getHyperlinkBeginTime() {
        return ((SVGAnimationContext)this.getSVGContext()).getHyperlinkBeginTime();
    }

    public boolean beginElement() throws DOMException {
        return ((SVGAnimationContext)this.getSVGContext()).beginElement();
    }

    public boolean beginElementAt(float offset) throws DOMException {
        return ((SVGAnimationContext)this.getSVGContext()).beginElementAt(offset);
    }

    public boolean endElement() throws DOMException {
        return ((SVGAnimationContext)this.getSVGContext()).endElement();
    }

    public boolean endElementAt(float offset) throws DOMException {
        return ((SVGAnimationContext)this.getSVGContext()).endElementAt(offset);
    }

    public SVGAnimatedBoolean getExternalResourcesRequired() {
        return this.externalResourcesRequired;
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

    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
    }

    static {
        DoublyIndexedTable t = new DoublyIndexedTable(SVGOMElement.xmlTraitInformation);
        t.put(null, (Object)"externalResourcesRequired", (Object)new TraitInformation(true, 49));
        xmlTraitInformation = t;
    }
}

