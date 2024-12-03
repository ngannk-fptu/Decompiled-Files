/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarasm.asm;

public abstract class ModuleVisitor {
    protected final int api;
    protected ModuleVisitor mv;

    public ModuleVisitor(int api) {
        this(api, null);
    }

    public ModuleVisitor(int api, ModuleVisitor mv) {
        if (api != 393216) {
            throw new IllegalArgumentException();
        }
        this.api = api;
        this.mv = mv;
    }

    public void visitMainClass(String mainClass) {
        if (this.mv != null) {
            this.mv.visitMainClass(mainClass);
        }
    }

    public void visitPackage(String packaze) {
        if (this.mv != null) {
            this.mv.visitPackage(packaze);
        }
    }

    public void visitRequire(String module, int access, String version) {
        if (this.mv != null) {
            this.mv.visitRequire(module, access, version);
        }
    }

    public void visitExport(String packaze, int access, String ... modules) {
        if (this.mv != null) {
            this.mv.visitExport(packaze, access, modules);
        }
    }

    public void visitOpen(String packaze, int access, String ... modules) {
        if (this.mv != null) {
            this.mv.visitOpen(packaze, access, modules);
        }
    }

    public void visitUse(String service) {
        if (this.mv != null) {
            this.mv.visitUse(service);
        }
    }

    public void visitProvide(String service, String ... providers) {
        if (this.mv != null) {
            this.mv.visitProvide(service, providers);
        }
    }

    public void visitEnd() {
        if (this.mv != null) {
            this.mv.visitEnd();
        }
    }
}

