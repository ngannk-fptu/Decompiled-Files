/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.ArrayList;
import java.util.function.Predicate;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.env.IModuleAwareNameEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.PlainPackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.SplitPackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.UnresolvedReferenceBinding;
import org.eclipse.jdt.internal.compiler.util.HashtableOfPackage;
import org.eclipse.jdt.internal.compiler.util.HashtableOfType;

public abstract class PackageBinding
extends Binding
implements TypeConstants {
    public long tagBits = 0L;
    public char[][] compoundName;
    PackageBinding parent;
    ArrayList<SplitPackageBinding> wrappingSplitPackageBindings;
    public LookupEnvironment environment;
    public HashtableOfType knownTypes;
    HashtableOfPackage<PackageBinding> knownPackages;
    private int defaultNullness = -1;
    public ModuleBinding enclosingModule;
    Boolean isExported;

    protected PackageBinding(char[][] compoundName, LookupEnvironment environment) {
        this.compoundName = compoundName;
        this.environment = environment;
    }

    public PackageBinding(char[][] compoundName, PackageBinding parent, LookupEnvironment environment, ModuleBinding enclosingModule) {
        this.compoundName = compoundName;
        this.parent = parent;
        this.environment = environment;
        this.knownTypes = null;
        this.knownPackages = new HashtableOfPackage(3);
        if (compoundName != CharOperation.NO_CHAR_CHAR) {
            this.checkIfNullAnnotationPackage();
        }
        if (enclosingModule != null) {
            this.enclosingModule = enclosingModule;
        } else if (parent != null) {
            this.enclosingModule = parent.enclosingModule;
        }
        if (this.enclosingModule == null) {
            throw new IllegalStateException("Package should have an enclosing module");
        }
    }

    protected void addNotFoundPackage(char[] simpleName) {
        if (!this.environment.suppressImportErrors) {
            this.knownPackages.put(simpleName, LookupEnvironment.TheNotFoundPackage);
        }
    }

    private void addNotFoundType(char[] simpleName) {
        if (this.environment.suppressImportErrors) {
            return;
        }
        if (this.knownTypes == null) {
            this.knownTypes = new HashtableOfType(25);
        }
        this.knownTypes.put(simpleName, LookupEnvironment.TheNotFoundType);
    }

    PackageBinding addPackage(PackageBinding element, ModuleBinding module) {
        if ((element.tagBits & 0x80L) == 0L) {
            this.clearMissingTagBit();
        }
        this.knownPackages.put(element.compoundName[element.compoundName.length - 1], element);
        return element;
    }

    void addType(ReferenceBinding element) {
        char[] name;
        ReferenceBinding priorType;
        if ((element.tagBits & 0x80L) == 0L) {
            this.clearMissingTagBit();
        }
        if (this.knownTypes == null) {
            this.knownTypes = new HashtableOfType(25);
        }
        if ((priorType = this.knownTypes.getput(name = element.compoundName[element.compoundName.length - 1], element)) != null && priorType.isUnresolvedType() && !element.isUnresolvedType()) {
            ((UnresolvedReferenceBinding)priorType).setResolvedType(element, this.environment);
        }
        if (this.environment.globalOptions.isAnnotationBasedNullAnalysisEnabled && (element.isAnnotationType() || element instanceof UnresolvedReferenceBinding)) {
            this.checkIfNullAnnotationType(element);
        }
        if (!element.isUnresolvedType() && this.wrappingSplitPackageBindings != null) {
            for (SplitPackageBinding splitPackageBinding : this.wrappingSplitPackageBindings) {
                ReferenceBinding prior;
                if (splitPackageBinding.knownTypes == null || (prior = splitPackageBinding.knownTypes.get(name)) == null || !prior.isUnresolvedType() || element.isUnresolvedType()) continue;
                ((UnresolvedReferenceBinding)prior).setResolvedType(element, this.environment);
                splitPackageBinding.knownTypes.put(name, null);
            }
        }
    }

    ModuleBinding[] getDeclaringModules() {
        return new ModuleBinding[]{this.enclosingModule};
    }

    void clearMissingTagBit() {
        PackageBinding current = this;
        do {
            current.tagBits &= 0xFFFFFFFFFFFFFF7FL;
        } while ((current = current.parent) != null);
    }

    @Override
    public char[] computeUniqueKey(boolean isLeaf) {
        return CharOperation.concatWith(this.compoundName, '/');
    }

    protected PackageBinding findPackage(char[] name, ModuleBinding module) {
        return module.getVisiblePackage(CharOperation.arrayConcat(this.compoundName, name));
    }

    PackageBinding getPackage(char[] name, ModuleBinding mod) {
        PackageBinding binding = this.getPackage0(name);
        if (binding != null) {
            if (binding == LookupEnvironment.TheNotFoundPackage) {
                return null;
            }
            return binding;
        }
        binding = this.findPackage(name, mod);
        if (binding != null) {
            return binding;
        }
        this.addNotFoundPackage(name);
        return null;
    }

    PackageBinding getPackage0(char[] name) {
        return this.knownPackages.get(name);
    }

    PackageBinding getPackage0Any(char[] name) {
        return this.knownPackages.get(name);
    }

    ReferenceBinding getType(char[] name, ModuleBinding mod) {
        ReferenceBinding referenceBinding = this.getType0(name);
        if (referenceBinding == null && (referenceBinding = this.environment.askForType(this, name, mod)) == null) {
            this.addNotFoundType(name);
            return null;
        }
        if (referenceBinding == LookupEnvironment.TheNotFoundType) {
            return null;
        }
        if ((referenceBinding = (ReferenceBinding)BinaryTypeBinding.resolveType(referenceBinding, this.environment, false)).isNestedType()) {
            return new ProblemReferenceBinding(new char[][]{name}, referenceBinding, 4);
        }
        if (!mod.canAccess(this)) {
            return new ProblemReferenceBinding(referenceBinding.compoundName, referenceBinding, 30);
        }
        return referenceBinding;
    }

    ReferenceBinding getType0(char[] name) {
        if (this.knownTypes == null) {
            return null;
        }
        return this.knownTypes.get(name);
    }

    boolean hasType0Any(char[] name) {
        ReferenceBinding type0 = this.getType0(name);
        return type0 != null && type0.isValidBinding() && !(type0 instanceof UnresolvedReferenceBinding);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public Binding getTypeOrPackage(char[] name, ModuleBinding mod, boolean splitPackageAllowed) {
        PackageBinding packageBinding;
        ProblemReferenceBinding problemBinding = null;
        ReferenceBinding referenceBinding = this.getType0(name);
        if (referenceBinding != null && referenceBinding != LookupEnvironment.TheNotFoundType) {
            boolean isSameModule;
            if ((referenceBinding = (ReferenceBinding)BinaryTypeBinding.resolveType(referenceBinding, this.environment, false)).isNestedType()) {
                return new ProblemReferenceBinding(new char[][]{name}, referenceBinding, 4);
            }
            boolean bl = this instanceof SplitPackageBinding ? referenceBinding.module() == mod : (isSameModule = this.enclosingModule == mod);
            if (!isSameModule && referenceBinding.isValidBinding() && !mod.canAccess(referenceBinding.fPackage)) {
                problemBinding = new ProblemReferenceBinding(referenceBinding.compoundName, referenceBinding, 30);
            } else if ((referenceBinding.tagBits & 0x80L) == 0L) {
                return referenceBinding;
            }
        }
        if ((packageBinding = this.getPackage0(name)) != null && packageBinding != LookupEnvironment.TheNotFoundPackage) {
            if (splitPackageAllowed) return packageBinding;
            return packageBinding.getVisibleFor(mod, false);
        }
        if (referenceBinding == null && problemBinding == null) {
            referenceBinding = this.environment.askForType(this, name, mod);
            if (referenceBinding != null) {
                if (referenceBinding.isNestedType()) {
                    return new ProblemReferenceBinding(new char[][]{name}, referenceBinding, 4);
                }
                if (!referenceBinding.isValidBinding() || mod.canAccess(referenceBinding.fPackage)) return referenceBinding;
                problemBinding = new ProblemReferenceBinding(referenceBinding.compoundName, referenceBinding, 30);
            } else {
                this.addNotFoundType(name);
            }
        }
        if (packageBinding != null) return problemBinding;
        packageBinding = this.findPackage(name, mod);
        if (packageBinding != null) {
            if (splitPackageAllowed) return packageBinding;
            return packageBinding.getVisibleFor(mod, false);
        }
        if (referenceBinding != null && referenceBinding != LookupEnvironment.TheNotFoundType) {
            if (problemBinding == null) return referenceBinding;
            return problemBinding;
        }
        this.addNotFoundPackage(name);
        return problemBinding;
    }

    public final boolean isViewedAsDeprecated() {
        if ((this.tagBits & 0x400000000L) == 0L) {
            ReferenceBinding packageInfo;
            this.tagBits |= 0x400000000L;
            if (this.compoundName != CharOperation.NO_CHAR_CHAR && (packageInfo = this.getType(TypeConstants.PACKAGE_INFO_NAME, this.enclosingModule)) != null) {
                packageInfo.initializeDeprecatedAnnotationTagBits();
                this.tagBits |= packageInfo.tagBits & 0x77FFFFF840000000L;
            }
        }
        return (this.tagBits & 0x400000000000L) != 0L;
    }

    private void initDefaultNullness() {
        if (this.defaultNullness == -1) {
            ReferenceBinding packageInfo = this.getType(TypeConstants.PACKAGE_INFO_NAME, this.enclosingModule);
            if (packageInfo != null) {
                packageInfo.getAnnotationTagBits();
                this.defaultNullness = packageInfo instanceof SourceTypeBinding ? ((SourceTypeBinding)packageInfo).defaultNullness : ((BinaryTypeBinding)packageInfo).defaultNullness;
            } else {
                this.defaultNullness = 0;
            }
        }
    }

    public int getDefaultNullness() {
        this.initDefaultNullness();
        if (this.defaultNullness == 0) {
            return this.enclosingModule.getDefaultNullness();
        }
        return this.defaultNullness;
    }

    public void setDefaultNullness(int nullness) {
        this.defaultNullness = nullness;
    }

    public Binding findDefaultNullnessTarget(Predicate<Integer> predicate) {
        this.initDefaultNullness();
        if (predicate.test(this.defaultNullness)) {
            return this;
        }
        if (this.defaultNullness == 0 && predicate.test(this.enclosingModule.getDefaultNullness())) {
            return this.enclosingModule;
        }
        return null;
    }

    @Override
    public final int kind() {
        return 16;
    }

    @Override
    public int problemId() {
        if ((this.tagBits & 0x80L) != 0L) {
            return 1;
        }
        return 0;
    }

    void checkIfNullAnnotationPackage() {
        LookupEnvironment env = this.environment;
        if (env.globalOptions.isAnnotationBasedNullAnalysisEnabled) {
            if (this.isPackageOfQualifiedTypeName(this.compoundName, env.getNullableAnnotationName())) {
                env.nullableAnnotationPackage = this;
            }
            if (this.isPackageOfQualifiedTypeName(this.compoundName, env.getNonNullAnnotationName())) {
                env.nonnullAnnotationPackage = this;
            }
            if (this.isPackageOfQualifiedTypeName(this.compoundName, env.getNonNullByDefaultAnnotationName())) {
                env.nonnullByDefaultAnnotationPackage = this;
            }
        }
    }

    private boolean isPackageOfQualifiedTypeName(char[][] packageName, char[][] typeName) {
        int length;
        if (typeName == null || (length = packageName.length) != typeName.length - 1) {
            return false;
        }
        int i = 0;
        while (i < length) {
            if (!CharOperation.equals(packageName[i], typeName[i])) {
                return false;
            }
            ++i;
        }
        return true;
    }

    void checkIfNullAnnotationType(ReferenceBinding type) {
        if (this.environment.nullableAnnotationPackage == this && CharOperation.equals(type.compoundName, this.environment.getNullableAnnotationName())) {
            type.typeBits |= 0x40;
            if (!(type instanceof UnresolvedReferenceBinding)) {
                this.environment.nullableAnnotationPackage = null;
            }
        } else if (this.environment.nonnullAnnotationPackage == this && CharOperation.equals(type.compoundName, this.environment.getNonNullAnnotationName())) {
            type.typeBits |= 0x20;
            if (!(type instanceof UnresolvedReferenceBinding)) {
                this.environment.nonnullAnnotationPackage = null;
            }
        } else if (this.environment.nonnullByDefaultAnnotationPackage == this && CharOperation.equals(type.compoundName, this.environment.getNonNullByDefaultAnnotationName())) {
            type.typeBits |= 0x80;
            if (!(type instanceof UnresolvedReferenceBinding)) {
                this.environment.nonnullByDefaultAnnotationPackage = null;
            }
        } else {
            type.typeBits |= this.environment.getNullAnnotationBit(type.compoundName);
        }
    }

    @Override
    public char[] readableName() {
        return CharOperation.concatWith(this.compoundName, '.');
    }

    public String toString() {
        String str = this.compoundName == CharOperation.NO_CHAR_CHAR ? "The Default Package" : "package " + (this.compoundName != null ? CharOperation.toString(this.compoundName) : "UNNAMED");
        if ((this.tagBits & 0x80L) != 0L) {
            str = String.valueOf(str) + "[MISSING]";
        }
        return str;
    }

    public boolean isDeclaredIn(ModuleBinding moduleBinding) {
        return this.enclosingModule == moduleBinding;
    }

    public boolean subsumes(PackageBinding binding) {
        return binding == this;
    }

    public boolean isExported() {
        if (this.isExported == null) {
            if (this.enclosingModule.isAuto) {
                this.isExported = Boolean.TRUE;
            } else {
                this.enclosingModule.getExports();
                if (this.isExported == null) {
                    this.isExported = Boolean.FALSE;
                }
            }
        }
        return this.isExported == Boolean.TRUE;
    }

    public PackageBinding getVisibleFor(ModuleBinding module, boolean preferLocal) {
        return this;
    }

    public abstract PlainPackageBinding getIncarnation(ModuleBinding var1);

    public boolean hasCompilationUnit(boolean checkCUs) {
        if (this.knownTypes != null) {
            ReferenceBinding[] referenceBindingArray = this.knownTypes.valueTable;
            int n = this.knownTypes.valueTable.length;
            int n2 = 0;
            while (n2 < n) {
                ReferenceBinding knownType = referenceBindingArray[n2];
                if (knownType != null && knownType != LookupEnvironment.TheNotFoundType && !knownType.isUnresolvedType()) {
                    return true;
                }
                ++n2;
            }
        }
        if (this.environment.useModuleSystem) {
            IModuleAwareNameEnvironment moduleEnv = (IModuleAwareNameEnvironment)this.environment.nameEnvironment;
            return moduleEnv.hasCompilationUnit(this.compoundName, this.enclosingModule.nameForCUCheck(), checkCUs);
        }
        return false;
    }

    public void addWrappingSplitPackageBinding(SplitPackageBinding splitPackageBinding) {
        if (this.wrappingSplitPackageBindings == null) {
            this.wrappingSplitPackageBindings = new ArrayList();
        }
        this.wrappingSplitPackageBindings.add(splitPackageBinding);
    }
}

