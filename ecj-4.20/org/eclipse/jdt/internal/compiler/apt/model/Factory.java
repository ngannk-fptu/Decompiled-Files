/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.apt.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import org.eclipse.jdt.internal.compiler.apt.model.AnnotationMirrorImpl;
import org.eclipse.jdt.internal.compiler.apt.model.ArrayTypeImpl;
import org.eclipse.jdt.internal.compiler.apt.model.DeclaredTypeImpl;
import org.eclipse.jdt.internal.compiler.apt.model.ErrorTypeElement;
import org.eclipse.jdt.internal.compiler.apt.model.ErrorTypeImpl;
import org.eclipse.jdt.internal.compiler.apt.model.ExecutableElementImpl;
import org.eclipse.jdt.internal.compiler.apt.model.ExecutableTypeImpl;
import org.eclipse.jdt.internal.compiler.apt.model.ModuleElementImpl;
import org.eclipse.jdt.internal.compiler.apt.model.NoTypeImpl;
import org.eclipse.jdt.internal.compiler.apt.model.PackageElementImpl;
import org.eclipse.jdt.internal.compiler.apt.model.PrimitiveTypeImpl;
import org.eclipse.jdt.internal.compiler.apt.model.TypeElementImpl;
import org.eclipse.jdt.internal.compiler.apt.model.TypeParameterElementImpl;
import org.eclipse.jdt.internal.compiler.apt.model.TypeVariableImpl;
import org.eclipse.jdt.internal.compiler.apt.model.VariableElementImpl;
import org.eclipse.jdt.internal.compiler.apt.model.WildcardTypeImpl;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.ElementValuePair;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;

public class Factory {
    public static final Byte DUMMY_BYTE = 0;
    public static final Character DUMMY_CHAR = Character.valueOf('0');
    public static final Double DUMMY_DOUBLE = 0.0;
    public static final Float DUMMY_FLOAT = Float.valueOf(0.0f);
    public static final Integer DUMMY_INTEGER = 0;
    public static final Long DUMMY_LONG = 0L;
    public static final Short DUMMY_SHORT = 0;
    private final BaseProcessingEnvImpl _env;
    public static List<? extends AnnotationMirror> EMPTY_ANNOTATION_MIRRORS = Collections.emptyList();

    public Factory(BaseProcessingEnvImpl env) {
        this._env = env;
    }

    public List<? extends AnnotationMirror> getAnnotationMirrors(AnnotationBinding[] annotations) {
        if (annotations == null || annotations.length == 0) {
            return Collections.emptyList();
        }
        ArrayList<AnnotationMirror> list = new ArrayList<AnnotationMirror>(annotations.length);
        AnnotationBinding[] annotationBindingArray = annotations;
        int n = annotations.length;
        int n2 = 0;
        while (n2 < n) {
            AnnotationBinding annotation = annotationBindingArray[n2];
            if (annotation != null) {
                list.add(this.newAnnotationMirror(annotation));
            }
            ++n2;
        }
        return Collections.unmodifiableList(list);
    }

    public <A extends Annotation> A[] getAnnotationsByType(AnnotationBinding[] annoInstances, Class<A> annotationClass) {
        Annotation[] result = this.getAnnotations(annoInstances, annotationClass, false);
        return result == null ? (Annotation[])Array.newInstance(annotationClass, 0) : result;
    }

    public <A extends Annotation> A getAnnotation(AnnotationBinding[] annoInstances, Class<A> annotationClass) {
        Annotation[] result = this.getAnnotations(annoInstances, annotationClass, true);
        return (A)(result == null ? null : result[0]);
    }

