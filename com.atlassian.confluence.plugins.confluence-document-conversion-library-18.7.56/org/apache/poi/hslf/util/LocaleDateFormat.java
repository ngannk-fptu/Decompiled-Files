/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.util;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.AbstractMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LocaleID;
import org.apache.poi.util.SuppressForbidden;

@Internal
public final class LocaleDateFormat {
    private LocaleDateFormat() {
    }

    public static DateTimeFormatter map(LocaleID lcid, int formatID, MapFormatId mapFormatId) {
        Object mappedFormat;
        Locale loc = Locale.forLanguageTag(lcid.getLanguageTag());
        int mappedFormatId = formatID;
        if (mapFormatId == MapFormatId.PPT) {
            mappedFormat = MapFormatPPT.mapFormatId(lcid, formatID);
            if (mappedFormat instanceof String) {
                return DateTimeFormatter.ofPattern((String)mappedFormat, loc);
            }
            mappedFormatId = (Integer)mappedFormat;
        }
        if ((mappedFormat = MapFormatException.mapFormatId(lcid, mappedFormatId)) instanceof String) {
            return DateTimeFormatter.ofPattern((String)mappedFormat, loc);
        }
        return MapFormatBase.mapFormatId(loc, (Integer)mappedFormat);
    }

    private static boolean isOldFmt() {
        return System.getProperty("java.version").startsWith("1.8");
    }

    static /* synthetic */ boolean access$000() {
        return LocaleDateFormat.isOldFmt();
    }

    @SuppressForbidden(value="DateTimeFormatter::ofLocalizedDate and others will be localized in mapFormatId")
    private static enum MapFormatBase {
        SHORT_DATE(null, FormatStyle.MEDIUM, DateTimeFormatter::ofLocalizedDate),
        LONG_DATE(null, FormatStyle.FULL, DateTimeFormatter::ofLocalizedDate),
        LONG_DATE_WITHOUT_WEEKDAY("d. MMMM yyyy", null, null),
        ALTERNATE_SHORT_DATE("dd/MM/yy", null, null),
        ISO_STANDARD_DATE("yyyy-MM-dd", null, null),
        SHORT_DATE_WITH_ABBREVIATED_MONTH("d-MMM-yy", null, null),
        SHORT_DATE_WITH_SLASHES("d/M/y", null, null),
        ALTERNATE_SHORT_DATE_WITH_ABBREVIATED_MONTH("d. MMM yy", null, null),
        ENGLISH_DATE("d MMMM yyyy", null, null),
        MONTH_AND_YEAR("MMMM yy", null, null),
        ABBREVIATED_MONTH_AND_YEAR(LocaleDateFormat.access$000() ? "MMM-yy" : "LLL-yy", null, null),
        DATE_AND_HOUR12_TIME(null, FormatStyle.MEDIUM, fs -> new DateTimeFormatterBuilder().appendLocalized(FormatStyle.SHORT, null).appendLiteral("  ").appendLocalized(null, FormatStyle.SHORT).toFormatter()),
        DATE_AND_HOUR12_TIME_WITH_SECONDS(null, FormatStyle.MEDIUM, fs -> new DateTimeFormatterBuilder().appendLocalized(FormatStyle.SHORT, null).appendLiteral("  ").appendLocalized(null, (FormatStyle)((Object)fs)).toFormatter()),
        HOUR12_TIME("K:mm", null, null),
        HOUR12_TIME_WITH_SECONDS("K:mm:ss", null, null),
        HOUR24_TIME("HH:mm", null, null),
        HOUR24_TIME_WITH_SECONDS("HH:mm:ss", null, null);

        private final String datefmt;
        private final FormatStyle formatStyle;
        private final Function<FormatStyle, DateTimeFormatter> formatFct;

        private MapFormatBase(String datefmt, FormatStyle formatStyle, Function<FormatStyle, DateTimeFormatter> formatFct) {
            this.formatStyle = formatStyle;
            this.datefmt = datefmt;
            this.formatFct = formatFct;
        }

