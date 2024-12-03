/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.apt.model;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.type.WildcardType;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import org.eclipse.jdt.internal.compiler.apt.model.TypeMirrorImpl;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;

public class WildcardTypeImpl
extends TypeMirrorImpl
implements WildcardType {
    WildcardTypeImpl(BaseProcessingEnvImpl env, WildcardBinding binding) {
        super(env, binding);
    }

    @Override
    public TypeMirror getExtendsBound() {
        WildcardBinding wildcardBinding = (WildcardBinding)this._binding;
        if (wildcardBinding.boundKind != 1) {
            return null;
        }
        TypeBinding bound = wildcardBinding.bound;
        if (bound == null) {
            return null;
        }
        return this._env.getFactory().newTypeMirror(bound);
    }

    @Override
    public TypeKind getKind() {
        return TypeKind.WILDCARD;
    }

    @Override
    public TypeMirror getSuperBound() {
        WildcardBinding wildcardBinding = (WildcardBinding)this._binding;
        if (wildcardBinding.boundKind != 2) {
            return null;
        }
        TypeBinding bound = wildcardBinding.bound;
        if (bound == null) {
            return null;
        }
        return this._env.getFactory().newTypeMirror(bound);
    }

    @Override
    public <R, P> R accept(TypeVisitor<R, P> v, P p) {
        return v.visitWildcard(this, p);
    }
}

