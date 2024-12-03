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
import freemarker.template.TemplateSequenceModel;
import freemarker.template.WrappingTemplateModel;
import freemarker.template.utility.ObjectWrapperWithAPISupport;
import freemarker.template.utility.RichObjectWrapper;
import java.io.Serializable;
import java.util.AbstractSequentialList;
import java.util.List;

public class DefaultListAdapter
extends WrappingTemplateModel
implements TemplateSequenceModel,
AdapterTemplateModel,
WrapperTemplateModel,
TemplateModelWithAPISupport,
Serializable {
    protected final List list;

    public static DefaultListAdapter adapt(List list, RichObjectWrapper wrapper) {
        return list instanceof AbstractSequentialList ? new DefaultListAdapterWithCollectionSupport(list, wrapper) : new DefaultListAdapter(list, wrapper);
    }

    private DefaultListAdapter(List list, RichObjectWrapper wrapper) {
        super(wrapper);
        this.list = list;
    }

    @Override
    public TemplateModel get(int index) throws TemplateModelException {
        return index >= 0 && index < this.list.size() ? this.wrap(this.list.get(index)) : null;
    }

    @Override
    public int size() throws TemplateModelException {
        return this.list.size();
    }

    public Object getAdaptedObject(Class hint) {
        return this.getWrappedObject();
    }

    @Override
    public Object getWrappedObject() {
        return this.list;
    }

    @Override
    public TemplateModel getAPI() throws TemplateModelException {
        return ((ObjectWrapperWithAPISupport)this.getObjectWrapper()).wrapAsAPI(this.list);
    }

    private static class DefaultListAdapterWithCollectionSupport
    extends DefaultListAdapter
    implements TemplateCollectionModel {
        private DefaultListAdapterWithCollectionSupport(List list, RichObjectWrapper wrapper) {
            super(list, wrapper);
        }

        @Override
        public TemplateModelIterator iterator() throws TemplateModelException {
            return new IteratorToTemplateModelIteratorAdapter(this.list.iterator(), this.getObjectWrapper());
        }
    }
}

