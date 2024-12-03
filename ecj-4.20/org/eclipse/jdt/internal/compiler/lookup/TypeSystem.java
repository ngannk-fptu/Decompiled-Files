/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.HashMap;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.lookup.AnnotatableTypeSystem;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.CaptureBinding;
import org.eclipse.jdt.internal.compiler.lookup.IntersectionTypeBinding18;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.RawTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.UnresolvedAnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.UnresolvedReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
import org.eclipse.jdt.internal.compiler.util.SimpleLookupTable;
import org.eclipse.jdt.internal.compiler.util.Util;

public class TypeSystem {
    private int typeid = 128;
    private TypeBinding[][] types;
    protected HashedParameterizedTypes parameterizedTypes;
    private SimpleLookupTable annotationTypes;
    LookupEnvironment environment;

    public TypeSystem(LookupEnvironment environment) {
        this.environment = environment;
        this.annotationTypes = new SimpleLookupTable(16);
        this.typeid = 128;
        this.types = new TypeBinding[256][];
        this.parameterizedTypes = new HashedParameterizedTypes();
    }

    public final TypeBinding getUnannotatedType(TypeBinding type) {
        UnresolvedReferenceBinding urb = null;
        if (type.isUnresolvedType()) {
            urb = (UnresolvedReferenceBinding)type;
            ReferenceBinding resolvedType = urb.resolvedType;
            if (resolvedType != null) {
                type = resolvedType;
            }
        }
        try {
            if (type.id == Integer.MAX_VALUE) {
                if (type.hasTypeAnnotations()) {
                    throw new IllegalStateException();
                }
                int typesLength = this.types.length;
                if (this.typeid == typesLength) {
                    this.types = new TypeBinding[typesLength * 2][];
                    System.arraycopy(this.types, 0, this.types, 0, typesLength);
                }
                type.id = this.typeid++;
                this.types[type.id] = new TypeBinding[4];
            } else {
                TypeBinding nakedType;
                TypeBinding typeBinding = nakedType = this.types[type.id] == null ? null : this.types[type.id][0];
                if (type.hasTypeAnnotations() && nakedType == null) {
                    throw new IllegalStateException();
                }
                if (nakedType != null) {
                    TypeBinding typeBinding2 = nakedType;
                    return typeBinding2;
                }
                this.types[type.id] = new TypeBinding[4];
            }
        }
        finally {
            if (urb != null && urb.id == Integer.MAX_VALUE) {
                urb.id = type.id;
            }
        }
        TypeBinding typeBinding = type;
        this.types[type.id][0] = typeBinding;
        return typeBinding;
    }

    public void forceRegisterAsDerived(TypeBinding derived) {
        TypeBinding unannotated;
        int id = derived.id;
        if (id != Integer.MAX_VALUE && this.types[id] != null) {
            unannotated = this.types[id][0];
            if (unannotated == derived) {
                this.types[id][0] = unannotated = derived.clone(null);
            }
        } else {
            throw new IllegalStateException("Type was not yet registered as expected: " + derived);
        }
        this.cacheDerivedType(unannotated, derived);
    }

    public TypeBinding[] getAnnotatedTypes(TypeBinding type) {
        return Binding.NO_TYPES;
    }

