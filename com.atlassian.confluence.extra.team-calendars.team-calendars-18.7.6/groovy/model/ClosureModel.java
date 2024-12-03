/*
 * Decompiled with CFR 0.152.
 */
package groovy.model;

import groovy.lang.Closure;
import groovy.model.NestedValueModel;
import groovy.model.ValueModel;

public class ClosureModel
implements ValueModel,
NestedValueModel {
    private final ValueModel sourceModel;
    private final Closure readClosure;
    private final Closure writeClosure;
    private final Class type;

    public ClosureModel(ValueModel sourceModel, Closure readClosure) {
        this(sourceModel, readClosure, null);
    }

    public ClosureModel(ValueModel sourceModel, Closure readClosure, Closure writeClosure) {
        this(sourceModel, readClosure, writeClosure, Object.class);
    }

    public ClosureModel(ValueModel sourceModel, Closure readClosure, Closure writeClosure, Class type) {
        this.sourceModel = sourceModel;
        this.readClosure = readClosure;
        this.writeClosure = writeClosure;
        this.type = type;
    }

    @Override
    public ValueModel getSourceModel() {
        return this.sourceModel;
    }

    @Override
    public Object getValue() {
        Object source = this.sourceModel.getValue();
        if (source != null) {
            return this.readClosure.call(source);
        }
        return null;
    }

    @Override
    public void setValue(Object value) {
        Object source;
        if (this.writeClosure != null && (source = this.sourceModel.getValue()) != null) {
            this.writeClosure.call(source, value);
        }
    }

    @Override
    public Class getType() {
        return this.type;
    }

    @Override
    public boolean isEditable() {
        return this.writeClosure != null;
    }
}

