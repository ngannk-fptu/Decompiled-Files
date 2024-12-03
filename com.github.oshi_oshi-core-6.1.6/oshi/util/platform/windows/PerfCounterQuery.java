/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.platform.win32.COM.WbemcliUtil$WmiQuery
 *  com.sun.jna.platform.win32.COM.WbemcliUtil$WmiResult
 *  com.sun.jna.platform.win32.PdhUtil
 *  com.sun.jna.platform.win32.PdhUtil$PdhException
 *  com.sun.jna.platform.win32.VersionHelpers
 *  com.sun.jna.platform.win32.Win32Exception
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package oshi.util.platform.windows;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import com.sun.jna.platform.win32.PdhUtil;
import com.sun.jna.platform.win32.VersionHelpers;
import com.sun.jna.platform.win32.Win32Exception;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.PerfCounterQueryHandler;
import oshi.util.platform.windows.PerfDataUtil;
import oshi.util.platform.windows.WmiQueryHandler;
import oshi.util.platform.windows.WmiUtil;

@ThreadSafe
public final class PerfCounterQuery {
    private static final Logger LOG = LoggerFactory.getLogger(PerfCounterQuery.class);
    private static final boolean IS_VISTA_OR_GREATER = VersionHelpers.IsWindowsVistaOrGreater();
    private static final Set<String> FAILED_QUERY_CACHE = ConcurrentHashMap.newKeySet();
    private static final ConcurrentHashMap<String, String> LOCALIZE_CACHE = IS_VISTA_OR_GREATER ? null : new ConcurrentHashMap();
    public static final String TOTAL_INSTANCE = "_Total";
    public static final String TOTAL_INSTANCES = "*_Total";
    public static final String NOT_TOTAL_INSTANCE = "^_Total";
    public static final String NOT_TOTAL_INSTANCES = "^*_Total";

    private PerfCounterQuery() {
    }

    public static <T extends Enum<T>> Map<T, Long> queryValues(Class<T> propertyEnum, String perfObject, String perfWmiClass) {
        if (!FAILED_QUERY_CACHE.contains(perfObject)) {
            Map<T, Long> valueMap = PerfCounterQuery.queryValuesFromPDH(propertyEnum, perfObject);
            if (!valueMap.isEmpty()) {
                return valueMap;
            }
            LOG.warn("Disabling further attempts to query {}.", (Object)perfObject);
            FAILED_QUERY_CACHE.add(perfObject);
        }
        return PerfCounterQuery.queryValuesFromWMI(propertyEnum, perfWmiClass);
    }

    public static <T extends Enum<T>> Map<T, Long> queryValuesFromPDH(Class<T> propertyEnum, String perfObject) {
        Enum[] props = (Enum[])propertyEnum.getEnumConstants();
        String perfObjectLocalized = PerfCounterQuery.localizeIfNeeded(perfObject);
        EnumMap<T, PerfDataUtil.PerfCounter> counterMap = new EnumMap<T, PerfDataUtil.PerfCounter>(propertyEnum);
        EnumMap<T, Long> valueMap = new EnumMap<T, Long>(propertyEnum);
        try (PerfCounterQueryHandler pdhQueryHandler = new PerfCounterQueryHandler();){
            for (Enum prop : props) {
                PerfDataUtil.PerfCounter counter = PerfDataUtil.createCounter(perfObjectLocalized, ((PdhCounterProperty)((Object)prop)).getInstance(), ((PdhCounterProperty)((Object)prop)).getCounter());
                counterMap.put(prop, counter);
                if (pdhQueryHandler.addCounterToQuery(counter)) continue;
                EnumMap<T, Long> enumMap = valueMap;
                return enumMap;
            }
            if (0L < pdhQueryHandler.updateQuery()) {
                for (Enum prop : props) {
                    valueMap.put(prop, pdhQueryHandler.queryCounter((PerfDataUtil.PerfCounter)counterMap.get(prop)));
                }
            }
        }
        return valueMap;
    }

    public static <T extends Enum<T>> Map<T, Long> queryValuesFromWMI(Class<T> propertyEnum, String wmiClass) {
        WbemcliUtil.WmiQuery query = new WbemcliUtil.WmiQuery(wmiClass, propertyEnum);
        WbemcliUtil.WmiResult result = Objects.requireNonNull(WmiQueryHandler.createInstance()).queryWMI(query);
        EnumMap<T, Long> valueMap = new EnumMap<T, Long>(propertyEnum);
        if (result.getResultCount() > 0) {
            block6: for (Enum prop : (Enum[])propertyEnum.getEnumConstants()) {
                switch (result.getCIMType(prop)) {
                    case 18: {
                        valueMap.put(prop, Long.valueOf(WmiUtil.getUint16(result, prop, 0)));
                        continue block6;
                    }
                    case 19: {
                        valueMap.put(prop, WmiUtil.getUint32asLong(result, prop, 0));
                        continue block6;
                    }
                    case 21: {
                        valueMap.put(prop, WmiUtil.getUint64(result, prop, 0));
                        continue block6;
                    }
                    case 101: {
                        valueMap.put(prop, WmiUtil.getDateTime(result, prop, 0).toInstant().toEpochMilli());
                        continue block6;
                    }
                    default: {
                        throw new ClassCastException("Unimplemented CIM Type Mapping.");
                    }
                }
            }
        }
        return valueMap;
    }

    public static String localizeIfNeeded(String perfObject) {
        return IS_VISTA_OR_GREATER ? perfObject : LOCALIZE_CACHE.computeIfAbsent(perfObject, PerfCounterQuery::localizeUsingPerfIndex);
    }

    private static String localizeUsingPerfIndex(String perfObject) {
        String localized = perfObject;
        try {
            localized = PdhUtil.PdhLookupPerfNameByIndex(null, (int)PdhUtil.PdhLookupPerfIndexByEnglishName((String)perfObject));
        }
        catch (Win32Exception e) {
            LOG.warn("Unable to locate English counter names in registry Perflib 009. Assuming English counters. Error {}. {}", (Object)String.format("0x%x", e.getHR().intValue()), (Object)"See https://support.microsoft.com/en-us/help/300956/how-to-manually-rebuild-performance-counter-library-values");
        }
        catch (PdhUtil.PdhException e) {
            LOG.warn("Unable to localize {} performance counter.  Error {}.", (Object)perfObject, (Object)String.format("0x%x", e.getErrorCode()));
        }
        if (localized.isEmpty()) {
            return perfObject;
        }
        LOG.debug("Localized {} to {}", (Object)perfObject, (Object)localized);
        return localized;
    }

    public static interface PdhCounterProperty {
        public String getInstance();

        public String getCounter();
    }
}

