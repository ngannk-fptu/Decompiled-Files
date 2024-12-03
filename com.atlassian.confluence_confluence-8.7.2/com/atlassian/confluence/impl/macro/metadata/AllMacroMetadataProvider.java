/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.predicate.ModuleDescriptorPredicate
 */
package com.atlassian.confluence.impl.macro.metadata;

import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.browser.MacroMetadataSource;
import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.predicate.ModuleDescriptorPredicate;
import java.util.Map;
import java.util.function.BiFunction;

public interface AllMacroMetadataProvider<T extends ModuleDescriptor<Macro> & MacroMetadataSource>
extends BiFunction<Class<T>, ModuleDescriptorPredicate<Macro>, Map<String, MacroMetadata>> {
}

