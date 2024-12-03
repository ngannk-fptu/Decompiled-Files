/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Scope;
import com.google.inject.internal.BindingImpl;
import com.google.inject.internal.Scoping;
import com.google.inject.internal.UntargettedBindingImpl;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.spi.Element;
import com.google.inject.spi.InstanceBinding;
import java.lang.annotation.Annotation;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AbstractBindingBuilder<T> {
    public static final String IMPLEMENTATION_ALREADY_SET = "Implementation is set more than once.";
    public static final String SINGLE_INSTANCE_AND_SCOPE = "Setting the scope is not permitted when binding to a single instance.";
    public static final String SCOPE_ALREADY_SET = "Scope is set more than once.";
    public static final String BINDING_TO_NULL = "Binding to null instances is not allowed. Use toProvider(Providers.of(null)) if this is your intended behaviour.";
    public static final String CONSTANT_VALUE_ALREADY_SET = "Constant value is set more than once.";
    public static final String ANNOTATION_ALREADY_SPECIFIED = "More than one annotation is specified for this binding.";
    protected static final Key<?> NULL_KEY = Key.get(Void.class);
    protected List<Element> elements;
    protected int position;
    protected final Binder binder;
    private BindingImpl<T> binding;

    public AbstractBindingBuilder(Binder binder, List<Element> elements, Object source, Key<T> key) {
        this.binder = binder;
        this.elements = elements;
        this.position = elements.size();
        this.binding = new UntargettedBindingImpl<T>(source, key, Scoping.UNSCOPED);
        elements.add(this.position, this.binding);
    }

    protected BindingImpl<T> getBinding() {
        return this.binding;
    }

    protected BindingImpl<T> setBinding(BindingImpl<T> binding) {
        this.binding = binding;
        this.elements.set(this.position, binding);
        return binding;
    }

    protected BindingImpl<T> annotatedWithInternal(Class<? extends Annotation> annotationType) {
        $Preconditions.checkNotNull(annotationType, "annotationType");
        this.checkNotAnnotated();
        return this.setBinding(this.binding.withKey(Key.get(this.binding.getKey().getTypeLiteral(), annotationType)));
    }

    protected BindingImpl<T> annotatedWithInternal(Annotation annotation) {
        $Preconditions.checkNotNull(annotation, "annotation");
        this.checkNotAnnotated();
        return this.setBinding(this.binding.withKey(Key.get(this.binding.getKey().getTypeLiteral(), annotation)));
    }

    public void in(Class<? extends Annotation> scopeAnnotation) {
        $Preconditions.checkNotNull(scopeAnnotation, "scopeAnnotation");
        this.checkNotScoped();
        this.setBinding(this.getBinding().withScoping(Scoping.forAnnotation(scopeAnnotation)));
    }

    public void in(Scope scope) {
        $Preconditions.checkNotNull(scope, "scope");
        this.checkNotScoped();
        this.setBinding(this.getBinding().withScoping(Scoping.forInstance(scope)));
    }

    public void asEagerSingleton() {
        this.checkNotScoped();
        this.setBinding(this.getBinding().withScoping(Scoping.EAGER_SINGLETON));
    }

    protected boolean keyTypeIsSet() {
        return !Void.class.equals((Object)this.binding.getKey().getTypeLiteral().getType());
    }

    protected void checkNotTargetted() {
        if (!(this.binding instanceof UntargettedBindingImpl)) {
            this.binder.addError(IMPLEMENTATION_ALREADY_SET, new Object[0]);
        }
    }

    protected void checkNotAnnotated() {
        if (this.binding.getKey().getAnnotationType() != null) {
            this.binder.addError(ANNOTATION_ALREADY_SPECIFIED, new Object[0]);
        }
    }

    protected void checkNotScoped() {
        if (this.binding instanceof InstanceBinding) {
            this.binder.addError(SINGLE_INSTANCE_AND_SCOPE, new Object[0]);
            return;
        }
        if (this.binding.getScoping().isExplicitlyScoped()) {
            this.binder.addError(SCOPE_ALREADY_SET, new Object[0]);
        }
    }
}

