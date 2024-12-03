/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.dom;

import org.apache.batik.css.dom.CSSOMSVGStyleDeclaration;
import org.apache.batik.css.dom.CSSOMStyleDeclaration;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.StyleDeclaration;
import org.apache.batik.css.engine.StyleDeclarationProvider;
import org.apache.batik.css.engine.value.Value;

public abstract class CSSOMStoredStyleDeclaration
extends CSSOMSVGStyleDeclaration
implements CSSOMStyleDeclaration.ValueProvider,
CSSOMStyleDeclaration.ModificationHandler,
StyleDeclarationProvider {
    protected StyleDeclaration declaration;

    public CSSOMStoredStyleDeclaration(CSSEngine eng) {
        super(null, null, eng);
        this.valueProvider = this;
        this.setModificationHandler(this);
    }

    @Override
    public StyleDeclaration getStyleDeclaration() {
        return this.declaration;
    }

    @Override
    public void setStyleDeclaration(StyleDeclaration sd) {
        this.declaration = sd;
    }

    @Override
    public Value getValue(String name) {
        int idx = this.cssEngine.getPropertyIndex(name);
        for (int i = 0; i < this.declaration.size(); ++i) {
            if (idx != this.declaration.getIndex(i)) continue;
            return this.declaration.getValue(i);
        }
        return null;
    }

    @Override
    public boolean isImportant(String name) {
        int idx = this.cssEngine.getPropertyIndex(name);
        for (int i = 0; i < this.declaration.size(); ++i) {
            if (idx != this.declaration.getIndex(i)) continue;
            return this.declaration.getPriority(i);
        }
        return false;
    }

    @Override
    public String getText() {
        return this.declaration.toString(this.cssEngine);
    }

    @Override
    public int getLength() {
        return this.declaration.size();
    }

    @Override
    public String item(int idx) {
        return this.cssEngine.getPropertyName(this.declaration.getIndex(idx));
    }
}

