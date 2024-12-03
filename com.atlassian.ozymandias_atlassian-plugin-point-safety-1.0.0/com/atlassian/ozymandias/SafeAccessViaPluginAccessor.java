/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  io.atlassian.fugue.Either
 *  io.atlassian.fugue.Option
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.ozymandias;

import com.atlassian.ozymandias.ModuleDescriptorFunction;
import com.atlassian.ozymandias.ModuleDescriptorVisitor;
import com.atlassian.ozymandias.PluginPointFunction;
import com.atlassian.ozymandias.PluginPointVisitor;
import com.atlassian.ozymandias.error.ModuleAccessError;
import com.atlassian.plugin.ModuleDescriptor;
import io.atlassian.fugue.Either;
import io.atlassian.fugue.Option;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface SafeAccessViaPluginAccessor {
    public <MT, RT, D extends ModuleDescriptor<MT>> List<RT> forType(Class<D> var1, PluginPointFunction<D, MT, RT> var2);

    public <MT, D extends ModuleDescriptor<MT>> void forType(Class<D> var1, PluginPointVisitor<D, MT> var2);

    public <MT, RT, D extends ModuleDescriptor<MT>> Either<? extends ModuleAccessError, RT> forKey(String var1, Class<D> var2, PluginPointFunction<D, MT, RT> var3);

    public <MT, D extends ModuleDescriptor<MT>> Option<? extends ModuleAccessError> forKey(String var1, Class<D> var2, PluginPointVisitor<D, MT> var3);

    public <RT, D extends ModuleDescriptor<?>> List<RT> forType(Class<D> var1, ModuleDescriptorFunction<D, RT> var2);

    public <D extends ModuleDescriptor<?>> void forType(Class<D> var1, ModuleDescriptorVisitor<D> var2);

    public <RT, D extends ModuleDescriptor<?>> Either<? extends ModuleAccessError, RT> forKey(String var1, Class<D> var2, ModuleDescriptorFunction<D, RT> var3);

    public <D extends ModuleDescriptor<?>> Option<? extends ModuleAccessError> forKey(String var1, Class<D> var2, ModuleDescriptorVisitor<D> var3);
}