    public ArrayBinding getArrayType(TypeBinding leafType, int dimensions) {
        if (leafType instanceof ArrayBinding) {
            dimensions += leafType.dimensions();
            leafType = leafType.leafComponentType();
        }
        TypeBinding unannotatedLeafType = this.getUnannotatedType(leafType);
        TypeBinding[] derivedTypes = this.types[unannotatedLeafType.id];
        int length = derivedTypes.length;
        int i = 0;
        while (i < length) {
            TypeBinding derivedType = derivedTypes[i];
            if (derivedType == null) break;
            if (derivedType.isArrayType() && !derivedType.hasTypeAnnotations() && derivedType.leafComponentType() == unannotatedLeafType && derivedType.dimensions() == dimensions) {
                return (ArrayBinding)derivedType;
            }
            ++i;
        }
        if (i == length) {
            TypeBinding[] typeBindingArray = derivedTypes;
            derivedTypes = new TypeBinding[length * 2];
            System.arraycopy(typeBindingArray, 0, derivedTypes, 0, length);
            this.types[unannotatedLeafType.id] = derivedTypes;
        }
        derivedTypes[i] = new ArrayBinding(unannotatedLeafType, dimensions, this.environment);
        ArrayBinding arrayType = derivedTypes[i];
        int typesLength = this.types.length;
        if (this.typeid == typesLength) {
            this.types = new TypeBinding[typesLength * 2][];
            System.arraycopy(this.types, 0, this.types, 0, typesLength);
        }
        this.types[this.typeid] = new TypeBinding[1];
        arrayType.id = this.typeid++;
        ArrayBinding arrayBinding = arrayType;
        this.types[arrayType.id][0] = arrayBinding;
        return arrayBinding;
    }

    public ArrayBinding getArrayType(TypeBinding leafComponentType, int dimensions, AnnotationBinding[] annotations) {
        return this.getArrayType(leafComponentType, dimensions);
    }

    public ReferenceBinding getMemberType(ReferenceBinding memberType, ReferenceBinding enclosingType) {
        return memberType;
    }

    public ParameterizedTypeBinding getParameterizedType(ReferenceBinding genericType, TypeBinding[] typeArguments, ReferenceBinding enclosingType) {
        ReferenceBinding unannotatedGenericType = (ReferenceBinding)this.getUnannotatedType(genericType);
        if (enclosingType == null && genericType instanceof UnresolvedReferenceBinding && !(unannotatedGenericType instanceof UnresolvedReferenceBinding)) {
            enclosingType = unannotatedGenericType.enclosingType();
        }
        int typeArgumentsLength = typeArguments == null ? 0 : typeArguments.length;
        TypeBinding[] unannotatedTypeArguments = typeArguments == null ? null : new TypeBinding[typeArgumentsLength];
        int i = 0;
        while (i < typeArgumentsLength) {
            unannotatedTypeArguments[i] = this.getUnannotatedType(typeArguments[i]);
            ++i;
        }
        ReferenceBinding unannotatedEnclosingType = enclosingType == null ? null : (ReferenceBinding)this.getUnannotatedType(enclosingType);
        ParameterizedTypeBinding parameterizedType = this.parameterizedTypes.get(unannotatedGenericType, unannotatedTypeArguments, unannotatedEnclosingType, Binding.NO_ANNOTATIONS);
        if (parameterizedType != null) {
            return parameterizedType;
        }
        parameterizedType = new ParameterizedTypeBinding(unannotatedGenericType, unannotatedTypeArguments, unannotatedEnclosingType, this.environment);
        this.cacheDerivedType(unannotatedGenericType, parameterizedType);
        this.parameterizedTypes.put(genericType, typeArguments, enclosingType, parameterizedType);
        int typesLength = this.types.length;
        if (this.typeid == typesLength) {
            this.types = new TypeBinding[typesLength * 2][];
            System.arraycopy(this.types, 0, this.types, 0, typesLength);
        }
        this.types[this.typeid] = new TypeBinding[1];
        parameterizedType.id = this.typeid++;
        ParameterizedTypeBinding parameterizedTypeBinding = parameterizedType;
        this.types[parameterizedType.id][0] = parameterizedTypeBinding;
        return parameterizedTypeBinding;
    }

    public ParameterizedTypeBinding getParameterizedType(ReferenceBinding genericType, TypeBinding[] typeArguments, ReferenceBinding enclosingType, AnnotationBinding[] annotations) {
        return this.getParameterizedType(genericType, typeArguments, enclosingType);
    }

