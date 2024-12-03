/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.bouncycastle.util.Longs;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class LocaleUtil {
    private static final Map localeCache = new HashMap();
    public static Locale EN_Locale = LocaleUtil.forEN();

    private static Locale forEN() {
        if ("en".equalsIgnoreCase(Locale.getDefault().getLanguage())) {
            return Locale.getDefault();
        }
        Locale[] locales = Locale.getAvailableLocales();
        for (int i = 0; i != locales.length; ++i) {
            if (!"en".equalsIgnoreCase(locales[i].getLanguage())) continue;
            return locales[i];
        }
        return Locale.getDefault();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static Date epochAdjust(Date date) throws ParseException {
        Locale locale = Locale.getDefault();
        if (locale == null) {
            return date;
        }
        Map map = localeCache;
        synchronized (map) {
            Long adj = (Long)localeCache.get(locale);
            if (adj == null) {
                SimpleDateFormat dateF = new SimpleDateFormat("yyyyMMddHHmmssz");
                long v = dateF.parse("19700101000000GMT+00:00").getTime();
                adj = LocaleUtil.longValueOf(v);
                localeCache.put(locale, adj);
            }
            if (adj != 0L) {
                return new Date(date.getTime() - adj);
            }
            return date;
        }
    }

    private static Long longValueOf(long v) {
        return Longs.valueOf(v);
    }
}