    private <A extends Annotation> A[] getAnnotations(AnnotationBinding[] annoInstances, Class<A> annotationClass, boolean justTheFirst) {
        if (annoInstances == null || annoInstances.length == 0 || annotationClass == null) {
            return null;
        }
        String annoTypeName = annotationClass.getName();
        if (annoTypeName == null) {
            return null;
        }
        ArrayList<Annotation> list = new ArrayList<Annotation>(annoInstances.length);
        AnnotationBinding[] annotationBindingArray = annoInstances;
        int n = annoInstances.length;
        int n2 = 0;
        while (n2 < n) {
            AnnotationMirrorImpl annoMirror;
            AnnotationBinding annoInstance = annotationBindingArray[n2];
            if (annoInstance != null && (annoMirror = this.createAnnotationMirror(annoTypeName, annoInstance)) != null) {
                list.add((Annotation)Proxy.newProxyInstance(annotationClass.getClassLoader(), new Class[]{annotationClass}, (InvocationHandler)annoMirror));
                if (justTheFirst) break;
            }
            ++n2;
        }
        Annotation[] result = (Annotation[])Array.newInstance(annotationClass, list.size());
        return list.size() > 0 ? list.toArray(result) : null;
    }

    private AnnotationMirrorImpl createAnnotationMirror(String annoTypeName, AnnotationBinding annoInstance) {
        ReferenceBinding binding = annoInstance.getAnnotationType();
        if (binding != null && binding.isAnnotationType()) {
            char[] qName;
            if (binding.isMemberType()) {
                annoTypeName = annoTypeName.replace('$', '.');
                qName = CharOperation.concatWith(binding.enclosingType().compoundName, binding.sourceName, '.');
                CharOperation.replace(qName, '$', '.');
            } else {
                qName = CharOperation.concatWith(binding.compoundName, '.');
            }
            if (annoTypeName.equals(new String(qName))) {
                return (AnnotationMirrorImpl)this._env.getFactory().newAnnotationMirror(annoInstance);
            }
        }
        return null;
    }

    private static void appendModifier(Set<Modifier> result, int modifiers, int modifierConstant, Modifier modifier) {
        if ((modifiers & modifierConstant) != 0) {
            result.add(modifier);
        }
    }

    private static void decodeModifiers(Set<Modifier> result, int modifiers, int[] checkBits) {
        if (checkBits == null) {
            return;
        }
        int i = 0;
        int max = checkBits.length;
        while (i < max) {
            switch (checkBits[i]) {
                case 1: {
                    Factory.appendModifier(result, modifiers, checkBits[i], Modifier.PUBLIC);
                    break;
                }
                case 4: {
                    Factory.appendModifier(result, modifiers, checkBits[i], Modifier.PROTECTED);
                    break;
                }
                case 2: {
                    Factory.appendModifier(result, modifiers, checkBits[i], Modifier.PRIVATE);
                    break;
                }
                case 1024: {
                    Factory.appendModifier(result, modifiers, checkBits[i], Modifier.ABSTRACT);
                    break;
                }
                case 65536: {
                    try {
                        Factory.appendModifier(result, modifiers, checkBits[i], Modifier.valueOf("DEFAULT"));
                    }
                    catch (IllegalArgumentException illegalArgumentException) {}
                    break;
                }
                case 8: {
                    Factory.appendModifier(result, modifiers, checkBits[i], Modifier.STATIC);
                    break;
                }
                case 16: {
                    Factory.appendModifier(result, modifiers, checkBits[i], Modifier.FINAL);
                    break;
                }
                case 32: {
                    Factory.appendModifier(result, modifiers, checkBits[i], Modifier.SYNCHRONIZED);
                    break;
                }
                case 256: {
                    Factory.appendModifier(result, modifiers, checkBits[i], Modifier.NATIVE);
                    break;
                }
                case 2048: {
                    Factory.appendModifier(result, modifiers, checkBits[i], Modifier.STRICTFP);
                    break;
                }
                case 128: {
                    Factory.appendModifier(result, modifiers, checkBits[i], Modifier.TRANSIENT);
                    break;
                }
                case 64: {
                    Factory.appendModifier(result, modifiers, checkBits[i], Modifier.VOLATILE);
                    break;
                }
                case 0x4000000: {
                    try {
                        Factory.appendModifier(result, modifiers, checkBits[i], Modifier.valueOf("NON_SEALED"));
                    }
                    catch (IllegalArgumentException illegalArgumentException) {}
                    break;
                }
                case 0x10000000: {
                    try {
                        Factory.appendModifier(result, modifiers, checkBits[i], Modifier.valueOf("SEALED"));
                        break;
                    }
                    catch (IllegalArgumentException illegalArgumentException) {}
                }
            }
            ++i;
        }
    }

