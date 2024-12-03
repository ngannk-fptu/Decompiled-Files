/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.filters;

public abstract class Filter {
    public abstract void append(StringBuilder var1);

    public String toString() {
        StringBuilder builder = new StringBuilder();
        this.append(builder);
        return builder.toString();
    }
}

