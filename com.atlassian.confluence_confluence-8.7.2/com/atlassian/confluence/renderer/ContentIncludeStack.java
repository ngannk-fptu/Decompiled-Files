/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.renderer;

import com.atlassian.confluence.core.ContentEntityObject;
import java.util.Stack;

public class ContentIncludeStack {
    private static ThreadLocal threadLocal = new ThreadLocal();

    public static ContentEntityObject pop() {
        return (ContentEntityObject)ContentIncludeStack.getStack().pop();
    }

    public static void push(ContentEntityObject c) {
        ContentIncludeStack.getStack().push(c);
    }

    public static boolean contains(ContentEntityObject c) {
        return ContentIncludeStack.getStack().contains(c);
    }

    public static ContentEntityObject peek() {
        Stack stack = ContentIncludeStack.getStack();
        if (stack.size() == 0) {
            return null;
        }
        return (ContentEntityObject)ContentIncludeStack.getStack().peek();
    }

    private static Stack getStack() {
        Stack stack = (Stack)threadLocal.get();
        if (stack == null) {
            stack = new Stack();
            threadLocal.set(stack);
        }
        return stack;
    }

    public static boolean isEmpty() {
        return ContentIncludeStack.getStack().isEmpty();
    }
}

