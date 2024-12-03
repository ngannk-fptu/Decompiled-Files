/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.base;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.J2ktIncompatible;
import com.google.common.base.CommonPattern;
import com.google.common.base.ElementTypesAreNonnullByDefault;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
interface PatternCompiler {
    public CommonPattern compile(String var1);

    public boolean isPcreLike();
}

