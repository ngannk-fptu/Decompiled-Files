/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.TemplateValueFormat;
import freemarker.core.TemplateValueFormatException;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateModelException;

public abstract class TemplateDateFormat
extends TemplateValueFormat {
    public abstract String formatToPlainText(TemplateDateModel var1) throws TemplateValueFormatException, TemplateModelException;

    public Object format(TemplateDateModel dateModel) throws TemplateValueFormatException, TemplateModelException {
        return this.formatToPlainText(dateModel);
    }

    public abstract Object parse(String var1, int var2) throws TemplateValueFormatException;

    public abstract boolean isLocaleBound();

    public abstract boolean isTimeZoneBound();
}

