/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory
 *  javax.annotation.Nullable
 */
package com.atlassian.plugin.schema.descriptor;

import com.atlassian.plugin.osgi.external.ListableModuleDescriptorFactory;
import com.atlassian.plugin.schema.spi.Schema;
import javax.annotation.Nullable;

public interface DescribedModuleDescriptorFactory
extends ListableModuleDescriptorFactory {
    @Nullable
    public Schema getSchema(String var1);
}

