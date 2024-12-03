/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.binding;

import groovy.lang.GroovyObjectSupport;
import groovy.lang.ReadOnlyPropertyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.binding.BindingUpdatable;
import org.codehaus.groovy.binding.FullBinding;
import org.codehaus.groovy.binding.PropertyBinding;
import org.codehaus.groovy.binding.SourceBinding;
import org.codehaus.groovy.binding.TargetBinding;

public class BindingProxy
extends GroovyObjectSupport
implements BindingUpdatable {
    Object model;
    boolean bound;
    final Map<String, PropertyBinding> propertyBindings = new HashMap<String, PropertyBinding>();
    final List<FullBinding> generatedBindings = new ArrayList<FullBinding>();

    public BindingProxy(Object model) {
        this.model = model;
    }

    public Object getModel() {
        return this.model;
    }

    public synchronized void setModel(Object model) {
        boolean bindAgain = this.bound;
        this.model = model;
        this.unbind();
        for (PropertyBinding propertyBinding : this.propertyBindings.values()) {
            propertyBinding.setBean(model);
        }
        if (bindAgain) {
            this.bind();
            this.update();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object getProperty(String property) {
        PropertyBinding pb;
        Map<String, PropertyBinding> map = this.propertyBindings;
        synchronized (map) {
            pb = this.propertyBindings.get(property);
            if (pb == null) {
                pb = new ModelBindingPropertyBinding(this.model, property);
                this.propertyBindings.put(property, pb);
            }
        }
        FullBinding fb = pb.createBinding(pb, null);
        if (this.bound) {
            fb.bind();
        }
        return fb;
    }

    @Override
    public void setProperty(String property, Object value) {
        throw new ReadOnlyPropertyException(property, this.model.getClass());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void bind() {
        List<FullBinding> list = this.generatedBindings;
        synchronized (list) {
            if (!this.bound) {
                this.bound = true;
                for (FullBinding generatedBinding : this.generatedBindings) {
                    generatedBinding.bind();
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unbind() {
        List<FullBinding> list = this.generatedBindings;
        synchronized (list) {
            if (this.bound) {
                this.bound = false;
                for (FullBinding generatedBinding : this.generatedBindings) {
                    generatedBinding.unbind();
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void rebind() {
        List<FullBinding> list = this.generatedBindings;
        synchronized (list) {
            if (this.bound) {
                for (FullBinding generatedBinding : this.generatedBindings) {
                    generatedBinding.rebind();
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void update() {
        List<FullBinding> list = this.generatedBindings;
        synchronized (list) {
            for (FullBinding generatedBinding : this.generatedBindings) {
                generatedBinding.update();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void reverseUpdate() {
        List<FullBinding> list = this.generatedBindings;
        synchronized (list) {
            for (FullBinding generatedBinding : this.generatedBindings) {
                generatedBinding.reverseUpdate();
            }
        }
    }

    class ModelBindingPropertyBinding
    extends PropertyBinding {
        public ModelBindingPropertyBinding(Object bean, String propertyName) {
            super(bean, propertyName);
        }

        @Override
        public FullBinding createBinding(SourceBinding source, TargetBinding target) {
            FullBinding fb = super.createBinding(source, target);
            BindingProxy.this.generatedBindings.add(fb);
            return fb;
        }
    }
}

