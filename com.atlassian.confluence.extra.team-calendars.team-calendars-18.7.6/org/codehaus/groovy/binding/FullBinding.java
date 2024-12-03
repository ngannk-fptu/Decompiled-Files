/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.binding;

import groovy.lang.Closure;
import org.codehaus.groovy.binding.BindingUpdatable;
import org.codehaus.groovy.binding.SourceBinding;
import org.codehaus.groovy.binding.TargetBinding;

public interface FullBinding
extends BindingUpdatable {
    public SourceBinding getSourceBinding();

    public TargetBinding getTargetBinding();

    public void setSourceBinding(SourceBinding var1);

    public void setTargetBinding(TargetBinding var1);

    public void setValidator(Closure var1);

    public Closure getValidator();

    public void setConverter(Closure var1);

    public Closure getConverter();

    public void setReverseConverter(Closure var1);

    public Closure getReverseConverter();
}

