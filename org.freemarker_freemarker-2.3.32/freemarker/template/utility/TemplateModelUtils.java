/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template.utility;

import freemarker.core.CollectionAndSequence;
import freemarker.core._MessageUtil;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateScalarModel;
import freemarker.template._ObjectWrappers;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.Constants;
import freemarker.template.utility.NullArgumentException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class TemplateModelUtils {
    private TemplateModelUtils() {
    }

    public static final TemplateHashModelEx2.KeyValuePairIterator getKeyValuePairIterator(TemplateHashModelEx hash) throws TemplateModelException {
        return hash instanceof TemplateHashModelEx2 ? ((TemplateHashModelEx2)hash).keyValuePairIterator() : new TemplateHashModelExKeyValuePairIterator(hash);
    }

    public static TemplateHashModel wrapAsHashUnion(ObjectWrapper objectWrapper, Object ... hashLikeObjects) throws TemplateModelException {
        return TemplateModelUtils.wrapAsHashUnion(objectWrapper, Arrays.asList(hashLikeObjects));
    }

    public static TemplateHashModel wrapAsHashUnion(ObjectWrapper objectWrapper, List<?> hashLikeObjects) throws TemplateModelException {
        NullArgumentException.check("hashLikeObjects", hashLikeObjects);
        ArrayList<TemplateHashModel> hashes = new ArrayList<TemplateHashModel>(hashLikeObjects.size());
        boolean allTHMEx = true;
        for (Object hashLikeObject : hashLikeObjects) {
            if (hashLikeObject == null) continue;
            TemplateModel tm = hashLikeObject instanceof TemplateModel ? (TemplateModel)hashLikeObject : objectWrapper.wrap(hashLikeObject);
            if (!(tm instanceof TemplateHashModelEx)) {
                allTHMEx = false;
                if (!(tm instanceof TemplateHashModel)) {
                    throw new TemplateModelException("One of the objects of the hash union is not hash-like: " + ClassUtil.getFTLTypeDescription(tm));
                }
            }
            hashes.add((TemplateHashModel)tm);
        }
        return hashes.isEmpty() ? Constants.EMPTY_HASH : (hashes.size() == 1 ? (TemplateHashModel)hashes.get(0) : (allTHMEx ? new HashExUnionModel(hashes) : new HashUnionModel((List<? extends TemplateHashModel>)hashes)));
    }

    private static final class HashExUnionModel
    extends AbstractHashUnionModel<TemplateHashModelEx>
    implements TemplateHashModelEx {
        private CollectionAndSequence keys;
        private CollectionAndSequence values;

        private HashExUnionModel(List<? extends TemplateHashModelEx> hashes) {
            super(hashes);
        }

        @Override
        public int size() throws TemplateModelException {
            this.initKeys();
            return this.keys.size();
        }

        @Override
        public TemplateCollectionModel keys() throws TemplateModelException {
            this.initKeys();
            return this.keys;
        }

        @Override
        public TemplateCollectionModel values() throws TemplateModelException {
            this.initValues();
            return this.values;
        }

        private void initKeys() throws TemplateModelException {
            if (this.keys == null) {
                HashSet<String> keySet = new HashSet<String>();
                SimpleSequence keySeq = new SimpleSequence(_ObjectWrappers.SAFE_OBJECT_WRAPPER);
                for (TemplateHashModelEx hash : this.hashes) {
                    HashExUnionModel.addKeys(keySet, keySeq, hash);
                }
                this.keys = new CollectionAndSequence(keySeq);
            }
        }

        private static void addKeys(Set<String> keySet, SimpleSequence keySeq, TemplateHashModelEx hash) throws TemplateModelException {
            TemplateModelIterator it = hash.keys().iterator();
            while (it.hasNext()) {
                TemplateScalarModel tsm = (TemplateScalarModel)it.next();
                if (!keySet.add(tsm.getAsString())) continue;
                keySeq.add(tsm);
            }
        }

        private void initValues() throws TemplateModelException {
            if (this.values == null) {
                SimpleSequence seq = new SimpleSequence(this.size(), (ObjectWrapper)_ObjectWrappers.SAFE_OBJECT_WRAPPER);
                int ln = this.keys.size();
                for (int i = 0; i < ln; ++i) {
                    seq.add(this.get(((TemplateScalarModel)this.keys.get(i)).getAsString()));
                }
                this.values = new CollectionAndSequence(seq);
            }
        }
    }

    private static class HashUnionModel
    extends AbstractHashUnionModel<TemplateHashModel> {
        HashUnionModel(List<? extends TemplateHashModel> hashes) {
            super(hashes);
        }
    }

    private static abstract class AbstractHashUnionModel<T extends TemplateHashModel>
    implements TemplateHashModel {
        protected final List<? extends T> hashes;

        public AbstractHashUnionModel(List<? extends T> hashes) {
            this.hashes = hashes;
        }

        @Override
        public TemplateModel get(String key) throws TemplateModelException {
            for (int i = this.hashes.size() - 1; i >= 0; --i) {
                TemplateModel value = ((TemplateHashModel)this.hashes.get(i)).get(key);
                if (value == null) continue;
                return value;
            }
            return null;
        }

        @Override
        public boolean isEmpty() throws TemplateModelException {
            for (TemplateHashModel hash : this.hashes) {
                if (hash.isEmpty()) continue;
                return false;
            }
            return true;
        }
    }

    private static class TemplateHashModelExKeyValuePairIterator
    implements TemplateHashModelEx2.KeyValuePairIterator {
        private final TemplateHashModelEx hash;
        private final TemplateModelIterator keyIter;

        private TemplateHashModelExKeyValuePairIterator(TemplateHashModelEx hash) throws TemplateModelException {
            this.hash = hash;
            this.keyIter = hash.keys().iterator();
        }

        @Override
        public boolean hasNext() throws TemplateModelException {
            return this.keyIter.hasNext();
        }

        @Override
        public TemplateHashModelEx2.KeyValuePair next() throws TemplateModelException {
            final TemplateModel key = this.keyIter.next();
            if (!(key instanceof TemplateScalarModel)) {
                throw _MessageUtil.newKeyValuePairListingNonStringKeyExceptionMessage(key, this.hash);
            }
            return new TemplateHashModelEx2.KeyValuePair(){

                @Override
                public TemplateModel getKey() throws TemplateModelException {
                    return key;
                }

                @Override
                public TemplateModel getValue() throws TemplateModelException {
                    return TemplateHashModelExKeyValuePairIterator.this.hash.get(((TemplateScalarModel)key).getAsString());
                }
            };
        }
    }
}

