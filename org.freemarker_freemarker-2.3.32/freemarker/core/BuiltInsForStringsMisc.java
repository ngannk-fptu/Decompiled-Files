/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BuiltInForString;
import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.FMParser;
import freemarker.core.FMParserTokenManager;
import freemarker.core.JSONParser;
import freemarker.core.NonNumericalException;
import freemarker.core.OutputFormatBoundBuiltIn;
import freemarker.core.ParseException;
import freemarker.core.ParserConfiguration;
import freemarker.core.SimpleCharStream;
import freemarker.core.TokenMgrError;
import freemarker.core._DelayedGetMessage;
import freemarker.core._DelayedGetMessageWithoutStackTop;
import freemarker.core._DelayedJQuote;
import freemarker.core._MiscTemplateException;
import freemarker.core._ParserConfigurationWithInheritedFormat;
import freemarker.core._TemplateModelException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.SimpleNumber;
import freemarker.template.Template;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import java.io.StringReader;
import java.util.List;

class BuiltInsForStringsMisc {
    private BuiltInsForStringsMisc() {
    }

    static class absolute_template_nameBI
    extends BuiltInForString {
        absolute_template_nameBI() {
        }

        @Override
        TemplateModel calculateResult(String s, Environment env) throws TemplateException {
            return new AbsoluteTemplateNameResult(s, env);
        }

        private class AbsoluteTemplateNameResult
        implements TemplateScalarModel,
        TemplateMethodModelEx {
            private final String pathToResolve;
            private final Environment env;

            public AbsoluteTemplateNameResult(String pathToResolve, Environment env) {
                this.pathToResolve = pathToResolve;
                this.env = env;
            }

            @Override
            public Object exec(List args) throws TemplateModelException {
                absolute_template_nameBI.this.checkMethodArgCount(args, 1);
                return this.resolvePath(absolute_template_nameBI.this.getStringMethodArg(args, 0));
            }

            @Override
            public String getAsString() throws TemplateModelException {
                return this.resolvePath(absolute_template_nameBI.this.getTemplate().getName());
            }

            private String resolvePath(String basePath) throws TemplateModelException {
                try {
                    return this.env.rootBasedToAbsoluteTemplateName(this.env.toFullTemplateName(basePath, this.pathToResolve));
                }
                catch (MalformedTemplateNameException e) {
                    throw new _TemplateModelException((Throwable)e, "Can't resolve ", new _DelayedJQuote(this.pathToResolve), "to absolute template name using base ", new _DelayedJQuote(basePath), "; see cause exception");
                }
            }
        }
    }

    static class numberBI
    extends BuiltInForString {
        numberBI() {
        }

        @Override
        TemplateModel calculateResult(String s, Environment env) throws TemplateException {
            try {
                return new SimpleNumber(env.getArithmeticEngine().toNumber(s));
            }
            catch (NumberFormatException nfe) {
                throw NonNumericalException.newMalformedNumberException(this, s, env);
            }
        }
    }

    static class evalJsonBI
    extends BuiltInForString {
        evalJsonBI() {
        }

        @Override
        TemplateModel calculateResult(String s, Environment env) throws TemplateException {
            try {
                return JSONParser.parse(s);
            }
            catch (JSONParser.JSONParseException e) {
                throw new _MiscTemplateException((Expression)this, env, "Failed to \"?", this.key, "\" string with this error:\n\n", "---begin-message---\n", new _DelayedGetMessage(e), "\n---end-message---", "\n\nThe failing expression:");
            }
        }
    }

    static class evalBI
    extends OutputFormatBoundBuiltIn {
        evalBI() {
        }

        @Override
        protected TemplateModel calculateResult(Environment env) throws TemplateException {
            return this.calculateResult(BuiltInForString.getTargetString(this.target, env), env);
        }

        TemplateModel calculateResult(String s, Environment env) throws TemplateException {
            Template parentTemplate = this.getTemplate();
            Expression exp = null;
            try {
                try {
                    ParserConfiguration pCfg = parentTemplate.getParserConfiguration();
                    SimpleCharStream simpleCharStream = new SimpleCharStream(new StringReader("(" + s + ")"), -1000000000, 1, s.length() + 2);
                    simpleCharStream.setTabSize(pCfg.getTabSize());
                    FMParserTokenManager tkMan = new FMParserTokenManager(simpleCharStream);
                    tkMan.SwitchTo(2);
                    if (pCfg.getOutputFormat() != this.outputFormat) {
                        pCfg = new _ParserConfigurationWithInheritedFormat(pCfg, this.outputFormat, this.autoEscapingPolicy);
                    }
                    FMParser parser = new FMParser(parentTemplate, false, tkMan, pCfg);
                    exp = parser.Expression();
                }
                catch (TokenMgrError e) {
                    throw e.toParseException(parentTemplate);
                }
            }
            catch (ParseException e) {
                throw new _MiscTemplateException((Expression)this, env, "Failed to \"?", this.key, "\" string with this error:\n\n", "---begin-message---\n", new _DelayedGetMessage(e), "\n---end-message---", "\n\nThe failing expression:");
            }
            try {
                return exp.eval(env);
            }
            catch (TemplateException e) {
                throw new _MiscTemplateException((Throwable)e, this, env, "Failed to \"?", this.key, "\" string with this error:\n\n", "---begin-message---\n", new _DelayedGetMessageWithoutStackTop(e), "\n---end-message---", "\n\nThe failing expression:");
            }
        }
    }

    static class booleanBI
    extends BuiltInForString {
        booleanBI() {
        }

        @Override
        TemplateModel calculateResult(String s, Environment env) throws TemplateException {
            boolean b;
            if (s.equals("true")) {
                b = true;
            } else if (s.equals("false")) {
                b = false;
            } else if (s.equals(env.getTrueStringValue())) {
                b = true;
            } else if (s.equals(env.getFalseStringValue())) {
                b = false;
            } else {
                throw new _MiscTemplateException((Expression)this, env, "Can't convert this string to boolean: ", new _DelayedJQuote(s));
            }
            return b ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }
}

