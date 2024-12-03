/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hmef.attribute;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hmef.attribute.MAPIAttribute;
import org.apache.poi.hpsf.Filetime;
import org.apache.poi.hsmf.datatypes.MAPIProperty;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LocaleUtil;

public final class MAPIDateAttribute
extends MAPIAttribute {
    private static final Logger LOG = LogManager.getLogger(MAPIDateAttribute.class);
    private final Date data;

    protected MAPIDateAttribute(MAPIProperty property, int type, byte[] data) {
        super(property, type, data);
        this.data = Filetime.filetimeToDate(LittleEndian.getLong(data, 0));
    }

    public Date getDate() {
        return this.data;
    }

    @Override
    public String toString() {
        DateFormatSymbols dfs = DateFormatSymbols.getInstance(Locale.ROOT);
        SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", dfs);
        df.setTimeZone(LocaleUtil.TIMEZONE_UTC);
        return this.getProperty() + " " + df.format(this.data);
    }

    public static Date getAsDate(MAPIAttribute attr) {
        if (attr == null) {
            return null;
        }
        if (attr instanceof MAPIDateAttribute) {
            return ((MAPIDateAttribute)attr).getDate();
        }
        LOG.atWarn().log("Warning, non date property found: {}", (Object)attr);
        return null;
    }
}

