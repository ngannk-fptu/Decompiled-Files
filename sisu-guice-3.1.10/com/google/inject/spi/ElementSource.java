/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 */
package com.google.inject.spi;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.inject.internal.util.StackTraceElements;
import com.google.inject.spi.ModuleSource;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ElementSource {
    final ElementSource originalElementSource;
    final ModuleSource moduleSource;
    final StackTraceElements.InMemoryStackTraceElement[] partialCallStack;
    final Object declaringSource;

    ElementSource(ElementSource originalSource, Object declaringSource, ModuleSource moduleSource, StackTraceElement[] partialCallStack) {
        Preconditions.checkNotNull((Object)declaringSource, (Object)"declaringSource cannot be null.");
        Preconditions.checkNotNull((Object)moduleSource, (Object)"moduleSource cannot be null.");
        Preconditions.checkNotNull((Object)partialCallStack, (Object)"partialCallStack cannot be null.");
        this.originalElementSource = originalSource;
        this.declaringSource = declaringSource;
        this.moduleSource = moduleSource;
        this.partialCallStack = StackTraceElements.convertToInMemoryStackTraceElement(partialCallStack);
    }

    public ElementSource getOriginalElementSource() {
        return this.originalElementSource;
    }

    public Object getDeclaringSource() {
        return this.declaringSource;
    }

    public List<String> getModuleClassNames() {
        return this.moduleSource.getModuleClassNames();
    }

    public List<Integer> getModuleConfigurePositionsInStackTrace() {
        int size = this.moduleSource.size();
        Object[] positions = new Integer[size];
        int chunkSize = this.partialCallStack.length;
        positions[0] = chunkSize - 1;
        ModuleSource current = this.moduleSource;
        for (int cursor = 1; cursor < size; ++cursor) {
            chunkSize = current.getPartialCallStackSize();
            positions[cursor] = (Integer)positions[cursor - 1] + chunkSize;
            current = current.getParent();
        }
        return ImmutableList.copyOf((Object[])positions);
    }

    public StackTraceElement[] getStackTrace() {
        int modulesCallStackSize = this.moduleSource.getStackTraceSize();
        int chunkSize = this.partialCallStack.length;
        int size = this.moduleSource.getStackTraceSize() + chunkSize;
        StackTraceElement[] callStack = new StackTraceElement[size];
        System.arraycopy(StackTraceElements.convertToStackTraceElement(this.partialCallStack), 0, callStack, 0, chunkSize);
        System.arraycopy(this.moduleSource.getStackTrace(), 0, callStack, chunkSize, modulesCallStackSize);
        return callStack;
    }

    public String toString() {
        return this.getDeclaringSource().toString();
    }
}

