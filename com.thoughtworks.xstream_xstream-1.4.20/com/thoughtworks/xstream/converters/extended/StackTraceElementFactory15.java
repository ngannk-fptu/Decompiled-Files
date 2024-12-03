/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.extended.StackTraceElementFactory;

class StackTraceElementFactory15
extends StackTraceElementFactory {
    StackTraceElementFactory15() {
    }

    protected StackTraceElement create(String declaringClass, String methodName, String fileName, int lineNumber) {
        return new StackTraceElement(declaringClass, methodName, fileName, lineNumber);
    }
}

