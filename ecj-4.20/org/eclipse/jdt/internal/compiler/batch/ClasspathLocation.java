/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.batch;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
import org.eclipse.jdt.internal.compiler.env.IModule;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.util.SuffixConstants;

public abstract class ClasspathLocation
implements FileSystem.Classpath,
SuffixConstants {
    public static final int SOURCE = 1;
    public static final int BINARY = 2;
    String path;
    char[] normalizedPath;
    public AccessRuleSet accessRuleSet;
    IModule module;
    public String destinationPath;

    protected ClasspathLocation(AccessRuleSet accessRuleSet, String destinationPath) {
        this.accessRuleSet = accessRuleSet;
        this.destinationPath = destinationPath;
    }

    protected AccessRestriction fetchAccessRestriction(String qualifiedBinaryFileName) {
        if (this.accessRuleSet == null) {
            return null;
        }
        char[] qualifiedTypeName = qualifiedBinaryFileName.substring(0, qualifiedBinaryFileName.length() - SUFFIX_CLASS.length).toCharArray();
        if (File.separatorChar == '\\') {
            CharOperation.replace(qualifiedTypeName, File.separatorChar, '/');
        }
        return this.accessRuleSet.getViolatedRestriction(qualifiedTypeName);
    }

    public int getMode() {
        return 3;
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + this.getMode();
        result = 31 * result + (this.path == null ? 0 : this.path.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ClasspathLocation other = (ClasspathLocation)obj;
        String localPath = this.getPath();
        String otherPath = other.getPath();
        if (localPath == null ? otherPath != null : !localPath.equals(otherPath)) {
            return false;
        }
        return this.getMode() == other.getMode();
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public String getDestinationPath() {
        return this.destinationPath;
    }

    @Override
    public void acceptModule(IModule mod) {
        this.module = mod;
    }

    @Override
    public boolean isAutomaticModule() {
        return this.module == null ? false : this.module.isAutomatic();
    }

    @Override
    public Collection<String> getModuleNames(Collection<String> limitModules) {
        return this.getModuleNames(limitModules, m -> this.getModule(m.toCharArray()));
    }

    @Override
    public Collection<String> getModuleNames(Collection<String> limitModules, Function<String, IModule> getModule) {
        if (this.module != null) {
            String name = String.valueOf(this.module.name());
            return this.selectModules(Collections.singleton(name), limitModules, getModule);
        }
        return Collections.emptyList();
    }

    protected Collection<String> selectModules(Set<String> modules, Collection<String> limitModules, Function<String, IModule> getModule) {
        Collection<String> rootModules;
        if (limitModules != null) {
            HashSet<String> result = new HashSet<String>(modules);
            result.retainAll(limitModules);
            rootModules = result;
        } else {
            rootModules = this.allModules(modules, s -> s, m -> this.getModule(m.toCharArray()));
        }
        HashSet<String> allModules = new HashSet<String>(rootModules);
        for (String mod : rootModules) {
            this.addRequired(mod, allModules, getModule);
        }
        return allModules;
    }

    private void addRequired(String mod, Set<String> allModules, Function<String, IModule> getModule) {
        IModule iMod = this.getModule(mod.toCharArray());
        if (iMod != null) {
            IModule.IModuleReference[] iModuleReferenceArray = iMod.requires();
            int n = iModuleReferenceArray.length;
            int n2 = 0;
            while (n2 < n) {
                String reqModName;
                IModule.IModuleReference requiredRef = iModuleReferenceArray[n2];
                IModule reqMod = getModule.apply(new String(requiredRef.name()));
                if (reqMod != null && allModules.add(reqModName = String.valueOf(reqMod.name()))) {
                    this.addRequired(reqModName, allModules, getModule);
                }
                ++n2;
            }
        }
    }

    protected <T> List<String> allModules(Iterable<T> allSystemModules, Function<T, String> getModuleName, Function<T, IModule> getModule) {
        ArrayList<String> result = new ArrayList<String>();
        for (T mod : allSystemModules) {
            String moduleName = getModuleName.apply(mod);
            result.add(moduleName);
        }
        return result;
    }

    @Override
    public boolean isPackage(String qualifiedPackageName, String moduleName) {
        return this.getModulesDeclaringPackage(qualifiedPackageName, moduleName) != null;
    }

    protected char[][] singletonModuleNameIf(boolean condition) {
        if (!condition) {
            return null;
        }
        if (this.module != null) {
            return new char[][]{this.module.name()};
        }
        return new char[][]{ModuleBinding.UNNAMED};
    }

    @Override
    public void reset() {
        this.module = null;
    }
}

