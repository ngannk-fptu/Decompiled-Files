/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.binding;

import org.codehaus.groovy.binding.DeadEndException;

class DeadEndObject {
    DeadEndObject() {
    }

    public Object getProperty(String property) {
        throw new DeadEndException("Cannot bind to a property on the return value of a method call");
    }

    public Object invokeMethod(String name, Object args) {
        return this;
    }
}

