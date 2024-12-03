/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.AdapterTemplateModel;
import freemarker.template.IteratorToTemplateModelIteratorAdapter;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateModelWithAPISupport;
import freemarker.template.WrappingTemplateModel;
import freemarker.template.utility.ObjectWrapperWithAPISupport;
import java.io.Serializable;

public class DefaultIterableAdapter
extends WrappingTemplateModel
implements TemplateCollectionModel,
AdapterTemplateModel,
WrapperTemplateModel,
TemplateModelWithAPISupport,
Serializable {
    private final Iterable<?> iterable;

    public static DefaultIterableAdapter adapt(Iterable<?> iterable, ObjectWrapperWithAPISupport wrapper) {
        return new DefaultIterableAdapter(iterable, wrapper);
    }

    private DefaultIterableAdapter(Iterable<?> iterable, ObjectWrapperWithAPISupport wrapper) {
        super(wrapper);
        this.iterable = iterable;
    }

    @Override
    public TemplateModelIterator iterator() throws TemplateModelException {
        return new IteratorToTemplateModelIteratorAdapter(this.iterable.iterator(), this.getObjectWrapper());
    }

    @Override
    public Object getWrappedObject() {
        return this.iterable;
    }

    public Object getAdaptedObject(Class hint) {
        return this.getWrappedObject();
    }

    @Override
    public TemplateModel getAPI() throws TemplateModelException {
        return ((ObjectWrapperWithAPISupport)this.getObjectWrapper()).wrapAsAPI(this.iterable);
    }
}

