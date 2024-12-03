/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarasm.asm.commons;

import groovyjarjarasm.asm.Label;

public interface TableSwitchGenerator {
    public void generateCase(int var1, Label var2);

    public void generateDefault();
}

