/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.TemplateDateFormat;
import freemarker.core.TemplateFormatUtil;
import freemarker.core.UnparsableValueException;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateModelException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class JavaTemplateDateFormat
extends TemplateDateFormat {
    private final DateFormat javaDateFormat;

    public JavaTemplateDateFormat(DateFormat javaDateFormat) {
        this.javaDateFormat = javaDateFormat;
    }

    @Override
    public String formatToPlainText(TemplateDateModel dateModel) throws TemplateModelException {
        return this.javaDateFormat.format(TemplateFormatUtil.getNonNullDate(dateModel));
    }

    @Override
    public Date parse(String s, int dateType) throws UnparsableValueException {
        try {
            return this.javaDateFormat.parse(s);
        }
        catch (ParseException e) {
            throw new UnparsableValueException(e.getMessage(), e);
        }
    }

    @Override
    public String getDescription() {
        return this.javaDateFormat instanceof SimpleDateFormat ? ((SimpleDateFormat)this.javaDateFormat).toPattern() : this.javaDateFormat.toString();
    }

    @Override
    public boolean isLocaleBound() {
        return true;
    }

    @Override
    public boolean isTimeZoneBound() {
        return true;
    }
}

