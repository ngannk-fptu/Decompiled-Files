/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.EvalUtil;
import freemarker.core.Expression;
import freemarker.core.FMParser;
import freemarker.core.FMParserTokenManager;
import freemarker.core.Interpolation;
import freemarker.core.OutputFormat;
import freemarker.core.ParameterRole;
import freemarker.core.ParseException;
import freemarker.core.ParserConfiguration;
import freemarker.core.SimpleCharStream;
import freemarker.core.TemplateMarkupOutputModel;
import freemarker.template.SimpleScalar;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.utility.StringUtil;
import java.io.StringReader;
import java.util.List;

final class StringLiteral
extends Expression
implements TemplateScalarModel {
    private final String value;
    private List<Object> dynamicValue;

    StringLiteral(String value) {
        this.value = value;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void parseValue(FMParser parentParser, OutputFormat outputFormat) throws ParseException {
        Template parentTemplate = this.getTemplate();
        ParserConfiguration pcfg = parentTemplate.getParserConfiguration();
        int intSyn = pcfg.getInterpolationSyntax();
        if (this.value.length() > 3 && ((intSyn == 20 || intSyn == 21) && (this.value.indexOf("${") != -1 || intSyn == 20 && this.value.indexOf("#{") != -1) || intSyn == 22 && this.value.indexOf("[=") != -1)) {
            try {
                SimpleCharStream simpleCharacterStream = new SimpleCharStream(new StringReader(this.value), this.beginLine, this.beginColumn + 1, this.value.length());
                simpleCharacterStream.setTabSize(pcfg.getTabSize());
                FMParserTokenManager tkMan = new FMParserTokenManager(simpleCharacterStream);
                FMParser parser = new FMParser(parentTemplate, false, tkMan, pcfg);
                parser.setupStringLiteralMode(parentParser, outputFormat);
                try {
                    this.dynamicValue = parser.StaticTextAndInterpolations();
                }
                finally {
                    parser.tearDownStringLiteralMode(parentParser);
                }
            }
            catch (ParseException e) {
                e.setTemplateName(parentTemplate.getSourceName());
                throw e;
            }
            this.constantValue = null;
        }
    }

    @Override
    TemplateModel _eval(Environment env) throws TemplateException {
        if (this.dynamicValue == null) {
            return new SimpleScalar(this.value);
        }
        StringBuilder plainTextResult = null;
        TemplateModel markupResult = null;
        for (Object part : this.dynamicValue) {
            Object calcedPart;
            Object object = calcedPart = part instanceof String ? part : ((Interpolation)part).calculateInterpolatedStringOrMarkup(env);
            if (markupResult != null) {
                TemplateMarkupOutputModel partMO = calcedPart instanceof String ? markupResult.getOutputFormat().fromPlainTextByEscaping((String)calcedPart) : (TemplateMarkupOutputModel)calcedPart;
                markupResult = EvalUtil.concatMarkupOutputs(this, (TemplateMarkupOutputModel)markupResult, partMO);
                continue;
            }
            if (calcedPart instanceof String) {
                String partStr = (String)calcedPart;
                if (plainTextResult == null) {
                    plainTextResult = new StringBuilder(partStr);
                    continue;
                }
                plainTextResult.append(partStr);
                continue;
            }
            TemplateMarkupOutputModel moPart = (TemplateMarkupOutputModel)calcedPart;
            if (plainTextResult != null) {
                Object leftHandMO = moPart.getOutputFormat().fromPlainTextByEscaping(plainTextResult.toString());
                markupResult = EvalUtil.concatMarkupOutputs(this, leftHandMO, moPart);
                plainTextResult = null;
                continue;
            }
            markupResult = moPart;
        }
        return markupResult != null ? markupResult : (plainTextResult != null ? new SimpleScalar(plainTextResult.toString()) : SimpleScalar.EMPTY_STRING);
    }

    @Override
    public String getAsString() {
        return this.value;
    }

    boolean isSingleInterpolationLiteral() {
        return this.dynamicValue != null && this.dynamicValue.size() == 1 && this.dynamicValue.get(0) instanceof Interpolation;
    }

    @Override
    public String getCanonicalForm() {
        if (this.dynamicValue == null) {
            return StringUtil.ftlQuote(this.value);
        }
        StringBuilder sb = new StringBuilder();
        sb.append('\"');
        for (Object child : this.dynamicValue) {
            if (child instanceof Interpolation) {
                sb.append(((Interpolation)child).getCanonicalFormInStringLiteral());
                continue;
            }
            sb.append(StringUtil.FTLStringLiteralEnc((String)child, '\"'));
        }
        sb.append('\"');
        return sb.toString();
    }

    @Override
    String getNodeTypeSymbol() {
        return this.dynamicValue == null ? this.getCanonicalForm() : "dynamic \"...\"";
    }

    @Override
    boolean isLiteral() {
        return this.dynamicValue == null;
    }

    @Override
    protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
        StringLiteral cloned = new StringLiteral(this.value);
        cloned.dynamicValue = this.dynamicValue;
        return cloned;
    }

    @Override
    int getParameterCount() {
        return this.dynamicValue == null ? 0 : this.dynamicValue.size();
    }

    @Override
    Object getParameterValue(int idx) {
        this.checkIndex(idx);
        return this.dynamicValue.get(idx);
    }

    private void checkIndex(int idx) {
        if (this.dynamicValue == null || idx >= this.dynamicValue.size()) {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        this.checkIndex(idx);
        return ParameterRole.VALUE_PART;
    }
}

