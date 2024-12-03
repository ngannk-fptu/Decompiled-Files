/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl;

import software.amazon.ion.SymbolTable;
import software.amazon.ion.impl.PrivateUtils;

@Deprecated
public final class PrivateSymtabExtendsCache {
    private SymbolTable myWriterSymtab;
    private SymbolTable myReaderSymtab;
    private int myWriterSymtabMaxId;
    private int myReaderSymtabMaxId;
    private boolean myResult;

    public boolean symtabsCompat(SymbolTable writerSymtab, SymbolTable readerSymtab) {
        assert (writerSymtab != null && readerSymtab != null) : "writer's and reader's current symtab cannot be null";
        if (this.myWriterSymtab == writerSymtab && this.myReaderSymtab == readerSymtab && this.myWriterSymtabMaxId == writerSymtab.getMaxId() && this.myReaderSymtabMaxId == readerSymtab.getMaxId()) {
            return this.myResult;
        }
        this.myResult = PrivateUtils.symtabExtends(writerSymtab, readerSymtab);
        this.myWriterSymtab = writerSymtab;
        this.myReaderSymtab = readerSymtab;
        this.myWriterSymtabMaxId = writerSymtab.getMaxId();
        this.myReaderSymtabMaxId = readerSymtab.getMaxId();
        return this.myResult;
    }
}

