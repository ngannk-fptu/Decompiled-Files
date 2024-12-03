/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import java.util.HashMap;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ExpressionContext;
import org.eclipse.jdt.internal.compiler.ast.FakedTrackingVariable;
import org.eclipse.jdt.internal.compiler.ast.FunctionalExpression;
import org.eclipse.jdt.internal.compiler.ast.IPolyExpression;
import org.eclipse.jdt.internal.compiler.ast.Invocation;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.NameReference;
import org.eclipse.jdt.internal.compiler.ast.NullAnnotationMatching;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedSuperReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.UnlikelyArgumentCheck;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.codegen.ConstantPool;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.flow.FieldInitsFakingFlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.impl.IrritantSet;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ImplicitNullAnnotationVerifier;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedGenericMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.PolyTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticArgumentBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.parser.Scanner;

public class ReferenceExpression
extends FunctionalExpression
implements IPolyExpression,
InvocationSite {
    private static final String SecretReceiverVariableName = " rec_";
    private static final char[] ImplicitArgName = " arg".toCharArray();
    public LocalVariableBinding receiverVariable;
    public Expression lhs;
    public TypeReference[] typeArguments;
    public char[] selector;
    public int nameSourceStart;
    public TypeBinding receiverType;
    public boolean haveReceiver;
    public TypeBinding[] resolvedTypeArguments;
    private boolean typeArgumentsHaveErrors;
    MethodBinding syntheticAccessor;
    private int depth;
    private MethodBinding exactMethodBinding;
    private boolean receiverPrecedesParameters = false;
    private TypeBinding[] freeParameters;
    private boolean checkingPotentialCompatibility;
    private MethodBinding[] potentialMethods = Binding.NO_METHODS;
    protected ReferenceExpression original = this;
    private HashMap<TypeBinding, ReferenceExpression> copiesPerTargetType;
    public char[] text;
    private HashMap<ParameterizedGenericMethodBinding, InferenceContext18> inferenceContexts;
    private Scanner scanner;

    public ReferenceExpression(Scanner scanner) {
        this.scanner = scanner;
    }

    public void initialize(CompilationResult result, Expression expression, TypeReference[] optionalTypeArguments, char[] identifierOrNew, int sourceEndPosition) {
        super.setCompilationResult(result);
        this.lhs = expression;
        this.typeArguments = optionalTypeArguments;
        this.selector = identifierOrNew;
        this.sourceStart = expression.sourceStart;
        this.sourceEnd = sourceEndPosition;
    }

    private ReferenceExpression copy() {
        Parser parser = new Parser(this.enclosingScope.problemReporter(), false);
        ICompilationUnit compilationUnit = this.compilationResult.getCompilationUnit();
        char[] source = compilationUnit != null ? compilationUnit.getContents() : this.text;
        parser.scanner = this.scanner;
        ReferenceExpression copy = (ReferenceExpression)parser.parseExpression(source, compilationUnit != null ? this.sourceStart : 0, this.sourceEnd - this.sourceStart + 1, this.enclosingScope.referenceCompilationUnit(), false);
        copy.original = this;
        copy.sourceStart = this.sourceStart;
        copy.sourceEnd = this.sourceEnd;
        return copy;
    }

    private boolean shouldGenerateSecretReceiverVariable() {
        if (this.isMethodReference() && this.haveReceiver) {
            if (this.lhs instanceof Invocation) {
                return true;
            }
            return new ASTVisitor(){
                boolean accessesnonFinalOuterLocals;

                @Override
                public boolean visit(SingleNameReference name, BlockScope skope) {
                    LocalVariableBinding localBinding;
                    Binding local = skope.getBinding(name.getName(), ReferenceExpression.this);
                    if (local instanceof LocalVariableBinding && !(localBinding = (LocalVariableBinding)local).isFinal() && !localBinding.isEffectivelyFinal()) {
                        this.accessesnonFinalOuterLocals = true;
                    }
                    return false;
                }

                public boolean accessesnonFinalOuterLocals() {
                    ReferenceExpression.this.lhs.traverse((ASTVisitor)this, ReferenceExpression.this.enclosingScope);
                    return this.accessesnonFinalOuterLocals;
                }
            }.accessesnonFinalOuterLocals();
        }
        return false;
    }

    public void generateImplicitLambda(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
        ReferenceExpression copy = this.copy();
        int argc = this.descriptor.parameters.length;
        LambdaExpression implicitLambda = new LambdaExpression(this.compilationResult, false, (this.binding.modifiers & 0x40000000) != 0);
        Argument[] arguments = new Argument[argc];
        int i = 0;
        while (i < argc) {
            arguments[i] = new Argument(CharOperation.append(ImplicitArgName, Integer.toString(i).toCharArray()), 0L, null, 0, true);
            ++i;
        }
        implicitLambda.setArguments(arguments);
        implicitLambda.setExpressionContext(this.expressionContext);
        implicitLambda.setExpectedType(this.expectedType);
        int parameterShift = this.receiverPrecedesParameters ? 1 : 0;
        SingleNameReference[] argv = new SingleNameReference[argc - parameterShift];
        int i2 = 0;
        int length = argv.length;
        while (i2 < length) {
            char[] name = CharOperation.append(ImplicitArgName, Integer.toString(i2 + parameterShift).toCharArray());
            argv[i2] = new SingleNameReference(name, 0L);
            ++i2;
        }
        boolean generateSecretReceiverVariable = this.shouldGenerateSecretReceiverVariable();
        if (this.isMethodReference()) {
            if (generateSecretReceiverVariable) {
                this.lhs.generateCode(currentScope, codeStream, true);
                codeStream.store(this.receiverVariable, false);
                codeStream.addVariable(this.receiverVariable);
            }
            MessageSend message = new MessageSend();
            message.selector = this.selector;
            Expression receiver = generateSecretReceiverVariable ? new SingleNameReference(this.receiverVariable.name, 0L) : copy.lhs;
            message.receiver = this.receiverPrecedesParameters ? new SingleNameReference(CharOperation.append(ImplicitArgName, Integer.toString(0).toCharArray()), 0L) : receiver;
            message.typeArguments = copy.typeArguments;
            message.arguments = argv;
            implicitLambda.setBody(message);
        } else if (this.isArrayConstructorReference()) {
            ArrayAllocationExpression arrayAllocationExpression = new ArrayAllocationExpression();
            arrayAllocationExpression.dimensions = new Expression[]{argv[0]};
            if (this.lhs instanceof ArrayTypeReference) {
                ArrayTypeReference arrayTypeReference = (ArrayTypeReference)this.lhs;
                arrayAllocationExpression.type = arrayTypeReference.dimensions == 1 ? new SingleTypeReference(arrayTypeReference.token, 0L) : new ArrayTypeReference(arrayTypeReference.token, arrayTypeReference.dimensions - 1, 0L);
            } else {
                ArrayQualifiedTypeReference arrayQualifiedTypeReference = (ArrayQualifiedTypeReference)this.lhs;
                arrayAllocationExpression.type = arrayQualifiedTypeReference.dimensions == 1 ? new QualifiedTypeReference(arrayQualifiedTypeReference.tokens, arrayQualifiedTypeReference.sourcePositions) : new ArrayQualifiedTypeReference(arrayQualifiedTypeReference.tokens, arrayQualifiedTypeReference.dimensions - 1, arrayQualifiedTypeReference.sourcePositions);
            }
            implicitLambda.setBody(arrayAllocationExpression);
        } else {
            AllocationExpression allocation = new AllocationExpression();
            if (this.lhs instanceof TypeReference) {
                allocation.type = (TypeReference)this.lhs;
            } else if (this.lhs instanceof SingleNameReference) {
                allocation.type = new SingleTypeReference(((SingleNameReference)this.lhs).token, 0L);
            } else if (this.lhs instanceof QualifiedNameReference) {
                allocation.type = new QualifiedTypeReference(((QualifiedNameReference)this.lhs).tokens, new long[((QualifiedNameReference)this.lhs).tokens.length]);
            } else {
                throw new IllegalStateException("Unexpected node type");
            }
            allocation.typeArguments = copy.typeArguments;
            allocation.arguments = argv;
            implicitLambda.setBody(allocation);
        }
        BlockScope lambdaScope = this.receiverVariable != null ? this.receiverVariable.declaringScope : currentScope;
        IErrorHandlingPolicy oldPolicy = lambdaScope.problemReporter().switchErrorHandlingPolicy(silentErrorHandlingPolicy);
        try {
            implicitLambda.resolveType(lambdaScope, true);
            implicitLambda.analyseCode(lambdaScope, new FieldInitsFakingFlowContext(null, this, Binding.NO_EXCEPTIONS, null, lambdaScope, FlowInfo.DEAD_END), UnconditionalFlowInfo.fakeInitializedFlowInfo(lambdaScope.outerMostMethodScope().analysisIndex, lambdaScope.referenceType().maxFieldCount));
        }
        finally {
            lambdaScope.problemReporter().switchErrorHandlingPolicy(oldPolicy);
        }
        SyntheticArgumentBinding[] outerLocals = this.receiverType.syntheticOuterLocalVariables();
        int i3 = 0;
        int length2 = outerLocals == null ? 0 : outerLocals.length;
        while (i3 < length2) {
            implicitLambda.addSyntheticArgument(outerLocals[i3].actualOuterLocalVariable);
            ++i3;
        }
        implicitLambda.generateCode(lambdaScope, codeStream, valueRequired);
        if (generateSecretReceiverVariable) {
            codeStream.removeVariable(this.receiverVariable);
        }
    }

    private boolean shouldGenerateImplicitLambda(BlockScope currentScope) {
        return this.binding.isVarargs() || this.isConstructorReference() && this.receiverType.syntheticOuterLocalVariables() != null && this.shouldCaptureInstance || this.requiresBridges() || !this.isDirectCodeGenPossible();
    }

    private boolean isDirectCodeGenPossible() {
        if (this.binding != null) {
            if (this.isMethodReference() && this.syntheticAccessor == null && TypeBinding.notEquals(this.binding.declaringClass, this.lhs.resolvedType.erasure()) && !this.binding.declaringClass.canBeSeenBy(this.enclosingScope)) {
                if (this.binding.isDefaultMethod()) {
                    return false;
                }
                return !this.binding.isFinal() && !this.binding.isStatic();
            }
            TypeBinding[] descriptorParams = this.descriptor.parameters;
            TypeBinding[] origParams = this.binding.original().parameters;
            TypeBinding[] origDescParams = this.descriptor.original().parameters;
            int offset = this.receiverPrecedesParameters ? 1 : 0;
            int i = 0;
            while (i < descriptorParams.length - offset) {
                TypeBinding descType = descriptorParams[i + offset];
                TypeBinding origDescType = origDescParams[i + offset];
                if (descType.isIntersectionType18() || descType.isTypeVariable() && ((TypeVariableBinding)descType).boundsCount() > 1) {
                    return CharOperation.equals(origDescType.signature(), origParams[i].signature());
                }
                ++i;
            }
        }
        return true;
    }

    @Override
    public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
        this.actualMethodBinding = this.binding;
        if (this.shouldGenerateImplicitLambda(currentScope)) {
            this.generateImplicitLambda(currentScope, codeStream, valueRequired);
            return;
        }
        SourceTypeBinding sourceType = currentScope.enclosingSourceType();
        if (this.receiverType.isArrayType()) {
            char[] lambdaName = CharOperation.concat(TypeConstants.ANONYMOUS_METHOD, Integer.toString(this.ordinal).toCharArray());
            if (this.isConstructorReference()) {
                this.actualMethodBinding = this.binding = sourceType.addSyntheticArrayMethod((ArrayBinding)this.receiverType, 14, lambdaName);
            } else if (CharOperation.equals(this.selector, TypeConstants.CLONE)) {
                this.actualMethodBinding = this.binding = sourceType.addSyntheticArrayMethod((ArrayBinding)this.receiverType, 15, lambdaName);
            }
        } else if (this.syntheticAccessor != null) {
            if (this.lhs.isSuper() || this.isMethodReference()) {
                this.binding = this.syntheticAccessor;
            }
        } else if (this.binding != null && this.isMethodReference() && TypeBinding.notEquals(this.binding.declaringClass, this.lhs.resolvedType.erasure()) && !this.binding.declaringClass.canBeSeenBy(currentScope)) {
            this.binding = new MethodBinding(this.binding.original(), (ReferenceBinding)this.lhs.resolvedType.erasure());
        }
        int pc = codeStream.position;
        StringBuffer buffer = new StringBuffer();
        int argumentsSize = 0;
        buffer.append('(');
        if (this.haveReceiver) {
            this.lhs.generateCode(currentScope, codeStream, true);
            if (this.isMethodReference() && !this.lhs.isThis() && !this.lhs.isSuper()) {
                MethodBinding mb = currentScope.getJavaLangObject().getExactMethod(TypeConstants.GETCLASS, Binding.NO_PARAMETERS, currentScope.compilationUnitScope());
                codeStream.dup();
                codeStream.invoke((byte)-74, mb, mb.declaringClass);
                codeStream.pop();
            }
            if (this.lhs.isSuper() && !this.actualMethodBinding.isPrivate()) {
                if (this.lhs instanceof QualifiedSuperReference) {
                    QualifiedSuperReference qualifiedSuperReference = (QualifiedSuperReference)this.lhs;
                    TypeReference qualification = qualifiedSuperReference.qualification;
                    if (qualification.resolvedType.isInterface()) {
                        buffer.append(sourceType.signature());
                    } else {
                        buffer.append(((QualifiedSuperReference)this.lhs).currentCompatibleType.signature());
                    }
                } else {
                    buffer.append(sourceType.signature());
                }
            } else {
                buffer.append(this.receiverType.signature());
            }
            argumentsSize = 1;
        } else if (this.isConstructorReference()) {
            TypeBinding[] enclosingInstances = Binding.UNINITIALIZED_REFERENCE_TYPES;
            if (this.receiverType.isNestedType()) {
                ReferenceBinding nestedType = (ReferenceBinding)this.receiverType;
                enclosingInstances = nestedType.syntheticEnclosingInstanceTypes();
                if (enclosingInstances != null) {
                    int length;
                    argumentsSize = length = enclosingInstances.length;
                    int i = 0;
                    while (i < length) {
                        TypeBinding syntheticArgumentType = enclosingInstances[i];
                        buffer.append(((ReferenceBinding)syntheticArgumentType).signature());
                        Object[] emulationPath = currentScope.getEmulationPath((ReferenceBinding)syntheticArgumentType, false, true);
                        codeStream.generateOuterAccess(emulationPath, this, syntheticArgumentType, currentScope);
                        ++i;
                    }
                } else {
                    enclosingInstances = Binding.NO_REFERENCE_TYPES;
                }
            }
            if (this.syntheticAccessor != null) {
                char[] lambdaName = CharOperation.concat(TypeConstants.ANONYMOUS_METHOD, Integer.toString(this.ordinal).toCharArray());
                this.binding = sourceType.addSyntheticFactoryMethod(this.binding, this.syntheticAccessor, enclosingInstances, lambdaName);
                this.syntheticAccessor = null;
            }
        }
        buffer.append(')');
        buffer.append('L');
        if (this.resolvedType.isIntersectionType18()) {
            buffer.append(this.descriptor.declaringClass.constantPoolName());
        } else {
            buffer.append(this.resolvedType.constantPoolName());
        }
        buffer.append(';');
        if (this.isSerializable) {
            sourceType.addSyntheticMethod(this);
        }
        int invokeDynamicNumber = codeStream.classFile.recordBootstrapMethod(this);
        codeStream.invokeDynamic(invokeDynamicNumber, argumentsSize, 1, this.descriptor.selector, buffer.toString().toCharArray(), this.isConstructorReference(), this.lhs instanceof TypeReference ? (TypeReference)this.lhs : null, this.typeArguments, this.resolvedType.id, this.resolvedType);
        if (!valueRequired) {
            codeStream.pop();
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }

    @Override
    public void cleanUp() {
        if (this.copiesPerTargetType != null) {
            for (ReferenceExpression copy : this.copiesPerTargetType.values()) {
                copy.scanner = null;
            }
        }
        if (this.original != null && this.original != this) {
            this.original.cleanUp();
        }
        this.scanner = null;
        this.receiverVariable = null;
    }

    public void manageSyntheticAccessIfNecessary(BlockScope currentScope, FlowInfo flowInfo) {
        if ((flowInfo.tagBits & 1) != 0 || this.binding == null || !this.binding.isValidBinding()) {
            return;
        }
        MethodBinding codegenBinding = this.binding.original();
        if (codegenBinding.isVarargs()) {
            return;
        }
        SourceTypeBinding enclosingSourceType = currentScope.enclosingSourceType();
        if (this.isConstructorReference()) {
            ReferenceBinding allocatedType = codegenBinding.declaringClass;
            if (codegenBinding.isPrivate() && TypeBinding.notEquals(enclosingSourceType, allocatedType = codegenBinding.declaringClass)) {
                if ((allocatedType.tagBits & 0x10L) != 0L) {
                    codegenBinding.tagBits |= 0x200L;
                } else {
                    if (currentScope.enclosingSourceType().isNestmateOf(this.binding.declaringClass)) {
                        this.syntheticAccessor = codegenBinding;
                        return;
                    }
                    this.syntheticAccessor = ((SourceTypeBinding)allocatedType).addSyntheticMethod(codegenBinding, false);
                    currentScope.problemReporter().needToEmulateMethodAccess(codegenBinding, this);
                }
            }
            return;
        }
        if (this.binding.isPrivate()) {
            if (TypeBinding.notEquals(enclosingSourceType, codegenBinding.declaringClass)) {
                this.syntheticAccessor = ((SourceTypeBinding)codegenBinding.declaringClass).addSyntheticMethod(codegenBinding, false);
                currentScope.problemReporter().needToEmulateMethodAccess(codegenBinding, this);
            }
            return;
        }
        if (this.lhs.isSuper()) {
            SourceTypeBinding destinationType = enclosingSourceType;
            if (this.lhs instanceof QualifiedSuperReference) {
                QualifiedSuperReference qualifiedSuperReference = (QualifiedSuperReference)this.lhs;
                TypeReference qualification = qualifiedSuperReference.qualification;
                if (!qualification.resolvedType.isInterface()) {
                    destinationType = (SourceTypeBinding)qualifiedSuperReference.currentCompatibleType;
                }
            }
            this.syntheticAccessor = destinationType.addSyntheticMethod(codegenBinding, true);
            currentScope.problemReporter().needToEmulateMethodAccess(codegenBinding, this);
            return;
        }
        if (this.binding.isProtected() && (this.bits & 0x1FE0) != 0 && codegenBinding.declaringClass.getPackage() != enclosingSourceType.getPackage()) {
            SourceTypeBinding currentCompatibleType = (SourceTypeBinding)enclosingSourceType.enclosingTypeAt((this.bits & 0x1FE0) >> 5);
            this.syntheticAccessor = currentCompatibleType.addSyntheticMethod(codegenBinding, this.isSuperAccess());
            currentScope.problemReporter().needToEmulateMethodAccess(codegenBinding, this);
            return;
        }
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        FakedTrackingVariable trackingVariable;
        TypeBinding type;
        if (this.haveReceiver) {
            this.lhs.analyseCode(currentScope, flowContext, flowInfo, true);
            this.lhs.checkNPE(currentScope, flowContext, flowInfo);
        } else if (this.isConstructorReference() && (type = this.receiverType.leafComponentType()).isNestedType() && type instanceof ReferenceBinding && !((ReferenceBinding)type).isStatic()) {
            currentScope.tagAsAccessingEnclosingInstanceStateOf((ReferenceBinding)type, false);
            this.shouldCaptureInstance = true;
            ReferenceBinding allocatedTypeErasure = (ReferenceBinding)type.erasure();
            if (allocatedTypeErasure.isLocalType()) {
                ((LocalTypeBinding)allocatedTypeErasure).addInnerEmulationDependent(currentScope, false);
            }
        }
        if (currentScope.compilerOptions().isAnyEnabled(IrritantSet.UNLIKELY_ARGUMENT_TYPE) && this.binding.isValidBinding() && this.binding != null && this.binding.parameters != null) {
            TypeBinding argumentType1;
            TypeBinding argumentType2;
            UnlikelyArgumentCheck argumentCheck;
            if (this.binding.parameters.length == 1 && this.descriptor.parameters.length == (this.receiverPrecedesParameters ? 2 : 1) && !this.binding.isStatic()) {
                ReferenceBinding actualReceiverType;
                TypeBinding argumentType = this.descriptor.parameters[this.receiverPrecedesParameters ? 1 : 0];
                UnlikelyArgumentCheck argumentCheck2 = UnlikelyArgumentCheck.determineCheckForNonStaticSingleArgumentMethod(argumentType, currentScope, this.selector, actualReceiverType = this.receiverPrecedesParameters ? this.descriptor.parameters[0] : this.binding.declaringClass, this.binding.parameters);
                if (argumentCheck2 != null && argumentCheck2.isDangerous(currentScope)) {
                    currentScope.problemReporter().unlikelyArgumentType(this, this.binding, argumentType, argumentCheck2.typeToReport, argumentCheck2.dangerousMethod);
                }
            } else if (this.binding.parameters.length == 2 && this.descriptor.parameters.length == 2 && this.binding.isStatic() && (argumentCheck = UnlikelyArgumentCheck.determineCheckForStaticTwoArgumentMethod(argumentType2 = this.descriptor.parameters[1], currentScope, this.selector, argumentType1 = this.descriptor.parameters[0], this.binding.parameters, this.receiverType)) != null && argumentCheck.isDangerous(currentScope)) {
                currentScope.problemReporter().unlikelyArgumentType(this, this.binding, argumentType2, argumentCheck.typeToReport, argumentCheck.dangerousMethod);
            }
        }
        if (currentScope.compilerOptions().analyseResourceLeaks && this.haveReceiver && CharOperation.equals(this.selector, TypeConstants.CLOSE) && (trackingVariable = FakedTrackingVariable.getCloseTrackingVariable(this.lhs, flowInfo, flowContext)) != null) {
            trackingVariable.markClosedInNestedMethod();
        }
        this.manageSyntheticAccessIfNecessary(currentScope, flowInfo);
        return flowInfo;
    }

    @Override
    public boolean checkingPotentialCompatibility() {
        return this.checkingPotentialCompatibility;
    }

    @Override
    public void acceptPotentiallyCompatibleMethods(MethodBinding[] methods) {
        if (this.checkingPotentialCompatibility) {
            this.potentialMethods = methods;
        }
    }

    @Override
    public TypeBinding resolveType(BlockScope scope) {
        TypeBinding potentialReceiver;
        TypeBinding lhsType;
        CompilerOptions compilerOptions = scope.compilerOptions();
        if (this.constant != Constant.NotAConstant) {
            this.constant = Constant.NotAConstant;
            this.enclosingScope = scope;
            if (this.original == this) {
                this.ordinal = this.recordFunctionalType(scope);
            }
            this.lhs.bits |= 0x40000000;
            lhsType = this.lhs.resolveType(scope);
            this.lhs.computeConversion(scope, lhsType, lhsType);
            if (this.typeArguments != null) {
                int length = this.typeArguments.length;
                this.typeArgumentsHaveErrors = compilerOptions.sourceLevel < 0x310000L;
                this.resolvedTypeArguments = new TypeBinding[length];
                int i = 0;
                while (i < length) {
                    TypeReference typeReference = this.typeArguments[i];
                    this.resolvedTypeArguments[i] = typeReference.resolveType(scope, true);
                    if (this.resolvedTypeArguments[i] == null) {
                        this.typeArgumentsHaveErrors = true;
                    }
                    if (this.typeArgumentsHaveErrors && typeReference instanceof Wildcard) {
                        scope.problemReporter().illegalUsageOfWildcard(typeReference);
                    }
                    ++i;
                }
                if (this.typeArgumentsHaveErrors || lhsType == null) {
                    this.resolvedType = null;
                    return null;
                }
                if (this.isConstructorReference() && lhsType.isRawType()) {
                    scope.problemReporter().rawConstructorReferenceNotWithExplicitTypeArguments(this.typeArguments);
                    this.resolvedType = null;
                    return null;
                }
            }
            if (this.typeArgumentsHaveErrors || lhsType == null) {
                this.resolvedType = null;
                return null;
            }
            if (lhsType.problemId() == 21) {
                lhsType = lhsType.closestMatch();
            }
            if (lhsType == null || !lhsType.isValidBinding()) {
                this.resolvedType = null;
                return null;
            }
            this.receiverType = lhsType;
            this.haveReceiver = true;
            if (this.lhs instanceof NameReference) {
                if ((this.lhs.bits & 7) == 4) {
                    this.haveReceiver = false;
                } else if (this.isConstructorReference()) {
                    scope.problemReporter().invalidType(this.lhs, new ProblemReferenceBinding(((NameReference)this.lhs).getName(), null, 1));
                    this.resolvedType = null;
                    return null;
                }
            } else if (this.lhs instanceof TypeReference) {
                this.haveReceiver = false;
            }
            if (!(this.haveReceiver || this.lhs.isSuper() || this.isArrayConstructorReference())) {
                this.receiverType = lhsType.capture(scope, this.sourceStart, this.sourceEnd);
            }
            if (!lhsType.isRawType()) {
                this.exactMethodBinding = this.isMethodReference() ? scope.getExactMethod(lhsType, this.selector, this) : scope.getExactConstructor(lhsType, this);
                this.binding = this.exactMethodBinding;
            }
            if (this.isConstructorReference() && !lhsType.canBeInstantiated()) {
                scope.problemReporter().cannotInstantiate(this.lhs, lhsType);
                this.resolvedType = null;
                return null;
            }
            if (this.lhs instanceof TypeReference && ((TypeReference)this.lhs).hasNullTypeAnnotation(TypeReference.AnnotationPosition.ANY)) {
                scope.problemReporter().nullAnnotationUnsupportedLocation((TypeReference)this.lhs);
            }
            if (this.isConstructorReference() && lhsType.isArrayType()) {
                TypeBinding leafComponentType = lhsType.leafComponentType();
                if (!leafComponentType.isReifiable()) {
                    scope.problemReporter().illegalGenericArray(leafComponentType, this);
                    this.resolvedType = null;
                    return null;
                }
                if (this.typeArguments != null) {
                    scope.problemReporter().invalidTypeArguments(this.typeArguments);
                    this.resolvedType = null;
                    return null;
                }
                this.binding = this.exactMethodBinding = scope.getExactConstructor(lhsType, this);
            }
            if (this.isMethodReference() && this.haveReceiver && this.original == this) {
                this.receiverVariable = new LocalVariableBinding((SecretReceiverVariableName + this.nameSourceStart).toCharArray(), this.lhs.resolvedType, 0, false);
                scope.addLocalVariable(this.receiverVariable);
                this.receiverVariable.setConstant(Constant.NotAConstant);
                this.receiverVariable.useFlag = 1;
            }
            if (this.expectedType == null && this.expressionContext == ExpressionContext.INVOCATION_CONTEXT) {
                if (compilerOptions.isAnnotationBasedNullAnalysisEnabled && this.binding != null) {
                    ImplicitNullAnnotationVerifier.ensureNullnessIsKnown(this.binding, scope);
                }
                return new PolyTypeBinding(this);
            }
        } else {
            lhsType = this.lhs.resolvedType;
            if (this.typeArgumentsHaveErrors || lhsType == null) {
                this.resolvedType = null;
                return null;
            }
        }
        super.resolveType(scope);
        if (this.descriptor == null || !this.descriptor.isValidBinding()) {
            this.resolvedType = null;
            return null;
        }
        TypeBinding[] descriptorParameters = this.descriptorParametersAsArgumentExpressions();
        if (lhsType.isBaseType()) {
            scope.problemReporter().errorNoMethodFor(this.lhs, lhsType, this.selector, descriptorParameters);
            this.resolvedType = null;
            return null;
        }
        int parametersLength = descriptorParameters.length;
        if (this.isConstructorReference() && lhsType.isArrayType()) {
            if (parametersLength != 1 || scope.parameterCompatibilityLevel(descriptorParameters[0], TypeBinding.INT) == -1) {
                scope.problemReporter().invalidArrayConstructorReference(this, lhsType, descriptorParameters);
                this.resolvedType = null;
                return null;
            }
            if (this.descriptor.returnType.isProperType(true) && !lhsType.isCompatibleWith(this.descriptor.returnType) && this.descriptor.returnType.id != 6) {
                scope.problemReporter().constructedArrayIncompatible(this, lhsType, this.descriptor.returnType);
                this.resolvedType = null;
                return null;
            }
            this.checkNullAnnotations(scope);
            return this.resolvedType;
        }
        boolean isMethodReference = this.isMethodReference();
        this.depth = 0;
        this.freeParameters = descriptorParameters;
        MethodBinding someMethod = null;
        if (isMethodReference) {
            someMethod = scope.getMethod(this.receiverType, this.selector, descriptorParameters, this);
        } else {
            if (this.argumentsTypeElided() && this.receiverType.isRawType()) {
                boolean[] inferredReturnType = new boolean[1];
                someMethod = AllocationExpression.inferDiamondConstructor(scope, this, this.receiverType, this.descriptor.parameters, inferredReturnType);
            }
            if (someMethod == null) {
                someMethod = scope.getConstructor((ReferenceBinding)this.receiverType, descriptorParameters, this);
            }
        }
        int someMethodDepth = this.depth;
        int anotherMethodDepth = 0;
        if (someMethod != null && someMethod.isValidBinding() && someMethod.isStatic() && (this.haveReceiver || this.receiverType.isParameterizedTypeWithActualArguments())) {
            scope.problemReporter().methodMustBeAccessedStatically(this, someMethod);
            this.resolvedType = null;
            return null;
        }
        if (this.lhs.isSuper() && this.lhs.resolvedType.isInterface()) {
            scope.checkAppropriateMethodAgainstSupers(this.selector, someMethod, this.descriptor.parameters, this);
        }
        Binding anotherMethod = null;
        this.receiverPrecedesParameters = false;
        if (!this.haveReceiver && isMethodReference && parametersLength > 0 && (potentialReceiver = descriptorParameters[0]).isCompatibleWith(this.receiverType, scope)) {
            TypeBinding superType;
            TypeBinding typeToSearch = this.receiverType;
            if (this.receiverType.isRawType() && (superType = potentialReceiver.findSuperTypeOriginatingFrom(this.receiverType)) != null) {
                typeToSearch = superType.capture(scope, this.sourceStart, this.sourceEnd);
            }
            TypeBinding[] parameters = Binding.NO_PARAMETERS;
            if (parametersLength > 1) {
                parameters = new TypeBinding[parametersLength - 1];
                System.arraycopy(descriptorParameters, 1, parameters, 0, parametersLength - 1);
            }
            this.depth = 0;
            this.freeParameters = parameters;
            anotherMethod = scope.getMethod(typeToSearch, this.selector, parameters, this);
            anotherMethodDepth = this.depth;
            this.depth = 0;
        }
        if (someMethod != null && someMethod.isValidBinding() && someMethod.isStatic() && anotherMethod != null && anotherMethod.isValidBinding() && !((MethodBinding)anotherMethod).isStatic()) {
            scope.problemReporter().methodReferenceSwingsBothWays(this, (MethodBinding)anotherMethod, someMethod);
            this.resolvedType = null;
            return null;
        }
        if (someMethod != null && someMethod.isValidBinding() && (anotherMethod == null || !anotherMethod.isValidBinding() || ((MethodBinding)anotherMethod).isStatic())) {
            this.binding = someMethod;
            this.bits &= 0xFFFFE01F;
            if (someMethodDepth > 0) {
                this.bits |= (someMethodDepth & 0xFF) << 5;
            }
            if (!(this.haveReceiver || someMethod.isStatic() || someMethod.isConstructor())) {
                scope.problemReporter().methodMustBeAccessedWithInstance(this, someMethod);
                this.resolvedType = null;
                return null;
            }
        } else if (!(anotherMethod == null || !anotherMethod.isValidBinding() || someMethod != null && someMethod.isValidBinding() && someMethod.isStatic())) {
            this.binding = anotherMethod;
            this.receiverPrecedesParameters = true;
            this.bits &= 0xFFFFE01F;
            if (anotherMethodDepth > 0) {
                this.bits |= (anotherMethodDepth & 0xFF) << 5;
            }
            if (((MethodBinding)anotherMethod).isStatic()) {
                scope.problemReporter().methodMustBeAccessedStatically(this, (MethodBinding)anotherMethod);
                this.resolvedType = null;
                return null;
            }
        } else {
            this.binding = null;
            this.bits &= 0xFFFFE01F;
        }
        if (this.binding == null) {
            char[] visibleName = this.isConstructorReference() ? this.receiverType.sourceName() : this.selector;
            scope.problemReporter().danglingReference(this, this.receiverType, visibleName, descriptorParameters);
            this.resolvedType = null;
            return null;
        }
        if (this.binding.isAbstract() && this.lhs.isSuper()) {
            scope.problemReporter().cannotDireclyInvokeAbstractMethod(this, this.binding);
        }
        if (this.binding.isStatic()) {
            if (TypeBinding.notEquals(this.binding.declaringClass, this.receiverType)) {
                scope.problemReporter().indirectAccessToStaticMethod(this, this.binding);
            }
        } else {
            AbstractMethodDeclaration srcMethod = this.binding.sourceMethod();
            if (srcMethod != null && srcMethod.isMethod()) {
                srcMethod.bits &= 0xFFFFFEFF;
            }
        }
        if (this.isMethodUseDeprecated(this.binding, scope, true, this)) {
            scope.problemReporter().deprecatedMethod(this.binding, this);
        }
        if (this.typeArguments != null && this.binding.original().typeVariables == Binding.NO_TYPE_VARIABLES) {
            scope.problemReporter().unnecessaryTypeArgumentsForMethodInvocation(this.binding, this.resolvedTypeArguments, this.typeArguments);
        }
        if ((this.binding.tagBits & 0x80L) != 0L) {
            scope.problemReporter().missingTypeInMethod(this, this.binding);
        }
        ReferenceBinding[] methodExceptions = this.binding.thrownExceptions;
        ReferenceBinding[] kosherExceptions = this.descriptor.thrownExceptions;
        int i = 0;
        int iMax = methodExceptions.length;
        while (i < iMax) {
            block74: {
                if (!((TypeBinding)methodExceptions[i]).isUncheckedException(false)) {
                    int j = 0;
                    int jMax = kosherExceptions.length;
                    while (j < jMax) {
                        if (!((TypeBinding)methodExceptions[i]).isCompatibleWith(kosherExceptions[j], scope)) {
                            ++j;
                            continue;
                        }
                        break block74;
                    }
                    scope.problemReporter().unhandledException((TypeBinding)methodExceptions[i], this);
                }
            }
            ++i;
        }
        this.checkNullAnnotations(scope);
        this.freeParameters = null;
        if (ReferenceExpression.checkInvocationArguments(scope, null, this.receiverType, this.binding, null, descriptorParameters, false, this)) {
            this.bits |= 0x10000;
        }
        if (this.descriptor.returnType.id != 6) {
            TypeBinding returnType = null;
            if (this.binding.isConstructor()) {
                returnType = this.receiverType;
            } else if ((this.bits & 0x10000) != 0 && this.resolvedTypeArguments == null) {
                returnType = this.binding.returnType;
                if (returnType != null) {
                    returnType = scope.environment().convertToRawType(returnType.erasure(), true);
                }
            } else {
                returnType = this.binding.returnType;
                if (returnType != null) {
                    returnType = returnType.capture(scope, this.sourceStart, this.sourceEnd);
                }
            }
            if (this.descriptor.returnType.isProperType(true) && !returnType.isCompatibleWith(this.descriptor.returnType, scope) && !this.isBoxingCompatible(returnType, this.descriptor.returnType, this, scope)) {
                scope.problemReporter().incompatibleReturnType(this, this.binding, this.descriptor.returnType);
                this.binding = null;
                this.resolvedType = null;
            }
        }
        return this.resolvedType;
    }

    protected void checkNullAnnotations(BlockScope scope) {
        CompilerOptions compilerOptions = scope.compilerOptions();
        if (compilerOptions.isAnnotationBasedNullAnalysisEnabled && (this.expectedType == null || !NullAnnotationMatching.hasContradictions(this.expectedType))) {
            int len;
            ImplicitNullAnnotationVerifier.ensureNullnessIsKnown(this.binding, scope);
            int expectedlen = this.binding.parameters.length;
            int providedLen = this.descriptor.parameters.length;
            if (this.receiverPrecedesParameters) {
                --providedLen;
                TypeBinding descriptorParameter = this.descriptor.parameters[0];
                if ((descriptorParameter.tagBits & 0x80000000000000L) != 0L) {
                    TypeBinding receiver = scope.environment().createAnnotatedType((TypeBinding)this.binding.declaringClass, new AnnotationBinding[]{scope.environment().getNonNullAnnotation()});
                    scope.problemReporter().referenceExpressionArgumentNullityMismatch(this, receiver, descriptorParameter, this.descriptor, -1, NullAnnotationMatching.NULL_ANNOTATIONS_MISMATCH);
                }
            }
            boolean isVarArgs = false;
            if (this.binding.isVarargs()) {
                isVarArgs = providedLen == expectedlen ? !this.descriptor.parameters[expectedlen - 1].isCompatibleWith(this.binding.parameters[expectedlen - 1]) : true;
                len = providedLen;
            } else {
                len = Math.min(expectedlen, providedLen);
            }
            int i = 0;
            while (i < len) {
                TypeBinding descriptorParameter = this.descriptor.parameters[i + (this.receiverPrecedesParameters ? 1 : 0)];
                TypeBinding bindingParameter = InferenceContext18.getParameter(this.binding.parameters, i, isVarArgs);
                TypeBinding bindingParameterToCheck = bindingParameter.isPrimitiveType() && !descriptorParameter.isPrimitiveType() ? scope.environment().createAnnotatedType(scope.boxing(bindingParameter), new AnnotationBinding[]{scope.environment().getNonNullAnnotation()}) : bindingParameter;
                NullAnnotationMatching annotationStatus = NullAnnotationMatching.analyse(bindingParameterToCheck, descriptorParameter, 1);
                if (annotationStatus.isAnyMismatch()) {
                    scope.problemReporter().referenceExpressionArgumentNullityMismatch(this, bindingParameter, descriptorParameter, this.descriptor, i, annotationStatus);
                }
                ++i;
            }
            TypeBinding returnType = this.binding.returnType;
            if (!returnType.isPrimitiveType()) {
                NullAnnotationMatching annotationStatus;
                if (this.binding.isConstructor()) {
                    returnType = scope.environment().createAnnotatedType(this.receiverType, new AnnotationBinding[]{scope.environment().getNonNullAnnotation()});
                }
                if ((annotationStatus = NullAnnotationMatching.analyse(this.descriptor.returnType, returnType, 1)).isAnyMismatch()) {
                    scope.problemReporter().illegalReturnRedefinition(this, this.descriptor, annotationStatus.isUnchecked(), returnType);
                }
            }
        }
    }

    private TypeBinding[] descriptorParametersAsArgumentExpressions() {
        if (this.descriptor == null || this.descriptor.parameters == null || this.descriptor.parameters.length == 0) {
            return Binding.NO_PARAMETERS;
        }
        if (this.expectedType.isParameterizedType()) {
            ParameterizedTypeBinding type = (ParameterizedTypeBinding)this.expectedType;
            MethodBinding method = type.getSingleAbstractMethod(this.enclosingScope, true, this.sourceStart, this.sourceEnd);
            return method.parameters;
        }
        return this.descriptor.parameters;
    }

    private boolean contextHasSyntaxError() {
        ReferenceContext referenceContext = this.enclosingScope.referenceContext();
        return referenceContext instanceof AbstractMethodDeclaration && (((AbstractMethodDeclaration)referenceContext).bits & 0x80000) != 0;
    }

    private ReferenceExpression cachedResolvedCopy(TypeBinding targetType) {
        ReferenceExpression copy;
        ReferenceExpression referenceExpression = copy = this.copiesPerTargetType != null ? this.copiesPerTargetType.get(targetType) : null;
        if (copy != null) {
            return copy;
        }
        if (this.contextHasSyntaxError()) {
            return null;
        }
        IErrorHandlingPolicy oldPolicy = this.enclosingScope.problemReporter().switchErrorHandlingPolicy(silentErrorHandlingPolicy);
        try {
            copy = this.copy();
            if (copy == null) {
                return null;
            }
            copy.setExpressionContext(this.expressionContext);
            copy.setExpectedType(targetType);
            copy.resolveType(this.enclosingScope);
            if (this.copiesPerTargetType == null) {
                this.copiesPerTargetType = new HashMap();
            }
            this.copiesPerTargetType.put(targetType, copy);
            ReferenceExpression referenceExpression2 = copy;
            return referenceExpression2;
        }
        finally {
            this.enclosingScope.problemReporter().switchErrorHandlingPolicy(oldPolicy);
        }
    }

    public void registerInferenceContext(ParameterizedGenericMethodBinding method, InferenceContext18 context) {
        if (this.inferenceContexts == null) {
            this.inferenceContexts = new HashMap();
        }
        this.inferenceContexts.put(method, context);
    }

    public InferenceContext18 getInferenceContext(ParameterizedMethodBinding method) {
        if (this.inferenceContexts == null) {
            return null;
        }
        return this.inferenceContexts.get(method);
    }

    @Override
    public ReferenceExpression resolveExpressionExpecting(TypeBinding targetType, Scope scope, InferenceContext18 inferenceContext) {
        if (this.exactMethodBinding != null) {
            MethodBinding functionType = targetType.getSingleAbstractMethod(scope, true);
            if (functionType == null || functionType.problemId() == 17) {
                return null;
            }
            int n = functionType.parameters.length;
            int k = this.exactMethodBinding.parameters.length;
            if (!this.haveReceiver && this.isMethodReference() && !this.exactMethodBinding.isStatic()) {
                ++k;
            }
            return n == k ? this : null;
        }
        ReferenceExpression copy = this.cachedResolvedCopy(targetType);
        return copy != null && copy.resolvedType != null && copy.resolvedType.isValidBinding() && copy.binding != null && copy.binding.isValidBinding() ? copy : null;
    }

    public boolean isConstructorReference() {
        return CharOperation.equals(this.selector, ConstantPool.Init);
    }

    @Override
    public boolean isExactMethodReference() {
        return this.exactMethodBinding != null;
    }

    public MethodBinding getExactMethod() {
        return this.exactMethodBinding;
    }

    public boolean isMethodReference() {
        return !CharOperation.equals(this.selector, ConstantPool.Init);
    }

    @Override
    public boolean isPertinentToApplicability(TypeBinding targetType, MethodBinding method) {
        if (!this.isExactMethodReference()) {
            return false;
        }
        return super.isPertinentToApplicability(targetType, method);
    }

    @Override
    public TypeBinding[] genericTypeArguments() {
        return this.resolvedTypeArguments;
    }

    @Override
    public InferenceContext18 freshInferenceContext(Scope scope) {
        if (this.expressionContext != ExpressionContext.VANILLA_CONTEXT) {
            Expression[] arguments = this.createPseudoExpressions(this.freeParameters);
            return new InferenceContext18(scope, arguments, this, null);
        }
        return null;
    }

    @Override
    public boolean isSuperAccess() {
        return this.lhs.isSuper();
    }

    @Override
    public boolean isTypeAccess() {
        return !this.haveReceiver;
    }

    @Override
    public void setActualReceiverType(ReferenceBinding receiverType) {
    }

    @Override
    public void setDepth(int depth) {
        this.depth = depth;
    }

    @Override
    public void setFieldIndex(int depth) {
    }

    @Override
    public StringBuffer printExpression(int tab, StringBuffer output) {
        this.lhs.print(0, output);
        output.append("::");
        if (this.typeArguments != null) {
            output.append('<');
            int max = this.typeArguments.length - 1;
            int j = 0;
            while (j < max) {
                this.typeArguments[j].print(0, output);
                output.append(", ");
                ++j;
            }
            this.typeArguments[max].print(0, output);
            output.append('>');
        }
        if (this.isConstructorReference()) {
            output.append("new");
        } else {
            output.append(this.selector);
        }
        return output;
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope blockScope) {
        if (visitor.visit(this, blockScope)) {
            this.lhs.traverse(visitor, blockScope);
            int length = this.typeArguments == null ? 0 : this.typeArguments.length;
            int i = 0;
            while (i < length) {
                this.typeArguments[i].traverse(visitor, blockScope);
                ++i;
            }
        }
        visitor.endVisit(this, blockScope);
    }

    public Expression[] createPseudoExpressions(TypeBinding[] p) {
        Expression[] expressions = new Expression[p.length];
        long pos = ((long)this.sourceStart << 32) + (long)this.sourceEnd;
        int i = 0;
        while (i < p.length) {
            expressions[i] = new SingleNameReference(("fakeArg" + i).toCharArray(), pos);
            expressions[i].resolvedType = p[i];
            ++i;
        }
        return expressions;
    }

    @Override
    public boolean isPotentiallyCompatibleWith(TypeBinding targetType, Scope scope) {
        boolean isConstructorRef = this.isConstructorReference();
        if (isConstructorRef) {
            TypeBinding leafComponentType;
            if (this.receiverType == null) {
                return false;
            }
            if (this.receiverType.isArrayType() && !(leafComponentType = this.receiverType.leafComponentType()).isReifiable()) {
                return false;
            }
        }
        if (!super.isPertinentToApplicability(targetType, null)) {
            return true;
        }
        MethodBinding sam = targetType.getSingleAbstractMethod(this.enclosingScope, true);
        if (sam == null || !sam.isValidBinding()) {
            return false;
        }
        if (this.typeArgumentsHaveErrors || this.receiverType == null || !this.receiverType.isValidBinding()) {
            return false;
        }
        int parametersLength = sam.parameters.length;
        TypeBinding[] descriptorParameters = new TypeBinding[parametersLength];
        int i = 0;
        while (i < parametersLength) {
            descriptorParameters[i] = new ReferenceBinding(){
                {
                    this.compoundName = CharOperation.NO_CHAR_CHAR;
                }

                @Override
                public boolean isCompatibleWith(TypeBinding otherType, Scope captureScope) {
                    return true;
                }

                @Override
                public TypeBinding findSuperTypeOriginatingFrom(TypeBinding otherType) {
                    return otherType;
                }

                public String toString() {
                    return "(wildcard)";
                }
            };
            ++i;
        }
        this.freeParameters = descriptorParameters;
        this.checkingPotentialCompatibility = true;
        try {
            MethodBinding compileTimeDeclaration = this.getCompileTimeDeclaration(scope, isConstructorRef, descriptorParameters);
            if (compileTimeDeclaration != null && compileTimeDeclaration.isValidBinding()) {
                this.potentialMethods = new MethodBinding[]{compileTimeDeclaration};
            }
            int i2 = 0;
            int length = this.potentialMethods.length;
            while (i2 < length) {
                if (this.potentialMethods[i2].isStatic() || this.potentialMethods[i2].isConstructor() ? !this.haveReceiver : this.haveReceiver) {
                    return true;
                }
                ++i2;
            }
            if (this.haveReceiver || parametersLength == 0) {
                return false;
            }
            TypeBinding[] typeBindingArray = descriptorParameters;
            descriptorParameters = new TypeBinding[parametersLength - 1];
            System.arraycopy(typeBindingArray, 1, descriptorParameters, 0, parametersLength - 1);
            this.freeParameters = descriptorParameters;
            this.potentialMethods = Binding.NO_METHODS;
            compileTimeDeclaration = this.getCompileTimeDeclaration(scope, false, descriptorParameters);
            if (compileTimeDeclaration != null && compileTimeDeclaration.isValidBinding()) {
                this.potentialMethods = new MethodBinding[]{compileTimeDeclaration};
            }
            i2 = 0;
            length = this.potentialMethods.length;
            while (i2 < length) {
                if (!this.potentialMethods[i2].isStatic() && !this.potentialMethods[i2].isConstructor()) {
                    return true;
                }
                ++i2;
            }
        }
        finally {
            this.checkingPotentialCompatibility = false;
            this.potentialMethods = Binding.NO_METHODS;
            this.freeParameters = null;
        }
        return false;
    }

    MethodBinding getCompileTimeDeclaration(Scope scope, boolean isConstructorRef, TypeBinding[] parameters) {
        if (this.exactMethodBinding != null) {
            return this.exactMethodBinding;
        }
        if (this.receiverType.isArrayType()) {
            return scope.findMethodForArray((ArrayBinding)this.receiverType, this.selector, Binding.NO_PARAMETERS, this);
        }
        if (isConstructorRef) {
            return scope.getConstructor((ReferenceBinding)this.receiverType, parameters, this);
        }
        return scope.getMethod(this.receiverType, this.selector, parameters, this);
    }

    @Override
    public boolean isCompatibleWith(TypeBinding targetType, Scope scope) {
        ReferenceExpression copy = this.cachedResolvedCopy(targetType);
        if (copy == null) {
            return this.contextHasSyntaxError();
        }
        return copy.resolvedType != null && copy.resolvedType.isValidBinding() && copy.binding != null && copy.binding.isValidBinding();
    }

    @Override
    public boolean sIsMoreSpecific(TypeBinding s, TypeBinding t, Scope scope) {
        if (super.sIsMoreSpecific(s, t, scope)) {
            return true;
        }
        if (this.exactMethodBinding == null || t.findSuperTypeOriginatingFrom(s) != null) {
            return false;
        }
        MethodBinding sSam = (s = s.capture(this.enclosingScope, this.sourceStart, this.sourceEnd)).getSingleAbstractMethod(this.enclosingScope, true);
        if (sSam == null || !sSam.isValidBinding()) {
            return false;
        }
        TypeBinding r1 = sSam.returnType;
        MethodBinding tSam = t.getSingleAbstractMethod(this.enclosingScope, true);
        if (tSam == null || !tSam.isValidBinding()) {
            return false;
        }
        TypeBinding r2 = tSam.returnType;
        TypeBinding[] sParams = sSam.parameters;
        TypeBinding[] tParams = tSam.parameters;
        int i = 0;
        while (i < sParams.length) {
            if (TypeBinding.notEquals(sParams[i], tParams[i])) {
                return false;
            }
            ++i;
        }
        if (r2.id == 6) {
            return true;
        }
        if (r1.id == 6) {
            return false;
        }
        if (r1.isCompatibleWith(r2, scope)) {
            return true;
        }
        return r1.isBaseType() != r2.isBaseType() && r1.isBaseType() == this.exactMethodBinding.returnType.isBaseType();
    }

    @Override
    public MethodBinding getMethodBinding() {
        if (this.actualMethodBinding == null) {
            this.actualMethodBinding = this.binding;
        }
        return this.actualMethodBinding;
    }

    public boolean isArrayConstructorReference() {
        return this.isConstructorReference() && this.lhs.resolvedType != null && this.lhs.resolvedType.isArrayType();
    }
}

