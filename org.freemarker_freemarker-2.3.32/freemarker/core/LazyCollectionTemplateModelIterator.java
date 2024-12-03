/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;

class LazyCollectionTemplateModelIterator
implements TemplateModelIterator {
    private final TemplateCollectionModel templateCollectionModel;
    private TemplateModelIterator iterator;

    public LazyCollectionTemplateModelIterator(TemplateCollectionModel templateCollectionModel) {
        this.templateCollectionModel = templateCollectionModel;
    }

    @Override
    public TemplateModel next() throws TemplateModelException {
        this.ensureIteratorInitialized();
        return this.iterator.next();
    }

    @Override
    public boolean hasNext() throws TemplateModelException {
        this.ensureIteratorInitialized();
        return this.iterator.hasNext();
    }

    private void ensureIteratorInitialized() throws TemplateModelException {
        if (this.iterator == null) {
            this.iterator = this.templateCollectionModel.iterator();
        }
    }
}

