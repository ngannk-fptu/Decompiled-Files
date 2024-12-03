/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.udf;

import org.apache.poi.ss.formula.functions.FreeRefFunction;

public interface UDFFinder {
    public FreeRefFunction findFunction(String var1);
}

