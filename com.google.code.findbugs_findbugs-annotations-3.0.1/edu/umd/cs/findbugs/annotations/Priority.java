/*
 * Decompiled with CFR 0.152.
 */
package edu.umd.cs.findbugs.annotations;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Deprecated
public enum Priority {
    HIGH(1),
    MEDIUM(2),
    LOW(3),
    IGNORE(5);

    private final int priorityValue;

    public int getPriorityValue() {
        return this.priorityValue;
    }

    private Priority(int p) {
        this.priorityValue = p;
    }
}

