/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.dom;

import org.apache.batik.css.dom.CSSOMSVGComputedStyle;
import org.apache.batik.css.dom.CSSOMViewCSS;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.CSSStylableElement;
import org.w3c.dom.Element;
import org.w3c.dom.css.CSSStyleDeclaration;

public class CSSOMSVGViewCSS
extends CSSOMViewCSS {
    public CSSOMSVGViewCSS(CSSEngine engine) {
        super(engine);
    }

    @Override
    public CSSStyleDeclaration getComputedStyle(Element elt, String pseudoElt) {
        if (elt instanceof CSSStylableElement) {
            return new CSSOMSVGComputedStyle(this.cssEngine, (CSSStylableElement)elt, pseudoElt);
        }
        return null;
    }
}

