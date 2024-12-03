/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.BuiltinAtomicType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.Discrete;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.SimpleURType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.UnicodeUtil;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;

public class StringType
extends BuiltinAtomicType
implements Discrete {
    public static final StringType theInstance = new StringType("string", WhiteSpaceProcessor.thePreserve, true);
    private final boolean isAlwaysValid;
    private static final long serialVersionUID = 1L;

    protected StringType(String typeName, WhiteSpaceProcessor whiteSpace) {
        this(typeName, whiteSpace, false);
    }

    protected StringType(String typeName, WhiteSpaceProcessor whiteSpace, boolean _isAlwaysValid) {
        super(typeName, whiteSpace);
        this.isAlwaysValid = _isAlwaysValid;
    }

    public XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }

    protected final boolean checkFormat(String content, ValidationContext context) {
        return this._createValue(content, context) != null;
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

    public final int countLength(Object value) {
        return UnicodeUtil.countLength((String)value);
    }

    public final int isFacetApplicable(String facetName) {
        if (facetName.equals("pattern") || facetName.equals("enumeration") || facetName.equals("whiteSpace") || facetName.equals("length") || facetName.equals("maxLength") || facetName.equals("minLength")) {
            return 0;
        }
        return -2;
    }

    public boolean isAlwaysValid() {
        return this.isAlwaysValid;
    }
}

