/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.PlainPackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;

public class SplitPackageBinding
extends PackageBinding {
    Set<ModuleBinding> declaringModules = new HashSet<ModuleBinding>();
    public Set<PlainPackageBinding> incarnations = new HashSet<PlainPackageBinding>();

    public static PackageBinding combine(PackageBinding binding, PackageBinding previous, ModuleBinding primaryModule) {
        int curRank;
        int prevRank = SplitPackageBinding.rank(previous);
        if (prevRank < (curRank = SplitPackageBinding.rank(binding))) {
            return binding;
        }
        if (prevRank > curRank) {
            return previous;
        }
        if (previous == null) {
            return null;
        }
        if (previous.subsumes(binding)) {
            return previous;
        }
        if (binding.subsumes(previous)) {
            return binding;
        }
        SplitPackageBinding split = new SplitPackageBinding(previous, primaryModule);
        split.add(binding);
        return split;
    }

    private static int rank(PackageBinding candidate) {
        if (candidate == null) {
            return 0;
        }
        if (candidate == LookupEnvironment.TheNotFoundPackage) {
            return 1;
        }
        if (!candidate.isValidBinding()) {
            return 2;
        }
        return 3;
    }

    public SplitPackageBinding(PackageBinding initialBinding, ModuleBinding primaryModule) {
        super(initialBinding.compoundName, initialBinding.parent, primaryModule.environment, primaryModule);
        this.add(initialBinding);
    }

    public void add(PackageBinding packageBinding) {
        if (packageBinding instanceof SplitPackageBinding) {
            SplitPackageBinding split = (SplitPackageBinding)packageBinding;
            this.declaringModules.addAll(split.declaringModules);
            for (PlainPackageBinding incarnation : split.incarnations) {
                if (!this.incarnations.add(incarnation)) continue;
                incarnation.addWrappingSplitPackageBinding(this);
            }
        } else if (packageBinding instanceof PlainPackageBinding) {
            this.declaringModules.add(packageBinding.enclosingModule);
            if (this.incarnations.add((PlainPackageBinding)packageBinding)) {
                packageBinding.addWrappingSplitPackageBinding(this);
            }
        }
    }

    @Override
    PackageBinding addPackage(PackageBinding element, ModuleBinding module) {
        PlainPackageBinding elementIncarnation;
        char[] simpleName = element.compoundName[element.compoundName.length - 1];
        element = this.combineWithSiblings(element, simpleName, module);
        Object visible = this.knownPackages.get(simpleName);
        visible = SplitPackageBinding.combine(element, visible, this.enclosingModule);
        this.knownPackages.put(simpleName, (PackageBinding)visible);
        PlainPackageBinding incarnation = this.getIncarnation(element.enclosingModule);
        if (incarnation != null && (elementIncarnation = element.getIncarnation(element.enclosingModule)) != null) {
            incarnation.addPackage(elementIncarnation, module);
        }
        return element;
    }

    PackageBinding combineWithSiblings(PackageBinding childPackage, char[] name, ModuleBinding module) {
        ModuleBinding primaryModule = childPackage.enclosingModule;
        char[] flatName = CharOperation.concatWith(childPackage.compoundName, '.');
        for (PackageBinding packageBinding : this.incarnations) {
            ModuleBinding moduleBinding = packageBinding.enclosingModule;
            if (moduleBinding == module || childPackage.isDeclaredIn(moduleBinding)) continue;
            PlainPackageBinding next = moduleBinding.getDeclaredPackage(flatName);
            childPackage = SplitPackageBinding.combine(next, childPackage, primaryModule);
        }
        return childPackage;
    }

    @Override
    ModuleBinding[] getDeclaringModules() {
        return this.declaringModules.toArray(new ModuleBinding[this.declaringModules.size()]);
    }

    @Override
    PackageBinding getPackage0(char[] name) {
        PackageBinding knownPackage = super.getPackage0(name);
        if (knownPackage != null) {
            return knownPackage;
        }
        PackageBinding candidate = null;
        for (PackageBinding packageBinding : this.incarnations) {
            PackageBinding package0 = packageBinding.getPackage0(name);
            if (package0 == null) {
                return null;
            }
            candidate = SplitPackageBinding.combine(package0, candidate, this.enclosingModule);
        }
        if (candidate != null) {
            this.knownPackages.put(name, candidate);
        }
        return candidate;
    }

    @Override
    PackageBinding getPackage0Any(char[] name) {
        PackageBinding knownPackage = super.getPackage0(name);
        if (knownPackage != null) {
            return knownPackage;
        }
        PackageBinding candidate = null;
        for (PackageBinding packageBinding : this.incarnations) {
            PackageBinding package0 = packageBinding.getPackage0(name);
            if (package0 == null) continue;
            candidate = SplitPackageBinding.combine(package0, candidate, this.enclosingModule);
        }
        return candidate;
    }

    @Override
    protected PackageBinding findPackage(char[] name, ModuleBinding module) {
        char[][] subpackageCompoundName = CharOperation.arrayConcat(this.compoundName, name);
        HashSet<PackageBinding> candidates = new HashSet<PackageBinding>();
        for (ModuleBinding candidateModule : this.declaringModules) {
            PackageBinding candidate = candidateModule.getVisiblePackage(subpackageCompoundName);
            if (candidate == null || candidate == LookupEnvironment.TheNotFoundPackage || (candidate.tagBits & 0x80L) != 0L) continue;
            candidates.add(candidate);
        }
        int count = candidates.size();
        PackageBinding result = null;
        if (count == 1) {
            result = (PackageBinding)candidates.iterator().next();
        } else if (count > 1) {
            Iterator iterator = candidates.iterator();
            SplitPackageBinding split = new SplitPackageBinding((PackageBinding)iterator.next(), this.enclosingModule);
            while (iterator.hasNext()) {
                split.add((PackageBinding)iterator.next());
            }
            result = split;
        }
        if (result == null) {
            this.addNotFoundPackage(name);
        } else {
            this.addPackage(result, module);
        }
        return result;
    }

    @Override
    public PlainPackageBinding getIncarnation(ModuleBinding requestedModule) {
        for (PlainPackageBinding incarnation : this.incarnations) {
            if (incarnation.enclosingModule != requestedModule) continue;
            return incarnation;
        }
        return null;
    }

    @Override
    public boolean subsumes(PackageBinding binding) {
        if (!CharOperation.equals(this.compoundName, binding.compoundName)) {
            return false;
        }
        if (binding instanceof SplitPackageBinding) {
            return this.declaringModules.containsAll(((SplitPackageBinding)binding).declaringModules);
        }
        return this.declaringModules.contains(binding.enclosingModule);
    }

    @Override
    boolean hasType0Any(char[] name) {
        if (super.hasType0Any(name)) {
            return true;
        }
        for (PackageBinding packageBinding : this.incarnations) {
            if (!packageBinding.hasType0Any(name)) continue;
            return true;
        }
        return false;
    }

    ReferenceBinding getType0ForModule(ModuleBinding module, char[] name) {
        if (this.declaringModules.contains(module)) {
            return this.getIncarnation(module).getType0(name);
        }
        return null;
    }

    @Override
    ReferenceBinding getType(char[] name, ModuleBinding mod) {
        ReferenceBinding candidate = null;
        boolean accessible = false;
        for (PackageBinding packageBinding : this.incarnations) {
            ReferenceBinding type = packageBinding.getType(name, mod);
            if (type == null) continue;
            if (candidate == null || !accessible) {
                candidate = type;
                accessible = mod.canAccess(packageBinding);
                continue;
            }
            if (!mod.canAccess(packageBinding)) continue;
            return new ProblemReferenceBinding(type.compoundName, candidate, 3);
        }
        if (candidate != null && !accessible) {
            return new ProblemReferenceBinding(candidate.compoundName, candidate, 30);
        }
        return candidate;
    }

    @Override
    public boolean isDeclaredIn(ModuleBinding moduleBinding) {
        return this.declaringModules.contains(moduleBinding);
    }

    @Override
    public PackageBinding getVisibleFor(ModuleBinding clientModule, boolean preferLocal) {
        int visibleCount = 0;
        PlainPackageBinding unique = null;
        for (PlainPackageBinding incarnation : this.incarnations) {
            if (!incarnation.hasCompilationUnit(false)) continue;
            if (preferLocal && incarnation.enclosingModule == clientModule) {
                return incarnation;
            }
            if (!clientModule.canAccess(incarnation)) continue;
            ++visibleCount;
            unique = incarnation;
        }
        if (visibleCount > 1) {
            return this;
        }
        return unique;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(super.toString());
        buf.append(" (from ");
        String sep = "";
        for (ModuleBinding mod : this.declaringModules) {
            buf.append(sep).append(mod.readableName());
            sep = ", ";
        }
        buf.append(")");
        return buf.toString();
    }
}

