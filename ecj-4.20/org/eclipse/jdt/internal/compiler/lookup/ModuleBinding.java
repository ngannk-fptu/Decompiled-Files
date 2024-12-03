/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.env.IModuleAwareNameEnvironment;
import org.eclipse.jdt.internal.compiler.env.IUpdatableModule;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationHolder;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.PlainPackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SplitPackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.util.HashtableOfPackage;
import org.eclipse.jdt.internal.compiler.util.SimpleLookupTable;
import org.eclipse.jdt.internal.compiler.util.SimpleSetOfCharArray;

public class ModuleBinding
extends Binding
implements IUpdatableModule {
    public static final char[] UNNAMED = "".toCharArray();
    public static final char[] ALL_UNNAMED = "ALL-UNNAMED".toCharArray();
    public static final char[] ANY = "".toCharArray();
    public static final char[] ANY_NAMED = "".toCharArray();
    public static final char[] UNOBSERVABLE = "".toCharArray();
    public char[] moduleName;
    protected ModuleBinding[] requires;
    protected ModuleBinding[] requiresTransitive;
    protected PlainPackageBinding[] exportedPackages;
    private Map<PlainPackageBinding, SimpleSetOfCharArray> exportRestrictions;
    protected PlainPackageBinding[] openedPackages;
    private Map<PlainPackageBinding, SimpleSetOfCharArray> openRestrictions;
    protected TypeBinding[] uses;
    protected TypeBinding[] services;
    public Map<TypeBinding, TypeBinding[]> implementations;
    public char[] mainClassName;
    private SimpleSetOfCharArray packageNames;
    public int modifiers;
    public LookupEnvironment environment;
    public long tagBits;
    public int defaultNullness = 0;
    ModuleBinding[] requiredModules = null;
    boolean isAuto = false;
    private boolean[] isComplete = new boolean[IUpdatableModule.UpdateKind.values().length];
    private Set<ModuleBinding> transitiveRequires;
    SimpleLookupTable storedAnnotations = null;
    public HashtableOfPackage<PlainPackageBinding> declaredPackages;

    ModuleBinding(LookupEnvironment env) {
        this.moduleName = UNNAMED;
        this.environment = env;
        this.requires = Binding.NO_MODULES;
        this.requiresTransitive = Binding.NO_MODULES;
        this.exportedPackages = Binding.NO_PLAIN_PACKAGES;
        this.openedPackages = Binding.NO_PLAIN_PACKAGES;
        this.declaredPackages = new HashtableOfPackage();
        Arrays.fill(this.isComplete, true);
    }

    ModuleBinding(char[] moduleName) {
        this.moduleName = moduleName;
        this.requires = Binding.NO_MODULES;
        this.requiresTransitive = Binding.NO_MODULES;
        this.exportedPackages = Binding.NO_PLAIN_PACKAGES;
        this.openedPackages = Binding.NO_PLAIN_PACKAGES;
        this.uses = Binding.NO_TYPES;
        this.services = Binding.NO_TYPES;
        this.declaredPackages = new HashtableOfPackage(5);
    }

    protected ModuleBinding(char[] moduleName, LookupEnvironment existingEnvironment) {
        this.moduleName = moduleName;
        this.requires = Binding.NO_MODULES;
        this.requiresTransitive = Binding.NO_MODULES;
        this.environment = new LookupEnvironment(existingEnvironment.root, this);
        this.declaredPackages = new HashtableOfPackage(5);
    }

    public PlainPackageBinding[] getExports() {
        this.completeIfNeeded(IUpdatableModule.UpdateKind.PACKAGE);
        return this.exportedPackages;
    }

    public String[] getExportRestrictions(PackageBinding pack) {
        SimpleSetOfCharArray set;
        this.completeIfNeeded(IUpdatableModule.UpdateKind.PACKAGE);
        if (this.exportRestrictions != null && (set = this.exportRestrictions.get(pack)) != null) {
            char[][] names = new char[set.elementSize][];
            set.asArray((Object[])names);
            return CharOperation.charArrayToStringArray(names);
        }
        return CharOperation.NO_STRINGS;
    }

    public PlainPackageBinding[] getOpens() {
        this.completeIfNeeded(IUpdatableModule.UpdateKind.PACKAGE);
        return this.openedPackages;
    }

    public String[] getOpenRestrictions(PackageBinding pack) {
        SimpleSetOfCharArray set;
        this.completeIfNeeded(IUpdatableModule.UpdateKind.PACKAGE);
        if (this.openRestrictions != null && (set = this.openRestrictions.get(pack)) != null) {
            char[][] names = new char[set.elementSize][];
            set.asArray((Object[])names);
            return CharOperation.charArrayToStringArray(names);
        }
        return CharOperation.NO_STRINGS;
    }

    public TypeBinding[] getImplementations(TypeBinding binding) {
        if (this.implementations != null) {
            return this.implementations.get(binding);
        }
        return null;
    }

    public ModuleBinding[] getRequires() {
        this.completeIfNeeded(IUpdatableModule.UpdateKind.MODULE);
        return this.requires;
    }

    public ModuleBinding[] getRequiresTransitive() {
        this.completeIfNeeded(IUpdatableModule.UpdateKind.MODULE);
        return this.requiresTransitive;
    }

    public TypeBinding[] getUses() {
        return this.uses;
    }

    public TypeBinding[] getServices() {
        return this.services;
    }

    void completeIfNeeded(IUpdatableModule.UpdateKind kind) {
        if (!this.isComplete[kind.ordinal()]) {
            this.isComplete[kind.ordinal()] = true;
            if (this.environment.nameEnvironment instanceof IModuleAwareNameEnvironment) {
                ((IModuleAwareNameEnvironment)this.environment.nameEnvironment).applyModuleUpdates(this, kind);
            }
        }
    }

    @Override
    public void addReads(char[] requiredModuleName) {
        ModuleBinding requiredModule = this.environment.getModule(requiredModuleName);
        if (requiredModule != null) {
            int len = this.requires.length;
            if (len == 0) {
                this.requires = new ModuleBinding[]{requiredModule};
            } else {
                this.requires = new ModuleBinding[len + 1];
                System.arraycopy(this.requires, 0, this.requires, 0, len);
                this.requires[len] = requiredModule;
            }
        } else {
            this.environment.problemReporter.missingModuleAddReads(requiredModuleName);
            return;
        }
    }

    @Override
    public void addExports(char[] packageName, char[][] targetModules) {
        PlainPackageBinding declaredPackage = this.getOrCreateDeclaredPackage(CharOperation.splitOn('.', packageName));
        if (declaredPackage != null && declaredPackage.isValidBinding()) {
            this.addResolvedExport(declaredPackage, targetModules);
        }
    }

    @Override
    public void setMainClassName(char[] mainClassName) {
        this.mainClassName = mainClassName;
    }

    @Override
    public void setPackageNames(SimpleSetOfCharArray packageNames) {
        this.packageNames = packageNames;
    }

    public char[][] getPackageNamesForClassFile() {
        PlainPackageBinding packageBinding;
        if (this.packageNames == null) {
            return null;
        }
        PlainPackageBinding[] plainPackageBindingArray = this.exportedPackages;
        int n = this.exportedPackages.length;
        int n2 = 0;
        while (n2 < n) {
            packageBinding = plainPackageBindingArray[n2];
            this.packageNames.add(packageBinding.readableName());
            ++n2;
        }
        plainPackageBindingArray = this.openedPackages;
        n = this.openedPackages.length;
        n2 = 0;
        while (n2 < n) {
            packageBinding = plainPackageBindingArray[n2];
            this.packageNames.add(packageBinding.readableName());
            ++n2;
        }
        if (this.implementations != null) {
            Iterator<TypeBinding[]> iterator = this.implementations.values().iterator();
            while (iterator.hasNext()) {
                TypeBinding[] types;
                TypeBinding[] typeBindingArray = types = iterator.next();
                int n3 = types.length;
                int n4 = 0;
                while (n4 < n3) {
                    TypeBinding typeBinding = typeBindingArray[n4];
                    this.packageNames.add(((ReferenceBinding)typeBinding).fPackage.readableName());
                    ++n4;
                }
            }
        }
        return this.packageNames.values;
    }

    PlainPackageBinding createDeclaredToplevelPackage(char[] name) {
        PlainPackageBinding packageBinding = new PlainPackageBinding(name, this.environment, this);
        this.declaredPackages.put(name, packageBinding);
        return packageBinding;
    }

    PlainPackageBinding createDeclaredPackage(char[][] compoundName, PackageBinding parent) {
        PlainPackageBinding packageBinding = new PlainPackageBinding(compoundName, parent, this.environment, this);
        this.declaredPackages.put(CharOperation.concatWith(compoundName, '.'), packageBinding);
        return packageBinding;
    }

    public PlainPackageBinding getOrCreateDeclaredPackage(char[][] compoundName) {
        char[] flatName = CharOperation.concatWith(compoundName, '.');
        PlainPackageBinding pkgBinding = this.declaredPackages.get(flatName);
        if (pkgBinding != null) {
            return pkgBinding;
        }
        if (compoundName.length > 1) {
            PlainPackageBinding parent = this.getOrCreateDeclaredPackage(CharOperation.subarray(compoundName, 0, compoundName.length - 1));
            pkgBinding = new PlainPackageBinding(compoundName, parent, this.environment, this);
            parent.addPackage(pkgBinding, this);
        } else {
            pkgBinding = new PlainPackageBinding(compoundName[0], this.environment, this);
            PackageBinding problemPackage = this.environment.getPackage0(compoundName[0]);
            if (problemPackage == LookupEnvironment.TheNotFoundPackage) {
                this.environment.knownPackages.put(compoundName[0], null);
            }
        }
        this.declaredPackages.put(flatName, pkgBinding);
        return pkgBinding;
    }

    public void addResolvedExport(PlainPackageBinding declaredPackage, char[][] targetModules) {
        if (declaredPackage == null || !declaredPackage.isValidBinding()) {
            return;
        }
        if (this.exportedPackages == null || this.exportedPackages.length == 0) {
            this.exportedPackages = new PlainPackageBinding[]{declaredPackage};
        } else {
            int len = this.exportedPackages.length;
            this.exportedPackages = new PlainPackageBinding[len + 1];
            System.arraycopy(this.exportedPackages, 0, this.exportedPackages, 0, len);
            this.exportedPackages[len] = declaredPackage;
        }
        declaredPackage.isExported = Boolean.TRUE;
        this.recordExportRestrictions(declaredPackage, targetModules);
    }

    public void addResolvedOpens(PlainPackageBinding declaredPackage, char[][] targetModules) {
        int len = this.openedPackages.length;
        if (declaredPackage == null || !declaredPackage.isValidBinding()) {
            return;
        }
        if (len == 0) {
            this.openedPackages = new PlainPackageBinding[]{declaredPackage};
        } else {
            this.openedPackages = new PlainPackageBinding[len + 1];
            System.arraycopy(this.openedPackages, 0, this.openedPackages, 0, len);
            this.openedPackages[len] = declaredPackage;
        }
        this.recordOpensRestrictions(declaredPackage, targetModules);
    }

    protected void recordExportRestrictions(PlainPackageBinding exportedPackage, char[][] targetModules) {
        if (targetModules != null && targetModules.length > 0) {
            SimpleSetOfCharArray targetModuleSet = null;
            if (this.exportRestrictions != null) {
                targetModuleSet = this.exportRestrictions.get(exportedPackage);
            } else {
                this.exportRestrictions = new HashMap<PlainPackageBinding, SimpleSetOfCharArray>();
            }
            if (targetModuleSet == null) {
                targetModuleSet = new SimpleSetOfCharArray(targetModules.length);
                this.exportRestrictions.put(exportedPackage, targetModuleSet);
            }
            int i = 0;
            while (i < targetModules.length) {
                targetModuleSet.add(targetModules[i]);
                ++i;
            }
        }
    }

    protected void recordOpensRestrictions(PlainPackageBinding openedPackage, char[][] targetModules) {
        if (targetModules != null && targetModules.length > 0) {
            SimpleSetOfCharArray targetModuleSet = null;
            if (this.openRestrictions != null) {
                targetModuleSet = this.openRestrictions.get(openedPackage);
            } else {
                this.openRestrictions = new HashMap<PlainPackageBinding, SimpleSetOfCharArray>();
            }
            if (targetModuleSet == null) {
                targetModuleSet = new SimpleSetOfCharArray(targetModules.length);
                this.openRestrictions.put(openedPackage, targetModuleSet);
            }
            int i = 0;
            while (i < targetModules.length) {
                targetModuleSet.add(targetModules[i]);
                ++i;
            }
        }
    }

    Stream<ModuleBinding> getRequiredModules(boolean transitiveOnly) {
        return Stream.of(transitiveOnly ? this.getRequiresTransitive() : this.getRequires());
    }

    private void collectAllDependencies(Set<ModuleBinding> deps) {
        this.getRequiredModules(false).forEach(m -> {
            if (deps.add((ModuleBinding)m)) {
                m.collectAllDependencies(deps);
            }
        });
    }

    private void collectTransitiveDependencies(Set<ModuleBinding> deps) {
        this.getRequiredModules(true).forEach(m -> {
            if (deps.add((ModuleBinding)m)) {
                m.collectTransitiveDependencies(deps);
            }
        });
    }

    public Supplier<Collection<ModuleBinding>> dependencyGraphCollector() {
        return () -> this.getRequiredModules(false).collect(HashSet::new, (set, mod) -> {
            set.add(mod);
            mod.collectAllDependencies((Set<ModuleBinding>)set);
        }, AbstractCollection::addAll);
    }

    public Supplier<Collection<ModuleBinding>> dependencyCollector() {
        return () -> this.getRequiredModules(false).collect(HashSet::new, (set, mod) -> {
            set.add(mod);
            mod.collectTransitiveDependencies((Set<ModuleBinding>)set);
        }, AbstractCollection::addAll);
    }

    public ModuleBinding[] getAllRequiredModules() {
        if (this.requiredModules != null) {
            return this.requiredModules;
        }
        Collection<ModuleBinding> allRequires = this.dependencyCollector().get();
        if (allRequires.contains(this)) {
            return NO_MODULES;
        }
        ModuleBinding javaBase = this.environment.javaBaseModule();
        if (!CharOperation.equals(this.moduleName, TypeConstants.JAVA_BASE) && javaBase != null && javaBase != this.environment.UnNamedModule) {
            allRequires.add(javaBase);
        }
        this.requiredModules = allRequires.size() > 0 ? allRequires.toArray(new ModuleBinding[allRequires.size()]) : Binding.NO_MODULES;
        return this.requiredModules;
    }

    @Override
    public char[] name() {
        return this.moduleName;
    }

    public char[] nameForLookup() {
        return this.moduleName;
    }

    public char[] nameForCUCheck() {
        return this.nameForLookup();
    }

    public boolean isPackageExportedTo(PackageBinding pkg, ModuleBinding client) {
        PlainPackageBinding resolved = pkg.getIncarnation(this);
        if (resolved != null) {
            if (this.isAuto) {
                return pkg.enclosingModule == this;
            }
            PlainPackageBinding[] initializedExports = this.getExports();
            int i = 0;
            while (i < initializedExports.length) {
                PlainPackageBinding export = initializedExports[i];
                if (export.subsumes(resolved)) {
                    SimpleSetOfCharArray restrictions;
                    if (this.exportRestrictions != null && (restrictions = this.exportRestrictions.get(export)) != null) {
                        if (client.isUnnamed()) {
                            return restrictions.includes(ALL_UNNAMED);
                        }
                        return restrictions.includes(client.name());
                    }
                    return true;
                }
                ++i;
            }
        }
        return false;
    }

    public PackageBinding getTopLevelPackage(char[] name) {
        return this.getVisiblePackage(null, name);
    }

    PlainPackageBinding getDeclaredPackage(char[] flatName) {
        return this.declaredPackages.get(flatName);
    }

    PackageBinding getVisiblePackage(PackageBinding parent, char[] name) {
        PackageBinding pkg = parent != null ? parent.getPackage0(name) : this.environment.getPackage0(name);
        if (pkg != null) {
            if (pkg == LookupEnvironment.TheNotFoundPackage) {
                return null;
            }
            return pkg;
        }
        char[][] parentName = parent == null ? CharOperation.NO_CHAR_CHAR : parent.compoundName;
        char[][] subPkgCompoundName = CharOperation.arrayConcat(parentName, name);
        char[] fullFlatName = CharOperation.concatWith(subPkgCompoundName, '.');
        PackageBinding binding = this.declaredPackages.get(fullFlatName);
        char[][] declaringModuleNames = null;
        if (this.environment.useModuleSystem) {
            IModuleAwareNameEnvironment moduleEnv = (IModuleAwareNameEnvironment)this.environment.nameEnvironment;
            declaringModuleNames = moduleEnv.getUniqueModulesDeclaringPackage(subPkgCompoundName, this.nameForLookup());
            if (binding == null && declaringModuleNames != null) {
                if (CharOperation.containsEqual(declaringModuleNames, this.moduleName)) {
                    PlainPackageBinding singleParent;
                    if (parent != null && (singleParent = parent.getIncarnation(this)) != null && singleParent != parent) {
                        binding = singleParent.getPackage0(name);
                    }
                    if (binding == null) {
                        binding = this.createDeclaredPackage(subPkgCompoundName, parent);
                    }
                } else {
                    char[][] cArray = declaringModuleNames;
                    int n = declaringModuleNames.length;
                    int n2 = 0;
                    while (n2 < n) {
                        char[] declaringModuleName = cArray[n2];
                        ModuleBinding declaringModule = this.environment.root.getModule(declaringModuleName);
                        if (declaringModule != null) {
                            PlainPackageBinding declaredPackage = declaringModule.getDeclaredPackage(fullFlatName);
                            binding = SplitPackageBinding.combine(declaredPackage, binding, this);
                        }
                        ++n2;
                    }
                }
            }
        } else if (this.environment.nameEnvironment.isPackage(parentName, name)) {
            binding = this.createDeclaredPackage(subPkgCompoundName, parent);
        }
        binding = this.combineWithPackagesFromOtherRelevantModules(binding, subPkgCompoundName, declaringModuleNames);
        assert (binding == null || binding instanceof PlainPackageBinding || binding.enclosingModule == this);
        if (binding == null || !binding.isValidBinding()) {
            if (parent != null) {
                if (binding == null) {
                    parent.addNotFoundPackage(name);
                } else {
                    parent.knownPackages.put(name, binding);
                }
            } else {
                this.environment.knownPackages.put(name, LookupEnvironment.TheNotFoundPackage);
            }
            return null;
        }
        if (parentName.length == 0) {
            this.environment.knownPackages.put(name, binding);
        } else if (parent != null) {
            binding = parent.addPackage(binding, this);
        }
        return binding;
    }

    public PackageBinding getVisiblePackage(char[][] qualifiedPackageName) {
        if (qualifiedPackageName == null || qualifiedPackageName.length == 0) {
            return this.environment.defaultPackage;
        }
        PackageBinding parent = this.getTopLevelPackage(qualifiedPackageName[0]);
        if (parent == null) {
            return null;
        }
        int i = 1;
        while (i < qualifiedPackageName.length) {
            PackageBinding binding = this.getVisiblePackage(parent, qualifiedPackageName[i]);
            if (binding == null) {
                return null;
            }
            parent = binding;
            ++i;
        }
        return parent;
    }

    PackageBinding combineWithPackagesFromOtherRelevantModules(PackageBinding currentBinding, char[][] compoundName, char[][] declaringModuleNames) {
        for (ModuleBinding moduleBinding : this.otherRelevantModules(declaringModuleNames)) {
            PlainPackageBinding nextBinding = moduleBinding.getDeclaredPackage(CharOperation.concatWith(compoundName, '.'));
            currentBinding = SplitPackageBinding.combine(nextBinding, currentBinding, this);
        }
        return currentBinding;
    }

    List<ModuleBinding> otherRelevantModules(char[][] declaringModuleNames) {
        if (this.isUnnamed() && declaringModuleNames != null) {
            return Arrays.stream(declaringModuleNames).filter(modName -> modName != UNNAMED).map(modName -> this.environment.getModule((char[])modName)).filter(Objects::nonNull).collect(Collectors.toList());
        }
        return Arrays.asList(this.getAllRequiredModules());
    }

    public boolean canAccess(PackageBinding pkg) {
        if (pkg.isDeclaredIn(this)) {
            return true;
        }
        ModuleBinding[] moduleBindingArray = this.getAllRequiredModules();
        int n = moduleBindingArray.length;
        int n2 = 0;
        while (n2 < n) {
            ModuleBinding requiredModule = moduleBindingArray[n2];
            if (requiredModule.isPackageExportedTo(pkg, this)) {
                return true;
            }
            ++n2;
        }
        return false;
    }

    @Override
    public char[] computeUniqueKey(boolean isLeaf) {
        return CharOperation.prepend('\"', this.moduleName);
    }

    @Override
    public int kind() {
        return 64;
    }

    @Override
    public char[] readableName() {
        return this.moduleName;
    }

    public String toString() {
        char[] targetModule;
        int n;
        int n2;
        char[][] cArrayArray;
        char[][] allNames;
        int n3;
        int i;
        StringBuffer buffer = new StringBuffer(30);
        if (this.isOpen()) {
            buffer.append("open ");
        }
        buffer.append("module " + new String(this.readableName()));
        if (this.requires.length > 0) {
            buffer.append("\n/*    requires    */\n");
            i = 0;
            while (i < this.requires.length) {
                buffer.append("\n\t");
                if (this.requiresTransitive != null) {
                    ModuleBinding[] moduleBindingArray = this.requiresTransitive;
                    n3 = this.requiresTransitive.length;
                    int n4 = 0;
                    while (n4 < n3) {
                        ModuleBinding reqTrans = moduleBindingArray[n4];
                        if (reqTrans == this.requires[i]) {
                            buffer.append("transitive ");
                            break;
                        }
                        ++n4;
                    }
                }
                buffer.append(this.requires[i].moduleName);
                ++i;
            }
        } else {
            buffer.append("\nNo Requires");
        }
        if (this.exportedPackages != null && this.exportedPackages.length > 0) {
            buffer.append("\n/*    exports    */\n");
            i = 0;
            while (i < this.exportedPackages.length) {
                PlainPackageBinding export = this.exportedPackages[i];
                buffer.append("\n\t");
                if (export == null) {
                    buffer.append("<unresolved>");
                } else {
                    SimpleSetOfCharArray restrictions;
                    buffer.append(export.readableName());
                    SimpleSetOfCharArray simpleSetOfCharArray = restrictions = this.exportRestrictions != null ? this.exportRestrictions.get(export) : null;
                    if (restrictions != null) {
                        buffer.append(" to ");
                        String sep = "";
                        allNames = new char[restrictions.elementSize][];
                        restrictions.asArray((Object[])allNames);
                        cArrayArray = allNames;
                        n2 = allNames.length;
                        n = 0;
                        while (n < n2) {
                            targetModule = cArrayArray[n];
                            buffer.append(sep);
                            buffer.append(targetModule);
                            sep = ", ";
                            ++n;
                        }
                    }
                }
                ++i;
            }
        } else {
            buffer.append("\nNo Exports");
        }
        if (this.openedPackages != null && this.openedPackages.length > 0) {
            buffer.append("\n/*    exports    */\n");
            i = 0;
            while (i < this.openedPackages.length) {
                PlainPackageBinding opens = this.openedPackages[i];
                buffer.append("\n\t");
                if (opens == null) {
                    buffer.append("<unresolved>");
                } else {
                    SimpleSetOfCharArray restrictions;
                    buffer.append(opens.readableName());
                    SimpleSetOfCharArray simpleSetOfCharArray = restrictions = this.openRestrictions != null ? this.openRestrictions.get(opens) : null;
                    if (restrictions != null) {
                        buffer.append(" to ");
                        String sep = "";
                        allNames = new char[restrictions.elementSize][];
                        restrictions.asArray((Object[])allNames);
                        cArrayArray = allNames;
                        n2 = allNames.length;
                        n = 0;
                        while (n < n2) {
                            targetModule = cArrayArray[n];
                            buffer.append(sep);
                            buffer.append(targetModule);
                            sep = ", ";
                            ++n;
                        }
                    }
                }
                ++i;
            }
        } else {
            buffer.append("\nNo Opens");
        }
        if (this.uses != null && this.uses.length > 0) {
            buffer.append("\n/*    uses    /*\n");
            i = 0;
            while (i < this.uses.length) {
                buffer.append("\n\t");
                buffer.append(this.uses[i].debugName());
                ++i;
            }
        } else {
            buffer.append("\nNo Uses");
        }
        if (this.services != null && this.services.length > 0) {
            buffer.append("\n/*    Services    */\n");
            i = 0;
            while (i < this.services.length) {
                buffer.append("\n\t");
                buffer.append("provides ");
                buffer.append(this.services[i].debugName());
                buffer.append(" with ");
                if (this.implementations != null && this.implementations.containsKey(this.services[i])) {
                    String sep = "";
                    TypeBinding[] typeBindingArray = this.implementations.get(this.services[i]);
                    int n5 = typeBindingArray.length;
                    n3 = 0;
                    while (n3 < n5) {
                        TypeBinding impl = typeBindingArray[n3];
                        buffer.append(sep).append(impl.debugName());
                        sep = ", ";
                        ++n3;
                    }
                } else {
                    buffer.append("<missing implementations>");
                }
                ++i;
            }
        } else {
            buffer.append("\nNo Services");
        }
        return buffer.toString();
    }

    public boolean isUnnamed() {
        return false;
    }

    public boolean isOpen() {
        return (this.modifiers & 0x20) != 0;
    }

    public boolean isDeprecated() {
        return (this.tagBits & 0x400000000000L) != 0L;
    }

    public boolean hasUnstableAutoName() {
        return false;
    }

    public boolean isTransitivelyRequired(ModuleBinding otherModule) {
        if (this.transitiveRequires == null) {
            HashSet<ModuleBinding> transitiveDeps = new HashSet<ModuleBinding>();
            this.collectTransitiveDependencies(transitiveDeps);
            this.transitiveRequires = transitiveDeps;
        }
        return this.transitiveRequires.contains(otherModule);
    }

    public int getDefaultNullness() {
        this.getAnnotationTagBits();
        return this.defaultNullness;
    }

    SimpleLookupTable storedAnnotations(boolean forceInitialize, boolean forceStore) {
        if (forceInitialize && this.storedAnnotations == null) {
            if (!this.environment.globalOptions.storeAnnotations && !forceStore) {
                return null;
            }
            this.storedAnnotations = new SimpleLookupTable(3);
        }
        return this.storedAnnotations;
    }

    public AnnotationHolder retrieveAnnotationHolder(Binding binding, boolean forceInitialization) {
        SimpleLookupTable store = this.storedAnnotations(forceInitialization, false);
        return store == null ? null : (AnnotationHolder)store.get(binding);
    }

    AnnotationBinding[] retrieveAnnotations(Binding binding) {
        AnnotationHolder holder = this.retrieveAnnotationHolder(binding, true);
        return holder == null ? Binding.NO_ANNOTATIONS : holder.getAnnotations();
    }

    @Override
    public void setAnnotations(AnnotationBinding[] annotations, boolean forceStore) {
        this.storeAnnotations(this, annotations, forceStore);
    }

    void storeAnnotationHolder(Binding binding, AnnotationHolder holder) {
        if (holder == null) {
            SimpleLookupTable store = this.storedAnnotations(false, false);
            if (store != null) {
                store.removeKey(binding);
            }
        } else {
            SimpleLookupTable store = this.storedAnnotations(true, false);
            if (store != null) {
                store.put(binding, holder);
            }
        }
    }

    void storeAnnotations(Binding binding, AnnotationBinding[] annotations, boolean forceStore) {
        AnnotationHolder holder = null;
        if (annotations == null || annotations.length == 0) {
            SimpleLookupTable store = this.storedAnnotations(false, forceStore);
            if (store != null) {
                holder = (AnnotationHolder)store.get(binding);
            }
            if (holder == null) {
                return;
            }
        } else {
            SimpleLookupTable store = this.storedAnnotations(true, forceStore);
            if (store == null) {
                return;
            }
            holder = (AnnotationHolder)store.get(binding);
            if (holder == null) {
                holder = new AnnotationHolder();
            }
        }
        this.storeAnnotationHolder(binding, holder.setAnnotations(annotations));
    }

    public static class UnNamedModule
    extends ModuleBinding {
        private static final char[] UNNAMED_READABLE_NAME = "<unnamed>".toCharArray();

        UnNamedModule(LookupEnvironment env) {
            super(env);
        }

        @Override
        public ModuleBinding[] getAllRequiredModules() {
            return Binding.NO_MODULES;
        }

        @Override
        public boolean canAccess(PackageBinding pkg) {
            if (pkg instanceof SplitPackageBinding) {
                for (PackageBinding packageBinding : ((SplitPackageBinding)pkg).incarnations) {
                    if (!this.canAccess(packageBinding)) continue;
                    return true;
                }
                return false;
            }
            ModuleBinding moduleBinding = pkg.enclosingModule;
            if (moduleBinding != null && moduleBinding != this) {
                return moduleBinding.isPackageExportedTo(pkg, this);
            }
            return true;
        }

        @Override
        public boolean isPackageExportedTo(PackageBinding pkg, ModuleBinding client) {
            return pkg.isDeclaredIn(this) && pkg.hasCompilationUnit(false);
        }

        @Override
        PlainPackageBinding getDeclaredPackage(char[] flatName) {
            char[][] compoundName;
            IModuleAwareNameEnvironment moduleEnv;
            char[][] declaringModuleNames;
            PlainPackageBinding declaredPackage = super.getDeclaredPackage(flatName);
            if (declaredPackage == null && this.environment.useModuleSystem && (declaringModuleNames = (moduleEnv = (IModuleAwareNameEnvironment)this.environment.nameEnvironment).getUniqueModulesDeclaringPackage(compoundName = CharOperation.splitOn('.', flatName), this.nameForLookup())) != null && CharOperation.containsEqual(declaringModuleNames, this.moduleName)) {
                declaredPackage = this.getOrCreateDeclaredPackage(compoundName);
            }
            return declaredPackage;
        }

        @Override
        public boolean isUnnamed() {
            return true;
        }

        @Override
        public char[] nameForLookup() {
            return ANY;
        }

        @Override
        public char[] nameForCUCheck() {
            return UNNAMED;
        }

        @Override
        public char[] readableName() {
            return UNNAMED_READABLE_NAME;
        }

        @Override
        public String toString() {
            return "The Unnamed Module";
        }
    }
}

