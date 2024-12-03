/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BugException;
import freemarker.core.Environment;
import freemarker.core.ISOLikeTemplateDateFormatFactory;
import freemarker.core.InvalidFormatParametersException;
import freemarker.core.TemplateDateFormat;
import freemarker.core.TemplateFormatUtil;
import freemarker.core.UnknownDateTypeFormattingUnsupportedException;
import freemarker.core.UnparsableValueException;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DateUtil;
import freemarker.template.utility.StringUtil;
import java.util.Date;
import java.util.TimeZone;

abstract class ISOLikeTemplateDateFormat
extends TemplateDateFormat {
    private static final String XS_LESS_THAN_SECONDS_ACCURACY_ERROR_MESSAGE = "Less than seconds accuracy isn't allowed by the XML Schema format";
    private final ISOLikeTemplateDateFormatFactory factory;
    private final Environment env;
    protected final int dateType;
    protected final boolean zonelessInput;
    protected final TimeZone timeZone;
    protected final Boolean forceUTC;
    protected final Boolean showZoneOffset;
    protected final int accuracy;

    public ISOLikeTemplateDateFormat(String formatString, int parsingStart, int dateType, boolean zonelessInput, TimeZone timeZone, ISOLikeTemplateDateFormatFactory factory, Environment env) throws InvalidFormatParametersException, UnknownDateTypeFormattingUnsupportedException {
        this.factory = factory;
        this.env = env;
        if (dateType == 0) {
            throw new UnknownDateTypeFormattingUnsupportedException();
        }
        this.dateType = dateType;
        this.zonelessInput = zonelessInput;
        int ln = formatString.length();
        boolean afterSeparator = false;
        int i = parsingStart;
        int accuracy = 7;
        Boolean showZoneOffset = null;
        Boolean forceUTC = Boolean.FALSE;
        while (i < ln) {
            char c;
            if ((c = formatString.charAt(i++)) == '_' || c == ' ') {
                afterSeparator = true;
                continue;
            }
            if (!afterSeparator) {
                throw new InvalidFormatParametersException("Missing space or \"_\" before \"" + c + "\" (at char pos. " + i + ").");
            }
            block0 : switch (c) {
                case 'h': 
                case 'm': 
                case 's': {
                    if (accuracy != 7) {
                        throw new InvalidFormatParametersException("Character \"" + c + "\" is unexpected as accuracy was already specified earlier (at char pos. " + i + ").");
                    }
                    switch (c) {
                        case 'h': {
                            if (this.isXSMode()) {
                                throw new InvalidFormatParametersException(XS_LESS_THAN_SECONDS_ACCURACY_ERROR_MESSAGE);
                            }
                            accuracy = 4;
                            break;
                        }
                        case 'm': {
                            if (i < ln && formatString.charAt(i) == 's') {
                                ++i;
                                accuracy = 8;
                                break;
                            }
                            if (this.isXSMode()) {
                                throw new InvalidFormatParametersException(XS_LESS_THAN_SECONDS_ACCURACY_ERROR_MESSAGE);
                            }
                            accuracy = 5;
                            break;
                        }
                        case 's': {
                            accuracy = 6;
                        }
                    }
                    break;
                }
                case 'f': {
                    if (i < ln && formatString.charAt(i) == 'u') {
                        this.checkForceUTCNotSet(forceUTC);
                        ++i;
                        forceUTC = Boolean.TRUE;
                        break;
                    }
                }
                case 'n': {
                    if (showZoneOffset != null) {
                        throw new InvalidFormatParametersException("Character \"" + c + "\" is unexpected as zone offset visibility was already specified earlier. (at char pos. " + i + ").");
                    }
                    switch (c) {
                        case 'n': {
                            if (i < ln && formatString.charAt(i) == 'z') {
                                ++i;
                                showZoneOffset = Boolean.FALSE;
                                break block0;
                            }
                            throw new InvalidFormatParametersException("\"n\" must be followed by \"z\" (at char pos. " + i + ").");
                        }
                        case 'f': {
                            if (i < ln && formatString.charAt(i) == 'z') {
                                ++i;
                                showZoneOffset = Boolean.TRUE;
                                break block0;
                            }
                            throw new InvalidFormatParametersException("\"f\" must be followed by \"z\" (at char pos. " + i + ").");
                        }
                    }
                    break;
                }
                case 'u': {
                    this.checkForceUTCNotSet(forceUTC);
                    forceUTC = null;
                    break;
                }
                default: {
                    throw new InvalidFormatParametersException("Unexpected character, " + StringUtil.jQuote(String.valueOf(c)) + ". Expected the beginning of one of: h, m, s, ms, nz, fz, u (at char pos. " + i + ").");
                }
            }
            afterSeparator = false;
        }
        this.accuracy = accuracy;
        this.showZoneOffset = showZoneOffset;
        this.forceUTC = forceUTC;
        this.timeZone = timeZone;
    }

    private void checkForceUTCNotSet(Boolean fourceUTC) throws InvalidFormatParametersException {
        if (fourceUTC != Boolean.FALSE) {
            throw new InvalidFormatParametersException("The UTC usage option was already set earlier.");
        }
    }

    @Override
    public final String formatToPlainText(TemplateDateModel dateModel) throws TemplateModelException {
        Date date = TemplateFormatUtil.getNonNullDate(dateModel);
        return this.format(date, this.dateType != 1, this.dateType != 2, this.showZoneOffset == null ? !this.zonelessInput : this.showZoneOffset, this.accuracy, (this.forceUTC == null ? !this.zonelessInput : this.forceUTC != false) ? DateUtil.UTC : this.timeZone, this.factory.getISOBuiltInCalendar(this.env));
    }

    protected abstract String format(Date var1, boolean var2, boolean var3, boolean var4, int var5, TimeZone var6, DateUtil.DateToISO8601CalendarFactory var7);

    @Override
    public final Date parse(String s, int dateType) throws UnparsableValueException {
        DateUtil.CalendarFieldsToDateConverter calToDateConverter = this.factory.getCalendarFieldsToDateCalculator(this.env);
        TimeZone tz = this.forceUTC != Boolean.FALSE ? DateUtil.UTC : this.timeZone;
        try {
            if (dateType == 2) {
                return this.parseDate(s, tz, calToDateConverter);
            }
            if (dateType == 1) {
                return this.parseTime(s, tz, calToDateConverter);
            }
            if (dateType == 3) {
                return this.parseDateTime(s, tz, calToDateConverter);
            }
            throw new BugException("Unexpected date type: " + dateType);
        }
        catch (DateUtil.DateParseException e) {
            throw new UnparsableValueException(e.getMessage(), e);
        }
    }

    protected abstract Date parseDate(String var1, TimeZone var2, DateUtil.CalendarFieldsToDateConverter var3) throws DateUtil.DateParseException;

    protected abstract Date parseTime(String var1, TimeZone var2, DateUtil.CalendarFieldsToDateConverter var3) throws DateUtil.DateParseException;

    protected abstract Date parseDateTime(String var1, TimeZone var2, DateUtil.CalendarFieldsToDateConverter var3) throws DateUtil.DateParseException;

    @Override
    public final String getDescription() {
        switch (this.dateType) {
            case 2: {
                return this.getDateDescription();
            }
            case 1: {
                return this.getTimeDescription();
            }
            case 3: {
                return this.getDateTimeDescription();
            }
        }
        return "<error: wrong format dateType>";
    }

    protected abstract String getDateDescription();

    protected abstract String getTimeDescription();

    protected abstract String getDateTimeDescription();

    @Override
    public final boolean isLocaleBound() {
        return false;
    }

    @Override
    public boolean isTimeZoneBound() {
        return true;
    }

    protected abstract boolean isXSMode();
}

