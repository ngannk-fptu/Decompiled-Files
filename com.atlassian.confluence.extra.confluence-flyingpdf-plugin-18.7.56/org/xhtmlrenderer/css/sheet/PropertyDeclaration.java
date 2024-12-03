/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.sheet;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.parser.PropertyValue;

public class PropertyDeclaration {
    private String propName;
    private CSSName cssName;
    private CSSPrimitiveValue cssPrimitiveValue;
    private boolean important;
    private int origin;
    private IdentValue _identVal;
    private boolean identIsSet;
    private String _fingerprint;
    public static final int IMPORTANCE_AND_ORIGIN_COUNT = 6;
    private static final int USER_AGENT = 1;
    private static final int USER_NORMAL = 2;
    private static final int AUTHOR_NORMAL = 3;
    private static final int AUTHOR_IMPORTANT = 4;
    private static final int USER_IMPORTANT = 5;

    public PropertyDeclaration(CSSName cssName, CSSPrimitiveValue value, boolean imp, int orig) {
        this.propName = cssName.toString();
        this.cssName = cssName;
        this.cssPrimitiveValue = value;
        this.important = imp;
        this.origin = orig;
    }

    public String toString() {
        return this.getPropertyName() + ": " + this.getValue().toString();
    }

    public IdentValue asIdentValue() {
        if (!this.identIsSet) {
            this._identVal = IdentValue.getByIdentString(this.cssPrimitiveValue.getCssText());
            this.identIsSet = true;
        }
        return this._identVal;
    }

    public String getDeclarationStandardText() {
        return this.cssName + ": " + this.cssPrimitiveValue.getCssText() + ";";
    }

    public String getFingerprint() {
        if (this._fingerprint == null) {
            this._fingerprint = 80 + this.cssName.FS_ID + 58 + ((PropertyValue)this.cssPrimitiveValue).getFingerprint() + ';';
        }
        return this._fingerprint;
    }

    public int getImportanceAndOrigin() {
        if (this.origin == 0) {
            return 1;
        }
        if (this.origin == 1) {
            if (this.important) {
                return 5;
            }
            return 2;
        }
        if (this.important) {
            return 4;
        }
        return 3;
    }

    public String getPropertyName() {
        return this.propName;
    }

    public CSSName getCSSName() {
        return this.cssName;
    }

    public CSSPrimitiveValue getValue() {
        return this.cssPrimitiveValue;
    }

    public boolean isImportant() {
        return this.important;
    }

    public int getOrigin() {
        return this.origin;
    }
}

