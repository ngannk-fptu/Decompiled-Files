/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.EvalUtil;
import freemarker.core.Expression;
import freemarker.core.Interpolation;
import freemarker.core.MarkupOutputFormat;
import freemarker.core.OutputFormat;
import freemarker.core.ParameterRole;
import freemarker.core.TemplateElement;
import freemarker.core.TemplateMarkupOutputModel;
import freemarker.core._DelayedToString;
import freemarker.core._TemplateModelException;
import freemarker.template.TemplateException;
import freemarker.template.utility.StringUtil;
import java.io.IOException;
import java.io.Writer;

final class DollarVariable
extends Interpolation {
    private final Expression expression;
    private final Expression escapedExpression;
    private final OutputFormat outputFormat;
    private final MarkupOutputFormat markupOutputFormat;
    private final boolean autoEscape;

    DollarVariable(Expression expression, Expression escapedExpression, OutputFormat outputFormat, boolean autoEscape) {
        this.expression = expression;
        this.escapedExpression = escapedExpression;
        this.outputFormat = outputFormat;
        this.markupOutputFormat = (MarkupOutputFormat)(outputFormat instanceof MarkupOutputFormat ? outputFormat : null);
        this.autoEscape = autoEscape;
    }

    @Override
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        Object moOrStr = this.calculateInterpolatedStringOrMarkup(env);
        Writer out = env.getOut();
        if (moOrStr instanceof String) {
            String s = (String)moOrStr;
            if (this.autoEscape) {
                this.markupOutputFormat.output(s, out);
            } else {
                out.write(s);
            }
        } else {
            TemplateMarkupOutputModel mo = (TemplateMarkupOutputModel)moOrStr;
            MarkupOutputFormat<TemplateMarkupOutputModel> moOF = mo.getOutputFormat();
            if (moOF == this.outputFormat) {
                moOF.output(mo, out);
            } else if (!this.outputFormat.isOutputFormatMixingAllowed()) {
                String srcPlainText = moOF.getSourcePlainText(mo);
                if (srcPlainText == null) {
                    throw new _TemplateModelException(this.escapedExpression, "The value to print is in ", new _DelayedToString(moOF), " format, which differs from the current output format, ", new _DelayedToString(this.outputFormat), ". Format conversion wasn't possible.");
                }
                if (this.markupOutputFormat != null) {
                    this.markupOutputFormat.output(srcPlainText, out);
                } else {
                    out.write(srcPlainText);
                }
            } else if (this.markupOutputFormat != null) {
                this.markupOutputFormat.outputForeign(mo, out);
            } else {
                moOF.output(mo, out);
            }
        }
        return null;
    }

    @Override
    protected Object calculateInterpolatedStringOrMarkup(Environment env) throws TemplateException {
        return EvalUtil.coerceModelToStringOrMarkup(this.escapedExpression.eval(env), this.escapedExpression, null, env);
    }

    @Override
    protected String dump(boolean canonical, boolean inStringLiteral) {
        StringBuilder sb = new StringBuilder();
        int syntax = this.getTemplate().getInterpolationSyntax();
        sb.append(syntax != 22 ? "${" : "[=");
        String exprCF = this.expression.getCanonicalForm();
        sb.append(inStringLiteral ? StringUtil.FTLStringLiteralEnc(exprCF, '\"') : exprCF);
        sb.append(syntax != 22 ? "}" : "]");
        if (!canonical && this.expression != this.escapedExpression) {
            sb.append(" auto-escaped");
        }
        return sb.toString();
    }

    @Override
    String getNodeTypeSymbol() {
        return "${...}";
    }

    @Override
    boolean heedsOpeningWhitespace() {
        return true;
    }

    @Override
    boolean heedsTrailingWhitespace() {
        return true;
    }

    @Override
    int getParameterCount() {
        return 1;
    }

    @Override
    Object getParameterValue(int idx) {
        if (idx != 0) {
            throw new IndexOutOfBoundsException();
        }
        return this.expression;
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        if (idx != 0) {
            throw new IndexOutOfBoundsException();
        }
        return ParameterRole.CONTENT;
    }

    @Override
    boolean isNestedBlockRepeater() {
        return false;
    }
}

