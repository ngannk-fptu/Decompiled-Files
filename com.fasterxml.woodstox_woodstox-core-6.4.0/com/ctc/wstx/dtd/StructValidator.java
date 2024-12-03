/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import com.ctc.wstx.util.PrefixedName;

public abstract class StructValidator {
    public abstract StructValidator newInstance();

    public abstract String tryToValidate(PrefixedName var1);

    public abstract String fullyValid();
}

