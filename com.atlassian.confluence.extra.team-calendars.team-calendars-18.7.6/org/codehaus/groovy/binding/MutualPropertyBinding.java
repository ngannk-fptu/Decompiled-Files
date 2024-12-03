/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.binding;

import groovy.lang.Closure;
import org.codehaus.groovy.binding.FullBinding;
import org.codehaus.groovy.binding.PropertyBinding;
import org.codehaus.groovy.binding.SourceBinding;
import org.codehaus.groovy.binding.TargetBinding;
import org.codehaus.groovy.binding.TriggerBinding;

public class MutualPropertyBinding
implements FullBinding {
    boolean bound;
    PropertyBinding sourceBinding;
    PropertyBinding targetBinding;
    Closure validator;
    Closure converter;
    Closure reverseConverter;
    Closure triggerFactory;
    TriggerBinding forwardTriggerBinding;
    FullBinding forwardBinding;
    TriggerBinding reverseTriggerBinding;
    FullBinding reverseBinding;

    MutualPropertyBinding(TriggerBinding forwardTrigger, PropertyBinding source, PropertyBinding target, Closure triggerFactory) {
        this.triggerFactory = triggerFactory;
        this.sourceBinding = source;
        this.forwardTriggerBinding = forwardTrigger;
        this.setTargetBinding(target);
        this.rebuildBindings();
    }

    @Override
    public SourceBinding getSourceBinding() {
        return this.sourceBinding;
    }

    @Override
    public TargetBinding getTargetBinding() {
        return this.targetBinding;
    }

    @Override
    public void setSourceBinding(SourceBinding sourceBinding) {
        try {
            this.forwardTriggerBinding = sourceBinding == null ? null : (TriggerBinding)this.triggerFactory.call((Object)sourceBinding);
            this.sourceBinding = (PropertyBinding)sourceBinding;
        }
        catch (RuntimeException re) {
            throw new UnsupportedOperationException("Mutual Bindings may only change source bindings to other PropertyBindings");
        }
        this.rebuildBindings();
    }

    @Override
    public void setTargetBinding(TargetBinding targetBinding) {
        try {
            this.reverseTriggerBinding = targetBinding == null ? null : (TriggerBinding)this.triggerFactory.call((Object)targetBinding);
            this.targetBinding = (PropertyBinding)targetBinding;
        }
        catch (RuntimeException re) {
            throw new UnsupportedOperationException("Mutual Bindings may only change target bindings to other PropertyBindings");
        }
        this.rebuildBindings();
    }

    @Override
    public void setValidator(Closure validator) {
        this.validator = validator;
        this.rebuildBindings();
    }

    @Override
    public Closure getValidator() {
        return this.validator;
    }

    @Override
    public void setConverter(Closure converter) {
        this.converter = converter;
        this.rebuildBindings();
    }

    @Override
    public Closure getConverter() {
        return this.converter;
    }

    @Override
    public void setReverseConverter(Closure reverseConverter) {
        this.reverseConverter = reverseConverter;
        this.rebuildBindings();
    }

    @Override
    public Closure getReverseConverter() {
        return this.reverseConverter;
    }

    protected void rebuildBindings() {
        if (this.bound) {
            if (this.forwardBinding != null) {
                this.forwardBinding.unbind();
            }
            if (this.reverseBinding != null) {
                this.reverseBinding.unbind();
            }
        }
        if (this.forwardTriggerBinding == null || this.sourceBinding == null || this.reverseTriggerBinding == null || this.targetBinding == null) {
            return;
        }
        this.forwardBinding = this.forwardTriggerBinding.createBinding(this.sourceBinding, this.targetBinding);
        this.reverseBinding = this.reverseTriggerBinding.createBinding(this.targetBinding, this.sourceBinding);
        if (this.converter != null && this.reverseConverter != null) {
            this.forwardBinding.setConverter(this.converter);
            this.reverseBinding.setConverter(this.reverseConverter);
        }
        if (this.validator != null) {
            this.forwardBinding.setValidator(this.validator);
        }
        if (this.bound) {
            this.forwardBinding.bind();
            this.reverseBinding.bind();
        }
    }

    @Override
    public void bind() {
        if (!this.bound) {
            this.bound = true;
            if (this.converter == null != (this.reverseConverter == null)) {
                throw new RuntimeException("Both converter or reverseConverter must be set or unset to bind.  Only " + (this.converter != null ? "converter" : "reverseConverter") + " is set.");
            }
            if (this.forwardBinding == null || this.reverseBinding == null) {
                return;
            }
            this.forwardBinding.bind();
            this.reverseBinding.bind();
        }
    }

    @Override
    public void unbind() {
        if (this.bound) {
            this.forwardBinding.unbind();
            this.reverseBinding.unbind();
            this.bound = false;
        }
    }

    @Override
    public void rebind() {
        if (this.bound) {
            this.unbind();
            this.bind();
        }
    }

    @Override
    public void update() {
        this.forwardBinding.update();
    }

    @Override
    public void reverseUpdate() {
        this.reverseBinding.update();
    }
}

