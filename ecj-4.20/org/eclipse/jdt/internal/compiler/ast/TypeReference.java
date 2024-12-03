/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MarkerAnnotation;
import org.eclipse.jdt.internal.compiler.ast.NormalAnnotation;
import org.eclipse.jdt.internal.compiler.ast.NullAnnotationMatching;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.RecordComponent;
import org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.codegen.AnnotationContext;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.RecordComponentBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Substitution;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;

public abstract class TypeReference
extends Expression {
    public static final TypeReference[] NO_TYPE_ARGUMENTS = new TypeReference[0];
    public Annotation[][] annotations = null;

    public static final TypeReference baseTypeReference(int baseType, int dim, Annotation[][] dimAnnotations) {
        if (dim == 0) {
            switch (baseType) {
                case 6: {
                    return new SingleTypeReference(TypeBinding.VOID.simpleName, 0L);
                }
                case 5: {
                    return new SingleTypeReference(TypeBinding.BOOLEAN.simpleName, 0L);
                }
                case 2: {
                    return new SingleTypeReference(TypeBinding.CHAR.simpleName, 0L);
                }
                case 9: {
                    return new SingleTypeReference(TypeBinding.FLOAT.simpleName, 0L);
                }
                case 8: {
                    return new SingleTypeReference(TypeBinding.DOUBLE.simpleName, 0L);
                }
                case 3: {
                    return new SingleTypeReference(TypeBinding.BYTE.simpleName, 0L);
                }
                case 4: {
                    return new SingleTypeReference(TypeBinding.SHORT.simpleName, 0L);
                }
                case 10: {
                    return new SingleTypeReference(TypeBinding.INT.simpleName, 0L);
                }
            }
            return new SingleTypeReference(TypeBinding.LONG.simpleName, 0L);
        }
        switch (baseType) {
            case 6: {
                return new ArrayTypeReference(TypeBinding.VOID.simpleName, dim, dimAnnotations, 0L);
            }
            case 5: {
                return new ArrayTypeReference(TypeBinding.BOOLEAN.simpleName, dim, dimAnnotations, 0L);
            }
            case 2: {
                return new ArrayTypeReference(TypeBinding.CHAR.simpleName, dim, dimAnnotations, 0L);
            }
            case 9: {
                return new ArrayTypeReference(TypeBinding.FLOAT.simpleName, dim, dimAnnotations, 0L);
            }
            case 8: {
                return new ArrayTypeReference(TypeBinding.DOUBLE.simpleName, dim, dimAnnotations, 0L);
            }
            case 3: {
                return new ArrayTypeReference(TypeBinding.BYTE.simpleName, dim, dimAnnotations, 0L);
            }
            case 4: {
                return new ArrayTypeReference(TypeBinding.SHORT.simpleName, dim, dimAnnotations, 0L);
            }
            case 10: {
                return new ArrayTypeReference(TypeBinding.INT.simpleName, dim, dimAnnotations, 0L);
            }
        }
        return new ArrayTypeReference(TypeBinding.LONG.simpleName, dim, dimAnnotations, 0L);
    }

    public static final TypeReference baseTypeReference(int baseType, int dim) {
        return TypeReference.baseTypeReference(baseType, dim, null);
    }

    public void aboutToResolve(Scope scope) {
    }

    private void checkYieldUsage(Scope currentScope) {
        char[][] qName = this.getTypeName();
        String name = qName != null && qName[0] != null ? new String(qName[0]) : null;
        long sourceLevel = currentScope.compilerOptions().sourceLevel;
        if (sourceLevel < 0x3A0000L || name == null || !"yield".equals(new String(name))) {
            return;
        }
        if (sourceLevel >= 0x3A0000L) {
            currentScope.problemReporter().switchExpressionsYieldTypeDeclarationError(this);
        } else {
            currentScope.problemReporter().switchExpressionsYieldTypeDeclarationWarning(this);
        }
    }

    @Override
    public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
        return flowInfo;
    }

    public void checkBounds(Scope scope) {
    }

    public abstract TypeReference augmentTypeWithAdditionalDimensions(int var1, Annotation[][] var2, boolean var3);

    protected Annotation[][] getMergedAnnotationsOnDimensions(int additionalDimensions, Annotation[][] additionalAnnotations) {
        Annotation[][] annotationsOnDimensions = this.getAnnotationsOnDimensions(true);
        int dimensions = this.dimensions();
        if (annotationsOnDimensions == null && additionalAnnotations == null) {
            return null;
        }
        int totalDimensions = dimensions + additionalDimensions;
        Annotation[][] mergedAnnotations = new Annotation[totalDimensions][];
        if (annotationsOnDimensions != null) {
            System.arraycopy(annotationsOnDimensions, 0, mergedAnnotations, 0, dimensions);
        }
        if (additionalAnnotations != null) {
            int i = dimensions;
            int j = 0;
            while (i < totalDimensions) {
                mergedAnnotations[i] = additionalAnnotations[j];
                ++i;
                ++j;
            }
        }
        return mergedAnnotations;
    }

    public int dimensions() {
        return 0;
    }

    public int extraDimensions() {
        return 0;
    }

    public AnnotationContext[] getAllAnnotationContexts(int targetType) {
        ArrayList<AnnotationContext> allAnnotationContexts = new ArrayList<AnnotationContext>();
        AnnotationCollector collector = new AnnotationCollector(this, targetType, allAnnotationContexts);
        this.traverse((ASTVisitor)collector, (BlockScope)null);
        return allAnnotationContexts.toArray(new AnnotationContext[allAnnotationContexts.size()]);
    }

    public void getAllAnnotationContexts(int targetType, int info, List<AnnotationContext> allAnnotationContexts) {
        AnnotationCollector collector = new AnnotationCollector(this, targetType, info, allAnnotationContexts);
        this.traverse((ASTVisitor)collector, (BlockScope)null);
    }

    public void getAllAnnotationContexts(int targetType, int info, List<AnnotationContext> allAnnotationContexts, Annotation[] se7Annotations) {
        AnnotationCollector collector = new AnnotationCollector(this, targetType, info, allAnnotationContexts);
        int i = 0;
        int length = se7Annotations == null ? 0 : se7Annotations.length;
        while (i < length) {
            Annotation annotation = se7Annotations[i];
            annotation.traverse((ASTVisitor)collector, (BlockScope)null);
            ++i;
        }
        this.traverse((ASTVisitor)collector, (BlockScope)null);
    }

    public void getAllAnnotationContexts(int targetType, int info, List<AnnotationContext> allAnnotationContexts, Annotation[][] annotationsOnDimensions, int dimensions) {
        AnnotationCollector collector = new AnnotationCollector(this, targetType, info, allAnnotationContexts, annotationsOnDimensions, dimensions);
        this.traverse((ASTVisitor)collector, (BlockScope)null);
        if (annotationsOnDimensions != null) {
            int i = 0;
            int max = annotationsOnDimensions.length;
            while (i < max) {
                Annotation[] annotationsOnDimension = annotationsOnDimensions[i];
                if (annotationsOnDimension != null) {
                    int j = 0;
                    int max2 = annotationsOnDimension.length;
                    while (j < max2) {
                        annotationsOnDimension[j].traverse((ASTVisitor)collector, (BlockScope)null);
                        ++j;
                    }
                }
                ++i;
            }
        }
    }

    public void getAllAnnotationContexts(int targetType, int info, int typeIndex, List<AnnotationContext> allAnnotationContexts) {
        AnnotationCollector collector = new AnnotationCollector(this, targetType, info, typeIndex, allAnnotationContexts);
        this.traverse((ASTVisitor)collector, (BlockScope)null);
    }

    public void getAllAnnotationContexts(int targetType, List<AnnotationContext> allAnnotationContexts) {
        AnnotationCollector collector = new AnnotationCollector(this, targetType, allAnnotationContexts);
        this.traverse((ASTVisitor)collector, (BlockScope)null);
    }

    public Annotation[][] getAnnotationsOnDimensions() {
        return this.getAnnotationsOnDimensions(false);
    }

    public TypeReference[][] getTypeArguments() {
        return null;
    }

    public Annotation[][] getAnnotationsOnDimensions(boolean useSourceOrder) {
        return null;
    }

    public void setAnnotationsOnDimensions(Annotation[][] annotationsOnDimensions) {
    }

    public abstract char[] getLastToken();

    public char[][] getParameterizedTypeName() {
        return this.getTypeName();
    }

    protected abstract TypeBinding getTypeBinding(Scope var1);

    public abstract char[][] getTypeName();

    protected TypeBinding internalResolveType(Scope scope, int location) {
        TypeBinding type;
        boolean hasError;
        block19: {
            block18: {
                this.constant = Constant.NotAConstant;
                this.checkYieldUsage(scope);
                if (this.resolvedType != null) {
                    if (this.resolvedType.isValidBinding()) {
                        return this.resolvedType;
                    }
                    switch (this.resolvedType.problemId()) {
                        case 1: 
                        case 2: 
                        case 5: {
                            TypeBinding type2 = this.resolvedType.closestMatch();
                            if (type2 == null) {
                                return null;
                            }
                            return scope.environment().convertToRawType(type2, false);
                        }
                    }
                    return null;
                }
                hasError = false;
                this.resolvedType = this.getTypeBinding(scope);
                type = this.resolvedType;
                if (type == null) {
                    return null;
                }
                hasError = !type.isValidBinding();
                if (!hasError) break block18;
                if (this.isTypeNameVar(scope)) {
                    this.reportVarIsNotAllowedHere(scope);
                } else if (!scope.problemReporter().validateRestrictedKeywords(this.getLastToken(), this)) {
                    this.reportInvalidType(scope);
                }
                switch (type.problemId()) {
                    case 1: 
                    case 2: 
                    case 5: {
                        type = type.closestMatch();
                        if (type == null) {
                            return null;
                        }
                        break block19;
                    }
                    default: {
                        return null;
                    }
                }
            }
            scope.problemReporter().validateRestrictedKeywords(this.getLastToken(), this);
        }
        if (type.isArrayType() && ((ArrayBinding)type).leafComponentType == TypeBinding.VOID) {
            scope.problemReporter().cannotAllocateVoidArray(this);
            return null;
        }
        if (!(this instanceof QualifiedTypeReference) && this.isTypeUseDeprecated(type, scope)) {
            this.reportDeprecatedType(type, scope);
        }
        if ((type = scope.environment().convertToRawType(type, false)).leafComponentType().isRawType() && (this.bits & 0x40000000) == 0 && scope.compilerOptions().getSeverity(0x20010000) != 256) {
            scope.problemReporter().rawTypeReference(this, type);
        }
        if (hasError) {
            this.resolveAnnotations(scope, 0);
            return type;
        }
        this.resolvedType = type;
        this.resolveAnnotations(scope, location);
        return this.resolvedType;
    }

    @Override
    public boolean isTypeReference() {
        return true;
    }

    public boolean isWildcard() {
        return false;
    }

    public boolean isUnionType() {
        return false;
    }

    public boolean isVarargs() {
        return (this.bits & 0x4000) != 0;
    }

    public boolean isParameterizedTypeReference() {
        return false;
    }

    protected void reportDeprecatedType(TypeBinding type, Scope scope, int index) {
        scope.problemReporter().deprecatedType(type, this, index);
    }

    protected void reportDeprecatedType(TypeBinding type, Scope scope) {
        scope.problemReporter().deprecatedType(type, this, Integer.MAX_VALUE);
    }

    protected void reportInvalidType(Scope scope) {
        scope.problemReporter().invalidType(this, this.resolvedType);
    }

    protected void reportVarIsNotAllowedHere(Scope scope) {
        scope.problemReporter().varIsNotAllowedHere(this);
    }

    public TypeBinding resolveSuperType(ClassScope scope) {
        TypeBinding superType = this.resolveType(scope);
        if (superType == null) {
            return null;
        }
        if (superType.isTypeVariable()) {
            if (this.resolvedType.isValidBinding()) {
                this.resolvedType = new ProblemReferenceBinding(this.getTypeName(), (ReferenceBinding)this.resolvedType, 9);
                this.reportInvalidType(scope);
            }
            return null;
        }
        return superType;
    }

    @Override
    public final TypeBinding resolveType(BlockScope blockScope) {
        return this.resolveType(blockScope, true);
    }

    public TypeBinding resolveType(BlockScope scope, boolean checkBounds) {
        return this.resolveType(scope, checkBounds, 0);
    }

    public TypeBinding resolveType(BlockScope scope, boolean checkBounds, int location) {
        return this.internalResolveType(scope, location);
    }

    @Override
    public TypeBinding resolveType(ClassScope scope) {
        return this.resolveType(scope, 0);
    }

    public TypeBinding resolveType(ClassScope scope, int location) {
        return this.internalResolveType(scope, location);
    }

    public TypeBinding resolveTypeArgument(BlockScope blockScope, ReferenceBinding genericType, int rank) {
        return this.resolveType(blockScope, true, 64);
    }

    public TypeBinding resolveTypeArgument(ClassScope classScope, ReferenceBinding genericType, int rank) {
        SourceTypeBinding ref = classScope.referenceContext.binding;
        boolean pauseHierarchyCheck = false;
        try {
            if (ref.isHierarchyBeingConnected()) {
                pauseHierarchyCheck = (ref.tagBits & 0x80000L) == 0L;
                ref.tagBits |= 0x80000L;
            }
            TypeBinding typeBinding = this.resolveType(classScope, 64);
            return typeBinding;
        }
        finally {
            if (pauseHierarchyCheck) {
                ref.tagBits &= 0xFFFFFFFFFFF7FFFFL;
            }
        }
    }

    @Override
    public abstract void traverse(ASTVisitor var1, BlockScope var2);

    @Override
    public abstract void traverse(ASTVisitor var1, ClassScope var2);

    protected void resolveAnnotations(Scope scope, int location) {
        BlockScope resolutionScope;
        Annotation[][] annotationsOnDimensions = this.getAnnotationsOnDimensions();
        if ((this.annotations != null || annotationsOnDimensions != null) && (resolutionScope = Scope.typeAnnotationsResolutionScope(scope)) != null) {
            int dimensions = this.dimensions();
            if (this.annotations != null) {
                TypeBinding leafComponentType = this.resolvedType.leafComponentType();
                leafComponentType = TypeReference.resolveAnnotations(resolutionScope, this.annotations, leafComponentType);
                TypeBinding typeBinding = this.resolvedType = dimensions > 0 ? scope.environment().createArrayType(leafComponentType, dimensions) : leafComponentType;
            }
            if (annotationsOnDimensions != null) {
                long[] nullTagBitsPerDimension;
                this.resolvedType = TypeReference.resolveAnnotations(resolutionScope, annotationsOnDimensions, this.resolvedType);
                if (this.resolvedType instanceof ArrayBinding && (nullTagBitsPerDimension = ((ArrayBinding)this.resolvedType).nullTagBitsPerDimension) != null) {
                    int i = 0;
                    while (i < dimensions) {
                        if ((nullTagBitsPerDimension[i] & 0x180000000000000L) == 0x180000000000000L) {
                            scope.problemReporter().contradictoryNullAnnotations(annotationsOnDimensions[i]);
                            nullTagBitsPerDimension[i] = 0L;
                        }
                        ++i;
                    }
                }
            }
        }
        if (scope.compilerOptions().isAnnotationBasedNullAnalysisEnabled && this.resolvedType != null && (this.resolvedType.tagBits & 0x180000000000000L) == 0L && !this.resolvedType.isTypeVariable() && !this.resolvedType.isWildcard() && location != 0 && scope.hasDefaultNullnessFor(location, this.sourceStart)) {
            if (location == 256 && this.resolvedType.id == 1) {
                scope.problemReporter().implicitObjectBoundNoNullDefault(this);
            } else {
                LookupEnvironment environment = scope.environment();
                AnnotationBinding[] annots = new AnnotationBinding[]{environment.getNonNullAnnotation()};
                this.resolvedType = environment.createAnnotatedType(this.resolvedType, annots);
            }
        }
    }

    public int getAnnotatableLevels() {
        return 1;
    }

    protected void checkIllegalNullAnnotations(Scope scope, TypeReference[] typeArguments) {
        if (scope.environment().usesNullTypeAnnotations() && typeArguments != null) {
            int i = 0;
            while (i < typeArguments.length) {
                TypeReference arg = typeArguments[i];
                if (arg.resolvedType != null) {
                    arg.checkIllegalNullAnnotation(scope);
                }
                ++i;
            }
        }
    }

    protected void checkNullConstraints(Scope scope, Substitution substitution, TypeBinding[] variables, int rank) {
        TypeBinding variable;
        if (variables != null && variables.length > rank && (variable = variables[rank]).hasNullTypeAnnotations() && NullAnnotationMatching.analyse(variable, this.resolvedType, null, substitution, -1, null, NullAnnotationMatching.CheckMode.BOUND_CHECK).isAnyMismatch()) {
            scope.problemReporter().nullityMismatchTypeArgument(variable, this.resolvedType, this);
        }
        this.checkIllegalNullAnnotation(scope);
    }

    protected void checkIllegalNullAnnotation(Scope scope) {
        if (this.resolvedType.leafComponentType().isBaseType() && this.hasNullTypeAnnotation(AnnotationPosition.LEAF_TYPE)) {
            scope.problemReporter().illegalAnnotationForBaseType(this, this.annotations[0], this.resolvedType.tagBits & 0x180000000000000L);
        }
    }

    public Annotation findAnnotation(long nullTagBits) {
        Annotation[] innerAnnotations;
        if (this.annotations != null && (innerAnnotations = this.annotations[this.annotations.length - 1]) != null) {
            int annBit = nullTagBits == 0x100000000000000L ? 32 : 64;
            int i = 0;
            while (i < innerAnnotations.length) {
                if (innerAnnotations[i] != null && innerAnnotations[i].hasNullBit(annBit)) {
                    return innerAnnotations[i];
                }
                ++i;
            }
        }
        return null;
    }

    public boolean hasNullTypeAnnotation(AnnotationPosition position) {
        if (this.annotations != null) {
            if (position == AnnotationPosition.MAIN_TYPE) {
                Annotation[] innerAnnotations = this.annotations[this.annotations.length - 1];
                return TypeReference.containsNullAnnotation(innerAnnotations);
            }
            Annotation[][] annotationArray = this.annotations;
            int n = this.annotations.length;
            int n2 = 0;
            while (n2 < n) {
                Annotation[] someAnnotations = annotationArray[n2];
                if (TypeReference.containsNullAnnotation(someAnnotations)) {
                    return true;
                }
                ++n2;
            }
        }
        return false;
    }

    public static boolean containsNullAnnotation(Annotation[] annotations) {
        if (annotations != null) {
            int i = 0;
            while (i < annotations.length) {
                if (annotations[i] != null && annotations[i].hasNullBit(96)) {
                    return true;
                }
                ++i;
            }
        }
        return false;
    }

    public TypeReference[] getTypeReferences() {
        return new TypeReference[]{this};
    }

    public boolean isBaseTypeReference() {
        return false;
    }

    private char[] getTypeName(int index) {
        char[][] typeName = this.getTypeName();
        return typeName != null && typeName.length > index ? typeName[index] : CharOperation.NO_CHAR;
    }

    public boolean isTypeNameVar(Scope scope) {
        CompilerOptions compilerOptions;
        CompilerOptions compilerOptions2 = compilerOptions = scope != null ? scope.compilerOptions() : null;
        if (compilerOptions != null && compilerOptions.sourceLevel < 0x360000L) {
            return false;
        }
        return CharOperation.equals(this.getTypeName(0), TypeConstants.VAR);
    }

    static class AnnotationCollector
    extends ASTVisitor {
        List<AnnotationContext> annotationContexts;
        Expression typeReference;
        int targetType;
        int info = 0;
        int info2 = 0;
        LocalVariableBinding localVariable;
        Annotation[][] annotationsOnDimensions;
        int dimensions;
        Wildcard currentWildcard;
        RecordComponentBinding recordComponentBinding;

        public AnnotationCollector(TypeParameter typeParameter, int targetType, int typeParameterIndex, List<AnnotationContext> annotationContexts) {
            this.annotationContexts = annotationContexts;
            this.typeReference = typeParameter.type;
            this.targetType = targetType;
            this.info = typeParameterIndex;
        }

        public AnnotationCollector(LocalDeclaration localDeclaration, int targetType, LocalVariableBinding localVariable, List<AnnotationContext> annotationContexts) {
            this.annotationContexts = annotationContexts;
            this.typeReference = localDeclaration.type;
            this.targetType = targetType;
            this.localVariable = localVariable;
        }

        public AnnotationCollector(LocalDeclaration localDeclaration, int targetType, int parameterIndex, List<AnnotationContext> annotationContexts) {
            this.annotationContexts = annotationContexts;
            this.typeReference = localDeclaration.type;
            this.targetType = targetType;
            this.info = parameterIndex;
        }

        public AnnotationCollector(TypeReference typeReference, int targetType, List<AnnotationContext> annotationContexts) {
            this.annotationContexts = annotationContexts;
            this.typeReference = typeReference;
            this.targetType = targetType;
        }

        public AnnotationCollector(Expression typeReference, int targetType, int info, List<AnnotationContext> annotationContexts) {
            this.annotationContexts = annotationContexts;
            this.typeReference = typeReference;
            this.info = info;
            this.targetType = targetType;
        }

        public AnnotationCollector(TypeReference typeReference, int targetType, int info, int typeIndex, List<AnnotationContext> annotationContexts) {
            this.annotationContexts = annotationContexts;
            this.typeReference = typeReference;
            this.info = info;
            this.targetType = targetType;
            this.info2 = typeIndex;
        }

        public AnnotationCollector(TypeReference typeReference, int targetType, int info, List<AnnotationContext> annotationContexts, Annotation[][] annotationsOnDimensions, int dimensions) {
            this.annotationContexts = annotationContexts;
            this.typeReference = typeReference;
            this.info = info;
            this.targetType = targetType;
            this.annotationsOnDimensions = annotationsOnDimensions;
            this.dimensions = dimensions;
        }

        public AnnotationCollector(RecordComponent recordComponent, int targetType, List<AnnotationContext> annotationContexts) {
            this.annotationContexts = annotationContexts;
            this.typeReference = recordComponent.type;
            this.targetType = targetType;
            this.recordComponentBinding = recordComponent.binding;
        }

        private boolean internalVisit(Annotation annotation) {
            AnnotationContext annotationContext = null;
            if (annotation.isRuntimeTypeInvisible()) {
                annotationContext = new AnnotationContext(annotation, this.typeReference, this.targetType, 2);
            } else if (annotation.isRuntimeTypeVisible()) {
                annotationContext = new AnnotationContext(annotation, this.typeReference, this.targetType, 1);
            }
            if (annotationContext != null) {
                annotationContext.wildcard = this.currentWildcard;
                switch (this.targetType) {
                    case 0: 
                    case 1: 
                    case 16: 
                    case 22: 
                    case 23: 
                    case 66: 
                    case 67: 
                    case 68: 
                    case 69: 
                    case 70: {
                        annotationContext.info = this.info;
                        break;
                    }
                    case 64: 
                    case 65: {
                        annotationContext.variableBinding = this.localVariable;
                        break;
                    }
                    case 17: 
                    case 18: 
                    case 71: 
                    case 72: 
                    case 73: 
                    case 74: 
                    case 75: {
                        annotationContext.info2 = this.info2;
                        annotationContext.info = this.info;
                        break;
                    }
                }
                this.annotationContexts.add(annotationContext);
            }
            return true;
        }

        @Override
        public boolean visit(MarkerAnnotation annotation, BlockScope scope) {
            return this.internalVisit(annotation);
        }

        @Override
        public boolean visit(NormalAnnotation annotation, BlockScope scope) {
            return this.internalVisit(annotation);
        }

        @Override
        public boolean visit(SingleMemberAnnotation annotation, BlockScope scope) {
            return this.internalVisit(annotation);
        }

        @Override
        public boolean visit(Wildcard wildcard, BlockScope scope) {
            this.currentWildcard = wildcard;
            return true;
        }

        @Override
        public boolean visit(Argument argument, BlockScope scope) {
            if ((argument.bits & 0x20000000) == 0) {
                return true;
            }
            int i = 0;
            int max = this.localVariable.initializationCount;
            while (i < max) {
                int startPC = this.localVariable.initializationPCs[i << 1];
                int endPC = this.localVariable.initializationPCs[(i << 1) + 1];
                if (startPC != endPC) {
                    return true;
                }
                ++i;
            }
            return false;
        }

        @Override
        public boolean visit(Argument argument, ClassScope scope) {
            if ((argument.bits & 0x20000000) == 0) {
                return true;
            }
            int i = 0;
            int max = this.localVariable.initializationCount;
            while (i < max) {
                int startPC = this.localVariable.initializationPCs[i << 1];
                int endPC = this.localVariable.initializationPCs[(i << 1) + 1];
                if (startPC != endPC) {
                    return true;
                }
                ++i;
            }
            return false;
        }

        @Override
        public boolean visit(LocalDeclaration localDeclaration, BlockScope scope) {
            int i = 0;
            int max = this.localVariable.initializationCount;
            while (i < max) {
                int startPC = this.localVariable.initializationPCs[i << 1];
                int endPC = this.localVariable.initializationPCs[(i << 1) + 1];
                if (startPC != endPC) {
                    return true;
                }
                ++i;
            }
            return false;
        }

        @Override
        public void endVisit(Wildcard wildcard, BlockScope scope) {
            this.currentWildcard = null;
        }
    }

    public static enum AnnotationPosition {
        MAIN_TYPE,
        LEAF_TYPE,
        ANY;

    }
}

