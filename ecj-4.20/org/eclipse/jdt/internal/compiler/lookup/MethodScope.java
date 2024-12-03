/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.codegen.ConstantPool;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.NestedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemFieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticArgumentBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;

public class MethodScope
extends BlockScope {
    public ReferenceContext referenceContext;
    public boolean isStatic;
    public boolean isConstructorCall = false;
    public FieldBinding initializedField;
    public int lastVisibleFieldID = -1;
    private static int baseAnalysisIndex = 0;
    public int analysisIndex = baseAnalysisIndex;
    public boolean isPropagatingInnerClassEmulation;
    public int lastIndex = 0;
    public long[] definiteInits = new long[4];
    public long[][] extraDefiniteInits = new long[4][];
    public SyntheticArgumentBinding[] extraSyntheticArguments;
    public boolean hasMissingSwitchDefault;
    public boolean isCompactConstructorScope = false;

    static {
        if (Boolean.getBoolean("jdt.flow.test.extra")) {
            baseAnalysisIndex = 64;
            System.out.println("JDT/Core testing with -Djdt.flow.test.extra=true");
        }
    }

    public MethodScope(Scope parent, ReferenceContext context, boolean isStatic) {
        super(2, parent);
        this.locals = new LocalVariableBinding[5];
        this.referenceContext = context;
        this.isStatic = isStatic;
        this.startIndex = 0;
    }

    public MethodScope(Scope parent, ReferenceContext context, boolean isStatic, int lastVisibleFieldID) {
        this(parent, context, isStatic);
        this.lastVisibleFieldID = lastVisibleFieldID;
    }

    @Override
    String basicToString(int tab) {
        String newLine = "\n";
        int i = tab;
        while (--i >= 0) {
            newLine = String.valueOf(newLine) + "\t";
        }
        String s = String.valueOf(newLine) + "--- Method Scope ---";
        newLine = String.valueOf(newLine) + "\t";
        s = String.valueOf(s) + newLine + "locals:";
        int i2 = 0;
        while (i2 < this.localIndex) {
            s = String.valueOf(s) + newLine + "\t" + this.locals[i2].toString();
            ++i2;
        }
        s = String.valueOf(s) + newLine + "startIndex = " + this.startIndex;
        s = String.valueOf(s) + newLine + "isConstructorCall = " + this.isConstructorCall;
        s = String.valueOf(s) + newLine + "initializedField = " + this.initializedField;
        s = String.valueOf(s) + newLine + "lastVisibleFieldID = " + this.lastVisibleFieldID;
        s = String.valueOf(s) + newLine + "referenceContext = " + this.referenceContext;
        return s;
    }

    private void checkAndSetModifiersForConstructor(MethodBinding methodBinding) {
        int flags;
        int astNodeBits;
        int modifiers = methodBinding.modifiers;
        ReferenceBinding declaringClass = methodBinding.declaringClass;
        if ((modifiers & 0x400000) != 0) {
            this.problemReporter().duplicateModifierForMethod(declaringClass, (AbstractMethodDeclaration)this.referenceContext);
        }
        if ((((astNodeBits = ((ConstructorDeclaration)this.referenceContext).bits) & 0x80) != 0 || (astNodeBits & 0x400) != 0 && (astNodeBits & 0x200) != 0) && (flags = declaringClass.modifiers & 0x4005) != 0) {
            if ((flags & 0x4000) != 0) {
                modifiers &= 0xFFFFFFF8;
                modifiers |= 2;
            } else {
                modifiers &= 0xFFFFFFF8;
                modifiers |= flags;
            }
        }
        int realModifiers = modifiers & 0xFFFF;
        if (declaringClass.isEnum() && (((ConstructorDeclaration)this.referenceContext).bits & 0x80) == 0) {
            if ((realModifiers & 0xFFFFF7FD) != 0) {
                this.problemReporter().illegalModifierForEnumConstructor((AbstractMethodDeclaration)this.referenceContext);
                modifiers &= 0xFFFF0802;
            } else if ((((AbstractMethodDeclaration)this.referenceContext).modifiers & 0x800) != 0) {
                this.problemReporter().illegalModifierForMethod((AbstractMethodDeclaration)this.referenceContext);
            }
            modifiers |= 2;
        } else if ((realModifiers & 0xFFFFF7F8) != 0) {
            this.problemReporter().illegalModifierForMethod((AbstractMethodDeclaration)this.referenceContext);
            modifiers &= 0xFFFF0807;
        } else if ((((AbstractMethodDeclaration)this.referenceContext).modifiers & 0x800) != 0) {
            this.problemReporter().illegalModifierForMethod((AbstractMethodDeclaration)this.referenceContext);
        }
        int accessorBits = realModifiers & 7;
        if ((accessorBits & accessorBits - 1) != 0) {
            this.problemReporter().illegalVisibilityModifierCombinationForMethod(declaringClass, (AbstractMethodDeclaration)this.referenceContext);
            if ((accessorBits & 1) != 0) {
                if ((accessorBits & 4) != 0) {
                    modifiers &= 0xFFFFFFFB;
                }
                if ((accessorBits & 2) != 0) {
                    modifiers &= 0xFFFFFFFD;
                }
            } else if ((accessorBits & 4) != 0 && (accessorBits & 2) != 0) {
                modifiers &= 0xFFFFFFFD;
            }
        }
        methodBinding.modifiers = modifiers;
    }

    private void checkAndSetModifiersForMethod(MethodBinding methodBinding) {
        int accessorBits;
        int modifiers = methodBinding.modifiers;
        ReferenceBinding declaringClass = methodBinding.declaringClass;
        if ((modifiers & 0x400000) != 0) {
            this.problemReporter().duplicateModifierForMethod(declaringClass, (AbstractMethodDeclaration)this.referenceContext);
        }
        int realModifiers = modifiers & 0xFFFF;
        long sourceLevel = this.compilerOptions().sourceLevel;
        if (declaringClass.isInterface()) {
            int expectedModifiers = 1025;
            boolean isDefaultMethod = (modifiers & 0x10000) != 0;
            boolean reportIllegalModifierCombination = false;
            if (sourceLevel >= 0x340000L && !declaringClass.isAnnotationType()) {
                int remaining;
                expectedModifiers |= 0x10808;
                expectedModifiers |= sourceLevel >= 0x350000L ? 2 : 0;
                if (!methodBinding.isAbstract()) {
                    reportIllegalModifierCombination = isDefaultMethod && methodBinding.isStatic();
                } else {
                    boolean bl = reportIllegalModifierCombination = isDefaultMethod || methodBinding.isStatic();
                    if (methodBinding.isStrictfp()) {
                        this.problemReporter().illegalAbstractModifierCombinationForMethod((AbstractMethodDeclaration)this.referenceContext);
                    }
                }
                if (reportIllegalModifierCombination) {
                    this.problemReporter().illegalModifierCombinationForInterfaceMethod((AbstractMethodDeclaration)this.referenceContext);
                }
                if (sourceLevel >= 0x350000L && (methodBinding.modifiers & 2) != 0 && (remaining = realModifiers & ~expectedModifiers) == 0) {
                    remaining = realModifiers & 0xFFFFF7F5;
                    if (isDefaultMethod || remaining != 0) {
                        this.problemReporter().illegalModifierCombinationForPrivateInterfaceMethod((AbstractMethodDeclaration)this.referenceContext);
                    }
                }
                if (isDefaultMethod) {
                    realModifiers |= 0x10000;
                }
            }
            if ((realModifiers & ~expectedModifiers) != 0) {
                if ((declaringClass.modifiers & 0x2000) != 0) {
                    this.problemReporter().illegalModifierForAnnotationMember((AbstractMethodDeclaration)this.referenceContext);
                } else {
                    this.problemReporter().illegalModifierForInterfaceMethod((AbstractMethodDeclaration)this.referenceContext, sourceLevel);
                }
                methodBinding.modifiers &= expectedModifiers | 0xFFFF0000;
            }
            return;
        }
        if (declaringClass.isAnonymousType() && sourceLevel >= 0x350000L) {
            LocalTypeBinding local = (LocalTypeBinding)declaringClass;
            TypeReference ref = local.scope.referenceContext.allocation.type;
            if (ref != null && (ref.bits & 0x80000) != 0 && (realModifiers & 0xA) == 0) {
                methodBinding.tagBits |= 0x2000000000000L;
            }
        }
        if ((realModifiers & 0xFFFFF2C0) != 0) {
            this.problemReporter().illegalModifierForMethod((AbstractMethodDeclaration)this.referenceContext);
            modifiers &= 0xFFFF0D3F;
        }
        if (((accessorBits = realModifiers & 7) & accessorBits - 1) != 0) {
            this.problemReporter().illegalVisibilityModifierCombinationForMethod(declaringClass, (AbstractMethodDeclaration)this.referenceContext);
            if ((accessorBits & 1) != 0) {
                if ((accessorBits & 4) != 0) {
                    modifiers &= 0xFFFFFFFB;
                }
                if ((accessorBits & 2) != 0) {
                    modifiers &= 0xFFFFFFFD;
                }
            } else if ((accessorBits & 4) != 0 && (accessorBits & 2) != 0) {
                modifiers &= 0xFFFFFFFD;
            }
        }
        if ((modifiers & 0x400) != 0) {
            int incompatibleWithAbstract = 2362;
            if ((modifiers & incompatibleWithAbstract) != 0) {
                this.problemReporter().illegalAbstractModifierCombinationForMethod(declaringClass, (AbstractMethodDeclaration)this.referenceContext);
            }
            if (!methodBinding.declaringClass.isAbstract()) {
                this.problemReporter().abstractMethodInAbstractClass((SourceTypeBinding)declaringClass, (AbstractMethodDeclaration)this.referenceContext);
            }
        }
        if ((modifiers & 0x100) != 0 && (modifiers & 0x800) != 0) {
            this.problemReporter().nativeMethodsCannotBeStrictfp(declaringClass, (AbstractMethodDeclaration)this.referenceContext);
        }
        if (sourceLevel < 0x3C0000L && (realModifiers & 8) != 0 && declaringClass.isNestedType() && !declaringClass.isStatic()) {
            this.problemReporter().unexpectedStaticModifierForMethod(declaringClass, (AbstractMethodDeclaration)this.referenceContext);
        }
        methodBinding.modifiers = modifiers;
    }

    public void checkUnusedParameters(MethodBinding method) {
        if (method.isAbstract() || method.isImplementing() && !this.compilerOptions().reportUnusedParameterWhenImplementingAbstract || method.isOverriding() && !method.isImplementing() && !this.compilerOptions().reportUnusedParameterWhenOverridingConcrete || method.isMain()) {
            return;
        }
        int i = 0;
        int maxLocals = this.localIndex;
        while (i < maxLocals) {
            LocalVariableBinding local = this.locals[i];
            if (local == null || (local.tagBits & 0x400L) == 0L) break;
            if (local.useFlag == 0 && (local.declaration.bits & 0x40000000) != 0) {
                this.problemReporter().unusedArgument(local.declaration);
            }
            ++i;
        }
    }

    public void computeLocalVariablePositions(int initOffset, CodeStream codeStream) {
        this.offset = initOffset;
        this.maxOffset = initOffset;
        int ilocal = 0;
        int maxLocals = this.localIndex;
        while (ilocal < maxLocals) {
            LocalVariableBinding local = this.locals[ilocal];
            if (local == null || (local.tagBits & 0x400L) == 0L) break;
            codeStream.record(local);
            local.resolvedPosition = this.offset++;
            if (TypeBinding.equalsEquals(local.type, TypeBinding.LONG) || TypeBinding.equalsEquals(local.type, TypeBinding.DOUBLE)) {
                this.offset += 2;
            }
            if (this.offset > 255) {
                this.problemReporter().noMoreAvailableSpaceForArgument(local, local.declaration);
            }
            ++ilocal;
        }
        if (this.extraSyntheticArguments != null) {
            int iarg = 0;
            int maxArguments = this.extraSyntheticArguments.length;
            while (iarg < maxArguments) {
                SyntheticArgumentBinding argument = this.extraSyntheticArguments[iarg];
                argument.resolvedPosition = this.offset++;
                if (TypeBinding.equalsEquals(argument.type, TypeBinding.LONG) || TypeBinding.equalsEquals(argument.type, TypeBinding.DOUBLE)) {
                    this.offset += 2;
                }
                if (this.offset > 255) {
                    this.problemReporter().noMoreAvailableSpaceForArgument(argument, (ASTNode)((Object)this.referenceContext));
                }
                ++iarg;
            }
        }
        this.computeLocalVariablePositions(ilocal, this.offset, codeStream);
    }

    MethodBinding createMethod(AbstractMethodDeclaration method) {
        TypeParameter[] typeParameters;
        int argLength;
        this.referenceContext = method;
        method.scope = this;
        long sourceLevel = this.compilerOptions().sourceLevel;
        SourceTypeBinding declaringClass = this.referenceType().binding;
        int modifiers = method.modifiers | 0x2000000;
        if (method.isConstructor()) {
            if (method.isDefaultConstructor()) {
                modifiers |= 0x4000000;
            }
            method.binding = new MethodBinding(modifiers, null, null, declaringClass);
            this.checkAndSetModifiersForConstructor(method.binding);
        } else {
            if (declaringClass.isInterface() && (sourceLevel < 0x350000L || (method.modifiers & 2) == 0)) {
                modifiers = method.isDefaultMethod() || method.isStatic() ? (modifiers |= 1) : (modifiers |= 0x401);
            }
            method.binding = new MethodBinding(modifiers, method.selector, null, null, null, declaringClass);
            this.checkAndSetModifiersForMethod(method.binding);
        }
        this.isStatic = method.binding.isStatic();
        Argument[] argTypes = method.arguments;
        int n = argLength = argTypes == null ? 0 : argTypes.length;
        if (argLength > 0) {
            Argument argument = argTypes[argLength - 1];
            method.binding.parameterNames = new char[argLength][];
            method.binding.parameterNames[--argLength] = argument.name;
            if (argument.isVarArgs() && sourceLevel >= 0x310000L) {
                method.binding.modifiers |= 0x80;
            }
            if (CharOperation.equals(argument.name, ConstantPool.This)) {
                this.problemReporter().illegalThisDeclaration(argument);
            }
            while (--argLength >= 0) {
                argument = argTypes[argLength];
                method.binding.parameterNames[argLength] = argument.name;
                if (argument.isVarArgs() && sourceLevel >= 0x310000L) {
                    this.problemReporter().illegalVararg(argument, method);
                }
                if (!CharOperation.equals(argument.name, ConstantPool.This)) continue;
                this.problemReporter().illegalThisDeclaration(argument);
            }
        }
        if (method.receiver != null) {
            if (sourceLevel <= 0x330000L) {
                this.problemReporter().illegalSourceLevelForThis(method.receiver);
            }
            if (method.receiver.annotations != null) {
                method.bits |= 0x100000;
            }
        }
        if ((typeParameters = method.typeParameters()) == null || typeParameters.length == 0) {
            method.binding.typeVariables = Binding.NO_TYPE_VARIABLES;
        } else {
            method.binding.typeVariables = this.createTypeVariables(typeParameters, method.binding);
            method.binding.modifiers |= 0x40000000;
        }
        this.checkAndSetRecordCanonicalConsAndMethods(method);
        return method.binding;
    }

    private void checkAndSetRecordCanonicalConsAndMethods(AbstractMethodDeclaration am) {
        if (am.binding != null && (am.bits & 0x400) != 0) {
            am.binding.tagBits |= 0x1000L;
            am.binding.tagBits = am.binding.tagBits | ((am.bits & 0x200) != 0 ? 2048L : 0L);
        }
    }

    @Override
    public FieldBinding findField(TypeBinding receiverType, char[] fieldName, InvocationSite invocationSite, boolean needResolve) {
        FieldBinding field = super.findField(receiverType, fieldName, invocationSite, needResolve);
        if (field == null) {
            return null;
        }
        if (!field.isValidBinding()) {
            return field;
        }
        if (receiverType.isInterface() && invocationSite.isQualifiedSuper()) {
            return new ProblemFieldBinding(field, field.declaringClass, fieldName, 28);
        }
        if (field.isStatic()) {
            return field;
        }
        if (!this.isConstructorCall || TypeBinding.notEquals(receiverType, this.enclosingSourceType())) {
            return field;
        }
        if (invocationSite instanceof SingleNameReference) {
            return new ProblemFieldBinding(field, field.declaringClass, fieldName, 6);
        }
        if (invocationSite instanceof QualifiedNameReference) {
            QualifiedNameReference name = (QualifiedNameReference)invocationSite;
            if (name.binding == null) {
                return new ProblemFieldBinding(field, field.declaringClass, fieldName, 6);
            }
        }
        return field;
    }

    public boolean isInsideConstructor() {
        return this.referenceContext instanceof ConstructorDeclaration;
    }

    public boolean isInsideInitializer() {
        return this.referenceContext instanceof TypeDeclaration;
    }

    @Override
    public boolean isLambdaScope() {
        return this.referenceContext instanceof LambdaExpression;
    }

    public boolean isInsideInitializerOrConstructor() {
        return this.referenceContext instanceof TypeDeclaration || this.referenceContext instanceof ConstructorDeclaration;
    }

    @Override
    public ProblemReporter problemReporter() {
        ProblemReporter problemReporter = this.referenceCompilationUnit().problemReporter;
        problemReporter.referenceContext = this.referenceContext;
        return problemReporter;
    }

    public final int recordInitializationStates(FlowInfo flowInfo) {
        if ((flowInfo.tagBits & 1) != 0) {
            return -1;
        }
        UnconditionalFlowInfo unconditionalFlowInfo = flowInfo.unconditionalInitsWithoutSideEffect();
        long[] extraInits = unconditionalFlowInfo.extra == null ? null : unconditionalFlowInfo.extra[0];
        long inits = unconditionalFlowInfo.definiteInits;
        int i = this.lastIndex;
        block0: while (--i >= 0) {
            if (this.definiteInits[i] != inits) continue;
            long[] otherInits = this.extraDefiniteInits[i];
            if (extraInits != null && otherInits != null) {
                if (extraInits.length != otherInits.length) continue;
                int j = 0;
                int max = extraInits.length;
                while (j < max) {
                    if (extraInits[j] != otherInits[j]) continue block0;
                    ++j;
                }
                return i;
            }
            if (extraInits != null || otherInits != null) continue;
            return i;
        }
        if (this.definiteInits.length == this.lastIndex) {
            this.definiteInits = new long[this.lastIndex + 20];
            System.arraycopy(this.definiteInits, 0, this.definiteInits, 0, this.lastIndex);
            long[][] lArrayArray = new long[this.lastIndex + 20][];
            this.extraDefiniteInits = lArrayArray;
            System.arraycopy(this.extraDefiniteInits, 0, lArrayArray, 0, this.lastIndex);
        }
        this.definiteInits[this.lastIndex] = inits;
        if (extraInits != null) {
            this.extraDefiniteInits[this.lastIndex] = new long[extraInits.length];
            System.arraycopy(extraInits, 0, this.extraDefiniteInits[this.lastIndex], 0, extraInits.length);
        }
        return this.lastIndex++;
    }

    public AbstractMethodDeclaration referenceMethod() {
        if (this.referenceContext instanceof AbstractMethodDeclaration) {
            return (AbstractMethodDeclaration)this.referenceContext;
        }
        return null;
    }

    public MethodBinding referenceMethodBinding() {
        if (this.referenceContext instanceof LambdaExpression) {
            return ((LambdaExpression)this.referenceContext).binding;
        }
        if (this.referenceContext instanceof AbstractMethodDeclaration) {
            return ((AbstractMethodDeclaration)this.referenceContext).binding;
        }
        return null;
    }

    @Override
    public TypeDeclaration referenceType() {
        ClassScope scope = this.enclosingClassScope();
        return scope == null ? null : scope.referenceContext;
    }

    @Override
    void resolveTypeParameter(TypeParameter typeParameter) {
        typeParameter.resolve(this);
    }

    @Override
    public boolean hasDefaultNullnessFor(int location, int sourceStart) {
        MethodBinding binding;
        int nonNullByDefaultValue = this.localNonNullByDefaultValue(sourceStart);
        if (nonNullByDefaultValue != 0) {
            return (nonNullByDefaultValue & location) != 0;
        }
        AbstractMethodDeclaration referenceMethod = this.referenceMethod();
        if (referenceMethod != null && (binding = referenceMethod.binding) != null && binding.defaultNullness != 0) {
            return (binding.defaultNullness & location) != 0;
        }
        return this.parent.hasDefaultNullnessFor(location, sourceStart);
    }

    @Override
    public Binding checkRedundantDefaultNullness(int nullBits, int sourceStart) {
        MethodBinding binding;
        Binding target = this.localCheckRedundantDefaultNullness(nullBits, sourceStart);
        if (target != null) {
            return target;
        }
        AbstractMethodDeclaration referenceMethod = this.referenceMethod();
        if (referenceMethod != null && (binding = referenceMethod.binding) != null && binding.defaultNullness != 0) {
            return binding.defaultNullness == nullBits ? binding : null;
        }
        return this.parent.checkRedundantDefaultNullness(nullBits, sourceStart);
    }

    public boolean shouldCheckAPILeaks(ReferenceBinding declaringClass, boolean memberIsPublic) {
        if (this.environment().useModuleSystem) {
            return memberIsPublic && declaringClass.isPublic() && declaringClass.fPackage.isExported();
        }
        return false;
    }

    public void detectAPILeaks(ASTNode typeNode, TypeBinding type) {
        if (this.environment().useModuleSystem) {
            ASTVisitor visitor = new ASTVisitor(){

                @Override
                public boolean visit(SingleTypeReference typeReference, BlockScope scope) {
                    if (typeReference.resolvedType instanceof ReferenceBinding) {
                        this.checkType((ReferenceBinding)typeReference.resolvedType, typeReference.sourceStart, typeReference.sourceEnd);
                    }
                    return true;
                }

                @Override
                public boolean visit(QualifiedTypeReference typeReference, BlockScope scope) {
                    if (typeReference.resolvedType instanceof ReferenceBinding) {
                        this.checkType((ReferenceBinding)typeReference.resolvedType, typeReference.sourceStart, typeReference.sourceEnd);
                    }
                    return true;
                }

                @Override
                public boolean visit(ArrayTypeReference typeReference, BlockScope scope) {
                    TypeBinding leafComponentType = typeReference.resolvedType.leafComponentType();
                    if (leafComponentType instanceof ReferenceBinding) {
                        this.checkType((ReferenceBinding)leafComponentType, typeReference.sourceStart, typeReference.originalSourceEnd);
                    }
                    return true;
                }

                private void checkType(ReferenceBinding referenceBinding, int sourceStart, int sourceEnd) {
                    if (!referenceBinding.isValidBinding()) {
                        return;
                    }
                    ModuleBinding otherModule = referenceBinding.module();
                    if (otherModule == otherModule.environment.javaBaseModule()) {
                        return;
                    }
                    if (!this.isFullyPublic(referenceBinding)) {
                        MethodScope.this.problemReporter().nonPublicTypeInAPI(referenceBinding, sourceStart, sourceEnd);
                    } else if (!referenceBinding.fPackage.isExported()) {
                        MethodScope.this.problemReporter().notExportedTypeInAPI(referenceBinding, sourceStart, sourceEnd);
                    } else if (this.isUnrelatedModule(referenceBinding.fPackage)) {
                        MethodScope.this.problemReporter().missingRequiresTransitiveForTypeInAPI(referenceBinding, sourceStart, sourceEnd);
                    }
                }

                private boolean isFullyPublic(ReferenceBinding referenceBinding) {
                    if (!referenceBinding.isPublic()) {
                        return false;
                    }
                    if (referenceBinding instanceof NestedTypeBinding) {
                        return this.isFullyPublic(((NestedTypeBinding)referenceBinding).enclosingType);
                    }
                    return true;
                }

                private boolean isUnrelatedModule(PackageBinding fPackage) {
                    ModuleBinding otherModule = fPackage.enclosingModule;
                    ModuleBinding thisModule = MethodScope.this.module();
                    if (thisModule != otherModule) {
                        return !thisModule.isTransitivelyRequired(otherModule);
                    }
                    return false;
                }
            };
            typeNode.traverse(visitor, this);
        }
    }
}

