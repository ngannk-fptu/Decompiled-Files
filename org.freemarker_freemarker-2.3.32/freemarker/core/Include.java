/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BugException;
import freemarker.core.Environment;
import freemarker.core.EvalUtil;
import freemarker.core.Expression;
import freemarker.core.NonBooleanException;
import freemarker.core.ParameterRole;
import freemarker.core.ParseException;
import freemarker.core.StringLiteral;
import freemarker.core.TemplateElement;
import freemarker.core._DelayedGetMessage;
import freemarker.core._DelayedJQuote;
import freemarker.core._MiscTemplateException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.utility.StringUtil;
import java.io.IOException;

final class Include
extends TemplateElement {
    private final Expression includedTemplateNameExp;
    private final Expression encodingExp;
    private final Expression parseExp;
    private final Expression ignoreMissingExp;
    private final String encoding;
    private final Boolean parse;
    private final Boolean ignoreMissingExpPrecalcedValue;

    Include(Template template, Expression includedTemplatePathExp, Expression encodingExp, Expression parseExp, Expression ignoreMissingExp) throws ParseException {
        block21: {
            this.includedTemplateNameExp = includedTemplatePathExp;
            this.encodingExp = encodingExp;
            if (encodingExp == null) {
                this.encoding = null;
            } else if (encodingExp.isLiteral()) {
                try {
                    TemplateModel tm = encodingExp.eval(null);
                    if (!(tm instanceof TemplateScalarModel)) {
                        throw new ParseException("Expected a string as the value of the \"encoding\" argument", encodingExp);
                    }
                    this.encoding = ((TemplateScalarModel)tm).getAsString();
                }
                catch (TemplateException e) {
                    throw new BugException(e);
                }
            } else {
                this.encoding = null;
            }
            this.parseExp = parseExp;
            if (parseExp == null) {
                this.parse = Boolean.TRUE;
            } else if (parseExp.isLiteral()) {
                try {
                    if (parseExp instanceof StringLiteral) {
                        this.parse = StringUtil.getYesNo(parseExp.evalAndCoerceToPlainText(null));
                    }
                    try {
                        this.parse = parseExp.evalToBoolean(template.getConfiguration());
                    }
                    catch (NonBooleanException e) {
                        throw new ParseException("Expected a boolean or string as the value of the parse attribute", parseExp, e);
                    }
                }
                catch (TemplateException e) {
                    throw new BugException(e);
                }
            } else {
                this.parse = null;
            }
            this.ignoreMissingExp = ignoreMissingExp;
            if (ignoreMissingExp != null && ignoreMissingExp.isLiteral()) {
                try {
                    try {
                        this.ignoreMissingExpPrecalcedValue = ignoreMissingExp.evalToBoolean(template.getConfiguration());
                        break block21;
                    }
                    catch (NonBooleanException e) {
                        throw new ParseException("Expected a boolean as the value of the \"ignore_missing\" attribute", ignoreMissingExp, e);
                    }
                }
                catch (TemplateException e) {
                    throw new BugException(e);
                }
            }
            this.ignoreMissingExpPrecalcedValue = null;
        }
    }

    @Override
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        Template includedTemplate;
        TemplateModel tm;
        String encoding;
        String fullIncludedTemplateName;
        String includedTemplateName = this.includedTemplateNameExp.evalAndCoerceToPlainText(env);
        try {
            fullIncludedTemplateName = env.toFullTemplateName(this.getTemplate().getName(), includedTemplateName);
        }
        catch (MalformedTemplateNameException e) {
            throw new _MiscTemplateException((Throwable)e, env, "Malformed template name ", new _DelayedJQuote(e.getTemplateName()), ":\n", e.getMalformednessDescription());
        }
        String string = this.encoding != null ? this.encoding : (encoding = this.encodingExp != null ? this.encodingExp.evalAndCoerceToPlainText(env) : null);
        boolean parse = this.parse != null ? this.parse : ((tm = this.parseExp.eval(env)) instanceof TemplateScalarModel ? this.getYesNo(this.parseExp, EvalUtil.modelToString((TemplateScalarModel)tm, this.parseExp, env)) : this.parseExp.modelToBoolean(tm, env));
        boolean ignoreMissing = this.ignoreMissingExpPrecalcedValue != null ? this.ignoreMissingExpPrecalcedValue : (this.ignoreMissingExp != null ? this.ignoreMissingExp.evalToBoolean(env) : false);
        try {
            includedTemplate = env.getTemplateForInclusion(fullIncludedTemplateName, encoding, parse, ignoreMissing);
        }
        catch (IOException e) {
            throw new _MiscTemplateException((Throwable)e, env, "Template inclusion failed (for parameter value ", new _DelayedJQuote(includedTemplateName), "):\n", new _DelayedGetMessage(e));
        }
        if (includedTemplate != null) {
            env.include(includedTemplate);
        }
        return null;
    }

    @Override
    protected String dump(boolean canonical) {
        StringBuilder buf = new StringBuilder();
        if (canonical) {
            buf.append('<');
        }
        buf.append(this.getNodeTypeSymbol());
        buf.append(' ');
        buf.append(this.includedTemplateNameExp.getCanonicalForm());
        if (this.encodingExp != null) {
            buf.append(" encoding=").append(this.encodingExp.getCanonicalForm());
        }
        if (this.parseExp != null) {
            buf.append(" parse=").append(this.parseExp.getCanonicalForm());
        }
        if (this.ignoreMissingExp != null) {
            buf.append(" ignore_missing=").append(this.ignoreMissingExp.getCanonicalForm());
        }
        if (canonical) {
            buf.append("/>");
        }
        return buf.toString();
    }

    @Override
    String getNodeTypeSymbol() {
        return "#include";
    }

    @Override
    int getParameterCount() {
        return 4;
    }

    @Override
    Object getParameterValue(int idx) {
        switch (idx) {
            case 0: {
                return this.includedTemplateNameExp;
            }
            case 1: {
                return this.parseExp;
            }
            case 2: {
                return this.encodingExp;
            }
            case 3: {
                return this.ignoreMissingExp;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        switch (idx) {
            case 0: {
                return ParameterRole.TEMPLATE_NAME;
            }
            case 1: {
                return ParameterRole.PARSE_PARAMETER;
            }
            case 2: {
                return ParameterRole.ENCODING_PARAMETER;
            }
            case 3: {
                return ParameterRole.IGNORE_MISSING_PARAMETER;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    boolean isNestedBlockRepeater() {
        return false;
    }

    private boolean getYesNo(Expression exp, String s) throws TemplateException {
        try {
            return StringUtil.getYesNo(s);
        }
        catch (IllegalArgumentException iae) {
            throw new _MiscTemplateException(exp, "Value must be boolean (or one of these strings: \"n\", \"no\", \"f\", \"false\", \"y\", \"yes\", \"t\", \"true\"), but it was ", new _DelayedJQuote(s), ".");
        }
    }

    @Override
    boolean isShownInStackTrace() {
        return true;
    }
}

