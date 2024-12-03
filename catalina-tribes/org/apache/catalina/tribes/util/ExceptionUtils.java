/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.util;

public class ExceptionUtils {
    public static void handleThrowable(Throwable t) {
        if (t instanceof ThreadDeath) {
            throw (ThreadDeath)t;
        }
        if (t instanceof StackOverflowError) {
            return;
        }
        if (t instanceof VirtualMachineError) {
            throw (VirtualMachineError)t;
        }
    }
}

