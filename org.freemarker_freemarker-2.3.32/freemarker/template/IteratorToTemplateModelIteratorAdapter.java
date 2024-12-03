/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import java.util.Iterator;
import java.util.NoSuchElementException;

class IteratorToTemplateModelIteratorAdapter
implements TemplateModelIterator {
    private final Iterator<?> it;
    private final ObjectWrapper wrapper;

    IteratorToTemplateModelIteratorAdapter(Iterator<?> it, ObjectWrapper wrapper) {
        this.it = it;
        this.wrapper = wrapper;
    }

    @Override
    public TemplateModel next() throws TemplateModelException {
        try {
            return this.wrapper.wrap(this.it.next());
        }
        catch (NoSuchElementException e) {
            throw new TemplateModelException("The collection has no more items.", e);
        }
    }

    @Override
    public boolean hasNext() throws TemplateModelException {
        return this.it.hasNext();
    }
}