    public RawTypeBinding getRawType(ReferenceBinding genericType, ReferenceBinding enclosingType) {
        if (!genericType.hasEnclosingInstanceContext() && enclosingType != null) {
            enclosingType = (ReferenceBinding)enclosingType.original();
        }
        ReferenceBinding unannotatedGenericType = (ReferenceBinding)this.getUnannotatedType(genericType);
        ReferenceBinding unannotatedEnclosingType = enclosingType == null ? null : (ReferenceBinding)this.getUnannotatedType(enclosingType);
        TypeBinding[] derivedTypes = this.types[unannotatedGenericType.id];
        int length = derivedTypes.length;
        int i = 0;
        while (i < length) {
            TypeBinding derivedType = derivedTypes[i];
            if (derivedType == null) break;
            if (derivedType.isRawType() && derivedType.actualType() == unannotatedGenericType && !derivedType.hasTypeAnnotations() && derivedType.enclosingType() == unannotatedEnclosingType) {
                return (RawTypeBinding)derivedType;
            }
            ++i;
        }
        if (i == length) {
            TypeBinding[] typeBindingArray = derivedTypes;
            derivedTypes = new TypeBinding[length * 2];
            System.arraycopy(typeBindingArray, 0, derivedTypes, 0, length);
            this.types[unannotatedGenericType.id] = derivedTypes;
        }
        derivedTypes[i] = new RawTypeBinding(unannotatedGenericType, unannotatedEnclosingType, this.environment);
        RawTypeBinding rawTytpe = derivedTypes[i];
        int typesLength = this.types.length;
        if (this.typeid == typesLength) {
            this.types = new TypeBinding[typesLength * 2][];
            System.arraycopy(this.types, 0, this.types, 0, typesLength);
        }
        this.types[this.typeid] = new TypeBinding[1];
        rawTytpe.id = this.typeid++;
        RawTypeBinding rawTypeBinding = rawTytpe;
        this.types[rawTytpe.id][0] = rawTypeBinding;
        return rawTypeBinding;
    }

    public RawTypeBinding getRawType(ReferenceBinding genericType, ReferenceBinding enclosingType, AnnotationBinding[] annotations) {
        return this.getRawType(genericType, enclosingType);
    }

    public WildcardBinding getWildcard(ReferenceBinding genericType, int rank, TypeBinding bound, TypeBinding[] otherBounds, int boundKind) {
        if (genericType == null) {
            genericType = ReferenceBinding.LUB_GENERIC;
        }
        ReferenceBinding unannotatedGenericType = (ReferenceBinding)this.getUnannotatedType(genericType);
        int otherBoundsLength = otherBounds == null ? 0 : otherBounds.length;
        Object[] unannotatedOtherBounds = otherBounds == null ? null : new TypeBinding[otherBoundsLength];
        int i = 0;
        while (i < otherBoundsLength) {
            unannotatedOtherBounds[i] = this.getUnannotatedType(otherBounds[i]);
            ++i;
        }
        TypeBinding unannotatedBound = bound == null ? null : this.getUnannotatedType(bound);
        boolean useDerivedTypesOfBound = unannotatedBound instanceof TypeVariableBinding || unannotatedBound instanceof ParameterizedTypeBinding && !(unannotatedBound instanceof RawTypeBinding);
        TypeBinding[] derivedTypes = this.types[useDerivedTypesOfBound ? unannotatedBound.id : unannotatedGenericType.id];
        int length = derivedTypes.length;
        int i2 = 0;
        while (i2 < length) {
            TypeBinding derivedType = derivedTypes[i2];
            if (derivedType == null) break;
            if (derivedType.isWildcard() && derivedType.actualType() == unannotatedGenericType && !derivedType.hasTypeAnnotations() && derivedType.rank() == rank && derivedType.boundKind() == boundKind && derivedType.bound() == unannotatedBound && Util.effectivelyEqual(derivedType.additionalBounds(), unannotatedOtherBounds)) {
                return (WildcardBinding)derivedType;
            }
            ++i2;
        }
        if (i2 == length) {
            TypeBinding[] typeBindingArray = derivedTypes;
            derivedTypes = new TypeBinding[length * 2];
            System.arraycopy(typeBindingArray, 0, derivedTypes, 0, length);
            this.types[useDerivedTypesOfBound ? unannotatedBound.id : unannotatedGenericType.id] = derivedTypes;
        }
        derivedTypes[i2] = new WildcardBinding(unannotatedGenericType, rank, unannotatedBound, (TypeBinding[])unannotatedOtherBounds, boundKind, this.environment);
        WildcardBinding wildcard = derivedTypes[i2];
        int typesLength = this.types.length;
        if (this.typeid == typesLength) {
            this.types = new TypeBinding[typesLength * 2][];
            System.arraycopy(this.types, 0, this.types, 0, typesLength);
        }
        this.types[this.typeid] = new TypeBinding[1];
        wildcard.id = this.typeid++;
        WildcardBinding wildcardBinding = wildcard;
        this.types[wildcard.id][0] = wildcardBinding;
        return wildcardBinding;
    }

