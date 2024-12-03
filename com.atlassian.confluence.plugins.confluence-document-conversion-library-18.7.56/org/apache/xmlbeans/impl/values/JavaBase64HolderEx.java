/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.impl.common.ValidationContext;
import org.apache.xmlbeans.impl.values.JavaBase64Holder;
import org.apache.xmlbeans.impl.values.XmlObjectBase;

public abstract class JavaBase64HolderEx
extends JavaBase64Holder {
    private final SchemaType _schemaType;

    @Override
    public SchemaType schemaType() {
        return this._schemaType;
    }

    public JavaBase64HolderEx(SchemaType type, boolean complex) {
        this._schemaType = type;
        this.initComplexType(complex, false);
    }

    @Override
    protected int get_wscanon_rule() {
        return this.schemaType().getWhiteSpaceRule();
    }

    @Override
    protected void set_text(String s) {
        byte[] v = this._validateOnSet() ? JavaBase64HolderEx.validateLexical(s, this.schemaType(), _voorVc) : JavaBase64HolderEx.lex(s, _voorVc);
        if (v != null && this._validateOnSet()) {
            JavaBase64HolderEx.validateValue(v, this.schemaType(), XmlObjectBase._voorVc);
        }
        if (v != null) {
            super.set_ByteArray(v);
        }
    }

    @Override
    protected void set_ByteArray(byte[] v) {
        if (this._validateOnSet()) {
            JavaBase64HolderEx.validateValue(v, this.schemaType(), _voorVc);
        }
        super.set_ByteArray(v);
    }

    public static void validateValue(byte[] v, SchemaType sType, ValidationContext context) {
        XmlAnySimpleType[] vals;
        int i;
        XmlAnySimpleType o = sType.getFacet(0);
        if (o != null && (i = ((XmlObjectBase)((Object)o)).getBigIntegerValue().intValue()) != v.length) {
            context.invalid("cvc-length-valid.1.2", new Object[]{"base64Binary", v.length, i, QNameHelper.readable(sType)});
        }
        if ((o = sType.getFacet(1)) != null && (i = ((XmlObjectBase)((Object)o)).getBigIntegerValue().intValue()) > v.length) {
            context.invalid("cvc-minLength-valid.1.2", new Object[]{"base64Binary", v.length, i, QNameHelper.readable(sType)});
        }
        if ((o = sType.getFacet(2)) != null && (i = ((XmlObjectBase)((Object)o)).getBigIntegerValue().intValue()) < v.length) {
            context.invalid("cvc-maxLength-valid.1.2", new Object[]{"base64Binary", v.length, i, QNameHelper.readable(sType)});
        }
        if ((vals = sType.getEnumerationValues()) != null) {
            block0: for (i = 0; i < vals.length; ++i) {
                byte[] enumBytes = ((XmlObjectBase)((Object)vals[i])).getByteArrayValue();
                if (enumBytes.length != v.length) continue;
                for (int j = 0; j < enumBytes.length; ++j) {
                    if (enumBytes[j] != v[j]) continue block0;
                }
            }
            if (i >= vals.length) {
                context.invalid("cvc-enumeration-valid.b", new Object[]{"base64Binary", QNameHelper.readable(sType)});
            }
        }
    }

    @Override
    protected void validate_simpleval(String lexical, ValidationContext ctx) {
        JavaBase64HolderEx.validateLexical(lexical, this.schemaType(), ctx);
        JavaBase64HolderEx.validateValue(this.getByteArrayValue(), this.schemaType(), ctx);
    }
}

