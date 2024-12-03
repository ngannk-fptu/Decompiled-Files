/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.core._DelayedShortClassName;
import freemarker.core._TemplateModelException;
import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.AdapterTemplateModel;
import freemarker.template.IteratorToTemplateModelIteratorAdapter;
import freemarker.template.ObjectWrapperAndUnwrapper;
import freemarker.template.TemplateCollectionModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateModelWithAPISupport;
import freemarker.template.WrappingTemplateModel;
import freemarker.template.utility.ObjectWrapperWithAPISupport;
import java.io.Serializable;
import java.util.Collection;

public class DefaultNonListCollectionAdapter
extends WrappingTemplateModel
implements TemplateCollectionModelEx,
AdapterTemplateModel,
WrapperTemplateModel,
TemplateModelWithAPISupport,
Serializable {
    private final Collection collection;

    public static DefaultNonListCollectionAdapter adapt(Collection collection, ObjectWrapperWithAPISupport wrapper) {
        return new DefaultNonListCollectionAdapter(collection, wrapper);
    }

    private DefaultNonListCollectionAdapter(Collection collection, ObjectWrapperWithAPISupport wrapper) {
        super(wrapper);
        this.collection = collection;
    }

    @Override
    public TemplateModelIterator iterator() throws TemplateModelException {
        return new IteratorToTemplateModelIteratorAdapter(this.collection.iterator(), this.getObjectWrapper());
    }

    @Override
    public int size() {
        return this.collection.size();
    }

    @Override
    public boolean isEmpty() {
        return this.collection.isEmpty();
    }

    @Override
    public Object getWrappedObject() {
        return this.collection;
    }

    public Object getAdaptedObject(Class hint) {
        return this.getWrappedObject();
    }

    public boolean contains(TemplateModel item) throws TemplateModelException {
        Object itemPojo = ((ObjectWrapperAndUnwrapper)this.getObjectWrapper()).unwrap(item);
        try {
            return this.collection.contains(itemPojo);
        }
        catch (ClassCastException e) {
            throw new _TemplateModelException((Throwable)e, "Failed to check if the collection contains the item. Probably the item's Java type, ", itemPojo != null ? new _DelayedShortClassName(itemPojo.getClass()) : "Null", ", doesn't match the type of (some of) the collection items; see cause exception.");
        }
    }

    @Override
    public TemplateModel getAPI() throws TemplateModelException {
        return ((ObjectWrapperWithAPISupport)this.getObjectWrapper()).wrapAsAPI(this.collection);
    }
}