    public final CaptureBinding getCapturedWildcard(WildcardBinding wildcard, ReferenceBinding contextType, int start, int end, ASTNode cud, int id) {
        int length;
        WildcardBinding unannotatedWildcard = (WildcardBinding)this.getUnannotatedType(wildcard);
        TypeBinding[] derivedTypes = this.types[unannotatedWildcard.id];
        int nullSlot = length = derivedTypes.length;
        int i = length - 1;
        while (i >= -1) {
            if (i == -1) {
                i = nullSlot;
                break;
            }
            TypeBinding derivedType = derivedTypes[i];
            if (derivedType == null) {
                nullSlot = i;
            } else if (derivedType.isCapture()) {
                CaptureBinding prior = (CaptureBinding)derivedType;
                if (prior.cud != cud) {
                    i = nullSlot;
                    break;
                }
                if (prior.sourceType == contextType && prior.start == start && prior.end == end) {
                    return prior;
                }
            }
            --i;
        }
        if (i == length) {
            TypeBinding[] typeBindingArray = derivedTypes;
            derivedTypes = new TypeBinding[length * 2];
            System.arraycopy(typeBindingArray, 0, derivedTypes, 0, length);
            this.types[unannotatedWildcard.id] = derivedTypes;
        }
        derivedTypes[i] = new CaptureBinding(wildcard, contextType, start, end, cud, id);
        return derivedTypes[i];
    }

    public WildcardBinding getWildcard(ReferenceBinding genericType, int rank, TypeBinding bound, TypeBinding[] otherBounds, int boundKind, AnnotationBinding[] annotations) {
        return this.getWildcard(genericType, rank, bound, otherBounds, boundKind);
    }

    public TypeBinding getAnnotatedType(TypeBinding type, AnnotationBinding[][] annotations) {
        return type;
    }

    protected final TypeBinding[] getDerivedTypes(TypeBinding keyType) {
        keyType = this.getUnannotatedType(keyType);
        return this.types[keyType.id];
    }

    private TypeBinding cacheDerivedType(TypeBinding keyType, TypeBinding derivedType) {
        if (keyType == null || derivedType == null || keyType.id == Integer.MAX_VALUE) {
            throw new IllegalStateException();
        }
        TypeBinding[] derivedTypes = this.types[keyType.id];
        int length = derivedTypes.length;
        int first = 0;
        int last = length;
        int i = (first + last) / 2;
        do {
            if (derivedTypes[i] == null) {
                if (i == first || i > 0 && derivedTypes[i - 1] != null) break;
                last = i - 1;
                continue;
            }
            first = i + 1;
        } while ((i = (first + last) / 2) < length && first <= last);
        if (i == length) {
            TypeBinding[] typeBindingArray = derivedTypes;
            derivedTypes = new TypeBinding[length * 2];
            System.arraycopy(typeBindingArray, 0, derivedTypes, 0, length);
            this.types[keyType.id] = derivedTypes;
        }
        derivedTypes[i] = derivedType;
        return derivedTypes[i];
    }

