/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.TemplateNumberFormat;
import freemarker.template.TemplateException;
import java.text.NumberFormat;

public abstract class CFormat {
    CFormat() {
    }

    abstract TemplateNumberFormat getTemplateNumberFormat(Environment var1);

    @Deprecated
    abstract NumberFormat getLegacyNumberFormat(Environment var1);

    abstract String formatString(String var1, Environment var2) throws TemplateException;

    abstract String getTrueString();

    abstract String getFalseString();

    abstract String getNullString();

    public abstract String getName();
}

