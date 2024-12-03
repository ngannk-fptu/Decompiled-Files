/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd.ngimpl;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeBuilder;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.TypeIncubator;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl;

class DatatypeBuilderImpl
implements DatatypeBuilder {
    private TypeIncubator incubator;

    DatatypeBuilderImpl(XSDatatype baseType) {
        this.incubator = new TypeIncubator(baseType);
    }

    public void addParameter(String name, String value, ValidationContext context) throws DatatypeException {
        if (name.equals("enumeration")) {
            throw new DatatypeException(XSDatatypeImpl.localize("BadTypeException.NotApplicableFacet", name));
        }
        this.incubator.addFacet(name, value, false, context);
        if (name.equals("pattern")) {
            this.incubator = new TypeIncubator(this.incubator.derive(null, null));
        }
    }

    public Datatype createDatatype() throws DatatypeException {
        return this.incubator.derive(null, null);
    }
}