    protected final TypeBinding cacheDerivedType(TypeBinding keyType, TypeBinding nakedType, TypeBinding derivedType) {
        this.cacheDerivedType(keyType, derivedType);
        if (nakedType.id != keyType.id) {
            this.cacheDerivedType(nakedType, derivedType);
        }
        return derivedType;
    }

    public final AnnotationBinding getAnnotationType(ReferenceBinding annotationType, boolean requiredResolved) {
        AnnotationBinding annotation = (AnnotationBinding)this.annotationTypes.get(annotationType);
        if (annotation == null) {
            annotation = requiredResolved ? new AnnotationBinding(annotationType, Binding.NO_ELEMENT_VALUE_PAIRS) : new UnresolvedAnnotationBinding(annotationType, Binding.NO_ELEMENT_VALUE_PAIRS, this.environment);
            this.annotationTypes.put(annotationType, annotation);
        }
        if (requiredResolved) {
            annotation.resolve();
        }
        return annotation;
    }

    public boolean isAnnotatedTypeSystem() {
        return false;
    }

    public void cleanUp(int typeId) {
        TypeBinding[] typesForId;
        if (typeId != -1 && typeId < this.typeid && this.types != null && (typesForId = this.types[typeId]) != null) {
            TypeBinding[] typeBindingArray = typesForId;
            int n = typesForId.length;
            int n2 = 0;
            while (n2 < n) {
                TypeBinding type = typeBindingArray[n2];
                if (type instanceof SourceTypeBinding) {
                    ((SourceTypeBinding)type).scope = null;
                }
                ++n2;
            }
        }
    }

    public void reset() {
        this.annotationTypes = new SimpleLookupTable(16);
        this.typeid = 128;
        this.types = new TypeBinding[256][];
        this.parameterizedTypes = new HashedParameterizedTypes();
    }

    public void updateCaches(UnresolvedReferenceBinding unresolvedType, ReferenceBinding resolvedType) {
        int i;
        int unresolvedTypeId = unresolvedType.id;
        if (resolvedType.id != Integer.MAX_VALUE) {
            unresolvedType.id = resolvedType.id;
        }
        if (unresolvedTypeId != Integer.MAX_VALUE) {
            TypeBinding[] derivedTypes = this.types[unresolvedTypeId];
            i = 0;
            int length = derivedTypes == null ? 0 : derivedTypes.length;
            while (i < length) {
                if (derivedTypes[i] == null) break;
                if (derivedTypes[i] == unresolvedType) {
                    if (resolvedType.id == Integer.MAX_VALUE) {
                        resolvedType.id = unresolvedTypeId;
                    }
                    derivedTypes[i] = resolvedType;
                }
                ++i;
            }
        }
        if (this.annotationTypes.get(unresolvedType) != null) {
            Object[] keys = this.annotationTypes.keyTable;
            i = 0;
            int l = keys.length;
            while (i < l) {
                if (keys[i] == unresolvedType) {
                    keys[i] = resolvedType;
                    break;
                }
                ++i;
            }
        }
    }

    public final TypeBinding getIntersectionType18(ReferenceBinding[] intersectingTypes) {
        int intersectingTypesLength;
        int n = intersectingTypesLength = intersectingTypes == null ? 0 : intersectingTypes.length;
        if (intersectingTypesLength == 0) {
            return null;
        }
        ReferenceBinding keyType = intersectingTypes[0];
        if (keyType == null || intersectingTypesLength == 1) {
            return keyType;
        }
        TypeBinding[] derivedTypes = this.getDerivedTypes(keyType);
        int length = derivedTypes.length;
        int i = 0;
        while (i < length) {
            block6: {
                ReferenceBinding[] priorIntersectingTypes;
                TypeBinding derivedType = derivedTypes[i];
                if (derivedType == null) break;
                if (derivedType.isIntersectionType18() && (priorIntersectingTypes = derivedType.getIntersectingTypes()).length == intersectingTypesLength) {
                    int j = 0;
                    while (j < intersectingTypesLength) {
                        if (intersectingTypes[j] == priorIntersectingTypes[j]) {
                            ++j;
                            continue;
                        }
                        break block6;
                    }
                    return derivedType;
                }
            }
            ++i;
        }
        return this.cacheDerivedType(keyType, new IntersectionTypeBinding18(intersectingTypes, this.environment));
    }

