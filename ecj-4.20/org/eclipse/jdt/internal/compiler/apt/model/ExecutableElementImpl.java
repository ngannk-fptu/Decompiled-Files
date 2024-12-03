/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.apt.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import org.eclipse.jdt.internal.compiler.apt.model.AnnotationMemberValue;
import org.eclipse.jdt.internal.compiler.apt.model.ElementImpl;
import org.eclipse.jdt.internal.compiler.apt.model.Factory;
import org.eclipse.jdt.internal.compiler.apt.model.NameImpl;
import org.eclipse.jdt.internal.compiler.apt.model.TypeElementImpl;
import org.eclipse.jdt.internal.compiler.apt.model.VariableElementImpl;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationHolder;
import org.eclipse.jdt.internal.compiler.lookup.AptBinaryLocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodVerifier;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;

public class ExecutableElementImpl
extends ElementImpl
implements ExecutableElement {
    private Name _name = null;

    ExecutableElementImpl(BaseProcessingEnvImpl env, MethodBinding binding) {
        super(env, binding);
    }

    @Override
    public <R, P> R accept(ElementVisitor<R, P> v, P p) {
        return v.visitExecutable(this, p);
    }

    @Override
    protected AnnotationBinding[] getAnnotationBindings() {
        return ((MethodBinding)this._binding).getAnnotations();
    }

    @Override
    public AnnotationValue getDefaultValue() {
        MethodBinding binding = (MethodBinding)this._binding;
        Object defaultValue = binding.getDefaultValue();
        if (defaultValue != null) {
            return new AnnotationMemberValue(this._env, defaultValue, binding);
        }
        return null;
    }

    @Override
    public List<? extends Element> getEnclosedElements() {
        return Collections.emptyList();
    }

    @Override
    public Element getEnclosingElement() {
        MethodBinding binding = (MethodBinding)this._binding;
        if (binding.declaringClass == null) {
            return null;
        }
        return this._env.getFactory().newElement(binding.declaringClass);
    }

    @Override
    public String getFileName() {
        ReferenceBinding dc = ((MethodBinding)this._binding).declaringClass;
        char[] name = dc.getFileName();
        if (name == null) {
            return null;
        }
        return new String(name);
    }

    @Override
    public ElementKind getKind() {
        MethodBinding binding = (MethodBinding)this._binding;
        if (binding.isConstructor()) {
            return ElementKind.CONSTRUCTOR;
        }
        if (CharOperation.equals(binding.selector, TypeConstants.CLINIT)) {
            return ElementKind.STATIC_INIT;
        }
        if (CharOperation.equals(binding.selector, TypeConstants.INIT)) {
            return ElementKind.INSTANCE_INIT;
        }
        return ElementKind.METHOD;
    }

    @Override
    public Set<Modifier> getModifiers() {
        MethodBinding binding = (MethodBinding)this._binding;
        return Factory.getModifiers(binding.modifiers, this.getKind());
    }

    @Override
    PackageElement getPackage() {
        MethodBinding binding = (MethodBinding)this._binding;
        if (binding.declaringClass == null) {
            return null;
        }
        return this._env.getFactory().newPackageElement(binding.declaringClass.fPackage);
    }

    @Override
    public List<? extends VariableElement> getParameters() {
        int length;
        MethodBinding binding = (MethodBinding)this._binding;
        int n = length = binding.parameters == null ? 0 : binding.parameters.length;
        if (length != 0) {
            AbstractMethodDeclaration methodDeclaration = binding.sourceMethod();
            ArrayList<VariableElementImpl> params = new ArrayList<VariableElementImpl>(length);
            if (methodDeclaration != null) {
                Argument[] argumentArray = methodDeclaration.arguments;
                int n2 = methodDeclaration.arguments.length;
                int n3 = 0;
                while (n3 < n2) {
                    Argument argument = argumentArray[n3];
                    VariableElementImpl param = new VariableElementImpl(this._env, argument.binding);
                    params.add(param);
                    ++n3;
                }
            } else {
                AnnotationBinding[][] parameterAnnotationBindings = null;
                AnnotationHolder annotationHolder = binding.declaringClass.retrieveAnnotationHolder(binding, false);
                if (annotationHolder != null) {
                    parameterAnnotationBindings = annotationHolder.getParameterAnnotations();
                }
                int i = 0;
                TypeBinding[] typeBindingArray = binding.parameters;
                int n4 = binding.parameters.length;
                int n5 = 0;
                while (n5 < n4) {
                    char[] name;
                    TypeBinding typeBinding = typeBindingArray[n5];
                    char[] cArray = name = binding.parameterNames.length > i ? binding.parameterNames[i] : null;
                    if (name == null) {
                        StringBuilder builder = new StringBuilder("arg");
                        builder.append(i);
                        name = String.valueOf(builder).toCharArray();
                    }
                    VariableElementImpl param = new VariableElementImpl(this._env, new AptBinaryLocalVariableBinding(name, typeBinding, 0, parameterAnnotationBindings != null ? parameterAnnotationBindings[i] : null, binding));
                    params.add(param);
                    ++i;
                    ++n5;
                }
            }
            return Collections.unmodifiableList(params);
        }
        return Collections.emptyList();
    }

    @Override
    public TypeMirror getReturnType() {
        MethodBinding binding = (MethodBinding)this._binding;
        if (binding.returnType == null) {
            return null;
        }
        return this._env.getFactory().newTypeMirror(binding.returnType);
    }

    @Override
    public Name getSimpleName() {
        MethodBinding binding = (MethodBinding)this._binding;
        if (this._name == null) {
            this._name = new NameImpl(binding.selector);
        }
        return this._name;
    }

    @Override
    public List<? extends TypeMirror> getThrownTypes() {
        MethodBinding binding = (MethodBinding)this._binding;
        if (binding.thrownExceptions.length == 0) {
            return Collections.emptyList();
        }
        ArrayList<TypeMirror> list = new ArrayList<TypeMirror>(binding.thrownExceptions.length);
        ReferenceBinding[] referenceBindingArray = binding.thrownExceptions;
        int n = binding.thrownExceptions.length;
        int n2 = 0;
        while (n2 < n) {
            ReferenceBinding exception = referenceBindingArray[n2];
            list.add(this._env.getFactory().newTypeMirror(exception));
            ++n2;
        }
        return list;
    }

    @Override
    public List<? extends TypeParameterElement> getTypeParameters() {
        MethodBinding binding = (MethodBinding)this._binding;
        TypeVariableBinding[] variables = binding.typeVariables();
        if (variables.length == 0) {
            return Collections.emptyList();
        }
        ArrayList<TypeParameterElement> params = new ArrayList<TypeParameterElement>(variables.length);
        TypeVariableBinding[] typeVariableBindingArray = variables;
        int n = variables.length;
        int n2 = 0;
        while (n2 < n) {
            TypeVariableBinding variable = typeVariableBindingArray[n2];
            params.add(this._env.getFactory().newTypeParameterElement(variable, this));
            ++n2;
        }
        return Collections.unmodifiableList(params);
    }

    @Override
    public boolean hides(Element hidden) {
        if (!(hidden instanceof ExecutableElementImpl)) {
            return false;
        }
        MethodBinding hiderBinding = (MethodBinding)this._binding;
        MethodBinding hiddenBinding = (MethodBinding)((ExecutableElementImpl)hidden)._binding;
        if (hiderBinding == hiddenBinding) {
            return false;
        }
        if (hiddenBinding.isPrivate()) {
            return false;
        }
        if (!hiderBinding.isStatic() || !hiddenBinding.isStatic()) {
            return false;
        }
        if (!CharOperation.equals(hiddenBinding.selector, hiderBinding.selector)) {
            return false;
        }
        if (!this._env.getLookupEnvironment().methodVerifier().isMethodSubsignature(hiderBinding, hiddenBinding)) {
            return false;
        }
        return hiderBinding.declaringClass.findSuperTypeOriginatingFrom(hiddenBinding.declaringClass) != null;
    }

    @Override
    public boolean isVarArgs() {
        return ((MethodBinding)this._binding).isVarargs();
    }

    public boolean overrides(ExecutableElement overridden, TypeElement type) {
        MethodBinding overriddenBinding = (MethodBinding)((ExecutableElementImpl)overridden)._binding;
        ReferenceBinding overriderContext = (ReferenceBinding)((TypeElementImpl)type)._binding;
        if ((MethodBinding)this._binding == overriddenBinding || overriddenBinding.isStatic() || overriddenBinding.isPrivate() || ((MethodBinding)this._binding).isStatic()) {
            return false;
        }
        char[] selector = ((MethodBinding)this._binding).selector;
        if (!CharOperation.equals(selector, overriddenBinding.selector)) {
            return false;
        }
        if (overriderContext.findSuperTypeOriginatingFrom(((MethodBinding)this._binding).declaringClass) == null && ((MethodBinding)this._binding).declaringClass.findSuperTypeOriginatingFrom(overriderContext) == null) {
            return false;
        }
        MethodBinding overriderBinding = new MethodBinding((MethodBinding)this._binding, overriderContext);
        if (overriderBinding.isPrivate()) {
            return false;
        }
        TypeBinding match = overriderBinding.declaringClass.findSuperTypeOriginatingFrom(overriddenBinding.declaringClass);
        if (!(match instanceof ReferenceBinding)) {
            return false;
        }
        MethodBinding[] superMethods = ((ReferenceBinding)match).getMethods(selector);
        LookupEnvironment lookupEnvironment = this._env.getLookupEnvironment();
        if (lookupEnvironment == null) {
            return false;
        }
        MethodVerifier methodVerifier = lookupEnvironment.methodVerifier();
        int i = 0;
        int length = superMethods.length;
        while (i < length) {
            if (superMethods[i].original() == overriddenBinding) {
                return methodVerifier.doesMethodOverride(overriderBinding, superMethods[i]);
            }
            ++i;
        }
        return false;
    }

    @Override
    public TypeMirror getReceiverType() {
        return this._env.getFactory().getReceiverType((MethodBinding)this._binding);
    }

    @Override
    public boolean isDefault() {
        if (this._binding != null) {
            return ((MethodBinding)this._binding).isDefaultMethod();
        }
        return false;
    }
}