    public static Object getMatchingDummyValue(Class<?> expectedType) {
        if (expectedType.isPrimitive()) {
            if (expectedType == Boolean.TYPE) {
                return Boolean.FALSE;
            }
            if (expectedType == Byte.TYPE) {
                return DUMMY_BYTE;
            }
            if (expectedType == Character.TYPE) {
                return DUMMY_CHAR;
            }
            if (expectedType == Double.TYPE) {
                return DUMMY_DOUBLE;
            }
            if (expectedType == Float.TYPE) {
                return DUMMY_FLOAT;
            }
            if (expectedType == Integer.TYPE) {
                return DUMMY_INTEGER;
            }
            if (expectedType == Long.TYPE) {
                return DUMMY_LONG;
            }
            if (expectedType == Short.TYPE) {
                return DUMMY_SHORT;
            }
            return DUMMY_INTEGER;
        }
        return null;
    }

    public TypeMirror getReceiverType(MethodBinding binding) {
        if (binding != null) {
            if (binding.receiver != null) {
                return this._env.getFactory().newTypeMirror(binding.receiver);
            }
            if (!(binding.declaringClass == null || binding.isStatic() || binding.isConstructor() && !binding.declaringClass.isMemberType())) {
                return this._env.getFactory().newTypeMirror(binding.declaringClass);
            }
        }
        return NoTypeImpl.NO_TYPE_NONE;
    }

    public static Set<Modifier> getModifiers(int modifiers, ElementKind kind) {
        return Factory.getModifiers(modifiers, kind, false);
    }

    public static Set<Modifier> getModifiers(int modifiers, ElementKind kind, boolean isFromBinary) {
        EnumSet<Modifier> result = EnumSet.noneOf(Modifier.class);
        switch (kind) {
            case METHOD: 
            case CONSTRUCTOR: {
                Factory.decodeModifiers(result, modifiers, new int[]{1, 4, 2, 1024, 8, 16, 32, 256, 2048, 65536});
                break;
            }
            case ENUM_CONSTANT: 
            case FIELD: {
                Factory.decodeModifiers(result, modifiers, new int[]{1, 4, 2, 8, 16, 128, 64});
                break;
            }
            case ENUM: {
                if (isFromBinary) {
                    Factory.decodeModifiers(result, modifiers, new int[]{1, 4, 16, 2, 1024, 8, 2048, 0x10000000});
                    break;
                }
                Factory.decodeModifiers(result, modifiers, new int[]{1, 4, 16, 2, 8, 2048, 0x10000000});
                break;
            }
            case CLASS: 
            case ANNOTATION_TYPE: 
            case INTERFACE: 
            case RECORD: {
                Factory.decodeModifiers(result, modifiers, new int[]{1, 4, 1024, 16, 2, 8, 2048, 0x10000000, 0x4000000});
                break;
            }
            case MODULE: {
                Factory.decodeModifiers(result, modifiers, new int[]{32, 32});
            }
        }
        return Collections.unmodifiableSet(result);
    }

    public AnnotationMirror newAnnotationMirror(AnnotationBinding binding) {
        return new AnnotationMirrorImpl(this._env, binding);
    }

