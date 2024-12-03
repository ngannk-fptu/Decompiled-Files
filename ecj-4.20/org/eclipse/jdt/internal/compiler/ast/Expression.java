/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import java.util.ArrayList;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.ExpressionContext;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.NameReference;
import org.eclipse.jdt.internal.compiler.ast.PrefixExpression;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedThisReference;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.SwitchExpression;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
import org.eclipse.jdt.internal.compiler.problem.ShouldNotImplement;
import org.eclipse.jdt.internal.compiler.util.Messages;

public abstract class Expression
extends Statement {
    public Constant constant;
    public int statementEnd = -1;
    public int implicitConversion;
    public TypeBinding resolvedType;
    public static Expression[] NO_EXPRESSIONS = new Expression[0];

    public static final boolean isConstantValueRepresentable(Constant constant, int constantTypeID, int targetTypeID) {
        if (targetTypeID == constantTypeID) {
            return true;
        }
        switch (targetTypeID) {
            case 2: {
                switch (constantTypeID) {
                    case 2: {
                        return true;
                    }
                    case 8: {
                        return constant.doubleValue() == (double)constant.charValue();
                    }
                    case 9: {
                        return constant.floatValue() == (float)constant.charValue();
                    }
                    case 10: {
                        return constant.intValue() == constant.charValue();
                    }
                    case 4: {
                        return constant.shortValue() == constant.charValue();
                    }
                    case 3: {
                        return constant.byteValue() == constant.charValue();
                    }
                    case 7: {
                        return constant.longValue() == (long)constant.charValue();
                    }
                }
                return false;
            }
            case 9: {
                switch (constantTypeID) {
                    case 2: {
                        return (float)constant.charValue() == constant.floatValue();
                    }
                    case 8: {
                        return constant.doubleValue() == (double)constant.floatValue();
                    }
                    case 9: {
                        return true;
                    }
                    case 10: {
                        return (float)constant.intValue() == constant.floatValue();
                    }
                    case 4: {
                        return (float)constant.shortValue() == constant.floatValue();
                    }
                    case 3: {
                        return (float)constant.byteValue() == constant.floatValue();
                    }
                    case 7: {
                        return (float)constant.longValue() == constant.floatValue();
                    }
                }
                return false;
            }
            case 8: {
                switch (constantTypeID) {
                    case 2: {
                        return (double)constant.charValue() == constant.doubleValue();
                    }
                    case 8: {
                        return true;
                    }
                    case 9: {
                        return (double)constant.floatValue() == constant.doubleValue();
                    }
                    case 10: {
                        return (double)constant.intValue() == constant.doubleValue();
                    }
                    case 4: {
                        return (double)constant.shortValue() == constant.doubleValue();
                    }
                    case 3: {
                        return (double)constant.byteValue() == constant.doubleValue();
                    }
                    case 7: {
                        return (double)constant.longValue() == constant.doubleValue();
                    }
                }
                return false;
            }
            case 3: {
                switch (constantTypeID) {
                    case 2: {
                        return constant.charValue() == constant.byteValue();
                    }
                    case 8: {
                        return constant.doubleValue() == (double)constant.byteValue();
                    }
                    case 9: {
                        return constant.floatValue() == (float)constant.byteValue();
                    }
                    case 10: {
                        return constant.intValue() == constant.byteValue();
                    }
                    case 4: {
                        return constant.shortValue() == constant.byteValue();
                    }
                    case 3: {
                        return true;
                    }
                    case 7: {
                        return constant.longValue() == (long)constant.byteValue();
                    }
                }
                return false;
            }
            case 4: {
                switch (constantTypeID) {
                    case 2: {
                        return constant.charValue() == constant.shortValue();
                    }
                    case 8: {
                        return constant.doubleValue() == (double)constant.shortValue();
                    }
                    case 9: {
                        return constant.floatValue() == (float)constant.shortValue();
                    }
                    case 10: {
                        return constant.intValue() == constant.shortValue();
                    }
                    case 4: {
                        return true;
                    }
                    case 3: {
                        return constant.byteValue() == constant.shortValue();
                    }
                    case 7: {
                        return constant.longValue() == (long)constant.shortValue();
                    }
                }
                return false;
            }
            case 10: {
                switch (constantTypeID) {
                    case 2: {
                        return constant.charValue() == constant.intValue();
                    }
                    case 8: {
                        return constant.doubleValue() == (double)constant.intValue();
                    }
                    case 9: {
                        return constant.floatValue() == (float)constant.intValue();
                    }
                    case 10: {
                        return true;
                    }
                    case 4: {
                        return constant.shortValue() == constant.intValue();
                    }
                    case 3: {
                        return constant.byteValue() == constant.intValue();
                    }
                    case 7: {
                        return constant.longValue() == (long)constant.intValue();
                    }
                }
                return false;
            }
            case 7: {
                switch (constantTypeID) {
                    case 2: {
                        return (long)constant.charValue() == constant.longValue();
                    }
                    case 8: {
                        return constant.doubleValue() == (double)constant.longValue();
                    }
                    case 9: {
                        return constant.floatValue() == (float)constant.longValue();
                    }
                    case 10: {
                        return (long)constant.intValue() == constant.longValue();
                    }
                    case 4: {
                        return (long)constant.shortValue() == constant.longValue();
                    }
                    case 3: {
                        return (long)constant.byteValue() == constant.longValue();
                    }
                    case 7: {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        return flowInfo;
    }

    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo, boolean valueRequired) {
        return this.analyseCode(currentScope, flowContext, flowInfo);
    }

    protected void updateFlowOnBooleanResult(FlowInfo flowInfo, boolean result) {
    }

    public final boolean checkCastTypesCompatibility(Scope scope, TypeBinding castType, TypeBinding expressionType, Expression expression, boolean useAutoBoxing) {
        if (castType == null || expressionType == null) {
            return true;
        }
        boolean use15specifics = scope.compilerOptions().sourceLevel >= 0x310000L;
        boolean use17specifics = scope.compilerOptions().sourceLevel >= 0x330000L;
        useAutoBoxing &= use15specifics;
        if (castType.isBaseType()) {
            if (expressionType.isBaseType()) {
                if (TypeBinding.equalsEquals(expressionType, castType)) {
                    if (expression != null) {
                        this.constant = expression.constant;
                    }
                    this.tagAsUnnecessaryCast(scope, castType);
                    return true;
                }
                boolean necessary = false;
                if (expressionType.isCompatibleWith(castType) || (necessary = BaseTypeBinding.isNarrowing(castType.id, expressionType.id))) {
                    if (expression != null) {
                        expression.implicitConversion = (castType.id << 4) + expressionType.id;
                        if (expression.constant != Constant.NotAConstant) {
                            this.constant = expression.constant.castTo(expression.implicitConversion);
                        }
                    }
                    if (!necessary) {
                        this.tagAsUnnecessaryCast(scope, castType);
                    }
                    return true;
                }
            } else {
                if (useAutoBoxing && use17specifics && castType.isPrimitiveType() && expressionType instanceof ReferenceBinding && !expressionType.isBoxedPrimitiveType() && this.checkCastTypesCompatibility(scope, scope.boxing(castType), expressionType, expression, useAutoBoxing)) {
                    return true;
                }
                if (useAutoBoxing && scope.environment().computeBoxingType(expressionType).isCompatibleWith(castType)) {
                    this.tagAsUnnecessaryCast(scope, castType);
                    return true;
                }
            }
            return false;
        }
        if (useAutoBoxing && expressionType.isBaseType() && scope.environment().computeBoxingType(expressionType).isCompatibleWith(castType)) {
            this.tagAsUnnecessaryCast(scope, castType);
            return true;
        }
        if (castType.isIntersectionType18()) {
            ReferenceBinding[] intersectingTypes = castType.getIntersectingTypes();
            int i = 0;
            int length = intersectingTypes.length;
            while (i < length) {
                if (!this.checkCastTypesCompatibility(scope, intersectingTypes[i], expressionType, expression, useAutoBoxing)) {
                    return false;
                }
                ++i;
            }
            return true;
        }
        switch (expressionType.kind()) {
            case 132: {
                if (expressionType == TypeBinding.NULL) {
                    this.tagAsUnnecessaryCast(scope, castType);
                    return true;
                }
                return false;
            }
            case 68: {
                if (TypeBinding.equalsEquals(castType, expressionType)) {
                    this.tagAsUnnecessaryCast(scope, castType);
                    return true;
                }
                switch (castType.kind()) {
                    case 68: {
                        TypeBinding castElementType = ((ArrayBinding)castType).elementsType();
                        TypeBinding exprElementType = ((ArrayBinding)expressionType).elementsType();
                        if (exprElementType.isBaseType() || castElementType.isBaseType()) {
                            if (TypeBinding.equalsEquals(castElementType, exprElementType)) {
                                this.tagAsNeedCheckCast();
                                return true;
                            }
                            return false;
                        }
                        return this.checkCastTypesCompatibility(scope, castElementType, exprElementType, expression, useAutoBoxing);
                    }
                    case 4100: {
                        TypeBinding match2 = expressionType.findSuperTypeOriginatingFrom(castType);
                        if (match2 == null) {
                            this.checkUnsafeCast(scope, castType, expressionType, null, true);
                        }
                        TypeBinding[] typeBindingArray = ((TypeVariableBinding)castType).allUpperBounds();
                        int n = typeBindingArray.length;
                        int n2 = 0;
                        while (n2 < n) {
                            TypeBinding bound = typeBindingArray[n2];
                            if (!this.checkCastTypesCompatibility(scope, bound, expressionType, expression, useAutoBoxing)) {
                                return false;
                            }
                            ++n2;
                        }
                        return true;
                    }
                }
                switch (castType.id) {
                    case 36: 
                    case 37: {
                        this.tagAsNeedCheckCast();
                        return true;
                    }
                    case 1: {
                        this.tagAsUnnecessaryCast(scope, castType);
                        return true;
                    }
                }
                return false;
            }
            case 4100: {
                TypeBinding match = expressionType.findSuperTypeOriginatingFrom(castType);
                if (match == null) {
                    if (castType instanceof TypeVariableBinding) {
                        TypeBinding[] typeBindingArray = ((TypeVariableBinding)castType).allUpperBounds();
                        int bound = typeBindingArray.length;
                        int match2 = 0;
                        while (match2 < bound) {
                            TypeBinding bound2 = typeBindingArray[match2];
                            if (!this.checkCastTypesCompatibility(scope, bound2, expressionType, expression, useAutoBoxing)) {
                                return false;
                            }
                            ++match2;
                        }
                    } else {
                        TypeBinding[] typeBindingArray = ((TypeVariableBinding)expressionType).allUpperBounds();
                        int bound = typeBindingArray.length;
                        int match2 = 0;
                        while (match2 < bound) {
                            TypeBinding bound3 = typeBindingArray[match2];
                            if (!this.checkCastTypesCompatibility(scope, castType, bound3, expression, useAutoBoxing)) {
                                return false;
                            }
                            ++match2;
                        }
                    }
                }
                return this.checkUnsafeCast(scope, castType, expressionType, match, match == null);
            }
            case 516: 
            case 8196: {
                TypeBinding match = expressionType.findSuperTypeOriginatingFrom(castType);
                if (match != null) {
                    return this.checkUnsafeCast(scope, castType, expressionType, match, false);
                }
                TypeBinding bound = ((WildcardBinding)expressionType).bound;
                if (bound == null) {
                    bound = scope.getJavaLangObject();
                }
                return this.checkCastTypesCompatibility(scope, castType, bound, expression, useAutoBoxing);
            }
            case 32772: {
                ReferenceBinding[] intersectingTypes = expressionType.getIntersectingTypes();
                int i = 0;
                int length = intersectingTypes.length;
                while (i < length) {
                    if (this.checkCastTypesCompatibility(scope, castType, intersectingTypes[i], expression, useAutoBoxing)) {
                        return true;
                    }
                    ++i;
                }
                return false;
            }
        }
        if (expressionType.isInterface()) {
            switch (castType.kind()) {
                case 68: {
                    switch (expressionType.id) {
                        case 36: 
                        case 37: {
                            this.tagAsNeedCheckCast();
                            return true;
                        }
                    }
                    return false;
                }
                case 4100: {
                    TypeBinding match = expressionType.findSuperTypeOriginatingFrom(castType);
                    if (match == null) {
                        this.checkUnsafeCast(scope, castType, expressionType, null, true);
                    }
                    TypeBinding[] typeBindingArray = ((TypeVariableBinding)castType).allUpperBounds();
                    int n = typeBindingArray.length;
                    int length = 0;
                    while (length < n) {
                        TypeBinding upperBound = typeBindingArray[length];
                        if (!this.checkCastTypesCompatibility(scope, upperBound, expressionType, expression, useAutoBoxing)) {
                            return false;
                        }
                        ++length;
                    }
                    return true;
                }
            }
            if (castType.isInterface()) {
                ReferenceBinding interfaceType = (ReferenceBinding)expressionType;
                TypeBinding match = interfaceType.findSuperTypeOriginatingFrom(castType);
                if (match != null) {
                    return this.checkUnsafeCast(scope, castType, interfaceType, match, false);
                }
                this.tagAsNeedCheckCast();
                match = castType.findSuperTypeOriginatingFrom(interfaceType);
                if (match != null) {
                    return this.checkUnsafeCast(scope, castType, interfaceType, match, true);
                }
                if (use15specifics) {
                    this.checkUnsafeCast(scope, castType, expressionType, null, true);
                    if (scope.compilerOptions().complianceLevel < 0x330000L ? interfaceType.hasIncompatibleSuperType((ReferenceBinding)castType) : !castType.isRawType() && interfaceType.hasIncompatibleSuperType((ReferenceBinding)castType)) {
                        return false;
                    }
                } else {
                    MethodBinding[] castTypeMethods = this.getAllOriginalInheritedMethods((ReferenceBinding)castType);
                    MethodBinding[] expressionTypeMethods = this.getAllOriginalInheritedMethods((ReferenceBinding)expressionType);
                    int exprMethodsLength = expressionTypeMethods.length;
                    int i = 0;
                    int castMethodsLength = castTypeMethods.length;
                    while (i < castMethodsLength) {
                        int j = 0;
                        while (j < exprMethodsLength) {
                            if (TypeBinding.notEquals(castTypeMethods[i].returnType, expressionTypeMethods[j].returnType) && CharOperation.equals(castTypeMethods[i].selector, expressionTypeMethods[j].selector) && castTypeMethods[i].areParametersEqual(expressionTypeMethods[j])) {
                                return false;
                            }
                            ++j;
                        }
                        ++i;
                    }
                }
                return true;
            }
            if (castType.id == 1) {
                this.tagAsUnnecessaryCast(scope, castType);
                return true;
            }
            this.tagAsNeedCheckCast();
            TypeBinding match = castType.findSuperTypeOriginatingFrom(expressionType);
            if (match != null) {
                return this.checkUnsafeCast(scope, castType, expressionType, match, true);
            }
            if (((ReferenceBinding)castType).isFinal()) {
                return false;
            }
            if (use15specifics) {
                this.checkUnsafeCast(scope, castType, expressionType, null, true);
                if (scope.compilerOptions().complianceLevel < 0x330000L ? ((ReferenceBinding)castType).hasIncompatibleSuperType((ReferenceBinding)expressionType) : !castType.isRawType() && ((ReferenceBinding)castType).hasIncompatibleSuperType((ReferenceBinding)expressionType)) {
                    return false;
                }
            }
            return true;
        }
        switch (castType.kind()) {
            case 68: {
                if (expressionType.id == 1) {
                    if (use15specifics) {
                        this.checkUnsafeCast(scope, castType, expressionType, expressionType, true);
                    }
                    this.tagAsNeedCheckCast();
                    return true;
                }
                return false;
            }
            case 4100: {
                TypeBinding match = expressionType.findSuperTypeOriginatingFrom(castType);
                if (match == null) {
                    this.checkUnsafeCast(scope, castType, expressionType, null, true);
                }
                TypeBinding[] typeBindingArray = ((TypeVariableBinding)castType).allUpperBounds();
                int n = typeBindingArray.length;
                int n3 = 0;
                while (n3 < n) {
                    TypeBinding upperBound = typeBindingArray[n3];
                    if (!this.checkCastTypesCompatibility(scope, upperBound, expressionType, expression, useAutoBoxing)) {
                        return false;
                    }
                    ++n3;
                }
                return true;
            }
        }
        if (castType.isInterface()) {
            ReferenceBinding refExprType = (ReferenceBinding)expressionType;
            TypeBinding match = refExprType.findSuperTypeOriginatingFrom(castType);
            if (match != null) {
                return this.checkUnsafeCast(scope, castType, expressionType, match, false);
            }
            if (refExprType.isFinal()) {
                return false;
            }
            this.tagAsNeedCheckCast();
            match = castType.findSuperTypeOriginatingFrom(expressionType);
            if (match != null) {
                return this.checkUnsafeCast(scope, castType, expressionType, match, true);
            }
            if (use15specifics) {
                this.checkUnsafeCast(scope, castType, expressionType, null, true);
                if (scope.compilerOptions().complianceLevel < 0x330000L ? refExprType.hasIncompatibleSuperType((ReferenceBinding)castType) : !castType.isRawType() && refExprType.hasIncompatibleSuperType((ReferenceBinding)castType)) {
                    return false;
                }
            }
            return true;
        }
        TypeBinding match = expressionType.findSuperTypeOriginatingFrom(castType);
        if (match != null) {
            if (expression != null && castType.id == 11) {
                this.constant = expression.constant;
            }
            return this.checkUnsafeCast(scope, castType, expressionType, match, false);
        }
        match = castType.findSuperTypeOriginatingFrom(expressionType);
        if (match != null) {
            this.tagAsNeedCheckCast();
            return this.checkUnsafeCast(scope, castType, expressionType, match, true);
        }
        return false;
    }

    public boolean checkNPE(BlockScope scope, FlowContext flowContext, FlowInfo flowInfo, int ttlForFieldCheck) {
        LocalVariableBinding local;
        boolean isNullable = false;
        if (this.resolvedType != null) {
            if ((this.resolvedType.tagBits & 0x100000000000000L) != 0L) {
                return true;
            }
            if ((this.resolvedType.tagBits & 0x80000000000000L) != 0L) {
                isNullable = true;
            }
        }
        if ((local = this.localVariableBinding()) != null && (local.type.tagBits & 2L) == 0L) {
            if ((this.bits & 0x20000) == 0) {
                flowContext.recordUsingNullReference(scope, local, this, 3, flowInfo);
                if (!flowInfo.isDefinitelyNonNull(local)) {
                    flowContext.recordAbruptExit();
                }
            }
            flowInfo.markAsComparedEqualToNonNull(local);
            flowContext.markFinallyNullStatus(local, 4);
            return true;
        }
        if (isNullable) {
            scope.problemReporter().dereferencingNullableExpression(this);
            return true;
        }
        return false;
    }

    public boolean checkNPE(BlockScope scope, FlowContext flowContext, FlowInfo flowInfo) {
        return this.checkNPE(scope, flowContext, flowInfo, 0);
    }

    protected void checkNPEbyUnboxing(BlockScope scope, FlowContext flowContext, FlowInfo flowInfo) {
        int status;
        if ((this.implicitConversion & 0x400) != 0 && (this.bits & 0x20000) == 0 && (status = this.nullStatus(flowInfo, flowContext)) != 4) {
            flowContext.recordUnboxing(scope, this, status, flowInfo);
        }
    }

    public boolean checkUnsafeCast(Scope scope, TypeBinding castType, TypeBinding expressionType, TypeBinding match, boolean isNarrowing) {
        if (TypeBinding.equalsEquals(match, castType)) {
            if (!isNarrowing) {
                this.tagAsUnnecessaryCast(scope, castType);
            }
            return true;
        }
        if (!(match == null || castType.isReifiable() && expressionType.isReifiable() || !(isNarrowing ? match.isProvablyDistinct(expressionType) : castType.isProvablyDistinct(match)))) {
            return false;
        }
        if (!isNarrowing) {
            this.tagAsUnnecessaryCast(scope, castType);
        }
        return true;
    }

    public void computeConversion(Scope scope, TypeBinding runtimeType, TypeBinding compileTimeType) {
        int compileTimeTypeID;
        if (runtimeType == null || compileTimeType == null) {
            return;
        }
        if (this.implicitConversion != 0) {
            return;
        }
        if (runtimeType != TypeBinding.NULL && runtimeType.isBaseType()) {
            if (!compileTimeType.isBaseType()) {
                TypeBinding unboxedType = scope.environment().computeBoxingType(compileTimeType);
                this.implicitConversion = 1024;
                scope.problemReporter().autoboxing(this, compileTimeType, runtimeType);
                compileTimeType = unboxedType;
            }
        } else {
            if (compileTimeType != TypeBinding.NULL && compileTimeType.isBaseType()) {
                TypeBinding boxedType = scope.environment().computeBoxingType(runtimeType);
                if (TypeBinding.equalsEquals(boxedType, runtimeType)) {
                    boxedType = compileTimeType;
                }
                if (boxedType.id > 33) {
                    boxedType = compileTimeType;
                }
                this.implicitConversion = 0x200 | (boxedType.id << 4) + compileTimeType.id;
                scope.problemReporter().autoboxing(this, compileTimeType, scope.environment().computeBoxingType(boxedType));
                return;
            }
            if (this.constant != Constant.NotAConstant && this.constant.typeID() != 11) {
                this.implicitConversion = 512;
                return;
            }
        }
        if ((compileTimeTypeID = compileTimeType.id) >= 128) {
            compileTimeTypeID = compileTimeType.erasure().id == 11 ? 11 : 1;
        } else if (runtimeType.isPrimitiveType() && compileTimeType instanceof ReferenceBinding && !compileTimeType.isBoxedPrimitiveType()) {
            compileTimeTypeID = 1;
        }
        int runtimeTypeID = runtimeType.id;
        switch (runtimeTypeID) {
            case 2: 
            case 3: 
            case 4: {
                if (compileTimeTypeID == 1) {
                    this.implicitConversion |= (runtimeTypeID << 4) + compileTimeTypeID;
                    break;
                }
                this.implicitConversion |= 160 + compileTimeTypeID;
                break;
            }
            case 5: 
            case 7: 
            case 8: 
            case 9: 
            case 10: 
            case 11: {
                this.implicitConversion |= (runtimeTypeID << 4) + compileTimeTypeID;
            }
        }
    }

    public static int computeNullStatus(int status, int combinedStatus) {
        if ((combinedStatus & 0x12) != 0) {
            status |= 0x10;
        }
        if ((combinedStatus & 0x24) != 0) {
            status |= 0x20;
        }
        if ((combinedStatus & 9) != 0) {
            status |= 8;
        }
        return status;
    }

    @Override
    public void generateCode(BlockScope currentScope, CodeStream codeStream) {
        if ((this.bits & Integer.MIN_VALUE) == 0) {
            return;
        }
        this.generateCode(currentScope, codeStream, false);
    }

    public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
        if (this.constant == Constant.NotAConstant) {
            throw new ShouldNotImplement(Messages.ast_missingCode);
        }
        int pc = codeStream.position;
        codeStream.generateConstant(this.constant, this.implicitConversion);
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }

    public void addPatternVariables(BlockScope scope, CodeStream codeStream) {
    }

    protected LocalDeclaration getPatternVariableIntroduced() {
        return null;
    }

    public void collectPatternVariablesToScope(LocalVariableBinding[] variables, BlockScope scope) {
        new ASTVisitor(){
            LocalVariableBinding[] patternVariablesInScope;

            @Override
            public boolean visit(Argument argument, BlockScope skope) {
                argument.addPatternVariablesWhenTrue(this.patternVariablesInScope);
                return true;
            }

            @Override
            public boolean visit(QualifiedNameReference nameReference, BlockScope skope) {
                nameReference.addPatternVariablesWhenTrue(this.patternVariablesInScope);
                return true;
            }

            @Override
            public boolean visit(SingleNameReference nameReference, BlockScope skope) {
                nameReference.addPatternVariablesWhenTrue(this.patternVariablesInScope);
                return true;
            }

            public void propagatePatternVariablesInScope(LocalVariableBinding[] vars, BlockScope skope) {
                this.patternVariablesInScope = vars;
                Expression.this.traverse((ASTVisitor)this, skope);
            }
        }.propagatePatternVariablesInScope(variables, scope);
    }

    public void generateOptimizedBoolean(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired) {
        Constant cst = this.optimizedBooleanConstant();
        this.generateCode(currentScope, codeStream, valueRequired && cst == Constant.NotAConstant);
        if (cst != Constant.NotAConstant && cst.typeID() == 5) {
            int pc = codeStream.position;
            if (cst.booleanValue()) {
                if (valueRequired && falseLabel == null && trueLabel != null) {
                    codeStream.goto_(trueLabel);
                }
            } else if (valueRequired && falseLabel != null && trueLabel == null) {
                codeStream.goto_(falseLabel);
            }
            codeStream.recordPositionsFrom(pc, this.sourceStart);
            return;
        }
        int position = codeStream.position;
        if (valueRequired) {
            if (falseLabel == null) {
                if (trueLabel != null) {
                    codeStream.ifne(trueLabel);
                }
            } else if (trueLabel == null) {
                codeStream.ifeq(falseLabel);
            }
        }
        codeStream.recordPositionsFrom(position, this.sourceEnd);
    }

    public void generateOptimizedStringConcatenation(BlockScope blockScope, CodeStream codeStream, int typeID) {
        if (typeID == 11 && this.constant != Constant.NotAConstant && this.constant.stringValue().length() == 0) {
            return;
        }
        this.generateCode(blockScope, codeStream, true);
        codeStream.invokeStringConcatenationAppendForType(typeID);
    }

    public void generateOptimizedStringConcatenationCreation(BlockScope blockScope, CodeStream codeStream, int typeID) {
        codeStream.newStringContatenation();
        codeStream.dup();
        switch (typeID) {
            case 0: 
            case 1: {
                codeStream.invokeStringConcatenationDefaultConstructor();
                this.generateCode(blockScope, codeStream, true);
                codeStream.invokeStringConcatenationAppendForType(1);
                return;
            }
            case 11: 
            case 12: {
                if (this.constant != Constant.NotAConstant) {
                    String stringValue = this.constant.stringValue();
                    if (stringValue.length() == 0) {
                        codeStream.invokeStringConcatenationDefaultConstructor();
                        return;
                    }
                    codeStream.ldc(stringValue);
                    break;
                }
                this.generateCode(blockScope, codeStream, true);
                codeStream.invokeStringValueOf(1);
                break;
            }
            default: {
                this.generateCode(blockScope, codeStream, true);
                codeStream.invokeStringValueOf(typeID);
            }
        }
        codeStream.invokeStringConcatenationStringConstructor();
    }

    private MethodBinding[] getAllOriginalInheritedMethods(ReferenceBinding binding) {
        ArrayList<MethodBinding> collector = new ArrayList<MethodBinding>();
        this.getAllInheritedMethods0(binding, collector);
        int i = 0;
        int len = collector.size();
        while (i < len) {
            collector.set(i, collector.get(i).original());
            ++i;
        }
        return collector.toArray(new MethodBinding[collector.size()]);
    }

    private void getAllInheritedMethods0(ReferenceBinding binding, ArrayList<MethodBinding> collector) {
        if (!binding.isInterface()) {
            return;
        }
        MethodBinding[] methodBindings = binding.methods();
        int i = 0;
        int max = methodBindings.length;
        while (i < max) {
            collector.add(methodBindings[i]);
            ++i;
        }
        ReferenceBinding[] superInterfaces = binding.superInterfaces();
        int i2 = 0;
        int max2 = superInterfaces.length;
        while (i2 < max2) {
            this.getAllInheritedMethods0(superInterfaces[i2], collector);
            ++i2;
        }
    }

    public static Binding getDirectBinding(Expression someExpression) {
        if ((someExpression.bits & 0x20000000) != 0) {
            return null;
        }
        if (someExpression instanceof SingleNameReference) {
            return ((SingleNameReference)someExpression).binding;
        }
        if (someExpression instanceof FieldReference) {
            FieldReference fieldRef = (FieldReference)someExpression;
            if (fieldRef.receiver.isThis() && !(fieldRef.receiver instanceof QualifiedThisReference)) {
                return fieldRef.binding;
            }
        } else if (someExpression instanceof Assignment) {
            Expression lhs = ((Assignment)someExpression).lhs;
            if ((lhs.bits & 0x2000) != 0) {
                return Expression.getDirectBinding(((Assignment)someExpression).lhs);
            }
            if (someExpression instanceof PrefixExpression) {
                return Expression.getDirectBinding(((Assignment)someExpression).lhs);
            }
        } else if (someExpression instanceof QualifiedNameReference) {
            QualifiedNameReference qualifiedNameReference = (QualifiedNameReference)someExpression;
            if (qualifiedNameReference.indexOfFirstFieldBinding != 1 && qualifiedNameReference.otherBindings == null) {
                return qualifiedNameReference.binding;
            }
        } else if (someExpression.isThis()) {
            return someExpression.resolvedType;
        }
        return null;
    }

    public boolean isCompactableOperation() {
        return false;
    }

    public boolean isConstantValueOfTypeAssignableToType(TypeBinding constantType, TypeBinding targetType) {
        if (this.constant == Constant.NotAConstant) {
            return false;
        }
        if (TypeBinding.equalsEquals(constantType, targetType)) {
            return true;
        }
        if (BaseTypeBinding.isWidening(10, constantType.id) && BaseTypeBinding.isNarrowing(targetType.id, 10)) {
            return Expression.isConstantValueRepresentable(this.constant, constantType.id, targetType.id);
        }
        return false;
    }

    public boolean isTypeReference() {
        return false;
    }

    public LocalVariableBinding localVariableBinding() {
        return null;
    }

    public void markAsNonNull() {
        this.bits |= 0x20000;
    }

    public int nullStatus(FlowInfo flowInfo, FlowContext flowContext) {
        return 4;
    }

    public Constant optimizedBooleanConstant() {
        return this.constant;
    }

    public boolean isPertinentToApplicability(TypeBinding targetType, MethodBinding method) {
        return true;
    }

    public TypeBinding postConversionType(Scope scope) {
        TypeBinding convertedType = this.resolvedType;
        int runtimeType = (this.implicitConversion & 0xFF) >> 4;
        switch (runtimeType) {
            case 5: {
                convertedType = TypeBinding.BOOLEAN;
                break;
            }
            case 3: {
                convertedType = TypeBinding.BYTE;
                break;
            }
            case 4: {
                convertedType = TypeBinding.SHORT;
                break;
            }
            case 2: {
                convertedType = TypeBinding.CHAR;
                break;
            }
            case 10: {
                convertedType = TypeBinding.INT;
                break;
            }
            case 9: {
                convertedType = TypeBinding.FLOAT;
                break;
            }
            case 7: {
                convertedType = TypeBinding.LONG;
                break;
            }
            case 8: {
                convertedType = TypeBinding.DOUBLE;
            }
        }
        if ((this.implicitConversion & 0x200) != 0) {
            convertedType = scope.environment().computeBoxingType(convertedType);
        }
        return convertedType;
    }

    @Override
    public StringBuffer print(int indent, StringBuffer output) {
        Expression.printIndent(indent, output);
        return this.printExpression(indent, output);
    }

    public abstract StringBuffer printExpression(int var1, StringBuffer var2);

    @Override
    public StringBuffer printStatement(int indent, StringBuffer output) {
        return this.print(indent, output).append(";");
    }

    @Override
    public void resolve(BlockScope scope) {
        this.resolveType(scope);
    }

    @Override
    public TypeBinding resolveExpressionType(BlockScope scope) {
        return this.resolveType(scope);
    }

    public TypeBinding resolveType(BlockScope scope) {
        return null;
    }

    public TypeBinding resolveType(ClassScope scope) {
        return null;
    }

    public TypeBinding resolveTypeExpecting(BlockScope scope, TypeBinding expectedType) {
        this.setExpectedType(expectedType);
        TypeBinding expressionType = this.resolveType(scope);
        if (expressionType == null) {
            return null;
        }
        if (TypeBinding.equalsEquals(expressionType, expectedType)) {
            return expressionType;
        }
        if (!expressionType.isCompatibleWith(expectedType)) {
            if (scope.isBoxingCompatibleWith(expressionType, expectedType)) {
                this.computeConversion(scope, expectedType, expressionType);
            } else {
                scope.problemReporter().typeMismatchError(expressionType, expectedType, this, null);
                return null;
            }
        }
        return expressionType;
    }

    public Expression resolveExpressionExpecting(TypeBinding targetType, Scope scope, InferenceContext18 context) {
        return this;
    }

    public boolean forcedToBeRaw(ReferenceContext referenceContext) {
        if (this instanceof NameReference) {
            Binding receiverBinding = ((NameReference)this).binding;
            if (receiverBinding.isParameter() && (((LocalVariableBinding)receiverBinding).tagBits & 0x200L) != 0L) {
                return true;
            }
            if (receiverBinding instanceof FieldBinding) {
                FieldBinding field = (FieldBinding)receiverBinding;
                if (field.type.isRawType()) {
                    if (referenceContext instanceof AbstractMethodDeclaration) {
                        ReferenceBinding declaringClass;
                        AbstractMethodDeclaration methodDecl = (AbstractMethodDeclaration)referenceContext;
                        ReferenceBinding referenceBinding = declaringClass = methodDecl.binding != null ? methodDecl.binding.declaringClass : methodDecl.scope.enclosingReceiverType();
                        if (TypeBinding.notEquals(field.declaringClass, declaringClass)) {
                            return true;
                        }
                    } else if (referenceContext instanceof TypeDeclaration) {
                        TypeDeclaration type = (TypeDeclaration)referenceContext;
                        if (TypeBinding.notEquals(field.declaringClass, type.binding)) {
                            return true;
                        }
                    }
                }
            }
        } else if (this instanceof MessageSend) {
            if (!CharOperation.equals(((MessageSend)this).binding.declaringClass.getFileName(), referenceContext.compilationResult().getFileName())) {
                return true;
            }
        } else if (this instanceof FieldReference) {
            FieldBinding field = ((FieldReference)this).binding;
            if (!CharOperation.equals(field.declaringClass.getFileName(), referenceContext.compilationResult().getFileName())) {
                return true;
            }
            if (field.type.isRawType()) {
                if (referenceContext instanceof AbstractMethodDeclaration) {
                    ReferenceBinding declaringClass;
                    AbstractMethodDeclaration methodDecl = (AbstractMethodDeclaration)referenceContext;
                    ReferenceBinding referenceBinding = declaringClass = methodDecl.binding != null ? methodDecl.binding.declaringClass : methodDecl.scope.enclosingReceiverType();
                    if (TypeBinding.notEquals(field.declaringClass, declaringClass)) {
                        return true;
                    }
                } else if (referenceContext instanceof TypeDeclaration) {
                    TypeDeclaration type = (TypeDeclaration)referenceContext;
                    if (TypeBinding.notEquals(field.declaringClass, type.binding)) {
                        return true;
                    }
                }
            }
        } else if (this instanceof ConditionalExpression) {
            ConditionalExpression ternary = (ConditionalExpression)this;
            if (ternary.valueIfTrue.forcedToBeRaw(referenceContext) || ternary.valueIfFalse.forcedToBeRaw(referenceContext)) {
                return true;
            }
        } else if (this instanceof SwitchExpression) {
            SwitchExpression se = (SwitchExpression)this;
            for (Expression e : se.resultExpressions) {
                if (!e.forcedToBeRaw(referenceContext)) continue;
                return true;
            }
        }
        return false;
    }

    public Object reusableJSRTarget() {
        if (this.constant != Constant.NotAConstant && (this.implicitConversion & 0x200) == 0) {
            return this.constant;
        }
        return null;
    }

    public void setExpectedType(TypeBinding expectedType) {
    }

    public void setExpressionContext(ExpressionContext context) {
    }

    public boolean isCompatibleWith(TypeBinding left, Scope scope) {
        return this.resolvedType != null && this.resolvedType.isCompatibleWith(left, scope);
    }

    public boolean isBoxingCompatibleWith(TypeBinding left, Scope scope) {
        return this.resolvedType != null && this.isBoxingCompatible(this.resolvedType, left, this, scope);
    }

    public boolean sIsMoreSpecific(TypeBinding s, TypeBinding t, Scope scope) {
        return s.isCompatibleWith(t, scope);
    }

    public boolean isExactMethodReference() {
        return false;
    }

    public boolean isPolyExpression() throws UnsupportedOperationException {
        return false;
    }

    public boolean isPolyExpression(MethodBinding method) {
        return false;
    }

    public void tagAsNeedCheckCast() {
    }

    public void tagAsUnnecessaryCast(Scope scope, TypeBinding castType) {
    }

    public Expression toTypeReference() {
        return this;
    }

    @Override
    public void traverse(ASTVisitor visitor, BlockScope scope) {
    }

    public void traverse(ASTVisitor visitor, ClassScope scope) {
    }

    public boolean statementExpression() {
        return false;
    }

    public boolean isTrulyExpression() {
        return true;
    }

    public VariableBinding nullAnnotatedVariableBinding(boolean supportTypeAnnotations) {
        return null;
    }

    public boolean isFunctionalType() {
        return false;
    }

    public Expression[] getPolyExpressions() {
        Expression[] expressionArray;
        if (this.isPolyExpression()) {
            Expression[] expressionArray2 = new Expression[1];
            expressionArray = expressionArray2;
            expressionArray2[0] = this;
        } else {
            expressionArray = NO_EXPRESSIONS;
        }
        return expressionArray;
    }

    public boolean isPotentiallyCompatibleWith(TypeBinding targetType, Scope scope) {
        return this.isCompatibleWith(targetType, scope);
    }

    protected Constant optimizedNullComparisonConstant() {
        return Constant.NotAConstant;
    }
}

