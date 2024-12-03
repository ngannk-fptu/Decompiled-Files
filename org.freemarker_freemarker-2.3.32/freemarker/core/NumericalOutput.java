/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.Interpolation;
import freemarker.core.MarkupOutputFormat;
import freemarker.core.ParameterRole;
import freemarker.core.TemplateElement;
import freemarker.template.TemplateException;
import freemarker.template.utility.StringUtil;
import java.io.IOException;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.Locale;

final class NumericalOutput
extends Interpolation {
    private final Expression expression;
    private final boolean hasFormat;
    private final int minFracDigits;
    private final int maxFracDigits;
    private final MarkupOutputFormat autoEscapeOutputFormat;
    private volatile FormatHolder formatCache;

    NumericalOutput(Expression expression, MarkupOutputFormat autoEscapeOutputFormat) {
        this.expression = expression;
        this.hasFormat = false;
        this.minFracDigits = 0;
        this.maxFracDigits = 0;
        this.autoEscapeOutputFormat = autoEscapeOutputFormat;
    }

    NumericalOutput(Expression expression, int minFracDigits, int maxFracDigits, MarkupOutputFormat autoEscapeOutputFormat) {
        this.expression = expression;
        this.hasFormat = true;
        this.minFracDigits = minFracDigits;
        this.maxFracDigits = maxFracDigits;
        this.autoEscapeOutputFormat = autoEscapeOutputFormat;
    }

    @Override
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        String s = this.calculateInterpolatedStringOrMarkup(env);
        Writer out = env.getOut();
        if (this.autoEscapeOutputFormat != null) {
            this.autoEscapeOutputFormat.output(s, out);
        } else {
            out.write(s);
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected String calculateInterpolatedStringOrMarkup(Environment env) throws TemplateException {
        Number num = this.expression.evalToNumber(env);
        FormatHolder fmth = this.formatCache;
        if (fmth == null || !fmth.locale.equals(env.getLocale())) {
            NumericalOutput numericalOutput = this;
            synchronized (numericalOutput) {
                fmth = this.formatCache;
                if (fmth == null || !fmth.locale.equals(env.getLocale())) {
                    NumberFormat fmt = NumberFormat.getNumberInstance(env.getLocale());
                    if (this.hasFormat) {
                        fmt.setMinimumFractionDigits(this.minFracDigits);
                        fmt.setMaximumFractionDigits(this.maxFracDigits);
                    } else {
                        fmt.setMinimumFractionDigits(0);
                        fmt.setMaximumFractionDigits(50);
                    }
                    fmt.setGroupingUsed(false);
                    fmth = this.formatCache = new FormatHolder(fmt, env.getLocale());
                }
            }
        }
        String s = fmth.format.format(num);
        return s;
    }

    @Override
    protected String dump(boolean canonical, boolean inStringLiteral) {
        StringBuilder buf = new StringBuilder("#{");
        String exprCF = this.expression.getCanonicalForm();
        buf.append(inStringLiteral ? StringUtil.FTLStringLiteralEnc(exprCF, '\"') : exprCF);
        if (this.hasFormat) {
            buf.append(" ; ");
            buf.append("m");
            buf.append(this.minFracDigits);
            buf.append("M");
            buf.append(this.maxFracDigits);
        }
        buf.append("}");
        return buf.toString();
    }

    @Override
    String getNodeTypeSymbol() {
        return "#{...}";
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
        return 3;
    }

    @Override
    Object getParameterValue(int idx) {
        switch (idx) {
            case 0: {
                return this.expression;
            }
            case 1: {
                return this.hasFormat ? Integer.valueOf(this.minFracDigits) : null;
            }
            case 2: {
                return this.hasFormat ? Integer.valueOf(this.maxFracDigits) : null;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        switch (idx) {
            case 0: {
                return ParameterRole.CONTENT;
            }
            case 1: {
                return ParameterRole.MINIMUM_DECIMALS;
            }
            case 2: {
                return ParameterRole.MAXIMUM_DECIMALS;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    boolean isNestedBlockRepeater() {
        return false;
    }

    private static class FormatHolder {
        final NumberFormat format;
        final Locale locale;

        FormatHolder(NumberFormat format, Locale locale) {
            this.format = format;
            this.locale = locale;
        }
    }
}

