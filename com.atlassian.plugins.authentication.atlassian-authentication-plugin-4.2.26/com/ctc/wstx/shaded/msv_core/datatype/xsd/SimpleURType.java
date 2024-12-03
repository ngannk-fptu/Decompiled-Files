/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.BuiltinAtomicType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;

public class SimpleURType
extends BuiltinAtomicType {
    public static final SimpleURType theInstance = new SimpleURType();
    private static final long serialVersionUID = 1L;

    protected SimpleURType() {
        super("anySimpleType", WhiteSpaceProcessor.thePreserve);
    }

    public final XSDatatype getBaseType() {
        return null;
    }

    protected final boolean checkFormat(String content, ValidationContext context) {
        return true;
    }

    public Object _createValue(String lexicalValue, ValidationContext context) {
        return lexicalValue;
    }

    public Class getJavaObjectType() {
        return String.class;
    }

    public String convertToLexicalValue(Object value, SerializationContext context) {
        if (value instanceof String) {
            return (String)value;
        }
        throw new IllegalArgumentException();
    }

    public final int isFacetApplicable(String facetName) {
        return 0;
    }
}

