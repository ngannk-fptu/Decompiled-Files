/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.impl.common.ValidationContext;
import org.apache.xmlbeans.impl.values.JavaStringHolder;
import org.apache.xmlbeans.impl.values.XmlObjectBase;
import org.apache.xmlbeans.impl.values.XmlValueOutOfRangeException;

public abstract class JavaStringHolderEx
extends JavaStringHolder {
    private final SchemaType _schemaType;

    @Override
    public SchemaType schemaType() {
        return this._schemaType;
    }

    public JavaStringHolderEx(SchemaType type, boolean complex) {
        this._schemaType = type;
        this.initComplexType(complex, false);
    }

    @Override
    protected int get_wscanon_rule() {
        return this.schemaType().getWhiteSpaceRule();
    }

    @Override
    protected void set_text(String s) {
        if (this._validateOnSet()) {
            JavaStringHolderEx.validateLexical(s, this._schemaType, _voorVc);
        }
        super.set_text(s);
    }

    @Override
    protected boolean is_defaultable_ws(String v) {
        try {
            JavaStringHolderEx.validateLexical(v, this._schemaType, _voorVc);
            return false;
        }
        catch (XmlValueOutOfRangeException e) {
            return true;
        }
    }

    public static void validateLexical(String v, SchemaType sType, ValidationContext context) {
        XmlAnySimpleType[] vals;
        XmlAnySimpleType max;
        XmlAnySimpleType min;
        if (!sType.matchPatternFacet(v)) {
            context.invalid("cvc-datatype-valid.1.1", new Object[]{"string", v, QNameHelper.readable(sType)});
            return;
        }
        XmlAnySimpleType len = sType.getFacet(0);
        if (len != null) {
            int m = ((XmlObjectBase)((Object)len)).getBigIntegerValue().intValue();
            if (v.length() != m) {
                context.invalid("cvc-length-valid.1.1", new Object[]{"string", v.length(), m, QNameHelper.readable(sType)});
                return;
            }
        }
        if ((min = sType.getFacet(1)) != null) {
            int m = ((XmlObjectBase)((Object)min)).getBigIntegerValue().intValue();
            if (v.length() < m) {
                context.invalid("cvc-minLength-valid.1.1", new Object[]{"string", v.length(), m, QNameHelper.readable(sType)});
                return;
            }
        }
        if ((max = sType.getFacet(2)) != null) {
            int m = ((XmlObjectBase)((Object)max)).getBigIntegerValue().intValue();
            if (v.length() > m) {
                context.invalid("cvc-maxLength-valid.1.1", new Object[]{"string", v.length(), m, QNameHelper.readable(sType)});
                return;
            }
        }
        if ((vals = sType.getEnumerationValues()) != null) {
            for (XmlAnySimpleType val : vals) {
                if (!v.equals(val.getStringValue())) continue;
                return;
            }
            context.invalid("cvc-enumeration-valid", new Object[]{"string", v, QNameHelper.readable(sType)});
        }
    }

    @Override
    protected void validate_simpleval(String lexical, ValidationContext ctx) {
        JavaStringHolderEx.validateLexical(this.getStringValue(), this.schemaType(), ctx);
    }
}

