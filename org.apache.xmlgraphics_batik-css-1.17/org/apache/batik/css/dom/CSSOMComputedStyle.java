/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.dom;

import java.util.HashMap;
import java.util.Map;
import org.apache.batik.css.dom.CSSOMValue;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.value.Value;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;

public class CSSOMComputedStyle
implements CSSStyleDeclaration {
    protected CSSEngine cssEngine;
    protected CSSStylableElement element;
    protected String pseudoElement;
    protected Map values = new HashMap();

    public CSSOMComputedStyle(CSSEngine e, CSSStylableElement elt, String pseudoElt) {
        this.cssEngine = e;
        this.element = elt;
        this.pseudoElement = pseudoElt;
    }

    @Override
    public String getCssText() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.cssEngine.getNumberOfProperties(); ++i) {
            sb.append(this.cssEngine.getPropertyName(i));
            sb.append(": ");
            sb.append(this.cssEngine.getComputedStyle(this.element, this.pseudoElement, i).getCssText());
            sb.append(";\n");
        }
        return sb.toString();
    }

    @Override
    public void setCssText(String cssText) throws DOMException {
        throw new DOMException(7, "");
    }

    @Override
    public String getPropertyValue(String propertyName) {
        int idx = this.cssEngine.getPropertyIndex(propertyName);
        if (idx == -1) {
            return "";
        }
        Value v = this.cssEngine.getComputedStyle(this.element, this.pseudoElement, idx);
        return v.getCssText();
    }

    @Override
    public CSSValue getPropertyCSSValue(String propertyName) {
        int idx;
        CSSValue result = (CSSValue)this.values.get(propertyName);
        if (result == null && (idx = this.cssEngine.getPropertyIndex(propertyName)) != -1) {
            result = this.createCSSValue(idx);
            this.values.put(propertyName, result);
        }
        return result;
    }

    @Override
    public String removeProperty(String propertyName) throws DOMException {
        throw new DOMException(7, "");
    }

    @Override
    public String getPropertyPriority(String propertyName) {
        return "";
    }

    @Override
    public void setProperty(String propertyName, String value, String prio) throws DOMException {
        throw new DOMException(7, "");
    }

    @Override
    public int getLength() {
        return this.cssEngine.getNumberOfProperties();
    }

    @Override
    public String item(int index) {
        if (index < 0 || index >= this.cssEngine.getNumberOfProperties()) {
            return "";
        }
        return this.cssEngine.getPropertyName(index);
    }

    @Override
    public CSSRule getParentRule() {
        return null;
    }

    protected CSSValue createCSSValue(int idx) {
        return new ComputedCSSValue(idx);
    }

    public class ComputedCSSValue
    extends CSSOMValue
    implements CSSOMValue.ValueProvider {
        protected int index;

        public ComputedCSSValue(int idx) {
            super(null);
            this.valueProvider = this;
            this.index = idx;
        }

        @Override
        public Value getValue() {
            return CSSOMComputedStyle.this.cssEngine.getComputedStyle(CSSOMComputedStyle.this.element, CSSOMComputedStyle.this.pseudoElement, this.index);
        }
    }
}

