/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.relax;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.BuiltinAtomicType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.SimpleURType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;

public class EmptyStringType
extends BuiltinAtomicType {
    public static final EmptyStringType theInstance = new EmptyStringType();
    private static final long serialVersionUID = 1L;

    private EmptyStringType() {
        super("emptyString");
    }

    public final XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }

    public int isFacetApplicable(String facetName) {
        return -2;
    }

    public boolean checkFormat(String literal, ValidationContext context) {
        return literal.equals("");
    }

    public Object _createValue(String lexicalValue, ValidationContext context) {
        if (lexicalValue.equals("")) {
            return lexicalValue;
        }
        return null;
    }

    public String convertToLexicalValue(Object o, SerializationContext context) {
        if (o.equals("")) {
            return "";
        }
        throw new IllegalArgumentException();
    }

    public Class getJavaObjectType() {
        return String.class;
    }
}

