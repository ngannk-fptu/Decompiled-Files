/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Lists
 *  com.google.common.util.concurrent.AtomicLongMap
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.macro.count;

import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.count.MacroCount;
import com.atlassian.confluence.macro.count.MacroCounter;
import com.atlassian.confluence.macro.count.MacroMetricsKey;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AtomicLongMap;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;

public class DefaultMacroCounter
implements MacroCounter {
    private final AtomicLongMap<MacroMetricsKey> macroCounts = AtomicLongMap.create();

    @Override
    public void addMacroUsage(MacroDefinition macroDefinition, @Nullable Macro macro) {
        this.macroCounts.incrementAndGet((Object)MacroMetricsKey.createFrom(macroDefinition, macro));
    }

    public List<MacroCount> getUsages() {
        return Lists.newArrayList((Iterable)Collections2.transform(this.macroCounts.asMap().entrySet(), count -> new MacroCount(((MacroMetricsKey)count.getKey()).getMacroType(), ((Long)count.getValue()).intValue())));
    }
}

