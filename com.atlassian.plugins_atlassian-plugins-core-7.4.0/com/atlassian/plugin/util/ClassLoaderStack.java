/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.util;

import java.util.Deque;
import java.util.LinkedList;

public class ClassLoaderStack {
    private static final ThreadLocal<Deque<ClassLoader>> CLASS_LOADER_STACK = new ThreadLocal();

    public static void push(ClassLoader loader) {
        if (loader == null) {
            return;
        }
        Deque<ClassLoader> stack = CLASS_LOADER_STACK.get();
        if (stack == null) {
            stack = new LinkedList<ClassLoader>();
            CLASS_LOADER_STACK.set(stack);
        }
        stack.push(Thread.currentThread().getContextClassLoader());
        Thread.currentThread().setContextClassLoader(loader);
    }

    public static ClassLoader pop() {
        Deque<ClassLoader> stack = CLASS_LOADER_STACK.get();
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(stack.pop());
        if (stack.isEmpty()) {
            CLASS_LOADER_STACK.remove();
        }
        return currentClassLoader;
    }
}