        public static DateTimeFormatter mapFormatId(Locale loc, int formatId) {
            MapFormatBase[] mfb = MapFormatBase.values();
            if (formatId < 0 || formatId >= mfb.length) {
                return DateTimeFormatter.BASIC_ISO_DATE;
            }
            MapFormatBase mf = mfb[formatId];
            return mf.datefmt == null ? mf.formatFct.apply(mf.formatStyle).withLocale(loc) : DateTimeFormatter.ofPattern(mf.datefmt, loc);
        }
    }

    private static enum MapFormatException {
        CHINESE(new LocaleID[]{LocaleID.ZH, LocaleID.ZH_HANS, LocaleID.ZH_HANT, LocaleID.ZH_CN, LocaleID.ZH_SG, LocaleID.ZH_MO, LocaleID.ZH_HK, LocaleID.ZH_YUE_HK}, 0, 1, "yyyy\u5e74M\u6708d\u65e5\u661f\u671fW", "yyyy\u5e74M\u6708d\u65e5", "yyyy/M/d", "yy.M.d", "yyyy\u5e74M\u6708d\u65e5\u661f\u671fW", "yyyy\u5e74M\u6708d\u65e5", "yyyy\u5e74M\u6708d\u65e5\u661f\u671fW", "yyyy\u5e74M\u6708", "yyyy\u5e74M\u6708", "h\u65f6m\u5206s\u79d2", "h\u65f6m\u5206", "h\u65f6m\u5206", "h\u65f6m\u5206", "ah\u65f6m\u5206", "ah\u65f6m\u5206", "EEEE\u5e74O\u6708A\u65e5", "EEEE\u5e74O\u6708A\u65e5\u661f\u671fW", "EEEE\u5e74O\u6708"),
        HINDI(new LocaleID[]{LocaleID.HI, LocaleID.HI_IN}, "dd/M/g", "dddd, d MMMM yyyy", "dd MMMM yyyy", "dd/M/yy", "yy-M-dd", "d-MMMM-yyyy", "dd.M.g", "dd MMMM. yy", "dd MMMM yy", "MMMM YY", "MMMM-g", "dd/M/g HH:mm", "dd/M/g HH:mm:ss", "HH:mm a", "HH:mm:ss a", "HH:mm", "HH:mm:ss"),
        JAPANESE(new LocaleID[]{LocaleID.JA, LocaleID.JA_JP, LocaleID.JA_PLOC_JP}, 0, 1, "EEEy\u5e74M\u6708d\u65e5", "yyyy\u5e74M\u6708d\u65e5", "yyyy/M/d", "yyyy\u5e74M\u6708d\u65e5", "yy\u5e74M\u6708d\u65e5", "yyyy\u5e74M\u6708d\u65e5", "yyyy\u5e74M\u6708d\u65e5(EEE)", "yyyy\u5e74M\u6708", "yyyy\u5e74M\u6708", "yy/M/d H\u6642m\u5206", "yy/M/d H\u6642m\u5206s\u79d2", "a h\u6642m\u5206", "a h\u6642m\u5206s\u79d2", "H\u6642m\u5206", "H\u6642m\u5206s\u79d2", "yyyy\u5e74M\u6708d\u65e5 EEE\u66dc\u65e5"),
        KOREAN(new LocaleID[]{LocaleID.KO, LocaleID.KO_KR}, 0, 1, "yyyy\ub144 M\uc6d4 d\uc77c EEE\uc694\uc77c", "yyyy\ub144 M\uc6d4 d\uc77c", "yyyy/M/d", "yyMMdd", "yyyy\ub144 M\uc6d4 d\uc77c", "yyyy\ub144 M\uc6d4", "yyyy\ub144 M\uc6d4 d\uc77c", "yyyy", "yyyy\ub144 M\uc6d4", "yyyy\ub144 M\uc6d4 d\uc77c a h\uc2dc m\ubd84", "yy\ub144 M\uc6d4 d\uc77c H\uc2dc m\ubd84 s\ucd08", "a h\uc2dc m\ubd84", "a h\uc2dc m\ubd84 s\ucd08", "H\uc2dc m\ubd84", "H\uc2dc m\ubd84 S\ucd08"),
        HUNGARIAN(new LocaleID[]{LocaleID.HU, LocaleID.HU_HU}, 0, 1, 2, 3, 4, 5, 6, "yy. MMM. dd.", "\u2019yy MMM.", "MMMM \u2019yy", 10, 11, 12, "a h:mm", "a h:mm:ss", 15, 16),
        BOKMAL(new LocaleID[]{LocaleID.NB_NO}, 0, 1, 2, 3, 4, "d. MMM. yyyy", "d/m yyyy", "MMM. yy", "yyyy.mm.dd", 9, "d. MMM.", 11, 12, 13, 14, 15, 16),
        CZECH(new LocaleID[]{LocaleID.CS, LocaleID.CS_CZ}, 0, 1, 2, 3, 4, 5, 6, 7, 8, "MMMM \u2019yy", 10, 11, 12, 13, 14, 15, 16),
        DANISH(new LocaleID[]{LocaleID.DA, LocaleID.DA_DK}, 0, "d. MMMM yyyy", "yy-MM-dd", "yyyy.MM.dd", 4, "MMMM yyyy", "d.M.yy", "d/M yyyy", "dd.MM.yyyy", "d.M.yyyy", "dd/MM yyyy", 11, 12, 13, 14, 15, 16),
        DUTCH(new LocaleID[]{LocaleID.NL, LocaleID.NL_BE, LocaleID.NL_NL}, 0, 1, 2, 3, 4, 5, 6, 7, 8, "MMMM \u2019yy", 10, 11, 12, 13, 14, 15, 16),
        FINISH(new LocaleID[]{LocaleID.FI, LocaleID.FI_FI}, 0, 1, 2, 3, 4, 5, 6, 7, 8, "MMMM \u2019yy", 10, 11, 12, 13, 14, 15, 16),
        FRENCH_CANADIAN(new LocaleID[]{LocaleID.FR_CA}, 0, 1, 2, "yy MM dd", 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16),
        GERMAN(new LocaleID[]{LocaleID.DE, LocaleID.DE_AT, LocaleID.DE_CH, LocaleID.DE_DE, LocaleID.DE_LI, LocaleID.DE_LU}, 0, 1, 2, 3, 4, "yy-MM-dd", 6, "dd. MMM. yyyy", 8, 9, 10, 11, 12, 13, 14, 15, 16),
        ITALIAN(new LocaleID[]{LocaleID.IT, LocaleID.IT_IT, LocaleID.IT_CH}, 0, 1, 2, 3, 4, "d-MMM.-yy", 6, "d. MMM. yy", "MMM. \u2019yy", "MMMM \u2019yy", 10, 11, 12, 13, 14, 15, 16),
        NO_MAP(new LocaleID[]{LocaleID.INVALID_O}, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);

