/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.binding;

import java.util.LinkedHashSet;
import java.util.Set;
import org.codehaus.groovy.binding.BindingUpdatable;

public class AggregateBinding
implements BindingUpdatable {
    protected boolean bound;
    protected Set<BindingUpdatable> bindings = new LinkedHashSet<BindingUpdatable>();

    public void addBinding(BindingUpdatable binding) {
        if (binding == null || this.bindings.contains(binding)) {
            return;
        }
        if (this.bound) {
            binding.bind();
        }
        this.bindings.add(binding);
    }

    public void removeBinding(BindingUpdatable binding) {
        this.bindings.remove(binding);
    }

    @Override
    public void bind() {
        if (!this.bound) {
            this.bound = true;
            for (BindingUpdatable binding : this.bindings) {
                binding.bind();
            }
        }
    }

    @Override
    public void unbind() {
        if (this.bound) {
            for (BindingUpdatable binding : this.bindings) {
                binding.unbind();
            }
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
        for (BindingUpdatable binding : this.bindings) {
            binding.update();
        }
    }

    @Override
    public void reverseUpdate() {
        for (BindingUpdatable binding : this.bindings) {
            binding.reverseUpdate();
        }
    }
}

