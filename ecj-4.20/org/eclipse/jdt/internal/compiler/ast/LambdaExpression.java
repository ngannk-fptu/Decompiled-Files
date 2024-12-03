/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.ExpressionContext;
import org.eclipse.jdt.internal.compiler.ast.FunctionalExpression;
import org.eclipse.jdt.internal.compiler.ast.IPolyExpression;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.flow.ExceptionHandlingFlowContext;
import org.eclipse.jdt.internal.compiler.flow.ExceptionInferenceFlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import org.eclipse.jdt.internal.compiler.lookup.IntersectionTypeBinding18;
import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedGenericMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.PolyTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Substitution;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticArgumentBinding;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilationUnit;
import org.eclipse.jdt.internal.compiler.problem.AbortMethod;
import org.eclipse.jdt.internal.compiler.problem.AbortType;
import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;

public class LambdaExpression
extends FunctionalExpression
implements IPolyExpression,
ReferenceContext,
ProblemSeverities {
    public Argument[] arguments;
    private TypeBinding[] argumentTypes;
    public int arrowPosition;
    public Statement body;
    public boolean hasParentheses;
    public MethodScope scope;
    boolean voidCompatible = true;
    boolean valueCompatible = false;
    boolean returnsValue;
    private boolean requiresGenericSignature;
    boolean returnsVoid;
    public LambdaExpression original = this;
    private boolean committed = false;
    public SyntheticArgumentBinding[] outerLocalVariables = NO_SYNTHETIC_ARGUMENTS;
    private int outerLocalVariablesSlotSize = 0;
    private boolean assistNode = false;
    private boolean hasIgnoredMandatoryErrors = false;
    private ReferenceBinding classType;
    private Set thrownExceptions;
    public char[] text;
    private static final SyntheticArgumentBinding[] NO_SYNTHETIC_ARGUMENTS = new SyntheticArgumentBinding[0];
    private static final Block NO_BODY = new Block(0);
    private HashMap<TypeBinding, LambdaExpression> copiesPerTargetType;
    protected Expression[] resultExpressions = NO_EXPRESSIONS;
    public InferenceContext18 inferenceContext;
    private Map<Integer, LocalTypeBinding> localTypes;
    public boolean argumentsTypeVar = false;

    public LambdaExpression(CompilationResult compilationResult, boolean assistNode, boolean requiresGenericSignature) {
        super(compilationResult);
        this.assistNode = assistNode;
        this.requiresGenericSignature = requiresGenericSignature;
        this.setArguments(NO_ARGUMENTS);
        this.setBody(NO_BODY);
    }

    public LambdaExpression(CompilationResult compilationResult, boolean assistNode) {
        this(compilationResult, assistNode, false);
    }

    public void setArguments(Argument[] arguments) {
        this.arguments = arguments != null ? arguments : ASTNode.NO_ARGUMENTS;
        this.argumentTypes = new TypeBinding[arguments != null ? arguments.length : 0];
    }

    public Argument[] arguments() {
        return this.arguments;
    }

    public TypeBinding[] argumentTypes() {
        return this.argumentTypes;
    }

    public void setBody(Statement body) {
        this.body = body == null ? NO_BODY : body;
    }

    public Statement body() {
        return this.body;
    }

    public Expression[] resultExpressions() {
        return this.resultExpressions;
    }

    public void setArrowPosition(int arrowPosition) {
        this.arrowPosition = arrowPosition;
    }

    public int arrowPosition() {
        return this.arrowPosition;
    }

    protected FunctionalExpression original() {
        return this.original;
    }

    @Override
    public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
        this.binding.modifiers = this.shouldCaptureInstance ? (this.binding.modifiers &= 0xFFFFFFF7) : (this.binding.modifiers |= 8);
        SourceTypeBinding sourceType = currentScope.enclosingSourceType();
        boolean firstSpill = !(this.binding instanceof SyntheticMethodBinding);
        this.binding = sourceType.addSyntheticMethod(this);
        int pc = codeStream.position;
        StringBuffer signature = new StringBuffer();
        signature.append('(');
        if (this.shouldCaptureInstance) {
            codeStream.aload_0();
            signature.append(sourceType.signature());
        }
        int i = 0;
        int length = this.outerLocalVariables == null ? 0 : this.outerLocalVariables.length;
        while (i < length) {
            SyntheticArgumentBinding syntheticArgument = this.outerLocalVariables[i];
            if (this.shouldCaptureInstance && firstSpill) {
                syntheticArgument.resolvedPosition = syntheticArgument.resolvedPosition + 1;
            }
            signature.append(syntheticArgument.type.signature());
            LocalVariableBinding capturedOuterLocal = syntheticArgument.actualOuterLocalVariable;
            Object[] path = currentScope.getEmulationPath(capturedOuterLocal);
            codeStream.generateOuterAccess(path, this, capturedOuterLocal, currentScope);
            ++i;
        }
        signature.append(')');
        if (this.expectedType instanceof IntersectionTypeBinding18) {
            signature.append(((IntersectionTypeBinding18)this.expectedType).getSAMType(currentScope).signature());
        } else {
            signature.append(this.expectedType.signature());
        }
        int invokeDynamicNumber = codeStream.classFile.recordBootstrapMethod(this);
        codeStream.invokeDynamic(invokeDynamicNumber, (this.shouldCaptureInstance ? 1 : 0) + this.outerLocalVariablesSlotSize, 1, this.descriptor.selector, signature.toString().toCharArray(), this.resolvedType.id, this.resolvedType);
        if (!valueRequired) {
            codeStream.pop();
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }

    @Override
    public boolean kosherDescriptor(Scope currentScope, MethodBinding sam, boolean shouldChatter) {
        if (sam.typeVariables != Binding.NO_TYPE_VARIABLES) {
            if (shouldChatter) {
                currentScope.problemReporter().lambdaExpressionCannotImplementGenericMethod(this, sam);
            }
            return false;
        }
        return super.kosherDescriptor(currentScope, sam, shouldChatter);
    }

    @Override
    public TypeBinding resolveType(BlockScope blockScope, boolean skipKosherCheck) {
        int parametersLength;
        boolean haveDescriptor;
        int argumentsLength;
        boolean argumentsTypeElided = this.argumentsTypeElided();
        int n = argumentsLength = this.arguments == null ? 0 : this.arguments.length;
        if (this.constant != Constant.NotAConstant) {
            this.constant = Constant.NotAConstant;
            this.enclosingScope = blockScope;
            if (this.original == this) {
                this.ordinal = this.recordFunctionalType(blockScope);
            }
            if (!argumentsTypeElided) {
                int i = 0;
                while (i < argumentsLength) {
                    this.argumentTypes[i] = this.arguments[i].type.resolveType(blockScope, true);
                    ++i;
                }
            }
            if (this.expectedType == null && this.expressionContext == ExpressionContext.INVOCATION_CONTEXT) {
                return new PolyTypeBinding(this);
            }
        }
        MethodScope methodScope = blockScope.methodScope();
        this.scope = new MethodScope(blockScope, this, methodScope.isStatic, methodScope.lastVisibleFieldID);
        this.scope.isConstructorCall = methodScope.isConstructorCall;
        super.resolveType(blockScope, skipKosherCheck);
        boolean bl = haveDescriptor = this.descriptor != null;
        if (!(skipKosherCheck || haveDescriptor && this.descriptor.typeVariables == Binding.NO_TYPE_VARIABLES)) {
            this.resolvedType = null;
            return null;
        }
        this.binding = new MethodBinding(0x2001002, CharOperation.concat(TypeConstants.ANONYMOUS_METHOD, Integer.toString(this.ordinal).toCharArray()), haveDescriptor ? this.descriptor.returnType : TypeBinding.VOID, Binding.NO_PARAMETERS, haveDescriptor ? this.descriptor.thrownExceptions : Binding.NO_EXCEPTIONS, blockScope.enclosingSourceType());
        this.binding.typeVariables = Binding.NO_TYPE_VARIABLES;
        boolean argumentsHaveErrors = false;
        if (haveDescriptor && (parametersLength = this.descriptor.parameters.length) != argumentsLength) {
            this.scope.problemReporter().lambdaSignatureMismatched(this);
            if (argumentsTypeElided || this.original != this) {
                this.resolvedType = null;
                return null;
            }
            this.resolvedType = null;
            argumentsHaveErrors = true;
        }
        TypeBinding[] newParameters = new TypeBinding[argumentsLength];
        AnnotationBinding[][] parameterAnnotations = null;
        int i = 0;
        while (i < argumentsLength) {
            TypeBinding argumentType;
            Argument argument = this.arguments[i];
            if (argument.isVarArgs()) {
                if (i == argumentsLength - 1) {
                    this.binding.modifiers |= 0x80;
                } else {
                    this.scope.problemReporter().illegalVarargInLambda(argument);
                    argumentsHaveErrors = true;
                }
            }
            TypeBinding expectedParameterType = haveDescriptor && i < this.descriptor.parameters.length ? this.descriptor.parameters[i] : null;
            TypeBinding typeBinding = argumentType = argumentsTypeElided ? expectedParameterType : this.argumentTypes[i];
            if (argumentType == null) {
                argumentsHaveErrors = true;
            } else if (argumentType == TypeBinding.VOID) {
                this.scope.problemReporter().argumentTypeCannotBeVoid(this, argument);
                argumentsHaveErrors = true;
            } else {
                if (!argumentType.isValidBinding()) {
                    this.binding.tagBits |= 0x200L;
                }
                if ((argumentType.tagBits & 0x80L) != 0L) {
                    this.binding.tagBits |= 0x80L;
                }
            }
            ++i;
        }
        if (!argumentsTypeElided && !argumentsHaveErrors) {
            ReferenceBinding groundType = null;
            ReferenceBinding expectedSAMType = null;
            if (this.expectedType instanceof IntersectionTypeBinding18) {
                expectedSAMType = (ReferenceBinding)((IntersectionTypeBinding18)this.expectedType).getSAMType(blockScope);
            } else if (this.expectedType instanceof ReferenceBinding) {
                expectedSAMType = (ReferenceBinding)this.expectedType;
            }
            if (expectedSAMType != null) {
                groundType = this.findGroundTargetType(blockScope, this.expectedType, expectedSAMType, argumentsTypeElided);
            }
            if (groundType != null) {
                this.descriptor = groundType.getSingleAbstractMethod(blockScope, true);
                if (!this.descriptor.isValidBinding()) {
                    this.reportSamProblem(blockScope, this.descriptor);
                } else {
                    if (groundType != expectedSAMType && !groundType.isCompatibleWith(expectedSAMType, this.scope)) {
                        blockScope.problemReporter().typeMismatchError((TypeBinding)groundType, this.expectedType, this, null);
                        return null;
                    }
                    this.resolvedType = groundType;
                }
            } else {
                this.binding = new ProblemMethodBinding(TypeConstants.ANONYMOUS_METHOD, null, 18);
                this.reportSamProblem(blockScope, this.binding);
                this.resolvedType = null;
                return null;
            }
        }
        boolean parametersHaveErrors = false;
        boolean genericSignatureNeeded = this.requiresGenericSignature || blockScope.compilerOptions().generateGenericSignatureForLambdaExpressions;
        TypeBinding[] expectedParameterTypes = new TypeBinding[argumentsLength];
        int i2 = 0;
        while (i2 < argumentsLength) {
            Argument argument = this.arguments[i2];
            TypeBinding expectedParameterType = haveDescriptor && i2 < this.descriptor.parameters.length ? this.descriptor.parameters[i2] : null;
            TypeBinding argumentType = argumentsTypeElided ? expectedParameterType : this.argumentTypes[i2];
            expectedParameterTypes[i2] = expectedParameterType;
            if (argumentType != null && argumentType != TypeBinding.VOID) {
                TypeBinding leafType;
                if (haveDescriptor && expectedParameterType != null && argumentType.isValidBinding() && TypeBinding.notEquals(argumentType, expectedParameterType) && expectedParameterType.isProperType(true) && !this.isOnlyWildcardMismatch(expectedParameterType, argumentType)) {
                    this.scope.problemReporter().lambdaParameterTypeMismatched(argument, argument.type, expectedParameterType);
                    parametersHaveErrors = true;
                }
                if (genericSignatureNeeded && (leafType = argumentType.leafComponentType()) instanceof ReferenceBinding && (((ReferenceBinding)leafType).modifiers & 0x40000000) != 0) {
                    this.binding.modifiers |= 0x40000000;
                }
                newParameters[i2] = argument.bind(this.scope, argumentType, false);
                if (argument.annotations != null) {
                    this.binding.tagBits |= 0x400L;
                    if (parameterAnnotations == null) {
                        parameterAnnotations = new AnnotationBinding[argumentsLength][];
                        int j = 0;
                        while (j < i2) {
                            parameterAnnotations[j] = Binding.NO_ANNOTATIONS;
                            ++j;
                        }
                    }
                    parameterAnnotations[i2] = argument.binding.getAnnotations();
                } else if (parameterAnnotations != null) {
                    parameterAnnotations[i2] = Binding.NO_ANNOTATIONS;
                }
            }
            ++i2;
        }
        if (this.argumentsTypeVar) {
            i2 = 0;
            while (i2 < argumentsLength) {
                this.arguments[i2].type.resolvedType = expectedParameterTypes[i2];
                ++i2;
            }
        }
        if (!argumentsHaveErrors) {
            this.binding.parameters = newParameters;
            if (parameterAnnotations != null) {
                this.binding.setParameterAnnotations(parameterAnnotations);
            }
        }
        if (!argumentsTypeElided && !argumentsHaveErrors && this.binding.isVarargs() && !this.binding.parameters[this.binding.parameters.length - 1].isReifiable()) {
            this.scope.problemReporter().possibleHeapPollutionFromVararg(this.arguments[this.arguments.length - 1]);
        }
        ReferenceBinding[] exceptions = this.binding.thrownExceptions;
        int exceptionsLength = exceptions.length;
        int i3 = 0;
        while (i3 < exceptionsLength) {
            ReferenceBinding exception = exceptions[i3];
            if ((exception.tagBits & 0x80L) != 0L) {
                this.binding.tagBits |= 0x80L;
            }
            if (genericSignatureNeeded) {
                this.binding.modifiers |= exception.modifiers & 0x40000000;
            }
            ++i3;
        }
        TypeBinding returnType = this.binding.returnType;
        if (returnType != null) {
            TypeBinding leafType;
            if ((returnType.tagBits & 0x80L) != 0L) {
                this.binding.tagBits |= 0x80L;
            }
            if (genericSignatureNeeded && (leafType = returnType.leafComponentType()) instanceof ReferenceBinding && (((ReferenceBinding)leafType).modifiers & 0x40000000) != 0) {
                this.binding.modifiers |= 0x40000000;
            }
        }
        if (haveDescriptor && !argumentsHaveErrors && blockScope.compilerOptions().isAnnotationBasedNullAnalysisEnabled) {
            if (!argumentsTypeElided) {
                AbstractMethodDeclaration.createArgumentBindings(this.arguments, this.binding, this.scope);
                this.mergeParameterNullAnnotations(blockScope);
            }
            this.binding.tagBits |= this.descriptor.tagBits & 0x180000000000000L;
        }
        this.binding.modifiers &= 0xFDFFFFFF;
        if (this.body instanceof Expression && ((Expression)this.body).isTrulyExpression()) {
            Expression expression = (Expression)this.body;
            new ReturnStatement(expression, expression.sourceStart, expression.sourceEnd, true).resolve(this.scope);
            if (expression.resolvedType == TypeBinding.VOID && !expression.statementExpression()) {
                this.scope.problemReporter().invalidExpressionAsStatement(expression);
            }
        } else {
            this.body.resolve(this.scope);
            if (!this.returnsVoid && !this.returnsValue) {
                this.valueCompatible = this.body.doesNotCompleteNormally();
            }
        }
        if ((this.binding.tagBits & 0x80L) != 0L) {
            this.scope.problemReporter().missingTypeInLambda(this, this.binding);
        }
        if (this.shouldCaptureInstance && this.scope.isConstructorCall) {
            this.scope.problemReporter().fieldsOrThisBeforeConstructorInvocation(this);
        }
        this.updateLocalTypes();
        if (this.original == this) {
            this.committed = true;
        }
        return argumentsHaveErrors | parametersHaveErrors ? null : this.resolvedType;
    }

    private boolean isOnlyWildcardMismatch(TypeBinding expected, TypeBinding argument) {
        boolean onlyWildcardMismatch = false;
        if (expected.isParameterizedType() && argument.isParameterizedType()) {
            TypeBinding[] expectedArgs = ((ParameterizedTypeBinding)expected).typeArguments();
            TypeBinding[] args = ((ParameterizedTypeBinding)argument).typeArguments();
            if (args.length != expectedArgs.length) {
                return false;
            }
            int j = 0;
            while (j < args.length) {
                if (TypeBinding.notEquals(expectedArgs[j], args[j])) {
                    if (expectedArgs[j].isWildcard() && args[j].isUnboundWildcard()) {
                        WildcardBinding wc = (WildcardBinding)expectedArgs[j];
                        TypeBinding bound = wc.allBounds();
                        if (bound != null && wc.boundKind == 1 && bound.id == 1) {
                            onlyWildcardMismatch = true;
                        }
                    } else {
                        onlyWildcardMismatch = false;
                        break;
                    }
                }
                ++j;
            }
        }
        return onlyWildcardMismatch;
    }

    private ReferenceBinding findGroundTargetType(BlockScope blockScope, TypeBinding targetType, TypeBinding expectedSAMType, boolean argumentTypesElided) {
        if (expectedSAMType instanceof IntersectionTypeBinding18) {
            expectedSAMType = ((IntersectionTypeBinding18)expectedSAMType).getSAMType(blockScope);
        }
        if (expectedSAMType instanceof ReferenceBinding && expectedSAMType.isValidBinding()) {
            ParameterizedTypeBinding withWildCards = InferenceContext18.parameterizedWithWildcard(expectedSAMType);
            if (withWildCards != null) {
                if (!argumentTypesElided) {
                    InferenceContext18 freshInferenceContext = new InferenceContext18(blockScope);
                    try {
                        ReferenceBinding referenceBinding = freshInferenceContext.inferFunctionalInterfaceParameterization(this, blockScope, withWildCards);
                        return referenceBinding;
                    }
                    finally {
                        freshInferenceContext.cleanUp();
                    }
                }
                return this.findGroundTargetTypeForElidedLambda(blockScope, withWildCards);
            }
            if (targetType instanceof ReferenceBinding) {
                return (ReferenceBinding)targetType;
            }
        }
        return null;
    }

    public ReferenceBinding findGroundTargetTypeForElidedLambda(BlockScope blockScope, ParameterizedTypeBinding withWildCards) {
        TypeBinding[] types = withWildCards.getNonWildcardParameterization(blockScope);
        if (types == null) {
            return null;
        }
        ReferenceBinding genericType = withWildCards.genericType();
        return blockScope.environment().createParameterizedType(genericType, types, withWildCards.enclosingType());
    }

    @Override
    public boolean argumentsTypeElided() {
        return this.arguments.length > 0 && this.arguments[0].hasElidedType() || this.argumentsTypeVar;
    }

    private void analyzeExceptions() {
        CompilerOptions compilerOptions = this.scope.compilerOptions();
        boolean oldAnalyseResources = compilerOptions.analyseResourceLeaks;
        compilerOptions.analyseResourceLeaks = false;
        try {
            try {
                ExceptionInferenceFlowContext ehfc = new ExceptionInferenceFlowContext(null, this, Binding.NO_EXCEPTIONS, null, this.scope, FlowInfo.DEAD_END);
                this.body.analyseCode(this.scope, ehfc, UnconditionalFlowInfo.fakeInitializedFlowInfo(this.scope.outerMostMethodScope().analysisIndex, this.scope.referenceType().maxFieldCount));
                this.thrownExceptions = ehfc.extendedExceptions == null ? Collections.emptySet() : new HashSet(ehfc.extendedExceptions);
            }
            catch (Exception exception) {
                compilerOptions.analyseResourceLeaks = oldAnalyseResources;
            }
        }
        finally {
            compilerOptions.analyseResourceLeaks = oldAnalyseResources;
        }
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        if (this.ignoreFurtherInvestigation) {
            return flowInfo;
        }
        FlowInfo lambdaInfo = flowInfo.copy();
        ExceptionHandlingFlowContext methodContext = new ExceptionHandlingFlowContext(flowContext, this, this.binding.thrownExceptions, flowContext.getInitializationContext(), this.scope, FlowInfo.DEAD_END);
        MethodBinding methodWithParameterDeclaration = this.argumentsTypeElided() ? this.descriptor : this.binding;
        AbstractMethodDeclaration.analyseArguments(currentScope.environment(), lambdaInfo, this.arguments, methodWithParameterDeclaration);
        if (this.arguments != null) {
            int i = 0;
            int count = this.arguments.length;
            while (i < count) {
                this.bits |= this.arguments[i].bits & 0x100000;
                ++i;
            }
        }
        lambdaInfo = this.body.analyseCode(this.scope, methodContext, lambdaInfo);
        if (this.body instanceof Block) {
            TypeBinding returnTypeBinding = this.expectedResultType();
            if (returnTypeBinding == TypeBinding.VOID) {
                if ((lambdaInfo.tagBits & 1) == 0 || ((Block)this.body).statements == null) {
                    this.bits |= 0x40;
                }
            } else if (lambdaInfo != FlowInfo.DEAD_END) {
                this.scope.problemReporter().shouldReturn(returnTypeBinding, this);
            }
        } else if (currentScope.compilerOptions().isAnnotationBasedNullAnalysisEnabled && lambdaInfo.reachMode() == 0) {
            Expression expression = (Expression)this.body;
            this.checkAgainstNullAnnotation(flowContext, expression, flowInfo, expression.nullStatus(lambdaInfo, flowContext));
        }
        return flowInfo;
    }

    void validateNullAnnotations() {
        if (this.binding != null) {
            int length = this.binding.parameters.length;
            int i = 0;
            while (i < length) {
                if (!this.scope.validateNullAnnotation(this.binding.returnType.tagBits, this.arguments[i].type, this.arguments[i].annotations)) {
                    this.binding.returnType = this.binding.returnType.withoutToplevelNullAnnotation();
                }
                ++i;
            }
        }
    }

    private void mergeParameterNullAnnotations(BlockScope currentScope) {
        LookupEnvironment env = currentScope.environment();
        TypeBinding[] ourParameters = this.binding.parameters;
        TypeBinding[] descParameters = this.descriptor.parameters;
        int len = Math.min(ourParameters.length, descParameters.length);
        int i = 0;
        while (i < len) {
            long ourTagBits = ourParameters[i].tagBits & 0x180000000000000L;
            long descTagBits = descParameters[i].tagBits & 0x180000000000000L;
            if (ourTagBits == 0L) {
                if (descTagBits != 0L && !ourParameters[i].isBaseType()) {
                    AnnotationBinding[] annotations = descParameters[i].getTypeAnnotations();
                    int j = 0;
                    int length = annotations.length;
                    while (j < length) {
                        AnnotationBinding annotation = annotations[j];
                        if (annotation != null && annotation.getAnnotationType().hasNullBit(96)) {
                            ourParameters[i] = env.createAnnotatedType(ourParameters[i], new AnnotationBinding[]{annotation});
                        }
                        ++j;
                    }
                }
            } else if (ourTagBits != descTagBits && ourTagBits == 0x100000000000000L) {
                char[][] inheritedAnnotationName = null;
                if (descTagBits == 0x80000000000000L) {
                    inheritedAnnotationName = env.getNullableAnnotationName();
                }
                currentScope.problemReporter().illegalRedefinitionToNonNullParameter(this.arguments[i], this.descriptor.declaringClass, inheritedAnnotationName);
            }
            ++i;
        }
    }

    void checkAgainstNullAnnotation(FlowContext flowContext, Expression expression, FlowInfo flowInfo, int nullStatus) {
        if (nullStatus != 4 && (this.descriptor.returnType.tagBits & 0x100000000000000L) != 0L) {
            flowContext.recordNullityMismatch(this.scope, expression, expression.resolvedType, this.descriptor.returnType, flowInfo, nullStatus, null);
        }
    }

    @Override
    public boolean isPertinentToApplicability(TypeBinding targetType, MethodBinding method) {
        if (targetType == null) {
            return true;
        }
        if (this.argumentsTypeElided()) {
            return false;
        }
        if (!super.isPertinentToApplicability(targetType, method)) {
            return false;
        }
        if (this.body instanceof Expression && ((Expression)this.body).isTrulyExpression()) {
            if (!((Expression)this.body).isPertinentToApplicability(targetType, method)) {
                return false;
            }
        } else {
            Expression[] returnExpressions = this.resultExpressions;
            if (returnExpressions != NO_EXPRESSIONS) {
                int i = 0;
                int length = returnExpressions.length;
                while (i < length) {
                    if (!returnExpressions[i].isPertinentToApplicability(targetType, method)) {
                        return false;
                    }
                    ++i;
                }
            } else {
                class NotPertientToApplicability
                extends RuntimeException {
                    private static final long serialVersionUID = 1L;

                    NotPertientToApplicability() {
                    }
                }
                try {
                    class ResultsAnalyser
                    extends ASTVisitor {
                        private final /* synthetic */ TypeBinding val$targetType;
                        private final /* synthetic */ MethodBinding val$method;

                        ResultsAnalyser(TypeBinding typeBinding, MethodBinding methodBinding) {
                            this.val$targetType = typeBinding;
                            this.val$method = methodBinding;
                        }

                        @Override
                        public boolean visit(TypeDeclaration type, BlockScope skope) {
                            return false;
                        }

                        @Override
                        public boolean visit(TypeDeclaration type, ClassScope skope) {
                            return false;
                        }

                        @Override
                        public boolean visit(LambdaExpression type, BlockScope skope) {
                            return false;
                        }

                        @Override
                        public boolean visit(ReturnStatement returnStatement, BlockScope skope) {
                            if (returnStatement.expression != null && !returnStatement.expression.isPertinentToApplicability(this.val$targetType, this.val$method)) {
                                throw new NotPertientToApplicability();
                            }
                            return false;
                        }
                    }
                    this.body.traverse(new ResultsAnalyser(targetType, method), this.scope);
                }
                catch (NotPertientToApplicability notPertientToApplicability) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isVoidCompatible() {
        return this.voidCompatible;
    }

    public boolean isValueCompatible() {
        return this.valueCompatible;
    }

    @Override
    public StringBuffer printExpression(int tab, StringBuffer output) {
        return this.printExpression(tab, output, false);
    }

    public StringBuffer printExpression(int tab, StringBuffer output, boolean makeShort) {
        int parenthesesCount = (this.bits & 0x1FE00000) >> 21;
        String suffix = "";
        int i = 0;
        while (i < parenthesesCount) {
            output.append('(');
            suffix = String.valueOf(suffix) + ')';
            ++i;
        }
        output.append('(');
        if (this.arguments != null) {
            i = 0;
            while (i < this.arguments.length) {
                if (i > 0) {
                    output.append(", ");
                }
                this.arguments[i].print(0, output);
                ++i;
            }
        }
        output.append(") -> ");
        if (makeShort) {
            output.append("{}");
        } else if (this.body != null) {
            this.body.print(this.body instanceof Block ? tab : 0, output);
        } else {
            output.append("<@incubator>");
        }
        return output.append(suffix);
    }

    public TypeBinding expectedResultType() {
        return this.descriptor != null && this.descriptor.isValidBinding() ? this.descriptor.returnType : null;
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope blockScope) {
        if (visitor.visit(this, blockScope)) {
            if (this.arguments != null) {
                int argumentsLength = this.arguments.length;
                int i = 0;
                while (i < argumentsLength) {
                    this.arguments[i].traverse(visitor, this.scope);
                    ++i;
                }
            }
            if (this.body != null) {
                this.body.traverse(visitor, this.scope);
            }
        }
        visitor.endVisit(this, blockScope);
    }

    public MethodScope getScope() {
        return this.scope;
    }

    private boolean enclosingScopesHaveErrors() {
        Scope skope = this.enclosingScope;
        while (skope != null) {
            ReferenceContext context = skope.referenceContext();
            if (context != null && context.hasErrors()) {
                return true;
            }
            skope = skope.parent;
        }
        return false;
    }

    private void analyzeShape() {
        if (this.body instanceof Expression && ((Expression)this.body).isTrulyExpression()) {
            this.voidCompatible = this.assistNode ? true : ((Expression)this.body).statementExpression();
            this.valueCompatible = true;
        } else {
            if (this.assistNode) {
                this.voidCompatible = true;
                this.valueCompatible = true;
            }
            class ShapeComputer
            extends ASTVisitor {
                ShapeComputer() {
                }

                @Override
                public boolean visit(TypeDeclaration type, BlockScope skope) {
                    return false;
                }

                @Override
                public boolean visit(TypeDeclaration type, ClassScope skope) {
                    return false;
                }

                @Override
                public boolean visit(LambdaExpression type, BlockScope skope) {
                    return false;
                }

                @Override
                public boolean visit(ReturnStatement returnStatement, BlockScope skope) {
                    if (returnStatement.expression != null) {
                        LambdaExpression.this.valueCompatible = true;
                        LambdaExpression.this.voidCompatible = false;
                        LambdaExpression.this.returnsValue = true;
                    } else {
                        LambdaExpression.this.voidCompatible = true;
                        LambdaExpression.this.valueCompatible = false;
                        LambdaExpression.this.returnsVoid = true;
                    }
                    return false;
                }
            }
            this.body.traverse(new ShapeComputer(), null);
            if (!this.returnsValue && !this.returnsVoid) {
                this.valueCompatible = this.body.doesNotCompleteNormally();
            }
        }
    }

    @Override
    public boolean isPotentiallyCompatibleWith(TypeBinding targetType, Scope skope) {
        if (!super.isPertinentToApplicability(targetType, null)) {
            return true;
        }
        MethodBinding sam = targetType.getSingleAbstractMethod(skope, true);
        if (sam == null || !sam.isValidBinding()) {
            return false;
        }
        if (sam.parameters.length != this.arguments.length) {
            return false;
        }
        this.analyzeShape();
        return !(sam.returnType.id == 6 ? !this.voidCompatible : !this.valueCompatible);
    }

    public boolean reportShapeError(TypeBinding targetType, Scope skope) {
        return this.internalIsCompatibleWith(targetType, skope, true) == CompatibilityResult.REPORTED;
    }

    @Override
    public boolean isCompatibleWith(TypeBinding targetType, Scope skope) {
        return this.internalIsCompatibleWith(targetType, skope, false) == CompatibilityResult.COMPATIBLE;
    }

    CompatibilityResult internalIsCompatibleWith(TypeBinding targetType, Scope skope, boolean reportShapeProblem) {
        if (!super.isPertinentToApplicability(targetType, null)) {
            return CompatibilityResult.COMPATIBLE;
        }
        LambdaExpression copy = null;
        try {
            copy = this.cachedResolvedCopy(targetType, this.argumentsTypeElided(), false, null, skope);
        }
        catch (CopyFailureException copyFailureException) {
            if (this.assistNode) {
                return CompatibilityResult.COMPATIBLE;
            }
            return this.isPertinentToApplicability(targetType, null) ? CompatibilityResult.INCOMPATIBLE : CompatibilityResult.COMPATIBLE;
        }
        if (copy == null) {
            return CompatibilityResult.INCOMPATIBLE;
        }
        MethodBinding sam = (targetType = this.findGroundTargetType(this.enclosingScope, targetType, targetType, this.argumentsTypeElided())).getSingleAbstractMethod(this.enclosingScope, true);
        if (sam == null || sam.problemId() == 17) {
            return CompatibilityResult.INCOMPATIBLE;
        }
        if (sam.returnType.id == 6) {
            if (!copy.voidCompatible) {
                return CompatibilityResult.INCOMPATIBLE;
            }
        } else if (!copy.valueCompatible) {
            if (reportShapeProblem) {
                skope.problemReporter().missingValueFromLambda(this, sam.returnType);
                return CompatibilityResult.REPORTED;
            }
            return CompatibilityResult.INCOMPATIBLE;
        }
        if (reportShapeProblem) {
            return CompatibilityResult.COMPATIBLE;
        }
        if (!this.isPertinentToApplicability(targetType, null)) {
            return CompatibilityResult.COMPATIBLE;
        }
        if (!this.kosherDescriptor(this.enclosingScope, sam, false)) {
            return CompatibilityResult.INCOMPATIBLE;
        }
        Expression[] returnExpressions = copy.resultExpressions;
        int i = 0;
        int length = returnExpressions.length;
        while (i < length) {
            if (sam.returnType.isProperType(true) && this.enclosingScope.parameterCompatibilityLevel(returnExpressions[i].resolvedType, sam.returnType) == -1 && !returnExpressions[i].isConstantValueOfTypeAssignableToType(returnExpressions[i].resolvedType, sam.returnType) && (sam.returnType.id != 6 || this.body instanceof Block)) {
                return CompatibilityResult.INCOMPATIBLE;
            }
            ++i;
        }
        return CompatibilityResult.COMPATIBLE;
    }

    private LambdaExpression cachedResolvedCopy(TypeBinding targetType, boolean anyTargetOk, boolean requireExceptionAnalysis, InferenceContext18 context, Scope outerScope) {
        if (this.committed && outerScope instanceof BlockScope) {
            LambdaExpression firstCopy;
            this.enclosingScope = (BlockScope)outerScope;
            if (this.copiesPerTargetType != null && !this.copiesPerTargetType.isEmpty() && (firstCopy = this.copiesPerTargetType.values().iterator().next()) != null) {
                this.valueCompatible = firstCopy.valueCompatible;
                this.voidCompatible = firstCopy.voidCompatible;
            }
            return this;
        }
        if ((targetType = this.findGroundTargetType(this.enclosingScope, targetType, targetType, this.argumentsTypeElided())) == null) {
            return null;
        }
        MethodBinding sam = targetType.getSingleAbstractMethod(this.enclosingScope, true);
        if (sam == null || !sam.isValidBinding()) {
            return null;
        }
        if (sam.parameters.length != this.arguments.length) {
            return null;
        }
        LambdaExpression copy = null;
        if (this.copiesPerTargetType != null && (copy = this.copiesPerTargetType.get(targetType)) == null && anyTargetOk && this.copiesPerTargetType.values().size() > 0) {
            copy = this.copiesPerTargetType.values().iterator().next();
        }
        IErrorHandlingPolicy oldPolicy = this.enclosingScope.problemReporter().switchErrorHandlingPolicy(silentErrorHandlingPolicy);
        try {
            if (copy == null) {
                copy = this.copy();
                if (copy == null) {
                    throw new CopyFailureException();
                }
                copy.setExpressionContext(this.expressionContext);
                copy.setExpectedType(targetType);
                copy.inferenceContext = context;
                TypeBinding type = copy.resolveType(this.enclosingScope, true);
                if (type == null || !type.isValidBinding()) {
                    return null;
                }
                targetType = copy.expectedType;
                if (this.copiesPerTargetType == null) {
                    this.copiesPerTargetType = new HashMap();
                }
                this.copiesPerTargetType.put(targetType, copy);
            }
            if (!requireExceptionAnalysis) {
                LambdaExpression lambdaExpression = copy;
                return lambdaExpression;
            }
            if (copy.thrownExceptions == null && !copy.hasIgnoredMandatoryErrors && !this.enclosingScopesHaveErrors()) {
                copy.analyzeExceptions();
            }
            LambdaExpression lambdaExpression = copy;
            return lambdaExpression;
        }
        finally {
            this.enclosingScope.problemReporter().switchErrorHandlingPolicy(oldPolicy);
        }
    }

    @Override
    public LambdaExpression resolveExpressionExpecting(TypeBinding targetType, Scope skope, InferenceContext18 context) {
        LambdaExpression copy = null;
        try {
            copy = this.cachedResolvedCopy(targetType, false, true, context, null);
        }
        catch (CopyFailureException copyFailureException) {
            return null;
        }
        return copy;
    }

    @Override
    public boolean sIsMoreSpecific(TypeBinding s, TypeBinding t, Scope skope) {
        int returnExpressionsLength;
        LambdaExpression copy;
        if (super.sIsMoreSpecific(s, t, skope)) {
            return true;
        }
        if (this.argumentsTypeElided() || t.findSuperTypeOriginatingFrom(s) != null) {
            return false;
        }
        TypeBinding sPrime = s;
        MethodBinding sSam = (s = s.capture(this.enclosingScope, this.sourceStart, this.sourceEnd)).getSingleAbstractMethod(this.enclosingScope, true);
        if (sSam == null || !sSam.isValidBinding()) {
            return false;
        }
        MethodBinding tSam = t.getSingleAbstractMethod(this.enclosingScope, true);
        if (tSam == null || !tSam.isValidBinding()) {
            return true;
        }
        MethodBinding adapted = tSam.computeSubstitutedMethod(sSam, skope.environment());
        if (adapted == null) {
            return false;
        }
        MethodBinding sSamPrime = sPrime.getSingleAbstractMethod(this.enclosingScope, true);
        TypeBinding[] ps = adapted.parameters;
        MethodBinding prime = tSam.computeSubstitutedMethod(sSamPrime, skope.environment());
        TypeBinding[] pPrimes = prime.parameters;
        TypeBinding[] qs = tSam.parameters;
        int i = 0;
        while (i < ps.length) {
            if (!qs[i].isCompatibleWith(ps[i]) || TypeBinding.notEquals(qs[i], pPrimes[i])) {
                return false;
            }
            ++i;
        }
        TypeBinding r1 = adapted.returnType;
        TypeBinding r2 = tSam.returnType;
        if (r2.id == 6) {
            return true;
        }
        if (r1.id == 6) {
            return false;
        }
        if (r1.isCompatibleWith(r2, skope)) {
            return true;
        }
        try {
            copy = this.cachedResolvedCopy(s, true, false, null, null);
        }
        catch (CopyFailureException cfe) {
            if (this.assistNode) {
                return false;
            }
            throw cfe;
        }
        Expression[] returnExpressions = copy.resultExpressions;
        int n = returnExpressionsLength = returnExpressions == null ? 0 : returnExpressions.length;
        if (returnExpressionsLength > 0) {
            int i2;
            if (r1.isBaseType() && !r2.isBaseType()) {
                i2 = 0;
                while (i2 < returnExpressionsLength) {
                    if (returnExpressions[i2].isPolyExpression() || !returnExpressions[i2].resolvedType.isBaseType()) break;
                    ++i2;
                }
                if (i2 == returnExpressionsLength) {
                    return true;
                }
            }
            if (!r1.isBaseType() && r2.isBaseType()) {
                i2 = 0;
                while (i2 < returnExpressionsLength) {
                    if (returnExpressions[i2].resolvedType.isBaseType()) break;
                    ++i2;
                }
                if (i2 == returnExpressionsLength) {
                    return true;
                }
            }
            if (r1.isFunctionalInterface(this.enclosingScope) && r2.isFunctionalInterface(this.enclosingScope)) {
                i2 = 0;
                while (i2 < returnExpressionsLength) {
                    Expression resultExpression = returnExpressions[i2];
                    if (!resultExpression.sIsMoreSpecific(r1, r2, skope)) break;
                    ++i2;
                }
                if (i2 == returnExpressionsLength) {
                    return true;
                }
            }
        }
        return false;
    }

    LambdaExpression copy() {
        ICompilationUnit compilationUnit;
        char[] source;
        Parser parser = new Parser(this.enclosingScope.problemReporter(), false);
        LambdaExpression copy = (LambdaExpression)parser.parseLambdaExpression(source = (compilationUnit = this.compilationResult.getCompilationUnit()) != null ? compilationUnit.getContents() : this.text, compilationUnit != null ? this.sourceStart : 0, this.sourceEnd - this.sourceStart + 1, this.enclosingScope.referenceCompilationUnit(), false);
        if (copy != null) {
            copy.original = this;
            copy.assistNode = this.assistNode;
            copy.enclosingScope = this.enclosingScope;
        }
        return copy;
    }

    public void returnsExpression(Expression expression, TypeBinding resultType) {
        if (this.original == this) {
            return;
        }
        if (this.body instanceof Expression && ((Expression)this.body).isTrulyExpression()) {
            this.valueCompatible = resultType == null || resultType.id != 6;
            this.voidCompatible = this.assistNode ? true : ((Expression)this.body).statementExpression();
            this.resultExpressions = new Expression[]{expression};
            return;
        }
        if (expression != null) {
            this.returnsValue = true;
            this.voidCompatible = false;
            this.valueCompatible = !this.returnsVoid;
            Expression[] returnExpressions = this.resultExpressions;
            int resultsLength = returnExpressions.length;
            Expression[] expressionArray = returnExpressions;
            returnExpressions = new Expression[resultsLength + 1];
            System.arraycopy(expressionArray, 0, returnExpressions, 0, resultsLength);
            returnExpressions[resultsLength] = expression;
            this.resultExpressions = returnExpressions;
        } else {
            this.returnsVoid = true;
            this.valueCompatible = false;
            this.voidCompatible = !this.returnsValue;
        }
    }

    @Override
    public CompilationResult compilationResult() {
        return this.compilationResult;
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
            case 8: {
                throw new AbortType(this.compilationResult, problem);
            }
        }
        throw new AbortMethod(this.compilationResult, problem);
    }

    @Override
    public CompilationUnitDeclaration getCompilationUnitDeclaration() {
        return this.enclosingScope == null ? null : this.enclosingScope.compilationUnitScope().referenceContext;
    }

    @Override
    public boolean hasErrors() {
        return this.ignoreFurtherInvestigation;
    }

    @Override
    public void tagAsHavingErrors() {
        this.ignoreFurtherInvestigation = true;
        Scope parent = this.enclosingScope.parent;
        while (parent != null) {
            switch (parent.kind) {
                case 2: 
                case 3: {
                    ReferenceContext parentAST = parent.referenceContext();
                    if (parentAST == this) break;
                    parentAST.tagAsHavingErrors();
                    return;
                }
            }
            parent = parent.parent;
        }
    }

    @Override
    public void tagAsHavingIgnoredMandatoryErrors(int problemId) {
        switch (problemId) {
            case 16777362: 
            case 16777384: 
            case 16778098: {
                return;
            }
            case 99: 
            case 0x1000013: 
            case 67108969: 
            case 0x4000303: 
            case 553648781: 
            case 553648783: 
            case 553648784: 
            case 553648785: 
            case 553648786: 
            case 553648787: 
            case 603979884: {
                return;
            }
        }
        this.hasIgnoredMandatoryErrors = true;
        MethodScope enclosingLambdaScope = this.scope == null ? null : this.scope.enclosingLambdaScope();
        while (enclosingLambdaScope != null) {
            LambdaExpression enclosingLambda = (LambdaExpression)enclosingLambdaScope.referenceContext;
            enclosingLambda.hasIgnoredMandatoryErrors = true;
            enclosingLambdaScope = enclosingLambdaScope.enclosingLambdaScope();
        }
    }

    public Set<TypeBinding> getThrownExceptions() {
        if (this.thrownExceptions == null) {
            return Collections.emptySet();
        }
        return this.thrownExceptions;
    }

    public void generateCode(ClassScope classScope, ClassFile classFile) {
        int problemResetPC = 0;
        classFile.codeStream.wideMode = false;
        boolean restart = false;
        do {
            try {
                problemResetPC = classFile.contentsOffset;
                this.generateCode(classFile);
                restart = false;
            }
            catch (AbortMethod e) {
                if (e.compilationResult == CodeStream.RESTART_IN_WIDE_MODE) {
                    classFile.contentsOffset = problemResetPC;
                    --classFile.methodCount;
                    classFile.codeStream.resetInWideMode();
                    restart = true;
                    continue;
                }
                if (e.compilationResult == CodeStream.RESTART_CODE_GEN_FOR_UNUSED_LOCALS_MODE) {
                    classFile.contentsOffset = problemResetPC;
                    --classFile.methodCount;
                    classFile.codeStream.resetForCodeGenUnusedLocals();
                    restart = true;
                    continue;
                }
                throw new AbortType(this.compilationResult, e.problem);
            }
        } while (restart);
    }

    public void generateCode(ClassFile classFile) {
        LocalVariableBinding argBinding;
        int max;
        int i;
        classFile.generateMethodInfoHeader(this.binding);
        int methodAttributeOffset = classFile.contentsOffset;
        int attributeNumber = classFile.generateMethodInfoAttributes(this.binding);
        int codeAttributeOffset = classFile.contentsOffset;
        classFile.generateCodeAttributeHeader();
        CodeStream codeStream = classFile.codeStream;
        codeStream.reset(this, classFile);
        this.scope.computeLocalVariablePositions(this.outerLocalVariablesSlotSize + (this.binding.isStatic() ? 0 : 1), codeStream);
        if (this.outerLocalVariables != null) {
            i = 0;
            max = this.outerLocalVariables.length;
            while (i < max) {
                argBinding = this.outerLocalVariables[i];
                codeStream.addVisibleLocalVariable(argBinding);
                codeStream.record(argBinding);
                argBinding.recordInitializationStartPC(0);
                ++i;
            }
        }
        if (this.arguments != null) {
            i = 0;
            max = this.arguments.length;
            while (i < max) {
                argBinding = this.arguments[i].binding;
                codeStream.addVisibleLocalVariable(argBinding);
                argBinding.recordInitializationStartPC(0);
                ++i;
            }
        }
        if (this.body instanceof Block) {
            this.body.generateCode(this.scope, codeStream);
            if ((this.bits & 0x40) != 0) {
                codeStream.return_();
            }
        } else {
            Expression expression = (Expression)this.body;
            expression.generateCode(this.scope, codeStream, true);
            if (this.binding.returnType == TypeBinding.VOID) {
                codeStream.return_();
            } else {
                codeStream.generateReturnBytecode(expression);
            }
        }
        codeStream.exitUserScope(this.scope);
        codeStream.recordPositionsFrom(0, this.sourceEnd);
        try {
            classFile.completeCodeAttribute(codeAttributeOffset, this.scope);
        }
        catch (NegativeArraySizeException negativeArraySizeException) {
            throw new AbortMethod(this.scope.referenceCompilationUnit().compilationResult, null);
        }
        classFile.completeMethodInfo(this.binding, methodAttributeOffset, ++attributeNumber);
    }

    public void addSyntheticArgument(LocalVariableBinding actualOuterLocalVariable) {
        if (this.original != this || this.binding == null) {
            return;
        }
        SyntheticArgumentBinding syntheticLocal = null;
        int newSlot = this.outerLocalVariables.length;
        int i = 0;
        while (i < newSlot) {
            if (this.outerLocalVariables[i].actualOuterLocalVariable == actualOuterLocalVariable) {
                return;
            }
            ++i;
        }
        this.outerLocalVariables = new SyntheticArgumentBinding[newSlot + 1];
        System.arraycopy(this.outerLocalVariables, 0, this.outerLocalVariables, 0, newSlot);
        this.outerLocalVariables[newSlot] = syntheticLocal = new SyntheticArgumentBinding(actualOuterLocalVariable);
        syntheticLocal.resolvedPosition = this.outerLocalVariablesSlotSize;
        syntheticLocal.declaringScope = this.scope;
        int parameterCount = this.binding.parameters.length;
        TypeBinding[] newParameters = new TypeBinding[parameterCount + 1];
        newParameters[newSlot] = actualOuterLocalVariable.type;
        int i2 = 0;
        int j = 0;
        while (i2 < parameterCount) {
            if (i2 == newSlot) {
                ++j;
            }
            newParameters[j] = this.binding.parameters[i2];
            ++i2;
            ++j;
        }
        this.binding.parameters = newParameters;
        switch (syntheticLocal.type.id) {
            case 7: 
            case 8: {
                this.outerLocalVariablesSlotSize += 2;
                break;
            }
            default: {
                ++this.outerLocalVariablesSlotSize;
            }
        }
    }

    public SyntheticArgumentBinding getSyntheticArgument(LocalVariableBinding actualOuterLocalVariable) {
        int i = 0;
        int length = this.outerLocalVariables == null ? 0 : this.outerLocalVariables.length;
        while (i < length) {
            if (this.outerLocalVariables[i].actualOuterLocalVariable == actualOuterLocalVariable) {
                return this.outerLocalVariables[i];
            }
            ++i;
        }
        return null;
    }

    @Override
    public MethodBinding getMethodBinding() {
        if (this.actualMethodBinding == null) {
            if (this.binding != null) {
                TypeBinding[] newParams = null;
                if (this.binding instanceof SyntheticMethodBinding && this.outerLocalVariables.length > 0) {
                    newParams = new TypeBinding[this.binding.parameters.length - this.outerLocalVariables.length];
                    System.arraycopy(this.binding.parameters, this.outerLocalVariables.length, newParams, 0, newParams.length);
                } else {
                    newParams = this.binding.parameters;
                }
                this.actualMethodBinding = new MethodBinding(this.binding.modifiers, this.binding.selector, this.binding.returnType, newParams, this.binding.thrownExceptions, this.binding.declaringClass);
                this.actualMethodBinding.tagBits = this.binding.tagBits;
            } else {
                this.actualMethodBinding = new ProblemMethodBinding(CharOperation.NO_CHAR, null, 17);
            }
        }
        return this.actualMethodBinding;
    }

    @Override
    public int diagnosticsSourceEnd() {
        return this.body instanceof Block ? this.arrowPosition : this.sourceEnd;
    }

    public TypeBinding[] getMarkerInterfaces() {
        if (this.expectedType instanceof IntersectionTypeBinding18) {
            LinkedHashSet<ReferenceBinding> markerBindings = new LinkedHashSet<ReferenceBinding>();
            IntersectionTypeBinding18 intersectionType = (IntersectionTypeBinding18)this.expectedType;
            ReferenceBinding[] intersectionTypes = intersectionType.intersectingTypes;
            TypeBinding samType = intersectionType.getSAMType(this.enclosingScope);
            int i = 0;
            int max = intersectionTypes.length;
            while (i < max) {
                ReferenceBinding typeBinding = intersectionTypes[i];
                if (((TypeBinding)typeBinding).isInterface() && !TypeBinding.equalsEquals(samType, typeBinding) && typeBinding.id != 37) {
                    markerBindings.add(typeBinding);
                }
                ++i;
            }
            if (markerBindings.size() > 0) {
                return markerBindings.toArray(new TypeBinding[markerBindings.size()]);
            }
        }
        return null;
    }

    public ReferenceBinding getTypeBinding() {
        if (this.classType != null || this.resolvedType == null) {
            return null;
        }
        class LambdaTypeBinding
        extends ReferenceBinding {
            LambdaTypeBinding() {
            }

            @Override
            public MethodBinding[] methods() {
                return new MethodBinding[]{LambdaExpression.this.getMethodBinding()};
            }

            @Override
            public char[] sourceName() {
                return TypeConstants.LAMBDA_TYPE;
            }

            @Override
            public ReferenceBinding superclass() {
                return LambdaExpression.this.scope.getJavaLangObject();
            }

            @Override
            public ReferenceBinding[] superInterfaces() {
                return new ReferenceBinding[]{(ReferenceBinding)LambdaExpression.this.resolvedType};
            }

            @Override
            public char[] computeUniqueKey() {
                return LambdaExpression.this.descriptor.declaringClass.computeUniqueKey();
            }

            public String toString() {
                StringBuffer output = new StringBuffer("()->{} implements ");
                output.append(LambdaExpression.this.descriptor.declaringClass.sourceName());
                output.append('.');
                output.append(LambdaExpression.this.descriptor.toString());
                return output.toString();
            }
        }
        this.classType = new LambdaTypeBinding();
        return this.classType;
    }

    public void addLocalType(LocalTypeBinding localTypeBinding) {
        if (this.localTypes == null) {
            this.localTypes = new HashMap<Integer, LocalTypeBinding>();
        }
        this.localTypes.put(localTypeBinding.sourceStart, localTypeBinding);
    }

    boolean updateLocalTypes() {
        if (this.descriptor == null || this.localTypes == null) {
            return false;
        }
        LocalTypeSubstitutor substor = new LocalTypeSubstitutor(this.localTypes, null);
        Substitution.NullSubstitution subst = new Substitution.NullSubstitution(this.scope.environment());
        LambdaExpression.updateLocalTypesInMethod(this.binding, substor, subst);
        LambdaExpression.updateLocalTypesInMethod(this.descriptor, substor, subst);
        this.resolvedType = substor.substitute((Substitution)subst, this.resolvedType);
        this.expectedType = substor.substitute((Substitution)subst, this.expectedType);
        return true;
    }

    boolean updateLocalTypesInMethod(MethodBinding method) {
        if (this.localTypes == null) {
            return false;
        }
        LambdaExpression.updateLocalTypesInMethod(method, new LocalTypeSubstitutor(this.localTypes, method), new Substitution.NullSubstitution(this.scope.environment()));
        return true;
    }

    public static void updateLocalTypesInMethod(MethodBinding method, Scope.Substitutor substor, Substitution subst) {
        method.declaringClass = (ReferenceBinding)substor.substitute(subst, method.declaringClass);
        method.returnType = substor.substitute(subst, method.returnType);
        int i = 0;
        while (i < method.parameters.length) {
            method.parameters[i] = substor.substitute(subst, method.parameters[i]);
            ++i;
        }
        if (method instanceof ParameterizedGenericMethodBinding) {
            ParameterizedGenericMethodBinding pgmb = (ParameterizedGenericMethodBinding)method;
            int i2 = 0;
            while (i2 < pgmb.typeArguments.length) {
                pgmb.typeArguments[i2] = substor.substitute(subst, pgmb.typeArguments[i2]);
                ++i2;
            }
        }
    }

    private static enum CompatibilityResult {
        COMPATIBLE,
        INCOMPATIBLE,
        REPORTED;

    }

    static class CopyFailureException
    extends RuntimeException {
        private static final long serialVersionUID = 1L;

        CopyFailureException() {
        }
    }

    static class LocalTypeSubstitutor
    extends Scope.Substitutor {
        Map<Integer, LocalTypeBinding> localTypes2;

        public LocalTypeSubstitutor(Map<Integer, LocalTypeBinding> localTypes, MethodBinding methodBinding) {
            this.localTypes2 = localTypes;
            if (methodBinding != null && methodBinding.isStatic()) {
                this.staticContext = methodBinding.declaringClass;
            }
        }

        @Override
        public TypeBinding substitute(Substitution substitution, TypeBinding originalType) {
            if (originalType.isLocalType()) {
                TypeBinding substType;
                LocalTypeBinding orgLocal = (LocalTypeBinding)originalType.original();
                MethodScope lambdaScope2 = orgLocal.scope.enclosingLambdaScope();
                if (lambdaScope2 != null && (substType = (TypeBinding)this.localTypes2.get(orgLocal.sourceStart)) != null && substType != orgLocal) {
                    orgLocal.transferConstantPoolNameTo(substType);
                    return substType;
                }
                return originalType;
            }
            return super.substitute(substitution, originalType);
        }
    }
}