        private final LocaleID[] lcid;
        private final Object[] mapping;
        private static final Map<LocaleID, MapFormatException> LCID_LOOKUP;

        private MapFormatException(LocaleID[] lcid, Object ... mapping) {
            this.lcid = lcid;
            this.mapping = mapping;
        }

        public static Object mapFormatId(LocaleID lcid, int formatId) {
            Object[] mapping = MapFormatException.LCID_LOOKUP.getOrDefault((Object)((Object)lcid), (MapFormatException)MapFormatException.NO_MAP).mapping;
            return formatId >= 0 && formatId < mapping.length ? mapping[formatId] : Integer.valueOf(formatId);
        }

        static {
            LCID_LOOKUP = Stream.of(MapFormatException.values()).flatMap(m -> Stream.of(m.lcid).map(l -> new AbstractMap.SimpleEntry<LocaleID, MapFormatException>((LocaleID)((Object)((Object)l)), (MapFormatException)((Object)m)))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
    }

    private static enum MapFormatPPT {
        EN_US(LocaleID.EN_US, "MM/dd/yyyy", 1, 8, "MMMM dd, yyyy", 5, 9, 10, 11, 12, 15, 16, "h:mm a", "h:mm:ss a"),
        EN_AU(LocaleID.EN_AU, 0, 1, "d MMMM, yyy", 2, 5, 9, 10, 11, 12, 15, 16, 13, 14),
        JA_JP(LocaleID.JA_JP, 4, 8, 7, 3, 0, 9, 5, 11, 12, "HH:mm", "HH:mm:ss", 15, 16),
        ZH_TW(LocaleID.ZH_TW, 0, 1, 3, 7, 12, 9, 10, 4, 11, "HH:mm", "HH:mm:ss", "H:mm a", "H:mm:ss a"),
        KO_KR(LocaleID.KO_KR, 0, 1, 6, 3, 4, 10, 7, 12, 11, "HH:mm", "HH:mm:ss", 13, 14),
        AR_SA(LocaleID.AR_SA, 0, 1, 2, 3, 4, 5, 8, 7, 8, 1, 10, 11, 5),
        HE_IL(LocaleID.HE_IL, 0, 1, 2, 6, 11, 5, 12, 7, 8, 9, 1, 11, 6),
        SV_SE(LocaleID.SV_SE, 0, 1, 3, 2, 7, 9, 10, 11, 12, 15, 16, 13, 14),
        ZH_CN(LocaleID.ZH_CN, 0, 1, 2, 2, 4, 9, 5, "yyyy\u5e74M\u6708d\u65e5h\u65f6m\u5206", "yyyy\u5e74M\u6708d\u65e5\u661f\u671fWh\u65f6m\u5206s\u79d2", "HH:mm", "HH:mm:ss", "a h\u65f6m\u5206", "a h\u65f6m\u5206s\u79d2"),
        ZH_SG(LocaleID.ZH_SG, 0, 1, 3, 2, 4, 9, 5, "yyyy\u5e74M\u6708d\u65e5h\u65f6m\u5206", "yyyy\u5e74M\u6708d\u65e5\u661f\u671fWh\u65f6m\u5206s\u79d2", "HH:mm", "HH:mm:ss", "a h\u65f6m\u5206", "a h\u65f6m\u5206s\u79d2"),
        ZH_MO(LocaleID.ZH_MO, 0, 1, 3, 2, 4, 9, 5, "yyyy\u5e74M\u6708d\u65e5h\u65f6m\u5206", "yyyy\u5e74M\u6708d\u65e5\u661f\u671fWh\u65f6m\u5206s\u79d2", "HH:mm", "HH:mm:ss", "a h\u65f6m\u5206", "a h\u65f6m\u5206s\u79d2"),
        ZH_HK(LocaleID.ZH_HK, 0, 1, 3, 2, 4, 9, 5, "yyyy\u5e74M\u6708d\u65e5h\u65f6m\u5206", "yyyy\u5e74M\u6708d\u65e5\u661f\u671fWh\u65f6m\u5206s\u79d2", "HH:mm", "HH:mm:ss", "a h\u65f6m\u5206", "a h\u65f6m\u5206s\u79d2"),
        TH_TH(LocaleID.TH_TH, 0, 1, 2, 3, 5, 6, 7, 8, 9, 10, 11, 13, 14),
        VI_VN(LocaleID.VI_VN, 0, 1, 2, 3, 5, 6, 10, 11, 12, 13, 14, 15, 16),
        HI_IN(LocaleID.HI_IN, 1, 2, 3, 5, 7, 11, 13, 0, 1, 5, 10, 11, 14),
        SYR_SY(LocaleID.SYR_SY, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12),
        NO_MAP(LocaleID.INVALID_O, 0, 1, 3, 2, 5, 9, 10, 11, 12, 15, 16, 13, 14, 4, 6, 7, 8);

        private final LocaleID lcid;
        private final Object[] mapping;
        private static final Map<LocaleID, MapFormatPPT> LCID_LOOKUP;

        private MapFormatPPT(LocaleID lcid, Object ... mapping) {
            this.lcid = lcid;
            this.mapping = mapping;
        }

        public LocaleID getLocaleID() {
            return this.lcid;
        }

        public static Object mapFormatId(LocaleID lcid, int formatId) {
            Object[] mapping = MapFormatPPT.LCID_LOOKUP.getOrDefault((Object)((Object)lcid), (MapFormatPPT)MapFormatPPT.NO_MAP).mapping;
            return formatId >= 0 && formatId < mapping.length ? mapping[formatId] : Integer.valueOf(formatId);
        }

        static {
            LCID_LOOKUP = Stream.of(MapFormatPPT.values()).collect(Collectors.toMap(MapFormatPPT::getLocaleID, Function.identity()));
        }
    }

    public static enum MapFormatId {
        NONE,
        PPT;

    }
}

