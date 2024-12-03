/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.google.inject.internal;

import com.google.common.collect.Lists;
import com.google.inject.internal.CreationListener;
import com.google.inject.internal.Errors;
import java.util.List;

class ProcessedBindingData {
    private final List<CreationListener> creationListeners = Lists.newArrayList();
    private final List<Runnable> uninitializedBindings = Lists.newArrayList();

    ProcessedBindingData() {
    }

    void addCreationListener(CreationListener listener) {
        this.creationListeners.add(listener);
    }

    void addUninitializedBinding(Runnable runnable) {
        this.uninitializedBindings.add(runnable);
    }

    void initializeBindings() {
        for (Runnable initializer : this.uninitializedBindings) {
            initializer.run();
        }
    }

    void runCreationListeners(Errors errors) {
        for (CreationListener creationListener : this.creationListeners) {
            creationListener.notify(errors);
        }
    }
}

