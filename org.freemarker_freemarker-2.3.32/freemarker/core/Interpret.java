/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.DynamicKeyName;
import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.NumberLiteral;
import freemarker.core.OutputFormatBoundBuiltIn;
import freemarker.core.ParserConfiguration;
import freemarker.core.UnexpectedTypeException;
import freemarker.core._DelayedGetMessage;
import freemarker.core._MiscTemplateException;
import freemarker.core._ParserConfigurationWithInheritedFormat;
import freemarker.core._TemplateModelException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.TemplateTransformModel;
import freemarker.template._VersionInts;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.Map;

class Interpret
extends OutputFormatBoundBuiltIn {
    Interpret() {
    }

    @Override
    protected TemplateModel calculateResult(Environment env) throws TemplateException {
        Template interpretedTemplate;
        TemplateModel model = this.target.eval(env);
        Expression sourceExpr = null;
        String id = "anonymous_interpreted";
        if (model instanceof TemplateSequenceModel) {
            sourceExpr = (Expression)new DynamicKeyName(this.target, new NumberLiteral(0)).copyLocationFrom(this.target);
            if (((TemplateSequenceModel)model).size() > 1) {
                id = ((Expression)new DynamicKeyName(this.target, new NumberLiteral(1)).copyLocationFrom(this.target)).evalAndCoerceToPlainText(env);
            }
        } else if (model instanceof TemplateScalarModel) {
            sourceExpr = this.target;
        } else {
            throw new UnexpectedTypeException(this.target, model, "sequence or string", new Class[]{TemplateSequenceModel.class, TemplateScalarModel.class}, env);
        }
        String templateSource = sourceExpr.evalAndCoerceToPlainText(env);
        Template parentTemplate = env.getConfiguration().getIncompatibleImprovements().intValue() >= _VersionInts.V_2_3_26 ? env.getCurrentTemplate() : env.getTemplate();
        try {
            ParserConfiguration pCfg = parentTemplate.getParserConfiguration();
            if (pCfg.getOutputFormat() != this.outputFormat) {
                pCfg = new _ParserConfigurationWithInheritedFormat(pCfg, this.outputFormat, this.autoEscapingPolicy);
            }
            interpretedTemplate = new Template((parentTemplate.getName() != null ? parentTemplate.getName() : "nameless_template") + "->" + id, null, new StringReader(templateSource), parentTemplate.getConfiguration(), pCfg, null);
        }
        catch (IOException e) {
            throw new _MiscTemplateException((Expression)this, (Throwable)e, env, "Template parsing with \"?", this.key, "\" has failed with this error:\n\n", "---begin-message---\n", new _DelayedGetMessage(e), "\n---end-message---", "\n\nThe failed expression:");
        }
        interpretedTemplate.setLocale(env.getLocale());
        return new TemplateProcessorModel(interpretedTemplate);
    }

    private class TemplateProcessorModel
    implements TemplateTransformModel {
        private final Template template;

        TemplateProcessorModel(Template template) {
            this.template = template;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Writer getWriter(final Writer out, Map args) throws TemplateModelException, IOException {
            try {
                Environment env = Environment.getCurrentEnvironment();
                boolean lastFIRE = env.setFastInvalidReferenceExceptions(false);
                try {
                    env.include(this.template);
                }
                finally {
                    env.setFastInvalidReferenceExceptions(lastFIRE);
                }
            }
            catch (Exception e) {
                throw new _TemplateModelException((Throwable)e, "Template created with \"?", Interpret.this.key, "\" has stopped with this error:\n\n", "---begin-message---\n", new _DelayedGetMessage(e), "\n---end-message---");
            }
            return new Writer(out){

                @Override
                public void close() {
                }

                @Override
                public void flush() throws IOException {
                    out.flush();
                }

                @Override
                public void write(char[] cbuf, int off, int len) throws IOException {
                    out.write(cbuf, off, len);
                }
            };
        }
    }
}

