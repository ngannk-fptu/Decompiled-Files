/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.parser.property;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.parser.CSSParseException;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.parser.property.PropertyBuilder;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;

public abstract class AbstractPropertyBuilder
implements PropertyBuilder {
    @Override
    public List buildDeclarations(CSSName cssName, List values, int origin, boolean important) {
        return this.buildDeclarations(cssName, values, origin, important, true);
    }

    protected void checkValueCount(CSSName cssName, int expected, int found) {
        if (expected != found) {
            throw new CSSParseException("Found " + found + " value(s) for " + cssName + " when " + expected + " value(s) were expected", -1);
        }
    }

    protected void checkValueCount(CSSName cssName, int min, int max, int found) {
        if (found < min || found > max) {
            throw new CSSParseException("Found " + found + " value(s) for " + cssName + " when between " + min + " and " + max + " value(s) were expected", -1);
        }
    }

    protected void checkIdentType(CSSName cssName, CSSPrimitiveValue value) {
        if (value.getPrimitiveType() != 21) {
            throw new CSSParseException("Value for " + cssName + " must be an identifier", -1);
        }
    }

    protected void checkIdentOrURIType(CSSName cssName, CSSPrimitiveValue value) {
        short type = value.getPrimitiveType();
        if (type != 21 && type != 20) {
            throw new CSSParseException("Value for " + cssName + " must be an identifier or a URI", -1);
        }
    }

    protected void checkIdentOrColorType(CSSName cssName, CSSPrimitiveValue value) {
        short type = value.getPrimitiveType();
        if (type != 21 && type != 25) {
            throw new CSSParseException("Value for " + cssName + " must be an identifier or a color", -1);
        }
    }

    protected void checkIdentOrIntegerType(CSSName cssName, CSSPrimitiveValue value) {
        short type = value.getPrimitiveType();
        if (type != 21 && type != 1 || type == 1 && (int)value.getFloatValue((short)1) != Math.round(value.getFloatValue((short)1))) {
            throw new CSSParseException("Value for " + cssName + " must be an identifier or an integer", -1);
        }
    }

    protected void checkInteger(CSSName cssName, CSSPrimitiveValue value) {
        short type = value.getPrimitiveType();
        if (type != 1 || type == 1 && (int)value.getFloatValue((short)1) != Math.round(value.getFloatValue((short)1))) {
            throw new CSSParseException("Value for " + cssName + " must be an integer", -1);
        }
    }

    protected void checkIdentOrLengthType(CSSName cssName, CSSPrimitiveValue value) {
        short type = value.getPrimitiveType();
        if (type != 21 && !this.isLength(value)) {
            throw new CSSParseException("Value for " + cssName + " must be an identifier or a length", -1);
        }
    }

    protected void checkIdentOrNumberType(CSSName cssName, CSSPrimitiveValue value) {
        short type = value.getPrimitiveType();
        if (type != 21 && type != 1) {
            throw new CSSParseException("Value for " + cssName + " must be an identifier or a length", -1);
        }
    }

    protected void checkIdentLengthOrPercentType(CSSName cssName, CSSPrimitiveValue value) {
        short type = value.getPrimitiveType();
        if (type != 21 && !this.isLength(value) && type != 2) {
            throw new CSSParseException("Value for " + cssName + " must be an identifier, length, or percentage", -1);
        }
    }

    protected void checkLengthOrPercentType(CSSName cssName, CSSPrimitiveValue value) {
        short type = value.getPrimitiveType();
        if (!this.isLength(value) && type != 2) {
            throw new CSSParseException("Value for " + cssName + " must be a length or percentage", -1);
        }
    }

    protected void checkLengthType(CSSName cssName, CSSPrimitiveValue value) {
        if (!this.isLength(value)) {
            throw new CSSParseException("Value for " + cssName + " must be a length", -1);
        }
    }

    protected void checkNumberType(CSSName cssName, CSSPrimitiveValue value) {
        if (value.getPrimitiveType() != 1) {
            throw new CSSParseException("Value for " + cssName + " must be a number", -1);
        }
    }

    protected void checkStringType(CSSName cssName, CSSPrimitiveValue value) {
        if (value.getPrimitiveType() != 19) {
            throw new CSSParseException("Value for " + cssName + " must be a string", -1);
        }
    }

    protected void checkIdentOrString(CSSName cssName, CSSPrimitiveValue value) {
        short type = value.getPrimitiveType();
        if (type != 19 && type != 21) {
            throw new CSSParseException("Value for " + cssName + " must be an identifier or string", -1);
        }
    }

    protected void checkIdentLengthNumberOrPercentType(CSSName cssName, CSSPrimitiveValue value) {
        short type = value.getPrimitiveType();
        if (type != 21 && !this.isLength(value) && type != 2 && type != 1) {
            throw new CSSParseException("Value for " + cssName + " must be an identifier, length, or percentage", -1);
        }
    }

    protected boolean isLength(CSSPrimitiveValue value) {
        short unit = value.getPrimitiveType();
        return unit == 3 || unit == 4 || unit == 5 || unit == 8 || unit == 6 || unit == 7 || unit == 9 || unit == 10 || unit == 1 && value.getFloatValue((short)8) == 0.0f;
    }

    protected void checkValidity(CSSName cssName, BitSet validValues, IdentValue value) {
        if (!validValues.get(value.FS_ID)) {
            throw new CSSParseException("Ident " + value + " is an invalid or unsupported value for " + cssName, -1);
        }
    }

    protected IdentValue checkIdent(CSSName cssName, CSSPrimitiveValue value) {
        IdentValue result = IdentValue.valueOf(value.getStringValue());
        if (result == null) {
            throw new CSSParseException("Value " + value.getStringValue() + " is not a recognized identifier", -1);
        }
        ((PropertyValue)value).setIdentValue(result);
        return result;
    }

    protected PropertyDeclaration copyOf(PropertyDeclaration decl, CSSName newName) {
        return new PropertyDeclaration(newName, decl.getValue(), decl.isImportant(), decl.getOrigin());
    }

    protected void checkInheritAllowed(CSSPrimitiveValue value, boolean inheritAllowed) {
        if (value.getCssValueType() == 0 && !inheritAllowed) {
            throw new CSSParseException("Invalid use of inherit", -1);
        }
    }

    protected List checkInheritAll(CSSName[] all, List values, int origin, boolean important, boolean inheritAllowed) {
        if (values.size() == 1) {
            CSSPrimitiveValue value = (CSSPrimitiveValue)values.get(0);
            this.checkInheritAllowed(value, inheritAllowed);
            if (value.getCssValueType() == 0) {
                ArrayList<PropertyDeclaration> result = new ArrayList<PropertyDeclaration>(all.length);
                for (int i = 0; i < all.length; ++i) {
                    result.add(new PropertyDeclaration(all[i], value, important, origin));
                }
                return result;
            }
        }
        return null;
    }
}

