/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.RawTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeSystem;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
import org.eclipse.jdt.internal.compiler.util.Util;

public class AnnotatableTypeSystem
extends TypeSystem {
    private boolean isAnnotationBasedNullAnalysisEnabled;

    public AnnotatableTypeSystem(LookupEnvironment environment) {
        super(environment);
        this.environment = environment;
        this.isAnnotationBasedNullAnalysisEnabled = environment.globalOptions.isAnnotationBasedNullAnalysisEnabled;
    }

    @Override
    public TypeBinding[] getAnnotatedTypes(TypeBinding type) {
        TypeBinding[] derivedTypes = this.getDerivedTypes(type);
        int length = derivedTypes.length;
        TypeBinding[] annotatedVersions = new TypeBinding[length];
        int versions = 0;
        int i = 0;
        while (i < length) {
            TypeBinding derivedType = derivedTypes[i];
            if (derivedType == null) break;
            if (derivedType.hasTypeAnnotations() && derivedType.id == type.id) {
                annotatedVersions[versions++] = derivedType;
            }
            ++i;
        }
        if (versions != length) {
            TypeBinding[] typeBindingArray = annotatedVersions;
            annotatedVersions = new TypeBinding[versions];
            System.arraycopy(typeBindingArray, 0, annotatedVersions, 0, versions);
        }
        return annotatedVersions;
    }

    @Override
    public ArrayBinding getArrayType(TypeBinding leafType, int dimensions, AnnotationBinding[] annotations) {
        if (leafType instanceof ArrayBinding) {
            dimensions += leafType.dimensions();
            AnnotationBinding[] leafAnnotations = leafType.getTypeAnnotations();
            leafType = leafType.leafComponentType();
            AnnotationBinding[] allAnnotations = new AnnotationBinding[leafAnnotations.length + annotations.length + 1];
            System.arraycopy(annotations, 0, allAnnotations, 0, annotations.length);
            System.arraycopy(leafAnnotations, 0, allAnnotations, annotations.length + 1, leafAnnotations.length);
            annotations = allAnnotations;
        }
        ArrayBinding nakedType = null;
        TypeBinding[] derivedTypes = this.getDerivedTypes(leafType);
        int i = 0;
        int length = derivedTypes.length;
        while (i < length) {
            TypeBinding derivedType = derivedTypes[i];
            if (derivedType == null) break;
            if (derivedType.isArrayType() && derivedType.dimensions() == dimensions && derivedType.leafComponentType() == leafType) {
                if (Util.effectivelyEqual(derivedType.getTypeAnnotations(), annotations)) {
                    return (ArrayBinding)derivedType;
                }
                if (!derivedType.hasTypeAnnotations()) {
                    nakedType = (ArrayBinding)derivedType;
                }
            }
            ++i;
        }
        if (nakedType == null) {
            nakedType = super.getArrayType(leafType, dimensions);
        }
        if (!this.haveTypeAnnotations(leafType, annotations)) {
            return nakedType;
        }
        ArrayBinding arrayType = new ArrayBinding(leafType, dimensions, this.environment);
        arrayType.id = nakedType.id;
        arrayType.setTypeAnnotations(annotations, this.isAnnotationBasedNullAnalysisEnabled);
        return (ArrayBinding)this.cacheDerivedType(leafType, nakedType, arrayType);
    }

    @Override
    public ArrayBinding getArrayType(TypeBinding leaftType, int dimensions) {
        return this.getArrayType(leaftType, dimensions, Binding.NO_ANNOTATIONS);
    }

    @Override
    public ReferenceBinding getMemberType(ReferenceBinding memberType, ReferenceBinding enclosingType) {
        if (!this.haveTypeAnnotations((TypeBinding)memberType, enclosingType)) {
            return super.getMemberType(memberType, enclosingType);
        }
        return (ReferenceBinding)this.getAnnotatedType(memberType, enclosingType, memberType.getTypeAnnotations());
    }

    @Override
    public ParameterizedTypeBinding getParameterizedType(ReferenceBinding genericType, TypeBinding[] typeArguments, ReferenceBinding enclosingType, AnnotationBinding[] annotations) {
        if (genericType.hasTypeAnnotations()) {
            throw new IllegalStateException();
        }
        ParameterizedTypeBinding parameterizedType = this.parameterizedTypes.get(genericType, typeArguments, enclosingType, annotations);
        if (parameterizedType != null) {
            return parameterizedType;
        }
        ParameterizedTypeBinding nakedType = super.getParameterizedType(genericType, typeArguments, enclosingType);
        if (!this.haveTypeAnnotations(genericType, enclosingType, typeArguments, annotations)) {
            return nakedType;
        }
        parameterizedType = new ParameterizedTypeBinding(genericType, typeArguments, enclosingType, this.environment);
        parameterizedType.id = nakedType.id;
        parameterizedType.setTypeAnnotations(annotations, this.isAnnotationBasedNullAnalysisEnabled);
        this.parameterizedTypes.put(genericType, typeArguments, enclosingType, parameterizedType);
        return (ParameterizedTypeBinding)this.cacheDerivedType(genericType, nakedType, parameterizedType);
    }

    @Override
    public ParameterizedTypeBinding getParameterizedType(ReferenceBinding genericType, TypeBinding[] typeArguments, ReferenceBinding enclosingType) {
        return this.getParameterizedType(genericType, typeArguments, enclosingType, Binding.NO_ANNOTATIONS);
    }

    @Override
    public RawTypeBinding getRawType(ReferenceBinding genericType, ReferenceBinding enclosingType, AnnotationBinding[] annotations) {
        if (genericType.hasTypeAnnotations()) {
            throw new IllegalStateException();
        }
        if (!genericType.hasEnclosingInstanceContext() && enclosingType != null) {
            enclosingType = (ReferenceBinding)enclosingType.original();
        }
        RawTypeBinding nakedType = null;
        TypeBinding[] derivedTypes = this.getDerivedTypes(genericType);
        int i = 0;
        int length = derivedTypes.length;
        while (i < length) {
            TypeBinding derivedType = derivedTypes[i];
            if (derivedType == null) break;
            if (derivedType.isRawType() && derivedType.actualType() == genericType && derivedType.enclosingType() == enclosingType) {
                if (Util.effectivelyEqual(derivedType.getTypeAnnotations(), annotations)) {
                    return (RawTypeBinding)derivedType;
                }
                if (!derivedType.hasTypeAnnotations()) {
                    nakedType = (RawTypeBinding)derivedType;
                }
            }
            ++i;
        }
        if (nakedType == null) {
            nakedType = super.getRawType(genericType, enclosingType);
        }
        if (!this.haveTypeAnnotations(genericType, enclosingType, null, annotations)) {
            return nakedType;
        }
        RawTypeBinding rawType = new RawTypeBinding(genericType, enclosingType, this.environment);
        rawType.id = nakedType.id;
        rawType.setTypeAnnotations(annotations, this.isAnnotationBasedNullAnalysisEnabled);
        return (RawTypeBinding)this.cacheDerivedType(genericType, nakedType, rawType);
    }

    @Override
    public RawTypeBinding getRawType(ReferenceBinding genericType, ReferenceBinding enclosingType) {
        return this.getRawType(genericType, enclosingType, Binding.NO_ANNOTATIONS);
    }

    @Override
    public WildcardBinding getWildcard(ReferenceBinding genericType, int rank, TypeBinding bound, TypeBinding[] otherBounds, int boundKind, AnnotationBinding[] annotations) {
        if (genericType == null) {
            genericType = ReferenceBinding.LUB_GENERIC;
        }
        if (genericType.hasTypeAnnotations()) {
            throw new IllegalStateException();
        }
        WildcardBinding nakedType = null;
        boolean useDerivedTypesOfBound = bound instanceof TypeVariableBinding || bound instanceof ParameterizedTypeBinding && !(bound instanceof RawTypeBinding);
        TypeBinding[] derivedTypes = this.getDerivedTypes(useDerivedTypesOfBound ? bound : genericType);
        int i = 0;
        int length = derivedTypes.length;
        while (i < length) {
            TypeBinding derivedType = derivedTypes[i];
            if (derivedType == null) break;
            if (derivedType.isWildcard() && derivedType.actualType() == genericType && derivedType.rank() == rank && derivedType.boundKind() == boundKind && derivedType.bound() == bound && Util.effectivelyEqual(derivedType.additionalBounds(), otherBounds)) {
                if (Util.effectivelyEqual(derivedType.getTypeAnnotations(), annotations)) {
                    return (WildcardBinding)derivedType;
                }
                if (!derivedType.hasTypeAnnotations()) {
                    nakedType = (WildcardBinding)derivedType;
                }
            }
            ++i;
        }
        if (nakedType == null) {
            nakedType = super.getWildcard(genericType, rank, bound, otherBounds, boundKind);
        }
        if (!this.haveTypeAnnotations(genericType, bound, otherBounds, annotations)) {
            return nakedType;
        }
        WildcardBinding wildcard = new WildcardBinding(genericType, rank, bound, otherBounds, boundKind, this.environment);
        wildcard.id = nakedType.id;
        wildcard.setTypeAnnotations(annotations, this.isAnnotationBasedNullAnalysisEnabled);
        return (WildcardBinding)this.cacheDerivedType(useDerivedTypesOfBound ? bound : genericType, nakedType, wildcard);
    }

    @Override
    public WildcardBinding getWildcard(ReferenceBinding genericType, int rank, TypeBinding bound, TypeBinding[] otherBounds, int boundKind) {
        return this.getWildcard(genericType, rank, bound, otherBounds, boundKind, Binding.NO_ANNOTATIONS);
    }

    @Override
    public TypeBinding getAnnotatedType(TypeBinding type, AnnotationBinding[][] annotations) {
        if (type == null || !type.isValidBinding() || annotations == null || annotations.length == 0) {
            return type;
        }
        TypeBinding annotatedType = null;
        switch (type.kind()) {
            case 68: {
                ArrayBinding arrayBinding = (ArrayBinding)type;
                annotatedType = this.getArrayType(arrayBinding.leafComponentType, arrayBinding.dimensions, AnnotatableTypeSystem.flattenedAnnotations(annotations));
                break;
            }
            case 4: 
            case 132: 
            case 260: 
            case 516: 
            case 1028: 
            case 2052: 
            case 4100: 
            case 8196: 
            case 32772: {
                if (type.isUnresolvedType() && CharOperation.indexOf('$', type.sourceName()) > 0) {
                    type = BinaryTypeBinding.resolveType(type, this.environment, true);
                }
                int levels = type.depth() + 1;
                TypeBinding[] types = new TypeBinding[levels];
                types[--levels] = type;
                TypeBinding enclosingType = type.enclosingType();
                while (enclosingType != null) {
                    types[--levels] = enclosingType;
                    enclosingType = enclosingType.enclosingType();
                }
                levels = annotations.length;
                int j = types.length - levels;
                int i = 0;
                while (i < levels) {
                    if (annotations[i] != null && annotations[i].length > 0) break;
                    ++i;
                    ++j;
                }
                if (i == levels) {
                    return type;
                }
                if (j < 0) {
                    return type;
                }
                enclosingType = j == 0 ? null : types[j - 1];
                while (i < levels) {
                    TypeBinding currentType = types[j];
                    AnnotationBinding[] currentAnnotations = annotations[i] != null && annotations[i].length > 0 ? annotations[i] : currentType.getTypeAnnotations();
                    annotatedType = this.getAnnotatedType(currentType, enclosingType, currentAnnotations);
                    enclosingType = annotatedType;
                    ++i;
                    ++j;
                }
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        return annotatedType;
    }

    /*
     * Exception decompiling
     */
    private TypeBinding getAnnotatedType(TypeBinding type, TypeBinding enclosingType, AnnotationBinding[] annotations) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [4[CASE], 0[SWITCH]], but top level block is 5[SWITCH]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    private boolean haveTypeAnnotations(TypeBinding baseType, TypeBinding someType, TypeBinding[] someTypes, AnnotationBinding[] annotations) {
        if (baseType != null && baseType.hasTypeAnnotations()) {
            return true;
        }
        if (someType != null && someType.hasTypeAnnotations()) {
            return true;
        }
        int i = 0;
        int length = annotations == null ? 0 : annotations.length;
        while (i < length) {
            if (annotations[i] != null) {
                return true;
            }
            ++i;
        }
        i = 0;
        length = someTypes == null ? 0 : someTypes.length;
        while (i < length) {
            if (someTypes[i].hasTypeAnnotations()) {
                return true;
            }
            ++i;
        }
        return false;
    }

    private boolean haveTypeAnnotations(TypeBinding leafType, AnnotationBinding[] annotations) {
        return this.haveTypeAnnotations(leafType, null, null, annotations);
    }

    private boolean haveTypeAnnotations(TypeBinding memberType, TypeBinding enclosingType) {
        return this.haveTypeAnnotations(memberType, enclosingType, null, null);
    }

    static AnnotationBinding[] flattenedAnnotations(AnnotationBinding[][] annotations) {
        int levels;
        if (annotations == null || annotations.length == 0) {
            return Binding.NO_ANNOTATIONS;
        }
        int length = levels = annotations.length;
        int i = 0;
        while (i < levels) {
            length += annotations[i] == null ? 0 : annotations[i].length;
            ++i;
        }
        if (length == 0) {
            return Binding.NO_ANNOTATIONS;
        }
        AnnotationBinding[] series = new AnnotationBinding[length];
        int index = 0;
        int i2 = 0;
        while (i2 < levels) {
            int annotationsLength;
            int n = annotationsLength = annotations[i2] == null ? 0 : annotations[i2].length;
            if (annotationsLength > 0) {
                System.arraycopy(annotations[i2], 0, series, index, annotationsLength);
                index += annotationsLength;
            }
            series[index++] = null;
            ++i2;
        }
        if (index != length) {
            throw new IllegalStateException();
        }
        return series;
    }

    @Override
    public boolean isAnnotatedTypeSystem() {
        return true;
    }
}

