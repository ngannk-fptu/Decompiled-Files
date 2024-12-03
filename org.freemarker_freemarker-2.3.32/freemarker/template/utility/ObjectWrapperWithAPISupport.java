/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template.utility;

import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModelException;

public interface ObjectWrapperWithAPISupport
extends ObjectWrapper {
    public TemplateHashModel wrapAsAPI(Object var1) throws TemplateModelException;
}

