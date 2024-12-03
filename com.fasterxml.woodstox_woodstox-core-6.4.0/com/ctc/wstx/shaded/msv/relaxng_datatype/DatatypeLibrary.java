/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.relaxng_datatype;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeBuilder;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;

public interface DatatypeLibrary {
    public DatatypeBuilder createDatatypeBuilder(String var1) throws DatatypeException;

    public Datatype createDatatype(String var1) throws DatatypeException;
}

