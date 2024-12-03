/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.util;

import java.util.Stack;
import org.apache.bcel.classfile.JavaClass;

public class ClassStack {
    private final Stack<JavaClass> stack = new Stack();

    public boolean empty() {
        return this.stack.empty();
    }

    public JavaClass pop() {
        return this.stack.pop();
    }

    public void push(JavaClass clazz) {
        this.stack.push(clazz);
    }

    public JavaClass top() {
        return this.stack.peek();
    }
}

