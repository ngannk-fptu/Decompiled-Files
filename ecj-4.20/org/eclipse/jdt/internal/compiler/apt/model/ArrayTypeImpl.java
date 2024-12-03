/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.apt.model;

import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import org.eclipse.jdt.internal.compiler.apt.model.TypeMirrorImpl;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;

public class ArrayTypeImpl
extends TypeMirrorImpl
implements ArrayType {
    ArrayTypeImpl(BaseProcessingEnvImpl env, ArrayBinding binding) {
        super(env, binding);
    }

    @Override
    public TypeMirror getComponentType() {
        return this._env.getFactory().newTypeMirror(((ArrayBinding)this._binding).elementsType());
    }

    @Override
    public <R, P> R accept(TypeVisitor<R, P> v, P p) {
        return v.visitArray(this, p);
    }

    @Override
    protected AnnotationBinding[] getAnnotationBindings() {
        AnnotationBinding[] oldies = ((ArrayBinding)this._binding).getTypeAnnotations();
        AnnotationBinding[] newbies = Binding.NO_ANNOTATIONS;
        int i = 0;
        int length = oldies == null ? 0 : oldies.length;
        while (i < length) {
            if (oldies[i] == null) {
                newbies = new AnnotationBinding[i];
                System.arraycopy(oldies, 0, newbies, 0, i);
                return newbies;
            }
            ++i;
        }
        return newbies;
    }

    @Override
    public TypeKind getKind() {
        return TypeKind.ARRAY;
    }
}

