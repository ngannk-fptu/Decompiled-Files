/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginAccessor
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Lists
 *  io.atlassian.fugue.Either
 *  io.atlassian.fugue.Option
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.ozymandias;

import com.atlassian.ozymandias.ModuleDescriptorFunction;
import com.atlassian.ozymandias.ModuleDescriptorVisitor;
import com.atlassian.ozymandias.PluginPointFunction;
import com.atlassian.ozymandias.PluginPointVisitor;
import com.atlassian.ozymandias.SafeAccess;
import com.atlassian.ozymandias.SafeAccessViaPluginAccessor;
import com.atlassian.ozymandias.error.IncorrectModuleTypeError;
import com.atlassian.ozymandias.error.ModuleAccessError;
import com.atlassian.ozymandias.error.ModuleExceptionError;
import com.atlassian.ozymandias.error.ModuleNotFoundError;
import com.atlassian.ozymandias.error.ThrowableLogger;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.atlassian.fugue.Either;
import io.atlassian.fugue.Option;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class SafePluginPointAccess
implements SafeAccessViaPluginAccessor,
SafeAccess {
    private final PluginAccessor pluginAccessor;

    private SafePluginPointAccess() {
        this.pluginAccessor = null;
    }

    private SafePluginPointAccess(@Nonnull PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    public static SafeAccessViaPluginAccessor to(@Nonnull PluginAccessor pluginAccessor) {
        return new SafePluginPointAccess(pluginAccessor);
    }

    public static SafeAccess to() {
        return new SafePluginPointAccess();
    }

    @Override
    public <MT, RT, D extends ModuleDescriptor<MT>> List<RT> forType(Class<D> moduleDescriptorClass, PluginPointFunction<D, MT, RT> callback) {
        List<D> moduleDescriptors = this.getModuleDescriptors(moduleDescriptorClass);
        return this.descriptors(moduleDescriptors, callback);
    }

    @Override
    public <MT, D extends ModuleDescriptor<MT>> void forType(Class<D> moduleDescriptorClass, PluginPointVisitor<D, MT> visitor) {
        List<D> moduleDescriptors = this.getModuleDescriptors(moduleDescriptorClass);
        this.descriptors(moduleDescriptors, visitor);
    }

    @Override
    public <RT, D extends ModuleDescriptor<?>> List<RT> forType(Class<D> moduleDescriptorClass, ModuleDescriptorFunction<D, RT> callback) {
        List<D> moduleDescriptors = this.getModuleDescriptors(moduleDescriptorClass);
        return this.descriptors(moduleDescriptors, callback);
    }

    @Override
    public <D extends ModuleDescriptor<?>> void forType(Class<D> moduleDescriptorClass, ModuleDescriptorVisitor<D> visitor) {
        List<D> moduleDescriptors = this.getModuleDescriptors(moduleDescriptorClass);
        this.descriptors(moduleDescriptors, visitor);
    }

    @Override
    public <MT, RT, D extends ModuleDescriptor<MT>> Either<? extends ModuleAccessError, RT> forKey(String moduleKey, Class<D> moduleDescriptorClass, PluginPointFunction<D, MT, RT> callback) {
        AccumulatingVisitor visitor = new AccumulatingVisitor(callback);
        Option<ModuleAccessError> result = this.forKey(moduleKey, moduleDescriptorClass, visitor);
        if (result.isDefined()) {
            return Either.left((Object)result.get());
        }
        return Either.right(visitor.getResults().get(0));
    }

    @Override
    public <MT, D extends ModuleDescriptor<MT>> Option<? extends ModuleAccessError> forKey(String moduleKey, Class<D> moduleDescriptorClass, PluginPointVisitor<D, MT> visitor) {
        ModuleDescriptor temp = this.pluginAccessor.getEnabledPluginModule(moduleKey);
        if (temp == null) {
            return Option.some((Object)new ModuleNotFoundError());
        }
        if (moduleDescriptorClass.isAssignableFrom(temp.getClass())) {
            ModuleDescriptor moduleDescriptor = (ModuleDescriptor)moduleDescriptorClass.cast(temp);
            return this.descriptor(moduleDescriptor, visitor);
        }
        return Option.some((Object)new IncorrectModuleTypeError());
    }

    @Override
    public <RT, D extends ModuleDescriptor<?>> Either<? extends ModuleAccessError, RT> forKey(String moduleKey, Class<D> moduleDescriptorClass, ModuleDescriptorFunction<D, RT> callback) {
        AccumulatingDescriptorVisitor visitor = new AccumulatingDescriptorVisitor(callback);
        Option<ModuleAccessError> result = this.forKey(moduleKey, moduleDescriptorClass, visitor);
        if (result.isDefined()) {
            return Either.left((Object)result.get());
        }
        return Either.right(visitor.getResults().get(0));
    }

    @Override
    public <D extends ModuleDescriptor<?>> Option<? extends ModuleAccessError> forKey(String moduleKey, Class<D> moduleDescriptorClass, ModuleDescriptorVisitor<D> visitor) {
        ModuleDescriptor temp = this.pluginAccessor.getEnabledPluginModule(moduleKey);
        if (temp == null) {
            return Option.some((Object)new ModuleNotFoundError());
        }
        if (moduleDescriptorClass.isAssignableFrom(temp.getClass())) {
            ModuleDescriptor moduleDescriptor = (ModuleDescriptor)moduleDescriptorClass.cast(temp);
            return this.descriptor(moduleDescriptor, visitor);
        }
        return Option.some((Object)new IncorrectModuleTypeError());
    }

    @Override
    public <MT, RT, D extends ModuleDescriptor<MT>> List<RT> descriptors(Iterable<D> moduleDescriptors, PluginPointFunction<D, MT, RT> callback) {
        AccumulatingVisitor visitor = new AccumulatingVisitor(callback);
        this.descriptors(moduleDescriptors, visitor);
        return visitor.getResults();
    }

    @Override
    public <MT, RT, D extends ModuleDescriptor<MT>> RT descriptor(D moduleDescriptor, PluginPointFunction<D, MT, RT> callback) {
        AccumulatingVisitor visitor = new AccumulatingVisitor(callback);
        this.visitPluginPointImpl(moduleDescriptor, visitor);
        List result = visitor.getResults();
        return result.isEmpty() ? null : (RT)result.get(0);
    }

    @Override
    public <MT, D extends ModuleDescriptor<MT>> List<Option<? extends ModuleAccessError>> descriptors(Iterable<D> moduleDescriptors, PluginPointVisitor<D, MT> visitor) {
        ImmutableList.Builder listBuilder = ImmutableList.builder();
        for (ModuleDescriptor moduleDescriptor : moduleDescriptors) {
            listBuilder.add(this.visitPluginPointImpl(moduleDescriptor, visitor));
        }
        return listBuilder.build();
    }

    @Override
    public <RT, D extends ModuleDescriptor<?>> List<RT> descriptors(Iterable<D> moduleDescriptors, ModuleDescriptorFunction<D, RT> callback) {
        AccumulatingDescriptorVisitor visitor = new AccumulatingDescriptorVisitor(callback);
        this.descriptors(moduleDescriptors, visitor);
        return visitor.getResults();
    }

    @Override
    public <D extends ModuleDescriptor<?>> List<Option<? extends ModuleAccessError>> descriptors(Iterable<D> moduleDescriptors, ModuleDescriptorVisitor<D> visitor) {
        ImmutableList.Builder listBuilder = ImmutableList.builder();
        for (ModuleDescriptor moduleDescriptor : moduleDescriptors) {
            listBuilder.add(this.visitDescriptorImpl(moduleDescriptor, visitor));
        }
        return listBuilder.build();
    }

    @Override
    public <D extends ModuleDescriptor<?>> Option<? extends ModuleAccessError> descriptor(D moduleDescriptor, ModuleDescriptorVisitor<D> visitor) {
        return this.visitDescriptorImpl(moduleDescriptor, visitor);
    }

    @Override
    public <RT, D extends ModuleDescriptor<?>> RT descriptor(D moduleDescriptor, ModuleDescriptorFunction<D, RT> callback) {
        AccumulatingDescriptorVisitor visitor = new AccumulatingDescriptorVisitor(callback);
        this.descriptor(moduleDescriptor, visitor);
        List result = visitor.getResults();
        return result.isEmpty() ? null : (RT)result.get(0);
    }

    @Override
    public <MT, D extends ModuleDescriptor<MT>> Option<? extends ModuleAccessError> descriptor(D moduleDescriptor, PluginPointVisitor<D, MT> visitor) {
        return this.visitPluginPointImpl(moduleDescriptor, visitor);
    }

    @Override
    public <MT, RT, D extends ModuleDescriptor<MT>> List<RT> modules(Iterable<MT> modules, PluginPointFunction<D, MT, RT> callback) {
        AccumulatingVisitor visitor = new AccumulatingVisitor(callback);
        this.modules(modules, visitor);
        return visitor.getResults();
    }

    @Override
    public <MT, RT, D extends ModuleDescriptor<MT>> RT module(MT module, PluginPointFunction<D, MT, RT> callback) {
        AccumulatingVisitor visitor = new AccumulatingVisitor(callback);
        this.visitModulesImpl(module, visitor);
        List result = visitor.getResults();
        return result.isEmpty() ? null : (RT)result.get(0);
    }

    @Override
    public <MT, D extends ModuleDescriptor<MT>> List<Option<? extends ModuleAccessError>> modules(Iterable<MT> modules, PluginPointVisitor<D, MT> visitor) {
        ImmutableList.Builder listBuilder = ImmutableList.builder();
        for (MT module : modules) {
            listBuilder.add(this.visitModulesImpl(module, visitor));
        }
        return listBuilder.build();
    }

    @Override
    public <MT, D extends ModuleDescriptor<MT>> Option<? extends ModuleAccessError> module(MT module, PluginPointVisitor<D, MT> visitor) {
        return this.visitModulesImpl(module, visitor);
    }

    private <MT, D extends ModuleDescriptor<MT>> Option<? extends ModuleAccessError> visitPluginPointImpl(@Nullable D moduleDescriptor, PluginPointVisitor<D, MT> visitor) {
        Object module;
        if (moduleDescriptor == null) {
            return Option.some((Object)new ModuleNotFoundError());
        }
        try {
            module = moduleDescriptor.getModule();
        }
        catch (ClassCastException e) {
            return Option.some((Object)new IncorrectModuleTypeError());
        }
        catch (Throwable t) {
            SafePluginPointAccess.handleException(t, moduleDescriptor);
            return Option.some((Object)new ModuleExceptionError(t));
        }
        return this.invokeModule(visitor, moduleDescriptor, module);
    }

    private <D extends ModuleDescriptor<?>> Option<? extends ModuleAccessError> visitDescriptorImpl(@Nullable D moduleDescriptor, ModuleDescriptorVisitor<D> visitor) {
        if (moduleDescriptor == null) {
            return Option.some((Object)new ModuleNotFoundError());
        }
        try {
            visitor.visit(moduleDescriptor);
        }
        catch (Throwable t) {
            SafePluginPointAccess.handleException(t);
            return Option.some((Object)new ModuleExceptionError(t));
        }
        return Option.none();
    }

    private <MT, D extends ModuleDescriptor<MT>> Option<? extends ModuleAccessError> visitModulesImpl(@Nullable MT module, @Nonnull PluginPointVisitor<D, MT> visitor) {
        if (module == null) {
            return Option.some((Object)new ModuleNotFoundError());
        }
        return this.invokeModule(visitor, null, module);
    }

    @Override
    @Deprecated
    @Nullable
    public <RT> RT callable(Callable<RT> callable) {
        return (RT)SafePluginPointAccess.call(callable).getOrNull();
    }

    public static <RT> Option<RT> call(Callable<RT> callable) {
        try {
            return Option.option(callable.call());
        }
        catch (Throwable t) {
            SafePluginPointAccess.handleException(t);
            return Option.none();
        }
    }

    @Override
    public void runnable(Runnable runnable) {
        try {
            runnable.run();
        }
        catch (Throwable t) {
            SafePluginPointAccess.handleException(t);
        }
    }

    public static <T> Predicate<T> safe(Predicate<T> base) {
        return input -> {
            try {
                return base.test(input);
            }
            catch (Throwable t) {
                SafePluginPointAccess.handleException(t);
                return false;
            }
        };
    }

    public static <T> Supplier<T> safe(Supplier<T> base) {
        return () -> {
            try {
                return base.get();
            }
            catch (Throwable t) {
                return SafePluginPointAccess.handleException(t);
            }
        };
    }

    public static <F, T> Function<F, T> safe(Function<F, T> base) {
        return from -> {
            try {
                return base.apply(from);
            }
            catch (Throwable t) {
                return SafePluginPointAccess.handleException(t);
            }
        };
    }

    @Nullable
    public static <T> T handleError(Error e, @Nullable ModuleDescriptor moduleDescriptor, @Nullable Object module) throws Error {
        if (e instanceof LinkageError) {
            String msg = moduleDescriptor == null ? String.format("%s Unable to run plugin code because of '%s - %s'.", "A LinkageError indicates that plugin code was compiled with outdated versions.", ThrowableLogger.getClassName(e), e.getMessage()) : (module == null ? String.format("%s This is for descriptor '%s' of class '%s' because of '%s - %s'.  Continuing...", "A LinkageError indicates that plugin code was compiled with outdated versions.", SafePluginPointAccess.completeKey(moduleDescriptor), ThrowableLogger.getClassName(moduleDescriptor), ThrowableLogger.getClassName(e), e.getMessage()) : String.format("%s Unable to access module of type '%s' in descriptor '%s' of class '%s' because of '%s - %s'.  Continuing...", "A LinkageError indicates that plugin code was compiled with outdated versions.", ThrowableLogger.getClassName(module), SafePluginPointAccess.completeKey(moduleDescriptor), ThrowableLogger.getClassName(moduleDescriptor), ThrowableLogger.getClassName(e), e.getMessage()));
            ThrowableLogger.logThrowable(msg, e, SafePluginPointAccess.getLogger(moduleDescriptor, module));
            return null;
        }
        throw e;
    }

    @Nullable
    public static <T> T handleException(Throwable e, @Nullable ModuleDescriptor moduleDescriptor, @Nullable Object module) throws Error {
        if (e instanceof Error) {
            return SafePluginPointAccess.handleError((Error)e, moduleDescriptor, module);
        }
        String msg = moduleDescriptor == null ? String.format("Unable to run plugin code because of '%s - %s'.", ThrowableLogger.getClassName(e), e.getMessage()) : (module == null ? String.format("Unable to access module for descriptor '%s' of class '%s' because of '%s - %s'.  Continuing...", SafePluginPointAccess.completeKey(moduleDescriptor), ThrowableLogger.getClassName(moduleDescriptor), ThrowableLogger.getClassName(e), e.getMessage()) : String.format("Unable to access module of type '%s' in descriptor '%s' of class '%s' because of '%s - %s'.  Continuing...", ThrowableLogger.getClassName(module), SafePluginPointAccess.completeKey(moduleDescriptor), ThrowableLogger.getClassName(moduleDescriptor), ThrowableLogger.getClassName(e), e.getMessage()));
        ThrowableLogger.logThrowable(msg, e, SafePluginPointAccess.getLogger(moduleDescriptor, module));
        return null;
    }

    @Nullable
    public static <T> T handleError(Error e) throws Error {
        return SafePluginPointAccess.handleError(e, null, null);
    }

    @Nullable
    public static <T> T handleError(Error e, @Nullable ModuleDescriptor moduleDescriptor) throws Error {
        return SafePluginPointAccess.handleError(e, moduleDescriptor, null);
    }

    @Nullable
    public static <T> T handleException(Throwable e) throws Error {
        return SafePluginPointAccess.handleException(e, null, null);
    }

    @Nullable
    public static <T> T handleException(Throwable e, ModuleDescriptor moduleDescriptor) throws Error {
        return SafePluginPointAccess.handleException(e, moduleDescriptor, null);
    }

    private <MT, D extends ModuleDescriptor<MT>> Option<? extends ModuleAccessError> invokeModule(PluginPointVisitor<D, MT> visitor, D moduleDescriptor, MT module) {
        try {
            visitor.visit(moduleDescriptor, module);
            return Option.none();
        }
        catch (Throwable t) {
            SafePluginPointAccess.handleException(t);
            return Option.some((Object)new ModuleExceptionError(t));
        }
    }

    private <D extends ModuleDescriptor<?>> List<D> getModuleDescriptors(Class<D> moduleDescriptorClass) {
        if (this.pluginAccessor == null) {
            throw new IllegalStateException("If you are going to call on the PluginAccessor then you must build this object with one!");
        }
        return this.pluginAccessor.getEnabledModuleDescriptorsByClass(moduleDescriptorClass);
    }

    private static <D extends ModuleDescriptor<?>, MT> Logger getLogger(@Nullable D moduleDescriptor, @Nullable MT module) {
        Class<Object> logClass = module != null ? module.getClass() : (moduleDescriptor != null ? moduleDescriptor.getClass() : SafePluginPointAccess.class);
        return LoggerFactory.getLogger(logClass);
    }

    private static <D extends ModuleDescriptor<?>> String completeKey(@Nullable D moduleDescriptor) {
        if (moduleDescriptor == null) {
            return "NULL";
        }
        return moduleDescriptor.getCompleteKey();
    }

    private static class AccumulatingDescriptorVisitor<D extends ModuleDescriptor<?>, RT>
    implements ModuleDescriptorVisitor<D> {
        private final List<RT> results = Lists.newArrayList();
        private final ModuleDescriptorFunction<D, RT> callback;

        private AccumulatingDescriptorVisitor(ModuleDescriptorFunction<D, RT> callback) {
            this.callback = callback;
        }

        @Override
        public void visit(@Nonnull D moduleDescriptor) {
            RT result = this.callback.onModuleDescriptor(moduleDescriptor);
            this.results.add(result);
        }

        private List<RT> getResults() {
            return this.results;
        }
    }

    private static class AccumulatingVisitor<D extends ModuleDescriptor<MT>, MT, RT>
    implements PluginPointVisitor<D, MT> {
        private final List<RT> results = Lists.newArrayList();
        private final PluginPointFunction<D, MT, RT> callback;

        private AccumulatingVisitor(PluginPointFunction<D, MT, RT> callback) {
            this.callback = callback;
        }

        @Override
        public void visit(@Nonnull D moduleDescriptor, @Nullable MT module) {
            RT result = this.callback.onModule(moduleDescriptor, module);
            this.results.add(result);
        }

        private List<RT> getResults() {
            return this.results;
        }
    }
}

