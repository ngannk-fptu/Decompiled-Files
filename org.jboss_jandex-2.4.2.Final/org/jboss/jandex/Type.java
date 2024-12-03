/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.ArrayType;
import org.jboss.jandex.ClassType;
import org.jboss.jandex.DotName;
import org.jboss.jandex.ParameterizedType;
import org.jboss.jandex.PrimitiveType;
import org.jboss.jandex.TypeVariable;
import org.jboss.jandex.UnresolvedTypeVariable;
import org.jboss.jandex.VoidType;
import org.jboss.jandex.WildcardType;

public abstract class Type {
    public static final Type[] EMPTY_ARRAY = new Type[0];
    private static final AnnotationInstance[] EMPTY_ANNOTATIONS = new AnnotationInstance[0];
    private final DotName name;
    private final AnnotationInstance[] annotations;

    Type(DotName name, AnnotationInstance[] annotations) {
        this.name = name;
        AnnotationInstance[] annotationInstanceArray = annotations = annotations == null ? EMPTY_ANNOTATIONS : annotations;
        if (annotations.length > 1) {
            Arrays.sort(annotations, AnnotationInstance.NAME_COMPARATOR);
        }
        this.annotations = annotations;
    }

    public static Type create(DotName name, Kind kind) {
        if (name == null) {
            throw new IllegalArgumentException("name can not be null!");
        }
        if (kind == null) {
            throw new IllegalArgumentException("kind can not be null!");
        }
        switch (kind) {
            case ARRAY: {
                String string = name.toString();
                int start = string.lastIndexOf(91);
                if (start < 0) {
                    throw new IllegalArgumentException("Not a valid array name");
                }
                int depth = ++start;
                Type type = PrimitiveType.decode(string.charAt(start));
                if (type != null) {
                    return new ArrayType(type, depth);
                }
                char c = string.charAt(start);
                switch (c) {
                    case 'V': {
                        type = VoidType.VOID;
                        break;
                    }
                    case 'L': {
                        int end = start;
                        while (string.charAt(++end) != ';') {
                        }
                        type = new ClassType(DotName.createSimple(string.substring(start + 1, end)));
                        break;
                    }
                    default: {
                        type = PrimitiveType.decode(string.charAt(start));
                        if (type != null) break;
                        throw new IllegalArgumentException("Component type not supported: " + c);
                    }
                }
                return new ArrayType(type, depth);
            }
            case CLASS: {
                return new ClassType(name);
            }
            case PRIMITIVE: {
                return PrimitiveType.decode(name.toString());
            }
            case VOID: {
                return VoidType.VOID;
            }
        }
        throw new IllegalArgumentException("Kind not supported: " + (Object)((Object)kind));
    }

    public DotName name() {
        return this.name;
    }

    public abstract Kind kind();

    public ClassType asClassType() {
        throw new IllegalArgumentException("Not a class type!");
    }

    public ParameterizedType asParameterizedType() {
        throw new IllegalArgumentException("Not a parameterized type!");
    }

    public TypeVariable asTypeVariable() {
        throw new IllegalArgumentException("Not a type variable!");
    }

    public ArrayType asArrayType() {
        throw new IllegalArgumentException("Not an array type!");
    }

    public WildcardType asWildcardType() {
        throw new IllegalArgumentException("Not a wildcard type!");
    }

    public UnresolvedTypeVariable asUnresolvedTypeVariable() {
        throw new IllegalArgumentException("Not an unresolved type variable!");
    }

    public PrimitiveType asPrimitiveType() {
        throw new IllegalArgumentException("Not a primitive type!");
    }

    public VoidType asVoidType() {
        throw new IllegalArgumentException("Not a void type!");
    }

    public String toString() {
        return this.toString(false);
    }

    String toString(boolean simple) {
        StringBuilder builder = new StringBuilder();
        this.appendAnnotations(builder);
        builder.append(this.name);
        return builder.toString();
    }

    void appendAnnotations(StringBuilder builder) {
        AnnotationInstance[] annotations = this.annotations;
        if (annotations.length > 0) {
            for (AnnotationInstance instance : annotations) {
                builder.append(instance.toString(true)).append(' ');
            }
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Type type = (Type)o;
        return this.name.equals(type.name) && Arrays.equals(this.annotations, type.annotations);
    }

    public List<AnnotationInstance> annotations() {
        return Collections.unmodifiableList(Arrays.asList(this.annotations));
    }

    AnnotationInstance[] annotationArray() {
        return this.annotations;
    }

    public final AnnotationInstance annotation(DotName name) {
        AnnotationInstance key = new AnnotationInstance(name, null, null);
        int i = Arrays.binarySearch(this.annotations, key, AnnotationInstance.NAME_COMPARATOR);
        return i >= 0 ? this.annotations[i] : null;
    }

    public final boolean hasAnnotation(DotName name) {
        return this.annotation(name) != null;
    }

    Type addAnnotation(AnnotationInstance annotation) {
        AnnotationTarget target = annotation.target();
        if (target != null) {
            throw new IllegalArgumentException("Invalid target type");
        }
        AnnotationInstance[] newAnnotations = Arrays.copyOf(this.annotations, this.annotations.length + 1);
        newAnnotations[newAnnotations.length - 1] = annotation;
        return this.copyType(newAnnotations);
    }

    abstract Type copyType(AnnotationInstance[] var1);

    public int hashCode() {
        int result = this.name.hashCode();
        result = 31 * result + Arrays.hashCode(this.annotations);
        return result;
    }

    public static enum Kind {
        CLASS,
        ARRAY,
        PRIMITIVE,
        VOID,
        TYPE_VARIABLE,
        UNRESOLVED_TYPE_VARIABLE,
        WILDCARD_TYPE,
        PARAMETERIZED_TYPE;


        public static Kind fromOrdinal(int ordinal) {
            switch (ordinal) {
                case 0: {
                    return CLASS;
                }
                case 1: {
                    return ARRAY;
                }
                case 2: {
                    return PRIMITIVE;
                }
                default: {
                    return VOID;
                }
                case 4: {
                    return TYPE_VARIABLE;
                }
                case 5: {
                    return UNRESOLVED_TYPE_VARIABLE;
                }
                case 6: {
                    return WILDCARD_TYPE;
                }
                case 7: 
            }
            return PARAMETERIZED_TYPE;
        }
    }
}

