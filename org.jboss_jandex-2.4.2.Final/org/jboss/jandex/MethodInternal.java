/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.ClassType;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Type;
import org.jboss.jandex.TypeVariable;
import org.jboss.jandex.Utils;

final class MethodInternal {
    static final int SYNTHETIC = 4096;
    static final int MANDATED = 32768;
    static final int BRIDGE = 64;
    static final MethodInternal[] EMPTY_ARRAY = new MethodInternal[0];
    static final NameAndParameterComponentComparator NAME_AND_PARAMETER_COMPONENT_COMPARATOR = new NameAndParameterComponentComparator();
    static final byte[][] EMPTY_PARAMETER_NAMES = new byte[0][];
    private byte[] name;
    private byte[][] parameterNames;
    private Type[] parameters;
    private Type returnType;
    private Type[] exceptions;
    private Type receiverType;
    private Type[] typeParameters;
    private AnnotationInstance[] annotations;
    private AnnotationValue defaultValue;
    private short flags;

    MethodInternal(byte[] name, byte[][] parameterNames, Type[] parameters, Type returnType, short flags) {
        this(name, parameterNames, parameters, returnType, flags, Type.EMPTY_ARRAY, Type.EMPTY_ARRAY);
    }

    MethodInternal(byte[] name, byte[][] parameterNames, Type[] parameters, Type returnType, short flags, Type[] typeParameters, Type[] exceptions) {
        this(name, parameterNames, parameters, returnType, flags, null, typeParameters, exceptions, AnnotationInstance.EMPTY_ARRAY, null);
    }

    MethodInternal(byte[] name, byte[][] parameterNames, Type[] parameters, Type returnType, short flags, Type receiverType, Type[] typeParameters, Type[] exceptions, AnnotationInstance[] annotations, AnnotationValue defaultValue) {
        this.name = name;
        this.parameterNames = parameterNames;
        this.parameters = parameters.length == 0 ? Type.EMPTY_ARRAY : parameters;
        this.returnType = returnType;
        this.flags = flags;
        this.annotations = annotations;
        this.exceptions = exceptions;
        this.typeParameters = typeParameters;
        this.receiverType = receiverType;
        this.defaultValue = defaultValue;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MethodInternal methodInternal = (MethodInternal)o;
        if (this.flags != methodInternal.flags) {
            return false;
        }
        if (!Arrays.equals(this.annotations, methodInternal.annotations)) {
            return false;
        }
        if (!Arrays.equals(this.exceptions, methodInternal.exceptions)) {
            return false;
        }
        if (!Arrays.equals(this.name, methodInternal.name)) {
            return false;
        }
        if (!Arrays.deepEquals((Object[])this.parameterNames, (Object[])methodInternal.parameterNames)) {
            return false;
        }
        if (!Arrays.equals(this.parameters, methodInternal.parameters)) {
            return false;
        }
        if (this.receiverType != null ? !this.receiverType.equals(methodInternal.receiverType) : methodInternal.receiverType != null) {
            return false;
        }
        if (!this.returnType.equals(methodInternal.returnType)) {
            return false;
        }
        if (this.defaultValue != null ? !this.defaultValue.equals(methodInternal.defaultValue) : methodInternal.defaultValue != null) {
            return false;
        }
        return Arrays.equals(this.typeParameters, methodInternal.typeParameters);
    }

