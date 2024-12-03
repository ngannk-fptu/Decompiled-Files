/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.util.SVGConstants
 */
package org.apache.batik.gvt.font;

import org.apache.batik.util.SVGConstants;

public class GVTFontFace
implements SVGConstants {
    protected String familyName;
    protected float unitsPerEm;
    protected String fontWeight;
    protected String fontStyle;
    protected String fontVariant;
    protected String fontStretch;
    protected float slope;
    protected String panose1;
    protected float ascent;
    protected float descent;
    protected float strikethroughPosition;
    protected float strikethroughThickness;
    protected float underlinePosition;
    protected float underlineThickness;
    protected float overlinePosition;
    protected float overlineThickness;

    public GVTFontFace(String familyName, float unitsPerEm, String fontWeight, String fontStyle, String fontVariant, String fontStretch, float slope, String panose1, float ascent, float descent, float strikethroughPosition, float strikethroughThickness, float underlinePosition, float underlineThickness, float overlinePosition, float overlineThickness) {
        this.familyName = familyName;
        this.unitsPerEm = unitsPerEm;
        this.fontWeight = fontWeight;
        this.fontStyle = fontStyle;
        this.fontVariant = fontVariant;
        this.fontStretch = fontStretch;
        this.slope = slope;
        this.panose1 = panose1;
        this.ascent = ascent;
        this.descent = descent;
        this.strikethroughPosition = strikethroughPosition;
        this.strikethroughThickness = strikethroughThickness;
        this.underlinePosition = underlinePosition;
        this.underlineThickness = underlineThickness;
        this.overlinePosition = overlinePosition;
        this.overlineThickness = overlineThickness;
    }

    public GVTFontFace(String familyName) {
        this(familyName, 1000.0f, "all", "all", "normal", "normal", 0.0f, "0 0 0 0 0 0 0 0 0 0", 800.0f, 200.0f, 300.0f, 50.0f, -75.0f, 50.0f, 800.0f, 50.0f);
    }

    public String getFamilyName() {
        return this.familyName;
    }

    public boolean hasFamilyName(String family) {
        String ffname = this.familyName;
        if (ffname.length() < family.length()) {
            return false;
        }
        int idx = (ffname = ffname.toLowerCase()).indexOf(family.toLowerCase());
        if (idx == -1) {
            return false;
        }
        if (ffname.length() > family.length()) {
            int i;
            char c;
            boolean quote = false;
            if (idx > 0) {
                c = ffname.charAt(idx - 1);
                block0 : switch (c) {
                    default: {
                        return false;
                    }
                    case ' ': {
                        block18: for (i = idx - 2; i >= 0; --i) {
                            switch (ffname.charAt(i)) {
                                default: {
                                    return false;
                                }
                                case ' ': {
                                    continue block18;
                                }
                                case '\"': 
                                case '\'': {
                                    quote = true;
                                    break block0;
                                }
                            }
                        }
                        break;
                    }
                    case '\"': 
                    case '\'': {
                        quote = true;
                    }
                    case ',': 
                }
            }
            if (idx + family.length() < ffname.length()) {
                c = ffname.charAt(idx + family.length());
                block9 : switch (c) {
                    default: {
                        return false;
                    }
                    case ' ': {
                        block19: for (i = idx + family.length() + 1; i < ffname.length(); ++i) {
                            switch (ffname.charAt(i)) {
                                default: {
                                    return false;
                                }
                                case ' ': {
                                    continue block19;
                                }
                                case '\"': 
                                case '\'': {
                                    if (quote) break block9;
                                    return false;
                                }
                            }
                        }
                        break;
                    }
                    case '\"': 
                    case '\'': {
                        if (quote) break;
                        return false;
                    }
                    case ',': 
                }
            }
        }
        return true;
    }

    public String getFontWeight() {
        return this.fontWeight;
    }

    public String getFontStyle() {
        return this.fontStyle;
    }

    public float getUnitsPerEm() {
        return this.unitsPerEm;
    }

    public float getAscent() {
        return this.ascent;
    }

    public float getDescent() {
        return this.descent;
    }

    public float getStrikethroughPosition() {
        return this.strikethroughPosition;
    }

    public float getStrikethroughThickness() {
        return this.strikethroughThickness;
    }

    public float getUnderlinePosition() {
        return this.underlinePosition;
    }

    public float getUnderlineThickness() {
        return this.underlineThickness;
    }

    public float getOverlinePosition() {
        return this.overlinePosition;
    }

    public float getOverlineThickness() {
        return this.overlineThickness;
    }
}

