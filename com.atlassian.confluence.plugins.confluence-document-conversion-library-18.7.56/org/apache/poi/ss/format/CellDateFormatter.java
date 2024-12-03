/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.format;

import java.text.AttributedCharacterIterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.regex.Matcher;
import org.apache.poi.ss.format.CellFormatPart;
import org.apache.poi.ss.format.CellFormatType;
import org.apache.poi.ss.format.CellFormatter;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.util.StringUtil;

public class CellDateFormatter
extends CellFormatter {
    private boolean amPmUpper;
    private boolean showM;
    private boolean showAmPm;
    private final DateFormat dateFmt;
    private String sFmt;
    private static final Calendar EXCEL_EPOCH_CAL = LocaleUtil.getLocaleCalendar(1904, 0, 1);
    private static final int NUM_MILLISECONDS_IN_DAY = 86400000;
    private static CellDateFormatter SIMPLE_DATE_FORMATTER;

    public CellDateFormatter(String format) {
        this(LocaleUtil.getUserLocale(), format);
    }

    public CellDateFormatter(Locale locale, String format) {
        super(format);
        DatePartHandler partHandler = new DatePartHandler();
        StringBuffer descBuf = CellFormatPart.parseFormat(format, CellFormatType.DATE, partHandler);
        partHandler.finish(descBuf);
        this.dateFmt = new SimpleDateFormat(descBuf.toString(), locale);
        this.dateFmt.setTimeZone(LocaleUtil.getUserTimeZone());
    }

    @Override
    public synchronized void formatValue(StringBuffer toAppendTo, Object value) {
        if (value == null) {
            value = 0.0;
        }
        if (value instanceof Number) {
            Number num = (Number)value;
            double v = Math.round(num.doubleValue() * 8.64E7);
            if (v == 0.0) {
                value = EXCEL_EPOCH_CAL.getTime();
            } else {
                Calendar c = (Calendar)EXCEL_EPOCH_CAL.clone();
                int seconds = (int)(this.sFmt == null ? (double)Math.round(v / 1000.0) : v / 1000.0);
                c.add(13, seconds);
                c.add(14, (int)(v % 1000.0));
                value = c.getTime();
            }
        }
        AttributedCharacterIterator it = this.dateFmt.formatToCharacterIterator(value);
        boolean doneAm = false;
        boolean doneMillis = false;
        char ch = it.first();
        while (ch != '\uffff') {
            if (it.getAttribute(DateFormat.Field.MILLISECOND) != null) {
                if (!doneMillis) {
                    Date dateObj = (Date)value;
                    int pos = toAppendTo.length();
                    try (Formatter formatter = new Formatter(toAppendTo, Locale.ROOT);){
                        long msecs = dateObj.getTime() % 1000L;
                        if (msecs < 0L) {
                            msecs += 1000L;
                        }
                        formatter.format(this.locale, this.sFmt, (double)msecs / 1000.0);
                    }
                    toAppendTo.delete(pos, pos + 2);
                    doneMillis = true;
                }
            } else if (it.getAttribute(DateFormat.Field.AM_PM) != null) {
                if (!doneAm) {
                    if (this.showAmPm) {
                        if (this.amPmUpper) {
                            toAppendTo.append(StringUtil.toUpperCase(ch));
                            if (this.showM) {
                                toAppendTo.append('M');
                            }
                        } else {
                            toAppendTo.append(StringUtil.toLowerCase(ch));
                            if (this.showM) {
                                toAppendTo.append('m');
                            }
                        }
                    }
                    doneAm = true;
                }
            } else {
                toAppendTo.append(ch);
            }
            ch = it.next();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    @Override
    public void simpleValue(StringBuffer toAppendTo, Object value) {
        CellDateFormatter cellDateFormatter = SIMPLE_DATE_FORMATTER;
        if (cellDateFormatter == null) {
            Class<CellDateFormatter> clazz = CellDateFormatter.class;
            // MONITORENTER : org.apache.poi.ss.format.CellDateFormatter.class
            cellDateFormatter = SIMPLE_DATE_FORMATTER;
            if (cellDateFormatter == null) {
                SIMPLE_DATE_FORMATTER = cellDateFormatter = new CellDateFormatter("mm/d/y");
            }
            // MONITOREXIT : clazz
        }
        cellDateFormatter.formatValue(toAppendTo, value);
    }

    class DatePartHandler
    implements CellFormatPart.PartHandler {
        private int mStart = -1;
        private int mLen;
        private int hStart = -1;
        private int hLen;

        DatePartHandler() {
        }

        @Override
        public String handlePart(Matcher m, String part, CellFormatType type, StringBuffer desc) {
            int pos = desc.length();
            char firstCh = part.charAt(0);
            switch (firstCh) {
                case 'S': 
                case 's': {
                    if (this.mStart >= 0) {
                        for (int i = 0; i < this.mLen; ++i) {
                            desc.setCharAt(this.mStart + i, 'm');
                        }
                        this.mStart = -1;
                    }
                    return part.toLowerCase(Locale.ROOT);
                }
                case 'H': 
                case 'h': {
                    this.mStart = -1;
                    this.hStart = pos;
                    this.hLen = part.length();
                    return part.toLowerCase(Locale.ROOT);
                }
                case 'D': 
                case 'd': {
                    this.mStart = -1;
                    if (part.length() <= 2) {
                        return part.toLowerCase(Locale.ROOT);
                    }
                    return part.toLowerCase(Locale.ROOT).replace('d', 'E');
                }
                case 'M': 
                case 'm': {
                    this.mStart = pos;
                    this.mLen = part.length();
                    if (this.hStart >= 0) {
                        return part.toLowerCase(Locale.ROOT);
                    }
                    return part.toUpperCase(Locale.ROOT);
                }
                case 'Y': 
                case 'y': {
                    this.mStart = -1;
                    if (part.length() == 1) {
                        part = "yy";
                    } else if (part.length() == 3) {
                        part = "yyyy";
                    }
                    return part.toLowerCase(Locale.ROOT);
                }
                case '0': {
                    this.mStart = -1;
                    int sLen = part.length();
                    CellDateFormatter.this.sFmt = "%0" + (sLen + 2) + "." + sLen + "f";
                    return part.replace('0', 'S');
                }
                case 'A': 
                case 'P': 
                case 'a': 
                case 'p': {
                    if (part.length() <= 1) break;
                    this.mStart = -1;
                    CellDateFormatter.this.showAmPm = true;
                    CellDateFormatter.this.showM = StringUtil.toLowerCase(part.charAt(1)).equals("m");
                    CellDateFormatter.this.amPmUpper = CellDateFormatter.this.showM || StringUtil.isUpperCase(part.charAt(0));
                    return "a";
                }
            }
            return null;
        }

        public void updatePositions(int pos, int offset) {
            if (pos < this.hStart) {
                this.hStart += offset;
            }
            if (pos < this.mStart) {
                this.mStart += offset;
            }
        }

        public void finish(StringBuffer toAppendTo) {
            if (this.hStart >= 0 && !CellDateFormatter.this.showAmPm) {
                for (int i = 0; i < this.hLen; ++i) {
                    toAppendTo.setCharAt(this.hStart + i, 'H');
                }
            }
        }
    }
}

