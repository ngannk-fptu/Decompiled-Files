/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelAdapter;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.utility.UndeclaredThrowableException;
import java.util.AbstractList;

class SequenceAdapter
extends AbstractList
implements TemplateModelAdapter {
    private final BeansWrapper wrapper;
    private final TemplateSequenceModel model;

    SequenceAdapter(TemplateSequenceModel model, BeansWrapper wrapper) {
        this.model = model;
        this.wrapper = wrapper;
    }

    @Override
    public TemplateModel getTemplateModel() {
        return this.model;
    }

    @Override
    public int size() {
        try {
            return this.model.size();
        }
        catch (TemplateModelException e) {
            throw new UndeclaredThrowableException(e);
        }
    }

    @Override
    public Object get(int index) {
        try {
            return this.wrapper.unwrap(this.model.get(index));
        }
        catch (TemplateModelException e) {
            throw new UndeclaredThrowableException(e);
        }
    }

    public TemplateSequenceModel getTemplateSequenceModel() {
        return this.model;
    }
}

