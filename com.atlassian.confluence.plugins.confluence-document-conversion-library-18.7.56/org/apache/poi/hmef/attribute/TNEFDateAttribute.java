/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hmef.attribute;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hmef.attribute.TNEFAttribute;
import org.apache.poi.hpsf.Filetime;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LocaleUtil;

public final class TNEFDateAttribute
extends TNEFAttribute {
    private static final Logger LOG = LogManager.getLogger(TNEFDateAttribute.class);
    private final Date data;

    protected TNEFDateAttribute(int id, int type, InputStream inp) throws IOException {
        super(id, type, inp);
        byte[] binData = this.getData();
        if (binData.length == 8) {
            this.data = Filetime.filetimeToDate(LittleEndian.getLong(this.getData(), 0));
        } else if (binData.length == 14) {
            Calendar c = LocaleUtil.getLocaleCalendar(LocaleUtil.TIMEZONE_UTC);
            c.set(1, LittleEndian.getUShort(binData, 0));
            c.set(2, LittleEndian.getUShort(binData, 2) - 1);
            c.set(5, LittleEndian.getUShort(binData, 4));
            c.set(11, LittleEndian.getUShort(binData, 6));
            c.set(12, LittleEndian.getUShort(binData, 8));
            c.set(13, LittleEndian.getUShort(binData, 10));
            c.clear(14);
            this.data = c.getTime();
        } else {
            throw new IllegalArgumentException("Invalid date, found " + binData.length + " bytes");
        }
    }

    public Date getDate() {
        return this.data;
    }

    @Override
    public String toString() {
        DateFormatSymbols dfs = DateFormatSymbols.getInstance(Locale.ROOT);
        SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", dfs);
        df.setTimeZone(LocaleUtil.TIMEZONE_UTC);
        return "Attribute " + this.getProperty() + ", type=" + this.getType() + ", date=" + df.format(this.data);
    }

    public static Date getAsDate(TNEFAttribute attr) {
        if (attr == null) {
            return null;
        }
        if (attr instanceof TNEFDateAttribute) {
            return ((TNEFDateAttribute)attr).getDate();
        }
        LOG.atWarn().log("Warning, non date property found: {}", (Object)attr);
        return null;
    }
}

