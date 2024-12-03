/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.ISOLikeTemplateDateFormatFactory;
import freemarker.core.ISOTemplateDateFormat;
import freemarker.core.InvalidFormatParametersException;
import freemarker.core.TemplateDateFormat;
import freemarker.core.UnknownDateTypeFormattingUnsupportedException;
import java.util.Locale;
import java.util.TimeZone;

class ISOTemplateDateFormatFactory
extends ISOLikeTemplateDateFormatFactory {
    static final ISOTemplateDateFormatFactory INSTANCE = new ISOTemplateDateFormatFactory();

    private ISOTemplateDateFormatFactory() {
    }

    @Override
    public TemplateDateFormat get(String params, int dateType, Locale locale, TimeZone timeZone, boolean zonelessInput, Environment env) throws UnknownDateTypeFormattingUnsupportedException, InvalidFormatParametersException {
        return new ISOTemplateDateFormat(params, 3, dateType, zonelessInput, timeZone, this, env);
    }
}

