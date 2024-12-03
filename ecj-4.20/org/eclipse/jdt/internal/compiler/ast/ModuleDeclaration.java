/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ExportsStatement;
import org.eclipse.jdt.internal.compiler.ast.ModuleReference;
import org.eclipse.jdt.internal.compiler.ast.OpensStatement;
import org.eclipse.jdt.internal.compiler.ast.PackageVisibilityStatement;
import org.eclipse.jdt.internal.compiler.ast.ProvidesStatement;
import org.eclipse.jdt.internal.compiler.ast.RequiresStatement;
import org.eclipse.jdt.internal.compiler.ast.UsesStatement;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.ModuleScope;
import org.eclipse.jdt.internal.compiler.lookup.PlainPackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilationUnit;
import org.eclipse.jdt.internal.compiler.problem.AbortMethod;
import org.eclipse.jdt.internal.compiler.problem.AbortType;
import org.eclipse.jdt.internal.compiler.util.HashtableOfObject;

public class ModuleDeclaration
extends ASTNode
implements ReferenceContext {
    public ExportsStatement[] exports;
    public RequiresStatement[] requires;
    public UsesStatement[] uses;
    public ProvidesStatement[] services;
    public OpensStatement[] opens;
    public Annotation[] annotations;
    public int exportsCount;
    public int requiresCount;
    public int usesCount;
    public int servicesCount;
    public int opensCount;
    public SourceModuleBinding binding;
    public int declarationSourceStart;
    public int declarationSourceEnd;
    public int bodyStart;
    public int bodyEnd;
    public int modifiersSourceStart;
    public ModuleScope scope;
    public char[][] tokens;
    public char[] moduleName;
    public long[] sourcePositions;
    public int modifiers = 0;
    boolean ignoreFurtherInvestigation;
    boolean hasResolvedModuleDirectives;
    boolean hasResolvedPackageDirectives;
    boolean hasResolvedTypeDirectives;
    CompilationResult compilationResult;

    public ModuleDeclaration(CompilationResult compilationResult, char[][] tokens, long[] positions) {
        this.compilationResult = compilationResult;
        this.exportsCount = 0;
        this.requiresCount = 0;
        this.tokens = tokens;
        this.moduleName = CharOperation.concatWith(tokens, '.');
        this.sourcePositions = positions;
        this.sourceEnd = (int)(positions[positions.length - 1] & 0xFFFFFFFFFFFFFFFFL);
        this.sourceStart = (int)(positions[0] >>> 32);
    }

    public ModuleBinding setBinding(SourceModuleBinding sourceModuleBinding) {
        this.binding = sourceModuleBinding;
        return sourceModuleBinding;
    }

    public void checkAndSetModifiers() {
        int effectiveModifiers;
        int realModifiers = this.modifiers & 0xFFFF;
        int expectedModifiers = 4128;
        if ((realModifiers & ~expectedModifiers) != 0) {
            this.scope.problemReporter().illegalModifierForModule(this);
            realModifiers &= expectedModifiers;
        }
        this.modifiers = this.binding.modifiers = (effectiveModifiers = 0x8000 | realModifiers);
    }

    public boolean isOpen() {
        return (this.modifiers & 0x20) != 0;
    }

    public void createScope(Scope parentScope) {
        this.scope = new ModuleScope(parentScope, this);
    }

    public void generateCode() {
        block4: {
            if ((this.bits & 0x2000) != 0) {
                return;
            }
            this.bits |= 0x2000;
            if (this.ignoreFurtherInvestigation) {
                return;
            }
            try {
                LookupEnvironment env = this.scope.environment();
                ClassFile classFile = env.classFilePool.acquireForModule(this.binding, env.globalOptions);
                classFile.initializeForModule(this.binding);
                classFile.addModuleAttributes(this.binding, this.annotations, this.scope.referenceCompilationUnit());
                this.scope.referenceCompilationUnit().compilationResult.record(this.binding.moduleName, classFile);
            }
            catch (AbortType abortType) {
                if (this.binding != null) break block4;
                return;
            }
        }
    }

    public void resolveModuleDirectives(CompilationUnitScope cuScope) {
        ModuleReference moduleReference;
        int n;
        int n2;
        ModuleReference[] moduleReferenceArray;
        PackageVisibilityStatement[] packageVisibilityStatementArray;
        if (this.binding == null) {
            this.ignoreFurtherInvestigation = true;
            return;
        }
        if (this.hasResolvedModuleDirectives) {
            return;
        }
        this.hasResolvedModuleDirectives = true;
        HashSet<ModuleBinding> requiredModules = new HashSet<ModuleBinding>();
        HashSet<ModuleBinding> requiredTransitiveModules = new HashSet<ModuleBinding>();
        int i = 0;
        while (i < this.requiresCount) {
            RequiresStatement ref = this.requires[i];
            if (ref != null && ref.resolve(cuScope) != null) {
                Collection<ModuleBinding> deps;
                if (!requiredModules.add(ref.resolvedBinding)) {
                    cuScope.problemReporter().duplicateModuleReference(8389909, ref.module);
                }
                if (ref.isTransitive()) {
                    requiredTransitiveModules.add(ref.resolvedBinding);
                }
                if ((deps = ref.resolvedBinding.dependencyGraphCollector().get()).contains(this.binding)) {
                    cuScope.problemReporter().cyclicModuleDependency(this.binding, ref.module);
                    requiredModules.remove(ref.module.binding);
                }
            }
            ++i;
        }
        this.binding.setRequires(requiredModules.toArray(new ModuleBinding[requiredModules.size()]), requiredTransitiveModules.toArray(new ModuleBinding[requiredTransitiveModules.size()]));
        if (this.exports != null) {
            packageVisibilityStatementArray = this.exports;
            int n3 = this.exports.length;
            int n4 = 0;
            while (n4 < n3) {
                PackageVisibilityStatement exportsStatement = packageVisibilityStatementArray[n4];
                if (exportsStatement.isQualified()) {
                    moduleReferenceArray = ((ExportsStatement)exportsStatement).targets;
                    n2 = ((ExportsStatement)exportsStatement).targets.length;
                    n = 0;
                    while (n < n2) {
                        moduleReference = moduleReferenceArray[n];
                        moduleReference.resolve(cuScope);
                        ++n;
                    }
                }
                ++n4;
            }
        }
        if (this.opens != null) {
            packageVisibilityStatementArray = this.opens;
            int n5 = this.opens.length;
            int n6 = 0;
            while (n6 < n5) {
                PackageVisibilityStatement opensStatement = packageVisibilityStatementArray[n6];
                if (opensStatement.isQualified()) {
                    moduleReferenceArray = ((OpensStatement)opensStatement).targets;
                    n2 = ((OpensStatement)opensStatement).targets.length;
                    n = 0;
                    while (n < n2) {
                        moduleReference = moduleReferenceArray[n];
                        moduleReference.resolve(cuScope);
                        ++n;
                    }
                }
                ++n6;
            }
        }
    }

    public void resolvePackageDirectives(CompilationUnitScope cuScope) {
        if (this.binding == null) {
            this.ignoreFurtherInvestigation = true;
            return;
        }
        if (this.hasResolvedPackageDirectives) {
            return;
        }
        this.hasResolvedPackageDirectives = true;
        HashSet<PlainPackageBinding> exportedPkgs = new HashSet<PlainPackageBinding>();
        int i = 0;
        while (i < this.exportsCount) {
            ExportsStatement ref = this.exports[i];
            if (ref != null && ref.resolve(cuScope)) {
                if (!exportedPkgs.add(ref.resolvedPackage)) {
                    cuScope.problemReporter().invalidPackageReference(8389910, ref);
                }
                char[][] targets = null;
                if (ref.targets != null) {
                    targets = new char[ref.targets.length][];
                    int j = 0;
                    while (j < targets.length) {
                        targets[j] = ref.targets[j].moduleName;
                        ++j;
                    }
                }
                this.binding.addResolvedExport(ref.resolvedPackage, targets);
            }
            ++i;
        }
        HashtableOfObject openedPkgs = new HashtableOfObject();
        int i2 = 0;
        while (i2 < this.opensCount) {
            OpensStatement ref = this.opens[i2];
            if (this.isOpen()) {
                cuScope.problemReporter().invalidOpensStatement(ref, this);
            } else {
                if (openedPkgs.containsKey(ref.pkgName)) {
                    cuScope.problemReporter().invalidPackageReference(8389921, ref);
                } else {
                    openedPkgs.put(ref.pkgName, ref);
                    ref.resolve(cuScope);
                }
                char[][] targets = null;
                if (ref.targets != null) {
                    targets = new char[ref.targets.length][];
                    int j = 0;
                    while (j < targets.length) {
                        targets[j] = ref.targets[j].moduleName;
                        ++j;
                    }
                }
                this.binding.addResolvedOpens(ref.resolvedPackage, targets);
            }
            ++i2;
        }
    }

    public void resolveTypeDirectives(CompilationUnitScope cuScope) {
        if (this.binding == null) {
            this.ignoreFurtherInvestigation = true;
            return;
        }
        if (this.hasResolvedTypeDirectives) {
            return;
        }
        this.hasResolvedTypeDirectives = true;
        ASTNode.resolveAnnotations((BlockScope)this.scope, this.annotations, this.binding);
        HashSet<TypeBinding> allTypes = new HashSet<TypeBinding>();
        int i = 0;
        while (i < this.usesCount) {
            TypeBinding serviceBinding = this.uses[i].serviceInterface.resolveType(this.scope);
            if (serviceBinding != null && serviceBinding.isValidBinding()) {
                if (!(serviceBinding.isClass() || serviceBinding.isInterface() || serviceBinding.isAnnotationType())) {
                    cuScope.problemReporter().invalidServiceRef(8389924, this.uses[i].serviceInterface);
                }
                if (!allTypes.add(this.uses[i].serviceInterface.resolvedType)) {
                    cuScope.problemReporter().duplicateTypeReference(8389911, this.uses[i].serviceInterface);
                }
            }
            ++i;
        }
        this.binding.setUses(allTypes.toArray(new TypeBinding[allTypes.size()]));
        HashSet<TypeBinding> interfaces = new HashSet<TypeBinding>();
        int i2 = 0;
        while (i2 < this.servicesCount) {
            this.services[i2].resolve(this.scope);
            TypeBinding infBinding = this.services[i2].serviceInterface.resolvedType;
            if (infBinding != null && infBinding.isValidBinding()) {
                if (!interfaces.add(this.services[i2].serviceInterface.resolvedType)) {
                    cuScope.problemReporter().duplicateTypeReference(8389912, this.services[i2].serviceInterface);
                }
                this.binding.setImplementations(infBinding, this.services[i2].getResolvedImplementations());
            }
            ++i2;
        }
        this.binding.setServices(interfaces.toArray(new TypeBinding[interfaces.size()]));
    }

    public void analyseCode(CompilationUnitScope skope) {
        this.analyseModuleGraph(skope);
        this.analyseReferencedPackages(skope);
    }

    private void analyseReferencedPackages(CompilationUnitScope skope) {
        if (this.exports != null) {
            this.analyseSomeReferencedPackages(this.exports, skope);
        }
        if (this.opens != null) {
            this.analyseSomeReferencedPackages(this.opens, skope);
        }
    }

    private void analyseSomeReferencedPackages(PackageVisibilityStatement[] stats, CompilationUnitScope skope) {
        PackageVisibilityStatement[] packageVisibilityStatementArray = stats;
        int n = stats.length;
        int n2 = 0;
        while (n2 < n) {
            PackageVisibilityStatement stat = packageVisibilityStatementArray[n2];
            PlainPackageBinding pb = stat.resolvedPackage;
            if (pb != null && !pb.hasCompilationUnit(true)) {
                ModuleBinding[] moduleBindingArray = this.binding.getAllRequiredModules();
                int n3 = moduleBindingArray.length;
                int n4 = 0;
                while (n4 < n3) {
                    ModuleBinding req = moduleBindingArray[n4];
                    PlainPackageBinding[] plainPackageBindingArray = req.getExports();
                    int n5 = plainPackageBindingArray.length;
                    int n6 = 0;
                    while (n6 < n5) {
                        PlainPackageBinding exported = plainPackageBindingArray[n6];
                        if (CharOperation.equals(pb.compoundName, exported.compoundName)) {
                            skope.problemReporter().exportingForeignPackage(stat, req);
                            return;
                        }
                        ++n6;
                    }
                    ++n4;
                }
                skope.problemReporter().invalidPackageReference(8389919, stat);
            }
            ++n2;
        }
    }

    public void analyseModuleGraph(CompilationUnitScope skope) {
        if (this.requires != null) {
            int n;
            HashMap<String, Set<ModuleBinding>> pack2mods = new HashMap<String, Set<ModuleBinding>>();
            Object[] objectArray = this.binding.getAllRequiredModules();
            int n2 = objectArray.length;
            int n3 = 0;
            while (n3 < n2) {
                ModuleBinding requiredModule = objectArray[n3];
                PlainPackageBinding[] plainPackageBindingArray = requiredModule.getExports();
                n = plainPackageBindingArray.length;
                int n4 = 0;
                while (n4 < n) {
                    PlainPackageBinding exportedPackage = plainPackageBindingArray[n4];
                    if (this.binding.canAccess(exportedPackage)) {
                        String packName = String.valueOf(exportedPackage.readableName());
                        HashSet<ModuleBinding> mods = (HashSet<ModuleBinding>)pack2mods.get(packName);
                        if (mods == null) {
                            mods = new HashSet<ModuleBinding>();
                            pack2mods.put(packName, mods);
                        }
                        mods.add(requiredModule);
                    }
                    ++n4;
                }
                ++n3;
            }
            objectArray = this.requires;
            n2 = this.requires.length;
            n3 = 0;
            while (n3 < n2) {
                Object requiresStat = objectArray[n3];
                ModuleBinding requiredModule = ((RequiresStatement)requiresStat).resolvedBinding;
                if (requiredModule != null) {
                    if (requiredModule.isDeprecated()) {
                        skope.problemReporter().deprecatedModule(((RequiresStatement)requiresStat).module, requiredModule);
                    }
                    this.analyseOneDependency((RequiresStatement)requiresStat, requiredModule, skope, pack2mods);
                    if (((RequiresStatement)requiresStat).isTransitive()) {
                        ModuleBinding[] moduleBindingArray = requiredModule.getAllRequiredModules();
                        int n5 = moduleBindingArray.length;
                        n = 0;
                        while (n < n5) {
                            ModuleBinding secondLevelModule = moduleBindingArray[n];
                            this.analyseOneDependency((RequiresStatement)requiresStat, secondLevelModule, skope, pack2mods);
                            ++n;
                        }
                    }
                }
                ++n3;
            }
        }
    }

    private void analyseOneDependency(RequiresStatement requiresStat, ModuleBinding requiredModule, CompilationUnitScope skope, Map<String, Set<ModuleBinding>> pack2mods) {
        PlainPackageBinding[] plainPackageBindingArray = requiredModule.getExports();
        int n = plainPackageBindingArray.length;
        int n2 = 0;
        while (n2 < n) {
            PlainPackageBinding pack = plainPackageBindingArray[n2];
            Set<ModuleBinding> mods = pack2mods.get(String.valueOf(pack.readableName()));
            if (mods != null && mods.size() > 1) {
                CompilerOptions compilerOptions = skope.compilerOptions();
                boolean inJdtDebugCompileMode = compilerOptions.enableJdtDebugCompileMode;
                if (!inJdtDebugCompileMode) {
                    skope.problemReporter().conflictingPackagesFromModules(pack, mods, requiresStat.sourceStart, requiresStat.sourceEnd);
                }
            }
            ++n2;
        }
    }

    public void traverse(ASTVisitor visitor, CompilationUnitScope unitScope) {
        visitor.visit(this, unitScope);
    }

    public StringBuffer printHeader(int indent, StringBuffer output) {
        if (this.annotations != null) {
            int i = 0;
            while (i < this.annotations.length) {
                this.annotations[i].print(indent, output);
                if (i != this.annotations.length - 1) {
                    output.append(" ");
                }
                ++i;
            }
            output.append('\n');
        }
        if (this.isOpen()) {
            output.append("open ");
        }
        output.append("module ");
        output.append(CharOperation.charToString(this.moduleName));
        return output;
    }

    public StringBuffer printBody(int indent, StringBuffer output) {
        int i;
        output.append(" {");
        if (this.requires != null) {
            i = 0;
            while (i < this.requiresCount) {
                output.append('\n');
                ModuleDeclaration.printIndent(indent + 1, output);
                this.requires[i].print(0, output);
                ++i;
            }
        }
        if (this.exports != null) {
            i = 0;
            while (i < this.exportsCount) {
                output.append('\n');
                this.exports[i].print(indent + 1, output);
                ++i;
            }
        }
        if (this.opens != null) {
            i = 0;
            while (i < this.opensCount) {
                output.append('\n');
                this.opens[i].print(indent + 1, output);
                ++i;
            }
        }
        if (this.uses != null) {
            i = 0;
            while (i < this.usesCount) {
                output.append('\n');
                this.uses[i].print(indent + 1, output);
                ++i;
            }
        }
        if (this.servicesCount != 0) {
            i = 0;
            while (i < this.servicesCount) {
                output.append('\n');
                this.services[i].print(indent + 1, output);
                ++i;
            }
        }
        output.append('\n');
        return ModuleDeclaration.printIndent(indent, output).append('}');
    }

    @Override
    public StringBuffer print(int indent, StringBuffer output) {
        ModuleDeclaration.printIndent(indent, output);
        this.printHeader(0, output);
        return this.printBody(indent, output);
    }

    @Override
    public void abort(int abortLevel, CategorizedProblem problem) {
        switch (abortLevel) {
            case 2: {
                throw new AbortCompilation(this.compilationResult, problem);
            }
            case 4: {
                throw new AbortCompilationUnit(this.compilationResult, problem);
            }
            case 16: {
                throw new AbortMethod(this.compilationResult, problem);
            }
        }
        throw new AbortType(this.compilationResult, problem);
    }

    @Override
    public CompilationResult compilationResult() {
        return this.compilationResult;
    }

    @Override
    public CompilationUnitDeclaration getCompilationUnitDeclaration() {
        return this.scope.referenceCompilationUnit();
    }

    @Override
    public boolean hasErrors() {
        return this.ignoreFurtherInvestigation;
    }

    @Override
    public void tagAsHavingErrors() {
        this.ignoreFurtherInvestigation = true;
    }

    @Override
    public void tagAsHavingIgnoredMandatoryErrors(int problemId) {
    }

    public String getModuleVersion() {
        if (this.scope != null) {
            LookupEnvironment env = this.scope.environment().root;
            return env.moduleVersion;
        }
        return null;
    }
}

