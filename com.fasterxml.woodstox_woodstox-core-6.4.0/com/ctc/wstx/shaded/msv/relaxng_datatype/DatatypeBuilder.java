/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.relaxng_datatype;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;

public interface DatatypeBuilder {
    public void addParameter(String var1, String var2, ValidationContext var3) throws DatatypeException;

    public Datatype createDatatype() throws DatatypeException;
}

