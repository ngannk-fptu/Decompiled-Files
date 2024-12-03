/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.util.ModelFactory;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateSequenceModel;
import java.lang.reflect.Array;

public class ArrayModel
extends BeanModel
implements TemplateCollectionModel,
TemplateSequenceModel {
    static final ModelFactory FACTORY = new ModelFactory(){

        @Override
        public TemplateModel create(Object object, ObjectWrapper wrapper) {
            return new ArrayModel(object, (BeansWrapper)wrapper);
        }
    };
    private final int length;

    public ArrayModel(Object array, BeansWrapper wrapper) {
        super(array, wrapper);
        Class<?> clazz = array.getClass();
        if (!clazz.isArray()) {
            throw new IllegalArgumentException("Object is not an array, it's " + array.getClass().getName());
        }
        this.length = Array.getLength(array);
    }

    @Override
    public TemplateModelIterator iterator() {
        return new Iterator();
    }

    @Override
    public TemplateModel get(int index) throws TemplateModelException {
        try {
            return this.wrap(Array.get(this.object, index));
        }
        catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public int size() {
        return this.length;
    }

    @Override
    public boolean isEmpty() {
        return this.length == 0;
    }

    private class Iterator
    implements TemplateSequenceModel,
    TemplateModelIterator {
        private int position = 0;

        private Iterator() {
        }

        @Override
        public boolean hasNext() {
            return this.position < ArrayModel.this.length;
        }

        @Override
        public TemplateModel get(int index) throws TemplateModelException {
            return ArrayModel.this.get(index);
        }

        @Override
        public TemplateModel next() throws TemplateModelException {
            return this.position < ArrayModel.this.length ? this.get(this.position++) : null;
        }

        @Override
        public int size() {
            return ArrayModel.this.size();
        }
    }
}

