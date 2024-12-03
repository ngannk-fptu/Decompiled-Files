/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.env;

import java.util.jar.Manifest;
import org.eclipse.jdt.internal.compiler.env.AutomaticModuleNaming;

public interface IModule {
    public static final IModuleReference[] NO_MODULE_REFS = new IModuleReference[0];
    public static final IPackageExport[] NO_EXPORTS = new IPackageExport[0];
    public static final char[][] NO_USES = new char[0][];
    public static final IService[] NO_PROVIDES = new IService[0];
    public static final IModule[] NO_MODULES = new IModule[0];
    public static final IPackageExport[] NO_OPENS = new IPackageExport[0];
    public static final String MODULE_INFO = "module-info";
    public static final String MODULE_INFO_JAVA = "module-info.java";
    public static final String MODULE_INFO_CLASS = "module-info.class";

    public char[] name();

    public IModuleReference[] requires();

    public IPackageExport[] exports();

    public char[][] uses();

    public IService[] provides();

    public IPackageExport[] opens();

    default public boolean isAutomatic() {
        return false;
    }

    default public boolean isAutoNameFromManifest() {
        return false;
    }

    public boolean isOpen();

    public static IModule createAutomatic(char[] moduleName, boolean fromManifest) {
        final class AutoModule
        implements IModule {
            char[] name;
            boolean nameFromManifest;

            public AutoModule(char[] name, boolean nameFromManifest) {
                this.name = name;
                this.nameFromManifest = nameFromManifest;
            }

            @Override
            public char[] name() {
                return this.name;
            }

            @Override
            public IModuleReference[] requires() {
                return NO_MODULE_REFS;
            }

            @Override
            public IPackageExport[] exports() {
                return NO_EXPORTS;
            }

            @Override
            public char[][] uses() {
                return NO_USES;
            }

            @Override
            public IService[] provides() {
                return NO_PROVIDES;
            }

            @Override
            public IPackageExport[] opens() {
                return NO_OPENS;
            }

            @Override
            public boolean isAutomatic() {
                return true;
            }

            @Override
            public boolean isAutoNameFromManifest() {
                return this.nameFromManifest;
            }

            @Override
            public boolean isOpen() {
                return false;
            }
        }
        return new AutoModule(moduleName, fromManifest);
    }

    public static IModule createAutomatic(String fileName, boolean isFile, Manifest manifest) {
        boolean fromManifest = true;
        char[] inferredName = AutomaticModuleNaming.determineAutomaticModuleNameFromManifest(manifest);
        if (inferredName == null) {
            fromManifest = false;
            inferredName = AutomaticModuleNaming.determineAutomaticModuleNameFromFileName(fileName, true, isFile);
        }
        return IModule.createAutomatic(inferredName, fromManifest);
    }

    public static interface IModuleReference {
        public char[] name();

        default public boolean isTransitive() {
            return (this.getModifiers() & 0x20) != 0;
        }

        public int getModifiers();

        default public boolean isStatic() {
            return (this.getModifiers() & 0x40) != 0;
        }
    }

    public static interface IPackageExport {
        public char[] name();

        public char[][] targets();

        default public boolean isQualified() {
            char[][] targets = this.targets();
            return targets != null && targets.length > 0;
        }
    }

    public static interface IService {
        public char[] name();

        public char[][] with();
    }
}

