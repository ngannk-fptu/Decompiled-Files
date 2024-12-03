/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.css.engine.CSSEngine
 *  org.apache.batik.css.engine.FontFaceRule
 *  org.apache.batik.css.engine.StyleMap
 *  org.apache.batik.css.engine.value.Value
 *  org.apache.batik.css.engine.value.ValueConstants
 *  org.apache.batik.css.engine.value.ValueManager
 *  org.apache.batik.gvt.font.GVTFontFamily
 *  org.apache.batik.util.ParsedURL
 *  org.apache.batik.util.SVGConstants
 */
package org.apache.batik.bridge;

import java.util.LinkedList;
import java.util.List;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.FontFace;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.FontFaceRule;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.ValueConstants;
import org.apache.batik.css.engine.value.ValueManager;
import org.apache.batik.gvt.font.GVTFontFamily;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.util.SVGConstants;

public class CSSFontFace
extends FontFace
implements SVGConstants {
    GVTFontFamily fontFamily = null;

    public CSSFontFace(List srcs, String familyName, float unitsPerEm, String fontWeight, String fontStyle, String fontVariant, String fontStretch, float slope, String panose1, float ascent, float descent, float strikethroughPosition, float strikethroughThickness, float underlinePosition, float underlineThickness, float overlinePosition, float overlineThickness) {
        super(srcs, familyName, unitsPerEm, fontWeight, fontStyle, fontVariant, fontStretch, slope, panose1, ascent, descent, strikethroughPosition, strikethroughThickness, underlinePosition, underlineThickness, overlinePosition, overlineThickness);
    }

    protected CSSFontFace(String familyName) {
        super(familyName);
    }

    public static CSSFontFace createCSSFontFace(CSSEngine eng, FontFaceRule ffr) {
        StyleMap sm = ffr.getStyleMap();
        String familyName = CSSFontFace.getStringProp(sm, eng, 21);
        CSSFontFace ret = new CSSFontFace(familyName);
        Value v = sm.getValue(27);
        if (v != null) {
            ret.fontWeight = v.getCssText();
        }
        if ((v = sm.getValue(25)) != null) {
            ret.fontStyle = v.getCssText();
        }
        if ((v = sm.getValue(26)) != null) {
            ret.fontVariant = v.getCssText();
        }
        if ((v = sm.getValue(24)) != null) {
            ret.fontStretch = v.getCssText();
        }
        v = sm.getValue(41);
        ParsedURL base = ffr.getURL();
        if (v != null && v != ValueConstants.NONE_VALUE) {
            if (v.getCssValueType() == 1) {
                ret.srcs = new LinkedList();
                ret.srcs.add(CSSFontFace.getSrcValue(v, base));
            } else if (v.getCssValueType() == 2) {
                ret.srcs = new LinkedList();
                for (int i = 0; i < v.getLength(); ++i) {
                    ret.srcs.add(CSSFontFace.getSrcValue(v.item(i), base));
                }
            }
        }
        return ret;
    }

    public static Object getSrcValue(Value v, ParsedURL base) {
        if (v.getCssValueType() != 1) {
            return null;
        }
        if (v.getPrimitiveType() == 20) {
            if (base != null) {
                return new ParsedURL(base, v.getStringValue());
            }
            return new ParsedURL(v.getStringValue());
        }
        if (v.getPrimitiveType() == 19) {
            return v.getStringValue();
        }
        return null;
    }

    public static String getStringProp(StyleMap sm, CSSEngine eng, int pidx) {
        Value v = sm.getValue(pidx);
        ValueManager[] vms = eng.getValueManagers();
        if (v == null) {
            ValueManager vm = vms[pidx];
            v = vm.getDefaultValue();
        }
        while (v.getCssValueType() == 2) {
            v = v.item(0);
        }
        return v.getStringValue();
    }

    public static float getFloatProp(StyleMap sm, CSSEngine eng, int pidx) {
        Value v = sm.getValue(pidx);
        ValueManager[] vms = eng.getValueManagers();
        if (v == null) {
            ValueManager vm = vms[pidx];
            v = vm.getDefaultValue();
        }
        while (v.getCssValueType() == 2) {
            v = v.item(0);
        }
        return v.getFloatValue();
    }

    @Override
    public GVTFontFamily getFontFamily(BridgeContext ctx) {
        if (this.fontFamily != null) {
            return this.fontFamily;
        }
        this.fontFamily = super.getFontFamily(ctx);
        return this.fontFamily;
    }
}

