/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.BuiltinAtomicType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.NcnameType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;

public class EntityType
extends BuiltinAtomicType {
    public static final EntityType theInstance = new EntityType();
    private static final long serialVersionUID = 1L;

    private EntityType() {
        super("ENTITY");
    }

    public final XSDatatype getBaseType() {
        return NcnameType.theInstance;
    }

    public boolean isContextDependent() {
        return true;
    }

    public final int isFacetApplicable(String facetName) {
        if (facetName.equals("length") || facetName.equals("minLength") || facetName.equals("maxLength") || facetName.equals("pattern") || facetName.equals("whiteSpace") || facetName.equals("enumeration")) {
            return 0;
        }
        return -2;
    }

    protected boolean checkFormat(String content, ValidationContext context) {
        return context.isUnparsedEntity(content);
    }

    public Object _createValue(String content, ValidationContext context) {
        if (context.isUnparsedEntity(content)) {
            return content;
        }
        return null;
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
}

