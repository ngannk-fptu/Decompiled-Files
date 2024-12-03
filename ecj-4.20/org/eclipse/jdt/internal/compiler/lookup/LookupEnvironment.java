/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.lang.invoke.LambdaMetafactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.ClassFilePool;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.env.IModule;
import org.eclipse.jdt.internal.compiler.env.IModuleAwareNameEnvironment;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.env.INameEnvironmentExtension;
import org.eclipse.jdt.internal.compiler.env.ITypeAnnotationWalker;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.ITypeRequestor;
import org.eclipse.jdt.internal.compiler.lookup.AnnotatableTypeSystem;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.CaptureBinding;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.ElementValuePair;
import org.eclipse.jdt.internal.compiler.lookup.ExternalAnnotationSuperimposer;
import org.eclipse.jdt.internal.compiler.lookup.IQualifiedTypeResolutionListener;
import org.eclipse.jdt.internal.compiler.lookup.ImportBinding;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodVerifier;
import org.eclipse.jdt.internal.compiler.lookup.MethodVerifier15;
import org.eclipse.jdt.internal.compiler.lookup.MissingTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedGenericMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.PlainPackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.PolyTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.PolymorphicMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemPackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReasons;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.RawTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SignatureWrapper;
import org.eclipse.jdt.internal.compiler.lookup.SourceModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.SplitPackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeSystem;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.UnresolvedAnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.UnresolvedReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.util.HashtableOfModule;
import org.eclipse.jdt.internal.compiler.util.HashtableOfPackage;
import org.eclipse.jdt.internal.compiler.util.SimpleLookupTable;

