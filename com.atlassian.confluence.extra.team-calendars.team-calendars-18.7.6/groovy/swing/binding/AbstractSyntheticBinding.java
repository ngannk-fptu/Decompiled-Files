/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.binding;

import org.codehaus.groovy.binding.AbstractFullBinding;
import org.codehaus.groovy.binding.PropertyBinding;
import org.codehaus.groovy.binding.SourceBinding;
import org.codehaus.groovy.binding.TargetBinding;

public abstract class AbstractSyntheticBinding
extends AbstractFullBinding {
    boolean bound;
    String propertyName;
    Class klass;

    public AbstractSyntheticBinding(PropertyBinding source, TargetBinding target, Class klass, String propertyName) {
        this.propertyName = propertyName;
        this.klass = klass;
        this.bound = false;
        this.setSourceBinding(source);
        this.setTargetBinding(target);
    }

    @Override
    public void bind() {
        if (!this.bound) {
            try {
                this.syntheticBind();
                this.bound = true;
            }
            catch (RuntimeException re) {
                try {
                    this.syntheticUnbind();
                }
                catch (Exception exception) {
                    // empty catch block
                }
                throw re;
            }
        }
    }

    @Override
    public void unbind() {
        if (this.bound) {
            this.bound = false;
            this.syntheticUnbind();
        }
    }

    protected abstract void syntheticBind();

    protected abstract void syntheticUnbind();

    @Override
    public void rebind() {
        if (this.bound) {
            this.unbind();
            this.bind();
        }
    }

    @Override
    public void setSourceBinding(SourceBinding source) {
        if (!(source instanceof PropertyBinding)) {
            throw new IllegalArgumentException("Only PropertySourceBindings are accepted");
        }
        if (!this.propertyName.equals(((PropertyBinding)source).getPropertyName())) {
            throw new IllegalArgumentException("PropertyName must be '" + this.propertyName + "'");
        }
        Object bean = ((PropertyBinding)source).getBean();
        if (bean == null || !this.klass.isAssignableFrom(bean.getClass())) {
            throw new IllegalArgumentException("SourceBean must be a " + this.klass.getName());
        }
        super.setSourceBinding(source);
    }

    @Override
    public void setTargetBinding(TargetBinding target) {
        super.setTargetBinding(target);
    }
}

