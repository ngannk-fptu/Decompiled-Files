/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.core.Environment;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import java.util.Locale;

public abstract class LocalizedString
implements TemplateScalarModel {
    @Override
    public String getAsString() throws TemplateModelException {
        Environment env = Environment.getCurrentEnvironment();
        Locale locale = env.getLocale();
        return this.getLocalizedString(locale);
    }

    public abstract String getLocalizedString(Locale var1) throws TemplateModelException;
}

