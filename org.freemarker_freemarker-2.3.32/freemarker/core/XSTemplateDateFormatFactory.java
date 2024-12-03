/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.ISOLikeTemplateDateFormatFactory;
import freemarker.core.InvalidFormatParametersException;
import freemarker.core.TemplateDateFormat;
import freemarker.core.UnknownDateTypeFormattingUnsupportedException;
import freemarker.core.XSTemplateDateFormat;
import java.util.Locale;
import java.util.TimeZone;

class XSTemplateDateFormatFactory
extends ISOLikeTemplateDateFormatFactory {
    static final XSTemplateDateFormatFactory INSTANCE = new XSTemplateDateFormatFactory();

    private XSTemplateDateFormatFactory() {
    }

    @Override
    public TemplateDateFormat get(String params, int dateType, Locale locale, TimeZone timeZone, boolean zonelessInput, Environment env) throws UnknownDateTypeFormattingUnsupportedException, InvalidFormatParametersException {
        return new XSTemplateDateFormat(params, 2, dateType, zonelessInput, timeZone, this, env);
    }
}

