/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp;

public interface XSTypeIncubator {
    public void addFacet(String var1, String var2, boolean var3, ValidationContext var4) throws DatatypeException;

    public XSDatatypeExp derive(String var1, String var2) throws DatatypeException;
}

