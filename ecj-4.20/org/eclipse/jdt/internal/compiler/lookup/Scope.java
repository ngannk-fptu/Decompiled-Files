/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.CaseStatement;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ExpressionContext;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.Invocation;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ModuleDeclaration;
import org.eclipse.jdt.internal.compiler.ast.NameReference;
import org.eclipse.jdt.internal.compiler.ast.ReferenceExpression;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.codegen.ConstantPool;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.CaptureBinding;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.ImportBinding;
import org.eclipse.jdt.internal.compiler.lookup.ImportConflictBinding;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import org.eclipse.jdt.internal.compiler.lookup.IntersectionTypeBinding18;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodVerifier;
import org.eclipse.jdt.internal.compiler.lookup.MissingTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.ModuleScope;
import org.eclipse.jdt.internal.compiler.lookup.MostSpecificExceptionMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedGenericMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.PlainPackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.PolyParameterizedGenericMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.PolyTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemFieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Substitution;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticFactoryMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.util.HashtableOfObject;
import org.eclipse.jdt.internal.compiler.util.ObjectVector;
import org.eclipse.jdt.internal.compiler.util.SimpleLookupTable;
import org.eclipse.jdt.internal.compiler.util.SimpleSet;

public abstract class Scope {
    public static Binding NOT_REDUNDANT = new Binding(){

        @Override
        public int kind() {
            throw new IllegalStateException();
        }

        @Override
        public char[] readableName() {
            throw new IllegalStateException();
        }
    };
    public static final int BLOCK_SCOPE = 1;
    public static final int CLASS_SCOPE = 3;
    public static final int COMPILATION_UNIT_SCOPE = 4;
    public static final int METHOD_SCOPE = 2;
    public static final int MODULE_SCOPE = 5;
    public static final int NOT_COMPATIBLE = -1;
    public static final int COMPATIBLE = 0;
    public static final int AUTOBOX_COMPATIBLE = 1;
    public static final int VARARGS_COMPATIBLE = 2;
    public static final int EQUAL_OR_MORE_SPECIFIC = -1;
    public static final int NOT_RELATED = 0;
    public static final int MORE_GENERIC = 1;
    public int kind;
    public Scope parent;
    private Map<String, Supplier<ReferenceBinding>> commonTypeBindings = null;
    private ArrayList<NullDefaultRange> nullDefaultRanges;
    private static Substitutor defaultSubstitutor = new Substitutor();

    protected Scope(int kind, Scope parent) {
        this.kind = kind;
        this.parent = parent;
        this.commonTypeBindings = null;
    }

    public static int compareTypes(TypeBinding left, TypeBinding right) {
        if (left.isCompatibleWith(right)) {
            return -1;
        }
        if (right.isCompatibleWith(left)) {
            return 1;
        }
        return 0;
    }

    public static TypeBinding convertEliminatingTypeVariables(TypeBinding originalType, ReferenceBinding genericType, int rank, Set eliminatedVariables) {
        if ((originalType.tagBits & 0x20000000L) != 0L) {
            switch (originalType.kind()) {
                case 68: {
                    ArrayBinding originalArrayType = (ArrayBinding)originalType;
                    TypeBinding originalLeafComponentType = originalArrayType.leafComponentType;
                    TypeBinding substitute = Scope.convertEliminatingTypeVariables(originalLeafComponentType, genericType, rank, eliminatedVariables);
                    if (!TypeBinding.notEquals(substitute, originalLeafComponentType)) break;
                    return originalArrayType.environment.createArrayType(substitute.leafComponentType(), substitute.dimensions() + originalArrayType.dimensions());
                }
                case 260: {
                    TypeBinding[] originalArguments;
                    ReferenceBinding originalEnclosing;
                    ParameterizedTypeBinding paramType = (ParameterizedTypeBinding)originalType;
                    ReferenceBinding substitutedEnclosing = originalEnclosing = paramType.enclosingType();
                    if (originalEnclosing != null) {
                        substitutedEnclosing = (ReferenceBinding)Scope.convertEliminatingTypeVariables(originalEnclosing, genericType, rank, eliminatedVariables);
                    }
                    TypeBinding[] substitutedArguments = originalArguments = paramType.arguments;
                    int i = 0;
                    int length = originalArguments == null ? 0 : originalArguments.length;
                    while (i < length) {
                        TypeBinding originalArgument = originalArguments[i];
                        TypeBinding substitutedArgument = Scope.convertEliminatingTypeVariables(originalArgument, paramType.genericType(), i, eliminatedVariables);
                        if (TypeBinding.notEquals(substitutedArgument, originalArgument)) {
                            if (substitutedArguments == originalArguments) {
                                substitutedArguments = new TypeBinding[length];
                                System.arraycopy(originalArguments, 0, substitutedArguments, 0, i);
                            }
                            substitutedArguments[i] = substitutedArgument;
                        } else if (substitutedArguments != originalArguments) {
                            substitutedArguments[i] = originalArgument;
                        }
                        ++i;
                    }
                    if (!TypeBinding.notEquals(originalEnclosing, substitutedEnclosing) && originalArguments == substitutedArguments) break;
                    return paramType.environment.createParameterizedType(paramType.genericType(), substitutedArguments, substitutedEnclosing);
                }
                case 4100: {
                    if (genericType == null) break;
                    TypeVariableBinding originalVariable = (TypeVariableBinding)originalType;
                    if (eliminatedVariables != null && eliminatedVariables.contains(originalType)) {
                        return originalVariable.environment.createWildcard(genericType, rank, null, null, 0);
                    }
                    TypeBinding originalUpperBound = originalVariable.upperBound();
                    if (eliminatedVariables == null) {
                        eliminatedVariables = new HashSet<TypeVariableBinding>(2);
                    }
                    eliminatedVariables.add(originalVariable);
                    TypeBinding substitutedUpperBound = Scope.convertEliminatingTypeVariables(originalUpperBound, genericType, rank, eliminatedVariables);
                    eliminatedVariables.remove(originalVariable);
                    return originalVariable.environment.createWildcard(genericType, rank, substitutedUpperBound, null, 1);
                }
                case 1028: {
                    break;
                }
                case 2052: {
                    TypeBinding[] originalArguments;
                    ReferenceBinding originalEnclosing;
                    ReferenceBinding currentType = (ReferenceBinding)originalType;
                    ReferenceBinding substitutedEnclosing = originalEnclosing = currentType.enclosingType();
                    if (originalEnclosing != null) {
                        substitutedEnclosing = (ReferenceBinding)Scope.convertEliminatingTypeVariables(originalEnclosing, genericType, rank, eliminatedVariables);
                    }
                    TypeBinding[] substitutedArguments = originalArguments = currentType.typeVariables();
                    int i = 0;
                    int length = originalArguments == null ? 0 : originalArguments.length;
                    while (i < length) {
                        TypeVariableBinding originalArgument = originalArguments[i];
                        TypeBinding substitutedArgument = Scope.convertEliminatingTypeVariables(originalArgument, currentType, i, eliminatedVariables);
                        if (TypeBinding.notEquals(substitutedArgument, originalArgument)) {
                            if (substitutedArguments == originalArguments) {
                                substitutedArguments = new TypeBinding[length];
                                System.arraycopy(originalArguments, 0, substitutedArguments, 0, i);
                            }
                            substitutedArguments[i] = substitutedArgument;
                        } else if (substitutedArguments != originalArguments) {
                            substitutedArguments[i] = originalArgument;
                        }
                        ++i;
                    }
                    if (!TypeBinding.notEquals(originalEnclosing, substitutedEnclosing) && originalArguments == substitutedArguments) break;
                    return originalArguments[0].environment.createParameterizedType(genericType, substitutedArguments, substitutedEnclosing);
                }
                case 516: {
                    TypeBinding originalBound;
                    WildcardBinding wildcard = (WildcardBinding)originalType;
                    TypeBinding substitutedBound = originalBound = wildcard.bound;
                    if (originalBound == null || !TypeBinding.notEquals(substitutedBound = Scope.convertEliminatingTypeVariables(originalBound, genericType, rank, eliminatedVariables), originalBound)) break;
                    return wildcard.environment.createWildcard(wildcard.genericType, wildcard.rank, substitutedBound, null, wildcard.boundKind);
                }
                case 8196: {
                    TypeBinding[] originalOtherBounds;
                    TypeBinding originalBound;
                    WildcardBinding intersection = (WildcardBinding)originalType;
                    TypeBinding substitutedBound = originalBound = intersection.bound;
                    if (originalBound != null) {
                        substitutedBound = Scope.convertEliminatingTypeVariables(originalBound, genericType, rank, eliminatedVariables);
                    }
                    TypeBinding[] substitutedOtherBounds = originalOtherBounds = intersection.otherBounds;
                    int i = 0;
                    int length = originalOtherBounds == null ? 0 : originalOtherBounds.length;
                    while (i < length) {
                        TypeBinding originalOtherBound = originalOtherBounds[i];
                        TypeBinding substitutedOtherBound = Scope.convertEliminatingTypeVariables(originalOtherBound, genericType, rank, eliminatedVariables);
                        if (TypeBinding.notEquals(substitutedOtherBound, originalOtherBound)) {
                            if (substitutedOtherBounds == originalOtherBounds) {
                                substitutedOtherBounds = new TypeBinding[length];
                                System.arraycopy(originalOtherBounds, 0, substitutedOtherBounds, 0, i);
                            }
                            substitutedOtherBounds[i] = substitutedOtherBound;
                        } else if (substitutedOtherBounds != originalOtherBounds) {
                            substitutedOtherBounds[i] = originalOtherBound;
                        }
                        ++i;
                    }
                    if (!TypeBinding.notEquals(substitutedBound, originalBound) && substitutedOtherBounds == originalOtherBounds) break;
                    return intersection.environment.createWildcard(intersection.genericType, intersection.rank, substitutedBound, substitutedOtherBounds, intersection.boundKind);
                }
            }
        }
        return originalType;
    }

    public static TypeBinding getBaseType(char[] name) {
        int length = name.length;
        if (length > 2 && length < 8) {
            switch (name[0]) {
                case 'i': {
                    if (length != 3 || name[1] != 'n' || name[2] != 't') break;
                    return TypeBinding.INT;
                }
                case 'v': {
                    if (length != 4 || name[1] != 'o' || name[2] != 'i' || name[3] != 'd') break;
                    return TypeBinding.VOID;
                }
                case 'b': {
                    if (length == 7 && name[1] == 'o' && name[2] == 'o' && name[3] == 'l' && name[4] == 'e' && name[5] == 'a' && name[6] == 'n') {
                        return TypeBinding.BOOLEAN;
                    }
                    if (length != 4 || name[1] != 'y' || name[2] != 't' || name[3] != 'e') break;
                    return TypeBinding.BYTE;
                }
                case 'c': {
                    if (length != 4 || name[1] != 'h' || name[2] != 'a' || name[3] != 'r') break;
                    return TypeBinding.CHAR;
                }
                case 'd': {
                    if (length != 6 || name[1] != 'o' || name[2] != 'u' || name[3] != 'b' || name[4] != 'l' || name[5] != 'e') break;
                    return TypeBinding.DOUBLE;
                }
                case 'f': {
                    if (length != 5 || name[1] != 'l' || name[2] != 'o' || name[3] != 'a' || name[4] != 't') break;
                    return TypeBinding.FLOAT;
                }
                case 'l': {
                    if (length != 4 || name[1] != 'o' || name[2] != 'n' || name[3] != 'g') break;
                    return TypeBinding.LONG;
                }
                case 's': {
                    if (length != 5 || name[1] != 'h' || name[2] != 'o' || name[3] != 'r' || name[4] != 't') break;
                    return TypeBinding.SHORT;
                }
            }
        }
        return null;
    }

    public static ReferenceBinding[] greaterLowerBound(ReferenceBinding[] types) {
        if (types == null) {
            return null;
        }
        int length = (types = (ReferenceBinding[])Scope.filterValidTypes((TypeBinding[])types, ReferenceBinding[]::new)).length;
        if (length == 0) {
            return null;
        }
        ReferenceBinding[] result = types;
        int removed = 0;
        int i = 0;
        while (i < length) {
            ReferenceBinding iType = result[i];
            if (iType != null) {
                int j = 0;
                while (j < length) {
                    ReferenceBinding jType;
                    if (i != j && (jType = result[j]) != null) {
                        if (Scope.isMalformedPair(iType, jType, null)) {
                            return null;
                        }
                        if (iType.isCompatibleWith(jType)) {
                            if (result == types) {
                                ReferenceBinding[] referenceBindingArray = result;
                                result = new ReferenceBinding[length];
                                System.arraycopy(referenceBindingArray, 0, result, 0, length);
                            }
                            result[j] = null;
                            ++removed;
                        }
                    }
                    ++j;
                }
            }
            ++i;
        }
        if (removed == 0) {
            return result;
        }
        if (length == removed) {
            return null;
        }
        ReferenceBinding[] trimmedResult = new ReferenceBinding[length - removed];
        int i2 = 0;
        int index = 0;
        while (i2 < length) {
            ReferenceBinding iType = result[i2];
            if (iType != null) {
                trimmedResult[index++] = iType;
            }
            ++i2;
        }
        return trimmedResult;
    }

    public static TypeBinding[] greaterLowerBound(TypeBinding[] types, Scope scope, LookupEnvironment environment) {
        if (types == null) {
            return null;
        }
        int length = (types = Scope.filterValidTypes((TypeBinding[])types, TypeBinding[]::new)).length;
        if (length == 0) {
            return null;
        }
        TypeBinding[] result = types;
        int removed = 0;
        int i = 0;
        while (i < length) {
            block16: {
                TypeBinding iType = result[i];
                if (iType == null) break block16;
                int j = 0;
                while (j < length) {
                    block17: {
                        ParameterizedTypeBinding narrowType;
                        ParameterizedTypeBinding wideType;
                        block20: {
                            TypeBinding jType;
                            block19: {
                                block18: {
                                    if (i == j || (jType = result[j]) == null) break block17;
                                    if (Scope.isMalformedPair(iType, jType, scope)) {
                                        return null;
                                    }
                                    if (!iType.isCompatibleWith(jType, scope)) break block18;
                                    if (result == types) {
                                        TypeBinding[] typeBindingArray = result;
                                        result = new TypeBinding[length];
                                        System.arraycopy(typeBindingArray, 0, result, 0, length);
                                    }
                                    result[j] = null;
                                    ++removed;
                                    break block17;
                                }
                                if (jType.isCompatibleWith(iType, scope) || !iType.isParameterizedType() || !jType.isParameterizedType()) break block17;
                                if (!iType.original().isCompatibleWith(jType.original(), scope)) break block19;
                                wideType = (ParameterizedTypeBinding)jType;
                                narrowType = (ParameterizedTypeBinding)iType;
                                break block20;
                            }
                            if (!jType.original().isCompatibleWith(iType.original(), scope)) break block17;
                            wideType = (ParameterizedTypeBinding)iType;
                            narrowType = (ParameterizedTypeBinding)jType;
                        }
                        if (wideType.arguments != null && narrowType.isProperType(false) && wideType.isProperType(false)) {
                            int numTypeArgs = wideType.arguments.length;
                            TypeBinding[] bounds = new TypeBinding[numTypeArgs];
                            int k = 0;
                            while (k < numTypeArgs) {
                                TypeBinding argument = wideType.arguments[k];
                                bounds[k] = argument.isTypeVariable() ? ((TypeVariableBinding)argument).upperBound() : argument;
                                ++k;
                            }
                            ReferenceBinding wideOriginal = (ReferenceBinding)wideType.original();
                            ParameterizedTypeBinding substitutedWideType = environment.createParameterizedType(wideOriginal, bounds, wideOriginal.enclosingType());
                            if (!narrowType.isCompatibleWith(substitutedWideType, scope)) {
                                return null;
                            }
                        }
                    }
                    ++j;
                }
            }
            ++i;
        }
        if (removed == 0) {
            return result;
        }
        if (length == removed) {
            return null;
        }
        TypeBinding[] trimmedResult = new TypeBinding[length - removed];
        int i2 = 0;
        int index = 0;
        while (i2 < length) {
            TypeBinding iType = result[i2];
            if (iType != null) {
                trimmedResult[index++] = iType;
            }
            ++i2;
        }
        return trimmedResult;
    }

    static <T extends TypeBinding> T[] filterValidTypes(T[] allTypes, Function<Integer, T[]> ctor) {
        TypeBinding[] valid = (TypeBinding[])ctor.apply(allTypes.length);
        int count = 0;
        int i = 0;
        while (i < allTypes.length) {
            if (((Binding)allTypes[i]).isValidBinding()) {
                valid[count++] = allTypes[i];
            }
            ++i;
        }
        if (count == allTypes.length) {
            return allTypes;
        }
        if (count == 0 && allTypes.length > 0) {
            return (TypeBinding[])Arrays.copyOf(allTypes, 1);
        }
        return Arrays.copyOf(valid, count);
    }

    static boolean isMalformedPair(TypeBinding t1, TypeBinding t2, Scope scope) {
        switch (t1.kind()) {
            case 4: 
            case 260: 
            case 1028: 
            case 2052: {
                TypeBinding bound;
                if (!t1.isClass() || t2.getClass() != TypeVariableBinding.class || (bound = ((TypeVariableBinding)t2).firstBound) != null && bound.erasure().isCompatibleWith(t1.erasure())) break;
                return true;
            }
        }
        return false;
    }

    public static ReferenceBinding[] substitute(Substitution substitution, ReferenceBinding[] originalTypes) {
        return defaultSubstitutor.substitute(substitution, originalTypes);
    }

    public static TypeBinding substitute(Substitution substitution, TypeBinding originalType) {
        return defaultSubstitutor.substitute(substitution, originalType);
    }

    public static TypeBinding[] substitute(Substitution substitution, TypeBinding[] originalTypes) {
        return defaultSubstitutor.substitute(substitution, originalTypes);
    }

    public TypeBinding boxing(TypeBinding type) {
        if (type.isBaseType() || type.kind() == 65540) {
            return this.environment().computeBoxingType(type);
        }
        return type;
    }

    public final ClassScope classScope() {
        Scope scope = this;
        do {
            if (!(scope instanceof ClassScope)) continue;
            return (ClassScope)scope;
        } while ((scope = scope.parent) != null);
        return null;
    }

    public final CompilationUnitScope compilationUnitScope() {
        Scope lastScope = null;
        Scope scope = this;
        do {
            lastScope = scope;
        } while ((scope = scope.parent) != null);
        return (CompilationUnitScope)lastScope;
    }

    public ModuleBinding module() {
        return this.environment().module;
    }

    public boolean isLambdaScope() {
        return false;
    }

    public boolean isLambdaSubscope() {
        Scope scope = this;
        while (scope != null) {
            switch (scope.kind) {
                case 1: {
                    break;
                }
                case 2: {
                    return scope.isLambdaScope();
                }
                default: {
                    return false;
                }
            }
            scope = scope.parent;
        }
        return false;
    }

    public final CompilerOptions compilerOptions() {
        return this.compilationUnitScope().environment.globalOptions;
    }

    protected final MethodBinding computeCompatibleMethod(MethodBinding method, TypeBinding[] arguments, InvocationSite invocationSite) {
        return this.computeCompatibleMethod(method, arguments, invocationSite, false);
    }

