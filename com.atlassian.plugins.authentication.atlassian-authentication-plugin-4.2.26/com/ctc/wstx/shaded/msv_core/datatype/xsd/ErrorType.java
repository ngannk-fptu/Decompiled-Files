/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.BuiltinAtomicType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.SimpleURType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;

public class ErrorType
extends BuiltinAtomicType {
    public static final ErrorType theInstance = new ErrorType();
    private static final long serialVersionUID = 1L;

    protected ErrorType() {
        super("error");
    }

    protected Object _createValue(String content, ValidationContext context) {
        return this;
    }

    protected boolean checkFormat(String literal, ValidationContext context) {
        return true;
    }

    public String convertToLexicalValue(Object valueObject, SerializationContext context) throws IllegalArgumentException {
        return "";
    }

    public int isFacetApplicable(String facetName) {
        return 0;
    }

    public XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }

    public Class getJavaObjectType() {
        return this.getClass();
    }
}

