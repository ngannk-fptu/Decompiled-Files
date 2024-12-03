/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.binding;

import groovy.lang.Closure;
import org.codehaus.groovy.binding.FullBinding;
import org.codehaus.groovy.binding.SourceBinding;
import org.codehaus.groovy.binding.TargetBinding;

public abstract class AbstractFullBinding
implements FullBinding {
    protected SourceBinding sourceBinding;
    protected TargetBinding targetBinding;
    protected Closure validator;
    protected Closure converter;
    protected Closure reverseConverter;

    private void fireBinding() {
        Object validation;
        if (this.sourceBinding == null || this.targetBinding == null) {
            return;
        }
        Object result = this.sourceBinding.getSourceValue();
        if (this.getValidator() != null && ((validation = this.getValidator().call(result)) == null || validation instanceof Boolean && !((Boolean)validation).booleanValue())) {
            return;
        }
        if (this.getConverter() != null) {
            result = this.getConverter().call(result);
        }
        this.targetBinding.updateTargetValue(result);
    }

    @Override
    public void update() {
        this.fireBinding();
    }

    private void fireReverseBinding() {
        if (!(this.sourceBinding instanceof TargetBinding) || !(this.targetBinding instanceof SourceBinding)) {
            throw new RuntimeException("Binding Instance is not reversable");
        }
        Object result = ((SourceBinding)((Object)this.targetBinding)).getSourceValue();
        if (this.getReverseConverter() != null) {
            result = this.getReverseConverter().call(result);
        }
        ((TargetBinding)((Object)this.sourceBinding)).updateTargetValue(result);
    }

    @Override
    public void reverseUpdate() {
        this.fireReverseBinding();
    }

    @Override
    public SourceBinding getSourceBinding() {
        return this.sourceBinding;
    }

    @Override
    public void setSourceBinding(SourceBinding sourceBinding) {
        this.sourceBinding = sourceBinding;
    }

    @Override
    public TargetBinding getTargetBinding() {
        return this.targetBinding;
    }

    @Override
    public void setTargetBinding(TargetBinding targetBinding) {
        this.targetBinding = targetBinding;
    }

    @Override
    public Closure getValidator() {
        return this.validator;
    }

    @Override
    public void setValidator(Closure validator) {
        this.validator = validator;
    }

    @Override
    public Closure getConverter() {
        return this.converter;
    }

    @Override
    public void setConverter(Closure converter) {
        this.converter = converter;
    }

    @Override
    public Closure getReverseConverter() {
        return this.reverseConverter;
    }

    @Override
    public void setReverseConverter(Closure reverseConverter) {
        this.reverseConverter = reverseConverter;
    }
}