    public Element newElement(Binding binding, ElementKind kindHint) {
        if (binding == null) {
            return null;
        }
        switch (binding.kind()) {
            case 1: 
            case 2: 
            case 3: 
            case 131072: {
                return new VariableElementImpl(this._env, (VariableBinding)binding);
            }
            case 4: 
            case 2052: {
                ReferenceBinding referenceBinding = (ReferenceBinding)binding;
                if ((referenceBinding.tagBits & 0x80L) != 0L) {
                    return new ErrorTypeElement(this._env, referenceBinding);
                }
                if (CharOperation.equals(referenceBinding.sourceName, TypeConstants.PACKAGE_INFO_NAME)) {
                    return this.newPackageElement(referenceBinding.fPackage);
                }
                return new TypeElementImpl(this._env, referenceBinding, kindHint);
            }
            case 8: {
                return new ExecutableElementImpl(this._env, (MethodBinding)binding);
            }
            case 260: 
            case 1028: {
                return new TypeElementImpl(this._env, ((ParameterizedTypeBinding)binding).genericType(), kindHint);
            }
            case 16: {
                return this.newPackageElement((PackageBinding)binding);
            }
            case 4100: {
                return new TypeParameterElementImpl(this._env, (TypeVariableBinding)binding);
            }
            case 64: {
                return new ModuleElementImpl(this._env, (ModuleBinding)binding);
            }
            case 32: 
            case 68: 
            case 132: 
            case 516: 
            case 8196: {
                throw new UnsupportedOperationException("NYI: binding type " + binding.kind());
            }
        }
        return null;
    }

    public Element newElement(Binding binding) {
        return this.newElement(binding, null);
    }

    public PackageElement newPackageElement(PackageBinding binding) {
        if (binding != null && binding.enclosingModule != null) {
            binding = binding.getIncarnation(binding.enclosingModule);
        }
        if (binding == null) {
            return null;
        }
        return new PackageElementImpl(this._env, binding);
    }

    public NullType getNullType() {
        return NoTypeImpl.NULL_TYPE;
    }

    public NoType getNoType(TypeKind kind) {
        switch (kind) {
            case NONE: {
                return NoTypeImpl.NO_TYPE_NONE;
            }
            case VOID: {
                return NoTypeImpl.NO_TYPE_VOID;
            }
            case PACKAGE: {
                return NoTypeImpl.NO_TYPE_PACKAGE;
            }
            case MODULE: {
                return new NoTypeImpl(kind);
            }
        }
        throw new IllegalArgumentException();
    }

    public PrimitiveTypeImpl getPrimitiveType(TypeKind kind) {
        switch (kind) {
            case BOOLEAN: {
                return PrimitiveTypeImpl.BOOLEAN;
            }
            case BYTE: {
                return PrimitiveTypeImpl.BYTE;
            }
            case CHAR: {
                return PrimitiveTypeImpl.CHAR;
            }
            case DOUBLE: {
                return PrimitiveTypeImpl.DOUBLE;
            }
            case FLOAT: {
                return PrimitiveTypeImpl.FLOAT;
            }
            case INT: {
                return PrimitiveTypeImpl.INT;
            }
            case LONG: {
                return PrimitiveTypeImpl.LONG;
            }
            case SHORT: {
                return PrimitiveTypeImpl.SHORT;
            }
        }
        throw new IllegalArgumentException();
    }

    public PrimitiveTypeImpl getPrimitiveType(BaseTypeBinding binding) {
        AnnotationBinding[] annotations = binding.getTypeAnnotations();
        if (annotations == null || annotations.length == 0) {
            return this.getPrimitiveType(PrimitiveTypeImpl.getKind(binding));
        }
        return new PrimitiveTypeImpl(this._env, binding);
    }