    protected final MethodBinding computeCompatibleMethod(MethodBinding method, TypeBinding[] arguments, InvocationSite invocationSite, boolean tiebreakingVarargsMethods) {
        Invocation invocation;
        InferenceContext18 infCtx;
        TypeBinding[] genericTypeArguments = invocationSite.genericTypeArguments();
        TypeBinding[] parameters = method.parameters;
        TypeVariableBinding[] typeVariables = method.typeVariables;
        if (parameters == arguments && (method.returnType.tagBits & 0x20000000L) == 0L && genericTypeArguments == null && typeVariables == Binding.NO_TYPE_VARIABLES) {
            return method;
        }
        int argLength = arguments.length;
        int paramLength = parameters.length;
        boolean isVarArgs = method.isVarargs();
        if (!(argLength == paramLength || isVarArgs && argLength >= paramLength - 1)) {
            return null;
        }
        CompilerOptions compilerOptions = this.compilerOptions();
        if (typeVariables != Binding.NO_TYPE_VARIABLES && compilerOptions.sourceLevel >= 0x310000L) {
            Invocation invocation2;
            InferenceContext18 infCtx2;
            TypeBinding[] newArgs = null;
            if (compilerOptions.sourceLevel < 0x340000L || genericTypeArguments != null) {
                int i = 0;
                while (i < argLength) {
                    TypeBinding param;
                    TypeBinding typeBinding = param = i < paramLength ? parameters[i] : parameters[paramLength - 1];
                    if (arguments[i].isBaseType() != param.isBaseType()) {
                        if (newArgs == null) {
                            newArgs = new TypeBinding[argLength];
                            System.arraycopy(arguments, 0, newArgs, 0, argLength);
                        }
                        newArgs[i] = this.environment().computeBoxingType(arguments[i]);
                    }
                    ++i;
                }
            }
            if (newArgs != null) {
                arguments = newArgs;
            }
            if ((method = ParameterizedGenericMethodBinding.computeCompatibleMethod(method, arguments, this, invocationSite)) == null) {
                return null;
            }
            if (!method.isValidBinding()) {
                return method;
            }
            if (compilerOptions.sourceLevel >= 0x340000L && method instanceof ParameterizedGenericMethodBinding && invocationSite instanceof Invocation && (infCtx2 = (invocation2 = (Invocation)invocationSite).getInferenceContext((ParameterizedGenericMethodBinding)method)) != null) {
                return method;
            }
        } else if (genericTypeArguments != null && compilerOptions.complianceLevel < 0x330000L) {
            if (method instanceof ParameterizedGenericMethodBinding) {
                if (!((ParameterizedGenericMethodBinding)method).wasInferred) {
                    return new ProblemMethodBinding(method, method.selector, genericTypeArguments, 13);
                }
            } else if (!method.isOverriding() || !this.isOverriddenMethodGeneric(method)) {
                return new ProblemMethodBinding(method, method.selector, genericTypeArguments, 11);
            }
        } else if (typeVariables == Binding.NO_TYPE_VARIABLES && method instanceof ParameterizedGenericMethodBinding && compilerOptions.sourceLevel >= 0x340000L && invocationSite instanceof Invocation && (infCtx = (invocation = (Invocation)invocationSite).getInferenceContext((ParameterizedGenericMethodBinding)method)) != null) {
            return method;
        }
        if (tiebreakingVarargsMethods && CompilerOptions.tolerateIllegalAmbiguousVarargsInvocation && compilerOptions.complianceLevel < 0x330000L) {
            tiebreakingVarargsMethods = false;
        }
        if (this.parameterCompatibilityLevel(method, arguments, tiebreakingVarargsMethods) > -1) {
            if ((method.tagBits & 0x10000000000000L) != 0L) {
                return this.environment().createPolymorphicMethod(method, arguments, this);
            }
            return method;
        }
        if (genericTypeArguments != null && typeVariables != Binding.NO_TYPE_VARIABLES) {
            return new ProblemMethodBinding(method, method.selector, arguments, 12);
        }
        if (method instanceof PolyParameterizedGenericMethodBinding) {
            return new ProblemMethodBinding(method, method.selector, method.parameters, 27);
        }
        return null;
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    protected boolean connectTypeVariables(TypeParameter[] typeParameters, boolean checkForErasedCandidateCollisions) {
        if (typeParameters == null || typeParameters.length == 0) {
            return true;
        }
        invocations = new HashMap<K, V>(2);
        noProblems = true;
        paramLength = typeParameters.length;
        i = 0;
        while (i < paramLength) {
            typeParameter = typeParameters[i];
            typeVariable = typeParameter.binding;
            if (typeVariable == null) {
                return false;
            }
            typeVariable.setSuperClass(this.getJavaLangObject());
            typeVariable.setSuperInterfaces(Binding.NO_SUPERINTERFACES);
            typeVariable.setFirstBound(null);
            ++i;
        }
        i = 0;
        while (i < paramLength) {
            block29: {
                block31: {
                    block27: {
                        block30: {
                            typeParameter = typeParameters[i];
                            typeVariable = typeParameter.binding;
                            typeRef = typeParameter.type;
                            if (typeRef == null) break block29;
                            isFirstBoundTypeVariable = false;
                            v0 = superType = this.kind == 2 ? typeRef.resolveType((BlockScope)this, false, 256) : typeRef.resolveType((ClassScope)this, 256);
                            if (superType != null) break block30;
                            typeVariable.tagBits |= 131072L;
                            break block27;
                        }
                        typeRef.resolvedType = superType;
                        switch (superType.kind()) {
                            case 68: {
                                this.problemReporter().boundCannotBeArray(typeRef, superType);
                                typeVariable.tagBits |= 131072L;
                                break block27;
                            }
                            case 4100: {
                                isFirstBoundTypeVariable = true;
                                varSuperType = (TypeVariableBinding)superType;
                                if (varSuperType.rank < typeVariable.rank || varSuperType.declaringElement != typeVariable.declaringElement || this.compilerOptions().complianceLevel > 0x320000L) ** GOTO lbl45
                                this.problemReporter().forwardTypeVariableReference(typeParameter, varSuperType);
                                typeVariable.tagBits |= 131072L;
                                break block27;
lbl45:
                                // 1 sources

                                if (this.compilerOptions().complianceLevel <= 0x320000L || typeVariable.rank < varSuperType.rank || varSuperType.declaringElement != typeVariable.declaringElement) break;
                                set = new SimpleSet(typeParameters.length);
                                set.add(typeVariable);
                                superBinding /* !! */  = varSuperType;
                                while (superBinding /* !! */  instanceof TypeVariableBinding) {
                                    if (set.includes(superBinding /* !! */ )) {
                                        this.problemReporter().hierarchyCircularity(typeVariable, (ReferenceBinding)varSuperType, typeRef);
                                        typeVariable.tagBits |= 131072L;
                                        break block27;
                                    }
                                    set.add(superBinding /* !! */ );
                                    superBinding /* !! */  = superBinding /* !! */ .superclass;
                                }
                                break;
                            }
                            default: {
                                if (!((ReferenceBinding)superType).isFinal() || this.environment().usesNullTypeAnnotations() && (superType.tagBits & 0x80000000000000L) != 0L) break;
                                this.problemReporter().finalVariableBound(typeVariable, typeRef);
                            }
                        }
                        superRefType = (ReferenceBinding)superType;
                        if (!superType.isInterface()) {
                            typeVariable.setSuperClass(superRefType);
                        } else {
                            typeVariable.setSuperInterfaces(new ReferenceBinding[]{superRefType});
                        }
                        typeVariable.tagBits |= superType.tagBits & 2048L;
                        typeVariable.setFirstBound(superRefType);
                    }
                    boundRefs = typeParameter.bounds;
                    if (boundRefs == null) break block31;
                    j = 0;
                    boundLength = boundRefs.length;
                    while (j < boundLength) {
                        block28: {
                            block33: {
                                block32: {
                                    typeRef = boundRefs[j];
                                    v1 = superType = this.kind == 2 ? typeRef.resolveType((BlockScope)this, false) : typeRef.resolveType((ClassScope)this);
                                    if (superType != null) break block32;
                                    typeVariable.tagBits |= 131072L;
                                    break block28;
                                }
                                typeVariable.tagBits |= superType.tagBits & 2048L;
                                v2 = didAlreadyComplain = typeRef.resolvedType.isValidBinding() == false;
                                if (!isFirstBoundTypeVariable || j != 0) break block33;
                                this.problemReporter().noAdditionalBoundAfterTypeVariable(typeRef);
                                typeVariable.tagBits |= 131072L;
                                didAlreadyComplain = true;
                                ** GOTO lbl-1000
                            }
                            if (superType.isArrayType()) {
                                if (!didAlreadyComplain) {
                                    this.problemReporter().boundCannotBeArray(typeRef, superType);
                                    typeVariable.tagBits |= 131072L;
                                }
                            } else if (!superType.isInterface()) {
                                if (!didAlreadyComplain) {
                                    this.problemReporter().boundMustBeAnInterface(typeRef, superType);
                                    typeVariable.tagBits |= 131072L;
                                }
                            } else if (!(checkForErasedCandidateCollisions && TypeBinding.equalsEquals(typeVariable.firstBound, typeVariable.superclass) && this.hasErasedCandidatesCollisions(superType, typeVariable.superclass, invocations, typeVariable, typeRef))) {
                                superRefType = (ReferenceBinding)superType;
                                index = typeVariable.superInterfaces.length;
                                while (--index >= 0) {
                                    previousInterface = typeVariable.superInterfaces[index];
                                    if (TypeBinding.equalsEquals(previousInterface, superRefType)) {
                                        this.problemReporter().duplicateBounds(typeRef, superType);
                                        typeVariable.tagBits |= 131072L;
                                    } else if (!checkForErasedCandidateCollisions || !this.hasErasedCandidatesCollisions(superType, previousInterface, invocations, typeVariable, typeRef)) {
                                        continue;
                                    }
                                    break block28;
                                }
                                size = typeVariable.superInterfaces.length;
                                System.arraycopy(typeVariable.superInterfaces, 0, typeVariable.setSuperInterfaces(new ReferenceBinding[size + 1]), 0, size);
                                typeVariable.superInterfaces[size] = superRefType;
                            }
                        }
                        ++j;
                    }
                }
                noProblems &= (typeVariable.tagBits & 131072L) == 0L;
            }
            ++i;
        }
        declaresNullTypeAnnotation = false;
        i = 0;
        while (i < paramLength) {
            this.resolveTypeParameter(typeParameters[i]);
            declaresNullTypeAnnotation |= typeParameters[i].binding.hasNullTypeAnnotations();
            ++i;
        }
        if (declaresNullTypeAnnotation) {
            i = 0;
            while (i < paramLength) {
                typeParameters[i].binding.updateTagBits();
                ++i;
            }
        }
        return noProblems;
    }

    public ArrayBinding createArrayType(TypeBinding type, int dimension) {
        return this.createArrayType(type, dimension, Binding.NO_ANNOTATIONS);
    }

    public ArrayBinding createArrayType(TypeBinding type, int dimension, AnnotationBinding[] annotations) {
        if (type.isValidBinding()) {
            return this.environment().createArrayType(type, dimension, annotations);
        }
        return new ArrayBinding(type, dimension, this.environment());
    }

    public TypeVariableBinding[] createTypeVariables(TypeParameter[] typeParameters, Binding declaringElement) {
        if (typeParameters == null || typeParameters.length == 0) {
            return Binding.NO_TYPE_VARIABLES;
        }
        PlainPackageBinding unitPackage = this.compilationUnitScope().fPackage;
        int length = typeParameters.length;
        TypeVariableBinding[] typeVariableBindings = new TypeVariableBinding[length];
        int count = 0;
        int i = 0;
        while (i < length) {
            TypeParameter typeParameter = typeParameters[i];
            TypeVariableBinding parameterBinding = new TypeVariableBinding(typeParameter.name, declaringElement, i, this.environment());
            parameterBinding.fPackage = unitPackage;
            typeParameter.binding = parameterBinding;
            if ((typeParameter.bits & 0x100000) != 0) {
                switch (declaringElement.kind()) {
                    case 8: {
                        MethodBinding methodBinding = (MethodBinding)declaringElement;
                        AbstractMethodDeclaration sourceMethod = methodBinding.sourceMethod();
                        if (sourceMethod == null) break;
                        sourceMethod.bits |= 0x100000;
                        break;
                    }
                    case 4: {
                        if (!(declaringElement instanceof SourceTypeBinding)) break;
                        SourceTypeBinding sourceTypeBinding = (SourceTypeBinding)declaringElement;
                        TypeDeclaration typeDeclaration = sourceTypeBinding.scope.referenceContext;
                        if (typeDeclaration == null) break;
                        typeDeclaration.bits |= 0x100000;
                    }
                }
            }
            int j = 0;
            while (j < count) {
                TypeVariableBinding knownVar = typeVariableBindings[j];
                if (CharOperation.equals(knownVar.sourceName, typeParameter.name)) {
                    this.problemReporter().duplicateTypeParameterInType(typeParameter);
                }
                ++j;
            }
            typeVariableBindings[count++] = parameterBinding;
            ++i;
        }
        if (count != length) {
            TypeVariableBinding[] typeVariableBindingArray = typeVariableBindings;
            typeVariableBindings = new TypeVariableBinding[count];
            System.arraycopy(typeVariableBindingArray, 0, typeVariableBindings, 0, count);
        }
        return typeVariableBindings;
    }

    void resolveTypeParameter(TypeParameter typeParameter) {
    }

    public final ClassScope enclosingClassScope() {
        Scope scope = this;
        while ((scope = scope.parent) != null) {
            if (!(scope instanceof ClassScope)) continue;
            return (ClassScope)scope;
        }
        return null;
    }

    public final ClassScope enclosingTopMostClassScope() {
        Scope scope = this;
        while (scope != null) {
            Scope t = scope.parent;
            if (t instanceof CompilationUnitScope) break;
            scope = t;
        }
        return scope instanceof ClassScope ? (ClassScope)scope : null;
    }

    public final MethodScope enclosingMethodScope() {
        Scope scope = this;
        while ((scope = scope.parent) != null) {
            if (!(scope instanceof MethodScope)) continue;
            return (MethodScope)scope;
        }
        return null;
    }

    public final MethodScope enclosingLambdaScope() {
        Scope scope = this;
        while ((scope = scope.parent) != null) {
            if (!(scope instanceof MethodScope)) continue;
            MethodScope methodScope = (MethodScope)scope;
            if (!(methodScope.referenceContext instanceof LambdaExpression)) continue;
            return methodScope;
        }
        return null;
    }

    public final ReferenceBinding enclosingReceiverType() {
        Scope scope = this;
        do {
            if (!(scope instanceof ClassScope)) continue;
            return this.environment().convertToParameterizedType(((ClassScope)scope).referenceContext.binding);
        } while ((scope = scope.parent) != null);
        return null;
    }

    public ReferenceContext enclosingReferenceContext() {
        Scope current = this;
        while ((current = current.parent) != null) {
            switch (current.kind) {
                case 2: {
                    return ((MethodScope)current).referenceContext;
                }
                case 3: {
                    return ((ClassScope)current).referenceContext;
                }
                case 5: {
                    return ((ModuleScope)current).referenceContext;
                }
                case 4: {
                    return ((CompilationUnitScope)current).referenceContext;
                }
            }
        }
        return null;
    }

    public final SourceTypeBinding enclosingSourceType() {
        Scope scope = this;
        do {
            if (!(scope instanceof ClassScope)) continue;
            return ((ClassScope)scope).referenceContext.binding;
        } while ((scope = scope.parent) != null);
        return null;
    }

    public final LookupEnvironment environment() {
        Scope scope;
        Scope unitScope = this;
        while ((scope = unitScope.parent) != null) {
            unitScope = scope;
        }
        return ((CompilationUnitScope)unitScope).environment;
    }

    protected MethodBinding findDefaultAbstractMethod(ReferenceBinding receiverType, char[] selector, TypeBinding[] argumentTypes, InvocationSite invocationSite, ReferenceBinding classHierarchyStart, ObjectVector found, MethodBinding[] concreteMatches) {
        int startFoundSize = found.size;
        boolean sourceLevel18 = this.compilerOptions().sourceLevel >= 0x340000L;
        ReferenceBinding currentType = classHierarchyStart;
        ArrayList<TypeBinding> visitedTypes = new ArrayList<TypeBinding>();
        while (currentType != null) {
            this.findMethodInSuperInterfaces(currentType, selector, found, visitedTypes, invocationSite);
            currentType = currentType.superclass();
        }
        int candidatesCount = concreteMatches == null ? 0 : concreteMatches.length;
        int foundSize = found.size;
        MethodBinding[] candidates = new MethodBinding[foundSize - startFoundSize + candidatesCount];
        if (concreteMatches != null) {
            System.arraycopy(concreteMatches, 0, candidates, 0, candidatesCount);
        }
        MethodBinding problemMethod = null;
        if (foundSize > startFoundSize) {
            MethodVerifier methodVerifier = this.environment().methodVerifier();
            int i = startFoundSize;
            while (i < foundSize) {
                MethodBinding methodBinding = (MethodBinding)found.elementAt(i);
                MethodBinding compatibleMethod = this.computeCompatibleMethod(methodBinding, argumentTypes, invocationSite);
                if (compatibleMethod != null) {
                    if (compatibleMethod.isValidBinding()) {
                        int j;
                        if (concreteMatches != null) {
                            j = 0;
                            int length = concreteMatches.length;
                            while (j < length) {
                                if (methodVerifier.areMethodsCompatible(concreteMatches[j], compatibleMethod)) {
                                    // empty if block
                                }
                                ++j;
                            }
                        }
                        if (sourceLevel18 || !compatibleMethod.isVarargs() || !(compatibleMethod instanceof ParameterizedGenericMethodBinding)) {
                            j = 0;
                            while (j < startFoundSize) {
                                MethodBinding classMethod = (MethodBinding)found.elementAt(j);
                                if (classMethod == null || !methodVerifier.areMethodsCompatible(classMethod, compatibleMethod)) {
                                    ++j;
                                    continue;
                                }
                                break;
                            }
                        } else {
                            candidates[candidatesCount++] = compatibleMethod;
                        }
                    } else if (problemMethod == null) {
                        problemMethod = compatibleMethod;
                    }
                }
                ++i;
            }
        }
        MethodBinding concreteMatch = null;
        if (candidatesCount < 2) {
            if (concreteMatches == null && candidatesCount == 0) {
                return problemMethod;
            }
            concreteMatch = candidates[0];
            if (concreteMatch != null) {
                this.compilationUnitScope().recordTypeReferences(concreteMatch.thrownExceptions);
            }
            return concreteMatch;
        }
        if (this.compilerOptions().complianceLevel >= 0x300000L) {
            return this.mostSpecificMethodBinding(candidates, candidatesCount, argumentTypes, invocationSite, receiverType);
        }
        return this.mostSpecificInterfaceMethodBinding(candidates, candidatesCount, invocationSite);
    }

    public ReferenceBinding findDirectMemberType(char[] typeName, ReferenceBinding enclosingType) {
        if ((enclosingType.tagBits & 0x10000L) != 0L) {
            return null;
        }
        ReferenceBinding enclosingReceiverType = this.enclosingReceiverType();
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordReference(enclosingType, typeName);
        ReferenceBinding memberType = enclosingType.getMemberType(typeName);
        if (memberType != null) {
            unitScope.recordTypeReference(memberType);
            if (enclosingReceiverType == null ? memberType.canBeSeenBy(this.getCurrentPackage()) : memberType.canBeSeenBy(enclosingType, enclosingReceiverType)) {
                return memberType;
            }
            return new ProblemReferenceBinding(new char[][]{typeName}, memberType, 2);
        }
        return null;
    }

    public MethodBinding findExactMethod(ReferenceBinding receiverType, char[] selector, TypeBinding[] argumentTypes, InvocationSite invocationSite) {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordTypeReferences(argumentTypes);
        MethodBinding exactMethod = receiverType.getExactMethod(selector, argumentTypes, unitScope);
        if (exactMethod != null && exactMethod.typeVariables == Binding.NO_TYPE_VARIABLES && !exactMethod.isBridge()) {
            if (this.compilerOptions().sourceLevel >= 0x310000L) {
                int i = argumentTypes.length;
                while (--i >= 0) {
                    ReferenceBinding r;
                    TypeBinding t = argumentTypes[i].leafComponentType();
                    if (!(t instanceof ReferenceBinding) || !((r = (ReferenceBinding)t).isHierarchyConnected() ? this.isSubtypeOfRawType(r) : r.isRawType())) continue;
                    return null;
                }
            }
            unitScope.recordTypeReferences(exactMethod.thrownExceptions);
            if (exactMethod.isAbstract() && exactMethod.thrownExceptions != Binding.NO_EXCEPTIONS) {
                return null;
            }
            if (exactMethod.canBeSeenBy(receiverType, invocationSite, this)) {
                if (argumentTypes == Binding.NO_PARAMETERS && CharOperation.equals(selector, TypeConstants.GETCLASS) && exactMethod.returnType.isParameterizedType()) {
                    return this.environment().createGetClassMethod(receiverType, exactMethod, this);
                }
                if (invocationSite.genericTypeArguments() != null) {
                    exactMethod = this.computeCompatibleMethod(exactMethod, argumentTypes, invocationSite);
                } else if ((exactMethod.tagBits & 0x10000000000000L) != 0L) {
                    return this.environment().createPolymorphicMethod(exactMethod, argumentTypes, this);
                }
                return exactMethod;
            }
        }
        return null;
    }

    public FieldBinding findField(TypeBinding receiverType, char[] fieldName, InvocationSite invocationSite, boolean needResolve) {
        return this.findField(receiverType, fieldName, invocationSite, needResolve, false);
    }

    /*
     * Enabled aggressive block sorting
     */
    public FieldBinding findField(TypeBinding receiverType, char[] fieldName, InvocationSite invocationSite, boolean needResolve, boolean invisibleFieldsOk) {
        boolean insideTypeAnnotations;
        CompilationUnitScope unitScope;
        block37: {
            TypeBinding leafType;
            unitScope = this.compilationUnitScope();
            unitScope.recordTypeReference(receiverType);
            switch (receiverType.kind()) {
                case 132: {
                    return null;
                }
                case 516: 
                case 4100: 
                case 8196: {
                    TypeBinding receiverErasure = receiverType.erasure();
                    if (receiverErasure.isArrayType()) {
                        leafType = receiverErasure.leafComponentType();
                        break;
                    }
                    break block37;
                }
                case 68: {
                    leafType = receiverType.leafComponentType();
                    break;
                }
                default: {
                    break block37;
                }
            }
            if (leafType instanceof ReferenceBinding && !((ReferenceBinding)leafType).canBeSeenBy(this)) {
                return new ProblemFieldBinding((ReferenceBinding)leafType, fieldName, 8);
            }
            if (!CharOperation.equals(fieldName, TypeConstants.LENGTH)) return null;
            if ((leafType.tagBits & 0x80L) == 0L) return ArrayBinding.ArrayLength;
            return new ProblemFieldBinding(ArrayBinding.ArrayLength, null, fieldName, 1);
        }
        ReferenceBinding currentType = (ReferenceBinding)receiverType;
        if (!currentType.canBeSeenBy(this)) {
            return new ProblemFieldBinding(currentType, fieldName, 8);
        }
        currentType.initializeForStaticImports();
        FieldBinding field = currentType.getField(fieldName, needResolve);
        boolean bl = insideTypeAnnotations = this instanceof MethodScope && ((MethodScope)this).insideTypeAnnotation;
        if (field != null) {
            if (invisibleFieldsOk) {
                return field;
            }
            if (invocationSite != null && !insideTypeAnnotations) {
                if (!field.canBeSeenBy(currentType, invocationSite, this)) return new ProblemFieldBinding(field, field.declaringClass, fieldName, 2);
                return field;
            }
            if (!field.canBeSeenBy(this.getCurrentPackage())) return new ProblemFieldBinding(field, field.declaringClass, fieldName, 2);
            return field;
        }
        ReferenceBinding[] interfacesToVisit = null;
        int nextPosition = 0;
        FieldBinding visibleField = null;
        boolean keepLooking = true;
        FieldBinding notVisibleField = null;
        while (keepLooking) {
            ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
            if (itsInterfaces != null && itsInterfaces != Binding.NO_SUPERINTERFACES) {
                if (interfacesToVisit == null) {
                    interfacesToVisit = itsInterfaces;
                    nextPosition = interfacesToVisit.length;
                } else {
                    int itsLength = itsInterfaces.length;
                    if (nextPosition + itsLength >= interfacesToVisit.length) {
                        ReferenceBinding[] referenceBindingArray = interfacesToVisit;
                        interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5];
                        System.arraycopy(referenceBindingArray, 0, interfacesToVisit, 0, nextPosition);
                    }
                    int a = 0;
                    while (a < itsLength) {
                        block34: {
                            ReferenceBinding next = itsInterfaces[a];
                            int b = 0;
                            while (b < nextPosition) {
                                if (!TypeBinding.equalsEquals(next, interfacesToVisit[b])) {
                                    ++b;
                                    continue;
                                }
                                break block34;
                            }
                            interfacesToVisit[nextPosition++] = next;
                        }
                        ++a;
                    }
                }
            }
            if ((currentType = currentType.superclass()) == null) break;
            unitScope.recordTypeReference(currentType);
            currentType.initializeForStaticImports();
            currentType = (ReferenceBinding)currentType.capture(this, invocationSite == null ? 0 : invocationSite.sourceStart(), invocationSite == null ? 0 : invocationSite.sourceEnd());
            field = currentType.getField(fieldName, needResolve);
            if (field == null) continue;
            if (invisibleFieldsOk) {
                return field;
            }
            keepLooking = false;
            if (field.canBeSeenBy(receiverType, invocationSite, this)) {
                if (visibleField != null) return new ProblemFieldBinding(visibleField, visibleField.declaringClass, fieldName, 3);
                visibleField = field;
                continue;
            }
            if (notVisibleField != null) continue;
            notVisibleField = field;
        }
        if (interfacesToVisit != null) {
            ProblemFieldBinding ambiguous = null;
            int i = 0;
            while (i < nextPosition) {
                block35: {
                    ReferenceBinding anInterface = interfacesToVisit[i];
                    unitScope.recordTypeReference(anInterface);
                    field = anInterface.getField(fieldName, true);
                    if (field != null) {
                        if (invisibleFieldsOk) {
                            return field;
                        }
                        if (visibleField == null) {
                            visibleField = field;
                            break block35;
                        } else {
                            ambiguous = new ProblemFieldBinding(visibleField, visibleField.declaringClass, fieldName, 3);
                            break;
                        }
                    }
                    ReferenceBinding[] itsInterfaces = anInterface.superInterfaces();
                    if (itsInterfaces != null && itsInterfaces != Binding.NO_SUPERINTERFACES) {
                        int itsLength = itsInterfaces.length;
                        if (nextPosition + itsLength >= interfacesToVisit.length) {
                            ReferenceBinding[] referenceBindingArray = interfacesToVisit;
                            interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5];
                            System.arraycopy(referenceBindingArray, 0, interfacesToVisit, 0, nextPosition);
                        }
                        int a = 0;
                        while (a < itsLength) {
                            block36: {
                                ReferenceBinding next = itsInterfaces[a];
                                int b = 0;
                                while (b < nextPosition) {
                                    if (!TypeBinding.equalsEquals(next, interfacesToVisit[b])) {
                                        ++b;
                                        continue;
                                    }
                                    break block36;
                                }
                                interfacesToVisit[nextPosition++] = next;
                            }
                            ++a;
                        }
                    }
                }
                ++i;
            }
            if (ambiguous != null) {
                return ambiguous;
            }
        }
        if (visibleField != null) {
            return visibleField;
        }
        if (notVisibleField == null) return null;
        return new ProblemFieldBinding(notVisibleField, currentType, fieldName, 2);
    }

    /*
     * Enabled aggressive block sorting
     */
    public ReferenceBinding findMemberType(char[] typeName, ReferenceBinding enclosingType) {
        if ((enclosingType.tagBits & 0x10000L) != 0L) {
            return null;
        }
        SourceTypeBinding enclosingSourceType = this.enclosingSourceType();
        PackageBinding currentPackage = this.getCurrentPackage();
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordReference(enclosingType, typeName);
        ReferenceBinding memberType = enclosingType.getMemberType(typeName);
        if (memberType != null) {
            unitScope.recordTypeReference(memberType);
            if (enclosingSourceType == null || this.parent == unitScope && (enclosingSourceType.tagBits & 0x40000L) == 0L ? memberType.canBeSeenBy(currentPackage) : memberType.canBeSeenBy(enclosingType, enclosingSourceType)) {
                return memberType;
            }
            return new ProblemReferenceBinding(new char[][]{typeName}, memberType, 2);
        }
        ReferenceBinding currentType = enclosingType;
        ReferenceBinding[] interfacesToVisit = null;
        int nextPosition = 0;
        ReferenceBinding visibleMemberType = null;
        boolean keepLooking = true;
        ReferenceBinding notVisible = null;
        while (keepLooking) {
            ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
            if (itsInterfaces == null) {
                ReferenceBinding sourceType;
                ReferenceBinding referenceBinding = sourceType = currentType.isParameterizedType() ? ((ParameterizedTypeBinding)currentType).genericType() : currentType;
                if (sourceType instanceof SourceTypeBinding) {
                    if (sourceType.isHierarchyBeingConnected()) {
                        return null;
                    }
                    ((SourceTypeBinding)sourceType).scope.connectTypeHierarchy();
                }
                itsInterfaces = currentType.superInterfaces();
            }
            if (itsInterfaces != null && itsInterfaces != Binding.NO_SUPERINTERFACES) {
                if (interfacesToVisit == null) {
                    interfacesToVisit = itsInterfaces;
                    nextPosition = interfacesToVisit.length;
                } else {
                    int itsLength = itsInterfaces.length;
                    if (nextPosition + itsLength >= interfacesToVisit.length) {
                        ReferenceBinding[] referenceBindingArray = interfacesToVisit;
                        interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5];
                        System.arraycopy(referenceBindingArray, 0, interfacesToVisit, 0, nextPosition);
                    }
                    int a = 0;
                    while (a < itsLength) {
                        block28: {
                            ReferenceBinding next = itsInterfaces[a];
                            int b = 0;
                            while (b < nextPosition) {
                                if (!TypeBinding.equalsEquals(next, interfacesToVisit[b])) {
                                    ++b;
                                    continue;
                                }
                                break block28;
                            }
                            interfacesToVisit[nextPosition++] = next;
                        }
                        ++a;
                    }
                }
            }
            if ((currentType = currentType.superclass()) == null) break;
            unitScope.recordReference(currentType, typeName);
            memberType = currentType.getMemberType(typeName);
            if (memberType == null) continue;
            unitScope.recordTypeReference(memberType);
            keepLooking = false;
            if (enclosingSourceType == null ? memberType.canBeSeenBy(currentPackage) : memberType.canBeSeenBy(enclosingType, enclosingSourceType)) {
                if (visibleMemberType != null) {
                    return new ProblemReferenceBinding(new char[][]{typeName}, visibleMemberType, 3);
                }
                visibleMemberType = memberType;
                continue;
            }
            notVisible = memberType;
        }
        if (interfacesToVisit != null) {
            ProblemReferenceBinding ambiguous = null;
            int i = 0;
            while (i < nextPosition) {
                block29: {
                    void anInterface = interfacesToVisit[i];
                    unitScope.recordReference((ReferenceBinding)anInterface, typeName);
                    memberType = anInterface.getMemberType(typeName);
                    if (memberType != null) {
                        unitScope.recordTypeReference(memberType);
                        if (visibleMemberType == null) {
                            visibleMemberType = memberType;
                            break block29;
                        } else {
                            ambiguous = new ProblemReferenceBinding(new char[][]{typeName}, visibleMemberType, 3);
                            break;
                        }
                    }
                    ReferenceBinding[] itsInterfaces = anInterface.superInterfaces();
                    if (itsInterfaces != null && itsInterfaces != Binding.NO_SUPERINTERFACES) {
                        int itsLength = itsInterfaces.length;
                        if (nextPosition + itsLength >= interfacesToVisit.length) {
                            ReferenceBinding[] referenceBindingArray = interfacesToVisit;
                            interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5];
                            System.arraycopy(referenceBindingArray, 0, interfacesToVisit, 0, nextPosition);
                        }
                        int a = 0;
                        while (a < itsLength) {
                            block30: {
                                ReferenceBinding next = itsInterfaces[a];
                                int b = 0;
                                while (b < nextPosition) {
                                    if (!TypeBinding.equalsEquals(next, interfacesToVisit[b])) {
                                        ++b;
                                        continue;
                                    }
                                    break block30;
                                }
                                interfacesToVisit[nextPosition++] = next;
                            }
                            ++a;
                        }
                    }
                }
                ++i;
            }
            if (ambiguous != null) {
                return ambiguous;
            }
        }
        if (visibleMemberType != null) {
            return visibleMemberType;
        }
        if (notVisible == null) return null;
        return new ProblemReferenceBinding(new char[][]{typeName}, notVisible, 2);
    }

    public MethodBinding findMethod(ReferenceBinding receiverType, char[] selector, TypeBinding[] argumentTypes, InvocationSite invocationSite, boolean inStaticContext) {
        TypeBinding elementType;
        MethodBinding method = this.findMethod0(receiverType, selector, argumentTypes, invocationSite, inStaticContext);
        if (method != null && method.isValidBinding() && method.isVarargs() && (elementType = method.parameters[method.parameters.length - 1].leafComponentType()) instanceof ReferenceBinding && !((ReferenceBinding)elementType).canBeSeenBy(this)) {
            return new ProblemMethodBinding(method, method.selector, invocationSite.genericTypeArguments(), 16);
        }
        return method;
    }

    public MethodBinding findMethod0(ReferenceBinding receiverType, char[] selector, TypeBinding[] argumentTypes, InvocationSite invocationSite, boolean inStaticContext) {
        MethodBinding candidate;
        boolean searchForDefaultAbstractMethod;
        int candidatesCount;
        MethodBinding[] candidates;
        ReferenceBinding classHierarchyStart;
        boolean soureLevel18;
        long complianceLevel;
        CompilationUnitScope unitScope;
        ObjectVector found;
        block67: {
            MethodBinding interfaceMethod;
            ReferenceBinding currentType = receiverType;
            boolean receiverTypeIsInterface = receiverType.isInterface();
            found = new ObjectVector(3);
            unitScope = this.compilationUnitScope();
            unitScope.recordTypeReferences(argumentTypes);
            ArrayList<TypeBinding> visitedTypes = new ArrayList<TypeBinding>();
            if (receiverTypeIsInterface) {
                unitScope.recordTypeReference(receiverType);
                Object[] receiverMethods = receiverType.getMethods(selector, argumentTypes.length);
                if (receiverMethods.length > 0) {
                    found.addAll(receiverMethods);
                }
                this.findMethodInSuperInterfaces(receiverType, selector, found, visitedTypes, invocationSite);
                currentType = this.getJavaLangObject();
            }
            boolean isCompliant14 = (complianceLevel = this.compilerOptions().complianceLevel) >= 0x300000L;
            boolean isCompliant15 = complianceLevel >= 0x310000L;
            soureLevel18 = this.compilerOptions().sourceLevel >= 0x340000L;
            classHierarchyStart = currentType;
            MethodVerifier verifier = this.environment().methodVerifier();
            while (currentType != null) {
                unitScope.recordTypeReference(currentType);
                currentType = (ReferenceBinding)currentType.capture(this, invocationSite == null ? 0 : invocationSite.sourceStart(), invocationSite == null ? 0 : invocationSite.sourceEnd());
                Object[] currentMethods = currentType.getMethods(selector, argumentTypes.length);
                int currentLength = currentMethods.length;
                if (currentLength > 0) {
                    Object currentMethod;
                    int i;
                    if (isCompliant14 && (receiverTypeIsInterface || found.size > 0)) {
                        i = 0;
                        int l = currentLength;
                        while (i < l) {
                            currentMethod = currentMethods[i];
                            if (currentMethod != null) {
                                if (receiverTypeIsInterface && !((MethodBinding)currentMethod).isPublic()) {
                                    --currentLength;
                                    currentMethods[i] = null;
                                } else {
                                    int j = 0;
                                    int max = found.size;
                                    while (j < max) {
                                        MethodBinding matchingMethod = (MethodBinding)found.elementAt(j);
                                        MethodBinding matchingOriginal = matchingMethod.original();
                                        MethodBinding currentOriginal = matchingOriginal.findOriginalInheritedMethod((MethodBinding)currentMethod);
                                        if (currentOriginal != null && verifier.isParameterSubsignature(matchingOriginal, currentOriginal)) {
                                            if (isCompliant15 && matchingMethod.isBridge() && !((MethodBinding)currentMethod).isBridge()) break;
                                            --currentLength;
                                            currentMethods[i] = null;
                                            break;
                                        }
                                        ++j;
                                    }
                                }
                            }
                            ++i;
                        }
                    }
                    if (currentLength > 0) {
                        if (currentMethods.length == currentLength) {
                            found.addAll(currentMethods);
                        } else {
                            i = 0;
                            int max = currentMethods.length;
                            while (i < max) {
                                currentMethod = currentMethods[i];
                                if (currentMethod != null) {
                                    found.add(currentMethod);
                                }
                                ++i;
                            }
                        }
                    }
                }
                currentType = currentType.superclass();
            }
            int foundSize = found.size;
            candidates = null;
            candidatesCount = 0;
            Binding problemMethod = null;
            boolean bl = searchForDefaultAbstractMethod = soureLevel18 || isCompliant14 && !receiverTypeIsInterface && (receiverType.isAbstract() || receiverType.isTypeVariable());
            if (foundSize > 0) {
                int i = 0;
                while (i < foundSize) {
                    MethodBinding methodBinding = (MethodBinding)found.elementAt(i);
                    MethodBinding compatibleMethod = this.computeCompatibleMethod(methodBinding, argumentTypes, invocationSite);
                    if (compatibleMethod != null) {
                        if (compatibleMethod.isValidBinding() || compatibleMethod.problemId() == 23) {
                            if (foundSize == 1 && compatibleMethod.canBeSeenBy(receiverType, invocationSite, this)) {
                                if (searchForDefaultAbstractMethod) {
                                    return this.findDefaultAbstractMethod(receiverType, selector, argumentTypes, invocationSite, classHierarchyStart, found, new MethodBinding[]{compatibleMethod});
                                }
                                unitScope.recordTypeReferences(compatibleMethod.thrownExceptions);
                                return compatibleMethod;
                            }
                            if (candidatesCount == 0) {
                                candidates = new MethodBinding[foundSize];
                            }
                            candidates[candidatesCount++] = compatibleMethod;
                        } else if (problemMethod == null) {
                            problemMethod = compatibleMethod;
                        }
                    }
                    ++i;
                }
            }
            if (candidatesCount != 0) break block67;
            if (problemMethod != null) {
                switch (problemMethod.problemId()) {
                    case 11: 
                    case 13: {
                        return problemMethod;
                    }
                }
            }
            if ((interfaceMethod = this.findDefaultAbstractMethod(receiverType, selector, argumentTypes, invocationSite, classHierarchyStart, found, null)) != null) {
                if (soureLevel18 && foundSize > 0 && interfaceMethod.isVarargs() && interfaceMethod instanceof ParameterizedGenericMethodBinding) {
                    MethodBinding original = interfaceMethod.original();
                    int i = 0;
                    while (i < foundSize) {
                        MethodBinding substitute;
                        MethodBinding classMethod = (MethodBinding)found.elementAt(i);
                        if (!classMethod.isAbstract() && (substitute = verifier.computeSubstituteMethod(original, classMethod)) != null && verifier.isSubstituteParameterSubsignature(classMethod, substitute)) {
                            return new ProblemMethodBinding(interfaceMethod, selector, argumentTypes, 24);
                        }
                        ++i;
                    }
                }
                return interfaceMethod;
            }
            if (found.size == 0) {
                return null;
            }
            if (problemMethod != null) {
                return problemMethod;
            }
            int bestArgMatches = -1;
            MethodBinding bestGuess = (MethodBinding)found.elementAt(0);
            int argLength = argumentTypes.length;
            foundSize = found.size;
            int i = 0;
            while (i < foundSize) {
                block68: {
                    int argMatches;
                    MethodBinding methodBinding;
                    block69: {
                        int diff2;
                        methodBinding = (MethodBinding)found.elementAt(i);
                        TypeBinding[] params = methodBinding.parameters;
                        int paramLength = params.length;
                        argMatches = 0;
                        int a = 0;
                        while (a < argLength) {
                            TypeBinding arg = argumentTypes[a];
                            int p = a == 0 ? 0 : a - 1;
                            while (p < paramLength && p < a + 1) {
                                if (TypeBinding.equalsEquals(params[p], arg)) {
                                    ++argMatches;
                                    break;
                                }
                                ++p;
                            }
                            ++a;
                        }
                        if (argMatches < bestArgMatches) break block68;
                        if (argMatches != bestArgMatches) break block69;
                        int diff1 = paramLength < argLength ? 2 * (argLength - paramLength) : paramLength - argLength;
                        int bestLength = bestGuess.parameters.length;
                        int n = diff2 = bestLength < argLength ? 2 * (argLength - bestLength) : bestLength - argLength;
                        if (diff1 >= diff2) break block68;
                    }
                    if (bestGuess == methodBinding || !MethodVerifier.doesMethodOverride(bestGuess, methodBinding, this.environment())) {
                        bestArgMatches = argMatches;
                        bestGuess = methodBinding;
                    }
                }
                ++i;
            }
            return new ProblemMethodBinding(bestGuess, bestGuess.selector, argumentTypes, 1);
        }
        int visiblesCount = 0;
        int i = 0;
        while (i < candidatesCount) {
            void methodBinding = candidates[i];
            if (methodBinding.canBeSeenBy(receiverType, invocationSite, this)) {
                if (visiblesCount != i) {
                    candidates[i] = null;
                    candidates[visiblesCount] = methodBinding;
                }
                ++visiblesCount;
            }
            ++i;
        }
        switch (visiblesCount) {
            case 0: {
                MethodBinding interfaceMethod = this.findDefaultAbstractMethod(receiverType, selector, argumentTypes, invocationSite, classHierarchyStart, found, null);
                if (interfaceMethod != null) {
                    return interfaceMethod;
                }
                candidate = candidates[0];
                int reason = 2;
                if (candidate.isStatic() && candidate.declaringClass.isInterface() && !candidate.isPrivate()) {
                    reason = soureLevel18 ? 20 : 29;
                }
                return new ProblemMethodBinding(candidate, candidate.selector, candidate.parameters, reason);
            }
            case 1: {
                if (searchForDefaultAbstractMethod) {
                    return this.findDefaultAbstractMethod(receiverType, selector, argumentTypes, invocationSite, classHierarchyStart, found, new MethodBinding[]{candidates[0]});
                }
                candidate = candidates[0];
                if (candidate != null) {
                    unitScope.recordTypeReferences(candidate.thrownExceptions);
                }
                return candidate;
            }
        }
        if (complianceLevel <= 0x2F0000L) {
            ReferenceBinding declaringClass = candidates[0].declaringClass;
            return !declaringClass.isInterface() ? this.mostSpecificClassMethodBinding(candidates, visiblesCount, invocationSite) : this.mostSpecificInterfaceMethodBinding(candidates, visiblesCount, invocationSite);
        }
        if (this.compilerOptions().sourceLevel >= 0x310000L) {
            i = 0;
            while (i < visiblesCount) {
                candidate = candidates[i];
                if (candidate.isParameterizedGeneric()) {
                    candidate = candidate.shallowOriginal();
                }
                if (candidate.hasSubstitutedParameters()) {
                    int j = i + 1;
                    while (j < visiblesCount) {
                        MethodBinding otherCandidate = candidates[j];
                        if (otherCandidate.hasSubstitutedParameters() && (otherCandidate == candidate || TypeBinding.equalsEquals(candidate.declaringClass, otherCandidate.declaringClass) && candidate.areParametersEqual(otherCandidate))) {
                            return new ProblemMethodBinding(candidates[i], candidates[i].selector, candidates[i].parameters, 3);
                        }
                        ++j;
                    }
                }
                ++i;
            }
        }
        if (inStaticContext) {
            MethodBinding[] staticCandidates = new MethodBinding[visiblesCount];
            int staticCount = 0;
            int i2 = 0;
            while (i2 < visiblesCount) {
                if (candidates[i2].isStatic()) {
                    staticCandidates[staticCount++] = candidates[i2];
                }
                ++i2;
            }
            if (staticCount == 1) {
                return staticCandidates[0];
            }
            if (staticCount > 1) {
                return this.mostSpecificMethodBinding(staticCandidates, staticCount, argumentTypes, invocationSite, receiverType);
            }
        }
        if (visiblesCount != candidates.length) {
            MethodBinding[] methodBindingArray = candidates;
            candidates = new MethodBinding[visiblesCount];
            System.arraycopy(methodBindingArray, 0, candidates, 0, visiblesCount);
        }
        return searchForDefaultAbstractMethod ? this.findDefaultAbstractMethod(receiverType, selector, argumentTypes, invocationSite, classHierarchyStart, found, candidates) : this.mostSpecificMethodBinding(candidates, visiblesCount, argumentTypes, invocationSite, receiverType);
    }

    public MethodBinding findMethodForArray(ArrayBinding receiverType, char[] selector, TypeBinding[] argumentTypes, InvocationSite invocationSite) {
        TypeBinding leafType = receiverType.leafComponentType();
        if (leafType instanceof ReferenceBinding && !((ReferenceBinding)leafType).canBeSeenBy(this)) {
            return new ProblemMethodBinding(selector, Binding.NO_PARAMETERS, (ReferenceBinding)leafType, 8);
        }
        ReferenceBinding object = this.getJavaLangObject();
        MethodBinding methodBinding = object.getExactMethod(selector, argumentTypes, null);
        if (methodBinding != null) {
            if (argumentTypes == Binding.NO_PARAMETERS) {
                switch (selector[0]) {
                    case 'c': {
                        if (!CharOperation.equals(selector, TypeConstants.CLONE)) break;
                        return receiverType.getCloneMethod(methodBinding);
                    }
                    case 'g': {
                        if (!CharOperation.equals(selector, TypeConstants.GETCLASS) || !methodBinding.returnType.isParameterizedType()) break;
                        return this.environment().createGetClassMethod(receiverType, methodBinding, this);
                    }
                }
            }
            if (methodBinding.canBeSeenBy(receiverType, invocationSite, this)) {
                return methodBinding;
            }
        }
        if ((methodBinding = this.findMethod(object, selector, argumentTypes, invocationSite, false)) == null) {
            return new ProblemMethodBinding(selector, argumentTypes, 26);
        }
        return methodBinding;
    }

    protected void findMethodInSuperInterfaces(ReferenceBinding receiverType, char[] selector, ObjectVector found, List<TypeBinding> visitedTypes, InvocationSite invocationSite) {
        ReferenceBinding currentType = receiverType;
        ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
        if (itsInterfaces != null && itsInterfaces != Binding.NO_SUPERINTERFACES) {
            ReferenceBinding[] interfacesToVisit = itsInterfaces;
            int nextPosition = interfacesToVisit.length;
            int i = 0;
            while (i < nextPosition) {
                block17: {
                    currentType = interfacesToVisit[i];
                    if (visitedTypes != null) {
                        TypeBinding uncaptured = currentType.uncapture(this);
                        for (TypeBinding visited : visitedTypes) {
                            if (!uncaptured.isEquivalentTo(visited)) {
                                continue;
                            }
                            break block17;
                        }
                        visitedTypes.add(uncaptured);
                    }
                    this.compilationUnitScope().recordTypeReference(currentType);
                    currentType = (ReferenceBinding)currentType.capture(this, invocationSite == null ? 0 : invocationSite.sourceStart(), invocationSite == null ? 0 : invocationSite.sourceEnd());
                    MethodBinding[] currentMethods = currentType.getMethods(selector);
                    if (currentMethods.length > 0) {
                        int foundSize = found.size;
                        int c = 0;
                        int l = currentMethods.length;
                        while (c < l) {
                            MethodBinding current = currentMethods[c];
                            if (current.canBeSeenBy(receiverType, invocationSite, this)) {
                                if (foundSize > 0) {
                                    int f = 0;
                                    while (f < foundSize) {
                                        if (current != found.elementAt(f)) {
                                            ++f;
                                            continue;
                                        }
                                        break;
                                    }
                                } else {
                                    found.add(current);
                                }
                            }
                            ++c;
                        }
                    }
                    if ((itsInterfaces = currentType.superInterfaces()) != null && itsInterfaces != Binding.NO_SUPERINTERFACES) {
                        int itsLength = itsInterfaces.length;
                        if (nextPosition + itsLength >= interfacesToVisit.length) {
                            ReferenceBinding[] referenceBindingArray = interfacesToVisit;
                            interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5];
                            System.arraycopy(referenceBindingArray, 0, interfacesToVisit, 0, nextPosition);
                        }
                        int a = 0;
                        while (a < itsLength) {
                            block18: {
                                ReferenceBinding next = itsInterfaces[a];
                                int b = 0;
                                while (b < nextPosition) {
                                    if (!TypeBinding.equalsEquals(next, interfacesToVisit[b])) {
                                        ++b;
                                        continue;
                                    }
                                    break block18;
                                }
                                interfacesToVisit[nextPosition++] = next;
                            }
                            ++a;
                        }
                    }
                }
                ++i;
            }
        }
    }

    public ReferenceBinding findType(char[] typeName, PackageBinding declarationPackage, PackageBinding invocationPackage) {
        this.compilationUnitScope().recordReference(declarationPackage.compoundName, typeName);
        ReferenceBinding typeBinding = declarationPackage.getType(typeName, this.module());
        if (typeBinding == null) {
            return null;
        }
        if (typeBinding.isValidBinding() && declarationPackage != invocationPackage && !typeBinding.canBeSeenBy(invocationPackage)) {
            return new ProblemReferenceBinding(new char[][]{typeName}, typeBinding, 2);
        }
        return typeBinding;
    }

    public LocalVariableBinding findVariable(char[] variable, InvocationSite invocationSite) {
        return null;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public Binding getBinding(char[] name, int mask, InvocationSite invocationSite, boolean needResolve) {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        LookupEnvironment env = unitScope.environment;
        try {
            env.missingClassFileLocation = invocationSite;
            Binding binding = null;
            Binding problemField = null;
            if ((mask & 3) != 0) {
                boolean insideStaticContext = false;
                boolean insideConstructorCall = false;
                boolean insideTypeAnnotation = false;
                Binding foundField = null;
                ProblemFieldBinding foundInsideProblem = null;
                Scope scope = this;
                MethodScope methodScope = null;
                int depth = 0;
                int foundDepth = 0;
                boolean shouldTrackOuterLocals = false;
                ReferenceBinding foundActualReceiverType = null;
                block27: while (true) {
                    switch (scope.kind) {
                        case 2: {
                            methodScope = (MethodScope)scope;
                            insideStaticContext |= methodScope.isStatic;
                            insideConstructorCall |= methodScope.isConstructorCall;
                            insideTypeAnnotation = methodScope.insideTypeAnnotation;
                        }
                        case 1: {
                            LocalVariableBinding variableBinding = scope.findVariable(name, invocationSite);
                            if (variableBinding == null) break;
                            if (foundField != null && foundField.isValidBinding()) {
                                ProblemFieldBinding problemFieldBinding = new ProblemFieldBinding((FieldBinding)foundField, ((FieldBinding)foundField).declaringClass, name, 5);
                                return problemFieldBinding;
                            }
                            if (depth > 0) {
                                invocationSite.setDepth(depth);
                            }
                            if (shouldTrackOuterLocals) {
                                if (invocationSite instanceof NameReference) {
                                    NameReference nameReference = (NameReference)invocationSite;
                                    nameReference.bits |= 0x80000;
                                } else if (invocationSite instanceof AbstractVariableDeclaration) {
                                    AbstractVariableDeclaration variableDeclaration = (AbstractVariableDeclaration)invocationSite;
                                    variableDeclaration.bits |= 0x200000;
                                }
                            }
                            LocalVariableBinding localVariableBinding = variableBinding;
                            return localVariableBinding;
                        }
                        case 3: {
                            FieldBinding fieldBinding;
                            ClassScope classScope = (ClassScope)scope;
                            ReferenceBinding receiverType = classScope.enclosingReceiverType();
                            if (!insideTypeAnnotation && (fieldBinding = classScope.findField(receiverType, name, invocationSite, needResolve)) != null) {
                                if (fieldBinding.problemId() == 3) {
                                    if (foundField != null && foundField.problemId() != 2) {
                                        ProblemFieldBinding problemFieldBinding = new ProblemFieldBinding((FieldBinding)foundField, ((FieldBinding)foundField).declaringClass, name, 5);
                                        return problemFieldBinding;
                                    }
                                    FieldBinding fieldBinding2 = fieldBinding;
                                    return fieldBinding2;
                                }
                                ProblemFieldBinding insideProblem = null;
                                if (fieldBinding.isValidBinding()) {
                                    if (!fieldBinding.isStatic()) {
                                        if (insideConstructorCall) {
                                            insideProblem = new ProblemFieldBinding(fieldBinding, fieldBinding.declaringClass, name, 6);
                                        } else if (insideStaticContext) {
                                            insideProblem = new ProblemFieldBinding(fieldBinding, fieldBinding.declaringClass, name, 7);
                                        }
                                    }
                                    if (TypeBinding.equalsEquals(receiverType, fieldBinding.declaringClass) || this.compilerOptions().complianceLevel >= 0x300000L) {
                                        if (foundField == null || foundField.problemId() == 2) {
                                            if (depth > 0) {
                                                invocationSite.setDepth(depth);
                                                invocationSite.setActualReceiverType(receiverType);
                                            }
                                            FieldBinding fieldBinding3 = insideProblem == null ? fieldBinding : insideProblem;
                                            return fieldBinding3;
                                        }
                                        if (foundField.isValidBinding() && TypeBinding.notEquals(((FieldBinding)foundField).declaringClass, fieldBinding.declaringClass) && TypeBinding.notEquals(((FieldBinding)foundField).declaringClass, foundActualReceiverType)) {
                                            ProblemFieldBinding problemFieldBinding = new ProblemFieldBinding((FieldBinding)foundField, ((FieldBinding)foundField).declaringClass, name, 5);
                                            return problemFieldBinding;
                                        }
                                    }
                                }
                                if (foundField == null || foundField.problemId() == 2 && fieldBinding.problemId() != 2) {
                                    foundDepth = depth;
                                    foundActualReceiverType = receiverType;
                                    foundInsideProblem = insideProblem;
                                    foundField = fieldBinding;
                                }
                            }
                            insideTypeAnnotation = false;
                            ++depth;
                            shouldTrackOuterLocals = true;
                            insideStaticContext |= receiverType.isStatic();
                            MethodScope enclosingMethodScope = scope.methodScope();
                            insideConstructorCall = enclosingMethodScope == null ? false : enclosingMethodScope.isConstructorCall;
                            break;
                        }
                        case 4: 
                        case 5: {
                            break block27;
                        }
                    }
                    if (scope.isLambdaScope()) {
                        shouldTrackOuterLocals = true;
                    }
                    scope = scope.parent;
                }
                if (foundInsideProblem != null) {
                    ProblemFieldBinding problemFieldBinding = foundInsideProblem;
                    return problemFieldBinding;
                }
                if (foundField != null) {
                    if (foundField.isValidBinding()) {
                        if (foundDepth > 0) {
                            invocationSite.setDepth(foundDepth);
                            invocationSite.setActualReceiverType(foundActualReceiverType);
                        }
                        Binding binding2 = foundField;
                        return binding2;
                    }
                    problemField = foundField;
                    foundField = null;
                }
                if (this.compilerOptions().sourceLevel >= 0x310000L) {
                    unitScope.faultInImports();
                    ImportBinding[] imports = unitScope.imports;
                    if (imports != null) {
                        int i = 0;
                        int length = imports.length;
                        while (i < length) {
                            ImportBinding importBinding = imports[i];
                            if (importBinding.isStatic() && !importBinding.onDemand && CharOperation.equals(importBinding.getSimpleName(), name) && unitScope.resolveSingleImport(importBinding, 13) != null && importBinding.resolvedImport instanceof FieldBinding) {
                                foundField = (FieldBinding)importBinding.resolvedImport;
                                ImportReference importReference = importBinding.reference;
                                if (importReference != null && needResolve) {
                                    importReference.bits |= 2;
                                }
                                invocationSite.setActualReceiverType(((FieldBinding)foundField).declaringClass);
                                if (foundField.isValidBinding()) {
                                    Binding binding3 = foundField;
                                    return binding3;
                                }
                                if (problemField == null) {
                                    problemField = foundField;
                                }
                            }
                            ++i;
                        }
                        boolean foundInImport = false;
                        int i2 = 0;
                        int length2 = imports.length;
                        while (i2 < length2) {
                            FieldBinding temp;
                            Binding resolvedImport;
                            ImportBinding importBinding = imports[i2];
                            if (importBinding.isStatic() && importBinding.onDemand && (resolvedImport = importBinding.resolvedImport) instanceof ReferenceBinding && (temp = this.findField((ReferenceBinding)resolvedImport, name, invocationSite, needResolve)) != null) {
                                if (!temp.isValidBinding()) {
                                    if (problemField == null) {
                                        problemField = temp;
                                    }
                                } else if (temp.isStatic() && foundField != temp) {
                                    ImportReference importReference = importBinding.reference;
                                    if (importReference != null && needResolve) {
                                        importReference.bits |= 2;
                                    }
                                    if (foundInImport) {
                                        ProblemFieldBinding problemFieldBinding = new ProblemFieldBinding((FieldBinding)foundField, ((FieldBinding)foundField).declaringClass, name, 3);
                                        return problemFieldBinding;
                                    }
                                    foundField = temp;
                                    foundInImport = true;
                                }
                            }
                            ++i2;
                        }
                        if (foundField != null) {
                            invocationSite.setActualReceiverType(((FieldBinding)foundField).declaringClass);
                            Binding binding4 = foundField;
                            return binding4;
                        }
                    }
                }
            }
            if ((mask & 4) != 0) {
                binding = Scope.getBaseType(name);
                if (binding != null) {
                    TypeBinding typeBinding = binding;
                    return typeBinding;
                }
                binding = this.getTypeOrPackage(name, (mask & 0x10) == 0 ? 4 : 20, needResolve);
                if (binding.isValidBinding() || mask == 4) {
                    Binding binding5 = binding;
                    return binding5;
                }
            } else if ((mask & 0x10) != 0) {
                unitScope.recordSimpleReference(name);
                binding = env.getTopLevelPackage(name);
                if (binding != null) {
                    Binding binding6 = binding;
                    return binding6;
                }
            }
            if (problemField != null) {
                Binding binding7 = problemField;
                return binding7;
            }
            if (binding != null && binding.problemId() != 1) {
                Binding binding8 = binding;
                return binding8;
            }
            ProblemBinding problemBinding = new ProblemBinding(name, (ReferenceBinding)this.enclosingSourceType(), 1);
            return problemBinding;
        }
        catch (AbortCompilation e) {
            e.updateContext(invocationSite, this.referenceCompilationUnit().compilationResult);
            throw e;
        }
        finally {
            env.missingClassFileLocation = null;
        }
    }

    private MethodBinding getExactMethod(TypeBinding receiverType, TypeBinding type, char[] selector, InvocationSite invocationSite, MethodBinding candidate) {
        if (type == null) {
            return null;
        }
        ReferenceBinding[] superInterfaces = type.superInterfaces();
        TypeBinding[] typePlusSupertypes = new TypeBinding[2 + superInterfaces.length];
        typePlusSupertypes[0] = type;
        typePlusSupertypes[1] = type.superclass();
        if (superInterfaces.length != 0) {
            System.arraycopy(superInterfaces, 0, typePlusSupertypes, 2, superInterfaces.length);
        }
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordTypeReference(type);
        type = type.capture(this, invocationSite.sourceStart(), invocationSite.sourceEnd());
        int i = 0;
        int typesLength = typePlusSupertypes.length;
        while (i < typesLength) {
            MethodBinding[] methodBindingArray;
            if (i == 0) {
                methodBindingArray = type.getMethods(selector);
            } else {
                MethodBinding[] methodBindingArray2 = new MethodBinding[1];
                methodBindingArray = methodBindingArray2;
                methodBindingArray2[0] = this.getExactMethod(receiverType, typePlusSupertypes[i], selector, invocationSite, candidate);
            }
            MethodBinding[] methods = methodBindingArray;
            int j = 0;
            int length = methods.length;
            while (j < length) {
                MethodBinding currentMethod = methods[j];
                if (currentMethod != null && candidate != currentMethod && (i != 0 || currentMethod.canBeSeenBy(receiverType, invocationSite, this) && !currentMethod.isSynthetic() && !currentMethod.isBridge())) {
                    if (candidate != null) {
                        if (!candidate.areParameterErasuresEqual(currentMethod)) {
                            throw new MethodClashException();
                        }
                    } else {
                        candidate = currentMethod;
                    }
                }
                ++j;
            }
            ++i;
        }
        return candidate;
    }

    public MethodBinding getExactMethod(TypeBinding receiverType, char[] selector, InvocationSite invocationSite) {
        if (receiverType == null || !receiverType.isValidBinding() || receiverType.isBaseType()) {
            return null;
        }
        TypeBinding currentType = receiverType;
        if (currentType.isArrayType()) {
            if (!currentType.leafComponentType().canBeSeenBy(this)) {
                return null;
            }
            currentType = this.getJavaLangObject();
        }
        MethodBinding exactMethod = null;
        try {
            exactMethod = this.getExactMethod(receiverType, currentType, selector, invocationSite, null);
        }
        catch (MethodClashException methodClashException) {
            return null;
        }
        if (exactMethod == null || !exactMethod.canBeSeenBy(invocationSite, this)) {
            return null;
        }
        TypeBinding[] typeArguments = invocationSite.genericTypeArguments();
        TypeVariableBinding[] typeVariables = exactMethod.typeVariables();
        if (exactMethod.isVarargs() || typeVariables != Binding.NO_TYPE_VARIABLES && (typeArguments == null || typeArguments.length != typeVariables.length)) {
            return null;
        }
        if (receiverType.isArrayType()) {
            if (CharOperation.equals(selector, TypeConstants.CLONE)) {
                return ((ArrayBinding)receiverType).getCloneMethod(exactMethod);
            }
            if (CharOperation.equals(selector, TypeConstants.GETCLASS)) {
                return this.environment().createGetClassMethod(receiverType, exactMethod, this);
            }
        }
        if (exactMethod.declaringClass.id == 1 && CharOperation.equals(selector, TypeConstants.GETCLASS) && exactMethod.returnType.isParameterizedType()) {
            return this.environment().createGetClassMethod(receiverType, exactMethod, this);
        }
        if (typeVariables != Binding.NO_TYPE_VARIABLES) {
            return this.environment().createParameterizedGenericMethod(exactMethod, typeArguments);
        }
        return exactMethod;
    }

    public MethodBinding getExactConstructor(TypeBinding receiverType, InvocationSite invocationSite) {
        TypeVariableBinding[] typeVariables;
        if (receiverType == null || !receiverType.isValidBinding() || !receiverType.canBeInstantiated() || receiverType.isBaseType()) {
            return null;
        }
        if (receiverType.isArrayType()) {
            TypeBinding leafType = receiverType.leafComponentType();
            if (!leafType.canBeSeenBy(this) || !leafType.isReifiable()) {
                return null;
            }
            return new MethodBinding(4097, TypeConstants.INIT, receiverType, new TypeBinding[]{TypeBinding.INT}, Binding.NO_EXCEPTIONS, this.getJavaLangObject());
        }
        CompilationUnitScope unitScope = this.compilationUnitScope();
        MethodBinding exactConstructor = null;
        unitScope.recordTypeReference(receiverType);
        MethodBinding[] methods = receiverType.getMethods(TypeConstants.INIT);
        TypeBinding[] genericTypeArguments = invocationSite.genericTypeArguments();
        int i = 0;
        int length = methods.length;
        while (i < length) {
            MethodBinding constructor = methods[i];
            if (constructor.canBeSeenBy(invocationSite, this)) {
                if (constructor.isVarargs()) {
                    return null;
                }
                if (constructor.typeVariables() != Binding.NO_TYPE_VARIABLES && genericTypeArguments == null) {
                    return null;
                }
                if (exactConstructor == null) {
                    exactConstructor = constructor;
                } else {
                    return null;
                }
            }
            ++i;
        }
        if (exactConstructor != null && (typeVariables = exactConstructor.typeVariables()) != Binding.NO_TYPE_VARIABLES) {
            if (typeVariables.length != genericTypeArguments.length) {
                return null;
            }
            exactConstructor = this.environment().createParameterizedGenericMethod(exactConstructor, genericTypeArguments);
        }
        return exactConstructor;
    }

    public MethodBinding getConstructor(ReferenceBinding receiverType, TypeBinding[] argumentTypes, InvocationSite invocationSite) {
        TypeBinding elementType;
        MethodBinding method = this.getConstructor0(receiverType, argumentTypes, invocationSite);
        if (method != null && method.isValidBinding() && method.isVarargs() && (elementType = method.parameters[method.parameters.length - 1].leafComponentType()) instanceof ReferenceBinding && !((ReferenceBinding)elementType).canBeSeenBy(this)) {
            return new ProblemMethodBinding(method, method.selector, invocationSite.genericTypeArguments(), 16);
        }
        return method;
    }

    public MethodBinding getConstructor0(ReferenceBinding receiverType, TypeBinding[] argumentTypes, InvocationSite invocationSite) {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        LookupEnvironment env = unitScope.environment;
        try {
            env.missingClassFileLocation = invocationSite;
            unitScope.recordTypeReference(receiverType);
            unitScope.recordTypeReferences(argumentTypes);
            MethodBinding methodBinding = receiverType.getExactConstructor(argumentTypes);
            if (methodBinding != null && methodBinding.canBeSeenBy(invocationSite, this)) {
                if (invocationSite.genericTypeArguments() != null) {
                    methodBinding = this.computeCompatibleMethod(methodBinding, argumentTypes, invocationSite);
                }
                MethodBinding methodBinding2 = methodBinding;
                return methodBinding2;
            }
            MethodBinding[] methods = receiverType.getMethods(TypeConstants.INIT, argumentTypes.length);
            if (methods == Binding.NO_METHODS) {
                ProblemMethodBinding problemMethodBinding = new ProblemMethodBinding(TypeConstants.INIT, argumentTypes, 1);
                return problemMethodBinding;
            }
            MethodBinding[] compatible = new MethodBinding[methods.length];
            int compatibleIndex = 0;
            MethodBinding problemMethod = null;
            int i = 0;
            int length = methods.length;
            while (i < length) {
                MethodBinding compatibleMethod = this.computeCompatibleMethod(methods[i], argumentTypes, invocationSite);
                if (compatibleMethod != null) {
                    if (compatibleMethod.isValidBinding()) {
                        compatible[compatibleIndex++] = compatibleMethod;
                    } else if (problemMethod == null) {
                        problemMethod = compatibleMethod;
                    }
                }
                ++i;
            }
            if (compatibleIndex == 0) {
                if (problemMethod == null) {
                    ProblemMethodBinding problemMethodBinding = new ProblemMethodBinding(methods[0], TypeConstants.INIT, argumentTypes, 1);
                    return problemMethodBinding;
                }
                MethodBinding methodBinding3 = problemMethod;
                return methodBinding3;
            }
            MethodBinding[] visible = new MethodBinding[compatibleIndex];
            int visibleIndex = 0;
            int i2 = 0;
            while (i2 < compatibleIndex) {
                MethodBinding method = compatible[i2];
                if (method.canBeSeenBy(invocationSite, this)) {
                    visible[visibleIndex++] = method;
                }
                ++i2;
            }
            if (visibleIndex == 1) {
                MethodBinding methodBinding4 = visible[0];
                return methodBinding4;
            }
            if (visibleIndex == 0) {
                ProblemMethodBinding problemMethodBinding = new ProblemMethodBinding(compatible[0], TypeConstants.INIT, compatible[0].parameters, 2);
                return problemMethodBinding;
            }
            MethodBinding methodBinding5 = this.mostSpecificMethodBinding(visible, visibleIndex, argumentTypes, invocationSite, receiverType);
            return methodBinding5;
        }
        catch (AbortCompilation e) {
            e.updateContext(invocationSite, this.referenceCompilationUnit().compilationResult);
            throw e;
        }
        finally {
            env.missingClassFileLocation = null;
        }
    }

    public final PackageBinding getCurrentPackage() {
        Scope scope;
        Scope unitScope = this;
        while ((scope = unitScope.parent) != null) {
            unitScope = scope;
        }
        return ((CompilationUnitScope)unitScope).fPackage;
    }

    public int getDeclarationModifiers() {
        switch (this.kind) {
            case 1: 
            case 2: {
                MethodScope methodScope = this.methodScope();
                if (!methodScope.isInsideInitializer()) {
                    MethodBinding context = ((AbstractMethodDeclaration)methodScope.referenceContext).binding;
                    if (context == null) break;
                    return context.modifiers;
                }
                SourceTypeBinding type = ((BlockScope)this).referenceType().binding;
                if (methodScope.initializedField != null) {
                    return methodScope.initializedField.modifiers;
                }
                if (type == null) break;
                return type.modifiers;
            }
            case 5: {
                return ((ModuleScope)this).referenceContext.modifiers;
            }
            case 3: {
                SourceTypeBinding context = ((ClassScope)this).referenceType().binding;
                if (context == null) break;
                return context.modifiers;
            }
        }
        return -1;
    }

    public FieldBinding getField(TypeBinding receiverType, char[] fieldName, InvocationSite invocationSite) {
        LookupEnvironment env = this.environment();
        try {
            env.missingClassFileLocation = invocationSite;
            FieldBinding field = this.findField(receiverType, fieldName, invocationSite, true);
            if (field != null) {
                FieldBinding fieldBinding = field;
                return fieldBinding;
            }
            ProblemFieldBinding problemFieldBinding = new ProblemFieldBinding(receiverType instanceof ReferenceBinding ? (ReferenceBinding)receiverType : null, fieldName, 1);
            return problemFieldBinding;
        }
        catch (AbortCompilation e) {
            e.updateContext(invocationSite, this.referenceCompilationUnit().compilationResult);
            throw e;
        }
        finally {
            env.missingClassFileLocation = null;
        }
    }

    /*
     * Unable to fully structure code
     */
    public MethodBinding getImplicitMethod(char[] selector, TypeBinding[] argumentTypes, InvocationSite invocationSite) {
        insideStaticContext = false;
        insideConstructorCall = false;
        insideTypeAnnotation = false;
        foundMethod = null;
        foundProblem = null;
        foundProblemVisible = false;
        scope = this;
        methodScope = null;
        depth = 0;
        options = this.compilerOptions();
        inheritedHasPrecedence = options.complianceLevel >= 0x300000L;
        block5: while (true) {
            switch (scope.kind) {
                case 2: {
                    methodScope = (MethodScope)scope;
                    insideStaticContext |= methodScope.isStatic;
                    insideConstructorCall |= methodScope.isConstructorCall;
                    insideTypeAnnotation = methodScope.insideTypeAnnotation;
                    ** GOTO lbl71
                }
                case 3: {
                    classScope = (ClassScope)scope;
                    receiverType = classScope.enclosingReceiverType();
                    if (!insideTypeAnnotation) {
                        methodBinding = classScope.findExactMethod(receiverType, selector, argumentTypes, invocationSite);
                        if (methodBinding == null) {
                            methodBinding = classScope.findMethod(receiverType, selector, argumentTypes, invocationSite, false);
                        }
                        if (methodBinding != null) {
                            if (foundMethod == null) {
                                if (methodBinding.isValidBinding()) {
                                    if (!methodBinding.isStatic() && (insideConstructorCall || insideStaticContext)) {
                                        if (foundProblem != null && foundProblem.problemId() != 2) {
                                            return foundProblem;
                                        }
                                        return new ProblemMethodBinding(methodBinding, methodBinding.selector, methodBinding.parameters, insideConstructorCall != false ? 6 : 7);
                                    }
                                    if (!methodBinding.isStatic() && methodScope != null) {
                                        this.tagAsAccessingEnclosingInstanceStateOf(receiverType, false);
                                    }
                                    if (inheritedHasPrecedence || TypeBinding.equalsEquals(receiverType, methodBinding.declaringClass) || receiverType.getMethods(selector) != Binding.NO_METHODS) {
                                        if (foundProblemVisible) {
                                            return foundProblem;
                                        }
                                        if (depth > 0) {
                                            invocationSite.setDepth(depth);
                                            invocationSite.setActualReceiverType(receiverType);
                                        }
                                        if (argumentTypes == Binding.NO_PARAMETERS && CharOperation.equals(selector, TypeConstants.GETCLASS) && methodBinding.returnType.isParameterizedType()) {
                                            return this.environment().createGetClassMethod(receiverType, methodBinding, this);
                                        }
                                        return methodBinding;
                                    }
                                    if (foundProblem == null || foundProblem.problemId() == 2) {
                                        if (foundProblem != null) {
                                            foundProblem = null;
                                        }
                                        if (depth > 0) {
                                            invocationSite.setDepth(depth);
                                            invocationSite.setActualReceiverType(receiverType);
                                        }
                                        foundMethod = methodBinding;
                                    }
                                } else {
                                    if (methodBinding.problemId() != 2 && methodBinding.problemId() != 1) {
                                        return methodBinding;
                                    }
                                    if (foundProblem == null) {
                                        foundProblem = methodBinding;
                                    }
                                    if (!foundProblemVisible && methodBinding.problemId() == 1 && (closestMatch = ((ProblemMethodBinding)methodBinding).closestMatch) != null && closestMatch.canBeSeenBy(receiverType, invocationSite, this)) {
                                        foundProblem = methodBinding;
                                        foundProblemVisible = true;
                                    }
                                }
                            } else if (methodBinding.problemId() == 3 || TypeBinding.notEquals(foundMethod.declaringClass, methodBinding.declaringClass) && (TypeBinding.equalsEquals(receiverType, methodBinding.declaringClass) || receiverType.getMethods(selector) != Binding.NO_METHODS)) {
                                return new ProblemMethodBinding(methodBinding, selector, argumentTypes, 5);
                            }
                        }
                    }
                    insideTypeAnnotation = false;
                    ++depth;
                    insideStaticContext |= receiverType.isStatic();
                    enclosingMethodScope = scope.methodScope();
                    insideConstructorCall = enclosingMethodScope == null ? false : enclosingMethodScope.isConstructorCall;
                    ** GOTO lbl71
                }
                case 4: {
                    break block5;
                }
lbl71:
                // 3 sources

                default: {
                    scope = scope.parent;
                    continue block5;
                }
            }
            break;
        }
        if (insideStaticContext && options.sourceLevel >= 0x310000L) {
            if (foundProblem != null) {
                if (foundProblem.declaringClass != null && foundProblem.declaringClass.id == 1) {
                    return foundProblem;
                }
                if (foundProblem.problemId() == 1 && foundProblemVisible) {
                    return foundProblem;
                }
            }
            unitScope = (CompilationUnitScope)scope;
            unitScope.faultInImports();
            imports = unitScope.imports;
            if (imports != null) {
                visible = null;
                skipOnDemand = false;
                i = 0;
                length = imports.length;
                while (i < length) {
                    importBinding = imports[i];
                    if (importBinding.isStatic()) {
                        resolvedImport = importBinding.resolvedImport;
                        possible = null;
                        if (importBinding.onDemand) {
                            if (!skipOnDemand && resolvedImport instanceof ReferenceBinding) {
                                possible = this.findMethod((ReferenceBinding)resolvedImport, selector, argumentTypes, invocationSite, true);
                            }
                        } else if (resolvedImport instanceof MethodBinding) {
                            staticMethod = (MethodBinding)resolvedImport;
                            if (CharOperation.equals(staticMethod.selector, selector)) {
                                possible = this.findMethod(staticMethod.declaringClass, selector, argumentTypes, invocationSite, true);
                            }
                        } else if (resolvedImport instanceof FieldBinding) {
                            staticField = (FieldBinding)resolvedImport;
                            if (CharOperation.equals(staticField.name, selector) && (referencedType = this.getType(importName = importBinding.reference.tokens, importName.length - 1)) != null) {
                                possible = this.findMethod((ReferenceBinding)referencedType, selector, argumentTypes, invocationSite, true);
                            }
                        }
                        if (possible != null && possible != foundProblem) {
                            if (!possible.isValidBinding()) {
                                if (foundProblem == null) {
                                    foundProblem = possible;
                                }
                            } else if (possible.isStatic()) {
                                compatibleMethod = this.computeCompatibleMethod((MethodBinding)possible, argumentTypes, invocationSite);
                                if (compatibleMethod != null) {
                                    if (compatibleMethod.isValidBinding()) {
                                        if (compatibleMethod.canBeSeenBy(unitScope.fPackage)) {
                                            if (!skipOnDemand && !importBinding.onDemand) {
                                                visible = null;
                                                skipOnDemand = true;
                                            }
                                            if (visible == null || !visible.contains(compatibleMethod)) {
                                                importReference = importBinding.reference;
                                                if (importReference != null) {
                                                    importReference.bits |= 2;
                                                }
                                                if (visible == null) {
                                                    visible = new ObjectVector(3);
                                                }
                                                visible.add(compatibleMethod);
                                            }
                                        } else if (foundProblem == null) {
                                            foundProblem = new ProblemMethodBinding(compatibleMethod, selector, compatibleMethod.parameters, 2);
                                        }
                                    } else if (foundProblem == null) {
                                        foundProblem = compatibleMethod;
                                    }
                                } else if (foundProblem == null) {
                                    foundProblem = new ProblemMethodBinding((MethodBinding)possible, selector, argumentTypes, 1);
                                }
                            }
                        }
                    }
                    ++i;
                }
                if (visible != null) {
                    if (visible.size == 1) {
                        foundMethod = (MethodBinding)visible.elementAt(0);
                    } else {
                        temp = new MethodBinding[visible.size];
                        visible.copyInto(temp);
                        foundMethod = this.mostSpecificMethodBinding((MethodBinding[])temp, temp.length, argumentTypes, invocationSite, null);
                    }
                }
            }
        }
        if (foundMethod != null) {
            invocationSite.setActualReceiverType(foundMethod.declaringClass);
            return foundMethod;
        }
        if (foundProblem != null) {
            return foundProblem;
        }
        return new ProblemMethodBinding(selector, argumentTypes, 1);
    }

    public final ReferenceBinding getJavaIoSerializable() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_IO_SERIALIZABLE);
        return unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_IO_SERIALIZABLE, this);
    }

    public final ReferenceBinding getJavaLangAnnotationAnnotation() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_ANNOTATION_ANNOTATION);
        return unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_ANNOTATION_ANNOTATION, this);
    }

    public final ReferenceBinding getJavaLangAssertionError() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_ASSERTIONERROR);
        return unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_ASSERTIONERROR, this);
    }

    public final ReferenceBinding getJavaLangBoolean() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_BOOLEAN);
        return unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_BOOLEAN, this);
    }

    public final ReferenceBinding getJavaLangByte() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_BYTE);
        return unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_BYTE, this);
    }

    public final ReferenceBinding getJavaLangCharacter() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_CHARACTER);
        return unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_CHARACTER, this);
    }

    public final ReferenceBinding getJavaLangClass() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_CLASS);
        return unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_CLASS, this);
    }

    public final ReferenceBinding getJavaLangCloneable() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_CLONEABLE);
        return unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_CLONEABLE, this);
    }

    public final ReferenceBinding getJavaLangClassNotFoundException() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_CLASSNOTFOUNDEXCEPTION);
        return unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_CLASSNOTFOUNDEXCEPTION, this);
    }

    public final ReferenceBinding getJavaLangDouble() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_DOUBLE);
        return unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_DOUBLE, this);
    }

    public final ReferenceBinding getJavaLangFloat() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_FLOAT);
        return unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_FLOAT, this);
    }

    public final ReferenceBinding getJavaLangIncompatibleClassChangeError() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_INCOMPATIBLECLASSCHANGEERROR);
        return unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_INCOMPATIBLECLASSCHANGEERROR, this);
    }

    public final ReferenceBinding getJavaLangNoClassDefFoundError() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_NOCLASSDEFERROR);
        return unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_NOCLASSDEFERROR, this);
    }

    public final ReferenceBinding getJavaLangNoSuchFieldError() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_NOSUCHFIELDERROR);
        return unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_NOSUCHFIELDERROR, this);
    }

    public final ReferenceBinding getJavaLangEnum() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_ENUM);
        return unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_ENUM, this);
    }

    public final ReferenceBinding getJavaLangError() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_ERROR);
        return unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_ERROR, this);
    }

    public final ReferenceBinding getJavaLangReflectField() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_REFLECT_FIELD);
        return unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_REFLECT_FIELD, this);
    }

    public final ReferenceBinding getJavaLangReflectMethod() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_REFLECT_METHOD);
        return unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_REFLECT_METHOD, this);
    }

    public final ReferenceBinding getJavaLangRuntimeObjectMethods() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_RUNTIME_OBJECTMETHODS);
        return unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_RUNTIME_OBJECTMETHODS, this);
    }

    public final ReferenceBinding getJavaLangInvokeLambdaMetafactory() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_INVOKE_LAMBDAMETAFACTORY);
        return unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_INVOKE_LAMBDAMETAFACTORY, this);
    }

    public final ReferenceBinding getJavaLangInvokeSerializedLambda() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_INVOKE_SERIALIZEDLAMBDA);
        return unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_INVOKE_SERIALIZEDLAMBDA, this);
    }

    public final ReferenceBinding getJavaLangInvokeMethodHandlesLookup() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_INVOKE_METHODHANDLES);
        ReferenceBinding outerType = unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_INVOKE_METHODHANDLES, this);
        return this.findDirectMemberType("Lookup".toCharArray(), outerType);
    }

    public final ReferenceBinding getJavaLangInteger() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_INTEGER);
        return unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_INTEGER, this);
    }

    public final ReferenceBinding getJavaLangIterable() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_ITERABLE);
        return unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_ITERABLE, this);
    }

    public final ReferenceBinding getJavaLangLong() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_LONG);
        return unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_LONG, this);
    }

    public final ReferenceBinding getJavaLangObject() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_OBJECT);
        return unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_OBJECT, this);
    }

    public final ReferenceBinding getJavaLangRecord() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_RECORD);
        return unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_RECORD, this);
    }

    public final ReferenceBinding getJavaLangShort() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_SHORT);
        return unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_SHORT, this);
    }

    public final ReferenceBinding getJavaLangString() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_STRING);
        return unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_STRING, this);
    }

    public final ReferenceBinding getJavaLangStringBuffer() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_STRINGBUFFER);
        return unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_STRINGBUFFER, this);
    }

    public final ReferenceBinding getJavaLangStringBuilder() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_STRINGBUILDER);
        return unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_STRINGBUILDER, this);
    }

    public final ReferenceBinding getJavaLangThrowable() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_THROWABLE);
        return unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_THROWABLE, this);
    }

    public final ReferenceBinding getJavaLangVoid() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_VOID);
        return unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_VOID, this);
    }

    public final ReferenceBinding getJavaLangIllegalArgumentException() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_ILLEGALARGUMENTEXCEPTION);
        return unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_ILLEGALARGUMENTEXCEPTION, this);
    }

    public final ReferenceBinding getJavaUtilIterator() {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(TypeConstants.JAVA_UTIL_ITERATOR);
        return unitScope.environment.getResolvedJavaBaseType(TypeConstants.JAVA_UTIL_ITERATOR, this);
    }

    public final ReferenceBinding getMemberType(char[] typeName, ReferenceBinding enclosingType) {
        ReferenceBinding memberType = this.findMemberType(typeName, enclosingType);
        if (memberType != null) {
            return memberType;
        }
        char[][] compoundName = new char[][]{typeName};
        return new ProblemReferenceBinding(compoundName, null, 1);
    }

    public MethodBinding getMethod(TypeBinding receiverType, char[] selector, TypeBinding[] argumentTypes, InvocationSite invocationSite) {
        CompilationUnitScope unitScope = this.compilationUnitScope();
        LookupEnvironment env = unitScope.environment;
        try {
            env.missingClassFileLocation = invocationSite;
            switch (receiverType.kind()) {
                case 132: {
                    ProblemMethodBinding problemMethodBinding = new ProblemMethodBinding(selector, argumentTypes, 1);
                    return problemMethodBinding;
                }
                case 68: {
                    unitScope.recordTypeReference(receiverType);
                    MethodBinding methodBinding = this.findMethodForArray((ArrayBinding)receiverType, selector, argumentTypes, invocationSite);
                    return methodBinding;
                }
            }
            unitScope.recordTypeReference(receiverType);
            ReferenceBinding currentType = (ReferenceBinding)receiverType;
            if (!currentType.canBeSeenBy(this)) {
                ProblemMethodBinding problemMethodBinding = new ProblemMethodBinding(selector, argumentTypes, 8);
                return problemMethodBinding;
            }
            MethodBinding methodBinding = this.findExactMethod(currentType, selector, argumentTypes, invocationSite);
            if (methodBinding != null && methodBinding.isValidBinding()) {
                MethodBinding methodBinding2 = methodBinding;
                return methodBinding2;
            }
            methodBinding = this.findMethod(currentType, selector, argumentTypes, invocationSite, false);
            if (methodBinding == null) {
                ProblemMethodBinding problemMethodBinding = new ProblemMethodBinding(selector, argumentTypes, 1);
                return problemMethodBinding;
            }
            if (!methodBinding.isValidBinding()) {
                MethodBinding methodBinding3 = methodBinding;
                return methodBinding3;
            }
            if (argumentTypes == Binding.NO_PARAMETERS && CharOperation.equals(selector, TypeConstants.GETCLASS) && methodBinding.returnType.isParameterizedType()) {
                ParameterizedMethodBinding parameterizedMethodBinding = this.environment().createGetClassMethod(receiverType, methodBinding, this);
                return parameterizedMethodBinding;
            }
            MethodBinding methodBinding4 = methodBinding;
            return methodBinding4;
        }
        catch (AbortCompilation e) {
            e.updateContext(invocationSite, this.referenceCompilationUnit().compilationResult);
            throw e;
        }
        finally {
            env.missingClassFileLocation = null;
        }
    }

    public final Binding getPackage(char[][] compoundName) {
        this.compilationUnitScope().recordQualifiedReference(compoundName);
        Binding binding = this.getTypeOrPackage(compoundName[0], 20, true);
        if (binding == null) {
            char[][] qName = new char[][]{compoundName[0]};
            return new ProblemReferenceBinding(qName, this.environment().createMissingType(null, compoundName), 1);
        }
        if (!binding.isValidBinding()) {
            if (binding instanceof PackageBinding) {
                char[][] qName = new char[][]{compoundName[0]};
                return new ProblemReferenceBinding(qName, null, 1);
            }
            return this.problemType(compoundName, -1, binding);
        }
        if (!(binding instanceof PackageBinding)) {
            return null;
        }
        int currentIndex = 1;
        int length = compoundName.length;
        PackageBinding packageBinding = (PackageBinding)binding;
        while (currentIndex < length) {
            if ((binding = packageBinding.getTypeOrPackage(compoundName[currentIndex++], this.module(), currentIndex < length)) == null) {
                return this.problemType(compoundName, currentIndex, null);
            }
            if (!binding.isValidBinding() && binding.problemId() != 3) {
                return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), binding instanceof ReferenceBinding ? (ReferenceBinding)((ReferenceBinding)binding).closestMatch() : null, binding.problemId());
            }
            if (!(binding instanceof PackageBinding)) {
                return packageBinding;
            }
            packageBinding = (PackageBinding)binding;
        }
        return new ProblemReferenceBinding(compoundName, null, 1);
    }

    Binding problemType(char[][] compoundName, int currentIndex, Binding previousProblem) {
        ReferenceBinding notAccessibleType;
        if (previousProblem != null && previousProblem.problemId() != 1) {
            return previousProblem;
        }
        LookupEnvironment environment = this.environment();
        if (environment.useModuleSystem && this.module() != environment.UnNamedModule && (notAccessibleType = environment.root.getType(compoundName, environment.UnNamedModule)) != null && notAccessibleType.isValidBinding()) {
            return new ProblemReferenceBinding(compoundName, notAccessibleType, 30);
        }
        return previousProblem != null ? previousProblem : new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), null, 1);
    }

    public final Binding getOnlyPackage(char[][] compoundName) {
        this.compilationUnitScope().recordQualifiedReference(compoundName);
        Binding binding = this.getTypeOrPackage(compoundName[0], 16, true);
        if (binding == null || !binding.isValidBinding()) {
            char[][] qName = new char[][]{compoundName[0]};
            return new ProblemReferenceBinding(qName, null, 1);
        }
        if (!(binding instanceof PackageBinding)) {
            return null;
        }
        int currentIndex = 1;
        int length = compoundName.length;
        PackageBinding packageBinding = (PackageBinding)binding;
        while (currentIndex < length) {
            if ((binding = packageBinding.getPackage(compoundName[currentIndex++], this.module())) == null) {
                return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), null, 1);
            }
            if (!binding.isValidBinding()) {
                return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), binding instanceof ReferenceBinding ? (ReferenceBinding)((ReferenceBinding)binding).closestMatch() : null, binding.problemId());
            }
            packageBinding = (PackageBinding)binding;
        }
        return packageBinding;
    }

    public final TypeBinding getType(char[] name) {
        TypeBinding binding = Scope.getBaseType(name);
        if (binding != null) {
            return binding;
        }
        return (ReferenceBinding)this.getTypeOrPackage(name, 4, true);
    }

    public final TypeBinding getType(char[] name, PackageBinding packageBinding) {
        if (packageBinding == null) {
            return this.getType(name);
        }
        Binding binding = packageBinding.getTypeOrPackage(name, this.module(), false);
        if (binding == null) {
            return new ProblemReferenceBinding(CharOperation.arrayConcat(packageBinding.compoundName, name), null, 1);
        }
        if (!binding.isValidBinding()) {
            return new ProblemReferenceBinding(binding instanceof ReferenceBinding ? ((ReferenceBinding)binding).compoundName : CharOperation.arrayConcat(packageBinding.compoundName, name), binding instanceof ReferenceBinding ? (ReferenceBinding)((ReferenceBinding)binding).closestMatch() : null, binding.problemId());
        }
        ReferenceBinding typeBinding = (ReferenceBinding)binding;
        if (!typeBinding.canBeSeenBy(this)) {
            return new ProblemReferenceBinding(typeBinding.compoundName, typeBinding, 2);
        }
        return typeBinding;
    }

    /*
     * Unable to fully structure code
     */
    public final TypeBinding getType(char[][] compoundName, int typeNameLength) {
        if (typeNameLength == 1 && (binding = Scope.getBaseType(compoundName[0])) != null) {
            return binding;
        }
        unitScope = this.compilationUnitScope();
        unitScope.recordQualifiedReference(compoundName);
        binding = this.getTypeOrPackage(compoundName[0], typeNameLength == 1 ? 4 : 20, true);
        if (binding == null) {
            qName = new char[][]{compoundName[0]};
            return new ProblemReferenceBinding(qName, this.environment().createMissingType(this.compilationUnitScope().getCurrentPackage(), qName), 1);
        }
        if (!binding.isValidBinding()) {
            if (binding instanceof PackageBinding) {
                qName = new char[][]{compoundName[0]};
                return new ProblemReferenceBinding(qName, this.environment().createMissingType(null, qName), 1);
            }
            return (ReferenceBinding)binding;
        }
        currentIndex = 1;
        checkVisibility = false;
        if (binding instanceof PackageBinding) {
            packageBinding = (PackageBinding)binding;
            while (currentIndex < typeNameLength) {
                if ((binding = packageBinding.getTypeOrPackage(compoundName[currentIndex++], this.module(), currentIndex < typeNameLength)) == null) {
                    qName = CharOperation.subarray(compoundName, 0, currentIndex);
                    return new ProblemReferenceBinding(qName, this.environment().createMissingType(packageBinding, qName), 1);
                }
                if (!binding.isValidBinding()) {
                    return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), binding instanceof ReferenceBinding != false ? (ReferenceBinding)((ReferenceBinding)binding).closestMatch() : null, binding.problemId());
                }
                if (!(binding instanceof PackageBinding)) break;
                packageBinding = (PackageBinding)binding;
            }
            if (binding instanceof PackageBinding) {
                qName = CharOperation.subarray(compoundName, 0, currentIndex);
                return new ProblemReferenceBinding(qName, this.environment().createMissingType(null, qName), 1);
            }
            checkVisibility = true;
        }
        typeBinding = (ReferenceBinding)binding;
        unitScope.recordTypeReference(typeBinding);
        if (!checkVisibility || typeBinding.canBeSeenBy(this)) ** GOTO lbl40
        return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), typeBinding, 2);