public class LookupEnvironment
implements ProblemReasons,
TypeConstants {
    private Map accessRestrictions;
    ImportBinding[] defaultImports;
    public final LookupEnvironment root;
    public ModuleBinding UnNamedModule;
    public ModuleBinding JavaBaseModule;
    public ModuleBinding module;
    public PlainPackageBinding defaultPackage;
    HashtableOfPackage knownPackages;
    private int lastCompletedUnitIndex = -1;
    private int lastUnitIndex = -1;
    TypeSystem typeSystem;
    public INameEnvironment nameEnvironment;
    public CompilerOptions globalOptions;
    public ProblemReporter problemReporter;
    public ClassFilePool classFilePool;
    private int stepCompleted;
    public ITypeRequestor typeRequestor;
    private SimpleLookupTable uniqueParameterizedGenericMethodBindings;
    private SimpleLookupTable uniquePolymorphicMethodBindings;
    private SimpleLookupTable uniqueGetClassMethodBinding;
    boolean useModuleSystem;
    public HashtableOfModule knownModules;
    public CompilationUnitDeclaration unitBeingCompleted = null;
    public Object missingClassFileLocation = null;
    private CompilationUnitDeclaration[] units = new CompilationUnitDeclaration[4];
    private MethodVerifier verifier;
    private ArrayList missingTypes;
    Set<SourceTypeBinding> typesBeingConnected;
    public boolean isProcessingAnnotations = false;
    public boolean mayTolerateMissingType = false;
    PackageBinding nullableAnnotationPackage;
    PackageBinding nonnullAnnotationPackage;
    PackageBinding nonnullByDefaultAnnotationPackage;
    AnnotationBinding nonNullAnnotation;
    AnnotationBinding nullableAnnotation;
    Map<String, Integer> allNullAnnotations = null;
    final List<MethodBinding> deferredEnumMethods;
    InferenceContext18 currentInferenceContext;
    public boolean suppressImportErrors;
    public String moduleVersion;
    static final int BUILD_FIELDS_AND_METHODS = 4;
    static final int BUILD_TYPE_HIERARCHY = 1;
    static final int CHECK_AND_SET_IMPORTS = 2;
    static final int CONNECT_TYPE_HIERARCHY = 3;
    static final ProblemPackageBinding TheNotFoundPackage = new ProblemPackageBinding(CharOperation.NO_CHAR, 1, null);
    static final ProblemReferenceBinding TheNotFoundType = new ProblemReferenceBinding(CharOperation.NO_CHAR_CHAR, null, 1);
    static final ModuleBinding TheNotFoundModule = new ModuleBinding(CharOperation.NO_CHAR);
    public IQualifiedTypeResolutionListener[] resolutionListeners;

    public LookupEnvironment(ITypeRequestor typeRequestor, CompilerOptions globalOptions, ProblemReporter problemReporter, INameEnvironment nameEnvironment) {
        this.root = this;
        this.module = this.UnNamedModule = new ModuleBinding.UnNamedModule(this);
        this.typeRequestor = typeRequestor;
        this.globalOptions = globalOptions;
        this.problemReporter = problemReporter;
        this.defaultPackage = new PlainPackageBinding(this);
        this.defaultImports = null;
        this.nameEnvironment = nameEnvironment;
        this.knownPackages = new HashtableOfPackage();
        this.uniqueParameterizedGenericMethodBindings = new SimpleLookupTable(3);
        this.uniquePolymorphicMethodBindings = new SimpleLookupTable(3);
        this.missingTypes = null;
        this.accessRestrictions = new HashMap(3);
        this.classFilePool = ClassFilePool.newInstance();
        this.typesBeingConnected = new HashSet<SourceTypeBinding>();
        this.deferredEnumMethods = new ArrayList<MethodBinding>();
        this.typeSystem = this.globalOptions.sourceLevel >= 0x340000L && this.globalOptions.storeAnnotations ? new AnnotatableTypeSystem(this) : new TypeSystem(this);
        this.knownModules = new HashtableOfModule();
        this.useModuleSystem = nameEnvironment instanceof IModuleAwareNameEnvironment && globalOptions.complianceLevel >= 0x350000L;
        this.resolutionListeners = new IQualifiedTypeResolutionListener[0];
    }

    LookupEnvironment(LookupEnvironment rootEnv, ModuleBinding module) {
        this.root = rootEnv;
        this.UnNamedModule = rootEnv.UnNamedModule;
        this.module = module;
        this.typeRequestor = rootEnv.typeRequestor;
        this.globalOptions = rootEnv.globalOptions;
        this.problemReporter = rootEnv.problemReporter;
        this.defaultPackage = new PlainPackageBinding(this);
        this.defaultImports = null;
        this.nameEnvironment = rootEnv.nameEnvironment;
        this.knownPackages = new HashtableOfPackage();
        this.uniqueParameterizedGenericMethodBindings = new SimpleLookupTable(3);
        this.uniquePolymorphicMethodBindings = new SimpleLookupTable(3);
        this.missingTypes = null;
        this.accessRestrictions = new HashMap(3);
        this.classFilePool = rootEnv.classFilePool;
        this.typesBeingConnected = rootEnv.typesBeingConnected;
        this.deferredEnumMethods = rootEnv.deferredEnumMethods;
        this.typeSystem = rootEnv.typeSystem;
        this.useModuleSystem = rootEnv.useModuleSystem;
    }

    public ModuleBinding getModule(char[] name) {
        if (this.root != this) {
            return this.root.getModule(name);
        }
        if (name == null || name == ModuleBinding.UNNAMED || CharOperation.equals(name, ModuleBinding.ALL_UNNAMED)) {
            return this.UnNamedModule;
        }
        ModuleBinding moduleBinding = this.knownModules.get(name);
        if (moduleBinding == null) {
            if (this.useModuleSystem) {
                IModule mod = ((IModuleAwareNameEnvironment)this.nameEnvironment).getModule(name);
                if (mod != null) {
                    this.typeRequestor.accept(mod, this);
                    moduleBinding = this.root.knownModules.get(name);
                }
            } else {
                return this.UnNamedModule;
            }
        }
        return moduleBinding;
    }

    public ReferenceBinding askForType(char[][] compoundName, ModuleBinding clientModule) {
        assert (clientModule != null) : "lookup needs a module";
        NameEnvironmentAnswer[] answers = null;
        if (this.useModuleSystem) {
            IModuleAwareNameEnvironment moduleEnv = (IModuleAwareNameEnvironment)this.nameEnvironment;
            answers = this.askForTypeFromModules(clientModule, clientModule.getAllRequiredModules(), mod -> moduleEnv.findType(compoundName, mod.nameForLookup()));
        } else {
            NameEnvironmentAnswer answer = this.nameEnvironment.findType(compoundName);
            if (answer != null) {
                answer.moduleBinding = this.module;
                answers = new NameEnvironmentAnswer[]{answer};
            }
        }
        if (answers == null) {
            return null;
        }
        ReferenceBinding candidate = null;
        NameEnvironmentAnswer[] nameEnvironmentAnswerArray = answers;
        int n = answers.length;
        int n2 = 0;
        while (n2 < n) {
            NameEnvironmentAnswer answer = nameEnvironmentAnswerArray[n2];
            if (answer != null) {
                ReferenceBinding binding;
                PackageBinding pkg;
                ModuleBinding answerModule;
                ModuleBinding moduleBinding = answerModule = answer.moduleBinding != null ? answer.moduleBinding : this.UnNamedModule;
                if (answer.isBinaryType()) {
                    pkg = answerModule.environment.computePackageFrom(compoundName, false);
                    this.typeRequestor.accept(answer.getBinaryType(), pkg, answer.getAccessRestriction());
                    binding = pkg.getType0(compoundName[compoundName.length - 1]);
                    if (binding instanceof BinaryTypeBinding) {
                        ((BinaryTypeBinding)binding).module = answerModule;
                        if (pkg.enclosingModule == null) {
                            pkg.enclosingModule = answerModule;
                        }
                    }
                } else if (answer.isCompilationUnit()) {
                    this.typeRequestor.accept(answer.getCompilationUnit(), answer.getAccessRestriction());
                } else if (answer.isSourceType()) {
                    pkg = answerModule.environment.computePackageFrom(compoundName, false);
                    this.typeRequestor.accept(answer.getSourceTypes(), pkg, answer.getAccessRestriction());
                    binding = pkg.getType0(compoundName[compoundName.length - 1]);
                    if (binding instanceof SourceTypeBinding) {
                        ((SourceTypeBinding)binding).module = answerModule;
                        if (pkg.enclosingModule == null) {
                            pkg.enclosingModule = answerModule;
                        }
                    }
                }
                candidate = this.combine(candidate, answerModule.environment.getCachedType(compoundName), clientModule);
            }
            ++n2;
        }
        return candidate;
    }

    /*
     * Unable to fully structure code
     */
    ReferenceBinding askForType(PackageBinding packageBinding, char[] name, ModuleBinding clientModule) {
        if (!LookupEnvironment.$assertionsDisabled && clientModule == null) {
            throw new AssertionError((Object)"lookup needs a module");
        }
        if (packageBinding == null) {
            packageBinding = this.defaultPackage;
        }
        answers = null;
        if (this.useModuleSystem) {
            moduleEnv = (IModuleAwareNameEnvironment)this.nameEnvironment;
            pack = packageBinding;
            answers = this.askForTypeFromModules(null, packageBinding.getDeclaringModules(), (Function<ModuleBinding, NameEnvironmentAnswer>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, lambda$1(org.eclipse.jdt.internal.compiler.env.IModuleAwareNameEnvironment org.eclipse.jdt.internal.compiler.lookup.PackageBinding char[] org.eclipse.jdt.internal.compiler.lookup.ModuleBinding ), (Lorg/eclipse/jdt/internal/compiler/lookup/ModuleBinding;)Lorg/eclipse/jdt/internal/compiler/env/NameEnvironmentAnswer;)((IModuleAwareNameEnvironment)moduleEnv, (PackageBinding)pack, (char[])name));
        } else {
            answer = this.nameEnvironment.findType(name, packageBinding.compoundName);
            if (answer != null) {
                answer.moduleBinding = this.module;
                answers = new NameEnvironmentAnswer[]{answer};
            }
        }
        if (answers == null) {
            return null;
        }
        candidate = null;
        var9_7 = answers;
        var8_8 = answers.length;
        var7_9 = 0;
        while (var7_9 < var8_8) {
            block17: {
                block20: {
                    block19: {
                        block18: {
                            answer = var9_7[var7_9];
                            if (answer == null) break block17;
                            if (candidate != null && candidate.problemId() == 3) {
                                return candidate;
                            }
                            answerModule = answer.moduleBinding != null ? answer.moduleBinding : this.UnNamedModule;
                            answerPackage = packageBinding;
                            if (answerModule == null) break block18;
                            if (!answerPackage.isDeclaredIn(answerModule)) break block17;
                            answerPackage = answerPackage.getIncarnation(answerModule);
                        }
                        if (!answer.isResolvedBinding()) break block19;
                        candidate = this.combine((ReferenceBinding)candidate, answer.getResolvedBinding(), clientModule);
                        break block17;
                    }
                    if (!answer.isBinaryType()) break block20;
                    this.typeRequestor.accept(answer.getBinaryType(), answerPackage, answer.getAccessRestriction());
                    binding = answerPackage.getType0(name);
                    if (binding instanceof BinaryTypeBinding) {
                        ((BinaryTypeBinding)binding).module = answerModule;
                    }
                    ** GOTO lbl-1000
                }
                if (answer.isCompilationUnit()) {
                    try {
                        this.typeRequestor.accept(answer.getCompilationUnit(), answer.getAccessRestriction());
                    }
                    catch (AbortCompilation abort) {
                        if (CharOperation.equals(name, TypeConstants.PACKAGE_INFO_NAME)) {
                            return null;
                        }
                        throw abort;
                    }
                }
                if (answer.isSourceType()) {
                    this.typeRequestor.accept(answer.getSourceTypes(), answerPackage, answer.getAccessRestriction());
                    binding = answerPackage.getType0(name);
                    if (binding instanceof SourceTypeBinding) {
                        ((SourceTypeBinding)binding).module = answerModule;
                    }
                    if ((externalAnnotationPath = answer.getExternalAnnotationPath()) != null && this.globalOptions.isAnnotationBasedNullAnalysisEnabled && binding instanceof SourceTypeBinding) {
                        ExternalAnnotationSuperimposer.apply((SourceTypeBinding)binding, externalAnnotationPath);
                    }
                    candidate = this.combine((ReferenceBinding)candidate, binding, clientModule);
                } else lbl-1000:
                // 3 sources

                {
                    candidate = this.combine((ReferenceBinding)candidate, answerPackage.getType0(name), clientModule);
                }
            }
            ++var7_9;
        }
        return candidate;
    }

    private ReferenceBinding combine(ReferenceBinding one, ReferenceBinding two, ModuleBinding clientModule) {
        if (one == null) {
            return two;
        }
        if (two == null) {
            return one;
        }
        if (one.fPackage == null || !clientModule.canAccess(one.fPackage)) {
            return two;
        }
        if (two.fPackage == null || !clientModule.canAccess(two.fPackage)) {
            return one;
        }
        if (one == two) {
            return one;
        }
        return new ProblemReferenceBinding(one.compoundName, one, 3);
    }

    private NameEnvironmentAnswer[] askForTypeFromModules(ModuleBinding clientModule, ModuleBinding[] otherModules, Function<ModuleBinding, NameEnvironmentAnswer> oracle) {
        if (clientModule != null && clientModule.nameForLookup().length == 0) {
            NameEnvironmentAnswer answer = oracle.apply(clientModule);
            if (answer != null) {
                answer.moduleBinding = this.root.getModuleFromAnswer(answer);
            }
            return new NameEnvironmentAnswer[]{answer};
        }
        boolean found = false;
        NameEnvironmentAnswer[] answers = null;
        if (clientModule != null) {
            answers = new NameEnvironmentAnswer[otherModules.length + 1];
            NameEnvironmentAnswer answer = oracle.apply(clientModule);
            if (answer != null) {
                answer.moduleBinding = clientModule;
                answers[answers.length - 1] = answer;
                found = true;
            }
        } else {
            answers = new NameEnvironmentAnswer[otherModules.length];
        }
        int i = 0;
        while (i < otherModules.length) {
            NameEnvironmentAnswer answer = oracle.apply(otherModules[i]);
            if (answer != null) {
                if (answer.moduleBinding == null) {
                    char[] nameFromAnswer = answer.moduleName();
                    answer.moduleBinding = CharOperation.equals(nameFromAnswer, otherModules[i].moduleName) ? otherModules[i] : this.getModule(nameFromAnswer);
                }
                answers[i] = answer;
                found = true;
            }
            ++i;
        }
        return found ? answers : null;
    }

    private static NameEnvironmentAnswer fromSplitPackageOrOracle(IModuleAwareNameEnvironment moduleEnv, ModuleBinding module, PackageBinding packageBinding, char[] name) {
        ReferenceBinding binding;
        if (packageBinding instanceof SplitPackageBinding && (binding = ((SplitPackageBinding)packageBinding).getType0ForModule(module, name)) != null && binding.isValidBinding()) {
            if (binding instanceof UnresolvedReferenceBinding) {
                binding = ((UnresolvedReferenceBinding)binding).resolve(module.environment, false);
            }
            if (binding.isValidBinding()) {
                return new NameEnvironmentAnswer(binding, module);
            }
        }
        return moduleEnv.findType(name, packageBinding.compoundName, module.nameForLookup());
    }

    private ModuleBinding getModuleFromAnswer(NameEnvironmentAnswer answer) {
        char[] moduleName = answer.moduleName();
        if (moduleName != null) {
            ModuleBinding moduleBinding;
            if (!this.useModuleSystem || moduleName == ModuleBinding.UNNAMED) {
                moduleBinding = this.UnNamedModule;
            } else {
                moduleBinding = this.knownModules.get(moduleName);
                if (moduleBinding == null && this.nameEnvironment instanceof IModuleAwareNameEnvironment) {
                    IModule iModule = ((IModuleAwareNameEnvironment)this.nameEnvironment).getModule(moduleName);
                    try {
                        this.typeRequestor.accept(iModule, this);
                        moduleBinding = this.knownModules.get(moduleName);
                    }
                    catch (NullPointerException e) {
                        System.err.println("Bug 529367: moduleName: " + new String(moduleName) + "iModule null" + (iModule == null ? "true" : "false"));
                        throw e;
                    }
                }
            }
            return moduleBinding;
        }
        return null;
    }

    public boolean canTypeBeAccessed(SourceTypeBinding binding, Scope scope) {
        ModuleBinding client = scope.module();
        return client.canAccess(binding.fPackage);
    }

    public void buildTypeBindings(CompilationUnitDeclaration unit, AccessRestriction accessRestriction) {
        CompilationUnitScope scope;
        ModuleBinding unitModule = null;
        if (unit.moduleDeclaration != null) {
            char[] moduleName = unit.moduleDeclaration.moduleName;
            scope = new CompilationUnitScope(unit, this.globalOptions);
            unitModule = unit.moduleDeclaration.setBinding(new SourceModuleBinding(moduleName, scope, this.root));
        } else {
            if (this.globalOptions.sourceLevel >= 0x350000L) {
                unitModule = unit.module(this);
            }
            scope = new CompilationUnitScope(unit, unitModule != null ? unitModule.environment : this);
        }
        scope.buildTypeBindings(accessRestriction);
        LookupEnvironment rootEnv = this.root;
        int unitsLength = rootEnv.units.length;
        if (++rootEnv.lastUnitIndex >= unitsLength) {
            rootEnv.units = new CompilationUnitDeclaration[2 * unitsLength];
            System.arraycopy(rootEnv.units, 0, rootEnv.units, 0, unitsLength);
        }
        rootEnv.units[rootEnv.lastUnitIndex] = unit;
    }

    public BinaryTypeBinding cacheBinaryType(IBinaryType binaryType, AccessRestriction accessRestriction) {
        return this.cacheBinaryType(binaryType, true, accessRestriction);
    }

    public BinaryTypeBinding cacheBinaryType(IBinaryType binaryType, boolean needFieldsAndMethods, AccessRestriction accessRestriction) {
        char[][] compoundName = CharOperation.splitOn('/', binaryType.getName());
        ReferenceBinding existingType = this.getCachedType(compoundName);
        if (existingType == null || existingType instanceof UnresolvedReferenceBinding) {
            return this.createBinaryTypeFrom(binaryType, this.computePackageFrom(compoundName, false), needFieldsAndMethods, accessRestriction);
        }
        return null;
    }

    public void completeTypeBindings() {
        if (this != this.root) {
            this.root.completeTypeBindings();
            return;
        }
        this.stepCompleted = 1;
        int i = this.lastCompletedUnitIndex + 1;
        while (i <= this.lastUnitIndex) {
            this.unitBeingCompleted = this.units[i];
            this.unitBeingCompleted.scope.checkAndSetImports();
            ++i;
        }
        this.stepCompleted = 2;
        i = this.lastCompletedUnitIndex + 1;
        while (i <= this.lastUnitIndex) {
            this.unitBeingCompleted = this.units[i];
            this.unitBeingCompleted.scope.connectTypeHierarchy();
            ++i;
        }
        this.stepCompleted = 3;
        i = this.lastCompletedUnitIndex + 1;
        while (i <= this.lastUnitIndex) {
            this.unitBeingCompleted = this.units[i];
            CompilationUnitScope unitScope = this.unitBeingCompleted.scope;
            unitScope.checkParameterizedTypes();
            unitScope.buildFieldsAndMethods();
            this.units[i] = null;
            ++i;
        }
        this.stepCompleted = 4;
        this.lastCompletedUnitIndex = this.lastUnitIndex;
        this.unitBeingCompleted = null;
    }

    public void completeTypeBindings(CompilationUnitDeclaration parsedUnit) {
        if (this != this.root) {
            this.root.completeTypeBindings(parsedUnit);
            return;
        }
        if (this.stepCompleted == 4) {
            this.completeTypeBindings();
        } else {
            if (parsedUnit.scope == null) {
                return;
            }
            if (this.stepCompleted >= 2) {
                this.unitBeingCompleted = parsedUnit;
                this.unitBeingCompleted.scope.checkAndSetImports();
            }
            if (this.stepCompleted >= 3) {
                this.unitBeingCompleted = parsedUnit;
                this.unitBeingCompleted.scope.connectTypeHierarchy();
            }
            this.unitBeingCompleted = null;
        }
    }

    public void completeTypeBindings(CompilationUnitDeclaration parsedUnit, boolean buildFieldsAndMethods) {
        if (parsedUnit.scope == null) {
            return;
        }
        LookupEnvironment rootEnv = this.root;
        CompilationUnitDeclaration previousUnitBeingCompleted = rootEnv.unitBeingCompleted;
        rootEnv.unitBeingCompleted = parsedUnit;
        rootEnv.unitBeingCompleted.scope.checkAndSetImports();
        parsedUnit.scope.connectTypeHierarchy();
        parsedUnit.scope.checkParameterizedTypes();
        if (buildFieldsAndMethods) {
            parsedUnit.scope.buildFieldsAndMethods();
        }
        rootEnv.unitBeingCompleted = previousUnitBeingCompleted;
    }

    public void completeTypeBindings(CompilationUnitDeclaration[] parsedUnits, boolean[] buildFieldsAndMethods, int unitCount) {
        CompilationUnitDeclaration parsedUnit;
        LookupEnvironment rootEnv = this.root;
        int i = 0;
        while (i < unitCount) {
            parsedUnit = parsedUnits[i];
            if (parsedUnit.scope != null) {
                rootEnv.unitBeingCompleted = parsedUnit;
                rootEnv.unitBeingCompleted.scope.checkAndSetImports();
            }
            ++i;
        }
        i = 0;
        while (i < unitCount) {
            parsedUnit = parsedUnits[i];
            if (parsedUnit.scope != null) {
                rootEnv.unitBeingCompleted = parsedUnit;
                rootEnv.unitBeingCompleted.scope.connectTypeHierarchy();
            }
            ++i;
        }
        i = 0;
        while (i < unitCount) {
            parsedUnit = parsedUnits[i];
            if (parsedUnit.scope != null) {
                rootEnv.unitBeingCompleted = parsedUnit;
                rootEnv.unitBeingCompleted.scope.checkParameterizedTypes();
                if (buildFieldsAndMethods[i]) {
                    parsedUnit.scope.buildFieldsAndMethods();
                }
            }
            ++i;
        }
        rootEnv.unitBeingCompleted = null;
    }

    public TypeBinding computeBoxingType(TypeBinding type) {
        switch (type.id) {
            case 33: {
                return TypeBinding.BOOLEAN;
            }
            case 26: {
                return TypeBinding.BYTE;
            }
            case 28: {
                return TypeBinding.CHAR;
            }
            case 27: {
                return TypeBinding.SHORT;
            }
            case 32: {
                return TypeBinding.DOUBLE;
            }
            case 31: {
                return TypeBinding.FLOAT;
            }
            case 29: {
                return TypeBinding.INT;
            }
            case 30: {
                return TypeBinding.LONG;
            }
            case 10: {
                ReferenceBinding boxedType = this.getType(JAVA_LANG_INTEGER, this.javaBaseModule());
                if (boxedType != null) {
                    return boxedType;
                }
                return new ProblemReferenceBinding(JAVA_LANG_INTEGER, null, 1);
            }
            case 3: {
                ReferenceBinding boxedType = this.getType(JAVA_LANG_BYTE, this.javaBaseModule());
                if (boxedType != null) {
                    return boxedType;
                }
                return new ProblemReferenceBinding(JAVA_LANG_BYTE, null, 1);
            }
            case 4: {
                ReferenceBinding boxedType = this.getType(JAVA_LANG_SHORT, this.javaBaseModule());
                if (boxedType != null) {
                    return boxedType;
                }
                return new ProblemReferenceBinding(JAVA_LANG_SHORT, null, 1);
            }
            case 2: {
                ReferenceBinding boxedType = this.getType(JAVA_LANG_CHARACTER, this.javaBaseModule());
                if (boxedType != null) {
                    return boxedType;
                }
                return new ProblemReferenceBinding(JAVA_LANG_CHARACTER, null, 1);
            }
            case 7: {
                ReferenceBinding boxedType = this.getType(JAVA_LANG_LONG, this.javaBaseModule());
                if (boxedType != null) {
                    return boxedType;
                }
                return new ProblemReferenceBinding(JAVA_LANG_LONG, null, 1);
            }
            case 9: {
                ReferenceBinding boxedType = this.getType(JAVA_LANG_FLOAT, this.javaBaseModule());
                if (boxedType != null) {
                    return boxedType;
                }
                return new ProblemReferenceBinding(JAVA_LANG_FLOAT, null, 1);
            }
            case 8: {
                ReferenceBinding boxedType = this.getType(JAVA_LANG_DOUBLE, this.javaBaseModule());
                if (boxedType != null) {
                    return boxedType;
                }
                return new ProblemReferenceBinding(JAVA_LANG_DOUBLE, null, 1);
            }
            case 5: {
                ReferenceBinding boxedType = this.getType(JAVA_LANG_BOOLEAN, this.javaBaseModule());
                if (boxedType != null) {
                    return boxedType;
                }
                return new ProblemReferenceBinding(JAVA_LANG_BOOLEAN, null, 1);
            }
        }
        switch (type.kind()) {
            case 516: 
            case 4100: 
            case 8196: 
            case 32772: {
                switch (type.erasure().id) {
                    case 33: {
                        return TypeBinding.BOOLEAN;
                    }
                    case 26: {
                        return TypeBinding.BYTE;
                    }
                    case 28: {
                        return TypeBinding.CHAR;
                    }
                    case 27: {
                        return TypeBinding.SHORT;
                    }
                    case 32: {
                        return TypeBinding.DOUBLE;
                    }
                    case 31: {
                        return TypeBinding.FLOAT;
                    }
                    case 29: {
                        return TypeBinding.INT;
                    }
                    case 30: {
                        return TypeBinding.LONG;
                    }
                }
                break;
            }
            case 65540: {
                return ((PolyTypeBinding)type).computeBoxingType();
            }
        }
        return type;
    }

    public ModuleBinding javaBaseModule() {
        if (this.JavaBaseModule != null) {
            return this.JavaBaseModule;
        }
        if (this.root != this) {
            this.JavaBaseModule = this.root.javaBaseModule();
            return this.JavaBaseModule;
        }
        ModuleBinding resolvedModel = null;
        if (this.useModuleSystem) {
            resolvedModel = this.getModule(TypeConstants.JAVA_BASE);
        }
        this.JavaBaseModule = resolvedModel != null ? resolvedModel : this.UnNamedModule;
        return this.JavaBaseModule;
    }

    private PackageBinding computePackageFrom(char[][] constantPoolName, boolean isMissing) {
        if (constantPoolName.length == 1) {
            return this.defaultPackage;
        }
        PackageBinding packageBinding = this.getPackage0(constantPoolName[0]);
        if (packageBinding == null || packageBinding == TheNotFoundPackage) {
            if (this.useModuleSystem) {
                if (this.module.isUnnamed()) {
                    char[][] declaringModules = ((IModuleAwareNameEnvironment)this.nameEnvironment).getUniqueModulesDeclaringPackage(new char[][]{constantPoolName[0]}, ModuleBinding.ANY);
                    if (declaringModules != null) {
                        char[][] cArray = declaringModules;
                        int n = declaringModules.length;
                        int n2 = 0;
                        while (n2 < n) {
                            char[] cArray2 = cArray[n2];
                            ModuleBinding declaringModule = this.root.getModule(cArray2);
                            if (declaringModule != null) {
                                packageBinding = SplitPackageBinding.combine(declaringModule.getTopLevelPackage(constantPoolName[0]), packageBinding, this.module);
                            }
                            ++n2;
                        }
                    }
                } else {
                    packageBinding = this.module.getTopLevelPackage(constantPoolName[0]);
                }
            }
            if (packageBinding == null || packageBinding == TheNotFoundPackage) {
                packageBinding = this.module.createDeclaredToplevelPackage(constantPoolName[0]);
            }
            if (isMissing) {
                packageBinding.tagBits |= 0x80L;
            }
            this.knownPackages.put(constantPoolName[0], packageBinding);
        }
        int i = 1;
        int n = constantPoolName.length - 1;
        while (i < n) {
            PackageBinding parent = packageBinding;
            if ((packageBinding = parent.getPackage0(constantPoolName[i])) == null || packageBinding == TheNotFoundPackage) {
                if (this.useModuleSystem) {
                    if (this.module.isUnnamed()) {
                        char[][] currentCompoundName = CharOperation.arrayConcat(parent.compoundName, constantPoolName[i]);
                        char[][] declaringModules = ((IModuleAwareNameEnvironment)this.nameEnvironment).getModulesDeclaringPackage(currentCompoundName, ModuleBinding.ANY);
                        if (declaringModules != null) {
                            char[][] cArray = declaringModules;
                            int n2 = declaringModules.length;
                            int n3 = 0;
                            while (n3 < n2) {
                                char[] mod = cArray[n3];
                                ModuleBinding declaringModule = this.root.getModule(mod);
                                if (declaringModule != null) {
                                    packageBinding = SplitPackageBinding.combine(declaringModule.getVisiblePackage(currentCompoundName), packageBinding, this.module);
                                }
                                ++n3;
                            }
                        }
                    } else {
                        packageBinding = this.module.getVisiblePackage(parent, constantPoolName[i]);
                    }
                }
                if (packageBinding == null || packageBinding == TheNotFoundPackage) {
                    packageBinding = this.module.createDeclaredPackage(CharOperation.subarray(constantPoolName, 0, i + 1), parent);
                }
                if (isMissing) {
                    packageBinding.tagBits |= 0x80L;
                }
                packageBinding = parent.addPackage(packageBinding, this.module);
            }
            ++i;
        }
        if (packageBinding instanceof SplitPackageBinding) {
            PackageBinding candidate = null;
            for (PackageBinding packageBinding2 : ((SplitPackageBinding)packageBinding).incarnations) {
                if (!packageBinding2.hasCompilationUnit(false)) continue;
                if (candidate != null) {
                    candidate = null;
                    break;
                }
                candidate = packageBinding2;
            }
            if (candidate != null) {
                return candidate;
            }
        }
        return packageBinding;
    }

    public ReferenceBinding convertToParameterizedType(ReferenceBinding originalType) {
        if (originalType != null) {
            ReferenceBinding originalEnclosingType;
            boolean isGeneric = originalType.isGenericType();
            if (!isGeneric && !originalType.hasEnclosingInstanceContext()) {
                return originalType;
            }
            ReferenceBinding convertedEnclosingType = originalEnclosingType = originalType.enclosingType();
            boolean needToConvert = isGeneric;
            if (originalEnclosingType != null && originalType.hasEnclosingInstanceContext()) {
                convertedEnclosingType = this.convertToParameterizedType(originalEnclosingType);
                needToConvert |= TypeBinding.notEquals(originalEnclosingType, convertedEnclosingType);
            }
            if (needToConvert) {
                return this.createParameterizedType(originalType, isGeneric ? originalType.typeVariables() : null, convertedEnclosingType);
            }
        }
        return originalType;
    }

    public TypeBinding convertToRawType(TypeBinding type, boolean forceRawEnclosingType) {
        TypeBinding convertedType;
        boolean needToConvert;
        TypeBinding originalType;
        int dimension;
        switch (type.kind()) {
            case 132: 
            case 516: 
            case 1028: 
            case 4100: 
            case 8196: {
                return type;
            }
            case 68: {
                dimension = type.dimensions();
                originalType = type.leafComponentType();
                break;
            }
            default: {
                if (type.id == 1) {
                    return type;
                }
                dimension = 0;
                originalType = type;
            }
        }
        switch (originalType.kind()) {
            case 132: {
                return type;
            }
            case 2052: {
                needToConvert = true;
                break;
            }
            case 260: {
                ParameterizedTypeBinding paramType = (ParameterizedTypeBinding)originalType;
                needToConvert = paramType.genericType().isGenericType();
                break;
            }
            default: {
                needToConvert = false;
            }
        }
        forceRawEnclosingType &= !originalType.isStatic();
        ReferenceBinding originalEnclosing = originalType.enclosingType();
        if (originalEnclosing == null) {
            convertedType = needToConvert ? this.createRawType((ReferenceBinding)originalType.erasure(), null) : originalType;
        } else {
            ReferenceBinding convertedEnclosing;
            if (!((ReferenceBinding)originalType).hasEnclosingInstanceContext()) {
                convertedEnclosing = (ReferenceBinding)originalEnclosing.original();
            } else if (originalEnclosing.kind() == 1028) {
                convertedEnclosing = originalEnclosing;
                needToConvert = true;
            } else if (forceRawEnclosingType && !needToConvert) {
                convertedEnclosing = (ReferenceBinding)this.convertToRawType(originalEnclosing, forceRawEnclosingType);
                needToConvert = TypeBinding.notEquals(originalEnclosing, convertedEnclosing);
            } else {
                convertedEnclosing = needToConvert ? (ReferenceBinding)this.convertToRawType(originalEnclosing, false) : this.convertToParameterizedType(originalEnclosing);
            }
            convertedType = needToConvert ? this.createRawType((ReferenceBinding)originalType.erasure(), convertedEnclosing) : (TypeBinding.notEquals(originalEnclosing, convertedEnclosing) ? this.createParameterizedType((ReferenceBinding)originalType.erasure(), null, convertedEnclosing) : originalType);
        }
        if (TypeBinding.notEquals(originalType, convertedType)) {
            return dimension > 0 ? this.createArrayType(convertedType, dimension) : convertedType;
        }
        return type;
    }

    public ReferenceBinding[] convertToRawTypes(ReferenceBinding[] originalTypes, boolean forceErasure, boolean forceRawEnclosingType) {
        if (originalTypes == null) {
            return null;
        }
        ReferenceBinding[] convertedTypes = originalTypes;
        int i = 0;
        int length = originalTypes.length;
        while (i < length) {
            ReferenceBinding originalType = originalTypes[i];
            ReferenceBinding convertedType = (ReferenceBinding)this.convertToRawType(forceErasure ? originalType.erasure() : originalType, forceRawEnclosingType);
            if (TypeBinding.notEquals(convertedType, originalType)) {
                if (convertedTypes == originalTypes) {
                    convertedTypes = new ReferenceBinding[length];
                    System.arraycopy(originalTypes, 0, convertedTypes, 0, i);
                }
                convertedTypes[i] = convertedType;
            } else if (convertedTypes != originalTypes) {
                convertedTypes[i] = originalType;
            }
            ++i;
        }
        return convertedTypes;
    }

    public TypeBinding convertUnresolvedBinaryToRawType(TypeBinding type) {
        TypeBinding convertedType;
        boolean needToConvert;
        TypeBinding originalType;
        int dimension;
        switch (type.kind()) {
            case 132: 
            case 516: 
            case 1028: 
            case 4100: 
            case 8196: {
                return type;
            }
            case 68: {
                dimension = type.dimensions();
                originalType = type.leafComponentType();
                break;
            }
            default: {
                if (type.id == 1) {
                    return type;
                }
                dimension = 0;
                originalType = type;
            }
        }
        switch (originalType.kind()) {
            case 132: {
                return type;
            }
            case 2052: {
                needToConvert = true;
                break;
            }
            case 260: {
                ParameterizedTypeBinding paramType = (ParameterizedTypeBinding)originalType;
                needToConvert = paramType.genericType().isGenericType();
                break;
            }
            default: {
                needToConvert = false;
            }
        }
        ReferenceBinding originalEnclosing = originalType.enclosingType();
        if (originalEnclosing == null) {
            convertedType = needToConvert ? this.createRawType((ReferenceBinding)originalType.erasure(), null) : originalType;
        } else {
            if (!needToConvert && originalType.isStatic()) {
                return originalType;
            }
            ReferenceBinding convertedEnclosing = (ReferenceBinding)this.convertUnresolvedBinaryToRawType(originalEnclosing);
            if (TypeBinding.notEquals(convertedEnclosing, originalEnclosing)) {
                needToConvert = true;
            }
            convertedType = needToConvert ? this.createRawType((ReferenceBinding)originalType.erasure(), convertedEnclosing) : originalType;
        }
        if (TypeBinding.notEquals(originalType, convertedType)) {
            return dimension > 0 ? this.createArrayType(convertedType, dimension) : convertedType;
        }
        return type;
    }

    public AnnotationBinding createAnnotation(ReferenceBinding annotationType, ElementValuePair[] pairs) {
        if (pairs.length != 0) {
            AnnotationBinding.setMethodBindings(annotationType, pairs);
            return new AnnotationBinding(annotationType, pairs);
        }
        return this.typeSystem.getAnnotationType(annotationType, true);
    }

    public AnnotationBinding createUnresolvedAnnotation(ReferenceBinding annotationType, ElementValuePair[] pairs) {
        if (pairs.length != 0) {
            return new UnresolvedAnnotationBinding(annotationType, pairs, this);
        }
        return this.typeSystem.getAnnotationType(annotationType, false);
    }

    public ArrayBinding createArrayType(TypeBinding leafComponentType, int dimensionCount) {
        return this.typeSystem.getArrayType(leafComponentType, dimensionCount);
    }

    public ArrayBinding createArrayType(TypeBinding leafComponentType, int dimensionCount, AnnotationBinding[] annotations) {
        return this.typeSystem.getArrayType(leafComponentType, dimensionCount, annotations);
    }

    public TypeBinding createIntersectionType18(ReferenceBinding[] intersectingTypes) {
        if (!intersectingTypes[0].isClass()) {
            Arrays.sort(intersectingTypes, new Comparator<TypeBinding>(){

                @Override
                public int compare(TypeBinding o1, TypeBinding o2) {
                    return o1.isClass() ? -1 : (o2.isClass() ? 1 : CharOperation.compareTo(o1.readableName(), o2.readableName()));
                }
            });
        }
        return this.typeSystem.getIntersectionType18(intersectingTypes);
    }

    public BinaryTypeBinding createBinaryTypeFrom(IBinaryType binaryType, PackageBinding packageBinding, AccessRestriction accessRestriction) {
        return this.createBinaryTypeFrom(binaryType, packageBinding, true, accessRestriction);
    }

    public BinaryTypeBinding createBinaryTypeFrom(IBinaryType binaryType, PackageBinding packageBinding, boolean needFieldsAndMethods, AccessRestriction accessRestriction) {
        if (this != packageBinding.environment) {
            return packageBinding.environment.createBinaryTypeFrom(binaryType, packageBinding, needFieldsAndMethods, accessRestriction);
        }
        BinaryTypeBinding binaryBinding = new BinaryTypeBinding(packageBinding, binaryType, this);
        ReferenceBinding cachedType = packageBinding.getType0(binaryBinding.compoundName[binaryBinding.compoundName.length - 1]);
        if (cachedType != null && !cachedType.isUnresolvedType()) {
            if (cachedType.isBinaryBinding()) {
                return (BinaryTypeBinding)cachedType;
            }
            return null;
        }
        packageBinding.addType(binaryBinding);
        this.setAccessRestriction(binaryBinding, accessRestriction);
        binaryBinding.cachePartsFrom(binaryType, needFieldsAndMethods);
        return binaryBinding;
    }

    public MissingTypeBinding createMissingType(PackageBinding packageBinding, char[][] compoundName) {
        if (packageBinding == null && (packageBinding = this.computePackageFrom(compoundName, true)) == TheNotFoundPackage) {
            packageBinding = this.defaultPackage;
        }
        MissingTypeBinding missingType = new MissingTypeBinding(packageBinding, compoundName, this);
        if (missingType.id != 1) {
            ReferenceBinding objectType = this.getType(TypeConstants.JAVA_LANG_OBJECT, this.javaBaseModule());
            if (objectType == null) {
                objectType = this.createMissingType(null, TypeConstants.JAVA_LANG_OBJECT);
            }
            missingType.setMissingSuperclass(objectType);
        }
        packageBinding.addType(missingType);
        if (this.missingTypes == null) {
            this.missingTypes = new ArrayList(3);
        }
        this.missingTypes.add(missingType);
        return missingType;
    }

    public PackageBinding createPackage(char[][] compoundName) {
        return this.createPlainPackage(compoundName);
    }

    public PlainPackageBinding createPlainPackage(char[][] compoundName) {
        PackageBinding packageBinding = this.module.getDeclaredPackage(CharOperation.concatWith(compoundName, '.'));
        if (packageBinding != null && packageBinding.isValidBinding()) {
            packageBinding = this.getTopLevelPackage(compoundName[0]);
        } else {
            packageBinding = this.getPackage0(compoundName[0]);
            if (packageBinding == null || packageBinding == TheNotFoundPackage) {
                packageBinding = this.module.getOrCreateDeclaredPackage(new char[][]{compoundName[0]});
                if (this.useModuleSystem) {
                    char[][] declaringModuleNames = null;
                    if (this.module.isUnnamed()) {
                        IModuleAwareNameEnvironment moduleEnv = (IModuleAwareNameEnvironment)this.nameEnvironment;
                        declaringModuleNames = moduleEnv.getUniqueModulesDeclaringPackage(new char[][]{packageBinding.readableName()}, ModuleBinding.ANY);
                    }
                    packageBinding = this.module.combineWithPackagesFromOtherRelevantModules(packageBinding, packageBinding.compoundName, declaringModuleNames);
                }
                this.knownPackages.put(compoundName[0], packageBinding);
            }
        }
        int i = 1;
        int length = compoundName.length;
        while (i < length) {
            if (packageBinding.hasType0Any(compoundName[i])) {
                return null;
            }
            PackageBinding parent = packageBinding;
            if ((packageBinding = parent.getPackage0(compoundName[i])) == null || packageBinding == TheNotFoundPackage) {
                if (this.nameEnvironment instanceof INameEnvironmentExtension ? ((INameEnvironmentExtension)this.nameEnvironment).findType(compoundName[i], parent.compoundName, false, this.module.nameForLookup()) != null : this.nameEnvironment.findType(compoundName[i], parent.compoundName) != null) {
                    return null;
                }
                PlainPackageBinding singleParent = parent.getIncarnation(this.module);
                if (singleParent != parent && singleParent != null) {
                    packageBinding = singleParent.getPackage0(compoundName[i]);
                }
                if (packageBinding == null) {
                    packageBinding = this.module.createDeclaredPackage(CharOperation.subarray(compoundName, 0, i + 1), parent);
                    packageBinding = parent.addPackage(packageBinding, this.module);
                }
            }
            ++i;
        }
        return packageBinding.getIncarnation(this.module);
    }

    public ParameterizedGenericMethodBinding createParameterizedGenericMethod(MethodBinding genericMethod, RawTypeBinding rawType) {
        ParameterizedGenericMethodBinding parameterizedGenericMethod;
        ParameterizedGenericMethodBinding[] cachedInfo = (ParameterizedGenericMethodBinding[])this.uniqueParameterizedGenericMethodBindings.get(genericMethod);
        boolean needToGrow = false;
        int index = 0;
        if (cachedInfo != null) {
            int max = cachedInfo.length;
            while (index < max) {
                ParameterizedGenericMethodBinding cachedMethod = cachedInfo[index];
                if (cachedMethod == null) break;
                if (cachedMethod.isRaw && cachedMethod.declaringClass == (rawType == null ? genericMethod.declaringClass : rawType)) {
                    return cachedMethod;
                }
                ++index;
            }
            needToGrow = true;
        } else {
            cachedInfo = new ParameterizedGenericMethodBinding[5];
            this.uniqueParameterizedGenericMethodBindings.put(genericMethod, cachedInfo);
        }
        int length = cachedInfo.length;
        if (needToGrow && index == length) {
            ParameterizedGenericMethodBinding[] parameterizedGenericMethodBindingArray = cachedInfo;
            cachedInfo = new ParameterizedGenericMethodBinding[length * 2];
            System.arraycopy(parameterizedGenericMethodBindingArray, 0, cachedInfo, 0, length);
            this.uniqueParameterizedGenericMethodBindings.put(genericMethod, cachedInfo);
        }
        cachedInfo[index] = parameterizedGenericMethod = new ParameterizedGenericMethodBinding(genericMethod, rawType, this);
        return parameterizedGenericMethod;
    }

    public ParameterizedGenericMethodBinding createParameterizedGenericMethod(MethodBinding genericMethod, TypeBinding[] typeArguments) {
        return this.createParameterizedGenericMethod(genericMethod, typeArguments, null);
    }

    public ParameterizedGenericMethodBinding createParameterizedGenericMethod(MethodBinding genericMethod, TypeBinding[] typeArguments, TypeBinding targetType) {
        return this.createParameterizedGenericMethod(genericMethod, typeArguments, false, false, targetType);
    }

    public ParameterizedGenericMethodBinding createParameterizedGenericMethod(MethodBinding genericMethod, TypeBinding[] typeArguments, boolean inferredWithUncheckedConversion, boolean hasReturnProblem, TypeBinding targetType) {
        ParameterizedGenericMethodBinding parameterizedGenericMethod;
        ParameterizedGenericMethodBinding[] cachedInfo = (ParameterizedGenericMethodBinding[])this.uniqueParameterizedGenericMethodBindings.get(genericMethod);
        int argLength = typeArguments == null ? 0 : typeArguments.length;
        boolean needToGrow = false;
        int index = 0;
        if (cachedInfo != null) {
            int max = cachedInfo.length;
            while (index < max) {
                block13: {
                    ParameterizedGenericMethodBinding cachedMethod = cachedInfo[index];
                    if (cachedMethod == null) break;
                    if (!cachedMethod.isRaw && cachedMethod.targetType == targetType && cachedMethod.inferredWithUncheckedConversion == inferredWithUncheckedConversion) {
                        int cachedArgLength;
                        TypeBinding[] cachedArguments = cachedMethod.typeArguments;
                        int n = cachedArgLength = cachedArguments == null ? 0 : cachedArguments.length;
                        if (argLength == cachedArgLength) {
                            int j = 0;
                            while (j < cachedArgLength) {
                                if (typeArguments[j] == cachedArguments[j]) {
                                    ++j;
                                    continue;
                                }
                                break block13;
                            }
                            if (inferredWithUncheckedConversion) {
                                if (!cachedMethod.returnType.isParameterizedType() && !cachedMethod.returnType.isTypeVariable()) {
                                    ReferenceBinding[] referenceBindingArray = cachedMethod.thrownExceptions;
                                    int n2 = cachedMethod.thrownExceptions.length;
                                    int n3 = 0;
                                    while (n3 < n2) {
                                        ReferenceBinding exc = referenceBindingArray[n3];
                                        if (!exc.isParameterizedType() && !exc.isTypeVariable()) {
                                            ++n3;
                                            continue;
                                        }
                                        break;
                                    }
                                }
                            } else {
                                return cachedMethod;
                            }
                        }
                    }
                }
                ++index;
            }
            needToGrow = true;
        } else {
            cachedInfo = new ParameterizedGenericMethodBinding[5];
            this.uniqueParameterizedGenericMethodBindings.put(genericMethod, cachedInfo);
        }
        int length = cachedInfo.length;
        if (needToGrow && index == length) {
            ParameterizedGenericMethodBinding[] parameterizedGenericMethodBindingArray = cachedInfo;
            cachedInfo = new ParameterizedGenericMethodBinding[length * 2];
            System.arraycopy(parameterizedGenericMethodBindingArray, 0, cachedInfo, 0, length);
            this.uniqueParameterizedGenericMethodBindings.put(genericMethod, cachedInfo);
        }
        cachedInfo[index] = parameterizedGenericMethod = new ParameterizedGenericMethodBinding(genericMethod, typeArguments, this, inferredWithUncheckedConversion, hasReturnProblem, targetType);
        return parameterizedGenericMethod;
    }

    public PolymorphicMethodBinding createPolymorphicMethod(MethodBinding originalPolymorphicMethod, TypeBinding[] parameters, Scope scope) {
        PolymorphicMethodBinding polymorphicMethod;
        String key = new String(originalPolymorphicMethod.selector);
        PolymorphicMethodBinding[] cachedInfo = (PolymorphicMethodBinding[])this.uniquePolymorphicMethodBindings.get(key);
        int parametersLength = parameters == null ? 0 : parameters.length;
        TypeBinding[] parametersTypeBinding = new TypeBinding[parametersLength];
        int i = 0;
        while (i < parametersLength) {
            TypeBinding parameterTypeBinding = parameters[i];
            if (parameterTypeBinding.id == 12) {
                parametersTypeBinding[i] = this.getType(JAVA_LANG_VOID, this.javaBaseModule());
            } else if (parameterTypeBinding.isPolyType()) {
                PolyTypeBinding ptb = (PolyTypeBinding)parameterTypeBinding;
                if (scope instanceof BlockScope && ptb.expression.resolvedType == null) {
                    ptb.expression.setExpectedType(scope.getJavaLangObject());
                    parametersTypeBinding[i] = ptb.expression.resolveType((BlockScope)scope);
                } else {
                    parametersTypeBinding[i] = ptb.expression.resolvedType;
                }
            } else {
                parametersTypeBinding[i] = parameterTypeBinding.erasure();
            }
            ++i;
        }
        boolean needToGrow = false;
        int index = 0;
        if (cachedInfo != null) {
            int max = cachedInfo.length;
            while (index < max) {
                PolymorphicMethodBinding cachedMethod = cachedInfo[index];
                if (cachedMethod == null) break;
                if (cachedMethod.matches(parametersTypeBinding, originalPolymorphicMethod.returnType)) {
                    return cachedMethod;
                }
                ++index;
            }
            needToGrow = true;
        } else {
            cachedInfo = new PolymorphicMethodBinding[5];
            this.uniquePolymorphicMethodBindings.put(key, cachedInfo);
        }
        int length = cachedInfo.length;
        if (needToGrow && index == length) {
            PolymorphicMethodBinding[] polymorphicMethodBindingArray = cachedInfo;
            cachedInfo = new PolymorphicMethodBinding[length * 2];
            System.arraycopy(polymorphicMethodBindingArray, 0, cachedInfo, 0, length);
            this.uniquePolymorphicMethodBindings.put(key, cachedInfo);
        }
        cachedInfo[index] = polymorphicMethod = new PolymorphicMethodBinding(originalPolymorphicMethod, parametersTypeBinding);
        return polymorphicMethod;
    }

    public boolean usesAnnotatedTypeSystem() {
        return this.typeSystem.isAnnotatedTypeSystem();
    }

    public MethodBinding updatePolymorphicMethodReturnType(PolymorphicMethodBinding binding, TypeBinding typeBinding) {
        PolymorphicMethodBinding polymorphicMethod;
        String key = new String(binding.selector);
        PolymorphicMethodBinding[] cachedInfo = (PolymorphicMethodBinding[])this.uniquePolymorphicMethodBindings.get(key);
        boolean needToGrow = false;
        int index = 0;
        TypeBinding[] parameters = binding.parameters;
        if (cachedInfo != null) {
            int max = cachedInfo.length;
            while (index < max) {
                PolymorphicMethodBinding cachedMethod = cachedInfo[index];
                if (cachedMethod == null) break;
                if (cachedMethod.matches(parameters, typeBinding)) {
                    return cachedMethod;
                }
                ++index;
            }
            needToGrow = true;
        } else {
            cachedInfo = new PolymorphicMethodBinding[5];
            this.uniquePolymorphicMethodBindings.put(key, cachedInfo);
        }
        int length = cachedInfo.length;
        if (needToGrow && index == length) {
            PolymorphicMethodBinding[] polymorphicMethodBindingArray = cachedInfo;
            cachedInfo = new PolymorphicMethodBinding[length * 2];
            System.arraycopy(polymorphicMethodBindingArray, 0, cachedInfo, 0, length);
            this.uniquePolymorphicMethodBindings.put(key, cachedInfo);
        }
        cachedInfo[index] = polymorphicMethod = new PolymorphicMethodBinding(binding.original(), typeBinding, parameters);
        return polymorphicMethod;
    }

    public ParameterizedMethodBinding createGetClassMethod(TypeBinding receiverType, MethodBinding originalMethod, Scope scope) {
        ParameterizedMethodBinding retVal = null;
        if (this.uniqueGetClassMethodBinding == null) {
            this.uniqueGetClassMethodBinding = new SimpleLookupTable(3);
        } else {
            retVal = (ParameterizedMethodBinding)this.uniqueGetClassMethodBinding.get(receiverType);
        }
        if (retVal == null) {
            retVal = ParameterizedMethodBinding.instantiateGetClass(receiverType, originalMethod, scope);
            this.uniqueGetClassMethodBinding.put(receiverType, retVal);
        }
        return retVal;
    }

    public ReferenceBinding createMemberType(ReferenceBinding memberType, ReferenceBinding enclosingType) {
        return this.typeSystem.getMemberType(memberType, enclosingType);
    }

    public ParameterizedTypeBinding createParameterizedType(ReferenceBinding genericType, TypeBinding[] typeArguments, ReferenceBinding enclosingType) {
        AnnotationBinding[] annotations = genericType.typeAnnotations;
        if (annotations != Binding.NO_ANNOTATIONS) {
            return this.typeSystem.getParameterizedType((ReferenceBinding)genericType.unannotated(), typeArguments, enclosingType, annotations);
        }
        return this.typeSystem.getParameterizedType(genericType, typeArguments, enclosingType);
    }

    public ParameterizedTypeBinding createParameterizedType(ReferenceBinding genericType, TypeBinding[] typeArguments, ReferenceBinding enclosingType, AnnotationBinding[] annotations) {
        return this.typeSystem.getParameterizedType(genericType, typeArguments, enclosingType, annotations);
    }

    public ReferenceBinding maybeCreateParameterizedType(ReferenceBinding nonGenericType, ReferenceBinding enclosingType) {
        boolean canSeeEnclosingTypeParameters;
        boolean bl = canSeeEnclosingTypeParameters = enclosingType != null && enclosingType.isParameterizedType() | enclosingType.isRawType() && !nonGenericType.isStatic();
        if (canSeeEnclosingTypeParameters) {
            return this.createParameterizedType(nonGenericType, null, enclosingType);
        }
        return nonGenericType;
    }

    public TypeBinding createAnnotatedType(TypeBinding type, AnnotationBinding[][] annotations) {
        return this.typeSystem.getAnnotatedType(type, annotations);
    }

    public TypeBinding createAnnotatedType(TypeBinding type, AnnotationBinding[] newbies) {
        int oldLength;
        int newLength;
        int n = newLength = newbies == null ? 0 : newbies.length;
        if (type == null || newLength == 0) {
            return type;
        }
        AnnotationBinding[] oldies = type.getTypeAnnotations();
        int n2 = oldLength = oldies == null ? 0 : oldies.length;
        if (oldLength > 0) {
            AnnotationBinding[] annotationBindingArray = newbies;
            newbies = new AnnotationBinding[newLength + oldLength];
            System.arraycopy(annotationBindingArray, 0, newbies, 0, newLength);
            System.arraycopy(oldies, 0, newbies, newLength, oldLength);
        }
        if (this.globalOptions.isAnnotationBasedNullAnalysisEnabled) {
            long tagBitsSeen = 0L;
            AnnotationBinding[] filtered = new AnnotationBinding[newbies.length];
            int count = 0;
            int i = 0;
            while (i < newbies.length) {
                if (newbies[i] == null) {
                    filtered[count++] = null;
                    tagBitsSeen = 0L;
                } else {
                    long tagBits = 0L;
                    if (newbies[i].type.hasNullBit(32)) {
                        tagBits = 0x100000000000000L;
                    } else if (newbies[i].type.hasNullBit(64)) {
                        tagBits = 0x80000000000000L;
                    }
                    if ((tagBitsSeen & tagBits) == 0L) {
                        tagBitsSeen |= tagBits;
                        filtered[count++] = newbies[i];
                    }
                }
                ++i;
            }
            if (count < newbies.length) {
                newbies = new AnnotationBinding[count];
                System.arraycopy(filtered, 0, newbies, 0, count);
            }
        }
        return this.typeSystem.getAnnotatedType(type, new AnnotationBinding[][]{newbies});
    }

    public RawTypeBinding createRawType(ReferenceBinding genericType, ReferenceBinding enclosingType) {
        AnnotationBinding[] annotations = genericType.typeAnnotations;
        if (annotations != Binding.NO_ANNOTATIONS) {
            return this.typeSystem.getRawType((ReferenceBinding)genericType.unannotated(), enclosingType, annotations);
        }
        return this.typeSystem.getRawType(genericType, enclosingType);
    }

    public RawTypeBinding createRawType(ReferenceBinding genericType, ReferenceBinding enclosingType, AnnotationBinding[] annotations) {
        return this.typeSystem.getRawType(genericType, enclosingType, annotations);
    }

    public WildcardBinding createWildcard(ReferenceBinding genericType, int rank, TypeBinding bound, TypeBinding[] otherBounds, int boundKind) {
        AnnotationBinding[] annotations;
        if (genericType != null && (annotations = genericType.typeAnnotations) != Binding.NO_ANNOTATIONS) {
            return this.typeSystem.getWildcard((ReferenceBinding)genericType.unannotated(), rank, bound, otherBounds, boundKind, annotations);
        }
        return this.typeSystem.getWildcard(genericType, rank, bound, otherBounds, boundKind);
    }

    public CaptureBinding createCapturedWildcard(WildcardBinding wildcard, ReferenceBinding contextType, int start, int end, ASTNode cud, int id) {
        return this.typeSystem.getCapturedWildcard(wildcard, contextType, start, end, cud, id);
    }

    public WildcardBinding createWildcard(ReferenceBinding genericType, int rank, TypeBinding bound, TypeBinding[] otherBounds, int boundKind, AnnotationBinding[] annotations) {
        return this.typeSystem.getWildcard(genericType, rank, bound, otherBounds, boundKind, annotations);
    }

    public AccessRestriction getAccessRestriction(TypeBinding type) {
        return (AccessRestriction)this.accessRestrictions.get(type);
    }

    public ReferenceBinding getCachedType(char[][] compoundName) {
        ReferenceBinding result = this.getCachedType0(compoundName);
        if (result == null && this.useModuleSystem) {
            ModuleBinding[] modulesToSearch;
            ModuleBinding[] moduleBindingArray = modulesToSearch = this.module.isUnnamed() || this.module.isAuto ? this.root.knownModules.valueTable : this.module.getAllRequiredModules();
            int n = modulesToSearch.length;
            int n2 = 0;
            while (n2 < n) {
                ModuleBinding someModule = moduleBindingArray[n2];
                if (someModule != null && (result = someModule.environment.getCachedType0(compoundName)) != null && result.isValidBinding()) break;
                ++n2;
            }
        }
        return result;
    }

    private boolean flaggedJavaBaseTypeErrors(ReferenceBinding result, char[][] compoundName) {
        ReferenceBinding type;
        assert (result != null && !result.isValidBinding());
        if (CharOperation.equals(TypeConstants.JAVA, compoundName[0]) && (type = this.getType(compoundName, this.javaBaseModule())) != null && type.isValidBinding()) {
            char[] readableName;
            PackageBinding pack = type.getPackage();
            char[] cArray = readableName = pack != null ? pack.readableName() : null;
            if (readableName != null && (CharOperation.equals(readableName, TypeConstants.JAVA) || CharOperation.equals(readableName, CharOperation.concatWith(TypeConstants.JAVA_LANG, '.')) || CharOperation.equals(readableName, CharOperation.concatWith(TypeConstants.JAVA_IO, '.')))) {
                ModuleBinding visibleModule;
                PackageBinding currentPack = this.getTopLevelPackage(readableName);
                ModuleBinding moduleBinding = visibleModule = currentPack != null ? currentPack.enclosingModule : null;
                if (visibleModule != null && visibleModule != this.javaBaseModule() && !this.globalOptions.enableJdtDebugCompileMode) {
                    this.problemReporter.conflictingPackageInModules(compoundName, this.root.unitBeingCompleted, this.missingClassFileLocation, readableName, TypeConstants.JAVA_BASE, visibleModule.readableName());
                    return true;
                }
            }
        }
        return false;
    }

    public ReferenceBinding getCachedType0(char[][] compoundName) {
        if (compoundName.length == 1) {
            return this.defaultPackage.getType0(compoundName[0]);
        }
        PackageBinding packageBinding = this.getPackage0(compoundName[0]);
        if (packageBinding == null || packageBinding == TheNotFoundPackage) {
            return null;
        }
        if ((packageBinding = packageBinding.getIncarnation(this.module)) == null || packageBinding == TheNotFoundPackage) {
            return null;
        }
        int i = 1;
        int packageLength = compoundName.length - 1;
        while (i < packageLength) {
            if ((packageBinding = packageBinding.getPackage0Any(compoundName[i])) == null || packageBinding == TheNotFoundPackage) {
                return null;
            }
            ++i;
        }
        return packageBinding.getType0(compoundName[compoundName.length - 1]);
    }

    public AnnotationBinding getNullableAnnotation() {
        if (this.nullableAnnotation != null) {
            return this.nullableAnnotation;
        }
        if (this.root != this) {
            this.nullableAnnotation = this.root.getNullableAnnotation();
            return this.nullableAnnotation;
        }
        ReferenceBinding nullable = this.getResolvedType(this.globalOptions.nullableAnnotationName, null);
        this.nullableAnnotation = this.typeSystem.getAnnotationType(nullable, true);
        return this.nullableAnnotation;
    }

    public char[][] getNullableAnnotationName() {
        return this.globalOptions.nullableAnnotationName;
    }

    public AnnotationBinding getNonNullAnnotation() {
        if (this.nonNullAnnotation != null) {
            return this.nonNullAnnotation;
        }
        if (this.root != this) {
            this.nonNullAnnotation = this.root.getNonNullAnnotation();
            return this.nonNullAnnotation;
        }
        ReferenceBinding nonNull = this.getResolvedType(this.globalOptions.nonNullAnnotationName, this.UnNamedModule, null, true);
        this.nonNullAnnotation = this.typeSystem.getAnnotationType(nonNull, true);
        return this.nonNullAnnotation;
    }

    public AnnotationBinding[] nullAnnotationsFromTagBits(long nullTagBits) {
        if (nullTagBits == 0x100000000000000L) {
            return new AnnotationBinding[]{this.getNonNullAnnotation()};
        }
        if (nullTagBits == 0x80000000000000L) {
            return new AnnotationBinding[]{this.getNullableAnnotation()};
        }
        return null;
    }

    public char[][] getNonNullAnnotationName() {
        return this.globalOptions.nonNullAnnotationName;
    }

    public char[][] getNonNullByDefaultAnnotationName() {
        return this.globalOptions.nonNullByDefaultAnnotationName;
    }

    int getNullAnnotationBit(char[][] qualifiedTypeName) {
        String qualifiedTypeString;
        Integer typeBit;
        if (this.allNullAnnotations == null) {
            String name;
            this.allNullAnnotations = new HashMap<String, Integer>();
            this.allNullAnnotations.put(CharOperation.toString(this.globalOptions.nonNullAnnotationName), 32);
            this.allNullAnnotations.put(CharOperation.toString(this.globalOptions.nullableAnnotationName), 64);
            this.allNullAnnotations.put(CharOperation.toString(this.globalOptions.nonNullByDefaultAnnotationName), 128);
            String[] stringArray = this.globalOptions.nullableAnnotationSecondaryNames;
            int n = this.globalOptions.nullableAnnotationSecondaryNames.length;
            int n2 = 0;
            while (n2 < n) {
                name = stringArray[n2];
                this.allNullAnnotations.put(name, 64);
                ++n2;
            }
            stringArray = this.globalOptions.nonNullAnnotationSecondaryNames;
            n = this.globalOptions.nonNullAnnotationSecondaryNames.length;
            n2 = 0;
            while (n2 < n) {
                name = stringArray[n2];
                this.allNullAnnotations.put(name, 32);
                ++n2;
            }
            stringArray = this.globalOptions.nonNullByDefaultAnnotationSecondaryNames;
            n = this.globalOptions.nonNullByDefaultAnnotationSecondaryNames.length;
            n2 = 0;
            while (n2 < n) {
                name = stringArray[n2];
                this.allNullAnnotations.put(name, 128);
                ++n2;
            }
        }
        return (typeBit = this.allNullAnnotations.get(qualifiedTypeString = CharOperation.toString(qualifiedTypeName))) == null ? 0 : typeBit;
    }

    public boolean isNullnessAnnotationPackage(PackageBinding pkg) {
        return this.nonnullAnnotationPackage == pkg || this.nullableAnnotationPackage == pkg || this.nonnullByDefaultAnnotationPackage == pkg;
    }

    public boolean usesNullTypeAnnotations() {
        if (this.root != this) {
            return this.root.usesNullTypeAnnotations();
        }
        if (this.globalOptions.useNullTypeAnnotations != null) {
            return this.globalOptions.useNullTypeAnnotations;
        }
        this.initializeUsesNullTypeAnnotation();
        for (MethodBinding enumMethod : this.deferredEnumMethods) {
            int purpose = 0;
            if (CharOperation.equals(enumMethod.selector, TypeConstants.VALUEOF)) {
                purpose = 10;
            } else if (CharOperation.equals(enumMethod.selector, TypeConstants.VALUES)) {
                purpose = 9;
            }
            if (purpose == 0) continue;
            SyntheticMethodBinding.markNonNull(enumMethod, purpose, this);
        }
        this.deferredEnumMethods.clear();
        return this.globalOptions.useNullTypeAnnotations;
    }

    private void initializeUsesNullTypeAnnotation() {
        long nonNullMetaBits;
        ReferenceBinding nonNull;
        ReferenceBinding nullable;
        this.globalOptions.useNullTypeAnnotations = Boolean.FALSE;
        if (!this.globalOptions.isAnnotationBasedNullAnalysisEnabled || this.globalOptions.originalSourceLevel < 0x340000L) {
            return;
        }
        boolean origMayTolerateMissingType = this.mayTolerateMissingType;
        this.mayTolerateMissingType = true;
        try {
            nullable = this.nullableAnnotation != null ? this.nullableAnnotation.getAnnotationType() : this.getType(this.getNullableAnnotationName(), this.UnNamedModule);
            nonNull = this.nonNullAnnotation != null ? this.nonNullAnnotation.getAnnotationType() : this.getType(this.getNonNullAnnotationName(), this.UnNamedModule);
        }
        finally {
            this.mayTolerateMissingType = origMayTolerateMissingType;
        }
        if (nullable == null && nonNull == null) {
            return;
        }
        if (nullable == null || nonNull == null) {
            return;
        }
        long nullableMetaBits = nullable.getAnnotationTagBits() & 0x20000000000000L;
        if (nullableMetaBits != (nonNullMetaBits = nonNull.getAnnotationTagBits() & 0x20000000000000L)) {
            return;
        }
        if (nullableMetaBits == 0L) {
            return;
        }
        this.globalOptions.useNullTypeAnnotations = Boolean.TRUE;
    }

    PackageBinding getPackage0(char[] name) {
        return this.knownPackages.get(name);
    }

    public ReferenceBinding getResolvedType(char[][] compoundName, Scope scope) {
        return this.getResolvedType(compoundName, scope == null ? this.UnNamedModule : scope.module(), scope, false);
    }

    public ReferenceBinding getResolvedType(char[][] compoundName, ModuleBinding moduleBinding, Scope scope, boolean implicitAnnotationUse) {
        if (this.module != moduleBinding) {
            return moduleBinding.environment.getResolvedType(compoundName, moduleBinding, scope, implicitAnnotationUse);
        }
        ReferenceBinding type = this.getType(compoundName, moduleBinding);
        if (type != null) {
            return type;
        }
        this.problemReporter.isClassPathCorrect(compoundName, scope == null ? this.root.unitBeingCompleted : scope.referenceCompilationUnit(), this.missingClassFileLocation, implicitAnnotationUse);
        return this.createMissingType(null, compoundName);
    }

    public ReferenceBinding getResolvedJavaBaseType(char[][] compoundName, Scope scope) {
        return this.getResolvedType(compoundName, this.javaBaseModule(), scope, false);
    }

    PackageBinding getTopLevelPackage(char[] name) {
        if (this.useModuleSystem) {
            return this.module.getTopLevelPackage(name);
        }
        PackageBinding packageBinding = this.getPackage0(name);
        if (packageBinding != null) {
            if (packageBinding == TheNotFoundPackage) {
                return null;
            }
            return packageBinding;
        }
        if (this.nameEnvironment.isPackage(null, name)) {
            packageBinding = this.module.createDeclaredToplevelPackage(name);
            this.knownPackages.put(name, packageBinding);
            return packageBinding;
        }
        this.knownPackages.put(name, TheNotFoundPackage);
        return null;
    }

    public ReferenceBinding getType(char[][] compoundName) {
        return this.getType(compoundName, this.UnNamedModule);
    }

    public ReferenceBinding getType(char[][] compoundName, ModuleBinding mod) {
        ReferenceBinding referenceBinding;
        if (compoundName.length == 1) {
            referenceBinding = this.defaultPackage.getType0(compoundName[0]);
            if (referenceBinding == null) {
                PackageBinding packageBinding = this.getPackage0(compoundName[0]);
                if (packageBinding != null && packageBinding != TheNotFoundPackage) {
                    return null;
                }
                referenceBinding = this.askForType(this.defaultPackage, compoundName[0], mod);
            }
        } else {
            PackageBinding packageBinding = this.getPackage0(compoundName[0]);
            if (packageBinding == TheNotFoundPackage) {
                return null;
            }
            if (packageBinding != null) {
                int i = 1;
                int packageLength = compoundName.length - 1;
                while (i < packageLength) {
                    if ((packageBinding = packageBinding.getPackage0(compoundName[i])) == null) break;
                    if (packageBinding == TheNotFoundPackage) {
                        return null;
                    }
                    ++i;
                }
            }
            if (packageBinding == null) {
                referenceBinding = this.askForType(compoundName, mod);
            } else {
                referenceBinding = packageBinding.getType0(compoundName[compoundName.length - 1]);
                if (referenceBinding == null) {
                    referenceBinding = this.askForType(packageBinding, compoundName[compoundName.length - 1], mod);
                }
            }
        }
        if (referenceBinding == null || referenceBinding == TheNotFoundType) {
            return null;
        }
        referenceBinding = (ReferenceBinding)BinaryTypeBinding.resolveType(referenceBinding, this, false);
        return referenceBinding;
    }

    private TypeBinding[] getTypeArgumentsFromSignature(SignatureWrapper wrapper, TypeVariableBinding[] staticVariables, ReferenceBinding enclosingType, ReferenceBinding genericType, char[][][] missingTypeNames, ITypeAnnotationWalker walker) {
        ArrayList<TypeBinding> args = new ArrayList<TypeBinding>(2);
        int rank = 0;
        do {
            args.add(this.getTypeFromVariantTypeSignature(wrapper, staticVariables, enclosingType, genericType, rank, missingTypeNames, walker.toTypeArgument(rank++)));
        } while (wrapper.signature[wrapper.start] != '>');
        ++wrapper.start;
        TypeBinding[] typeArguments = new TypeBinding[args.size()];
        args.toArray(typeArguments);
        return typeArguments;
    }

    private ReferenceBinding getTypeFromCompoundName(char[][] compoundName, boolean isParameterized, boolean wasMissingType) {
        ReferenceBinding binding = this.getCachedType(compoundName);
        if (binding == null) {
            PackageBinding packageBinding = this.computePackageFrom(compoundName, false);
            if (this.useModuleSystem) {
                binding = packageBinding.getType0(compoundName[compoundName.length - 1]);
            }
            if (binding == null) {
                binding = new UnresolvedReferenceBinding(compoundName, packageBinding);
                if (wasMissingType) {
                    binding.tagBits |= 0x80L;
                }
                packageBinding.addType(binding);
            }
        }
        if (binding == TheNotFoundType) {
            if (!wasMissingType && !this.flaggedJavaBaseTypeErrors(binding, compoundName)) {
                this.problemReporter.isClassPathCorrect(compoundName, this.root.unitBeingCompleted, this.missingClassFileLocation, false);
            }
            binding = this.createMissingType(null, compoundName);
        } else if (!isParameterized) {
            binding = (ReferenceBinding)this.convertUnresolvedBinaryToRawType(binding);
        }
        return binding;
    }

    ReferenceBinding getTypeFromConstantPoolName(char[] signature, int start, int end, boolean isParameterized, char[][][] missingTypeNames, ITypeAnnotationWalker walker) {
        if (end == -1) {
            end = signature.length;
        }
        char[][] compoundName = CharOperation.splitOn('/', signature, start, end);
        boolean wasMissingType = false;
        if (missingTypeNames != null) {
            int i = 0;
            int max = missingTypeNames.length;
            while (i < max) {
                if (CharOperation.equals(compoundName, missingTypeNames[i])) {
                    wasMissingType = true;
                    break;
                }
                ++i;
            }
        }
        ReferenceBinding binding = this.getTypeFromCompoundName(compoundName, isParameterized, wasMissingType);
        if (walker != ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER) {
            binding = (ReferenceBinding)this.annotateType(binding, walker, missingTypeNames);
        }
        return binding;
    }

    ReferenceBinding getTypeFromConstantPoolName(char[] signature, int start, int end, boolean isParameterized, char[][][] missingTypeNames) {
        return this.getTypeFromConstantPoolName(signature, start, end, isParameterized, missingTypeNames, ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER);
    }

    TypeBinding getTypeFromSignature(char[] signature, int start, int end, boolean isParameterized, TypeBinding enclosingType, char[][][] missingTypeNames, ITypeAnnotationWalker walker) {
        int dimension = 0;
        while (signature[start] == '[') {
            ++start;
            ++dimension;
        }
        AnnotationBinding[][] annotationsOnDimensions = null;
        if (dimension > 0 && walker != ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER) {
            int i = 0;
            while (i < dimension) {
                AnnotationBinding[] annotations = BinaryTypeBinding.createAnnotations(walker.getAnnotationsAtCursor(0, true), this, missingTypeNames);
                if (annotations != Binding.NO_ANNOTATIONS) {
                    if (annotationsOnDimensions == null) {
                        annotationsOnDimensions = new AnnotationBinding[dimension][];
                    }
                    annotationsOnDimensions[i] = annotations;
                }
                walker = walker.toNextArrayDimension();
                ++i;
            }
        }
        if (end == -1) {
            end = signature.length - 1;
        }
        TypeBinding binding = null;
        if (start == end) {
            switch (signature[start]) {
                case 'I': {
                    binding = TypeBinding.INT;
                    break;
                }
                case 'Z': {
                    binding = TypeBinding.BOOLEAN;
                    break;
                }
                case 'V': {
                    binding = TypeBinding.VOID;
                    break;
                }
                case 'C': {
                    binding = TypeBinding.CHAR;
                    break;
                }
                case 'D': {
                    binding = TypeBinding.DOUBLE;
                    break;
                }
                case 'B': {
                    binding = TypeBinding.BYTE;
                    break;
                }
                case 'F': {
                    binding = TypeBinding.FLOAT;
                    break;
                }
                case 'J': {
                    binding = TypeBinding.LONG;
                    break;
                }
                case 'S': {
                    binding = TypeBinding.SHORT;
                    break;
                }
                default: {
                    this.problemReporter.corruptedSignature(enclosingType, signature, start);
                    break;
                }
            }
        } else {
            binding = this.getTypeFromConstantPoolName(signature, start + 1, end, isParameterized, missingTypeNames);
        }
        if (isParameterized) {
            if (dimension != 0) {
                throw new IllegalStateException();
            }
            return binding;
        }
        if (walker != ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER) {
            binding = this.annotateType(binding, walker, missingTypeNames);
        }
        if (dimension != 0) {
            binding = this.typeSystem.getArrayType(binding, dimension, AnnotatableTypeSystem.flattenedAnnotations(annotationsOnDimensions));
        }
        return binding;
    }

    private TypeBinding annotateType(TypeBinding binding, ITypeAnnotationWalker walker, char[][][] missingTypeNames) {
        if (walker == ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER) {
            return binding;
        }
        int depth = binding.depth() + 1;
        if (depth > 1) {
            if (binding.isUnresolvedType()) {
                binding = ((UnresolvedReferenceBinding)binding).resolve(this, true);
            }
            depth = this.countNonStaticNestingLevels(binding) + 1;
        }
        AnnotationBinding[][] annotations = null;
        int i = 0;
        while (i < depth) {
            AnnotationBinding[] annots = BinaryTypeBinding.createAnnotations(walker.getAnnotationsAtCursor(binding.id, i == depth - 1), this, missingTypeNames);
            if (annots != null && annots.length > 0) {
                if (annotations == null) {
                    annotations = new AnnotationBinding[depth][];
                }
                annotations[i] = annots;
            }
            walker = walker.toNextNestedType();
            ++i;
        }
        if (annotations != null) {
            binding = this.createAnnotatedType(binding, annotations);
        }
        return binding;
    }

    private int countNonStaticNestingLevels(TypeBinding binding) {
        if (binding.isUnresolvedType()) {
            throw new IllegalStateException();
        }
        int depth = -1;
        TypeBinding currentBinding = binding;
        while (currentBinding != null) {
            ++depth;
            if (currentBinding.isStatic()) break;
            currentBinding = currentBinding.enclosingType();
        }
        return depth;
    }

    boolean qualifiedNameMatchesSignature(char[][] name, char[] signature) {
        int s = 1;
        int i = 0;
        while (i < name.length) {
            char[] n = name[i];
            int j = 0;
            while (j < n.length) {
                if (n[j] != signature[s++]) {
                    return false;
                }
                ++j;
            }
            if (signature[s] == ';' && i == name.length - 1) {
                return true;
            }
            if (signature[s++] != '/') {
                return false;
            }
            ++i;
        }
        return false;
    }

    public TypeBinding getTypeFromTypeSignature(SignatureWrapper wrapper, TypeVariableBinding[] staticVariables, ReferenceBinding enclosingType, char[][][] missingTypeNames, ITypeAnnotationWalker walker) {
        int dimension = 0;
        while (wrapper.signature[wrapper.start] == '[') {
            ++wrapper.start;
            ++dimension;
        }
        AnnotationBinding[][] annotationsOnDimensions = null;
        if (dimension > 0 && walker != ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER) {
            int i = 0;
            while (i < dimension) {
                AnnotationBinding[] annotations = BinaryTypeBinding.createAnnotations(walker.getAnnotationsAtCursor(0, true), this, missingTypeNames);
                if (annotations != Binding.NO_ANNOTATIONS) {
                    if (annotationsOnDimensions == null) {
                        annotationsOnDimensions = new AnnotationBinding[dimension][];
                    }
                    annotationsOnDimensions[i] = annotations;
                }
                walker = walker.toNextArrayDimension();
                ++i;
            }
        }
        if (wrapper.signature[wrapper.start] == 'T') {
            int varStart = wrapper.start + 1;
            int varEnd = wrapper.computeEnd();
            int i = staticVariables.length;
            while (--i >= 0) {
                if (!CharOperation.equals(staticVariables[i].sourceName, wrapper.signature, varStart, varEnd)) continue;
                return this.getTypeFromTypeVariable(staticVariables[i], dimension, annotationsOnDimensions, walker, missingTypeNames);
            }
            ReferenceBinding initialType = enclosingType;
            do {
                TypeVariableBinding[] enclosingTypeVariables = enclosingType instanceof BinaryTypeBinding ? ((BinaryTypeBinding)enclosingType).typeVariables : enclosingType.typeVariables();
                int i2 = enclosingTypeVariables.length;
                while (--i2 >= 0) {
                    if (!CharOperation.equals(enclosingTypeVariables[i2].sourceName, wrapper.signature, varStart, varEnd)) continue;
                    return this.getTypeFromTypeVariable(enclosingTypeVariables[i2], dimension, annotationsOnDimensions, walker, missingTypeNames);
                }
            } while ((enclosingType = enclosingType.enclosingType()) != null);
            this.problemReporter.undefinedTypeVariableSignature(CharOperation.subarray(wrapper.signature, varStart, varEnd), initialType);
            return null;
        }
        boolean isParameterized = wrapper.end == wrapper.bracket;
        TypeBinding type = this.getTypeFromSignature(wrapper.signature, wrapper.start, wrapper.computeEnd(), isParameterized, enclosingType, missingTypeNames, walker);
        if (!isParameterized) {
            return dimension == 0 ? type : this.createArrayType(type, dimension, AnnotatableTypeSystem.flattenedAnnotations(annotationsOnDimensions));
        }
        ReferenceBinding actualType = (ReferenceBinding)type;
        if (walker != ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER && actualType instanceof UnresolvedReferenceBinding && actualType.depth() > 0) {
            actualType = (ReferenceBinding)BinaryTypeBinding.resolveType(actualType, this, false);
        }
        ReferenceBinding actualEnclosing = actualType.enclosingType();
        ITypeAnnotationWalker savedWalker = walker;
        if (walker != ITypeAnnotationWalker.EMPTY_ANNOTATION_WALKER && actualType.depth() > 0) {
            int nonStaticNestingLevels = this.countNonStaticNestingLevels(actualType);
            int i = 0;
            while (i < nonStaticNestingLevels) {
                walker = walker.toNextNestedType();
                ++i;
            }
        }
        TypeBinding[] typeArguments = this.getTypeArgumentsFromSignature(wrapper, staticVariables, enclosingType, actualType, missingTypeNames, walker);
        ReferenceBinding currentType = this.createParameterizedType(actualType, typeArguments, actualEnclosing);
        ReferenceBinding plainCurrent = actualType;
        while (wrapper.signature[wrapper.start] == '.') {
            int memberStart = ++wrapper.start;
            char[] memberName = wrapper.nextWord();
            ReferenceBinding memberType = (plainCurrent = (ReferenceBinding)BinaryTypeBinding.resolveType(plainCurrent, this, false)).getMemberType(memberName);
            if (memberType == null) {
                this.problemReporter.corruptedSignature(currentType, wrapper.signature, memberStart);
            }
            walker = memberType.isStatic() ? savedWalker : walker.toNextNestedType();
            if (wrapper.signature[wrapper.start] == '<') {
                ++wrapper.start;
                typeArguments = this.getTypeArgumentsFromSignature(wrapper, staticVariables, enclosingType, memberType, missingTypeNames, walker);
            } else {
                typeArguments = null;
            }
            if (typeArguments != null || !memberType.isStatic() && ((TypeBinding)currentType).isParameterizedType()) {
                if (memberType.isStatic()) {
                    currentType = plainCurrent;
                }
                currentType = this.createParameterizedType(memberType, typeArguments, currentType);
            } else {
                currentType = memberType;
            }
            plainCurrent = memberType;
        }
        ++wrapper.start;
        TypeBinding annotatedType = this.annotateType(currentType, savedWalker, missingTypeNames);
        return dimension == 0 ? annotatedType : this.createArrayType(annotatedType, dimension, AnnotatableTypeSystem.flattenedAnnotations(annotationsOnDimensions));
    }

    private TypeBinding getTypeFromTypeVariable(TypeVariableBinding typeVariableBinding, int dimension, AnnotationBinding[][] annotationsOnDimensions, ITypeAnnotationWalker walker, char[][][] missingTypeNames) {
        AnnotationBinding[] annotations = BinaryTypeBinding.createAnnotations(walker.getAnnotationsAtCursor(-1, false), this, missingTypeNames);
        if (annotations != null && annotations != Binding.NO_ANNOTATIONS) {
            typeVariableBinding = (TypeVariableBinding)this.createAnnotatedType((TypeBinding)typeVariableBinding, new AnnotationBinding[][]{annotations});
        }
        if (dimension == 0) {
            return typeVariableBinding;
        }
        return this.typeSystem.getArrayType(typeVariableBinding, dimension, AnnotatableTypeSystem.flattenedAnnotations(annotationsOnDimensions));
    }

    TypeBinding getTypeFromVariantTypeSignature(SignatureWrapper wrapper, TypeVariableBinding[] staticVariables, ReferenceBinding enclosingType, ReferenceBinding genericType, int rank, char[][][] missingTypeNames, ITypeAnnotationWalker walker) {
        switch (wrapper.signature[wrapper.start]) {
            case '-': {
                ++wrapper.start;
                TypeBinding bound = this.getTypeFromTypeSignature(wrapper, staticVariables, enclosingType, missingTypeNames, walker.toWildcardBound());
                AnnotationBinding[] annotations = BinaryTypeBinding.createAnnotations(walker.getAnnotationsAtCursor(-1, false), this, missingTypeNames);
                return this.typeSystem.getWildcard(genericType, rank, bound, null, 2, annotations);
            }
            case '+': {
                ++wrapper.start;
                TypeBinding bound = this.getTypeFromTypeSignature(wrapper, staticVariables, enclosingType, missingTypeNames, walker.toWildcardBound());
                AnnotationBinding[] annotations = BinaryTypeBinding.createAnnotations(walker.getAnnotationsAtCursor(-1, false), this, missingTypeNames);
                return this.typeSystem.getWildcard(genericType, rank, bound, null, 1, annotations);
            }
            case '*': {
                ++wrapper.start;
                AnnotationBinding[] annotations = BinaryTypeBinding.createAnnotations(walker.getAnnotationsAtCursor(-1, false), this, missingTypeNames);
                return this.typeSystem.getWildcard(genericType, rank, null, null, 0, annotations);
            }
        }
        return this.getTypeFromTypeSignature(wrapper, staticVariables, enclosingType, missingTypeNames, walker);
    }

    boolean isMissingType(char[] typeName) {
        int i = this.missingTypes == null ? 0 : this.missingTypes.size();
        while (--i >= 0) {
            MissingTypeBinding missingType = (MissingTypeBinding)this.missingTypes.get(i);
            if (!CharOperation.equals(missingType.sourceName, typeName)) continue;
            return true;
        }
        return false;
    }

    public MethodVerifier methodVerifier() {
        if (this.verifier == null) {
            this.verifier = this.newMethodVerifier();
        }
        return this.verifier;
    }

    public MethodVerifier newMethodVerifier() {
        return new MethodVerifier15(this);
    }

    public void releaseClassFiles(ClassFile[] classFiles) {
        int i = 0;
        int fileCount = classFiles.length;
        while (i < fileCount) {
            this.classFilePool.release(classFiles[i]);
            ++i;
        }
    }

    public void reset() {
        if (this.root != this) {
            this.root.reset();
            return;
        }
        this.stepCompleted = 0;
        this.knownModules = new HashtableOfModule();
        this.module = this.UnNamedModule = new ModuleBinding.UnNamedModule(this);
        this.JavaBaseModule = null;
        this.defaultPackage = new PlainPackageBinding(this);
        this.defaultImports = null;
        this.knownPackages = new HashtableOfPackage();
        this.accessRestrictions = new HashMap(3);
        this.verifier = null;
        this.uniqueParameterizedGenericMethodBindings = new SimpleLookupTable(3);
        this.uniquePolymorphicMethodBindings = new SimpleLookupTable(3);
        this.uniqueGetClassMethodBinding = null;
        this.missingTypes = null;
        this.typesBeingConnected = new HashSet<SourceTypeBinding>();
        int i = this.units.length;
        while (--i >= 0) {
            this.units[i] = null;
        }
        this.lastUnitIndex = -1;
        this.lastCompletedUnitIndex = -1;
        this.unitBeingCompleted = null;
        this.classFilePool.reset();
        this.typeSystem.reset();
    }

    public void setAccessRestriction(ReferenceBinding type, AccessRestriction accessRestriction) {
        if (accessRestriction == null) {
            return;
        }
        type.modifiers |= 0x40000;
        this.accessRestrictions.put(type, accessRestriction);
    }

    void updateCaches(UnresolvedReferenceBinding unresolvedType, ReferenceBinding resolvedType) {
        this.typeSystem.updateCaches(unresolvedType, resolvedType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addResolutionListener(IQualifiedTypeResolutionListener resolutionListener) {
        LookupEnvironment lookupEnvironment = this.root;
        synchronized (lookupEnvironment) {
            int length = this.root.resolutionListeners.length;
            int i = 0;
            while (i < length) {
                if (this.root.resolutionListeners[i].equals(resolutionListener)) {
                    return;
                }
                ++i;
            }
            this.root.resolutionListeners = new IQualifiedTypeResolutionListener[length + 1];
            System.arraycopy(this.root.resolutionListeners, 0, this.root.resolutionListeners, 0, length);
            this.root.resolutionListeners[length] = resolutionListener;
        }
    }

    public TypeBinding getUnannotatedType(TypeBinding typeBinding) {
        return this.typeSystem.getUnannotatedType(typeBinding);
    }

    public TypeBinding[] getAnnotatedTypes(TypeBinding type) {
        return this.typeSystem.getAnnotatedTypes(type);
    }

    public AnnotationBinding[] filterNullTypeAnnotations(AnnotationBinding[] typeAnnotations) {
        if (typeAnnotations.length == 0) {
            return typeAnnotations;
        }
        AnnotationBinding[] filtered = new AnnotationBinding[typeAnnotations.length];
        int count = 0;
        int i = 0;
        while (i < typeAnnotations.length) {
            AnnotationBinding typeAnnotation = typeAnnotations[i];
            if (typeAnnotation == null) {
                ++count;
            } else if (!typeAnnotation.type.hasNullBit(96)) {
                filtered[count++] = typeAnnotation;
            }
            ++i;
        }
        if (count == 0) {
            return Binding.NO_ANNOTATIONS;
        }
        if (count == typeAnnotations.length) {
            return typeAnnotations;
        }
        AnnotationBinding[] annotationBindingArray = filtered;
        filtered = new AnnotationBinding[count];
        System.arraycopy(annotationBindingArray, 0, filtered, 0, count);
        return filtered;
    }

    public boolean containsNullTypeAnnotation(IBinaryAnnotation[] typeAnnotations) {
        if (typeAnnotations.length == 0) {
            return false;
        }
        int i = 0;
        while (i < typeAnnotations.length) {
            char[][] name;
            IBinaryAnnotation typeAnnotation = typeAnnotations[i];
            char[] typeName = typeAnnotation.getTypeName();
            if (typeName != null && typeName.length >= 3 && typeName[0] == 'L' && this.getNullAnnotationBit(name = CharOperation.splitOn('/', typeName, 1, typeName.length - 1)) != 0) {
                return true;
            }
            ++i;
        }
        return false;
    }

    public boolean containsNullTypeAnnotation(AnnotationBinding[] typeAnnotations) {
        if (typeAnnotations.length == 0) {
            return false;
        }
        int i = 0;
        while (i < typeAnnotations.length) {
            AnnotationBinding typeAnnotation = typeAnnotations[i];
            if (typeAnnotation.type.hasNullBit(96)) {
                return true;
            }
            ++i;
        }
        return false;
    }

    public Binding getInaccessibleBinding(char[][] compoundName, ModuleBinding clientModule) {
        if (this.root != this) {
            return this.root.getInaccessibleBinding(compoundName, clientModule);
        }
        if (this.nameEnvironment instanceof IModuleAwareNameEnvironment) {
            int length;
            IModuleAwareNameEnvironment moduleEnv = (IModuleAwareNameEnvironment)this.nameEnvironment;
            int j = length = compoundName.length;
            while (j > 0) {
                char[][] candidateName = CharOperation.subarray(compoundName, 0, j);
                char[][] moduleNames = moduleEnv.getModulesDeclaringPackage(candidateName, ModuleBinding.ANY);
                if (moduleNames != null) {
                    ReferenceBinding type;
                    PackageBinding inaccessiblePackage = null;
                    char[][] cArray = moduleNames;
                    int n = moduleNames.length;
                    int n2 = 0;
                    while (n2 < n) {
                        PackageBinding pack;
                        ModuleBinding mod;
                        char[] moduleName = cArray[n2];
                        if (moduleName != ModuleBinding.UNOBSERVABLE && (mod = this.getModule(moduleName)) != null && (pack = mod.getVisiblePackage(candidateName)) != null && pack.isValidBinding()) {
                            if (clientModule.canAccess(pack)) {
                                return null;
                            }
                            inaccessiblePackage = pack;
                        }
                        ++n2;
                    }
                    if (inaccessiblePackage == null) {
                        return null;
                    }
                    if (j < length && (type = inaccessiblePackage.getType(compoundName[j], inaccessiblePackage.enclosingModule)) instanceof ReferenceBinding && type.isValidBinding()) {
                        return new ProblemReferenceBinding(compoundName, type, 30);
                    }
                    return new ProblemPackageBinding(candidateName, 30, this);
                }
                --j;
            }
        }
        return null;
    }

    private static /* synthetic */ NameEnvironmentAnswer lambda$1(IModuleAwareNameEnvironment iModuleAwareNameEnvironment, PackageBinding packageBinding, char[] cArray, ModuleBinding mod) {
        return LookupEnvironment.fromSplitPackageOrOracle(iModuleAwareNameEnvironment, mod, packageBinding, cArray);
    }
}

