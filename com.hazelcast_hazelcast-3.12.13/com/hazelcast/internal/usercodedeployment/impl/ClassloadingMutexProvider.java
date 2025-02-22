/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.usercodedeployment.impl;

import com.hazelcast.internal.usercodedeployment.impl.GlobalMutex;
import com.hazelcast.internal.util.JavaVersion;
import com.hazelcast.util.ContextMutexFactory;
import java.io.Closeable;

public class ClassloadingMutexProvider {
    private static final boolean USE_PARALLEL_LOADING = ClassloadingMutexProvider.isParallelClassLoadingPossible();
    private final ContextMutexFactory mutexFactory = new ContextMutexFactory();
    private final GlobalMutex globalMutex = new GlobalMutex();

    public Closeable getMutexForClass(String classname) {
        if (USE_PARALLEL_LOADING) {
            return this.mutexFactory.mutexFor(classname);
        }
        return this.globalMutex;
    }

    private static boolean isParallelClassLoadingPossible() {
        return JavaVersion.isAtLeast(JavaVersion.JAVA_1_7);
    }
}

