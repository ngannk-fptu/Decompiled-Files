/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BuiltInForMarkupOutput;
import freemarker.core.TemplateMarkupOutputModel;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

class BuiltInsForMarkupOutputs {
    BuiltInsForMarkupOutputs() {
    }

    static class markup_stringBI
    extends BuiltInForMarkupOutput {
        markup_stringBI() {
        }

        @Override
        protected TemplateModel calculateResult(TemplateMarkupOutputModel model) throws TemplateModelException {
            return new SimpleScalar(model.getOutputFormat().getMarkupString(model));
        }
    }
}

