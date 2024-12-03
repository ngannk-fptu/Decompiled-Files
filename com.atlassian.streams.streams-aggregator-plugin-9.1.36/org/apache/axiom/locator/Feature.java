/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.locator;

final class Feature {
    private final String name;
    private final int priority;

    Feature(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }

    String getName() {
        return this.name;
    }

    int getPriority() {
        return this.priority;
    }

    public String toString() {
        return this.name + "(priority=" + this.priority + ")";
    }
}

