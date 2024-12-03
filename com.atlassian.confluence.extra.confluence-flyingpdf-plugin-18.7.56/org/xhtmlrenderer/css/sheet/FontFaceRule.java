/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.sheet;

import org.xhtmlrenderer.css.newmatch.CascadedStyle;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;
import org.xhtmlrenderer.css.sheet.Ruleset;
import org.xhtmlrenderer.css.sheet.RulesetContainer;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.EmptyStyle;
import org.xhtmlrenderer.util.XRRuntimeException;

public class FontFaceRule
implements RulesetContainer {
    private int _origin;
    private Ruleset _ruleset;
    private CalculatedStyle _calculatedStyle;

    public FontFaceRule(int origin) {
        this._origin = origin;
    }

    @Override
    public void addContent(Ruleset ruleset) {
        if (this._ruleset != null) {
            throw new XRRuntimeException("Ruleset can only be set once");
        }
        this._ruleset = ruleset;
    }

    @Override
    public int getOrigin() {
        return this._origin;
    }

    public void setOrigin(int origin) {
        this._origin = origin;
    }

    public CalculatedStyle getCalculatedStyle() {
        if (this._calculatedStyle == null) {
            this._calculatedStyle = new EmptyStyle().deriveStyle(CascadedStyle.createLayoutStyle(this._ruleset.getPropertyDeclarations()));
        }
        return this._calculatedStyle;
    }

    public boolean hasFontFamily() {
        for (PropertyDeclaration decl : this._ruleset.getPropertyDeclarations()) {
            if (!decl.getPropertyName().equals("font-family")) continue;
            return true;
        }
        return false;
    }

    public boolean hasFontWeight() {
        for (PropertyDeclaration decl : this._ruleset.getPropertyDeclarations()) {
            if (!decl.getPropertyName().equals("font-weight")) continue;
            return true;
        }
        return false;
    }

    public boolean hasFontStyle() {
        for (PropertyDeclaration decl : this._ruleset.getPropertyDeclarations()) {
            if (!decl.getPropertyName().equals("font-style")) continue;
            return true;
        }
        return false;
    }
}

