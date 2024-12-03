/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.relaxng_datatype.helpers;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeBuilder;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;

public final class ParameterlessDatatypeBuilder
implements DatatypeBuilder {
    private final Datatype baseType;

    public ParameterlessDatatypeBuilder(Datatype datatype) {
        this.baseType = datatype;
    }

    public void addParameter(String string, String string2, ValidationContext validationContext) throws DatatypeException {
        throw new DatatypeException();
    }

    public Datatype createDatatype() throws DatatypeException {
        return this.baseType;
    }
}

