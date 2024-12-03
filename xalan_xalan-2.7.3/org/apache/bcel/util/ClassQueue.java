/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.util;

import java.util.LinkedList;
import org.apache.bcel.classfile.JavaClass;

public class ClassQueue {
    @Deprecated
    protected LinkedList<JavaClass> vec = new LinkedList();

    public JavaClass dequeue() {
        return this.vec.removeFirst();
    }

    public boolean empty() {
        return this.vec.isEmpty();
    }

    public void enqueue(JavaClass clazz) {
        this.vec.addLast(clazz);
    }

    public String toString() {
        return this.vec.toString();
    }
}

