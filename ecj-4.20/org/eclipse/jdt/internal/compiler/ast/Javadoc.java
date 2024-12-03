/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.IJavadocTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.JavadocFieldReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocMessageSend;
import org.eclipse.jdt.internal.compiler.ast.JavadocModuleReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.JavadocSingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ModuleDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ModuleReference;
import org.eclipse.jdt.internal.compiler.ast.ProvidesStatement;
import org.eclipse.jdt.internal.compiler.ast.RecordComponent;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.UsesStatement;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.ImportBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.ModuleScope;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;

public class Javadoc
extends ASTNode {
    public JavadocSingleNameReference[] paramReferences;
    public JavadocSingleTypeReference[] paramTypeParameters;
    public TypeReference[] exceptionReferences;
    public JavadocReturnStatement returnStatement;
    public Expression[] seeReferences;
    public IJavadocTypeReference[] usesReferences;
    public IJavadocTypeReference[] providesReferences;
    public long[] inheritedPositions = null;
    public JavadocSingleNameReference[] invalidParameters;
    public long valuePositions = -1L;

    public Javadoc(int sourceStart, int sourceEnd) {
        this.sourceStart = sourceStart;
        this.sourceEnd = sourceEnd;
        this.bits |= 0x10000;
    }

    boolean canBeSeen(int visibility, int modifiers) {
        if (modifiers < 0) {
            return true;
        }
        switch (modifiers & 7) {
            case 1: {
                return true;
            }
            case 4: {
                return visibility != 1;
            }
            case 0: {
                return visibility == 0 || visibility == 2;
            }
            case 2: {
                return visibility == 2;
            }
        }
        return true;
    }

    public ASTNode getNodeStartingAt(int start) {
        Expression param;
        int i;
        int length = 0;
        if (this.paramReferences != null) {
            length = this.paramReferences.length;
            i = 0;
            while (i < length) {
                param = this.paramReferences[i];
                if (param.sourceStart == start) {
                    return param;
                }
                ++i;
            }
        }
        if (this.invalidParameters != null) {
            length = this.invalidParameters.length;
            i = 0;
            while (i < length) {
                param = this.invalidParameters[i];
                if (param.sourceStart == start) {
                    return param;
                }
                ++i;
            }
        }
        if (this.paramTypeParameters != null) {
            length = this.paramTypeParameters.length;
            i = 0;
            while (i < length) {
                param = this.paramTypeParameters[i];
                if (((JavadocSingleTypeReference)param).sourceStart == start) {
                    return param;
                }
                ++i;
            }
        }
        if (this.exceptionReferences != null) {
            length = this.exceptionReferences.length;
            i = 0;
            while (i < length) {
                TypeReference typeRef = this.exceptionReferences[i];
                if (typeRef.sourceStart == start) {
                    return typeRef;
                }
                ++i;
            }
        }
        if (this.seeReferences != null) {
            length = this.seeReferences.length;
            i = 0;
            while (i < length) {
                int l;
                Expression expression = this.seeReferences[i];
                if (expression.sourceStart == start) {
                    return expression;
                }
                if (expression instanceof JavadocAllocationExpression) {
                    JavadocAllocationExpression allocationExpr = (JavadocAllocationExpression)this.seeReferences[i];
                    if (allocationExpr.binding != null && allocationExpr.binding.isValidBinding() && allocationExpr.arguments != null) {
                        int j = 0;
                        l = allocationExpr.arguments.length;
                        while (j < l) {
                            if (allocationExpr.arguments[j].sourceStart == start) {
                                return allocationExpr.arguments[j];
                            }
                            ++j;
                        }
                    }
                } else if (expression instanceof JavadocMessageSend) {
                    JavadocMessageSend messageSend = (JavadocMessageSend)this.seeReferences[i];
                    if (messageSend.binding != null && messageSend.binding.isValidBinding() && messageSend.arguments != null) {
                        int j = 0;
                        l = messageSend.arguments.length;
                        while (j < l) {
                            if (messageSend.arguments[j].sourceStart == start) {
                                return messageSend.arguments[j];
                            }
                            ++j;
                        }
                    }
                } else if (expression instanceof JavadocModuleReference) {
                    JavadocModuleReference modRef = (JavadocModuleReference)expression;
                    if (modRef.typeReference != null && modRef.typeReference.sourceStart == start) {
                        return modRef.typeReference;
                    }
                } else if (expression instanceof JavadocFieldReference) {
                    JavadocFieldReference fieldRef = (JavadocFieldReference)expression;
                    if (fieldRef.receiver instanceof JavadocModuleReference) {
                        JavadocModuleReference modRef = (JavadocModuleReference)fieldRef.receiver;
                        if (modRef.sourceStart == start) {
                            return modRef;
                        }
                        if (modRef.typeReference != null && modRef.typeReference.sourceStart == start) {
                            return modRef.typeReference;
                        }
                    }
                }
                ++i;
            }
        }
        return null;
    }

    @Override
    public StringBuffer print(int indent, StringBuffer output) {
        int length;
        int i;
        Javadoc.printIndent(indent, output).append("/**\n");
        if (this.paramReferences != null) {
            i = 0;
            length = this.paramReferences.length;
            while (i < length) {
                Javadoc.printIndent(indent + 1, output).append(" * @param ");
                this.paramReferences[i].print(indent, output).append('\n');
                ++i;
            }
        }
        if (this.paramTypeParameters != null) {
            i = 0;
            length = this.paramTypeParameters.length;
            while (i < length) {
                Javadoc.printIndent(indent + 1, output).append(" * @param <");
                this.paramTypeParameters[i].print(indent, output).append(">\n");
                ++i;
            }
        }
        if (this.returnStatement != null) {
            Javadoc.printIndent(indent + 1, output).append(" * @");
            this.returnStatement.print(indent, output).append('\n');
        }
        if (this.exceptionReferences != null) {
            i = 0;
            length = this.exceptionReferences.length;
            while (i < length) {
                Javadoc.printIndent(indent + 1, output).append(" * @throws ");
                this.exceptionReferences[i].print(indent, output).append('\n');
                ++i;
            }
        }
        if (this.seeReferences != null) {
            i = 0;
            length = this.seeReferences.length;
            while (i < length) {
                Javadoc.printIndent(indent + 1, output).append(" * @see ");
                this.seeReferences[i].print(indent, output).append('\n');
                ++i;
            }
        }
        Javadoc.printIndent(indent, output).append(" */\n");
        return output;
    }

    public void resolve(ClassScope scope) {
        boolean source15;
        int i;
        if ((this.bits & 0x10000) == 0) {
            return;
        }
        this.bits &= 0xFFFEFFFF;
        if (this.inheritedPositions != null) {
            int length = this.inheritedPositions.length;
            i = 0;
            while (i < length) {
                int start = (int)(this.inheritedPositions[i] >>> 32);
                int end = (int)this.inheritedPositions[i];
                scope.problemReporter().javadocUnexpectedTag(start, end);
                ++i;
            }
        }
        int paramTagsSize = this.paramReferences == null ? 0 : this.paramReferences.length;
        i = 0;
        while (i < paramTagsSize) {
            if (scope.referenceContext.nRecordComponents > 0) break;
            JavadocSingleNameReference param = this.paramReferences[i];
            scope.problemReporter().javadocUnexpectedTag(param.tagSourceStart, param.tagSourceEnd);
            ++i;
        }
        this.resolveTypeParameterTags(scope, true);
        if (this.returnStatement != null) {
            scope.problemReporter().javadocUnexpectedTag(this.returnStatement.sourceStart, this.returnStatement.sourceEnd);
        }
        int throwsTagsLength = this.exceptionReferences == null ? 0 : this.exceptionReferences.length;
        int i2 = 0;
        while (i2 < throwsTagsLength) {
            int end;
            int start;
            TypeReference typeRef = this.exceptionReferences[i2];
            if (typeRef instanceof JavadocSingleTypeReference) {
                JavadocSingleTypeReference singleRef = (JavadocSingleTypeReference)typeRef;
                start = singleRef.tagSourceStart;
                end = singleRef.tagSourceEnd;
            } else if (typeRef instanceof JavadocQualifiedTypeReference) {
                JavadocQualifiedTypeReference qualifiedRef = (JavadocQualifiedTypeReference)typeRef;
                start = qualifiedRef.tagSourceStart;
                end = qualifiedRef.tagSourceEnd;
            } else {
                start = typeRef.sourceStart;
                end = typeRef.sourceEnd;
            }
            scope.problemReporter().javadocUnexpectedTag(start, end);
            ++i2;
        }
        int seeTagsLength = this.seeReferences == null ? 0 : this.seeReferences.length;
        int i3 = 0;
        while (i3 < seeTagsLength) {
            this.resolveReference(this.seeReferences[i3], scope);
            ++i3;
        }
        boolean bl = source15 = scope.compilerOptions().sourceLevel >= 0x310000L;
        if (!source15 && this.valuePositions != -1L) {
            scope.problemReporter().javadocUnexpectedTag((int)(this.valuePositions >>> 32), (int)this.valuePositions);
        }
    }

    public void resolve(CompilationUnitScope unitScope) {
        if ((this.bits & 0x10000) == 0) {
            return;
        }
    }

    public void resolve(ModuleScope moduleScope) {
        if ((this.bits & 0x10000) == 0) {
            return;
        }
        this.bits &= 0xFFFEFFFF;
        int seeTagsLength = this.seeReferences == null ? 0 : this.seeReferences.length;
        int i = 0;
        while (i < seeTagsLength) {
            this.resolveReference(this.seeReferences[i], moduleScope);
            ++i;
        }
        this.resolveUsesTags(moduleScope, true);
        this.resolveProvidesTags(moduleScope, true);
    }

    public void resolve(MethodScope methScope) {
        boolean source15;
        boolean reportMissing;
        if ((this.bits & 0x10000) == 0) {
            return;
        }
        this.bits &= 0xFFFEFFFF;
        AbstractMethodDeclaration methDecl = methScope.referenceMethod();
        boolean overriding = methDecl == null || methDecl.binding == null ? false : !methDecl.binding.isStatic() && (methDecl.binding.modifiers & 0x30000000) != 0;
        int seeTagsLength = this.seeReferences == null ? 0 : this.seeReferences.length;
        boolean superRef = false;
        int i = 0;
        while (i < seeTagsLength) {
            this.resolveReference(this.seeReferences[i], methScope);
            if (methDecl != null && !superRef) {
                TypeBinding superType;
                if (!methDecl.isConstructor()) {
                    if (overriding && this.seeReferences[i] instanceof JavadocMessageSend) {
                        ReferenceBinding methodReceiverType;
                        JavadocMessageSend messageSend = (JavadocMessageSend)this.seeReferences[i];
                        if (messageSend.binding != null && messageSend.binding.isValidBinding() && messageSend.actualReceiverType instanceof ReferenceBinding && (superType = methDecl.binding.declaringClass.findSuperTypeOriginatingFrom(methodReceiverType = (ReferenceBinding)messageSend.actualReceiverType)) != null && TypeBinding.notEquals(superType.original(), methDecl.binding.declaringClass) && CharOperation.equals(messageSend.selector, methDecl.selector) && methScope.environment().methodVerifier().doesMethodOverride(methDecl.binding, messageSend.binding.original())) {
                            superRef = true;
                        }
                    }
                } else if (this.seeReferences[i] instanceof JavadocAllocationExpression) {
                    MethodBinding superConstructor;
                    ReferenceBinding allocType;
                    JavadocAllocationExpression allocationExpr = (JavadocAllocationExpression)this.seeReferences[i];
                    if (allocationExpr.binding != null && allocationExpr.binding.isValidBinding() && (superType = (ReferenceBinding)methDecl.binding.declaringClass.findSuperTypeOriginatingFrom(allocType = (ReferenceBinding)allocationExpr.resolvedType.original())) != null && TypeBinding.notEquals(superType.original(), methDecl.binding.declaringClass) && (superConstructor = methScope.getConstructor((ReferenceBinding)superType, methDecl.binding.parameters, allocationExpr)).isValidBinding() && superConstructor.original() == allocationExpr.binding.original()) {
                        MethodBinding current = methDecl.binding;
                        if (methScope.compilerOptions().sourceLevel >= 0x340000L && current.typeVariables != Binding.NO_TYPE_VARIABLES) {
                            current = current.asRawMethod(methScope.environment());
                        }
                        if (superConstructor.areParametersEqual(current)) {
                            superRef = true;
                        }
                    }
                }
            }
            ++i;
        }
        if (!superRef && methDecl != null && methDecl.annotations != null) {
            int length = methDecl.annotations.length;
            int i2 = 0;
            while (i2 < length && !superRef) {
                superRef = (methDecl.binding.tagBits & 0x2000000000000L) != 0L;
                ++i2;
            }
        }
        boolean bl = reportMissing = methDecl == null || (!overriding || this.inheritedPositions == null) && !superRef && (methDecl.binding.declaringClass == null || !methDecl.binding.declaringClass.isLocalType());
        if (!overriding && this.inheritedPositions != null) {
            int length = this.inheritedPositions.length;
            int i3 = 0;
            while (i3 < length) {
                int start = (int)(this.inheritedPositions[i3] >>> 32);
                int end = (int)this.inheritedPositions[i3];
                methScope.problemReporter().javadocUnexpectedTag(start, end);
                ++i3;
            }
        }
        CompilerOptions compilerOptions = methScope.compilerOptions();
        this.resolveParamTags(methScope, reportMissing, compilerOptions.reportUnusedParameterIncludeDocCommentReference);
        this.resolveTypeParameterTags(methScope, reportMissing && compilerOptions.reportMissingJavadocTagsMethodTypeParameters);
        if (this.returnStatement == null) {
            if (reportMissing && methDecl != null && methDecl.isMethod()) {
                MethodDeclaration meth = (MethodDeclaration)methDecl;
                if (meth.binding.returnType != TypeBinding.VOID) {
                    methScope.problemReporter().javadocMissingReturnTag(meth.returnType.sourceStart, meth.returnType.sourceEnd, methDecl.binding.modifiers);
                }
            }
        } else {
            this.returnStatement.resolve(methScope);
        }
        this.resolveThrowsTags(methScope, reportMissing);
        boolean bl2 = source15 = compilerOptions.sourceLevel >= 0x310000L;
        if (!source15 && methDecl != null && this.valuePositions != -1L) {
            methScope.problemReporter().javadocUnexpectedTag((int)(this.valuePositions >>> 32), (int)this.valuePositions);
        }
        int length = this.invalidParameters == null ? 0 : this.invalidParameters.length;
        int i4 = 0;
        while (i4 < length) {
            this.invalidParameters[i4].resolve(methScope, false, false);
            ++i4;
        }
    }

    private void resolveReference(Expression reference, Scope scope) {
        ReferenceBinding resolvedType;
        int problemCount = scope.referenceContext().compilationResult().problemCount;
        switch (scope.kind) {
            case 2: {
                reference.resolveType((MethodScope)scope);
                break;
            }
            case 3: {
                reference.resolveType((ClassScope)scope);
            }
        }
        boolean hasProblems = scope.referenceContext().compilationResult().problemCount > problemCount;
        boolean source15 = scope.compilerOptions().sourceLevel >= 0x310000L;
        int scopeModifiers = -1;
        if (reference instanceof JavadocFieldReference) {
            ReferenceBinding resolvedType2;
            JavadocFieldReference fieldRef = (JavadocFieldReference)reference;
            if (fieldRef.methodBinding != null) {
                if (fieldRef.tagValue == 10) {
                    if (scopeModifiers == -1) {
                        scopeModifiers = scope.getDeclarationModifiers();
                    }
                    scope.problemReporter().javadocInvalidValueReference(fieldRef.sourceStart, fieldRef.sourceEnd, scopeModifiers);
                } else if (fieldRef.actualReceiverType != null) {
                    if (scope.kind != 5 && scope.enclosingSourceType().isCompatibleWith(fieldRef.actualReceiverType)) {
                        fieldRef.bits |= 0x4000;
                    }
                    fieldRef.methodBinding = CharOperation.equals((resolvedType2 = (ReferenceBinding)fieldRef.actualReceiverType).sourceName(), fieldRef.token) ? scope.getConstructor(resolvedType2, Binding.NO_TYPES, fieldRef) : scope.findMethod(resolvedType2, fieldRef.token, Binding.NO_TYPES, fieldRef, false);
                }
            } else if (source15 && fieldRef.binding != null && fieldRef.binding.isValidBinding() && fieldRef.tagValue == 10 && !fieldRef.binding.isStatic()) {
                if (scopeModifiers == -1) {
                    scopeModifiers = scope.getDeclarationModifiers();
                }
                scope.problemReporter().javadocInvalidValueReference(fieldRef.sourceStart, fieldRef.sourceEnd, scopeModifiers);
            }
            if (!hasProblems && fieldRef.binding != null && fieldRef.binding.isValidBinding() && fieldRef.actualReceiverType instanceof ReferenceBinding) {
                resolvedType2 = (ReferenceBinding)fieldRef.actualReceiverType;
                this.verifyTypeReference(fieldRef, fieldRef.receiver, scope, source15, resolvedType2, fieldRef.binding.modifiers);
            }
            return;
        }
        if (!hasProblems && (reference instanceof JavadocSingleTypeReference || reference instanceof JavadocQualifiedTypeReference) && reference.resolvedType instanceof ReferenceBinding) {
            ReferenceBinding resolvedType3 = (ReferenceBinding)reference.resolvedType;
            this.verifyTypeReference(reference, reference, scope, source15, resolvedType3, resolvedType3.modifiers);
        }
        if (!hasProblems && reference instanceof JavadocModuleReference) {
            TypeReference tRef;
            ModuleBinding mType;
            JavadocModuleReference ref = (JavadocModuleReference)reference;
            ref.resolve(scope);
            ModuleReference mRef = ref.getModuleReference();
            if (mRef != null && (mType = mRef.resolve(scope)) != null && this.verifyModuleReference(reference, reference, scope, source15, mType, mType.modifiers) && ((tRef = ref.getTypeReference()) instanceof JavadocSingleTypeReference || tRef instanceof JavadocQualifiedTypeReference) && tRef.resolvedType instanceof ReferenceBinding) {
                ReferenceBinding resolvedType4 = (ReferenceBinding)tRef.resolvedType;
                this.verifyTypeReference(reference, reference, scope, source15, resolvedType4, resolvedType4.modifiers);
            }
        }
        if (reference instanceof JavadocMessageSend) {
            JavadocMessageSend msgSend = (JavadocMessageSend)reference;
            if (source15 && msgSend.tagValue == 10) {
                if (scopeModifiers == -1) {
                    scopeModifiers = scope.getDeclarationModifiers();
                }
                scope.problemReporter().javadocInvalidValueReference(msgSend.sourceStart, msgSend.sourceEnd, scopeModifiers);
            }
            if (!hasProblems && msgSend.binding != null && msgSend.binding.isValidBinding() && msgSend.actualReceiverType instanceof ReferenceBinding) {
                resolvedType = (ReferenceBinding)msgSend.actualReceiverType;
                this.verifyTypeReference(msgSend, msgSend.receiver, scope, source15, resolvedType, msgSend.binding.modifiers);
            }
        } else if (reference instanceof JavadocAllocationExpression) {
            JavadocAllocationExpression alloc = (JavadocAllocationExpression)reference;
            if (source15 && alloc.tagValue == 10) {
                if (scopeModifiers == -1) {
                    scopeModifiers = scope.getDeclarationModifiers();
                }
                scope.problemReporter().javadocInvalidValueReference(alloc.sourceStart, alloc.sourceEnd, scopeModifiers);
            }
            if (!hasProblems && alloc.binding != null && alloc.binding.isValidBinding() && alloc.resolvedType instanceof ReferenceBinding) {
                resolvedType = (ReferenceBinding)alloc.resolvedType;
                this.verifyTypeReference(alloc, alloc.type, scope, source15, resolvedType, alloc.binding.modifiers);
            }
        } else if (reference instanceof JavadocSingleTypeReference && reference.resolvedType != null && reference.resolvedType.isTypeVariable()) {
            scope.problemReporter().javadocInvalidReference(reference.sourceStart, reference.sourceEnd);
        }
    }

    private void resolveParamTags(MethodScope scope, boolean reportMissing, boolean considerParamRefAsUsage) {
        block14: {
            int j;
            boolean found;
            int argumentsSize;
            int paramTagsSize;
            AbstractMethodDeclaration methodDecl;
            block13: {
                methodDecl = scope.referenceMethod();
                int n = paramTagsSize = this.paramReferences == null ? 0 : this.paramReferences.length;
                if (methodDecl == null) {
                    int i = 0;
                    while (i < paramTagsSize) {
                        JavadocSingleNameReference param = this.paramReferences[i];
                        scope.problemReporter().javadocUnexpectedTag(param.tagSourceStart, param.tagSourceEnd);
                        ++i;
                    }
                    return;
                }
                int n2 = argumentsSize = methodDecl.arguments == null ? 0 : methodDecl.arguments.length;
                if (paramTagsSize != 0) break block13;
                if (!reportMissing) break block14;
                int i = 0;
                while (i < argumentsSize) {
                    Argument arg = methodDecl.arguments[i];
                    scope.problemReporter().javadocMissingParamTag(arg.name, arg.sourceStart, arg.sourceEnd, methodDecl.binding.modifiers);
                    ++i;
                }
                break block14;
            }
            LocalVariableBinding[] bindings = new LocalVariableBinding[paramTagsSize];
            int maxBindings = 0;
            int i = 0;
            while (i < paramTagsSize) {
                JavadocSingleNameReference param = this.paramReferences[i];
                param.resolve(scope, true, considerParamRefAsUsage);
                if (param.binding != null && param.binding.isValidBinding()) {
                    found = false;
                    j = 0;
                    while (j < maxBindings && !found) {
                        if (bindings[j] == param.binding) {
                            scope.problemReporter().javadocDuplicatedParamTag(param.token, param.sourceStart, param.sourceEnd, methodDecl.binding.modifiers);
                            found = true;
                        }
                        ++j;
                    }
                    if (!found) {
                        bindings[maxBindings++] = (LocalVariableBinding)param.binding;
                    }
                }
                ++i;
            }
            if (reportMissing) {
                i = 0;
                while (i < argumentsSize) {
                    Argument arg = methodDecl.arguments[i];
                    found = false;
                    j = 0;
                    while (j < maxBindings) {
                        LocalVariableBinding binding = bindings[j];
                        if (arg.binding == binding) {
                            found = true;
                            break;
                        }
                        ++j;
                    }
                    if (!found) {
                        scope.problemReporter().javadocMissingParamTag(arg.name, arg.sourceStart, arg.sourceEnd, methodDecl.binding.modifiers);
                    }
                    ++i;
                }
            }
        }
    }

    private void resolveUsesTags(BlockScope scope, boolean reportMissing) {
        block16: {
            int j;
            boolean found;
            int usesSize;
            int usesTagsSize;
            ModuleDeclaration moduleDecl;
            block15: {
                moduleDecl = (ModuleDeclaration)scope.referenceContext();
                int n = usesTagsSize = this.usesReferences == null ? 0 : this.usesReferences.length;
                if (moduleDecl == null) {
                    int i = 0;
                    while (i < usesTagsSize) {
                        IJavadocTypeReference uses = this.usesReferences[i];
                        scope.problemReporter().javadocUnexpectedTag(uses.getTagSourceStart(), uses.getTagSourceEnd());
                        ++i;
                    }
                    return;
                }
                usesSize = moduleDecl.usesCount;
                if (usesTagsSize != 0) break block15;
                if (!reportMissing) break block16;
                int i = 0;
                while (i < usesSize) {
                    UsesStatement uses = moduleDecl.uses[i];
                    scope.problemReporter().javadocMissingUsesTag(uses.serviceInterface, uses.sourceStart, uses.sourceEnd, moduleDecl.binding.modifiers);
                    ++i;
                }
                break block16;
            }
            TypeBinding[] bindings = new TypeBinding[usesTagsSize];
            int maxBindings = 0;
            int i = 0;
            while (i < usesTagsSize) {
                TypeReference usesRef = (TypeReference)((Object)this.usesReferences[i]);
                try {
                    usesRef.resolve(scope);
                    if (usesRef.resolvedType != null && usesRef.resolvedType.isValidBinding()) {
                        found = false;
                        j = 0;
                        while (j < maxBindings && !found) {
                            if (bindings[j].equals(usesRef.resolvedType)) {
                                scope.problemReporter().javadocDuplicatedUsesTag(usesRef.sourceStart, usesRef.sourceEnd);
                                found = true;
                            }
                            ++j;
                        }
                        if (!found) {
                            bindings[maxBindings++] = usesRef.resolvedType;
                        }
                    }
                }
                catch (Exception exception) {
                    scope.problemReporter().javadocInvalidUsesClass(usesRef.sourceStart, usesRef.sourceEnd);
                }
                ++i;
            }
            if (reportMissing) {
                i = 0;
                while (i < usesSize) {
                    UsesStatement uses = moduleDecl.uses[i];
                    found = false;
                    j = 0;
                    while (j < maxBindings && !found) {
                        TypeBinding binding = bindings[j];
                        if (uses.serviceInterface.getTypeBinding(scope).equals(binding)) {
                            found = true;
                        }
                        ++j;
                    }
                    if (!found) {
                        scope.problemReporter().javadocMissingUsesTag(uses.serviceInterface, uses.sourceStart, uses.sourceEnd, moduleDecl.binding.modifiers);
                    }
                    ++i;
                }
            }
        }
    }

    private void resolveProvidesTags(BlockScope scope, boolean reportMissing) {
        block16: {
            int j;
            boolean found;
            int providesSize;
            int providesTagsSize;
            ModuleDeclaration moduleDecl;
            block15: {
                moduleDecl = (ModuleDeclaration)scope.referenceContext();
                int n = providesTagsSize = this.providesReferences == null ? 0 : this.providesReferences.length;
                if (moduleDecl == null) {
                    int i = 0;
                    while (i < providesTagsSize) {
                        IJavadocTypeReference provides = this.providesReferences[i];
                        scope.problemReporter().javadocUnexpectedTag(provides.getTagSourceStart(), provides.getTagSourceEnd());
                        ++i;
                    }
                    return;
                }
                providesSize = moduleDecl.servicesCount;
                if (providesTagsSize != 0) break block15;
                if (!reportMissing) break block16;
                int i = 0;
                while (i < providesSize) {
                    ProvidesStatement provides = moduleDecl.services[i];
                    scope.problemReporter().javadocMissingProvidesTag(provides.serviceInterface, provides.sourceStart, provides.sourceEnd, moduleDecl.binding.modifiers);
                    ++i;
                }
                break block16;
            }
            TypeBinding[] bindings = new TypeBinding[providesTagsSize];
            int maxBindings = 0;
            int i = 0;
            while (i < providesTagsSize) {
                TypeReference providesRef = (TypeReference)((Object)this.providesReferences[i]);
                try {
                    providesRef.resolve(scope);
                    if (providesRef.resolvedType != null && providesRef.resolvedType.isValidBinding()) {
                        found = false;
                        j = 0;
                        while (j < maxBindings && !found) {
                            if (bindings[j].equals(providesRef.resolvedType)) {
                                scope.problemReporter().javadocDuplicatedProvidesTag(providesRef.sourceStart, providesRef.sourceEnd);
                                found = true;
                            }
                            ++j;
                        }
                        if (!found) {
                            bindings[maxBindings++] = providesRef.resolvedType;
                        }
                    }
                }
                catch (Exception exception) {
                    scope.problemReporter().javadocInvalidProvidesClass(providesRef.sourceStart, providesRef.sourceEnd);
                }
                ++i;
            }
            if (reportMissing) {
                i = 0;
                while (i < providesSize) {
                    ProvidesStatement provides = moduleDecl.services[i];
                    found = false;
                    j = 0;
                    while (j < maxBindings && !found) {
                        TypeBinding binding = bindings[j];
                        if (provides.serviceInterface.getTypeBinding(scope).equals(binding)) {
                            found = true;
                        }
                        ++j;
                    }
                    if (!found) {
                        scope.problemReporter().javadocMissingProvidesTag(provides.serviceInterface, provides.sourceStart, provides.sourceEnd, moduleDecl.binding.modifiers);
                    }
                    ++i;
                }
            }
        }
    }

    private void resolveTypeParameterTags(Scope scope, boolean reportMissing) {
        block42: {
            int typeParametersLength;
            int j;
            boolean duplicate;
            int i;
            int modifiers;
            TypeVariableBinding[] typeVariables;
            TypeParameter[] parameters;
            int paramTypeParamLength;
            block43: {
                paramTypeParamLength = this.paramTypeParameters == null ? 0 : this.paramTypeParameters.length;
                int paramReferencesLength = this.paramReferences == null ? 0 : this.paramReferences.length;
                parameters = null;
                typeVariables = null;
                RecordComponent[] recordParameters = null;
                modifiers = -1;
                switch (scope.kind) {
                    case 2: {
                        AbstractMethodDeclaration methodDeclaration = ((MethodScope)scope).referenceMethod();
                        if (methodDeclaration == null) {
                            int i2 = 0;
                            while (i2 < paramTypeParamLength) {
                                JavadocSingleTypeReference param = this.paramTypeParameters[i2];
                                scope.problemReporter().javadocUnexpectedTag(param.tagSourceStart, param.tagSourceEnd);
                                ++i2;
                            }
                            return;
                        }
                        parameters = methodDeclaration.typeParameters();
                        typeVariables = methodDeclaration.binding.typeVariables;
                        modifiers = methodDeclaration.binding.modifiers;
                        break;
                    }
                    case 3: {
                        TypeDeclaration typeDeclaration = ((ClassScope)scope).referenceContext;
                        parameters = typeDeclaration.typeParameters;
                        typeVariables = typeDeclaration.binding.typeVariables;
                        modifiers = typeDeclaration.binding.modifiers;
                        recordParameters = typeDeclaration.recordComponents;
                    }
                }
                if (!(recordParameters != null && recordParameters.length != 0 || typeVariables != null && typeVariables.length != 0)) {
                    int i3 = 0;
                    while (i3 < paramTypeParamLength) {
                        JavadocSingleTypeReference param = this.paramTypeParameters[i3];
                        scope.problemReporter().javadocUnexpectedTag(param.tagSourceStart, param.tagSourceEnd);
                        ++i3;
                    }
                    return;
                }
                if (recordParameters != null) {
                    reportMissing = reportMissing && scope.compilerOptions().sourceLevel >= 0x310000L;
                    int recordParametersLength = recordParameters.length;
                    String[] argNames = new String[paramReferencesLength];
                    if (paramReferencesLength == 0) {
                        if (reportMissing) {
                            i = 0;
                            int l = recordParametersLength;
                            while (i < l) {
                                scope.problemReporter().javadocMissingParamTag(recordParameters[i].name, recordParameters[i].sourceStart, recordParameters[i].sourceEnd, modifiers);
                                ++i;
                            }
                        }
                    } else {
                        String paramName;
                        JavadocSingleNameReference param;
                        i = 0;
                        while (i < paramReferencesLength) {
                            param = this.paramReferences[i];
                            paramName = new String(param.getName()[0]);
                            duplicate = false;
                            int j2 = 0;
                            while (j2 < i && !duplicate) {
                                if (paramName.equals(argNames[j2])) {
                                    scope.problemReporter().javadocDuplicatedParamTag(param.token, param.sourceStart, param.sourceEnd, modifiers);
                                    duplicate = true;
                                }
                                ++j2;
                            }
                            if (!duplicate) {
                                argNames[i] = paramName;
                            }
                            ++i;
                        }
                        if (reportMissing) {
                            i = 0;
                            while (i < recordParameters.length) {
                                RecordComponent component = recordParameters[i];
                                boolean found = false;
                                j = 0;
                                while (j < paramReferencesLength && !found) {
                                    JavadocSingleNameReference param2 = this.paramReferences[j];
                                    String paramName2 = new String(param2.getName()[0]);
                                    if (paramName2.equals(new String(component.name))) {
                                        found = true;
                                    }
                                    ++j;
                                }
                                if (!found) {
                                    scope.problemReporter().javadocMissingParamTag(component.name, component.sourceStart, component.sourceEnd, modifiers);
                                }
                                ++i;
                            }
                        }
                        i = 0;
                        while (i < paramReferencesLength) {
                            param = this.paramReferences[i];
                            paramName = new String(param.getName()[0]);
                            boolean found = false;
                            int j3 = 0;
                            while (j3 < recordParameters.length) {
                                RecordComponent component = recordParameters[j3];
                                if (paramName.equals(new String(component.name))) {
                                    found = true;
                                }
                                ++j3;
                            }
                            if (!found) {
                                scope.problemReporter().javadocInvalidParamTagName(param.sourceStart, param.sourceEnd);
                            }
                            ++i;
                        }
                    }
                }
                if (parameters == null) break block42;
                reportMissing = reportMissing && scope.compilerOptions().sourceLevel >= 0x310000L;
                typeParametersLength = parameters.length;
                if (paramTypeParamLength != 0) break block43;
                if (!reportMissing) break block42;
                int i4 = 0;
                int l = typeParametersLength;
                while (i4 < l) {
                    scope.problemReporter().javadocMissingParamTag(parameters[i4].name, parameters[i4].sourceStart, parameters[i4].sourceEnd, modifiers);
                    ++i4;
                }
                break block42;
            }
            if (typeVariables.length == typeParametersLength) {
                TypeVariableBinding[] bindings = new TypeVariableBinding[paramTypeParamLength];
                i = 0;
                while (i < paramTypeParamLength) {
                    JavadocSingleTypeReference param = this.paramTypeParameters[i];
                    TypeBinding paramBindind = param.internalResolveType(scope, 0);
                    if (paramBindind != null && paramBindind.isValidBinding()) {
                        if (paramBindind.isTypeVariable()) {
                            if (scope.compilerOptions().reportUnusedParameterIncludeDocCommentReference) {
                                TypeVariableBinding typeVariableBinding = (TypeVariableBinding)paramBindind;
                                typeVariableBinding.modifiers |= 0x8000000;
                            }
                            duplicate = false;
                            int j4 = 0;
                            while (j4 < i && !duplicate) {
                                if (TypeBinding.equalsEquals(bindings[j4], param.resolvedType)) {
                                    scope.problemReporter().javadocDuplicatedParamTag(param.token, param.sourceStart, param.sourceEnd, modifiers);
                                    duplicate = true;
                                }
                                ++j4;
                            }
                            if (!duplicate) {
                                bindings[i] = (TypeVariableBinding)param.resolvedType;
                            }
                        } else {
                            scope.problemReporter().javadocUndeclaredParamTagName(param.token, param.sourceStart, param.sourceEnd, modifiers);
                        }
                    }
                    ++i;
                }
                i = 0;
                while (i < typeParametersLength) {
                    TypeParameter parameter = parameters[i];
                    boolean found = false;
                    j = 0;
                    while (j < paramTypeParamLength && !found) {
                        if (TypeBinding.equalsEquals(parameter.binding, bindings[j])) {
                            found = true;
                            bindings[j] = null;
                        }
                        ++j;
                    }
                    if (!found && reportMissing) {
                        scope.problemReporter().javadocMissingParamTag(parameter.name, parameter.sourceStart, parameter.sourceEnd, modifiers);
                    }
                    ++i;
                }
                i = 0;
                while (i < paramTypeParamLength) {
                    if (bindings[i] != null) {
                        JavadocSingleTypeReference param = this.paramTypeParameters[i];
                        scope.problemReporter().javadocUndeclaredParamTagName(param.token, param.sourceStart, param.sourceEnd, modifiers);
                    }
                    ++i;
                }
            }
        }
    }

    private void resolveThrowsTags(MethodScope methScope, boolean reportMissing) {
        block24: {
            int j;
            TypeReference typeRef;
            int thrownExceptionLength;
            int boundExceptionLength;
            int throwsTagsLength;
            AbstractMethodDeclaration md;
            block23: {
                md = methScope.referenceMethod();
                int n = throwsTagsLength = this.exceptionReferences == null ? 0 : this.exceptionReferences.length;
                if (md == null) {
                    int i = 0;
                    while (i < throwsTagsLength) {
                        TypeReference typeRef2 = this.exceptionReferences[i];
                        int start = typeRef2.sourceStart;
                        int end = typeRef2.sourceEnd;
                        if (typeRef2 instanceof JavadocQualifiedTypeReference) {
                            start = ((JavadocQualifiedTypeReference)typeRef2).tagSourceStart;
                            end = ((JavadocQualifiedTypeReference)typeRef2).tagSourceEnd;
                        } else if (typeRef2 instanceof JavadocSingleTypeReference) {
                            start = ((JavadocSingleTypeReference)typeRef2).tagSourceStart;
                            end = ((JavadocSingleTypeReference)typeRef2).tagSourceEnd;
                        }
                        methScope.problemReporter().javadocUnexpectedTag(start, end);
                        ++i;
                    }
                    return;
                }
                boundExceptionLength = md.binding == null ? 0 : md.binding.thrownExceptions.length;
                int n2 = thrownExceptionLength = md.thrownExceptions == null ? 0 : md.thrownExceptions.length;
                if (throwsTagsLength != 0) break block23;
                if (!reportMissing) break block24;
                int i = 0;
                while (i < boundExceptionLength) {
                    ReferenceBinding exceptionBinding = md.binding.thrownExceptions[i];
                    if (exceptionBinding != null && exceptionBinding.isValidBinding()) {
                        int j2 = i;
                        while (j2 < thrownExceptionLength && TypeBinding.notEquals(exceptionBinding, md.thrownExceptions[j2].resolvedType)) {
                            ++j2;
                        }
                        if (j2 < thrownExceptionLength) {
                            methScope.problemReporter().javadocMissingThrowsTag(md.thrownExceptions[j2], md.binding.modifiers);
                        }
                    }
                    ++i;
                }
                break block24;
            }
            int maxRef = 0;
            TypeReference[] typeReferences = new TypeReference[throwsTagsLength];
            int i = 0;
            while (i < throwsTagsLength) {
                typeRef = this.exceptionReferences[i];
                typeRef.resolve(methScope);
                TypeBinding typeBinding = typeRef.resolvedType;
                if (typeBinding != null && typeBinding.isValidBinding() && typeBinding.isClass()) {
                    typeReferences[maxRef++] = typeRef;
                }
                ++i;
            }
            i = 0;
            while (i < boundExceptionLength) {
                ReferenceBinding exceptionBinding = md.binding.thrownExceptions[i];
                if (exceptionBinding != null) {
                    exceptionBinding = (ReferenceBinding)exceptionBinding.erasure();
                }
                boolean found = false;
                j = 0;
                while (j < maxRef && !found) {
                    TypeBinding typeBinding;
                    if (typeReferences[j] != null && TypeBinding.equalsEquals(exceptionBinding, typeBinding = typeReferences[j].resolvedType)) {
                        found = true;
                        typeReferences[j] = null;
                    }
                    ++j;
                }
                if (!found && reportMissing && exceptionBinding != null && exceptionBinding.isValidBinding()) {
                    int k = i;
                    while (k < thrownExceptionLength && TypeBinding.notEquals(exceptionBinding, md.thrownExceptions[k].resolvedType)) {
                        ++k;
                    }
                    if (k < thrownExceptionLength) {
                        methScope.problemReporter().javadocMissingThrowsTag(md.thrownExceptions[k], md.binding.modifiers);
                    }
                }
                ++i;
            }
            i = 0;
            while (i < maxRef) {
                typeRef = typeReferences[i];
                if (typeRef != null) {
                    boolean compatible = false;
                    j = 0;
                    while (j < thrownExceptionLength && !compatible) {
                        TypeBinding exceptionBinding = md.thrownExceptions[j].resolvedType;
                        if (exceptionBinding != null) {
                            compatible = typeRef.resolvedType.isCompatibleWith(exceptionBinding);
                        }
                        ++j;
                    }
                    if (!compatible && !typeRef.resolvedType.isUncheckedException(false)) {
                        methScope.problemReporter().javadocInvalidThrowsClassName(typeRef, md.binding.modifiers);
                    }
                }
                ++i;
            }
        }
    }

    private void verifyTypeReference(Expression reference, Expression typeReference, Scope scope, boolean source15, ReferenceBinding resolvedType, int modifiers) {
        if (resolvedType.isValidBinding()) {
            int scopeModifiers = -1;
            if (!this.canBeSeen(scope.problemReporter().options.reportInvalidJavadocTagsVisibility, modifiers)) {
                scope.problemReporter().javadocHiddenReference(typeReference.sourceStart, reference.sourceEnd, scope, modifiers);
                return;
            }
            if (reference != typeReference && !this.canBeSeen(scope.problemReporter().options.reportInvalidJavadocTagsVisibility, resolvedType.modifiers)) {
                scope.problemReporter().javadocHiddenReference(typeReference.sourceStart, typeReference.sourceEnd, scope, resolvedType.modifiers);
                return;
            }
            if (resolvedType.isMemberType()) {
                ReferenceBinding topLevelType = resolvedType;
                int packageLength = topLevelType.fPackage.compoundName.length;
                int depth = resolvedType.depth();
                int idx = depth + packageLength;
                char[][] computedCompoundName = new char[idx + 1][];
                computedCompoundName[idx] = topLevelType.sourceName;
                while (topLevelType.enclosingType() != null) {
                    topLevelType = topLevelType.enclosingType();
                    computedCompoundName[--idx] = topLevelType.sourceName;
                }
                int i = packageLength;
                while (--i >= 0) {
                    computedCompoundName[--idx] = topLevelType.fPackage.compoundName[i];
                }
                if (scope.kind != 5) {
                    ClassScope topLevelScope = scope.classScope();
                    if (topLevelScope.parent.kind != 4 || !CharOperation.equals(topLevelType.sourceName, topLevelScope.referenceContext.name)) {
                        topLevelScope = topLevelScope.outerMostClassScope();
                        if (typeReference instanceof JavadocSingleTypeReference && (!source15 && depth == 1 || TypeBinding.notEquals(topLevelType, topLevelScope.referenceContext.binding))) {
                            boolean hasValidImport = false;
                            if (source15) {
                                CompilationUnitScope unitScope = topLevelScope.compilationUnitScope();
                                ImportBinding[] imports = unitScope.imports;
                                int length = imports == null ? 0 : imports.length;
                                int i2 = 0;
                                block2: while (i2 < length) {
                                    char[][] compoundName = imports[i2].compoundName;
                                    int compoundNameLength = compoundName.length;
                                    if (imports[i2].onDemand && compoundNameLength == computedCompoundName.length - 1 || compoundNameLength == computedCompoundName.length) {
                                        int j = compoundNameLength;
                                        while (--j >= 0) {
                                            if (!CharOperation.equals(imports[i2].compoundName[j], computedCompoundName[j])) break;
                                            if (j != 0) continue;
                                            hasValidImport = true;
                                            ImportReference importReference = imports[i2].reference;
                                            if (importReference == null) break block2;
                                            importReference.bits |= 2;
                                            break block2;
                                        }
                                    }
                                    ++i2;
                                }
                                if (!hasValidImport) {
                                    if (scopeModifiers == -1) {
                                        scopeModifiers = scope.getDeclarationModifiers();
                                    }
                                    scope.problemReporter().javadocInvalidMemberTypeQualification(typeReference.sourceStart, typeReference.sourceEnd, scopeModifiers);
                                }
                            } else {
                                if (scopeModifiers == -1) {
                                    scopeModifiers = scope.getDeclarationModifiers();
                                }
                                scope.problemReporter().javadocInvalidMemberTypeQualification(typeReference.sourceStart, typeReference.sourceEnd, scopeModifiers);
                                return;
                            }
                        }
                    }
                    if (typeReference instanceof JavadocQualifiedTypeReference && !scope.isDefinedInSameUnit(resolvedType)) {
                        char[][] typeRefName = ((JavadocQualifiedTypeReference)typeReference).getTypeName();
                        int skipLength = 0;
                        if (topLevelScope.getCurrentPackage() == resolvedType.getPackage() && typeRefName.length < computedCompoundName.length) {
                            skipLength = resolvedType.fPackage.compoundName.length;
                        }
                        boolean valid = true;
                        if (typeRefName.length == computedCompoundName.length - skipLength) {
                            int i3 = 0;
                            while (i3 < typeRefName.length) {
                                if (!CharOperation.equals(typeRefName[i3], computedCompoundName[i3 + skipLength])) {
                                    valid = false;
                                    break;
                                }
                                ++i3;
                            }
                        } else {
                            valid = false;
                        }
                        if (!valid) {
                            if (scopeModifiers == -1) {
                                scopeModifiers = scope.getDeclarationModifiers();
                            }
                            scope.problemReporter().javadocInvalidMemberTypeQualification(typeReference.sourceStart, typeReference.sourceEnd, scopeModifiers);
                            return;
                        }
                    }
                }
            }
            if (scope.referenceCompilationUnit().isPackageInfo() && typeReference instanceof JavadocSingleTypeReference && resolvedType.fPackage.compoundName.length > 0) {
                scope.problemReporter().javadocInvalidReference(typeReference.sourceStart, typeReference.sourceEnd);
                return;
            }
        }
    }

    private boolean verifyModuleReference(Expression reference, Expression typeReference, Scope scope, boolean source15, ModuleBinding moduleType, int modifiers) {
        boolean bindingFound = false;
        if (moduleType != null && moduleType.isValidBinding()) {
            int scopeModifiers = -1;
            ModuleBinding mBinding = scope.module();
            if (mBinding == null) {
                scope.problemReporter().javadocInvalidModuleQualification(typeReference.sourceStart, typeReference.sourceEnd, scopeModifiers);
                return bindingFound;
            }
            if (mBinding.equals(moduleType)) {
                bindingFound = true;
            } else {
                ModuleBinding[] bindings;
                ModuleBinding[] moduleBindingArray = bindings = mBinding.getAllRequiredModules();
                int n = bindings.length;
                int n2 = 0;
                while (n2 < n) {
                    ModuleBinding binding = moduleBindingArray[n2];
                    if (moduleType.equals(binding)) {
                        bindingFound = true;
                        break;
                    }
                    ++n2;
                }
            }
            if (!bindingFound && !this.canBeSeen(scope.problemReporter().options.reportInvalidJavadocTagsVisibility, moduleType.modifiers)) {
                scope.problemReporter().javadocHiddenReference(typeReference.sourceStart, typeReference.sourceEnd, scope, moduleType.modifiers);
                return bindingFound;
            }
        }
        return bindingFound;
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
        if (visitor.visit(this, scope)) {
            int length;
            int i;
            if (this.paramReferences != null) {
                i = 0;
                length = this.paramReferences.length;
                while (i < length) {
                    this.paramReferences[i].traverse(visitor, scope);
                    ++i;
                }
            }
            if (this.paramTypeParameters != null) {
                i = 0;
                length = this.paramTypeParameters.length;
                while (i < length) {
                    this.paramTypeParameters[i].traverse(visitor, scope);
                    ++i;
                }
            }
            if (this.returnStatement != null) {
                this.returnStatement.traverse(visitor, scope);
            }
            if (this.exceptionReferences != null) {
                i = 0;
                length = this.exceptionReferences.length;
                while (i < length) {
                    this.exceptionReferences[i].traverse(visitor, scope);
                    ++i;
                }
            }
            if (this.seeReferences != null) {
                i = 0;
                length = this.seeReferences.length;
                while (i < length) {
                    this.seeReferences[i].traverse(visitor, scope);
                    ++i;
                }
            }
        }
        visitor.endVisit(this, scope);
    }

    public void traverse(ASTVisitor visitor, ClassScope scope) {
        if (visitor.visit(this, scope)) {
            int length;
            int i;
            if (this.paramReferences != null) {
                i = 0;
                length = this.paramReferences.length;
                while (i < length) {
                    this.paramReferences[i].traverse(visitor, scope);
                    ++i;
                }
            }
            if (this.paramTypeParameters != null) {
                i = 0;
                length = this.paramTypeParameters.length;
                while (i < length) {
                    this.paramTypeParameters[i].traverse(visitor, scope);
                    ++i;
                }
            }
            if (this.returnStatement != null) {
                this.returnStatement.traverse(visitor, scope);
            }
            if (this.exceptionReferences != null) {
                i = 0;
                length = this.exceptionReferences.length;
                while (i < length) {
                    this.exceptionReferences[i].traverse(visitor, scope);
                    ++i;
                }
            }
            if (this.seeReferences != null) {
                i = 0;
                length = this.seeReferences.length;
                while (i < length) {
                    this.seeReferences[i].traverse(visitor, scope);
                    ++i;
                }
            }
        }
        visitor.endVisit(this, scope);
    }
}

