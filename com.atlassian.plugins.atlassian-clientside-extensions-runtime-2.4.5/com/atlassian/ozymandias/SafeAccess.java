/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  io.atlassian.fugue.Option
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.ozymandias;

import com.atlassian.ozymandias.ModuleDescriptorFunction;
import com.atlassian.ozymandias.ModuleDescriptorVisitor;
import com.atlassian.ozymandias.PluginPointFunction;
import com.atlassian.ozymandias.PluginPointVisitor;
import com.atlassian.ozymandias.error.ModuleAccessError;
import com.atlassian.plugin.ModuleDescriptor;
import io.atlassian.fugue.Option;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface SafeAccess {
    public <MT, RT, D extends ModuleDescriptor<MT>> List<RT> descriptors(Iterable<D> var1, PluginPointFunction<D, MT, RT> var2);

    @Nullable
    public <MT, RT, D extends ModuleDescriptor<MT>> RT descriptor(D var1, PluginPointFunction<D, MT, RT> var2);

    public <MT, RT, D extends ModuleDescriptor<MT>> List<RT> modules(Iterable<MT> var1, PluginPointFunction<D, MT, RT> var2);

    @Nullable
    public <MT, RT, D extends ModuleDescriptor<MT>> RT module(MT var1, PluginPointFunction<D, MT, RT> var2);

    public <MT, D extends ModuleDescriptor<MT>> List<Option<? extends ModuleAccessError>> descriptors(Iterable<D> var1, PluginPointVisitor<D, MT> var2);

    public <MT, D extends ModuleDescriptor<MT>> Option<? extends ModuleAccessError> descriptor(D var1, PluginPointVisitor<D, MT> var2);

    public <RT, D extends ModuleDescriptor<?>> List<RT> descriptors(Iterable<D> var1, ModuleDescriptorFunction<D, RT> var2);

    public <D extends ModuleDescriptor<?>> List<Option<? extends ModuleAccessError>> descriptors(Iterable<D> var1, ModuleDescriptorVisitor<D> var2);

    @Nullable
    public <RT, D extends ModuleDescriptor<?>> RT descriptor(D var1, ModuleDescriptorFunction<D, RT> var2);

    public <D extends ModuleDescriptor<?>> Option<? extends ModuleAccessError> descriptor(D var1, ModuleDescriptorVisitor<D> var2);

    public <MT, D extends ModuleDescriptor<MT>> List<Option<? extends ModuleAccessError>> modules(Iterable<MT> var1, PluginPointVisitor<D, MT> var2);

    public <MT, D extends ModuleDescriptor<MT>> Option<? extends ModuleAccessError> module(MT var1, PluginPointVisitor<D, MT> var2);

    public <RT> RT callable(Callable<RT> var1);

    public void runnable(Runnable var1);
}