    public TypeMirror newTypeMirror(Binding binding) {
        switch (binding.kind()) {
            case 1: 
            case 2: 
            case 3: 
            case 131072: {
                return this.newTypeMirror(((VariableBinding)binding).type);
            }
            case 16: {
                return this.getNoType(TypeKind.PACKAGE);
            }
            case 32: {
                throw new UnsupportedOperationException("NYI: import type " + binding.kind());
            }
            case 8: {
                return new ExecutableTypeImpl(this._env, (MethodBinding)binding);
            }
            case 4: 
            case 260: 
            case 1028: 
            case 2052: {
                ReferenceBinding referenceBinding = (ReferenceBinding)binding;
                if ((referenceBinding.tagBits & 0x80L) != 0L) {
                    return this.getErrorType(referenceBinding);
                }
                return new DeclaredTypeImpl(this._env, (ReferenceBinding)binding);
            }
            case 68: {
                return new ArrayTypeImpl(this._env, (ArrayBinding)binding);
            }
            case 132: {
                BaseTypeBinding btb = (BaseTypeBinding)binding;
                switch (btb.id) {
                    case 6: {
                        return this.getNoType(TypeKind.VOID);
                    }
                    case 12: {
                        return this.getNullType();
                    }
                }
                return this.getPrimitiveType(btb);
            }
            case 516: 
            case 8196: {
                return new WildcardTypeImpl(this._env, (WildcardBinding)binding);
            }
            case 4100: {
                return new TypeVariableImpl(this._env, (TypeVariableBinding)binding);
            }
            case 64: {
                return this.getNoType(TypeKind.MODULE);
            }
        }
        return null;
    }

    public TypeParameterElement newTypeParameterElement(TypeVariableBinding variable, Element declaringElement) {
        return new TypeParameterElementImpl(this._env, variable, declaringElement);
    }

    public ErrorType getErrorType(ReferenceBinding binding) {
        return new ErrorTypeImpl(this._env, binding);
    }

    public static Object performNecessaryPrimitiveTypeConversion(Class<?> expectedType, Object value, boolean avoidReflectException) {
        assert (expectedType.isPrimitive()) : "expectedType is not a primitive type: " + expectedType.getName();
        if (value == null) {
            return avoidReflectException ? Factory.getMatchingDummyValue(expectedType) : null;
        }
        String typeName = expectedType.getName();
        char expectedTypeChar = typeName.charAt(0);
        int nameLen = typeName.length();
        if (value instanceof Byte) {
            byte b = (Byte)value;
            switch (expectedTypeChar) {
                case 'b': {
                    if (nameLen == 4) {
                        return value;
                    }
                    return avoidReflectException ? Boolean.FALSE : value;
                }
                case 'c': {
                    return Character.valueOf((char)b);
                }
                case 'd': {
                    return (double)b;
                }
                case 'f': {
                    return Float.valueOf(b);
                }
                case 'i': {
                    return (int)b;
                }
                case 'l': {
                    return (long)b;
                }
                case 's': {
                    return (short)b;
                }
            }
            throw new IllegalStateException("unknown type " + expectedTypeChar);
        }
        if (value instanceof Short) {
            short s = (Short)value;
            switch (expectedTypeChar) {
                case 'b': {
                    if (nameLen == 4) {
                        return (byte)s;
                    }
                    return avoidReflectException ? Boolean.FALSE : value;
                }
                case 'c': {
                    return Character.valueOf((char)s);
                }
                case 'd': {
                    return (double)s;
                }
                case 'f': {
                    return Float.valueOf(s);
                }
                case 'i': {
                    return (int)s;
                }
                case 'l': {
                    return (long)s;
                }
                case 's': {
                    return value;
                }
            }
            throw new IllegalStateException("unknown type " + expectedTypeChar);
        }
        if (value instanceof Character) {
            char c = ((Character)value).charValue();
            switch (expectedTypeChar) {
                case 'b': {
                    if (nameLen == 4) {
                        return (byte)c;
                    }
                    return avoidReflectException ? Boolean.FALSE : value;
                }
                case 'c': {
                    return value;
                }
                case 'd': {
                    return (double)c;
                }
                case 'f': {
                    return Float.valueOf(c);
                }
                case 'i': {
                    return (int)c;
                }
                case 'l': {
                    return (long)c;
                }
                case 's': {
                    return (short)c;
                }
            }
            throw new IllegalStateException("unknown type " + expectedTypeChar);
        }
        if (value instanceof Integer) {
            int i = (Integer)value;
            switch (expectedTypeChar) {
                case 'b': {
                    if (nameLen == 4) {
                        return (byte)i;
                    }
                    return avoidReflectException ? Boolean.FALSE : value;
                }
                case 'c': {
                    return Character.valueOf((char)i);
                }
                case 'd': {
                    return (double)i;
                }
                case 'f': {
                    return Float.valueOf(i);
                }
                case 'i': {
                    return value;
                }
                case 'l': {
                    return (long)i;
                }
                case 's': {
                    return (short)i;
                }
            }
            throw new IllegalStateException("unknown type " + expectedTypeChar);
        }
        if (value instanceof Long) {
            long l = (Long)value;
            switch (expectedTypeChar) {
                case 'b': 
                case 'c': 
                case 'i': 
                case 's': {
                    return avoidReflectException ? Factory.getMatchingDummyValue(expectedType) : value;
                }
                case 'd': {
                    return (double)l;
                }
                case 'f': {
                    return Float.valueOf(l);
                }
                case 'l': {
                    return value;
                }
            }
            throw new IllegalStateException("unknown type " + expectedTypeChar);
        }
        if (value instanceof Float) {
            float f = ((Float)value).floatValue();
            switch (expectedTypeChar) {
                case 'b': 
                case 'c': 
                case 'i': 
                case 'l': 
                case 's': {
                    return avoidReflectException ? Factory.getMatchingDummyValue(expectedType) : value;
                }
                case 'd': {
                    return (double)f;
                }
                case 'f': {
                    return value;
                }
            }
            throw new IllegalStateException("unknown type " + expectedTypeChar);
        }
        if (value instanceof Double) {
            if (expectedTypeChar == 'd') {
                return value;
            }
            return avoidReflectException ? Factory.getMatchingDummyValue(expectedType) : value;
        }
        if (value instanceof Boolean) {
            if (expectedTypeChar == 'b' && nameLen == 7) {
                return value;
            }
            return avoidReflectException ? Factory.getMatchingDummyValue(expectedType) : value;
        }
        return avoidReflectException ? Factory.getMatchingDummyValue(expectedType) : value;
    }

