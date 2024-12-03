/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarasm.asm.commons;

import groovyjarjarasm.asm.ModuleVisitor;
import groovyjarjarasm.asm.commons.Remapper;

public class ModuleRemapper
extends ModuleVisitor {
    private final Remapper remapper;

    public ModuleRemapper(ModuleVisitor mv, Remapper remapper) {
        this(393216, mv, remapper);
    }

    protected ModuleRemapper(int api, ModuleVisitor mv, Remapper remapper) {
        super(api, mv);
        this.remapper = remapper;
    }

    public void visitMainClass(String mainClass) {
        super.visitMainClass(this.remapper.mapType(mainClass));
    }

    public void visitPackage(String packaze) {
        super.visitPackage(this.remapper.mapPackageName(packaze));
    }

    public void visitRequire(String module, int access, String version) {
        super.visitRequire(this.remapper.mapModuleName(module), access, version);
    }

    public void visitExport(String packaze, int access, String ... modules) {
        String[] newModules = null;
        if (modules != null) {
            newModules = new String[modules.length];
            for (int i = 0; i < modules.length; ++i) {
                newModules[i] = this.remapper.mapModuleName(modules[i]);
            }
        }
        super.visitExport(this.remapper.mapPackageName(packaze), access, newModules);
    }

    public void visitOpen(String packaze, int access, String ... modules) {
        String[] newModules = null;
        if (modules != null) {
            newModules = new String[modules.length];
            for (int i = 0; i < modules.length; ++i) {
                newModules[i] = this.remapper.mapModuleName(modules[i]);
            }
        }
        super.visitOpen(this.remapper.mapPackageName(packaze), access, newModules);
    }

    public void visitUse(String service) {
        super.visitUse(this.remapper.mapType(service));
    }

    public void visitProvide(String service, String ... providers) {
        String[] newProviders = new String[providers.length];
        for (int i = 0; i < providers.length; ++i) {
            newProviders[i] = this.remapper.mapType(providers[i]);
        }
        super.visitProvide(this.remapper.mapType(service), newProviders);
    }
}

