/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.apt.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import org.eclipse.jdt.internal.compiler.apt.model.TypeMirrorImpl;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;

public class DeclaredTypeImpl
extends TypeMirrorImpl
implements DeclaredType {
    private final ElementKind _elementKindHint;

    DeclaredTypeImpl(BaseProcessingEnvImpl env, ReferenceBinding binding) {
        super(env, binding);
        this._elementKindHint = null;
    }

    DeclaredTypeImpl(BaseProcessingEnvImpl env, ReferenceBinding binding, ElementKind elementKindHint) {
        super(env, binding);
        this._elementKindHint = elementKindHint;
    }

    @Override
    public Element asElement() {
        TypeBinding prototype = null;
        if (this._binding instanceof TypeBinding) {
            prototype = ((TypeBinding)this._binding).prototype();
        }
        if (prototype != null) {
            return this._env.getFactory().newElement(prototype, this._elementKindHint);
        }
        return this._env.getFactory().newElement((ReferenceBinding)this._binding, this._elementKindHint);
    }

    @Override
    public TypeMirror getEnclosingType() {
        ReferenceBinding binding = (ReferenceBinding)this._binding;
        ReferenceBinding enclosingType = binding.enclosingType();
        if (enclosingType != null) {
            return this._env.getFactory().newTypeMirror(enclosingType);
        }
        return this._env.getFactory().getNoType(TypeKind.NONE);
    }

    @Override
    public List<? extends TypeMirror> getTypeArguments() {
        ReferenceBinding binding = (ReferenceBinding)this._binding;
        if (binding.isParameterizedType()) {
            int length;
            ParameterizedTypeBinding ptb = (ParameterizedTypeBinding)this._binding;
            TypeBinding[] arguments = ptb.arguments;
            int n = length = arguments == null ? 0 : arguments.length;
            if (length == 0) {
                return Collections.emptyList();
            }
            ArrayList<TypeMirror> args = new ArrayList<TypeMirror>(length);
            TypeBinding[] typeBindingArray = arguments;
            int n2 = arguments.length;
            int n3 = 0;
            while (n3 < n2) {
                TypeBinding arg = typeBindingArray[n3];
                args.add(this._env.getFactory().newTypeMirror(arg));
                ++n3;
            }
            return Collections.unmodifiableList(args);
        }
        if (binding.isGenericType()) {
            TypeVariableBinding[] typeVariables = binding.typeVariables();
            ArrayList<TypeMirror> args = new ArrayList<TypeMirror>(typeVariables.length);
            TypeVariableBinding[] typeVariableBindingArray = typeVariables;
            int n = typeVariables.length;
            int n4 = 0;
            while (n4 < n) {
                TypeVariableBinding arg = typeVariableBindingArray[n4];
                args.add(this._env.getFactory().newTypeMirror(arg));
                ++n4;
            }
            return Collections.unmodifiableList(args);
        }
        return Collections.emptyList();
    }

    @Override
    public <R, P> R accept(TypeVisitor<R, P> v, P p) {
        return v.visitDeclared(this, p);
    }

    @Override
    public TypeKind getKind() {
        return TypeKind.DECLARED;
    }

    @Override
    public String toString() {
        return new String(this._binding.readableName());
    }
}