    public static void setArrayMatchingDummyValue(Object array, int i, Class<?> expectedLeafType) {
        if (Boolean.TYPE.equals(expectedLeafType)) {
            Array.setBoolean(array, i, false);
        } else if (Byte.TYPE.equals(expectedLeafType)) {
            Array.setByte(array, i, DUMMY_BYTE);
        } else if (Character.TYPE.equals(expectedLeafType)) {
            Array.setChar(array, i, DUMMY_CHAR.charValue());
        } else if (Double.TYPE.equals(expectedLeafType)) {
            Array.setDouble(array, i, DUMMY_DOUBLE);
        } else if (Float.TYPE.equals(expectedLeafType)) {
            Array.setFloat(array, i, DUMMY_FLOAT.floatValue());
        } else if (Integer.TYPE.equals(expectedLeafType)) {
            Array.setInt(array, i, DUMMY_INTEGER);
        } else if (Long.TYPE.equals(expectedLeafType)) {
            Array.setLong(array, i, DUMMY_LONG);
        } else if (Short.TYPE.equals(expectedLeafType)) {
            Array.setShort(array, i, DUMMY_SHORT);
        } else {
            Array.set(array, i, null);
        }
    }

    public static AnnotationBinding[] getPackedAnnotationBindings(AnnotationBinding[] annotations) {
        int length;
        int n = length = annotations == null ? 0 : annotations.length;
        if (length == 0) {
            return annotations;
        }
        AnnotationBinding[] repackagedBindings = annotations;
        int i = 0;
        while (i < length) {
            MethodBinding[] values;
            ReferenceBinding containerType;
            ReferenceBinding annotationType;
            AnnotationBinding annotation = repackagedBindings[i];
            if (annotation != null && (annotationType = annotation.getAnnotationType()).isRepeatableAnnotationType() && (containerType = annotationType.containerAnnotationType()) != null && (values = containerType.getMethods(TypeConstants.VALUE)) != null && values.length == 1) {
                MethodBinding value = values[0];
                if (value.returnType != null && value.returnType.dimensions() == 1 && !TypeBinding.notEquals(value.returnType.leafComponentType(), annotationType)) {
                    ArrayList<AnnotationBinding> containees = null;
                    int j = i + 1;
                    while (j < length) {
                        AnnotationBinding otherAnnotation = repackagedBindings[j];
                        if (otherAnnotation != null && otherAnnotation.getAnnotationType() == annotationType) {
                            if (repackagedBindings == annotations) {
                                AnnotationBinding[] annotationBindingArray = repackagedBindings;
                                repackagedBindings = new AnnotationBinding[length];
                                System.arraycopy(annotationBindingArray, 0, repackagedBindings, 0, length);
                            }
                            repackagedBindings[j] = null;
                            if (containees == null) {
                                containees = new ArrayList<AnnotationBinding>();
                                containees.add(annotation);
                            }
                            containees.add(otherAnnotation);
                        }
                        ++j;
                    }
                    if (containees != null) {
                        ElementValuePair[] elementValuePairs = new ElementValuePair[]{new ElementValuePair(TypeConstants.VALUE, containees.toArray(), value)};
                        repackagedBindings[i] = new AnnotationBinding(containerType, elementValuePairs);
                    }
                }
            }
            ++i;
        }
        int finalTally = 0;
        int i2 = 0;
        while (i2 < length) {
            if (repackagedBindings[i2] != null) {
                ++finalTally;
            }
            ++i2;
        }
        if (repackagedBindings == annotations && finalTally == length) {
            return annotations;
        }
        annotations = new AnnotationBinding[finalTally];
        i2 = 0;
        int j = 0;
        while (i2 < length) {
            if (repackagedBindings[i2] != null) {
                annotations[j++] = repackagedBindings[i2];
            }
            ++i2;
        }
        return annotations;
    }

