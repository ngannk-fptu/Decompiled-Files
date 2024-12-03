/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.base.log;

public class MemoryUsageMessage {
    private final String message;

    public MemoryUsageMessage(String message) {
        this.message = message;
    }

    public String toString() {
        return this.message + "Free: " + Runtime.getRuntime().freeMemory() + "; " + "Total: " + Runtime.getRuntime().totalMemory();
    }
}

