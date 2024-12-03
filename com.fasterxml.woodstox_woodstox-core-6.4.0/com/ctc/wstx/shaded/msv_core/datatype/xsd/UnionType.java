/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.ConcreteType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.SimpleURType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl;

public final class UnionType
extends ConcreteType {
    public final XSDatatypeImpl[] memberTypes;
    private static final long serialVersionUID = 1L;

    public UnionType(String nsUri, String newTypeName, XSDatatype[] memberTypes) throws DatatypeException {
        super(nsUri, newTypeName);
        if (memberTypes.length == 0) {
            throw new DatatypeException(UnionType.localize("BadTypeException.EmptyUnion"));
        }
        XSDatatypeImpl[] m = new XSDatatypeImpl[memberTypes.length];
        System.arraycopy(memberTypes, 0, m, 0, memberTypes.length);
        for (int i = 0; i < m.length; ++i) {
            if (!m[i].isFinal(4)) continue;
            throw new DatatypeException(UnionType.localize("BadTypeException.InvalidMemberType", m[i].displayName()));
        }
        this.memberTypes = m;
    }

    public final XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }

    public final String displayName() {
        String name = this.getName();
        if (name != null) {
            return name;
        }
        return "union";
    }

    public boolean isContextDependent() {
        for (int i = 0; i < this.memberTypes.length; ++i) {
            if (!this.memberTypes[i].isContextDependent()) continue;
            return true;
        }
        return false;
    }

    public final int getVariety() {
        return 3;
    }

    public final int isFacetApplicable(String facetName) {
        if (facetName.equals("pattern") || facetName.equals("enumeration")) {
            return 0;
        }
        return -2;
    }

    protected final boolean checkFormat(String content, ValidationContext context) {
        for (int i = 0; i < this.memberTypes.length; ++i) {
            if (!this.memberTypes[i].checkFormat(content, context)) continue;
            return true;
        }
        return false;
    }

    public Object _createValue(String content, ValidationContext context) {
        for (int i = 0; i < this.memberTypes.length; ++i) {
            Object o = this.memberTypes[i]._createValue(content, context);
            if (o == null) continue;
            return o;
        }
        return null;
    }

    public Class getJavaObjectType() {
        return Object.class;
    }

    public String convertToLexicalValue(Object o, SerializationContext context) {
        for (int i = 0; i < this.memberTypes.length; ++i) {
            try {
                return this.memberTypes[i].convertToLexicalValue(o, context);
            }
            catch (Exception e) {
                continue;
            }
        }
        throw new IllegalArgumentException();
    }

    protected void _checkValid(String content, ValidationContext context) throws DatatypeException {
        if (this.checkFormat(content, context)) {
            return;
        }
        throw new DatatypeException();
    }
}

