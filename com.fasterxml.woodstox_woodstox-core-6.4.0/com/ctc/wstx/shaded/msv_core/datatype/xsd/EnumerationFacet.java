/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DataTypeWithValueConstraintFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class EnumerationFacet
extends DataTypeWithValueConstraintFacet {
    public final Set values;
    private static final long serialVersionUID = 1L;

    protected EnumerationFacet(String nsUri, String typeName, XSDatatypeImpl baseType, Collection _values, boolean _isFixed) throws DatatypeException {
        super(nsUri, typeName, baseType, "enumeration", _isFixed);
        this.values = new HashSet(_values);
    }

    public Object _createValue(String literal, ValidationContext context) {
        Object o = this.baseType._createValue(literal, context);
        if (o == null || !this.values.contains(o)) {
            return null;
        }
        return o;
    }

    protected void diagnoseByFacet(String content, ValidationContext context) throws DatatypeException {
        if (this._createValue(content, context) != null) {
            return;
        }
        if (this.values.size() <= 4) {
            Object[] members = this.values.toArray();
            String r = "";
            if (members[0] instanceof String || members[0] instanceof Number) {
                r = r + "\"" + members[0].toString() + "\"";
                for (int i = 1; i < members.length; ++i) {
                    r = r + "/\"" + members[i].toString() + "\"";
                }
                r = "(" + r + ")";
                throw new DatatypeException(-1, EnumerationFacet.localize("DataTypeErrorDiagnosis.Enumeration.Arg", r));
            }
        }
        throw new DatatypeException(-1, EnumerationFacet.localize("DataTypeErrorDiagnosis.Enumeration"));
    }
}

