/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelAdapter;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.utility.UndeclaredThrowableException;
import java.util.AbstractCollection;
import java.util.Iterator;

class CollectionAdapter
extends AbstractCollection
implements TemplateModelAdapter {
    private final BeansWrapper wrapper;
    private final TemplateCollectionModel model;

    CollectionAdapter(TemplateCollectionModel model, BeansWrapper wrapper) {
        this.model = model;
        this.wrapper = wrapper;
    }

    @Override
    public TemplateModel getTemplateModel() {
        return this.model;
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator iterator() {
        try {
            return new Iterator(){
                final TemplateModelIterator i;
                {
                    this.i = CollectionAdapter.this.model.iterator();
                }

                @Override
                public boolean hasNext() {
                    try {
                        return this.i.hasNext();
                    }
                    catch (TemplateModelException e) {
                        throw new UndeclaredThrowableException(e);
                    }
                }

                public Object next() {
                    try {
                        return CollectionAdapter.this.wrapper.unwrap(this.i.next());
                    }
                    catch (TemplateModelException e) {
                        throw new UndeclaredThrowableException(e);
                    }
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
        catch (TemplateModelException e) {
            throw new UndeclaredThrowableException(e);
        }
    }
}

