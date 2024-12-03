/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Pair
 *  com.atlassian.webresource.api.assembler.WebResourceSet
 *  io.atlassian.fugue.Pair
 */
package com.atlassian.confluence.plugin.webresource;

import com.atlassian.confluence.util.FugueConversionUtil;
import com.atlassian.webresource.api.assembler.WebResourceSet;
import io.atlassian.fugue.Pair;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public interface WebResourceDependenciesRecorder {
    @Deprecated
    default public <T> com.atlassian.fugue.Pair<T, RecordedResources> record(Callable<T> callback) throws Exception {
        return FugueConversionUtil.toComPair(this.recordResources(callback));
    }

    public <T> Pair<T, RecordedResources> recordResources(Callable<T> var1) throws Exception;

    @Deprecated
    default public <T> com.atlassian.fugue.Pair<T, RecordedResources> record(Iterable<String> additionalContexts, Iterable<String> additionalResources, Callable<T> callback) throws Exception {
        return FugueConversionUtil.toComPair(this.recordResources(additionalContexts, additionalResources, callback));
    }

    public <T> Pair<T, RecordedResources> recordResources(Iterable<String> var1, Iterable<String> var2, Callable<T> var3) throws Exception;

    @Deprecated
    default public <T> com.atlassian.fugue.Pair<T, RecordedResources> record(Iterable<String> additionalContexts, Iterable<String> additionalResources, boolean includeSuperbatch, Callable<T> callback) throws Exception {
        return FugueConversionUtil.toComPair(this.recordResources(additionalContexts, additionalResources, includeSuperbatch, callback));
    }

    public <T> Pair<T, RecordedResources> recordResources(Iterable<String> var1, Iterable<String> var2, boolean var3, Callable<T> var4) throws Exception;

    @Deprecated
    default public <T> com.atlassian.fugue.Pair<T, RecordedResources> record(Iterable<String> additionalContexts, Iterable<String> additionalResources, Iterable<String> excludeContexts, Iterable<String> excludeResources, boolean includeSuperbatch, Callable<T> callback) throws Exception {
        return FugueConversionUtil.toComPair(this.recordResources(additionalContexts, additionalResources, excludeContexts, excludeResources, includeSuperbatch, callback));
    }

    public <T> Pair<T, RecordedResources> recordResources(Iterable<String> var1, Iterable<String> var2, Iterable<String> var3, Iterable<String> var4, boolean var5, Callable<T> var6) throws Exception;

    public static interface RecordedResources {
        public Supplier<WebResourceSet> webresources();

        public Supplier<WebResourceSet> superbatch();

        public Iterable<String> contexts();

        public Iterable<String> resourceKeys();
    }
}

