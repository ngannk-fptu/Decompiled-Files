/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.BinaryValueType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.BuiltinAtomicType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.Discrete;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.SimpleURType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;

abstract class BinaryBaseType
extends BuiltinAtomicType
implements Discrete {
    private static final long serialVersionUID = -6355125980881791215L;

    BinaryBaseType(String typeName) {
        super(typeName);
    }

    public final int isFacetApplicable(String facetName) {
        if (facetName.equals("length") || facetName.equals("maxLength") || facetName.equals("minLength") || facetName.equals("pattern") || facetName.equals("whiteSpace") || facetName.equals("enumeration")) {
            return 0;
        }
        return -2;
    }

    public final XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }

    public final int countLength(Object value) {
        return ((BinaryValueType)value).rawData.length;
    }

    public Object _createJavaObject(String literal, ValidationContext context) {
        BinaryValueType v = (BinaryValueType)this.createValue(literal, context);
        if (v == null) {
            return null;
        }
        return v.rawData;
    }

    public abstract String serializeJavaObject(Object var1, SerializationContext var2);

    public Class getJavaObjectType() {
        return byte[].class;
    }
}

