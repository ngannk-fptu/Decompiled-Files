/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.SimpleObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public interface ObjectWrapper {
    @Deprecated
    public static final ObjectWrapper BEANS_WRAPPER = BeansWrapper.getDefaultInstance();
    @Deprecated
    public static final ObjectWrapper DEFAULT_WRAPPER = DefaultObjectWrapper.instance;
    @Deprecated
    public static final ObjectWrapper SIMPLE_WRAPPER = SimpleObjectWrapper.instance;

    public TemplateModel wrap(Object var1) throws TemplateModelException;
}

