/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package com.google.inject.spi;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.inject.Module;
import com.google.inject.internal.util.StackTraceElements;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class ModuleSource {
    private final String moduleClassName;
    private final ModuleSource parent;
    private final StackTraceElements.InMemoryStackTraceElement[] partialCallStack;

    ModuleSource(Module module, StackTraceElement[] partialCallStack) {
        this(null, module, partialCallStack);
    }

    private ModuleSource(ModuleSource parent, Module module, StackTraceElement[] partialCallStack) {
        Preconditions.checkNotNull((Object)module, (Object)"module cannot be null.");
        Preconditions.checkNotNull((Object)partialCallStack, (Object)"partialCallStack cannot be null.");
        this.parent = parent;
        this.moduleClassName = module.getClass().getName();
        this.partialCallStack = StackTraceElements.convertToInMemoryStackTraceElement(partialCallStack);
    }

    String getModuleClassName() {
        return this.moduleClassName;
    }

    StackTraceElement[] getPartialCallStack() {
        return StackTraceElements.convertToStackTraceElement(this.partialCallStack);
    }

    int getPartialCallStackSize() {
        return this.partialCallStack.length;
    }

    ModuleSource createChild(Module module, StackTraceElement[] partialCallStack) {
        return new ModuleSource(this, module, partialCallStack);
    }

    ModuleSource getParent() {
        return this.parent;
    }

    List<String> getModuleClassNames() {
        ImmutableList.Builder classNames = ImmutableList.builder();
        ModuleSource current = this;
        while (current != null) {
            String className = current.moduleClassName;
            classNames.add((Object)className);
            current = current.parent;
        }
        return classNames.build();
    }

    int size() {
        if (this.parent == null) {
            return 1;
        }
        return this.parent.size() + 1;
    }

    int getStackTraceSize() {
        if (this.parent == null) {
            return this.partialCallStack.length;
        }
        return this.parent.getStackTraceSize() + this.partialCallStack.length;
    }

    StackTraceElement[] getStackTrace() {
        int stackTraceSize = this.getStackTraceSize();
        StackTraceElement[] callStack = new StackTraceElement[stackTraceSize];
        int cursor = 0;
        ModuleSource current = this;
        while (current != null) {
            StackTraceElement[] chunk = StackTraceElements.convertToStackTraceElement(current.partialCallStack);
            int chunkSize = chunk.length;
            System.arraycopy(chunk, 0, callStack, cursor, chunkSize);
            current = current.parent;
            cursor += chunkSize;
        }
        return callStack;
    }
}

