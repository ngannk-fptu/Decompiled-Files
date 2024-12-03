/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.InvalidReferenceException;
import freemarker.core.UnexpectedTypeException;
import freemarker.core._ErrorDescriptionBuilder;
import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.utility.CollectionUtils;

public class NonSequenceOrCollectionException
extends UnexpectedTypeException {
    private static final Class[] EXPECTED_TYPES = new Class[]{TemplateSequenceModel.class, TemplateCollectionModel.class};
    private static final String ITERABLE_SUPPORT_HINT = "The problematic value is a java.lang.Iterable. Using DefaultObjectWrapper(..., iterableSupport=true) as the object_wrapper setting of the FreeMarker configuration should solve this.";

    public NonSequenceOrCollectionException(Environment env) {
        super(env, "Expecting sequence or collection value here");
    }

    public NonSequenceOrCollectionException(String description, Environment env) {
        super(env, description);
    }

    NonSequenceOrCollectionException(Environment env, _ErrorDescriptionBuilder description) {
        super(env, description);
    }

    NonSequenceOrCollectionException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
        this(blamed, model, CollectionUtils.EMPTY_OBJECT_ARRAY, env);
    }

    NonSequenceOrCollectionException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
        this(blamed, model, new Object[]{tip}, env);
    }

    NonSequenceOrCollectionException(Expression blamed, TemplateModel model, Object[] tips, Environment env) throws InvalidReferenceException {
        super(blamed, model, "sequence or collection", EXPECTED_TYPES, NonSequenceOrCollectionException.extendTipsIfIterable(model, tips), env);
    }

    private static Object[] extendTipsIfIterable(TemplateModel model, Object[] tips) {
        if (NonSequenceOrCollectionException.isWrappedIterable(model)) {
            int tipsLen = tips != null ? tips.length : 0;
            Object[] extendedTips = new Object[tipsLen + 1];
            for (int i = 0; i < tipsLen; ++i) {
                extendedTips[i] = tips[i];
            }
            extendedTips[tipsLen] = ITERABLE_SUPPORT_HINT;
            return extendedTips;
        }
        return tips;
    }

    public static boolean isWrappedIterable(TemplateModel model) {
        return model instanceof WrapperTemplateModel && ((WrapperTemplateModel)model).getWrappedObject() instanceof Iterable;
    }
}

