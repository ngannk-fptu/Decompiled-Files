/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.relaxng_datatype;

public interface ValidationContext {
    public String resolveNamespacePrefix(String var1);

    public String getBaseUri();

    public boolean isUnparsedEntity(String var1);

    public boolean isNotation(String var1);
}

