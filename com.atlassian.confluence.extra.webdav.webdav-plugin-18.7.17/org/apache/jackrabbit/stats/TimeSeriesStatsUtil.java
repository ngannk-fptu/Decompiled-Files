/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.stats;

import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import org.apache.jackrabbit.api.stats.TimeSeries;

public final class TimeSeriesStatsUtil {
    public static final String[] ITEM_NAMES = new String[]{"per second", "per minute", "per hour", "per week"};

    private TimeSeriesStatsUtil() {
    }

    public static CompositeData asCompositeData(TimeSeries timeSeries, String name) {
        try {
            long[][] values = new long[][]{timeSeries.getValuePerSecond(), timeSeries.getValuePerMinute(), timeSeries.getValuePerHour(), timeSeries.getValuePerWeek()};
            return new CompositeDataSupport(TimeSeriesStatsUtil.getCompositeType(name), ITEM_NAMES, (Object[])values);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Error creating CompositeData instance from TimeSeries", e);
        }
    }

    private static CompositeType getCompositeType(String name) throws OpenDataException {
        ArrayType longArrayType = new ArrayType(SimpleType.LONG, true);
        OpenType[] itemTypes = new OpenType[]{longArrayType, longArrayType, longArrayType, longArrayType};
        return new CompositeType(name, name + " time series", ITEM_NAMES, ITEM_NAMES, itemTypes);
    }
}

