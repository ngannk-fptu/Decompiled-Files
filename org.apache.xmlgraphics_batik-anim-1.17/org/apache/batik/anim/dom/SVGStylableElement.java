/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.css.dom.CSSOMSVGColor
 *  org.apache.batik.css.dom.CSSOMSVGColor$AbstractModificationHandler
 *  org.apache.batik.css.dom.CSSOMSVGColor$ModificationHandler
 *  org.apache.batik.css.dom.CSSOMSVGColor$ValueProvider
 *  org.apache.batik.css.dom.CSSOMSVGPaint
 *  org.apache.batik.css.dom.CSSOMSVGPaint$AbstractModificationHandler
 *  org.apache.batik.css.dom.CSSOMStoredStyleDeclaration
 *  org.apache.batik.css.dom.CSSOMValue
 *  org.apache.batik.css.dom.CSSOMValue$AbstractModificationHandler
 *  org.apache.batik.css.dom.CSSOMValue$ModificationHandler
 *  org.apache.batik.css.dom.CSSOMValue$ValueProvider
 *  org.apache.batik.css.engine.CSSEngine
 *  org.apache.batik.css.engine.CSSEngine$MainPropertyReceiver
 *  org.apache.batik.css.engine.CSSStylableElement
 *  org.apache.batik.css.engine.StyleDeclaration
 *  org.apache.batik.css.engine.StyleDeclarationProvider
 *  org.apache.batik.css.engine.StyleMap
 *  org.apache.batik.css.engine.value.Value
 *  org.apache.batik.css.engine.value.svg.SVGColorManager
 *  org.apache.batik.css.engine.value.svg.SVGPaintManager
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.dom.svg.LiveAttributeValue
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.apache.batik.util.ParsedURL
 *  org.w3c.dom.svg.SVGAnimatedString
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.AnimationTargetListener;
import org.apache.batik.anim.dom.SVGAnimationTargetContext;
import org.apache.batik.anim.dom.SVGOMAnimatedString;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.anim.dom.TraitInformation;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.css.dom.CSSOMSVGColor;
import org.apache.batik.css.dom.CSSOMSVGPaint;
import org.apache.batik.css.dom.CSSOMStoredStyleDeclaration;
import org.apache.batik.css.dom.CSSOMValue;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.StyleDeclarationProvider;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.svg.SVGColorManager;
import org.apache.batik.css.engine.value.svg.SVGPaintManager;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.svg.LiveAttributeValue;
import org.apache.batik.util.DoublyIndexedTable;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.svg.SVGAnimatedString;

