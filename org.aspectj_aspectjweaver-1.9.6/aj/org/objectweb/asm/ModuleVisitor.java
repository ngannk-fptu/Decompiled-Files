/*
 * Decompiled with CFR 0.152.
 */
package aj.org.objectweb.asm;

import aj.org.objectweb.asm.Constants;

public abstract class ModuleVisitor {
    protected final int api;
    protected ModuleVisitor mv;

    public ModuleVisitor(int api) {
        this(api, null);
    }

    public ModuleVisitor(int api, ModuleVisitor moduleVisitor) {
        if (api != 524288 && api != 458752 && api != 393216 && api != 327680 && api != 262144 && api != 0x1090000) {
            throw new IllegalArgumentException("Unsupported api " + api);
        }
        if (api == 0x1090000) {
            Constants.checkAsmExperimental(this);
        }
        this.api = api;
        this.mv = moduleVisitor;
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

