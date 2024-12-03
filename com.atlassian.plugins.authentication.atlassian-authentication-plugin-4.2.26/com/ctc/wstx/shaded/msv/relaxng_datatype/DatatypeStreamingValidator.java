/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.relaxng_datatype;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;

public interface DatatypeStreamingValidator {
    public void addCharacters(char[] var1, int var2, int var3);

    public boolean isValid();

    public void checkValid() throws DatatypeException;
}

