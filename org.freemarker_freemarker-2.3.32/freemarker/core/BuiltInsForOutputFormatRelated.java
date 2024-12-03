/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.EvalUtil;
import freemarker.core.MarkupOutputFormat;
import freemarker.core.MarkupOutputFormatBoundBuiltIn;
import freemarker.core.TemplateMarkupOutputModel;
import freemarker.core._DelayedToString;
import freemarker.core._TemplateModelException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

class BuiltInsForOutputFormatRelated {
    BuiltInsForOutputFormatRelated() {
    }

    static abstract class AbstractConverterBI
    extends MarkupOutputFormatBoundBuiltIn {
        AbstractConverterBI() {
        }

        @Override
        protected TemplateModel calculateResult(Environment env) throws TemplateException {
            TemplateModel lhoTM = this.target.eval(env);
            Object lhoMOOrStr = EvalUtil.coerceModelToStringOrMarkup(lhoTM, this.target, null, env);
            MarkupOutputFormat contextOF = this.outputFormat;
            if (lhoMOOrStr instanceof String) {
                return this.calculateResult((String)lhoMOOrStr, contextOF, env);
            }
            TemplateMarkupOutputModel lhoMO = (TemplateMarkupOutputModel)lhoMOOrStr;
            MarkupOutputFormat<TemplateMarkupOutputModel> lhoOF = lhoMO.getOutputFormat();
            if (lhoOF == contextOF || contextOF.isOutputFormatMixingAllowed()) {
                return lhoMO;
            }
            String lhoPlainTtext = lhoOF.getSourcePlainText(lhoMO);
            if (lhoPlainTtext == null) {
                throw new _TemplateModelException(this.target, "The left side operand of ?", this.key, " is in ", new _DelayedToString(lhoOF), " format, which differs from the current output format, ", new _DelayedToString(contextOF), ". Conversion wasn't possible.");
            }
            return contextOF.fromPlainTextByEscaping(lhoPlainTtext);
        }

        protected abstract TemplateModel calculateResult(String var1, MarkupOutputFormat var2, Environment var3) throws TemplateException;
    }

    static class escBI
    extends AbstractConverterBI {
        escBI() {
        }

        @Override
        protected TemplateModel calculateResult(String lho, MarkupOutputFormat outputFormat, Environment env) throws TemplateException {
            return outputFormat.fromPlainTextByEscaping(lho);
        }
    }

    static class no_escBI
    extends AbstractConverterBI {
        no_escBI() {
        }

        @Override
        protected TemplateModel calculateResult(String lho, MarkupOutputFormat outputFormat, Environment env) throws TemplateException {
            return outputFormat.fromMarkup(lho);
        }
    }
}