    public int hashCode() {
        int result = Arrays.hashCode(this.name);
        result = 31 * result + Arrays.deepHashCode((Object[])this.parameterNames);
        result = 31 * result + Arrays.hashCode(this.parameters);
        result = 31 * result + this.returnType.hashCode();
        result = 31 * result + Arrays.hashCode(this.exceptions);
        result = 31 * result + (this.receiverType != null ? this.receiverType.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(this.typeParameters);
        result = 31 * result + Arrays.hashCode(this.annotations);
        result = 31 * result + this.flags;
        result = 31 * result + (this.defaultValue != null ? this.defaultValue.hashCode() : 0);
        return result;
    }

    final String name() {
        return Utils.fromUTF8(this.name);
    }

    final String parameterName(int i) {
        if (i >= this.parameterNames.length) {
            return null;
        }
        return Utils.fromUTF8(this.parameterNames[i]);
    }

    final byte[] nameBytes() {
        return this.name;
    }

    final byte[][] parameterNamesBytes() {
        return this.parameterNames;
    }

    final Type[] copyParameters() {
        return (Type[])this.parameters.clone();
    }

    final Type[] parameterArray() {
        return this.parameters;
    }

    final Type[] copyExceptions() {
        return (Type[])this.exceptions.clone();
    }

    final List<Type> parameters() {
        return Collections.unmodifiableList(Arrays.asList(this.parameters));
    }

    final Type returnType() {
        return this.returnType;
    }

    final Type receiverType(ClassInfo clazz) {
        return this.receiverType != null ? this.receiverType : new ClassType(clazz.name());
    }

    final Type receiverTypeField() {
        return this.receiverType;
    }

    final List<Type> exceptions() {
        return Collections.unmodifiableList(Arrays.asList(this.exceptions));
    }

    final Type[] exceptionArray() {
        return this.exceptions;
    }

    final List<TypeVariable> typeParameters() {
        List<Type> list = Arrays.asList(this.typeParameters);
        return Collections.unmodifiableList(list);
    }

    final List<AnnotationInstance> annotations() {
        return Collections.unmodifiableList(Arrays.asList(this.annotations));
    }

    final AnnotationInstance[] annotationArray() {
        return this.annotations;
    }

    final AnnotationInstance annotation(DotName name) {
        AnnotationInstance key = new AnnotationInstance(name, null, null);
        int i = Arrays.binarySearch(this.annotations, key, AnnotationInstance.NAME_COMPARATOR);
        return i >= 0 ? this.annotations[i] : null;
    }

    final boolean hasAnnotation(DotName name) {
        return this.annotation(name) != null;
    }

    final Type[] typeParameterArray() {
        return this.typeParameters;
    }

    final AnnotationValue defaultValue() {
        return this.defaultValue;
    }

    final short flags() {
        return this.flags;
    }

    public String toString() {
        int i;
        StringBuilder builder = new StringBuilder();
        String name = this.name();
        builder.append(this.returnType).append(' ').append(name).append('(');
        for (i = 0; i < this.parameters.length; ++i) {
            builder.append(this.parameters[i]);
            String parameterName = this.parameterName(i);
            if (parameterName != null) {
                builder.append(' ');
                builder.append(parameterName);
            }
            if (i + 1 >= this.parameters.length) continue;
            builder.append(", ");
        }
        builder.append(')');
        if (this.exceptions.length > 0) {
            builder.append(" throws ");
            for (i = 0; i < this.exceptions.length; ++i) {
                builder.append(this.exceptions[i]);
                if (i >= this.exceptions.length - 1) continue;
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    void setTypeParameters(Type[] typeParameters) {
        if (typeParameters.length > 0) {
            this.typeParameters = typeParameters;
        }
    }

    void setParameterNames(byte[][] parameterNames) {
        this.parameterNames = parameterNames;
    }

    void setParameters(Type[] parameters) {
        this.parameters = parameters.length == 0 ? Type.EMPTY_ARRAY : parameters;
    }

    void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    void setExceptions(Type[] exceptions) {
        this.exceptions = exceptions.length == 0 ? Type.EMPTY_ARRAY : exceptions;
    }

    void setReceiverType(Type receiverType) {
        this.receiverType = receiverType;
    }

    void setAnnotations(List<AnnotationInstance> annotations) {
        if (annotations.size() > 0) {
            this.annotations = annotations.toArray(new AnnotationInstance[annotations.size()]);
            Arrays.sort(this.annotations, AnnotationInstance.NAME_COMPARATOR);
        }
    }

    void setDefaultValue(AnnotationValue defaultValue) {
        this.defaultValue = defaultValue;
    }

    static class NameAndParameterComponentComparator
    implements Comparator<MethodInternal> {
        NameAndParameterComponentComparator() {
        }

        @Override
        private int compare(byte[] left, byte[] right) {
            int i = 0;
            for (int j = 0; i < left.length && j < right.length; ++i, ++j) {
                int a = left[i] & 0xFF;
                int b = right[j] & 0xFF;
                if (a == b) continue;
                return a - b;
            }
            return left.length - right.length;
        }

        @Override
        public int compare(MethodInternal instance, MethodInternal instance2) {
            int x = this.compare(instance.name, instance2.name);
            if (x != 0) {
                return x;
            }
            int min = Math.min(instance.parameters.length, instance2.parameters.length);
            for (int i = 0; i < min; ++i) {
                Type t1 = instance.parameters[i];
                Type t2 = instance2.parameters[i];
                x = t1.name().compareTo(t2.name());
                if (x == 0) continue;
                return x;
            }
            x = instance.parameters.length - instance2.parameters.length;
            if (x != 0) {
                return x;
            }
            return (instance.flags & 0x1040) - (instance2.flags & 0x1040);
        }
    }
}

