/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.model;

import com.sun.jersey.api.model.AbstractModelComponent;
import com.sun.jersey.api.model.AbstractModelVisitor;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.api.model.Parameterized;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class AbstractResourceConstructor
implements Parameterized,
AbstractModelComponent {
    private Constructor ctor;
    private List<Parameter> parameters;

    public AbstractResourceConstructor(Constructor constructor) {
        this.ctor = constructor;
        this.parameters = new ArrayList<Parameter>();
    }

    @Override
    public List<Parameter> getParameters() {
        return this.parameters;
    }

    public Constructor getCtor() {
        return this.ctor;
    }

    @Override
    public void accept(AbstractModelVisitor visitor) {
        visitor.visitAbstractResourceConstructor(this);
    }

    @Override
    public List<AbstractModelComponent> getComponents() {
        return null;
    }
}

