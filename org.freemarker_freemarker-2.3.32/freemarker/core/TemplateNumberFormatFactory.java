/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.TemplateNumberFormat;
import freemarker.core.TemplateValueFormatException;
import freemarker.core.TemplateValueFormatFactory;
import java.util.Locale;

public abstract class TemplateNumberFormatFactory
extends TemplateValueFormatFactory {
    public abstract TemplateNumberFormat get(String var1, Locale var2, Environment var3) throws TemplateValueFormatException;
}

