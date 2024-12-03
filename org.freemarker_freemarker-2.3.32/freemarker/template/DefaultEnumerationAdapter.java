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
import java.util.Enumeration;

public class DefaultEnumerationAdapter
extends WrappingTemplateModel
implements TemplateCollectionModel,
AdapterTemplateModel,
WrapperTemplateModel,
TemplateModelWithAPISupport,
Serializable {
    private final Enumeration<?> enumeration;
    private boolean enumerationOwnedBySomeone;

    public static DefaultEnumerationAdapter adapt(Enumeration<?> enumeration, ObjectWrapper wrapper) {
        return new DefaultEnumerationAdapter(enumeration, wrapper);
    }

    private DefaultEnumerationAdapter(Enumeration<?> enumeration, ObjectWrapper wrapper) {
        super(wrapper);
        this.enumeration = enumeration;
    }

    @Override
    public Object getWrappedObject() {
        return this.enumeration;
    }

    @Override
    public Object getAdaptedObject(Class<?> hint) {
        return this.getWrappedObject();
    }

    @Override
    public TemplateModelIterator iterator() throws TemplateModelException {
        return new SimpleTemplateModelIterator();
    }

    @Override
    public TemplateModel getAPI() throws TemplateModelException {
        return ((ObjectWrapperWithAPISupport)this.getObjectWrapper()).wrapAsAPI(this.enumeration);
    }

    private class SimpleTemplateModelIterator
    implements TemplateModelIterator {
        private boolean enumerationOwnedByMe;

        private SimpleTemplateModelIterator() {
        }

        @Override
        public TemplateModel next() throws TemplateModelException {
            if (!this.enumerationOwnedByMe) {
                this.checkNotOwner();
                DefaultEnumerationAdapter.this.enumerationOwnedBySomeone = true;
                this.enumerationOwnedByMe = true;
            }
            if (!DefaultEnumerationAdapter.this.enumeration.hasMoreElements()) {
                throw new TemplateModelException("The collection has no more items.");
            }
            Object value = DefaultEnumerationAdapter.this.enumeration.nextElement();
            return value instanceof TemplateModel ? (TemplateModel)value : DefaultEnumerationAdapter.this.wrap(value);
        }

        @Override
        public boolean hasNext() throws TemplateModelException {
            if (!this.enumerationOwnedByMe) {
                this.checkNotOwner();
            }
            return DefaultEnumerationAdapter.this.enumeration.hasMoreElements();
        }

        private void checkNotOwner() throws TemplateModelException {
            if (DefaultEnumerationAdapter.this.enumerationOwnedBySomeone) {
                throw new TemplateModelException("This collection value wraps a java.util.Enumeration, thus it can be listed only once.");
            }
        }
    }
}

