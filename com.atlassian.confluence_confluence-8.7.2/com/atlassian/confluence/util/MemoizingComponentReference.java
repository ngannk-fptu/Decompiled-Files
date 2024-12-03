/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  io.atlassian.util.concurrent.Lazy
 */
package com.atlassian.confluence.util;

import com.atlassian.spring.container.ContainerManager;
import io.atlassian.util.concurrent.Lazy;
import java.util.function.Supplier;

public class MemoizingComponentReference {
    public static <T> Supplier<T> containerComponent(String name) {
        return Lazy.supplier(() -> ContainerManager.getComponent((String)name));
    }
}

