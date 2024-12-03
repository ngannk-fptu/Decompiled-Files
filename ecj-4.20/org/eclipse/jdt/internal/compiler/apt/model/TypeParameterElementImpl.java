/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.apt.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import org.eclipse.jdt.internal.compiler.apt.model.ElementImpl;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;

public class TypeParameterElementImpl
extends ElementImpl
implements TypeParameterElement {
    private final Element _declaringElement;
    private List<? extends TypeMirror> _bounds = null;

    TypeParameterElementImpl(BaseProcessingEnvImpl env, TypeVariableBinding binding, Element declaringElement) {
        super(env, binding);
        this._declaringElement = declaringElement;
    }

    TypeParameterElementImpl(BaseProcessingEnvImpl env, TypeVariableBinding binding) {
        super(env, binding);
        this._declaringElement = this._env.getFactory().newElement(binding.declaringElement);
    }

    @Override
    public List<? extends TypeMirror> getBounds() {
        if (this._bounds == null) {
            this._bounds = this.calculateBounds();
        }
        return this._bounds;
    }

    private List<? extends TypeMirror> calculateBounds() {
        TypeVariableBinding typeVariableBinding = (TypeVariableBinding)this._binding;
        ReferenceBinding varSuperclass = typeVariableBinding.superclass();
        TypeBinding firstClassOrArrayBound = typeVariableBinding.firstBound;
        int boundsLength = 0;
        boolean isFirstBoundATypeVariable = false;
        if (firstClassOrArrayBound != null) {
            if (firstClassOrArrayBound.isTypeVariable()) {
                isFirstBoundATypeVariable = true;
            }
            if (TypeBinding.equalsEquals(firstClassOrArrayBound, varSuperclass)) {
                ++boundsLength;
                if (firstClassOrArrayBound.isTypeVariable()) {
                    isFirstBoundATypeVariable = true;
                }
            } else if (firstClassOrArrayBound.isArrayType()) {
                ++boundsLength;
            } else {
                firstClassOrArrayBound = null;
            }
        }
        ReferenceBinding[] superinterfaces = typeVariableBinding.superInterfaces();
        int superinterfacesLength = 0;
        if (superinterfaces != null) {
            superinterfacesLength = superinterfaces.length;
            boundsLength += superinterfacesLength;
        }
        ArrayList<TypeMirror> typeBounds = new ArrayList<TypeMirror>(boundsLength);
        if (boundsLength != 0) {
            if (firstClassOrArrayBound != null) {
                TypeMirror typeBinding = this._env.getFactory().newTypeMirror(firstClassOrArrayBound);
                if (typeBinding == null) {
                    return Collections.emptyList();
                }
                typeBounds.add(typeBinding);
            }
            if (superinterfaces != null && !isFirstBoundATypeVariable) {
                int i = 0;
                while (i < superinterfacesLength) {
                    TypeMirror typeBinding = this._env.getFactory().newTypeMirror(superinterfaces[i]);
                    if (typeBinding == null) {
                        return Collections.emptyList();
                    }
                    typeBounds.add(typeBinding);
                    ++i;
                }
            }
        } else {
            typeBounds.add(this._env.getFactory().newTypeMirror(this._env.getLookupEnvironment().getType(LookupEnvironment.JAVA_LANG_OBJECT)));
        }
        return Collections.unmodifiableList(typeBounds);
    }

    @Override
    public Element getGenericElement() {
        return this._declaringElement;
    }

    @Override
    public <R, P> R accept(ElementVisitor<R, P> v, P p) {
        return v.visitTypeParameter(this, p);
    }

    @Override
    protected AnnotationBinding[] getAnnotationBindings() {
        return ((TypeVariableBinding)this._binding).getTypeAnnotations();
    }

    private boolean shouldEmulateJavacBug() {
        if (this._env.getLookupEnvironment().globalOptions.emulateJavacBug8031744) {
            AnnotationBinding[] annotations = this.getAnnotationBindings();
            int i = 0;
            int length = annotations.length;
            while (i < length) {
                ReferenceBinding firstAnnotationType = annotations[i].getAnnotationType();
                int j = i + 1;
                while (j < length) {
                    ReferenceBinding secondAnnotationType = annotations[j].getAnnotationType();
                    if (firstAnnotationType == secondAnnotationType) {
                        return true;
                    }
                    ++j;
                }
                ++i;
            }
        }
        return false;
    }

    @Override
    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        if (this.shouldEmulateJavacBug()) {
            return Collections.emptyList();
        }
        return super.getAnnotationMirrors();
    }

    @Override
    public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
        if (this.shouldEmulateJavacBug()) {
            return (Annotation[])Array.newInstance(annotationType, 0);
        }
        return super.getAnnotationsByType(annotationType);
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        if (this.shouldEmulateJavacBug()) {
            return null;
        }
        return super.getAnnotation(annotationType);
    }

    @Override
    public List<? extends Element> getEnclosedElements() {
        return Collections.emptyList();
    }

    @Override
    public Element getEnclosingElement() {
        return this.getGenericElement();
    }

    @Override
    public ElementKind getKind() {
        return ElementKind.TYPE_PARAMETER;
    }

    @Override
    PackageElement getPackage() {
        return null;
    }

    @Override
    public String toString() {
        return new String(this._binding.readableName());
    }
}