lbl-1000:
        // 1 sources

        {
            if ((typeBinding = this.getMemberType(compoundName[currentIndex++], typeBinding)).isValidBinding()) continue;
            if (typeBinding instanceof ProblemReferenceBinding) {
                problemBinding = (ProblemReferenceBinding)typeBinding;
                return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), problemBinding.closestReferenceMatch(), typeBinding.problemId());
            }
            return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), (ReferenceBinding)((ReferenceBinding)binding).closestMatch(), typeBinding.problemId());
lbl40:
            // 2 sources

            ** while (currentIndex < typeNameLength)
        }
lbl41:
        // 1 sources

        return typeBinding;
    }

    /*
     * Handled impossible loop by adding 'first' condition
     * Enabled aggressive block sorting
     */
    final Binding getTypeOrPackage(char[] name, int mask, boolean needResolve) {
        char[][] qName;
        PackageBinding packageBinding;
        Binding cachedBinding;
        Scope scope = this;
        MethodScope methodScope = null;
        Binding foundType = null;
        boolean insideStaticContext = false;
        boolean insideClassContext = false;
        boolean insideTypeAnnotation = false;
        if ((mask & 4) == 0) {
            Scope next = scope;
            boolean bl = true;
            do {
                if (bl && !(bl = false) && true) continue;
                scope = next;
            } while ((next = scope.parent) != null);
        } else {
            boolean inheritedHasPrecedence = this.compilerOptions().complianceLevel >= 0x300000L;
            block7: while (true) {
                switch (scope.kind) {
                    case 2: {
                        methodScope = (MethodScope)scope;
                        AbstractMethodDeclaration methodDecl = methodScope.referenceMethod();
                        if (methodDecl != null) {
                            if (methodDecl.binding != null) {
                                TypeVariableBinding typeVariable = methodDecl.binding.getTypeVariable(name);
                                if (typeVariable != null) {
                                    if (!insideStaticContext) return typeVariable;
                                    if (!insideClassContext) return typeVariable;
                                    return new ProblemReferenceBinding(new char[][]{name}, typeVariable, 7);
                                }
                            } else {
                                int i;
                                TypeParameter[] params = methodDecl.typeParameters();
                                int n = i = params == null ? 0 : params.length;
                                while (--i >= 0) {
                                    if (!CharOperation.equals(params[i].name, name) || params[i].binding == null || !params[i].binding.isValidBinding()) continue;
                                    return params[i].binding;
                                }
                            }
                        }
                        insideStaticContext |= methodScope.isStatic;
                        insideTypeAnnotation = methodScope.insideTypeAnnotation;
                    }
                    case 1: {
                        ReferenceBinding localType = ((BlockScope)scope).findLocalType(name);
                        if (localType == null) break;
                        if (foundType == null) return localType;
                        if (!TypeBinding.notEquals((TypeBinding)foundType, localType)) return localType;
                        return new ProblemReferenceBinding(new char[][]{name}, (ReferenceBinding)foundType, 5);
                    }
                    case 3: {
                        ReferenceBinding memberType;
                        TypeVariableBinding typeVariable;
                        SourceTypeBinding sourceType = ((ClassScope)scope).referenceContext.binding;
                        if (scope == this && (sourceType.tagBits & 0x40000L) == 0L) {
                            typeVariable = sourceType.getTypeVariable(name);
                            if (typeVariable != null) {
                                return typeVariable;
                            }
                            if (CharOperation.equals(name, sourceType.sourceName)) {
                                return sourceType;
                            }
                            insideStaticContext |= sourceType.isStatic();
                            break;
                        }
                        if (!insideTypeAnnotation && (memberType = this.findMemberType(name, sourceType)) != null) {
                            if (memberType.problemId() == 3) {
                                if (foundType == null) return memberType;
                                if (foundType.problemId() == 2) {
                                    return memberType;
                                }
                                return new ProblemReferenceBinding(new char[][]{name}, (ReferenceBinding)foundType, 5);
                            }
                            if (memberType.isValidBinding() && (TypeBinding.equalsEquals(sourceType, memberType.enclosingType()) || inheritedHasPrecedence)) {
                                if (insideStaticContext && !memberType.isStatic() && sourceType.isGenericType()) {
                                    return new ProblemReferenceBinding(new char[][]{name}, memberType, 7);
                                }
                                if (foundType == null) return memberType;
                                if (inheritedHasPrecedence && foundType.problemId() == 2) {
                                    return memberType;
                                }
                                if (foundType.isValidBinding() && TypeBinding.notEquals((TypeBinding)foundType, memberType)) {
                                    return new ProblemReferenceBinding(new char[][]{name}, (ReferenceBinding)foundType, 5);
                                }
                            }
                            if (foundType == null || foundType.problemId() == 2 && memberType.problemId() != 2) {
                                foundType = memberType;
                            }
                        }
                        if ((typeVariable = sourceType.getTypeVariable(name)) != null) {
                            if (!insideStaticContext) return typeVariable;
                            return new ProblemReferenceBinding(new char[][]{name}, typeVariable, 7);
                        }
                        insideStaticContext |= sourceType.isStatic();
                        insideClassContext = !sourceType.isAnonymousType();
                        insideTypeAnnotation = false;
                        if (!CharOperation.equals(sourceType.sourceName, name)) break;
                        if (foundType == null) return sourceType;
                        if (!TypeBinding.notEquals((TypeBinding)foundType, sourceType)) return sourceType;
                        if (foundType.problemId() == 2) return sourceType;
                        return new ProblemReferenceBinding(new char[][]{name}, (ReferenceBinding)foundType, 5);
                    }
                    case 4: {
                        break block7;
                    }
                }
                scope = scope.parent;
            }
            if (foundType != null && foundType.problemId() != 2) {
                return foundType;
            }
        }
        CompilationUnitScope unitScope = (CompilationUnitScope)scope;
        HashtableOfObject typeOrPackageCache = unitScope.typeOrPackageCache;
        if (typeOrPackageCache != null && (cachedBinding = (Binding)typeOrPackageCache.get(name)) != null) {
            if (cachedBinding instanceof ImportBinding) {
                ImportBinding importBinding = (ImportBinding)cachedBinding;
                ImportReference importReference = importBinding.reference;
                if (importReference != null && !this.isUnnecessarySamePackageImport(importBinding.resolvedImport, unitScope)) {
                    importReference.bits |= 2;
                }
                if (cachedBinding instanceof ImportConflictBinding) {
                    cachedBinding = ((ImportConflictBinding)cachedBinding).conflictingTypeBinding;
                    typeOrPackageCache.put(name, cachedBinding);
                } else {
                    cachedBinding = importBinding.resolvedImport;
                    typeOrPackageCache.put(name, cachedBinding);
                }
            }
            if ((mask & 4) != 0) {
                if (foundType != null && foundType.problemId() != 2 && cachedBinding.problemId() != 3) {
                    return foundType;
                }
                if (cachedBinding instanceof ReferenceBinding) {
                    return cachedBinding;
                }
            }
            if ((mask & 0x10) != 0 && cachedBinding instanceof PackageBinding) {
                return cachedBinding;
            }
        }
        if ((mask & 4) != 0) {
            ImportBinding[] imports = unitScope.imports;
            if (imports != null && typeOrPackageCache == null) {
                int i = 0;
                int length = imports.length;
                while (i < length) {
                    Binding resolvedImport;
                    ImportBinding importBinding = imports[i];
                    if (!importBinding.onDemand && CharOperation.equals(importBinding.getSimpleName(), name) && (resolvedImport = unitScope.resolveSingleImport(importBinding, 4)) != null && resolvedImport instanceof TypeBinding) {
                        ImportReference importReference = importBinding.reference;
                        if (importReference == null) return resolvedImport;
                        if (this.isUnnecessarySamePackageImport(importBinding.resolvedImport, unitScope)) return resolvedImport;
                        importReference.bits |= 2;
                        return resolvedImport;
                    }
                    ++i;
                }
            }
            PlainPackageBinding currentPackage = unitScope.fPackage;
            unitScope.recordReference(currentPackage.compoundName, name);
            Binding binding = currentPackage.getTypeOrPackage(name, this.module(), false);
            if (binding instanceof ReferenceBinding) {
                ReferenceBinding referenceType = (ReferenceBinding)binding;
                if ((referenceType.tagBits & 0x80L) == 0L) {
                    if (typeOrPackageCache == null) return referenceType;
                    typeOrPackageCache.put(name, referenceType);
                    return referenceType;
                }
            }
            if (imports != null) {
                boolean foundInImport = false;
                ReferenceBinding type = null;
                int i = 0;
                int length = imports.length;
                while (i < length) {
                    ImportBinding someImport = imports[i];
                    if (someImport.onDemand) {
                        Binding resolvedImport = someImport.resolvedImport;
                        ReferenceBinding temp = null;
                        if (resolvedImport instanceof PackageBinding) {
                            temp = this.findType(name, (PackageBinding)resolvedImport, currentPackage);
                        } else if (someImport.isStatic()) {
                            temp = this.compilationUnitScope().findMemberType(name, (ReferenceBinding)resolvedImport);
                            if (temp != null && !temp.isStatic()) {
                                temp = null;
                            }
                        } else {
                            temp = this.compilationUnitScope().findDirectMemberType(name, (ReferenceBinding)resolvedImport);
                        }
                        if (TypeBinding.notEquals(temp, type) && temp != null) {
                            if (temp.isValidBinding()) {
                                ImportReference importReference = someImport.reference;
                                if (importReference != null) {
                                    importReference.bits |= 2;
                                }
                                if (foundInImport) {
                                    temp = new ProblemReferenceBinding(new char[][]{name}, type, 3);
                                    if (typeOrPackageCache == null) return temp;
                                    typeOrPackageCache.put(name, temp);
                                    return temp;
                                }
                                type = temp;
                                foundInImport = true;
                            } else if (foundType == null) {
                                foundType = temp;
                            }
                        }
                    }
                    ++i;
                }
                if (type != null) {
                    if (typeOrPackageCache == null) return type;
                    typeOrPackageCache.put(name, type);
                    return type;
                }
            }
        }
        unitScope.recordSimpleReference(name);
        if ((mask & 0x10) != 0 && (packageBinding = unitScope.environment.getTopLevelPackage(name)) != null && (packageBinding.tagBits & 0x80L) == 0L) {
            if (typeOrPackageCache == null) return packageBinding;
            typeOrPackageCache.put(name, packageBinding);
            return packageBinding;
        }
        if (foundType != null) {
            if ((((ReferenceBinding)foundType).tagBits & 0x80L) == 0L) return foundType;
            qName = new char[][]{name};
            foundType = new ProblemReferenceBinding(qName, (ReferenceBinding)foundType, 1);
            if (typeOrPackageCache == null) return foundType;
            if ((mask & 0x10) == 0) return foundType;
            typeOrPackageCache.put(name, foundType);
            return foundType;
        }
        qName = new char[][]{name};
        MissingTypeBinding closestMatch = null;
        if ((mask & 0x10) != 0) {
            if (needResolve) {
                closestMatch = this.environment().createMissingType(unitScope.fPackage, qName);
            }
        } else {
            PackageBinding packageBinding2 = unitScope.environment.getTopLevelPackage(name);
            if ((packageBinding2 == null || !packageBinding2.isValidBinding()) && needResolve) {
                closestMatch = this.environment().createMissingType(unitScope.fPackage, qName);
            }
        }
        foundType = new ProblemReferenceBinding(qName, closestMatch, 1);
        if (typeOrPackageCache == null) return foundType;
        if ((mask & 0x10) == 0) return foundType;
        typeOrPackageCache.put(name, foundType);
        return foundType;
    }

    private boolean isUnnecessarySamePackageImport(Binding resolvedImport, Scope unitScope) {
        if (resolvedImport instanceof ReferenceBinding) {
            ReferenceBinding referenceBinding = (ReferenceBinding)resolvedImport;
            if (unitScope.getCurrentPackage() == referenceBinding.getPackage()) {
                return !referenceBinding.isNestedType();
            }
        }
        return false;
    }

    /*
     * Unable to fully structure code
     */
    public final Binding getTypeOrPackage(char[][] compoundName) {
        nameLength = compoundName.length;
        if (nameLength == 1 && (binding = Scope.getBaseType(compoundName[0])) != null) {
            return binding;
        }
        binding = this.getTypeOrPackage(compoundName[0], 20, true);
        if (!binding.isValidBinding()) {
            return binding;
        }
        currentIndex = 1;
        checkVisibility = false;
        if (binding instanceof PackageBinding) {
            packageBinding = (PackageBinding)binding;
            while (currentIndex < nameLength) {
                if ((binding = packageBinding.getTypeOrPackage(compoundName[currentIndex++], this.module(), currentIndex < nameLength)) == null) {
                    return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), null, 1);
                }
                if (!binding.isValidBinding()) {
                    return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), binding instanceof ReferenceBinding != false ? (ReferenceBinding)((ReferenceBinding)binding).closestMatch() : null, binding.problemId());
                }
                if (!(binding instanceof PackageBinding)) break;
                packageBinding = (PackageBinding)binding;
            }
            if (binding instanceof PackageBinding) {
                return binding;
            }
            checkVisibility = true;
        }
        typeBinding = (ReferenceBinding)binding;
        qualifiedType = (ReferenceBinding)this.environment().convertToRawType(typeBinding, false);
        if (!checkVisibility || typeBinding.canBeSeenBy(this)) ** GOTO lbl29
        return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), typeBinding, 2);
