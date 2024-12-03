/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.utility.Constants;
import java.util.List;

final class GeneralPurposeNothing
implements TemplateBooleanModel,
TemplateScalarModel,
TemplateSequenceModel,
TemplateHashModelEx2,
TemplateMethodModelEx {
    private static final TemplateModel instance = new GeneralPurposeNothing();

    private GeneralPurposeNothing() {
    }

    static TemplateModel getInstance() {
        return instance;
    }

    @Override
    public String getAsString() {
        return "";
    }

    @Override
    public boolean getAsBoolean() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public TemplateModel get(int i) throws TemplateModelException {
        throw new TemplateModelException("Can't get item from an empty sequence.");
    }

    @Override
    public TemplateModel get(String key) {
        return null;
    }

    @Override
    public Object exec(List args) {
        return null;
    }

    @Override
    public TemplateCollectionModel keys() {
        return Constants.EMPTY_COLLECTION;
    }

    @Override
    public TemplateCollectionModel values() {
        return Constants.EMPTY_COLLECTION;
    }

    @Override
    public TemplateHashModelEx2.KeyValuePairIterator keyValuePairIterator() throws TemplateModelException {
        return Constants.EMPTY_KEY_VALUE_PAIR_ITERATOR;
    }
}

