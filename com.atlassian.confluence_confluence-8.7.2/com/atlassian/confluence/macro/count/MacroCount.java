/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.macro.count;

import com.google.common.base.Preconditions;
import java.util.concurrent.atomic.AtomicInteger;
import org.checkerframework.checker.nullness.qual.NonNull;

public class MacroCount {
    private final String macroType;
    private final AtomicInteger count;

    public MacroCount(String macroType, int count) {
        Preconditions.checkArgument((count >= 0 ? 1 : 0) != 0);
        this.macroType = (String)Preconditions.checkNotNull((Object)macroType);
        this.count = new AtomicInteger(count);
    }

    public @NonNull String getMacroType() {
        return this.macroType;
    }

    public @NonNull Integer getCount() {
        return this.count.intValue();
    }
}

