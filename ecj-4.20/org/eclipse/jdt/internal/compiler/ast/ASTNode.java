/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.ContainerAnnotation;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Invocation;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.RecordComponent;
import org.eclipse.jdt.internal.compiler.ast.TrueLiteral;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.UnionTypeReference;
import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedGenericMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.RecordComponentBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;

public abstract class ASTNode
implements TypeConstants,
TypeIds {
    public int sourceStart;
    public int sourceEnd;
    public static final int Bit1 = 1;
    public static final int Bit2 = 2;
    public static final int Bit3 = 4;
    public static final int Bit4 = 8;
    public static final int Bit5 = 16;
    public static final int Bit6 = 32;
    public static final int Bit7 = 64;
    public static final int Bit8 = 128;
    public static final int Bit9 = 256;
    public static final int Bit10 = 512;
    public static final int Bit11 = 1024;
    public static final int Bit12 = 2048;
    public static final int Bit13 = 4096;
    public static final int Bit14 = 8192;
    public static final int Bit15 = 16384;
    public static final int Bit16 = 32768;
    public static final int Bit17 = 65536;
    public static final int Bit18 = 131072;
    public static final int Bit19 = 262144;
    public static final int Bit20 = 524288;
    public static final int Bit21 = 0x100000;
    public static final int Bit22 = 0x200000;
    public static final int Bit23 = 0x400000;
    public static final int Bit24 = 0x800000;
    public static final int Bit25 = 0x1000000;
    public static final int Bit26 = 0x2000000;
    public static final int Bit27 = 0x4000000;
    public static final int Bit28 = 0x8000000;
    public static final int Bit29 = 0x10000000;
    public static final int Bit30 = 0x20000000;
    public static final int Bit31 = 0x40000000;
    public static final int Bit32 = Integer.MIN_VALUE;
    public static final long Bit32L = 0x80000000L;
    public static final long Bit33L = 0x100000000L;
    public static final long Bit34L = 0x200000000L;
    public static final long Bit35L = 0x400000000L;
    public static final long Bit36L = 0x800000000L;
    public static final long Bit37L = 0x1000000000L;
    public static final long Bit38L = 0x2000000000L;
    public static final long Bit39L = 0x4000000000L;
    public static final long Bit40L = 0x8000000000L;
    public static final long Bit41L = 0x10000000000L;
    public static final long Bit42L = 0x20000000000L;
    public static final long Bit43L = 0x40000000000L;
    public static final long Bit44L = 0x80000000000L;
    public static final long Bit45L = 0x100000000000L;
    public static final long Bit46L = 0x200000000000L;
    public static final long Bit47L = 0x400000000000L;
    public static final long Bit48L = 0x800000000000L;
    public static final long Bit49L = 0x1000000000000L;
    public static final long Bit50L = 0x2000000000000L;
    public static final long Bit51L = 0x4000000000000L;
    public static final long Bit52L = 0x8000000000000L;
    public static final long Bit53L = 0x10000000000000L;
    public static final long Bit54L = 0x20000000000000L;
    public static final long Bit55L = 0x40000000000000L;
    public static final long Bit56L = 0x80000000000000L;
    public static final long Bit57L = 0x100000000000000L;
    public static final long Bit58L = 0x200000000000000L;
    public static final long Bit59L = 0x400000000000000L;
    public static final long Bit60L = 0x800000000000000L;
    public static final long Bit61L = 0x1000000000000000L;
    public static final long Bit62L = 0x2000000000000000L;
    public static final long Bit63L = 0x4000000000000000L;
    public static final long Bit64L = Long.MIN_VALUE;
    public int bits = Integer.MIN_VALUE;
    public static final int ReturnTypeIDMASK = 15;
    public static final int OperatorSHIFT = 8;
    public static final int OperatorMASK = 16128;
    public static final int IsReturnedValue = 16;
    public static final int UnnecessaryCast = 16384;
    public static final int DisableUnnecessaryCastCheck = 32;
    public static final int GenerateCheckcast = 64;
    public static final int UnsafeCast = 128;
    public static final int RestrictiveFlagMASK = 7;
    public static final int IsTypeElided = 2;
    public static final int IsArgument = 4;
    public static final int IsLocalDeclarationReachable = 0x40000000;
    public static final int IsForeachElementVariable = 16;
    public static final int ShadowsOuterLocal = 0x200000;
    public static final int IsAdditionalDeclarator = 0x400000;
    public static final int FirstAssignmentToLocal = 8;
    public static final int NeedReceiverGenericCast = 262144;
    public static final int IsImplicitThis = 4;
    public static final int DepthSHIFT = 5;
    public static final int DepthMASK = 8160;
    public static final int IsCapturedOuterLocal = 524288;
    public static final int IsSecretYieldValueUsage = 16;
    public static final int IsReachable = Integer.MIN_VALUE;
    public static final int LabelUsed = 64;
    public static final int DocumentedFallthrough = 0x20000000;
    public static final int DocumentedCasesOmitted = 0x40000000;
    public static final int IsSubRoutineEscaping = 16384;
    public static final int IsTryBlockExiting = 0x20000000;
    public static final int ContainsAssertion = 1;
    public static final int IsLocalType = 256;
    public static final int IsAnonymousType = 512;
    public static final int IsMemberType = 1024;
    public static final int HasAbstractMethods = 2048;
    public static final int IsSecondaryType = 4096;
    public static final int HasBeenGenerated = 8192;
    public static final int HasLocalType = 2;
    public static final int HasBeenResolved = 16;
    public static final int ParenthesizedSHIFT = 21;
    public static final int ParenthesizedMASK = 534773760;
    public static final int IgnoreNoEffectAssignCheck = 0x20000000;
    public static final int IsStrictlyAssigned = 8192;
    public static final int IsCompoundAssigned = 65536;
    public static final int DiscardEnclosingInstance = 8192;
    public static final int Unchecked = 65536;
    public static final int ResolveJavadoc = 65536;
    public static final int IsUsefulEmptyStatement = 1;
    public static final int UndocumentedEmptyBlock = 8;
    public static final int OverridingMethodWithSupercall = 16;
    public static final int CanBeStatic = 256;
    public static final int ErrorInSignature = 32;
    public static final int NeedFreeReturn = 64;
    public static final int IsDefaultConstructor = 128;
    public static final int IsCanonicalConstructor = 512;
    public static final int IsImplicit = 1024;
    public static final int HasAllMethodBodies = 16;
    public static final int IsImplicitUnit = 1;
    public static final int InsideJavadoc = 32768;
    public static final int SuperAccess = 16384;
    public static final int Empty = 262144;
    public static final int IsElseIfStatement = 0x20000000;
    public static final int ThenExit = 0x40000000;
    public static final int IsElseStatementUnreachable = 128;
    public static final int IsThenStatementUnreachable = 256;
    public static final int IsSuperType = 16;
    public static final int IsVarArgs = 16384;
    public static final int IgnoreRawTypeCheck = 0x40000000;
    public static final int IsAnnotationDefaultValue = 1;
    public static final int IsNonNull = 131072;
    public static final int NeededScope = 0x20000000;
    public static final int OnDemand = 131072;
    public static final int Used = 2;
    public static final int inModule = 262144;
    public static final int DidResolve = 262144;
    public static final int IsAnySubRoutineEscaping = 0x20000000;
    public static final int IsSynchronized = 0x40000000;
    public static final int BlockExit = 0x20000000;
    public static final int IsRecovered = 32;
    public static final int HasSyntaxErrors = 524288;
    public static final int INVOCATION_ARGUMENT_OK = 0;
    public static final int INVOCATION_ARGUMENT_UNCHECKED = 1;
    public static final int INVOCATION_ARGUMENT_WILDCARD = 2;
    public static final int HasTypeAnnotations = 0x100000;
    public static final int IsUnionType = 0x20000000;
    public static final int IsDiamond = 524288;
    public static final int InsideExpressionStatement = 0x100000;
    public static final int IsSynthetic = 64;
    public static final int HasFunctionalInterfaceTypes = 0x200000;
    public static final Argument[] NO_ARGUMENTS = new Argument[0];
    public static final RecordComponent[] NO_RECORD_COMPONENTS = new RecordComponent[0];

    private static int checkInvocationArgument(BlockScope scope, Expression argument, TypeBinding parameterType, TypeBinding argumentType, TypeBinding originalParameterType) {
        TypeBinding checkedParameterType;
        argument.computeConversion(scope, parameterType, argumentType);
        if (argumentType != TypeBinding.NULL && parameterType.kind() == 516) {
            WildcardBinding wildcard = (WildcardBinding)parameterType;
            if (wildcard.boundKind != 2) {
                return 2;
            }
        }
        if (TypeBinding.notEquals(argumentType, checkedParameterType = parameterType) && argumentType.needsUncheckedConversion(checkedParameterType)) {
            scope.problemReporter().unsafeTypeConversion(argument, argumentType, checkedParameterType);
            return 1;
        }
        return 0;
    }

    public static boolean checkInvocationArguments(BlockScope scope, Expression receiver, TypeBinding receiverType, MethodBinding method, Expression[] arguments, TypeBinding[] argumentTypes, boolean argsContainCast, InvocationSite invocationSite) {
        int invocationStatus;
        MethodBinding rawOriginalGenericMethod;
        boolean uncheckedBoundCheck;
        long sourceLevel;
        block21: {
            block23: {
                int paramLength;
                TypeBinding[] params;
                block22: {
                    int dimensions;
                    TypeBinding lastArgType;
                    ArrayBinding varargsType;
                    block25: {
                        block24: {
                            boolean is1_7;
                            block20: {
                                TypeBinding parameterType;
                                sourceLevel = scope.compilerOptions().sourceLevel;
                                is1_7 = sourceLevel >= 0x330000L;
                                params = method.parameters;
                                paramLength = params.length;
                                boolean isRawMemberInvocation = !method.isStatic() && !receiverType.isUnboundWildcard() && method.declaringClass.isRawType() && method.hasSubstitutedParameters();
                                uncheckedBoundCheck = (method.tagBits & 0x100L) != 0L;
                                rawOriginalGenericMethod = null;
                                if (!isRawMemberInvocation && method instanceof ParameterizedGenericMethodBinding) {
                                    ParameterizedGenericMethodBinding paramMethod = (ParameterizedGenericMethodBinding)method;
                                    if (paramMethod.isRaw && method.hasSubstitutedParameters()) {
                                        rawOriginalGenericMethod = method.original();
                                    }
                                }
                                invocationStatus = 0;
                                if (arguments != null) break block20;
                                if (!(!method.isVarargs() || (parameterType = ((ArrayBinding)params[paramLength - 1]).elementsType()).isReifiable() || is1_7 && (method.tagBits & 0x8000000000000L) != 0L)) {
                                    scope.problemReporter().unsafeGenericArrayForVarargs(parameterType, (ASTNode)((Object)invocationSite));
                                }
                                break block21;
                            }
                            if (!method.isVarargs()) break block22;
                            int lastIndex = paramLength - 1;
                            int i = 0;
                            while (i < lastIndex) {
                                TypeBinding originalRawParam = rawOriginalGenericMethod == null ? null : rawOriginalGenericMethod.parameters[i];
                                invocationStatus |= ASTNode.checkInvocationArgument(scope, arguments[i], params[i], argumentTypes[i], originalRawParam);
                                ++i;
                            }
                            int argLength = arguments.length;
                            if (lastIndex <= argLength) {
                                TypeBinding parameterType = params[lastIndex];
                                TypeBinding originalRawParam = null;
                                if (paramLength != argLength || parameterType.dimensions() != argumentTypes[lastIndex].dimensions()) {
                                    if (!((parameterType = ((ArrayBinding)parameterType).elementsType()).isReifiable() || is1_7 && (method.tagBits & 0x8000000000000L) != 0L)) {
                                        scope.problemReporter().unsafeGenericArrayForVarargs(parameterType, (ASTNode)((Object)invocationSite));
                                    }
                                    originalRawParam = rawOriginalGenericMethod == null ? null : ((ArrayBinding)rawOriginalGenericMethod.parameters[lastIndex]).elementsType();
                                }
                                int i2 = lastIndex;
                                while (i2 < argLength) {
                                    invocationStatus |= ASTNode.checkInvocationArgument(scope, arguments[i2], parameterType, argumentTypes[i2], originalRawParam);
                                    ++i2;
                                }
                            }
                            if (paramLength != argLength) break block23;
                            int varargsIndex = paramLength - 1;
                            varargsType = (ArrayBinding)params[varargsIndex];
                            lastArgType = argumentTypes[varargsIndex];
                            if (lastArgType != TypeBinding.NULL) break block24;
                            if (!varargsType.leafComponentType().isBaseType() || varargsType.dimensions() != 1) {
                                scope.problemReporter().varargsArgumentNeedCast(method, lastArgType, invocationSite);
                            }
                            break block23;
                        }
                        dimensions = lastArgType.dimensions();
                        if (varargsType.dimensions > dimensions) break block23;
                        if (lastArgType.leafComponentType().isBaseType()) {
                            --dimensions;
                        }
                        if (varargsType.dimensions >= dimensions) break block25;
                        scope.problemReporter().varargsArgumentNeedCast(method, lastArgType, invocationSite);
                        break block23;
                    }
                    if (varargsType.dimensions != dimensions || !TypeBinding.notEquals(lastArgType, varargsType) || !TypeBinding.notEquals(lastArgType.leafComponentType().erasure(), varargsType.leafComponentType.erasure()) || !lastArgType.isCompatibleWith(varargsType.elementsType()) || !lastArgType.isCompatibleWith(varargsType)) break block23;
                    scope.problemReporter().varargsArgumentNeedCast(method, lastArgType, invocationSite);
                    break block23;
                }
                int i = 0;
                while (i < paramLength) {
                    TypeBinding originalRawParam = rawOriginalGenericMethod == null ? null : rawOriginalGenericMethod.parameters[i];
                    invocationStatus |= ASTNode.checkInvocationArgument(scope, arguments[i], params[i], argumentTypes[i], originalRawParam);
                    ++i;
                }
            }
            if (argsContainCast) {
                CastExpression.checkNeedForArgumentCasts(scope, receiver, receiverType, method, arguments, argumentTypes, invocationSite);
            }
        }
        if ((invocationStatus & 2) != 0) {
            scope.problemReporter().wildcardInvocation((ASTNode)((Object)invocationSite), receiverType, method, argumentTypes);
        } else if (!method.isStatic() && !receiverType.isUnboundWildcard() && method.declaringClass.isRawType() && method.hasSubstitutedParameters()) {
            if (scope.compilerOptions().reportUnavoidableGenericTypeProblems || receiver == null || !receiver.forcedToBeRaw(scope.referenceContext())) {
                scope.problemReporter().unsafeRawInvocation((ASTNode)((Object)invocationSite), method);
            }
        } else if (rawOriginalGenericMethod != null || uncheckedBoundCheck || (invocationStatus & 1) != 0) {
            if (method instanceof ParameterizedGenericMethodBinding) {
                scope.problemReporter().unsafeRawGenericMethodInvocation((ASTNode)((Object)invocationSite), method, argumentTypes);
                return true;
            }
            if (sourceLevel >= 0x340000L) {
                return true;
            }
        }
        return false;
    }

    public ASTNode concreteStatement() {
        return this;
    }

    private void reportPreviewAPI(Scope scope, long modifiers) {
        if (scope.compilerOptions().enablePreviewFeatures) {
            return;
        }
        if ((modifiers & 0x180000000L) == 0x180000000L) {
            scope.problemReporter().previewAPIUsed(this.sourceStart, this.sourceEnd, (modifiers & 0x400L) != 0L);
        }
    }

    public final boolean isFieldUseDeprecated(FieldBinding field, Scope scope, int filteredBits) {
        ModuleBinding module;
        LookupEnvironment env;
        AccessRestriction restriction;
        if ((this.bits & 0x8000) == 0 && (filteredBits & 0x2000) == 0 && field.isOrEnclosedByPrivateType() && !scope.isDefinedInField(field)) {
            if ((filteredBits & 0x10000) != 0) {
                ++field.original().compoundUseFlag;
            } else {
                field.original().modifiers |= 0x8000000;
            }
        }
        this.reportPreviewAPI(scope, field.tagBits);
        if ((field.modifiers & 0x40000) != 0 && (restriction = (env = (module = field.declaringClass.module()) == null ? scope.environment() : module.environment).getAccessRestriction(field.declaringClass.erasure())) != null) {
            scope.problemReporter().forbiddenReference(field, this, restriction.classpathEntryType, restriction.classpathEntryName, restriction.getProblemId());
        }
        if (!field.isViewedAsDeprecated()) {
            return false;
        }
        if (scope.isDefinedInSameUnit(field.declaringClass)) {
            return false;
        }
        return scope.compilerOptions().reportDeprecationInsideDeprecatedCode || !scope.isInsideDeprecatedCode();
    }

    public boolean isImplicitThis() {
        return false;
    }

    public boolean receiverIsImplicitThis() {
        return false;
    }

    public final boolean isMethodUseDeprecated(MethodBinding method, Scope scope, boolean isExplicitUse, InvocationSite invocation) {
        ModuleBinding module;
        LookupEnvironment env;
        AccessRestriction restriction;
        this.reportPreviewAPI(scope, method.tagBits);
        if ((this.bits & 0x8000) == 0 && method.isOrEnclosedByPrivateType() && !scope.isDefinedInMethod(method)) {
            method.original().modifiers |= 0x8000000;
        }
        if (isExplicitUse && (method.modifiers & 0x40000) != 0 && (restriction = (env = (module = method.declaringClass.module()) == null ? scope.environment() : module.environment).getAccessRestriction(method.declaringClass.erasure())) != null) {
            scope.problemReporter().forbiddenReference(method, invocation, restriction.classpathEntryType, restriction.classpathEntryName, restriction.getProblemId());
        }
        if (!method.isViewedAsDeprecated()) {
            return false;
        }
        if (scope.isDefinedInSameUnit(method.declaringClass)) {
            return false;
        }
        if (!isExplicitUse && (method.modifiers & 0x100000) == 0) {
            return false;
        }
        return scope.compilerOptions().reportDeprecationInsideDeprecatedCode || !scope.isInsideDeprecatedCode();
    }

    public boolean isSuper() {
        return false;
    }

    public boolean isQualifiedSuper() {
        return false;
    }

    public boolean isThis() {
        return false;
    }

    public boolean isUnqualifiedSuper() {
        return false;
    }

    public final boolean isTypeUseDeprecated(TypeBinding type, Scope scope) {
        ModuleBinding module;
        LookupEnvironment env;
        AccessRestriction restriction;
        if (type.isArrayType()) {
            type = ((ArrayBinding)type).leafComponentType;
        }
        if (type.isBaseType()) {
            return false;
        }
        ReferenceBinding refType = (ReferenceBinding)type;
        if ((this.bits & 0x8000) == 0 && refType instanceof TypeVariableBinding) {
            refType.modifiers |= 0x8000000;
        }
        if ((this.bits & 0x8000) == 0 && refType.isOrEnclosedByPrivateType() && !scope.isDefinedInType(refType)) {
            ((ReferenceBinding)refType.erasure()).modifiers |= 0x8000000;
        }
        this.reportPreviewAPI(scope, type.extendedTagBits);
        if (refType.hasRestrictedAccess() && (restriction = (env = (module = refType.module()) == null ? scope.environment() : module.environment).getAccessRestriction(type.erasure())) != null) {
            scope.problemReporter().forbiddenReference(type, this, restriction.classpathEntryType, restriction.classpathEntryName, restriction.getProblemId());
        }
        refType.initializeDeprecatedAnnotationTagBits();
        if (!refType.isViewedAsDeprecated()) {
            return false;
        }
        if (scope.isDefinedInSameUnit(refType)) {
            return false;
        }
        return scope.compilerOptions().reportDeprecationInsideDeprecatedCode || !scope.isInsideDeprecatedCode();
    }

    public abstract StringBuffer print(int var1, StringBuffer var2);

    public static StringBuffer printAnnotations(Annotation[] annotations, StringBuffer output) {
        int length = annotations.length;
        int i = 0;
        while (i < length) {
            Annotation annotation2;
            if (i > 0) {
                output.append(" ");
            }
            if ((annotation2 = annotations[i]) != null) {
                annotation2.print(0, output);
            } else {
                output.append('?');
            }
            ++i;
        }
        return output;
    }

    public static StringBuffer printIndent(int indent, StringBuffer output) {
        int i = indent;
        while (i > 0) {
            output.append("  ");
            --i;
        }
        return output;
    }

    public static StringBuffer printModifiers(int modifiers, StringBuffer output) {
        if ((modifiers & 1) != 0) {
            output.append("public ");
        }
        if ((modifiers & 2) != 0) {
            output.append("private ");
        }
        if ((modifiers & 4) != 0) {
            output.append("protected ");
        }
        if ((modifiers & 8) != 0) {
            output.append("static ");
        }
        if ((modifiers & 0x10) != 0) {
            output.append("final ");
        }
        if ((modifiers & 0x20) != 0) {
            output.append("synchronized ");
        }
        if ((modifiers & 0x40) != 0) {
            output.append("volatile ");
        }
        if ((modifiers & 0x80) != 0) {
            output.append("transient ");
        }
        if ((modifiers & 0x100) != 0) {
            output.append("native ");
        }
        if ((modifiers & 0x400) != 0) {
            output.append("abstract ");
        }
        if ((modifiers & 0x10000) != 0) {
            output.append("default ");
        }
        if ((modifiers & 0x4000000) != 0) {
            output.append("non-sealed ");
        }
        if ((modifiers & 0x10000000) != 0) {
            output.append("sealed ");
        }
        return output;
    }

    /*
     * Unable to fully structure code
     */
    public static MethodBinding resolvePolyExpressionArguments(Invocation invocation, MethodBinding method, TypeBinding[] argumentTypes, BlockScope scope) {
        v0 = method.isValidBinding() != false ? method : (candidateMethod = method instanceof ProblemMethodBinding != false ? ((ProblemMethodBinding)method).closestMatch : null);
        if (candidateMethod == null) {
            return method;
        }
        problemMethod = null;
        variableArity = candidateMethod.isVarargs();
        parameters = candidateMethod.parameters;
        arguments = invocation.arguments();
        if (variableArity && arguments != null && parameters.length == arguments.length && arguments[arguments.length - 1].isCompatibleWith(parameters[parameters.length - 1], scope)) {
            variableArity = false;
        }
        i = 0;
        length = arguments == null ? 0 : arguments.length;
        while (i < length) {
            block10: {
                block12: {
                    block11: {
                        argument = arguments[i];
                        parameterType = InferenceContext18.getParameter(parameters, i, variableArity);
                        if (parameterType == null || argumentTypes[i] == null || !argumentTypes[i].isPolyType()) break block10;
                        argument.setExpectedType(parameterType);
                        if (!(argument instanceof LambdaExpression)) break block11;
                        lambda = (LambdaExpression)argument;
                        skipKosherCheck = method.problemId() == 3;
                        updatedArgumentType = lambda.resolveType(scope, skipKosherCheck);
                        if (lambda.hasErrors() || lambda.hasDescripterProblem) break block10;
                        lambda.updateLocalTypesInMethod(candidateMethod);
                        parameterType = InferenceContext18.getParameter(parameters, i, variableArity);
                        if (!lambda.isCompatibleWith(parameterType, scope)) {
                            if (method.isValidBinding() && problemMethod == null) {
                                originalArguments = Arrays.copyOf(argumentTypes, argumentTypes.length);
                                problemMethod = lambda.reportShapeError(parameterType, scope) ? new ProblemMethodBinding(candidateMethod, method.selector, originalArguments, 31) : new ProblemMethodBinding(candidateMethod, method.selector, originalArguments, 1);
                                ** GOTO lbl39
                            } else {
                                ** GOTO lbl31
                            }
                        }
                        break block12;
lbl31:
                        // 2 sources

                        break block10;
                    }
                    updatedArgumentType = argument.resolveType(scope);
                }
                if (updatedArgumentType != null && updatedArgumentType.kind() != 65540) {
                    argumentTypes[i] = updatedArgumentType;
                    if (candidateMethod.isPolymorphic()) {
                        candidateMethod.parameters[i] = updatedArgumentType;
                    }
                }
            }
            ++i;
        }
        if (method.returnType instanceof ReferenceBinding) {
            scope.referenceCompilationUnit().updateLocalTypesInMethod(method);
        }
        if (method instanceof ParameterizedGenericMethodBinding && (ic18 = invocation.getInferenceContext((ParameterizedMethodBinding)method)) != null) {
            ic18.flushBoundOutbox();
        }
        if (problemMethod != null) {
            return problemMethod;
        }
        return method;
    }

    public static void resolveAnnotations(BlockScope scope, Annotation[] sourceAnnotations, Binding recipient) {
        ASTNode.resolveAnnotations(scope, sourceAnnotations, recipient, false);
        if (recipient instanceof SourceTypeBinding) {
            ((SourceTypeBinding)recipient).evaluateNullAnnotations();
        }
    }

    public static AnnotationBinding[] resolveAnnotations(BlockScope scope, Annotation[] sourceAnnotations, Binding recipient, boolean copySE8AnnotationsToType) {
        Annotation annotation;
        LocalVariableBinding local;
        FieldBinding field;
        int length;
        AnnotationBinding[] annotations = null;
        int n = length = sourceAnnotations == null ? 0 : sourceAnnotations.length;
        if (recipient != null) {
            switch (recipient.kind()) {
                case 16: {
                    PackageBinding packageBinding = (PackageBinding)recipient;
                    if ((packageBinding.tagBits & 0x200000000L) != 0L) {
                        return annotations;
                    }
                    packageBinding.tagBits |= 0x600000000L;
                    break;
                }
                case 4: 
                case 2052: {
                    ReferenceBinding type = (ReferenceBinding)recipient;
                    if ((type.tagBits & 0x200000000L) != 0L) {
                        return annotations;
                    }
                    type.tagBits |= 0x600000000L;
                    if (length <= 0) break;
                    annotations = new AnnotationBinding[length];
                    type.setAnnotations(annotations, false);
                    break;
                }
                case 8: {
                    MethodBinding method = (MethodBinding)recipient;
                    if ((method.tagBits & 0x200000000L) != 0L) {
                        return annotations;
                    }
                    method.tagBits |= 0x600000000L;
                    if (length <= 0) break;
                    annotations = new AnnotationBinding[length];
                    method.setAnnotations(annotations, false);
                    break;
                }
                case 1: {
                    field = (FieldBinding)recipient;
                    if ((field.tagBits & 0x200000000L) != 0L) {
                        return annotations;
                    }
                    field.tagBits |= 0x600000000L;
                    if (length <= 0) break;
                    annotations = new AnnotationBinding[length];
                    field.setAnnotations(annotations, false);
                    break;
                }
                case 131072: {
                    RecordComponentBinding rcb = (RecordComponentBinding)recipient;
                    if ((rcb.tagBits & 0x200000000L) != 0L) {
                        return annotations;
                    }
                    rcb.tagBits |= 0x600000000L;
                    if (length <= 0) break;
                    annotations = new AnnotationBinding[length];
                    rcb.setAnnotations(annotations, false);
                    break;
                }
                case 2: {
                    local = (LocalVariableBinding)recipient;
                    if ((local.tagBits & 0x200000000L) != 0L) {
                        return annotations;
                    }
                    local.tagBits |= 0x600000000L;
                    if (length <= 0) break;
                    annotations = new AnnotationBinding[length];
                    local.setAnnotations(annotations, scope, false);
                    break;
                }
                case 4100: 
                case 16388: {
                    annotations = new AnnotationBinding[length];
                    break;
                }
                case 64: {
                    ModuleBinding module = (ModuleBinding)recipient;
                    if ((module.tagBits & 0x200000000L) != 0L) {
                        return annotations;
                    }
                    module.tagBits |= 0x600000000L;
                    if (length <= 0) break;
                    annotations = new AnnotationBinding[length];
                    module.setAnnotations(annotations, scope, false);
                    break;
                }
                default: {
                    return annotations;
                }
            }
        }
        if (sourceAnnotations == null) {
            return annotations;
        }
        int i = 0;
        while (i < length) {
            annotation = sourceAnnotations[i];
            Binding annotationRecipient = annotation.recipient;
            if (annotationRecipient != null && recipient != null) {
                switch (recipient.kind()) {
                    case 16388: {
                        if (annotations == null) break;
                        int j = 0;
                        while (j < length) {
                            annotations[j] = sourceAnnotations[j].getCompilerAnnotation();
                            ++j;
                        }
                        break;
                    }
                    case 1: {
                        field = (FieldBinding)recipient;
                        field.tagBits = ((FieldBinding)annotationRecipient).tagBits;
                        if (annotations == null) break;
                        int j = 0;
                        while (j < length) {
                            Annotation annot = sourceAnnotations[j];
                            annotations[j] = annot.getCompilerAnnotation();
                            ++j;
                        }
                        break;
                    }
                    case 131072: {
                        RecordComponentBinding recordComponentBinding = (RecordComponentBinding)recipient;
                        recordComponentBinding.tagBits = ((RecordComponentBinding)annotationRecipient).tagBits;
                        if (annotations == null) break;
                        int j = 0;
                        while (j < length) {
                            Annotation annot = sourceAnnotations[j];
                            annotations[j] = annot.getCompilerAnnotation();
                            ++j;
                        }
                        break;
                    }
                    case 2: {
                        local = (LocalVariableBinding)recipient;
                        long otherLocalTagBits = ((VariableBinding)annotationRecipient).tagBits;
                        local.tagBits = otherLocalTagBits | local.tagBits & 0x400L;
                        if ((otherLocalTagBits & 0x4000000000000L) == 0L) {
                            if (annotations != null) {
                                int j = 0;
                                while (j < length) {
                                    Annotation annot = sourceAnnotations[j];
                                    annotations[j] = annot.getCompilerAnnotation();
                                    ++j;
                                }
                            }
                        } else if (annotations != null) {
                            LocalDeclaration localDeclaration = local.declaration;
                            int declarationSourceEnd = localDeclaration.declarationSourceEnd;
                            int declarationSourceStart = localDeclaration.declarationSourceStart;
                            int j = 0;
                            while (j < length) {
                                ReferenceBinding annotationType;
                                AnnotationBinding annotationBinding;
                                Annotation annot = sourceAnnotations[j];
                                annotations[j] = annotationBinding = annot.getCompilerAnnotation();
                                if (annotationBinding != null && (annotationType = annotationBinding.getAnnotationType()) != null && annotationType.id == 49) {
                                    annot.recordSuppressWarnings(scope, declarationSourceStart, declarationSourceEnd, scope.compilerOptions().suppressWarnings);
                                }
                                ++j;
                            }
                        }
                        if (!(annotationRecipient instanceof RecordComponentBinding) || !copySE8AnnotationsToType) break;
                        ASTNode.copySE8AnnotationsToType(scope, recipient, sourceAnnotations, false);
                    }
                }
                return annotations;
            }
            annotation.recipient = recipient;
            annotation.resolveType(scope);
            if (annotations != null) {
                annotations[i] = annotation.getCompilerAnnotation();
            }
            ++i;
        }
        if (recipient != null && recipient.isTaggedRepeatable()) {
            i = 0;
            while (i < length) {
                ReferenceBinding annotationType;
                annotation = sourceAnnotations[i];
                ReferenceBinding referenceBinding = annotationType = annotations[i] != null ? annotations[i].getAnnotationType() : null;
                if (annotationType != null && annotationType.id == 90) {
                    annotation.checkRepeatableMetaAnnotation(scope);
                }
                ++i;
            }
        }
        if (annotations != null && length > 1) {
            ReferenceBinding annotationType;
            Object annotation2;
            AnnotationBinding[] distinctAnnotations = annotations;
            HashMap<ReferenceBinding, Annotation> implicitContainerAnnotations = null;
            int i2 = 0;
            while (i2 < length) {
                annotation2 = distinctAnnotations[i2];
                if (annotation2 != null) {
                    annotationType = ((AnnotationBinding)annotation2).getAnnotationType();
                    boolean foundDuplicate = false;
                    ContainerAnnotation container = null;
                    int j = i2 + 1;
                    while (j < length) {
                        AnnotationBinding otherAnnotation = distinctAnnotations[j];
                        if (otherAnnotation != null && TypeBinding.equalsEquals(otherAnnotation.getAnnotationType(), annotationType)) {
                            if (distinctAnnotations == annotations) {
                                AnnotationBinding[] annotationBindingArray = distinctAnnotations;
                                distinctAnnotations = new AnnotationBinding[length];
                                System.arraycopy(annotationBindingArray, 0, distinctAnnotations, 0, length);
                            }
                            distinctAnnotations[j] = null;
                            if (annotationType.isRepeatableAnnotationType()) {
                                Annotation persistibleAnnotation = sourceAnnotations[i2].getPersistibleAnnotation();
                                if (persistibleAnnotation instanceof ContainerAnnotation) {
                                    container = (ContainerAnnotation)persistibleAnnotation;
                                }
                                if (container == null) {
                                    ReferenceBinding containerAnnotationType = annotationType.containerAnnotationType();
                                    container = new ContainerAnnotation(sourceAnnotations[i2], containerAnnotationType, scope);
                                    if (implicitContainerAnnotations == null) {
                                        implicitContainerAnnotations = new HashMap<ReferenceBinding, Annotation>(3);
                                    }
                                    implicitContainerAnnotations.put(containerAnnotationType, sourceAnnotations[i2]);
                                    Annotation.checkForInstancesOfRepeatableWithRepeatingContainerAnnotation(scope, annotationType, sourceAnnotations);
                                }
                                container.addContainee(sourceAnnotations[j]);
                            } else {
                                foundDuplicate = true;
                                scope.problemReporter().duplicateAnnotation(sourceAnnotations[j], scope.compilerOptions().sourceLevel);
                            }
                        }
                        ++j;
                    }
                    if (container != null) {
                        container.resolveType(scope);
                    }
                    if (foundDuplicate) {
                        scope.problemReporter().duplicateAnnotation(sourceAnnotations[i2], scope.compilerOptions().sourceLevel);
                    }
                }
                ++i2;
            }
            if (implicitContainerAnnotations != null) {
                i2 = 0;
                while (i2 < length) {
                    if (distinctAnnotations[i2] != null) {
                        annotation2 = sourceAnnotations[i2];
                        annotationType = distinctAnnotations[i2].getAnnotationType();
                        if (implicitContainerAnnotations.containsKey(annotationType)) {
                            scope.problemReporter().repeatedAnnotationWithContainer((Annotation)implicitContainerAnnotations.get(annotationType), (Annotation)annotation2);
                        }
                    }
                    ++i2;
                }
            }
        }
        if (copySE8AnnotationsToType) {
            ASTNode.copySE8AnnotationsToType(scope, recipient, sourceAnnotations, false);
        }
        return annotations;
    }

    public static TypeBinding resolveAnnotations(BlockScope scope, Annotation[][] sourceAnnotations, TypeBinding type) {
        int levels;
        int n = levels = sourceAnnotations == null ? 0 : sourceAnnotations.length;
        if (type == null || levels == 0) {
            return type;
        }
        AnnotationBinding[][] annotationBindings = new AnnotationBinding[levels][];
        int i = 0;
        while (i < levels) {
            Annotation[] annotations = sourceAnnotations[i];
            if (annotations != null && annotations.length > 0) {
                annotationBindings[i] = ASTNode.resolveAnnotations(scope, annotations, TypeBinding.TYPE_USE_BINDING, false);
            }
            ++i;
        }
        return scope.environment().createAnnotatedType(type, annotationBindings);
    }

    public static void handleNonNullByDefault(BlockScope scope, Annotation[] sourceAnnotations, LocalDeclaration localDeclaration) {
        if (sourceAnnotations == null || sourceAnnotations.length == 0) {
            return;
        }
        int length = sourceAnnotations.length;
        int defaultNullness = 0;
        Annotation lastNNBDAnnotation = null;
        int i = 0;
        while (i < length) {
            Annotation annotation = sourceAnnotations[i];
            long value = annotation.handleNonNullByDefault(scope);
            if (value != 0L) {
                defaultNullness = (int)((long)defaultNullness | value);
                lastNNBDAnnotation = annotation;
            }
            ++i;
        }
        if (defaultNullness != 0) {
            LocalVariableBinding binding = new LocalVariableBinding(localDeclaration, null, 0, false);
            Binding target = scope.checkRedundantDefaultNullness(defaultNullness, localDeclaration.sourceStart);
            boolean recorded = scope.recordNonNullByDefault(binding, defaultNullness, lastNNBDAnnotation, lastNNBDAnnotation.sourceStart, localDeclaration.declarationSourceEnd);
            if (recorded && target != null) {
                scope.problemReporter().nullDefaultAnnotationIsRedundant(localDeclaration, new Annotation[]{lastNNBDAnnotation}, target);
            }
        }
    }

    public static void copySE8AnnotationsToType(BlockScope scope, Binding recipient, Annotation[] annotations, boolean annotatingEnumerator) {
        if (annotations == null || annotations.length == 0 || recipient == null) {
            return;
        }
        long recipientTargetMask = 0L;
        switch (recipient.kind()) {
            case 2: {
                recipientTargetMask = recipient.isParameter() ? 0x8000000000L : 0x20000000000L;
                break;
            }
            case 1: {
                recipientTargetMask = 0x2000000000L;
                break;
            }
            case 8: {
                MethodBinding method = (MethodBinding)recipient;
                recipientTargetMask = method.isConstructor() ? 0x10000000000L : 0x4000000000L;
                break;
            }
            case 131072: {
                recipientTargetMask = 0x40000000L;
                break;
            }
            default: {
                return;
            }
        }
        AnnotationBinding[] se8Annotations = null;
        int se8count = 0;
        long se8nullBits = 0L;
        Annotation se8NullAnnotation = null;
        int firstSE8 = -1;
        int i = 0;
        int length = annotations.length;
        while (i < length) {
            ReferenceBinding annotationType;
            long metaTagBits;
            AnnotationBinding annotation = annotations[i].getCompilerAnnotation();
            if (annotation != null && ((metaTagBits = (annotationType = annotation.getAnnotationType()).getAnnotationTagBits()) & 0x20000000000000L) != 0L) {
                if (annotatingEnumerator) {
                    if ((metaTagBits & recipientTargetMask) == 0L) {
                        scope.problemReporter().misplacedTypeAnnotations(annotations[i], annotations[i]);
                    }
                } else {
                    if (firstSE8 == -1) {
                        firstSE8 = i;
                    }
                    if (se8Annotations == null) {
                        se8Annotations = new AnnotationBinding[]{annotation};
                        se8count = 1;
                    } else {
                        AnnotationBinding[] annotationBindingArray = se8Annotations;
                        se8Annotations = new AnnotationBinding[se8count + 1];
                        System.arraycopy(annotationBindingArray, 0, se8Annotations, 0, se8count);
                        se8Annotations[se8count++] = annotation;
                    }
                    if (annotationType.hasNullBit(32)) {
                        se8nullBits |= 0x100000000000000L;
                        se8NullAnnotation = annotations[i];
                    } else if (annotationType.hasNullBit(64)) {
                        se8nullBits |= 0x80000000000000L;
                        se8NullAnnotation = annotations[i];
                    }
                }
            }
            ++i;
        }
        if (se8Annotations != null) {
            switch (recipient.kind()) {
                case 2: {
                    LocalVariableBinding local = (LocalVariableBinding)recipient;
                    TypeReference typeRef = local.declaration.type;
                    if (!Annotation.isTypeUseCompatible(typeRef, scope)) break;
                    local.declaration.bits |= 0x100000;
                    typeRef.bits |= 0x100000;
                    local.type = ASTNode.mergeAnnotationsIntoType(scope, se8Annotations, se8nullBits, se8NullAnnotation, typeRef, local.type);
                    if (!scope.environment().usesNullTypeAnnotations()) break;
                    local.tagBits &= se8nullBits ^ 0xFFFFFFFFFFFFFFFFL;
                    break;
                }
                case 1: {
                    FieldBinding field = (FieldBinding)recipient;
                    SourceTypeBinding sourceType = (SourceTypeBinding)field.declaringClass;
                    FieldDeclaration fieldDeclaration = sourceType.scope.referenceContext.declarationOf(field);
                    if (!Annotation.isTypeUseCompatible(fieldDeclaration.type, scope)) break;
                    fieldDeclaration.bits |= 0x100000;
                    fieldDeclaration.type.bits |= 0x100000;
                    field.type = ASTNode.mergeAnnotationsIntoType(scope, se8Annotations, se8nullBits, se8NullAnnotation, fieldDeclaration.type, field.type);
                    if (!scope.environment().usesNullTypeAnnotations()) break;
                    field.tagBits &= se8nullBits ^ 0xFFFFFFFFFFFFFFFFL;
                    break;
                }
                case 131072: {
                    RecordComponentBinding recordComponentBinding = (RecordComponentBinding)recipient;
                    RecordComponent recordComponent = recordComponentBinding.sourceRecordComponent();
                    if (!Annotation.isTypeUseCompatible(recordComponent.type, scope)) break;
                    recordComponent.bits |= 0x100000;
                    recordComponent.type.bits |= 0x100000;
                    recordComponentBinding.type = ASTNode.mergeAnnotationsIntoType(scope, se8Annotations, se8nullBits, se8NullAnnotation, recordComponent.type, recordComponentBinding.type);
                    if (!scope.environment().usesNullTypeAnnotations()) break;
                    recordComponentBinding.tagBits &= se8nullBits ^ 0xFFFFFFFFFFFFFFFFL;
                    break;
                }
                case 8: {
                    SourceTypeBinding sourceType;
                    MethodBinding method = (MethodBinding)recipient;
                    if (!method.isConstructor()) {
                        sourceType = (SourceTypeBinding)method.declaringClass;
                        MethodDeclaration methodDecl = (MethodDeclaration)sourceType.scope.referenceContext.declarationOf(method);
                        if (!Annotation.isTypeUseCompatible(methodDecl.returnType, scope)) break;
                        methodDecl.bits |= 0x100000;
                        methodDecl.returnType.bits |= 0x100000;
                        method.returnType = ASTNode.mergeAnnotationsIntoType(scope, se8Annotations, se8nullBits, se8NullAnnotation, methodDecl.returnType, method.returnType);
                        if (!scope.environment().usesNullTypeAnnotations()) break;
                        method.tagBits &= se8nullBits ^ 0xFFFFFFFFFFFFFFFFL;
                        break;
                    }
                    method.setTypeAnnotations(se8Annotations);
                }
            }
            AnnotationBinding[] recipientAnnotations = recipient.getAnnotations();
            length = recipientAnnotations == null ? 0 : recipientAnnotations.length;
            int newLength = 0;
            int i2 = 0;
            while (i2 < length) {
                long annotationTargetMask;
                AnnotationBinding recipientAnnotation = recipientAnnotations[i2];
                if (recipientAnnotation != null && ((annotationTargetMask = recipientAnnotation.getAnnotationType().getAnnotationTagBits() & 0x20600FF840000000L) == 0L || (annotationTargetMask & recipientTargetMask) != 0L)) {
                    recipientAnnotations[newLength++] = recipientAnnotation;
                }
                ++i2;
            }
            if (newLength != length) {
                AnnotationBinding[] annotationBindingArray = recipientAnnotations;
                recipientAnnotations = new AnnotationBinding[newLength];
                System.arraycopy(annotationBindingArray, 0, recipientAnnotations, 0, newLength);
                recipient.setAnnotations(recipientAnnotations, scope, false);
            }
        }
    }

    public static Annotation[] getRelevantAnnotations(Annotation[] annotations, long rcMask, List<AnnotationBinding> relevantAnnotations) {
        if (annotations == null || annotations.length == 0) {
            return null;
        }
        ArrayList<Annotation> filteredAnnotations = new ArrayList<Annotation>();
        Annotation[] annotationArray = annotations;
        int n = annotations.length;
        int n2 = 0;
        while (n2 < n) {
            ReferenceBinding annotationType;
            long metaTagBits;
            Annotation annot = annotationArray[n2];
            AnnotationBinding annotationBinding = annot.getCompilerAnnotation();
            if (annotationBinding != null && (((metaTagBits = (annotationType = annotationBinding.getAnnotationType()).getAnnotationTagBits()) & 0x20600FF840000000L) == 0L || (metaTagBits & rcMask) != 0L)) {
                filteredAnnotations.add(annot);
                if (relevantAnnotations != null) {
                    relevantAnnotations.add(annotationBinding);
                }
            }
            ++n2;
        }
        return filteredAnnotations.toArray(new Annotation[0]);
    }

    public static Annotation[] copyRecordComponentAnnotations(Scope scope, Binding recipient, Annotation[] annotations) {
        if (annotations == null || annotations.length == 0 || recipient == null) {
            return null;
        }
        long recipientTargetMask = 0L;
        switch (recipient.kind()) {
            case 2: {
                assert (recipient.isParameter());
                recipientTargetMask = recipient.isParameter() ? 0x8000000000L : 0x20000000000L;
                break;
            }
            case 1: {
                recipientTargetMask = 0x2000000000L;
                break;
            }
            case 8: {
                MethodBinding method = (MethodBinding)recipient;
                recipientTargetMask = method.isConstructor() ? 0x10000000000L : 0x4000000000L;
                break;
            }
            case 131072: {
                recipientTargetMask = 0x40000000L;
                break;
            }
            default: {
                return null;
            }
        }
        ArrayList<AnnotationBinding> relevantAnnotations = new ArrayList<AnnotationBinding>();
        Annotation[] filteredAnnotations = ASTNode.getRelevantAnnotations(annotations, recipientTargetMask, relevantAnnotations);
        AnnotationBinding[] recipientAnnotations = relevantAnnotations.toArray(new AnnotationBinding[relevantAnnotations.size()]);
        recipient.setAnnotations(recipientAnnotations, scope, true);
        return filteredAnnotations;
    }

    private static TypeBinding mergeAnnotationsIntoType(BlockScope scope, AnnotationBinding[] se8Annotations, long se8nullBits, Annotation se8NullAnnotation, TypeReference typeRef, TypeBinding existingType) {
        TypeBinding oldLeafType;
        if (existingType == null || !existingType.isValidBinding()) {
            return existingType;
        }
        TypeReference unionRef = typeRef.isUnionType() ? ((UnionTypeReference)typeRef).typeReferences[0] : null;
        TypeBinding typeBinding = oldLeafType = unionRef == null ? existingType.leafComponentType() : unionRef.resolvedType;
        if (se8nullBits != 0L && typeRef instanceof ArrayTypeReference) {
            ArrayTypeReference arrayTypeReference = (ArrayTypeReference)typeRef;
            if (arrayTypeReference.leafComponentTypeWithoutDefaultNullness != null) {
                oldLeafType = arrayTypeReference.leafComponentTypeWithoutDefaultNullness;
            }
        }
        if (se8nullBits != 0L && oldLeafType.isBaseType()) {
            scope.problemReporter().illegalAnnotationForBaseType(typeRef, new Annotation[]{se8NullAnnotation}, se8nullBits);
            return existingType;
        }
        long prevNullBits = oldLeafType.tagBits & 0x180000000000000L;
        if ((prevNullBits | se8nullBits) == 0x180000000000000L) {
            if (!(oldLeafType instanceof TypeVariableBinding)) {
                if (prevNullBits != 0x180000000000000L && se8nullBits != 0x180000000000000L) {
                    scope.problemReporter().contradictoryNullAnnotations(se8NullAnnotation);
                }
                se8Annotations = Binding.NO_ANNOTATIONS;
                se8nullBits = 0L;
            }
            oldLeafType = oldLeafType.withoutToplevelNullAnnotation();
        }
        AnnotationBinding[][] goodies = new AnnotationBinding[typeRef.getAnnotatableLevels()][];
        goodies[0] = se8Annotations;
        TypeBinding newLeafType = scope.environment().createAnnotatedType(oldLeafType, goodies);
        if (unionRef == null) {
            typeRef.resolvedType = existingType.isArrayType() ? scope.environment().createArrayType(newLeafType, existingType.dimensions(), existingType.getTypeAnnotations()) : newLeafType;
        } else {
            unionRef.resolvedType = newLeafType;
            unionRef.bits |= 0x100000;
        }
        return typeRef.resolvedType;
    }

    public static void resolveDeprecatedAnnotations(BlockScope scope, Annotation[] annotations, Binding recipient) {
        if (recipient != null) {
            int length;
            int kind = recipient.kind();
            if (annotations != null && (length = annotations.length) >= 0) {
                switch (kind) {
                    case 16: {
                        PackageBinding packageBinding = (PackageBinding)recipient;
                        if ((packageBinding.tagBits & 0x400000000L) == 0L) break;
                        return;
                    }
                    case 4: 
                    case 2052: {
                        ReferenceBinding type = (ReferenceBinding)recipient;
                        if ((type.tagBits & 0x400000000L) == 0L) break;
                        return;
                    }
                    case 8: {
                        MethodBinding method = (MethodBinding)recipient;
                        if ((method.tagBits & 0x400000000L) == 0L) break;
                        return;
                    }
                    case 1: {
                        FieldBinding field = (FieldBinding)recipient;
                        if ((field.tagBits & 0x400000000L) == 0L) break;
                        return;
                    }
                    case 2: {
                        LocalVariableBinding local = (LocalVariableBinding)recipient;
                        if ((local.tagBits & 0x400000000L) == 0L) break;
                        return;
                    }
                    case 131072: {
                        RecordComponentBinding recordComponentBinding = (RecordComponentBinding)recipient;
                        if ((recordComponentBinding.tagBits & 0x400000000L) == 0L) break;
                        return;
                    }
                    default: {
                        return;
                    }
                }
                int i = 0;
                while (i < length) {
                    TypeBinding annotationType;
                    TypeReference annotationTypeRef = annotations[i].type;
                    if (CharOperation.equals(TypeConstants.JAVA_LANG_DEPRECATED[2], annotationTypeRef.getLastToken()) && (annotationType = annotations[i].type.resolveType(scope)) != null && annotationType.isValidBinding() && annotationType.id == 44) {
                        long deprecationTagBits = 0x400400000000L;
                        if (scope.compilerOptions().complianceLevel >= 0x350000L) {
                            MemberValuePair[] memberValuePairArray = annotations[i].memberValuePairs();
                            int n = memberValuePairArray.length;
                            int n2 = 0;
                            while (n2 < n) {
                                MemberValuePair memberValuePair = memberValuePairArray[n2];
                                if (CharOperation.equals(memberValuePair.name, TypeConstants.FOR_REMOVAL)) {
                                    if (!(memberValuePair.value instanceof TrueLiteral)) break;
                                    deprecationTagBits |= 0x4000000000000000L;
                                    break;
                                }
                                ++n2;
                            }
                        }
                        switch (kind) {
                            case 16: {
                                PackageBinding packageBinding = (PackageBinding)recipient;
                                packageBinding.tagBits |= deprecationTagBits;
                                return;
                            }
                            case 4: 
                            case 2052: 
                            case 4100: {
                                ReferenceBinding type = (ReferenceBinding)recipient;
                                type.tagBits |= deprecationTagBits;
                                return;
                            }
                            case 8: {
                                MethodBinding method = (MethodBinding)recipient;
                                method.tagBits |= deprecationTagBits;
                                return;
                            }
                            case 1: {
                                FieldBinding field = (FieldBinding)recipient;
                                field.tagBits |= deprecationTagBits;
                                return;
                            }
                            case 2: {
                                LocalVariableBinding local = (LocalVariableBinding)recipient;
                                local.tagBits |= deprecationTagBits;
                                return;
                            }
                            case 131072: {
                                RecordComponentBinding recordComponentBinding = (RecordComponentBinding)recipient;
                                recordComponentBinding.tagBits |= deprecationTagBits;
                                return;
                            }
                        }
                        return;
                    }
                    ++i;
                }
            }
            switch (kind) {
                case 16: {
                    PackageBinding packageBinding = (PackageBinding)recipient;
                    packageBinding.tagBits |= 0x400000000L;
                    return;
                }
                case 4: 
                case 2052: 
                case 4100: {
                    ReferenceBinding type = (ReferenceBinding)recipient;
                    type.tagBits |= 0x400000000L;
                    return;
                }
                case 8: {
                    MethodBinding method = (MethodBinding)recipient;
                    method.tagBits |= 0x400000000L;
                    return;
                }
                case 1: {
                    FieldBinding field = (FieldBinding)recipient;
                    field.tagBits |= 0x400000000L;
                    return;
                }
                case 2: {
                    LocalVariableBinding local = (LocalVariableBinding)recipient;
                    local.tagBits |= 0x400000000L;
                    return;
                }
                case 131072: {
                    RecordComponentBinding recordComponentBinding = (RecordComponentBinding)recipient;
                    recordComponentBinding.tagBits |= 0x400000000L;
                    return;
                }
            }
            return;
        }
    }

    public boolean checkingPotentialCompatibility() {
        return false;
    }

    public void acceptPotentiallyCompatibleMethods(MethodBinding[] methods) {
    }

    public int sourceStart() {
        return this.sourceStart;
    }

    public int sourceEnd() {
        return this.sourceEnd;
    }

    public String toString() {
        return this.print(0, new StringBuffer(30)).toString();
    }

    public void traverse(ASTVisitor visitor, BlockScope scope) {
    }
}

