/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.TemplateDateFormat;
import freemarker.core.TemplateValueFormatException;
import freemarker.core.TemplateValueFormatFactory;
import java.util.Locale;
import java.util.TimeZone;

public abstract class TemplateDateFormatFactory
extends TemplateValueFormatFactory {
    public abstract TemplateDateFormat get(String var1, int var2, Locale var3, TimeZone var4, boolean var5, Environment var6) throws TemplateValueFormatException;
}

