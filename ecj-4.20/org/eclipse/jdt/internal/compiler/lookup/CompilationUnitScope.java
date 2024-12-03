/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.Invocation;
import org.eclipse.jdt.internal.compiler.ast.ModuleDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
import org.eclipse.jdt.internal.compiler.env.IUpdatableModule;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.ImportBinding;
import org.eclipse.jdt.internal.compiler.lookup.ImportConflictBinding;
import org.eclipse.jdt.internal.compiler.lookup.InferenceVariable;
import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodVerifier;
import org.eclipse.jdt.internal.compiler.lookup.MissingTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.PlainPackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemFieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemPackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBindingSetWrapper;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SortedCompoundNameVector;
import org.eclipse.jdt.internal.compiler.lookup.SortedSimpleNameVector;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeCollisionException;
import org.eclipse.jdt.internal.compiler.lookup.SplitPackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.util.HashtableOfObject;
import org.eclipse.jdt.internal.compiler.util.HashtableOfType;
import org.eclipse.jdt.internal.compiler.util.ObjectVector;

public class CompilationUnitScope
extends Scope {
    public LookupEnvironment environment;
    public CompilationUnitDeclaration referenceContext;
    public char[][] currentPackageName;
    public PlainPackageBinding fPackage;
    public ImportBinding[] imports;
    public int importPtr;
    public HashtableOfObject typeOrPackageCache;
    public SourceTypeBinding[] topLevelTypes;
    private SortedCompoundNameVector qualifiedReferences;
    private SortedSimpleNameVector simpleNameReferences;
    private SortedSimpleNameVector rootReferences;
    private LinkedHashSet<ReferenceBindingSetWrapper> referencedTypes;
    private Set<ReferenceBindingSetWrapper> referencedSuperTypesSet;
    private ObjectVector referencedSuperTypes;
    HashtableOfType constantPoolNameUsage;
    private int captureID = 1;
    private ImportBinding[] tempImports;
    private boolean skipCachingImports;
    boolean connectingHierarchy;
    private ArrayList<Invocation> inferredInvocations;
    Map<InferenceVariable.InferenceVarKey, InferenceVariable> uniqueInferenceVariables = new HashMap<InferenceVariable.InferenceVarKey, InferenceVariable>();

    public CompilationUnitScope(CompilationUnitDeclaration unit, LookupEnvironment environment) {
        this(unit, environment.globalOptions);
        this.environment = environment;
    }

    public CompilationUnitScope(CompilationUnitDeclaration unit, CompilerOptions compilerOptions) {
        super(4, null);
        this.referenceContext = unit;
        unit.scope = this;
        char[][] cArray = this.currentPackageName = unit.currentPackage == null ? CharOperation.NO_CHAR_CHAR : unit.currentPackage.tokens;
        if (compilerOptions.produceReferenceInfo) {
            this.qualifiedReferences = new SortedCompoundNameVector();
            this.simpleNameReferences = new SortedSimpleNameVector();
            this.rootReferences = new SortedSimpleNameVector();
            this.referencedTypes = new LinkedHashSet();
            this.referencedSuperTypesSet = new HashSet<ReferenceBindingSetWrapper>();
            this.referencedSuperTypes = new ObjectVector();
        } else {
            this.qualifiedReferences = null;
            this.simpleNameReferences = null;
            this.rootReferences = null;
            this.referencedTypes = null;
            this.referencedSuperTypesSet = null;
            this.referencedSuperTypes = null;
        }
    }

    void buildFieldsAndMethods() {
        int i = 0;
        int length = this.topLevelTypes.length;
        while (i < length) {
            this.topLevelTypes[i].scope.buildFieldsAndMethods();
            ++i;
        }
    }

    void buildTypeBindings(AccessRestriction accessRestriction) {
        TypeDeclaration[] types;
        char[][] expectedPackageName;
        this.topLevelTypes = new SourceTypeBinding[0];
        boolean firstIsSynthetic = false;
        if (this.referenceContext.compilationResult.compilationUnit != null && (expectedPackageName = this.referenceContext.compilationResult.compilationUnit.getPackageName()) != null && !this.referenceContext.isModuleInfo() && !CharOperation.equals(this.currentPackageName, expectedPackageName)) {
            if (this.referenceContext.currentPackage != null || this.referenceContext.types != null || this.referenceContext.imports != null) {
                this.problemReporter().packageIsNotExpectedPackage(this.referenceContext);
            }
            char[][] cArray = this.currentPackageName = expectedPackageName.length == 0 ? CharOperation.NO_CHAR_CHAR : expectedPackageName;
        }
        if (this.currentPackageName == CharOperation.NO_CHAR_CHAR) {
            this.fPackage = this.environment.defaultPackage;
            if (this.referenceContext.isModuleInfo()) {
                ModuleDeclaration moduleDecl = this.referenceContext.moduleDeclaration;
                if (moduleDecl != null) {
                    moduleDecl.createScope(this);
                    moduleDecl.checkAndSetModifiers();
                }
            } else if (this.module() != this.environment.UnNamedModule) {
                this.problemReporter().unnamedPackageInNamedModule(this.module());
            }
        } else {
            this.fPackage = this.environment.createPlainPackage(this.currentPackageName);
            if (this.fPackage == null) {
                if (this.referenceContext.currentPackage != null) {
                    this.problemReporter().packageCollidesWithType(this.referenceContext);
                }
                this.fPackage = this.environment.defaultPackage;
                return;
            }
            if (this.referenceContext.isPackageInfo()) {
                if (this.referenceContext.types == null || this.referenceContext.types.length == 0) {
                    this.referenceContext.types = new TypeDeclaration[1];
                    this.referenceContext.createPackageInfoType();
                    firstIsSynthetic = true;
                }
                if (this.referenceContext.currentPackage != null && this.referenceContext.currentPackage.annotations != null) {
                    this.referenceContext.types[0].annotations = this.referenceContext.currentPackage.annotations;
                }
            }
            this.recordQualifiedReference(this.currentPackageName);
        }
        int typeLength = (types = this.referenceContext.types) == null ? 0 : types.length;
        this.topLevelTypes = new SourceTypeBinding[typeLength];
        int count = 0;
        int i = 0;
        while (i < typeLength) {
            TypeDeclaration typeDecl = types[i];
            if (this.environment.root.isProcessingAnnotations && this.environment.isMissingType(typeDecl.name)) {
                throw new SourceTypeCollisionException();
            }
            this.recordSimpleReference(typeDecl.name);
            if (this.fPackage.hasType0Any(typeDecl.name)) {
                if (this.environment.root.isProcessingAnnotations) {
                    throw new SourceTypeCollisionException();
                }
                this.problemReporter().duplicateTypes(this.referenceContext, typeDecl);
            } else {
                char[] mainTypeName;
                if ((typeDecl.modifiers & 1) != 0 && (mainTypeName = this.referenceContext.getMainTypeName()) != null && !CharOperation.equals(mainTypeName, typeDecl.name)) {
                    this.problemReporter().publicClassMustMatchFileName(this.referenceContext, typeDecl);
                }
                ClassScope child = new ClassScope(this, typeDecl);
                SourceTypeBinding type = child.buildType(null, this.fPackage, accessRestriction);
                if (firstIsSynthetic && i == 0) {
                    type.modifiers |= 0x1000;
                }
                if (type != null) {
                    this.topLevelTypes[count++] = type;
                }
            }
            ++i;
        }
        if (count != this.topLevelTypes.length) {
            this.topLevelTypes = new SourceTypeBinding[count];
            System.arraycopy(this.topLevelTypes, 0, this.topLevelTypes, 0, count);
        }
        if (this.referenceContext.moduleDeclaration != null) {
            this.module().completeIfNeeded(IUpdatableModule.UpdateKind.MODULE);
            this.referenceContext.moduleDeclaration.resolvePackageDirectives(this);
            this.module().completeIfNeeded(IUpdatableModule.UpdateKind.PACKAGE);
        }
    }

    void checkAndSetImports() {
        TypeDeclaration[] types = this.referenceContext.types;
        if (types != null) {
            int i = 0;
            while (i < types.length) {
                TypeDeclaration typeDecl = types[i];
                if (this.fPackage != this.environment.defaultPackage && this.fPackage.getPackage(typeDecl.name, this.module()) != null) {
                    this.problemReporter().typeCollidesWithPackage(this.referenceContext, typeDecl);
                }
                ++i;
            }
        }
        if (this.referenceContext.moduleDeclaration != null) {
            this.referenceContext.moduleDeclaration.resolveModuleDirectives(this);
        }
        if (this.referenceContext.imports == null) {
            this.imports = this.getDefaultImports();
            return;
        }
        int numberOfStatements = this.referenceContext.imports.length;
        int numberOfImports = numberOfStatements + 1;
        int i = 0;
        while (i < numberOfStatements) {
            ImportReference importReference = this.referenceContext.imports[i];
            if ((importReference.bits & 0x20000) != 0 && CharOperation.equals(TypeConstants.JAVA_LANG, importReference.tokens) && !importReference.isStatic()) {
                --numberOfImports;
                break;
            }
            ++i;
        }
        ImportBinding[] resolvedImports = new ImportBinding[numberOfImports];
        resolvedImports[0] = this.getDefaultImports()[0];
        int index = 1;
        int i2 = 0;
        while (i2 < numberOfStatements) {
            block14: {
                ImportReference importReference = this.referenceContext.imports[i2];
                char[][] compoundName = importReference.tokens;
                int j = 0;
                while (j < index) {
                    ImportBinding resolved = resolvedImports[j];
                    if (resolved.onDemand != ((importReference.bits & 0x20000) != 0) || resolved.isStatic() != importReference.isStatic() || !CharOperation.equals(compoundName, resolvedImports[j].compoundName)) {
                        ++j;
                        continue;
                    }
                    break block14;
                }
                if ((importReference.bits & 0x20000) != 0) {
                    Binding importBinding;
                    if (!(CharOperation.equals(compoundName, this.currentPackageName) || !(importBinding = this.findImport(compoundName, compoundName.length)).isValidBinding() || importReference.isStatic() && importBinding instanceof PackageBinding)) {
                        resolvedImports[index++] = new ImportBinding(compoundName, true, importBinding, importReference);
                    }
                } else {
                    resolvedImports[index++] = new ImportBinding(compoundName, false, null, importReference);
                }
            }
            ++i2;
        }
        if (resolvedImports.length > index) {
            ImportBinding[] importBindingArray = resolvedImports;
            resolvedImports = new ImportBinding[index];
            System.arraycopy(importBindingArray, 0, resolvedImports, 0, index);
        }
        this.imports = resolvedImports;
    }

    void checkParameterizedTypes() {
        if (this.compilerOptions().sourceLevel < 0x310000L) {
            return;
        }
        int i = 0;
        int length = this.topLevelTypes.length;
        while (i < length) {
            ClassScope scope = this.topLevelTypes[i].scope;
            scope.checkParameterizedTypeBounds();
            scope.checkParameterizedSuperTypeCollisions();
            ++i;
        }
    }

    public char[] computeConstantPoolName(LocalTypeBinding localType) {
        char[] candidateName;
        boolean isCompliant15;
        if (localType.constantPoolName != null) {
            return localType.constantPoolName;
        }
        if (this.constantPoolNameUsage == null) {
            this.constantPoolNameUsage = new HashtableOfType();
        }
        SourceTypeBinding outerMostEnclosingType = localType.scope.outerMostClassScope().enclosingSourceType();
        int index = 0;
        boolean bl = isCompliant15 = this.compilerOptions().complianceLevel >= 0x310000L;
        while (true) {
            candidateName = localType.isMemberType() ? (index == 0 ? CharOperation.concat(localType.enclosingType().constantPoolName(), localType.sourceName, '$') : CharOperation.concat(localType.enclosingType().constantPoolName(), '$', String.valueOf(index).toCharArray(), '$', localType.sourceName)) : (localType.isAnonymousType() ? (isCompliant15 ? CharOperation.concat(localType.enclosingType.constantPoolName(), String.valueOf(index + 1).toCharArray(), '$') : CharOperation.concat(outerMostEnclosingType.constantPoolName(), String.valueOf(index + 1).toCharArray(), '$')) : (isCompliant15 ? CharOperation.concat(CharOperation.concat(localType.enclosingType().constantPoolName(), String.valueOf(index + 1).toCharArray(), '$'), localType.sourceName) : CharOperation.concat(outerMostEnclosingType.constantPoolName(), '$', String.valueOf(index + 1).toCharArray(), '$', localType.sourceName)));
            if (this.constantPoolNameUsage.get(candidateName) == null) break;
            ++index;
        }
        this.constantPoolNameUsage.put(candidateName, localType);
        return candidateName;
    }

    void connectTypeHierarchy() {
        int i = 0;
        int length = this.topLevelTypes.length;
        while (i < length) {
            this.topLevelTypes[i].scope.connectTypeHierarchy();
            ++i;
        }
        i = 0;
        length = this.topLevelTypes.length;
        while (i < length) {
            this.topLevelTypes[i].scope.connectImplicitPermittedTypes();
            ++i;
        }
    }

    /*
     * Unable to fully structure code
     */
    void faultInImports() {
        if (this.tempImports != null) {
            return;
        }
        unresolvedFound = false;
        v0 = reportUnresolved = this.environment.suppressImportErrors == false;
        if (this.typeOrPackageCache != null && !this.skipCachingImports) {
            return;
        }
        if (this.referenceContext.imports == null) {
            this.typeOrPackageCache = new HashtableOfObject(1);
            return;
        }
        numberOfStatements = this.referenceContext.imports.length;
        typesBySimpleNames = null;
        i = 0;
        while (i < numberOfStatements) {
            if ((this.referenceContext.imports[i].bits & 131072) == 0) {
                typesBySimpleNames = new HashtableOfType(this.topLevelTypes.length + numberOfStatements);
                j = 0;
                length = this.topLevelTypes.length;
                while (j < length) {
                    typesBySimpleNames.put(this.topLevelTypes[j].sourceName, this.topLevelTypes[j]);
                    ++j;
                }
                break;
            }
            ++i;
        }
        numberOfImports = numberOfStatements + 1;
        i = 0;
        while (i < numberOfStatements) {
            importReference = this.referenceContext.imports[i];
            if ((importReference.bits & 131072) != 0 && CharOperation.equals(TypeConstants.JAVA_LANG, importReference.tokens) && !importReference.isStatic()) {
                --numberOfImports;
                break;
            }
            ++i;
        }
        this.tempImports = new ImportBinding[numberOfImports];
        this.tempImports[0] = this.getDefaultImports()[0];
        this.importPtr = 1;
        compilerOptions = this.compilerOptions();
        inJdtDebugCompileMode = compilerOptions.enableJdtDebugCompileMode;
        i = 0;
        while (i < numberOfStatements) {
            block30: {
                block34: {
                    block33: {
                        block32: {
                            block31: {
                                importReference = this.referenceContext.imports[i];
                                compoundName = importReference.getImportName();
                                j = 0;
                                while (j < this.importPtr) {
                                    resolved = this.tempImports[j];
                                    if (resolved.onDemand == ((importReference.bits & 131072) != 0) && resolved.isStatic() == importReference.isStatic() && CharOperation.equals(compoundName, resolved.compoundName) && CharOperation.equals(importReference.getSimpleName(), resolved.getSimpleName())) {
                                        this.problemReporter().unusedImport(importReference);
                                        break block30;
                                    }
                                    ++j;
                                }
                                if ((importReference.bits & 131072) == 0) break block31;
                                importBinding = this.findImport(compoundName, compoundName.length);
                                if (!importBinding.isValidBinding()) {
                                    this.problemReporter().importProblem(importReference, importBinding);
                                } else if (importBinding instanceof PackageBinding && (uniquePackage = ((PackageBinding)importBinding).getVisibleFor(this.module(), false)) instanceof SplitPackageBinding && !inJdtDebugCompileMode) {
                                    splitPackage = (SplitPackageBinding)uniquePackage;
                                    this.problemReporter().conflictingPackagesFromModules(splitPackage, this.module(), importReference.sourceStart, importReference.sourceEnd);
                                } else if (importReference.isStatic() && importBinding instanceof PackageBinding) {
                                    this.problemReporter().cannotImportPackage(importReference);
                                } else {
                                    this.recordImportBinding(new ImportBinding(compoundName, true, importBinding, importReference));
                                }
                                break block30;
                            }
                            importBinding = this.findSingleImport(compoundName, 13, importReference.isStatic());
                            if (!(importBinding instanceof SplitPackageBinding) || inJdtDebugCompileMode) break block32;
                            splitPackage = (SplitPackageBinding)importBinding;
                            sourceEnd = (int)(importReference.sourcePositions[splitPackage.compoundName.length - 1] & 65535L);
                            this.problemReporter().conflictingPackagesFromModules((SplitPackageBinding)importBinding, this.module(), importReference.sourceStart, sourceEnd);
                            break block30;
                        }
                        if (importBinding.isValidBinding() || importBinding.problemId() == 3) break block33;
                        unresolvedFound = true;
                        if (reportUnresolved) {
                            this.problemReporter().importProblem(importReference, importBinding);
                        }
                        break block30;
                    }
                    if (!(importBinding instanceof PackageBinding)) break block34;
                    this.problemReporter().cannotImportPackage(importReference);
                    break block30;
                }
                if (!this.environment.useModuleSystem || !(importBinding instanceof ReferenceBinding) || (importedPackage = ((ReferenceBinding)importBinding).fPackage) == null) ** GOTO lbl-1000
                if (!importedPackage.isValidBinding()) {
                    this.problemReporter().importProblem(importReference, importedPackage);
                } else {
                    importedPackage = (PackageBinding)this.findImport(importedPackage.compoundName, false, true);
                    if (importedPackage != null) {
                        importedPackage = importedPackage.getVisibleFor(this.module(), true);
                    }
                    if (importedPackage instanceof SplitPackageBinding && !inJdtDebugCompileMode) {
                        splitPackage = (SplitPackageBinding)importedPackage;
                        sourceEnd = (int)importReference.sourcePositions[splitPackage.compoundName.length - 1];
                        this.problemReporter().conflictingPackagesFromModules(splitPackage, this.module(), importReference.sourceStart, sourceEnd);
                    } else if (this.checkAndRecordImportBinding(importBinding, typesBySimpleNames, importReference, compoundName) != -1 && importReference.isStatic()) {
                        if (importBinding.kind() == 1) {
                            this.checkMoreStaticBindings(compoundName, typesBySimpleNames, 12, importReference);
                        } else if (importBinding.kind() == 8) {
                            this.checkMoreStaticBindings(compoundName, typesBySimpleNames, 4, importReference);
                        }
                    }
                }
            }
            ++i;
        }
        if (this.tempImports.length > this.importPtr) {
            this.tempImports = new ImportBinding[this.importPtr];
            System.arraycopy(this.tempImports, 0, this.tempImports, 0, this.importPtr);
        }
        this.imports = this.tempImports;
        this.tempImports = null;
        length = this.imports.length;
        this.typeOrPackageCache = new HashtableOfObject(length);
        i = 0;
        while (i < length) {
            binding = this.imports[i];
            if (!binding.onDemand && binding.resolvedImport instanceof ReferenceBinding || binding instanceof ImportConflictBinding) {
                this.typeOrPackageCache.put(binding.getSimpleName(), binding);
            }
            ++i;
        }
        this.skipCachingImports = this.environment.suppressImportErrors != false && unresolvedFound != false;
    }

    public void faultInTypes() {
        this.faultInImports();
        if (this.referenceContext.moduleDeclaration != null) {
            this.referenceContext.moduleDeclaration.resolveTypeDirectives(this);
        } else if (this.referenceContext.currentPackage != null) {
            this.referenceContext.currentPackage.checkPackageConflict(this);
        }
        int i = 0;
        int length = this.topLevelTypes.length;
        while (i < length) {
            this.topLevelTypes[i].faultInTypesForFieldsAndMethods();
            ++i;
        }
    }

    public Binding findImport(char[][] compoundName, boolean findStaticImports, boolean onDemand) {
        if (onDemand) {
            return this.findImport(compoundName, compoundName.length);
        }
        return this.findSingleImport(compoundName, 13, findStaticImports);
    }

    private Binding findImport(char[][] compoundName, int length) {
        ReferenceBinding type;
        int i;
        Binding binding;
        ModuleBinding module;
        block16: {
            this.recordQualifiedReference(compoundName);
            module = this.module();
            binding = this.environment.getTopLevelPackage(compoundName[0]);
            i = 1;
            if (binding != null) {
                PackageBinding packageBinding = binding;
                while (i < length) {
                    if ((binding = packageBinding.getTypeOrPackage(compoundName[i++], module, i < length)) instanceof ReferenceBinding && binding.problemId() == 30) {
                        return this.environment.convertToRawType((TypeBinding)binding, false);
                    }
                    if (binding == null) break block16;
                    if (!binding.isValidBinding()) {
                        if (binding.problemId() == 3 && packageBinding instanceof SplitPackageBinding) {
                            return packageBinding;
                        }
                        binding = null;
                        break block16;
                    }
                    if (!(binding instanceof PackageBinding)) {
                        PackageBinding visibleFor = packageBinding.getVisibleFor(module, false);
                        if (visibleFor instanceof SplitPackageBinding) {
                            return visibleFor;
                        }
                        break block16;
                    }
                    packageBinding = (PackageBinding)binding;
                }
                if (packageBinding.isValidBinding() && !module.canAccess(packageBinding)) {
                    return new ProblemPackageBinding(compoundName, 30, this.environment);
                }
                return packageBinding;
            }
        }
        if (binding == null) {
            Binding inaccessible;
            if (!module.isUnnamed() && (inaccessible = this.environment.getInaccessibleBinding(compoundName, module)) != null) {
                return inaccessible;
            }
            if (this.compilerOptions().complianceLevel >= 0x300000L) {
                return this.problemType(compoundName, i, null);
            }
            type = this.findType(compoundName[0], this.environment.defaultPackage, this.environment.defaultPackage);
            if (type == null || !type.isValidBinding()) {
                return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, i), null, 1);
            }
            i = 1;
        } else {
            type = (ReferenceBinding)binding;
        }
        while (i < length) {
            char[] name;
            if (!(type = (ReferenceBinding)this.environment.convertToRawType(type, false)).canBeSeenBy(this.fPackage)) {
                return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, i), type, 2);
            }
            if ((type = type.getMemberType(name = compoundName[i++])) != null) continue;
            return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, i), null, 1);
        }
        if (!type.canBeSeenBy(this.fPackage)) {
            return new ProblemReferenceBinding(compoundName, type, 2);
        }
        return type;
    }

    private Binding findSingleImport(char[][] compoundName, int mask, boolean findStaticImports) {
        if (compoundName.length == 1) {
            if (this.compilerOptions().complianceLevel >= 0x300000L && !this.referenceContext.isModuleInfo()) {
                return new ProblemReferenceBinding(compoundName, null, 1);
            }
            ReferenceBinding typeBinding = this.findType(compoundName[0], this.environment.defaultPackage, this.fPackage);
            if (typeBinding == null) {
                return new ProblemReferenceBinding(compoundName, null, 1);
            }
            return typeBinding;
        }
        if (findStaticImports) {
            return this.findSingleStaticImport(compoundName, mask);
        }
        return this.findImport(compoundName, compoundName.length);
    }

    private Binding findSingleStaticImport(char[][] compoundName, int mask) {
        MethodBinding method;
        FieldBinding field;
        Binding binding = this.findImport(compoundName, compoundName.length - 1);
        if (!binding.isValidBinding()) {
            return binding;
        }
        char[] name = compoundName[compoundName.length - 1];
        if (binding instanceof PackageBinding) {
            Binding temp = ((PackageBinding)binding).getTypeOrPackage(name, this.module(), false);
            if (temp != null && temp instanceof ReferenceBinding) {
                return new ProblemReferenceBinding(compoundName, (ReferenceBinding)temp, 14);
            }
            return binding;
        }
        ReferenceBinding type = (ReferenceBinding)binding;
        FieldBinding fieldBinding = field = (mask & 1) != 0 ? this.findField(type, name, null, true) : null;
        if (field != null) {
            if (field.problemId() == 3 && ((ProblemFieldBinding)field).closestMatch.isStatic()) {
                return field;
            }
            if (field.isValidBinding() && field.isStatic() && field.canBeSeenBy(type, null, this)) {
                return field;
            }
        }
        MethodBinding methodBinding = method = (mask & 8) != 0 ? this.findStaticMethod(type, name) : null;
        if (method != null) {
            return method;
        }
        if ((type = this.findMemberType(name, type)) == null || !type.isStatic()) {
            if (field != null && !field.isValidBinding() && field.problemId() != 1) {
                return field;
            }
            return new ProblemReferenceBinding(compoundName, type, 1);
        }
        if (type.isValidBinding() && !type.canBeSeenBy(this.fPackage)) {
            return new ProblemReferenceBinding(compoundName, type, 2);
        }
        if (type.problemId() == 2) {
            return new ProblemReferenceBinding(compoundName, ((ProblemReferenceBinding)type).closestMatch, 2);
        }
        return type;
    }

    private MethodBinding findStaticMethod(ReferenceBinding currentType, char[] selector) {
        if (!currentType.canBeSeenBy(this)) {
            return null;
        }
        do {
            currentType.initializeForStaticImports();
            MethodBinding[] methods = currentType.getMethods(selector);
            if (methods == Binding.NO_METHODS) continue;
            int i = methods.length;
            while (--i >= 0) {
                MethodBinding method = methods[i];
                if (!method.isStatic() || !method.canBeSeenBy(this.fPackage)) continue;
                return method;
            }
        } while ((currentType = currentType.superclass()) != null);
        return null;
    }

    ImportBinding[] getDefaultImports() {
        if (this.environment.root.defaultImports != null) {
            return this.environment.root.defaultImports;
        }
        Binding importBinding = this.environment.getTopLevelPackage(TypeConstants.JAVA);
        if (importBinding != null) {
            importBinding = importBinding.getTypeOrPackage(TypeConstants.JAVA_LANG[1], this.module(), false);
        }
        if (importBinding == null || !importBinding.isValidBinding()) {
            this.problemReporter().isClassPathCorrect(TypeConstants.JAVA_LANG_OBJECT, this.referenceContext, this.environment.missingClassFileLocation, false);
            MissingTypeBinding missingObject = this.environment.createMissingType(null, TypeConstants.JAVA_LANG_OBJECT);
            importBinding = missingObject.fPackage;
        }
        this.environment.root.defaultImports = new ImportBinding[]{new ImportBinding(TypeConstants.JAVA_LANG, true, importBinding, null)};
        return this.environment.root.defaultImports;
    }

    public final Binding getImport(char[][] compoundName, boolean onDemand, boolean isStaticImport) {
        if (onDemand) {
            return this.findImport(compoundName, compoundName.length);
        }
        return this.findSingleImport(compoundName, 13, isStaticImport);
    }

    public int nextCaptureID() {
        return this.captureID++;
    }

    @Override
    public ModuleBinding module() {
        if (!this.referenceContext.isModuleInfo() && this.referenceContext.types == null && this.referenceContext.currentPackage == null && this.referenceContext.imports == null) {
            this.environment = this.environment.UnNamedModule.environment;
            return this.environment.UnNamedModule;
        }
        return super.module();
    }

    @Override
    public ProblemReporter problemReporter() {
        ProblemReporter problemReporter = this.referenceContext.problemReporter;
        problemReporter.referenceContext = this.referenceContext;
        return problemReporter;
    }

    void recordQualifiedReference(char[][] qualifiedName) {
        if (this.qualifiedReferences == null) {
            return;
        }
        int length = ((char[][])qualifiedName).length;
        if (length > 1) {
            this.recordRootReference(qualifiedName[0]);
            while (this.qualifiedReferences.add((char[][])qualifiedName)) {
                if (length == 2) {
                    this.recordSimpleReference(qualifiedName[0]);
                    this.recordSimpleReference(qualifiedName[1]);
                    return;
                }
                this.recordSimpleReference(qualifiedName[--length]);
                char[][] cArray = qualifiedName;
                char[][] cArrayArray = new char[length][];
                qualifiedName = cArrayArray;
                System.arraycopy(cArray, 0, cArrayArray, 0, length);
            }
        } else if (length == 1) {
            this.recordRootReference(qualifiedName[0]);
            this.recordSimpleReference(qualifiedName[0]);
        }
    }

    void recordReference(char[][] qualifiedEnclosingName, char[] simpleName) {
        this.recordQualifiedReference(qualifiedEnclosingName);
        if (qualifiedEnclosingName.length == 0) {
            this.recordRootReference(simpleName);
        }
        this.recordSimpleReference(simpleName);
    }

    void recordReference(ReferenceBinding type, char[] simpleName) {
        ReferenceBinding actualType = this.typeToRecord(type);
        if (actualType != null) {
            this.recordReference(actualType.compoundName, simpleName);
        }
    }

    void recordRootReference(char[] simpleName) {
        if (this.rootReferences == null) {
            return;
        }
        this.rootReferences.add(simpleName);
    }

    void recordSimpleReference(char[] simpleName) {
        if (this.simpleNameReferences == null) {
            return;
        }
        this.simpleNameReferences.add(simpleName);
    }

    void recordSuperTypeReference(TypeBinding type) {
        if (this.referencedSuperTypes == null) {
            return;
        }
        ReferenceBinding actualType = this.typeToRecord(type);
        if (actualType != null && this.referencedSuperTypesSet.add(new ReferenceBindingSetWrapper(actualType))) {
            this.referencedSuperTypes.add(actualType);
        }
    }

    public void recordTypeConversion(TypeBinding superType, TypeBinding subType) {
        this.recordSuperTypeReference(subType);
    }

    void recordTypeReference(TypeBinding type) {
        if (this.referencedTypes == null) {
            return;
        }
        ReferenceBinding actualType = this.typeToRecord(type);
        if (actualType != null) {
            this.referencedTypes.add(new ReferenceBindingSetWrapper(actualType));
        }
    }

    void recordTypeReferences(TypeBinding[] types) {
        if (this.referencedTypes == null) {
            return;
        }
        if (types == null || types.length == 0) {
            return;
        }
        int i = 0;
        int max = types.length;
        while (i < max) {
            ReferenceBinding actualType = this.typeToRecord(types[i]);
            if (actualType != null) {
                this.referencedTypes.add(new ReferenceBindingSetWrapper(actualType));
            }
            ++i;
        }
    }

    Binding resolveSingleImport(ImportBinding importBinding, int mask) {
        if (importBinding.resolvedImport == null) {
            importBinding.resolvedImport = this.findSingleImport(importBinding.compoundName, mask, importBinding.isStatic());
            if (!importBinding.resolvedImport.isValidBinding() || importBinding.resolvedImport instanceof PackageBinding) {
                if (importBinding.resolvedImport.problemId() == 3) {
                    return importBinding.resolvedImport;
                }
                if (this.imports != null) {
                    ImportBinding[] newImports = new ImportBinding[this.imports.length - 1];
                    int i = 0;
                    int n = 0;
                    int max = this.imports.length;
                    while (i < max) {
                        if (this.imports[i] != importBinding) {
                            newImports[n++] = this.imports[i];
                        }
                        ++i;
                    }
                    this.imports = newImports;
                }
                return null;
            }
        }
        return importBinding.resolvedImport;
    }

    public void storeDependencyInfo() {
        int i = 0;
        while (i < this.referencedSuperTypes.size) {
            ReferenceBinding[] interfaces;
            ReferenceBinding superclass;
            ReferenceBinding enclosing;
            ReferenceBinding type = (ReferenceBinding)this.referencedSuperTypes.elementAt(i);
            this.referencedTypes.add(new ReferenceBindingSetWrapper(type));
            if (!type.isLocalType() && (enclosing = type.enclosingType()) != null) {
                this.recordSuperTypeReference(enclosing);
            }
            if ((superclass = type.superclass()) != null) {
                this.recordSuperTypeReference(superclass);
            }
            if ((interfaces = type.superInterfaces()) != null) {
                int j = 0;
                int length = interfaces.length;
                while (j < length) {
                    this.recordSuperTypeReference(interfaces[j]);
                    ++j;
                }
            }
            ++i;
        }
        for (ReferenceBindingSetWrapper wrapper : this.referencedTypes) {
            ReferenceBinding type = wrapper.referenceBinding;
            if (type.isLocalType()) continue;
            this.recordQualifiedReference(type.isMemberType() ? CharOperation.splitOn('.', type.readableName()) : type.compoundName);
        }
        int size = this.qualifiedReferences.size;
        char[][][] qualifiedRefs = new char[size][][];
        int i2 = 0;
        while (i2 < size) {
            qualifiedRefs[i2] = this.qualifiedReferences.elementAt(i2);
            ++i2;
        }
        this.referenceContext.compilationResult.qualifiedReferences = qualifiedRefs;
        size = this.simpleNameReferences.size;
        char[][] simpleRefs = new char[size][];
        int i3 = 0;
        while (i3 < size) {
            simpleRefs[i3] = this.simpleNameReferences.elementAt(i3);
            ++i3;
        }
        this.referenceContext.compilationResult.simpleNameReferences = simpleRefs;
        size = this.rootReferences.size;
        char[][] rootRefs = new char[size][];
        int i4 = 0;
        while (i4 < size) {
            rootRefs[i4] = this.rootReferences.elementAt(i4);
            ++i4;
        }
        this.referenceContext.compilationResult.rootReferences = rootRefs;
    }

    public String toString() {
        return "--- CompilationUnit Scope : " + new String(this.referenceContext.getFileName());
    }

    /*
     * Unable to fully structure code
     */
    private ReferenceBinding typeToRecord(TypeBinding type) {
        if (type != null) ** GOTO lbl4
        return null;
lbl-1000:
        // 1 sources

        {
            type = ((ArrayBinding)type).leafComponentType();
lbl4:
            // 2 sources

            ** while (type.isArrayType())
        }
lbl5:
        // 1 sources

        switch (type.kind()) {
            case 132: 
            case 516: 
            case 4100: 
            case 8196: 
            case 32772: 
            case 65540: {
                return null;
            }
            case 260: 
            case 1028: {
                type = type.erasure();
            }
        }
        refType = (ReferenceBinding)type;
        if (refType.isLocalType()) {
            return null;
        }
        return refType;
    }

    public void verifyMethods(MethodVerifier verifier) {
        int i = 0;
        int length = this.topLevelTypes.length;
        while (i < length) {
            this.topLevelTypes[i].verifyMethods(verifier);
            ++i;
        }
    }

    private void recordImportBinding(ImportBinding bindingToAdd) {
        if (this.tempImports.length == this.importPtr) {
            this.tempImports = new ImportBinding[this.importPtr + 1];
            System.arraycopy(this.tempImports, 0, this.tempImports, 0, this.importPtr);
        }
        this.tempImports[this.importPtr++] = bindingToAdd;
    }

    private void checkMoreStaticBindings(char[][] compoundName, HashtableOfType typesBySimpleNames, int mask, ImportReference importReference) {
        Binding importBinding = this.findSingleStaticImport(compoundName, mask);
        if (!importBinding.isValidBinding()) {
            if (importBinding.problemId() == 3) {
                this.checkAndRecordImportBinding(importBinding, typesBySimpleNames, importReference, compoundName);
            }
        } else {
            this.checkAndRecordImportBinding(importBinding, typesBySimpleNames, importReference, compoundName);
        }
        if ((mask & 8) != 0 && importBinding.kind() == 8) {
            this.checkMoreStaticBindings(compoundName, typesBySimpleNames, mask &= 0xFFFFFFF7, importReference);
        }
    }

    private int checkAndRecordImportBinding(Binding importBinding, HashtableOfType typesBySimpleNames, ImportReference importReference, char[][] compoundName) {
        ReferenceBinding conflictingType = null;
        if (importBinding instanceof MethodBinding && (!(conflictingType = (ReferenceBinding)this.getType(compoundName, compoundName.length)).isValidBinding() || importReference.isStatic() && !conflictingType.isStatic())) {
            conflictingType = null;
        }
        char[] name = importReference.getSimpleName();
        if (importBinding instanceof ReferenceBinding || conflictingType != null) {
            ReferenceBinding existingType;
            ReferenceBinding typeToCheck;
            ReferenceBinding referenceBinding = conflictingType == null ? (ReferenceBinding)importBinding : conflictingType;
            ReferenceBinding referenceBinding2 = typeToCheck = referenceBinding.problemId() == 3 ? ((ProblemReferenceBinding)referenceBinding).closestMatch : referenceBinding;
            if (importReference.isTypeUseDeprecated(typeToCheck, this)) {
                this.problemReporter().deprecatedType(typeToCheck, importReference);
            }
            if ((existingType = typesBySimpleNames.get(name)) != null) {
                if (TypeBinding.equalsEquals(existingType, referenceBinding)) {
                    int j = 0;
                    while (j < this.importPtr) {
                        ImportBinding resolved = this.tempImports[j];
                        if (resolved instanceof ImportConflictBinding) {
                            ImportConflictBinding importConflictBinding = (ImportConflictBinding)resolved;
                            if (TypeBinding.equalsEquals(importConflictBinding.conflictingTypeBinding, referenceBinding) && !importReference.isStatic()) {
                                this.problemReporter().duplicateImport(importReference);
                                this.recordImportBinding(new ImportBinding(compoundName, false, importBinding, importReference));
                            }
                        } else if (resolved.resolvedImport == referenceBinding && importReference.isStatic() != resolved.isStatic()) {
                            this.recordImportBinding(new ImportBinding(compoundName, false, importBinding, importReference));
                        }
                        ++j;
                    }
                    return -1;
                }
                int j = 0;
                int length = this.topLevelTypes.length;
                while (j < length) {
                    if (CharOperation.equals(this.topLevelTypes[j].sourceName, existingType.sourceName)) {
                        this.problemReporter().conflictingImport(importReference);
                        return -1;
                    }
                    ++j;
                }
                if (importReference.isStatic() && importBinding instanceof ReferenceBinding && this.compilerOptions().sourceLevel >= 0x340000L) {
                    j = 0;
                    while (j < this.importPtr) {
                        ImportBinding resolved = this.tempImports[j];
                        if (resolved.isStatic() && resolved.resolvedImport instanceof ReferenceBinding && importBinding != resolved.resolvedImport && CharOperation.equals(compoundName[compoundName.length - 1], resolved.compoundName[resolved.compoundName.length - 1])) {
                            ReferenceBinding type = (ReferenceBinding)resolved.resolvedImport;
                            resolved.resolvedImport = new ProblemReferenceBinding(new char[][]{name}, type, 3);
                            return -1;
                        }
                        ++j;
                    }
                }
                this.problemReporter().duplicateImport(importReference);
                return -1;
            }
            typesBySimpleNames.put(name, referenceBinding);
        } else if (importBinding instanceof FieldBinding) {
            int j = 0;
            while (j < this.importPtr) {
                ImportBinding resolved = this.tempImports[j];
                if (resolved.isStatic() && resolved.resolvedImport instanceof FieldBinding && importBinding != resolved.resolvedImport && CharOperation.equals(name, resolved.compoundName[resolved.compoundName.length - 1])) {
                    if (this.compilerOptions().sourceLevel >= 0x340000L) {
                        FieldBinding field = (FieldBinding)resolved.resolvedImport;
                        resolved.resolvedImport = new ProblemFieldBinding(field, field.declaringClass, name, 3);
                        return -1;
                    }
                    this.problemReporter().duplicateImport(importReference);
                    return -1;
                }
                ++j;
            }
        }
        if (conflictingType == null) {
            this.recordImportBinding(new ImportBinding(compoundName, false, importBinding, importReference));
        } else {
            this.recordImportBinding(new ImportConflictBinding(compoundName, importBinding, conflictingType, importReference));
        }
        return this.importPtr;
    }

    @Override
    public boolean hasDefaultNullnessFor(int location, int sourceStart) {
        int nonNullByDefaultValue = this.localNonNullByDefaultValue(sourceStart);
        if (nonNullByDefaultValue != 0) {
            return (nonNullByDefaultValue & location) != 0;
        }
        if (this.fPackage != null) {
            return (this.fPackage.getDefaultNullness() & location) != 0;
        }
        return false;
    }

    @Override
    public Binding checkRedundantDefaultNullness(int nullBits, int sourceStart) {
        Binding target = this.localCheckRedundantDefaultNullness(nullBits, sourceStart);
        if (target != null) {
            return target;
        }
        if (this.fPackage != null) {
            return this.fPackage.findDefaultNullnessTarget(n2 -> n2 == nullBits);
        }
        return null;
    }

    public void registerInferredInvocation(Invocation invocation) {
        if (this.inferredInvocations == null) {
            this.inferredInvocations = new ArrayList();
        }
        this.inferredInvocations.add(invocation);
    }

    public void cleanUpInferenceContexts() {
        if (this.inferredInvocations == null) {
            return;
        }
        for (Invocation invocation : this.inferredInvocations) {
            invocation.cleanUpInferenceContexts();
        }
        this.inferredInvocations = null;
    }
}

