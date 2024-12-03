/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.RangeFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl;

public class MaxExclusiveFacet
extends RangeFacet {
    private static final long serialVersionUID = 1L;

    protected MaxExclusiveFacet(String nsUri, String typeName, XSDatatypeImpl baseType, Object limit, boolean _isFixed) throws DatatypeException {
        super(nsUri, typeName, baseType, "maxExclusive", limit, _isFixed);
    }

    protected final boolean rangeCheck(int r) {
        return r == 1;
    }
}

