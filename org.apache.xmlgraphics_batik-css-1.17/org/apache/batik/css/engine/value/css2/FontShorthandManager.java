/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.css.sac.LexicalUnit
 */
package org.apache.batik.css.engine.value.css2;

import java.util.HashSet;
import java.util.Set;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.value.AbstractValueFactory;
import org.apache.batik.css.engine.value.IdentifierManager;
import org.apache.batik.css.engine.value.ShorthandManager;
import org.apache.batik.css.engine.value.StringMap;
import org.apache.batik.css.engine.value.ValueManager;
import org.apache.batik.css.engine.value.css2.FontSizeManager;
import org.apache.batik.css.parser.CSSLexicalUnit;
import org.w3c.css.sac.LexicalUnit;

public class FontShorthandManager
extends AbstractValueFactory
implements ShorthandManager {
    static LexicalUnit NORMAL_LU = CSSLexicalUnit.createString((short)35, "normal", null);
    static LexicalUnit BOLD_LU = CSSLexicalUnit.createString((short)35, "bold", null);
    static LexicalUnit MEDIUM_LU = CSSLexicalUnit.createString((short)35, "medium", null);
    static LexicalUnit SZ_10PT_LU = CSSLexicalUnit.createFloat((short)21, 10.0f, null);
    static LexicalUnit SZ_8PT_LU = CSSLexicalUnit.createFloat((short)21, 8.0f, null);
    static LexicalUnit FONT_FAMILY_LU = CSSLexicalUnit.createString((short)35, "Dialog", null);
    protected static final Set values;

    @Override
    public String getPropertyName() {
        return "font";
    }

    @Override
    public boolean isAnimatableProperty() {
        return true;
    }

    @Override
    public boolean isAdditiveProperty() {
        return false;
    }

    public void handleSystemFont(CSSEngine eng, ShorthandManager.PropertyHandler ph, String s, boolean imp) {
        LexicalUnit fontStyle = NORMAL_LU;
        LexicalUnit fontVariant = NORMAL_LU;
        LexicalUnit fontWeight = NORMAL_LU;
        LexicalUnit lineHeight = NORMAL_LU;
        LexicalUnit fontFamily = FONT_FAMILY_LU;
        LexicalUnit fontSize = s.equals("small-caption") ? SZ_8PT_LU : SZ_10PT_LU;
        ph.property("font-family", fontFamily, imp);
        ph.property("font-style", fontStyle, imp);
        ph.property("font-variant", fontVariant, imp);
        ph.property("font-weight", fontWeight, imp);
        ph.property("font-size", fontSize, imp);
        ph.property("line-height", lineHeight, imp);
    }

    @Override
    public void setValues(CSSEngine eng, ShorthandManager.PropertyHandler ph, LexicalUnit lu, boolean imp) {
        String s;
        switch (lu.getLexicalUnitType()) {
            case 12: {
                return;
            }
            case 35: {
                String s2 = lu.getStringValue().toLowerCase();
                if (!values.contains(s2)) break;
                this.handleSystemFont(eng, ph, s2, imp);
                return;
            }
        }
        LexicalUnit fontStyle = null;
        LexicalUnit fontVariant = null;
        LexicalUnit fontWeight = null;
        LexicalUnit fontSize = null;
        LexicalUnit lineHeight = null;
        LexicalUnit fontFamily = null;
        ValueManager[] vMgrs = eng.getValueManagers();
        int fst = eng.getPropertyIndex("font-style");
        int fv = eng.getPropertyIndex("font-variant");
        int fw = eng.getPropertyIndex("font-weight");
        int fsz = eng.getPropertyIndex("font-size");
        int lh = eng.getPropertyIndex("line-height");
        IdentifierManager fstVM = (IdentifierManager)vMgrs[fst];
        IdentifierManager fvVM = (IdentifierManager)vMgrs[fv];
        IdentifierManager fwVM = (IdentifierManager)vMgrs[fw];
        FontSizeManager fszVM = (FontSizeManager)vMgrs[fsz];
        StringMap fstSM = fstVM.getIdentifiers();
        StringMap fvSM = fvVM.getIdentifiers();
        StringMap fwSM = fwVM.getIdentifiers();
        StringMap fszSM = fszVM.getIdentifiers();
        boolean svwDone = false;
        LexicalUnit intLU = null;
        while (!svwDone && lu != null) {
            switch (lu.getLexicalUnitType()) {
                case 35: {
                    s = lu.getStringValue().toLowerCase().intern();
                    if (fontStyle == null && fstSM.get(s) != null) {
                        fontStyle = lu;
                        if (intLU == null) break;
                        if (fontWeight == null) {
                            fontWeight = intLU;
                            intLU = null;
                            break;
                        }
                        throw this.createInvalidLexicalUnitDOMException(intLU.getLexicalUnitType());
                    }
                    if (fontVariant == null && fvSM.get(s) != null) {
                        fontVariant = lu;
                        if (intLU == null) break;
                        if (fontWeight == null) {
                            fontWeight = intLU;
                            intLU = null;
                            break;
                        }
                        throw this.createInvalidLexicalUnitDOMException(intLU.getLexicalUnitType());
                    }
                    if (intLU == null && fontWeight == null && fwSM.get(s) != null) {
                        fontWeight = lu;
                        break;
                    }
                    svwDone = true;
                    break;
                }
                case 13: {
                    if (intLU == null && fontWeight == null) {
                        intLU = lu;
                        break;
                    }
                    svwDone = true;
                    break;
                }
                default: {
                    svwDone = true;
                }
            }
            if (svwDone) continue;
            lu = lu.getNextLexicalUnit();
        }
        if (lu == null) {
            throw this.createMalformedLexicalUnitDOMException();
        }
        switch (lu.getLexicalUnitType()) {
            case 35: {
                s = lu.getStringValue().toLowerCase().intern();
                if (fszSM.get(s) == null) break;
                fontSize = lu;
                lu = lu.getNextLexicalUnit();
                break;
            }
            case 13: 
            case 14: 
            case 15: 
            case 16: 
            case 17: 
            case 18: 
            case 19: 
            case 20: 
            case 21: 
            case 22: 
            case 23: {
                fontSize = lu;
                lu = lu.getNextLexicalUnit();
            }
        }
        if (fontSize == null) {
            if (intLU != null) {
                fontSize = intLU;
                intLU = null;
            } else {
                throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
            }
        }
        if (intLU != null) {
            if (fontWeight == null) {
                fontWeight = intLU;
            } else {
                throw this.createInvalidLexicalUnitDOMException(intLU.getLexicalUnitType());
            }
        }
        if (lu == null) {
            throw this.createMalformedLexicalUnitDOMException();
        }
        switch (lu.getLexicalUnitType()) {
            case 4: {
                lu = lu.getNextLexicalUnit();
                if (lu == null) {
                    throw this.createMalformedLexicalUnitDOMException();
                }
                lineHeight = lu;
                lu = lu.getNextLexicalUnit();
            }
        }
        if (lu == null) {
            throw this.createMalformedLexicalUnitDOMException();
        }
        fontFamily = lu;
        if (fontStyle == null) {
            fontStyle = NORMAL_LU;
        }
        if (fontVariant == null) {
            fontVariant = NORMAL_LU;
        }
        if (fontWeight == null) {
            fontWeight = NORMAL_LU;
        }
        if (lineHeight == null) {
            lineHeight = NORMAL_LU;
        }
        ph.property("font-family", fontFamily, imp);
        ph.property("font-style", fontStyle, imp);
        ph.property("font-variant", fontVariant, imp);
        ph.property("font-weight", fontWeight, imp);
        ph.property("font-size", fontSize, imp);
        if (lh != -1) {
            ph.property("line-height", lineHeight, imp);
        }
    }

    static {
        CSSLexicalUnit lu = CSSLexicalUnit.createString((short)35, "Helvetica", FONT_FAMILY_LU);
        CSSLexicalUnit.createString((short)35, "sans-serif", lu);
        values = new HashSet();
        values.add("caption");
        values.add("icon");
        values.add("menu");
        values.add("message-box");
        values.add("small-caption");
        values.add("status-bar");
    }
}

