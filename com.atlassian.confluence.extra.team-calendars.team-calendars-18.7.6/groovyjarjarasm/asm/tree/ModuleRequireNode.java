/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarasm.asm.tree;

import groovyjarjarasm.asm.ModuleVisitor;

public class ModuleRequireNode {
    public String module;
    public int access;
    public String version;

    public ModuleRequireNode(String module, int access, String version) {
        this.module = module;
        this.access = access;
        this.version = version;
    }

    public void accept(ModuleVisitor mv) {
        mv.visitRequire(this.module, this.access, this.version);
    }
}

