/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.TemplatePostProcessorException;
import freemarker.template.Template;

abstract class TemplatePostProcessor {
    TemplatePostProcessor() {
    }

    public abstract void postProcess(Template var1) throws TemplatePostProcessorException;
}

