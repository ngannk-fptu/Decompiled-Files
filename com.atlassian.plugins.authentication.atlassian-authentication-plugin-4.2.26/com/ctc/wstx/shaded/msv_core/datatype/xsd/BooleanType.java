/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.BuiltinAtomicType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.SimpleURType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;

public class BooleanType
extends BuiltinAtomicType {
    public static final BooleanType theInstance = new BooleanType();
    private static final long serialVersionUID = 1L;

    private BooleanType() {
        super("boolean");
    }

    public final XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }

    protected boolean checkFormat(String content, ValidationContext context) {
        return "true".equals(content) || "false".equals(content) || "0".equals(content) || "1".equals(content);
    }

    public Object _createValue(String lexicalValue, ValidationContext context) {
        return BooleanType.load(lexicalValue);
    }

    public static Boolean load(String s) {
        if (s.equals("true")) {
            return Boolean.TRUE;
        }
        if (s.equals("1")) {
            return Boolean.TRUE;
        }
        if (s.equals("0")) {
            return Boolean.FALSE;
        }
        if (s.equals("false")) {
            return Boolean.FALSE;
        }
        return null;
    }

    public String convertToLexicalValue(Object value, SerializationContext context) {
        if (value instanceof Boolean) {
            return BooleanType.save((Boolean)value);
        }
        throw new IllegalArgumentException();
    }

    public static String save(Boolean b) {
        if (b.booleanValue()) {
            return "true";
        }
        return "false";
    }

    public int isFacetApplicable(String facetName) {
        if (facetName.equals("pattern") || facetName.equals("enumeration") || facetName.equals("whiteSpace")) {
            return 0;
        }
        return -2;
    }

    public Class getJavaObjectType() {
        return Boolean.class;
    }
}

