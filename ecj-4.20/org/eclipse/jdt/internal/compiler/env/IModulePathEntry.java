/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.env;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.env.IModule;

public interface IModulePathEntry {
    default public IModule getModule() {
        return null;
    }

    default public IModule getModule(char[] name) {
        IModule mod = this.getModule();
        if (mod != null && CharOperation.equals(name, mod.name())) {
            return mod;
        }
        return null;
    }

    default public boolean servesModule(char[] name) {
        return this.getModule(name) != null;
    }

    public char[][] getModulesDeclaringPackage(String var1, String var2);

    public boolean hasCompilationUnit(String var1, String var2);

    default public char[][] listPackages() {
        return CharOperation.NO_CHAR_CHAR;
    }

    default public boolean isAutomaticModule() {
        return false;
    }
}

