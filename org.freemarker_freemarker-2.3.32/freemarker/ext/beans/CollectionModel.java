/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.IteratorModel;
import freemarker.ext.beans.StringModel;
import freemarker.ext.util.ModelFactory;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateSequenceModel;
import java.util.Collection;
import java.util.List;

public class CollectionModel
extends StringModel
implements TemplateCollectionModel,
TemplateSequenceModel {
    static final ModelFactory FACTORY = new ModelFactory(){

        @Override
        public TemplateModel create(Object object, ObjectWrapper wrapper) {
            return new CollectionModel((Collection)object, (BeansWrapper)wrapper);
        }
    };

    public CollectionModel(Collection collection, BeansWrapper wrapper) {
        super(collection, wrapper);
    }

    @Override
    public TemplateModel get(int index) throws TemplateModelException {
        if (this.object instanceof List) {
            try {
                return this.wrap(((List)this.object).get(index));
            }
            catch (IndexOutOfBoundsException e) {
                return null;
            }
        }
        throw new TemplateModelException("Underlying collection is not a list, it's " + this.object.getClass().getName());
    }

    public boolean getSupportsIndexedAccess() {
        return this.object instanceof List;
    }

    @Override
    public TemplateModelIterator iterator() {
        return new IteratorModel(((Collection)this.object).iterator(), this.wrapper);
    }

    @Override
    public int size() {
        return ((Collection)this.object).size();
    }
}

