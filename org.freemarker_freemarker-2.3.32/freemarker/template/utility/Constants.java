/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template.utility;

import freemarker.template.SimpleNumber;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import java.io.Serializable;
import java.util.NoSuchElementException;

public class Constants {
    public static final TemplateBooleanModel TRUE = TemplateBooleanModel.TRUE;
    public static final TemplateBooleanModel FALSE = TemplateBooleanModel.FALSE;
    public static final TemplateScalarModel EMPTY_STRING = (TemplateScalarModel)TemplateScalarModel.EMPTY_STRING;
    public static final TemplateNumberModel ZERO = new SimpleNumber(0);
    public static final TemplateNumberModel ONE = new SimpleNumber(1);
    public static final TemplateNumberModel MINUS_ONE = new SimpleNumber(-1);
    public static final TemplateModelIterator EMPTY_ITERATOR = new EmptyIteratorModel();
    public static final TemplateCollectionModel EMPTY_COLLECTION = new EmptyCollectionModel();
    public static final TemplateSequenceModel EMPTY_SEQUENCE = new EmptySequenceModel();
    public static final TemplateHashModelEx2 EMPTY_HASH_EX2 = new EmptyHashModel();
    public static final TemplateHashModelEx EMPTY_HASH = EMPTY_HASH_EX2;
    public static final TemplateHashModelEx2.KeyValuePairIterator EMPTY_KEY_VALUE_PAIR_ITERATOR = new EmptyKeyValuePairIterator();

    private static class EmptyKeyValuePairIterator
    implements TemplateHashModelEx2.KeyValuePairIterator {
        private EmptyKeyValuePairIterator() {
        }

        @Override
        public boolean hasNext() throws TemplateModelException {
            return false;
        }

        @Override
        public TemplateHashModelEx2.KeyValuePair next() throws TemplateModelException {
            throw new NoSuchElementException("Can't retrieve element from empty key-value pair iterator.");
        }
    }

    private static class EmptyHashModel
    implements TemplateHashModelEx2,
    Serializable {
        private EmptyHashModel() {
        }

        @Override
        public int size() throws TemplateModelException {
            return 0;
        }

        @Override
        public TemplateCollectionModel keys() throws TemplateModelException {
            return EMPTY_COLLECTION;
        }

        @Override
        public TemplateCollectionModel values() throws TemplateModelException {
            return EMPTY_COLLECTION;
        }

        @Override
        public TemplateModel get(String key) throws TemplateModelException {
            return null;
        }

        @Override
        public boolean isEmpty() throws TemplateModelException {
            return true;
        }

        @Override
        public TemplateHashModelEx2.KeyValuePairIterator keyValuePairIterator() throws TemplateModelException {
            return EMPTY_KEY_VALUE_PAIR_ITERATOR;
        }
    }

    private static class EmptySequenceModel
    implements TemplateSequenceModel,
    Serializable {
        private EmptySequenceModel() {
        }

        @Override
        public TemplateModel get(int index) throws TemplateModelException {
            return null;
        }

        @Override
        public int size() throws TemplateModelException {
            return 0;
        }
    }

    private static class EmptyCollectionModel
    implements TemplateCollectionModel,
    Serializable {
        private EmptyCollectionModel() {
        }

        @Override
        public TemplateModelIterator iterator() throws TemplateModelException {
            return EMPTY_ITERATOR;
        }
    }

    private static class EmptyIteratorModel
    implements TemplateModelIterator,
    Serializable {
        private EmptyIteratorModel() {
        }

        @Override
        public TemplateModel next() throws TemplateModelException {
            throw new TemplateModelException("The collection has no more elements.");
        }

        @Override
        public boolean hasNext() throws TemplateModelException {
            return false;
        }
    }
}