    public void fixTypeVariableDeclaringElement(TypeVariableBinding var, Binding declaringElement) {
        int id = var.id;
        if (id < this.typeid && this.types[id] != null) {
            TypeBinding[] typeBindingArray = this.types[id];
            int n = typeBindingArray.length;
            int n2 = 0;
            while (n2 < n) {
                TypeBinding t = typeBindingArray[n2];
                if (t instanceof TypeVariableBinding) {
                    ((TypeVariableBinding)t).declaringElement = declaringElement;
                }
                ++n2;
            }
        } else {
            var.declaringElement = declaringElement;
        }
    }

    public final class HashedParameterizedTypes {
        HashMap<PTBKey, ParameterizedTypeBinding[]> hashedParameterizedTypes = new HashMap(256);

        ParameterizedTypeBinding get(ReferenceBinding genericType, TypeBinding[] typeArguments, ReferenceBinding enclosingType, AnnotationBinding[] annotations) {
            ReferenceBinding unannotatedGenericType = (ReferenceBinding)TypeSystem.this.getUnannotatedType(genericType);
            int typeArgumentsLength = typeArguments == null ? 0 : typeArguments.length;
            Object[] unannotatedTypeArguments = typeArguments == null ? null : new TypeBinding[typeArgumentsLength];
            int i = 0;
            while (i < typeArgumentsLength) {
                unannotatedTypeArguments[i] = TypeSystem.this.getUnannotatedType(typeArguments[i]);
                ++i;
            }
            ReferenceBinding unannotatedEnclosingType = enclosingType == null ? null : (ReferenceBinding)TypeSystem.this.getUnannotatedType(enclosingType);
            PTBKey key = new PTBKey(unannotatedGenericType, (TypeBinding[])unannotatedTypeArguments, unannotatedEnclosingType, null);
            ReferenceBinding genericTypeToMatch = unannotatedGenericType;
            ReferenceBinding enclosingTypeToMatch = unannotatedEnclosingType;
            Object[] typeArgumentsToMatch = unannotatedTypeArguments;
            if (TypeSystem.this instanceof AnnotatableTypeSystem) {
                genericTypeToMatch = genericType;
                enclosingTypeToMatch = enclosingType;
                typeArgumentsToMatch = typeArguments;
            }
            ParameterizedTypeBinding[] parameterizedTypeBindings = this.hashedParameterizedTypes.get(key);
            int i2 = 0;
            int length = parameterizedTypeBindings == null ? 0 : parameterizedTypeBindings.length;
            while (i2 < length) {
                ParameterizedTypeBinding parameterizedType = parameterizedTypeBindings[i2];
                if (parameterizedType.actualType() == genericTypeToMatch && parameterizedType.enclosingType == enclosingTypeToMatch && Util.effectivelyEqual(parameterizedType.typeArguments(), typeArgumentsToMatch) && Util.effectivelyEqual(annotations, parameterizedType.getTypeAnnotations())) {
                    return parameterizedType;
                }
                ++i2;
            }
            return null;
        }

