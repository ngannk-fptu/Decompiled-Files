/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BuiltInForHashEx;
import freemarker.core.CollectionAndSequence;
import freemarker.core.Environment;
import freemarker.core.InvalidReferenceException;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;

class BuiltInsForHashes {
    private BuiltInsForHashes() {
    }

    static class valuesBI
    extends BuiltInForHashEx {
        valuesBI() {
        }

        @Override
        TemplateModel calculateResult(TemplateHashModelEx hashExModel, Environment env) throws TemplateModelException, InvalidReferenceException {
            TemplateCollectionModel values = hashExModel.values();
            if (values == null) {
                throw this.newNullPropertyException("values", hashExModel, env);
            }
            return values instanceof TemplateSequenceModel ? values : new CollectionAndSequence(values);
        }
    }

    static class keysBI
    extends BuiltInForHashEx {
        keysBI() {
        }

        @Override
        TemplateModel calculateResult(TemplateHashModelEx hashExModel, Environment env) throws TemplateModelException, InvalidReferenceException {
            TemplateCollectionModel keys = hashExModel.keys();
            if (keys == null) {
                throw this.newNullPropertyException("keys", hashExModel, env);
            }
            return keys instanceof TemplateSequenceModel ? keys : new CollectionAndSequence(keys);
        }
    }
}

