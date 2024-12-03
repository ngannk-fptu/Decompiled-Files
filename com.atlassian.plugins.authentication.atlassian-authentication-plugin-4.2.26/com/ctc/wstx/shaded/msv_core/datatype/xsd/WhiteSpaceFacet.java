/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DataTypeWithFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.TypeIncubator;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl;

public class WhiteSpaceFacet
extends DataTypeWithFacet {
    private static final long serialVersionUID = 1L;

    WhiteSpaceFacet(String nsUri, String typeName, XSDatatypeImpl baseType, TypeIncubator facets) throws DatatypeException {
        this(nsUri, typeName, baseType, WhiteSpaceProcessor.get((String)facets.getFacet("whiteSpace")), facets.isFixed("whiteSpace"));
    }

    WhiteSpaceFacet(String nsUri, String typeName, XSDatatypeImpl baseType, WhiteSpaceProcessor proc, boolean _isFixed) throws DatatypeException {
        super(nsUri, typeName, baseType, "whiteSpace", _isFixed, proc);
        if (baseType.whiteSpace.tightness() > this.whiteSpace.tightness()) {
            XSDatatypeImpl d = baseType.getFacetObject("whiteSpace");
            if (d == null) {
                d = this.getConcreteType();
            }
            throw new DatatypeException(WhiteSpaceFacet.localize("LoosenedFacet", "whiteSpace", d.displayName()));
        }
    }

    protected boolean checkFormat(String content, ValidationContext context) {
        return this.baseType.checkFormat(content, context);
    }

    public Object _createValue(String content, ValidationContext context) {
        return this.baseType._createValue(content, context);
    }

    protected void diagnoseByFacet(String content, ValidationContext context) {
    }
}