        void put(ReferenceBinding genericType, TypeBinding[] typeArguments, ReferenceBinding enclosingType, ParameterizedTypeBinding parameterizedType) {
            int slot;
            ReferenceBinding unannotatedGenericType = (ReferenceBinding)TypeSystem.this.getUnannotatedType(genericType);
            int typeArgumentsLength = typeArguments == null ? 0 : typeArguments.length;
            TypeBinding[] unannotatedTypeArguments = typeArguments == null ? null : new TypeBinding[typeArgumentsLength];
            int i = 0;
            while (i < typeArgumentsLength) {
                unannotatedTypeArguments[i] = TypeSystem.this.getUnannotatedType(typeArguments[i]);
                ++i;
            }
            ReferenceBinding unannotatedEnclosingType = enclosingType == null ? null : (ReferenceBinding)TypeSystem.this.getUnannotatedType(enclosingType);
            PTBKey key = new PTBKey(unannotatedGenericType, unannotatedTypeArguments, unannotatedEnclosingType, TypeSystem.this.environment);
            ParameterizedTypeBinding[] parameterizedTypeBindings = this.hashedParameterizedTypes.get(key);
            if (parameterizedTypeBindings == null) {
                slot = 0;
                parameterizedTypeBindings = new ParameterizedTypeBinding[1];
            } else {
                slot = parameterizedTypeBindings.length;
                ParameterizedTypeBinding[] parameterizedTypeBindingArray = parameterizedTypeBindings;
                parameterizedTypeBindings = new ParameterizedTypeBinding[slot + 1];
                System.arraycopy(parameterizedTypeBindingArray, 0, parameterizedTypeBindings, 0, slot);
            }
            parameterizedTypeBindings[slot] = parameterizedType;
            this.hashedParameterizedTypes.put(key, parameterizedTypeBindings);
        }

        private final class PTBKey
        extends ReferenceBinding {
            protected ReferenceBinding type;
            public TypeBinding[] arguments;
            private ReferenceBinding enclosingType;

            public PTBKey(ReferenceBinding type, TypeBinding[] arguments, ReferenceBinding enclosingType, LookupEnvironment environment) {
                this.type = type;
                this.arguments = arguments;
                this.enclosingType = enclosingType;
                if (environment != null) {
                    if (type instanceof UnresolvedReferenceBinding) {
                        ((UnresolvedReferenceBinding)type).addWrapper(this, environment);
                    }
                    if (arguments != null) {
                        int i = 0;
                        int l = arguments.length;
                        while (i < l) {
                            if (arguments[i] instanceof UnresolvedReferenceBinding) {
                                ((UnresolvedReferenceBinding)arguments[i]).addWrapper(this, environment);
                            }
                            if (arguments[i].hasNullTypeAnnotations()) {
                                this.tagBits |= 0x100000L;
                            }
                            ++i;
                        }
                    }
                }
            }

            @Override
            public void swapUnresolved(UnresolvedReferenceBinding unresolvedType, ReferenceBinding resolvedType, LookupEnvironment env) {
                if (this.type == unresolvedType) {
                    this.type = resolvedType;
                    ReferenceBinding enclosing = resolvedType.enclosingType();
                    if (enclosing != null) {
                        ReferenceBinding referenceBinding = this.enclosingType = resolvedType.isStatic() ? enclosing : (ReferenceBinding)env.convertUnresolvedBinaryToRawType(enclosing);
                    }
                }
                if (this.arguments != null) {
                    int i = 0;
                    int l = this.arguments.length;
                    while (i < l) {
                        if (this.arguments[i] == unresolvedType) {
                            this.arguments[i] = env.convertUnresolvedBinaryToRawType(resolvedType);
                        }
                        ++i;
                    }
                }
            }

            public boolean equals(Object other) {
                PTBKey that = (PTBKey)other;
                return this.type == that.type && this.enclosingType == that.enclosingType && Util.effectivelyEqual(this.arguments, that.arguments);
            }

            final int hash(TypeBinding b) {
                if (b instanceof WildcardBinding || b instanceof TypeVariableBinding || b.getClass() == ParameterizedTypeBinding.class) {
                    return System.identityHashCode(b);
                }
                return b.hashCode();
            }

            @Override
            public int hashCode() {
                int hashCode = 1 + this.hash(this.type);
                int i = 0;
                int length = this.arguments == null ? 0 : this.arguments.length;
                while (i < length) {
                    hashCode = hashCode * 31 + this.hash(this.arguments[i]);
                    ++i;
                }
                return hashCode;
            }
        }
    }
}

