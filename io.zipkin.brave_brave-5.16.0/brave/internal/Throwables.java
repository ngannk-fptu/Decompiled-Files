/*
 * Decompiled with CFR 0.152.
 */
package brave.internal;

public final class Throwables {
    public static void propagateIfFatal(Throwable t) {
        if (t instanceof VirtualMachineError) {
            throw (VirtualMachineError)t;
        }
        if (t instanceof ThreadDeath) {
            throw (ThreadDeath)t;
        }
        if (t instanceof LinkageError) {
            throw (LinkageError)t;
        }
    }

    Throwables() {
    }
}

