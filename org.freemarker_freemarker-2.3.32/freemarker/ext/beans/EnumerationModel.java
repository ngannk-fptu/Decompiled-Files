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
import java.util.Enumeration;
import java.util.NoSuchElementException;

public class EnumerationModel
extends BeanModel
implements TemplateModelIterator,
TemplateCollectionModel {
    private boolean accessed = false;

    public EnumerationModel(Enumeration enumeration, BeansWrapper wrapper) {
        super(enumeration, wrapper);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TemplateModelIterator iterator() throws TemplateModelException {
        EnumerationModel enumerationModel = this;
        synchronized (enumerationModel) {
            if (this.accessed) {
                throw new TemplateModelException("This collection is stateful and can not be iterated over the second time.");
            }
            this.accessed = true;
        }
        return this;
    }

    @Override
    public boolean hasNext() {
        return ((Enumeration)this.object).hasMoreElements();
    }

    @Override
    public TemplateModel next() throws TemplateModelException {
        try {
            return this.wrap(((Enumeration)this.object).nextElement());
        }
        catch (NoSuchElementException e) {
            throw new TemplateModelException("No more elements in the enumeration.");
        }
    }

    public boolean getAsBoolean() {
        return this.hasNext();
    }
}

