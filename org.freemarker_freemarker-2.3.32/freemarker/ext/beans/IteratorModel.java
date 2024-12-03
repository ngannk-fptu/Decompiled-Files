/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class IteratorModel
extends BeanModel
implements TemplateModelIterator,
TemplateCollectionModel {
    private boolean accessed = false;

    public IteratorModel(Iterator iterator, BeansWrapper wrapper) {
        super(iterator, wrapper);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TemplateModelIterator iterator() throws TemplateModelException {
        IteratorModel iteratorModel = this;
        synchronized (iteratorModel) {
            if (this.accessed) {
                throw new TemplateModelException("This collection is stateful and can not be iterated over the second time.");
            }
            this.accessed = true;
        }
        return this;
    }

    @Override
    public boolean hasNext() {
        return ((Iterator)this.object).hasNext();
    }

    @Override
    public TemplateModel next() throws TemplateModelException {
        try {
            return this.wrap(((Iterator)this.object).next());
        }
        catch (NoSuchElementException e) {
            throw new TemplateModelException("No more elements in the iterator.", e);
        }
    }

    public boolean getAsBoolean() {
        return this.hasNext();
    }
}

