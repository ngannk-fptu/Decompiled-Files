/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.sr;

import com.ctc.wstx.dtd.DTDId;
import com.ctc.wstx.dtd.DTDSubset;
import com.ctc.wstx.util.SymbolTable;

public interface ReaderCreator {
    public DTDSubset findCachedDTD(DTDId var1);

    public void updateSymbolTable(SymbolTable var1);

    public void addCachedDTD(DTDId var1, DTDSubset var2);
}