lbl-1000:
        // 1 sources

        {
            if (!(typeBinding = this.getMemberType(compoundName[currentIndex++], typeBinding)).isValidBinding()) {
                return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), (ReferenceBinding)typeBinding.closestMatch(), typeBinding.problemId());
            }
            qualifiedType = typeBinding.isGenericType() != false ? this.environment().createRawType(typeBinding, qualifiedType) : this.environment().maybeCreateParameterizedType(typeBinding, qualifiedType);
lbl29:
            // 2 sources

            ** while (currentIndex < nameLength)
        }
lbl30:
        // 1 sources

        return qualifiedType;
    }

    public boolean hasErasedCandidatesCollisions(TypeBinding one, TypeBinding two, Map invocations, ReferenceBinding type, ASTNode typeRef) {
        invocations.clear();
        TypeBinding[] mecs = this.minimalErasedCandidates(new TypeBinding[]{one, two}, invocations);
        if (mecs != null) {
            int k = 0;
            int max = mecs.length;
            while (k < max) {
                Object value;
                TypeBinding mec = mecs[k];
                if (mec != null && (value = invocations.get(mec)) instanceof TypeBinding[]) {
                    TypeBinding[] invalidInvocations = (TypeBinding[])value;
                    this.problemReporter().superinterfacesCollide(invalidInvocations[0].erasure(), typeRef, invalidInvocations[0], invalidInvocations[1]);
                    type.tagBits |= 0x20000L;
                    return true;
                }
                ++k;
            }
        }
        return false;
    }

    public CaseStatement innermostSwitchCase() {
        Scope scope = this;
        do {
            if (!(scope instanceof BlockScope)) continue;
            return ((BlockScope)scope).enclosingCase;
        } while ((scope = scope.parent) != null);
        return null;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     */
    protected boolean isAcceptableMethod(MethodBinding one, MethodBinding two) {
        block19: {
            block18: {
                oneParams = one.parameters;
                oneParamsLength = oneParams.length;
                twoParams = two.parameters;
                twoParamsLength = twoParams.length;
                if (oneParamsLength != twoParamsLength) break block18;
                applyErasure = this.environment().globalOptions.sourceLevel < 0x310000L;
                i = 0;
                if (true) ** GOTO lbl51
            }
            if (!one.isVarargs() || !two.isVarargs()) break block19;
            if (CompilerOptions.tolerateIllegalAmbiguousVarargsInvocation && this.compilerOptions().complianceLevel < 0x330000L && oneParamsLength > twoParamsLength && ((ArrayBinding)twoParams[twoParamsLength - 1]).elementsType().id != 1) {
                return false;
            }
            i = (oneParamsLength > twoParamsLength ? twoParamsLength : oneParamsLength) - 2;
            if (true) ** GOTO lbl57
            do {
                block21: {
                    block20: {
                        oneParam = applyErasure != false ? oneParams[i].erasure() : oneParams[i];
                        v0 = twoParam = applyErasure != false ? twoParams[i].erasure() : twoParams[i];
                        if (!TypeBinding.equalsEquals(oneParam, twoParam) && !oneParam.isCompatibleWith(twoParam)) break block20;
                        if (two.declaringClass.isRawType()) break block21;
                        leafComponentType = two.original().parameters[i].leafComponentType();
                        originalTwoParam = applyErasure != false ? leafComponentType.erasure() : leafComponentType;
                        switch (originalTwoParam.kind()) {
                            case 4100: {
                                if (((TypeVariableBinding)originalTwoParam).hasOnlyRawBounds()) ** break;
                            }
                            case 260: 
                            case 516: 
                            case 8196: {
                                originalOneParam = one.original().parameters[i].leafComponentType();
                                switch (originalOneParam.kind()) {
                                    case 4: 
                                    case 2052: {
                                        inheritedTwoParam = oneParam.findSuperTypeOriginatingFrom(twoParam);
                                        if (inheritedTwoParam != null && inheritedTwoParam.leafComponentType().isRawType()) {
                                            return false;
                                        }
                                        break block21;
                                    }
                                    case 4100: {
                                        if (((TypeVariableBinding)originalOneParam).upperBound().isRawType()) {
                                            return false;
                                        }
                                        break block21;
                                    }
                                    case 1028: {
                                        return false;
                                    }
                                }
                            }
                            {
                                ** break;
                            }
                        }
lbl41:
                        // 2 sources

                        break block21;
                    }
                    if (i == oneParamsLength - 1 && one.isVarargs() && two.isVarargs()) {
                        oType = ((ArrayBinding)oneParam).elementsType();
                        eType = ((ArrayBinding)twoParam).elementsType();
                        if (CompilerOptions.tolerateIllegalAmbiguousVarargsInvocation != false && this.compilerOptions().complianceLevel < 0x330000L ? TypeBinding.equalsEquals(oneParam, eType) != false || oneParam.isCompatibleWith(eType) != false : TypeBinding.equalsEquals(oType, eType) != false || oType.isCompatibleWith(eType) != false) {
                            return true;
                        }
                    }
                    return false;
                }
                ++i;
lbl51:
                // 2 sources

            } while (i < oneParamsLength);
            return true;
            do {
                if (TypeBinding.notEquals(oneParams[i], twoParams[i]) && !oneParams[i].isCompatibleWith(twoParams[i])) {
                    return false;
                }
                --i;
lbl57:
                // 2 sources

            } while (i >= 0);
            if (this.parameterCompatibilityLevel(one, twoParams, true) == -1 && this.parameterCompatibilityLevel(two, oneParams, true) == 2) {
                return true;
            }
        }
        return false;
    }

    public boolean isBoxingCompatibleWith(TypeBinding expressionType, TypeBinding targetType) {
        LookupEnvironment environment = this.environment();
        if (environment.globalOptions.sourceLevel < 0x310000L || expressionType.isBaseType() == targetType.isBaseType()) {
            return false;
        }
        TypeBinding convertedType = environment.computeBoxingType(expressionType);
        return TypeBinding.equalsEquals(convertedType, targetType) || convertedType.isCompatibleWith(targetType, this);
    }

    public final boolean isDefinedInField(FieldBinding field) {
        Scope scope = this;
        do {
            if (!(scope instanceof MethodScope)) continue;
            MethodScope methodScope = (MethodScope)scope;
            if (methodScope.initializedField != field) continue;
            return true;
        } while ((scope = scope.parent) != null);
        return false;
    }

    public final boolean isDefinedInMethod(MethodBinding method) {
        method = method.original();
        Scope scope = this;
        do {
            ReferenceContext refContext;
            if (!(scope instanceof MethodScope) || !((refContext = ((MethodScope)scope).referenceContext) instanceof AbstractMethodDeclaration) || ((AbstractMethodDeclaration)refContext).binding != method) continue;
            return true;
        } while ((scope = scope.parent) != null);
        return false;
    }

    public final boolean isDefinedInSameUnit(ReferenceBinding type) {
        Scope scope;
        ReferenceBinding enclosingType = type;
        while ((type = enclosingType.enclosingType()) != null) {
            enclosingType = type;
        }
        Scope unitScope = this;
        while ((scope = unitScope.parent) != null) {
            unitScope = scope;
        }
        SourceTypeBinding[] topLevelTypes = ((CompilationUnitScope)unitScope).topLevelTypes;
        int i = topLevelTypes.length;
        while (--i >= 0) {
            if (!TypeBinding.equalsEquals(topLevelTypes[i], enclosingType.original())) continue;
            return true;
        }
        return false;
    }

    public final boolean isDefinedInType(ReferenceBinding type) {
        Scope scope = this;
        do {
            if (!(scope instanceof ClassScope) || !TypeBinding.equalsEquals(((ClassScope)scope).referenceContext.binding, type)) continue;
            return true;
        } while ((scope = scope.parent) != null);
        return false;
    }

    public boolean isInsideCase(CaseStatement caseStatement) {
        Scope scope = this;
        do {
            switch (scope.kind) {
                case 1: {
                    if (((BlockScope)scope).enclosingCase != caseStatement) break;
                    return true;
                }
            }
        } while ((scope = scope.parent) != null);
        return false;
    }

    public boolean isInsideDeprecatedCode() {
        switch (this.kind) {
            case 1: 
            case 2: {
                SourceTypeBinding declaringType;
                MethodScope methodScope = this.methodScope();
                if (!methodScope.isInsideInitializer()) {
                    ReferenceContext referenceContext = methodScope.referenceContext();
                    if (referenceContext instanceof AbstractMethodDeclaration) {
                        MethodBinding context = ((AbstractMethodDeclaration)referenceContext).binding;
                        if (context != null && context.isViewedAsDeprecated()) {
                            return true;
                        }
                    } else if (referenceContext instanceof LambdaExpression) {
                        MethodBinding context = ((LambdaExpression)referenceContext).binding;
                        if (context != null && context.isViewedAsDeprecated()) {
                            return true;
                        }
                    } else if (referenceContext instanceof ModuleDeclaration) {
                        SourceModuleBinding context = ((ModuleDeclaration)referenceContext).binding;
                        return context != null && context.isDeprecated();
                    }
                } else if (methodScope.initializedField != null && methodScope.initializedField.isViewedAsDeprecated()) {
                    return true;
                }
                if ((declaringType = ((BlockScope)this).referenceType().binding) == null) break;
                declaringType.initializeDeprecatedAnnotationTagBits();
                if (!declaringType.isViewedAsDeprecated()) break;
                return true;
            }
            case 3: {
                SourceTypeBinding context = ((ClassScope)this).referenceType().binding;
                if (context == null) break;
                ((Binding)context).initializeDeprecatedAnnotationTagBits();
                if (!context.isViewedAsDeprecated()) break;
                return true;
            }
            case 4: {
                SourceTypeBinding type;
                CompilationUnitDeclaration unit = this.referenceCompilationUnit();
                if (unit.types == null || unit.types.length <= 0 || (type = unit.types[0].binding) == null) break;
                type.initializeDeprecatedAnnotationTagBits();
                if (!type.isViewedAsDeprecated()) break;
                return true;
            }
        }
        return false;
    }

    private boolean isOverriddenMethodGeneric(MethodBinding method) {
        MethodVerifier verifier = this.environment().methodVerifier();
        ReferenceBinding currentType = method.declaringClass.superclass();
        while (currentType != null) {
            MethodBinding[] currentMethods = currentType.getMethods(method.selector);
            int i = 0;
            int l = currentMethods.length;
            while (i < l) {
                MethodBinding currentMethod = currentMethods[i];
                if (currentMethod != null && currentMethod.original().typeVariables != Binding.NO_TYPE_VARIABLES && verifier.doesMethodOverride(method, currentMethod)) {
                    return true;
                }
                ++i;
            }
            currentType = currentType.superclass();
        }
        return false;
    }

    public boolean isSubtypeOfRawType(TypeBinding paramType) {
        TypeBinding t = paramType.leafComponentType();
        if (t.isBaseType()) {
            return false;
        }
        ReferenceBinding currentType = (ReferenceBinding)t;
        ReferenceBinding[] interfacesToVisit = null;
        int nextPosition = 0;
        do {
            if (currentType.isRawType()) {
                return true;
            }
            ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
            if (itsInterfaces == null || itsInterfaces == Binding.NO_SUPERINTERFACES) continue;
            if (interfacesToVisit == null) {
                interfacesToVisit = itsInterfaces;
                nextPosition = interfacesToVisit.length;
                continue;
            }
            int itsLength = itsInterfaces.length;
            if (nextPosition + itsLength >= interfacesToVisit.length) {
                ReferenceBinding[] referenceBindingArray = interfacesToVisit;
                interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5];
                System.arraycopy(referenceBindingArray, 0, interfacesToVisit, 0, nextPosition);
            }
            int a = 0;
            while (a < itsLength) {
                block15: {
                    ReferenceBinding next = itsInterfaces[a];
                    int b = 0;
                    while (b < nextPosition) {
                        if (!TypeBinding.equalsEquals(next, interfacesToVisit[b])) {
                            ++b;
                            continue;
                        }
                        break block15;
                    }
                    interfacesToVisit[nextPosition++] = next;
                }
                ++a;
            }
        } while ((currentType = currentType.superclass()) != null);
        int i = 0;
        while (i < nextPosition) {
            currentType = interfacesToVisit[i];
            if (currentType.isRawType()) {
                return true;
            }
            ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
            if (itsInterfaces != null && itsInterfaces != Binding.NO_SUPERINTERFACES) {
                int itsLength = itsInterfaces.length;
                if (nextPosition + itsLength >= interfacesToVisit.length) {
                    ReferenceBinding[] referenceBindingArray = interfacesToVisit;
                    interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5];
                    System.arraycopy(referenceBindingArray, 0, interfacesToVisit, 0, nextPosition);
                }
                int a = 0;
                while (a < itsLength) {
                    block16: {
                        ReferenceBinding next = itsInterfaces[a];
                        int b = 0;
                        while (b < nextPosition) {
                            if (!TypeBinding.equalsEquals(next, interfacesToVisit[b])) {
                                ++b;
                                continue;
                            }
                            break block16;
                        }
                        interfacesToVisit[nextPosition++] = next;
                    }
                    ++a;
                }
            }
            ++i;
        }
        return false;
    }

    private TypeBinding leastContainingInvocation(TypeBinding mec, Object invocationData, ArrayList lubStack) {
        if (invocationData == null) {
            return mec;
        }
        if (invocationData instanceof TypeBinding) {
            return (TypeBinding)invocationData;
        }
        TypeBinding[] invocations = (TypeBinding[])invocationData;
        int dim = mec.dimensions();
        int argLength = (mec = mec.leafComponentType()).typeVariables().length;
        if (argLength == 0) {
            return mec;
        }
        TypeBinding[] bestArguments = new TypeBinding[argLength];
        int i = 0;
        int length = invocations.length;
        while (i < length) {
            TypeBinding invocation = invocations[i].leafComponentType();
            switch (invocation.kind()) {
                case 2052: {
                    TypeVariableBinding[] invocationVariables = invocation.typeVariables();
                    int j = 0;
                    while (j < argLength) {
                        TypeBinding bestArgument = this.leastContainingTypeArgument(bestArguments[j], invocationVariables[j], (ReferenceBinding)mec, j, (ArrayList)lubStack.clone());
                        if (bestArgument == null) {
                            return null;
                        }
                        bestArguments[j] = bestArgument;
                        ++j;
                    }
                    break;
                }
                case 260: {
                    ParameterizedTypeBinding parameterizedType = (ParameterizedTypeBinding)invocation;
                    int j = 0;
                    while (j < argLength) {
                        TypeBinding bestArgument = this.leastContainingTypeArgument(bestArguments[j], parameterizedType.arguments[j], (ReferenceBinding)mec, j, (ArrayList)lubStack.clone());
                        if (bestArgument == null) {
                            return null;
                        }
                        bestArguments[j] = bestArgument;
                        ++j;
                    }
                    break;
                }
                case 1028: {
                    return dim == 0 ? invocation : this.environment().createArrayType(invocation, dim);
                }
            }
            ++i;
        }
        ParameterizedTypeBinding least = this.environment().createParameterizedType((ReferenceBinding)mec.erasure(), bestArguments, mec.enclosingType());
        return dim == 0 ? least : this.environment().createArrayType(least, dim);
    }

    private TypeBinding leastContainingTypeArgument(TypeBinding u, TypeBinding v, ReferenceBinding genericType, int rank, ArrayList lubStack) {
        block34: {
            block32: {
                WildcardBinding wildV;
                block33: {
                    if (u == null) {
                        return v;
                    }
                    if (TypeBinding.equalsEquals(u, v)) {
                        return u;
                    }
                    if (!v.isWildcard()) break block32;
                    wildV = (WildcardBinding)v;
                    if (!u.isWildcard()) break block33;
                    WildcardBinding wildU = (WildcardBinding)u;
                    switch (wildU.boundKind) {
                        case 1: {
                            switch (wildV.boundKind) {
                                case 1: {
                                    TypeBinding lub = this.lowerUpperBound(new TypeBinding[]{wildU.bound, wildV.bound}, lubStack);
                                    if (lub == null) {
                                        return null;
                                    }
                                    if (TypeBinding.equalsEquals(lub, TypeBinding.INT)) {
                                        return this.environment().createWildcard(genericType, rank, null, null, 0);
                                    }
                                    return this.environment().createWildcard(genericType, rank, lub, null, 1);
                                }
                                case 2: {
                                    if (TypeBinding.equalsEquals(wildU.bound, wildV.bound)) {
                                        return wildU.bound;
                                    }
                                    return this.environment().createWildcard(genericType, rank, null, null, 0);
                                }
                            }
                            break;
                        }
                        case 2: {
                            if (wildU.boundKind == 2) {
                                TypeBinding[] glb = Scope.greaterLowerBound(new TypeBinding[]{wildU.bound, wildV.bound}, this, this.environment());
                                if (glb == null) {
                                    return null;
                                }
                                return this.environment().createWildcard(genericType, rank, glb[0], null, 2);
                            } else {
                                break;
                            }
                        }
                    }
                    break block34;
                }
                switch (wildV.boundKind) {
                    case 1: {
                        TypeBinding lub = this.lowerUpperBound(new TypeBinding[]{u, wildV.bound}, lubStack);
                        if (lub == null) {
                            return null;
                        }
                        if (TypeBinding.equalsEquals(lub, TypeBinding.INT)) {
                            return this.environment().createWildcard(genericType, rank, null, null, 0);
                        }
                        return this.environment().createWildcard(genericType, rank, lub, null, 1);
                    }
                    case 2: {
                        TypeBinding[] glb = Scope.greaterLowerBound(new TypeBinding[]{u, wildV.bound}, this, this.environment());
                        if (glb == null) {
                            return null;
                        }
                        return this.environment().createWildcard(genericType, rank, glb[0], null, 2);
                    }
                }
                break block34;
            }
            if (u.isWildcard()) {
                WildcardBinding wildU = (WildcardBinding)u;
                switch (wildU.boundKind) {
                    case 1: {
                        TypeBinding lub = this.lowerUpperBound(new TypeBinding[]{wildU.bound, v}, lubStack);
                        if (lub == null) {
                            return null;
                        }
                        if (TypeBinding.equalsEquals(lub, TypeBinding.INT)) {
                            return this.environment().createWildcard(genericType, rank, null, null, 0);
                        }
                        return this.environment().createWildcard(genericType, rank, lub, null, 1);
                    }
                    case 2: {
                        TypeBinding[] glb = Scope.greaterLowerBound(new TypeBinding[]{wildU.bound, v}, this, this.environment());
                        if (glb == null) {
                            return null;
                        }
                        return this.environment().createWildcard(genericType, rank, glb[0], null, 2);
                    }
                }
            }
        }
        TypeBinding lub = this.lowerUpperBound(new TypeBinding[]{u, v}, lubStack);
        if (lub == null) {
            return null;
        }
        if (TypeBinding.equalsEquals(lub, TypeBinding.INT)) {
            return this.environment().createWildcard(genericType, rank, null, null, 0);
        }
        return this.environment().createWildcard(genericType, rank, lub, null, 1);
    }

    public TypeBinding lowerUpperBound(TypeBinding[] types) {
        int typeLength = types.length;
        if (typeLength == 1) {
            TypeBinding type = types[0];
            return type == null ? TypeBinding.VOID : type;
        }
        return this.lowerUpperBound(types, new ArrayList(1));
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    private TypeBinding lowerUpperBound(TypeBinding[] types, ArrayList lubStack) {
        typeLength = types.length;
        if (typeLength == 1) {
            type = types[0];
            return type == null ? TypeBinding.VOID : type;
        }
        stackLength = lubStack.size();
        i = 0;
        while (i < stackLength) {
            block24: {
                lubTypes = (TypeBinding[])lubStack.get(i);
                lubTypeLength = lubTypes.length;
                if (lubTypeLength < typeLength) break block24;
                j = 0;
                block6: while (j < typeLength) {
                    type = types[j];
                    if (type == null) ** GOTO lbl21
                    k = 0;
                    while (k < lubTypeLength) {
                        lubType = lubTypes[k];
                        if (lubType == null || !TypeBinding.equalsEquals(lubType, type) && !lubType.isEquivalentTo(type)) {
                            ++k;
                            continue;
                        }
lbl21:
                        // 3 sources

                        ++j;
                        continue block6;
                    }
                    break block24;
                }
                return TypeBinding.INT;
            }
            ++i;
        }
        lubStack.add(types);
        invocations = new HashMap<K, V>(1);
        mecs = this.minimalErasedCandidates(types, invocations);
        if (mecs == null) {
            return null;
        }
        length = mecs.length;
        if (length == 0) {
            return TypeBinding.VOID;
        }
        count = 0;
        firstBound = null;
        commonDim = -1;
        i = 0;
        while (i < length) {
            mec = mecs[i];
            if (mec != null) {
                if ((mec = this.leastContainingInvocation(mec, invocations.get(mec), lubStack)) == null) {
                    return null;
                }
                dim = mec.dimensions();
                if (commonDim == -1) {
                    commonDim = dim;
                } else if (dim != commonDim) {
                    return null;
                }
                if (firstBound == null && !mec.leafComponentType().isInterface()) {
                    firstBound = mec.leafComponentType();
                }
                mecs[count++] = mec;
            }
            ++i;
        }
        switch (count) {
            case 0: {
                return TypeBinding.VOID;
            }
            case 1: {
                return mecs[0];
            }
            case 2: {
                if ((commonDim == 0 ? mecs[1].id : mecs[1].leafComponentType().id) == 1) {
                    return mecs[0];
                }
                if ((commonDim == 0 ? mecs[0].id : mecs[0].leafComponentType().id) != 1) break;
                return mecs[1];
            }
        }
        otherBounds = new TypeBinding[count - 1];
        rank = 0;
        i = 0;
        while (i < count) {
            v0 = mec = commonDim == 0 ? mecs[i] : mecs[i].leafComponentType();
            if (mec.isInterface()) {
                otherBounds[rank++] = mec;
            }
            ++i;
        }
        if (this.environment().globalOptions.complianceLevel < 0x340000L) {
            intersectionType /* !! */  = this.environment().createWildcard(null, 0, firstBound, otherBounds, 1);
        } else {
            intersectingTypes = new ReferenceBinding[otherBounds.length + 1];
            intersectingTypes[0] = (ReferenceBinding)firstBound;
            System.arraycopy(otherBounds, 0, intersectingTypes, 1, otherBounds.length);
            intersectionType /* !! */  = this.environment().createIntersectionType18(intersectingTypes);
        }
        return commonDim == 0 ? intersectionType /* !! */  : this.environment().createArrayType(intersectionType /* !! */ , commonDim);
    }

    public final MethodScope methodScope() {
        Scope scope = this;
        do {
            if (!(scope instanceof MethodScope)) continue;
            return (MethodScope)scope;
        } while ((scope = scope.parent) != null);
        return null;
    }

    public final MethodScope namedMethodScope() {
        Scope scope = this;
        do {
            if (!(scope instanceof MethodScope) || scope.isLambdaScope()) continue;
            return (MethodScope)scope;
        } while ((scope = scope.parent) != null);
        return null;
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    protected TypeBinding[] minimalErasedCandidates(TypeBinding[] types, Map allInvocations) {
        length = types.length;
        indexOfFirst = -1;
        actualLength = 0;
        i = 0;
        while (i < length) {
            type = types[i];
            if (type == TypeBinding.NULL) {
                type = null;
                types[i] = null;
            }
            if (type != null) {
                if (type.isBaseType()) {
                    return null;
                }
                if (indexOfFirst < 0) {
                    indexOfFirst = i;
                }
                ++actualLength;
            }
            ++i;
        }
        switch (actualLength) {
            case 0: {
                return Binding.NO_TYPES;
            }
            case 1: {
                return types;
            }
        }
        firstType = types[indexOfFirst];
        if (firstType.isBaseType()) {
            return null;
        }
        typesToVisit = new ArrayList<TypeBinding>(5);
        dim = firstType.dimensions();
        leafType = firstType.leafComponentType();
        switch (leafType.kind()) {
            case 68: 
            case 260: 
            case 1028: {
                firstErasure = firstType.erasure();
                break;
            }
            default: {
                firstErasure = firstType;
            }
        }
        if (TypeBinding.notEquals(firstErasure, firstType)) {
            allInvocations.put(firstErasure, firstType);
        }
        typesToVisit.add(firstType);
        max = 1;
        i = 0;
        while (i < max) {
            block71: {
                block70: {
                    typeToVisit = (TypeBinding)typesToVisit.get(i);
                    dim = typeToVisit.dimensions();
                    if (dim <= 0) break block70;
                    leafType = typeToVisit.leafComponentType();
                    switch (leafType.id) {
                        case 1: {
                            if (dim <= 1) ** GOTO lbl56
                            elementType = ((ArrayBinding)typeToVisit).elementsType();
                            if (!typesToVisit.contains(elementType)) {
                                typesToVisit.add(elementType);
                                ++max;
                            }
                            break block71;
                        }
lbl56:
                        // 2 sources

                        case 2: 
                        case 3: 
                        case 4: 
                        case 5: 
                        case 7: 
                        case 8: 
                        case 9: 
                        case 10: {
                            superType = this.getJavaIoSerializable();
                            if (!typesToVisit.contains(superType)) {
                                typesToVisit.add(superType);
                                ++max;
                            }
                            if (!typesToVisit.contains(superType = this.getJavaLangCloneable())) {
                                typesToVisit.add(superType);
                                ++max;
                            }
                            if (!typesToVisit.contains(superType = this.getJavaLangObject())) {
                                typesToVisit.add(superType);
                                ++max;
                            }
                            break block71;
                        }
                        default: {
                            typeToVisit = leafType;
                        }
                    }
                }
                if ((currentType = (ReferenceBinding)typeToVisit).isCapture() && (firstBound = ((CaptureBinding)currentType).firstBound) != null && firstBound.isArrayType()) {
                    v0 = superType = dim == 0 ? firstBound : this.environment().createArrayType(firstBound, dim);
                    if (!typesToVisit.contains(superType)) {
                        typesToVisit.add(superType);
                        ++max;
                        v1 = superTypeErasure = firstBound.isTypeVariable() != false || firstBound.isWildcard() != false ? superType : superType.erasure();
                        if (TypeBinding.notEquals(superTypeErasure, superType)) {
                            allInvocations.put(superTypeErasure, superType);
                        }
                    }
                } else {
                    itsInterfaces = currentType.superInterfaces();
                    if (itsInterfaces != null) {
                        j = 0;
                        count = itsInterfaces.length;
                        while (j < count) {
                            itsInterface = itsInterfaces[j];
                            v2 /* !! */  = superType = dim == 0 ? itsInterface : this.environment().createArrayType(itsInterface, dim);
                            if (!typesToVisit.contains(superType)) {
                                typesToVisit.add(superType);
                                ++max;
                                v3 /* !! */  = superTypeErasure = itsInterface.isTypeVariable() != false || itsInterface.isWildcard() != false ? superType : superType.erasure();
                                if (TypeBinding.notEquals(superTypeErasure, superType)) {
                                    allInvocations.put(superTypeErasure, superType);
                                }
                            }
                            ++j;
                        }
                    }
                    if ((itsSuperclass = currentType.superclass()) != null) {
                        v4 /* !! */  = superType = dim == 0 ? itsSuperclass : this.environment().createArrayType(itsSuperclass, dim);
                        if (!typesToVisit.contains(superType)) {
                            typesToVisit.add(superType);
                            ++max;
                            v5 /* !! */  = superTypeErasure = itsSuperclass.isTypeVariable() != false || itsSuperclass.isWildcard() != false ? superType : superType.erasure();
                            if (TypeBinding.notEquals(superTypeErasure, superType)) {
                                allInvocations.put(superTypeErasure, superType);
                            }
                        }
                    }
                }
            }
            ++i;
        }
        superLength = typesToVisit.size();
        erasedSuperTypes = new TypeBinding[superLength];
        rank = 0;
        for (TypeBinding type : typesToVisit) {
            leafType = type.leafComponentType();
            v6 = erasedSuperTypes[rank++] = leafType.isTypeVariable() != false || leafType.isWildcard() != false ? type : type.erasure();
        }
        remaining = superLength;
        i = indexOfFirst + 1;
        while (i < length) {
            block72: {
                block73: {
                    otherType = types[i];
                    if (otherType == null) break block72;
                    if (!otherType.isArrayType()) break block73;
                    j = 0;
                    while (j < superLength) {
                        block68: {
                            erasedSuperType = erasedSuperTypes[j];
                            if (erasedSuperType != null && !TypeBinding.equalsEquals(erasedSuperType, otherType)) {
                                match /* !! */  = otherType.findSuperTypeOriginatingFrom(erasedSuperType);
                                if (match /* !! */  == null) {
                                    erasedSuperTypes[j] = null;
                                    if (--remaining == 0) {
                                        return null;
                                    }
                                } else {
                                    invocationData = allInvocations.get(erasedSuperType);
                                    if (invocationData == null) {
                                        allInvocations.put(erasedSuperType, match /* !! */ );
                                    } else if (invocationData instanceof TypeBinding) {
                                        if (TypeBinding.notEquals(match /* !! */ , (TypeBinding)invocationData)) {
                                            someInvocations = new TypeBinding[]{(TypeBinding)invocationData, match /* !! */ };
                                            allInvocations.put(erasedSuperType, someInvocations);
                                        }
                                    } else {
                                        someInvocations = (TypeBinding[])invocationData;
                                        invocLength = someInvocations.length;
                                        k = 0;
                                        while (k < invocLength) {
                                            if (!TypeBinding.equalsEquals(someInvocations[k], match /* !! */ )) {
                                                ++k;
                                                continue;
                                            }
                                            break block68;
                                        }
                                        v7 = someInvocations;
                                        someInvocations = new TypeBinding[invocLength + 1];
                                        System.arraycopy(v7, 0, someInvocations, 0, invocLength);
                                        allInvocations.put(erasedSuperType, someInvocations);
                                        someInvocations[invocLength] = match /* !! */ ;
                                    }
                                }
                            }
                        }
                        ++j;
                    }
                    break block72;
                }
                j = 0;
                while (j < superLength) {
                    block69: {
                        block74: {
                            erasedSuperType = erasedSuperTypes[j];
                            if (erasedSuperType == null) break block69;
                            if (!TypeBinding.equalsEquals(erasedSuperType, otherType) && (erasedSuperType.id != 1 || !otherType.isInterface())) break block74;
                            match /* !! */  = erasedSuperType;
                            ** GOTO lbl-1000
                        }
                        match /* !! */  = erasedSuperType.isArrayType() != false ? null : otherType.findSuperTypeOriginatingFrom(erasedSuperType);
                        if (match /* !! */  == null) {
                            erasedSuperTypes[j] = null;
                            if (--remaining == 0) {
                                return null;
                            }
                        } else lbl-1000:
                        // 2 sources

                        {
                            invocationData = allInvocations.get(erasedSuperType);
                            if (invocationData == null) {
                                allInvocations.put(erasedSuperType, match /* !! */ );
                            } else if (invocationData instanceof TypeBinding) {
                                if (TypeBinding.notEquals(match /* !! */ , (TypeBinding)invocationData)) {
                                    someInvocations = new TypeBinding[]{(TypeBinding)invocationData, match /* !! */ };
                                    allInvocations.put(erasedSuperType, someInvocations);
                                }
                            } else {
                                someInvocations = (TypeBinding[])invocationData;
                                invocLength = someInvocations.length;
                                k = 0;
                                while (k < invocLength) {
                                    if (!TypeBinding.equalsEquals(someInvocations[k], match /* !! */ )) {
                                        ++k;
                                        continue;
                                    }
                                    break block69;
                                }
                                v8 = someInvocations;
                                someInvocations = new TypeBinding[invocLength + 1];
                                System.arraycopy(v8, 0, someInvocations, 0, invocLength);
                                allInvocations.put(erasedSuperType, someInvocations);
                                someInvocations[invocLength] = match /* !! */ ;
                            }
                        }
                    }
                    ++j;
                }
            }
            ++i;
        }
        if (remaining > 1) {
            i = 0;
            while (i < superLength) {
                erasedSuperType = erasedSuperTypes[i];
                if (erasedSuperType != null) {
                    j = 0;
                    while (j < superLength) {
                        if (i != j && (otherType = erasedSuperTypes[j]) != null) {
                            if (erasedSuperType instanceof ReferenceBinding) {
                                if (!(otherType.id == 1 && erasedSuperType.isInterface() || erasedSuperType.findSuperTypeOriginatingFrom(otherType) == null)) {
                                    erasedSuperTypes[j] = null;
                                    --remaining;
                                }
                            } else if (!(!erasedSuperType.isArrayType() || otherType.isArrayType() && otherType.leafComponentType().id == 1 && otherType.dimensions() == erasedSuperType.dimensions() && erasedSuperType.leafComponentType().isInterface() || erasedSuperType.findSuperTypeOriginatingFrom(otherType) == null)) {
                                erasedSuperTypes[j] = null;
                                --remaining;
                            }
                        }
                        ++j;
                    }
                }
                ++i;
            }
        }
        return erasedSuperTypes;
    }

    protected final MethodBinding mostSpecificClassMethodBinding(MethodBinding[] visible, int visibleSize, InvocationSite invocationSite) {
        MethodBinding previous = null;
        int i = 0;
        while (i < visibleSize) {
            block4: {
                MethodBinding method = visible[i];
                if (previous != null && TypeBinding.notEquals(method.declaringClass, previous.declaringClass)) break;
                if (!method.isStatic()) {
                    previous = method;
                }
                int j = 0;
                while (j < visibleSize) {
                    if (i == j || visible[j].areParametersCompatibleWith(method.parameters)) {
                        ++j;
                        continue;
                    }
                    break block4;
                }
                this.compilationUnitScope().recordTypeReferences(method.thrownExceptions);
                return method;
            }
            ++i;
        }
        return new ProblemMethodBinding(visible[0], visible[0].selector, visible[0].parameters, 3);
    }

    protected final MethodBinding mostSpecificInterfaceMethodBinding(MethodBinding[] visible, int visibleSize, InvocationSite invocationSite) {
        int i = 0;
        while (i < visibleSize) {
            block3: {
                MethodBinding method = visible[i];
                int j = 0;
                while (j < visibleSize) {
                    if (i == j || visible[j].areParametersCompatibleWith(method.parameters)) {
                        ++j;
                        continue;
                    }
                    break block3;
                }
                this.compilationUnitScope().recordTypeReferences(method.thrownExceptions);
                return method;
            }
            ++i;
        }
        return new ProblemMethodBinding(visible[0], visible[0].selector, visible[0].parameters, 3);
    }

    protected final MethodBinding mostSpecificMethodBinding(MethodBinding[] visible, int visibleSize, TypeBinding[] argumentTypes, final InvocationSite invocationSite, ReferenceBinding receiverType) {
        boolean isJdk18;
        boolean bl = isJdk18 = this.compilerOptions().sourceLevel >= 0x340000L;
        if (isJdk18 && invocationSite.checkingPotentialCompatibility()) {
            if (visibleSize != visible.length) {
                MethodBinding[] methodBindingArray = visible;
                visible = new MethodBinding[visibleSize];
                System.arraycopy(methodBindingArray, 0, visible, 0, visibleSize);
            }
            invocationSite.acceptPotentiallyCompatibleMethods(visible);
        }
        int[] compatibilityLevels = new int[visibleSize];
        int compatibleCount = 0;
        int i = 0;
        while (i < visibleSize) {
            compatibilityLevels[i] = this.parameterCompatibilityLevel(visible[i], argumentTypes, invocationSite);
            if (compatibilityLevels[i] != -1) {
                if (i != compatibleCount) {
                    visible[compatibleCount] = visible[i];
                    compatibilityLevels[compatibleCount] = compatibilityLevels[i];
                }
                ++compatibleCount;
            }
            ++i;
        }
        if (compatibleCount == 0) {
            return new ProblemMethodBinding(visible[0].selector, argumentTypes, 1);
        }
        if (compatibleCount == 1) {
            MethodBinding candidate = visible[0];
            if (candidate != null) {
                this.compilationUnitScope().recordTypeReferences(candidate.thrownExceptions);
            }
            return candidate;
        }
        if (compatibleCount != visibleSize) {
            MethodBinding[] methodBindingArray = visible;
            visibleSize = compatibleCount;
            visible = new MethodBinding[visibleSize];
            System.arraycopy(methodBindingArray, 0, visible, 0, compatibleCount);
            int[] nArray = compatibilityLevels;
            compatibilityLevels = new int[compatibleCount];
            System.arraycopy(nArray, 0, compatibilityLevels, 0, compatibleCount);
        }
        MethodBinding[] moreSpecific = new MethodBinding[visibleSize];
        if (isJdk18) {
            int count = 0;
            int j = 0;
            while (j < visibleSize) {
                block71: {
                    MethodBinding mbj = visible[j].genericMethod();
                    TypeBinding[] mbjParameters = mbj.parameters;
                    int levelj = compatibilityLevels[j];
                    int k = 0;
                    while (k < visibleSize) {
                        if (j != k) {
                            int levelk = compatibilityLevels[k];
                            if (levelj > -1 && levelk > -1 && levelj != levelk) {
                                if (levelj >= levelk) break block71;
                            } else {
                                MethodBinding mbk = visible[k].genericMethod();
                                TypeBinding[] mbkParameters = mbk.parameters;
                                if ((invocationSite instanceof Invocation || invocationSite instanceof ReferenceExpression) && mbk.typeVariables() != Binding.NO_TYPE_VARIABLES) {
                                    Expression[] expressions = null;
                                    expressions = invocationSite instanceof Invocation ? ((Invocation)invocationSite).arguments() : ((ReferenceExpression)invocationSite).createPseudoExpressions(argumentTypes);
                                    InferenceContext18 ic18 = new InferenceContext18(this, expressions, null, null);
                                    if (!ic18.isMoreSpecificThan(mbj, mbk, levelj == 2, levelk == 2)) {
                                        break block71;
                                    }
                                } else {
                                    TypeBinding t;
                                    TypeBinding s;
                                    int i2 = 0;
                                    int length = argumentTypes.length;
                                    while (i2 < length) {
                                        TypeBinding t2;
                                        TypeBinding argumentType = argumentTypes[i2];
                                        TypeBinding s2 = InferenceContext18.getParameter(mbjParameters, i2, levelj == 2);
                                        if (TypeBinding.equalsEquals(s2, t2 = InferenceContext18.getParameter(mbkParameters, i2, levelk == 2)) || argumentType.sIsMoreSpecific(s2, t2, this)) {
                                            ++i2;
                                            continue;
                                        }
                                        break block71;
                                    }
                                    if (levelj == 2 && levelk == 2 && TypeBinding.notEquals(s = InferenceContext18.getParameter(mbjParameters, argumentTypes.length, true), t = InferenceContext18.getParameter(mbkParameters, argumentTypes.length, true)) && t.isSubtypeOf(s, false)) break block71;
                                }
                            }
                        }
                        ++k;
                    }
                    moreSpecific[count++] = visible[j];
                }
                ++j;
            }
            if (count == 0) {
                return new ProblemMethodBinding(visible[0], visible[0].selector, visible[0].parameters, 3);
            }
            if (count == 1) {
                MethodBinding candidate = moreSpecific[0];
                if (candidate != null) {
                    this.compilationUnitScope().recordTypeReferences(candidate.thrownExceptions);
                }
                return candidate;
            }
            visibleSize = count;
        } else {
            InvocationSite tieBreakInvocationSite = new InvocationSite(){

                @Override
                public TypeBinding[] genericTypeArguments() {
                    return null;
                }

                @Override
                public boolean isSuperAccess() {
                    return invocationSite.isSuperAccess();
                }

                @Override
                public boolean isTypeAccess() {
                    return invocationSite.isTypeAccess();
                }

                @Override
                public void setActualReceiverType(ReferenceBinding actualReceiverType) {
                }

                @Override
                public void setDepth(int depth) {
                }

                @Override
                public void setFieldIndex(int depth) {
                }

                @Override
                public int sourceStart() {
                    return invocationSite.sourceStart();
                }

                @Override
                public int sourceEnd() {
                    return invocationSite.sourceStart();
                }

                @Override
                public TypeBinding invocationTargetType() {
                    return invocationSite.invocationTargetType();
                }

                @Override
                public boolean receiverIsImplicitThis() {
                    return invocationSite.receiverIsImplicitThis();
                }

                @Override
                public InferenceContext18 freshInferenceContext(Scope scope) {
                    return null;
                }

                @Override
                public ExpressionContext getExpressionContext() {
                    return ExpressionContext.VANILLA_CONTEXT;
                }

                @Override
                public boolean isQualifiedSuper() {
                    return invocationSite.isQualifiedSuper();
                }

                @Override
                public boolean checkingPotentialCompatibility() {
                    return false;
                }

                @Override
                public void acceptPotentiallyCompatibleMethods(MethodBinding[] methods) {
                }
            };
            int count = 0;
            int level = 0;
            int max = 2;
            while (level <= max) {
                int i3 = 0;
                while (i3 < visibleSize) {
                    block72: {
                        if (compatibilityLevels[i3] == level) {
                            max = level;
                            MethodBinding current = visible[i3];
                            MethodBinding original = current.original();
                            MethodBinding tiebreakMethod = current.tiebreakMethod();
                            int j = 0;
                            while (j < visibleSize) {
                                if (i3 != j && compatibilityLevels[j] == level) {
                                    MethodBinding next = visible[j];
                                    if (original == next.original()) {
                                        compatibilityLevels[j] = -1;
                                    } else {
                                        MethodBinding acceptable;
                                        MethodBinding methodToTest = next;
                                        if (next instanceof ParameterizedGenericMethodBinding) {
                                            ParameterizedGenericMethodBinding pNext = (ParameterizedGenericMethodBinding)next;
                                            if (!pNext.isRaw || pNext.isStatic()) {
                                                methodToTest = pNext.originalMethod;
                                            }
                                        }
                                        if ((acceptable = this.computeCompatibleMethod(methodToTest, tiebreakMethod.parameters, tieBreakInvocationSite, level == 2)) == null || !acceptable.isValidBinding() || !this.isAcceptableMethod(tiebreakMethod, acceptable) || current.isBridge() && !next.isBridge() && tiebreakMethod.areParametersEqual(acceptable)) break block72;
                                    }
                                }
                                ++j;
                            }
                            moreSpecific[i3] = current;
                            ++count;
                        }
                    }
                    ++i3;
                }
                ++level;
            }
            if (count == 1) {
                int i4 = 0;
                while (i4 < visibleSize) {
                    if (moreSpecific[i4] != null) {
                        MethodBinding candidate = visible[i4];
                        if (candidate != null) {
                            this.compilationUnitScope().recordTypeReferences(candidate.thrownExceptions);
                        }
                        return candidate;
                    }
                    ++i4;
                }
            } else if (count == 0) {
                return new ProblemMethodBinding(visible[0], visible[0].selector, visible[0].parameters, 3);
            }
        }
        if (receiverType != null) {
            receiverType = receiverType instanceof CaptureBinding ? receiverType : (ReferenceBinding)receiverType.erasure();
        }
        int i5 = 0;
        block8: while (i5 < visibleSize) {
            block73: {
                MethodBinding current = moreSpecific[i5];
                if (current != null) {
                    Object[] mostSpecificExceptions = null;
                    MethodBinding original = current.original();
                    boolean shouldIntersectExceptions = original.declaringClass.isAbstract() && original.thrownExceptions != Binding.NO_EXCEPTIONS;
                    int j = 0;
                    while (j < visibleSize) {
                        MethodBinding next = moreSpecific[j];
                        if (next != null && i5 != j) {
                            MethodBinding original2 = next.original();
                            if (TypeBinding.equalsEquals(original.declaringClass, original2.declaringClass)) break block8;
                            if (!original.isAbstract()) {
                                if (!(original2.isAbstract() || original2.isDefaultMethod() || (original2 = original.findOriginalInheritedMethod(original2)) != null && (!current.hasSubstitutedParameters() && original.typeVariables == Binding.NO_TYPE_VARIABLES || this.environment().methodVerifier().isParameterSubsignature(original, original2)))) {
                                    break block73;
                                }
                            } else if (receiverType != null) {
                                int l;
                                TypeBinding superType = receiverType.findSuperTypeOriginatingFrom(original.declaringClass.erasure());
                                if (!TypeBinding.equalsEquals(original.declaringClass, superType) && superType instanceof ReferenceBinding) {
                                    MethodBinding[] superMethods = ((ReferenceBinding)superType).getMethods(original.selector, argumentTypes.length);
                                    int m = 0;
                                    l = superMethods.length;
                                    while (m < l) {
                                        if (superMethods[m].original() == original) {
                                            original = superMethods[m];
                                            break;
                                        }
                                        ++m;
                                    }
                                }
                                if (!TypeBinding.equalsEquals(original2.declaringClass, superType = receiverType.findSuperTypeOriginatingFrom(original2.declaringClass.erasure())) && superType instanceof ReferenceBinding) {
                                    MethodBinding[] superMethods = ((ReferenceBinding)superType).getMethods(original2.selector, argumentTypes.length);
                                    int m = 0;
                                    l = superMethods.length;
                                    while (m < l) {
                                        if (superMethods[m].original() == original2) {
                                            original2 = superMethods[m];
                                            break;
                                        }
                                        ++m;
                                    }
                                }
                                if (original.typeVariables != Binding.NO_TYPE_VARIABLES) {
                                    original2 = original.computeSubstitutedMethod(original2, this.environment());
                                }
                                if (original2 == null || !original.areParameterErasuresEqual(original2) || TypeBinding.notEquals(original.returnType, original2.returnType) && (next.original().typeVariables == Binding.NO_TYPE_VARIABLES ? !current.returnType.isCompatibleWith(next.returnType) : original.returnType.erasure().findSuperTypeOriginatingFrom(original2.returnType.erasure()) == null)) break block73;
                                if (shouldIntersectExceptions && original2.declaringClass.isInterface() && current.thrownExceptions != next.thrownExceptions) {
                                    if (next.thrownExceptions == Binding.NO_EXCEPTIONS) {
                                        mostSpecificExceptions = Binding.NO_EXCEPTIONS;
                                    } else {
                                        if (mostSpecificExceptions == null) {
                                            mostSpecificExceptions = current.thrownExceptions;
                                        }
                                        int mostSpecificLength = mostSpecificExceptions.length;
                                        ReferenceBinding[] nextExceptions = this.getFilteredExceptions(next);
                                        int nextLength = nextExceptions.length;
                                        SimpleSet temp = new SimpleSet(mostSpecificLength);
                                        boolean changed = false;
                                        int t = 0;
                                        while (t < mostSpecificLength) {
                                            ReferenceBinding exception = mostSpecificExceptions[t];
                                            int s = 0;
                                            while (s < nextLength) {
                                                ReferenceBinding nextException = nextExceptions[s];
                                                if (exception.isCompatibleWith(nextException)) {
                                                    temp.add(exception);
                                                    break;
                                                }
                                                if (nextException.isCompatibleWith(exception)) {
                                                    temp.add(nextException);
                                                    changed = true;
                                                    break;
                                                }
                                                changed = true;
                                                ++s;
                                            }
                                            ++t;
                                        }
                                        if (changed) {
                                            mostSpecificExceptions = temp.elementSize == 0 ? Binding.NO_EXCEPTIONS : new ReferenceBinding[temp.elementSize];
                                            temp.asArray(mostSpecificExceptions);
                                        }
                                    }
                                }
                            }
                        }
                        ++j;
                    }
                    if (mostSpecificExceptions != null && mostSpecificExceptions != current.thrownExceptions) {
                        return new MostSpecificExceptionMethodBinding(current, (ReferenceBinding[])mostSpecificExceptions);
                    }
                    return current;
                }
            }
            ++i5;
        }
        return new ProblemMethodBinding(visible[0], visible[0].selector, visible[0].parameters, 3);
    }

    private ReferenceBinding[] getFilteredExceptions(MethodBinding method) {
        ReferenceBinding[] allExceptions = method.thrownExceptions;
        int length = allExceptions.length;
        if (length < 2) {
            return allExceptions;
        }
        ReferenceBinding[] filteredExceptions = new ReferenceBinding[length];
        int count = 0;
        int i = 0;
        while (i < length) {
            block9: {
                ReferenceBinding currentException = allExceptions[i];
                int j = 0;
                while (j < length) {
                    if (i != j) {
                        if (TypeBinding.equalsEquals(currentException, allExceptions[j])) {
                            if (i < j) {
                                break;
                            }
                            break block9;
                        }
                        if (currentException.isCompatibleWith(allExceptions[j])) break block9;
                    }
                    ++j;
                }
                filteredExceptions[count++] = currentException;
            }
            ++i;
        }
        if (count != length) {
            ReferenceBinding[] tmp = new ReferenceBinding[count];
            System.arraycopy(filteredExceptions, 0, tmp, 0, count);
            return tmp;
        }
        return allExceptions;
    }

    public final ClassScope outerMostClassScope() {
        ClassScope lastClassScope = null;
        Scope scope = this;
        do {
            if (!(scope instanceof ClassScope)) continue;
            lastClassScope = (ClassScope)scope;
        } while ((scope = scope.parent) != null);
        return lastClassScope;
    }

    public final MethodScope outerMostMethodScope() {
        MethodScope lastMethodScope = null;
        Scope scope = this;
        do {
            if (!(scope instanceof MethodScope)) continue;
            lastMethodScope = (MethodScope)scope;
        } while ((scope = scope.parent) != null);
        return lastMethodScope;
    }

    public int parameterCompatibilityLevel(MethodBinding method, TypeBinding[] arguments, InvocationSite site) {
        if (method.problemId() == 23 && (method = ((ProblemMethodBinding)method).closestMatch) == null) {
            return -1;
        }
        if (this.compilerOptions().sourceLevel >= 0x340000L && method instanceof ParameterizedGenericMethodBinding) {
            ReferenceExpression referenceExpression;
            int inferenceKind = 0;
            InferenceContext18 context = null;
            if (site instanceof Invocation) {
                Invocation invocation = (Invocation)site;
                context = invocation.getInferenceContext((ParameterizedGenericMethodBinding)method);
                if (context != null) {
                    inferenceKind = context.inferenceKind;
                }
            } else if (site instanceof ReferenceExpression && (context = (referenceExpression = (ReferenceExpression)site).getInferenceContext((ParameterizedGenericMethodBinding)method)) != null) {
                inferenceKind = context.inferenceKind;
            }
            if (site instanceof Invocation && context != null && context.stepCompleted >= 2) {
                int i = 0;
                int length = arguments.length;
                while (i < length) {
                    TypeBinding parameter;
                    TypeBinding argument = arguments[i];
                    if (argument.isFunctionalType() && !argument.isCompatibleWith(parameter = InferenceContext18.getParameter(method.parameters, i, context.isVarArgs()), this) && (!argument.isPolyType() || ((PolyTypeBinding)argument).expression.isPertinentToApplicability(parameter = InferenceContext18.getParameter(method.original().parameters, i, context.isVarArgs()), method))) {
                        return -1;
                    }
                    ++i;
                }
            }
            switch (inferenceKind) {
                case 1: {
                    return 0;
                }
                case 2: {
                    return 1;
                }
                case 3: {
                    return 2;
                }
            }
        }
        return this.parameterCompatibilityLevel(method, arguments, false);
    }

    public int parameterCompatibilityLevel(MethodBinding method, TypeBinding[] arguments) {
        return this.parameterCompatibilityLevel(method, arguments, false);
    }

    public int parameterCompatibilityLevel(MethodBinding method, TypeBinding[] arguments, boolean tiebreakingVarargsMethods) {
        TypeBinding arg;
        TypeBinding[] parameters = method.parameters;
        int paramLength = parameters.length;
        int argLength = arguments.length;
        CompilerOptions compilerOptions = this.compilerOptions();
        if (compilerOptions.sourceLevel < 0x310000L) {
            if (paramLength != argLength) {
                return -1;
            }
            int i = 0;
            while (i < argLength) {
                TypeBinding arg2 = arguments[i];
                TypeBinding param = parameters[i];
                if (TypeBinding.notEquals(arg2, param) && !arg2.isCompatibleWith(param.erasure(), this)) {
                    return -1;
                }
                ++i;
            }
            return 0;
        }
        if (tiebreakingVarargsMethods && CompilerOptions.tolerateIllegalAmbiguousVarargsInvocation && compilerOptions.complianceLevel < 0x330000L) {
            tiebreakingVarargsMethods = false;
        }
        int level = 0;
        int lastIndex = argLength;
        LookupEnvironment env = this.environment();
        if (method.isVarargs()) {
            TypeBinding param;
            lastIndex = paramLength - 1;
            if (paramLength == argLength) {
                param = parameters[lastIndex];
                TypeBinding arg3 = arguments[lastIndex];
                if (TypeBinding.notEquals(param, arg3) && (level = this.parameterCompatibilityLevel(arg3, param, env, tiebreakingVarargsMethods, method)) == -1) {
                    param = ((ArrayBinding)param).elementsType();
                    if (tiebreakingVarargsMethods) {
                        arg3 = ((ArrayBinding)arg3).elementsType();
                    }
                    if (this.parameterCompatibilityLevel(arg3, param, env, tiebreakingVarargsMethods, method) == -1) {
                        return -1;
                    }
                    level = 2;
                }
            } else {
                if (paramLength < argLength) {
                    param = ((ArrayBinding)parameters[lastIndex]).elementsType();
                    int i = lastIndex;
                    while (i < argLength) {
                        TypeBinding typeBinding = arg = tiebreakingVarargsMethods && i == argLength - 1 ? ((ArrayBinding)arguments[i]).elementsType() : arguments[i];
                        if (TypeBinding.notEquals(param, arg) && this.parameterCompatibilityLevel(arg, param, env, tiebreakingVarargsMethods, method) == -1) {
                            return -1;
                        }
                        ++i;
                    }
                } else if (lastIndex != argLength) {
                    return -1;
                }
                level = 2;
            }
        } else if (paramLength != argLength) {
            return -1;
        }
        int i = 0;
        while (i < lastIndex) {
            TypeBinding param = parameters[i];
            TypeBinding typeBinding = arg = tiebreakingVarargsMethods && i == argLength - 1 ? ((ArrayBinding)arguments[i]).elementsType() : arguments[i];
            if (TypeBinding.notEquals(arg, param)) {
                int newLevel = this.parameterCompatibilityLevel(arg, param, env, tiebreakingVarargsMethods, method);
                if (newLevel == -1) {
                    return -1;
                }
                if (newLevel > level) {
                    level = newLevel;
                }
            }
            ++i;
        }
        return level;
    }

    public int parameterCompatibilityLevel(TypeBinding arg, TypeBinding param) {
        TypeBinding convertedType;
        if (TypeBinding.equalsEquals(arg, param)) {
            return 0;
        }
        if (arg == null || param == null) {
            return -1;
        }
        if (arg.isCompatibleWith(param, this)) {
            return 0;
        }
        if ((arg.kind() == 65540 || arg.isBaseType() != param.isBaseType()) && (TypeBinding.equalsEquals(convertedType = this.environment().computeBoxingType(arg), param) || convertedType.isCompatibleWith(param, this))) {
            return 1;
        }
        return -1;
    }

    private int parameterCompatibilityLevel(TypeBinding arg, TypeBinding param, LookupEnvironment env, boolean tieBreakingVarargsMethods, MethodBinding method) {
        TypeBinding convertedType;
        if (arg == null || param == null) {
            return -1;
        }
        if (arg instanceof PolyTypeBinding && !((PolyTypeBinding)arg).expression.isPertinentToApplicability(param, method) ? arg.isPotentiallyCompatibleWith(param, this) : arg.isCompatibleWith(param, this)) {
            return 0;
        }
        if (tieBreakingVarargsMethods && (this.compilerOptions().complianceLevel >= 0x330000L || !CompilerOptions.tolerateIllegalAmbiguousVarargsInvocation)) {
            return -1;
        }
        if ((arg.kind() == 65540 || arg.isBaseType() != param.isBaseType()) && (TypeBinding.equalsEquals(convertedType = env.computeBoxingType(arg), param) || convertedType.isCompatibleWith(param, this))) {
            return 1;
        }
        return -1;
    }

    public abstract ProblemReporter problemReporter();

    public final CompilationUnitDeclaration referenceCompilationUnit() {
        Scope scope;
        Scope unitScope = this;
        while ((scope = unitScope.parent) != null) {
            unitScope = scope;
        }
        return ((CompilationUnitScope)unitScope).referenceContext;
    }

    public ReferenceContext referenceContext() {
        Scope current = this;
        do {
            switch (current.kind) {
                case 2: {
                    return ((MethodScope)current).referenceContext;
                }
                case 3: {
                    return ((ClassScope)current).referenceContext;
                }
                case 4: {
                    return ((CompilationUnitScope)current).referenceContext;
                }
                case 5: {
                    return ((ModuleScope)current).referenceContext;
                }
            }
        } while ((current = current.parent) != null);
        return null;
    }

    public ReferenceContext originalReferenceContext() {
        Scope current = this;
        do {
            switch (current.kind) {
                case 2: {
                    ReferenceContext context = ((MethodScope)current).referenceContext;
                    if (context instanceof LambdaExpression) {
                        LambdaExpression expression = (LambdaExpression)context;
                        while (expression != expression.original) {
                            expression = expression.original;
                        }
                        return expression;
                    }
                    return context;
                }
                case 3: {
                    return ((ClassScope)current).referenceContext;
                }
                case 4: {
                    return ((CompilationUnitScope)current).referenceContext;
                }
            }
        } while ((current = current.parent) != null);
        return null;
    }

    public boolean deferCheck(Runnable check) {
        if (this.parent != null) {
            return this.parent.deferCheck(check);
        }
        return false;
    }

    public void deferBoundCheck(TypeReference typeRef) {
        if (this.kind == 3) {
            ClassScope classScope = (ClassScope)this;
            if (classScope.deferredBoundChecks == null) {
                classScope.deferredBoundChecks = new ArrayList(3);
                classScope.deferredBoundChecks.add(typeRef);
            } else if (!classScope.deferredBoundChecks.contains(typeRef)) {
                classScope.deferredBoundChecks.add(typeRef);
            }
        }
    }

    int startIndex() {
        return 0;
    }

    public MethodBinding getStaticFactory(ParameterizedTypeBinding allocationType, ReferenceBinding originalEnclosingType, TypeBinding[] argumentTypes, InvocationSite allocationSite) {
        ReferenceBinding genericType;
        int classTypeVariablesArity = 0;
        TypeVariableBinding[] classTypeVariables = Binding.NO_TYPE_VARIABLES;
        ReferenceBinding currentType = genericType = allocationType.genericType();
        while (currentType != null) {
            int length;
            TypeVariableBinding[] typeVariables = currentType.typeVariables();
            int n = length = typeVariables == null ? 0 : typeVariables.length;
            if (length > 0) {
                TypeVariableBinding[] typeVariableBindingArray = classTypeVariables;
                classTypeVariables = new TypeVariableBinding[classTypeVariablesArity + length];
                System.arraycopy(typeVariableBindingArray, 0, classTypeVariables, 0, classTypeVariablesArity);
                System.arraycopy(typeVariables, 0, classTypeVariables, classTypeVariablesArity, length);
                classTypeVariablesArity += length;
            }
            if (currentType.isStatic()) break;
            currentType = currentType.enclosingType();
        }
        boolean isInterface = allocationType.isInterface();
        ReferenceBinding typeToSearch = isInterface ? this.getJavaLangObject() : allocationType;
        MethodBinding[] methods = typeToSearch.getMethods(TypeConstants.INIT, argumentTypes.length);
        MethodBinding[] staticFactories = new MethodBinding[methods.length];
        int sfi = 0;
        int i = 0;
        int length = methods.length;
        while (i < length) {
            MethodBinding method = methods[i];
            if (method.canBeSeenBy(allocationSite, this)) {
                int paramLength = method.parameters.length;
                boolean isVarArgs = method.isVarargs();
                if (argumentTypes.length == paramLength || isVarArgs && argumentTypes.length >= paramLength - 1) {
                    TypeVariableBinding[] methodTypeVariables = method.typeVariables();
                    int methodTypeVariablesArity = methodTypeVariables.length;
                    int factoryArity = classTypeVariablesArity + methodTypeVariablesArity;
                    LookupEnvironment environment = this.environment();
                    MethodBinding targetMethod = isInterface ? new MethodBinding(method.original(), genericType) : method.original();
                    SyntheticFactoryMethodBinding staticFactory = new SyntheticFactoryMethodBinding(targetMethod, environment, originalEnclosingType);
                    staticFactory.typeVariables = new TypeVariableBinding[factoryArity];
                    final SimpleLookupTable map = new SimpleLookupTable(factoryArity);
                    String prime = "";
                    Binding declaringElement = null;
                    int j = 0;
                    while (j < classTypeVariablesArity) {
                        TypeVariableBinding original = classTypeVariables[j];
                        if (original.declaringElement != declaringElement) {
                            declaringElement = original.declaringElement;
                            prime = String.valueOf(prime) + "'";
                        }
                        staticFactory.typeVariables[j] = new TypeVariableBinding(CharOperation.concat(original.sourceName, prime.toCharArray()), staticFactory, j, environment);
                        map.put(original.unannotated(), staticFactory.typeVariables[j]);
                        ++j;
                    }
                    prime = String.valueOf(prime) + "'";
                    j = classTypeVariablesArity;
                    int k = 0;
                    while (j < factoryArity) {
                        staticFactory.typeVariables[j] = new TypeVariableBinding(CharOperation.concat(methodTypeVariables[k].sourceName, prime.toCharArray()), staticFactory, j, environment);
                        map.put(methodTypeVariables[k].unannotated(), staticFactory.typeVariables[j]);
                        ++j;
                        ++k;
                    }
                    final Scope scope = this;
                    Substitution substitution = new Substitution(){

                        @Override
                        public LookupEnvironment environment() {
                            return scope.environment();
                        }

                        @Override
                        public boolean isRawSubstitution() {
                            return false;
                        }

                        @Override
                        public TypeBinding substitute(TypeVariableBinding typeVariable) {
                            TypeBinding retVal = (TypeBinding)map.get(typeVariable.unannotated());
                            return retVal == null ? typeVariable : (typeVariable.hasTypeAnnotations() ? this.environment().createAnnotatedType(retVal, typeVariable.getTypeAnnotations()) : retVal);
                        }
                    };
                    int j2 = 0;
                    while (j2 < factoryArity) {
                        TypeVariableBinding originalVariable = j2 < classTypeVariablesArity ? classTypeVariables[j2] : methodTypeVariables[j2 - classTypeVariablesArity];
                        TypeVariableBinding substitutedVariable = (TypeVariableBinding)map.get(originalVariable.unannotated());
                        TypeBinding substitutedSuperclass = Scope.substitute(substitution, originalVariable.superclass);
                        ReferenceBinding[] substitutedInterfaces = Scope.substitute(substitution, originalVariable.superInterfaces);
                        if (originalVariable.firstBound != null) {
                            TypeBinding firstBound = TypeBinding.equalsEquals(originalVariable.firstBound, originalVariable.superclass) ? substitutedSuperclass : substitutedInterfaces[0];
                            substitutedVariable.setFirstBound(firstBound);
                        }
                        switch (substitutedSuperclass.kind()) {
                            case 68: {
                                substitutedVariable.setSuperClass(environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_OBJECT, null));
                                substitutedVariable.setSuperInterfaces(substitutedInterfaces);
                                break;
                            }
                            default: {
                                if (substitutedSuperclass.isInterface()) {
                                    substitutedVariable.setSuperClass(environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_OBJECT, null));
                                    int interfaceCount = substitutedInterfaces.length;
                                    ReferenceBinding[] referenceBindingArray = substitutedInterfaces;
                                    substitutedInterfaces = new ReferenceBinding[interfaceCount + 1];
                                    System.arraycopy(referenceBindingArray, 0, substitutedInterfaces, 1, interfaceCount);
                                    substitutedInterfaces[0] = (ReferenceBinding)substitutedSuperclass;
                                    substitutedVariable.setSuperInterfaces(substitutedInterfaces);
                                    break;
                                }
                                substitutedVariable.setSuperClass((ReferenceBinding)substitutedSuperclass);
                                substitutedVariable.setSuperInterfaces(substitutedInterfaces);
                            }
                        }
                        ++j2;
                    }
                    staticFactory.returnType = environment.createParameterizedType(genericType, Scope.substitute(substitution, genericType.typeVariables()), originalEnclosingType);
                    staticFactory.parameters = Scope.substitute(substitution, method.parameters);
                    staticFactory.thrownExceptions = Scope.substitute(substitution, method.thrownExceptions);
                    if (staticFactory.thrownExceptions == null) {
                        staticFactory.thrownExceptions = Binding.NO_EXCEPTIONS;
                    }
                    staticFactories[sfi++] = new ParameterizedMethodBinding((ParameterizedTypeBinding)environment.convertToParameterizedType(isInterface ? allocationType : staticFactory.declaringClass), staticFactory);
                }
            }
            ++i;
        }
        if (sfi == 0) {
            return null;
        }
        if (sfi != methods.length) {
            MethodBinding[] methodBindingArray = staticFactories;
            staticFactories = new MethodBinding[sfi];
            System.arraycopy(methodBindingArray, 0, staticFactories, 0, sfi);
        }
        MethodBinding[] compatible = new MethodBinding[sfi];
        int compatibleIndex = 0;
        int i2 = 0;
        while (i2 < sfi) {
            MethodBinding compatibleMethod = this.computeCompatibleMethod(staticFactories[i2], argumentTypes, allocationSite);
            if (compatibleMethod != null && compatibleMethod.isValidBinding()) {
                compatible[compatibleIndex++] = compatibleMethod;
            }
            ++i2;
        }
        if (compatibleIndex == 0) {
            return null;
        }
        return compatibleIndex == 1 ? compatible[0] : this.mostSpecificMethodBinding(compatible, compatibleIndex, argumentTypes, allocationSite, allocationType);
    }

    public boolean validateNullAnnotation(long tagBits, TypeReference typeRef, Annotation[] annotations) {
        long nullAnnotationTagBit;
        if (typeRef == null || typeRef.resolvedType == null) {
            return true;
        }
        TypeBinding type = typeRef.resolvedType;
        boolean usesNullTypeAnnotations = this.environment().usesNullTypeAnnotations();
        if (usesNullTypeAnnotations) {
            type = type.leafComponentType();
            nullAnnotationTagBit = type.tagBits & 0x180000000000000L;
        } else {
            nullAnnotationTagBit = tagBits & 0x180000000000000L;
        }
        if (nullAnnotationTagBit != 0L && type != null && type.isBaseType()) {
            if (typeRef.resolvedType.id != 6 || !usesNullTypeAnnotations) {
                this.problemReporter().illegalAnnotationForBaseType(typeRef, annotations, nullAnnotationTagBit);
            }
            return false;
        }
        return true;
    }

    public boolean recordNonNullByDefault(Binding target, int value, Annotation annotation, int scopeStart, int scopeEnd) {
        ReferenceContext context = this.referenceContext();
        if (context instanceof LambdaExpression && context != ((LambdaExpression)context).original) {
            return false;
        }
        if (this.nullDefaultRanges == null) {
            this.nullDefaultRanges = new ArrayList(3);
        }
        for (NullDefaultRange nullDefaultRange : this.nullDefaultRanges) {
            if (nullDefaultRange.start != scopeStart || nullDefaultRange.end != scopeEnd) continue;
            if (nullDefaultRange.contains(annotation)) {
                return false;
            }
            nullDefaultRange.merge(value, annotation, target);
            return true;
        }
        this.nullDefaultRanges.add(new NullDefaultRange(value, annotation, scopeStart, scopeEnd, target));
        return true;
    }

    public Binding checkRedundantDefaultNullness(int nullBits, int sourceStart) {
        Binding target = this.localCheckRedundantDefaultNullness(nullBits, sourceStart);
        if (target != null) {
            return target;
        }
        return this.parent.checkRedundantDefaultNullness(nullBits, sourceStart);
    }

    public boolean hasDefaultNullnessFor(int location, int sourceStart) {
        int nonNullByDefaultValue = this.localNonNullByDefaultValue(sourceStart);
        if (nonNullByDefaultValue != 0) {
            return (nonNullByDefaultValue & location) != 0;
        }
        return this.parent.hasDefaultNullnessFor(location, sourceStart);
    }

    public final int localNonNullByDefaultValue(int start) {
        NullDefaultRange nullDefaultRange = this.nullDefaultRangeForPosition(start);
        return nullDefaultRange != null ? nullDefaultRange.value : 0;
    }

    protected final Binding localCheckRedundantDefaultNullness(int nullBits, int position) {
        NullDefaultRange nullDefaultRange = this.nullDefaultRangeForPosition(position);
        if (nullDefaultRange != null) {
            return nullBits == nullDefaultRange.value ? nullDefaultRange.target : NOT_REDUNDANT;
        }
        return null;
    }

    private NullDefaultRange nullDefaultRangeForPosition(int start) {
        if (this.nullDefaultRanges != null) {
            for (NullDefaultRange nullDefaultRange : this.nullDefaultRanges) {
                if (start < nullDefaultRange.start || start >= nullDefaultRange.end) continue;
                return nullDefaultRange;
            }
        }
        return null;
    }

    public static BlockScope typeAnnotationsResolutionScope(Scope scope) {
        BlockScope resolutionScope = null;
        switch (scope.kind) {
            case 3: {
                resolutionScope = ((ClassScope)scope).referenceContext.staticInitializerScope;
                break;
            }
            case 1: 
            case 2: {
                resolutionScope = (BlockScope)scope;
            }
        }
        return resolutionScope;
    }

    /*
     * Unable to fully structure code
     */
    public void tagAsAccessingEnclosingInstanceStateOf(ReferenceBinding enclosingType, boolean typeVariableAccess) {
        methodScope = this.methodScope();
        if (methodScope != null && methodScope.referenceContext instanceof TypeDeclaration && !methodScope.enclosingReceiverType().isCompatibleWith(enclosingType)) {
            methodScope = methodScope.enclosingMethodScope();
        }
        enclosingMethod = enclosingType != null ? enclosingType.enclosingMethod() : null;
        ** GOTO lbl19
        {
            lambda = (LambdaExpression)methodScope.referenceContext;
            if (!typeVariableAccess && !lambda.scope.isStatic) {
                lambda.shouldCaptureInstance = true;
            }
            methodScope = methodScope.enclosingMethodScope();
            do {
                if (methodScope != null && methodScope.referenceContext instanceof LambdaExpression) continue block0;
                if (methodScope == null) continue;
                if (methodScope.referenceContext instanceof MethodDeclaration) {
                    methodDeclaration = (MethodDeclaration)methodScope.referenceContext;
                    if (methodDeclaration.binding == enclosingMethod) break block0;
                    methodDeclaration.bits &= -257;
                }
                if ((enclosingClassScope = methodScope.enclosingClassScope()) == null || (type = enclosingClassScope.referenceContext) == null || type.binding == null || enclosingType == null || type.binding.isCompatibleWith(enclosingType.original())) break block0;
                methodScope = enclosingClassScope.enclosingMethodScope();
lbl19:
                // 3 sources

            } while (methodScope != null);
        }
    }

    public Supplier<ReferenceBinding> getCommonReferenceBinding(char[] typeName) {
        assert (typeName != null && typeName.length > 0);
        this.initializeCommonTypeBindings();
        Supplier<ReferenceBinding> typeSupplier = this.commonTypeBindings.get(new String(typeName));
        return typeSupplier;
    }

    private Map<String, Supplier<ReferenceBinding>> initializeCommonTypeBindings() {
        if (this.commonTypeBindings != null) {
            return this.commonTypeBindings;
        }
        HashMap<String, Supplier<ReferenceBinding>> t = new HashMap<String, Supplier<ReferenceBinding>>();
        t.put(new String(ConstantPool.JavaLangAssertionErrorConstantPoolName), this::getJavaLangAssertionError);
        t.put(new String(ConstantPool.JavaLangErrorConstantPoolName), this::getJavaLangError);
        t.put(new String(ConstantPool.JavaLangIncompatibleClassChangeErrorConstantPoolName), this::getJavaLangIncompatibleClassChangeError);
        t.put(new String(ConstantPool.JavaLangNoClassDefFoundErrorConstantPoolName), this::getJavaLangNoClassDefFoundError);
        t.put(new String(ConstantPool.JavaLangStringBufferConstantPoolName), this::getJavaLangStringBuffer);
        t.put(new String(ConstantPool.JavaLangIntegerConstantPoolName), this::getJavaLangInteger);
        t.put(new String(ConstantPool.JavaLangBooleanConstantPoolName), this::getJavaLangBoolean);
        t.put(new String(ConstantPool.JavaLangByteConstantPoolName), this::getJavaLangByte);
        t.put(new String(ConstantPool.JavaLangCharacterConstantPoolName), this::getJavaLangCharacter);
        t.put(new String(ConstantPool.JavaLangFloatConstantPoolName), this::getJavaLangFloat);
        t.put(new String(ConstantPool.JavaLangDoubleConstantPoolName), this::getJavaLangDouble);
        t.put(new String(ConstantPool.JavaLangShortConstantPoolName), this::getJavaLangShort);
        t.put(new String(ConstantPool.JavaLangLongConstantPoolName), this::getJavaLangLong);
        t.put(new String(ConstantPool.JavaLangVoidConstantPoolName), this::getJavaLangVoid);
        t.put(new String(ConstantPool.JavaLangStringConstantPoolName), this::getJavaLangString);
        t.put(new String(ConstantPool.JavaLangStringBuilderConstantPoolName), this::getJavaLangStringBuilder);
        t.put(new String(ConstantPool.JavaLangClassConstantPoolName), this::getJavaLangClass);
        t.put(new String(ConstantPool.JAVALANGREFLECTFIELD_CONSTANTPOOLNAME), this::getJavaLangReflectField);
        t.put(new String(ConstantPool.JAVALANGREFLECTMETHOD_CONSTANTPOOLNAME), this::getJavaLangReflectMethod);
        t.put(new String(ConstantPool.JavaUtilIteratorConstantPoolName), this::getJavaUtilIterator);
        t.put(new String(ConstantPool.JavaLangEnumConstantPoolName), this::getJavaLangEnum);
        t.put(new String(ConstantPool.JavaLangObjectConstantPoolName), this::getJavaLangObject);
        this.commonTypeBindings = t;
        return this.commonTypeBindings;
    }

    static class MethodClashException
    extends RuntimeException {
        private static final long serialVersionUID = -7996779527641476028L;

        MethodClashException() {
        }
    }

    private static class NullDefaultRange {
        final int start;
        final int end;
        int value;
        private Annotation[] annotations;
        Binding target;

        NullDefaultRange(int value, Annotation annotation, int start, int end, Binding target) {
            this.start = start;
            this.end = end;
            this.value = value;
            this.annotations = new Annotation[]{annotation};
            this.target = target;
        }

        boolean contains(Annotation annotation) {
            Annotation[] annotationArray = this.annotations;
            int n = this.annotations.length;
            int n2 = 0;
            while (n2 < n) {
                Annotation annotation2 = annotationArray[n2];
                if (annotation2 == annotation) {
                    return true;
                }
                ++n2;
            }
            return false;
        }

        void merge(int nextValue, Annotation nextAnnotation, Binding nextTarget) {
            int len = this.annotations.length;
            this.annotations = new Annotation[len + 1];
            System.arraycopy(this.annotations, 0, this.annotations, 0, len);
            this.annotations[len] = nextAnnotation;
            this.target = nextTarget;
            this.value |= nextValue;
        }
    }

    public static class Substitutor {
        protected ReferenceBinding staticContext;

        public ReferenceBinding[] substitute(Substitution substitution, ReferenceBinding[] originalTypes) {
            if (originalTypes == null) {
                return null;
            }
            ReferenceBinding[] substitutedTypes = originalTypes;
            int i = 0;
            int length = originalTypes.length;
            while (i < length) {
                ReferenceBinding originalType = originalTypes[i];
                TypeBinding substitutedType = this.substitute(substitution, originalType);
                if (!(substitutedType instanceof ReferenceBinding)) {
                    return null;
                }
                if (substitutedType != originalType) {
                    if (substitutedTypes == originalTypes) {
                        substitutedTypes = new ReferenceBinding[length];
                        System.arraycopy(originalTypes, 0, substitutedTypes, 0, i);
                    }
                    substitutedTypes[i] = (ReferenceBinding)substitutedType;
                } else if (substitutedTypes != originalTypes) {
                    substitutedTypes[i] = originalType;
                }
                ++i;
            }
            return substitutedTypes;
        }

        public TypeBinding substitute(Substitution substitution, TypeBinding originalType) {
            if (originalType == null) {
                return null;
            }
            switch (originalType.kind()) {
                case 4100: {
                    return substitution.substitute((TypeVariableBinding)originalType);
                }
                case 260: {
                    TypeBinding[] originalArguments;
                    ReferenceBinding originalEnclosing;
                    ParameterizedTypeBinding originalParameterizedType = (ParameterizedTypeBinding)originalType;
                    ReferenceBinding substitutedEnclosing = originalEnclosing = originalType.enclosingType();
                    if (originalEnclosing != null && originalParameterizedType.hasEnclosingInstanceContext() && Substitutor.isMemberTypeOfRaw(originalType, substitutedEnclosing = (ReferenceBinding)this.substitute(substitution, originalEnclosing))) {
                        return originalParameterizedType.environment.createRawType(originalParameterizedType.genericType(), substitutedEnclosing, originalType.getTypeAnnotations());
                    }
                    TypeBinding[] substitutedArguments = originalArguments = originalParameterizedType.arguments;
                    if (originalArguments != null) {
                        if (substitution.isRawSubstitution()) {
                            return originalParameterizedType.environment.createRawType(originalParameterizedType.genericType(), substitutedEnclosing, originalType.getTypeAnnotations());
                        }
                        substitutedArguments = this.substitute(substitution, originalArguments);
                    }
                    if (substitutedArguments == originalArguments && substitutedEnclosing == originalEnclosing) break;
                    return originalParameterizedType.environment.createParameterizedType(originalParameterizedType.genericType(), substitutedArguments, substitutedEnclosing, originalType.getTypeAnnotations());
                }
                case 68: {
                    ArrayBinding originalArrayType = (ArrayBinding)originalType;
                    TypeBinding originalLeafComponentType = originalArrayType.leafComponentType;
                    TypeBinding substitute = this.substitute(substitution, originalLeafComponentType);
                    if (substitute == originalLeafComponentType) break;
                    return originalArrayType.environment.createArrayType(substitute.leafComponentType(), substitute.dimensions() + originalType.dimensions(), originalType.getTypeAnnotations());
                }
                case 516: 
                case 8196: {
                    WildcardBinding wildcard = (WildcardBinding)originalType;
                    if (wildcard.boundKind == 0) break;
                    TypeBinding originalBound = wildcard.bound;
                    TypeBinding substitutedBound = this.substitute(substitution, originalBound);
                    TypeBinding[] originalOtherBounds = wildcard.otherBounds;
                    TypeBinding[] substitutedOtherBounds = this.substitute(substitution, originalOtherBounds);
                    if (substitutedBound == originalBound && originalOtherBounds == substitutedOtherBounds) break;
                    if (originalOtherBounds != null) {
                        TypeBinding[] bounds = new TypeBinding[1 + substitutedOtherBounds.length];
                        bounds[0] = substitutedBound;
                        System.arraycopy(substitutedOtherBounds, 0, bounds, 1, substitutedOtherBounds.length);
                        TypeBinding[] glb = Scope.greaterLowerBound(bounds, null, substitution.environment());
                        if (glb != null && glb != bounds) {
                            substitutedBound = glb[0];
                            if (glb.length == 1) {
                                substitutedOtherBounds = null;
                            } else {
                                substitutedOtherBounds = new TypeBinding[glb.length - 1];
                                System.arraycopy(glb, 1, substitutedOtherBounds, 0, glb.length - 1);
                            }
                        }
                    }
                    return wildcard.environment.createWildcard(wildcard.genericType, wildcard.rank, substitutedBound, substitutedOtherBounds, wildcard.boundKind, wildcard.getTypeAnnotations());
                }
                case 32772: {
                    IntersectionTypeBinding18 intersection = (IntersectionTypeBinding18)originalType;
                    ReferenceBinding[] types = intersection.getIntersectingTypes();
                    ReferenceBinding[] substitutes = this.substitute(substitution, types);
                    ReferenceBinding[] refSubsts = new ReferenceBinding[substitutes.length];
                    System.arraycopy(substitutes, 0, refSubsts, 0, substitutes.length);
                    return substitution.environment().createIntersectionType18(refSubsts);
                }
                case 4: {
                    ReferenceBinding originalEnclosing;
                    if (!originalType.isMemberType()) break;
                    ReferenceBinding originalReferenceType = (ReferenceBinding)originalType;
                    ReferenceBinding substitutedEnclosing = originalEnclosing = originalType.enclosingType();
                    if (originalEnclosing != null && Substitutor.isMemberTypeOfRaw(originalType, substitutedEnclosing = (ReferenceBinding)this.substitute(substitution, originalEnclosing))) {
                        return substitution.environment().createRawType(originalReferenceType, substitutedEnclosing, originalType.getTypeAnnotations());
                    }
                    if (substitutedEnclosing == originalEnclosing || !originalReferenceType.hasEnclosingInstanceContext()) break;
                    return substitution.isRawSubstitution() ? substitution.environment().createRawType(originalReferenceType, substitutedEnclosing, originalType.getTypeAnnotations()) : substitution.environment().createParameterizedType(originalReferenceType, null, substitutedEnclosing, originalType.getTypeAnnotations());
                }
                case 2052: {
                    ReferenceBinding originalEnclosing;
                    ReferenceBinding originalReferenceType = (ReferenceBinding)originalType.unannotated();
                    ReferenceBinding substitutedEnclosing = originalEnclosing = originalType.enclosingType();
                    if (originalEnclosing != null && Substitutor.isMemberTypeOfRaw(originalType, substitutedEnclosing = (ReferenceBinding)(originalType.isStatic() ? substitution.environment().convertToRawType(originalEnclosing, true) : (ReferenceBinding)this.substitute(substitution, originalEnclosing)))) {
                        return substitution.environment().createRawType(originalReferenceType, substitutedEnclosing, originalType.getTypeAnnotations());
                    }
                    if (substitution.isRawSubstitution()) {
                        return substitution.environment().createRawType(originalReferenceType, substitutedEnclosing, originalType.getTypeAnnotations());
                    }
                    if (TypeBinding.equalsEquals(this.staticContext, originalType)) {
                        return originalType;
                    }
                    TypeBinding[] originalArguments = originalReferenceType.typeVariables();
                    TypeBinding[] substitutedArguments = this.substitute(substitution, originalArguments);
                    return substitution.environment().createParameterizedType(originalReferenceType, substitutedArguments, substitutedEnclosing, originalType.getTypeAnnotations());
                }
            }
            return originalType;
        }

        private static boolean isMemberTypeOfRaw(TypeBinding originalType, ReferenceBinding substitutedEnclosing) {
            return substitutedEnclosing != null && substitutedEnclosing.isRawType() && originalType instanceof ReferenceBinding && !((ReferenceBinding)originalType).isStatic();
        }

        public TypeBinding[] substitute(Substitution substitution, TypeBinding[] originalTypes) {
            if (originalTypes == null) {
                return null;
            }
            TypeBinding[] substitutedTypes = originalTypes;
            int i = 0;
            int length = originalTypes.length;
            while (i < length) {
                TypeBinding originalType = originalTypes[i];
                TypeBinding substitutedParameter = this.substitute(substitution, originalType);
                if (substitutedParameter != originalType) {
                    if (substitutedTypes == originalTypes) {
                        substitutedTypes = new TypeBinding[length];
                        System.arraycopy(originalTypes, 0, substitutedTypes, 0, i);
                    }
                    substitutedTypes[i] = substitutedParameter;
                } else if (substitutedTypes != originalTypes) {
                    substitutedTypes[i] = originalType;
                }
                ++i;
            }
            return substitutedTypes;
        }
    }
}

