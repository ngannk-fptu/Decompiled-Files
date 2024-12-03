/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.apt.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeVisitor;
import org.eclipse.jdt.internal.compiler.apt.model.Factory;
import org.eclipse.jdt.internal.compiler.apt.model.TypeMirrorImpl;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class NoTypeImpl
extends TypeMirrorImpl
implements NoType,
NullType {
    private final TypeKind _kind;
    public static final NoType NO_TYPE_NONE = new NoTypeImpl(TypeKind.NONE);
    public static final NoType NO_TYPE_VOID = new NoTypeImpl(TypeKind.VOID, (Binding)TypeBinding.VOID);
    public static final NoType NO_TYPE_PACKAGE = new NoTypeImpl(TypeKind.PACKAGE);
    public static final NullType NULL_TYPE = new NoTypeImpl(TypeKind.NULL, (Binding)TypeBinding.NULL);
    public static final Binding NO_TYPE_BINDING = new Binding(){

        @Override
        public int kind() {
            throw new IllegalStateException();
        }

        @Override
        public char[] readableName() {
            throw new IllegalStateException();
        }
    };

    public NoTypeImpl(TypeKind kind) {
        super(null, NO_TYPE_BINDING);
        this._kind = kind;
    }

    public NoTypeImpl(TypeKind kind, Binding binding) {
        super(null, binding);
        this._kind = kind;
    }

    @Override
    public <R, P> R accept(TypeVisitor<R, P> v, P p) {
        switch (this.getKind()) {
            case NULL: {
                return v.visitNull(this, p);
            }
        }
        return v.visitNoType(this, p);
    }

    @Override
    public TypeKind getKind() {
        return this._kind;
    }

    @Override
    public String toString() {
        switch (this._kind) {
            default: {
                return "none";
            }
            case NULL: {
                return "null";
            }
            case VOID: {
                return "void";
            }
            case PACKAGE: {
                return "package";
            }
            case MODULE: 
        }
        return "module";
    }

    @Override
    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        return Factory.EMPTY_ANNOTATION_MIRRORS;
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return null;
    }

    @Override
    public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
        return (Annotation[])Array.newInstance(annotationType, 0);
    }
}

