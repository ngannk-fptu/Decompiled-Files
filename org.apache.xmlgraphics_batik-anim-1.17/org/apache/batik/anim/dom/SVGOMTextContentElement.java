/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.dom.svg.SVGTestsSupport
 *  org.apache.batik.dom.util.XMLSupport
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAnimatedBoolean
 *  org.w3c.dom.svg.SVGAnimatedEnumeration
 *  org.w3c.dom.svg.SVGAnimatedLength
 *  org.w3c.dom.svg.SVGLength
 *  org.w3c.dom.svg.SVGPoint
 *  org.w3c.dom.svg.SVGRect
 *  org.w3c.dom.svg.SVGStringList
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.AbstractSVGAnimatedLength;
import org.apache.batik.anim.dom.SVGOMAnimatedBoolean;
import org.apache.batik.anim.dom.SVGOMAnimatedEnumeration;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.anim.dom.SVGStylableElement;
import org.apache.batik.anim.dom.SVGTextContentSupport;
import org.apache.batik.anim.dom.TraitInformation;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.svg.SVGTestsSupport;
import org.apache.batik.dom.util.XMLSupport;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGLength;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGStringList;

public abstract class SVGOMTextContentElement
extends SVGStylableElement {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final String[] LENGTH_ADJUST_VALUES;
    protected SVGOMAnimatedBoolean externalResourcesRequired;
    protected AbstractSVGAnimatedLength textLength;
    protected SVGOMAnimatedEnumeration lengthAdjust;

    protected SVGOMTextContentElement() {
    }

    protected SVGOMTextContentElement(String prefix, AbstractDocument owner) {
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
        this.lengthAdjust = this.createLiveAnimatedEnumeration(null, "lengthAdjust", LENGTH_ADJUST_VALUES, (short)1);
        this.textLength = new AbstractSVGAnimatedLength(this, null, "textLength", 2, true){
            boolean usedDefault;

            @Override
            protected String getDefaultValue() {
                this.usedDefault = true;
                return String.valueOf(SVGOMTextContentElement.this.getComputedTextLength());
            }

            @Override
            public SVGLength getBaseVal() {
                if (this.baseVal == null) {
                    this.baseVal = new SVGTextLength(this.direction);
                }
                return this.baseVal;
            }

            class SVGTextLength
            extends AbstractSVGAnimatedLength.BaseSVGLength {
                public SVGTextLength(short direction) {
                    super(this, direction);
                }

                @Override
                protected void revalidate() {
                    usedDefault = false;
                    super.revalidate();
                    if (usedDefault) {
                        this.valid = false;
                    }
                }
            }
        };
        this.liveAttributeValues.put(null, (Object)"textLength", (Object)this.textLength);
        this.textLength.addAnimatedAttributeListener(((SVGOMDocument)this.ownerDocument).getAnimatedAttributeListener());
    }

    public SVGAnimatedLength getTextLength() {
        return this.textLength;
    }

    public SVGAnimatedEnumeration getLengthAdjust() {
        return this.lengthAdjust;
    }

    public int getNumberOfChars() {
        return SVGTextContentSupport.getNumberOfChars((Element)((Object)this));
    }

    public float getComputedTextLength() {
        return SVGTextContentSupport.getComputedTextLength((Element)((Object)this));
    }

    public float getSubStringLength(int charnum, int nchars) throws DOMException {
        return SVGTextContentSupport.getSubStringLength((Element)((Object)this), charnum, nchars);
    }

    public SVGPoint getStartPositionOfChar(int charnum) throws DOMException {
        return SVGTextContentSupport.getStartPositionOfChar((Element)((Object)this), charnum);
    }

    public SVGPoint getEndPositionOfChar(int charnum) throws DOMException {
        return SVGTextContentSupport.getEndPositionOfChar((Element)((Object)this), charnum);
    }

    public SVGRect getExtentOfChar(int charnum) throws DOMException {
        return SVGTextContentSupport.getExtentOfChar((Element)((Object)this), charnum);
    }

    public float getRotationOfChar(int charnum) throws DOMException {
        return SVGTextContentSupport.getRotationOfChar((Element)((Object)this), charnum);
    }

    public int getCharNumAtPosition(SVGPoint point) {
        return SVGTextContentSupport.getCharNumAtPosition((Element)((Object)this), point.getX(), point.getY());
    }

    public void selectSubString(int charnum, int nchars) throws DOMException {
        SVGTextContentSupport.selectSubString((Element)((Object)this), charnum, nchars);
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

    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
    }

    static {
        DoublyIndexedTable t = new DoublyIndexedTable(SVGStylableElement.xmlTraitInformation);
        t.put(null, (Object)"textLength", (Object)new TraitInformation(true, 3, 3));
        t.put(null, (Object)"lengthAdjust", (Object)new TraitInformation(true, 15));
        t.put(null, (Object)"externalResourcesRequired", (Object)new TraitInformation(true, 49));
        xmlTraitInformation = t;
        LENGTH_ADJUST_VALUES = new String[]{"", "spacing", "spacingAndGlyphs"};
    }
}