public abstract class SVGStylableElement
extends SVGOMElement
implements CSSStylableElement {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected StyleMap computedStyleMap;
    protected OverrideStyleDeclaration overrideStyleDeclaration;
    protected SVGOMAnimatedString className;
    protected StyleDeclaration style;

    protected SVGStylableElement() {
    }

    protected SVGStylableElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }

    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }

    private void initializeLiveAttributes() {
        this.className = this.createLiveAnimatedString(null, "class");
    }

    public CSSStyleDeclaration getOverrideStyle() {
        if (this.overrideStyleDeclaration == null) {
            CSSEngine eng = ((SVGOMDocument)((Object)this.getOwnerDocument())).getCSSEngine();
            this.overrideStyleDeclaration = new OverrideStyleDeclaration(eng);
        }
        return this.overrideStyleDeclaration;
    }

    public StyleMap getComputedStyleMap(String pseudoElement) {
        return this.computedStyleMap;
    }

    public void setComputedStyleMap(String pseudoElement, StyleMap sm) {
        this.computedStyleMap = sm;
    }

    public String getXMLId() {
        return this.getAttributeNS(null, "id");
    }

    public String getCSSClass() {
        return this.getAttributeNS(null, "class");
    }

    public ParsedURL getCSSBase() {
        if (this.getXblBoundElement() != null) {
            return null;
        }
        String bu = this.getBaseURI();
        return bu == null ? null : new ParsedURL(bu);
    }

    public boolean isPseudoInstanceOf(String pseudoClass) {
        if (pseudoClass.equals("first-child")) {
            Node n;
            for (n = this.getPreviousSibling(); n != null && n.getNodeType() != 1; n = n.getPreviousSibling()) {
            }
            return n == null;
        }
        return false;
    }

    public StyleDeclarationProvider getOverrideStyleDeclarationProvider() {
        return (StyleDeclarationProvider)this.getOverrideStyle();
    }

    @Override
    public void updatePropertyValue(String pn, AnimatableValue val) {
        CSSStyleDeclaration over = this.getOverrideStyle();
        if (val == null) {
            over.removeProperty(pn);
        } else {
            over.setProperty(pn, val.getCssText(), "");
        }
    }

    @Override
    public boolean useLinearRGBColorInterpolation() {
        CSSEngine eng = ((SVGOMDocument)((Object)this.getOwnerDocument())).getCSSEngine();
        Value v = eng.getComputedStyle((CSSStylableElement)this, null, 6);
        return v.getStringValue().charAt(0) == 'l';
    }

    @Override
    public void addTargetListener(String ns, String an, boolean isCSS, AnimationTargetListener l) {
        if (isCSS) {
            if (this.svgContext != null) {
                SVGAnimationTargetContext actx = (SVGAnimationTargetContext)this.svgContext;
                actx.addTargetListener(an, l);
            }
        } else {
            super.addTargetListener(ns, an, isCSS, l);
        }
    }

    @Override
    public void removeTargetListener(String ns, String an, boolean isCSS, AnimationTargetListener l) {
        if (isCSS) {
            if (this.svgContext != null) {
                SVGAnimationTargetContext actx = (SVGAnimationTargetContext)this.svgContext;
                actx.removeTargetListener(an, l);
            }
        } else {
            super.removeTargetListener(ns, an, isCSS, l);
        }
    }

    public CSSStyleDeclaration getStyle() {
        if (this.style == null) {
            CSSEngine eng = ((SVGOMDocument)((Object)this.getOwnerDocument())).getCSSEngine();
            this.style = new StyleDeclaration(eng);
            this.putLiveAttributeValue(null, "style", this.style);
        }
        return this.style;
    }

    public CSSValue getPresentationAttribute(String name) {
        Object result = (CSSValue)this.getLiveAttributeValue(null, name);
        if (result != null) {
            return result;
        }
        CSSEngine eng = ((SVGOMDocument)((Object)this.getOwnerDocument())).getCSSEngine();
        int idx = eng.getPropertyIndex(name);
        if (idx == -1) {
            return null;
        }
        if (idx > 59) {
            if (eng.getValueManagers()[idx] instanceof SVGPaintManager) {
                result = new PresentationAttributePaintValue(eng, name);
            }
            if (eng.getValueManagers()[idx] instanceof SVGColorManager) {
                result = new PresentationAttributeColorValue(eng, name);
            }
        } else {
            switch (idx) {
                case 15: 
                case 45: {
                    result = new PresentationAttributePaintValue(eng, name);
                    break;
                }
                case 19: 
                case 33: 
                case 43: {
                    result = new PresentationAttributeColorValue(eng, name);
                    break;
                }
                default: {
                    result = new PresentationAttributeValue(eng, name);
                }
            }
        }
        this.putLiveAttributeValue(null, name, (LiveAttributeValue)result);
        if (this.getAttributeNS(null, name).length() == 0) {
            return null;
        }
        return result;
    }

    public SVGAnimatedString getClassName() {
        return this.className;
    }

    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
    }

    static {
        DoublyIndexedTable t = new DoublyIndexedTable(SVGOMElement.xmlTraitInformation);
        t.put(null, (Object)"class", (Object)new TraitInformation(true, 16));
        xmlTraitInformation = t;
    }

    protected class OverrideStyleDeclaration
    extends CSSOMStoredStyleDeclaration {
        protected OverrideStyleDeclaration(CSSEngine eng) {
            super(eng);
            this.declaration = new org.apache.batik.css.engine.StyleDeclaration();
        }

        public void textChanged(String text) throws DOMException {
            ((SVGOMDocument)SVGStylableElement.this.ownerDocument).overrideStyleTextChanged(SVGStylableElement.this, text);
        }

        public void propertyRemoved(String name) throws DOMException {
            ((SVGOMDocument)SVGStylableElement.this.ownerDocument).overrideStylePropertyRemoved(SVGStylableElement.this, name);
        }

        public void propertyChanged(String name, String value, String prio) throws DOMException {
            ((SVGOMDocument)SVGStylableElement.this.ownerDocument).overrideStylePropertyChanged(SVGStylableElement.this, name, value, prio);
        }
    }

    public class StyleDeclaration
    extends CSSOMStoredStyleDeclaration
    implements LiveAttributeValue,
    CSSEngine.MainPropertyReceiver {
        protected boolean mutate;

        public StyleDeclaration(CSSEngine eng) {
            super(eng);
            this.declaration = this.cssEngine.parseStyleDeclaration((CSSStylableElement)SVGStylableElement.this, SVGStylableElement.this.getAttributeNS(null, "style"));
        }

        public void attrAdded(Attr node, String newv) {
            if (!this.mutate) {
                this.declaration = this.cssEngine.parseStyleDeclaration((CSSStylableElement)SVGStylableElement.this, newv);
            }
        }

        public void attrModified(Attr node, String oldv, String newv) {
            if (!this.mutate) {
                this.declaration = this.cssEngine.parseStyleDeclaration((CSSStylableElement)SVGStylableElement.this, newv);
            }
        }

        public void attrRemoved(Attr node, String oldv) {
            if (!this.mutate) {
                this.declaration = new org.apache.batik.css.engine.StyleDeclaration();
            }
        }

        public void textChanged(String text) throws DOMException {
            this.declaration = this.cssEngine.parseStyleDeclaration((CSSStylableElement)SVGStylableElement.this, text);
            this.mutate = true;
            SVGStylableElement.this.setAttributeNS(null, "style", text);
            this.mutate = false;
        }

        public void propertyRemoved(String name) throws DOMException {
            int idx = this.cssEngine.getPropertyIndex(name);
            for (int i = 0; i < this.declaration.size(); ++i) {
                if (idx != this.declaration.getIndex(i)) continue;
                this.declaration.remove(i);
                this.mutate = true;
                SVGStylableElement.this.setAttributeNS(null, "style", this.declaration.toString(this.cssEngine));
                this.mutate = false;
                return;
            }
        }

        public void propertyChanged(String name, String value, String prio) throws DOMException {
            boolean important = prio != null && prio.length() > 0;
            this.cssEngine.setMainProperties((CSSStylableElement)SVGStylableElement.this, (CSSEngine.MainPropertyReceiver)this, name, value, important);
            this.mutate = true;
            SVGStylableElement.this.setAttributeNS(null, "style", this.declaration.toString(this.cssEngine));
            this.mutate = false;
        }

        public void setMainProperty(String name, Value v, boolean important) {
            int i;
            int idx = this.cssEngine.getPropertyIndex(name);
            if (idx == -1) {
                return;
            }
            for (i = 0; i < this.declaration.size() && idx != this.declaration.getIndex(i); ++i) {
            }
            if (i < this.declaration.size()) {
                this.declaration.put(i, v, idx, important);
            } else {
                this.declaration.append(v, idx, important);
            }
        }
    }

    public class PresentationAttributePaintValue
    extends CSSOMSVGPaint
    implements LiveAttributeValue,
    CSSOMSVGColor.ValueProvider {
        protected CSSEngine cssEngine;
        protected String property;
        protected Value value;
        protected boolean mutate;

        public PresentationAttributePaintValue(CSSEngine eng, String prop) {
            super(null);
            this.valueProvider = this;
            this.setModificationHandler((CSSOMSVGColor.ModificationHandler)new CSSOMSVGPaint.AbstractModificationHandler(){

                protected Value getValue() {
                    return PresentationAttributePaintValue.this.getValue();
                }

                public void textChanged(String text) throws DOMException {
                    PresentationAttributePaintValue.this.value = PresentationAttributePaintValue.this.cssEngine.parsePropertyValue((CSSStylableElement)SVGStylableElement.this, PresentationAttributePaintValue.this.property, text);
                    PresentationAttributePaintValue.this.mutate = true;
                    SVGStylableElement.this.setAttributeNS(null, PresentationAttributePaintValue.this.property, text);
                    PresentationAttributePaintValue.this.mutate = false;
                }
            });
            this.cssEngine = eng;
            this.property = prop;
            Attr attr = SVGStylableElement.this.getAttributeNodeNS(null, prop);
            if (attr != null) {
                this.value = this.cssEngine.parsePropertyValue((CSSStylableElement)SVGStylableElement.this, prop, attr.getValue());
            }
        }

        public Value getValue() {
            if (this.value == null) {
                throw new DOMException(11, "");
            }
            return this.value;
        }

        public void attrAdded(Attr node, String newv) {
            if (!this.mutate) {
                this.value = this.cssEngine.parsePropertyValue((CSSStylableElement)SVGStylableElement.this, this.property, newv);
            }
        }

        public void attrModified(Attr node, String oldv, String newv) {
            if (!this.mutate) {
                this.value = this.cssEngine.parsePropertyValue((CSSStylableElement)SVGStylableElement.this, this.property, newv);
            }
        }

        public void attrRemoved(Attr node, String oldv) {
            if (!this.mutate) {
                this.value = null;
            }
        }
    }

    public class PresentationAttributeColorValue
    extends CSSOMSVGColor
    implements LiveAttributeValue,
    CSSOMSVGColor.ValueProvider {
        protected CSSEngine cssEngine;
        protected String property;
        protected Value value;
        protected boolean mutate;

        public PresentationAttributeColorValue(CSSEngine eng, String prop) {
            super(null);
            this.valueProvider = this;
            this.setModificationHandler((CSSOMSVGColor.ModificationHandler)new CSSOMSVGColor.AbstractModificationHandler(){

                protected Value getValue() {
                    return PresentationAttributeColorValue.this.getValue();
                }

                public void textChanged(String text) throws DOMException {
                    PresentationAttributeColorValue.this.value = PresentationAttributeColorValue.this.cssEngine.parsePropertyValue((CSSStylableElement)SVGStylableElement.this, PresentationAttributeColorValue.this.property, text);
                    PresentationAttributeColorValue.this.mutate = true;
                    SVGStylableElement.this.setAttributeNS(null, PresentationAttributeColorValue.this.property, text);
                    PresentationAttributeColorValue.this.mutate = false;
                }
            });
            this.cssEngine = eng;
            this.property = prop;
            Attr attr = SVGStylableElement.this.getAttributeNodeNS(null, prop);
            if (attr != null) {
                this.value = this.cssEngine.parsePropertyValue((CSSStylableElement)SVGStylableElement.this, prop, attr.getValue());
            }
        }

        public Value getValue() {
            if (this.value == null) {
                throw new DOMException(11, "");
            }
            return this.value;
        }

        public void attrAdded(Attr node, String newv) {
            if (!this.mutate) {
                this.value = this.cssEngine.parsePropertyValue((CSSStylableElement)SVGStylableElement.this, this.property, newv);
            }
        }

        public void attrModified(Attr node, String oldv, String newv) {
            if (!this.mutate) {
                this.value = this.cssEngine.parsePropertyValue((CSSStylableElement)SVGStylableElement.this, this.property, newv);
            }
        }

        public void attrRemoved(Attr node, String oldv) {
            if (!this.mutate) {
                this.value = null;
            }
        }
    }

    public class PresentationAttributeValue
    extends CSSOMValue
    implements LiveAttributeValue,
    CSSOMValue.ValueProvider {
        protected CSSEngine cssEngine;
        protected String property;
        protected Value value;
        protected boolean mutate;

        public PresentationAttributeValue(CSSEngine eng, String prop) {
            super(null);
            this.valueProvider = this;
            this.setModificationHandler((CSSOMValue.ModificationHandler)new CSSOMValue.AbstractModificationHandler(){

                protected Value getValue() {
                    return PresentationAttributeValue.this.getValue();
                }

                public void textChanged(String text) throws DOMException {
                    PresentationAttributeValue.this.value = PresentationAttributeValue.this.cssEngine.parsePropertyValue((CSSStylableElement)SVGStylableElement.this, PresentationAttributeValue.this.property, text);
                    PresentationAttributeValue.this.mutate = true;
                    SVGStylableElement.this.setAttributeNS(null, PresentationAttributeValue.this.property, text);
                    PresentationAttributeValue.this.mutate = false;
                }
            });
            this.cssEngine = eng;
            this.property = prop;
            Attr attr = SVGStylableElement.this.getAttributeNodeNS(null, prop);
            if (attr != null) {
                this.value = this.cssEngine.parsePropertyValue((CSSStylableElement)SVGStylableElement.this, prop, attr.getValue());
            }
        }

        public Value getValue() {
            if (this.value == null) {
                throw new DOMException(11, "");
            }
            return this.value;
        }

        public void attrAdded(Attr node, String newv) {
            if (!this.mutate) {
                this.value = this.cssEngine.parsePropertyValue((CSSStylableElement)SVGStylableElement.this, this.property, newv);
            }
        }

        public void attrModified(Attr node, String oldv, String newv) {
            if (!this.mutate) {
                this.value = this.cssEngine.parsePropertyValue((CSSStylableElement)SVGStylableElement.this, this.property, newv);
            }
        }

        public void attrRemoved(Attr node, String oldv) {
            if (!this.mutate) {
                this.value = null;
            }
        }
    }
}

