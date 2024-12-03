/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.WrappingTemplateModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SimpleSequence
extends WrappingTemplateModel
implements TemplateSequenceModel,
Serializable {
    protected final List list;
    private List unwrappedList;

    @Deprecated
    public SimpleSequence() {
        this((ObjectWrapper)null);
    }

    @Deprecated
    public SimpleSequence(int capacity) {
        this.list = new ArrayList(capacity);
    }

    @Deprecated
    public SimpleSequence(Collection collection) {
        this(collection, null);
    }

    public SimpleSequence(TemplateCollectionModel tcm) throws TemplateModelException {
        ArrayList<TemplateModel> alist = new ArrayList<TemplateModel>();
        TemplateModelIterator it = tcm.iterator();
        while (it.hasNext()) {
            alist.add(it.next());
        }
        alist.trimToSize();
        this.list = alist;
    }

    public SimpleSequence(ObjectWrapper wrapper) {
        super(wrapper);
        this.list = new ArrayList();
    }

    public SimpleSequence(int capacity, ObjectWrapper wrapper) {
        super(wrapper);
        this.list = new ArrayList(capacity);
    }

    public SimpleSequence(Collection collection, ObjectWrapper wrapper) {
        super(wrapper);
        this.list = new ArrayList(collection);
    }

    public void add(Object obj) {
        this.list.add(obj);
        this.unwrappedList = null;
    }

    @Deprecated
    public void add(boolean b) {
        this.add(b ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE);
    }

    @Deprecated
    public List toList() throws TemplateModelException {
        if (this.unwrappedList == null) {
            Class<?> listClass = this.list.getClass();
            List result = null;
            try {
                result = (List)listClass.newInstance();
            }
            catch (Exception e) {
                throw new TemplateModelException("Error instantiating an object of type " + listClass.getName(), e);
            }
            BeansWrapper bw = BeansWrapper.getDefaultInstance();
            for (int i = 0; i < this.list.size(); ++i) {
                Object elem = this.list.get(i);
                if (elem instanceof TemplateModel) {
                    elem = bw.unwrap((TemplateModel)elem);
                }
                result.add(elem);
            }
            this.unwrappedList = result;
        }
        return this.unwrappedList;
    }

    @Override
    public TemplateModel get(int index) throws TemplateModelException {
        try {
            Object value = this.list.get(index);
            if (value instanceof TemplateModel) {
                return (TemplateModel)value;
            }
            TemplateModel tm = this.wrap(value);
            this.list.set(index, tm);
            return tm;
        }
        catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public int size() {
        return this.list.size();
    }

    public SimpleSequence synchronizedWrapper() {
        return new SynchronizedSequence();
    }

    public String toString() {
        return this.list.toString();
    }

    private class SynchronizedSequence
    extends SimpleSequence {
        private SynchronizedSequence() {
            super(SimpleSequence.this.getObjectWrapper());
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void add(Object obj) {
            SimpleSequence simpleSequence = SimpleSequence.this;
            synchronized (simpleSequence) {
                SimpleSequence.this.add(obj);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public TemplateModel get(int i) throws TemplateModelException {
            SimpleSequence simpleSequence = SimpleSequence.this;
            synchronized (simpleSequence) {
                return SimpleSequence.this.get(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int size() {
            SimpleSequence simpleSequence = SimpleSequence.this;
            synchronized (simpleSequence) {
                return SimpleSequence.this.size();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public List toList() throws TemplateModelException {
            SimpleSequence simpleSequence = SimpleSequence.this;
            synchronized (simpleSequence) {
                return SimpleSequence.this.toList();
            }
        }

        @Override
        public SimpleSequence synchronizedWrapper() {
            return this;
        }
    }
}

