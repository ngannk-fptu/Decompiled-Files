/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.css.engine.CSSEngine
 */
package org.apache.batik.dom;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.ExtensibleDOMImplementation;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.DocumentCSS;
import org.w3c.dom.stylesheets.StyleSheetList;
import org.w3c.dom.views.AbstractView;
import org.w3c.dom.views.DocumentView;

public abstract class AbstractStylableDocument
extends AbstractDocument
implements DocumentCSS,
DocumentView {
    protected transient AbstractView defaultView;
    protected transient CSSEngine cssEngine;

    protected AbstractStylableDocument() {
    }

    protected AbstractStylableDocument(DocumentType dt, DOMImplementation impl) {
        super(dt, impl);
    }

    public void setCSSEngine(CSSEngine ctx) {
        this.cssEngine = ctx;
    }

    public CSSEngine getCSSEngine() {
        return this.cssEngine;
    }

    @Override
    public StyleSheetList getStyleSheets() {
        throw new RuntimeException(" !!! Not implemented");
    }

    @Override
    public AbstractView getDefaultView() {
        if (this.defaultView == null) {
            ExtensibleDOMImplementation impl = (ExtensibleDOMImplementation)this.implementation;
            this.defaultView = impl.createViewCSS(this);
        }
        return this.defaultView;
    }

    public void clearViewCSS() {
        this.defaultView = null;
        if (this.cssEngine != null) {
            this.cssEngine.dispose();
        }
        this.cssEngine = null;
    }

    @Override
    public CSSStyleDeclaration getOverrideStyle(Element elt, String pseudoElt) {
        throw new RuntimeException(" !!! Not implemented");
    }
}

