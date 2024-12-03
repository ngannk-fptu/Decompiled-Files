/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BackwardCompatibleTemplateNumberFormat;
import freemarker.core.TemplateFormatUtil;
import freemarker.core.UnformattableValueException;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import java.text.NumberFormat;

class JavaTemplateNumberFormat
extends BackwardCompatibleTemplateNumberFormat {
    private final String formatString;
    private final NumberFormat javaNumberFormat;

    public JavaTemplateNumberFormat(NumberFormat javaNumberFormat, String formatString) {
        this.formatString = formatString;
        this.javaNumberFormat = javaNumberFormat;
    }

    @Override
    public String formatToPlainText(TemplateNumberModel numberModel) throws UnformattableValueException, TemplateModelException {
        Number number = TemplateFormatUtil.getNonNullNumber(numberModel);
        return this.format(number);
    }

    @Override
    public boolean isLocaleBound() {
        return true;
    }

    @Override
    String format(Number number) throws UnformattableValueException {
        try {
            return this.javaNumberFormat.format(number);
        }
        catch (ArithmeticException e) {
            throw new UnformattableValueException("This format can't format the " + number + " number. Reason: " + e.getMessage(), e);
        }
    }

    public NumberFormat getJavaNumberFormat() {
        return this.javaNumberFormat;
    }

    @Override
    public String getDescription() {
        return this.formatString;
    }
}

