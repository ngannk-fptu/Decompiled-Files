/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.ast;

import java.util.Stack;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ContainerAnnotation;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ModuleDeclaration;
import org.eclipse.jdt.internal.compiler.ast.NameReference;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.RecordComponent;
import org.eclipse.jdt.internal.compiler.ast.Reference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TrueLiteral;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.env.EnumConstantSignature;
import org.eclipse.jdt.internal.compiler.impl.BooleanConstant;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.impl.IrritantSet;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.ElementValuePair;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.RecordComponentBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;

public abstract class Annotation
extends Expression {
    Annotation persistibleAnnotation = this;
    static final MemberValuePair[] NoValuePairs = new MemberValuePair[0];
    static final int[] TYPE_PATH_ELEMENT_ARRAY = new int[2];
    static final int[] TYPE_PATH_INNER_TYPE;
    static final int[] TYPE_PATH_ANNOTATION_ON_WILDCARD_BOUND;
    public int declarationSourceEnd;
    public Binding recipient;
    public TypeReference type;
    protected AnnotationBinding compilerAnnotation = null;

    static {
        int[] nArray = new int[2];
        nArray[0] = 1;
        TYPE_PATH_INNER_TYPE = nArray;
        int[] nArray2 = new int[2];
        nArray2[0] = 2;
        TYPE_PATH_ANNOTATION_ON_WILDCARD_BOUND = nArray2;
    }

    public static int[] getLocations(Expression reference, Annotation annotation) {
        if (reference == null) {
            return null;
        }
        class LocationCollector
        extends ASTVisitor {
            Stack typePathEntries = new Stack();
            Annotation searchedAnnotation;
            boolean continueSearch = true;

            public LocationCollector(Annotation currentAnnotation) {
                this.searchedAnnotation = currentAnnotation;
            }

            private int[] computeNestingDepth(TypeReference typeReference) {
                TypeBinding type = typeReference.resolvedType == null ? null : typeReference.resolvedType.leafComponentType();
                int[] nestingDepths = new int[typeReference.getAnnotatableLevels()];
                if (type != null && type.isNestedType()) {
                    int depth = 0;
                    TypeBinding currentType = type;
                    while (currentType != null) {
                        depth += currentType.isStatic() ? 0 : 1;
                        currentType = currentType.enclosingType();
                    }
                    int counter = nestingDepths.length - 1;
                    while (type != null && counter >= 0) {
                        nestingDepths[counter--] = depth;
                        depth -= type.isStatic() ? 0 : 1;
                        type = type.enclosingType();
                    }
                }
                return nestingDepths;
            }

            private void inspectAnnotations(Annotation[] annotations) {
                int i = 0;
                int length = annotations == null ? 0 : annotations.length;
                while (this.continueSearch && i < length) {
                    if (annotations[i] == this.searchedAnnotation) {
                        this.continueSearch = false;
                        break;
                    }
                    ++i;
                }
            }

            private void inspectArrayDimensions(Annotation[][] annotationsOnDimensions, int dimensions) {
                int i = 0;
                while (this.continueSearch && i < dimensions) {
                    Annotation[] annotations = annotationsOnDimensions == null ? null : annotationsOnDimensions[i];
                    this.inspectAnnotations(annotations);
                    if (!this.continueSearch) {
                        return;
                    }
                    this.typePathEntries.push(TYPE_PATH_ELEMENT_ARRAY);
                    ++i;
                }
            }

            private void inspectTypeArguments(TypeReference[] typeReferences) {
                int i = 0;
                int length = typeReferences == null ? 0 : typeReferences.length;
                while (this.continueSearch && i < length) {
                    int size = this.typePathEntries.size();
                    this.typePathEntries.add(new int[]{3, i});
                    typeReferences[i].traverse((ASTVisitor)this, (BlockScope)null);
                    if (!this.continueSearch) {
                        return;
                    }
                    this.typePathEntries.setSize(size);
                    ++i;
                }
            }

            public boolean visit(TypeReference typeReference, BlockScope scope) {
                if (this.continueSearch) {
                    this.inspectArrayDimensions(typeReference.getAnnotationsOnDimensions(), typeReference.dimensions());
                    if (this.continueSearch) {
                        int[] nestingDepths = this.computeNestingDepth(typeReference);
                        Annotation[][] annotations = typeReference.annotations;
                        TypeReference[][] typeArguments = typeReference.getTypeArguments();
                        int levels = typeReference.getAnnotatableLevels();
                        int size = this.typePathEntries.size();
                        int i = levels - 1;
                        while (this.continueSearch && i >= 0) {
                            this.typePathEntries.setSize(size);
                            int j = 0;
                            int depth = nestingDepths[i];
                            while (j < depth) {
                                this.typePathEntries.add(TYPE_PATH_INNER_TYPE);
                                ++j;
                            }
                            if (annotations != null) {
                                this.inspectAnnotations(annotations[i]);
                            }
                            if (this.continueSearch && typeArguments != null) {
                                this.inspectTypeArguments(typeArguments[i]);
                            }
                            --i;
                        }
                    }
                }
                return false;
            }

            @Override
            public boolean visit(SingleTypeReference typeReference, BlockScope scope) {
                return this.visit((TypeReference)typeReference, scope);
            }

            @Override
            public boolean visit(ArrayTypeReference typeReference, BlockScope scope) {
                return this.visit((TypeReference)typeReference, scope);
            }

            @Override
            public boolean visit(ParameterizedSingleTypeReference typeReference, BlockScope scope) {
                return this.visit((TypeReference)typeReference, scope);
            }

            @Override
            public boolean visit(QualifiedTypeReference typeReference, BlockScope scope) {
                return this.visit((TypeReference)typeReference, scope);
            }

            @Override
            public boolean visit(ArrayQualifiedTypeReference typeReference, BlockScope scope) {
                return this.visit((TypeReference)typeReference, scope);
            }

            @Override
            public boolean visit(ParameterizedQualifiedTypeReference typeReference, BlockScope scope) {
                return this.visit((TypeReference)typeReference, scope);
            }

            @Override
            public boolean visit(Wildcard typeReference, BlockScope scope) {
                TypeReference bound;
                this.visit((TypeReference)typeReference, scope);
                if (this.continueSearch && (bound = typeReference.bound) != null) {
                    int size = this.typePathEntries.size();
                    this.typePathEntries.push(TYPE_PATH_ANNOTATION_ON_WILDCARD_BOUND);
                    bound.traverse((ASTVisitor)this, scope);
                    if (this.continueSearch) {
                        this.typePathEntries.setSize(size);
                    }
                }
                return false;
            }

            @Override
            public boolean visit(ArrayAllocationExpression allocationExpression, BlockScope scope) {
                if (this.continueSearch) {
                    this.inspectArrayDimensions(allocationExpression.getAnnotationsOnDimensions(), allocationExpression.dimensions.length);
                    if (this.continueSearch) {
                        allocationExpression.type.traverse((ASTVisitor)this, scope);
                    }
                    if (this.continueSearch) {
                        throw new IllegalStateException();
                    }
                }
                return false;
            }

            public String toString() {
                StringBuffer buffer = new StringBuffer();
                buffer.append("search location for ").append(this.searchedAnnotation).append("\ncurrent type_path entries : ");
                int i = 0;
                int maxi = this.typePathEntries.size();
                while (i < maxi) {
                    int[] typePathEntry = (int[])this.typePathEntries.get(i);
                    buffer.append('(').append(typePathEntry[0]).append(',').append(typePathEntry[1]).append(')');
                    ++i;
                }
                return String.valueOf(buffer);
            }
        }
        LocationCollector collector = new LocationCollector(annotation);
        reference.traverse((ASTVisitor)collector, (BlockScope)null);
        if (collector.typePathEntries.isEmpty()) {
            return null;
        }
        int size = collector.typePathEntries.size();
        int[] result = new int[size * 2];
        int offset = 0;
        int i = 0;
        while (i < size) {
            int[] pathElement = (int[])collector.typePathEntries.get(i);
            result[offset++] = pathElement[0];
            result[offset++] = pathElement[1];
            ++i;
        }
        return result;
    }

    public static long getRetentionPolicy(char[] policyName) {
        if (policyName == null || policyName.length == 0) {
            return 0L;
        }
        switch (policyName[0]) {
            case 'C': {
                if (!CharOperation.equals(policyName, TypeConstants.UPPER_CLASS)) break;
                return 0x200000000000L;
            }
            case 'S': {
                if (!CharOperation.equals(policyName, TypeConstants.UPPER_SOURCE)) break;
                return 0x100000000000L;
            }
            case 'R': {
                if (!CharOperation.equals(policyName, TypeConstants.UPPER_RUNTIME)) break;
                return 0x300000000000L;
            }
        }
        return 0L;
    }

    public static long getTargetElementType(char[] elementName) {
        if (elementName == null || elementName.length == 0) {
            return 0L;
        }
        switch (elementName[0]) {
            case 'A': {
                if (!CharOperation.equals(elementName, TypeConstants.UPPER_ANNOTATION_TYPE)) break;
                return 0x40000000000L;
            }
            case 'C': {
                if (!CharOperation.equals(elementName, TypeConstants.UPPER_CONSTRUCTOR)) break;
                return 0x10000000000L;
            }
            case 'F': {
                if (!CharOperation.equals(elementName, TypeConstants.UPPER_FIELD)) break;
                return 0x2000000000L;
            }
            case 'L': {
                if (!CharOperation.equals(elementName, TypeConstants.UPPER_LOCAL_VARIABLE)) break;
                return 0x20000000000L;
            }
            case 'M': {
                if (CharOperation.equals(elementName, TypeConstants.UPPER_METHOD)) {
                    return 0x4000000000L;
                }
                if (!CharOperation.equals(elementName, TypeConstants.UPPER_MODULE)) break;
                return 0x2000000000000000L;
            }
            case 'P': {
                if (CharOperation.equals(elementName, TypeConstants.UPPER_PARAMETER)) {
                    return 0x8000000000L;
                }
                if (!CharOperation.equals(elementName, TypeConstants.UPPER_PACKAGE)) break;
                return 0x80000000000L;
            }
            case 'R': {
                if (!CharOperation.equals(elementName, TypeConstants.UPPER_RECORD_COMPONENT)) break;
                return 0x40000000L;
            }
            case 'T': {
                if (CharOperation.equals(elementName, TypeConstants.TYPE)) {
                    return 0x1000000000L;
                }
                if (CharOperation.equals(elementName, TypeConstants.TYPE_USE_TARGET)) {
                    return 0x20000000000000L;
                }
                if (!CharOperation.equals(elementName, TypeConstants.TYPE_PARAMETER_TARGET)) break;
                return 0x40000000000000L;
            }
        }
        return 0L;
    }

    public ElementValuePair[] computeElementValuePairs() {
        return Binding.NO_ELEMENT_VALUE_PAIRS;
    }

    private long detectStandardAnnotation(Scope scope, ReferenceBinding annotationType, MemberValuePair valueAttribute) {
        long tagBits = 0L;
        block0 : switch (annotationType.id) {
            case 48: {
                FieldBinding field;
                if (valueAttribute == null) break;
                Expression expr = valueAttribute.value;
                if ((expr.bits & 3) != 1 || !(expr instanceof Reference) || (field = ((Reference)expr).fieldBinding()) == null || field.declaringClass.id != 51) break;
                tagBits |= Annotation.getRetentionPolicy(field.name);
                break;
            }
            case 50: {
                FieldBinding field;
                tagBits |= 0x800000000L;
                if (valueAttribute == null) break;
                Expression expr = valueAttribute.value;
                if (expr instanceof ArrayInitializer) {
                    ArrayInitializer initializer = (ArrayInitializer)expr;
                    Expression[] expressions = initializer.expressions;
                    if (expressions == null) break;
                    int i = 0;
                    int length = expressions.length;
                    while (i < length) {
                        FieldBinding field2;
                        Expression initExpr = expressions[i];
                        if ((initExpr.bits & 3) == 1 && (field2 = ((Reference)initExpr).fieldBinding()) != null && field2.declaringClass.id == 52) {
                            long element = Annotation.getTargetElementType(field2.name);
                            if ((tagBits & element) != 0L) {
                                scope.problemReporter().duplicateTargetInTargetAnnotation(annotationType, (NameReference)initExpr);
                            } else {
                                tagBits |= element;
                            }
                        }
                        ++i;
                    }
                    break;
                }
                if ((expr.bits & 3) != 1 || (field = ((Reference)expr).fieldBinding()) == null || field.declaringClass.id != 52) break;
                tagBits |= Annotation.getTargetElementType(field.name);
                break;
            }
            case 94: {
                tagBits |= 0x180000000L;
                MemberValuePair[] memberValuePairArray = this.memberValuePairs();
                int n = memberValuePairArray.length;
                int n2 = 0;
                while (n2 < n) {
                    MemberValuePair memberValuePair = memberValuePairArray[n2];
                    if (CharOperation.equals(memberValuePair.name, TypeConstants.ESSENTIAL_API) && memberValuePair.value instanceof TrueLiteral) {
                        tagBits |= 0x400L;
                    }
                    ++n2;
                }
                break;
            }
            case 44: {
                tagBits |= 0x400000000000L;
                if (scope.compilerOptions().complianceLevel < 0x350000L) break;
                MemberValuePair[] memberValuePairArray = this.memberValuePairs();
                int n = memberValuePairArray.length;
                int n3 = 0;
                while (n3 < n) {
                    MemberValuePair memberValuePair = memberValuePairArray[n3];
                    if (CharOperation.equals(memberValuePair.name, TypeConstants.FOR_REMOVAL)) {
                        if (!(memberValuePair.value instanceof TrueLiteral)) break block0;
                        tagBits |= 0x4000000000000000L;
                        break block0;
                    }
                    ++n3;
                }
                break;
            }
            case 45: {
                tagBits |= 0x800000000000L;
                break;
            }
            case 46: {
                tagBits |= 0x1000000000000L;
                break;
            }
            case 47: {
                tagBits |= 0x2000000000000L;
                break;
            }
            case 77: {
                tagBits |= 0x800000000000000L;
                break;
            }
            case 90: {
                tagBits |= 0x1000000000000000L;
                break;
            }
            case 49: {
                tagBits |= 0x4000000000000L;
                break;
            }
            case 60: {
                tagBits |= 0x8000000000000L;
                break;
            }
            case 61: {
                tagBits |= 0x10000000000000L;
            }
        }
        if (annotationType.hasNullBit(64)) {
            tagBits |= 0x80000000000000L;
        } else if (annotationType.hasNullBit(32)) {
            tagBits |= 0x100000000000000L;
        } else if (annotationType.hasNullBit(128)) {
            tagBits |= this.determineNonNullByDefaultTagBits(annotationType, valueAttribute);
        }
        return tagBits;
    }

    private long determineNonNullByDefaultTagBits(ReferenceBinding annotationType, MemberValuePair valueAttribute) {
        long tagBits = 0L;
        Object value = null;
        if (valueAttribute != null) {
            if (valueAttribute.compilerElementPair != null) {
                value = valueAttribute.compilerElementPair.value;
            }
        } else {
            MethodBinding[] methods = annotationType.methods();
            if (methods != null && methods.length == 1) {
                value = methods[0].getDefaultValue();
            } else {
                tagBits |= 0x38L;
            }
        }
        if (value instanceof BooleanConstant) {
            tagBits |= (long)(((BooleanConstant)value).booleanValue() ? 56 : 2);
        } else if (value != null) {
            tagBits |= (long)Annotation.nullLocationBitsFromAnnotationValue(value);
        } else {
            int result = BinaryTypeBinding.evaluateTypeQualifierDefault(annotationType);
            if (result != 0) {
                return result;
            }
        }
        return tagBits;
    }

    public static int nullLocationBitsFromAnnotationValue(Object value) {
        if (value instanceof Object[]) {
            if (((Object[])value).length == 0) {
                return 2;
            }
            int bits = 0;
            Object[] objectArray = (Object[])value;
            int n = objectArray.length;
            int n2 = 0;
            while (n2 < n) {
                Object single = objectArray[n2];
                bits |= Annotation.evaluateDefaultNullnessLocation(single);
                ++n2;
            }
            return bits;
        }
        return Annotation.evaluateDefaultNullnessLocation(value);
    }

    private static int evaluateDefaultNullnessLocation(Object value) {
        char[] name = null;
        if (value instanceof FieldBinding) {
            name = ((FieldBinding)value).name;
        } else if (value instanceof EnumConstantSignature) {
            name = ((EnumConstantSignature)value).getEnumConstantName();
        } else if (value instanceof ElementValuePair.UnresolvedEnumConstant) {
            name = ((ElementValuePair.UnresolvedEnumConstant)value).getEnumConstantName();
        } else if (value instanceof BooleanConstant) {
            return ((BooleanConstant)value).booleanValue() ? 56 : 2;
        }
        if (name != null) {
            switch (name.length) {
                case 5: {
                    if (!CharOperation.equals(name, TypeConstants.DEFAULT_LOCATION__FIELD)) break;
                    return 32;
                }
                case 9: {
                    if (!CharOperation.equals(name, TypeConstants.DEFAULT_LOCATION__PARAMETER)) break;
                    return 8;
                }
                case 10: {
                    if (!CharOperation.equals(name, TypeConstants.DEFAULT_LOCATION__TYPE_BOUND)) break;
                    return 256;
                }
                case 11: {
                    if (!CharOperation.equals(name, TypeConstants.DEFAULT_LOCATION__RETURN_TYPE)) break;
                    return 16;
                }
                case 13: {
                    if (!CharOperation.equals(name, TypeConstants.DEFAULT_LOCATION__TYPE_ARGUMENT)) break;
                    return 64;
                }
                case 14: {
                    if (CharOperation.equals(name, TypeConstants.DEFAULT_LOCATION__TYPE_PARAMETER)) {
                        return 128;
                    }
                    if (!CharOperation.equals(name, TypeConstants.DEFAULT_LOCATION__ARRAY_CONTENTS)) break;
                    return 512;
                }
            }
        }
        return 0;
    }

    public static int nullLocationBitsFromElementTypeAnnotationValue(Object value) {
        if (value instanceof Object[]) {
            if (((Object[])value).length == 0) {
                return 2;
            }
            int bits = 0;
            Object[] objectArray = (Object[])value;
            int n = objectArray.length;
            int n2 = 0;
            while (n2 < n) {
                Object single = objectArray[n2];
                bits |= Annotation.evaluateElementTypeNullnessLocation(single);
                ++n2;
            }
            return bits;
        }
        return Annotation.evaluateElementTypeNullnessLocation(value);
    }

    private static int evaluateElementTypeNullnessLocation(Object value) {
        char[] name = null;
        if (value instanceof FieldBinding) {
            name = ((FieldBinding)value).name;
        } else if (value instanceof EnumConstantSignature) {
            name = ((EnumConstantSignature)value).getEnumConstantName();
        } else if (value instanceof ElementValuePair.UnresolvedEnumConstant) {
            name = ((ElementValuePair.UnresolvedEnumConstant)value).getEnumConstantName();
        }
        if (name != null) {
            switch (name.length) {
                case 5: {
                    if (!CharOperation.equals(name, TypeConstants.UPPER_FIELD)) break;
                    return 32;
                }
                case 6: {
                    if (!CharOperation.equals(name, TypeConstants.UPPER_METHOD)) break;
                    return 16;
                }
                case 9: {
                    if (!CharOperation.equals(name, TypeConstants.UPPER_PARAMETER)) break;
                    return 8;
                }
            }
        }
        return 0;
    }

    static String getRetentionName(long tagBits) {
        if ((tagBits & 0x300000000000L) == 0x300000000000L) {
            return new String(UPPER_RUNTIME);
        }
        if ((tagBits & 0x100000000000L) != 0L) {
            return new String(UPPER_SOURCE);
        }
        return new String(TypeConstants.UPPER_CLASS);
    }

    private static long getAnnotationRetention(ReferenceBinding binding) {
        long retention = binding.getAnnotationTagBits() & 0x300000000000L;
        return retention != 0L ? retention : 0x200000000000L;
    }

    public void checkRepeatableMetaAnnotation(BlockScope scope) {
        ReferenceBinding repeatableAnnotationType = (ReferenceBinding)this.recipient;
        MemberValuePair[] valuePairs = this.memberValuePairs();
        if (valuePairs == null || valuePairs.length != 1) {
            return;
        }
        Object value = valuePairs[0].compilerElementPair.value;
        if (!(value instanceof ReferenceBinding)) {
            return;
        }
        ReferenceBinding containerAnnotationType = (ReferenceBinding)value;
        if (!containerAnnotationType.isAnnotationType()) {
            return;
        }
        repeatableAnnotationType.setContainerAnnotationType(containerAnnotationType);
        Annotation.checkContainerAnnotationType(valuePairs[0], scope, containerAnnotationType, repeatableAnnotationType, false);
    }

    public static void checkContainerAnnotationType(ASTNode culpritNode, BlockScope scope, ReferenceBinding containerAnnotationType, ReferenceBinding repeatableAnnotationType, boolean useSite) {
        MethodBinding[] annotationMethods = containerAnnotationType.methods();
        boolean sawValue = false;
        int i = 0;
        int length = annotationMethods.length;
        while (i < length) {
            MethodBinding method = annotationMethods[i];
            if (CharOperation.equals(method.selector, TypeConstants.VALUE)) {
                ArrayBinding array;
                sawValue = true;
                if (!method.returnType.isArrayType() || method.returnType.dimensions() != 1 || !TypeBinding.equalsEquals((array = (ArrayBinding)method.returnType).elementsType(), repeatableAnnotationType)) {
                    repeatableAnnotationType.tagAsHavingDefectiveContainerType();
                    scope.problemReporter().containerAnnotationTypeHasWrongValueType(culpritNode, containerAnnotationType, repeatableAnnotationType, method.returnType);
                }
            } else if ((method.modifiers & 0x20000) == 0) {
                repeatableAnnotationType.tagAsHavingDefectiveContainerType();
                scope.problemReporter().containerAnnotationTypeHasNonDefaultMembers(culpritNode, containerAnnotationType, method.selector);
            }
            ++i;
        }
        if (!sawValue) {
            repeatableAnnotationType.tagAsHavingDefectiveContainerType();
            scope.problemReporter().containerAnnotationTypeMustHaveValue(culpritNode, containerAnnotationType);
        }
        if (useSite) {
            Annotation.checkContainingAnnotationTargetAtUse((Annotation)culpritNode, scope, containerAnnotationType, repeatableAnnotationType);
        } else {
            Annotation.checkContainerAnnotationTypeTarget(culpritNode, scope, containerAnnotationType, repeatableAnnotationType);
        }
        long annotationTypeBits = Annotation.getAnnotationRetention(repeatableAnnotationType);
        long containerTypeBits = Annotation.getAnnotationRetention(containerAnnotationType);
        if (containerTypeBits < annotationTypeBits) {
            repeatableAnnotationType.tagAsHavingDefectiveContainerType();
            scope.problemReporter().containerAnnotationTypeHasShorterRetention(culpritNode, repeatableAnnotationType, Annotation.getRetentionName(annotationTypeBits), containerAnnotationType, Annotation.getRetentionName(containerTypeBits));
        }
        if ((repeatableAnnotationType.getAnnotationTagBits() & 0x800000000000L) != 0L && (containerAnnotationType.getAnnotationTagBits() & 0x800000000000L) == 0L) {
            repeatableAnnotationType.tagAsHavingDefectiveContainerType();
            scope.problemReporter().repeatableAnnotationTypeIsDocumented(culpritNode, repeatableAnnotationType, containerAnnotationType);
        }
        if ((repeatableAnnotationType.getAnnotationTagBits() & 0x1000000000000L) != 0L && (containerAnnotationType.getAnnotationTagBits() & 0x1000000000000L) == 0L) {
            repeatableAnnotationType.tagAsHavingDefectiveContainerType();
            scope.problemReporter().repeatableAnnotationTypeIsInherited(culpritNode, repeatableAnnotationType, containerAnnotationType);
        }
    }

    private static void checkContainerAnnotationTypeTarget(ASTNode culpritNode, Scope scope, ReferenceBinding containerType, ReferenceBinding repeatableAnnotationType) {
        long targets;
        long containerAnnotationTypeTargets;
        long containerAnnotationTypeTypeTagBits;
        long tagBits = repeatableAnnotationType.getAnnotationTagBits();
        if ((tagBits & 0x20600FF840000000L) == 0L) {
            tagBits = 0xFF000000000L;
        }
        if (((containerAnnotationTypeTypeTagBits = containerType.getAnnotationTagBits()) & 0x20600FF840000000L) == 0L) {
            containerAnnotationTypeTypeTagBits = 0xFF000000000L;
        }
        if (((containerAnnotationTypeTargets = containerAnnotationTypeTypeTagBits & 0x20600FF840000000L) & ((targets = tagBits & 0x20600FF840000000L) ^ 0xFFFFFFFFFFFFFFFFL)) != 0L) {
            class MissingTargetBuilder {
                StringBuffer targetBuffer = new StringBuffer();
                private final /* synthetic */ long val$containerAnnotationTypeTargets;
                private final /* synthetic */ long val$targets;

                MissingTargetBuilder(long l, long l2) {
                    this.val$containerAnnotationTypeTargets = l;
                    this.val$targets = l2;
                }

                void check(long targetMask, char[] targetName) {
                    if ((this.val$containerAnnotationTypeTargets & targetMask & (this.val$targets ^ 0xFFFFFFFFFFFFFFFFL)) != 0L) {
                        if (targetMask == 0x1000000000L && (this.val$targets & 0x20000000000000L) != 0L) {
                            return;
                        }
                        this.add(targetName);
                    }
                }

                void checkAnnotationType(char[] targetName) {
                    if ((this.val$containerAnnotationTypeTargets & 0x40000000000L) != 0L && (this.val$targets & 0x41000000000L) == 0L) {
                        this.add(targetName);
                    }
                }

                private void add(char[] targetName) {
                    if (this.targetBuffer.length() != 0) {
                        this.targetBuffer.append(", ");
                    }
                    this.targetBuffer.append(targetName);
                }

                public String toString() {
                    return this.targetBuffer.toString();
                }

                public boolean hasError() {
                    return this.targetBuffer.length() != 0;
                }
            }
            MissingTargetBuilder builder = new MissingTargetBuilder(containerAnnotationTypeTargets, targets);
            builder.check(0x1000000000L, TypeConstants.TYPE);
            builder.check(0x2000000000L, TypeConstants.UPPER_FIELD);
            builder.check(0x4000000000L, TypeConstants.UPPER_METHOD);
            builder.check(0x8000000000L, TypeConstants.UPPER_PARAMETER);
            builder.check(0x10000000000L, TypeConstants.UPPER_CONSTRUCTOR);
            builder.check(0x20000000000L, TypeConstants.UPPER_LOCAL_VARIABLE);
            builder.checkAnnotationType(TypeConstants.UPPER_ANNOTATION_TYPE);
            builder.check(0x80000000000L, TypeConstants.UPPER_PACKAGE);
            builder.check(0x40000000000000L, TypeConstants.TYPE_PARAMETER_TARGET);
            builder.check(0x20000000000000L, TypeConstants.TYPE_USE_TARGET);
            builder.check(0x2000000000000000L, TypeConstants.UPPER_MODULE);
            builder.check(0x40000000L, TypeConstants.UPPER_RECORD_COMPONENT);
            if (builder.hasError()) {
                repeatableAnnotationType.tagAsHavingDefectiveContainerType();
                scope.problemReporter().repeatableAnnotationTypeTargetMismatch(culpritNode, repeatableAnnotationType, containerType, builder.toString());
            }
        }
    }

    public static void checkContainingAnnotationTargetAtUse(Annotation repeatingAnnotation, BlockScope scope, TypeBinding containerAnnotationType, TypeBinding repeatingAnnotationType) {
        if (!repeatingAnnotationType.isValidBinding()) {
            return;
        }
        if (Annotation.isAnnotationTargetAllowed(repeatingAnnotation, scope, containerAnnotationType, repeatingAnnotation.recipient.kind()) != AnnotationTargetAllowed.YES) {
            scope.problemReporter().disallowedTargetForContainerAnnotation(repeatingAnnotation, containerAnnotationType);
        }
    }

    public AnnotationBinding getCompilerAnnotation() {
        return this.compilerAnnotation;
    }

    public boolean isRuntimeInvisible() {
        TypeBinding annotationBinding = this.resolvedType;
        if (annotationBinding == null) {
            return false;
        }
        long metaTagBits = annotationBinding.getAnnotationTagBits();
        if ((metaTagBits & 0x60000000000000L) != 0L && (metaTagBits & 0xFF000000000L) == 0L) {
            return false;
        }
        if ((metaTagBits & 0x300000000000L) == 0L) {
            return true;
        }
        return (metaTagBits & 0x300000000000L) == 0x200000000000L;
    }

    public boolean isRuntimeTypeInvisible() {
        TypeBinding annotationBinding = this.resolvedType;
        if (annotationBinding == null) {
            return false;
        }
        long metaTagBits = annotationBinding.getAnnotationTagBits();
        if ((metaTagBits & 0x20600FF840000000L) != 0L && (metaTagBits & 0x60000000000000L) == 0L) {
            return false;
        }
        if ((metaTagBits & 0x300000000000L) == 0L) {
            return true;
        }
        return (metaTagBits & 0x300000000000L) == 0x200000000000L;
    }

    public boolean isRuntimeTypeVisible() {
        TypeBinding annotationBinding = this.resolvedType;
        if (annotationBinding == null) {
            return false;
        }
        long metaTagBits = annotationBinding.getAnnotationTagBits();
        if ((metaTagBits & 0x20600FF840000000L) != 0L && (metaTagBits & 0x60000000000000L) == 0L) {
            return false;
        }
        if ((metaTagBits & 0x300000000000L) == 0L) {
            return false;
        }
        return (metaTagBits & 0x300000000000L) == 0x300000000000L;
    }

    public boolean isRuntimeVisible() {
        TypeBinding annotationBinding = this.resolvedType;
        if (annotationBinding == null) {
            return false;
        }
        long metaTagBits = annotationBinding.getAnnotationTagBits();
        if ((metaTagBits & 0x60000000000000L) != 0L && (metaTagBits & 0xFF000000000L) == 0L) {
            return false;
        }
        if ((metaTagBits & 0x300000000000L) == 0L) {
            return false;
        }
        return (metaTagBits & 0x300000000000L) == 0x300000000000L;
    }

    public abstract MemberValuePair[] memberValuePairs();

    @Override
    public StringBuffer printExpression(int indent, StringBuffer output) {
        output.append('@');
        this.type.printExpression(0, output);
        return output;
    }

    public void recordSuppressWarnings(Scope scope, int startSuppresss, int endSuppress, boolean isSuppressingWarnings) {
        IrritantSet suppressWarningIrritants = null;
        MemberValuePair[] pairs = this.memberValuePairs();
        int i = 0;
        int length = pairs.length;
        while (i < length) {
            MemberValuePair pair = pairs[i];
            if (CharOperation.equals(pair.name, TypeConstants.VALUE)) {
                Expression value = pair.value;
                if (value instanceof ArrayInitializer) {
                    ArrayInitializer initializer = (ArrayInitializer)value;
                    Expression[] inits = initializer.expressions;
                    if (inits == null) break;
                    int j = 0;
                    int initsLength = inits.length;
                    while (j < initsLength) {
                        Constant cst = inits[j].constant;
                        if (cst != Constant.NotAConstant && cst.typeID() == 11) {
                            IrritantSet irritants = CompilerOptions.warningTokenToIrritants(cst.stringValue());
                            if (irritants != null) {
                                if (suppressWarningIrritants == null) {
                                    suppressWarningIrritants = new IrritantSet(irritants);
                                } else if (suppressWarningIrritants.set(irritants) == null) {
                                    scope.problemReporter().unusedWarningToken(inits[j]);
                                }
                            } else {
                                scope.problemReporter().unhandledWarningToken(inits[j]);
                            }
                        }
                        ++j;
                    }
                    break;
                }
                Constant cst = value.constant;
                if (cst == Constant.NotAConstant || cst.typeID() != 11) break;
                IrritantSet irritants = CompilerOptions.warningTokenToIrritants(cst.stringValue());
                if (irritants != null) {
                    suppressWarningIrritants = new IrritantSet(irritants);
                    break;
                }
                scope.problemReporter().unhandledWarningToken(value);
                break;
            }
            ++i;
        }
        if (isSuppressingWarnings && suppressWarningIrritants != null) {
            scope.referenceCompilationUnit().recordSuppressWarnings(suppressWarningIrritants, this, startSuppresss, endSuppress, scope.referenceContext());
        }
    }

    @Override
    public TypeBinding resolveType(BlockScope scope) {
        MemberValuePair[] pairs;
        TypeBinding typeBinding;
        if (this.compilerAnnotation != null) {
            return this.resolvedType;
        }
        this.constant = Constant.NotAConstant;
        if (this.resolvedType == null) {
            typeBinding = this.type.resolveType(scope);
            if (typeBinding == null) {
                this.resolvedType = new ProblemReferenceBinding(this.type.getTypeName(), null, 1);
                return null;
            }
            this.resolvedType = typeBinding;
        } else {
            typeBinding = this.resolvedType;
        }
        if (!typeBinding.isAnnotationType() && typeBinding.isValidBinding()) {
            scope.problemReporter().notAnnotationType(typeBinding, this.type);
            return null;
        }
        ReferenceBinding annotationType = (ReferenceBinding)this.resolvedType;
        MethodBinding[] methods = annotationType.methods();
        MemberValuePair[] originalValuePairs = this.memberValuePairs();
        MemberValuePair valueAttribute = null;
        int pairsLength = originalValuePairs.length;
        if (pairsLength > 0) {
            pairs = new MemberValuePair[pairsLength];
            System.arraycopy(originalValuePairs, 0, pairs, 0, pairsLength);
        } else {
            pairs = originalValuePairs;
        }
        int i = 0;
        int requiredLength = methods.length;
        while (i < requiredLength) {
            block43: {
                MethodBinding method = methods[i];
                char[] selector = method.selector;
                boolean foundValue = false;
                int j = 0;
                while (j < pairsLength) {
                    char[] name;
                    MemberValuePair pair = pairs[j];
                    if (pair != null && CharOperation.equals(name = pair.name, selector)) {
                        if (valueAttribute == null && CharOperation.equals(name, TypeConstants.VALUE)) {
                            valueAttribute = pair;
                        }
                        pair.binding = method;
                        pair.resolveTypeExpecting(scope, method.returnType);
                        pairs[j] = null;
                        foundValue = true;
                        boolean foundDuplicate = false;
                        int k = j + 1;
                        while (k < pairsLength) {
                            MemberValuePair otherPair = pairs[k];
                            if (otherPair != null && CharOperation.equals(otherPair.name, selector)) {
                                foundDuplicate = true;
                                scope.problemReporter().duplicateAnnotationValue(annotationType, otherPair);
                                otherPair.binding = method;
                                otherPair.resolveTypeExpecting(scope, method.returnType);
                                pairs[k] = null;
                            }
                            ++k;
                        }
                        if (foundDuplicate) {
                            scope.problemReporter().duplicateAnnotationValue(annotationType, pair);
                            break block43;
                        }
                    }
                    ++j;
                }
                if (!foundValue && (method.modifiers & 0x20000) == 0 && (this.bits & 0x20) == 0 && annotationType.isValidBinding()) {
                    scope.problemReporter().missingValueForAnnotationMember(this, selector);
                }
            }
            ++i;
        }
        i = 0;
        while (i < pairsLength) {
            if (pairs[i] != null) {
                if (annotationType.isValidBinding()) {
                    scope.problemReporter().undefinedAnnotationValue(annotationType, pairs[i]);
                }
                pairs[i].resolveTypeExpecting(scope, null);
            }
            ++i;
        }
        this.compilerAnnotation = scope.environment().createAnnotation((ReferenceBinding)this.resolvedType, this.computeElementValuePairs());
        long tagBits = this.detectStandardAnnotation(scope, annotationType, valueAttribute);
        int defaultNullness = (int)(tagBits & 0x3FAL);
        CompilerOptions compilerOptions = scope.compilerOptions();
        if (((tagBits &= 0xFFFFFFFFFFFFFC05L) & 0x400000000000L) != 0L && compilerOptions.complianceLevel >= 0x350000L && !compilerOptions.storeAnnotations) {
            this.recipient.setAnnotations(new AnnotationBinding[]{this.compilerAnnotation}, true);
        }
        scope.referenceCompilationUnit().recordSuppressWarnings(IrritantSet.NLS, null, this.sourceStart, this.declarationSourceEnd, scope.referenceContext());
        if (this.recipient != null) {
            int kind = this.recipient.kind();
            if (tagBits != 0L || defaultNullness != 0) {
                switch (kind) {
                    case 64: {
                        SourceModuleBinding module = (SourceModuleBinding)this.recipient;
                        module.tagBits |= tagBits;
                        if ((tagBits & 0x4000000000000L) != 0L) {
                            ModuleDeclaration moduleDeclaration = module.scope.referenceContext.moduleDeclaration;
                            this.recordSuppressWarnings(scope, 0, moduleDeclaration.declarationSourceEnd, compilerOptions.suppressWarnings);
                        }
                        module.defaultNullness |= defaultNullness;
                        break;
                    }
                    case 16: {
                        ((PackageBinding)this.recipient).tagBits |= tagBits;
                        break;
                    }
                    case 4: 
                    case 2052: {
                        SourceTypeBinding sourceType = (SourceTypeBinding)this.recipient;
                        if ((tagBits & 0x1000000000000000L) == 0L || sourceType.isAnnotationType()) {
                            sourceType.tagBits |= tagBits;
                        }
                        if ((tagBits & 0x4000000000000L) != 0L) {
                            TypeDeclaration typeDeclaration = sourceType.scope.referenceContext;
                            int start = scope.referenceCompilationUnit().types[0] == typeDeclaration ? 0 : typeDeclaration.declarationSourceStart;
                            this.recordSuppressWarnings(scope, start, typeDeclaration.declarationSourceEnd, compilerOptions.suppressWarnings);
                        }
                        sourceType.defaultNullness |= defaultNullness;
                        break;
                    }
                    case 8: {
                        long nullBits;
                        SourceTypeBinding sourceType;
                        MethodBinding sourceMethod = (MethodBinding)this.recipient;
                        sourceMethod.tagBits |= tagBits;
                        if ((tagBits & 0x4000000000000L) != 0L) {
                            sourceType = (SourceTypeBinding)sourceMethod.declaringClass;
                            AbstractMethodDeclaration methodDeclaration = sourceType.scope.referenceContext.declarationOf(sourceMethod);
                            this.recordSuppressWarnings(scope, methodDeclaration.declarationSourceStart, methodDeclaration.declarationSourceEnd, compilerOptions.suppressWarnings);
                        }
                        if ((nullBits = sourceMethod.tagBits & 0x180000000000000L) == 0x180000000000000L) {
                            scope.problemReporter().contradictoryNullAnnotations(this);
                            sourceMethod.tagBits &= 0xFE7FFFFFFFFFFFFFL;
                        }
                        if (nullBits != 0L && sourceMethod.isConstructor()) {
                            if (compilerOptions.sourceLevel >= 0x340000L) {
                                scope.problemReporter().nullAnnotationUnsupportedLocation(this);
                            }
                            sourceMethod.tagBits &= 0xFE7FFFFFFFFFFFFFL;
                        }
                        sourceMethod.defaultNullness |= defaultNullness;
                        break;
                    }
                    case 131072: {
                        RecordComponentBinding sourceRecordComponent = (RecordComponentBinding)this.recipient;
                        sourceRecordComponent.tagBits |= tagBits;
                        if ((tagBits & 0x4000000000000L) == 0L) break;
                        RecordComponent recordComponent = sourceRecordComponent.sourceRecordComponent();
                        this.recordSuppressWarnings(scope, recordComponent.declarationSourceStart, recordComponent.declarationSourceEnd, compilerOptions.suppressWarnings);
                        break;
                    }
                    case 1: {
                        FieldDeclaration fieldDeclaration;
                        SourceTypeBinding sourceType;
                        FieldBinding sourceField = (FieldBinding)this.recipient;
                        sourceField.tagBits |= tagBits;
                        if ((tagBits & 0x4000000000000L) != 0L) {
                            sourceType = (SourceTypeBinding)sourceField.declaringClass;
                            fieldDeclaration = sourceType.scope.referenceContext.declarationOf(sourceField);
                            this.recordSuppressWarnings(scope, fieldDeclaration.declarationSourceStart, fieldDeclaration.declarationSourceEnd, compilerOptions.suppressWarnings);
                        }
                        if (defaultNullness != 0) {
                            sourceType = (SourceTypeBinding)sourceField.declaringClass;
                            fieldDeclaration = sourceType.scope.referenceContext.declarationOf(sourceField);
                            Binding target = scope.parent.checkRedundantDefaultNullness(defaultNullness | scope.localNonNullByDefaultValue(fieldDeclaration.sourceStart), fieldDeclaration.sourceStart);
                            scope.recordNonNullByDefault(fieldDeclaration.binding, defaultNullness, this, fieldDeclaration.declarationSourceStart, fieldDeclaration.declarationSourceEnd);
                            if (target != null) {
                                scope.problemReporter().nullDefaultAnnotationIsRedundant(fieldDeclaration, new Annotation[]{this}, target);
                            }
                        }
                        if ((sourceField.tagBits & 0x180000000000000L) != 0x180000000000000L) break;
                        scope.problemReporter().contradictoryNullAnnotations(this);
                        sourceField.tagBits &= 0xFE7FFFFFFFFFFFFFL;
                        break;
                    }
                    case 2: {
                        LocalVariableBinding variable = (LocalVariableBinding)this.recipient;
                        variable.tagBits |= tagBits;
                        if ((variable.tagBits & 0x180000000000000L) == 0x180000000000000L) {
                            scope.problemReporter().contradictoryNullAnnotations(this);
                            variable.tagBits &= 0xFE7FFFFFFFFFFFFFL;
                        }
                        if ((tagBits & 0x4000000000000L) == 0L) break;
                        LocalDeclaration localDeclaration = variable.declaration;
                        this.recordSuppressWarnings(scope, localDeclaration.declarationSourceStart, localDeclaration.declarationSourceEnd, compilerOptions.suppressWarnings);
                    }
                }
            }
            if (kind == 4) {
                SourceTypeBinding sourceType = (SourceTypeBinding)this.recipient;
                if (CharOperation.equals(sourceType.sourceName, TypeConstants.PACKAGE_INFO_NAME)) {
                    kind = 16;
                }
            }
            Annotation.checkAnnotationTarget(this, scope, annotationType, kind, this.recipient, tagBits & 0x180000000000000L);
        }
        return this.resolvedType;
    }

    public long handleNonNullByDefault(BlockScope scope) {
        TypeBinding typeBinding = this.resolvedType;
        if (typeBinding == null) {
            typeBinding = this.type.resolveType(scope);
            if (typeBinding == null) {
                return 0L;
            }
            this.resolvedType = typeBinding;
        }
        if (!typeBinding.isAnnotationType()) {
            return 0L;
        }
        ReferenceBinding annotationType = (ReferenceBinding)typeBinding;
        if (!annotationType.hasNullBit(128)) {
            return 0L;
        }
        MethodBinding[] methods = annotationType.methods();
        MemberValuePair[] pairs = this.memberValuePairs();
        MemberValuePair valueAttribute = null;
        int pairsLength = pairs.length;
        int i = 0;
        int requiredLength = methods.length;
        while (i < requiredLength) {
            MethodBinding method = methods[i];
            char[] selector = method.selector;
            int j = 0;
            while (j < pairsLength) {
                char[] name;
                MemberValuePair pair = pairs[j];
                if (pair != null && CharOperation.equals(name = pair.name, selector) && valueAttribute == null && CharOperation.equals(name, TypeConstants.VALUE)) {
                    valueAttribute = pair;
                    pair.binding = method;
                    pair.resolveTypeExpecting(scope, method.returnType);
                }
                ++j;
            }
            ++i;
        }
        long tagBits = this.determineNonNullByDefaultTagBits(annotationType, valueAttribute);
        return (int)(tagBits & 0x3FAL);
    }

    private static AnnotationTargetAllowed isAnnotationTargetAllowed(Binding recipient, BlockScope scope, TypeBinding annotationType, int kind, long metaTagBits) {
        switch (kind) {
            case 16: {
                if ((metaTagBits & 0x80000000000L) != 0L) {
                    return AnnotationTargetAllowed.YES;
                }
                if (scope.compilerOptions().sourceLevel > 0x320000L) break;
                SourceTypeBinding sourceType = (SourceTypeBinding)recipient;
                if (!CharOperation.equals(sourceType.sourceName, TypeConstants.PACKAGE_INFO_NAME)) break;
                return AnnotationTargetAllowed.YES;
            }
            case 16388: {
                if ((metaTagBits & 0x20000000000000L) != 0L) {
                    return AnnotationTargetAllowed.YES;
                }
                if (scope.compilerOptions().sourceLevel >= 0x340000L) break;
                return AnnotationTargetAllowed.YES;
            }
            case 4: 
            case 2052: {
                if (((ReferenceBinding)recipient).isAnnotationType()) {
                    if ((metaTagBits & 0x20041000000000L) == 0L) break;
                    return AnnotationTargetAllowed.YES;
                }
                if ((metaTagBits & 0x20001000000000L) != 0L) {
                    return AnnotationTargetAllowed.YES;
                }
                if ((metaTagBits & 0x80000000000L) == 0L || !CharOperation.equals(((ReferenceBinding)recipient).sourceName, TypeConstants.PACKAGE_INFO_NAME)) break;
                return AnnotationTargetAllowed.YES;
            }
            case 8: {
                MethodBinding methodBinding = (MethodBinding)recipient;
                if (methodBinding.isConstructor()) {
                    if ((metaTagBits & 0x20010000000000L) == 0L) break;
                    return AnnotationTargetAllowed.YES;
                }
                if ((metaTagBits & 0x4000000000L) != 0L) {
                    return AnnotationTargetAllowed.YES;
                }
                if ((metaTagBits & 0x20000000000000L) == 0L) break;
                SourceTypeBinding sourceType = (SourceTypeBinding)methodBinding.declaringClass;
                MethodDeclaration methodDecl = (MethodDeclaration)sourceType.scope.referenceContext.declarationOf(methodBinding);
                if (Annotation.isTypeUseCompatible(methodDecl.returnType, scope)) {
                    return AnnotationTargetAllowed.YES;
                }
                return AnnotationTargetAllowed.TYPE_ANNOTATION_ON_QUALIFIED_NAME;
            }
            case 1: {
                if ((metaTagBits & 0x2000000000L) != 0L) {
                    return AnnotationTargetAllowed.YES;
                }
                if (((FieldBinding)recipient).isRecordComponent()) {
                    long recordComponentMask = 9008024962203648L;
                    return (metaTagBits & recordComponentMask) != 0L ? AnnotationTargetAllowed.YES : AnnotationTargetAllowed.NO;
                }
                if ((metaTagBits & 0x20000000000000L) == 0L) break;
                FieldBinding sourceField = (FieldBinding)recipient;
                SourceTypeBinding sourceType = (SourceTypeBinding)sourceField.declaringClass;
                FieldDeclaration fieldDeclaration = sourceType.scope.referenceContext.declarationOf(sourceField);
                if (Annotation.isTypeUseCompatible(fieldDeclaration.type, scope)) {
                    return AnnotationTargetAllowed.YES;
                }
                return AnnotationTargetAllowed.TYPE_ANNOTATION_ON_QUALIFIED_NAME;
            }
            case 131072: {
                long recordComponentMask = 9008162401157120L;
                return (metaTagBits & recordComponentMask) != 0L ? AnnotationTargetAllowed.YES : AnnotationTargetAllowed.NO;
            }
            case 2: {
                LocalVariableBinding localVariableBinding = (LocalVariableBinding)recipient;
                if ((localVariableBinding.tagBits & 0x400L) != 0L) {
                    if ((metaTagBits & 0x8000000000L) != 0L) {
                        return AnnotationTargetAllowed.YES;
                    }
                    if ((metaTagBits & 0x20000000000000L) == 0L) break;
                    if (Annotation.isTypeUseCompatible(localVariableBinding.declaration.type, scope)) {
                        return AnnotationTargetAllowed.YES;
                    }
                    return AnnotationTargetAllowed.TYPE_ANNOTATION_ON_QUALIFIED_NAME;
                }
                if ((annotationType.tagBits & 0x20000000000L) != 0L) {
                    return AnnotationTargetAllowed.YES;
                }
                if ((metaTagBits & 0x20000000000000L) == 0L) break;
                if (localVariableBinding.declaration.isTypeNameVar(scope)) {
                    return AnnotationTargetAllowed.NO;
                }
                if (Annotation.isTypeUseCompatible(localVariableBinding.declaration.type, scope)) {
                    return AnnotationTargetAllowed.YES;
                }
                return AnnotationTargetAllowed.TYPE_ANNOTATION_ON_QUALIFIED_NAME;
            }
            case 4100: {
                if ((metaTagBits & 0x60000000000000L) == 0L) break;
                return AnnotationTargetAllowed.YES;
            }
            case 64: {
                if ((metaTagBits & 0x2000000000000000L) == 0L) break;
                return AnnotationTargetAllowed.YES;
            }
        }
        return AnnotationTargetAllowed.NO;
    }

    public static boolean isAnnotationTargetAllowed(BlockScope scope, TypeBinding annotationType, Binding recipient) {
        long metaTagBits = annotationType.getAnnotationTagBits();
        if ((metaTagBits & 0x20600FF840000000L) == 0L) {
            return true;
        }
        return Annotation.isAnnotationTargetAllowed(recipient, scope, annotationType, recipient.kind(), metaTagBits) == AnnotationTargetAllowed.YES;
    }

    static AnnotationTargetAllowed isAnnotationTargetAllowed(Annotation annotation, BlockScope scope, TypeBinding annotationType, int kind) {
        long metaTagBits = annotationType.getAnnotationTagBits();
        if ((metaTagBits & 0x20600FF840000000L) == 0L) {
            return AnnotationTargetAllowed.YES;
        }
        if ((metaTagBits & 0xFF000000000L) == 0L && (metaTagBits & 0x60000000000000L) != 0L && scope.compilerOptions().sourceLevel < 0x340000L) {
            switch (kind) {
                case 1: 
                case 2: 
                case 4: 
                case 8: 
                case 16: 
                case 2052: 
                case 131072: {
                    scope.problemReporter().invalidUsageOfTypeAnnotations(annotation);
                }
            }
        }
        return Annotation.isAnnotationTargetAllowed(annotation.recipient, scope, annotationType, kind, metaTagBits);
    }

    static void checkAnnotationTarget(Annotation annotation, BlockScope scope, ReferenceBinding annotationType, int kind, Binding recipient, long tagBitsToRevert) {
        if (!annotationType.isValidBinding()) {
            return;
        }
        AnnotationTargetAllowed annotationTargetAllowed = Annotation.isAnnotationTargetAllowed(annotation, scope, annotationType, kind);
        if (annotationTargetAllowed != AnnotationTargetAllowed.YES) {
            if (annotationTargetAllowed == AnnotationTargetAllowed.TYPE_ANNOTATION_ON_QUALIFIED_NAME) {
                scope.problemReporter().typeAnnotationAtQualifiedName(annotation);
            } else {
                scope.problemReporter().disallowedTargetForAnnotation(annotation);
            }
            if (recipient instanceof TypeBinding) {
                ((TypeBinding)recipient).tagBits &= tagBitsToRevert ^ 0xFFFFFFFFFFFFFFFFL;
            }
        }
    }

    public static void checkForInstancesOfRepeatableWithRepeatingContainerAnnotation(BlockScope scope, ReferenceBinding repeatedAnnotationType, Annotation[] sourceAnnotations) {
        MethodBinding[] valueMethods = repeatedAnnotationType.getMethods(TypeConstants.VALUE);
        if (valueMethods.length != 1) {
            return;
        }
        TypeBinding methodReturnType = valueMethods[0].returnType;
        if (!methodReturnType.isArrayType() || methodReturnType.dimensions() != 1) {
            return;
        }
        ArrayBinding array = (ArrayBinding)methodReturnType;
        TypeBinding elementsType = array.elementsType();
        if (!elementsType.isRepeatableAnnotationType()) {
            return;
        }
        int i = 0;
        while (i < sourceAnnotations.length) {
            Annotation annotation = sourceAnnotations[i];
            if (TypeBinding.equalsEquals(elementsType, annotation.resolvedType)) {
                scope.problemReporter().repeatableAnnotationWithRepeatingContainer(annotation, repeatedAnnotationType);
                return;
            }
            ++i;
        }
    }

    public static boolean isTypeUseCompatible(TypeReference reference, Scope scope) {
        Binding binding;
        return reference == null || reference instanceof SingleTypeReference || !((binding = scope.getPackage(reference.getTypeName())) instanceof PackageBinding);
    }

    public static void isTypeUseCompatible(TypeReference reference, Scope scope, Annotation[] annotations) {
        TypeBinding resolvedType;
        if (annotations == null || reference == null || reference.getAnnotatableLevels() == 1) {
            return;
        }
        if (scope.environment().globalOptions.sourceLevel < 0x340000L) {
            return;
        }
        TypeBinding typeBinding = resolvedType = reference.resolvedType == null ? null : reference.resolvedType.leafComponentType();
        if (resolvedType == null || !resolvedType.isNestedType()) {
            return;
        }
        int i = 0;
        int annotationsLength = annotations.length;
        while (i < annotationsLength) {
            Annotation annotation = annotations[i];
            long metaTagBits = annotation.resolvedType.getAnnotationTagBits();
            if ((metaTagBits & 0x20000000000000L) != 0L && (metaTagBits & 0xFF000000000L) == 0L) {
                ReferenceBinding currentType = (ReferenceBinding)resolvedType;
                while (currentType.isNestedType()) {
                    if (currentType.isStatic()) {
                        QualifiedTypeReference.rejectAnnotationsOnStaticMemberQualififer(scope, currentType, new Annotation[]{annotation});
                        break;
                    }
                    if (annotation.hasNullBit(96)) {
                        scope.problemReporter().nullAnnotationAtQualifyingType(annotation);
                        break;
                    }
                    currentType = currentType.enclosingType();
                }
            }
            ++i;
        }
    }

    public boolean hasNullBit(int bit) {
        return this.resolvedType instanceof ReferenceBinding && ((ReferenceBinding)this.resolvedType).hasNullBit(bit);
    }

    @Override
    public abstract void traverse(ASTVisitor var1, BlockScope var2);

    @Override
    public abstract void traverse(ASTVisitor var1, ClassScope var2);

    public Annotation getPersistibleAnnotation() {
        return this.persistibleAnnotation;
    }

    public void setPersistibleAnnotation(ContainerAnnotation container) {
        this.persistibleAnnotation = container;
    }

    public static enum AnnotationTargetAllowed {
        YES,
        TYPE_ANNOTATION_ON_QUALIFIED_NAME,
        NO;

    }
}

