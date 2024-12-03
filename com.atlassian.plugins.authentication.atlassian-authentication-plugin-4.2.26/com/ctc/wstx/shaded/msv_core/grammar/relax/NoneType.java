/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.relax;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.BuiltinAtomicType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.SimpleURType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;

public class NoneType
extends BuiltinAtomicType {
    public static final NoneType theInstance = new NoneType();
    private static final long serialVersionUID = 1L;

    private NoneType() {
        super("none");
    }

    public final XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }

    public int isFacetApplicable(String facetName) {
        return -2;
    }

    public boolean checkFormat(String literal, ValidationContext context) {
        return false;
    }

    public Object _createValue(String lexicalValue, ValidationContext context) {
        return null;
    }

    public String convertToLexicalValue(Object o, SerializationContext context) {
        throw new IllegalArgumentException();
    }

    public Class getJavaObjectType() {
        return Object.class;
    }
}

