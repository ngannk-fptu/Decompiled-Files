/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public interface ObjectWrapperAndUnwrapper
extends ObjectWrapper {
    public static final Object CANT_UNWRAP_TO_TARGET_CLASS = new Object();

    public Object unwrap(TemplateModel var1) throws TemplateModelException;

    public Object tryUnwrapTo(TemplateModel var1, Class<?> var2) throws TemplateModelException;
}