    public static AnnotationBinding[] getUnpackedAnnotationBindings(AnnotationBinding[] annotations) {
        int length;
        int n = length = annotations == null ? 0 : annotations.length;
        if (length == 0) {
            return annotations;
        }
        ArrayList<AnnotationBinding> unpackedAnnotations = new ArrayList<AnnotationBinding>();
        int i = 0;
        while (i < length) {
            AnnotationBinding annotation = annotations[i];
            if (annotation != null) {
                unpackedAnnotations.add(annotation);
                ReferenceBinding annotationType = annotation.getAnnotationType();
                MethodBinding[] values = annotationType.getMethods(TypeConstants.VALUE);
                if (values != null && values.length == 1) {
                    TypeBinding containeeType;
                    MethodBinding value = values[0];
                    if (value.returnType.dimensions() == 1 && (containeeType = value.returnType.leafComponentType()) != null && containeeType.isAnnotationType() && containeeType.isRepeatableAnnotationType() && containeeType.containerAnnotationType() == annotationType) {
                        ElementValuePair[] elementValuePairs;
                        ElementValuePair[] elementValuePairArray = elementValuePairs = annotation.getElementValuePairs();
                        int n2 = elementValuePairs.length;
                        int n3 = 0;
                        while (n3 < n2) {
                            ElementValuePair elementValuePair = elementValuePairArray[n3];
                            if (CharOperation.equals(elementValuePair.getName(), TypeConstants.VALUE)) {
                                Object[] containees;
                                Object[] objectArray = containees = (Object[])elementValuePair.getValue();
                                int n4 = containees.length;
                                int n5 = 0;
                                while (n5 < n4) {
                                    Object object = objectArray[n5];
                                    unpackedAnnotations.add((AnnotationBinding)object);
                                    ++n5;
                                }
                                break;
                            }
                            ++n3;
                        }
                    }
                }
            }
            ++i;
        }
        return unpackedAnnotations.toArray(new AnnotationBinding[unpackedAnnotations.size()]);
    }
}

