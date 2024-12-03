/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.AdapterTemplateModel;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateModelWithAPISupport;
import freemarker.template.WrappingTemplateModel;
import freemarker.template.utility.ObjectWrapperWithAPISupport;
import java.io.Serializable;
import java.util.Iterator;

public class DefaultIteratorAdapter
extends WrappingTemplateModel
implements TemplateCollectionModel,
AdapterTemplateModel,
WrapperTemplateModel,
TemplateModelWithAPISupport,
Serializable {
    private final Iterator iterator;
    private boolean iteratorOwnedBySomeone;

    public static DefaultIteratorAdapter adapt(Iterator iterator, ObjectWrapper wrapper) {
        return new DefaultIteratorAdapter(iterator, wrapper);
    }

    private DefaultIteratorAdapter(Iterator iterator, ObjectWrapper wrapper) {
        super(wrapper);
        this.iterator = iterator;
    }

    @Override
    public Object getWrappedObject() {
        return this.iterator;
    }

    public Object getAdaptedObject(Class hint) {
        return this.getWrappedObject();
    }

    @Override
    public TemplateModelIterator iterator() throws TemplateModelException {
        return new SimpleTemplateModelIterator();
    }

    @Override
    public TemplateModel getAPI() throws TemplateModelException {
        return ((ObjectWrapperWithAPISupport)this.getObjectWrapper()).wrapAsAPI(this.iterator);
    }

    private class SimpleTemplateModelIterator
    implements TemplateModelIterator {
        private boolean iteratorOwnedByMe;

        private SimpleTemplateModelIterator() {
        }

        @Override
        public TemplateModel next() throws TemplateModelException {
            if (!this.iteratorOwnedByMe) {
                this.checkNotOwner();
                DefaultIteratorAdapter.this.iteratorOwnedBySomeone = true;
                this.iteratorOwnedByMe = true;
            }
            if (!DefaultIteratorAdapter.this.iterator.hasNext()) {
                throw new TemplateModelException("The collection has no more items.");
            }
            Object value = DefaultIteratorAdapter.this.iterator.next();
            return value instanceof TemplateModel ? (TemplateModel)value : DefaultIteratorAdapter.this.wrap(value);
        }

        @Override
        public boolean hasNext() throws TemplateModelException {
            if (!this.iteratorOwnedByMe) {
                this.checkNotOwner();
            }
            return DefaultIteratorAdapter.this.iterator.hasNext();
        }

        private void checkNotOwner() throws TemplateModelException {
            if (DefaultIteratorAdapter.this.iteratorOwnedBySomeone) {
                throw new TemplateModelException("This collection value wraps a java.util.Iterator, thus it can be listed only once.");
            }
        }
    }
}

