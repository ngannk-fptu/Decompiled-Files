/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.model;

import com.sun.jersey.api.model.AbstractModelComponent;
import com.sun.jersey.api.model.AbstractModelVisitor;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.api.model.Parameterized;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AbstractField
implements Parameterized,
AbstractModelComponent {
    private List<Parameter> parameters;
    private Field field;

    public AbstractField(Field field) {
        assert (null != field);
        this.field = field;
        this.parameters = new ArrayList<Parameter>();
    }

    public Field getField() {
        return this.field;
    }

    @Override
    public List<Parameter> getParameters() {
        return this.parameters;
    }

    @Override
    public void accept(AbstractModelVisitor visitor) {
        visitor.visitAbstractField(this);
    }

    @Override
    public List<AbstractModelComponent> getComponents() {
        return null;
    }

    public String toString() {
        return "AbstractField(" + this.getField().getDeclaringClass().getSimpleName() + "#" + this.getField().getName() + ")";
    }
}

